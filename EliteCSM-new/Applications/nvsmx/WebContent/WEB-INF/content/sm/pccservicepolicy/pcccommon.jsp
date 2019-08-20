<%@ page import="com.elitecore.corenetvertex.constants.PCRFKeyConstants" %>
<%@ page import="com.elitecore.corenetvertex.constants.PCRFKeyType" %>
<%@include file="/view/commons/general/AutoCompleteIncludes.jsp" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/expressionlibrary/validateMapping.js"></script>

<script type="text/javascript">

    $(function(){
        $("#ruleset").autocomplete();
        var dbFieldArray = [];
        <%  for(PCRFKeyConstants constant:PCRFKeyConstants.values(PCRFKeyType.RULE)){ %>
            dbFieldArray.push('<%=constant.getVal()%>');
        <% } %>
        var rulesetAutoCompleter = createModel(dbFieldArray);
        expresssionAutoComplete('ruleset',rulesetAutoCompleter);
    });

    var identityAttributesSuggestions = new Array();
    <%  for(PCRFKeyConstants pccAttribute : PCRFKeyConstants.values(PCRFKeyType.REQUEST)){ %>
            identityAttributesSuggestions.push('<%=pccAttribute.getVal()%>');
    <%  } %>

    $(function(){
        $(".identityAttributesSuggestions").focus(function () {
            $(this).autocomplete();
            var id= $(this).attr('id');
            commonAutoComplete(id,identityAttributesSuggestions);
        });
        enableDisablePkgWithUnknownUserAction();
    });

    function verifyCdrDrivers(){
        var policyCdrDriverId = $("#policyCdrDriverId").val();
        var chargingCdrDriverId = $("#chargingCdrDriverId").val();

        if(isNullOrEmpty(chargingCdrDriverId)){
            setError("chargingCdrDriverId", "Charging CDR is Required");
            return false;
        }

        if(chargingCdrDriverId==policyCdrDriverId){
            setError("chargingCdrDriverId", "Charging and Policy CDR cannot be same");
            return false;
        }else{
            clearErrorMessagesById("chargingCdrDriverId");
        }
        return true;
    }

    function enableDisablePkgWithUnknownUserAction(){

        var unknownUserAction = $("#unknownUserAction").val();
        var packageId = $("#unknownUserPkgId").val();
        if("ALLOW_UNKNOWN_USER"==unknownUserAction){
            $('#unknownUserPkgId').prop('disabled', false);
        }else{
            clearErrorMessagesById("unknownUserPkgId")
            $("#unknownUserPkgId option[value='']").prop('selected', true);
            $('#unknownUserPkgId').prop('disabled', 'disabled');
        }
    }

    function checkUnknownUserPackage(){
        var unknownUserAction = $("#unknownUserAction").val();
        if("ALLOW_UNKNOWN_USER"==unknownUserAction){
            var packageId = $("#unknownUserPkgId").val();
            if(isNullOrEmpty(packageId)){
                setError("unknownUserPkgId", "Unknown User Package is Required for action 'Allow Unknown User'");
                return false;
            }else{
                return true;
            }
        }else{
            clearErrorMessagesById("unknownUserPkgId");
            return true;
        }
    }

    function verifyUniquenessOnSubmit(fieldId,mode,entityId,className,pkgId,propertyName){
        var actionName="/validate/policydesigner/util/NameValidation/doValidation";
        var pkgNameVal = $("#"+fieldId).val();
        clearErrorMessagesById(fieldId);
        var response=false;
        $.ajax({
            type: "POST",
            url: contextPath+actionName,
            async:false,
            data : {
                "id" : entityId,
                "mode" : mode,
                "name" : pkgNameVal,
                "className" : className,
                "parentId" : pkgId,
                "property" : propertyName
            },
            success: function(data){
                if(data.messageCode == "1"){
                    setSuccess(fieldId);
                    response= true;
                }else{
                    setError(fieldId,data.message);
                    response=false;
                }
            },
            error: function(data){
                console.log(data);
            }
        });
        return response;
    }

    function validateRuleset(){
        var rulesetId = "ruleset";
        var value = $("#"+rulesetId).val();
        if(isNullOrEmpty(value)){
            return true;
        }

        var methodUrl = "${pageContext.request.contextPath}/sm/pccservicepolicy/pcc-service-policy/1/validateRuleset";
        clearErrorMessagesById(rulesetId);
        var isValid = false;
        $.ajax({
           type:"GET",
           url:methodUrl,
           async: false,
           data:{
               "rulesetValue" : value
           },
           success: function(data){
               if($.trim(data)=='VALID'){
                   setSuccess(rulesetId);
                   isValid = true;
               }else{
                   setError(rulesetId,"Invalid Ruleset");
               }
           },
           error: function(data){
               setError(rulesetId,"Invalid Ruleset");
           }
        });
        return isValid;
    }
</script>