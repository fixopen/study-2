$(function () {
//
    //location.reload()
    //html利用js接受url传递来的参数。 比如:x.htm?abc=222。
    //htm中可以利用js获取到abc的值222。代码如下：

    let url=window.location.href;
    //       alert(url)
   //  http://localhost:8080/KnowledgePointsDetail.html?volumeId=1&id=1
    let volumeId=url.indexOf('=');
    if (volumeId>-1){
        volumeId=url.substring(volumeId+1);
    }
    volumeId = volumeId.substring(0,volumeId.indexOf('&'))
    //  alert(volumeId);
    let id=url.indexOf('&id=');
    if(id>-1){
        id=url.substring(id+4);

    }
     alert(id);
   //  let volumeId = getUrlParameter("volumeId")
   //  let id = getUrlParameter("id")
   //  alert(id);
    let  a = parseInt(volumeId)
    let  b = parseInt(id)
    let  knowledgePoints;


    // "apiolumes/" + b+"/",
    $.ajax({
        type:"get",
        url:'api/knowledgePoints/'+b+'/contents',
        dataType: 'json',
        async : false,
        success: function(kps){
          //  knowledgePoints=kps;
            alert(JSON.stringify(kps))
        }
    })

    // $.ajax({
    //     type:"get",
    //     url:'api/knowledgePoints?filter=' + JSON.stringify({subjectId:1,volumeId:a}),
    //     dataType: 'json',
    //     async : false,
    //     success: function(kps){
    //         knowledgePoints=kps;
    //         alert(JSON.stringify(kps))
    //     }
    // })

//     let knowledgePointsContent;
//     let texts,images,videos,problems;
//     filter={
//         volumesId: a,
//         knowledgePointsId:b
//     }
//     alert(JSON.stringify(filter));
//
//
// //            let contents=[];
//     $.ajax({
//         type:"get",
// //                url:'api/texts?filter=' + JSON.stringify(filter),
//         url:'api/texts',
//         dataType: 'json',
//         async : false,
//         success: function(ts){
//             alert(JSON.stringify(ts));
//         //    console.info(ts);
// //                    for(let i=0;i<ts.length;i++){
// //                        let tempObj={};
// //                        tempObj.type='text';
// //                        tempObj.content=ts[i].content;
// //                        contents.push(tempObj);
// //                     }
//             texts=ts;
// //                    console.log(contents);
// //                    alert(contents);
//         }})
//
//

//            $.ajax({
//                type:"get",
////                url:'api/knowledgePointContentMaps?filter=' + JSON.stringify(filter),
//                url:'api/knowledgePointContentMaps',
//                dataType: 'json',
//                async : false,
//                success: function(cs){
//                   alert(JSON.stringify(cs))
//                    knowledgePointsContent=cs;
//                }
//            })
//     $.ajax({
//         type:"get",
// //                url:'api/images?filter=' + JSON.stringify(filter),
//         url:'api/images',
//         dataType: 'json',
//         async : false,
//         success: function(is){
//             alert(JSON.stringify(is))
//             //console.info(is);
//             images=is;
// //                    for(let i=0;i<is.length;i++){
// //                        let temObj={};
// //                        temObj.type='image';
// //                        temObj.imgHref=is[i].storePath;
// //                        contents.push(temObj);
// //                    }
// //                    console.log(contents);
// //                    alert(contents);
//         }
//     })
//
//     $.ajax({
//         type:"get",
// //                url:'api/images?videos=' + JSON.stringify(filter),
//         url:'api/videos',
//         dataType: 'json',
//         async : false,
//         success: function(vs){
//             //  alert(JSON.stringify(vs))
//             videos=vs;
//         }
//     })
//            $.ajax({
//                type:"get",
////                url:'api/problems?filter=' + JSON.stringify(filter),
//                url:'api/problems',
//                dataType: 'json',
//                async : false,
//                success: function(ps){
//                   // alert(JSON.stringify(ps))
//                    problems=ps;
//                }
//            })
//            $.ajax({
//                type:"get",
//                url:'api/contents',
//                dataType: 'json',
//                data : JSON.stringify(filter),
//                async : false,
//                success: function(cs){
//                    alert(JSON.stringify(cs))
//                    knowledgePointsContent=cs;
//                }
//            })

//            let data = {
//                title: '放荡不羁名垂青史的竹林七贤',
//                origins: [
//                    {
//                        content: '陈留阮籍、谯国嵇康、河内山涛，三人年皆相比，康年少亚之。预此契者：沛国刘伶、陈留阮咸、河内向秀、琅邪王戎。七人常集于竹林之下，肆意酣畅，故世谓“竹林七贤”。',
//                        source: ' ——【南朝】刘义庆《世说新语·任诞》'
//                    },
//                    {
//                        content: '刘伶恒纵酒放达，或脱衣裸形在屋中，人见讥之，伶曰：“我以天地为栋宇，屋室为裈衣，诸君何为入我裈中？”',
//                        source: ' ——【南朝】刘义庆《世说新语·任诞》'
//                    }
//                ],
//                contents: [
//                    {
//                        type: 'text',
//                        content: '魏晋时期，是一个有太多动荡，太多危机的时期，长久的战乱，阴毒的政治，都使得离别、死亡成为太轻易的事，这些生离和死别，使得当时的知识分子们深刻地意识到生命的短暂和可贵，那么，如何在乱世中、在挣扎里，将生命活得珍重而绚烂呢？'
//                    },
//                    {
//                        type: 'image',
//                        imgHref: 'img/video1.png',
//                        description: '竹林七贤魅力担当——嵇康'
//                    },
//                    {
//                        type: 'text',
//                        content: '魏晋的名士们身体力行地向我们展示了一种耀眼的答案：他们轻视礼教，率直放诞，清俊通脱，喝酒纵歌，崇尚老庄，超然物外，成为了那阴森时期最为清亮的一抹颜色，在历史上绽放出令人神往的辉光，于是，他们的风度便成了“魏晋风度”。'
//                    },
//                    {
//                        type: 'text',
//                        content: '说起魏晋风度，必须要提到魏晋风度的最佳言人，也就是竹林七贤。所谓竹林七贤，就是常常在聚集在竹林，一起喝酒纵歌的七个人，他们分别是阮籍、嵇康、山涛、刘伶、阮咸、向秀、王戎，他们都有着共同的特点，那就是不拘礼法、特立独行，当然也都非常非常的有才华。'
//                    },
//                    {
//                        type: 'text',
//                        content: '竹林七贤中，最具有人格魅力的，当属嵇康了吧！嵇康身材高大，仪容俊美，精通音律，文采卓然。这样优秀的一个人，不喜结交权贵，拒绝涉足官场，选择在竹林中打铁谋生，真是超然物外，不为世俗所拘呀！见到他的人都称赞他的气度：“萧萧肃肃，爽朗清举”。后来因为得罪了小人，而被定罪处死，在行刑之时，一个超级的震撼的场面出现了：三千太学生集体为嵇康请愿，请求赦免嵇康之罪，并让嵇康当他们的老师。唉，可惜统治者并没有答应太学生们的请求，仍要赴死的嵇康面色不变，取出一把琴，弹了一曲慷慨动人的《广陵散》，然后从容赴死，真是从生到死，都那么酷帅啊！'
//                    },
//                    {
//                        type: 'text',
//                        content: '竹林七贤中另一位名声赫赫的个性大咖，那便是阮籍了。阮籍有一个最最拿手的特技，那就是“青白眼”！阮籍不经常说话，却常常用眼睛来帮自己表态.'
//                    },
//                    {
//                        type: 'image',
//                        imgHref: 'img/video2.png',
//                        description: '竹林七贤放荡之最——刘伶'
//                    },
//                    {
//                        type: 'text',
//                        content: '对待讨厌的人，就用白眼，对待喜欢的人，便用青眼。据说，阮籍的母亲去世后，嵇康的哥哥嵇喜前来致哀，而嵇喜是在朝为官的礼法之士，阮籍非常不喜欢他. 便当面赏他一个大大的白眼，而当嵇康带着酒、夹了琴登门时，阮籍立刻转白眼为青眼，亲热非常地迎了上去。阮籍的青白眼还真是好恶鲜明啊！'
//                    },
//                    {
//                        type: 'text',
//                        content: '在竹林七贤中，还有一位最最放诞、最最不羁的人，他就是刘伶！刘伶很丑，但很有性格，常常脱掉衣服，一丝不挂地在屋中饮酒，当有客人来访时，面对奇葩的刘伶，客人当然会嘲笑他的不雅之举，可是刘伶却对客人说：“天地就是我的房屋，而房屋就是我的衣裤，哎呀，你为什么跑到我的裤子里来啦？”真是极端的潇洒放达啊！'
//                    },
//                    {
//                        type: 'image',
//                        imgHref: 'img/video3.png',
//                        description: '竹林七贤放荡之最——刘伶'
//                    },
//                    {
//                        type: 'text',
//                        content: '此外，七贤中的山涛，极重信义，向秀，妙解《庄子》，阮咸，精善琵琶，王戎，吝啬精明，也是各有各的独特，各有各的闪光亮点。总之，竹林七贤展示着潇洒超脱，诠释着魏晋风度，完全是中国历史上最最闪亮的组合之一呢！'
//                    }
//                ],
//                video: {
//                    href: '',
//                    coverHref: 'img/video3.png',
//                    coverDescription: '塞花飘客泪,边柳挂乡愁'
//                },
//                problems: [
//                    {
//                        type: 'image',
//                        href: 'img/danx.png',
//                        title: '“萧萧肃肃，爽朗清举”是古人对谁的赞誉？',
//                        options: [{name:'A',option: '阮籍'}, {name:'B',option: '嵇康'}, {name:'C',option: '山涛'}, {name:'D',option: '阮咸'}]
//                    },
//                    {
//                        type: 'image',
//                        href: 'img/danx.png',
//                        title: '以下哪些人被认为是“魏晋风度”的代表？',
//                        options: [{name:'A',option: '嵇喜'}, {name:'B',option: '王戎'}, {name:'C',option: '曹操'}, {name:'D',option: '阮籍'}]
//                    },
//                    {
//                        type: 'image',
//                        href: 'img/double.png',
//                        title: '诸子百家中，最受“竹林七贤”喜欢的是谁？',
//                        options: [{name:'A',option: '庄子'}, {name:'B',option: '孟子'}, {name:'C',option: '孔子'}, {name:'D',option: '老子'}]
//                    }
//                ],
//                interaction: {
//                    readCount: 10000,
//                    likeCount: 3000,
//                    previous: '',
//                    next: ''
//                },
//                comments: [
//                    {
//                        avatar: 'img/pict1.png',
//                        name: '张三',
//                        likeCount: 20,
//                        content: '潇洒人生',
//                        time: '一天前'
//                    },
//                    {
//                        avatar: 'img/pict3.png',
//                        name: '李四',
//                        likeCount: 20,
//                        content: '不知所谓',
//                        time: '三天前'
//                    }
//                ]
//            }



    proc({
        templateId: 'title-template',
        data: knowledgePoints,
        containerId: 'title'
    })

//            proc({
//                templateId: 'origin-template',
//                data: data.origins,
//                containerId: 'origin'
//            })
    proc({
        data: contents,
        containerId: 'content',
        alterTemplates: [
            {type: 'text', templateId: 'content-text-template'},
            {type: 'image', templateId: 'content-img-template'}
        ]
    })
//            proc({
//                templateId: 'video-template',
//                data: data.video,
//                containerId: 'video'
//            })
//
//            proc({
//                templateId: 'problem-template',
//                data: data.problems,
//                containerId: 'problem',
//                secondBind: {
//                    extPoint: 'options',
//                    dataFieldName: 'options',
//                    templateId: 'problem-option-template'
//                }
//            })
//
//            proc({
//                templateId: 'interaction-template',
//                data: data.interaction,
//                containerId: 'interaction'
//            })
//
//            proc({
//                templateId: 'comment-template',
//                data: data.comments,
//                containerId: 'comments'
//            })
})