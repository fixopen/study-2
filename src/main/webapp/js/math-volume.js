$(document).ready(function () {
    var userId = g.getUrlParameter('userid')
    g.setCookie('userId', userId)
    //let subjectId  =  getUrlParameter('subjectId')
    $.ajax({
        type: "get",
        url: 'api/subjects/2/low/volumes',
        success: function (volumesL) {
            proc({
                data: volumesL,
                containerId: 'volumes-low',
                alterTemplates: [
                    {type: 'old', templateId: 'volumes-old-template'},
                    {type: 'new', templateId: 'volumes-new-template'},
                ]
            });
        }
    })

    $.ajax({
        type: "get",
        url: 'api/subjects/2/high/volumes',
        success: function (volumesH) {
            proc({
                data: volumesH,
                containerId: 'volumes-hight',
                alterTemplates: [
                    {type: 'old', templateId: 'volumes-old-template'},
                    {type: 'new', templateId: 'volumes-new-template'},
                ]
            })
        }
    });
    $.ajax({
        type: "get",
        url: '/api/subjects/' + 2 + '/popup',
        dataType: "json",
        success: function (pop) {
            //{"popup": true|false}
            var isPopup = pop.popup
            if (!isPopup) {
                newClass();//课程更新弹窗
                $.ajax({
                    type: "post",
                    url: '/api/subjects/' + 2,
                    data: JSON.stringify({}),
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    success: function () {
                    }
                })
            }
        }
    });
})