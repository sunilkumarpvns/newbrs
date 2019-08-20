<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<script src="${pageContext.request.contextPath}/js/packageutility.js"></script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="rncpackage.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/rncpackage" action="rnc-package" id="rncpackage" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
            <s:hidden name="_method" value="put" />
            
            <s:token/>
            <s:hidden name="currency" id="currency" value="%{currency}"/>

                <div class="col-sm-9 col-lg-8">
                    <s:textfield name="name" key="rncpackage.name" id="rncpackageName" cssClass="form-control focusElement" maxlength="100" tabindex="1" />
                    <s:textarea name="description" key="rncpackage.description" id="rncpackageDesc" cssClass="form-control" maxlength="2000" tabindex="2"/>
                    <s:select name="groups" value="groupValuesForUpdate" key="rncpackage.groups" cssClass="form-control select2" list="#session.staffBelongingGroups" id="rncPackageGroups" multiple="true" listKey="id" listValue="name" cssStyle="width:100%" tabindex="3"/>
                    <div class="form-group">
                        <div class = "col-xs-12 col-sm-4"></div>
                        <div class = "col-xs-12 col-sm-8"><span id="validateGroup" style="display: none;"><strong style="color: red">Note : </strong>Changing group may invalidate product offer.</span></div>
                    </div>
                    <s:select 	 name="status" 	id="pkgStatus"	    key="rncpackage.status" list="@com.elitecore.corenetvertex.constants.PkgStatus@values()" 		cssClass="form-control" tabindex="4" />
                    <s:if test="%{mode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
                        <s:hidden name="groups"/>
                    </s:if>
                    <s:hidden name="type"/>
                    <s:textfield key="rncpackage.type" cssClass="form-control" value="%{@com.elitecore.corenetvertex.pkg.RnCPkgType@fromName(type).getVal()}" readonly="true"/>
                    <s:textfield name="chargingType" key="rncpackage.chargingtype" cssClass="form-control" readonly="true"/>

                    <s:select name="currencyList" id="currencyList" key="rncpackage.currency" value="%{currency}"
                              cssClass="form-control" tabindex="5"
                              list="@java.util.Currency@getAvailableCurrencies()"
                              listKey="getCurrencyCode()"
                              listValue="getCurrencyCode()+' ('+getDisplayName()+')'"

                              onchange="updateCurrency()" />

                    <s:hidden name="mode" id="pkgMode"/>
                </div>
                
        <div class="row">
            <div class="col-xs-12" align="center" >
                <button type="submit" class="btn btn-primary btn-sm"  role="submit" tabindex="6" formaction="${pageContext.request.contextPath}/pd/rncpackage/rnc-package/${id}" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                <button type="button" id="btnCancel" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/rncpackage/rnc-package'" tabindex="6" ><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>

            </div>
        </div>
      </s:form>
	</div>
</div>

		<script type="text/javascript">
    var existingProductGroup = [];
	function validateForm() {
		return verifyUniquenessOnSubmit('rncpackageName','update','<s:property value="id"/>','com.elitecore.corenetvertex.pd.rncpackage.RncPackageData','','');
   } 
	
	$(function(){
	    $( ".select2" ).select2();
	    checkRnCPackageMode();
	    fetchExistingProductGroup();
        updateCurrency();
    })

    function fetchExistingProductGroup() {
        existingProductGroup = $('[name=groups]').val();
    }

    $('[name=groups]').on("change",function() {
        validateProductGroup(existingProductGroup, $('[name=groups]').val());
    })

    function checkRnCPackageMode(){
        var pkgMode =$("#pkgMode").val();
        if(pkgMode=="LIVE2"){
            $("#rncpackageName").attr("readonly","true");
            $("#rncpackageDesc").attr("readonly","true");
            $("#rncPackageGroups").attr("disabled","true");

        }
    }

    function updateCurrency(){
        var currencyValue = $("#currencyList").val();
        $("#currency").val(currencyValue);
        var systemParameterUpdated =false;
        systemParameterUpdated  = <s:property value="getCurrencyUpdateAllowed()"/>;
        if(systemParameterUpdated === false){
            $("#currencyList").attr("disabled",true);
        }
    }

</script>
                