$(function () {

// message---------

    let createComment = document.getElementById('createComment');
    createComment.addEventListener('click', writeMessage, false);
    function writeMessage() {
        $('#commentWriter').toggle();
        let btn = document.getElementById('btn');
        btn.addEventListener('click', submit, false);
        function submit(e) {
            let textarea = document.getElementById('textarea');
            let value = textarea.value;
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
                    alert(JSON.stringify(data))
                }
            })
        }
    }

    let volumeId = g.getUrlParameter('volumeId')
    $.ajax({
        type: 'get',
        url: 'api/knowledge-points?filter=' + JSON.stringify({
            volumeId: parseInt(volumeId)
        }),
        dataType: 'json',
        success: function (knowledgePointList) {
            let id = g.getUrlParameter('id')
            $.ajax({
                type: "get",
                url: 'api/knowledge-points/' + id + '/contents',
                dataType: 'json',
                async: false,
                success: function (data) {
                    alert(JSON.stringify(data))
                    for (let i = 0; i < data.problems.length; ++i) {
                        let p = data.problems[i]
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
                            {type: 'pinyinText', templateId: 'content-pinyin-template'},
                            {type: 'image', templateId: 'content-img-template'}
                        ]
                    })

                    // pinyin
                    let pinyins = []
                    for(let i=0;i<data.contents.length;i++){
                        if (data.contents[i].type == 'pinyinText') {
                            pinyins.push(data.contents[i])
                        }
                    }

                    let ps = ['，', '。', '？','！','《','》','；','、','“','”','：','（','）','']

                    let isP = function(c) {
                        let result = false
                        for (let i = 0; i < ps.length; ++i) {
                            if (ps[i] == c) {
                                result = true
                                break
                            }
                        }
                        return result
                    }

                    for (let i = 0; i < pinyins.length; ++i) {
                        let pinyinItem = pinyins[i]
                        let pinyin = pinyinItem.pinyin.split(' ')
                        let chineseIndex = 0;
                        for (let j = 0; j < pinyin.length; ++j) {
                            let pinyinValue = pinyin[j]
                            let c = pinyinItem.content[chineseIndex]
                            ++chineseIndex
                            if (isP(c)) {
                                e = c
                                c = pinyinItem.content[chineseIndex]
                                ++chineseIndex
                            }
                            //<ruby><p>c</p><rt>pinyinValue</rt></ruby>
                            bind(e, {"pinyin": pinyinValue, "content":c})
                            e
                        }
                      //  let pinyin=data.contents[i].pinyin.split(" ");
                        //alert(pinyin);
                        //let content=data.contents[i].content.split('');
                        //alert(content);

                    }

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

                    // let type=
                    // if(type="单选题"){
                    //
                    // }

                    //answers
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


                    let problemContainer = document.getElementById('problem')
                    problemContainer.addEventListener('click', function (e) {
                        //e.currentTarget == problemContainer
                        let clickedElement = e.target
                        let trueImage = document.createElement('img')
                        trueImage.setAttribute('class', 'daan_error')
                        trueImage.setAttribute('src', 'img/true.png')
                        trueImage.setAttribute('alt', '')

                        let falseImage = document.createElement('img')
                        falseImage.setAttribute('class', 'daan_error')
                        falseImage.setAttribute('src', 'img/error.png')
                        falseImage.setAttribute('alt', '')

                        if (clickedElement.hasClass('daan_quan')) { // == [class="daan_quan"]
                            let problemId = clickedElement.parentNode.parentNode.dataset.id
                            let problem = findProblem(problemId)
                            if (problem) {
                                let index = getIndex(clickedElement.textContent)
                                let r = compareAnswer(index, problem.standardAnswers)
                                if (r) {
                                    clickedElement.parentNode.addClass('daanLi_true')
                                    clickedElement.innerHTML = ''
                                    clickedElement.appendChild(trueImage)
                                } else {
                                    clickedElement.parentNode.addClass('daanLi_error')
                                    clickedElement.innerHTML = ''
                                    clickedElement.appendChild(falseImage)
                                }
                            }
                        }
                    }, false)

                    //上一课下一课
                    let baseUrl = 'chineseKnowledgePointsDetail.html?volumeId=' + volumeId + "&id="
                    for (let i = 0; i < knowledgePointList.length; ++i) {
                        let id = g.getUrlParameter('id')
                        if (knowledgePointList[i].id == id) {
                            let prevIndex = i
                            let nextIndex = i
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
                    let id = g.getUrlParameter("id")
                    $.ajax({
                        type: "get",
                        url: 'api/knowledge-points/' + id + '/is-self-like',
                        dataType: "json",
                        success: function (like) {
                            let liked = like.like
                            let icon = document.getElementById('icon');
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
                                            e.target.nextElementSibling.textContent = data.interaction.likeCount
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
                        },
                        error: function (unlike) {
                            //liked = false
                        }
                    })

                    proc({
                        templateId: 'comment-template',
                        data: data.comments,
                        containerId: 'comments'
                    })

                    $('.ul01_imgzan_').on('click', function (e) {
                        let id = e.target.parentNode.dataset.id
                        $.ajax({
                            type: "get",
                            url: 'api/comments/' + id + '/is-self-like',
                            dataType: "json",
                            success: function (like) {
                               let  likeds = like.like;
                               if(likeds){
                                    let id = e.target.parentNode.dataset.id
                                    $.ajax({
                                        type: "put",
                                        url: '/api/comments/' + id + '/unlike',
                                        data: JSON.stringify({}),
                                        dataType: "json",
                                        contentType: "application/json; charset=utf-8",
                                        success: function (unlike) {
                                            for (let i = 0; i < data.comments.length; ++i) {
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
                                    let id = e.target.parentNode.dataset.id
                                    $.ajax({
                                        type: "put",
                                        url: '/api/comments/' + id + '/like',
                                        data: JSON.stringify({}),
                                        dataType: "json",
                                        contentType: "application/json; charset=utf-8",
                                        success: function (like) {
                                            for (let i = 0; i < data.comments.length; ++i) {
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
