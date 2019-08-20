<%@ taglib uri="/struts-tags/ec" prefix="s" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<script src="${pageContext.request.contextPath}/select2/js/nvselect2groups.js"></script>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="radius.gateway.update"/></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/gateway" id="radiusGatewayUpdate" action="radius-gateway" method="post" cssClass="form-horizontal" validate="true"
                labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9"
                validator="validateForm()">
            <s:hidden name="_method" value="put"/>
            <s:token/>
            <div class="row">
                <div class="col-xs-6">
                    <s:textfield name="name" key="radius.gateway.name" id="name"
                                 cssClass="form-control focusElement" tabindex="1"/>
                    <s:textarea name="description" key="radius.gateway.description" cssClass="form-control"
                                rows="2" tabindex="2"/>
                    <s:select name="status" key="radius.gateway.status" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.ResourceStatus@values()" id="resourceStatus"	 tabindex="4" />
                    <s:select name="policyEnforcementMethod" key="radius.gateway.policyEnforcementMethod"
                              cssClass="form-control"
                              list="@com.elitecore.corenetvertex.constants.PolicyEnforcementMethod@getRadiusMethods()" listKey="name()"
                              listValue="val" tabindex="5"/>
                </div>

                <div class="col-xs-6">
                    <s:select name="radiusGatewayProfileId" key="radius.gateway.radiusprofile"
                              cssClass="form-control"
                              list="%{radiusGatewayProfiles}" headerKey="" headerValue="   SELECT "
                              listKey="id" listValue="name" tabindex="6"/>
                    <s:textfield name="connectionURL" key="radius.gateway.connectionURL" id="connectionUrl"
                                 cssClass="form-control"
                                 tabindex="7" maxlength="45"/>
                    <s:textfield name="sharedSecret" key="radius.gateway.sharedSecret" id="sharedSecret"
                                 cssClass="form-control"
                                 tabindex="8" maxlength="40"/>
                    <s:textfield name="minLocalPort" key="radius.gateway.minLocalPort" id="minLocalPort"
                                 cssClass="form-control"
                                 tabindex="9" maxlength="8" onkeypress="return isNaturalInteger(event);"/>
                </div>
            </div>


            <div class="row">
                <div class="col-xs-12" align="center">
                    <button class="btn btn-sm btn-primary" type="submit" role="button"
                            formaction="${pageContext.request.contextPath}/sm/gateway/radius-gateway/${id}" tabindex="10"><span
                            class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="11"
                            style="margin-right:10px;"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gateway/radius-gateway/${id}'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.back"/>
                    </button>
                </div>
            </div>
        </s:form>
    </div>
</div>
</div>
<script type="text/javascript">
    $(function(){
        $( ".select2" ).select2();
    });
    function validateForm() {
        return verifyUniquenessOnSubmit('name', 'update', '<s:text name="id"/>', 'com.elitecore.corenetvertex.sm.gateway.RadiusGatewayData', '', '');
    }
</script>
