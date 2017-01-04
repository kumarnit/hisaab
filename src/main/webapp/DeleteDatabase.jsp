<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<link href="css/style.css" rel="stylesheet">
<link href="resources/bootstrap/css/bootstrap.min.css" rel="stylesheet"> 
<style>

.button {
  display: inline-block;
  padding: 15px 25px;
  font-size: 24px;
  cursor: pointer;
  text-align: center;
  text-decoration: none;
  outline: none;
  color: #fff;
  background-color: #4CAF50;
  border: none;
  border-radius: 15px;
  box-shadow: 0 9px #999;
}

.button:hover {background-color: #3e8e41}

.button:active {
  background-color: #3e8e41;
  box-shadow: 0 5px #666;
  transform: translateY(4px);
}

</style>

</head>
<body>
<div class="container home-wrapper">

		<button class="button" id="deleteDatabase" style="vertical-align:middle">Delete Database</button>

</div>

<div class="col-sm-12" style="text-align:center">
	<div id="status"></div>
</div>

<div class="col-sm-12 loader" style="text-align:center"><img  src="images/loader.gif"  /></div>

</body>
<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript">

$(".loader").hide();

$(document).ready(function () {

 $('#deleteDatabase').click(function () {
	 		$(".loader").show();
            $.ajax({          
            url: 'rest/v1/transaction/delete/Database',
            type: 'GET',
            success:function(){
            	$(".loader").hide();
            	console.log(" successfully delete");
            	$("#status").html("<b style='color: rgba(31, 35, 95, 0.74);'>Sucessfully Deleted!</b>");
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



</script>

</html>