<div class="col-xs-12 col-sm-6">
    <s:textfield key="data.topup.quotatype" name="quotaType" readonly="true" id="quotaType" cssClass="form-control"/>
    <s:textfield name="volumeBalance"  type="number" id = "volumeBalance" readonly="true"
                 key="data.topup.volume.balance" cssClass="form-control"
                 onkeypress="return isNaturalInteger(event);" min="1" max="999999999999999999"
                 maxlength="18"/>
    <s:textfield cssClass="form-control" name="volumeBalanceUnit" id="volumeBalanceUnit" readonly="true"
              key="data.topup.volume.balance.unit"/>

</div>
<div class="col-xs-12 col-sm-6">
    <s:textfield key="data.topup.unittype" name="unitType" readonly="true" id="unitType" cssClass="form-control"/>
    <s:textfield name="timeBalance"  type="number" id="timeBalance" key="data.topup.time.balance" cssClass="form-control" readonly="true"
                 onkeypress="return isNaturalInteger(event);" min="1" max="999999999999999999"
                 maxlength="18" />
    <s:textfield cssClass="form-control" name="timeBalanceUnit" id="timeBalanceUnit" readonly="true"
              key="data.topup.time.balance.unit"/>

</div>