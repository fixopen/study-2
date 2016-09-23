$(function () {
    // let data = {
    //   title: '横式问题找怪物',
    //   knowledgePoints: [
    //     {
    //       type:'point',
    //       name: '加与减',
    //       readCount: 10,
    //       likeCount: 20
    //     },
    //     {
    //       type:'point',
    //       name: '简单的图形数量规律',
    //       readCount: 10,
    //       likeCount: 20
    //     },
    //     {
    //       type:'point',
    //       name: '简单的图形规律',
    //       readCount: 10,
    //       likeCount: 20
    //     },
    //     {
    //       type:'point',
    //       name: '图形位置变化规律',
    //       readCount: 10,
    //       likeCount: 20
    //     },
    //     {
    //       type:'point',
    //       name: '图形组合规律',
    //       readCount: 10,
    //       likeCount: 20
    //     },
    //     {
    //       type:'point',
    //       name: '竖式的认识',
    //       readCount: 10,
    //       likeCount: 20
    //     },
    //     {
    //       type:'test',
    //       name: '挑战百分百',
    //       readCount: 10,
    //       likeCount: 20
    //     },
    //     {
    //       type:'point',
    //       name: '加与减',
    //       readCount: 10,
    //       likeCount: 20
    //     },
    //     {
    //       type:'point',
    //       name: '简单的图形数量规律',
    //       readCount: 10,
    //       likeCount: 20
    //     },
    //     {
    //       type:'point',
    //       name: '简单的图形规律',
    //       readCount: 10,
    //       likeCount: 20
    //     },
    //     {
    //       type:'point',
    //       name: '图形位置变化规律',
    //       readCount: 10,
    //       likeCount: 20
    //     },
    //     {
    //       type:'point',
    //       name: '图形组合规律',
    //       readCount: 10,
    //       likeCount: 20
    //     },
    //     {
    //       type:'point',
    //       name: '竖式的认识',
    //       readCount: 10,
    //       likeCount: 20
    //     },
    //     {
    //       type:'test',
    //       name: '挑战百分百',
    //       readCount: 10,
    //       likeCount: 20
    //     }
    //   ]
    // }
    //location.reload()
    //html利用js接受url传递来的参数。 比如:x.htm?abc=222。
    //htm中可以利用js获取到abc的值222。代码如下：

    var url = window.location.href;
    //alert(url)
    //http://localhost:8080/KnowledgePointsIndex.html?volumeId=1&grade=20
    var volumeId = url.indexOf('=');
    if (volumeId > -1) {
        volumeId = url.substring(volumeId + 1);
    }
    volumeId = volumeId.substring(0, volumeId.indexOf('&'))
    var grade = url.indexOf('&grade=');
    if (grade > -1) {
        grade = url.substring(grade + 7);

    }

    var a = parseInt(volumeId)
    var b = parseInt(grade)
    var knowledgePoints;
    var volumes;
    $.ajax({
        type: "get",
        // url:'api/volumes/' + volumeId,
        url: 'api/volumes?' + JSON.stringify(volumeId),
        dataType: 'json',
        async: false,
        success: function (vs) {
            volumes = vs;
            // alert(JSON.stringify(vs))
        }
    })

//            var volumes;

    let filterds = {
        subjectId: 2,
        volumeId: a,
        grade: b
    };
    //alert(JSON.stringify(filterds))
    $.ajax({
        type: "get",
        url: 'api/knowledgePoints?filter=' + JSON.stringify(filterds),
        dataType: 'json',
        async: false,
        success: function (kps) {
            knowledgePoints = kps;
            //alert("知识点"+JSON.stringify(kps))
            // alert(JSON.stringify(kps))
            proc({
                data: knowledgePoints,
                containerId: 'knowledge-point',
                templateId: 'knowledge-point-template'
                // alterTemplates: [
                //     {type: 'point', templateId: 'knowledge-point-template'},
                //     {type: 'test', templateId: 'knowledge-test-template'}
                // ]
            })
        }
    })


    proc({
        templateId: 'title-template',
        data: volumes[a],
        containerId: 'title'
    })

    // $("#split_line").css("width", $("#whole_width").width())

    $(window).resize(function () {
        $("#split_line").css("width", $("#whole_width").width())
    })
    window.onpageshow = function () {
        $("#split_line").css("width", $("#whole_width").width())
    }


})
