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
        <h3 class="panel-title"><s:text name="pccrule.mapping.create" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/pccrulemapping" id="radiusPacketMappingCreate" action="radius-pcc-rule-mapping" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
            <s:token/>
            <div class="row">
                <div class="col-xs-6">
                    <s:textfield name="name" key="pccrule.mapping.name" id="name"
                                 cssClass="form-control focusElement"
                                 tabindex="1"/>
                    <s:textarea name="description" key="pccrule.mapping.description" cssClass="form-control"
                                rows="2" tabindex="2" id="description"/>
                    <s:select name="status" key="pccrule.mapping.status" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.ResourceStatus@values()" id="status"	 tabindex="4" />
                    <div id="staticAttributeMappings"></div>

                    <div id="tempStaticAttributeMappings" style="display: none;"><s:hidden id="staticMapping" /></div>

                    <div id="dynamicAttributeMappings"></div>

                    <div id="tempDynamicAttributeMappings" style="display: none;"><s:hidden id="dynamicMapping" /></div>

                </div>
            </div>
            <div class="col-xs-12 col-sm-12">
                <div class="treetable" id="staticMappings">

                </div>
            </div>


            <div class="col-xs-12 col-sm-12">
                <div class="treetable" id="dynamicMappings">

                </div>
                <div class="col-xs-12" id="generalError"></div>
            </div>

            <div class="row">
                <div class="col-xs-12" align="center">
                    <s:submit cssClass="btn btn-sm btn-primary" type="button" role="button" tabindex="5" id="btnCreate"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                    <button id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="6"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/pccrulemapping/radius-pcc-rule-mapping'">
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
            isValid =  verifyUniquenessOnSubmit('name', 'create', '', 'com.elitecore.corenetvertex.sm.gateway.PCCRuleMappingData', '', '');
            if(isValid == true){
                isValid =  validatePccRuleMappingRows();
                if(isValid == true){
                    getAttributeMappingList();
                }
            }
        }
        return isValid;
    }

    $(function(){
        getAttributeMappingsForRadius();
        setPolicyKeyAttributes();
    });

</script>
<%@include file="pcc-rule-mapping-utility.jsp"%>