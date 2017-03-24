package hisaab.util;



public class ExcecutorHelper {
public static void addExecutionLog(final String content){
	Constants.executorService.execute(new Runnable() {
		public void run() {
			/*try {
				System.out.println("wait");
				this.wait(500l);
				System.out.println("after");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			CustomProfileLogs.writeToLogFile("E:\\",content);
		}
		
	});
}
}
