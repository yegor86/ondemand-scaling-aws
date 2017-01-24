package swf.agent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

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
			s3client.putObject(new PutObjectRequest(bucketName, "file.txt", createSampleFile()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private static File createSampleFile() throws IOException {
        File file = File.createTempFile("test", ".txt");
        file.deleteOnExit();

        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write("Hello\n");
        writer.write("world\n");
        writer.write("!@#$%^&*()-=[]{};':',.<>/?\n");
        writer.write("01234567890112345678901234\n");
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.close();

        return file;
    }
}
