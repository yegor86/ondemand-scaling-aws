package swf.agent;

import java.util.concurrent.TimeUnit;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.amazonaws.services.simpleworkflow.flow.ActivityWorker;

public class ActivityHost {
	
	private static String swfServiceUrl = "http://swf.us-east-1.amazonaws.com";
	private static String domain = "ondemand.scaling";
    private static String taskList = "taskList";
	
    public static void main(String[] args) throws Exception {
    	AmazonSimpleWorkflow swfService = new AmazonSimpleWorkflowClient(new EnvironmentVariableCredentialsProvider());
    	swfService.setEndpoint(swfServiceUrl);
        
        // Start worker to poll the common task list
        final ActivityWorker activityWorker = new ActivityWorker(swfService, domain, taskList);
        TaskActivityImpl storeActivityImpl = new TaskActivityImpl();
        activityWorker.addActivitiesImplementation(storeActivityImpl);
        
        activityWorker.setTaskExecutorThreadPoolSize(1);
        activityWorker.start();
        System.out.println("Host Service Started for Task List: " + taskList);        
        
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                try {
                    activityWorker.shutdown();
                    activityWorker.awaitTermination(1, TimeUnit.MINUTES);
                    System.out.println("Activity Workers Exited.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}