$(function () {

  let volumeId = getUrlParameter("volumeId")
  let knowledgePointList = []

  $.ajax({
    type: 'get',
    url: 'api/knowledge-points?filter=' + JSON.stringify({
      volumeId: parseInt(volumeId)
    }),
    dataType: 'json',
    success: function (knowledgePoints) {
      knowledgePointList = knowledgePoints
    }
  })

  let id = g.getUrlParameter('id')

  $.ajax({
    type:"get",
    url: 'api/knowledge-points/' + id + '/contents',
    dataType: 'json',
    async : false,
    success: function(data){
      alert( JSON.stringify(data))
      for (let i = 0; i < data.problems.length; ++i) {
        let p = data.problems[i];
        if (p.options && (p.options.length == 4)) {
          p.options[0].title = 'A';
          p.options[1].title = 'B';
          p.options[2].title = 'C';
          p.options[3].title = 'D'
        }
      }

      proc({
        templateId: 'title1-template',
        data:order,
        containerId: 'title1'
      });
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


