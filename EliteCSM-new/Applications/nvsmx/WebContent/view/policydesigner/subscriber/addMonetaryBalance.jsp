<%@ page import="com.elitecore.corenetvertex.constants.CommonConstants" %>
<div id="add-subscription">

    <s:if test="%{subscriber.status.equals(@com.elitecore.corenetvertex.constants.SubscriberStatus@DELETED.name()) || subscriber.status.equals(@com.elitecore.corenetvertex.constants.SubscriberStatus@INACTIVE.name()) }">
    <button type="button" class="btn btn-primary btn-sm" role="button" id="btnAddMonetaryBalance" data-toggle="modal"
            data-target="#notPermittedForDelete">
    </s:if>
    <s:elseif test="%{subscriber.getProfileExpiredHours() >= 0 }">
    <button type="button" class="btn btn-primary btn-sm" role="button" id="btnAddMonetaryBalance" data-toggle="modal"
            data-target="#notPermittedForProfileExpired">
    </s:elseif>
    <s:else>
    <button type="button" class="btn btn-primary btn-sm" role="button" id="btnAddMonetaryBalance"
            data-toggle="modal" data-target="#addMonetaryBalance" onclick="$('#addBalanceForm')[0].reset();">
    </s:else>
        <span class="glyphicon glyphicon-plus-sign" title="Add"></span> <s:text name="monetary.balance"/>
    </button>

    <div class="modal fade" id="addMonetaryBalance" tabindex="-1" role="dialog"
         aria-labelledby="addMonetaryBalanceLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title" id="addBalanceLabel"><s:text
                            name="monetary.add.balance"></s:text></h4>
                </div>
                <s:form action="policydesigner/subscriber/Subscriber/addMonetaryBalance" id="addBalanceForm"
                        cssClass="form-vertical form-group-sm " labelCssClass="col-xs-4"
                        elementCssClass="col-xs-8" validate="true" onsubmit="return validateMonetaryBalance()">
                    <s:token/>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-xs-12">
                                <s:select autofocus="autofocus" name="service" id="selectedService"
                                          key="balance.service" list="services" listKey="id" listValue="name"
                                          headerKey="" headerValue="All Services"
                                          cssClass="form-control">
                                </s:select>
                                <s:set var="balanceTag">
                                    <s:property value="getText('monetary.balance')"/> <s:property value="getText('opening.braces')"/><s:property value="%{currency}"/><s:property value="getText('closing.braces')"/>
                                </s:set>
                                <s:textfield name="balance" key="balanceTag" id="balance" cssClass="form-control"
                                             maxlength="100" placeholder="Amount"/>
                                <s:datepicker key="balance.start.date" name="balanceStartDate"
                                               id="balanceStartDate" parentTheme="bootstrap" changeMonth="true"
                                               changeYear="true"
                                               timepickerSecondText="00" cssClass="form-control" showAnim="slideDown"
                                               duration="fast" showOn="focus" placeholder="DD-MON-YYYY HH:MM:SS"
                                               displayFormat="dd-M-yy" timepicker="true" timepickerFormat="HH:mm:ss"
                                               readonly="true"/>
                                <s:datepicker key="balance.expiry.date" name="balanceExpiryDate"
                                               id="balanceEndDate" parentTheme="bootstrap" changeMonth="true"
                                               changeYear="true"
                                               timepickerSecondText="00" cssClass="form-control" showAnim="slideDown"
                                               duration="fast" showOn="focus" placeholder="DD-MON-YYYY HH:MM:SS"
                                               displayFormat="dd-M-yy" timepicker="true" timepickerFormat="HH:mm:ss"
                                               readonly="true"/>
                                <s:textarea name="remark" key="balance.remark" id="balanceRemark"
                                            cssClass="form-control" rows="2"
                                            value="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDefaultDescription(request)}"
                                            maxlength="2000"/>
                                <s:select name="updateAction" key="balance.update.action" cssClass="form-control" list="@com.elitecore.nvsmx.ws.util.UpdateActions@values()" listKey="val()" listValue="label()" id="updateAction" />
                                <hr>
                                <hr>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" data-dismiss="modal"
                                onclick="clearDialog();"><s:text name="balance.close"></s:text></button>
                        <s:submit cssClass="btn btn-primary" name="AddBalance" key="balance.add"
                                  type="button"></s:submit>
                    </div>
                </s:form>
            </div>
        </div>
    </div>
</div>

<script>
    function validateMonetaryBalance() {
        clearErrorMessages(addBalanceForm);
        if($("#balance").val()=="" || isNaN($("#balance").val())){
            setError('balance','<s:text name="error.balance.incorrect"/>');
            return false;
        }

        if($("#balance").val()<=0){
            setError('balance','<s:text name="error.balance.non.negative"/>');
            return false;
        }

        if($("#balance").val() > <s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@MAX_MONETARY_VALUE" />){
            setError('balance',"Max allowed value for balance is <s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@MAX_MONETARY_VALUE" />");
            return false;
        }

        if(isNullOrEmpty($("#selectedService").val())){
            if($("#balanceEndDate").datepicker("getDate")!=null){
                if($("#balanceStartDate").datepicker("getDate") == null){
                    setError('balanceStartDate','<s:text name='error.valueRequired'/>');
                    return false;
                }
            }

            if($("#balanceStartDate").datepicker("getDate")!=null){
                if($("#balanceEndDate").datepicker("getDate") == null){
                    setError('balanceEndDate','<s:text name='error.valueRequired'/>');
                    return false;
                }
            }

        }else{
            if($("#balanceStartDate").datepicker("getDate") == null){
                setError('balanceStartDate',"<s:text name='error.valueRequired'/>");
                return false;
            }
            if($("#balanceEndDate").datepicker("getDate")==null){
                setError('balanceEndDate',"<s:text name='error.valueRequired'/>");
                return false;
            }
        }


        if($("#balanceEndDate").datepicker("getDate")!=null){
            if($("#balanceEndDate").datepicker("getDate").getTime()<new Date().getTime()){
                setError('balanceEndDate',"<s:text name='error.balance.end.date.past'/>");
                return false;
            }

            if($("#balanceStartDate").datepicker("getDate")!=null){
                if($("#balanceEndDate").datepicker("getDate").getTime()<$("#balanceStartDate").datepicker("getDate").getTime()){
                    setError('balanceEndDate',"<s:text name='error.balance.end.date.invalid'/>");
                    return false;
                }
            }
        }

        if(isNullOrEmpty($("#balanceRemark").val())){
            setError('balanceRemark',"<s:text name='error.balance.remark'/>");
            return false;
        }
        return true;
    }
</script>