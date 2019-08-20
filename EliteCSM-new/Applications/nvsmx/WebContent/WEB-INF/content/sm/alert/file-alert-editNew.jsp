<%@ taglib uri="/struts-tags/ec" prefix="s" %>


<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="file.alert.create"/></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/alert" id="fileAlertListenerForm" action="file-alert" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9"
                validator="validateFileAlertListenerForm('create','');">
            <s:token/>
            <div class="row">
                <div class="col-xs-12 col-sm-6">
                    <s:textfield name="name" key="file.alert.name" id="name" cssClass="form-control focusElement" tabindex="1" maxlength="100"/>
                    <s:hidden name="type" value="%{@com.elitecore.corenetvertex.sm.alerts.AlertTypes@FILE.name()}"/>
                    <s:textfield name="fileName" key="file.alert.filename" id="fileName" cssClass="form-control"
                                 maxlength="18" tabindex="2" maxLength="100"/>

                    <s:select name="rollingType" key="file.alert.rollingtype" cssClass="form-control" id="rollingType"
                              listKey="value" listValue="label"
                              list="@com.elitecore.corenetvertex.constants.RollingType@values()" tabindex="3"
                              onchange="setRollingUnit()"/>
                </div>
                <div class="col-xs-12 col-sm-6">

                    <div id="maxRolledUnitDiv" >
                        <s:textfield name="maxRollingUnit" key="file.alert.max.rollingunit" id="maxRollingUnit" onkeypress="return isNaturalInteger(event);" cssClass="form-control" maxlength="3" tabindex="4"  />
                    </div>
                    <s:select name="compRollingUnit" key="file.alert.compressed.unit" id="compressedRollingUnit"
                              listValue="stringName" listKey="booleanValue"
                              list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" tabindex="5"/>
                    <div id="rollingUnitDiv">
                        <s:select name="rollingUnit" key="file.alert.rollingunit" cssClass="form-control"
                                  id="rollingUnitSelect"
                                  listKey="value" listValue="unit"
                                  list="@com.elitecore.corenetvertex.constants.TimeBasedRollingUnit@values()"
                                  tabindex="7"/>
                    </div>
                </div>
            </div>
            <%--alert  mapping--%>
            <%@include file="alert-utility.jsp" %>

            <%--general error div--%>
            <%@include file="/view/commons/general/GeneralErrorDiv.jsp"%>

            <div class="row">
                <div class="col-xs-12" align="center">
                    <s:submit cssClass="btn btn-sm btn-primary" type="button" role="button" tabindex="7"><span
                            class="glyphicon glyphicon-floppy-disk"></span>
                        <s:text name="button.save"/></s:submit>
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="32"
                            style="margin-right:10px;"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/alert/file-alert'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.list"/>
                    </button>
                </div>
            </div>
        </s:form>
    </div>
</div>

<div style="display: none" id="tempTimeBaseRollingDiv">
    <s:select name="rollingUnit" key="file.alert.rollingunit" cssClass="form-control" id="rollingUnit" listKey="value" listValue="unit" list="@com.elitecore.corenetvertex.constants.TimeBasedRollingUnit@values()"  tabindex="7"/>
</div>
<div style="display: none" id="tempSizeBaseRollingUnitDiv">
    <s:textfield name="rollingUnit" key="file.alert.rollingunit.size" id="rollingUnit" cssClass="form-control" maxlength="12" tabindex="7" type="number"/>
</div>

<%@include file="file-alert-utility.jsp"%>