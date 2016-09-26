$(function () {
    let getUrlParameter = function (name) {
        var result = null
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)") //构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg) //匹配目标参数
        if (r != null) {
            result = decodeURI(r[2])
        }
        return result;//返回参数值
    }

    proc({
        templateId: 'title-template',
        data: {title: getUrlParameter('volume')},
        containerId: 'title'
    })

    $.ajax({
        type: "get",
        url: 'api/knowledge-points?filter=' + JSON.stringify({
            subjectId: 2,
            volumeId: parseInt(getUrlParameter('volumeId')),
            grade: parseInt(getUrlParameter('grade'))
        }),
        dataType: 'json',
        async: false,
        success: function (knowledgePoints) {
            proc({
                data: knowledgePoints,
                containerId: 'knowledge-point',
                templateId: 'knowledge-point-template'
            });
        }
    })
})
