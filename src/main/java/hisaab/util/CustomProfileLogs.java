package hisaab.util;




import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;

public class CustomProfileLogs {
	
	public static void writeToLogFile(String contextPath, String content){
		
		if(!Constants.DEV_MODE){
//			contextPath = System.getenv("OPENSHIFT_LOG_DIR");
			contextPath = Constants.LOG_FILE_PATH;
		}
		
		File logFile = new File(contextPath+Constants.SERVICE_LOG_FILE);
		
		try{
		
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			FileWriter fw = new FileWriter(logFile.getAbsoluteFile(),true);
			   BufferedWriter bw = new BufferedWriter(fw);
			   bw.write(content);
			   bw.close();
	
			/*RandomAccessFile f = new RandomAccessFile(logFile, "rw");
			f.seek(0); // to the beginning
			f.write(content.getBytes());
			f.close();
	*/
		/*	Path filePath = Paths.get(path+Constants.PURCHASE_LOG_FILE);
			if (!Files.exists(filePath)) {
			    Files.createFile(filePath);
			}
			Files.write(filePath, data.getBytes(), StandardOpenOption.APPEND);*/
			System.out.println("Done");
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
	}
}
