  $('.div0_one').show();
  $('.table_li').eq(0).css({"box-shadow": "0rem 0rem 1.25rem #fe6569"});
  $('.table_li').eq(0).children('img').show();

  $('.table_li').eq(0).tap(function() {
    $('.table_li').eq(0).children('img').show();
    $('.table_li').eq(1).children('img').hide();
    $(this).css({"box-shadow": "0rem 0rem 1.25rem #fe6569"});
    // $('.div0_two').addClass('rotateX');
    $('.table_li').eq(1).css({"box-shadow": "0rem 0rem 0rem #fff"});
    $('.div0_one').show();
    $('.div0_two').hide();
  });

  $('.table_li').eq(1).tap(function() {
    $('.table_li').eq(0).children('img').hide();
    $('.table_li').eq(1).children('img').show();
    $(this).css({"box-shadow": "0rem 0rem 1.25rem #23cbd1"});
    // $('.div0_one').addClass('rotateX');
    $('.table_li').eq(0).css({"box-shadow": "0rem 0rem 0rem #fff"});
    $('.div0_one').hide();
    $('.div0_two').show();
  });