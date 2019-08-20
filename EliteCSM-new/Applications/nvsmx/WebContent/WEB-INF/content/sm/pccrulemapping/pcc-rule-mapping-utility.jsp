<%--
  Created by IntelliJ IDEA.
  User: Ishani
  Date: 17/10/17
  Time: 3:15 PM
  To change this template use File | Settings | File Templates.
--%>
<script src="${pageContext.request.contextPath}/js/third-party/jquery.edittreetable.js"></script>
<script src="${pageContext.request.contextPath}/js/expressionlibrary/dictionaryexpressionlibrary.js"></script>
<script type="text/javascript">
    var staticList = null;
    var dynamicList = null;
    function setPolicyKeyAttributes() {
        commonAutoCompleteUsingCssClass('.policyKeySuggestions',${PCRFKeySuggestions});
    }

    function setAttributeSuggestionsForGatewayToPCRF(id, attributes,protocol){
        $("#"+id).autocomplete();
        var dictionaryAutoCompleter;
        if(protocol == "DIAMETER"){
            dictionaryAutoCompleter = createModelForDictionary(attributes);
        }else if(protocol == "RADIUS"){
            dictionaryAutoCompleter = createModelForRadiusDictionary(attributes);
        }
        expressionAutoCompleteForDictionary(id,dictionaryAutoCompleter);

    }

    function setAttributeSuggestionsForPCRFToGateway(attributes) {
        var dataArrayToBePassed = new Object();
        for(var key in attributes){
            dataArrayToBePassed[key] = attributes[key].attributeName;
        }
        commonAutoCompleteUsingCssClassBasedOnKeyValue('.attribute',dataArrayToBePassed);
    }

    function getAttributeMappingsForDiameter(){
        $("#staticMappings").unbind();
        $("#staticMappings").html("");

        $("#dynamicMappings").unbind();
        $("#dynamicMappings").html("");

        /// Static Mapping Configuration
        staticList = new Object();
        var mappingData = [];
        var mappings = $("#staticAttributeMappings").find("input");
        for (var i = 0; i < mappings.length; i++) {
            var inputElement = mappings[i];
            if (isNullOrEmpty($(inputElement).val()) == false) {
                var jsonData = JSON.parse($(inputElement).val());
                mappingData.push(jsonData);
                staticList[jsonData.id] = JSON.stringify(jsonData);
            }
        }


        $("#staticMappings").bstreetable({
                data : mappingData,
                maintitle:"Attribute",
                maxlevel:10,
                title:"Static Attribute Mappings",
                type:'<s:property value="@com.elitecore.corenetvertex.constants.PacketMappingConstants@STATIC.name()" />',
                dictionaryAttributes : ${dictionaryData},
                protocolType: '<s:property value="@com.elitecore.corenetvertex.constants.CommunicationProtocol@DIAMETER.name()" />',
                nodeaddCallback:function(data,callback){
                    callback({id:data.id,attribute:data.attribute,policykey:data.policykey,defaultvalue:data.defaultvalue,valuemapping:data.valuemapping,pid:data.pid});

                },
                noderemoveCallback:function(data,callback){
                    delete staticList[data];
                    callback();
                },
                nodeupdateCallback:function(data,callback){
                    staticList[data.id] = JSON.stringify(data);
                    callback();

                },
                extfield:[
                    {title:"Policy Key",key:"policykey",type:"input",cssClass:"form-control input-sm policyKeySuggestions",id:"policykey-"},
                    {title:"Default Value",key:"defaultvalue",type:"input", cssClass:"form-control input-sm",id:"defaultValue-"},
                    {title:"Value Mapping",key:"valuemapping",type:"input",cssClass:"form-control input-sm",id:"valueMapping-"}
                ]
            }
        );
        setAttributeSuggestionsForPCRFToGateway(${dictionaryData});
        dynamicList = new Object();

        var dynamicMappingData = [];

        var dynamicMappings = $("#dynamicAttributeMappings").find("input");
        for (var i = 0; i < dynamicMappings.length; i++) {
            var inputElement = dynamicMappings[i];
            if (isNullOrEmpty($(inputElement).val()) == false) {
                var jsonData = JSON.parse($(inputElement).val());
                dynamicMappingData.push(jsonData);
                dynamicList[jsonData.id] = JSON.stringify(jsonData);
            }
        }
        $("#dynamicMappings").bstreetable({
                data : dynamicMappingData,
                maintitle:"Attribute",
                maxlevel:10,
                title:"Dynamic Attribute Mappings",
                type:'<s:property value="@com.elitecore.corenetvertex.constants.PacketMappingConstants@DYNAMIC.name()" />',
                dictionaryAttributes : ${dictionaryData},
                protocolType: '<s:property value="@com.elitecore.corenetvertex.constants.CommunicationProtocol@DIAMETER.name()" />',
                nodeaddCallback:function(data,callback){
                    callback({id:data.id,attribute:data.attribute,policykey:data.policykey,defaultvalue:data.defaultvalue,valuemapping:data.valuemapping,pid:data.pid});

                },
                noderemoveCallback:function(data,callback){
                    delete dynamicList[data];
                    callback();
                },
                nodeupdateCallback:function(data,callback){
                    dynamicList[data.id] = JSON.stringify(data);
                    callback();

                },
                extfield:[
                    {title:"Policy Key",key:"policykey",type:"input",cssClass:"form-control input-sm policyKeySuggestions",id:"policykey-"},
                    {title:"Default Value",key:"defaultvalue",type:"input", cssClass:"form-control input-sm",id:"defaultValue-"},
                    {title:"Value Mapping",key:"valuemapping",type:"input",cssClass:"form-control input-sm",id:"valueMapping-"}
                ]
            }
        );
        setAttributeSuggestionsForPCRFToGateway(${dictionaryData});

    }

    function getAttributeMappingsForRadius(){
        $("#staticMappings").unbind();
        $("#staticMappings").html("");

        $("#dynamicMappings").unbind();
        $("#dynamicMappings").html("");

        /// Static Mapping Configuration
        staticList = new Object();
        var mappingData = [];
        var mappings = $("#staticAttributeMappings").find("input");
        for (var i = 0; i < mappings.length; i++) {
            var inputElement = mappings[i];
            if (isNullOrEmpty($(inputElement).val()) == false) {
                var jsonData = JSON.parse($(inputElement).val());
                mappingData.push(jsonData);
                staticList[jsonData.id] = JSON.stringify(jsonData);
            }
        }


        $("#staticMappings").bstreetable({
                data : mappingData,
                maintitle:"Attribute",
                maxlevel:10,
                title:"Static Attribute Mappings",
                type:'<s:property value="@com.elitecore.corenetvertex.constants.PacketMappingConstants@STATIC.name()" />',
                dictionaryAttributes : ${dictionaryData},
                protocolType: '<s:property value="@com.elitecore.corenetvertex.constants.CommunicationProtocol@RADIUS.name()" />',
                nodeaddCallback:function(data,callback){
                    callback({id:data.id,attribute:data.attribute,policykey:data.policykey,defaultvalue:data.defaultvalue,valuemapping:data.valuemapping,pid:data.pid});

                },
                noderemoveCallback:function(data,callback){
                    delete staticList[data];
                    callback();
                },
                nodeupdateCallback:function(data,callback){
                    staticList[data.id] = JSON.stringify(data);
                    callback();

                },
                extfield:[
                    {title:"Policy Key",key:"policykey",type:"input",cssClass:"form-control input-sm policyKeySuggestions",id:"policykey-"},
                    {title:"Default Value",key:"defaultvalue",type:"input", cssClass:"form-control input-sm",id:"defaultValue-"},
                    {title:"Value Mapping",key:"valuemapping",type:"input",cssClass:"form-control input-sm",id:"valueMapping-"}
                ]
            }
        );


        dynamicList = new Object();
        var dynamicMappingData = [];
        var dynamicMappings = $("#dynamicAttributeMappings").find("input");
        for (var i = 0; i < dynamicMappings.length; i++) {
            var inputElement = dynamicMappings[i];
            if (isNullOrEmpty($(inputElement).val()) == false) {
                var jsonData = JSON.parse($(inputElement).val());
                dynamicMappingData.push(jsonData);
                dynamicList[jsonData.id] = JSON.stringify(jsonData);
            }
        }


        $("#dynamicMappings").bstreetable({
                data : dynamicMappingData,
                maintitle:"Attribute",
                maxlevel:10,
                title:"Dynamic Attribute Mappings",
                type:'<s:property value="@com.elitecore.corenetvertex.constants.PacketMappingConstants@DYNAMIC.name()" />',
                dictionaryAttributes : ${dictionaryData},
                protocolType: '<s:property value="@com.elitecore.corenetvertex.constants.CommunicationProtocol@RADIUS.name()" />',
                nodeaddCallback:function(data,callback){
                    callback({id:data.id,attribute:data.attribute,policykey:data.policykey,defaultvalue:data.defaultvalue,valuemapping:data.valuemapping,pid:data.pid});

                },
                noderemoveCallback:function(data,callback){
                    delete dynamicList[data];
                    callback();
                },
                nodeupdateCallback:function(data,callback){
                    dynamicList[data.id] = JSON.stringify(data);
                    callback();

                },
                extfield:[
                    {title:"Policy Key",key:"policykey",type:"input",cssClass:"form-control input-sm policyKeySuggestions",id:"policykey-"},
                    {title:"Default Value",key:"defaultvalue",type:"input", cssClass:"form-control input-sm",id:"defaultValue-"},
                    {title:"Value Mapping",key:"valuemapping",type:"input",cssClass:"form-control input-sm",id:"valueMapping-"}
                ]
            }
        );
        setAttributeSuggestionsForPCRFToGateway(getParentAttributesForRadius());

    }

    var index = 0;
    var jIndex = 0;

    function getAttributeMappingList() {
        index = parseInt(index);
        $("#staticAttributeMappings").find("input").remove();
        for (var d in staticList) {
            var attrName = "staticAttributeMappings.mappings[" + index + "]";
            $("#staticAttributeMappings").append($("#tempStaticAttributeMappings").html());
            var NAME = "name";
            $("#staticAttributeMappings").find("input:last-child").attr(NAME, attrName).attr("value", "");
            $("#staticAttributeMappings").find("input:last-child").attr(NAME, attrName).attr("value", staticList[d]);
            index++;
        }
        // dynamic attribute mapping
        jIndex = parseInt(jIndex);
        $("#dynamicAttributeMappings").find("input").remove();
        for (var d in dynamicList) {
            var attrName = "dynamicAttributeMappings.mappings[" + jIndex + "]";
            $("#dynamicAttributeMappings").append($("#tempDynamicAttributeMappings").html());
            var NAME = "name";
            $("#dynamicAttributeMappings").find("input:last-child").attr(NAME, attrName).attr("value", "");
            $("#dynamicAttributeMappings").find("input:last-child").attr(NAME, attrName).attr("value", dynamicList[d]);
            jIndex++;
        }
    }

    function validatePccRuleMappingRows(){
        var staticMappingCnt = 0;
        var dynamicMappingCnt = 0;
        for(var d in staticList){
            staticMappingCnt = +staticMappingCnt + 1;
        }

        for(var d in dynamicList){
            dynamicMappingCnt = +dynamicMappingCnt + 1;
        }
        if(staticMappingCnt == 0 && dynamicMappingCnt == 0){
            $("#generalError").addClass("bg-danger");
            $("#generalError").text('<s:text name="attribute.mapping.required" />');
            return false;
        }
        if(staticMappingCnt > '<s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@DEFAULT_MAPPING_COUNT" />'){
            alert('<s:text name="attribute.mapping.maximum.count" />');
            return false;
        }

        if(dynamicMappingCnt > '<s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@DEFAULT_MAPPING_COUNT" />'){
            alert('<s:text name="attribute.mapping.maximum.count" />');
            return false;
        }
        return true;
    }

    function validatePolicyKeyAndAttributes(){
        var isValidKeys = true;
        var policyKeys = document.getElementsByClassName("policyKeySuggestions");
        for(var i = 0; i < policyKeys.length; ++i){
            var element = policyKeys[i];
            var val = element.value;
            if(isNullOrEmpty(val) && element.disabled == false){
                setError(element.id,"Policy Key Required");
                isValidKeys =  false;
            }else if(val.toLowerCase() == "PCCRULE".toLowerCase()){
                setError(element.id,"Invalid value for Policy Key");
                isValidKeys = false;
            }
        }

        var attributeKeys = document.getElementsByClassName("attribute");
        for(var i = 0; i < attributeKeys.length; ++i){
            var element = attributeKeys[i];
            var val = element.value;
            if(isNullOrEmpty(val)){
                setError(element.id,"Attribute Required");
                isValidKeys =  false;
            }
        }

        var attributeKeys = document.getElementsByClassName("attributeSuggestion");
        for(var i = 0; i < attributeKeys.length; ++i){
            var element = attributeKeys[i];
            var val = element.value;
            if(isNullOrEmpty(val)){
                setError(element.id,"Attribute Required");
                isValidKeys =  false;
            }
        }
        return isValidKeys;
    }


    function isGroupedAttribute(attributeValue,dictionaryAttributeList){
        for(var key in dictionaryAttributeList){
            var type = dictionaryAttributeList[key].type;
            if(key == attributeValue && isNullOrEmpty(type) == false && type.toLowerCase() == "grouped"){
                return true;
            }
        }
        return false;
    }



    function getParentAttributesForRadius(){
       return new Object(${parentAttributes});
    }


    function getChildAttributeForParent(id,attributeKey) {
        $.ajax({
            async: true,
            type: "POST",
            dataType : "json",
            url: "${pageContext.request.contextPath}/policydesigner/util/AjaxUtility/childAttributeForParent",
            data: {"attribute": attributeKey},
            success: function (data) {
                commonAutoCompleteBasedOnKeyValueForGivenId(id,data);
            }, error: function (data) {
            }
        });
    }

</script>