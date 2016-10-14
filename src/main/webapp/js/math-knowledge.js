$(function () {
    // getUrlParameter = function (name) {
    //     var result = null
    //     var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)") //构造一个含有目标参数的正则表达式对象
    //     var r = window.location.search.substr(1).match(reg) //匹配目标参数
    //     if (r != null) {
    //         result = decodeURI(r[2])
    //     }
    //     return result;//返回参数值
    // }
    proc({
        templateId: 'title-template',
        data: {title: g.getUrlParameter('volume')},
        containerId: 'title'
    })
    let volumeId = parseInt(g.getUrlParameter('volumeId'));
    $.ajax({
        type: "get",
        // url: 'api/knowledge-points?filter=' + JSON.stringify({
        //     subjectId: 2,
        //     volumeId: parseInt(getUrlParameter('volumeId')),
        //     grade: parseInt(getUrlParameter('grade'))
        // }),
        url: 'api/volumes/' + volumeId + '/knowledge-points',
        dataType: 'json',
        async: false,
        success: function (knowledgePoints) {
            alert(JSON.stringify(knowledgePoints))
            // let kp = []
            // for (let i = 0; i < knowledgePoints.length; ++i) {
            //     kp[i] = knowledgePoints[i]
            // }
            // let ktt = null
            // let kpt = []
            // if (kp.length > 0) {
            //     ktt = kp[kp.length - 1]
            //     kp.pop()
            //     kpt = kp
            // }
            // function select(knowledgePoints) {
            //     if(data.knowledgePoints.name=="挑战百分百"){
            //
            //     }
            // }
            proc({
                data: knowledgePoints,
                containerId: 'knowledge',
                alterTemplates: [
                    {type: 'normal', templateId: 'knowledge-point-template'},
                    {type: 'pk', templateId: 'knowledge-test-template'}
                ]
            });
            // proc({
            //     data: kpt,
            //     containerId: 'knowledge-point',
            //     templateId: 'knowledge-point-template'
            // });
            //
            // proc({
            //     data: ktt,
            //     containerId: 'knowledge-test',
            //     templateId: 'knowledge-test-template'
            // });
        }
    })
})
