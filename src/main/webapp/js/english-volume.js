// $(function () {
//
//
//     let volumesR;
//     filter = {
//         subjectId: 3,
//         grade: 20
//     };
//     $.ajax({
//         type: "get",
//         url: 'api/volumes?filter=' + JSON.stringify(filter),
//         async: false,
//         success: function (vs) {
//             volumesR = vs;
//         }
//     });
//     let volumesP;
//     filter = {
//         subjectId: 3,
//         grade: 21
//     };
//     $.ajax({
//         type: "get",
//         url: 'api/volumes?filter=' + JSON.stringify(filter),
//         async: false,
//         success: function (vs) {
//             volumesP = vs
//         }
//     });
//     proc({
//         templateId: 'volumes-template',
//         data: volumesR,
//         containerId: 'volumes-reading'
//     })
//
//     proc({
//         templateId: 'volumes-template',
//         data: volumesP,
//         containerId: 'volumes-phonics'
//     })
//
// })
$(document).ready(function () {
    var userId = g.getUrlParameter('userid')
    g.setCookie('userId', userId)

    // $.ajax({
    //     type: "get",
    //     url: 'api/subjects/3/low/volumes',
    //     success: function (volumesL) {
    //         proc({
    //             data: volumesL,
    //             containerId: 'volumesReading',
    //             alterTemplates: [
    //                 {type: 'old', templateId: 'volumes-old-template'},
    //                 {type: 'new', templateId: 'volumes-new-template'},
    //             ]
    //         })
    //     }
    // })

    // $.ajax({
    //     type: "get",
    //     url: 'api/subjects/3/high/volumes',
    //     success: function (volumesH) {
    //         proc({
    //             data: volumesH,
    //             containerId: 'volumesPhonics',
    //             alterTemplates: [
    //                 {type: 'old', templateId: 'volumes-old-template'},
    //                 {type: 'new', templateId: 'volumes-new-template'},
    //             ]
    //         })
    //     }
    // });

    $.ajax({
        type: "get",
        url: '/api/subjects/' + 3 + '/popup',
        dataType: "json",
        success: function (pop) {
            //{"popup": true|false}
            var isPopup = pop.popup
            if (!isPopup) {
                newClass();//课程更新弹窗
                $.ajax({
                    type: "post",
                    url: '/api/subjects/' + 3,
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
