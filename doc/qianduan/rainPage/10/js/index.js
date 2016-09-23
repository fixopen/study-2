/* @Author: zhaoxq*/
function studyLiTap() { //点击三个项目
    $(".study-record-li").each(function (index, el) {
        $(this).tap(function () {
            if ($(this).hasClass("cur")) {
                $(".study-record-li").removeClass("cur");
                $(this).removeClass("cur");
            } else {
                $(".study-record-li").removeClass("cur");
                $(this).addClass("cur");
            }
        });
    });
}