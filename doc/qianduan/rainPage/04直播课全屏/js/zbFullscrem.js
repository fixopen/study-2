$(function () {
    let data = {

        contents: [
            {
                name: '飞飞',
                content: ' When do you usually get up?什意思?How do you usually go to school?How do you usually go to school?'
            },
            {
                name: '王强',
                content: 'When do you have breakfast?'
            },
            {
                name: '李小刚',
                content: 'How do you usually go to school?'
            },
            {
                name: '王强',
                content: 'When do you have breakfast?'
            },
            {
                name: '飞飞',
                content: ' When do you usually get up?什意思?How do you usually go to school?How do you usually go to school?'
            },
            {
                name: '王强',
                content: 'When do you have breakfast?'
            }
        ],
        timeBar: {time: '60', countTime: '00:45:32'}
    }


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
        templateId: 'timeLength-template',
        data: data.timeBar,
        containerId: 'timeLength'
    })
    proc({
        templateId: 'discuss-template',
        data: data.contents,
        containerId: 'discuss'
    })


})
