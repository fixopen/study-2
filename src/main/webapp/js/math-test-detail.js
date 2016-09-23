$(function () {
    let data = {
        questions: [
            {
                // type: '单选题',
                title: '1. 图中共有多少个水果呢？下列选项中，算式及结果均正确的是哪个？',
                img: 'img/math6.png',
                options: [{name: 'A', option: '6+4=10'}, {name: 'B', option: '6+4=9'}, {
                    name: 'C',
                    option: '7+3=10'
                }, {name: 'D', option: '7+3=9'}],
                video: {
                    href: '',
                    coverHref: 'img/math5.png',
                    coverDescription: '塞花飘客泪,边柳挂乡愁'
                }
            },
            {
                // type: '单选题',
                title: '2. 原来有10只蝴蝶，后来飞走了7只，那么现在还剩下几只蝴蝶？下列选项中，算式及结果均正确的是哪个？',
                img: 'img/math7.png',
                options: [{name: 'A', option: '10-6=4'}, {name: 'B', option: '10-7=3'}, {
                    name: 'C',
                    option: '10-7=4'
                }, {name: 'D', option: '10-5=4'}],
                video: {
                    href: '',
                    coverHref: 'img/math5.png',
                    coverDescription: '塞花飘客泪,边柳挂乡愁'
                }
            },
            {
                // type: '单选题',
                title: '3. 图中共有多少个水果呢？下列选项中，算式及结果均正确的是哪个？',
                img: 'img/math6.png',
                options: [{name: 'A', option: '6+4=10'}, {name: 'B', option: '6+4=9'}, {
                    name: 'C',
                    option: '7+3=10'
                }, {name: 'D', option: '7+3=9'}],
                video: {
                    href: '',
                    coverHref: 'img/math5.png',
                    coverDescription: '塞花飘客泪,边柳挂乡愁'
                }
            }
        ]
    }


    proc({
        templateId: 'video-template',
        data: data.video,
        containerId: 'video'
    })

    proc({
        templateId: 'question-template',
        data: data.questions,
        containerId: 'question',
        secondBind: [
            {
                extPoint: 'options',
                dataFieldName: 'options',
                templateId: 'question-option-template'
            },
            {
                extPoint: 'explain',
                dataFieldName: 'video',
                templateId: 'video-template'
            }
        ]
    })

})