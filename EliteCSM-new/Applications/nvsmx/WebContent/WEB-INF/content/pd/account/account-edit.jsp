<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="account.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/account" action="" id = "account" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
            <s:hidden name="_method" value="put" />
            <s:hidden name="partnerIds"    value="%{partnerIds}" />
            <s:token/>
            	<div class="col-xs-12 col-sm-6">
                    <s:textfield name="name" key="account.name" id="accountName" cssClass="form-control focusElement" maxlength="100" tabindex="1" />
                    <s:textfield name="accountManager" key="account.accountManager" id="accountManager" cssClass="form-control" maxlength="100" tabindex="2" />
            		<s:select name="accountCurrency" key="account.accountCurrency" cssClass="form-control" list="@java.util.Currency@getAvailableCurrencies()" listKey="getCurrencyCode()" listValue="getCurrencyCode()+' ('+getDisplayName()+')'" id="accountCurrency" value="@java.text.NumberFormat@getCurrencyInstance().getCurrency()"/>
					<s:datepicker name="creationDate" key="account.creationDate" parentTheme="bootstrap" changeMonth="true" changeYear="true" cssClass="form-control" showAnim="slideDown" duration="fast" showOn="focus" placeholder="DD-MON-YYYY" displayFormat="dd-M-yy" id="creationDate" readonly="true" tabindex="4"/>
                    <s:textfield name="timeZone" key="account.timeZone" id="timeZone" cssClass="form-control" maxlength="50" tabindex="5" />
                </div>
                
                <div class="col-xs-12 col-sm-6">
                	<s:select id="lob" name="lobId" key="account.lob" cssClass="form-control" list="lobDataList" listKey="id" listValue="name" tabindex="6"/>
                	<s:select id="partnerGroup" name="partnerGroupId" key="account.partnerGroup" cssClass="form-control" list="partnerGroupDataList" listKey="id" listValue="name" tabindex="7"/>
       				<s:select id="productSpec" name="productSpecificationId" key="account.productSpecification" cssClass="form-control" list="productSpecDataList" listKey="id" listValue="name" tabindex="8"/>
                </div>
                        
				<div class="row">
           			<div class="col-xs-12" align="center">
               			<button type="submit" class="btn btn-primary btn-sm" id="btnSave"  role="submit" formaction="${pageContext.request.contextPath}/pd/account/account/${id}" tabindex="9"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                   		<button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/account/account/${id}'" tabindex="10"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.back"/></button>
               		</div>
            	</div>
    	</s:form>
	</div>
</div>
<script type="text/javascript">

	function validateForm() {
		return verifyUniquenessOnSubmit('accountName','update','<s:property value="id"/>','com.elitecore.corenetvertex.pd.account.AccountData','','');
   	} 
</script>
