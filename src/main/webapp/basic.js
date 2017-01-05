var doc = document

/*
proc syntax change:
proc({
    container|containerId: element|elementId,
    data: dataObject|dataArray,
    template|templateId: template|templateId,
    templates: [
        {type: 'selector', template|templateId: template|templateId},
        {type: 'selector', template|templateId: template|templateId},
        {type: 'selector', template|templateId: template|templateId}
    ],
    next: {
        template|templateId: template|templateId,
        templates: [
            {type: 'selector', template|templateId: template|templateId},
            {type: 'selector', template|templateId: template|templateId},
            {type: 'selector', template|templateId: template|templateId}
        ],
    },
    nexts: [
        {
            dataSelector: 'dataSelector',
            template|templateId: template|templateId,
            templates: [
                {type: 'selector', template|templateId: template|templateId},
                {type: 'selector', template|templateId: template|templateId},
                {type: 'selector', template|templateId: template|templateId}
            ],
        }
    ]
})

其中template|templateId和templates在同一级别只能出现一个。templates就是原来的altTemplates。
对于data是单个对象的，不能选用templates，只能是template或者templateId。
next就是原来的secondBind，它是单个对象。去掉了里面extPoint和dataFieldName——它们将由模板中的data-ext-point属性和数据中对应的字段推导出。
next是由template|templateId或者templates组成的。
next可以嵌套，当扩展点里面还有扩展点时，就用嵌套。嵌套意味着数据也是层层累积起来的。
nexts是next的数组，当有多个扩展点时，用nexts处理。next和nexts二者最多有一个。
nexts（多个扩展点）时，加入dataSelector用于指示该扩展适应于那个数据。
*/

var getTemplate = function (templateId) {
    var result
    var template = doc.getElementById(templateId)
    if (template) {
        result = template.content.children[0]
    }
    return result
}

var bind = function (element, data, next) {
    if (element) {
        element.innerHTML = element.innerHTML.replace('%7B', '{').replace('%7D', '}').replace(/\$\{(\w+)\}/g, function (all, variable) {
            var result = ''
            if (typeof data == 'string' && variable == '') {
                result = data
            }
            if (data && variable && (data[variable] != undefined)) {
                result = data[variable]
            }
            return result
        })
        var cs = element.querySelectorAll('[data-ext-point]')
        for (var i = 0; i < cs.length; ++i) {
            var c = cs[i]
            if (c && next) {
                var opt = {
                    container: c,
                    data: data[c.dataset.extPoint],
                    next: next.next || next.nexts
                }
                if (Array.isArray(next)) {
                    for (var i = 0; i < next.length; ++i) {
                        if (next[i].dataSelector == c.dataset.extPoint) {
                            opt.template = next[i].template || getTemplate(next[i].templateId)
                            if (!opt.template) {
                                opt.templates = next[i].templates
                            }
                            break
                        }
                    }
                } else {
                    opt.template = next.template || getTemplate(next.templateId)
                    if (!opt.template) {
                        opt.templates = next.templates
                    }
                }
                proc(opt)
            }
        }
    }
}

var proc = function (option) {
    var clone = null

    var template = option.template || getTemplate(option.templateId)
    if (template) {
        clone = function () {
            return template.cloneNode(true)
        }
    }

    if (clone == null) {
        if (option.templates) {
            clone = function (type) {
                for (var i = 0; i < option.templates.length; ++i) {
                    if (option.templates[i]['type'] == type) {
                        var template = option.templates[i].template || getTemplate(option.templates[i].templateId)
                        if (template) {
                            return template.cloneNode(true)
                        }
                    }
                }
            }
        }
    }

    //prepare container
    var container = option.container || doc.getElementById(option.containerId)

    if (clone && container) {
        var bindAndAppend = function (container, element, data, next) {
            bind(element, data, next)
            container.appendChild(element)
        }

        if (Array.isArray(option.data)) { //data is array of object
            for (var i = 0; i < option.data.length; ++i) {
                var element = clone(option.data[i]['type'])
                if (element) {
                    bindAndAppend(container, element, option.data[i], option.next || option.nexts)
                }
            }
        } else { //data is object
            var element = clone()
            if (element) {
                bindAndAppend(container, element, option.data, option.next || option.nexts)
            }
        }
    }
}

var login = function (loginName, password) {
    $.ajax({
        type: "PUT",
        url: "/api/sessions",
        data: JSON.stringify({
            type: "name",
            info: loginName,
            key: password,
            deviceType: 'web',
            deviceNo: ''
        }),
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        success: function (d) {
            alert("登录成功")
        },
        error: function (xhr) {
            alert(xhr.status + ' ' + xhr.statusText)
            //alert(JSON.stringify(e))
        }
    })
}

var transaction = function (params) {
    if (this == window) {
        return new _$_(params)
    }

    for (var property in params) {
        this[property] = params[property]
    }

    if (!this.count) {
        this.count = 0
    }

    var getCardById = function(self, id) {
        var result = null
        for (var i = 0; i < self.cards.length; ++i) {
            if (self.cards[i].id == id) {
                result = self.cards[i]
                break
            }
        }
        return result
    }

    var getCardsBySubjectId = function(self, subjectId) {
        var result = []
        for (var i = 0; i < self.cards.length; ++i) {
            if (self.cards[i].subjectId == subjectId) {
                result.push(self.cards[i])
            }
        }
        return result
    }

    var getOtherCardsBySubjectId = function(self, subjectId) {
        var result = []
        for (var i = 0; i < self.cards.length; ++i) {
            if (self.cards[i].subjectId != subjectId) {
                result.push(self.cards[i])
            }
        }
        return result
    }

    this.try = function () {
        // this.objectType = params.objectType
        // this.objectId = params.objectId
        var self = this
        $.ajax({
            type: "get",
            url: "/api/resources/" + self.objectType + "/" + self.objectId + "/sale-info",
            dataType: "json",
            success: function (info) {
                this.money = info.price
                this.subjectId = info.subjectId
                var isOk = false
                var correctCards = getCardsBySubjectId(self, self.subjectId)
                for (var i = 0; i < correctCards.length; ++i) {
                    if (correctCards[i].amount >= self.money) {
                        isOk = true
                        self.card = correctCards[i]
                        break
                    }
                }
                if (isOk) {
                    var dialog = document.getElementById('transactionDialog')
                    proc({
                        container: dialog.querySelector('#transactionDialogContent'),
                        data: {
                            price: info.price,
                            objectType: info.objectType,
                            subjectName: getSubjectNameById(info.subjectId)
                        },
                        templateId: 'transactionDialog-template'
                    })
                    dialog.querySelector('#resourceTip').style.display = 'block'
                    dialog.querySelector('#confirm').style.display = 'block'
                    dialog.querySelector('#nextTip').style.display = 'block'
                    dialog.querySelector('.kou_dou').style.display = 'block'
                    dialog.querySelector('#okButton').addEventListener('click', function(e) {
                        self.purchase()
                        dialog.style.display = 'none'
                    }, false)
                    dialog.style.display = 'block'
                } else {
                    var incorrectCards = getOtherCardsBySubjectId(self, self.subjectId)
                    for (var i = 0; i < incorrectCards.length; ++i) {
                        if (incorrectCards[i].amount >= self.money) {
                            isOk = true
                            self.card = incorrectCards[i]
                            break
                        }
                    }
                    if (isOk) {
                        var dialog = document.getElementById('transactionDialog')
                        proc({
                            container: dialog.querySelector('#transactionDialogContent'),
                            data: {
                                price: info.price,
                                objectType: info.objectType,
                                subjectName: getSubjectNameById(self.card.subjectId)
                            },
                            templateId: 'transactionDialog-template'
                        })
                        dialog.querySelector('#resourceTip').style.display = 'block'
                        dialog.querySelector('#confirm').style.display = 'block'
                        dialog.querySelector('#nextTip').style.display = 'block'
                        dialog.querySelector('.kou_dou').style.display = 'block'
                        dialog.querySelector('#okButton').addEventListener('click', function(e) {
                            self.purchase()
                            dialog.style.display = 'none'
                        }, false)
                        dialog.style.display = 'block'
                    } else {
                        if (self.user.amount + self.card.amount > self.money) {
                            var dialog = document.getElementById('transactionDialog')
                            proc({
                                container: dialog.querySelector('#transactionDialogContent'),
                                data: {
                                    price: info.price,
                                    objectType: info.objectType,
                                    subjectName: getSubjectNameById(info.subjectId)
                                },
                                templateId: 'transactionDialog-template'
                            })
                            dialog.querySelector('#resourceTip').style.display = 'block'
                            dialog.querySelector('#balanceTip').style.display = 'block'
                            dialog.querySelector('#rechargeButton').style.display = 'block'
                            dialog.querySelector('#rechargeButton').addEventListener('click', function(e) {
                                self.recharge()
                                dialog.style.display = 'none'
                            }, false)
                            dialog.querySelector('#transferButton').style.display = 'block'
                            dialog.querySelector('#transferButton').addEventListener('click', function(e) {
                                self.transfer()
                                dialog.style.display = 'none'
                            }, false)
                            dialog.style.display = 'block'
                        } else {
                            //you must recharge
                        }
                    }
                }
            }
        })
    }

    this.purchase = function () {
        if (this.count == 0) {
            this.count = 1
        }
        params.money = this.money
        params.sourceType = 'card' // | 'user'
        params.sourceId = this.card.id // | this.user.id
        params.count = this.count
        params.objectType = this.objectType
        params.objectId = this.objectId
        var self = this
        $.ajax({
            type: "post",
            url: "/api/transactions",
            dataType: "json",
            data: JSON.stringify(params),
            contentType: "application/json; charset=utf-8",
            success: function (t) {
                self.card.amount -= t.money
            },
            error: function(xhr) {
                //
            }
        })
    }

    this.getContent = function (postProcessor) {
        // params.objectType = this.objectType
        // params.objectId = this.objectId
        var self = this
        $.ajax({
            type: "get",
            url: "/api/resources/" + this.objectType + "/" + this.objectId,
            dataType: "json",
            success: function (content) {
                if (postProcessor) {
                    postProcessor(content)
                }
            },
            error: function (xhr) {
                params.postProcessor = postProcessor
                self.try()
            }
        })
    }

    this.recharge = function (money) {
        state.switchTo('recharge')
        //select money
        if (money) {
            this.money = money
        }
        params.money = this.money
        //popup dialog for select sink
        var choice = document.getElementById('to')
        proc({
            container: choice.querySelector('#account'),
            data: this.user,
            templateId: 'user-template'
        })
        proc({
            container: choice.querySelector('#cards'),
            data: this.cards,
            templateId: 'card-template'
        })
        choice.style.display = 'block'
        //dialog event process
        params.objectType = 'card' // | 'user
        params.objectId = this.card.id // | this.user.id
        var self = this
        $.ajax({
            type: "post",
            url: "/api/transactions",
            dataType: "json",
            data: JSON.stringify(params),
            contentType: "application/json; charset=utf-8",
            success: function (t) {
                self.card.amount += t.money
                //self.user.amount += t.money
            }
        })
    }

    this.transfer = function (money) {
        //select money
        if (money) {
            this.money = money
        }
        //popup dialog for select source | sink
        var choice = document.getElementById('to')
        proc({
            container: choice.querySelector('#account'),
            data: this.user,
            templateId: 'user-template'
        })
        proc({
            container: choice.querySelector('#cards'),
            data: this.cards,
            templateId: 'card-template'
        })
        choice.style.display = 'block'
        //dialog event process
        params.sourceType = 'user' // | 'card'
        params.sourceId = this.user.id // | this.card.id
        params.money = this.money
        params.objectType = 'card' // | 'user
        params.objectId = this.card.id // | this.user.id
        var self = this
        $.ajax({
            type: "post",
            url: "/api/transactions",
            dataType: "json",
            data: JSON.stringify(params),
            contentType: "application/json; charset=utf-8",
            success: function (t) {
                self.card.amount += t.money
                self.user.amount -= t.money
            }
        })
    }

    var self = this
    if (!this.user) {
        $.ajax({
            type: 'get',
            url: '/api/users/self',
            success: function (u) {
                self.user = u
                $.ajax({
                    type: 'get',
                    url: '/api/users/self/cards',
                    success: function (cs) {
                        self.cards = cs
                    }
                })
            }
        })
    } else if (!this.cards) {
        $.ajax({
            type: 'get',
            url: '/api/users/self/cards',
            success: function (cs) {
                self.cards = cs
            }
        })
    }
}
