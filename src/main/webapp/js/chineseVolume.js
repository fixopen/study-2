$(document).ready(function() {

  let volumesL
  // $.ajax({
  // 	type: "get",
  // 	url: "api/subjects",
  // 	dataType: "json",
  // 	async: false,
  // 	success: function (a) {
  // 		volumesL = a;
  // 		for (var i = 0; i < a.length; ++i) {
  filter = {
    subjectId: 1,
    grade: 20
  };
  $.ajax({
    type: "get",
    url: 'api/volumes?filter=' + JSON.stringify(filter),
    async: false,
    success: function (vs) {
    // alert(JSON.stringify(vs))
      volumesL= vs
    }
  })
  // 		}
  // 	}
  // })
  // alert(JSON.stringify(volumesL))
  let volumesH
  filter = {
    subjectId: 1,
    grade: 21
  };
  $.ajax({
    type: "get",
    url: 'api/volumes?filter=' + JSON.stringify(filter),
    async: false,
    success: function (vs) {
      // alert(JSON.stringify(vs))
      volumesH = vs
      /*for (let j = 0; j < vs.length; j++) {
       volumesH[j].high = vs[j].title

       }*/
      //alert(JSON.stringify(subjects[i].low))
    }
  })
  //alert(JSON.stringify(volumesH))
  //alert(JSON.stringify(volumesH))



  proc({
    templateId: 'volumes-template',
    data: volumesL,
    containerId: 'mathStudy-volumesL'
  })

  proc({
    templateId: 'volumes-template',
    data: volumesH,
    containerId: 'mathStudy-volumesH'
  })

});
