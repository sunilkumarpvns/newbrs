<div class="row">
    <div class="col-xs-6">
        <s:textfield name="name" id="name" key="plan.name" cssClass="form-control focusElement" maxlength="50" tabindex="1"/>
        <s:textarea name="description" key="plan.description" cssClass="form-control" maxlength="2000" tabindex="2" />
        <s:select name="groups" id="planGroups" value="groupValuesForUpdate" key="plan.groups" cssClass="form-control select2" list="#session.staffBelongingGroups" multiple="true" listKey="id" listValue="name" cssStyle="width:100%" tabindex="3"/>
        <s:select name="status" key="plan.status" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.ResourceStatus@values()" maxlength="50" tabindex="4" />
    </div>
    <div class="col-xs-6">
        <s:textfield name="price" id="price" key="priceTag" cssClass="form-control" maxlength="16" tabindex="5"/>
        <s:textfield name="amount" id="amount" key="amountTag" cssClass="form-control" maxlength="16" tabindex="6"/>
        <s:textfield name="validity" id="validity" key="plan.validity" cssClass="form-control" type="number" tabindex="7" onkeypress="return isNaturalInteger(event);"/>
        <s:select name="validityPeriodUnit" key="plan.validity.period.unit" cssClass="form-control"
                  list="@com.elitecore.corenetvertex.constants.ValidityPeriodUnit@values()"
                  id="validityPeriodUnit" listKey="name()" listValue="displayValue" tabindex="8"/>
    </div>
</div>