$(document).ready(function () {
    var userId = g.getUrlParameter('userid')
    g.setCookie('userId', userId)

    $.ajax({
        type: "get",
        url: 'api/subjects/1/low/volumes',
        success: function (volumesL) {
            // proc({
            //     templateId: 'volumes-template',
            //     data: volumesL,
            //     containerId: 'volumesL'
            // });
            proc({
                data: volumesL,
                containerId: 'volumesL',
                alterTemplates: [
                    {type: 'old', templateId: 'volumes-old-template'},
                    {type: 'new', templateId: 'volumes-new-template'},
                ]
            })
        }
    })

    $.ajax({
        type: "get",
        url: 'api/subjects/1/high/volumes',
        success: function (volumesH) {
            // proc({
            //     templateId: 'volumes-template',
            //     data: volumesH,
            //     containerId: 'volumesH'
            // })
            proc({
                data: volumesH,
                containerId: 'volumesH',
                alterTemplates: [
                    {type: 'old', templateId: 'volumes-old-template'},
                    {type: 'new', templateId: 'volumes-new-template'},
                ]
            })
        }
    });

    $.ajax({
        type: "get",
        url: '/api/subjects/' + 1 + '/popup',
        dataType: "json",
        success: function (pop) {
            //{"popup": true|false}
           var isPopup = pop.popup
           if(!isPopup){
               newClass();//课程更新弹窗
               $.ajax({
                   type: "post",
                   url: '/api/subjects/' + 1,
                   data: JSON.stringify({}),
                   dataType: "json",
                   contentType: "application/json; charset=utf-8",
                   success: function () {
                   }
               })
           }
        }
    });

});
