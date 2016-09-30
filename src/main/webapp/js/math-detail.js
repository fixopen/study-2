function formateDate(date) {
    var y = date.getFullYear();
    var m = date.getMonth() + 1;
    var d = date.getDate();
    var h = date.getHours();
    var mi = date.getMinutes();
    m = m > 9 ? m : '0' + m;
    return y + '-' + m + '-' + d + ' ' + h + ':' + mi;

}
function like(el) {
    var total = document.getElementById('total');
    total.innerText = parseInt(total.innerText) + 1;
    el.disabled = true;
    // setTimeout(function () {
    //     el.disabled = false;
    // }, 10000);
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
    // setTimeout(function () {
    //     el.disabled = false;
    // }, 10000);
    let data ={
        objectType:'comment',
        Id:g.getUrlParameter("id"),
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
    let data ={
        objectType:'knowledge-point',
        objectId:parseInt(g.getUrlParameter("id")),
        action:'like'
    }
    $.ajax({
        type: 'get',
        url: 'api/logs?filter=' + JSON.stringify(data ),
        dataType: 'json',
        success: function (like) {
            alert(JSON.stringify(like))
            if(like !=null){
                alert("你已经点过赞了")
            }else{
            }
        }
    })


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
                url: "/api/comments",
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


