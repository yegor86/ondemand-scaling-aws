package swf.agent;

import java.util.concurrent.TimeUnit;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.amazonaws.services.simpleworkflow.flow.WorkflowWorker;

public class WorkflowHost {

	private static String swfServiceUrl = "http://swf.us-east-1.amazonaws.com";
	private static String domain = "ondemand.scaling";
    private static String taskList = "taskList";
    
    public static void main(String[] args) throws Exception {
    	AmazonSimpleWorkflow swfService = new AmazonSimpleWorkflowClient(new EnvironmentVariableCredentialsProvider());
    	swfService.setEndpoint(swfServiceUrl);
        
        final WorkflowWorker worker = new WorkflowWorker(swfService, domain, taskList);
        worker.addWorkflowImplementationType(TaskWorkflowImpl.class, null, new Object[] {});
        
        worker.start();

        System.out.println("Workflow Host Service Started for Task List: " + taskList);

        Runtime.getRuntime().addShutdownHook(new Thread() {

            public void run() {
                try {
                    worker.shutdownAndAwaitTermination(1, TimeUnit.MINUTES);
                    System.out.println("Workflow Host Service Terminated...");
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
