function subjectTab(){ //课程选项卡
	$(".subject-tab-li").each(function(index, el) {
		$(this).click(function() {
			$(".subject-tab-li").removeClass("cur").eq($(this).index()).addClass("cur");
			$(".subject-list").hide().eq($(this).index()).show();
		});
	});
}

function curseDetailTap(){ //点击“课程详情”
	$(".course-detail-btn").each(function(index, el) {
		$(this).click(function() {
			if($(this).children("img").is(":visible")){
				$(".course-detail-btn img").show();
				$(".course-detail").hide();
				$(this).children("img").hide();
				$(".course-detail").eq(index).show();
			}else{
				$(".course-detail-btn img").show();
				$(".course-detail").hide();
				$(this).children("img").show();
				$(".course-detail").eq(index).hide();
			}
		});
	});
}
