$(document).ready(function () {

    $.ajax({
        type: "get",
        url: 'api/volumes?filter=' + JSON.stringify({
            subjectId: 1, //parseInt(subjectId),
            grade: 20
        }),
        success: function (volumesL) {
            proc({
                templateId: 'volumes-template',
                data: volumesL,
                containerId: 'volumesL'
            });
        }
    })

    $.ajax({
        type: "get",
        url: 'api/volumes?filter=' + JSON.stringify({
            subjectId: 1,
            grade: 21
        }),
        success: function (volumesH) {
            proc({
                templateId: 'volumes-template',
                data: volumesH,
                containerId: 'volumesH'
            })
        }
    });

});
