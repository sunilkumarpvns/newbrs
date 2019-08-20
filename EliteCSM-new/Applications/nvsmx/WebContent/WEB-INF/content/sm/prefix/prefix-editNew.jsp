<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<%@include file="prefix-utility.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="prefix.create" /></h3>
    </div>
    <div class="panel-body">
            <s:form namespace="/sm/prefix" id="prefixCreateFormId" action="prefix" method="post" cssClass="form-horizontal"
                    validate="true"
                    validator="validateForm()" labelCssClass="col-xs-12 col-sm-4 col-lg-3"
                    elementCssClass="col-xs-12 col-sm-8 col-lg-9">
            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-8">
                    <s:textfield name="prefix" key="prefix.prefix"  id="prefix" cssClass="form-control focusElement"  maxlength="15"  tabindex="1"/>
                    <s:select name="countryData.id" list="countryList" listKey="id" listValue="name" key="prefix.country" cssClass="form-control" id="countryNames" tabindex="2" onchange="getNetwork()"/>
                    <s:select name="operatorData.id"  list="operatorList" listKey="id" listValue="name" key="prefix.operator" cssClass="form-control" id="operatorNames" tabindex="3" onchange="getNetwork()"/>
                    <s:select name="networkData.id" list="networkList" listKey="id" listValue="name" key="prefix.network"  headerValue="-select-" headerKey="" cssClass="form-control select2" id="networkNames" tabindex="4"/>
                </div>
                <div class="row">
                    <div class="col-xs-12" align="center">
                        <s:submit type="button" cssClass="btn  btn-sm btn-primary" id="btnSave" role="button" tabindex="5"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                        <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/prefix/prefix'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>
                    </div>
                </div>
            </div>
        </s:form>
    </div>
</div>
<script type="text/javascript">



    $(function(){
        $("#countryNames").select2();
        $("#operatorNames").select2();
        $("#networkNames").select2();
        getNetwork();
    });

    function validateForm(){
        clearErrorMessages();


        if( verifyUniquenessOnSubmit('prefix','create','<s:property value="id"/>','com.elitecore.corenetvertex.pd.prefix.PrefixData','','prefix')==false){
            return false;
        }
            if(isNullOrEmpty($("#networkNames").val())){
                setError('networkNames','<s:text name='prefix.network.required'/>');
                return false;
            }
            return true;
    }


</script> 