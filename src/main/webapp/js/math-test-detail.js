$(function () {

    var trueImage = document.createElement('img');
    trueImage.setAttribute('class', 'daan_error');
    trueImage.setAttribute('src', 'img/true.png');
    trueImage.setAttribute('alt', '');

    var falseImage = document.createElement('img');
    falseImage.setAttribute('class', 'daan_error');
    falseImage.setAttribute('src', 'img/error.png');
    falseImage.setAttribute('alt', '');



    var volumeId = g.getUrlParameter("volumeId");
    $.ajax({
        type: 'get',
        url: 'api/knowledge-points?filter=' + JSON.stringify({
            volumeId: parseInt(volumeId)
        }),
        dataType: 'json',
        success: function (knowledgePointList) {
            var id = g.getUrlParameter('id');
            $.ajax({
                type: "get",
                url: 'api/knowledge-points/' + id + '/contents',
                dataType: 'json',
                async: false,
                success: function (data) {
                    alert(JSON.stringify(data))
                    for (var i = 0; i < knowledgePointList.length; ++i) {
                        if (knowledgePointList[i].id == id) {
                            proc({
                                templateId: 'title-template',
                                data: {title: knowledgePointList[i].title},
                                containerId: 'title'
                            });
                            break
                        }
                    }
                    for (var i = 0; i < data.problems.length; ++i) {
                        var p = data.problems[i];
                        p.options[0].title = 'A';
                        p.options[1].title = 'B';
                        p.options[2].title = 'C';
                        p.options[3].title = 'D'
                    }

                    proc({
                        templateId: 'question-template',
                        data: data.problems,
                        containerId: 'question',
                        secondBind: [
                            {
                                extPoint: 'options',
                                dataFieldName: 'options',
                                templateId: 'question-option-template'
                            }
                        ]
                    });
                    var findProblem = function (problemId) {
                        var problem = null
                        for (var i = 0; i < data.problems.length; ++i) {
                            if (data.problems[i].id == problemId) {
                                problem = data.problems[i]
                                break
                            }
                        }
                        return problem
                    }

                    var getIndex = function (content) {
                        var index = -1
                        switch (content) {
                            case 'A':
                                index = 0
                                break
                            case 'B':
                                index = 1
                                break
                            case 'C':
                                index = 2
                                break
                            case 'D':
                                index = 3
                                break
                            default:
                                break
                        }
                        return index
                    }

                    var compareAnswer = function (index, standardAnswers) {
                        var finded = false
                        for (var j = 0; j < standardAnswers.length; ++j) {
                            if (index == standardAnswers[j].name) {
                                finded = true
                                break
                            }
                        }
                        return finded
                    }

                    var judgement = function (e) {
                        //e.currentTarget == problemContainer
                        var clickedElement = e.target;

                        if (clickedElement.hasClass('daan_quan')) { // == [class="daan_quan"]
                            var problemId = clickedElement.parentNode.parentNode.dataset.id
                            var problem = findProblem(problemId);
                            if (problem) {
                                var index = getIndex(clickedElement.textContent);
                                var r = compareAnswer(index, problem.standardAnswers);
                                if (r) {
                                    clickedElement.parentNode.addClass('daanLi_true');
                                    clickedElement.innerHTML = '';
                                    clickedElement.appendChild(trueImage.cloneNode(true))
                                } else {
                                    clickedElement.parentNode.addClass('daanLi_error')
                                    clickedElement.innerHTML = ''
                                    clickedElement.appendChild(falseImage.cloneNode(true))
                                }
                            }
                        }
                        var data = {
                            objectType: 'knowledge-point',
                            objectId: 'problemId',
                            objectName: 'index',
                            action: 'click'
                        };
                        $.ajax({
                            type: "post",
                            url: 'api/answer-records',
                            async: false,
                            data: data,
                            success: function (data) {
                                alert(JSON.stringify(data))
                            }
                        })
                    };
                    var problemContainer = document.getElementById('question')
                    problemContainer.addEventListener('click', judgement, false)

                }
            })
        }
    })
})