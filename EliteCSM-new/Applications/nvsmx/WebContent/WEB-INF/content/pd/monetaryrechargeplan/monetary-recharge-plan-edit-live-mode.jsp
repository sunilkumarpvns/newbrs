<div class="row">
    <div class="col-xs-6">
        <s:textfield name="name" key="plan.name" cssClass="form-control" maxlength="50" tabindex="1" readonly="true"/>
        <s:textarea name="description" key="plan.description" cssClass="form-control" maxlength="2000" tabindex="2" readonly="true"/>
        <s:select disabled="true" name="groups" value="groupValuesForUpdate" key="plan.groups" cssClass="form-control select2"
                  list="#session.staffBelongingGroups" id="planGroups" multiple="true" listKey="id"
                  listValue="name" cssStyle="width:100%" tabindex="3"/>
        <s:select name="status" key="plan.status" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.ResourceStatus@values()" maxlength="50" tabindex="4" />
    </div>
    <div class="col-xs-6">
        <s:textfield name="price" key="priceTag" cssClass="form-control" tabindex="5" readonly="true"/>
        <s:textfield name="amount" key="amountTag" cssClass="form-control" tabindex="6" readonly="true"/>
        <s:textfield name="validity" key="plan.validity" cssClass="form-control" tabindex="7" readonly="true"/>
        <s:select name="validityPeriodUnit" key="plan.validity.period.unit" cssClass="form-control"
                  list="@com.elitecore.corenetvertex.constants.ValidityPeriodUnit@values()"
                  id="validityPeriodUnit" listKey="name()" listValue="displayValue" tabindex="8" readonly="true"/>
    </div>
</div>