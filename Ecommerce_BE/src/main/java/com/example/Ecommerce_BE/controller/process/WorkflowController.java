package com.example.Ecommerce_BE.controller.process;

import com.example.Ecommerce_BE.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/workflow")
@RequiredArgsConstructor
public class WorkflowController {
    private final RuntimeService runtimeService;
    private final HistoryService historyService;
    private final TaskService taskService;
    private final RepositoryService repositoryService;
    private final AuthService authService;

    public static class StartProcessRequest {
        @NotBlank
        private String processDefinitionKey;
        private Map<String, Object> variables;

        public String getProcessDefinitionKey() {
            return processDefinitionKey;
        }

        public void setProcessDefinitionKey(String processDefinitionKey) {
            this.processDefinitionKey = processDefinitionKey;
        }

        public Map<String, Object> getVariables() {
            return variables;
        }

        public void setVariables(Map<String, Object> variables) {
            this.variables = variables;
        }
    }

    public static class AssignTaskRequest {
        @NotBlank
        private String assignee;

        public String getAssignee() {
            return assignee;
        }

        public void setAssignee(String assignee) {
            this.assignee = assignee;
        }
    }

    public static class ChangeTaskStatusRequest {
        @NotBlank
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    private boolean isAdmin(String authToken) {
        List<String> roles = authService.getUserRoles(authToken);
        return roles.contains("admin");
    }

    @PostMapping("/start")
    public ResponseEntity<?> startProcess(@CookieValue(value = "auth_token", defaultValue = "") String authToken,
                                          @Valid @RequestBody StartProcessRequest request) {
        if (!isAdmin(authToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Admin role required.");
        }

        if (request.getProcessDefinitionKey() == null || request.getProcessDefinitionKey().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", "Process definition key is required"
            ));
        }

        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(request.getProcessDefinitionKey(), request.getVariables());
            return ResponseEntity.ok(Map.of(
                    "processInstanceId", processInstance.getId(),
                    "message", "Process started successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", "Failed to start process",
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProcesses(@CookieValue(value = "auth_token", defaultValue = "") String authToken,
                                             @RequestParam(required = false) String status,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        if (!isAdmin(authToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Admin role required.");
        }
        List<Map<String, Object>> processes = new ArrayList<>();

        if (status == null || status.equalsIgnoreCase("RUNNING")) {
            List<ProcessInstance> runningInstances = runtimeService.createProcessInstanceQuery()
                    .listPage(page * size, size);

            runningInstances.forEach(instance -> {
                String processDefinitionKey = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(instance.getProcessDefinitionId())
                        .singleResult()
                        .getKey();

                processes.add(Map.of(
                        "processInstanceId", instance.getId(),
                        "processDefinitionKey", processDefinitionKey,
                        "status", "RUNNING",
                        "currentTask", Optional.ofNullable(taskService.createTaskQuery()
                                        .processInstanceId(instance.getId())
                                        .singleResult())
                                .map(Task::getName)
                                .orElse("No active task")
                ));
            });
        }

        if (status == null || !status.equalsIgnoreCase("RUNNING")) {
            List<HistoricProcessInstance> completedInstances = historyService.createHistoricProcessInstanceQuery()
                    .finished()
                    .listPage(page * size, size);

            completedInstances.forEach(instance -> {
                String processDefinitionKey = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(instance.getProcessDefinitionId())
                        .singleResult()
                        .getKey();

                processes.add(Map.of(
                        "processInstanceId", instance.getId(),
                        "processDefinitionKey", processDefinitionKey,
                        "status", "COMPLETED",
                        "endTime", instance.getEndTime()
                ));
            });
        }

        return ResponseEntity.ok(processes);
    }

    @GetMapping("/{processInstanceId}")
    public ResponseEntity<?> getProcessDetails(@CookieValue(value = "auth_token", defaultValue = "") String authToken,
                                               @PathVariable String processInstanceId) {
        if (!isAdmin(authToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Admin role required.");
        }
        try {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();

            if (processInstance != null) {
                String processDefinitionKey = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(processInstance.getProcessDefinitionId())
                        .singleResult()
                        .getKey();

                List<Task> tasks = taskService.createTaskQuery()
                        .processInstanceId(processInstanceId)
                        .list();

                List<Map<String, Object>> taskDetails = tasks.stream().map(task -> {
                    Map<String, Object> taskMap = new HashMap<>();
                    taskMap.put("taskId", task.getId());
                    taskMap.put("taskName", task.getName());
                    taskMap.put("assignee", task.getAssignee());
                    taskMap.put("createTime", task.getCreateTime());
                    taskMap.put("processDefinitionKey", processDefinitionKey);
                    return taskMap;
                }).collect(Collectors.toList());

                return ResponseEntity.ok(Map.of(
                        "processInstanceId", processInstance.getId(),
                        "processDefinitionKey", processDefinitionKey,
                        "status", "RUNNING",
                        "tasks", taskDetails
                ));
            }

            return handleCompletedProcessDetails(processInstanceId);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "An error occurred while processing the request.", "error", e.getMessage()));
        }
    }

    private ResponseEntity<?> handleCompletedProcessDetails(String processInstanceId) {
        try {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();

            if (historicProcessInstance == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("message", "Process instance not found.", "processInstanceId", processInstanceId));
            }

            String processDefinitionKey = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(historicProcessInstance.getProcessDefinitionId())
                    .singleResult()
                    .getKey();

            return ResponseEntity.ok(Map.of(
                    "processInstanceId", historicProcessInstance.getId(),
                    "processDefinitionKey", processDefinitionKey,
                    "status", "COMPLETED",
                    "startTime", historicProcessInstance.getStartTime(),
                    "endTime", historicProcessInstance.getEndTime()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "An error occurred while handling completed process details.", "error", e.getMessage()));
        }
    }

    @DeleteMapping("/cancel/{processInstanceId}")
    public ResponseEntity<?> cancelProcess(@CookieValue(value = "auth_token", defaultValue = "") String authToken,
                                           @PathVariable String processInstanceId,
                                           @RequestBody Map<String, Object> request) {
        if (!isAdmin(authToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Admin role required.");
        }

        String reason = Optional.ofNullable((String) request.get("reason")).orElse("Canceled by user");

        try {
            runtimeService.deleteProcessInstance(processInstanceId, reason);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Failed to cancel process",
                    "error", e.getMessage()
            ));
        }

        return ResponseEntity.ok(Map.of(
                "processInstanceId", processInstanceId,
                "reason", reason,
                "message", "Process canceled successfully"
        ));
    }

    @PostMapping("/{taskId}/complete")
    public ResponseEntity<?> completeTask(@CookieValue(value = "auth_token", defaultValue = "") String authToken,
                                          @PathVariable String taskId,
                                          @RequestBody(required = false) Map<String, Object> request) {
        if (!isAdmin(authToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Admin role required.");
        }
        if (taskId == null || !taskId.matches("^[0-9a-fA-F-]{36}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid taskId format"));
        }

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Task not found"));
        }

        Map<String, Object> variables = Optional.ofNullable(request).orElse(new HashMap<>());
        System.out.println("Received variables: " + variables);

        try {
            taskService.complete(taskId, variables);
            return ResponseEntity.ok(Map.of(
                    "taskId", taskId,
                    "message", "Task completed successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Failed to complete task",
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getProcessStatistics(@CookieValue(value = "auth_token", defaultValue = "") String authToken) {
        if (!isAdmin(authToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Admin role required.");
        }
        long runningCount = runtimeService.createProcessInstanceQuery().count();
        long completedCount = historyService.createHistoricProcessInstanceQuery().finished().count();

        return ResponseEntity.ok(Map.of(
                "running", runningCount,
                "completed", completedCount
        ));
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<Map<String, Object>>> getTasks(@CookieValue(value = "auth_token", defaultValue = "") String authToken) {
        if (!isAdmin(authToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
        }

        List<Task> tasks = taskService.createTaskQuery().list();
        List<Map<String, Object>> taskDetails = tasks.stream().map(task -> {
            Map<String, Object> taskData = new HashMap<>();
            taskData.put("id", task.getId());
            taskData.put("name", task.getName());
            taskData.put("processInstanceId", task.getProcessInstanceId());
            taskData.put("variables", taskService.getVariables(task.getId()));
            return taskData;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(taskDetails);
    }

    @PostMapping("/{taskId}/claim")
    public ResponseEntity<?> claimTask(@CookieValue(value = "auth_token", defaultValue = "") String authToken,
                                       @PathVariable String taskId) {
        if (!isAdmin(authToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Admin role required.");
        }

        String usernameFromToken = authService.getUsernameFromToken(authToken);
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Task not found"));
        }

        try {
            taskService.claim(taskId, usernameFromToken);
            return ResponseEntity.ok(Map.of(
                    "taskId", taskId,
                    "message", "Task claimed successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Failed to claim task",
                    "error", e.getMessage()
            ));
        }
    }
}