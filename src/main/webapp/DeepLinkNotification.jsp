<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Update Detail</title>
<link href="resources/bootstrap/css/bootstrap.min.css" rel="stylesheet">

<script src="js/jquery.js" type="text/javascript"></script>
<script src="resources/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
<style>
.entry:not(:first-of-type)
{
    margin-top: 10px;
}

.glyphicon
{
    font-size: 12px;
}
</style>

</head>
<body>
<!--  
<div class="container">
	<div class="row">
        <div class="control-group" id="fields">
        <form action="" method="get">
        <label class="control-label" for="field1">Min App Version</label>
        <input class="form-control" name="min" type="text" placeholder="Type something" />
        <label class="control-label" for="field1">Max Availble Version</label>
        <input class="form-control" name="fields[]" type="text" placeholder="Type something"/>
        <label class="control-label" for="field1">Stop Support Version</label>
        <input class="form-control" name="fields[]" type="text" placeholder="Type something"/>
            
            </form>
            <label class="control-label" for="field1">Features</label>
            <div class="controls"> 
                <form role="form" autocomplete="off" action="" method ="get">
                    <div class="entry input-group col-sm-3">
                        <input class="form-control" name="fields[]" type="text" placeholder="Type something" />
                    	<span class="input-group-btn">
                            <button class="btn btn-success btn-add" type="button">
                                <span class="glyphicon glyphicon-plus"></span>
                            </button>
                        </span>
                    </div>
                    <input type="submit" value="submit">
                </form>
            <br>
            <small>Press <span class="glyphicon glyphicon-plus gs"></span> to add Features :)</small>
            </div>
        </div>
	</div>
</div>

-->
<div class="controls">
<form  id="form1">
	
 	<div class="formcol">
	    <label for="msg">Message</label>
	    <input type="text" id="msg" name="msg" required />
  </div>
  <div class="formcol">
	    <label for="link">Link</label>
	    <input type="text" id="link" name="link" required/>
  </div>
  
 
  <input type="submit" value="submit" id="submit1">
</form>
</div>
<div id="status"></div>
<script>
$('#form1').on('submit', function (e){
	   e.preventDefault();
	   $('#submit1').prop('disabled', true);
	   var msg = $('#msg').val();
	   var link = $('#link').val();
	  
	   var obj = {"msg":msg,"link":link}
	  /*  obj.upcNum = upc;
	   obj.itemNum = $('#itemNum').val(); */
	   console.log("i m in");
	   console.log(obj)
	   if(obj != ""){
	     /*  var datastring = $("#obj").serialize();
	      console.log(datastring); */
	     $.ajax({
	               type:"POST",
	               url: "rest/v1/notify/deepLink",
	               data:  JSON.stringify(obj),
	               contentType: "application/json; charset=utf-8",
	               dataType: "json",
	               success: function(data) {
	                 console.log("in success ",data);
	                 
	          	  if(data.status == 200){
		          	  $('#msg').val("");
		          	   $('#link').val("");
	                  $("#status").html("<b style='color: green;'>Sent Successfully.</b>");
	        			$("#status").show();
	                 $('#submit1').prop('disabled', false);
	                 hideStatus();
	        }
	               
	                 
	               }
	     
         
	     });
	   }else{
	    $('#upcDiv').addClass('has-error');
	    $('#upcSpan').show();
	   }
	   function hideStatus(){
		   
		   setTimeout(function(){ $("#status").fadeOut(200); }, 3000);
		  }
	  });
</script>
</body>
</html>