/*@Author: zhaoxq*/

function densoTap(){ //点击语文、数学、英语、少年作家班选项卡
	$(".item-li").each(function(index, el) {
		$(this).click(function() {
			$(".item-li").removeClass("cur").eq(index).addClass("cur");
			$(".denso").removeClass("cur").eq(index).addClass("cur");
		});
	});
}