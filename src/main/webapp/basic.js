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
            if (data && variable && (data[variable] != undefined)) {
                result = data[variable]
            }
            return result
        })
    }
}

var proc = function (option) {
    var clone = null

    var template = option.template || getTemplate(option.templateId)
    if (template) {
        clone = function() {
            return template.cloneNode(true)
        }
    }

    if (clone == null) {
        //
    }
    var prepareAltTemplates = function(option) {
        //prepare alt templates
        var templates
        if (option.alterTemplates) {
            templates = []
            for (var i = 0; i < option.alterTemplates.length; ++i) {
                templates.push({type: option.alterTemplates[i].type, template: option.alterTemplates[i].template || getTemplate(option.alterTemplates[i].templateId)})
            }
        }
        return templates
    }

    var templates = prepareAltTemplates(option)

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
                var process = function(element, data, option) {
                    if (option.alterTemplates) {
                        proc({
                            container: element.querySelector('*[data-ext-point="' + option.secondBind.extPoint + '"]'),
                            alterTemplates: option.secondBind.alterTemplates,
                            data: data[option.secondBind.dataFieldName]
                        })
                    } else {
                        proc({
                            container: element.querySelector('*[data-ext-point="' + option.secondBind.extPoint + '"]'),
                            templateId: option.secondBind.templateId,
                            data: data[option.secondBind.dataFieldName]
                        })
                    }
                }
                if (Array.isArray(option.secondBind)) {
                    for (var i = 0; i < option.secondBind.length; ++i) {
                        var o = {}
                        for (var p in option.secondBind[i]) {
                            o[p] = option.secondBind[i][p]
                        }
                        process(element, data, o)
                    }
                } else {
                    process(element, data, option)
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