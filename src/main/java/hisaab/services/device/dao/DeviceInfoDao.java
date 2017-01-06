package hisaab.services.device.dao;

import org.codehaus.jackson.map.ObjectMapper;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import hisaab.config.morphia.MorphiaDatastoreTrasaction;
import hisaab.services.device.modal.DeviceInfo;
import hisaab.services.device.modal.DeviceInfoDoc;
import hisaab.services.user.modal.UserMaster;

public class DeviceInfoDao {

	public static DeviceInfoDoc getDeviceInfoDoc(UserMaster user) {
		
			Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(DeviceInfoDoc.class);
			DeviceInfoDoc deviceInfoDoc = null;
			Query<DeviceInfoDoc> query = datastore.createQuery(DeviceInfoDoc.class);
			query.field("_id").equal(""+user.getUserId());
			if(query.get() != null){
				ObjectMapper mapper = new ObjectMapper();
				deviceInfoDoc = query.get();
			}            
			else{
				deviceInfoDoc = new DeviceInfoDoc();
				deviceInfoDoc.setUserId(""+user.getUserId());
				deviceInfoDoc.setCreatedTime(System.currentTimeMillis());
				deviceInfoDoc.setUpdatedTime(System.currentTimeMillis());
				datastore.save(deviceInfoDoc);
			}
			return deviceInfoDoc;
		
	}

	public static boolean setDeviceInfo(DeviceInfoDoc deviceInfodoc) {
		boolean flag = false;
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(DeviceInfoDoc.class);
		Query<DeviceInfoDoc> query = datastore.createQuery(DeviceInfoDoc.class);
        long epoch = System.currentTimeMillis();
        deviceInfodoc.getDevice().get(0).setCreatedTime(epoch);
        deviceInfodoc.getDevice().get(0).setId(1+deviceInfodoc.getIdCount());
		query.field("_id").equal(deviceInfodoc.getUserId());
		UpdateOperations<DeviceInfoDoc> op = datastore.createUpdateOperations(DeviceInfoDoc.class);
		op.set("updatedTime", epoch);
		op.set("idCount",deviceInfodoc.getIdCount()+1);
		op.addAll("device", deviceInfodoc.getDevice(),false);
		UpdateResults ur = datastore.update(query,op );
		if(ur.getUpdatedCount()>0){
			flag = true;
		}

		return flag;
		
	}

	public static boolean checkAndUpdateForDeviceId(DeviceInfoDoc deviceInfodoc) {
		boolean flag = false;
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(DeviceInfoDoc.class);
		Query<DeviceInfoDoc> query = datastore.createQuery(DeviceInfoDoc.class);
        DeviceInfo deviceInfo = deviceInfodoc.getDevice().get(0);
		long epoch = System.currentTimeMillis();
        
		query.field("_id").equal(deviceInfodoc.getUserId());
		query.filter("device.deviceId", deviceInfodoc.getDevice().get(0).getDeviceId());
		UpdateOperations<DeviceInfoDoc> op = datastore.createUpdateOperations(DeviceInfoDoc.class);
		op.disableValidation();
		op.set("updatedTime", epoch);
		op.set("device.$.updatedTime", epoch);
		op.set("device.$.device", deviceInfo.getDevice());
		op.set("device.$.osVersion", deviceInfo.getOsVersion());
		op.set("device.$.osApiLevel", deviceInfo.getOsApiLevel());
		op.set("device.$.release", deviceInfo.getRelease());
		op.set("device.$.brand", deviceInfo.getBrand());
		op.set("device.$.display", deviceInfo.getDisplay());
		op.set("device.$.maufacturer", deviceInfo.getMaufacturer());
		op.enableValidation();
		UpdateResults ur = datastore.update(query,op );
		if(ur.getUpdatedCount()>0){
			flag = true;
		}

		return flag;
	}

}
