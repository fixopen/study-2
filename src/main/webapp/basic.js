var doc = document

var getTemplate = function (templateId) {
    var result
    var template = doc.getElementById(templateId)
    if (template) {
        result = template.content.children[0]
    }
    return result
}

var bind = function (element, data) {
    if (element) {
        element.innerHTML = element.innerHTML.replace('%7B', '{').replace('%7D', '}').replace(/\$\{(\w+)\}/g, function (all, variable) {
            var result = ''
            if (data && variable && data[variable]) {
                result = data[variable]
            }
            return result
        })
    }
}

var proc = function (option) {
    //prepare template
    var template = option.template
    if (!template) {
        template = getTemplate(option.templateId)
    }
    //prepare alt templates
    var templates
    if (option.alterTemplates) {
        templates = []
        for (var i = 0; i < option.alterTemplates.length; ++i) {
            var altTemplate = getTemplate(option.alterTemplates[i].templateId)
            if (altTemplate) {
                templates.push({type: option.alterTemplates[i].type, template: altTemplate})
            }
        }
    }
    //prepare container
    var container = option.container
    if (!container) {
        container = doc.getElementById(option.containerId)
    }
    if ((template || templates) && container) {
        //clone element via template or alt templates
        var cloneElement = function (type) {
            var element
            if (template) {
                element = template.cloneNode(true)
            } else if (templates) {
                for (var j = 0; j < templates.length; ++j) {
                    if (type == templates[j].type) {
                        element = templates[j].template.cloneNode(true)
                        break
                    }
                }
            }
            return element
        }
        //proc second bind
        var procSecond = function (element, data) {
            if (option.secondBind) {
                //prepare second templates
                var secondTemplates = []
                if (Array.isArray(option.secondBind)) {
                    for (var i = 0; i < option.secondBind.length; ++i) {
                        var secondTemplate = getTemplate(option.secondBind[i].templateId)
                        secondTemplates.push({
                            extPoint: option.secondBind[i].extPoint,
                            template: secondTemplate
                        })
                    }
                } else {
                    var secondTemplate = getTemplate(option.secondBind.templateId)
                    secondTemplates.push({extPoint: option.secondBind.extPoint, template: secondTemplate})
                }
                //get second template from second templates
                var getSecondTemplate = function (secondTemplates, extPoint) {
                    var secondTemplate
                    for (var j = 0; j < secondTemplates.length; ++j) {
                        if (secondTemplates[j].extPoint == extPoint) {
                            secondTemplate = secondTemplates[j].template
                        }
                    }
                    return secondTemplate
                }
                if (Array.isArray(option.secondBind)) {
                    for (var i = 0; i < option.secondBind.length; ++i) {
                        proc({
                            container: element.querySelector('*[data-ext-point="' + option.secondBind[i].extPoint + '"]'),
                            template: getSecondTemplate(secondTemplates, option.secondBind[i].extPoint),
                            data: data[option.secondBind[i].dataFieldName]
                        })
                    }
                } else {
                    proc({
                        container: element.querySelector('*[data-ext-point="' + option.secondBind.extPoint + '"]'),
                        template: getSecondTemplate(secondTemplates, option.secondBind.extPoint),
                        data: data[option.secondBind.dataFieldName]
                    })
                }
            }
        }
        if (Array.isArray(option.data)) { //data is array of object
            for (var i = 0; i < option.data.length; ++i) {
                var element = cloneElement(option.data[i].type)
                if (element) {
                    bind(element, option.data[i])
                    procSecond(element, option.data[i])
                    container.appendChild(element)
                }
            }
        } else { //data is object
            var element = cloneElement()
            if (element) {
                bind(element, option.data)
                procSecond(element, option.data)
                container.appendChild(element)
            }
        }
    }
}

var login = function(loginName, password) {
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
        success: function (a) {
            alert("登录成功")
        },
        error: function (e) {
            alert(JSON.stringify(e))
        }
    })
}