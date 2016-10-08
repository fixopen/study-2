//***扩展对象点赞插件、点赞特效***//
//***Zynblog**//
//***2016-5-11**//
//***用法：jQuery('.praisebtn').praise(options);***//
// (function ($) {
//     $.fn.praise = function (options) {
//         var defaults = {
//             obj: null, //jq对象，针对哪个对象使用这个tipsBox函数
//             str: "+1", //字符串，要显示的内容;也可以传一段html，如: "<b style='font-family:Microsoft YaHei;'>哈哈</b>"
//             startSize: "10px", //动画开始的文字大小
//             endSize: "30px", //动画结束的文字大小
//             interval: 600, //文字动画时间间隔
//             color: "red", //文字颜色
//             callback: function () { } //回调函数
//         };
//         var opt = $.extend(defaults, options); //合并参数
//         $("body").append("<span class='num'>" + opt.str + "</span>");
//         var box = $(".num");
//         var left = opt.obj.offset().left + opt.obj.width()/2; //span btn左侧距离加上自身宽度的一半
//         var top = opt.obj.offset().top - opt.obj.height();//顶部距离减去自身的高度
//         box.css({
//             "position": "absolute",
//             "left": left + "px",
//             "top": top + "px",
//             "z-index": 9999,
//             "font-size": opt.startSize,
//             "line-height": opt.endSize,
//             "color": opt.color
//         });
//         box.animate({
//             "font-size": opt.endSize,
//             "opacity": "0",
//             "top": top - parseInt(opt.endSize) + "px"
//         }, opt.interval, function () {
//             box.remove();
//             opt.callback();
//         });
//     }
// })(jQuery);
//
// //点赞图标恢复原样
// function niceIn(prop) {
//     prop.find('.praisenum').addClass('niceIn').css("color", "red");
//     setTimeout(function () {
//         prop.find('.praisenum').css("color", "#45BCF9").removeClass('niceIn');
//     }, 1000);
// };
// //点赞特效+Ajax统计点赞数量
// pariseShow:  function () {
//     //使用自定义的点赞特效插件,在zynblog.js前要先引入这个插件
//     //jquery给暂未生成的标签绑定事件要用on('事件','对象','事件句柄')
//     jQuery(document).on("click", ".praisebtn", function (e) {
//         e.preventDefault();
//         //获取被点赞文章的id praise-flag:0没攒过，1：赞过了
//         //页面刚生成时，可以从库中确定该用户是否点赞，并为praise-flag属性赋初值
//         //这里没必要那么严谨，所以初值均为1，(顶多是再在cookie中给个标记)
//         var praiseFlag = jQuery(this).children('a').attr('praise-flag');
//         //alert(praiseFlag);
//         var praiseArtId = jQuery(this).children('a').attr('data-id');
//         //alert(praiseArtId);
//
//         //1. 如果没赞过
//         if (praiseFlag == 0) {
//             var curPraise = jQuery(this).children('a');
//             curPraise.attr('praise-flag', "1");//先把点赞标识的属性值设为1
//
//             jQuery(this).praise({
//                 obj: jQuery(this),
//                 str: "+1",
//                 callback: function () {
//                     jQuery.post("/Archives/PraiseStatic", { "artId": praiseArtId }, function (data) {
//                         if (data.Status == 1) {
//                             var praisecount = parseInt(curPraise.text().match(/\d+/));
//                             curPraise.text(curPraise.text().replace(praisecount, praisecount + 1));
//                         } else if (data.Status == 2) {
//                             alert(data.Message);
//                         } else if (data.Status == 0) {
//                             alert(data.Message);
//                         }
//                     });
//                 }
//             });
//             niceIn(jQuery(this));
//         } else if (praiseFlag == 1) {
//             //2. 如果已经已赞
//             jQuery("body").append("<span class='praisetip'>您已赞过~</span>");
//             var tipbox = jQuery(".praisetip");
//             var left = jQuery(this).offset().left;
//             var top = jQuery(this).offset().top + jQuery(this).height();
//             tipbox.css({
//                 "position": "absolute",
//                 "left": left + "px",
//                 "top": top + "px",
//                 "z-index": 9999,
//                 "font-size": "12px",
//                 "line-height": "13px",
//                 "color": "red"
//             });
//             tipbox.animate({
//                 "opacity": "0"
//             }, 1200, function () {
//                 tipbox.remove();
//             });
//         }
//     });
// };
function like(el) {
    var total = document.getElementById('total');
    total.innerText = parseInt(total.innerText) + 1;
    el.disabled = true;
    $(function () {
        $("#icon").one("click", function () {})
    })
    let data ={
        objectType:'knowledge-point',
        objectId:g.getUrlParameter("id"),
        action:'like'
    }
    $.ajax({
        type: "post",
        url: "/api/logs",
        data: JSON.stringify(data),
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        success: function (like) {
            alert(JSON.stringify(like))
        }
    })
}
function commentLike(el) {
    var total = document.getElementById('all');
    total.innerText = parseInt(total.innerText) + 1;
    el.disabled = true;
    let data ={
        objectType:'comment',
        id:data.comments.id,
        action:'like'
    }
// let commentId=
    $.ajax({
        type: "post",
        url: "/api/comment",
        data: JSON.stringify(data),
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        success: function (like) {
            alert(JSON.stringify(like))
        }
    })
}
// function putUps(oid){
//     var params ={
//         oid:oid
//     };
//     $.ajax({
//         data: params,
//         url: '/addups',
//         type:'post',
//         jsonpCallback: 'callback',
//         success: function(data){
//             console.log(data);
//             $('#putups'+oid).attr("title",data+'个赞');
//             $('#putups'+oid).html("赞["+data+"]");
//         },
//         error: function(jqXHR, textStatus, errorThrown){
//             alert('error ' + textStatus + " " + errorThrown);
//         }
//     });
// }
// Mind.getById = function(id, callback) {
//     mongodb.open(function(err, db) {
//         if (err) {
//             return callback(err);
//         }
//         db.collection('minds', function(err, collection) {
//             if (err) {
//                 db.close();
//                 return callback(err);
//             }
//             collection.findOne({
//                 _id : new  ObjectID(id)
//             }, function(err, mind) {
//                 db.close();
//                 if (err) {
//                     return callback(err);
//                 }
//                 callback(null, mind);
//             });
//         });
//     });
// };
// app.post('/addups', function(req, res) {
//     var oid=req.body.oid;
//     Mind.getById(oid, function(err, mind) {
//         if (!mind) {
//             req.flash('error', err);
//             return res.redirect('/');
//         }
//         var temp = mind.ups + 1;
//         Mind.update(oid, temp, mind.downs, mind.comments,function(err) {
//             if (err) {
//                 req.flash('error', err);
//                 return res.redirect('/');
//             }
//             console.log(temp);
//             res.writeHead(200, { 'Content-Type': 'text/plain' });
//             res.end(temp.toString());
//             //res.json({success:1});
//             return;
//         });
//     });
// });
// 定义disabled函数，禁用投票按钮

// function like() {
//
//     // 定义vote函数，计算票数
//
//     // let data ={
//     //     //userId: 1,
//     //     objectType:'knowledge-point',
//     //     objectId:g.getUrlParameter("id"),
//     //     action:'unlike'
//     // }
//
//
// }


$(function () {
    // let data ={
    //     objectType:'knowledge-point',
    //     objectId:parseInt(g.getUrlParameter("id")),
    //     action:'like'
    // }
    // $.ajax({
    //     type: 'get',
    //     url: 'api/logs?filter=' + JSON.stringify(data ),
    //     dataType: 'json',
    //     success: function (like) {
    //         alert(JSON.stringify(like))
    //         if(like !=null){
    //             alert("你已经点过赞了")
    //         }else{
    //         }
    //     }
    // })


    let createComment=document.getElementById('createComment');
    createComment .addEventListener('click', writeMessage, false);
    function writeMessage() {
        $('#commentWriter').toggle();
        let btn=document.getElementById('btn');
        btn.addEventListener('click', submit, false);
        function submit(e) {
            let textarea=document.getElementById('textarea');
            let value=textarea.value;
            textarea.value='';
            e.target.style.color = '#f5f5f5';
            // e.target.style.backgroundColor = '#3e8f3e';
            $.ajax({
                type: "post",
                url: "/api/comment",
                data: JSON.stringify({objectType:'knowledge-point', objectId:g.getUrlParameter("id"), content: value}),
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                success: function (data) {
                    alert(JSON.stringify(data))
                }
            })
        }

    }

    let trueImage = document.createElement('img')
    trueImage.setAttribute('class', 'daan_error')
    trueImage.setAttribute('src', 'img/true.png')
    trueImage.setAttribute('alt', '')

    let falseImage = document.createElement('img')
    falseImage.setAttribute('class', 'daan_error')
    falseImage.setAttribute('src', 'img/error.png')
    falseImage.setAttribute('alt', '')

    // // //change icon via liked state
    // let icon = document.getElementById('icon')
    // icon.addEventListener('click', function(e) {
    //     if (liked) {
    //         liked = false
    // //     // *   unlike
    // //     //     *   //event processor unlike
    // //     //     *       notification unlike
    // //     //     *       icon change to like, unliked
    // //     //     *       likeCount - 1
    //     } else {
    //         liked = true
    // //         //     *   like
    // //         //     *   //event processor like
    // //         //     *       notification like
    // //         // *       icon change to unlike, liked
    // //         // *       likeCount + 1
    //     }
    // }, false)
    // message---------



    let volumeId = g.getUrlParameter("volumeId");
    $.ajax({
        type: 'get',
        url: 'api/knowledge-points?filter=' + JSON.stringify({
            volumeId: parseInt(volumeId)
        }),
        dataType: 'json',
        success: function (knowledgePointList) {
            let id = g.getUrlParameter('id');
            $.ajax({
                type: "get",
                url: 'api/knowledge-points/' + id + '/contents',
                dataType: 'json',
                async: false,
                success: function (data) {
                    alert(JSON.stringify(data))
                    // 上一个，下一个---------------------------------------------------------------
                    let baseUrl = 'mathKnowledgePointsDetail.html?volumeId=' + volumeId + "&id="

                    for (let i = 0; i < knowledgePointList.length; ++i) {
                        if (knowledgePointList[i].id == id) {
                            let prevIndex = i;
                            let nextIndex = i;
                            if (i > 0) {
                                prevIndex = i - 1
                            }
                            if (i < knowledgePointList.length - 1) {
                                nextIndex = i + 1
                            }
                            data.interaction.previous = baseUrl + knowledgePointList[prevIndex].id;
                            data.interaction.next = baseUrl + knowledgePointList[nextIndex].id;
                            break
                        }
                    }
                    proc({
                        templateId: 'interaction-template',
                        data: data.interaction,
                        containerId: 'interaction'
                    });
                    //-------------------------------------------------------------------------------
                    for (let i = 0; i < data.problems.length; ++i) {
                        let p = data.problems[i];
                        p.options[0].title = 'A';
                        p.options[1].title = 'B';
                        p.options[2].title = 'C';
                        p.options[3].title = 'D'
                    }

                    for (let i = 0; i < knowledgePointList.length; ++i) {
                        if (knowledgePointList[i].id == id) {
                            proc({
                                templateId: 'title1-template',
                                data: {title1: knowledgePointList[i].order},
                                containerId: 'title1'
                            });
                            proc({
                                templateId: 'title2-template',
                                data: {title: knowledgePointList[i].title},
                                containerId: 'title2'
                            });
                            break
                        }
                    }

                    proc({
                        templateId: 'challenge-template',
                        data: data.quotes,
                        containerId: 'challenge'
                    });

                    proc({
                        data: data.contents,
                        containerId: 'content',
                        alterTemplates: [
                            {type: 'text', templateId: 'content-text-template'},
                            {type: 'imageText', templateId: 'content-image-template'}
                        ]
                    });

                    proc({
                        templateId: 'video-template',
                        data: data.video,
                        containerId: 'video'
                    });
                    proc({
                        templateId: 'comment-template',
                        data: data.comments,
                        containerId: 'comments'
                    })
                    //data.problems
                    let ps = []
                    for (let i = 0; i < data.problems.length; ++i) {
                        ps[i] = data.problems[i]
                    }
                    let pk = null
                    let strongestBrains = []
                    if (ps.length > 0) {
                        pk = ps[ps.length - 1]
                        strongestBrains = ps.pop()
                    }
                    proc({
                        templateId: 'strongest-brain-template',
                        data: strongestBrains,
                        containerId: 'strongest-brain',
                        secondBind: [
                            {
                                extPoint: 'options',
                                dataFieldName: 'options',
                                templateId: 'strongest-brain-option-template'
                            },
                            {
                                extPoint: 'explain',
                                dataFieldName: 'video',
                                templateId: 'video-template'
                            }
                        ]
                    });

                    proc({
                        templateId: 'pk-template',
                        data: pk,
                        containerId: 'pk',
                        secondBind: [
                            {
                                extPoint: 'options',
                                dataFieldName: 'options',
                                templateId: 'strongest-brain-option-template'
                            }
                        ]
                    });




                    // 选项判错--------------------------------------------------------
                    let findProblem = function (problemId) {
                        let problem = null
                        for (let i = 0; i < data.problems.length; ++i) {
                            if (data.problems[i].id == problemId) {
                                problem = data.problems[i]
                                break
                            }
                        }
                        return problem
                    }

                    let getIndex = function (content) {
                        let index = -1
                        switch (content) {
                            case 'A':
                                index = 0
                                break
                            case 'B':
                                index = 1
                                break
                            case 'C':
                                index = 2
                                break
                            case 'D':
                                index = 3
                                break
                            default:
                                break
                        }
                        return index
                    }

                    let compareAnswer = function (index, standardAnswers) {
                        let finded = false
                        for (let j = 0; j < standardAnswers.length; ++j) {
                            if (index == standardAnswers[j].name) {
                                finded = true
                                break
                            }
                        }
                        return finded
                    }

                    let judgement = function (e) {
                        //e.currentTarget == problemContainer
                        let clickedElement = e.target

                        if (clickedElement.hasClass('daan_quan')) { // == [class="daan_quan"]
                            let problemId = clickedElement.parentNode.parentNode.dataset.id
                            let problem = findProblem(problemId)
                            if (problem) {
                                let index = getIndex(clickedElement.textContent)
                                let r = compareAnswer(index, problem.standardAnswers)
                                if (r) {
                                    clickedElement.parentNode.addClass('daanLi_true')
                                    clickedElement.innerHTML = ''
                                    clickedElement.appendChild(trueImage.cloneNode(true))
                                } else {
                                    clickedElement.parentNode.addClass('daanLi_error')
                                    clickedElement.innerHTML = ''
                                    clickedElement.appendChild(falseImage.cloneNode(true))
                                }
                            }
                        }

                        let data = {
                            objectType: 'knowledge-point',
                            objectId: 'problemId',
                            objectName: 'index',
                            action: 'click'
                        }

                        $.ajax({
                            type: "post",
                            url: 'api/answer-records',
                            async: false,
                            data: data,
                            success: function (data) {
                                alert(JSON.stringify(data))
                            }
                        })
                    }

                    let problemContainer = document.getElementById('strongest-brain')
                    problemContainer.addEventListener('click', judgement, false)

                    let pkContainer = document.getElementById('pk')
                    pkContainer.addEventListener('click', judgement, false)

                    //-----------------------------------------------------------------------------------
                    //POST /api/problems/{id}/answers
                    // answer-records
                    //
                    //[1,3,4]
                }
            })
        }
    })
})


