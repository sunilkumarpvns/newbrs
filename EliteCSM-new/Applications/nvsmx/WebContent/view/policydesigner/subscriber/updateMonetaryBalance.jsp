<style>
    .mrgnTop5Px{
        margin-top: 5px;
    }
</style>
<div id="add-subscription">
    <div class="modal fade" id="updateMonetaryBalance" tabindex="-1" role="dialog"
         aria-labelledby="updateMonetaryBalanceLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title" id="updateBalanceLabel"><s:text
                            name="monetary.update.balance"></s:text></h4>
                </div>
                <s:form action="policydesigner/subscriber/Subscriber/updateMonetaryBalance" id="updateBalanceForm"
                        cssClass="form-vertical form-group-sm " labelCssClass="col-xs-4"
                        elementCssClass="col-xs-8" validate="true" onsubmit="return validateUpdateMpnitory();">
                    <s:token/>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-xs-12">
                                <s:select autofocus="autofocus" name="service" id="selectedService1"
                                          key="balance.service" list="services" listKey="id" listValue="name"
                                          headerKey="" headerValue="All Services"
                                          cssClass="form-control" disabled = "true">
                                </s:select>
                                <s:hidden name="selectedBalanceId" id="selectedBalanceId"/>
                                <s:hidden name="creditLimit" id="creditLimit"/>
                                <s:select name="balanceOperation" id="balanceOperation"
                                          key="balance.operation" list="@com.elitecore.nvsmx.ws.subscription.blmanager.OperationType@values()"
                                          listKey="name" listValue="displayName"
                                          cssClass="form-control" onchange="showUpdatedBalance()">
                                </s:select>
                                <s:set var="balanceTag">
                                    <s:property value="getText('monetary.balance')"/> <s:property value="getText('opening.braces')"/><s:property value="%{currency}"/><s:property value="getText('closing.braces')"/>
                                </s:set>
                                <s:textfield name="balance" onblur="showUpdatedBalance()" key="balanceTag" id="updateBalance" cssClass="form-control"
                                             maxlength="100" placeholder="Amount"/>
                                <s:textarea name="remark" key="balance.remark" id="balanceRemark1"
                                            cssClass="form-control" rows="2"
                                            value="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDefaultDescription(request)}"
                                            maxlength="2000" row="2" cssStyle="margin-bottom:2px;"/>
                                <div class="row">
                                    <div class="col-xs-12 bottom-space">
                                        <div class="col-xs-6">
                                            <div class="row customized-row-margin">
                                                <strong class="strongTitle" style="padding-right: 1px"><s:text name="monetary.available.balance"/></strong>
                                                <s:textfield name="availableBalance" id="availableBalance"
                                                             cssClass="form-control mrgnTop5Px"
                                                             maxlength="100" disabled="true"/>
                                            </div>
                                        </div>
                                        <div class="col-xs-6">
                                            <div class="row customized-row-margin" style="padding-left: 1px">
                                                <strong class="strongTitle"><s:text name="monetary.updated.balance"/></strong>
                                                <s:textfield name="updatedBalance" id="updatedBalance"
                                                             cssClass="form-control mrgnTop5Px"
                                                             maxlength="100" disabled="true"/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <s:select name="updateAction" key="balance.update.action" cssClass="form-control" list="@com.elitecore.nvsmx.ws.util.UpdateActions@values()" listKey="val()" listValue="label()" id="updateAction" />
                                <hr>
                                <hr>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" data-dismiss="modal"
                                onclick="clearDialog();"><s:text name="balance.close"></s:text></button>
                        <s:submit cssClass="btn btn-primary" name="UpdateBalance" key="balance.update"
                                  type="button"></s:submit>
                    </div>
                </s:form>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="warningOverUsage" tabindex="-1" role="dialog" aria-labelledby="warningOverUsageLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="warningOverUsageLabel" >Warning!!!</h4>
            </div>
            <div class="modal-body">
                <div>
                    <s:text name="warning.over.usage"/>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" data-dismiss="modal" ><s:text name="button.no"></s:text> </button>
                <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="submitForm();" ><s:text name="button.yes"></s:text> </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="warningNegativeBalance" tabindex="-1" role="dialog" aria-labelledby="warningNegativeBalanceLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="warningNegativeBalanceLabel" >Warning!!!</h4>
            </div>
            <div class="modal-body">
                <div>
                    <s:text name="warning.negative.balance"/>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" data-dismiss="modal" ><s:text name="button.no"></s:text> </button>
                <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="submitForm();" ><s:text name="button.yes"></s:text> </button>
            </div>
        </div>
    </div>
</div>

<script>
    function submitForm(){
        document.forms["updateBalanceForm"].submit();

    }
    function validateUpdateMpnitory() {
        clearErrorMessages(updateBalanceForm);

        if ($("#updateBalance").val() == "" || isNaN($("#updateBalance").val())) {
            setError('updateBalance', "<s:text name='error.balance.incorrect'/>");
            return false;
        }

        if ($("#updateBalance").val() <= 0) {
            setError('updateBalance', "<s:text name='error.balance.non.negative'/>");
            return false;
        }

        if ($("#updatedBalance").val() > <s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@MAX_MONETARY_VALUE" />) {
            setError('updateBalance', "Updated balance should not exceed <s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@MAX_MONETARY_VALUE" />");
            return false;
        }

        if (isNullOrEmpty($("#balanceRemark1").val())) {
            setError('balanceRemark1', "<s:text name='error.balance.remark'/>");
            return false;
        }

        if ($("#balanceOperation").val() == "<s:property value="@com.elitecore.nvsmx.ws.subscription.blmanager.OperationType@DEBIT.getName()"/>"
            && Number.parseFloat($("#updatedBalance").val()) + Number.parseFloat($("#creditLimit").val()) < 0) {
            // return $("#creditLimit").val()>0
            //     ?confirm("Subscriber's credit usage exceeds their credit limit after this operation. Press OK to continue")
            //     :confirm("Subscriber's available balance is negative after this operation. Press OK to continue");
            $("#creditLimit").val()>0
                ?$("#warningOverUsage").modal('show')
                :$("#warningNegativeBalance").modal('show');
            return false;
        }

        return true;
    }
</script>