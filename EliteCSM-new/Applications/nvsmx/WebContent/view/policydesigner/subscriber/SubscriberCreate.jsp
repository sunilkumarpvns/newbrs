<%@page import="com.elitecore.corenetvertex.constants.CustomerType"%>
<%@page import="com.elitecore.corenetvertex.constants.QuotaProfileType"%>
<%@page import="com.elitecore.corenetvertex.constants.SubscriberStatus"%>
<%@page import="com.elitecore.corenetvertex.pkg.PkgMode"%>
<%@page import="com.elitecore.corenetvertex.pkg.PkgType"%>
<%@page import="com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage"%>
<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@page import="java.util.List"%>
<%@ page import="com.elitecore.corenetvertex.pm.offer.ProductOffer" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<html><head><style>
.package{
padding-left:30px;
padding-right:0px;
}
button.ui-datepicker-current { 
	display: none; 
}
</style></head></html>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<s:text name="subscriber.add" />
		</h3>
	</div>

	<div class="panel-body">
	
		<s:form action="policydesigner/subscriber/Subscriber/create"
			cssClass="form-horizontal form-group-sm " labelCssClass="col-xs-4 col-sm-5 text-right" elementCssClass="col-xs-8 col-sm-7" theme="bootstrap" validate="true" validator="validateForm()">
			<s:token />
			<div class="row">
				<div class="col-xs-12 col-sm-6 col-lg-4">
					<s:textfield name="subscriber.subscriberIdentity" key="subscriber.id" cssClass="form-control focusElement" maxlength="255"/>
					<div class="col-xs-11">
					<s:select name="subscriber.imsPackage" key="subscriber.imspackage" id="imsPackages" listValue="name" 
					listKey="name" listTitle="name" list="imsLivePackages" cssClass="form-control" elementCssClass="col-xs-8 col-sm-7 package" headerKey="" headerValue="-- Select --" />
					</div>
					<div  class="col-xs-1" style="padding-top:4px;" id="reloadImsPkg">
					<button type="button" class="btn btn-default btn-xs" role="button" id="btnRestore" onclick="reloadImsRecords();">
					<span class="glyphicon glyphicon-refresh" title="Refresh"></span>
					</button>
					</div>
					<div class="col-xs-1" style="padding-top:4px;display:none;" id="progressIms" >
					<img src='${pageContext.request.contextPath}/images/progress.gif' style="width:200%;" >
					</div>
				</div>
				<div class="col-xs-12 col-sm-6 col-lg-4">
					<s:select name="subscriber.subscriberMode" id="subscriberMode" key="subscriber.subscribermode" list="@com.elitecore.corenetvertex.spr.data.SPRInfo$SubscriberMode@values()" cssClass="form-control" onchange="handleChange();" />
					<s:select name="subscriber.syInterface" id="syinterface" key="subscriber.syinterface" list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" cssClass="form-control" listKey="isBooleanValue()" listValue="getStringNameBoolean()"/> 
				</div>

				<div class="col-xs-12 col-sm-6 col-lg-4">
					<div class="col-xs-11">
					<s:select name="subscriber.productOffer" key="subscriber.productoffer" id="packages" cssClass="form-control select2" listValue="name" listKey="name" listTitle="name" list="productOffers"  elementCssClass="col-xs-8 col-sm-7 package" headerKey="" headerValue="-- Select --"
							  cssStyle="width:100%"	  />
					</div>
					<div class="col-xs-1" style="padding-top:4px;" id="reloadPkg">
					<button type="button" class="btn btn-default btn-xs" role="button" id="btnRestore" onclick="reloadRecords();">
					<span class="glyphicon glyphicon-refresh" title="Refresh"></span>
					</button>
					</div>
					<div class="col-xs-1" style="padding-top:4px;display:none;" id="progress" >
					<img src='${pageContext.request.contextPath}/images/progress.gif' style="width:200%;" >
					</div>
				</div>
			</div>
			
			<div class="row">	
				<fieldset class="fieldSet-line">
					<legend>
						<s:text name="subscriber.identity.attributes"/>
					</legend>
					<div class="row" id="identityAttributesDetailContent">
						<div>
							<div class="col-sm-4">
								<s:textfield name="subscriber.msisdn" key="subscriber.msisdn" cssClass="form-control" maxlength="15" id="msisdn"/>
								<s:textfield name="subscriber.imsi" key="subscriber.imsi" cssClass="form-control" maxlength="100"/>
								<s:textfield name="subscriber.imei" key="subscriber.imei" cssClass="form-control" maxlength="100"/>
								<s:textfield name="subscriber.userName" key="subscriber.name" cssClass="form-control" maxlength="255"/>
							</div>
							<div class="col-sm-4">
								<s:textfield name="subscriber.cui" key="subscriber.cui" cssClass="form-control" maxlength="255"/>
								<s:textfield name="subscriber.mac" key="subscriber.mac" cssClass="form-control" maxlength="100"/>
								<s:textfield name="subscriber.meid" key="subscriber.meid" cssClass="form-control" maxlength="100"/>
							</div>
							<div class="col-sm-4">
								<s:textfield name="subscriber.eui64" key="subscriber.eui64" cssClass="form-control" maxlength="100"/>
								<s:textfield name="subscriber.modifiedEui64" key="subscriber.modifiedeui64" cssClass="form-control" maxlength="100"/>
								<s:textfield name="subscriber.sipURL" key="subscriber.sipurl" cssClass="form-control" maxlength="200"/>
							</div>
						</div>
					</div>
				</fieldset>
			</div>

			<div class="row">
				<fieldset class="fieldSet-line">
					<legend>
						<s:text name="subscriber.subscription"/>
					</legend>
					<div class="row" id="subscriptionDetailContent">
						<div>
							<div class="col-sm-4">
								<s:textfield name="subscriber.customerType" key="subscriber.customertype"
											 cssClass="form-control" type="text" id="customerType" value="Prepaid"
											 maxlength="10"/>
								<s:textfield name="subscriber.status" key="subscriber.status" cssClass="form-control"
											 type="text" id="status" maxlength="24"/>
								<s:datepicker name="subscriber.expiryDate" key="subscriber.expirydate"
											   parentTheme="bootstrap" changeMonth="true" changeYear="true"
											   cssClass="form-control"
											   showAnim="slideDown" duration="fast" showOn="focus"
											   placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy"
											   timepicker="true" timepickerFormat="HH:mm:ss"
											   id="expiryDate" readonly="true" theme="bootstrap"/>
							</div>
							<div class="col-sm-4">
								<s:textfield name="subscriber.parentId" key="subscriber.parentid"
											 cssClass="form-control" maxlength="255"/>
								<s:textfield name="subscriber.groupName" key="subscriber.groupname"
											 cssClass="form-control" maxlength="255"/>

							</div>
							<div class="col-sm-4">
								<s:textfield name="subscriber.arpu" key="subscriber.arpu" cssClass="form-control"
											 maxlength="20" onkeypress="return isNaturalInteger(event);" type="number"/>
								<s:textfield name="subscriber.billingDate" key="subscriber.billingdate"
											 cssClass="form-control" maxlength="2" placeholder="1-28"
											 onkeypress="return isNaturalInteger(event);" type="number" />
							</div>
						</div>
					</div>
				</fieldset>
			</div>

			<div class="row">
				<fieldset class="fieldSet-line">
					<legend>
						<s:text name="subscriber.payg.roadming"/>
					</legend>
					<div class="row" id="paygDetailContent">
						<div>
							<div class="col-sm-4">

								<s:select name="subscriber.paygInternationalDataRoaming" id="paygRoaming" key="subscriber.international.data"
										  list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
										  cssClass="form-control" listKey="isBooleanValue()" listValue="getDisplayBooleanValue()"/>
							</div>
						</div>
					</div>
				</fieldset>
			</div>
			
			<div class="row" >
				<fieldset class="fieldSet-line">
					<legend>
						<s:text name="subscriber.personal"/>
					</legend>
					<div class="row" id="personalDetailContent">
						<div>
							<div class="col-sm-4">
								<s:textfield name="subscriber.email" key="subscriber.email" cssClass="form-control" maxlength="100"/>
								<s:textfield name="subscriber.password" key="subscriber.password" cssClass="form-control" maxlength="100" />
								<s:select name="subscriber.passwordCheck" id="passwordCheck" key="subscriber.passwordcheck" list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" cssClass="form-control" listKey="isBooleanValue()" listValue="getStringNameBoolean()"/>
							</div>
							<div class="col-sm-4">
								<s:select name="subscriber.encryptionType" id="encryptionType" key="subscriber.encryptiontype" list="@com.elitecore.corenetvertex.constants.PasswordEncryptionType@values()" cssClass="form-control" listKey="val" listValue="displayVal" data-options='{"mode": "datebox", "useNewStyle":true}'/>
								<s:datepicker name="subscriber.birthdate"  key="subscriber.birthdate" parentTheme="bootstrap"
								changeMonth="true" changeYear="true" cssClass="form-control nobackground" showAnim="slideDown" 
								duration="fast" showOn="focus" placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy"
								id="birthDate" readonly="true"  
								maxDate="0"/>
								<s:textfield name="subscriber.phone" key="subscriber.phone" cssClass="form-control" maxlength="15" />
							</div>
							<div class="col-sm-4">
								<s:textfield  name="subscriber.country" key="subscriber.country" cssClass="form-control"   type="text" id="country"  />
								<s:textfield name="subscriber.city" key="subscriber.city" cssClass="form-control" maxlength="20"/>
								<s:textfield name="subscriber.area" key="subscriber.area" cssClass="form-control" maxlength="20"/>
								<s:textfield name="subscriber.zone" key="subscriber.zone" cssClass="form-control" maxlength="20"/>
							</div>
						</div>
					</div>
				</fieldset>
			</div>
		
			<div class="row">	
				<fieldset class="fieldSet-line">
					<legend>
						<s:text name="subscriber.professional"/>
					</legend>
					<div class="row" id="professionalDetailContent">
						<div>
							<div class="col-sm-4">
								<s:textfield name="subscriber.company" key="subscriber.company" cssClass="form-control" maxlength="512"/>
								<s:textfield name="subscriber.cadre" key="subscriber.cadre" cssClass="form-control" maxlength="5"/>
							</div>
							<div class="col-sm-4">
								<s:textfield name="subscriber.department" key="subscriber.department" cssClass="form-control" maxlength="20"/>
							</div>
							<div class="col-sm-4">
								<s:textfield name="subscriber.role" key="subscriber.role" cssClass="form-control" maxlength="20"/>
							</div>
						</div>
					</div>
				</fieldset>
			</div>
			
			<div class="row">	
				<fieldset class="fieldSet-line">
					<legend>
						<s:text name="subscriber.other"/>
					</legend>
					<div class="row" id="otherDetailContent">
						<div>
							<div class="col-sm-4">
								<s:select  name="subscriber.subscriberLevelMetering" value="subscriber.subscriberLevelMetering" key="subscriber.subscriberlevelmetering" cssClass="form-control" list="@com.elitecore.corenetvertex.spr.data.SPRInfo$SubscriberLevelMetering@values()" listKey="name()" listValue="getText(status)" /> 
								<s:textfield name="subscriber.param1" key="subscriber.param1" cssClass="form-control" />
								<s:textfield name="subscriber.callingStationId" key="subscriber.callingstationid" cssClass="form-control" />
								<s:textfield name="subscriber.billingAccountId" key="subscriber.billing.account" cssClass="form-control" maxlength="100" />
							</div>
							<div class="col-sm-4">
								<s:textfield name="subscriber.param2" key="subscriber.param2" cssClass="form-control" />
								<s:textfield name="subscriber.param3" key="subscriber.param3" cssClass="form-control" />
								<s:textfield name="subscriber.framedIp" key="subscriber.framedip" cssClass="form-control" />
								<s:textfield name="subscriber.serviceInstanceId" key="subscriber.service.instance" cssClass="form-control" maxlength="100" />
							</div>
							<div class="col-sm-4">
								<s:textfield name="subscriber.param4" key="subscriber.param4" cssClass="form-control" />
								<s:textfield name="subscriber.param5" key="subscriber.param5" cssClass="form-control" />
								<s:textfield name="subscriber.nasPortId" key="subscriber.nasportid" cssClass="form-control" />
							</div>
						</div>
					</div>
				</fieldset>
			</div>
			<div align="center">
				<s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
			</div>	
		</s:form>
	</div>
</div>

<% List<ProductOffer> dataPackages = (List<ProductOffer>) request.getAttribute(Attributes.DATA_PACKAGES);%>
<% List<ProductOffer> livePackages = (List<ProductOffer>) request.getAttribute(Attributes.DATA_PACKAGES_LIVE); %>
<% List<IMSPackage> imsPackages = (List<IMSPackage>) request.getAttribute(Attributes.IMS_PACKAGES); %>
<% List<IMSPackage> imsLivePackages = (List<IMSPackage>) request.getAttribute(Attributes.IMS_PACKAGES_LIVE); %>

<script type="text/javascript">
$(document).ready(function(){
	autoCompleteForCustomerType(); 
	autoCompleteForStatus();
	autoCompleteForCountry();
	fillPackageList();
	fillIMSPackageList();
    $(".select2").select2();

});

function autoCompleteForCustomerType(){
	$('#customerType').autocomplete();
	var optionsForCustomerType = ['<%=CustomerType.PREPAID.val%>','<%=CustomerType.POSTPAID.val%>'];
	 commonAutoComplete("customerType",optionsForCustomerType);
}
function autoCompleteForStatus(){
	$('#status').autocomplete();
	var optionsForStatus = ['<%=SubscriberStatus.ACTIVE.name()%>','<%=SubscriberStatus.INACTIVE.name()%>'];
	commonAutoComplete("status",optionsForStatus);
}
function autoCompleteForCountry(){
	$('#country').autocomplete();
	var optionsForCountry = "${countryNames}";
	commonAutoComplete("country",optionsForCountry.split(','));
}
function fillPackageList() {
	var subscriberMode = $("#subscriberMode").val();
	if(subscriberMode=='TEST'){
	    setPackages();
	}
	if(subscriberMode=='LIVE'){
	    setLivePackages();
	}

}

function fillIMSPackageList() {
	$('#imsPackages').empty();
	var subscriberMode = $("#subscriberMode").val();
	if(subscriberMode=='TEST'){			
		setImsPackages();
	}
	if(subscriberMode=='LIVE'){			
		setImsLivePackages();
	}
}

function setLivePackages(){
    var selectedProductOffer = $("#packages").val();
    $('#packages').empty();
    $('#packages').append(new Option('-- Select --',''));

	<%int size = livePackages.size();
      for (int i=0;i<size;i++) {
		ProductOffer liveProductOffer = livePackages.get(i);
		String productValue = liveProductOffer.getName();
		String optionProductOffer = liveProductOffer.getName() + " (" +liveProductOffer.getCurrency() + ")";
	%>
    if(isNullOrEmpty(selectedProductOffer)) {
        $('#packages').append(new Option('<%= optionProductOffer %>', '<%= productValue %>'));
    }else{
        if(selectedProductOffer == '<%=productValue%>'){
            $('#packages').append(new Option('<%= optionProductOffer %>', '<%= productValue %>',false,true));
        }else{
            $('#packages').append(new Option('<%= optionProductOffer %>', '<%= productValue %>'));
        }
    }

	<%
	  }
    %>
    $('#packages').select2();
}

function setImsLivePackages(){
	$('#imsPackages').append(new Option('-- Select --',''));
	<% for (int i=0;i<imsLivePackages.size();i++) {				
		IMSPackage pkgData = imsLivePackages.get(i); 
	%>
		$('#imsPackages').append(new Option('<%= pkgData.getName() %>','<%= pkgData.getName() %>'));
	<%	
	  }
    %>
}

function setImsPackages(){
	$('#imsPackages').append(new Option('-- Select --',' '));
	<%  for (int i=0;i<imsPackages.size();i++) {				
		IMSPackage pkgData = imsPackages.get(i); 
	%>
		$('#imsPackages').append(new Option('<%= pkgData.getName() %>','<%= pkgData.getName() %>'));
	<%	
	  }
    %> 
}

function setPackages() {
    var selectedProductOffer = $("#packages").val();
    $('#packages').empty();
    $('#packages').append(new Option('-- Select --',''));
	<%	for (int i=0;i<dataPackages.size();i++) {
	    ProductOffer productOffer = dataPackages.get(i);
	    String productValue = productOffer.getName();
	    String optionProductOffer = productOffer.getName() + " (" +productOffer.getCurrency() + ")";
	%>
	   if(isNullOrEmpty(selectedProductOffer)) {
           $('#packages').append(new Option('<%= optionProductOffer %>', '<%= productValue %>'));
       }else{
	       if(selectedProductOffer == '<%=productValue%>'){
               $('#packages').append(new Option('<%= optionProductOffer %>', '<%= productValue %>',false,true));
		   }else{
               $('#packages').append(new Option('<%= optionProductOffer %>', '<%= productValue %>'));
		   }
	   }
	<%
	  }
	%>
}



function reloadLivePackages(liveProductOffers,selectedProductOffer){
	 $('#packages').empty();
    $('#packages').append(new Option('-- Select --',''));
	 if(typeof  liveProductOffers === "undefined"){
	     return false;
	 }
    for(var i=0;i<liveProductOffers.length;i++){
	     var optionProductOffer = liveProductOffers[i].name + " (" + liveProductOffers[i].currency + ")";
	     var productValue = liveProductOffers[i].name;
	     if(isNullOrEmpty(selectedProductOffer)){
             $('#packages').append(new Option(optionProductOffer,productValue));
		 }else{
	         if(selectedProductOffer == productValue){
                 $('#packages').append(new Option(optionProductOffer,productValue,false,true));
			 }else{
                 $('#packages').append(new Option(optionProductOffer,productValue));
			 }
		 }

    }
}

function reloadPackages(productOffers,selectedProductOffer) {
	$('#packages').empty();
    $('#packages').append(new Option('-- Select --',''));
	for(var i=0;i<productOffers.length;i++) {
		var optionProductOffer = productOffers[i].name + " (" + productOffers[i].currency + ")";
		var productValue = productOffers[i].name;
	    if(isNullOrEmpty(selectedProductOffer)){
            $('#packages').append(new Option(optionProductOffer,productValue));
		}else{
	        if(selectedProductOffer == productValue){
                $('#packages').append(new Option(optionProductOffer,productValue,false,true));
			}else{
                $('#packages').append(new Option(optionProductOffer,productValue));
			}
		}

	}
}

//RELOAD IMS PACKAGES

function reloadImsLivePackages(imsLivePackages){
	 $('#imsPackages').empty();
	$('#imsPackages').append(new Option('-- Select --',''));
    for(var i=0;i<imsLivePackages.length;i++){
   	 $('#imsPackages').append(new Option(imsLivePackages[i].name,imsLivePackages[i].name));
    }
}

function reloadImsPackages(imsPackages){
	 $('#imsPackages').empty();
	$('#imsPackages').append(new Option('-- Select --',''));
     for(var i=0;i<imsPackages.length;i++){
    	 $('#imsPackages').append(new Option(imsPackages[i].name,imsPackages[i].name));
     }
}

function handleChange() {
	console.log("handlechange() flag : "+flag+", imsflag: "+flagIms);
	if(flag == true){
		reloadAllPackages(jsonData);
	}else{
		fillPackageList();
	}
	
	if(flagIms == true){
		reloadAllImsPackages(jsonDataIms);
	}else{
		fillIMSPackageList();
	}
    $(".select2").select2();
}

var jsonData ;
var flag = false;
function reloadRecords(){
	$.ajax({
	    type 		: "POST",
	    url 		: "${pageContext.request.contextPath}/ajax/pkgReload/reloadProductOffers",
	    beforeSend: function(){
	    	$('#reloadPkg').attr('style','display:none');
	      	$('#progress').show();
        }, 
	    success: function(data){
	    	jsonData = data;
	    	flag = true;
	    	reloadAllPackages(data);
	    	$('#progress').hide();
	    	$('#reloadPkg').attr('style','padding-top:4px;');
	    },
	    error 		: function(xhr,status, errmsg) {
	    	console.log(status);
	    }
	}); 
}

 function reloadAllPackages(pkgs){
     var subscriberMode = $("#subscriberMode").val();
     var selectedProductOffer = $("#packages").val();
     if(subscriberMode=='TEST'){
         reloadPackages(pkgs.productOffers,selectedProductOffer);
     }
     if(subscriberMode=='LIVE'){
         reloadLivePackages(pkgs.liveProductOffers,selectedProductOffer);
     }
 }



//IMS PACKAGE AJAX CALL
var jsonDataIms ;
var flagIms = false;
function reloadImsRecords(){
	$.ajax({
	    type 		: "POST",
	    url 		: "${pageContext.request.contextPath}/ajax/pkgReload/reloadImsPackages",
	    beforeSend: function(){
	    	$('#reloadImsPkg').attr('style','display:none');
	      	$('#progressIms').show();
        }, 
	    success 	: function(data){
	    	jsonDataIms = data;
	    	flagIms = true;
	    	reloadAllImsPackages(data);
	    	$('#progressIms').hide();
	    	$('#reloadImsPkg').attr('style','padding-top:4px;');
	    },
	    error 		: function(xhr,status, errmsg) {
	    	console.log(status);
	    }
	}); 
}
 function reloadAllImsPackages(imsPkgs){
	 var subscriberMode = $("#subscriberMode").val();
		if(subscriberMode=='TEST'){
			reloadImsPackages(imsPkgs.imsPackages);
		}
		if(subscriberMode=='LIVE'){
			reloadImsLivePackages(imsPkgs.imsLivePackages);
		}
 }
 
 function validateForm(){
	 clearErrorMessagesById('packages');
	 var syInterfaceVal = $("#syinterface").val();
	 var dataPackageSelected = $('#packages').val();
	 var msisdn = $('#msisdn').val();
     if(syInterfaceVal == 'false'){
         <% int allsize = dataPackages.size();
             for (int i=0;i<allsize;i++) {
            ProductOffer productOffer = dataPackages.get(i);

            if(productOffer.getDataServicePkgData()==null){
                continue;
            }
        %>
         var pkgName = '<%=productOffer.getName()%>';
         if(dataPackageSelected == pkgName){
             var quotaProfileType = '<%=productOffer.getDataServicePkgData() == null ? null : productOffer.getDataServicePkgData().getQuotaProfileType().name()%>';
             var actualType = '<%=QuotaProfileType.SY_COUNTER_BASED.name()%>';
             if(quotaProfileType == actualType){
                 setError("packages","<s:text name='wrong.package.selected' />");
                 return false;
             }
         }
         <%}%>

	 }
	 if(msisdn.trim().length !=0 ){
		 if(msisdn.trim().length < 5|| msisdn.trim().length > 15){
			setError("msisdn","<s:text name='subscriber.msisdn.invalid' />");
			return false;
		 }
     }
 }
</script>
