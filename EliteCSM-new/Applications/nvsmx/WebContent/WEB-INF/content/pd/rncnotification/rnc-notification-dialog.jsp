<script type="text/javascript">
    function clearNotificationDialog() {
        clearErrorMessages(rncNotificationForm);
        $(".generalError").removeClass("bg-danger");
        $(".generalError").text("");
        $("#emailTemplateId").val("");
        $("#smsTemplateId").val("");
        $("#thresholdId").val("");
    }

    $(document).ready(function(){
        clearDialog();
    });

    function isUniqueNotification()
    {
        var rncNotificationId = $("#rncNotificationId").val();
        var tableData = $('#rncNotificationData').DataTable().rows().data();
        var rateCardId = $('#rateCardId').val();
        var thresholdId = Number($('#thresholdId').val());
        var checkUniqueness = true;

        tableData.each(function (value, index) {

            if (rncNotificationId == value.id) {
                return true;
            }

            if(checkUniqueness && value.rateCardData.id == rateCardId && value.threshold == thresholdId) {
                checkUniqueness = false;
                return false;
            }
        })

        if(!checkUniqueness) {
            $(".generalError").addClass("bg-danger");
            $(".generalError").text("Notification already configured for threshold: "+thresholdId);
        }

        return checkUniqueness;
    }

    function submitForm(){
        if(!isUniqueNotification()) {
            return false;
        }

        if($("#emailTemplateId").val() == "" && $("#smsTemplateId").val() == ""){
            setError('emailTemplateId', "Email Template or Sms Template is required");
            setError('smsTemplateId', "Email Template or Sms Template is required");
            return false;
        }

        $("#rncNotificationForm").submit();
    }


</script>

<s:form namespace="/pd/rncnotification" action="rnc-notification" id="rncNotificationForm" method="post" cssClass="form-horizontal" validate="true"  labelCssClass="col-xs-12 col-sm-4 col-lg-4" elementCssClass="col-xs-12 col-sm-8 col-lg-8">
  <s:token />
    <s:hidden name="_method" id="method" />
<div class="modal" id="rncNotificationDialog" tabindex="-1" role="dialog" aria-labelledby="serviceDialog" aria-hidden="true"  >

  <div class="modal-dialog" >

    <div class="modal-content" >

      <div class="modal-header" >
        <button type="button" class="close" data-dismiss="modal"
                aria-label="Close" onclick="clearDialog()">
          <span aria-hidden="true">&times;</span>
        </button>
        <h4 class="modal-title set-title" id="serviceDialogTitle">
          <s:text name="rnc.threshold.notification"/>
        </h4>
      </div>

      <div class="modal-body" style="padding-top: 15px !important; padding-bottom: 0px !important;" >
            <div class="row">
               <div class="col-xs-12">
				 <s:select 
			            id="rateCardId"
			            name="rateCardId"
                        key="rncnotification.ratecard"
                        cssClass="form-control"
                        listKey="id"
                        listValue="name"
                        list="rateCardList"/>
				</div>
            </div>
            <div class="row">
              <div class="col-xs-12">
                  <s:select
                          id="emailTemplateId"
                          name="emailTemplateId"
                          key="rncnotification.email.template"
                          cssClass="form-control"
                          listKey="id"
                          listValue="name"
                          list="emailTemplateList" headerValue="--Select--" headerKey=""/>
              </div>
            </div>
            <div class="row">
              <div class="col-xs-12">
                  <s:select
                          id="smsTemplateId"
                          name="smsTemplateId"
                          key="rncnotification.sms.template"
                          cssClass="form-control"
                          listKey="id"
                          listValue="name"
                          list="smsTemplateList" headerValue="--Select--" headerKey=""/>
              </div>
            </div>
            <div class="row">
              <div class="col-xs-12">
                  <s:textfield name="threshold" id="thresholdId" cssClass="form-control" key="rncnotification.threshold" onkeypress="return isNaturalInteger(event);" />
              </div>
          </div>
            <s:hidden name="rncPackageId" value="%{id}" />
      </div>
        <div class="col-xs-12">
            <div class="col-xs-12 generalError" ></div>
        </div>
      <div class="modal-footer">
        <button class="btn btn-sm btn-primary" type="button" onclick="submitForm()"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
        <button type="button" class="btn btn-primary" data-dismiss="modal"id="btnCancel" onclick="clearNotificationDialog()">Cancel</button>
      </div>

    </div>
  </div>
</div>
</s:form>
