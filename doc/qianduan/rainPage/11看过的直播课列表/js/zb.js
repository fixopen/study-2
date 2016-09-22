$(function () {
    let data = {
        titleSrc: '',
        chineseInfo: [
            {
                src: '',
                backgroundImage: 'images/kcdh1.jpg',
                lessonOrder: '1',
                description: '【新一语文】1期第一讲：语文作文与写作手法',
                teacher: '赵老师',
                grade: '一年级',
                watcherCount: '121',
                // detailList: [
                //     {
                //         detailList: '一、汉语拼音',
                //         listList: '1.读准汉语拼音的声母、韵母、声调认读的音节。',
                //         listList: '2.能默写声母、韵母并抄写音节，上写得正确整。',
                //     },
                //     {
                //         detailList: '二、识字、写字',
                //         listList: '1.掌握汉字的基本笔画、笔顺规则、间架结构和常用的偏旁部首。',
                //         listList: '2.能借助汉语拼音解字义。'
                //     }
                // ],
                teacherInfo: '董 霞，女，本科学历，小学语文高级教师，现任文学部教学主管，文学写作项目、童话阅读项目专业指导教师。《母婴杂志》绘本栏目特邀专家顾问。曾获杭州市教坛新秀、杭州市小学语文学科带头人、杭州市优秀班主任等荣誉；多次在杭州市语文教学观摩活动中获奖。出版《小学生成语故事》、《青少年宫文学写作专用教材》二、三册。撰写的论文多次在杭州市校外教育论文评选活动中获奖。',
                onlineTime: '2016-03-11    10:22:33',
                courseDuration: '30分钟'
            },
            {
                src: '',
                backgroundImage: 'images/kcdh2.jpg',
                lessonOrder: '1',
                description: '【新一语文】1期第一讲：哈哈哈哈哈哈哈哈',
                teacher: '赵老师',
                grade: '一年级',
                watcherCount: '121',
                // detailList: [
                //     {
                //         detailList: '一、汉语拼音',
                //         listList: '1.读准汉语拼音的声母、韵母、声调认读的音节。',
                //         listList: '2.能默写声母、韵母并抄写音节，上写得正确整。'
                //     },
                //     {
                //         detailList: '二、识字、写字',
                //         listList: '1.掌握汉字的基本笔画、笔顺规则、间架结构和常用的偏旁部首。',
                //         listList: '2.能借助汉语拼音解字义。'
                //     }
                // ],
                teacherInfo: '董 霞，女，本科学历，小学语文高级教师，现任文学部教学主管，文学写作项目、童话阅读项目专业指导教师。《母婴杂志》绘本栏目特邀专家顾问。曾获杭州市教坛新秀、杭州市小学语文学科带头人、杭州市优秀班主任等荣誉；多次在杭州市语文教学观摩活动中获奖。出版《小学生成语故事》、《青少年宫文学写作专用教材》二、三册。撰写的论文多次在杭州市校外教育论文评选活动中获奖。',
                onlineTime: '2016-03-11    10:22:33',
                courseDuration: '30分钟'
            }
        ],
        mathInfo: [
            {
                src: '',
                backgroundImage: 'images/kcdh2.jpg',
                lessonOrder: '1',
                description: '【新一数学】1期第一讲：啦啦啦啦啦啦啦啦啦',
                teacher: '赵老师',
                grade: '一年级',
                watcherCount: '121',
                // detailList: [
                //     {
                //         detailList: '一、汉语拼音',
                //         listList: '1.读准汉语拼音的声母、韵母、声调认读的音节。',
                //         listList: '2.能默写声母、韵母并抄写音节，上写得正确整。',
                //     },
                //     {
                //         detailList: '二、识字、写字',
                //         listList: '1.掌握汉字的基本笔画、笔顺规则、间架结构和常用的偏旁部首。',
                //         listList: '2.能借助汉语拼音解字义。'
                //     }
                // ],
                teacherInfo: '董 霞，女，本科学历，小学语文高级教师，现任文学部教学主管，文学写作项目、童话阅读项目专业指导教师。《母婴杂志》绘本栏目特邀专家顾问。曾获杭州市教坛新秀、杭州市小学语文学科带头人、杭州市优秀班主任等荣誉；多次在杭州市语文教学观摩活动中获奖。出版《小学生成语故事》、《青少年宫文学写作专用教材》二、三册。撰写的论文多次在杭州市校外教育论文评选活动中获奖。',
                onlineTime: '2016-03-11    10:22:33',
                courseDuration: '30分钟'
            },
            {
                src: '',
                backgroundImage: 'images/kcdh1.jpg',
                lessonOrder: '1',
                description: '【新一语文】1期第一讲：红红火火恍恍惚惚',
                teacher: '赵老师',
                grade: '一年级',
                watcherCount: '121',
                // detailList: [
                //     {
                //         detailList: '一、汉语拼音',
                //         listList: '1.读准汉语拼音的声母、韵母、声调认读的音节。',
                //         listList: '2.能默写声母、韵母并抄写音节，上写得正确整。'
                //     },
                //     {
                //         detailList: '二、识字、写字',
                //         listList: '1.掌握汉字的基本笔画、笔顺规则、间架结构和常用的偏旁部首。',
                //         listList: '2.能借助汉语拼音解字义。'
                //     }
                // ],
                teacherInfo: '董 霞，女，本科学历，小学语文高级教师，现任文学部教学主管，文学写作项目、童话阅读项目专业指导教师。《母婴杂志》绘本栏目特邀专家顾问。曾获杭州市教坛新秀、杭州市小学语文学科带头人、杭州市优秀班主任等荣誉；多次在杭州市语文教学观摩活动中获奖。出版《小学生成语故事》、《青少年宫文学写作专用教材》二、三册。撰写的论文多次在杭州市校外教育论文评选活动中获奖。',
                onlineTime: '2016-03-11    10:22:33',
                courseDuration: '30分钟'
            }
        ],
        englishInfo: [
            {
                src: '',
                backgroundImage: 'images/kcdh1.jpg',
                lessonOrder: '1',
                description: '【新一英语】1期第一讲：刚回家多久',
                teacher: '赵老师',
                grade: '一年级',
                watcherCount: '121',
                // detailList: [
                //     {
                //         detailList: '一、汉语拼音',
                //         listList: '1.读准汉语拼音的声母、韵母、声调认读的音节。',
                //         listList: '2.能默写声母、韵母并抄写音节，上写得正确整。',
                //     },
                //     {
                //         detailList: '二、识字、写字',
                //         listList: '1.掌握汉字的基本笔画、笔顺规则、间架结构和常用的偏旁部首。',
                //         listList: '2.能借助汉语拼音解字义。'
                //     }
                // ],
                teacherInfo: '董 霞，女，本科学历，小学语文高级教师，现任文学部教学主管，文学写作项目、童话阅读项目专业指导教师。《母婴杂志》绘本栏目特邀专家顾问。曾获杭州市教坛新秀、杭州市小学语文学科带头人、杭州市优秀班主任等荣誉；多次在杭州市语文教学观摩活动中获奖。出版《小学生成语故事》、《青少年宫文学写作专用教材》二、三册。撰写的论文多次在杭州市校外教育论文评选活动中获奖。',
                onlineTime: '2016-03-11    10:22:33',
                courseDuration: '30分钟'
            },
            {
                src: '',
                backgroundImage: 'images/kcdh1.jpg',
                lessonOrder: '1',
                description: '【新一语文】1期第一讲：到阜阳环卫固化为双方宽容覅热',
                teacher: '赵老师',
                grade: '一年级',
                watcherCount: '121',
                // detailList: [
                //     {
                //         detailList: '一、汉语拼音',
                //         listList: '1.读准汉语拼音的声母、韵母、声调认读的音节。',
                //         listList: '2.能默写声母、韵母并抄写音节，上写得正确整。'
                //     },
                //     {
                //         detailList: '二、识字、写字',
                //         listList: '1.掌握汉字的基本笔画、笔顺规则、间架结构和常用的偏旁部首。',
                //         listList: '2.能借助汉语拼音解字义。'
                //     }
                // ],
                teacherInfo: '董 霞，女，本科学历，小学语文高级教师，现任文学部教学主管，文学写作项目、童话阅读项目专业指导教师。《母婴杂志》绘本栏目特邀专家顾问。曾获杭州市教坛新秀、杭州市小学语文学科带头人、杭州市优秀班主任等荣誉；多次在杭州市语文教学观摩活动中获奖。出版《小学生成语故事》、《青少年宫文学写作专用教材》二、三册。撰写的论文多次在杭州市校外教育论文评选活动中获奖。',
                onlineTime: '2016-03-11    10:22:33',
                courseDuration: '30分钟'
            }
        ]
    }
    // let g = {
    //     chineseIfo: [
    //         {id: 1, name: '的催收发货', title: 'hhhh'},
    //         {id: 2, name: '电磁阀才打得过法律', title: 'sdt'},
    //         {id: 3, name: '没客人高热健康', title: 'xxxx'},
    //         {id: 4, name: 'xiao', title: 'jjjj'},
    //         {id: 5, name: 'xiao', title: 'ddds'},
    //         {id: 6, name: 'xiao', title: 'hhhh'},
    //         {id: 7, name: 'xiao', title: 'vccvcv'},
    //         {id: 8, name: 'xiao', title: 'ewr23'}
    //     ],
    //     mathIfo: [
    //         {id: 1, name: 'hight', title: 'hhhh'},
    //         {id: 2, name: 'hight', title: 'sdt'},
    //         {id: 3, name: 'hight', title: 'xxxx'},
    //         {id: 4, name: 'hight', title: 'jjjj'},
    //         {id: 5, name: 'hight', title: 'ddds'},
    //         {id: 6, name: 'hight', title: 'hhhh'},
    //         {id: 7, name: 'hight', title: 'vccvcv'},
    //         {id: 8, name: 'hight', title: 'ewr23'}
    //     ],
    //     englishIfo: [
    //         {id: 1, name: 'hight', title: 'hhhh'},
    //         {id: 2, name: 'hight', title: 'sdt'},
    //         {id: 3, name: 'hight', title: 'xxxx'},
    //         {id: 4, name: 'hight', title: 'jjjj'},
    //         {id: 5, name: 'hight', title: 'ddds'},
    //         {id: 6, name: 'hight', title: 'hhhh'},
    //         {id: 7, name: 'hight', title: 'vccvcv'},
    //         {id: 8, name: 'hight', title: 'ewr23'}
    //     ],
    // }
    //
    // g.bind = function (element, data) {
    //     element.innerHTML = element.innerHTML.replace('%7B', '{').replace('%7D', '}').replace(/\$\{(\w+)\}/g, function (all, letiable) {
    //         if (!letiable) {
    //             return ""
    //         }
    //         return data[letiable];
    //     });
    //     return element
    // };
    //
    // let subjects = document.getElementById('live-broadcast-template');
    // //low
    // let Cdetail = document.getElementById('chinese');
    // for (let i = 0; i < g.chineseIfo.length; i++) {
    //     let vvolumes = subjects.content.children[0].cloneNode(true);
    //     g.bind(vvolumes, g.chineseIfo[i]);
    //     Cdetail.appendChild(vvolumes);
    // }
    // //hight
    // let Mdetail = document.getElementById('math');
    // for (let i = 0; i < g.mathIfo.length; i++) {
    //     let vvolumes = subjects.content.children[0].cloneNode(true);
    //     g.bind(vvolumes, g.mathIfo[i]);
    //     Mdetail.appendChild(vvolumes);
    // }
    // let Edetail = document.getElementById('english');
    // for (let i = 0; i < g.englishIfo.length; i++) {
    //     let vvolumes = subjects.content.children[0].cloneNode(true);
    //     g.bind(vvolumes, g.englishIfo[i]);
    //     Edetail.appendChild(vvolumes);
    // }


    let doc = document

    let getTemplate = function (templateId) {
        let result
        let template = doc.getElementById(templateId)
        if (template) {
            result = template.content.children[0]
        }
        return result
    }

    let bind = function (element, data) {
        element.innerHTML = element.innerHTML.replace('%7B', '{').replace('%7D', '}').replace(/\$\{(\w+)\}/g, function (all, variable) {
            let result = ''
            if (variable) {
                result = data[variable]
            }
            return result
        })
    }

    let proc = function (option) {
        let template = option.template
        if (!template) {
            template = getTemplate(option.templateId)
        }
        let templates
        if (option.alterTemplates) {
            templates = []
            for (let i = 0; i < option.alterTemplates.length; ++i) {
                let template = getTemplate(option.alterTemplates[i].templateId)
                if (template) {
                    templates.push({type: option.alterTemplates[i].type, template: template})
                }
            }
        }
        let container = option.container
        if (!container) {
            container = doc.getElementById(option.containerId)
        }
        if ((template || templates) && container) {
            if (Array.isArray(option.data)) {
                let secondTemplate
                if (option.secondBind) {
                    secondTemplate = getTemplate(option.secondBind.templateId)
                }
                for (let i = 0; i < option.data.length; ++i) {
                    let element
                    if (template) {
                        element = template.cloneNode(true)
                    } else if (templates) {
                        for (let j = 0; j < templates.length; ++j) {
                            if (option.data[i].type == templates[j].type) {
                                element = templates[j].template.cloneNode(true)
                                break
                            }
                        }
                    }
                    bind(element, option.data[i])
                    if (option.secondBind) {
                        let secondContainer = element.querySelector('*[data-ext-point="' + option.secondBind.extPoint + '"]')
                        proc({
                            container: secondContainer,
                            template: secondTemplate,
                            data: option.data[i][option.secondBind.dataFieldName]
                        })
                    }
                    container.appendChild(element)
                }
            } else {
                let element = template.cloneNode(true)
                bind(element, option.data)
                if (option.secondBind) {
                    let secondContainer = element.querySelector('*[data-ext-point="' + option.secondBind.extPoint + '"]')
                    proc({
                        container: secondContainer,
                        templateId: option.secondBind.templateId,
                        data: option.data[i][option.secondBind.dataFieldName]
                    })
                }
                container.appendChild(element)
            }
        }
    }

    proc({
        templateId: 'title-template',
        data: data,
        containerId: 'title'
    })

    proc({
        templateId: 'live-broadcast-template',
        data: data.chineseInfo,
        containerId: 'chinese',
        // secondBind: {
        //     templateId: 'list-template',
        //     dataFieldName: 'detail-list',
        //     extPoint: 'detailList'
        // }
    })
    proc({
        templateId: 'live-broadcast-template',
        data: data.mathInfo,
        containerId: 'math',
        // secondBind: {
        //     templateId: 'list-template',
        //     dataFieldName: 'detail-list',
        //     extPoint: 'detailList'
        // }
    })
    proc({
        templateId: 'live-broadcast-template',
        data: data.englishInfo,
        containerId: 'english',
        // secondBind: {
        //     templateId: 'list-template',
        //     dataFieldName: 'detail-list',
        //     extPoint: 'detailList'
        // }
    })
    subjectTab();//课程选项卡
    curseDetailTap(); //点击“课程详情”
})
