$(document).ready(function () {
    var userId = g.getUrlParameter('userid')
    g.setCookie('userId', userId)

    //let subjectId  =  getUrlParameter('subjectId')
    $.ajax({
        type: "get",
        url: 'api/volumes?filter=' + JSON.stringify({
            subjectId: 2, //parseInt(subjectId),
            grade: 20
        }),
        success: function (volumesL) {
            proc({
                templateId: 'volumes-template',
                data: volumesL,
                containerId: 'volumes-low'
            });
        }
    })

    $.ajax({
        type: "get",
        url: 'api/volumes?filter=' + JSON.stringify({
            subjectId: 2,
            grade: 21
        }),
        success: function (volumesH) {
            proc({
                templateId: 'volumes-template',
                data: volumesH,
                containerId: 'volumes-hight'
            })
        }
    });
})