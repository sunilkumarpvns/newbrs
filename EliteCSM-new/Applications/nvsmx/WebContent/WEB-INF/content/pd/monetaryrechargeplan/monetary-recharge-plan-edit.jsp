<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="plan.update.detail" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/monetaryrechargeplan" id="monetaryrechargeplan" action="monetary-recharge-plan" method="post" cssClass="form-horizontal" validate="true" validator="validateForm()">
            <s:hidden name="_method" value="put" />
            <s:set var="priceTag">
                <s:property value="getText('plan.price')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
            </s:set>
            <s:set var="amountTag">
                <s:property value="getText('plan.amount')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
            </s:set>
            <s:if test="%{packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@DESIGN.name() ||
                          packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@TEST.name() }">
                <%@include file="monetary-recharge-plan-edit-design-mode.jsp" %>
            </s:if>
            <s:elseif test="%{packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name() ||
                              packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
                <%@include file="monetary-recharge-plan-edit-live-mode.jsp" %>
            </s:elseif>

                <div class="row">
                    <div class="col-xs-12" align="center">
                        <button type="submit" id="btnSubmit" class="btn btn-primary btn-sm" role="submit" id="btnSubmit" formaction="${pageContext.request.contextPath}/pd/monetaryrechargeplan/monetary-recharge-plan/${id}" tabindex="9">
                            <span class="glyphicon glyphicon-floppy-disk"></span>
                            <s:text name="button.save" />
                        </button>
                        <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/monetaryrechargeplan/monetary-recharge-plan/${id}'" tabindex="10">
                            <span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;
                            <s:text name="button.back" />
                        </button>
                    </div>
                </div>
            </div>
        </s:form>
    </div>
</div>

<script>
$(function(){
    $( ".select2" ).select2();
});

function validateForm() {
    var mode = '<s:text name="packageMode"/>';

    if( mode == 'DESIGN' || mode == 'TEST' ) {
        var entityId = '<s:text name="id"/>';
        if(verifyUniquenessForName('update', entityId)  && verifyPrice() && verifyUniquenessForPrice('update',entityId) && verifyAmount() && verifyAmountValidityCombo()) {
            setDefaults();
            return true;
        }
        return false;
    }
    return true;
}
</script>
<%@include file="monetary-recharge-plan-utility.jsp"%>