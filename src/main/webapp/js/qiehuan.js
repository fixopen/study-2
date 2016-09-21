$(document).ready(function() {
	
	//切换选项
	$(".order-area li").not('.JqIcon').click(function(){
		
		$(".order-area li").removeClass('show');
		$(this).addClass('show');
		var i=$(this).index();
		switch(i)
		{
			
			case 0:
				$(".searchBox").show();
				$(".typeRadioTeam,.comp").hide();
			  	break;
			case 1:
				$(".comp").css('display','table');
				$(".typeRadioTeam,.searchBox").hide();			  
			  	break;
			case 2:
				$(".typeRadioTeam").css('display','table');
				$(".comp,.searchBox").hide();
				break;
		  	
		}
		
	})




	//模拟单选按钮
	$(".comp .t-cell-css").click(function(){

		$(".comp").find(".myRadio").removeClass('radioChecked');
		$(this).find(".myRadio").addClass('radioChecked');


	})
	$(".typeRadioTeam .t-cell-css").click(function(){

		$(".typeRadioTeam").find(".myRadio").removeClass('radioChecked');
		$(this).find(".myRadio").addClass('radioChecked');


	})


	//大图、列表切换
	var x=0;
	$(".JqIcon").click(function(){

		if(x==0){

			$(this).removeClass('goPic').addClass('goList');
			$(".listBox").hide();
			$(".picBox").show();
			x=1;

		}else{
			$(this).removeClass('goList').addClass('goPic');
			$(".listBox").show();
			$(".picBox").hide();			
			x=0;
		}

	})


});
