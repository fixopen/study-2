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

function allxqTap(){ //点击“all课程详情”
	$(".course_detail").each(function(index, el) {
		$(this).click(function() {
			if($(this).parent().parent().siblings(".course-detail").is(":visible")){
				$(".course-detail").hide();
				$(".course-detail").eq(index).hide();
			}else{
				$(".course-detail").hide();
				$(".course-detail").eq(index).show();
			}
		});
	});
}

function xialaTap(){ //下拉选项
	$(".selectDiv").each(function(index, el) {
		$(this).click(function() {
			if($(this).children(".xiala_ul").is(":visible")){
				$(".xiala_ul").hide();
				$(".xiala_ul").eq(index).hide();
			}else{
				$(".xiala_ul").hide();
				$(".xiala_ul").eq(index).show();
			}
		});
	});
	$(".xiala_ul li").click(function() {
		$(this).parent(".xiala_ul").siblings().children('span').text($(this).text());
	});
}