function SchoFood(htmlId) {
    var t;
    var t2;
    var model = {
        views: [], //Holds view
        food: {}, //Information model needs to hold

        addView: function(view){ //add a view and update the model
            this.views.push(view);
            view(""); //update the view just added
        },
        //update all vies in the model with the msg
        updateViews: function(msg){
            var i = 0;
            for(i = 0; i < this.views.length; i++){
                this.views[i](msg);
            }
        },

        //load min
        loadWidgetData: function(){
            var that = this;
            $.get("widgets/SchoFood/SchoFoodWidget.html", function (data) {
                that.food["htmlD"] = data;
                that.updateViews("htmlD");

                $.getJSON("https://api.uwaterloo.ca/v2/foodservices/menu.json?key=3a78267c2e383a9968cdf1bcbb4f9d72", function (d) {
                    if (d.meta.status == 200) {
                        that.food["menu"] = d.data;
                    }
                });
                $.getJSON("https://api.uwaterloo.ca/v2/foodservices/locations.json?key=3a78267c2e383a9968cdf1bcbb4f9d72", function (d) {
                    if (d.meta.status == 200) {
                        that.food["locations"] = d.data;
                    }
                });

                $.getJSON("https://api.uwaterloo.ca/v2/foodservices/diets.json?key=3a78267c2e383a9968cdf1bcbb4f9d72", function (d) {
                    if (d.meta.status == 200) {
                        that.food["category"] = d.data;
                        that.updateViews("category");
                    } else {
                        console.log("error");
                    }
                });

            });
        },

        getInfoDataTable: function(dietOption){
          //  console.log(dietOption)
            var returnName = [];
            for(var i = 0; i < dietOption.length; i ++){
                var type = dietOption[i];
                var outLets = this.food["menu"].outlets;
                for(var j = 0; j < outLets.length; j++){
                    var outL = outLets[j];
                    var menuObj = outL.menu;
                    for(var x = 0; x < menuObj.length; x++){
                        var dmealObj = menuObj[x].meals.dinner;
                        var lmealObj = menuObj[x].meals.lunch;
                        for(var k = 0; k < lmealObj.length; k++){
                            var dtype = lmealObj[k].diet_type;
                            var indexof = $.inArray(dtype, dietOption);
                            var seenOrNot = $.inArray(outL.outlet_id, returnName);
                            if(indexof >= 0 && seenOrNot < 0){
                                returnName.push(outL.outlet_id);
                            }


                        }
                    }
                }
            }
            //console.log(returnName);
            this.food["VaildID"] = returnName;
            this.updateViews("UpdateRTable");
        },

        menuForClick: function(resID, dietOption){
            var weekday = new Array(7);
            weekday[0] = "Sunday";
            weekday[1] = "Monday";
            weekday[2] = "Tuesday";
            weekday[3] = "Wednesday";
            weekday[4] = "Thursday";
            weekday[5] = "Friday";
            weekday[6] = "Saturday";

            var d = new Date();
            var n = d.getDay();
            var day = weekday[n];
            var outLets = this.food["menu"].outlets;
            var dArray = [];
            var lArray = [];
            console.log(outLets);
            console.log(day);
            outLets.forEach(function(d){
                if(d.outlet_id === resID){
                    d.menu.forEach(function(obj){
                        if(day == obj.day){ //Change "Monday" -> day
                            console.log(obj.meals);
                            obj.meals.dinner.forEach(function(din){
                                   if($.inArray(din.diet_type, dietOption) >= 0){
                                       dArray.push(din);
                                   }
                            });
                            obj.meals.lunch.forEach(function(din){
                                if($.inArray(din.diet_type, dietOption) >= 0){
                                    lArray.push(din);
                                }
                            })

                        }
                    });
                }
            });
            this.food["dmenu"] = dArray;
            this.food["lmenu"] = lArray;
           /*console.log("here");
            console.log(dArray);
            console.log(lArray);
            console.log(this.food);*/
            this.updateViews("MenuTable");

        },

        foodInfoP: function(pid, fun) {
            $.getJSON("https://api.uwaterloo.ca/v2/foodservices/products/" + pid + ".json?key=3a78267c2e383a9968cdf1bcbb4f9d72", function (d) {
                    if(d.meta.status == 200){
                        var info = d.data;
                        console.log(info);
                        fun(info.protein_g, info.total_fat_g, info.calories);
                    }

            })
        }

    };



    var foodView = {
        updateView: function(msg){
            var allowDrop = function (ev) {
//                console.log("allowdrop");
                ev.preventDefault();
            };

            var drag = function (ev) {
                /*console.log("drag");
                 console.log(ev);
                 console.log(ev.target.id);*/
                ev.originalEvent.dataTransfer.setData("Text", ev.target.id);
            };

            var weekday = new Array(7);
            weekday[0] = "sunday";
            weekday[1] = "monday";
            weekday[2] = "tuesday";
            weekday[3] = "wednesday";
            weekday[4] = "thursday";
            weekday[5] = "friday";
            weekday[6] = "saturday";

            var d = new Date();
            var n = d.getDay();
            var drop = function (ev) {
//                console.log("drop");
                ev.preventDefault();
                if (ev.target.id === "SchoFood_dragit") {
                    $("#SchoFood_Searches").css("display", "");
                    ev.target = document.getElementById("SchoFood_Searches");
                    //console.log(ev.target);
                    $("#SchoFood_dragit").toggle();
                }
                var data = ev.originalEvent.dataTransfer.getData("Text");
                if (ev.target.className != "SchoFood_category" && ev.target.className != "SchoFood_searchspace") {
                    ev.target.appendChild(document.getElementById(data));
                    $("#SchoFood_Choices div").removeClass();
                    $("#SchoFood_Choices div").addClass('SchoFood_category');
                    $("#SchoFood_Searches div").removeClass();
                    $("#SchoFood_Searches div").addClass('SchoFood_searchspace');
                    var list = [];
                    $(".SchoFood_searchspace").each(function () {
                        list.push($(this).text());
                    });
                    if ($(".SchoFood_searchspace").length === 1) {
                        $("#SchoFood_dragit").toggle();
                        $("#SchoFood_dragit").removeClass();
                    }
                    model.getInfoDataTable(list);
                }

            };
            if(msg === "htmlD"){
                $(htmlId).html(model.food["htmlD"]);
                $(".SchoFood_subCommonWidget").on("drop", drop);
                $(".SchoFood_subCommonWidget").on("dragover",allowDrop);
                t =  $('#SchoFood_foodLocationInfo').dataTable( {
                    "aaData": false,
                    "bLengthChange": false,
                    "scrollY": "85px",
                    "scrollX": true
                } );
                t2 = $('#SchoFood_menuInfo').dataTable( {
                    "aaData": false,
                    "bLengthChange": false,
                    "scrollY": "85px",
                    "scrollX": true
                } );
            }else if(msg === "category"){
                var data = model.food["category"];
                console.log("works");
                //console.log(data);
                var Cdata = data;
                for (var obj in Cdata) {
                    //console.log(Cdata[obj]);
                    var float = "float: left;";
                    if (obj % 2 == 0) {
                        float = "float: right;";
                    }
                    $("#SchoFood_Choices").append("<div id=\"" + obj + "\"class=\"SchoFood_category\"draggable=\"true\" style=\"" + float + "\"><div id=" + obj + "box style=\"display: table-cell; vertical-align: middle;\">" + Cdata[obj].diet_type + "</div></div>")
                }
                $(".SchoFood_category").on("dragstart", drag);
                $(".SchoFood_searchspace").on("dragstart", drag);
            }else if(msg === "UpdateRTable"){
               // model.menuForClick(5, ["Halal", "Non Vegetarian"]);
                t.fnClearTable();
                var validID = model.food["VaildID"];
                model.food["locations"].forEach(function(obj){
                    if ($.inArray(obj.outlet_id, validID) >= 0) {
                        var day = weekday[n];
                      //  console.log(t);
                        var div = "<div id=\"SchoFood_" + obj.outlet_id + "menuItem\" class=\"SchoFood_menu\" style=\"cursor: pointer;\">" + obj.outlet_name + "</div>";
                        var list = [];
                        $(".SchoFood_searchspace").each(function () {
                            list.push($(this).text());
                        });
                        console.log(list);
                        t.fnAddData([div,obj.building, obj.opening_hours[day].opening_hour, obj.opening_hours[day].closing_hour]);
                        $("#SchoFood_" + obj.outlet_id + "menuItem").click(function(){
                            console.log("here there Here");
                            model.menuForClick(obj.outlet_id, list);
                            $(".SchoFood_menu").css("color", "black");
                            $(this).css("color", "lightgrey");
                        });
                    }
                });
            }else if (msg === "MenuTable"){
                t2.fnClearTable();
                var lunch = model.food["lmenu"];
                var dinner = model.food["dmenu"];
                console.log(lunch);
                console.log(dinner);
                lunch.forEach(function(obj) {
                    model.foodInfoP(obj.product_id ,function(p, fat, c) {
                        t2.fnAddData([obj.product_name, "Lunch", obj.diet_type, c, p, fat]);
                    })

                });
                dinner.forEach(function(obj) {
                    model.foodInfoP(obj.product_id ,function(p, fat, c) {
                        t2.fnAddData([obj.product_name, "Dinner", obj.diet_type, c, p, fat]);
                    })

                });
            }

        },
        intitView: function(){
            console.log("Initialize Widget");
            var parent = $(htmlId).parent();
            parent.css("padding-left", "0px");
            parent.css("padding-right", "0px");
            model.addView(this.updateView);

            var loadScriptCSS = document.createElement('link');

            loadScriptCSS.setAttribute("href", 'https://cdn.datatables.net/1.10.1/css/jquery.dataTables.css');
            loadScriptCSS.setAttribute("rel", "stylesheet");
            loadScriptCSS.setAttribute("type", "text/css");
            document.getElementsByTagName('head')[0].appendChild(loadScriptCSS);
            var loadScript = document.createElement('script');
            loadScript.onload = function(){
                console.log("here I am here");
                model.loadWidgetData();

            };
            loadScript.src='https://cdn.datatables.net/1.10.1/js/jquery.dataTables.min.js';
            document.getElementsByTagName('head')[0].appendChild(loadScript);

        }
    }
    foodView.intitView();

}
