import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CamundaProcessService {
  private baseUrl = 'http://localhost:8081/api/workflow';

  constructor(private http: HttpClient) {}

  getAllProcesses(status?: string): Observable<any> {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get(`${this.baseUrl}`, { params });
  }

  getProcessDetails(processInstanceId: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/${processInstanceId}`);
  }

  assignTask(taskId: string, assignee: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/${taskId}/assign`, { assignee });
  }

  cancelProcess(processInstanceId: string, reason: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/cancel/${processInstanceId}`, {
      body: { reason }
    });
  }

  getProcessStatistics(): Observable<any> {
    return this.http.get(`${this.baseUrl}/stats`);
  }

  claimTask(taskId: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/${taskId}/claim`, {});
  }

  completeTask(taskId: string, variables: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/${taskId}/complete`, variables);
  }
}
