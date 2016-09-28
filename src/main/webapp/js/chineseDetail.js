// function like() {
// //     let data ={
//         userId: 1,
//         objectType:'knowledge-point',
//         objectId:g.getUrlParameter("id"),
//         action:'like'
//     }
//     let data ={
//         userId: 1,
//         objectType:'knowledge-point',
//         objectId:g.getUrlParameter("id"),
//         action:'unlike'
//     }
//
//     $.ajax({
//         type: "post",
//         url: "/api/logs",
//         data: JSON.stringify(data),
//         dataType: "json",
//         contentType: "application/json; charset=utf-8",
//         success: function like() {
//             alert(JSON.stringify(data))
//         }
//     })
//
//     $.ajax({
//         type: "post",
//         url: "/api/comments",
//         data: JSON.stringify({objectType:'knowledge-point', objectId:g.getUrlParameter("id"), content: '...'}),
//         dataType: "json",
//         contentType: "application/json; charset=utf-8",
//         success: function like() {
//             alert(JSON.stringify(data))
//         }
//     })
// }

$(function () {
    let liked = false
    $.ajax({
        type: "get",
        url: "/api/logs?filter=" + JSON.stringify({objectType: 'knowledge-point', objectId: id, action: 'like'}),
        dataType: "json",
        success: function like(like) {
            liked = true
        },
        error: function like(unlike) {
            liked = false
        }
    })
    //change icon via liked state
    let icon = document.getElementById('icon')
    icon.addEventListener('click', function(e) {
        if (liked) {
            liked = false
        // *   unlike
        //     *   //event processor unlike
        //     *       notification unlike
        //     *       icon change to like, unliked
        //     *       likeCount - 1
        } else {
            liked = true
            //     *   like
            //     *   //event processor like
            //     *       notification like
            // *       icon change to unlike, liked
            // *       likeCount + 1
        }
    }, false)

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
                            {type: 'image', templateId: 'content-img-template'}
                        ]
                    })

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

                    let baseUrl = 'chineseKnowledgePointsDetail.html?volumeId=' + volumeId + "&id="
                    for (let i = 0; i < knowledgePointList.length; ++i) {
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

                    proc({
                        templateId: 'comment-template',
                        data: data.comments,
                        containerId: 'comments'
                    })

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
                                let r = compareAnswer(index, problem.ProblemStandardAnswer)
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

                    //POST /api/problems/{id}/answers
                    // answer-records
                    //
                    //[1,3,4]
                    // let problemId = clickedElement.parentNode.parentNode.dataset.id;
                    // let index = getIndex(clickedElement.textContent)
                    // let data ={
                    //     objectType:'knowledge-point',
                    //     objectId:'problemId',
                    //     objectName:'index',
                    //     action:'click'
                    // }
                    //
                    // $.ajax({
                    //     type: "post",
                    //     url: 'api/answer-records',
                    //     data: JSON.stringify(data),
                    //     async: false,
                    //     dataType: "json",
                    //     contentType: "application/json; charset=utf-8",
                    //     success: function (data) {
                    //         alert(JSON.stringify(data))
                    //     }
                    // })

                    // let data ={
                    //     objectType:'knowledge-point',
                    //     objectId:'problemId',
                    //     objectName:'index',
                    //     action:'click'
                    // }
                    //
                    // $.ajax({
                    //     type: "post",
                    //     url: 'api/answer-records',
                    //     async: false,
                    //     data: data,
                    //     success: function (data) {
                    //         alert(JSON.stringify(data))
                    //     }
                    // })

                }
            })
        }
    })
})
// {"comments":[],
//     "contents":[],
//     "interaction":{"likeCount":0,"readCount":76},
//     "title":"是神农",
//     "quotes":[],
//     "problems":[
//         {"options":[{"id":96647314735105,"problemId":96647314669568,"name":"11"},{"id":96647314735106,"problemId":96647314669568,"name":"22"},{"id":96647314735107,"problemId":96647314669568,"name":"33"},{"id":96647314735108,"problemId":96647314669568,"name":"44"}],
//             "id":96647314669568,
//             "type":"单选题",
//             "title":"1.神",
//             "ProblemStandardAnswer":[{"id":96647314735104,"problemId":96647314669568,"name":0}]
//         },
//        {"options":[{"id":96647316176898,"problemId":96647316176896,"name":"肃肃"},{"id":96647316176899,"problemId":96647316176896,"name":"物外"},{"id":96647316242432,"problemId":96647316176896,"name":"啊啊"},{"id":96647316242433,"problemId":96647316176896,"name":"吖吖"}],
//            "id":96647316176896,
//            "type":"单选题",
//            "title":"2.我",
//            "ProblemStandardAnswer":[{"id":96647316176897,"problemId":96647316176896,"name":1}]
//        }
//        ]
// }