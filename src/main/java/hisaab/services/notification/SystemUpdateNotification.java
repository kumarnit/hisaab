package hisaab.services.notification;

import java.util.ArrayList;
import java.util.List;

public class SystemUpdateNotification {

	
	private int notificationType;
	
	private String minStableAppVersion;
	
	private String maxAvialableVersion;
	
	private String stopSupportForVersion;
	
	private long lastDate;
	
	public long getLastDate() {
		return lastDate;
	}

	public void setLastDate(long lastDate) {
		this.lastDate = lastDate;
	}

	private List<String> newFeatures = new ArrayList<String>();
	

	public List<String> getNewFeatures() {
		return newFeatures;
	}

	public void setNewFeatures(List<String> newFeatures) {
		this.newFeatures = newFeatures;
	}

	public int getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(int notificationType) {
		this.notificationType = notificationType;
	}

	public String getMinStableAppVersion() {
		return minStableAppVersion;
	}

	public void setMinStableAppVersion(String minStableAppVersion) {
		this.minStableAppVersion = minStableAppVersion;
	}

	public String getMaxAvialableVersion() {
		return maxAvialableVersion;
	}

	public void setMaxAvialableVersion(String maxAvialableVersion) {
		this.maxAvialableVersion = maxAvialableVersion;
	}

	public String getStopSupportForVersion() {
		return stopSupportForVersion;
	}

	public void setStopSupportForVersion(String stopSupportForVersion) {
		this.stopSupportForVersion = stopSupportForVersion;
	}
	
	
	
}
