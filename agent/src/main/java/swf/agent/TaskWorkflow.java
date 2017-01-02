package swf.agent;

import com.amazonaws.services.simpleworkflow.flow.annotations.Execute;
import com.amazonaws.services.simpleworkflow.flow.annotations.GetState;
import com.amazonaws.services.simpleworkflow.flow.annotations.Workflow;
import com.amazonaws.services.simpleworkflow.flow.annotations.WorkflowRegistrationOptions;

@Workflow
@WorkflowRegistrationOptions(defaultExecutionStartToCloseTimeoutSeconds = 3000, defaultTaskStartToCloseTimeoutSeconds = 30)
public interface TaskWorkflow {
	@Execute(version = "2017-01-01")
    public void process();
    
    @GetState
    public String getState();
}
