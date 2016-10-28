$('.div0_one').show();
$('.table_li').eq(0).css({"box-shadow": "0rem 0rem 1.25rem #fe6569"});
$('.table_li').eq(0).children('img').show();

<<<<<<< HEAD
$('.table_li').eq(0).click(function() {
=======
$('.table_li').eq(0).tap(function() {
>>>>>>> eda43edf607380ec727dee60e8f899dc19599aa6
    $('.table_li').eq(0).children('img').show();
    $('.table_li').eq(1).children('img').hide();
    $(this).css({"box-shadow": "0rem 0rem 1.25rem #fe6569"});
    $('.table_li').eq(1).css({"box-shadow": "0rem 0rem 0rem #fff"});
    $('.div0_one').show();
    $('.div0_two').hide();
});

<<<<<<< HEAD
$('.table_li').eq(1).click(function() {
=======
$('.table_li').eq(1).tap(function() {
>>>>>>> eda43edf607380ec727dee60e8f899dc19599aa6
    $('.table_li').eq(0).children('img').hide();
    $('.table_li').eq(1).children('img').show();
    $(this).css({"box-shadow": "0rem 0rem 1.25rem #23cbd1"});
    $('.table_li').eq(0).css({"box-shadow": "0rem 0rem 0rem #fff"});
    $('.div0_one').hide();
    $('.div0_two').show();
});

function newClass() { //课程更新弹窗
<<<<<<< HEAD
    $("#tan,.new-class").bind('touchmove', function(event) {
=======
    $(".tan,.new-class").bind('touchmove', function(event) {
>>>>>>> eda43edf607380ec727dee60e8f899dc19599aa6
        event.preventDefault();
    });
    $(".new-class").click(function () {
        $(this).hide();
<<<<<<< HEAD
        $("#tan").hide();
    });
}

var ie=document.all
var dom=document.getElementById
var ns4=document.layers
var bouncelimit=32 //(must be divisible by 8)
var curtop
var direction="up"
var boxheight=''
function initbox(){
    if (!dom&&!ie&&!ns4)
        return
    crossobj=(dom)?document.getElementById("dropin").style : ie? document.all.dropin : document.dropin
    scroll_top=(ie)? document.body.scrollTop : window.pageYOffset
    crossobj.top=scroll_top-250
    crossobj.visibility=(dom||ie)? "visible" : "show"
    dropstart=setInterval("dropin()",50)
}

function dropin(){
    scroll_top=(ie)? document.body.scrollTop : window.pageYOffset
    if (parseInt(crossobj.top)<100+scroll_top)
        crossobj.top=parseInt(crossobj.top)+40
    else{
        clearInterval(dropstart)
        bouncestart=setInterval("bouncein()",50)
    }
}

function bouncein(){
    crossobj.top=parseInt(crossobj.top)-bouncelimit
    if (bouncelimit<0)
        bouncelimit+=8
    bouncelimit=bouncelimit*-1
    if (bouncelimit==0){
        clearInterval(bouncestart)
    }
}

function dismissbox(){
    if (window.bouncestart) clearInterval(bouncestart)
    crossobj.visibility="hidden"
}


function get_cookie(Name) {
    var search = Name + "="
    var returnvalue = ""
    if (document.cookie.length > 0) {
        offset = document.cookie.indexOf(search)
        if (offset != -1) {
            offset += search.length
            end = document.cookie.indexOf(";", offset)
            if (end == -1)
                end = document.cookie.length;
            returnvalue=unescape(document.cookie.substring(offset, end))
        }
    }
    return returnvalue;
}

function dropornot(){
    if (get_cookie("droppedin")==""){
        window.onload=initbox
        document.cookie="droppedin=yes"
    }
}
dropornot()
=======
        $(".tan").hide();
    });
}
>>>>>>> eda43edf607380ec727dee60e8f899dc19599aa6
