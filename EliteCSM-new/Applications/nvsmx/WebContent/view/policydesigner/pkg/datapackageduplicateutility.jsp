<script type="text/javascript">
    function validateDuplicate() {
        var isValidName = verifyUniquenessOnSubmit('duplicateEntityName', 'create', '', 'com.elitecore.corenetvertex.pkg.PkgData', '', '');
        var isValidPCCRule = true;
        var pccRuleNames = new Array();
        var pccRuleMonitoringKey = new Array();
        $("input[name='pccRuleIds']").each(function(){
            var nameElem = $("#"+$(this).val()+"-name");
            var monitoringkeyElem = $("#"+$(this).val()+"-monitoringKey");

            var isValidNamePCCRuleName = true;
            var isValidNamePCCRuleMonitoringKey = true;
            clearErrorMessagesById(nameElem.attr("id"));
            clearErrorMessagesById(monitoringkeyElem.attr("id"));
            if(pccRuleNames.indexOf(nameElem.val()) != -1){
                setError(nameElem.attr("id"), "Duplicate name found");
                isValidNamePCCRuleName = false;
            } else {
                isValidNamePCCRuleName = verifyUniquenessOnSubmit(nameElem.attr("id"),'create','','com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData','','');
            }
            if(pccRuleMonitoringKey.indexOf(monitoringkeyElem.val()) != -1){
                setError(monitoringkeyElem.attr("id"), "Duplicate monitoring key found");
                isValidNamePCCRuleMonitoringKey = false;
            } else {
                isValidNamePCCRuleMonitoringKey = verifyUniquenessOnSubmit(monitoringkeyElem.attr("id"),'create','','com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData','','monitoringKey');
            }

            if(!isValidNamePCCRuleName || !isValidNamePCCRuleMonitoringKey){
                isValidPCCRule = false;
                return false;
            } else {
                pccRuleNames.push(nameElem.val());
                pccRuleMonitoringKey.push(monitoringkeyElem.val());
            }
        });
        if (isValidName && isValidPCCRule) {
          document.forms['dataPackageCloningForm'].submit();
        }
        return false;
    }

    function clearErrorMessages(form) {
        $(".has-error").removeClass("has-error");
        $(".has-success").removeClass("has-success");
        $(".has-feedback").removeClass("has-feedback");
        $(".alert").remove();
        $(".nv-input-error").removeClass("nv-input-error");
        $(".glyphicon-remove").remove();
        $(".alert-danger").remove();
        $(".removeOnReset").remove();
    }

    function setError(elementid, errorText) {
        var curElement = $("#".concat(elementid));
        var parentElement = curElement.parent();
        if (parentElement.parent().hasClass("has-error has-feedback") == false) {
            parentElement.parent().addClass("has-error has-feedback");
            parentElement.append("<span class=\"glyphicon glyphicon-remove form-control-feedback removeOnReset\"></span>");
            parentElement.append("<span class=\"help-block alert-danger removeOnReset\">".concat(errorText).concat("</span>"));
        }
    }

    function setSuccess(elementid) {
        var curElement = $("#".concat(elementid));
        var parentElement = curElement.parent();
        parentElement.addClass("has-success has-feedback");
        parentElement.append("<span class=\"glyphicon glyphicon-ok form-control-feedback removeOnReset\"></span>");
    }

    function clearDialog() {
        clearErrorMessages(dataPackageCloningForm);
    }
</script>