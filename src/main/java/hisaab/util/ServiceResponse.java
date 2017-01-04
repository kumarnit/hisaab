package hisaab.util;

public class ServiceResponse {

	private int status;

	private String msg;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String message) {
		this.msg = message;
	}

	public static ServiceResponse getResponse(int status, String message) {

		ServiceResponse response = new ServiceResponse();
		response.setStatus(status);
		response.setMsg(message);

		return response;
	}
	
	
	
}
