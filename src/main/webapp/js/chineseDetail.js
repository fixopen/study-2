$(function () {
    let volumeId = g.getUrlParameter('volumeId')
    let knowledgePointList = []

    $.ajax({
        type: 'get',
        url: 'api/knowledge-points?filter=' + JSON.stringify({
            volumeId: parseInt(volumeId)
        }),
        dataType: 'json',
        success: function (knowledgePoints) {
            knowledgePointList = knowledgePoints
        }
    })

    let id = g.getUrlParameter('id')

    $.ajax({
        type: "get",
        url: 'api/knowledge-points/' + id + '/contents',
        dataType: 'json',
        async: false,
        success: function (data) {
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
                    if (i < knowledgePointList.length - 2) {
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
        }
    })
})
