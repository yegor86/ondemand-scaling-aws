package swf.agent.fileprocessing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileProcessingActivityImpl implements FileProcessingActivity {

    private final String localDirectory;

    public FileProcessingActivityImpl(String localDirectory) {
        this.localDirectory = localDirectory;
    }

    /**
     * This is the Activity implementation that does the zip of a file
     * 
     * @param localDirectory
     *            Path to the local directory containing the file to zip
     * @param fileName
     *            Name of file to zip
     * @param zipFileName
     *            Filename after zip
     */
    @Override
    public void processFile(String fileName, String zipFileName) throws Exception {
        String fileNameFullPath = localDirectory + fileName;
        String zipFileNameFullPath = localDirectory + zipFileName;

        System.out.println("processFile activity begin.  fileName= " + fileNameFullPath + ", zipFileName= " + zipFileNameFullPath);
        final int BUFFER = 1024;
        BufferedInputStream origin = null;
        ZipOutputStream out = null;

        try {
            FileOutputStream dest = new FileOutputStream(zipFileNameFullPath);
            out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER];

            FileInputStream fi = new FileInputStream(fileNameFullPath);
            origin = new BufferedInputStream(fi, BUFFER);
            ZipEntry entry = new ZipEntry(fileName);
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
        }
        finally {
            if (origin != null)
                origin.close();
            if (out != null)
                out.close();
        }

        System.out.println("zipFileActivity done.");
    }
}