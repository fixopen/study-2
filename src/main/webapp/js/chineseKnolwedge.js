$(function () {
    //location.reload()
    //html利用js接受url传递来的参数。 比如:x.htm?abc=222。
    //htm中可以利用js获取到abc的值222。代码如下：

    // var url=window.location.href;
    // //alert(url)
    // //http://localhost:8080/KnowledgePointsIndex.html?volumeId=1&grade=20
    // var volumeId=url.indexOf('=');
    // if (volumeId>-1){
    //     volumeId=url.substring(volumeId+1);
    // }
    // volumeId = volumeId.substring(0,volumeId.indexOf('&'))
    // var grade = url.indexOf('&grade=');
    // if(grade>-1){
    //     grade=url.substring(grade+7);
    //
    // }
    let volumeId = g.getUrlParameter('volumeId')
    let grade = g.getUrlParameter('grade')
    var volumes;

    filterds = {
        subjectId: 1,
        volumeId: a,
        grade: b
    };
    $.ajax({
        type:"get",
        url:'api/volumes?filter=' + JSON.stringify(filterds),
        dataType: 'json',
        async : false,
        success: function(vs){
            volumes=vs;
         alert(JSON.stringify(vs))
            proc({
                templateId: 'title-template',
                data: volumes,
                containerId: 'title'
            })
        }
    })
    var  a = parseInt(volumeId)
    var  b = parseInt(grade)
    var  knowledgePoints;
   // var likes;
//            var volumes;

    filterds = {
        volumeId: a,
    };
    $.ajax({
        type:"get",
        url:'api/knowledge-points?filter=' + JSON.stringify(filterds),
        dataType: 'json',
        async : false,
        success: function(kps){
            knowledgePoints=kps;
            alert(JSON.stringify(kps))
            proc({
                templateId: 'volume-template',
                data: knowledgePoints,
                containerId: 'volume'
            })
        }
    })



//            $.ajax({
//                type:"get",
// //                url:'api/likes?filter=' + JSON.stringify(filterds),
//                url:'api/likes',
//                dataType: 'json',
//                async : false,
//                success: function(ls){
//                  //  likes=ls;
//                    alert(JSON.stringify(ls))
//                }
//            })


//            let data ={
//                title:'哈哈！这些奇葩的作家',
//                volumes:[
//                        {
//                            name:'白居易的诗情画意',
//                            readCount:'85',
//                            likeCount:'435'
//                        },
//                    {
//                        name:'杜甫的家国抱负',
//                        readCount:'4765',
//                        likeCount:'563'
//                    },
//                    {
//                        name:'杜甫的家国抱负',
//                        readCount:'562',
//                        likeCount:'564563'
//                    },
//                    {
//                        name:'白居易的诗情画意',
//                        readCount:'565546',
//                        likeCount:'56356'
//                    },
//                    {
//                        name:'杜甫的家国抱负',
//                        readCount:'796879',
//                        likeCount:'-078'
//                    },
//                    {
//                        name:'杜甫的家国抱负',
//                        readCount:'576',
//                        likeCount:'-7875'
//                    },
//                    {
//                        name:'杜甫的家国抱负',
//                        readCount:'6748',
//                        likeCount:'6746'
//                    },
//                    {
//                        name:'白居易的诗情画意',
//                        readCount:'8968',
//                        likeCount:'96'
//                    },
//                    {
//                        name:'白居易的诗情画意',
//                        readCount:'5455',
//                        likeCount:'7751'
//                    }
//                ]
//            }






})