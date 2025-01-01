import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CamundaProcessComponent } from './camunda-process.component';

describe('CamundaProcessComponent', () => {
  let component: CamundaProcessComponent;
  let fixture: ComponentFixture<CamundaProcessComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CamundaProcessComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CamundaProcessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
