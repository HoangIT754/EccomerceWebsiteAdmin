import { TestBed } from '@angular/core/testing';

import { CamundaProcessServiceService } from './camunda-process-service.service';

describe('CamundaProcessServiceService', () => {
  let service: CamundaProcessServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CamundaProcessServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
