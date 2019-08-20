<%@ page import="com.elitecore.corenetvertex.constants.PacketType" %>
<%@ page import="com.elitecore.corenetvertex.constants.CommunicationProtocol" %>
<%@ page import="com.elitecore.corenetvertex.constants.ConversionType" %>
<%@ page import="com.elitecore.corenetvertex.constants.ProtocolType" %>
<%@ page import="com.elitecore.corenetvertex.constants.PCRFKeyConstants" %>
<%@ page import="com.elitecore.corenetvertex.constants.PCRFKeyType" %>
<%@include file="/view/commons/general/AutoCompleteIncludes.jsp" %>
<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/treetable.css"/>
<style>
    .removePadding{
        display: inline;
    }
    .wrap-word{
        word-break: break-all;
        width: 20%;
    }
</style>



<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="packet.mapping.create" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/packetmapping" id="radiusPacketMappingCreate" action="radius-packet-mapping" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
            <s:token/>
            <div class="row">
                <div class="col-xs-6">
                    <s:textfield name="name" key="packet.mapping.name" id="name"
                                 cssClass="form-control focusElement"
                                 tabindex="1"/>
                    <s:textarea name="description" key="packet.mapping.description" cssClass="form-control"
                                rows="2" tabindex="2"/>
                    <s:select name="status" key="packet.mapping.status" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.ResourceStatus@values()" id="resourceStatus"	 tabindex="4" />
                    <s:select name="type" key="packet.mapping.conversationtype"
                              cssClass="form-control" id="conversationType"
                              list="@com.elitecore.corenetvertex.constants.ConversionType@values()"  listValue="conversionType" onchange="getPacketTypeBasedOnRadiusProtocolAndConversationType()"
                              tabindex="3" />
                    <s:select name="packetType" key="packet.mapping.packettype"
                              cssClass="form-control"  list="{}"
                              tabindex="4" id="packetType" />
                    <div id="attributeMappings"></div>

                    <div id="tempAttributeMappings" style="display: none;"><s:hidden id="mappings" /></div>


                </div>
            </div>
            <div class="col-xs-12 col-sm-12">
                <div class="treetable" id="attributeData">

                </div>
                <div class="col-xs-12" id="generalError"></div>
            </div>

            <div class="row">
                <div class="col-xs-12" align="center">
                    <s:submit cssClass="btn btn-sm btn-primary" type="button" role="button" tabindex="5" id="btnCreate"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                    <button  id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="6"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/packetmapping/radius-packet-mapping'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.list"/>
                    </button>
                </div>
            </div>
        </s:form>
    </div>

</div>
<script type="text/javascript">
    function validateForm(){
        var isValid;
        isValid = validatePolicyKeyAndAttributes();
        if(isValid == true) {
            isValid =  verifyUniquenessOnSubmit('name', 'create', '', 'com.elitecore.corenetvertex.sm.gateway.PacketMappingData', '', '');
            if(isValid == true){
                isValid =  validatePacketMappingRows();
                if(isValid == true){
                    getAttributeMappingList();
                }
            }
        }
        return isValid;
    }
    $(function(){
        getPacketTypeBasedOnRadiusProtocolAndConversationType();
    });



</script>
<%@include file="PacketMappingValidation.jsp"%>