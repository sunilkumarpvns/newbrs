<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<s:form action="policydesigner/subscriber/Subscriber/addAlternateId" id="addAlternateIdForm" method="post"
        cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-4"
        elementCssClass="col-xs-12 col-sm-8 col-lg-8"
        onsubmit="return validateAddAlternateIdForm();">
    <s:token/>
    <div class="modal" id="addAlternateIdModal" tabindex="-1" role="dialog" aria-labelledby="serviceDialog"
         aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title set-title" id="serviceDialogTitle">
                          <s:text name="subscriber.add.alternate.id"/>
                    </h4>
                </div>
                <div class="modal-body" style="padding-top: 15px !important; padding-bottom: 0px !important;">
                    <s:hidden id="subscriberId" name="subscriberIdentity"/>
                    <s:hidden id="groups" name="groups"/>
                    <div class="row">
                        <div class="col-xs-12">
                            <s:textfield name="alternateId" id="alternateId" cssClass="form-control"
                                         key="subscriber.alternate.id"/>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <div class="col-xs-12">
                        <button type="button" class="btn btn-primary" data-dismiss="modal" id="btnCancel" >
                            <s:text name="subscription.close"/>
                        </button>
                        <button class="btn btn-primary btn-sm" type="submit" id="btnUpdate">
                            <span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.add"/>
                        </button>


                    </div>
                </div>
            </div>
        </div>
    </div>
</s:form>
<script type="text/javascript">
    function validateAddAlternateIdForm() {
        var alternateId = $('#alternateId').val();
        if (isNullOrEmpty(alternateId)) {
            setError('alternateId', '<s:text name="error.required.field"><s:param><s:text name="subscriber.alternate.id"/></s:param></s:text>');
            return false;
        }
        return validateAlternateIdLength('alternateId',alternateId,'<s:text name="subscriber.alternate.id"/>');
    }
</script>