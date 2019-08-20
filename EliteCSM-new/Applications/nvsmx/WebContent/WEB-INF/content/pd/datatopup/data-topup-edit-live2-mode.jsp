<div class="row">
    <div class="col-xs-6">
        <s:textfield name="name" key="data.topup.name" id="name" cssClass="form-control focusElement" readonly="true"
                     tabindex="1"/>

        <s:textarea name="description" key="data.topup.description" cssClass="form-control" readonly="true"
                    id="description" tabindex="2"/>
        <s:select name="groups" value="groupValuesForUpdate" key="data.topup.groups" cssClass="form-control select2"
                  disabled="true"
                  list="#session.staffBelongingGroups" id="groupNames" multiple="true" listKey="id"
                  listValue="name" cssStyle="width:100%" tabindex="3"/>
        <s:hidden name="groups"/>
        <s:select name="status" key="data.topup.status" cssClass="form-control" tabindex="4"
                  list="@com.elitecore.corenetvertex.constants.PkgStatus@values()" id="dataTopupStatus"/>
        <s:hidden name="packageMode" tabindex="5"/>

        <s:textfield key="data.topup.type" readonly="true"
                     value="%{@com.elitecore.corenetvertex.pd.topup.TopUpType@valueOf(topupType).getVal()}"
                     cssClass="form-control"/>
        <s:hidden name="topupType" tabindex="5"/>

        <s:select id="mulitpleSubscription" name="multipleSubscription" disabled="true"
                  key="data.topup.multiple.subscription"
                  list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
                  listKey="isBooleanValue()" listValue="getStringName()" cssClass="form-control" tabindex="7"/>

        <s:hidden name="multipleSubscription"/>
    </div>
    <div class="col-xs-6">

        <s:select name="applicablePCCProfiles" key="data.topup.applicable.pcc.profiles" cssClass="form-control select2" tabindex="8" disabled="true" value="pccProfileNamesForUpdate"
                  list="pccProfileNames" id="applicablePCCProfiles" multiple="true"/>

        <s:hidden name="applicablePCCProfiles"/>

        <s:textfield name="validityPeriod" key="data.topup.validity.period" cssClass="form-control" readonly="true"
                     type="number" id="validityPeriod" onkeypress="return isNaturalInteger(event);"
                     value="30" tabindex="10"/>


        <s:select name="validityPeriodUnit" key="data.topup.validity.period.unit" cssClass="form-control"
                  disabled="true"
                  list="@com.elitecore.corenetvertex.constants.ValidityPeriodUnit@values()"
                  id="validityPeriodUnit" listKey="name()" listValue="displayValue" tabindex="11"/>
        <s:hidden name="validityPeriodUnit"/>

        <s:set var="priceTag">
            <s:property value="getText('data.topup.price')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
        </s:set>

        <s:textfield name="price" key="priceTag" type="number" step="any" readonly="true"
                     id="price"
                     cssClass="form-control" tabindex="12"/>

        <s:textfield name="param1" key="data.topup.param1" cssClass="form-control" id="param1" tabindex="14"/>
        <s:textfield name="param2" key="data.topup.param2" cssClass="form-control" id="param2" tabindex="15"/>
    </div>
</div>
<div class="row">
    <fieldset class="fieldSet-line">
        <legend><s:text name="data.topup.quota.information"/></legend>
        <div class="row">
            <%@ include file="data-top-up-edit-live-mode-quota-information.jsp" %>
        </div>
    </fieldset>
</div>