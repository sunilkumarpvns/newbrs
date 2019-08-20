<%@ taglib uri="/struts-tags/ec" prefix="s" %>
<div class="row">
    <div class="col-xs-6">
        <s:textfield name="name" key="bod.package.name" id="name" cssClass="form-control focusElement"
                     tabindex="1" readonly="true"/>
        <s:textarea name="description" key="bod.package.description" cssClass="form-control"
                    id="description" tabindex="2" readonly="true"/>
        <s:select name="groups" value="groupValuesForUpdate" key="bod.package.groups" cssClass="form-control select2"
                  list="#session.staffBelongingGroups" id="groupNames" multiple="true" listKey="id"
                  listValue="name" cssStyle="width:100%" tabindex="3" disabled="true"/>
        <s:select name="status" key="bod.package.status" cssClass="form-control" tabindex="4"
                  list="@com.elitecore.corenetvertex.constants.PkgStatus@values()" id="bodPackageStatus"/>
        <s:hidden name="packageMode" id="packageMode"/>
        <s:select id="applicableQosProfiles" key="bod.package.applicableQosProfiles" list="qosProfileNames"
                  cssClass="form-control select2" multiple="true" name="applicableQosProfiles"
                  value="applicableQosProfileNames" cssStyle="width:100%" disabled="true" tabindex="5"/>
        <s:set var="priceTag">
            <s:property value="getText('bod.package.price')"/> <s:property
                value="getText('opening.braces')"/><s:property
                value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property
                value="getText('closing.braces')"/>
        </s:set>

        <s:textfield name="price" key="priceTag" step="any"
                     id="price" maxLength="9" type="number"
                     cssClass="form-control" tabindex="6" disabled="true"/>
    </div>
    <div class="col-xs-6">

        <s:textfield name="validityPeriod" key="bod.package.validity.period" cssClass="form-control"
                     type="number" id="validityPeriod" onkeypress="return isNaturalInteger(event);"
                     tabindex="7" readonly="true"/>
        <s:select name="validityPeriodUnit" key="bod.package.validity.period.unit" cssClass="form-control"
                  list="@com.elitecore.corenetvertex.constants.ValidityPeriodUnit@values()"
                  id="validityPeriodUnit" listKey="name()" listValue="displayValue" tabindex="8" readonly="true"/>
        <s:datepicker name="availabilityStartDate" key="bod.package.availability.start.date" disabled="true"
                      parentTheme="bootstrap" changeMonth="true" changeYear="true" cssClass="form-control"
                      showAnim="slideDown" duration="fast" showOn="focus"
                      placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy" timepicker="true"
                      timepickerFormat="HH:mm:ss" readonly="true"
                      id="availabilityStartDate"  tabindex="10"/>
        <s:datepicker name="availabilityEndDate" key="bod.package.availability.end.date" disabled="true"
                      parentTheme="bootstrap" changeMonth="true" changeYear="true" cssClass="form-control"
                      showAnim="slideDown" duration="fast" showOn="focus"
                      placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy" timepicker="true"
                      timepickerFormat="HH:mm:ss" readonly="true"
                      id="availabilityEndDate" tabindex="11"/>
        <s:textfield key="bod.package.param1" name="param1" cssClass="form-control" tabindex="12" maxLength="100" disabled="true"/>
        <s:textfield key="bod.package.param2" name="param2" cssClass="form-control" tabindex="13" maxlength="100" disabled="true"/>
    </div>
</div>