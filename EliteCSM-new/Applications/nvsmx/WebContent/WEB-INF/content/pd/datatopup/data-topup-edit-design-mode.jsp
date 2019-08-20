<%@ taglib uri="/struts-tags/ec" prefix="s" %>
<div class="row">
    <div class="col-xs-6">
        <s:textfield name="name" key="data.topup.name" id="name" cssClass="form-control focusElement"
                     tabindex="1"/>

        <s:textarea name="description" key="data.topup.description" cssClass="form-control"
                    id="description" tabindex="2"/>
        <s:select name="groups" value="groupValuesForUpdate" key="data.topup.groups" cssClass="form-control select2"
                  list="#session.staffBelongingGroups" id="groupNames" multiple="true" listKey="id"
                  listValue="name" cssStyle="width:100%" tabindex="3"/>
        <s:select name="status" key="data.topup.status" cssClass="form-control" tabindex="4"
                  list="@com.elitecore.corenetvertex.constants.PkgStatus@values()" id="dataTopupStatus"/>
        <s:hidden name="packageMode" id="packageMode" tabindex="5"/>
        <s:hidden name="topupType" id="topupType" tabindex="5"/>

        <s:textfield key="data.topup.type" readonly="true" value="%{@com.elitecore.corenetvertex.pd.topup.TopUpType@valueOf(topupType).getVal()}" cssClass="form-control"/>

        <s:select id="mulitpleSubscription" name="multipleSubscription"
                  key="data.topup.multiple.subscription"
                  list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
                  listKey="isBooleanValue()" listValue="getStringName()" cssClass="form-control" tabindex="7"/>
    </div>
    <div class="col-xs-6">

        <s:select name="applicablePCCProfiles" key="data.topup.applicable.pcc.profiles" cssClass="form-control select2" tabindex="8" value="pccProfileNamesForUpdate"
                  list="pccProfileNames" id="applicablePCCProfiles" multiple="true"/>

        <s:textfield name="validityPeriod" key="data.topup.validity.period" cssClass="form-control"
                     type="number" id="validityPeriod" onkeypress="return isNaturalInteger(event);"
                     value="30" tabindex="10"/>


        <s:select name="validityPeriodUnit" key="data.topup.validity.period.unit" cssClass="form-control"
                  list="@com.elitecore.corenetvertex.constants.ValidityPeriodUnit@values()"
                  id="validityPeriodUnit" listKey="name()" listValue="displayValue" tabindex="11"/>
        <s:set var="priceTag">
            <s:property value="getText('data.topup.price')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
        </s:set>

        <s:textfield name="price" key="priceTag" type="number" step="any"
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
            <div class="col-xs-12 col-sm-6">
                <s:select key="data.topup.quotatype" name="quotaType" tabindex="16" onchange="setVolumeType()"
                          list="@com.elitecore.corenetvertex.pd.topup.TopUpQuotaType@values()"
                          listValue="getValue()" id="quotaType"/>
                <s:textfield name="volumeBalance" tabindex="17" type="number" id = "volumeBalance"
                             key="data.topup.volume.balance" cssClass="form-control"
                             onkeypress="return isNaturalInteger(event);" min="1" max="999999999999999999"
                             maxlength="18"/>
                <s:select cssClass="form-control" name="volumeBalanceUnit" id="volumeBalanceUnit"
                          key="data.topup.volume.balance.unit" tabindex="18"
                          list="@com.elitecore.corenetvertex.constants.DataUnit@values()"/>
            </div>
            <div class="col-xs-12 col-sm-6">
                <s:select key="data.topup.unittype" name="unitType" tabindex="19"
                          list="@com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType@values()"
                          listValue="getValue()" id="unitType"/>
                <s:textfield name="timeBalance" tabindex="20" type="number" id="timeBalance" key="data.topup.time.balance" cssClass="form-control"
                             onkeypress="return isNaturalInteger(event);" min="1" max="999999999999999999"
                             maxlength="18" />
                <s:select cssClass="form-control" name="timeBalanceUnit" id="timeBalanceUnit"
                          key="data.topup.time.balance.unit" tabindex="21"
                          list="@com.elitecore.corenetvertex.constants.TimeUnit@values()"/>
            </div>
        </div>
    </fieldset>
</div>