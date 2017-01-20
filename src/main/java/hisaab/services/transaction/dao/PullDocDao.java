package hisaab.services.transaction.dao;

import hisaab.config.morphia.MorphiaDatastoreTrasaction;
import hisaab.services.pull.modal.PullDoc;
import hisaab.services.transaction.modal.Transaction;
import hisaab.services.transaction.modal.TransactionDoc;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

public class PullDocDao {

	public static PullDoc getPullDoc(PullDoc pullDoc ){
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(PullDoc.class);
		Query<PullDoc> query = datastore.createQuery(PullDoc.class);
		query.field("userId").equal(pullDoc.getUserId());
		if(query.get() != null){
			
			pullDoc = query.get();
		}
		else{
			
			datastore.save(pullDoc);
		}
		return pullDoc;
	}
	
	public static void addTransaction(TransactionDoc transactionDoc, PullDoc pullDoc) {
			
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(PullDoc.class);
		Query<PullDoc> query = datastore.createQuery(PullDoc.class);
		query.field("userId").equal(pullDoc.getUserId());
		
		UpdateOperations<PullDoc> op = datastore.createUpdateOperations(PullDoc.class);

		op.addAll("transactionList", transactionDoc.getTransactions(),false);
		UpdateResults ur = datastore.update(query,op );
		int stat = 0;
		if(ur != null)
			stat = ur.getUpdatedCount();
		
		}

	public static void addModifiedTransaction(TransactionDoc transactionDoc,
			PullDoc pullDoc) {
		
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(PullDoc.class);
		Query<PullDoc> query = datastore.createQuery(PullDoc.class);
		query.field("userId").equal(pullDoc.getUserId());
		
		UpdateOperations<PullDoc> op = datastore.createUpdateOperations(PullDoc.class);

		op.addAll("modifiedTransactionList", transactionDoc.getTransactions(),false);
		UpdateResults ur = datastore.update(query,op );
		int stat = 0;
		if(ur != null)
			stat = ur.getUpdatedCount();
		
	}
}
