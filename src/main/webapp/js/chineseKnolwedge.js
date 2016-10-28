$(function () {
    // var userId = g.getUrlParameter('userid')
    // g.setCookie('userId', userId)
  // location.reload();
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
            //alert(JSON.stringify(knowledgePoints))
            // var now = new Date();
            // for (var i = 0; i < knowledgePoints.length; ++i) {
            //     var t = knowledgePoints[i].showTime;
            //     if (t < now - 24) {
            //         knowledgePoints[i].state = 'jiude';
            //     } else {
            //         knowledgePoints[i].state = 'xinde';
            //     }
            // }
            proc({
                data: knowledgePoints,
                containerId: 'knowledge-point',
                templateId: 'knowledge-point-template'
            });
        }
    })

})