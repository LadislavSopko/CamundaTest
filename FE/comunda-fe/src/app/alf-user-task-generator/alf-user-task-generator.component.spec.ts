import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlfUserTaskGeneratorComponent } from './alf-user-task-generator.component';

describe('AlfUserTaskGeneratorComponent', () => {
  let component: AlfUserTaskGeneratorComponent;
  let fixture: ComponentFixture<AlfUserTaskGeneratorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AlfUserTaskGeneratorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AlfUserTaskGeneratorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
