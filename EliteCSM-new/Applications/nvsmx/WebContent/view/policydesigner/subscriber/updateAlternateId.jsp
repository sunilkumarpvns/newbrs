<s:form action="policydesigner/subscriber/Subscriber/updateAlternateIdentity" id="updateAlternateIdentityForm"
        method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-4"
        elementCssClass="col-xs-12 col-sm-8 col-lg-8" onsubmit="return validateUpdateAlternateIdentityForm();">
    <s:token/>
    <div class="modal" id="updateAlternateIdentityDialog" tabindex="-1" role="dialog" aria-labelledby="serviceDialog"
         aria-hidden="true">

        <div class="modal-dialog">

            <div class="modal-content">

                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close" >
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title set-title">
                        <s:text name="subscriber.update.alternate.id"/>
                    </h4>
                </div>
                <s:hidden id="subscriberId" name="subscriberIdentity"/>
                <s:hidden id="groups" name="groups"/>
                <div class="modal-body" style="padding-top: 15px !important; padding-bottom: 0 !important;">
                    <div class="row">
                        <div class="col-xs-12">
                            <s:textfield name="oldAlternateIdentity" id="oldAlternateIdentity" cssClass="form-control"
                                         key="subscriber.alternateid.old" readonly="true"/>
                        </div>
                    </div>
                </div>
                <div class="modal-body" style="padding-top: 15px !important; padding-bottom: 0 !important;">
                    <div class="row">
                        <div class="col-xs-12">
                            <s:textfield name="updatedAlternateIdentity" id="updatedAlternateIdentity"
                                         cssClass="form-control" key="subscriber.alternateid.new"/>
                        </div>
                    </div>
                </div>

                <div class="modal-footer">
                    <s:submit type="button" role="button" cssClass="btn btn-primary">
                        <span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/>
                    </s:submit>
                    <button type="button" class="btn btn-primary" data-dismiss="modal" id="btnCancel"><s:text name="button.cancel"/>
                    </button>
                </div>

            </div>
        </div>
    </div>
</s:form>
<script type="text/javascript">
    function validateUpdateAlternateIdentityForm() {
        clearErrorMessages();
        var updatedAlternateIdentity = $("#updatedAlternateIdentity").val();
        var oldAlternateIdentity = $("#oldAlternateIdentity").val();
        if (isNullOrEmpty(updatedAlternateIdentity)) {
            setError('updatedAlternateIdentity',
                '<s:text name="error.required.field"><s:param><s:text name="subscriber.alternateid.new"/></s:param></s:text>');
            return false;
        }
        if (updatedAlternateIdentity == oldAlternateIdentity) {
            setError('updatedAlternateIdentity','<s:text name="subscriber.oldalternateid.newalternateid"/>');
            return false;
        }
        return validateAlternateIdLength('updatedAlternateIdentity',updatedAlternateIdentity,'<s:text name="subscriber.alternateid.new"/>');
    }
</script>
