$(function () {

let volumeId = getUrlParameter("volumeId")
  let id = getUrlParameter("id")
  // let volumeId = getUrlParameter('volumeId')
  // //  alert(id);
  // let url=window.location.href;
  // //       alert(url)
  // //  http://localhost:8080/KnowledgePointsDetail.html?volumeId=1&id=1
  //
  // if (volumeId>-1){
  //   volumeId=url.substring(volumeId+1);
  // }
  // volumeId = volumeId.substring(0,volumeId.indexOf('&'))
  // //  alert(volumeId);

  // if(id>-1){
  //   id=url.substring(id+4);
  // }
  alert(id);
  alert(volumeId)
  //  let volumeId = getUrlParameter("volumeId")
  //  let id = getUrlParameter("id")
  //  alert(id);
  let  a = parseInt(volumeId)
  let  b = parseInt(id)
  let  data;

  $.ajax({
    type:"get",
    url:"api/knowledge-points/"+b+"/contents",
    dataType: 'json',
    async : false,
    success: function(kps){
      data=kps;
      for (let i = 0; i < data.problems.length; ++i) {
        let p = data.problems[i];
        if (p.options && (p.options.length == 4)) {
          p.options[0].title = 'A';
          p.options[1].title = 'B';
          p.options[2].title = 'C';
          p.options[3].title = 'D'
        }
      }
      alert(JSON.stringify(kps));
      console.info(kps);
      proc({
        templateId: 'title2-template',
        data:data,
        containerId: 'title2'
      });

      proc({
        templateId: 'challenge-template',
        data: data.quotes,
        containerId: 'challenge'
      });


      proc({
        data: data.contents,
        containerId: 'content',
        alterTemplates: [
          {type: 'text', templateId: 'content-text-template'},
          {type: 'imageText', templateId: 'content-image-template'}
        ]
      });

      proc({
        templateId: 'video-template',
        data: data.video,
        containerId: 'video'
      });

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
      });

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
      });

      proc({
        templateId: 'interaction-template',
        data: data.interaction,
        containerId: 'interaction'
      });

      proc({
        templateId: 'comment-template',
        data: data.comments,
        containerId: 'comments'
      })
    }
  })
})


