$(function () {
  // let data = {
  //   title1: '挑战一',
  //   title2: '简单的图形数量规律',
  //   challenge: [
  //     {
  //       content: '欢迎你加入直播数学战队，本期你将要挑战的是“掌握用凑十法计算20以内的加减法”。赶紧开始吧！',
  //     }
  //   ],
  //   contents: [
  //     {
  //       type: 'text',
  //       content: '今天，我们来学习加减法运算。'
  //     },
  //     {
  //       type: 'text',
  //       content: '在运算之前，我们先找找从1到10着10个数的计算规律。'
  //     },
  //     {
  //       type: 'image',
  //       href: 'img/math4.png',
  //       description: 'directions',
  //       content: ' 在20以内的加法计算中，估计你最喜欢10了，因为10加几就等于几，如10+1=11……如果所有的加法计算都像这样，那就太简单了，但这是不可能的啊。哈哈……'
  //     },
  //     {
  //       type: 'text',
  //       content: '很多加法算式都没有10，想要计算简单，就只能去凑十啦！'
  //     },
  //     {
  //       type: 'image',
  //       href: 'img/math4.png',
  //       description: 'directions',
  //       content: ' 咦，1和9,2和8,3和7……想家都等于10呀！我们把这样能凑十的两个数称为好朋友，只要在计算式找出好朋友，就能保证让你的计算更简单啦！'
  //     },
  //     {
  //       type: 'image',
  //       href: 'img/math4.png',
  //       description: 'directions',
  //       content: '咦，1和9,2和8,3和7……想家都等于10呀！我们把这样能凑十的两个数称为好朋友，只要在计算式找出好朋友，就能保证让你的计算更简单啦！'
  //     },
  //     {
  //       type: 'text',
  //       content: '总结一下：在做加法时，先找出好朋友凑出10，再加上剩下的数。在做减法时，只要拆除10来做减法，再将剩下的数加上。'
  //     },
  //
  //     {
  //       type: 'text',
  //       content: '好了，内容讲完了。下面，进入联系时间吧！'
  //     }
  //     // {
  //     //   type: 'video',
  //     //   href: '',
  //     //   coverHref: 'img/math5.png',
  //     //   coverDescription: '塞花飘客泪,边柳挂乡愁'
  //     // }
  //   ],
  //   video: {
  //     href: '',
  //     coverHref: 'img/math5.png',
  //     coverDescription: '塞花飘客泪,边柳挂乡愁'
  //   },
  //   strongestBrains: [
  //     {
  //       // type: '单选题',
  //       title: '1. 图中共有多少个水果呢？下列选项中，算式及结果均正确的是哪个？',
  //       img:'img/math6.png',
  //       options: [{name: 'A', option: '6+4=10'}, {name: 'B', option: '6+4=9'}, {name: 'C', option: '7+3=10'}, {name: 'D', option: '7+3=9'}],
  //       video: {
  //         href: '',
  //         coverHref: 'img/math5.png',
  //         coverDescription: '塞花飘客泪,边柳挂乡愁'
  //       }
  //     },
  //     {
  //       // type: '单选题',
  //       title: '2. 原来有10只蝴蝶，后来飞走了7只，那么现在还剩下几只蝴蝶？下列选项中，算式及结果均正确的是哪个？',
  //       img:'img/math7.png',
  //       options: [{name: 'A',option: '10-6=4'}, {name: 'B',option: '10-7=3'}, {name: 'C',option: '10-7=4'}, {name: 'D',option: '10-5=4'}],
  //       video: {
  //         href: '',
  //         coverHref: 'img/math5.png',
  //         coverDescription: '塞花飘客泪,边柳挂乡愁'
  //       }
  //     }
  //   ],
  //   pk: {
  //     // type: '单选题',
  //     title: '3. 以下算式中计算正确的是哪一个呢？你准备好和父母抢答了吗？',
  //     options: [{name: 'A',option: '2+8=10'}, {name: 'B',option: '3+9=11'}, {name: 'C',option: '8+4=11'}, {name: 'D',option: '7+5=10'}]
  //   },
  //   interaction: {
  //     readCount: 10000,
  //     likeCount: 3000,
  //     previous: '',
  //     next: ''
  //   },
  //   comments: [
  //     {
  //       avatar: 'img/pict1.png',
  //       name: '我是小学生',
  //       likeCount: 20,
  //       content: '依然是那片樱花林，依然是那棵樱花树，依然是我站在这里。',
  //       time: '一天前'
  //     },
  //     {
  //       avatar: 'img/pict2.png',
  //       name: '快乐的小孩儿',
  //       likeCount: 20,
  //       content: '依然是那片樱花林，依然是那棵樱花树，依然是我站在这里。',
  //       time: '三天前'
  //     }
  //   ]
  // }

//   let url=window.location.href;
// //            alert(url)
//   //  http://localhost:8080/KnowledgePointsDetail.html?volumeId=1&id=1
//   let volumeId=url.indexOf('=');
//   if (volumeId>-1){
//     volumeId=url.substring(volumeId+1);
//   }
//   volumeId = volumeId.substring(0,volumeId.indexOf('&'))
  //  alert(volumeId);
  // let id=url.indexOf('&id=');
  // if(id>-1){
  //   id=url.substring(id+4);
  // }

let volumeId = getUrlParameter("volumeId")
  let id = getUrlParameter("id")
  //  alert(id);


  let  a = parseInt(volumeId)
  let  b = parseInt(id)
  let  data;

  $.ajax({
    type:"get",
    url:"api/knowledgePoints/"+b+"/contents",
    dataType: 'json',
    async : false,
    success: function(kps){
      data=kps;
      for (let i = 0; i < data.problems.length; ++i) {
        let p = data.problems[i]
        if (p.options && (p.options.length == 4)) {
          p.options[0].title = 'A'
          p.options[1].title = 'B'
          p.options[2].title = 'C'
          p.options[3].title = 'D'
        }
      }
      alert(JSON.stringify(kps))
      console.info(kps)
      proc({
        templateId: 'title2-template',
        data:data,
        containerId: 'title2'
      })
      // proc({
      //   templateId: 'title1-template',
      //   data: {"title1":id},
      //   containerId: 'title1'
      // })


      proc({
        templateId: 'challenge-template',
        data: data.quotes,
        containerId: 'challenge'
      })


      proc({
        data: data.contents,
        containerId: 'content',
        alterTemplates: [
          {type: 'text', templateId: 'content-text-template'},
          {type: 'imageText', templateId: 'content-image-template'}
        ]
      })

      proc({
        templateId: 'video-template',
        data: data.video,
        containerId: 'video'
      })

      proc({
        templateId: 'strongest-brain-template',
        data: data.problems,
        containerId: 'strongest-brain',
        secondBind: [
          {
            extPoint: 'options',
            dataFieldName: 'options',
            templateId: 'strongest-brain-option-template'
          },
          {
            extPoint: 'explain',
            dataFieldName: 'video',
            templateId: 'video-template'
          }
        ]
      })

      proc({
        templateId: 'pk-template',
        data: data.problems,
        containerId: 'pk',
        secondBind: [
          {
            extPoint: 'options',
            dataFieldName: 'options',
            templateId: 'pk-option-template'
          }]
      })

      proc({
        templateId: 'interaction-template',
        data: data.interaction,
        containerId: 'interaction'
      })

      proc({
        templateId: 'comment-template',
        data: data.comments,
        containerId: 'comments'
      })
    }
  })
})
  //alert(JSON.stringify(filterds))
  // $.ajax({
  //   type:"get",
  //   url:'api/knowledgePoints?filter=' + JSON.stringify(filterds),
  //   dataType: 'json',
  //   async : false,
  //   success: function(kps){
  //     knowledgePoints=kps;
  //     //alert("知识点"+JSON.stringify(kps))
  //     // alert(JSON.stringify(kps))
  //     proc({
  //       data: knowledgePoints,
  //       containerId: 'knowledge-point',
  //       templateId: 'knowledge-point-template'
  //       // alterTemplates: [
  //       //     {type: 'point', templateId: 'knowledge-point-template'},
  //       //     {type: 'test', templateId: 'knowledge-test-template'}
  //       // ]
  //     })
  //   }
  // })


