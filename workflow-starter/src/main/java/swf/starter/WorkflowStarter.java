package swf.starter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNS;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.amazonaws.services.simpleworkflow.flow.JsonDataConverter;
import com.amazonaws.services.simpleworkflow.model.Run;
import com.amazonaws.services.simpleworkflow.model.StartWorkflowExecutionRequest;
import com.amazonaws.services.simpleworkflow.model.TaskList;
import com.amazonaws.services.simpleworkflow.model.WorkflowType;
import com.google.gson.Gson;

public class WorkflowStarter {
	private AmazonSimpleWorkflow swfService;
    private WorkflowType wfType;
    private String domain = "ondemand.scaling";
    private String taskList = "taskList";
    private JsonDataConverter dataConverter = new JsonDataConverter();
    private Gson gson = new Gson();
	
	public WorkflowStarter() throws URISyntaxException, IOException {
		swfService = new AmazonSimpleWorkflowClient(new EnvironmentVariableCredentialsProvider());
        wfType = new WorkflowType()
            .withName("TaskWorkflow.process")
            .withVersion("2017-02-13");
	}
	
	public void handler(SNSEvent event, Context context) throws Exception {
		System.out.println("Received event " + event);
		
		String message = event.getRecords().get(0).getSNS().getMessage();
		Map<String, String> input = (Map<String, String>)gson.fromJson(message, Map.class);
		
		Object[] wfInput = new Object[] {
			input.get("sourceBucketName"),
			input.get("sourceFilename"),
			input.get("targetBucketName"),
			input.get("targetFilename")
		};
    	Run run = swfService.startWorkflowExecution(new StartWorkflowExecutionRequest()
            .withDomain(domain)
            .withWorkflowType(wfType)
            .withTaskList(new TaskList().withName(taskList))
            .withWorkflowId(UUID.randomUUID().toString())
            .withInput(dataConverter.toData(wfInput)));
    	
    	System.out.println("Workflow execution started with the run id " + run.getRunId());
    }
	
    public static void main(String[] args) throws Exception {
    	SNSRecord snsRecord = new SNSRecord();
    	snsRecord.setSns(new SNS());
    	snsRecord.getSNS().setMessage("{\"sourceBucketName\": \"ic-ondeman-scaling\", \"sourceFilename\": \"input/file.txt\", \"targetBucketName\": \"ic-ondeman-scaling\", \"targetFilename\": \"output2/file.zip\"}");
    	SNSEvent event = new SNSEvent();
    	event.setRecords(Arrays.asList(snsRecord));
    	
    	WorkflowStarter starter = new WorkflowStarter();
    	starter.handler(event, null);
	}
}
