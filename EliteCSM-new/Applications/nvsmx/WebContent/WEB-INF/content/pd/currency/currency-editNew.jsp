<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<s:text name="currency.create" />
		</h3>
	</div>
	<div class="panel-body">
		<s:form namespace="/pd/currency" action="currency" method="post" cssClass="form-horizontal" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validate="true" validator="validateForm()">
			<s:token />
			<div>
				<div class="col-sm-9 col-lg-8">
					 <s:select name="fromIsoCode" key="currency.fromIsoCode"
						cssClass="form-control"
						list="@java.util.Currency@getAvailableCurrencies()"
						listKey="getCurrencyCode()"
						listValue="getCurrencyCode()+' ('+getDisplayName()+')'"
						id="fromIsoCode"
						value="@java.text.NumberFormat@getCurrencyInstance().getCurrency()"
						tabindex="1" />
						
					 <s:select name="toIsoCode" key="currency.toIsoCode"
						cssClass="form-control"
						list="@java.util.Currency@getAvailableCurrencies()"
						listKey="getCurrencyCode()"
						listValue="getCurrencyCode()+' ('+getDisplayName()+')'"
						id="toIsoCode"
						value="@java.text.NumberFormat@getCurrencyInstance().getCurrency()"
						tabindex="2" /> 
					 
					 <s:textfield id="rate" key="currency.rate" name="rate" type="text" cssClass="form-control" maxlength="12" tabindex="3" />
					 <s:datepicker id="effectiveDate" name="effectiveDate"
						key="currency.effectiveDate" parentTheme="bootstrap" changeMonth="true"
						changeYear="true" cssClass="form-control" showAnim="slideDown"
						duration="fast" showOn="focus" placeholder="DD-MON-YYYY"
						displayFormat="dd-M-yy" timepicker="false"
						timepickerFormat="HH:mm:ss" readonly="true" maxlength="20" tabindex="4"/>
				</div>
				<div class="row">
				  <div class="col-xs-12" align="center">
				      <button type="submit" class="btn btn-primary btn-sm" id="btnSave"  role="submit" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
					  <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/currency/currency'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list" /></button>
				  </div>
				</div>
			</div>
		</s:form>
	</div>
</div>


<script type="text/javascript">
	function validateForm(){
		return isValidRate('rate',$('#rate').val());
	} 
</script>

