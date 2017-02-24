<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>System Update Notification</title>
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
<form  role="form" action="NotificationServlet" method="get">
	<div class="formcol">
	    <label for="min">Min App Version</label>
	    <input type="text" id="min" size="24" name="min" />
 	</div>
 	<div class="formcol">
	    <label for="max">Max Available Version</label>
	    <input type="text" id="max" name="max" size="2" />
  </div>
  <div class="formcol">
    <div class="entry input-group col-sm-3">
           <input class="form-control feature" id="feature" name="fields[]" type="text" placeholder="Type something" />
           <span class="input-group-btn">
                   <button class="btn btn-success btn-add" type="button">
                            <span class="glyphicon glyphicon-plus"></span>
                  </button>
           </span>
    </div>
  </div>
 <div class="formcol">
    <label for="stop">Stop Support Version</label>
    <input type="text" id="stop" name="stop" />
  </div>
  <div class="formcol">
  <label for="date">Last Date</label>
    <input type="text" id="date" name="date" size="22" />
</div>
  <input type="submit" value="Submit">
</form>
</div>
<div id="status"></div>
<script>
$(function()
{
    $(document).on('click', '.btn-add', function(e)
    {
        e.preventDefault();

        var controlForm = $('.controls form:first'),
            currentEntry = $(this).parents('.entry:first'),
            newEntry = $(currentEntry.clone()).appendTo(controlForm);

        newEntry.find('input').val('');
        controlForm.find('.entry:not(:last) .btn-add')
            .removeClass('btn-add').addClass('btn-remove')
            .removeClass('btn-success').addClass('btn-danger')
            .html('<span class="glyphicon glyphicon-minus"></span>');	
    }).on('click', '.btn-remove', function(e)
    {
		$(this).parents('.entry:first').remove();

		e.preventDefault();
		return false;
	});
});

     /* var frm = $('#contactForm1');
    frm.submit(function (ev) {
    
        $.ajax({
            type: frm.attr('method'),
            url: frm.attr('action'),
            
            data: frm.serialize(),
            success: function (data) {
                alert('ok');
                console.log("sucessss")
               
            },
            error:function(data){
            	 console.log("failed")
            	alert("fail");
            	
            }
        });

        ev.preventDefault();
    }); 
     */
    
    
</script>
</body>
</html>