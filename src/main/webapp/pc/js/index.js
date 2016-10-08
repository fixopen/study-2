/*
* @Author: zhaoxq
* @Date:   2016-09-29 15:13:01
* @Last Modified by:   User
* @Last Modified time: 2016-10-08 11:11:08
*/

function cardPosition() { //请激活卡号垂直居中对齐
	var $activate = $(".activate");
	var loginHeight = $activate.outerHeight()/2;
	$activate.css({"top":"50%","margin-top":- loginHeight + "px"});
}

function simulateSelect() { //模拟下拉菜单
	// 1.点击"请选择"、"三角"显示(隐藏)下拉列表
	var $selectDiv = $(".selectDiv");
	var $mrSelSpan = $selectDiv.children('.mrSelSpan');
	var $selUl = $selectDiv.children('.selUl');
	var $sanjiao = $selectDiv.children('.sanjiao');
	function selUlShowHide($biaoqianName){
		$biaoqianName.each(function(index, el) {
			$(this).unbind("click");
	    	$(this).on('click', function(event) {
	    		event.stopPropagation();
	            if($selUl.eq(index).is(":visible")){
	                $selUl.stop().hide();
	                $selUl.eq(index).hide();
	            }else{
	                $selUl.stop().hide();
	                $selUl.eq(index).show();
	            }
	    	});
	    });
	}
	$(document).click(function(event) {
	    $selUl.stop().hide();//点击页面除selUl之外的任意位置，selUl都隐藏
	});
	selUlShowHide($mrSelSpan);//点击"请选择"显示(隐藏)下拉列表
	selUlShowHide($sanjiao);//点击"三角"显示(隐藏)下拉列表

	// 2.鼠标经过列表项时添加背景色
	$selUl.on('mouseenter mouseout', 'li', function(event) {
		if(event.type == "mouseenter"){
			$(this).css("background-color","#f7f7f7");
		}else{
			$(this).css("background-color","#fff");
		}
	});

	// 3.鼠标点击列表项时，mrSelSpan的内容变成点击的li的内容
	$selUl.on('click', 'li', function(event) {
		$(this).parent(".selUl").stop().hide().siblings(".mrSelSpan").text($(this).text());
	});
}

function fontCartoon() { //激活成功页面，文字动画效果
	var $activateImg = $(".activate-img");
	var $goldBean = $(".gold-bean");
	var $goSet = $(".go-set");

	$activateImg.stop().animate({"top":0}, "slow");;
	$goldBean.stop().animate({"left": 0},"slow");
	$goSet.stop().animate({"bottom":2}, "slow");
}

function submitPopup() { //点击"提交"按钮，弹出弹窗
	var $submitBtn = $(".submit-btn");
	var $popup = $(".popup");
	$submitBtn.click(function(event) {
		$popup.stop().show();
	});
}

function popupHeight() { //弹窗的宽高
	var pageWidth;
	var pageHeight;
	pageWidth = $(document).width();
	pageHeight = $(document).height();
	var $popup = $(".popup");
	$popup.css({"width":pageWidth,"height":pageHeight});
}

function popupClose() { //点击弹窗关闭按钮，关闭弹窗
	var $popupClose = $(".popup-close");
	var $popup = $(".popup");
	$popupClose.click(function(event) {
		$popup.stop().hide();
	});
}

function popupOk() { //点击弹窗"好的"按钮，关闭弹窗
	var $okBtn = $(".ok-btn");
	var $popup = $(".popup");
	$okBtn.click(function(event) {
		$popup.stop().hide();
	});
}