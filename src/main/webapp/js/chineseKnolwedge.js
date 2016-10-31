$(function () {
    // var userId = g.getUrlParameter('userid')
    // g.setCookie('userId', userId)

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

            //     proc({
            //         data: knowledgePoints,
            //         containerId: 'knowledge-point',
            //         templateId: 'knowledge-point-template'
            // });

            var now = new Date()
            for (var i = 0; i < knowledgePoints.length; ++i) {
                var t = new Date(knowledgePoints[i].showTime.replace(/-/g, "/"));
                var h = (now.getTime() - t.getTime()) / ( 60 * 60 * 1000);
                if (h < 24) {
                    knowledgePoints[i].type = 'new';
                    // $('.neir_lidiv a').eq(i).removeClass('neir_pzi').addClass('neir_pzi_');
                } else {
                    knowledgePoints[i].type = 'old';
                    // $('.neir_lidiv a').eq(i).removeClass('neir_pzi_').addClass('neir_pzi');
                }
            }

            proc({
                data: knowledgePoints,
                containerId: 'knowledge-point',
                alterTemplates: [
                    {type: 'old', templateId: 'knowledge-point-old-template'},
                    {type: 'new', templateId: 'knowledge-point-new-template'},
                ]
            })

        }
    })
})