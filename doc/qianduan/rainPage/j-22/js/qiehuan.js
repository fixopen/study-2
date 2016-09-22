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

	let g = {
		volumesL: [
			{id: 1, name: '的催收发货', title: 'hhhh'},
			{id: 2, name: '电磁阀才打得过法律', title: 'sdt'},
			{id: 3, name: '没客人高热健康', title: 'xxxx'},
			{id: 4, name: 'xiao', title: 'jjjj'},
			{id: 5, name: 'xiao', title: 'ddds'},
			{id: 6, name: 'xiao', title: 'hhhh'},
			{id: 7, name: 'xiao', title: 'vccvcv'},
			{id: 8, name: 'xiao', title: 'ewr23'}
		],
		volumesH: [
			{id: 1, name: 'hight', title: 'hhhh'},
			{id: 2, name: 'hight', title: 'sdt'},
			{id: 3, name: 'hight', title: 'xxxx'},
			{id: 4, name: 'hight', title: 'jjjj'},
			{id: 5, name: 'hight', title: 'ddds'},
			{id: 6, name: 'hight', title: 'hhhh'},
			{id: 7, name: 'hight', title: 'vccvcv'},
			{id: 8, name: 'hight', title: 'ewr23'}
		],
	}

	g.bind = function (element, data) {
		element.innerHTML = element.innerHTML.replace('%7B', '{').replace('%7D', '}').replace(/\$\{(\w+)\}/g, function (all, letiable) {
			if (!letiable) {
				return ""
			}
			return data[letiable];
		});
		return element
	};

	let volumes = document.getElementById('volumes-template');
	//low
	let lvolumes = document.getElementById('mathStudy-volumesL');
	for (let i = 0; i < g.volumesL.length; i++) {
		let vvolumes = volumes.content.children[0].cloneNode(true);
		g.bind(vvolumes, g.volumesL[i]);
		lvolumes.appendChild(vvolumes);
	}
	//hight
	let hvolumes = document.getElementById('mathStudy-volumesH');
	for (let i = 0; i < g.volumesH.length; i++) {
		let vvolumes = volumes.content.children[0].cloneNode(true);
		g.bind(vvolumes, g.volumesH[i]);
		hvolumes.appendChild(vvolumes);
	}
});
