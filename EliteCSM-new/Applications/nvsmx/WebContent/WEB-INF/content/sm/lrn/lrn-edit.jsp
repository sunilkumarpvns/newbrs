<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>


<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="lrn.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/lrn"  action="lrn" id="lrnUpdateFormId" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 " elementCssClass="col-xs-12 col-sm-8 " validator="validateForm()">
            <s:hidden name="_method" value="put" />
            <s:token/>
            <div class="row">
                <div class="col-sm-10">
                    <s:textfield name="lrn" key="lrn.lrn" id="name" cssClass="form-control focusElement" maxlength="10" tabindex="1"/>
                    <s:select name="operatorData.id"  list="operatorList" listKey="id" listValue="name" key="lrn.operator" cssClass="form-control" id="operatorNames" tabindex="4" onchange="getNetwork()"/>
                    <s:select name="networkData.id" list="networkList" listKey="id" listValue="name" key="lrn.network"  headerValue="-select-" headerKey="" cssClass="form-control" id="networkNames" tabindex="5"/>
                </div>
                <div class="row">
                    <div class="col-xs-12" align="center">
                        <button type="submit" class="btn btn-primary btn-sm" id="btnSave" tabindex="6" formaction="${pageContext.request.contextPath}/sm/lrn/lrn/${id}" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                        <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/lrn/lrn/'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>
                    </div>
                </div>
            </div>
        </s:form>
    </div>
</div>

<script type="text/javascript">

    $(function(){
        $("#operatorNames").select2();
        $("#networkNames").select2();
    });


    function validateForm(){
        clearErrorMessages();
        if(verifyUniquenessOnSubmit('name','update','<s:property value="id"/>','com.elitecore.corenetvertex.pd.lrn.LrnData','','lrn') == false) {
            return false;
        }
        return true;
    }

</script>

<%@include file="lrnutility.jsp"%>