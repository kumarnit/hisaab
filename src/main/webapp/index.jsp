<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<link href="css/style.css" rel="stylesheet">
<link href="resources/bootstrap/css/bootstrap.min.css" rel="stylesheet"> 


</head>
<body>

<!-- <div class="container home-wrapper">

		<button class="button" id="viewuser" style="vertical-align:middle">View User</button>

</div> -->
<div class="container home-wrapper">
<div id="status" align="center">&nbsp;</div>

<div class="top-buttons col-sm-12">
<div class="col-sm-6 left-div">
          <button class="btn btn-primary" id="viewuser" >View User</button>
          
</div>
<div class="col-sm-6 right-div">
          <button class="btn btn-primary" id="viewalluser" >View All user</button>
          
</div>
<div class="col-sm-6 left-div">
          <button class="btn btn-primary" id="setCount" >Set Transaction Count</button>
          
</div>
<div class="col-sm-6 right-div">
          <button class="btn btn-primary" id="viewUserTransCount" >View Transaction Count</button>
          
</div>
</div>
</div>
</body>
<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript">
/* $(document).ready(function () { */
$(document).ready(function () {
$('#viewuser').click(function() {
    window.location.href = 'viewUser.jsp';
    return false;
});
$('#viewalluser').click(function() {
    window.location.href = 'viewAllUser.jsp';
    return false;
});
$('#viewUserTransCount').click(function() {
    window.location.href = 'viewUserTransCount.jsp';
    return false;
});
$('#setCount').click(function () {
    $.ajax({          
    url: 'rest/v1/viewlogs/set/transactionCount',
    type: 'GET',
    success:function(){
    	console.log(" successfully Archived");
    	$("#status").html("<b style='color: green;'>Sucessfully Archived!</b>");
    	hideStatus();
    },
    error:function(){
    	console.log("error");
    }
});

})

function hideStatus(){
	
	setTimeout(function(){ $("#status").html("&nbsp;"); }, 5000);
}

})
 
/* $('#setCount').click(function() {
    window.location.href = 'rest/v1/viewlogs/set/transactionCount';
    return false;
}); */
/* $('#invUsers').click(function() {
    window.location.href = 'http://invoicelogs-tacktilesys.rhcloud.com/input.jsp';
    return false;
}); */


  
  /*  $('#generateExc').click(function() {
	    window.location.href = 'rest/v1/service/invoice/get/excelfile1';
	    return false;
	}); */
 

/* })

function hideStatus(){
	
	setTimeout(function(){ $("#status").html("&nbsp;"); }, 5000);
}
   
}) */

/* $('#generateExc').click(function () {
   	 
        $.ajax({          
        url: 'rest/v1/service/invoice/get/excelfile',
        type: 'GET',
        success:function(data){
        	console.log(" successfully delete",data);
        	JSONToCSVConvertor(data,  true);
        	       },
        error:function(){
        	console.log("error");
        }
        
        });

    })
  */  


function JSONToCSVConvertor(JSONData,  ShowLabel) {
    //If JSONData is not an object then JSON.parse will parse the JSON string in an Object
    var arrData = typeof JSONData != 'object' ? JSON.parse(JSONData) : JSONData;
    
    var CSV = '';    
    //Set Report title in first row or line
    
    

    //This condition will generate the Label/Header
     if (ShowLabel) {
        var row = "";
        
        row='Android_id,Organization Name,MIN_VER,MAX_VER,MIN_Inv,MAX_Inv,MIN_time,MAX_time,Contact Person,OrganizationEmail,Language,Google Account,Dev_type,Pur_stat'
        
        //append Label row with line break
        CSV += row + '\r\n';
    }
     /*if (ShowLabel) {
        var row = "";
        
        //This loop will extract the label from 1st index of on array
        for (var index in arrData[0]) {
            
            //Now convert each value to string and comma-seprated
            row += index + ',';
        }

        row = row.slice(0, -1);
        
        //append Label row with line break
        CSV += row + '\r\n';
    }*/
    
    //1st loop is to extract each row
    for (var i = 0; i < arrData.length; i++) {
        var row = "";
        
        //2nd loop will extract each column and convert it in string comma-seprated
        for (var index in arrData[i]) {
            row += '"' + arrData[i][index] + '",';
        }

        row.slice(0, row.length - 1);
        
        //add a line break after each row
        CSV += row + '\r\n';
    }

    if (CSV == '') {        
        alert("Invalid data");
        return;
    }   
    
    //Generate a file name
    var fileName = "User Report";
    //this will remove the blank-spaces from the title and replace it with an underscore
   // fileName += ReportTitle.replace(/ /g,"_");   
    
    //Initialize file format you want csv or xls
    var uri = 'data:text/csv;charset=utf-8,' + escape(CSV);
    
    // Now the little tricky part.
    // you can use either>> window.open(uri);
    // but this will not work in some browsers
    // or you will not get the correct file extension    
    
    //this trick will generate a temp <a /> tag
    var link = document.createElement("a");    
    link.href = uri;
    
    //set the visibility hidden so it will not effect on your web-layout
    link.style = "visibility:hidden";
    link.download = fileName + ".csv";
    
    //this part will append the anchor tag and remove it after automatic click
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

</script>

</html>
