<div class="col-xs-12 col-sm-6">
    <s:select key="data.topup.quotatype" name="quotaType" tabindex="16" onchange="setVolumeType()" disabled="true"
              list="@com.elitecore.corenetvertex.pd.topup.TopUpQuotaType@values()"
              listValue="getValue()" id="quotaType"/>

    <s:textfield name="volumeBalance" tabindex="17" type="number" id = "volumeBalance" readonly="true"
                 key="data.topup.volume.balance" cssClass="form-control"
                 onkeypress="return isNaturalInteger(event);" min="1" max="999999999999999999"
                 maxlength="18"/>
    <s:select cssClass="form-control" name="volumeBalanceUnit" id="volumeBalanceUnit" disabled="true"
              key="data.topup.volume.balance.unit" tabindex="18"
              list="@com.elitecore.corenetvertex.constants.DataUnit@values()"/>
    <s:hidden name="quotaType"/>
    <s:hidden name="volumeBalanceUnit"/>
</div>
<div class="col-xs-12 col-sm-6">
    <s:select key="data.topup.unittype" name="unitType" tabindex="19" disabled="true"
              list="@com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType@values()"
              listValue="getValue()" id="unitType"/>
    <s:textfield name="timeBalance" tabindex="20" type="number" id="timeBalance" key="data.topup.time.balance" cssClass="form-control" readonly="true"
                 onkeypress="return isNaturalInteger(event);" min="1" max="999999999999999999"
                 maxlength="18" />
    <s:select cssClass="form-control" name="timeBalanceUnit" id="timeBalanceUnit" disabled="true"
              key="data.topup.time.balance.unit" tabindex="21"
              list="@com.elitecore.corenetvertex.constants.TimeUnit@values()"/>
    <s:hidden name="unitType"/>
    <s:hidden name="timeBalanceUnit"/>
</div>