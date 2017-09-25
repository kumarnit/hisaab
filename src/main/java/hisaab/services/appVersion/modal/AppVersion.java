package hisaab.services.appVersion.modal;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * this is a object mapping class for app_version table in mySql
 * This is for keeping track of which app version is current and which versions are outdated*/
@Entity
@Table(name = "app_version")
public class AppVersion {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", length=11)
	private long id;
	
	@Column(name="stopped_version", length=20)
	private int stoppedVersion ;
	
	@Column(name="last_date", length=20)
	private long date;
	
	@Column(name="current_version", length=20)
	private int currentVersion;
	
	@Column(name="created_time", length=20)
	private long createdTime;
	
	@Column(name="latest_features",columnDefinition="TEXT")
	private  String latestFeatures = "";

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getStoppedVersion() {
		return stoppedVersion;
	}

	public void setStoppedVersion(int stoppedVersion) {
		this.stoppedVersion = stoppedVersion;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public int getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(int currentVersion) {
		this.currentVersion = currentVersion;
	}

	public Long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Long createdTime) {
		this.createdTime = createdTime;
	}

	public String getLatestFeatures() {
		return latestFeatures;
	}

	public void setLatestFeatures(String latestFeatures) {
		this.latestFeatures = latestFeatures;
	}
	
	
	

}
