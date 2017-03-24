package hisaab.util;

public class ExecutionTimeLog {

private long start = 0l ;
	
	private long stop = 0l;
	
	private long elapsed = 0l;
	
	private String method ="";
	
	public void start(){
		
		start = System.currentTimeMillis();
	}
	public void stop(){
		stop = System.currentTimeMillis();
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
        return method+","+start+","+stop+","+(stop - start)+"\n";

    }
	public void setMethodName(String string) {
		method = string;
		
	}
}
