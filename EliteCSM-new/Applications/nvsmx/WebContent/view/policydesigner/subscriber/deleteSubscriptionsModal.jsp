<div class="modal fade" id="deleteSubscription" tabindex="-1" role="dialog" aria-labelledby="deleteSubscriptionLabel" aria-hidden="true" >
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="deleteSubscriptionLabel"><s:text name="subscription.unsubscribe"></s:text> </h4>
            </div>
            <s:form action="captcha/policydesigner/subscriber/Subscriber/unsubscribeAddOn" id="unsubscribeForm"
                    cssClass="form-vertical form-group-sm " labelCssClass="col-xs-4" elementCssClass="col-xs-8" onsubmit="return validateRemark()" >
                <div class="modal-body">
                    <s:token />
                    <s:label key="getText('enter.captcha.code')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8"></s:label>
                    <jCaptcha:image label=""/>
                    <s:textfield label="Remark" id="remark" name="remark" cssClass="form-control"/>
                    <hr>
                    <div id="addOnDetailInfo">
                        <input type="hidden" id="addonSubscriptionId" name="addonSubscriptionId" />
                        <input type="hidden" id="addonId" name="addonId" />
                        <input type="hidden" id="unSubscriptionPkgType" name="unSubscriptionPkgType" />
                        <s:label for="unsubscribeDescription" key="getText('subscription.description')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8"></s:label>
                        <s:select name="updateAction" key="Update Action" cssClass="form-control" list="@com.elitecore.nvsmx.ws.util.UpdateActions@values()" listKey="val()" listValue="label()" id="updateAction" labelCssClass="col-xs-4" elementCssClass="col-xs-8" />
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" data-dismiss="modal"><s:text name="subscription.close"></s:text> </button>
                    <s:submit cssClass="btn btn-primary" name="Subscribe" value="Unsubscribe" ></s:submit>
                </div>
            </s:form>
        </div>
    </div>
</div>