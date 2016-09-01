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
            let secondTemplates = []
            if (option.secondBind) {
                if (Array.isArray(option.secondBind)) {
                    for (let i = 0; i < option.secondBind.length; ++i) {
                        let secondTemplate = getTemplate(option.secondBind[i].templateId)
                        secondTemplates.push({
                            extPoint: option.secondBind[i].extPoint,
                            template: secondTemplate
                        })
                    }
                } else {
                    let secondTemplate = getTemplate(option.secondBind.templateId)
                    secondTemplates.push({extPoint: option.secondBind.extPoint, template: secondTemplate})
                }
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
                    let secondContainer
                    let secondIndex
                    if (Array.isArray(option.secondBind)) {
                        for (let j = 0; j < option.secondBind.length; ++j) {
                            if (option.secondBind[j].extPoint == option.secondBind.extPoint) {
                                secondIndex = j
                                secondContainer = element.querySelector('*[data-ext-point="' + option.secondBind[j].extPoint + '"]')
                                break
                            }
                        }
                    } else {
                        secondContainer = element.querySelector('*[data-ext-point="' + option.secondBind.extPoint + '"]')
                    }
                    let secondTemplate
                    for (let j = 0; j < secondTemplates.length; ++j) {
                        if (secondTemplates[j].extPoint == option.secondBind.extPoint) {
                            secondTemplate = secondTemplates[j].template
                        }
                    }
                    let secondData = option.data[i][option.secondBind.dataFieldName]
                    if (Array.isArray(option.secondBind)) {
                        secondData = option.data[i][option.secondBind[secondIndex].dataFieldName]
                    }
                    if (secondContainer && secondTemplate) {
                        proc({
                            container: secondContainer,
                            template: secondTemplate,
                            data: secondData
                        })
                    }
                }
                container.appendChild(element)
            }
        } else {
            let element = template.cloneNode(true)
            bind(element, option.data)
            if (option.secondBind) {
                let secondContainer
                let secondIndex
                if (Array.isArray(option.secondBind)) {
                    for (let j = 0; j < option.secondBind.length; ++j) {
                        if (option.secondBind[j].extPoint == option.secondBind.extPoint) {
                            secondIndex = j
                            secondContainer = element.querySelector('*[data-ext-point="' + option.secondBind[j].extPoint + '"]')
                            break
                        }
                    }
                } else {
                    secondContainer = element.querySelector('*[data-ext-point="' + option.secondBind.extPoint + '"]')
                }
                let secondTemplate
                for (let j = 0; j < secondTemplates.length; ++j) {
                    if (secondTemplates[j].extPoint == option.secondBind.extPoint) {
                        secondTemplate = secondTemplates[j].template
                    }
                }
                let secondData = option.data[option.secondBind.dataFieldName]
                if (Array.isArray(option.secondBind)) {
                    secondData = option.data[option.secondBind[secondIndex].dataFieldName]
                }
                if (secondContainer && secondTemplate) {
                    proc({
                        container: secondContainer,
                        template: secondTemplate,
                        data: secondData
                    })
                }
            }
            container.appendChild(element)
        }
    }
}
