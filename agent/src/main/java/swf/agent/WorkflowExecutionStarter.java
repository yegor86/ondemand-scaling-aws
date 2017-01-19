package swf.agent;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.amazonaws.services.simpleworkflow.model.WorkflowExecution;

public class WorkflowExecutionStarter {
	
    private static String swfServiceUrl = "http://swf.us-east-1.amazonaws.com";
	private static String domain = "ondemand.scaling";
	private static String taskList = "taskList";
    
    public static void main(String[] args) throws Exception {
    	
    	AmazonSimpleWorkflow swfService = new AmazonSimpleWorkflowClient(new EnvironmentVariableCredentialsProvider());
    	swfService.setEndpoint(swfServiceUrl);
        
        TaskWorkflowClientExternalFactory clientFactory = new TaskWorkflowClientExternalFactoryImpl(swfService, domain);
        TaskWorkflowClientExternal workflow = clientFactory.getClient();
        
        workflow.process();
        
        WorkflowExecution workflowExecution = workflow.getWorkflowExecution();
        System.out.println("Started periodic workflow with workflowId=\"" + workflowExecution.getWorkflowId()
                + "\" and runId=\"" + workflowExecution.getRunId() + "\"");
    }
}