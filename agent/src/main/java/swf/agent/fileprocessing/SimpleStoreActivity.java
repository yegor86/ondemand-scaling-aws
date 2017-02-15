package swf.agent.fileprocessing;

import com.amazonaws.services.simpleworkflow.flow.annotations.Activities;
import com.amazonaws.services.simpleworkflow.flow.annotations.ActivityRegistrationOptions;
import com.amazonaws.services.simpleworkflow.flow.annotations.ExponentialRetry;

@Activities(version = "2017-02-13")
@ActivityRegistrationOptions(defaultTaskScheduleToStartTimeoutSeconds = 300, defaultTaskStartToCloseTimeoutSeconds = 3000)
public interface SimpleStoreActivity {
 
    /**
     * 
     * @param localName
     *          Name of the file to upload from temporary directory
     * @param remoteName
     *          Name of the file to use on S3 bucket after upload
     * @param fromBox
     *          Machine name which has the file that needs to be uploaded
     * @return
     */
    @ExponentialRetry(initialRetryIntervalSeconds = 10,  maximumAttempts = 10) 
    public void upload(String bucketName, String localName, String targetName);
    /**
     * 
     * @param remoteName 
     *          Name of the file to download from S3 bucket 
     * @param localName
     *          Name of the file used locally after download
     * @param toBox 
     *          This is an output parameter here.  
     *          Used to communicate the name of the box that runs download activity
     */
    @ExponentialRetry(initialRetryIntervalSeconds = 10, maximumAttempts = 10)
    public String download(String bucketName, String remoteName, String localName) throws Exception;
    /**
     * 
     * @param fileName 
     *          Name of file to delete from temporary folder
     * @param machineName
     *          Machine which has the file locally 
     * @return
     */
    @ExponentialRetry(initialRetryIntervalSeconds=10)
    public void deleteLocalFile(String fileName);

}
