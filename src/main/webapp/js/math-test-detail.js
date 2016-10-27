$(function () {

    let trueImage = document.createElement('img');
    trueImage.setAttribute('class', 'daan_error');
    trueImage.setAttribute('src', 'img/true.png');
    trueImage.setAttribute('alt', '');

    let falseImage = document.createElement('img');
    falseImage.setAttribute('class', 'daan_error');
    falseImage.setAttribute('src', 'img/error.png');
    falseImage.setAttribute('alt', '');


    let volumeId = g.getUrlParameter("volumeId");
    $.ajax({
        type: 'get',
        url: 'api/knowledge-points?filter=' + JSON.stringify({
            volumeId: parseInt(volumeId)
        }),
        dataType: 'json',
        success: function (knowledgePointList) {
            let id = g.getUrlParameter('id');
            $.ajax({
                type: "get",
                url: 'api/knowledge-points/' + id + '/contents',
                dataType: 'json',
                async: false,
                success: function (data) {
                    alert(JSON.stringify(data))
                    for (let i = 0; i < knowledgePointList.length; ++i) {
                        if (knowledgePointList[i].id == id) {
                            proc({
                                templateId: 'title-template',
                                data: {title: knowledgePointList[i].title},
                                containerId: 'title'
                            });
                            break
                        }
                    }
                    for (let i = 0; i < data.problems.length; ++i) {
                        let p = data.problems[i];
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
                    let findProblem = function (problemId) {
                        let problem = null
                        for (let i = 0; i < data.problems.length; ++i) {
                            if (data.problems[i].id == problemId) {
                                problem = data.problems[i]
                                break
                            }
                        }
                        return problem
                    }

                    let getIndex = function (content) {
                        let index = -1
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

                    let compareAnswer = function (index, standardAnswers) {
                        let finded = false
                        for (let j = 0; j < standardAnswers.length; ++j) {
                            if (index == standardAnswers[j].name) {
                                finded = true
                                break
                            }
                        }
                        return finded
                    }

                    let judgement = function (e) {
                        //e.currentTarget == problemContainer
                        let clickedElement = e.target;

                        if (clickedElement.hasClass('daan_quan')) { // == [class="daan_quan"]
                            let problemId = clickedElement.parentNode.parentNode.dataset.id
                            let problem = findProblem(problemId);
                            if (problem) {
                                let index = getIndex(clickedElement.textContent);
                                let r = compareAnswer(index, problem.standardAnswers);
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
                        let data = {
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
                    let problemContainer = document.getElementById('question')
                    problemContainer.addEventListener('click', judgement, false)

                }
            })
        }
    })
})