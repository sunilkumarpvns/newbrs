<div id="add-subscription">

    <s:if test="%{subscriber.status.equals(@com.elitecore.corenetvertex.constants.SubscriberStatus@DELETED.name()) || subscriber.status.equals(@com.elitecore.corenetvertex.constants.SubscriberStatus@INACTIVE.name()) }">
    <button type="button" class="btn btn-primary btn-sm" role="button" id="btnAddSubscription" data-toggle="modal" data-target="#notPermittedForDelete">
        </s:if>
        <s:elseif test="%{subscriber.getProfileExpiredHours() >= 0 }">
        <button type="button" class="btn btn-primary btn-sm" role="button" id="btnAddSubscription" data-toggle="modal" data-target="#notPermittedForProfileExpired">
            </s:elseif>
            <s:else>
            <button type="button" class="btn btn-primary btn-sm" role="button" id="btnAddSubscription" data-toggle="modal" data-target="#addSubscription">
                </s:else>
                <span class="glyphicon glyphicon-plus-sign" title="Add"></span> <s:text name="subscription"/>
            </button>

            <div class="modal fade" id="addSubscription" tabindex="-1" role="dialog" aria-labelledby="addSubscriptionLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <h4 class="modal-title" id="addSubscriptionLabel"><s:text name="subscription.subscribe.addon"></s:text> </h4>
                        </div>
                        <s:form action="policydesigner/subscriber/Subscriber/subscribeAddOn" id="subscribeForm" cssClass="form-vertical form-group-sm " labelCssClass="col-xs-4" elementCssClass="col-xs-8" validate="true" onsubmit="return validateSubscription();">
                            <s:token />
                            <div class="modal-body">
                                <div class="row">
                                    <div class="col-xs-11" >
                                        <s:select autofocus="autofocus" name="addon" id="selectedAddOn" key="subscription.addon" list="#{}"  listKey="id" listValue="name" cssClass="form-control" onchange="showDetails();" cssStyle="width:100%" >
                                            <s:optgroup label="Add On" list="#{}" listValue="name" listKey="id" />
                                            <s:optgroup label="Top-Up" list="#{}" listValue="name" listKey="id" />
                                            <s:optgroup label="BOD" list="#{}" listValue="name" listKey="id" />
                                        </s:select>
                                        <s:hidden name="pkgType" id="pkgType"/>
                                        <s:hidden name="selectedAddOn" id="selectedAddOnId"/>

                                    </div>
                                    <div class="col-xs-1" style="padding-top:32px;padding-left:0px" id="reloadAddOn">
                                        <button type="button" class="btn btn-default btn-xs" role="button" id="btnRestore" onclick="reload();">
                                            <span class="glyphicon glyphicon-refresh" title="Refresh" onclick=""></span>
                                        </button>
                                    </div>
                                    <div class="col-xs-1" style="padding-top:32px;padding-left:0px;display:none;" id="progressAddOn" >
                                        <img src='${pageContext.request.contextPath}/images/progress.gif' style="width:75%;" >
                                    </div>
                                </div>
                                <s:datepicker key="subscription.start.date" name="subscriptionStartDate" id="subscriptionStartDate" parentTheme="bootstrap" changeMonth="true" changeYear="true"
                                               timepickerSecondText="00" cssClass="form-control" showAnim="slideDown" duration="fast" showOn="focus" placeholder="DD-MON-YYYY HH:MM:SS"
                                               displayFormat="dd-M-yy" timepicker="true" timepickerFormat="HH:mm:ss" readonly="true" />
                                <div class="row">
                                    <div class="col-xs-12">
                                        <s:textfield name="priority" id="priority" cssClass="form-control" key="subscription.priority" onkeypress="isNaturalInteger(event);" maxlength="5" value="100" />
                                    </div>
                                </div>
                                <hr>
                                <div id="addOnDetailInfo">
                                    <s:label for="type" key="Type" value="%{addOns[0].type}" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8"></s:label>
                                    <s:label for="description" key="getText('subscription.description')" value="%{addOns[0].description}" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8"></s:label>
                                    <s:set var="priceTag">
                                        <s:property value="getText('subscription.price')"/> <s:property value="getText('opening.braces')"/><s:property value="%{currency}"/><s:property value="getText('closing.braces')"/>
                                    </s:set>
                                    <s:label for="price" key="priceTag" value="%{addOns[0].price}" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8"></s:label>
                                    <s:label for="validity" key="getText('subscription.validity')" value="%{addOns[0].validityPeriod}  %{addOns[0].validityPeriodUnit}" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8"></s:label>
                                    <s:select name="updateAction" key="Update Action" cssClass="form-control" list="@com.elitecore.nvsmx.ws.util.UpdateActions@values()" listKey="val()" listValue="label()" id="updateAction" labelCssClass="col-xs-4" elementCssClass="col-xs-8" />
                                </div>
                                <hr>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="clearDialog();"><s:text name="subscription.close"></s:text> </button>
                                <s:submit cssClass="btn btn-primary" name="Subscribe" value="Subscribe" type="button"></s:submit>
                            </div>
                        </s:form>
                    </div>
                </div>
            </div>
</div>

<script>
    function validateSubscription(){
        clearErrorMessagesById('selectedAddOn');
        var addOnSubscriptionId = $('#selectedAddOn').val();
        if (isNullOrEmpty(addOnSubscriptionId)) {
            setError('selectedAddOn', "AddOn is required for subscription");
            return false;
        }
        var priority = $("#priority").val();
        if(isNullOrEmpty(priority) == false){
            if(priority < 1 || priority > 65535){
                setError('priority','Priority Value Must Be Between 1 to 65535');
                return false;
            }
        }
        var idAndType = addOnSubscriptionId.split("|");
        var addOnId = idAndType[0];
        console.log("addOnId "+addOnId)
        $('#selectedAddOnId').val(addOnId);
        return true;
    }
</script>