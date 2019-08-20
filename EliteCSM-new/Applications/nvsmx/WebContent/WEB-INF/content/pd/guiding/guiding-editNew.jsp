<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<%@page import="com.elitecore.corenetvertex.constants.TrafficType"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="guiding.create" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/guiding" action="guiding" id="guidingCreateFormId" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm('create','')">
            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-6">
					 <s:textfield name="guidingName" key="guiding.guidingname" id="guidingName" cssClass="form-control focusElement" maxlength="100" tabindex="1"/>                
                  	 <s:select name="lobId" key="guiding.loblist" id="lobListId" list="lobDataList" listKey="id" listValue="getAlias()" cssClass="form-control select2" tabindex="1" />                	 
                     <s:select name="accountIdentifierType" key="guiding.accountidentifiertype" id="accountIdentifierTypeId" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.AccountIdentifierType@values()" listKey="name()" listValue="getValue()" headerKey="" headerValue="--Select--" tabindex="3" />                    
                     <s:select name="accountId" key="guiding.accountnumber" id="accountNumberId" onchange="setPartnerName()" list="accountDataList" listKey="id"  listValue="getName()" cssClass="form-control" headerKey="" headerValue="--Select--" tabindex="5" />                     
                     <s:datepicker name="startDate" key="guiding.startdate" 	parentTheme="bootstrap" changeMonth="true" changeYear="true" cssClass="form-control"
								 	showAnim="slideDown" duration="fast" showOn="focus" placeholder="DD-MON-YYYY" displayFormat="dd-M-yy" timepicker="false" timepickerFormat="HH:mm:ss"
								 	id="startDateId" readonly="true"  tabindex="7"/>   
                 	</div>
                  	<div class="col-sm-9 col-lg-6">
					<s:select name="serviceId" key="guiding.servicelist" id="serviceList"  list="serviceDataList" listKey="id" listValue="getAlias()" cssClass="form-control select2" tabindex="2" />
					<s:textfield name="accountIdentifierValue" key="guiding.accountidentifiervalue" id="accountIdentifierValueId" cssClass="form-control focusElement" maxlength="100" tabindex="4"/>
					 <s:textfield name="trafficType" key="guiding.traffictype" id="trafficTypeId" cssClass="form-control focusElement" maxlength="100" tabindex="6"/>
				    <s:label id="partnerNameId" key="guiding.partnername" name="partnerName" cssClass="control-label light-text"></s:label>
                 	<s:datepicker 	name="endDate" key="guiding.enddate" 	parentTheme="bootstrap" changeMonth="true" changeYear="true" cssClass="form-control"
								 	showAnim="slideDown" duration="fast" showOn="focus" placeholder="DD-MON-YYYY" displayFormat="dd-M-yy" timepicker="false" timepickerFormat="HH:mm:ss"  
								  	id="endDateId" readonly="true"  tabindex="8"/>	
                  	</div>
                  
                <div class="row">
                    <div class="col-xs-12" align="center">
                        <s:submit type="button" cssClass="btn  btn-sm btn-primary" id="btnSave" role="button" tabindex="10"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                        <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/guiding/guiding'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>
                    </div>
                </div>
        	</div>
        </s:form>
    </div>
</div>

<%@include file="guiding-utility.jsp"%>
<script type="text/javascript">
$(document).ready(function(){
	autoCompleteForTrafficType(); 
	setPartnerName();
});
</script>
  

 