<%@taglib uri="/struts-tags/ec" prefix="s" %>
<s:form namespace="/pd/topupnotification" action="topup-notification" id="topUpNotificationForm" method="post" cssClass="form-horizontal"
        labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9">
    <s:token/>
    <s:hidden name="_method" value="post" id="methodName"/>
    <s:hidden id="topUpNotificationId"/>
    <div class="modal col-xs-12" id="topUpNotificationDialog" tabindex="-1" role="dialog"
         aria-labelledby="topUpNotificationDialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="clearModal();">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title set-title">
                        <s:text name="data.topup.notification"/>
                    </h4>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-xs-12 col-sm-12">
                            <s:hidden name="dataTopUpId" value="%{id}"/>
                            <s:select name="emailTemplateId"
                                      key="data.topup.notification.email.template" list="emailTemplates" listKey="id" headerKey="" headerValue="SELECT"
                                      listValue="name" cssClass="form-control focusElement" id="selectedEmailTemplateId"  tabindex="1"/>
                            <s:select name="smsTemplateId" key="data.topup.notification.sms.template" headerKey="" headerValue="SELECT"
                                      list="smsTemplates" listKey="id" listValue="name"
                                      cssClass="form-control"  id="selectedSMSTemplateId" tabindex="2"/>
                            <s:textfield name="threshold" id="threshold" maxLength="4"  onkeypress="return isNaturalInteger(event);" key="data.topup.notification.threshold" cssClass="form-control" tabindex="3"/>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-sm btn-primary" type="button" role="button" onclick="formSubmit();"  tabindex="4">
                      <span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/>
                    </button>
                    <button type="button" class="btn btn-default" data-dismiss="modal" id="btnCancel" tabindex="5"
                            onclick="clearModal();"><s:text name="button.cancel"/></button>
                </div>
            </div>
        </div>
    </div>
</s:form>
<script>

    function clearModal(){
        clearErrorMessages();
        $('#topUpNotificationDialog').modal('hide');

    }

    function updateNotification(emailTemplateId,smsTemplateId,threshold,notificationId){
        $("#selectedEmailTemplateId").val(emailTemplateId);
        $("#selectedSMSTemplateId").val(smsTemplateId);
        $("#threshold").val(threshold);
        addRequired(document.getElementById("threshold"));
        $('#topUpNotificationDialog').modal('show');
        $('#methodName').val("put");
        $('#topUpNotificationId').val(notificationId);
        clearErrorMessages();
    }

    function validateNotificationConfiguration() {
        var emailTemplate = $("#selectedEmailTemplateId").val();
        var smsTemplate = $("#selectedSMSTemplateId").val();
        var notificationId=$('#topUpNotificationId').val();
        clearErrorMessages();
        if (isNullOrEmpty(emailTemplate) && isNullOrEmpty(smsTemplate)) {
            setError("selectedEmailTemplateId", "Email Or SMS Template is mandatory");
            setError("selectedSMSTemplateId", "Email Or SMS Template is mandatory");
            return false;
        }
        var thresholdValue = $("#threshold").val();
        if (isNullOrEmpty(thresholdValue)) {
            setError("threshold", 'Value Required');
            return false;
        }
        if (isNaN(thresholdValue)) {
            setError("threshold", 'threshold must be Numeric ');
            return false;
        }
        if (thresholdValue < 1  || thresholdValue > 100) {
            setError("threshold", 'Value of threshold must be between 1 and 100');
            return false;
        }
        return isNotificationAlreadyExist(thresholdValue,notificationId);
    }

    function isNotificationAlreadyExist(thresholdValue,notificationId){
        var isValidMapping = true;
        var errorMessage =  '<s:text name="notification.already.exist"><s:param>'+thresholdValue+'</s:param></s:text>';
        $("#notificationTable tbody tr").each(function () {
            var existingNotificationId = $(this).children("td").eq(0).children().eq(1).val();
            if (isNullOrEmpty(notificationId) == false && notificationId == existingNotificationId) {
                return;
            }
            var configuredThresholdValue = $(this).children("td").eq(2).text();
            configuredThresholdValue = $.trim(configuredThresholdValue);

            if (configuredThresholdValue == thresholdValue) {
                var existingData = $(this).children("td").eq(0).children().eq(2).val();
                if ( ($('#methodName').val() == 'put' && notificationId != existingData) || $('#methodName').val() != 'put') {
                    setError("threshold",errorMessage);
                    isValidMapping = false;
                    return false;
                }
            }

        });
        return isValidMapping;
    }


    function formSubmit() {
        var isValidForm = validateNotificationConfiguration();
        if(isValidForm == false){
            return false;
        }

        if ($('#methodName').val() == "post") {
            document.getElementById("topUpNotificationForm").action = "${pageContext.request.contextPath}/pd/topupnotification/topup-notification";
            document.getElementById("topUpNotificationForm").submit();
        } else if ($('#methodName').val() == "put") {
            document.getElementById("topUpNotificationForm").action = "${pageContext.request.contextPath}/pd/topupnotification/topup-notification/" + $('#topUpNotificationId').val();
            document.getElementById("topUpNotificationForm").submit();
        }
    }

    function addRequired(element) {
        var curElement = $("#lbl_".concat(element.id));
        if ($("#lbl_".concat(element.id)).length) {
                if(curElement.find("span").hasClass("required") == false) {
                    curElement.html(curElement.html().concat(" <span class=\"required\">*</span>"));
                }
        }
    }

    function setError(elementid, errorText) {
        var curElement = $("#".concat(elementid));
        var parentElement = curElement.parent();
        if(parentElement.parent().hasClass("has-error has-feedback") == false){
            parentElement.parent().addClass("has-error has-feedback");
            parentElement.append("<span class=\"glyphicon glyphicon-remove form-control-feedback removeOnReset\"></span>");
            parentElement.append("<span class=\"help-block alert-danger removeOnReset\">".concat(errorText).concat("</span>"));
        }
    }

    function clearErrorMessages(){
        $(".generalError").removeClass("bg-danger");
        $(".generalError").text("");
        $( ".has-error" ).removeClass("has-error");
        $( ".has-success" ).removeClass("has-success");
        $( ".has-feedback" ).removeClass("has-feedback");
        $( ".alert" ).remove();	$(".nv-input-error").removeClass("nv-input-error");
        $(".glyphicon-remove").remove();
        $(".alert-danger").remove();
        $(".removeOnReset").remove();
    }
</script>