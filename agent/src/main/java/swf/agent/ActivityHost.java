package swf.agent;

import java.util.concurrent.TimeUnit;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.amazonaws.services.simpleworkflow.flow.ActivityWorker;

import swf.agent.fileprocessing.FileProcessingActivityImpl;
import swf.agent.fileprocessing.SimpleStoreActivityS3Impl;

public class ActivityHost {
	
	private static String swfServiceUrl = "http://swf.us-east-1.amazonaws.com";
	private static String domain = "ondemand.scaling";
    private static String taskList = "taskList";
	
    public static void main(String[] args) throws Exception {
    	AmazonSimpleWorkflow swfService = new AmazonSimpleWorkflowClient();
    	AmazonS3 s3Client = new AmazonS3Client();
    	swfService.setEndpoint(swfServiceUrl);
        
        // Start worker to poll the common task list
        final ActivityWorker activityWorker = new ActivityWorker(swfService, domain, taskList);
        TaskActivityImpl storeActivityImpl = new TaskActivityImpl();
        FileProcessingActivityImpl fileProcessingActivity = new FileProcessingActivityImpl("/tmp/");
        SimpleStoreActivityS3Impl storeActivity = new SimpleStoreActivityS3Impl(s3Client, "/tmp/", taskList);
        
        activityWorker.addActivitiesImplementation(storeActivityImpl);
        activityWorker.addActivitiesImplementation(fileProcessingActivity);
        activityWorker.addActivitiesImplementation(storeActivity);
        
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