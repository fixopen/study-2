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
                list: [
                    {
                        detailList: '一、汉语拼音',
                        listList: '1.读准汉语拼音的声母、韵母、声调认读的音节。',
                        listList: '2.能默写声母、韵母并抄写音节，上写得正确整。',
                    },
                    {
                        detailList: '二、识字、写字',
                        listList: '1.掌握汉字的基本笔画、笔顺规则、间架结构和常用的偏旁部首。',
                        listList: '2.能借助汉语拼音解字义。'
                    }
                ],
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
                list: [
                    {
                        detailList: '一、汉语拼音',
                        listList: '1.读准汉语拼音的声母、韵母、声调认读的音节。',
                        listList: '2.能默写声母、韵母并抄写音节，上写得正确整。'
                    },
                    {
                        detailList: '二、识字、写字',
                        listList: '1.掌握汉字的基本笔画、笔顺规则、间架结构和常用的偏旁部首。',
                        listList: '2.能借助汉语拼音解字义。'
                    }
                ],
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
                list: [
                    {
                        detailList: '一、汉语拼音',
                        listList: '1.读准汉语拼音的声母、韵母、声调认读的音节。',
                        listList: '2.能默写声母、韵母并抄写音节，上写得正确整。',
                    },
                    {
                        detailList: '二、识字、写字',
                        listList: '1.掌握汉字的基本笔画、笔顺规则、间架结构和常用的偏旁部首。',
                        listList: '2.能借助汉语拼音解字义。'
                    }
                ],
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
                list: [
                    {
                        detailList: '一、汉语拼音',
                        listList: '1.读准汉语拼音的声母、韵母、声调认读的音节。',
                        listList: '2.能默写声母、韵母并抄写音节，上写得正确整。'
                    },
                    {
                        detailList: '二、识字、写字',
                        listList: '1.掌握汉字的基本笔画、笔顺规则、间架结构和常用的偏旁部首。',
                        listList: '2.能借助汉语拼音解字义。'
                    }
                ],
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
                list: [
                    {
                        detailList: '一、汉语拼音',
                        listList: '1.读准汉语拼音的声母、韵母、声调认读的音节。',
                        listList: '2.能默写声母、韵母并抄写音节，上写得正确整。',
                    },
                    {
                        detailList: '二、识字、写字',
                        listList: '1.掌握汉字的基本笔画、笔顺规则、间架结构和常用的偏旁部首。',
                        listList: '2.能借助汉语拼音解字义。'
                    }
                ],
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
                list: [
                    {
                        listTitle: '一、汉语拼音',
                        listList: '1.读准汉语拼音的声母、韵母、声调认读的音节。',
                        listList: '2.能默写声母、韵母并抄写音节，上写得正确整。'
                    },
                    {
                        listTitle: '二、识字、写字',
                        listList: '1.掌握汉字的基本笔画、笔顺规则、间架结构和常用的偏旁部首。',
                        listList: '2.能借助汉语拼音解字义。'
                    }
                ],
                teacherInfo: '董 霞，女，本科学历，小学语文高级教师，现任文学部教学主管，文学写作项目、童话阅读项目专业指导教师。《母婴杂志》绘本栏目特邀专家顾问。曾获杭州市教坛新秀、杭州市小学语文学科带头人、杭州市优秀班主任等荣誉；多次在杭州市语文教学观摩活动中获奖。出版《小学生成语故事》、《青少年宫文学写作专用教材》二、三册。撰写的论文多次在杭州市校外教育论文评选活动中获奖。',
                onlineTime: '2016-03-11    10:22:33',
                courseDuration: '30分钟'
            }
        ]
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
        secondBind: {
            templateId: 'list-template',
            dataFieldName: 'detail-list',
            extPoint: 'detailList'
        }
    })
    proc({
        templateId: 'live-broadcast-template',
        data: data.mathInfo,
        containerId: 'math',
        secondBind: {
            templateId: 'list-template',
            dataFieldName: 'detail-list',
            extPoint: 'detailList'
        }
    })
    proc({
        templateId: 'live-broadcast-template',
        data: data.englishInfo,
        containerId: 'english',
        procSecond: {
            templateId: 'list-template',
            dataFieldName: 'list',
            extPoint: 'detailList'
        }
    })

    subjectTab();//课程选项卡
    curseDetailTap(); //点击“课程详情”

})
