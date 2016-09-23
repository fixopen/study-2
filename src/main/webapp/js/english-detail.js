$(function () {
    let data = {
        title: 'My family',
        contents: [
            {
                type: 'image',
                href: 'img/eng_mo.png',
            },
            {
                type: 'image',
                href: 'img/eng_mot.png',
            },
            {
                type: 'image',
                href: 'img/eng_fa1.png',
            },
            {
                type: 'image',
                href: 'img/eng_fa2.png',
            },
            {
                type: 'image',
                href: 'img/eng_grma.png',
            },
        ],
        video: {
            href: '',
            coverHref: 'img/math5.png',
            coverDescription: '塞花飘客泪,边柳挂乡愁'
        },
        interaction: {
            readCount: 10000,
            likeCount: 3000,
            previous: '',
            next: ''
        },
        comments: [
            {
                avatar: 'img/pict1.png',
                name: '我是小学生',
                likeCount: 20,
                content: '依然是那片樱花林，依然是那棵樱花树，依然是我站在这里。',
                time: '一天前'
            },
            {
                avatar: 'img/pict2.png',
                name: '快乐的小孩儿',
                likeCount: 20,
                content: '依然是那片樱花林，依然是那棵樱花树，依然是我站在这里。',
                time: '三天前'
            }
        ]
    }


    proc({
        templateId: 'title-template',
        data: data,
        containerId: 'title'
    })
    proc({
        data: data.contents,
        containerId: 'contents',
        alterTemplates: [
            {type: 'image', templateId: 'content-image-template'}
        ]
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
})