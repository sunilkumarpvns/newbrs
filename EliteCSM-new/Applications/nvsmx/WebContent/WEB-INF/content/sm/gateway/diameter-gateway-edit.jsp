<%@ taglib uri="/struts-tags/ec" prefix="s" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<script src="${pageContext.request.contextPath}/select2/js/nvselect2groups.js"></script>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="diameter.gateway.update"/></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/gateway"  id="diameterGatewayUpdate" action="diameter-gateway" method="post" cssClass="form-horizontal" validate="true"
                labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9"
                validator="validateForm()">
            <s:hidden name="_method" value="put"/>
            <s:token/>
            <div class="row">
                <div class="col-xs-6">
                    <s:textfield name="name" key="diameter.gateway.name" id="name"
                                 cssClass="form-control focusElement" tabindex="1" />
                    <s:textarea name="description" key="diameter.gateway.description" cssClass="form-control"
                                rows="2" tabindex="2"/>
                    <s:select name="status" key="diameter.gateway.status" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.ResourceStatus@values()" id="resourceStatus"	 tabindex="4" />
                        <s:select name="policyEnforcementMethod" key="diameter.gateway.policyEnforcementMethod"
                                  cssClass="form-control "
                                  list="@com.elitecore.corenetvertex.constants.PolicyEnforcementMethod@getDiameterMethods()"
                                  listValue="val" tabindex="5"/>
                    <s:select name="diameterGatewayProfileId" key="diameter.gateway.diameterprofile"
                              cssClass="form-control"
                              list="%{diameterGatewayProfiles}" headerKey="" headerValue="   SELECT "
                              listKey="id" listValue="name" tabindex="6"/>
                    <s:select name="alternateHostId" key="diameter.gateway.alternateHost"
                              cssClass="form-control"
                              list="%{alternateHosts}" headerKey="" headerValue="   SELECT "
                              listKey="id" listValue="name" tabindex="7"/>

                </div>

                <div class="col-xs-6">

                    <s:textfield name="connectionURL" key="diameter.gateway.connectionURL" id="connectionURL"
                                 cssClass="form-control"
                                 tabindex="8" maxlength="45"/>
                    <s:textfield name="hostIdentity" key="diameter.gateway.hostIdentity"
                                 id="hostIdentity"
                                 cssClass="form-control"
                                 onblur="verifyUniqueness('hostIdentity','update','%{id}','com.elitecore.corenetvertex.sm.gateway.DiameterGatewayData','','hostIdentity');"
                                 tabindex="9" maxlength="100"/>
                    <s:textfield name="realm" key="diameter.gateway.realm" id="realm"
                                 cssClass="form-control"
                                 tabindex="10" maxlength="40"/>
                    <s:textfield name="localAddress" key="diameter.gateway.localAddress"
                                 id="localAddress"
                                 cssClass="form-control"
                                 tabindex="11" maxlength="40"/>
                    <s:textfield name="requestTimeout" key="diameter.gateway.requestTimeout"
                                 id="requestTimeout"
                                 cssClass="form-control"
                                 tabindex="12" type="number" maxlength="5" onkeypress="return isNaturalInteger(event);"/>
                    <s:textfield name="retransmissionCount" key="diameter.gateway.retransmissionCount"
                                 id="retransmissionCount"
                                 cssClass="form-control"
                                 tabindex="13" type="number" maxlength="1" onkeypress="return isNaturalInteger(event);"/>



                </div>
            </div>


            <div class="row">
                <div class="col-xs-12" align="center">
                    <button class="btn btn-sm btn-primary" type="submit" role="button"
                            formaction="${pageContext.request.contextPath}/sm/gateway/diameter-gateway/${id}" tabindex="14"><span
                            class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="15"
                            style="margin-right:10px;"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gateway/diameter-gateway/${id}'">
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
        var connectionURL = $("#connectionURL").val();
        var hostIdentity = $("#hostIdentity").val();
        if(isNullOrEmpty(connectionURL) && isNullOrEmpty(hostIdentity)){
            setError('hostIdentity', 'Host Identity or Connection URL is mandatory');
            return false;
        }
        if(isNullOrEmpty(hostIdentity) == false){
            var flag = verifyUniquenessOnSubmit('hostIdentity', 'update', '<s:text name="id"/>', 'com.elitecore.corenetvertex.sm.gateway.DiameterGatewayData', '', 'hostIdentity');
            if(flag == false){
                return false;
            }
        }
        return verifyUniquenessOnSubmit('name', 'update', '<s:text name="id"/>', 'com.elitecore.corenetvertex.sm.gateway.DiameterGatewayData', '', '');
    }
</script>
