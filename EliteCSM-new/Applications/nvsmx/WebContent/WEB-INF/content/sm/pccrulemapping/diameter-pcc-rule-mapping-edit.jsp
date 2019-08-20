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
        <h3 class="panel-title"><s:text name="pccrule.mapping.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/pccrulemapping" id="diameterPccRuleMappingUpdate" action="diameter-pcc-rule-mapping" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
            <s:token/>
            <s:hidden name="_method" value="put"/>
            <div class="row">
                <div class="col-xs-6">
                    <s:textfield name="name" key="pccrule.mapping.name" id="name"
                                 cssClass="form-control focusElement"
                                 tabindex="1"/>
                    <s:textarea name="description" key="pccrule.mapping.description" cssClass="form-control"
                                rows="2" tabindex="2"/>
                    <s:select name="status" key="pccrule.mapping.status" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.ResourceStatus@values()" id="resourceStatus"	 tabindex="4" />

                    <div id="staticAttributeMappings">
                        <s:iterator value="staticAttributeMappings.mappings" status="i" var="mapping">
                            <s:if test="%{#mapping != null}">
                                <s:hidden name="staticAttributeMappings.mappings[%{#i.count - 1}]"
                                          value="%{#mapping}"/>
                            </s:if>
                        </s:iterator>

                    </div>

                    <div id="tempStaticAttributeMappings" style="display: none;"><s:hidden id="staticMapping" /></div>

                    <div id="dynamicAttributeMappings">
                        <s:iterator value="dynamicAttributeMappings.mappings" status="j" var="mapping">
                            <s:if test="%{#mapping != null}">
                                <s:hidden name="dynamicAttributeMappings.mappings[%{#j.count - 1}]"
                                          value="%{#mapping}"/>
                            </s:if>
                        </s:iterator>
                    </div>

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
                    <button class="btn btn-sm btn-primary" type="submit" role="button"
                            formaction="${pageContext.request.contextPath}/sm/pccrulemapping/diameter-pcc-rule-mapping/${id}"><span
                            class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="15"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/pccrulemapping/diameter-pcc-rule-mapping/${id}'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.back"/>
                    </button>
                </div>
            </div>
        </s:form>
    </div>

</div>
<%@include file="pcc-rule-mapping-utility.jsp"%>
<script type="text/javascript">
    $(function(){
        getAttributeMappingsForDiameter();
        setPolicyKeyAttributes();
    });

    function validateForm(){
        var isValid;
        isValid = validatePolicyKeyAndAttributes();
        if(isValid == true) {
            isValid = verifyUniquenessOnSubmit('name', 'update', '<s:text name="id"/>', 'com.elitecore.corenetvertex.sm.gateway.PCCRuleMappingData', '', '');
            if(isValid == true){
                isValid =  validatePccRuleMappingRows();
                if(isValid == true){
                    getAttributeMappingList();
                }
            }
        }
        return isValid;
    }

</script>

