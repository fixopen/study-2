/* @Author: zhaoxq*/

function chargeTap() { //选择充值
    $(".rain-drop-li").each(function (index, el) {
        $(this).tap(function () {
            $(".rain-drop-li").removeClass("cur").eq(index).addClass("cur");
        });
    });
}

function accountInput() { //输入账号
    var $chargeUl = $(".charge-ul");
    var $chargeLi = $(".charge-li");
    $(".charge-number").tap(function () { //点击输入框显示或隐藏
        event.stopPropagation();
        if ($chargeUl.is(":hidden")) {
            $(".charge-number").css({"border-radius": "8px 8px 0 0"});
            $chargeUl.show();
        } else {
            $chargeUl.hide();
            $(".charge-number").css({"border-radius": "8px"});
        }
    });

    $chargeUl.on('tap', 'li', function (event) { //点击下拉框中的li
        $(".charge-number").attr({"value": $(this).text()});
        $(".charge-number").css({"border-radius": "8px"});
        $(".charge-number").select();
    });

    $(document).tap(function () { //点击页面任意位置下拉框收起
        $chargeUl.hide();
        $(".charge-number").css({"border-radius": "8px"});
    });
}