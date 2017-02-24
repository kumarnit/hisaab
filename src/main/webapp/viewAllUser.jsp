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
<%@ page import="hisaab.util.*" %>
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

<%
String userName = null;
Cookie[] cookies = request.getCookies();

if(cookies !=null){
for(Cookie cookie : cookies){
	if(cookie.getName().equals("user")) {userName = cookie.getValue();
		if(userName.equalsIgnoreCase("admin")){
		
			out.print("<body><h3 align=\"center\"> All User list </h3><table id=\"itemExpiryTable\" class=\"display\" cellspacing=\"0\" width=\"75%\"><thead><tr><th>Id</th><th align=\"center\">User Name</th><th align=\"center\">Contact No</th><th align=\"center\">Created On</th><th align=\"center\">Transaction Count</th><th align=\"center\">Status</th></tr></thead><tbody></tbody></table></body>");
		}
	 }
}
}
if(userName == null) response.sendRedirect("main.jsp");
%>
<%-- <h3>Hi <%=userName %>, Login successful.</h3> --%>
<br>
<form action="RespSer" method="post">
<input type="submit" value="Logout" >
</form>


<!-- <body>

	
	<h3 align="center"> All User list </h3>

	<br />
	
	

	<table id="itemExpiryTable" class="display" cellspacing="0" width="75%">
		
		<thead>
			<tr>
				<th>Id</th>
				<th align="center">User Name</th>
				<th align="center">Contact No</th>
				<th align="center">Created On</th>
				<th align="center">Transaction Count</th>
				<th align="center">Status</th>
			</tr>
		</thead>
		<tbody>
				</tbody>
		
	</table>
	

	
	</body> -->
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
		            url: "rest/v1/viewlogs/get/userdetailAll",
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


</html>