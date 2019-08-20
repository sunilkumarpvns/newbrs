<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<%@page import="com.elitecore.corenetvertex.constants.PartnerStatus"%>

<style type="text/css">
#partnerkey {
	margin-top: 0px;
}
</style>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<s:text name="partner.update" />
		</h3>
	</div>
	<div class="panel-body">
		<s:form namespace="/pd/partner" action="partner" method="post" id ="partner" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
			<s:hidden name="_method" value="put" />
			<s:token />
			<div>
				<fieldset class="fieldSet-line">
					<legend align="top">
						<s:text name="partner.basic.details" />
					</legend>
                	
                	<div class="col-xs-12 col-sm-6">
						<s:textfield name="name" key="partner.name" id="partnerName" cssClass="form-control focusElement" maxlength="100" tabindex="1"/>
						<s:checkbox name="isUnsignedPartner" key="partner.isunsigned" id="partnerkey" tabindex="2" value="false" />
						<s:textfield name="partnerLegalName" key="partner.legalName" id="partnerLegalName" cssClass="form-control" maxlength="100" tabindex="3"/>
					</div>
					
					<div class="col-xs-12 col-sm-6">
						<s:datepicker name="registraionDate" key="partner.registraionDate" id="availabilityStartDate" parentTheme="bootstrap" changeMonth="true" changeYear="true" cssClass="form-control" showAnim="slideDown" duration="fast" showOn="focus" placeholder="DD-MON-YYYY" displayFormat="dd-M-yy" readonly="true" tabindex="4"/>
						<s:textfield name="registrationNum" key="partner.registrationNum" id="registrationNum" cssClass="form-control" maxlength="24" tabindex="5" />
						<s:textfield name="status" key="partner.status" cssClass="form-control" type="text" id="status" maxlength="12" tabindex="6"  />
					</div>
				</fieldset>
			</div>

			<div>
				<fieldset class="fieldSet-line">
					<legend align="top">
						<s:text name="partner.register.address" />
					</legend>
					
                	<div class="col-xs-12 col-sm-6">
						<s:select id="countryId" name="countryId" key="partner.country" cssClass="form-control select2" list="countryDataList" listKey="id" listValue="name" onchange="onCountryChange()" tabindex="7"/>
                       	<s:select id="regionId" list="{}" name="regionId" key="partner.state" cssClass="form-control select2" listKey="id" listValue="name" onchange="onStateChange()" tabindex="8"/>
					</div>
					
					<div class="col-xs-12 col-sm-6">
						<s:select id="cityId" list="{}" name="cityId" key="partner.city" cssClass="form-control select2" listKey="id" listValue="name" tabindex="9"/>
						<s:textfield name="postCode" key="partner.postCode" onkeypress="return isNaturalInteger(event);" cssClass="form-control" id="postCode" maxlength="12" tabindex="10"/>
					</div>
				</fieldset>
			</div>
				
			<div>
				<fieldset class="fieldSet-line">
					<legend align="top">
						<s:text name="partner.contact.details" />
					</legend>
					
                	<div class="col-xs-12 col-sm-6">
						<s:textfield name="primaryContactName" key="partner.primaryContactName" id="primaryContactName" cssClass="form-control" maxlength="100" tabindex="11"/>
						<s:textfield name="primaryContactDesignation" key="partner.primaryContactDesignation" id="primaryContactDesignation" cssClass="form-control" maxlength="100" tabindex="12"/>
						<s:textfield name="primaryContactNumber" key="partner.primaryContactNumber" id="primaryContactNumber" onkeypress="return isNaturalInteger(event);" cssClass="form-control" maxlength="15" tabindex="13"/>
						<s:textfield name="primaryEmailAddress" key="partner.primaryEmailAddress" id="primaryEmailAddress" cssClass="form-control" maxlength="225" tabindex="14"/>
						<s:textfield name="helpDesk" key="partner.helpDesk" id="helpDesk" cssClass="form-control" maxlength="15" tabindex="15"/>
						<s:textfield name="webSiteUrl" key="partner.webSiteUrl" id="webSiteUrl" cssClass="form-control" maxlength="50" tabindex="16"/>
					</div>
					
					<div class="col-xs-12 col-sm-6">
						<s:textfield name="secondaryContactName" key="partner.secondaryContactName" id="secondaryContactName" cssClass="form-control" maxlength="100" tabindex="17"/>
						<s:textfield name="secondaryContactDesignation" key="partner.secondaryContactDesignation" id="secondaryContactDesignation" cssClass="form-control" maxlength="50" tabindex="18"/>
						<s:textfield name="secondaryContactNumber" key="partner.secondaryContactNumber" id="secondaryContactNumber" onkeypress="return isNaturalInteger(event);" cssClass="form-control" maxlength="15" tabindex="19"/>
						<s:textfield name="secondaryEmailAddress" key="partner.secondaryEmailAddress" id="secondaryEmailAddress" cssClass="form-control" maxlength="225" tabindex="20"/>
						<s:textfield name="faxNumber" key="partner.faxNumber" id="faxNumber" cssClass="form-control" maxlength="15" tabindex="21"/>
					</div>
				</fieldset>
			</div>
			
			<div class="row">
            	<div class="col-xs-12" align="center">
                	<button type="submit" class="btn btn-primary btn-sm" id="btnSave" role="submit" tabindex="22" formaction="${pageContext.request.contextPath}/pd/partner/partner/${id}" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" tabindex="23" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/partner/partner/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.back"/></button>
                </div>
            </div>
		</s:form>
	</div>
</div>

<%@ include file="partnerutility.jsp" %>
<script type="text/javascript">
  
    $(function(){
        onCountryChange();
        onStateChange();
    	autoCompleteForStatus();
    });
    
 	 function validateForm() {
    	return verifyUniquenessOnSubmit('partnerName','update','<s:property value="ID"/>','com.elitecore.corenetvertex.pd.partner.PartnerData','','');
       } 
</script>
