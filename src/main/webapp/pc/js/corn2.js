window.onload = function () {
    var oBtn = document.getElementById('btn1');
    init();
    var coin = new Coin();
    // oBtn.onclick=function(){
    // }
    var SHAKE_THRESHOLD = 400;
    var last_update = 0;
    var index = 0;
    var x = y = z = last_x = last_y = last_z = 0;
    var w_curTime = 0;

    function init() {
        if (window.DeviceMotionEvent) {
            window.addEventListener('devicemotion', deviceMotionHandler, false);
        } else {
            alert('not support mobile event');
        }
    }

    function deviceMotionHandler(eventData) {
        var acceleration = eventData.accelerationIncludingGravity;
        var curTime = new Date().getTime();
        if ((curTime - last_update) > 100) {
            var diffTime = curTime - last_update;
            last_update = curTime;
            x = acceleration.x;
            y = acceleration.y;
            z = acceleration.z;
            var speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
            var delta = Math.abs(x + y + z - last_x - last_y - last_z);
            if (speed > SHAKE_THRESHOLD) {
                if ((curTime - w_curTime) > 2000) {
                    w_curTime != 0 && new Coin({density: Math.round(delta)});
                    w_curTime = curTime;
                }
            }
            last_x = x;
            last_y = y;
            last_z = z;
        }
    }
}