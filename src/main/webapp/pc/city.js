$(function () {
    let china = {
        "北京": {
            "北京市": ["东城区", "西城区", "朝阳区", "丰台区", "石景山区", "海淀区", "门头沟区", "房山区", "通州区", "顺义区", "昌平区", "大兴区", "怀柔区", "平谷区", "密云县", "延庆区"],
        },
        "天津": {
            "天津市": ["和平区", "河东区", "河西区", "南开区", "河北区", "红桥区", "东丽区", "西青区", "津南区", "北辰区", "武清区", "宝坻区", "滨海新区", "宁河县", "静海县", "蓟县"],
        },
        "河北省":{
            "石家庄市":["长安区", "桥东区", "桥西区", "新华区", "井陉矿区", "裕华区", "井陉县", "正定县", "栾城县", "行唐县", "灵寿县", "高邑县", "深泽县", "赞皇县", "无极县", "平山县", "元氏县", "赵县", "辛集市", "藁城市", "晋州市", "新乐市", "鹿泉市"],
            "唐山市":["路南区", "路北区", "古冶区", "开平区", "丰南区", "丰润区", "曹妃甸区", "滦县", "滦南县", "乐亭县", "迁西县", "玉田县", "遵化市", "迁安市"],
            "秦皇岛市":["海港区", "山海关区", "北戴河区", "青龙满族自治县", "昌黎县", "抚宁县", "卢龙县"],
            "邯郸市":["邯山区", "丛台区", "复兴区", "峰峰矿区", "邯郸县", "临漳县", "成安县", "大名县", "涉县", "泽县", "肥乡县", "永年县", "邱县", "鸡泽县", "广平县", "馆陶县", "魏县", "曲周县", "武安市"],
        }
    }
    let china = {
        "北京": {
            "北京市": ["东城区", "西城区", "朝阳区", "丰台区", "石景山区", "海淀区", "门头沟区", "房山区", "通州区", "顺义区", "昌平区", "大兴区", "怀柔区", "平谷区", "密云县", "延庆区"],
        },
        "天津": {
            "天津市": ["和平区", "河东区", "河西区", "南开区", "河北区", "红桥区", "东丽区", "西青区", "津南区", "北辰区", "武清区", "宝坻区", "滨海新区", "宁河县", "静海县", "蓟县"],
        },
        "河北省":{
            "石家庄市":["长安区", "桥东区", "桥西区", "新华区", "井陉矿区", "裕华区", "井陉县", "正定县", "栾城县", "行唐县", "灵寿县", "高邑县", "深泽县", "赞皇县", "无极县", "平山县", "元氏县", "赵县", "辛集市", "藁城市", "晋州市", "新乐市", "鹿泉市"],
            "唐山市":["路南区", "路北区", "古冶区", "开平区", "丰南区", "丰润区", "曹妃甸区", "滦县", "滦南县", "乐亭县", "迁西县", "玉田县", "遵化市", "迁安市"],
            "秦皇岛市":["海港区", "山海关区", "北戴河区", "青龙满族自治县", "昌黎县", "抚宁县", "卢龙县"],
            "邯郸市":["邯山区", "丛台区", "复兴区", "峰峰矿区", "邯郸县", "临漳县", "成安县", "大名县", "涉县", "泽县", "肥乡县", "永年县", "邱县", "鸡泽县", "广平县", "馆陶县", "魏县", "曲周县", "武安市"],
        }
    }
    let state = [
        {"index": 0, "text": ""},
        {"index": 0, "text": ""},
        {"index": 0, "text": ""}
    ]

    let p = document.getElementById('province')
    let c = document.getElementById('city')
    let x = document.getElementById('county')

    let addr = document.getElementById('receipt_address')

    //<option value="...">...</option>

    x.addEventListener('change', function(e) {
        state[2].index = e.target.selectedIndex
        state[2].text = e.target.value

        addr.value = state[0].text + state[1].text + state[2].text
    })

    c.addEventListener('change', function(e) {
        state[1].index = e.target.selectedIndex
        state[1].text = e.target.value

        x.innerHTML = ''
        for (let i = 0; i < china[state[0].text][state[1].text].length; ++i) {
            let option = document.createElement('option')
            option.setAttribute('value', china[state[0].text][state[1].text][i])
            option.innerHTML = china[state[0].text][state[1].text][i]
            x.appendChild(option)
        }
        x.selectedIndex = 0
        state[2].index = 0
        state[2].text = x.value

        addr.value = state[0].text + state[1].text + state[2].text
    })

    p.addEventListener('change', function(e) {
        state[0].index = e.target.selectedIndex
        state[0].text = e.target.value

        c.innerHTML = ''
        for (let cv in china[state[0].text]) {
            let option = document.createElement('option')
            option.setAttribute('value', china[state[0].text][cv])
            option.innerHTML = china[state[0].text][cv]
            c.appendChild(option)
        }
        c.selectedIndex = 0
        state[1].index = 0
        state[1].text = c.value

        x.innerHTML = ''
        for (let i = 0; i < china[state[0].text][state[1].text].length; ++i) {
            let option = document.createElement('option')
            option.setAttribute('value', china[state[0].text][state[1].text][i])
            option.innerHTML = china[state[0].text][state[1].text][i]
            x.appendChild(option)
        }
        x.selectedIndex = 0
        state[2].index = 0
        state[2].text = x.value

        addr.value = state[0].text + state[1].text + state[2].text
    })
})
