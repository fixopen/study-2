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
        url: 'api/volumes/' + volumeId + '/knowledge-points',
        dataType: 'json',
        async: false,
        success: function (knowledgePoints) {
            var now = new Date()
            for (var i = 0; i < knowledgePoints.length; ++i) {
                var kp = knowledgePoints[i];
                var t = new Date(kp.showTime.replace(/-/g, "/"));
                var h = (now.getTime() - t.getTime()) / ( 60 * 60 * 1000);
                if (h < 24) {
                    kp.type = 'new';
                    knowledgePoints.splice(i, 1);
                    knowledgePoints.unshift(kp);
                } else {
                    kp.type = 'old';
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