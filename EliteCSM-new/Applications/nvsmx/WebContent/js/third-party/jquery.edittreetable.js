/*!
 * bootstrap-treetable - jQuery plugin for bootstrapview treetable
 *
 * Copyright (c) 2007-2015 songhlc
 *
 * Licensed under the MIT license:
 *   http://www.opensource.org/licenses/mit-license.php
 *
 * Project home:
 *   http://github.com/songhlc
 *
 * Version:  1.0.0
 *
 */
(function($){
    $.fn.bstreetable = function(options){
        $window = window;
        var element = this;
        var $container;
        var settings = {
            container:window,
            data:[],
            extfield:[],//{title:"column name",key:"",type:"input"}
            nodeaddEnable:true,
            maxlevel:4,
            view:false,
            title:"Tree Table",
            dictionaryAttributes : null,
            protocolType : null,
            type:null,
            nodeaddCallback:function(data,callback){},
            noderemoveCallback:function(data,callback){},
            nodeupdateCallback:function(data,callback){},
            customalert:function(msg){
                alert(msg);
            },
            customconfirm:function(msg){
                return confirm(msg);
            },
            text:{
                NodeDeleteText:"Are You Sure To Delete This Node?"
            }
        };

        var TREENODECACHE = "treenode";
        var language ={};
        language.addchild = "Add A Child Node";
        if(options) {
            $.extend(settings, options);
        }
        /* Cache container as jQuery as object. */
        $container = (settings.container === undefined ||
            settings.container === window) ? $window : $(settings.container);
        var dom_table = $("<div id='packetMappingTable'  style='display:table' class='table table-blue table-bordered'>");
        var caption_data = $("<caption class=\"caption-header\">" + settings.title+
            " <div align=\"right\" class=\"display-btn\" id='btnAdd'>" +
            " <span class=\"btn btn-group btn-group-xs defaultBtn j-addClass\" id=\"addRow\"> <span class=\"glyphicon glyphicon-plus\"></span></span>" +
            " </div>" +
            " </caption>");
        dom_table.append(caption_data);
        if (settings.view == false) {
            renderHeader(dom_table);
        } else {
            renderHeaderForView(dom_table);
        }

        var treeData = {};
        /*render tree based on existing data*/
        var data = settings.data;
        var dom_body =  $("<div class='tt-body'></div>");
        generateNodesForExistingData(data,dom_body);

        dom_table.append(dom_body);
        element.append(dom_table);


        /*delegate click event*/
        element.delegate(".j-addClass","click",function(){
            var curElement = $(this).parent().parent().parent().parent().find(".tt-body");
            var row = {id:generateUUID(),attribute:"",pid:0};
            var curLevel = 1;
            generateTreeNode(curElement,row,curLevel,false);
        });

        /*delegate remove event*/
        element.delegate(".j-remove", "click", function (event) {
            var parentDom = $(this).parents(".class-level-ul");

            for(var i=0;i<settings.data.length;i++){
                if(settings.data[i].pid==parentDom.attr("data-id")){
                    settings.data.splice($.inArray(settings.data[i], settings.data),1);
                }
            }
            if (settings.customconfirm(settings.text.NodeDeleteText)) {
                /*trigger remove callback*/
                settings.noderemoveCallback(parentDom.attr("data-id"), function () {
                    removeChildElementBasedOnParent(parentDom.attr("data-id"),list)
                    parentDom.parent().remove();
                });
            }
        });

        /*delegate addchild event*/
        element.delegate(".j-addChild", "click", function () {
            var curElement = $(this).closest(".class-level");
            var pid = curElement.find(".class-level-ul").attr("data-id");
            var curLevel = $(this).parents(".class-level-ul").attr("data-level") - 0 + 1;
            var row = {id: generateUUID(), attribute: "", pid: pid};
            var txtValue = $(this).parent().parent().find(".attribute").val();
            generateTreeNode(curElement, row, curLevel);
            var txtId =  curElement.find("[data-id='" + row.id + "']").find(".attribute").attr("id");
            if(settings.protocolType == "RADIUS") {
                getChildAttributeForParent(txtId,txtValue);
            }

        });

        element.delegate(".attribute",'blur',  function(event){
            enableConfigurationForPCRFToGateway(this);
        });

        element.delegate(".form-control","focus",function(){
            clearErrorMessagesById($(this).attr("id"));
        });

        /*delegate lose focus event*/
        element.delegate(".form-control","blur",function(){
            var curElement = $(this);
            var data = {};
            data.id = curElement.closest(".class-level-ul").attr("data-id");
            var parentUl = curElement.closest(".class-level-ul");
            data.pid = parentUl.attr("data-pid");
            data.innercode = parentUl.attr("data-innercode");
            data.pinnercode = curElement.parents(".class-level-"+(parentUl.attr("data-level")-1)).children("ul").attr("data-innercode");
            parentUl.find(".form-control").each(function(){
                data[$(this).attr("name")] = $(this).val();
            });
            if(!data.id&&!curElement.attr("data-oldval")){
                console.log("add node");
                settings.nodeaddCallback(data,function(_data){
                    if(_data){
                        curElement.parent().attr("data-id",_data.id);
                        curElement.parent().parent().attr("data-id",_data.id);
                        curElement.parent().parent().attr("data-innercode",_data.innercode);
                        curElement.attr("data-oldval",curElement.val());
                    }
                });
            }
            else if(curElement.attr("data-oldval")!=curElement.val()){
                console.log("update node");
                settings.nodeupdateCallback(data,function(){
                    curElement.attr("data-oldval", curElement.val());
                });

            }
        });

        function removeChildElementBasedOnParent(parentId,list) {
            for(var l in list){
                var element = JSON.parse(list[l]);
                if(element.pid == parentId){
                    delete list[element.id];
                    removeChildElementBasedOnParent(element.id,list);
                }
            }
        }

        function setError(elementid, errorText) {
            var curElement = $("#".concat(elementid));
            var parentElement = curElement.parent();
            if (parentElement.hasClass("has-error has-feedback") == false) {
                parentElement.addClass("has-error has-feedback");
                parentElement.append("<span class=\"glyphicon glyphicon-remove form-control-feedback removeOnReset\"></span>");
                parentElement.append("<span class=\"help-block alert-danger removeOnReset\">".concat(errorText).concat("</span>"));
            }
        }

        function generateNodesForExistingData(data,dom_body){
            var parentNodes = getParentNodes(data);
            for (var j = 0; j < parentNodes.length; j++) {
                var row = parentNodes[j];
                if(settings.view == false){
                    generateTreeNode(dom_body, row, 1, false);
                }else{
                    generateTreeNodeForView(dom_body, row, 1, false);
                }
                treeData[row.id] = row;
                var children =  getChildElementsBasedOnId(row.id,data);
                generateChildElements(row.id,children,dom_body,data);
            }
        }

        function getParentNodes(data){
            var index = 0;
            var parentNodeList = [];
            for(var j =0;j<data.length;j++){
                var row = data[j];
                if(row.pid == 0){
                    parentNodeList[index] = row;
                    index = +index + 1;
                }
            }
            return parentNodeList;
        }

        function generateChildElements(pid,children,dom_body,data){
            var parent_div = $(dom_body).find(".j-addNode").parent().find("[data-id='" + pid + "']").parent().parent();
            var level = $(parent_div).find(".class-level-ul").attr("data-level")-0 + 1;
            for(var j =0 ; j< children.length;j++){
                var childRow = children[j];
                if(settings.view == false) {
                    generateTreeNode(parent_div, childRow, level);
                }else{
                    generateTreeNodeForView(parent_div, childRow, level);
                }
                var subChildren =  getChildElementsBasedOnId(childRow.id,data);
                generateChildElements(childRow.id,subChildren,parent_div,data);
            }
        }

        function getChildElementsBasedOnId(pid, data){
            var childElements = [];
            var index = 0;
            for(var j =0;j< data.length;j++){
                if(data[j].pid == pid){
                    childElements[index] = data[j];
                    index = +index + 1;
                }
            }
            return childElements;
        }

        function renderHeader(_dom_table){
            var dom_row = $('<div class="tt-header"></div>');
            if(settings.type == "GATEWAY_TO_PCC"){
                for(var j=0;j<settings.extfield.length;j++){
                    var column = settings.extfield[j];
                    $("<div style='width:15%;'></div>").text(column.title).appendTo(dom_row);
                    if(j == 0){
                        dom_row.append($("<div class='maintitle' style='width:50%;text-align:left'></div>").text(settings.maintitle));
                    }

                }
            }else {
                dom_row.append($("<div class='maintitle' style='width:50%;text-align:left'></div>").text(settings.maintitle));
                //render extfield
                for(var j=0;j<settings.extfield.length;j++){
                    var column = settings.extfield[j];
                    $("<div style='width:15%;'></div>").text(column.title).appendTo(dom_row);
                }
            }
            dom_row.append($("<div style='width:5%'>&nbsp;</div>"));
            _dom_table.append(dom_row);
        }


        function renderHeaderForView(_dom_table){
            var dom_row = $('<div class="tt-header"></div>');
            if(settings.type == "GATEWAY_TO_PCC"){
                for(var j=0;j<settings.extfield.length;j++){
                    var column = settings.extfield[j];
                    $("<div style='width:15%;'></div>").text(column.title).appendTo(dom_row);
                    if(j == 0){
                        dom_row.append($("<div class='maintitle' style='width:55%;text-align:left'></div>").text(settings.maintitle));
                    }

                }
            }else {
                dom_row.append($("<div class='maintitle' style='width:55%;text-align:left'></div>").text(settings.maintitle));
                //render extfield
                for(var j=0;j<settings.extfield.length;j++){
                    var column = settings.extfield[j];
                    $("<div style='width:15%;'></div>").text(column.title).appendTo(dom_row);
                }
            }
            $(_dom_table).find("div#btnAdd").attr("style","display:none");
            _dom_table.append(dom_row);
        }

        function generateColumn(row,extfield){
            var generatedCol;
            switch(extfield.type){
                case "input":generatedCol=$("<input type='text'/>").val(row[extfield.key]).attr("data-oldval",row[extfield.key]).attr("name",extfield.key).attr("class", extfield.cssClass).attr("id",extfield.id+generateUUID());break;
                default:generatedCol=$("<div></div>").text(row[extfield.key]);break;
            }
            return generatedCol;
        }


        function generateColumnForView(row,extfield){
            var generatedCol;
            switch(extfield.type){
                case "input":generatedCol=$("<input type='text' readonly/>").val(row[extfield.key]).attr("data-oldval",row[extfield.key]).attr("name",extfield.key).attr("class", extfield.cssClass).attr("id",extfield.id+generateUUID());break;
                default:generatedCol=$("<div></div>").text(row[extfield.key]);break;
            }
            return generatedCol;
        }

        function enableConfigurationForPCRFToGateway(currentElement) {
            var attributeFromTextBox = $(currentElement).val();
            var pid = $(currentElement).closest(".class-level").find(".j-addNode").attr("data-id");
            var oldVal = $(currentElement).attr("data-oldval");
            if(settings.type=="STATIC"){
                list = staticList;
            }else if(settings.type=="DYNAMIC"){
                list=dynamicList;
            }
            if(attributeFromTextBox.toLowerCase() == 'none' && settings.type == "PCC_TO_GATEWAY"){
                var curElement = $(currentElement).closest(".class-level-ul");
                $(curElement).find("input").removeAttr("disabled");
                $(curElement).find(".input-group-addon").addClass("disabledAddButton");
            } else {
                var attributes = settings.dictionaryAttributes;
                for (var key in attributes) {
                    if (key == attributeFromTextBox) {
                        var curElement = $(currentElement).closest(".class-level-ul");
                        if (isNullOrEmpty(oldVal) == false && oldVal != key && isGroupedAttribute(oldVal, attributes) == true && isGroupedAttribute(key, attributes) == false) {
                            if (settings.customconfirm("A Non Grouped Attribute will remove the sub hierarchy, Would you like to continue?")) {
                                $(curElement).find("input").removeAttr("disabled");
                                $(curElement).find(".input-group-addon").addClass("disabledAddButton");
                                $(curElement).siblings().remove();
                                updateMappingList(pid, list);
                            } else {
                                $(currentElement).val(oldVal);
                            }
                        } else {
                            if (isGroupedAttribute(attributeFromTextBox, attributes) == false) {
                                $(curElement).find("input").removeAttr("disabled");
                                $(curElement).find(".input-group-addon").addClass("disabledAddButton");
                            } else {
                                $(curElement).find(".input-group-addon").removeClass("disabledAddButton");
                                $(curElement).find("input").attr("disabled", "disabled");
                                $(currentElement).removeAttr("disabled");
                                $(curElement).find("input:disabled").val("");
                            }
                        }
                        break;
                    }

                }
            }
        }

        function updateMappingList(pid,list) {
            for (var mapping in list) {
                var value = JSON.parse(list[mapping]);
                if (value.pid == pid) {
                    delete list[value.id];
                    updateMappingList(value.id,list);
                }
            }
        }

        function toggleicon(toggleElement){
            var _element = toggleElement.find(".fa");
            if(_element.hasClass("fa-plus")){
                _element.removeClass("fa-plus").addClass("fa-minus");
                toggleElement.parent().addClass("selected");
            }else{
                _element.removeClass("fa-minus").addClass("fa-plus");
                toggleElement.parent().removeClass("selected")
            }
        }
        function toggleExpendStatus(curElement){
            if(curElement.find(".fa-minus").length>0){
                curElement.parent().parent().find(".class-level").removeClass("rowhidden");
            }
            else{
                curElement.parent().parent().find(".class-level").addClass("rowhidden");
            }

        }
        function collapseNode(){

        }
        function expendNode(){

        }
        function loadNode(loadElement,parentNode){
            var curElement = loadElement.parent().parent();
            var curLevel = loadElement.parent().attr("data-level")-0+1;
            if(parentNode&&parentNode.id){
                for(var i=0;i<settings.data.length;i++){
                    var row = settings.data[i];
                    //render first level row while row.pid equals 0 or null or undefined
                    if(row.pid==parentNode.id){
                        if(settings.view == false) {
                            generateTreeNode(curElement, row, curLevel);
                        }else{
                            generateTreeNodeForView(curElement, row, curLevel);
                        }
                        //cache treenode
                        treeData[row.id] = row;
                    }
                }
            }
            loadElement.parent().attr('data-loaded',true);

        }

        function generateTreeNode(curElement,row,curLevel,isPrepend){
            var dom_row = $('<div class="class-level class-level-'+curLevel+'"></div>');
            var dom_ul = $('<div class="class-level-ul" style="display:table-row"></div>');
            dom_ul.attr("data-pid",row.pid).attr("data-level",curLevel).attr("data-id",row.id);
            row.innercode&&dom_ul.attr("data-innercode",row.innercode);
            if(settings.type == "GATEWAY_TO_PCC") {
                generateRowForGatewayToPCRF(row, curLevel, dom_ul);
            }else{
                generateRowForPCRFToGateway(row, curLevel, dom_ul);
            }

            dom_ul.append($("<div style='min-width:5%;display: table-cell;' class='borderLeft'><span class='glyphicon glyphicon-trash j-remove'></span></div>"));
            dom_row.append(dom_ul);
            if(isPrepend){
                curElement.prepend(dom_row);
            }
            else{
                curElement.append(dom_row);
            }
            setPolicyKeyAttributes();
            if(settings.type == "GATEWAY_TO_PCC"){
                var id = $(dom_ul).find(".attributeSuggestion").attr("id");
                setAttributeSuggestionsForGatewayToPCRF(id,settings.dictionaryAttributes,settings.protocolType);
            }else {
                if(settings.protocolType == "RADIUS"){
                    var attributeList = getParentAttributesForRadius();
                    setAttributeSuggestionsForPCRFToGateway(attributeList);
                }else {
                    setAttributeSuggestionsForPCRFToGateway(settings.dictionaryAttributes);
                }
            }

        }

        function renderViewForPCRFToGateway(row,curLevel,dom_ul){
            if(curLevel-0>=settings.maxlevel){
                $('<div class="j-addNode"></div>').append($("<input type='text' class='form-control input-sm' readonly='true'/>").attr("data-oldval",row['attribute']).val(row["attribute"]).attr("name","attribute")).attr('data-id',row.id).appendTo(dom_ul);
                dom_ul.attr("data-loaded",true);
            }
            else{
                $('<div class="j-addNode"  style="width:55%"></div>').append($("<input type='text' class='form-control input-sm' readonly='true'/>").attr("data-oldval",row['attribute']).val(row["attribute"]).attr("name","attribute")).attr('data-id',row.id).appendTo(dom_ul);
            }
            for(var j=0;j<settings.extfield.length;j++){
                var colrender = settings.extfield[j];
                var coltemplate = generateColumnForView(row,colrender);
                $('<div style="width:15%;display: table-cell;" class="borderLeft"></div>').attr("data-id",row.id).html(coltemplate).appendTo(dom_ul);
            }
        }

        function generateRowForPCRFToGateway(row,curLevel,dom_ul){
            var attrId = "attribute"+"-" + generateUUID();
            if(curLevel-0>=settings.maxlevel){
                $('<div class="j-addNode"></div>').append($("<input type='text' class='form-control input-sm attribute' />").attr("data-oldval",row['attribute']).val(row["attribute"]).attr("name","attribute").attr("id",attrId)).attr('data-id',row.id).appendTo(dom_ul);
                dom_ul.attr("data-loaded",true);
            }
            else{
                var groupedComponent = $('<div class="input-group"></div>').append("<div class='input-group-addon disabledAddButton'><span class='glyphicon glyphicon-plus j-addChild'></span></div>")
                    .append($("<input type='text' class='form-control input-sm attribute' id=id />").attr("data-oldval",row['attribute']).val(row["attribute"]).attr("name","attribute").attr("id",attrId));
                $('<div class="j-addNode"></div>').attr('data-id',row.id).append(groupedComponent).appendTo(dom_ul);
            }

            for(var j=0;j<settings.extfield.length;j++){
                var colrender = settings.extfield[j];
                var coltemplate = generateColumn(row,colrender);
                coltemplate.attr("disabled","disabled");
                $('<div style="width:15%;display: table-cell;" class="borderLeft"></div>').attr("data-id",row.id).html(coltemplate).appendTo(dom_ul);
            }
            var currentElement = $(dom_ul).find("#"+attrId);
            enableConfigurationForPCRFToGateway(currentElement);

        }

        function generateRowForGatewayToPCRF(row,curLevel,dom_ul){
            for(var j=0;j<settings.extfield.length;j++){
                var colrender = settings.extfield[j];
                var coltemplate = generateColumn(row,colrender);
                $('<div style="width:15%;display: table-cell;" class="borderLeft"></div>').attr("data-id",row.id).html(coltemplate).appendTo(dom_ul);
                if(j==0){
                    if(curLevel-0>=settings.maxlevel){
                        $('<div class="j-addNode"></div>').append($("<input type='text' class='form-control input-sm attributeSuggestion'   />").attr("data-oldval",row['attribute']).val(row["attribute"]).attr("name","attribute").attr("id","attribute"+"-" + generateUUID())).attr('data-id',row.id).appendTo(dom_ul);
                        dom_ul.attr("data-loaded",true);
                    }
                    else{
                        $('<div class="j-addNode"></div>').append($("<input type='text' class='form-control input-sm attributeSuggestion' id=id />").attr("data-oldval",row['attribute']).val(row["attribute"]).attr("name","attribute").attr("id","attribute"+"-" + generateUUID())).attr('data-id',row.id).appendTo(dom_ul);
                    }

                }
            }
            $(dom_ul).find("div:first-child").attr("class","borderRight");
        }

        function generateTreeNodeForView(curElement,row,curLevel,isPrepend){
            var dom_row = $('<div class="class-level class-level-'+curLevel+'"></div>');
            var dom_ul = $('<div class="class-level-ul" style="display:table-row;"></div>');
            dom_ul.attr("data-pid",row.pid).attr("data-level",curLevel).attr("data-id",row.id);
            row.innercode&&dom_ul.attr("data-innercode",row.innercode);
            if(settings.type == "GATEWAY_TO_PCC") {
                renderViewForGatewayToPCRF(row, curLevel, dom_ul);
            }else{
                renderViewForPCRFToGateway(row, curLevel, dom_ul);
            }

            dom_row.append(dom_ul);
            if(isPrepend){
                curElement.prepend(dom_row);
            }
            else{
                curElement.append(dom_row);
            }

        }
        function renderViewForGatewayToPCRF(row,curLevel,dom_ul){
            for(var j=0;j<settings.extfield.length;j++){
                var colrender = settings.extfield[j];
                var coltemplate = generateColumnForView(row,colrender);
                if(row["pid"] == 0) {
                    $('<div style="width:15%;display: table-cell;" class="borderLeft"></div>').attr("data-id", row.id).html(coltemplate).appendTo(dom_ul);
                }else{
                    coltemplate.attr("style","visibility:hidden");
                    $('<div style="width:15%;display: table-cell;" class="borderLeft"></div>').attr("data-id", row.id).html(coltemplate).appendTo(dom_ul);
                }
                if(j==0){
                    if(curLevel-0>=settings.maxlevel){
                        $('<div class="j-addNode" style="width:55% !important;"></div>').append($("<input type='text' class='form-control input-sm' readonly='true' />").attr("data-oldval",row['name']).val(row["attribute"]).attr("name","attribute").attr("id","attribute"+"-" + generateUUID())).attr('data-id',row.id).appendTo(dom_ul);
                        dom_ul.attr("data-loaded",true);
                    }
                    else{
                        $('<div class="j-addNode" style="width:55% !important;"></div>').append($("<input type='text' class='form-control input-sm' readonly='true' id=id />").attr("data-oldval",row['name']).val(row["attribute"]).attr("name","attribute").attr("id","attribute"+"-" + generateUUID())).attr('data-id',row.id).appendTo(dom_ul);
                    }

                }
            }
            $(dom_ul).find("div:first-child").attr("class","borderRight");
        }
        function renderViewForPCRFToGateway(row,curLevel,dom_ul){
            if(curLevel-0>=settings.maxlevel){
                $('<div class="j-addNode" style="width:55% !important;"></div>').append($("<input type='text' class='form-control input-sm' readonly='true'/>").attr("data-oldval",row['attribute']).val(row["attribute"]).attr("name","attribute")).attr('data-id',row.id).appendTo(dom_ul);
                dom_ul.attr("data-loaded",true);
            }
            else{
                $('<div class="j-addNode"  style="width:55% !important;"></div>').append($("<input type='text' class='form-control input-sm' readonly='true'/>").attr("data-oldval",row['attribute']).val(row["attribute"]).attr("name","attribute")).attr('data-id',row.id).appendTo(dom_ul);
            }
            for(var j=0;j<settings.extfield.length;j++){
                var colrender = settings.extfield[j];
                var coltemplate = generateColumnForView(row,colrender);
                $('<div style="width:15%;display: table-cell;" class="borderLeft"></div>').attr("data-id",row.id).html(coltemplate).appendTo(dom_ul);
            }
        }

        function generateUUID() {
            var d = new Date().getTime();
            var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                var r = (d + Math.random() * 16) % 16 | 0;
                d = Math.floor(d / 16);
                return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
            });
            return uuid;
        }

    }
})(jQuery)
