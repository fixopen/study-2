$(function () {

// message---------
    var createComment = document.getElementById('createComment');
    createComment.addEventListener('click', writeMessage, false);
    function writeMessage() {
        $('#commentWriter').toggle();
        var btn = document.getElementById('btn');
        btn.addEventListener('click', submit, false);
        function submit(e) {
            var textarea = document.getElementById('textarea');
            var value = textarea.value;
            if(value.length<1){
                return false;
            }
            textarea.value = '';
            e.target.style.color = '#f5f5f5';
            // e.target.style.backgroundColor = '#3e8f3e';
            $.ajax({
                type: "post",
                url: "/api/comments",
                data: JSON.stringify({
                    userId: 1,
                    objectType: 'knowledge-point',
                    objectId: g.getUrlParameter("id"),
                    content: value
                }),
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                success: function (data) {
                    location.reload();
                    //alert(JSON.stringify(data))
                }
            })
        }
    }
    //knowledge-points
    var volumeId = g.getUrlParameter('volumeId')
    $.ajax({
        type: 'get',
        url: 'api/knowledge-points?filter=' + JSON.stringify({
            volumeId: parseInt(volumeId)
        }),
        dataType: 'json',
        success: function (knowledgePointList) {
            var id = g.getUrlParameter('id')
            $.ajax({
                type: "get",
                url: 'api/knowledge-points/' + id + '/contents',
                dataType: 'json',
                async: false,
                success: function (data) {
                   //alert(JSON.stringify(data))
                    for (var i = 0; i < data.problems.length; ++i) {
                        var p = data.problems[i]
                        p.options[0].title = 'A'
                        p.options[1].title = 'B'
                        p.options[2].title = 'C'
                        p.options[3].title = 'D'
                    }

                    proc({
                        templateId: 'title-template',
                        data: data,
                        containerId: 'title'
                    })

                    proc({
                        templateId: 'origin-template',
                        data: data.quotes,
                        containerId: 'origin'
                    })

                    proc({
                        data: data.contents,
                        containerId: 'content',
                        alterTemplates: [
                            {type: 'text', templateId: 'content-text-template'},
                            {type: 'imageText', templateId: 'content-imagetext-template'},
                            // {type: 'pinyinText', templateId: 'content-pinyin-template'},
                            {type: 'image', templateId: 'content-img-template'}
                        ]
                    })

                   // pinyin-----begin------
                   //  var pinyins = []
                   //  for(var i=0;i<data.contents.length;i++){
                   //      if (data.contents[i].type == 'pinyinText') {
                   //          pinyins.push(data.contents[i])
                   //      }
                   //  }
                   //
                   //  var ps = ['，', '。', '？','！','《','》','；','、','“','”','：','（','）','——','……','·',
                   //      '0','1','2','3','4','5','6','7','8','9','曉','堯','a','b','c','d','e','f','g','h','i','j','k','l','m',
                   //      'n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N',
                   //      'O','P','Q','R','S','T', 'U','V','W','X','Y','Z',]
                   //
                   //  var isP = function(c) {
                   //      var result = false
                   //      for (var i = 0; i < ps.length; ++i) {
                   //          if (ps[i] == c) {
                   //              result = true
                   //              break
                   //          }
                   //      }
                   //      return result
                   //  }
                   //
                   //  for (var i = 0; i < pinyins.length; ++i) {
                   //      var pinyinItem = pinyins[i]
                   //      var pinyin = pinyinItem.pinyin.split(' ')
                   //      var chineseIndex = 0;
                   //      for (var j = 0; j < pinyin.length; ++j) {
                   //          var pinyinValue = pinyin[j]
                   //          var c = pinyinItem.content[chineseIndex]
                   //          ++chineseIndex
                   //          if (isP(c)) {
                   //               e = c
                   //              c = pinyinItem.content[chineseIndex]
                   //              ++chineseIndex
                   //          }
                   //
                   //          var g = {}
                   //          g.bind = function (element, data) {
                   //              element.innerHTML = element.innerHTML.replace('%7B', '{').replace('%7D', '}').replace(/\$\{(\w+)\}/g, function (all, variable) {
                   //                  if (!variable) {
                   //                      return ""
                   //                  }
                   //                  return data[variable];
                   //              });
                   //              return element
                   //          };
                   //
                   //          //<ruby><rb>c</rb><rt>pinyinValue</rt></ruby><span>c</span>
                   //          var e=document.getElementById('content-pinyin-template').content.children[0].cloneNode(true)
                   //          var content=document.getElementById('content')
                   //          g.bind(e, {"pinyin": pinyinValue, "content":c})
                   //          content.appendChild(e)
                   //
                   //
                   //          //ruby=getTemplate('content-pinyin-template').cloneNode(true);
                   //          // var d=document.getElementById('content-pinyin-template').content.children[0].cloneNode(true)
                   //          // var content=document.getElementById('pycontent')
                   //          // g.bind(d, {"pinyin": pinyinValue, "content":c})
                   //          // content.appendChild(d)
                   //          //
                   //          // var e=document.getElementById('content-py-template').content.children[0].cloneNode(true)
                   //          // g.bind(e, {"content":c})
                   //          // content.appendChild(e)
                   //      }
                   //    //  var pinyin=data.contents[i].pinyin.split(" ");
                   //      //alert(pinyin);
                   //      //var content=data.contents[i].content.split('');
                   //      //alert(content);
                   //
                   //  }
                 //   pinyin-----end-----



                    proc({
                        templateId: 'video-template',
                        data: data.video,
                        containerId: 'video'
                    })

                    proc({
                        templateId: 'problem-template',
                        data: data.problems,
                        containerId: 'problem',
                        secondBind: {
                            extPoint: 'options',
                            dataFieldName: 'options',
                            templateId: 'problem-option-template'
                        }
                    })

                    //文字样式
                    for(var i=0;i<data.problems.length;i++){
                        if (data.problems[i].type == '多选题') {
                            $('.addimg span').eq(i).removeClass('mld_liImg').addClass('mld_liImg_');
                        }else if(data.problems[i].type == '单选题'){
                            $('.addimg span').eq(i).removeClass('mld_liImg_').addClass('mld_liImg');
                        }
                    }


                    //判断对错
                    var findProblem = function (problemId) {
                        var problem = null
                        for (var i = 0; i < data.problems.length; ++i) {
                            if (data.problems[i].id == problemId) {
                                problem = data.problems[i]
                                break
                            }
                        }
                        return problem
                    }

                    // var getIndex = function (content) {
                    //     var index = -1
                    //     switch (content) {
                    //         case 'A':
                    //             index = 0
                    //             break
                    //         case 'B':
                    //             index = 1
                    //             break
                    //         case 'C':
                    //             index = 2
                    //             break
                    //         case 'D':
                    //             index = 3
                    //             break
                    //         default:
                    //             break
                    //     }
                    //     return index
                    // }

                    var compareAnswer = function (index, standardAnswers) {
                        var finded = false
                        for (var j = 0; j < standardAnswers.length; ++j) {
                            if (index == standardAnswers[j].name) {
                                finded = true
                                break
                            }
                        }
                        return finded
                    }

                 //   var problemContainer = document.getElementById('problem')
                    // problemContainer.addEventListener('click', function (e) {
                    //     //e.currentTarget == problemContainer
                    //     var clickedElement = e.target
                    //     var trueImage = document.createElement('img')
                    //     // trueImage.setAttribute('class', 'daan_error')
                    //     trueImage.setAttribute('src', 'img/true.png')
                    //     trueImage.setAttribute('alt', '')
                    //
                    //     var falseImage = document.createElement('img')
                    //     // falseImage.setAttribute('class', 'daan_error')
                    //     falseImage.setAttribute('src', 'img/error.png')
                    //     falseImage.setAttribute('alt', '')
                    //
                    //     if (clickedElement.hasClass('daan_quan')) { // == [class="daan_quan"]
                    //         var problemId = clickedElement.parentNode.parentNode.dataset.id
                    //         var problem = findProblem(problemId)
                    //         if (problem) {
                    //             var index = getIndex(clickedElement.textContent)
                    //             var r = compareAnswer(index, problem.standardAnswers)
                    //             if (r) {
                    //                 clickedElement.parentNode.addClass('daanLi_true')
                    //                 clickedElement.innerHTML = ''
                    //                 clickedElement.appendChild(trueImage)
                    //             } else {
                    //                 clickedElement.parentNode.addClass('daanLi_error')
                    //                 clickedElement.innerHTML = ''
                    //                 clickedElement.appendChild(falseImage)
                    //             }
                    //         }
                    //     }
                    // }, false)

                    var falseImage=getTemplate('falseImage');
                    var trueImage=getTemplate('trueImage');
                    var problemContainer = document.getElementById('problem');
                    problemContainer.addEventListener('click', function (e) {
                        var clickedElement = e.target;
                        if (clickedElement.hasClass('daan_quan') || clickedElement.hasClass('daana')) {
                            var problemType = clickedElement.parentNode.parentNode.previousElementSibling.firstElementChild.textContent;
                            var titleElement = clickedElement.previousElementSibling
                            switch (problemType) {
                                case '多选题':
                                    if (titleElement.dataset.selected == 'true' || titleElement.dataset.selected == true) {
                                        titleElement.dataset.selected = false;
                                        //
                                        titleElement.removeClass('daanLi_nowBai');
                                        titleElement.parentNode.removeClass('daanLi_now');
                                    } else {
                                        titleElement.dataset.selected = true;
                                        //
                                        titleElement.addClass('daanLi_nowBai');
                                        titleElement.parentNode.addClass('daanLi_now');
                                    }
                                    break
                                case '单选题':
                                    var optionsContainer = clickedElement.parentNode.parentNode;
                                    for (var i = 0; i < optionsContainer.children.length; ++i) {
                                        var option = optionsContainer.children[i];
                                        if (option.children[0].dataset.selected == 'true') {
                                            option.children[0].dataset.selected = false;
                                            option.children[0].removeClass('daanLi_nowBai');
                                            option.children[0].parentNode.removeClass('daanLi_now');
                                        }
                                    }
                                    titleElement.dataset.selected = true;
                                    titleElement.addClass('daanLi_nowBai');
                                    titleElement.parentNode.addClass('daanLi_now');
                                    break
                            }
                        } else if (clickedElement.hasClass('btnTrue')) {
                            var optionsContainer = clickedElement.parentNode.previousElementSibling;
                            for (var i = 0; i < optionsContainer.children.length; ++i) {
                                var option = optionsContainer.children[i];
                                var problem = findProblem(optionsContainer.dataset.id);
                                if (compareAnswer(i, problem.standardAnswers)) {
                                    option.addClass('daanLi_true');
                                    option.firstElementChild.innerHTML = ''
                                    option.firstElementChild.removeClass('daanLi_nowBai');
                                    option.firstElementChild.appendChild(trueImage.cloneNode(true))
                                }
                                if (option.children[0].dataset.selected == 'true' || option.children[0].dataset.selected == true) {
                                    if (!compareAnswer(i, problem.standardAnswers)) {
                                        option.addClass('daanLi_error');
                                        option.firstElementChild.innerHTML = ''
                                        option.firstElementChild.removeClass('daanLi_nowBai');
                                        option.firstElementChild.appendChild(falseImage.cloneNode(true))
                                    }
                                }
                            }
                        }
                    }, false)

                    //上一课下一课
                    var baseUrl = 'chineseKnowledgePointsDetail.html?volumeId=' + volumeId + "&id="
                    for (var i = 0; i < knowledgePointList.length; ++i) {
                        var id = g.getUrlParameter('id')
                        if (knowledgePointList[i].id == id) {
                            var prevIndex = i
                            var nextIndex = i
                            if (i > 0) {
                                prevIndex = i - 1
                            }
                            if (i < knowledgePointList.length - 1) {
                                nextIndex = i + 1
                            }
                            data.interaction.previous = baseUrl + knowledgePointList[prevIndex].id
                            data.interaction.next = baseUrl + knowledgePointList[nextIndex].id
                            break
                        }
                    }

                    proc({
                        templateId: 'interaction-template',
                        data: data.interaction,
                        containerId: 'interaction'
                    })

                   // likes
                    var id = g.getUrlParameter("id")
                    $.ajax({
                        type: "get",
                        url: 'api/knowledge-points/' + id + '/is-self-like',
                        dataType: "json",
                        success: function (like) {
                            var liked = like.like
                            var icon = document.getElementById('icon');
                            icon.addEventListener('click', function (e) {
                                if (liked) {
                                    $.ajax({
                                        type: "put",
                                        url: '/api/knowledge-points/' + id + '/unlike',
                                        data: JSON.stringify({}),
                                        dataType: "json",
                                        contentType: "application/json; charset=utf-8",
                                        success: function (unlike) {
                                            icon.setAttribute('src', 'img/zan.png');
                                            --data.interaction.likeCount
                                            e.target.nextElementSibling.textContent = data.interaction.likeCount;
                                            liked = false;
                                        }
                                    })
                                } else {
                                    $.ajax({
                                        type: "put",
                                        url: '/api/knowledge-points/' + id + '/like',
                                        data: JSON.stringify({}),
                                        dataType: "json",
                                        contentType: "application/json; charset=utf-8",
                                        success: function (like) {
                                            icon.setAttribute('src', 'img/zan-over.png');
                                            ++data.interaction.likeCount;
                                            e.target.nextElementSibling.textContent = data.interaction.likeCount
                                            liked = true;
                                        }
                                    })
                                }
                            }, false)
                        }
                    })


                    proc({
                        templateId: 'comment-template',
                        data: data.comments,
                        containerId: 'comments'
                    })
                    //comments-likes
                    $('.ul01_imgzan_').on('click', function (e) {
                        var id = e.target.parentNode.dataset.id
                        $.ajax({
                            type: "get",
                            url: 'api/comments/' + id + '/is-self-like',
                            dataType: "json",
                            success: function (like) {
                               var  likeds = like.like;
                               if(likeds){
                                    var id = e.target.parentNode.dataset.id
                                    $.ajax({
                                        type: "put",
                                        url: '/api/comments/' + id + '/unlike',
                                        data: JSON.stringify({}),
                                        dataType: "json",
                                        contentType: "application/json; charset=utf-8",
                                        success: function (unlike) {
                                            for (var i = 0; i < data.comments.length; ++i) {
                                                if (data.comments[i].id == id) {
                                                    e.target.setAttribute('src', 'img/zan.png');
                                                    --data.comments[i].likeCount;
                                                    e.target.nextElementSibling.textContent = data.comments[i].likeCount;
                                                    likeds = false;
                                                    break
                                                }
                                            }
                                        }
                                    })
                                }else{
                                    var id = e.target.parentNode.dataset.id
                                    $.ajax({
                                        type: "put",
                                        url: '/api/comments/' + id + '/like',
                                        data: JSON.stringify({}),
                                        dataType: "json",
                                        contentType: "application/json; charset=utf-8",
                                        success: function (like) {
                                            for (var i = 0; i < data.comments.length; ++i) {
                                                if (data.comments[i].id == id) {
                                                    e.target.setAttribute('src', 'img/zan-over.png');
                                                    ++data.comments[i].likeCount;
                                                    e.target.nextElementSibling.textContent = data.comments[i].likeCount;
                                                    likeds = true;
                                                    break
                                                }
                                            }
                                        }
                                    })
                                }
                            }
                        })
                    })
                }
            })
        }
    })
})

