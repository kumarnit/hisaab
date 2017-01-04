package hisaab.services.notification.android.fcm;

import java.util.List;


import org.codehaus.jackson.annotate.JsonSetter;

public class MulticastResultFcm {
	
	
	private long multicast_id;
	
	private int success;
	
	private int failure;
	
	private int conical_ids;
	
	private List<Result> results;

	public long getMulticast_id() {
		return multicast_id;
	}

	public int getSuccess() {
		return success;
	}

	public int getFailure() {
		return failure;
	}

	public int getConical_ids() {
		return conical_ids;
	}

	public List<Result> getResults() {
		return results;
	}

	@JsonSetter("multicast_id")
	public void setMulticast_id(long multicast_id) {
		this.multicast_id = multicast_id;
	}

	@JsonSetter("success")
	public void setSuccess(int success) {
		this.success = success;
	}

	@JsonSetter("failure")
	public void setFailure(int failure) {
		this.failure = failure;
	}

	@JsonSetter("canonical_ids")
	public void setConical_ids(int conical_ids) {
		this.conical_ids = conical_ids;
	}

	@JsonSetter("results")
	public void setResults(List<Result> results) {
		this.results = results;
	}
	
	
	
}
