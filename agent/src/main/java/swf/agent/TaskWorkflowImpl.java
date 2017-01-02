package swf.agent;

import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.core.TryCatchFinally;

public class TaskWorkflowImpl implements TaskWorkflow {

	private String state = "";
	private final TaskActivityClient activityClient;
	
	public TaskWorkflowImpl() {
		activityClient = new TaskActivityClientImpl();
	}
	
	@Override
	public void process() {
		
		new TryCatchFinally() {

            @Override
            protected void doTry() throws Throwable {
            	
            	Promise<Void> status = activityClient.execute(new Object());
            }

            @Override
            protected void doCatch(Throwable e) throws Throwable {
                state = "Workflow Failed";
                throw e;
            }

            @Override
            protected void doFinally() throws Throwable {
                if (!state.startsWith("Workflow Failed")) {
                    state = String.format("Workflow Completed");
                }
            }
	    };
		
	}
	
	@Override
	public String getState() {
		return state;
	}
}