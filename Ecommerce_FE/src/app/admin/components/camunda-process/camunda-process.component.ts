import { Component, OnInit } from '@angular/core';
import { CamundaProcessService } from '../../../services/camundaProcessService/camunda-process-service.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-camunda-process',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './camunda-process.component.html',
  styleUrls: ['./camunda-process.component.css']
})
export class CamundaProcessComponent implements OnInit {
  processes: any[] = [];
  processDetails: any;
  stats: any;
  selectedStatus: string = '';
  isLoading: boolean = false;
  isProcessDetailLoading: boolean = false;

  constructor(private camundaService: CamundaProcessService) {}

  ngOnInit(): void {
    this.loadProcesses();
    this.loadStatistics();
  }

  loadProcesses() {
    this.isLoading = true;
    const statusParam = this.selectedStatus === '' ? undefined : this.selectedStatus;
    this.camundaService.getAllProcesses(statusParam).subscribe({
      next: (data) => {
        this.processes = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error when loading processes:', err);
        this.isLoading = false;
      }
    });
  }

  viewProcessDetails(processInstanceId: string) {
    this.isProcessDetailLoading = true;
    this.camundaService.getProcessDetails(processInstanceId).subscribe({
      next: (details) => {
        this.processDetails = details;
        this.isProcessDetailLoading = false;
      },
      error: (err) => {
        this.handleError(err);
        this.isProcessDetailLoading = false;
      }
    });
  }  

  assignTask(taskId: string, assignee: string) {
    this.camundaService.assignTask(taskId, assignee).subscribe({
      next: () => {
        Swal.fire({
          title: 'Success!',
          text: `Task assigned to ${assignee}`,
          icon: 'success',
          confirmButtonText: 'OK'
        }).then(() => {
          if (this.processDetails) {
            this.viewProcessDetails(this.processDetails.processInstanceId);
          }
          this.loadProcesses();
          this.loadStatistics();
        });
      },
      error: (err) => this.handleError(err)
    });
  }

  cancelProcess(processInstanceId: string, reason: string) {
    this.camundaService.cancelProcess(processInstanceId, reason).subscribe({
      next: () => {
        Swal.fire({
          title: 'Success!',
          text: 'Process canceled successfully',
          icon: 'success',
          confirmButtonText: 'OK'
        }).then(() => {
          if (this.processDetails) {
            this.viewProcessDetails(this.processDetails.processInstanceId);
          }
          this.loadProcesses();
          this.loadStatistics();
        });
      },
      error: (err) => this.handleError(err)
    });
  }

  loadStatistics() {
    this.camundaService.getProcessStatistics().subscribe({
      next: (stats) => {
        this.stats = stats;
      },
      error: (err) => this.handleError(err)
    });
  }

  private handleError(error: any) {
    console.error('An error occurred:', error);
    Swal.fire({
      title: 'Error!',
      text: 'An error occurred. Please try again later.',
      icon: 'error',
      confirmButtonText: 'OK'
    });
  }

  claimTask(taskId: string) {
    this.camundaService.claimTask(taskId).subscribe({
      next: () => {
        Swal.fire({
          title: 'Success!',
          text: 'Task claimed successfully',
          icon: 'success',
          confirmButtonText: 'OK'
        }).then(() => {
          if (this.processDetails) {
            this.viewProcessDetails(this.processDetails.processInstanceId);
          }
          this.loadProcesses();
          this.loadStatistics();
        });
      },
      error: (err) => this.handleError(err)
    });
  }

  completeTask(taskId: string, isApprove: boolean) {
    const variables = { 
      status: "completed", 
      isApproval: isApprove 
    };
  
    this.camundaService.completeTask(taskId, variables).subscribe({
      next: () => {
        Swal.fire({
          title: 'Success!',
          text: 'Task completed successfully',
          icon: 'success',
          confirmButtonText: 'OK'
        }).then(() => {
          if (this.processDetails) {
            this.viewProcessDetails(this.processDetails.processInstanceId);
          }
          this.loadProcesses();
          this.loadStatistics();
        });
      },
      error: (err) => this.handleError(err)
    });
  }
  
}
