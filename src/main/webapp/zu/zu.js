var _$_ = function (params) {
    $('#container').load('/zu/zu.html')
    var t = {
        try: function () {
            this.objectType = params.objectType
            this.objectId = params.objectId
            this.showType = params.showType
            var biao = 0;
            $.ajax({
                type: "get",
                url: "/api/resources/" + params.objectType + "/" + params.objectId + "/sale-info",
                dataType: "json",
                success: function (info) {
                    this.cards = params.cards;
                    this.userAmount = params.user.amount;
                    this.subject = info.subjectId;
                    this.price = info.price
                    for (var i = 0; i < this.cards.length; i++) {
                        var a = this.cards[i].subjectId
                        if (this.cards[i].subjectId == this.subject && this.cards[i].amount >= this.price && this.price != 0) {
                            //第二步：你有这个科目下的卡&&卡余额>资源价格:弹出1床
                            var subject_card = t.transformation(a);
                            var object_Type = t.transformationType(params.showType);
                            document.getElementById("p11").innerHTML = "" + object_Type + "需要" + this.price + "金豆";
                            document.getElementById("p21").innerHTML = "确认要扣" + subject_card + "卡" + this.price + "金豆吗？";
                            document.getElementById("a11").innerHTML = "确认";
                            document.getElementById("a21").innerHTML = "充值";
                            document.getElementById("1").style.display = "block"
                            biao = 1
                            info.cardId = this.cards[i].id
                            /* card = cards[i];
                             object_Id = objectId;
                             object_Types = objectType;*/
                            break;

                        } else if (this.cards[i].subjectId == this.subject && this.cards[i].amount >= this.price && this.price == 0) {
                            na(this.objectType, this.objectId)
                            biao = 1;
                            break;
                        }
                    }
                    if (biao != 1) {
                        for (var i = 0; i < this.cards.length; i++) {
                            var a = this.cards[i].subjectId
                            var money = parseInt(this.userAmount) + 0 + parseInt(this.cards[i].amount);
                            var mp = "false";
                            if (money > this.price || money == this.price) {
                                mp = "true";
                            }
                            if (this.cards[i].subjectId == this.subject && this.cards[i].amount < this.price && biao != 1) {
                                //第三步：你有这个科目下的卡&&卡余额<资源价格:弹出2床
                                var subject_card = t.transformation(a);
                                var object_Type = t.transformationType(params.showType);
                                document.getElementById("p12").innerHTML = "" + object_Type + "需要" + this.price + "金豆";
                                document.getElementById("p22").innerHTML = "您的" + subject_card + "卡余额不足";
                                document.getElementById("a12").innerHTML = "充值";
                                if (mp == "true") {
                                    document.getElementById("a22").innerHTML = "转入";
                                } else if (mp == "false") {
                                    document.getElementById("a22").innerHTML = "";
                                }

                                document.getElementById("2").style.display = "block"
                                biao = 1;
                                break;
                            }
                        }
                    }
                    if (biao != 1) {
                        for (var i = 0; i < this.cards.length; i++) {
                            var a = this.cards[i].subjectId
                            if (this.cards[i].subjectId != this.subject && this.cards[i].amount >= this.price && biao != 1 && this.price != 0) {
                                //第二步：你有其他科目下的卡&&卡余额>资源价格:弹出3床
                                if(a != 1 && a != 2){
                                    var subject_card = a
                                    if (a == "语文") {
                                        var object_card = "数学"
                                    } else if (a == "数学") {
                                        var object_card = "语文"
                                    }
                                }else{
                                    var subject_card = t.transformation(a);
                                    if (a == 1) {
                                        var object_card = t.transformation(2);
                                    } else if (a == 2) {
                                        var object_card = t.transformation(1);
                                    }
                                }

                                var object_Type = t.transformationType(params.showType);
                                document.getElementById("p13").innerHTML = "您没有" + object_card + "卡";
                                document.getElementById("p23").innerHTML = "确认要扣" + subject_card + "卡" + this.price + "金豆吗？";
                                document.getElementById("a13").innerHTML = "确认";
                                document.getElementById("a23").innerHTML = "充值";
                                document.getElementById("3").style.display = "block"
                                biao = 1;
                                info.cardId = this.cards[i].id
                                /* card = cards[i];
                                 object_Id = objectId;
                                 object_Types = objectType;*/
                                break;
                            } else if (this.cards[i].subjectId != this.subject && this.cards[i].amount >= this.price && biao != 1 && this.price == 0) {
                                biao = 1;
                                na(this.objectType, this.objectId)
                                break;
                            }
                        }
                    }
                    if (biao != 1) {
                        for (var i = 0; i < this.cards.length; i++) {
                            var a = this.cards[i].subjectId
                            // var iYear=parseInt(iYear);
                            var money = parseInt(this.userAmount) + 0 + parseInt(this.cards[i].amount);
                            var am = parseInt(this.cards[i].amount)
                            var mp = "false";
                            if (money > this.price || money == this.price) {
                                mp = "true";
                            }
                            if (this.cards[i].subjectId != this.subject && am < this.price && biao != 1) {
                                //第二步：你有其他科目下的卡&&卡余额<资源价格:弹出4床
                                if(a != 1 && a != 2){
                                    var subject_card = a
                                    if (a == "语文") {
                                        var object_card = "数学"
                                    } else if (a == "数学") {
                                        var object_card = "语文"
                                    }
                                }else{
                                    var subject_card = t.transformation(a);
                                    if (a == 1) {
                                        var object_card = t.transformation(2);
                                    } else if (a == 2) {
                                        var object_card = t.transformation(1);
                                    }
                                }

                                var object_Type = t.transformationType(params.showType);
                                document.getElementById("p14").innerHTML = "您没有" + object_card + "卡";
                                document.getElementById("p24").innerHTML = "您的" + subject_card + "卡余额不足";
                                document.getElementById("a14").innerHTML = "充值";
                                if (mp == "true") {
                                    document.getElementById("a24").innerHTML = "转入";
                                } else if (mp == "false") {
                                    document.getElementById("a24").innerHTML = "";
                                }
                                document.getElementById("4").style.display = "block"
                                biao = 1;
                                break;
                            }
                        }
                    }
                    function eventRegister() {
                        var purchase1 = document.getElementById("a11")
                        purchase1.addEventListener('click', function (e) {
                            t.atom(info.cardId, "card", params.objectId, params.objectType, 1, info.price, 1);
                        })
                        purchase1.dataset.bind = true

                        /*var submitTransfer = document.getElementById("submitTransfer")
                         submitTransfer.addEventListener('click', function (e) {
                         t.atom(t.user.id, "user", $("#cardid").val(), "card", null, $("#convertedNumber").val())
                         })*/

                        var purchase2 = document.getElementById("a13")
                        purchase2.addEventListener('click', function (e) {
                            t.atom(info.cardId, "card", params.objectId, params.objectType, 1, info.price, 3);
                        })

                        var recharge1 = document.getElementById("a21")
                        recharge1.addEventListener('click', function (e) {
                            document.getElementById("1").style.display = "none"
                            t.rechargePage(t.cards, "c",1)
                        })


                        var recharge2 = document.getElementById("a12")
                        recharge2.addEventListener('click', function (e) {
                            document.getElementById("2").style.display = "none"
                            t.rechargePage(t.cards, "c",2)
                        })

                        var recharge3 = document.getElementById("a23")
                        recharge3.addEventListener('click', function (e) {
                            document.getElementById("3").style.display = "none"
                            t.rechargePage(t.cards, "c",3)
                        })

                        var recharge4 = document.getElementById("a14")
                        recharge4.addEventListener('click', function (e) {
                            document.getElementById("4").style.display = "none"
                            t.rechargePage(t.cards, 'c',4)
                        })

                        var turnOut1 = document.getElementById("a22")
                        turnOut1.addEventListener('click', function (e) {
                            document.getElementById("2").style.display = "none"
                            t.rechargePage(t.cards, 'z',2)
                        })

                        var turnOut2 = document.getElementById("a24")
                        turnOut2.addEventListener('click', function (e) {
                            document.getElementById("4").style.display = "none"
                            t.rechargePage(t.cards, 'z',4)
                        })

                        var close1 = document.getElementById("close1")
                        close1.addEventListener('click', function (e) {
                            document.getElementById("1").style.display = "none"
                        })

                        var close2 = document.getElementById("close2")
                        close2.addEventListener('click', function (e) {
                            document.getElementById("2").style.display = "none"
                        })

                        var close3 = document.getElementById("close3")
                        close3.addEventListener('click', function (e) {
                            document.getElementById("3").style.display = "none"
                        })

                        var close4 = document.getElementById("close4")
                        close4.addEventListener('click', function (e) {
                            document.getElementById("4").style.display = "none"
                        })

                    }

                    var purchase1 = document.getElementById("a11")
                    if (!purchase1.dataset.bind) {
                        eventRegister()
                    }
                }
            })

        },
        getContent: function (action) {
            params.action = action
            $.ajax({
                type: "get",
                url: "/api/resources/" + params.objectType + "/" + params.objectId,
                dataType: "json",
                success: function (content) {
                    action(content)
                    // t.count = content
                    //  alert(JSON.stringify(content))
                },
                error: function (e) {
                    t.try()
                }
            })
        },
        transformation: function (sex) {
            var result;
            if (sex == 1) {
                result = "语文"
            }
            if (sex == 2) {
                result = "数学"
            }
            return result;
        },
        transformationType: function (objectType) {
            var result;
            if (objectType == "knowledge-point") {
                result = "知识点"
            }
            if (objectType == "liveVideo") {
                result = "直播"
            }
            if (objectType == "video") {
                result = "回放"
            }
            return result;
        },
        nones: function (none) {
            if (none == 1) {
                document.getElementById("1").style.display = "none"
            }
            if (none == 2) {
                document.getElementById("2").style.display = "none"
            }
            if (none == 3) {
                document.getElementById("3").style.display = "none"
            }
            if (none == 4) {
                document.getElementById("4").style.display = "none"
            }
            if (none == 5) {
                document.getElementById("5").style.display = "none"
            }
        },
        getSubjectNameById: function (subjects) {
            var result = ''
            if (subjects == 1) {
                result = "语文"
            }
            if (subjects == 2) {
                result = "数学"
            }
            if (subjects == 3) {
                result = "英语"
            }
            return result
        },
        rechargePage: function (cards, T,non) {
            this.user = params.user
            $("#tozu").val(T)
            for (var i = 0; i < cards.length; i++) {
                if (cards[i].subjectId == 1 || cards[i].subjectId == "语文") {
                    cards[i].image = "keBiao.png"
                }
                if (cards[i].subjectId == 2 || cards[i].subjectId == "数学") {
                    cards[i].image = "mathBiao.png"
                }
                if (cards[i].subjectId == 1 || cards[i].subjectId == 2) {
                    cards[i].subjectId = t.getSubjectNameById(cards[i].subjectId)
                }
                if (cards[i].amount == 0) {
                    cards[i].amount = "0"
                }
            }
            if (T == "c") {
                document.getElementById("oneCardzu").innerHTML = ""
                document.getElementById("accountzu").innerHTML = ""
                var ua = this.user.amount
                if (ua == 0) {
                    ua = "0"
                }
                var uc = {
                    id: this.user.id,
                    amount: ua,
                    name: "账户",
                    image: "yueBiao.png",
                }
                proc({
                    containerId: 'accountzu',
                    data: uc,
                    templateId: 'userCardzu-template'
                })
                proc({
                    containerId: 'oneCardzu',
                    data: cards,
                    templateId: 'selectCardzu-template'
                })
                document.getElementById("rechargezu").style.display = "block"
                document.getElementById("tozu").style.display = "block"
                document.getElementById("rechargepagezu").style.display = "none"

            } else if (T == "z") {
                document.getElementById("rechargezu").style.display="none"
                document.getElementById("rechargepagezu").style.display = "none"
                document.getElementById("tozu").style.display = "none"
                document.getElementById("convertedzu").innerHTML = ""
                proc({
                    containerId: 'convertedzu',
                    data: cards,
                    templateId: 'selectCardzu-template'
                })
                document.getElementById("transferzu").style.display = "block"
            }
            $('.zu').on('click', function (e) {
                // document.getElementById("rechargepagezu").style.display = "block"
                var element = e.currentTarget
                var id = element.dataset.id;
                var amount = element.dataset.amount;
                var no = element.dataset.no;
                var subject = element.dataset.subject;
                var name = element.dataset.name;
                var img = element.dataset.img;
                t.switchRechargezu(id, amount, no, subject, name, img, $("#tozu").val())
            })
            var z = document.getElementById("zzu")
            if (!z.dataset.bind) {
                eventRegisters()
            }
            var submitTransferzu = document.getElementById("submitTransferzu")
            if (!submitTransferzu.dataset.bind) {
                eventRegisterss()
            }
            function eventRegisters() {
                var z = document.getElementById("zzu")
                z.dataset.bind = true
                z.addEventListener('click', function (e) {
                    t.rechargePage(params.cards,'c')
                })
            }
            function eventRegisterss() {
                var submitTransferzu = document.getElementById("submitTransferzu")
                submitTransferzu.dataset.bind = true
                submitTransferzu.addEventListener('click', function (e) {
                   var a = document.getElementById("convertedNumberzu").value;
                    if(a != null && a != "" && checkRate("convertedNumberzu") != false){
                        t.atom(params.id, "user", $("#cardidzu").val(), "card", null, $("#convertedNumberzu").val(),non,function (e) {
                            alert("成功"+JSON.stringify(e))
                        })
                    }
                })
            }
            function checkRate(input) {
                var nubmer = parseInt(document.getElementById(input).value);
                if (isNaN(nubmer) || nubmer <= 0 || !(/^\d+$/.test(nubmer))) {
                    {
                        alert("请输入正确的数值,只允许输入整数!");
                        document.getElementById(input).value = "";
                        return false;
                    }
                }
            }
        },
        switchRecharge: function (id, amount, no, subject, name, img, T) {
            if (T == "z") {
                document.getElementById("converted").innerHTML = ""
                document.getElementById("za").innerHTML = amount
                document.getElementById("tishi").innerHTML = t.user.amount
                document.getElementById("zn").innerHTML = no
                document.getElementById("zs").innerHTML = subject
                $("#cardid").val(id)
                document.getElementById("image").src = img
                document.getElementById("selectOneCard").style.display = "block"
            } else if (T == "c") {
                if (id == params.user.id) {
                    document.getElementById("name").innerHTML = name
                    document.getElementById("no").innerHTML = ""
                    document.getElementById("money").innerHTML = amount
                    document.getElementById("src").src = img
                    src
                } else {
                    document.getElementById("name").innerHTML = subject
                    document.getElementById("no").innerHTML = no
                    document.getElementById("money").innerHTML = amount
                    document.getElementById("src").src = img
                }
                document.getElementById("tozu").style.display = "none"
                document.getElementById("rechargepagezu").style.display = "block"

                document.getElementById("to").style.display = "none"
                document.getElementById("rechargepage").style.display = "block"
            }
        },
        switchRechargezu: function (id, amount, no, subject, name, img, T) {
            if (T == "z") {
                document.getElementById("convertedzu").innerHTML = ""
                document.getElementById("zazu").innerHTML = amount
                document.getElementById("tishizu").innerHTML = t.user.amount
                document.getElementById("znzu").innerHTML = no
                document.getElementById("zszu").innerHTML = subject
                $("#cardidzu").val(id)
                document.getElementById("imagezu").src = img
                document.getElementById("selectOneCardzu").style.display = "block"
            } else if (T == "c") {
                if (id == params.user.id) {
                    document.getElementById("namezu").innerHTML = name
                    document.getElementById("nozu").innerHTML = ""
                    document.getElementById("moneyzu").innerHTML = amount
                    document.getElementById("srczu").src = img
                    src
                } else {
                    document.getElementById("namezu").innerHTML = subject
                    document.getElementById("nozu").innerHTML = no
                    document.getElementById("moneyzu").innerHTML = amount
                    document.getElementById("srczu").src = img
                }
                document.getElementById("tozu").style.display = "none"
                document.getElementById("rechargepagezu").style.display = "block"
            }
        },
        atom: function (sourceId, sourceType, object_Id, object_Type, count, money, none,ac) {
            var result;
            var filter = {}
            if (money == null && count == null) {
                /*filter = { // 充值rechargeFilter
                    sourceType: sourceType,
                    objectId: objectId,
                    objectType: objectType,
                    money: money
                }
                $.ajax({
                    type: "post",
                    url: "/api/transactions",
                    dataType: "json",
                    data: JSON.stringify(filter),
                    contentType: "application/json; charset=utf-8",
                    success: function (e) {
                        alert("充值成功")
                        t.nones(none)
//                    t.getContent();
//                    t.na(object_Type, object_Id)
                    },
                    error: function (e) {
                        result = e;
                        // alert(JSON.stringify(e))
                        switch (JSON.stringify(e.status)) {
                            case "408":
                                alert("余额不足")
                                nones(none)
                                break;
                            case "200":
                                alert("购买成功")
                                nones(none)
                                break;
                        }
                    }
                })*/
            } else if (count == null) {
                filter = { //转账transferFilter
                    sourceId: sourceId,
                    sourceType: sourceType,
                    objectId: object_Id,
                    objectType: object_Type,
                    money: money
                }
                $.ajax({
                    type: "post",
                    url: "/api/transactions",
                    dataType: "json",
                    data: JSON.stringify(filter),
                    contentType: "application/json; charset=utf-8",
                    success: function (e) {
                        t.nones(none)
                        easd()
                        ac(e)
                    },
                    error: function (e) {
                        result = e;
                        switch (JSON.stringify(e.status)) {
                            case "408":
                                alert("余额不足")
                                t.nones(none)
                                break;
                            case "200":
                                alert("转账成功")
                                t.nones(none)
                                break;
                        }
                    }
                })
            } else if (money != null && count != null) {
                filter = { // 购买purchaseFilter
                    sourceId: sourceId,
                    sourceType: sourceType,
                    objectId: object_Id,
                    objectType: object_Type,
                    count: count,
                    money: money
                }
                $.ajax({
                    type: "post",
                    url: "/api/transactions",
                    dataType: "json",
                    data: JSON.stringify(filter),
                    contentType: "application/json; charset=utf-8",
                    success: function (e) {
                        alert("购买成功")
                        t.nones(none)
                        easd()
                        t.getContent(params.action);
                    },
                    error: function (e) {
                        result = e;
                        // alert(JSON.stringify(e))
                        switch (JSON.stringify(e.status)) {
                            case "408":
                                alert("余额不足")
                                t.nones(none)
                                break;
                            case "200":
                                alert("购买成功")
                                t.nones(none)
                                break;
                        }
                    }
                })
            }
            function easd() {
                document.getElementById("rechargezu").style.display="none"
                document.getElementById("tozu").style.display="none"
                document.getElementById("transferzu").style.display="none"
                document.getElementById("selectOneCardzu").style.display="none"
                document.getElementById("rechargepagezu").style.display="none"
            }
            return result;
        },
    }
    for (var property in params) {
        t[property] = params[property]
    }
    return t
}
