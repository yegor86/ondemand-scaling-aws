package swf.agent;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class TaskActivityImpl implements TaskActivity {
	
	private String bucketName = "ic-ondeman-scaling";
	
	public TaskActivityImpl() {
		
	}
	
	@Override
	public void execute(Object task) {
		try {
			AmazonS3 s3client = new AmazonS3Client(new InstanceProfileCredentialsProvider());
			s3client.putObject(new PutObjectRequest(bucketName, "file.txt", "Hello world"));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
