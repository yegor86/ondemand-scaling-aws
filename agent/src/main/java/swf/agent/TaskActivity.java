package swf.agent;

import com.amazonaws.services.simpleworkflow.flow.annotations.Activities;
import com.amazonaws.services.simpleworkflow.flow.annotations.ActivityRegistrationOptions;
import com.amazonaws.services.simpleworkflow.flow.annotations.ExponentialRetry;

@Activities(version = "2017-01-01")
@ActivityRegistrationOptions(defaultTaskHeartbeatTimeoutSeconds = 300, defaultTaskScheduleToStartTimeoutSeconds = 3000, defaultTaskStartToCloseTimeoutSeconds = 3000)
public interface TaskActivity {

	@ExponentialRetry(initialRetryIntervalSeconds = 10, maximumAttempts = 3)
	void execute(Object task) throws Exception;
}
