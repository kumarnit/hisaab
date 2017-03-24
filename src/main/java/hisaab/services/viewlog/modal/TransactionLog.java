package hisaab.services.viewlog.modal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="transaction_log")
public class TransactionLog {
	
		@Id
		@GeneratedValue(strategy=GenerationType.IDENTITY)
		@Column(name="id", length=20)
		private long id;
		
		@Column(name="contact_no", length=10)
		private String contactNo;
		
		@Column(name="contact_name",length=50)
		private String contactName;
		
		@Column(name="created_time", length=20)
		private long createdTime;
		
		@Column(name="transaction_type", length=2)
		private long transType;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getContactNo() {
			return contactNo;
		}

		public void setContactNo(String contactNo) {
			this.contactNo = contactNo;
		}

		public String getContactName() {
			return contactName;
		}

		public void setContactName(String contactName) {
			this.contactName = contactName;
		}

		public long getCreatedTime() {
			return createdTime;
		}

		public void setCreatedTime(long createdTime) {
			this.createdTime = createdTime;
		}

		public long getTransType() {
			return transType;
		}

		public void setTransType(long transType) {
			this.transType = transType;
		}
		
		
		
}
