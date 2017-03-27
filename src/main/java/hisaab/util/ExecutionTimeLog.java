package hisaab.util;

public class ExecutionTimeLog {

	private long start = 0l ;
	
	private long stop = 0l;
	
	private long nanoStart = 0l ;
	
	private long nanoStop = 0l;
	
	private long elapsed = 0l;
	
	private String method ="";
	
	private String userId = "";
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public void start(){
		
		start = System.currentTimeMillis();
		nanoStart = System.nanoTime();
	}
	public void stop(){
		stop = System.currentTimeMillis();
		nanoStop = System.nanoTime();
	}
	public long stopTime(){
		
		return stop;
	}
	public long startTime(){
		
		return start;
	}
	public long elapsedTime(){
		elapsed = (stop - start);
		return elapsed;
	}
	
	@Override
    public String toString() {
        return method+","+DateHelper.getDateString(start)+","+DateHelper.getDateString(stop)+","+(stop - start)+","+(nanoStop - nanoStart)+"\n";

    }
	public void setMethodName(String string) {
		method = string;
		
	}
}
