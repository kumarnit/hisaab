var editor; // use a global for the submit and return data rendering in the examples
		
		$(document).ready(function(){
			
//			$("#itemExpiryTable").dataTable(); 
			$("#itemExpiryTable").dataTable( {
				 "bProcessing": false,
			        "bServerSide": false,
			        "sAjaxSource": "app/itemExpiry",
			        "aoColumns": [
			                      { "mData": "itemNum" },
			                      { "mData": "brand" },
			                      { "mData": "productName" },
			                      { "mData": "expiryMonths" }
			                      ]
		    });
			
			//var cell;
			$(document).on("dblclick", ".expiryCell", function(){
				var cell = $(this);
				cell.find("span").hide();
				cell.find("input").show();
				cell.find("input").focus();
			});
			
			$(document).on("change focusout", ".expiryCell input", function(e){
				var event = e || window.event;
				if(event.type === 'change'){
					//alert('ok');
					var cell = $(this).parent();
					var cellValue = cell.find("input").val();
					var cellText = cell.find("span").text();
					var itemNumber = cell.find("input").attr("data-item-number");
					
					var status = confirm("Are you sure to change this record?");
					if(status){
						
						if(validateItem(cellValue)){
							$(".loaderDivClass").show();
							$.ajax({
								
								url: "app/UpdateItem",
								type: "POST",
								data:{itemNumber: itemNumber, expiryInMonths: cellValue},
								success:function(result){
									cell.find("input").hide();
									cell.find("span").text(cellValue);
									cell.find("span").show();
									$(".loaderDivClass").hide();
								}
							});
						}else{
							cell.find("input").hide();
							//cell.find("span").text(cellValue);
							cell.find("span").show();
						}
					}else{
						cell.find("input").hide();
						//cell.find("span").text(cellValue);
						cell.find("span").show();
					}
					
					
					
				}else if(event.type === 'focusout'){
			
					var cell = $(this).parent();
					
					cell.find("input").hide();
					//cell.find("span").text(cellValue);
					cell.find("span").show();
					
				}
				
				
			});
			
			
			$(document).on("mouseover mouseout","#itemExpiryTable tr",function(e){
				var event = e || window.event;
				if(event.type === 'mouseover'){
					
					$(this).find(".glyphicon-remove").show();
					
				}else if(event.type === 'mouseout'){
					
					$(this).find(".glyphicon-remove").hide();
					
				}
										
			});
			
			
			$(document).on("click","#itemExpiryTable tr .glyphicon-remove",function(){
				
				var status = confirm("Are you sure to delete this record?");
				if(status){
					$(".loaderDivClass").show();
					var row = $(this).parent().parent();
					
					var itemExpiryId = row.attr("data-item-expiry-id");
				
					
					$.ajax({
						
						url: "app/DeleteItem",
						type: "POST",
						data:{itemExpiryId: itemExpiryId},
						success:function(result){
							
							row.remove();
							$(".loaderDivClass").hide();
							
						}
						
					});
//					console.log("Yes");
					
				}
				
			});
			
		});
	
		function validateItem(expiryInMonths){

			var flag = true;
			if(isNaN(expiryInMonths)){
				flag = false;
			}

			return flag;
		}