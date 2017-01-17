<%@page import="java.util.Date"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.TimeZone"%>
<%@page import="java.util.Calendar"%>
<%-- <%@page import="iapp.com.invoice.util.Constants"%>
<%@page import="iapp.com.invoice.app.invoice.webservices.bean.InvoiceLogBean"%>
<%@page import="iapp.com.invoice.app.invoice.modal.InvLogDetail"%> --%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="java.util.ArrayList" %>
<%-- <jsp:useBean id="invBean" class="iapp.com.invoice.app.invoice.dao.InvoiceDao" scope="page" /> --%>
<%

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>LenaDena</title>
<link href="css/style.css" rel="stylesheet">
<!-- <link href="resources/bootstrap/css/bootstrap.min.css" rel="stylesheet">  -->
<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" />
<!-- <link rel="stylesheet"
	href="resources/bootstrap/css/bootstrap-theme.min.css" />-->
<script type="text/javascript" src="js/jquery.js"></script> 
 <link href="resources/datatable/css/jquery.dataTables.css" rel="stylesheet" /> 
<script type="text/javascript" charset="utf-8" src="resources/datatable/js/jquery.dataTables.min.js"></script>
<!--<link rel="stylesheet" type="text/css" href="css/style.css" /> -->
</head>
<body>
<%-- <%@include file="loader.jsp" %> --%>
	
	<%
		
	Calendar calendar = new GregorianCalendar();
	

	DateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");

	
	

	 %>
	
	<h3 align="center"> Lena Dena User list </h3>

	<br />
	
	<!--  <div class="">
	<div class="col-sm-3">
			<div class="form-group selectdevice">
			  <label for="sel1">Select Device:</label>
			  <select class="form-control" id="sel1">
					    <option value="1"> IOS</option>
					    <option value="2">Android</option>					 
			  </select>
			</div>
		</div>
		
		<div class="col-sm-offset-6 col-sm-3 search-field">
			<label>Search :</label>
			<div class="col-sm-12">
			<div class="col-sm-9">			
				<input type="text" id="searchTxt" name="" class="form-control" >
			</div>
		    <div class="col-sm-3">
				<button type="button" id="searchbtn">Search</button>
			</div>
			</div>
		</div>
	</div> -->

	<table id="itemExpiryTable" class="display" cellspacing="0" width="75%">
		
		<thead>
			<tr>
				<th>Id</th>
				<th align="center">User Name</th>
				<th align="center">Contact No</th>
				<th align="center">Created On</th>
				
			</tr>
		</thead>
		<tbody>
				</tbody>
		
	</table>
	

	
	
	<script type="text/javascript">
		
	var editor; // use a global for the submit and return data rendering in the examples
		
		$(document).ready(function(){
			
			var dev = $("#sel1").val();
			
			$("#itemExpiryTable").DataTable( {
		        "order": [[ 0, "desc" ]],
		        "pageLength": 100,
		        "processing": true,
		        "serverSide": true,
		        "sortClasses":false,
		        "searching":false,
		        "pagingType": "full",
		        "dom": '<"top"iflp<"clear">>rt<"bottom"iflp<"clear">>',
		        "ajax": {
		            url: "rest/v1/viewlogs/get/userdetail",
		            type: 'GET'
		            
		        }
		         
		    } );
			
			//var cell;
						
			
		});
	
		function validateItem(expiryInMonths){

			var flag = true;
			if(isNaN(expiryInMonths)){
				flag = false;
			}
			console.log("Flag = "+expiryInMonths);

			return flag;
		}
		
		
		/* $('#searchbtn').click(function() {
			var txt = $("#searchTxt").val();
		    window.location.href = 'InvoiceSearch.jsp?searchTerm='+txt;
		    return false;
		}); */
	</script>

</body>
</html>