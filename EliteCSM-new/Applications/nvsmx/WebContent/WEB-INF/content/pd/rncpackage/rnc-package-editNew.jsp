<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="rncpackage.create" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/rncpackage" action="rnc-package" id="rncpackage" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
            <s:token/>
            <s:hidden name="currency" id="currency" value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/>
                <div class="col-sm-9 col-lg-8">
                    <s:textfield name="name" key="rncpackage.name" id="rncpackageName" cssClass="form-control focusElement" maxlength="100" tabindex="1" />
                    <s:textarea name="description" key="rncpackage.description" id="rncpackageDesc" cssClass="form-control" maxlength="2000" value="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDefaultDescription(request)}" tabindex="2"/>
                    <s:select name="groups" id="rncPackageGroups" key="rncpackage.groups" cssClass="form-control select2" list="#session.staffBelongingGroups" multiple="true" listKey="id" listValue="name" cssStyle="width:100%" tabindex="3"/>
                    <s:select 		name="status" 			key="rncpackage.status" 	cssClass="form-control"			list="@com.elitecore.corenetvertex.constants.PkgStatus@values()" id="pkgStatus"	tabindex="4"/>
                    <s:select name="type" key="rncpackage.type" cssClass="form-control"
                              list="@com.elitecore.corenetvertex.pkg.RnCPkgType@values()"
                              listValue="top.val"
                              id="productOfferType" tabindex="5"/>
                    <s:select name="chargingType" key="rncpackage.chargingtype" list="@com.elitecore.corenetvertex.pkg.ChargingType@values()" cssClass="form-control" tabindex="6"/>
                    <s:select name="currencyList" key="rncpackage.currency" list="@java.util.Currency@getAvailableCurrencies()" listKey="getCurrencyCode()" listValue="getCurrencyCode()+' ('+getDisplayName()+')'" id="currencyList" cssClass="form-control" tabindex="5" value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()" disabled="!@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@isMultiCurrencyEnable()" onchange="updateCurrencyLabel()" />
                    <s:hidden name="mode" value="%{@com.elitecore.corenetvertex.pkg.PkgMode@DESIGN.name()}"/>
                </div>
                               
                <div class="row">
                    <div class="col-xs-12" align="center" >
                        <s:submit cssClass="btn btn-sm btn-primary" id="btnSave" type="button" role="button" tabindex="7" ><span class="glyphicon glyphicon-floppy-disk" ></span> <s:text name="button.save"/></s:submit>
                        <button type="button" id="btnCancel" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/rncpackage/rnc-package'" tabindex="8" ><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>
                    </div>
                </div>
    	</s:form>
	</div>
</div>
    
<script type="text/javascript">

	function validateForm() {
		return verifyUniquenessOnSubmit('rncpackageName','create','','com.elitecore.corenetvertex.pd.rncpackage.RncPackageData','','') && checkStatus();
   } 
	
	$(function(){
	    $( ".select2" ).select2();
        updateCurrencyLabel();
	});
	function checkStatus(){
        var status =$("#pkgStatus").val();
        if(status === '<s:property value="@com.elitecore.corenetvertex.constants.PkgStatus@RETIRED.name()"/>'){
            setError('pkgStatus',"<s:text name='error.status.retired'/>");
            return false;
        }
        return true;
    }

    function updateCurrencyLabel(){
        var currencyValue = $("#currencyList").val();
        $("#currency").val(currencyValue);

    }


</script>