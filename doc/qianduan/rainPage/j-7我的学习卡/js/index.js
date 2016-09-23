$(".Btn02").each(function (index, el) {
    $(this).tap(function () {
        $('.tan').show();
        $('.window1').eq(0).show();
    });
});
$(".p_btn.btn01").each(function (index, el) {
    $(this).tap(function () {
        $('.tan').hide();
        $('.window1').eq(0).hide();
    });
});
// 弹窗关闭按钮
$(".windowClose").each(function (index, el) {
    $(this).tap(function () {
        $('.tan').hide();
        $('.window1').eq(0).hide();
    });
});