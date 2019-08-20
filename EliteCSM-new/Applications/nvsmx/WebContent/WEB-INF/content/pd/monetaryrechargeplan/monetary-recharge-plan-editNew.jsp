<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="plan.create" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/monetaryrechargeplan" id="monetaryrechargeplan" action="monetary-recharge-plan" method="post" cssClass="form-horizontal" validate="true" validator="validateForm()" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9">
            <s:token/>
            <s:set var="priceTag">
                <s:property value="getText('plan.price')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
            </s:set>
            <s:set var="amountTag">
                <s:property value="getText('plan.amount')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
            </s:set>
            <div class="row">
                    <div class="col-xs-6">
                        <s:textfield name="name" key="plan.name" id="name" cssClass="form-control focusElement" maxlength="50" tabindex="1"/>
                        <s:textarea name="description" key="plan.description" cssClass="form-control" maxlength="2000" tabindex="2" value="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDefaultDescription(request)}"/>
                        <s:select name="groups" id="groupNames" key="plan.groups" cssClass="form-control select2" list="#session.staffBelongingGroups" multiple="true" listKey="id" listValue="name" cssStyle="width:100%" tabindex="3"/>
                        <s:select name="status" key="plan.status" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.ResourceStatus@values()" maxlength="50" tabindex="4" />
                    </div>
                    <div class="col-xs-6">
                        <s:textfield name="price" key="priceTag" id="price" cssClass="form-control" maxlength="16"  tabindex="5"/>
                        <s:textfield name="amount" key="amountTag" id="amount" cssClass="form-control" maxlength="16"  tabindex="6"/>
                        <s:textfield name="validity" key="plan.validity" id="validity" cssClass="form-control" type="number" tabindex="7"/>
                        <s:select name="validityPeriodUnit" key="plan.validity.period.unit" cssClass="form-control"
                                  list="@com.elitecore.corenetvertex.constants.ValidityPeriodUnit@values()"
                                  id="validityPeriodUnit" listKey="name()" listValue="displayValue" tabindex="8"/>
                    </div>

                <div class="row">
                    <div class="col-xs-12" align="center">
                        <button type="submit" class="btn btn-primary btn-sm" role="submit" formaction="${pageContext.request.contextPath}/pd/monetaryrechargeplan/monetary-recharge-plan/${id}" tabindex="9">
                            <span class="glyphicon glyphicon-floppy-disk"></span>
                            <s:text name="button.save" />
                        </button>
                        <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/monetaryrechargeplan/monetary-recharge-plan/${id}'" tabindex="10">
                            <span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;
                            <s:text name="button.list" />
                        </button>
                    </div>
                </div>
            </div>
        </s:form>
    </div>
</div>

<script>
    $(function(){
        $(".select2").select2();
    })
    function validateForm() {
        var amount = $('#amount').val();
        var validity = $('#validity').val();

        if(verifyUniquenessForName('create', '') && verifyPrice() && verifyUniquenessForPrice('create','') && verifyAmount() && verifyAmountValidityCombo()) {
            setDefaults();
            return true;
        }
        return false;
    }
</script>

<%@include file="monetary-recharge-plan-utility.jsp"%>