function xiao(objectId, objectType) {
    $.ajax({
        type: "get",
        url: "/api/resources/" + objectType + "/" + objectId + "/sale-info",
        dataType: "json",
        success: function (e) {
            sale_info = e;
            cards = e.cards;
            userAmount = e.user_amount;
            price = e.price;
            ke = e.subject;
            tan(cards, ke, objectType,objectId)
            //第二步根据自己的卡和价格资源弹窗
            function tan(cards, subject, objectType,objectId) {
                for (var i = 0; i < cards.length; i++) {
                    var a = cards[i].subjectId
                    if (cards[i].subjectId == subject && cards[i].amount >= price) {
                        //第二步：你有这个科目下的卡&&卡余额>资源价格:弹出1床
                        var subject_card = transformation(a);
                        var object_Type = transformationType(objectType);
                        document.getElementById("p11").innerHTML = "" + object_Type + "需要" + price + "金豆";
                        document.getElementById("p21").innerHTML = "确认要扣" + subject_card + "卡" + price + "金豆吗？";
                        document.getElementById("a11").innerHTML = "确认";
                        document.getElementById("a21").innerHTML = "充值";
                        document.getElementById("1").style.display = "block"
                        biao = 1
                        card = cards[i];
                        object_Id = objectId;
                        object_Types = objectType;
                        break;

                    }
                }
                if (biao != 1) {
                    for (var i = 0; i < cards.length; i++) {
                        var a = cards[i].subjectId
                        var money = parseInt(userAmount) + 0 + parseInt( cards[i].amount);
                        var mp = "false";
                        if(money > price || money == price){
                            mp = "true";
                        }
                        if (cards[i].subjectId == subject && cards[i].amount < price && biao != 1 ) {
                            //第三步：你有这个科目下的卡&&卡余额<资源价格:弹出2床
                            var subject_card = transformation(a);
                            var object_Type = transformationType(objectType);
                            document.getElementById("p12").innerHTML = "" + object_Type + "需要" + price + "金豆";
                            document.getElementById("p22").innerHTML = "您的" + subject_card + "卡余额不足";
                            document.getElementById("a12").innerHTML = "充值";
                            if(mp == "true"){
                                document.getElementById("a22").innerHTML = "转入";
                            }else if(mp == "false"){
                                document.getElementById("a22").innerHTML = "";
                            }

                            document.getElementById("2").style.display = "block"
                            biao = 1;
                            break;
                        }
                    }
                }
                if (biao != 1) {
                    for (var i = 0; i < cards.length; i++) {
                        var a = cards[i].subjectId
                        if (cards[i].subjectId != subject && cards[i].amount >= price && biao != 1) {
                            //第二步：你有其他科目下的卡&&卡余额>资源价格:弹出3床
                            var subject_card = transformation(a);
                            if (a == 1) {
                                var object_card = transformation(2);
                            } else if (a == 2) {
                                var object_card = transformation(1);
                            }
                            var object_Type = transformationType(objectType);
                            document.getElementById("p13").innerHTML = "您没有" + object_card + "卡";
                            document.getElementById("p23").innerHTML = "确认要扣" + subject_card + "卡" + price + "金豆吗？";
                            document.getElementById("a13").innerHTML = "确认";
                            document.getElementById("a23").innerHTML = "充值";
                            document.getElementById("3").style.display = "block"
                            biao = 1;
                            card = cards[i];
                            object_Id = objectId;
                            object_Types = objectType;
                            break;
                        }
                    }
                }
                if (biao != 1) {
                    for (var i = 0; i < cards.length; i++) {
                        var a = cards[i].subjectId
                        // var iYear=parseInt(iYear);
                        var money = parseInt(userAmount) + 0 + parseInt( cards[i].amount);
                        var am = parseInt(cards[i].amount)
                        var mp = "false";
                        if(money > price || money == price){
                            mp = "true";
                        }
                        if (cards[i].subjectId != subject && am < price && biao != 1) {
                            //第二步：你有其他科目下的卡&&卡余额<资源价格:弹出4床
                            var subject_card = transformation(a);
                            if (a == 1) {
                                var object_card = transformation(2);
                            } else if (a == 2) {
                                var object_card = transformation(1);
                            }
                            var object_Type = transformationType(objectType);
                            document.getElementById("p14").innerHTML = "您没有" + object_card + "卡";
                            document.getElementById("p24").innerHTML = "您的" + subject_card + "卡余额不足";
                            document.getElementById("a14").innerHTML = "充值";
                            if(mp == "true"){
                                document.getElementById("a24").innerHTML = "转入";
                            }else if(mp == "false"){
                                document.getElementById("a24").innerHTML = "";
                            }
                            document.getElementById("4").style.display = "block"
                            biao = 1;
                            break;
                        }
                    }
                }
                biao = 0;
            }
            var sale_info;
            var cards;
            var userAmount;
            var price;
            var d = document;
            var biao;


            var count = 1;
            var object_Id;
            var object_Types;
            var card;
            var ke;

            var card_evolution = [];
            var card_evolution_subject = [];
            var cards;
            var user_amount;
            var Consumption_amount;
            var transfer;
            var filter = {}
            //第三步确认购买||确认转入||确认充值
            function    (sourceId, sourceType, object_Id, object_Type, count, money, none) {
                var result;
                if(money == null && count == null){
                    filter  = { // 充值rechargeFilter
                        sourceType: sourceType,
                        objectId: objectId,
                        objectType: objectType,
                        money: money
                    }
                }else
                if(count == null){
                    filter = { //转账transferFilter
                        sourceId: sourceId,
                        sourceType: sourceType,
                        objectId: object_Id,
                        objectType: object_Type,
                        money: money
                    }
                }else
                if(money == null){
                    filter = { // 购买purchaseFilter
                        sourceId: sourceId,
                        sourceType: sourceType,
                        objectId: object_Id,
                        objectType: object_Type,
                        count: count
                    }
                }


                $.ajax({
                    type: "post",
                    url: "/api/transactions",
                    dataType: "json",
                    data: JSON.stringify(filter),
                    contentType: "application/json; charset=utf-8",
                    success: function (e) {
                        alert("成功")
                        if(none == 1){
                            document.getElementById("1").style.display = "none"
                        }
                        if(none == 2){
                            document.getElementById("2").style.display = "none"
                        }
                        if(none == 3){
                            document.getElementById("3").style.display = "none"
                        }
                        if(none == 4){
                            document.getElementById("4").style.display = "none"
                        }
                    },
                    error: function (e) {
                        result = e;
                        alert(JSON.stringify(e))
                        switch (JSON.stringify(e.status)) {
                            case "408":
                                alert("余额不足")
                                break;
                            case "200":
                                alert("成功")
                                break;
                        }
                    }
                })
                return result;
            }
            var purchase1 = document.getElementById("a11")
            purchase1.addEventListener('click', function (e) {
                atom(card.id,"card",object_Id,object_Types,count,null,1);
            })

            var purchase2 = document.getElementById("a13")
            purchase2.addEventListener('click', function (e) {
                atom(card.id,"card",object_Id,object_Types,count,null,3);
            })
            var recharge1 = document.getElementById("a21")
            recharge1.addEventListener('click', function (e) {
                rechargePage(cards,"c")
            })

            var recharge2 = document.getElementById("a12")
            recharge2.addEventListener('click', function (e) {
                rechargePage(cards,'c')
            })

            var recharge3 = document.getElementById("a23")
            recharge3.addEventListener('click', function (e) {
                rechargePage(cards,'c')
            })

            var recharge4 = document.getElementById("a14")
            recharge4.addEventListener('click', function (e) {
                document.getElementById("4").style.display = "none"
                rechargePage(cards,'c')
            })

            var turnOut1 = document.getElementById("a22")
            turnOut1.addEventListener('click', function (e) {
                document.getElementById("2").style.display = "none"
                turnOutPage(cards,"z")
            })

            var turnOut2 = document.getElementById("a24")
            turnOut2.addEventListener('click', function (e) {
                document.getElementById("4").style.display = "none"
                turnOutPage(cards,"z")
            })

            var z = document.getElementById("z")
            z.addEventListener('click',function (e) {
                document.getElementById("c").style.display = "none"
                turnOutPage(cards)
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
            close1.addEventListener('click', function (e) {
                document.getElementById("3").style.display = "none"
            })
            var close4 = document.getElementById("close4")
            close1.addEventListener('click', function (e) {
                document.getElementById("4").style.display = "none"
            })

            function transformation(sex) {
                var result;
                if (sex == 1) {
                    result = "语文"
                }
                if (sex == 2) {
                    result = "数学"
                }
                return result;
            }

            function transformationType(objectType) {
                var result;
                if (objectType == "knowledgePoint") {
                    result = "知识点"
                }
                if (objectType == "liveVideo") {
                    result = "直播"
                }
                if (objectType == "video") {
                    result = "回放"
                }
                return result;
            }

            function duo(id,T) {
                var w = 1;
                w = id;
                var arrList = new Array;
                for (var i = 0; i < cards.length; i++) {
                    if(cards[i].id == id){
                        arrList.push(cards[i]);
                        if(T == "z"){
                            dan(arrList)
                        }else
                        if(T == "c"){
                            rechargePagedan(arrList)
                        }
                    }
                }
            }

            function dan(cards) {
                var a = cards[0].subjectId
                cards[0].subjectId = transformation(a)
                if (cards[0].amount == 0) {
                    cards[0].amount = "0"
                }
                proc({
                    containerId: 'selectOneCard',
                    data: cards,
                    templateId: 'selectOneCard-template'
                })
                transfer = cards;
                document.getElementById("selectCard").style.display = "none"
                document.getElementById("tishi").innerHTML = "" + userAmount + " 金豆"


                document.getElementById("1").style.display = "none"
                document.getElementById("selectOneCard").style.display = "block"
                var submitTransfer = document.getElementById("submitTransfer")
                submitTransfer.addEventListener('click', function (e) {
                    //userAmount(transfer[0].id, $("#Number").val())
                    atom("96520767275011","user",transfer[0].id,"card",null,$("#Number").val())
                    document.getElementById("selectOneCard").style.display = "none"

                })
            }

            function turnOutPage(cards,T) {
                if (cards.length > 1) {
                    for (var i = 0; i < cards.length; i++) {
                        if (cards[i].subjectId == 1) {
                            cards[i].image = "keBiao.png"
                            //.push("image","myUser/image/true.png");
                        }
                        if (cards[i].subjectId == 2) {
                            cards[i].image = "mathBiao.png"
                            //.push("image","myUser/image/true.png");
                        }
                        cards[i].subjectId = transformation(cards[i].subjectId)
                        if (cards[i].amount == 0) {
                            cards[i].amount = "0"
                        }
                    }
                    proc({
                        containerId: 'selectCard',
                        data: cards,
                        templateId: 'selectCard-template'
                    })
                    document.getElementById("selectCard").style.display = "block"

                    $('.mine_zuli').on('click', function (e) {
                        var id = e.target.dataset.id
                        duo(id,T)
                    })
                } else if (cards.length == 1) {
                    dan(cards);
                }
            }

            function rechargePage(cards,T) {
                if (cards.length > 1) {
                    for (var i = 0; i < cards.length; i++) {
                        if (cards[i].subjectId == 1) {
                            cards[i].image = "keBiao.png"
                            //.push("image","myUser/image/true.png");
                        }
                        if (cards[i].subjectId == 2) {
                            cards[i].image = "mathBiao.png"
                            //.push("image","myUser/image/true.png");
                        }
                        cards[i].subjectId = transformation(cards[i].subjectId)
                        if (cards[i].amount == 0) {
                            cards[i].amount = "0"
                        }
                    }
                    proc({
                        containerId: 'selectCard',
                        data: cards,
                        templateId: 'selectCard-template'
                    })
                    //  document.getElementById("1").style.display = "none"

                    document.getElementById("selectCard").style.display = "block"

                    $('.mine_zuli').on('click', function (e) {
                        var id = e.target.dataset.id
                        duo(id,T)
                    })
                } else if (cards.length == 1) {
                    rechargePagedan(cards);
                }
            }

            function rechargePagedan(cards) {
                var a = cards[0].subjectId
                cards[0].subjectId = transformation(a)
                if (cards[0].amount == 0) {
                    cards[0].amount = "0"
                }
                document.getElementById("selectCard").style.display = "none"
                document.getElementById("name").innerHTML = ""+cards[0].no+""
                document.getElementById("usermoney").innerHTML = ""+cards[0].amount+""
                document.getElementById("c").style.display = "block"
            }

            function notice(objectType, subjectId, objectId) {
                var notice = {
                    objectType: objectType,
                    objectId: objectId
                }

                $.ajax({
                    type: "Put",
                    url: "/api/users/notice",
                    data: JSON.stringify(notice),
                    dataType: "JSON",
                    contentType: "application/json; charset=utf-8",
                    success: function (e) {
                        cards = e.cards;
                        user_amount = e.account;
                        Consumption_amount = e.amount;
                        for (var i = 0; i < cards.length; i++) {
                            if (cards[i].subjectId == subjectId) {
                                if (cards[i].amount > Consumption_amount) {
                                    card_evolution.push(cards[i]);//得到符合科目并符合余额大于最低消费金额的所有卡（直接是不是扣钱）
                                }
                            } else {
                                if (cards[i].amount > Consumption_amount) {
                                    card_evolution_subject.push(cards[i]);//得到不符合科目并符合余额大于最低消费金额的所有卡（直接是不是扣钱）
                                }
                            }
                        }
                        if (card_evolution.length != 0) {
                            var s = subject(card_evolution[0].subjectId)
                            document.getElementById("p1").innerHTML = "<br/>"
                            document.getElementById("p2").innerHTML = "您确定扣除" + s + "卡" + Consumption_amount + "金豆吗?"
                            document.getElementById("a1").innerHTML = "确认"
                            document.getElementById("a1").value = "submit"
                            document.getElementById("a2").innerHTML = "充值"
                            document.getElementById("a2").value = "chong"
                            document.getElementById("Popup").style.display = "block"
                            /*  // 直接扣除该科目的卡金额

                             document.getElementById("Popup").style.display="block"*/
                            // alert("是否扣除语文卡"+Consumption_amount+"金豆？");
                        } else if (card_evolution_subject.length != 0) {
                            var s = subject(subjectId)
                            document.getElementById("p1").innerHTML = "您没有" + s + "卡"
                            var ss = subject(card_evolution_subject[0].subjectId)
                            document.getElementById("p2").innerHTML = "您确定扣" + ss + "卡" + Consumption_amount + "金豆吗?"
                            document.getElementById("a1").innerHTML = "确认"
                            document.getElementById("a1").value = "save"

                            document.getElementById("a2").innerHTML = "购买" + s + "卡"
                            document.getElementById("a2").value = "shop"

                            document.getElementById("Popup").style.display = "block"
                            //直接扣除不同科目的卡金额
                            /*document.getElementById("Popup").style.display="block"
                             alert("你没有语文卡，是否扣除数学卡"+Consumption_amount+"金豆？");*/
                        } else if (user_amount > Consumption_amount) {
                            document.getElementById("p1").innerHTML = "<br/>"
                            document.getElementById("p2").innerHTML = "您确定扣除账户" + Consumption_amount + "金豆吗?"
                            document.getElementById("a1").innerHTML = "确认"
                            document.getElementById("a1").value = "user_save"
                            document.getElementById("a2").innerHTML = "转出"
                            document.getElementById("a2").value = "turnOut"
                            document.getElementById("Popup").style.display = "block"
                        } else if (card_evolution.length == 0 && card_evolution_subject.length == 0 && user_amount < Consumption_amount) {
                            document.getElementById("a1").innerHTML = "充值"
                            document.getElementById("a1").value = "chong"
                            document.getElementById("Popup").style.display = "block"
                        }
                    }
                })

                var a2 = document.getElementById("a2");
                a2.addEventListener('click', function (e) {
                    if (a2.value == "chong") {
                        alert("跳转到充值页面")
                    }
                    if (a2.value == "shop") {
                        alert("跳转到购买页面")
                    }
                    if (a2.value == "turnOut") {
                        TurnOut(cards)
                    }
                })

                var filter = {}
                var a1 = document.getElementById("a1");
                a1.addEventListener('click', function (e) {
                    if (a1.value == "submit") {
                        filter = {
                            subjectId: card_evolution[0].id,
                            objectType: objectType,
                            objectId: objectId,
                        }
                        save(filter)
                        //alert("提交"++objectType
                    }
                    if (a1.value == "save") {
                        /*filter = {
                         subjectId:card_evolution_subject[0].id,
                         objectType:objectType,
                         objectId:objectId,
                         }
                         save(filter)*/
                        //alert("提交没有"+card_evolution_subject[0].id+objectType)
                        alert("扣没有语文卡的数学卡的钱")
                    }
                    if (a1.value == "user_save") {
                        /*filter = {
                         subjectId:card_evolution_subject[0].id,
                         objectType:objectType,
                         objectId:objectId,
                         }*/
                        alert("扣账户钱")
                        //  alert("提账户user"+objectType)
                    }
                    if (a1.value == "chong") {
                        alert("充值")
                    }
                })

                function save(filter) {
                    $.ajax({
                        type: "put",
                        url: "/api/users/deductMoney",
                        data: JSON.stringify(filter),
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",
                        success: function (e) {
                            if (e == "200") {
                                document.getElementById("Popup").style.display = "none"
                                alert("购买成功，点击观看")
                            }

                        },
                        error: function (e) {
                            alert(e)
                        }
                    })
                }

                var close = document.getElementById("close");
                close.addEventListener('click', function (e) {
                    document.getElementById("Popup").style.display = "none"
                })

            }

            function transfers(cardId, money) {
                var filter = {
                    target: {
                        objectId: 0,
                        amount: money,
                        subjectId: cardId,
                    }


                }
                $.ajax({
                    type: "put",
                    url: "/api/users/transfer/transfer",
                    dataType: "json",
                    data: JSON.stringify(filter),
                    contentType: "application/json; charset=utf-8",
                    error: function (e) {
                        if (e.status == 408) {
                            alert("余额不足")
                        } else {
                            alert("转账成功")
                            document.getElementById("selectOneCard").style.display = "none"
                            notice("knowledgePoint", 2);
                        }

                    }
                })
            }

            function TurnOut(cards) {
                if (cards.length > 1) {
                    for (var i = 0; i < cards.length; i++) {
                        if (cards[i].subjectId == 1) {
                            cards[i].image = "keBiao.png"
                            //.push("image","myUser/image/true.png");
                        }
                        if (cards[i].subjectId == 2) {
                            cards[i].image = "mathBiao.png"
                            //.push("image","myUser/image/true.png");
                        }
                        cards[i].subjectId = subject(cards[i].subjectId)
                        if (cards[i].amount == 0) {
                            cards[i].amount = "0"
                        }
                    }
                    proc({
                        containerId: 'selectCard',
                        data: cards,
                        templateId: 'selectCard-template'
                    })
                    document.getElementById("Popup").style.display = "none"
                    document.getElementById("selectCard").style.display = "block"

                    $('.mine_zuli').on('click', function (e) {
                        var id = e.target.dataset.id
                        document.getElementById(id).src = "true.png";
                        setTimeout(function () {
                            duo(id)
                        }, 1000)

                        //duo(id)
                    })
                    // alert("到多个卡选择页面")
                } else if (cards.length == 1) {
                    dan(cards);
                }

                function duo(id) {
                    var w = 1;
                    w = id;
                    $.ajax({
                        type: "get",
                        url: "/api/cards/" + w,
                        dataType: "json",
                        success: function (e) {
                            var arrList = [];
                            arrList.push(e);
                            dan(arrList)
                        }
                    })
                }

                function dan(cards) {
                    var a = cards[0].subjectId
                    cards[0].subjectId = subject(a)
                    if (cards[0].amount == 0) {
                        cards[0].amount = "0"
                    }
                    proc({
                        containerId: 'selectOneCard',
                        data: cards,
                        templateId: 'selectOneCard-template'
                    })
                    transfer = cards;
                    document.getElementById("selectCard").style.display = "none"
                    document.getElementById("tishi").innerHTML = "" + user_amount + " 金豆"


                    document.getElementById("Popup").style.display = "none"
                    document.getElementById("selectOneCard").style.display = "block"
                    var submitTransfer = document.getElementById("submitTransfer")
                    submitTransfer.addEventListener('click', function (e) {
                        transfers(transfer[0].id, $("#Number").val())
                    })
                }
            }

        }
    })
}

