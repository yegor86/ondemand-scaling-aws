package swf.agent.fileprocessing;

import com.amazonaws.services.simpleworkflow.flow.annotations.Activities;
import com.amazonaws.services.simpleworkflow.flow.annotations.ActivityRegistrationOptions;

@Activities(version="2017-02-13")
@ActivityRegistrationOptions(defaultTaskScheduleToStartTimeoutSeconds = 300, defaultTaskStartToCloseTimeoutSeconds = 3000)
public interface FileProcessingActivity {

    public void processFile(String inputFileName, String outputFileName) throws Exception;
    
}