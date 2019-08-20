<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
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
        <h3 class="panel-title"><s:text name="geography.create" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/geography" action="geography" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
            <s:token/>
            <div class="row">
                <div class="col-xs-6">
                    <s:textfield name="name" key="geography.name" id="name"
                                 cssClass="form-control focusElement"
                                 tabindex="1"/>
                    <s:select multiple="true" name="country" key="geography.country" id="countryId"
                              cssClass="form-control select2" cssStyle="width:100%"
                              list="countryList"
                              listKey="code" listValue="name" tabindex="2"/>
                </div>
            </div>

            <div class="row">
                <div class="col-xs-12" align="center">
                    <s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" tabindex="5"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="6"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/geography/geography'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.list"/>
                    </button>
                </div>
            </div>
        </s:form>
    </div>

</div>
<script type="text/javascript">
    $(function(){
        $("#countryId").select2();
    });
    function validateForm(){
        return verifyUniquenessOnSubmit('name','create','','com.elitecore.corenetvertex.sm.location.geography.GeographyData','','') && validateCountry();
    }
    function validateCountry(){
        if(isNullOrEmpty($("#countryId").val())){
            setError("countryId",'<s:text name="error.valueRequired" />');
            return false;
        }
        return true;
    }
</script>
