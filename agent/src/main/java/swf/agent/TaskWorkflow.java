package swf.agent;

import com.amazonaws.services.simpleworkflow.flow.annotations.Execute;
import com.amazonaws.services.simpleworkflow.flow.annotations.GetState;
import com.amazonaws.services.simpleworkflow.flow.annotations.Workflow;
import com.amazonaws.services.simpleworkflow.flow.annotations.WorkflowRegistrationOptions;

@Workflow
@WorkflowRegistrationOptions(defaultExecutionStartToCloseTimeoutSeconds = 3000, defaultTaskStartToCloseTimeoutSeconds = 30)
public interface TaskWorkflow {
	@Execute(version = "2017-02-13")
    public void process(final String sourceBucketName, final String sourceFilename, final String targetBucketName, final String targetFilename);
    
    @GetState
    public String getState();
}
