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
    var list = null;
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
        dataArrayToBePassed["none"] = "none";
        commonAutoCompleteUsingCssClassBasedOnKeyValue('.attribute',dataArrayToBePassed);
    }

    function getPacketTypeBasedOnDiameterProtocolAndConversationType(){
        $("#attributeData").unbind();
        $("#attributeData").html("");
        var packetType = $("#packetType").val();
        clearSelectTag();
        var conversationTypeVal = $("#conversationType").val();
        if(conversationTypeVal == "<s:property value="@com.elitecore.corenetvertex.constants.ConversionType@GATEWAY_TO_PCC.name()" />" ){
            <% for(PacketType type : PacketType.getPacketTypeList(ProtocolType.DIAMETER,ConversionType.GATEWAY_TO_PCC)){%>
            $("#packetType").append(new Option('<%=type.name()%>','<%=type.name()%>'));
            <%}%>
            list = new Object();


        }else if(conversationTypeVal == "<s:property value="@com.elitecore.corenetvertex.constants.ConversionType@PCC_TO_GATEWAY.name()" />" ){
            <% for(PacketType type : PacketType.getPacketTypeList(ProtocolType.DIAMETER,ConversionType.PCC_TO_GATEWAY)){%>
            $("#packetType").append(new Option('<%=type.name()%>','<%=type.name()%>'));
            <%}%>
            list = new Object();

        }
        $( "#packetType" ).find('option[value="'+packetType+'"]').attr('selected','selected');

        var mappingData = [];
        var mappings = $("#attributeMappings").find("input");
        for (var i = 0; i < mappings.length; i++) {
            var inputElement = mappings[i];
            if (isNullOrEmpty($(inputElement).val()) == false) {
                var jsonData = JSON.parse($(inputElement).val());
                mappingData.push(jsonData);
                list[jsonData.id] = JSON.stringify(jsonData);
            }
        }

        $("#attributeData").bstreetable({
                data : mappingData,
                maintitle:"Attribute",
                maxlevel:10,
                title:"Attribute Mappings",
                type:conversationTypeVal,
                dictionaryAttributes : ${dictionaryData},
                protocolType: '<s:property value="@com.elitecore.corenetvertex.constants.CommunicationProtocol@DIAMETER.name()" />',
                nodeaddCallback:function(data,callback){
                    callback({id:data.id,attribute:data.attribute,policykey:data.policykey,defaultvalue:data.defaultvalue,valuemapping:data.valuemapping,pid:data.pid});

                },
                noderemoveCallback:function(data,callback){
                    delete list[data];
                    callback();
                },
                nodeupdateCallback:function(data,callback){
                    list[data.id] = JSON.stringify(data);
                    callback();

                },
                extfield:[
                    {title:"Policy Key",key:"policykey",type:"input",cssClass:"form-control input-sm policyKeySuggestions",id:"policykey-"},
                    {title:"Default Value",key:"defaultvalue",type:"input", cssClass:"form-control input-sm",id:"defaultValue-"},
                    {title:"Value Mapping",key:"valuemapping",type:"input",cssClass:"form-control input-sm",id:"valueMapping-"}
                ]
            }
        );
        if(conversationTypeVal == '<s:property value="@com.elitecore.corenetvertex.constants.ConversionType@GATEWAY_TO_PCC.name()" />'){
            var attributeSuggestions = document.getElementsByClassName("attributeSuggestion");
            for(var i=0;i<attributeSuggestions.length;i++){
                setAttributeSuggestionsForGatewayToPCRF($(attributeSuggestions[i]).attr("id"),${dictionaryData},"DIAMETER");
            }
        }else{
            setAttributeSuggestionsForPCRFToGateway(${dictionaryData});
        }

    }

    function getPacketTypeBasedOnRadiusProtocolAndConversationType(){
        $("#attributeData").unbind();
        $("#attributeData").html("");
        var packetType = $("#packetType").val();
        clearSelectTag();
        var conversationTypeVal = $("#conversationType").val();
        if(conversationTypeVal == "<s:property value="@com.elitecore.corenetvertex.constants.ConversionType@GATEWAY_TO_PCC.name()" />" ){
            <% for(PacketType type : PacketType.getPacketTypeList(ProtocolType.RADIUS,ConversionType.GATEWAY_TO_PCC)){%>
            $("#packetType").append(new Option('<%=type.name()%>','<%=type.name()%>'));
            <%}%>
            list = new Object();


        }else if(conversationTypeVal == "<s:property value="@com.elitecore.corenetvertex.constants.ConversionType@PCC_TO_GATEWAY.name()" />" ){
            <% for(PacketType type : PacketType.getPacketTypeList(ProtocolType.RADIUS,ConversionType.PCC_TO_GATEWAY)){%>
            $("#packetType").append(new Option('<%=type.name()%>','<%=type.name()%>'));
            <%}%>
            list = new Object();

        }
        $( "#packetType" ).find('option[value="'+packetType+'"]').attr('selected','selected');

        var mappingData = [];
        var mappings = $("#attributeMappings").find("input");
        for(var i=0;i< mappings.length;i++){
            var inputElement = mappings[i];
            if(isNullOrEmpty($(inputElement).val()) == false) {
                var jsonData = JSON.parse($(inputElement).val());
                mappingData.push(jsonData);
                list[jsonData.id] = JSON.stringify(jsonData);
            }
        }
        $("#attributeData").bstreetable({
                data : mappingData,
                maintitle:"Attribute",
                maxlevel:10,
                title:"Attribute Mappings",
                type:conversationTypeVal,
                dictionaryAttributes : ${dictionaryData},
                protocolType:'<s:property value="@com.elitecore.corenetvertex.constants.CommunicationProtocol@RADIUS.name()" />',
                nodeaddCallback:function(data,callback){
                    callback({id:data.id,attribute:data.attribute,policykey:data.policykey,defaultvalue:data.defaultvalue,valuemapping:data.valuemapping,pid:data.pid});
                },
                noderemoveCallback:function(data,callback){
                    delete list[data];
                    callback();
                },
                nodeupdateCallback:function(data,callback){
                    list[data.id] = JSON.stringify(data);
                    callback();

                },
                extfield:[
                    {title:"Policy Key",key:"policykey",type:"input",cssClass:"form-control input-sm policyKeySuggestions",id:"policykey-"},
                    {title:"Default Value",key:"defaultvalue",type:"input", cssClass:"form-control input-sm",id:"defaultValue-"},
                    {title:"Value Mapping",key:"valuemapping",type:"input",cssClass:"form-control input-sm",id:"valueMapping-"}
                ]
            }
        );

        if(conversationTypeVal == '<s:property value="@com.elitecore.corenetvertex.constants.ConversionType@GATEWAY_TO_PCC.name()" />'){
            var attributeSuggestions = document.getElementsByClassName("attributeSuggestion");
            for(var i=0;i<attributeSuggestions.length;i++){
                setAttributeSuggestionsForGatewayToPCRF($(attributeSuggestions[i]).attr("id"),getRadiusAttributes(),"RADIUS");
            }
        }else{
            setAttributeSuggestionsForPCRFToGateway(getParentAttributesForRadius());
        }
    }

    function clearSelectTag() {
        var elSel = document.forms[0].packetType;
        for (var i = elSel.length - 1; i>=0; i--) {
            elSel.remove(i);
        }
    }

    var index = 0;

    function getAttributeMappingList() {
        index = parseInt(index);
        $("#attributeMappings").find("input").remove();
        for (var d in list) {
            var attrName = "attributeMappingData.mappings[" + index + "]";
            $("#attributeMappings").append($("#tempAttributeMappings").html());
            var NAME = "name";
            $("#attributeMappings").find("input:last-child").attr(NAME, attrName).attr("value", "");
            $("#attributeMappings").find("input:last-child").attr(NAME, attrName).attr("value", list[d]);
            index++;
        }
    }

    function validatePacketMappingRows(){
        var rowCount = 0;
        for(var d in list){
            rowCount = +rowCount + 1;
        }
        if(rowCount == 0){
            $("#generalError").addClass("bg-danger");
            $("#generalError").text('<s:text name="attribute.mapping.required" />');
            return false;
        }
        if(rowCount > '<s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@DEFAULT_MAPPING_COUNT" />'){
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
        return isValidKeys && validateNoneAttributeWithPolicyKey();
    }

    function validateNoneAttributeWithPolicyKey(){
        var conversationTypeVal = $("#conversationType").val();
        var isValidAttribute = true;
        if(conversationTypeVal == "<s:property value="@com.elitecore.corenetvertex.constants.ConversionType@PCC_TO_GATEWAY.name()" />" ){
            var attributeKeys = document.getElementsByClassName("attribute");
            var policyKeys = document.getElementsByClassName("policyKeySuggestions");
            for(var i = 0; i < attributeKeys.length; ++i){
                var element = attributeKeys[i];
                var val = element.value;
                if(isNullOrEmpty(val) == false && val.toLowerCase() == 'none') {
                    var policyKeyElement = policyKeys[i];
                    if (policyKeyElement.value != 'PCCRule') {
                        setError(policyKeyElement.id, "Only PCCRule key supported");
                        isValidAttribute = false;
                    }
                }
            }
        }
        return isValidAttribute;

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


    function getRadiusAttributes(){
        var attribute = ${dictionaryData};
        return attribute;
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
                console.log("success---> : " + (data));
                commonAutoCompleteBasedOnKeyValueForGivenId(id,data);
            }, error: function (data) {
                console.log("error---> : " + JSON.stringify(data));
            }
        });
    }

</script>