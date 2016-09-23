$(function () {


    let volumesR;
    filter = {
        subjectId: 3,
        grade: 20
    };
    $.ajax({
        type: "get",
        url: 'api/volumes?filter=' + JSON.stringify(filter),
        async: false,
        success: function (vs) {
            volumesR = vs;
        }
    });
    let volumesP;
    filter = {
        subjectId: 3,
        grade: 21
    };
    $.ajax({
        type: "get",
        url: 'api/volumes?filter=' + JSON.stringify(filter),
        async: false,
        success: function (vs) {
            volumesP = vs
        }
    });
    proc({
        templateId: 'volumes-template',
        data: volumesR,
        containerId: 'volumes-reading'
    })

    proc({
        templateId: 'volumes-template',
        data: volumesP,
        containerId: 'volumes-phonics'
    })

})