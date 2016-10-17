$(function () {
    proc({
        templateId: 'title-template',
        data: {title: g.getUrlParameter('volume')},
        containerId: 'title'
    })
    var volumeId = parseInt(g.getUrlParameter('volumeId'));
    $.ajax({
        type: "get",
        // url: 'api/knowledge-points?filter=' + JSON.stringify({
        //     subjectId: 1,
        //     volumeId: parseInt(getUrlParameter('volumeId')),
        //     grade: parseInt(getUrlParameter('grade'))
        // }),
        url: 'api/volumes/' + volumeId + '/knowledge-points',
        dataType: 'json',
        async: false,
        success: function (knowledgePoints) {
            alert(JSON.stringify(knowledgePoints))
            proc({
                data: knowledgePoints,
                containerId: 'knowledge-point',
                templateId: 'knowledge-point-template'
            });
        }
    })
})