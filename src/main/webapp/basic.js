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
        if (option.alterTemplates) {
            var templates = []
            for (var i = 0; i < option.alterTemplates.length; ++i) {
                templates.push({type: option.alterTemplates[i].type, template: option.alterTemplates[i].template || getTemplate(option.alterTemplates[i].templateId)})
            }
            clone = function(type) {
                for (var j = 0; j < templates.length; ++j) {
                    if (type == templates[j].type) {
                        element = templates[j].template.cloneNode(true)
                        break
                    }
                }
            }
        }
    }

    //prepare container
    var container = option.container || doc.getElementById(option.containerId)

    if (clone && container) {
        var procSecond = function (element, data, config) {
            if (config) {
                if (Array.isArray(config)) {
                    for (var i = 0; i < config.length; ++i) {
                        procSecond(element, data, config[i])
                    }
                } else {
                    var opt = {
                        container: element.querySelector('*[data-ext-point="' + config.extPoint + '"]'),
                        data: data[config.dataFieldName],
                        template: config.template || getTemplate(config.templateId)
                    }
                    if (!opt.template) {
                        opt.alterTemplates = config.alterTemplates
                    }
                    proc(opt)
                }
            }
        }

        if (Array.isArray(option.data)) { //data is array of object
            for (var i = 0; i < option.data.length; ++i) {
                var element = clone(option.data[i].type)
                if (element) {
                    bind(element, option.data[i])
                    procSecond(element, option.data[i])
                    container.appendChild(element)
                }
            }
        } else { //data is object
            var element = clone()
            if (element) {
                bind(element, option.data)
                procSecond(element, option.data, option.secondBind)
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