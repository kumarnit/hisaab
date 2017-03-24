package hisaab.services.viewlog.webservices;

import java.util.List;

import hisaab.app.cronjob.TransactionCount;
import hisaab.services.user.modal.UserProfile;
import hisaab.services.viewlog.dao.TransactionLogDao;
import hisaab.services.viewlog.dao.UserLogDao;
import hisaab.services.viewlog.helper.ViewLogHelper;
import hisaab.services.viewlog.modal.TransactionLog;
import hisaab.services.viewlog.webservices.bean.PageRequest;
import hisaab.util.ServiceResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("v1/viewlogs")
public class ViewLogServices {
 
	@GET
	@Path("/get/userdetail")
	@Produces("application/json")
	@Consumes("application/json")
	public static Response getUserDetails(@QueryParam("start") int start, @QueryParam("length") int length,
			@QueryParam("draw") int draw, @QueryParam("order[0][column]") int order,
			@QueryParam("order[0][dir]") String ord){
		PageRequest resp = new PageRequest();
		resp.setDraw(draw);
		resp.setRecordsFiltered(length);
		resp.setStart(start);
		resp.setLength(length);
		List<UserProfile> userprofile = UserLogDao.getUserDetail(start, length, ord , order);
		ViewLogHelper.manageResponse(userprofile, resp);
		
		return Response.status(200).entity(resp).build();
	}
	
	@GET
	@Path("/get/userdetailAll")
	@Produces("application/json")
	@Consumes("application/json")
	public static Response getUserDetailsAll(@QueryParam("start") int start, @QueryParam("length") int length,
			@QueryParam("draw") int draw, @QueryParam("order[0][column]") int order,
			@QueryParam("order[0][dir]") String ord){
		PageRequest resp = new PageRequest();
		resp.setDraw(draw);
		resp.setRecordsFiltered(length);
		resp.setStart(start);
		resp.setLength(length);
		List<UserProfile> userprofile = UserLogDao.getUserDetailAll(start, length, ord , order);
		ViewLogHelper.manageResponseAll(userprofile, resp);
		
		return Response.status(200).entity(resp).build();
	}
	
	@GET
	@Path("/set/transactionCount")
	@Produces("application/json")
	@Consumes("application/json")
	public static Response setTransactionCount(){
		TransactionCount.setTransactionCount();
		Object result = ServiceResponse.getResponse(200, "sucess");
		return Response.status(200).entity(result).build();
	}

	@GET
	@Path("/get/activeUserdetail")
	@Produces("application/json")
	@Consumes("application/json")
	public static Response getActiveUserDetailsAll(@QueryParam("start") int start, @QueryParam("length") int length,
			@QueryParam("draw") int draw, @QueryParam("order[0][column]") int order,
			@QueryParam("order[0][dir]") String ord){
		PageRequest resp = new PageRequest();
		resp.setDraw(draw);
		resp.setRecordsFiltered(length);
		resp.setStart(start);
		resp.setLength(length);
		List<UserProfile> userprofile = UserLogDao.getActiveUserDetailAll(start, length, ord , order);
		ViewLogHelper.manageResponseTransCount(userprofile, resp);
		
		return Response.status(200).entity(resp).build();
	}
	
	@GET
	@Path("/get/transactionLog")
	@Produces("application/json")
	@Consumes("application/json")
	public static Response getTransactionLog(@QueryParam("start") int start, @QueryParam("length") int length,
			@QueryParam("draw") int draw, @QueryParam("order[0][column]") int order,
			@QueryParam("order[0][dir]") String ord){
		PageRequest resp = new PageRequest();
		resp.setDraw(draw);
		resp.setRecordsFiltered(length);
		resp.setStart(start);
		resp.setLength(length);
		List<TransactionLog> transactionLog = TransactionLogDao.getTransactionLog(start, length, ord , order);
		ViewLogHelper.manRespFortranLog(transactionLog, resp);
		
		return Response.status(200).entity(resp).build();
	}
}
