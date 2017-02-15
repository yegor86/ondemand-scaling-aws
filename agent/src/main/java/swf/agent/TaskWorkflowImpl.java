package swf.agent;

import java.io.File;

import com.amazonaws.services.simpleworkflow.flow.ActivitySchedulingOptions;
import com.amazonaws.services.simpleworkflow.flow.DecisionContextProviderImpl;
import com.amazonaws.services.simpleworkflow.flow.WorkflowContext;
import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.core.Settable;
import com.amazonaws.services.simpleworkflow.flow.core.TryCatchFinally;

import swf.agent.fileprocessing.FileProcessingActivityClient;
import swf.agent.fileprocessing.FileProcessingActivityClientImpl;
import swf.agent.fileprocessing.SimpleStoreActivityClient;
import swf.agent.fileprocessing.SimpleStoreActivityClientImpl;

public class TaskWorkflowImpl implements TaskWorkflow {

	private String state = "";
	private final SimpleStoreActivityClient storeClient;
	private final FileProcessingActivityClient processorClient;
	private final WorkflowContext workflowContext;
	
	public TaskWorkflowImpl() {
		storeClient = new SimpleStoreActivityClientImpl();
		processorClient = new FileProcessingActivityClientImpl();
		workflowContext = (new DecisionContextProviderImpl()).getDecisionContext().getWorkflowContext();
	}
	
	@Override
	public void process(final String sourceBucketName, final String sourceFilename, final String targetBucketName, final String targetFilename) {
		
		// Settable to store the worker specific task list returned by the activity
        final Settable<String> taskList = new Settable<String>();
        
		File localSource = new File(sourceFilename);
		final String localSourceFilename = localSource.getName();
		File localTarget = new File(targetFilename);
        final String localTargetFilename = localTarget.getName();
		
		new TryCatchFinally() {

            @Override
            protected void doTry() throws Throwable {
            	
            	Promise<String> activityWorkerTaskList = storeClient.download(sourceBucketName, sourceFilename, localSourceFilename);
                // chaining is a way for one promise get assigned value of another 
                taskList.chain(activityWorkerTaskList);
                // Call processFile activity to zip the file
                Promise<Void> fileProcessed = processFileOnHost(localSourceFilename, localTargetFilename, activityWorkerTaskList);
                // Call upload activity to upload zipped file
                upload(targetBucketName, targetFilename, localTargetFilename, taskList, fileProcessed);
            }

            @Override
            protected void doCatch(Throwable e) throws Throwable {
            	state = "Failed: " + e.getMessage();
                throw e;
            }

            @Override
            protected void doFinally() throws Throwable {
            	if (taskList.isReady()) { // File was downloaded

                    // Set option to schedule activity in worker specific task list
                    ActivitySchedulingOptions options = new ActivitySchedulingOptions().withTaskList(taskList.get());

                    // Call deleteLocalFile activity using the host specific task list
                    storeClient.deleteLocalFile(localSourceFilename, options);
                    storeClient.deleteLocalFile(localTargetFilename, options);
                }
                if (!state.startsWith("Failed:")) {
                    state = "Completed";
                }
            }
	    };
		
	}
	
	@Asynchronous
    private Promise<Void> processFileOnHost(String fileToProcess, String fileToUpload, Promise<String> taskList) {
        state = "Downloaded to " + taskList.get();
        // Call the activity to process the file using worker specific task list
        ActivitySchedulingOptions options = new ActivitySchedulingOptions().withTaskList(taskList.get());
        return processorClient.processFile(fileToProcess, fileToUpload, options);
    }

    @Asynchronous
    private void upload(final String targetBucketName, final String targetFilename, final String localTargetFilename,
            Promise<String> taskList, Promise<Void> fileProcessed) {
        state = "Processed at " + taskList.get();
        ActivitySchedulingOptions options = new ActivitySchedulingOptions().withTaskList(taskList.get());
        storeClient.upload(targetBucketName, localTargetFilename, targetFilename, options);
    }
	
	@Override
	public String getState() {
		return state;
	}
}