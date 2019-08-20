<%@page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@page import="java.util.Iterator"%>
<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData" %>
<%@ page import="com.elitecore.netvertexsm.web.gateway.profile.form.CreateProfileForm" %>
<%@ page import="com.elitecore.netvertexsm.web.gateway.profile.form.EditGatewayProfileForm" %>
<%@ page import="com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager" %>
<%@ page import="java.util.List" %> 
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData"%>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData"%>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>

<% EditGatewayProfileForm gatewayProfileData = (EditGatewayProfileForm)request.getAttribute("editGatewayProfileForm");
	int standard = gatewayProfileData.getSupportedStandard();
%>

<%@page import="com.elitecore.corenetvertex.constants.PCRFKeyType"%>
<%@page import="com.elitecore.corenetvertex.constants.ChargingRuleInstallMode"%><script language="javascript" src="<%=basePath%>/js/data.js"></script>

<style> 
.light-btn {  border:medium none; font-family: Arial; font-size: 12px; color: #FFFFFF; background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg'); font-weight: bold}
.rightborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 1px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.bottomborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 1px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.allborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.noborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.plcKey { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
</style> 

<script type="text/javascript">

var mainArray = new Array();
var refArray = new Array();
var count = 0;
var count1 = 0;

function openMapping(index) {
	$.fx.speeds._default = 1000;
	document.getElementById("popupfeildMapping").style.visibility = "visible";		
	$( "#popupfeildMapping" ).dialog({
		modal: true,
		autoOpen: false,		
		height: 300,
		width: 550,				
		buttons:{					
            'Add': function() {	
				var name = $('#packetMapId').text(); 
				var nameId = $('#packetMapId').val();
				var cond = $('#cond').val();
				var selectedItems = document.getElementsByName("mapList");
				 
	          	refArray[count1++] = name;
     			var i = 0;							
				var flag = true;												 	

				if(flag){	

                    for(var i=0; i<=selectedItems.length; i++) {
    					if(selectedItems[i].checked == true) {
                            var labelVal = selectedItems[i].value;
                            var labelid = selectedItems[i].id;

                            if(index == '1')
	                            $("#mapReq tr:last").after("<tr><td class='tblfirstcol' width='30%'>" + labelVal +"<input type='hidden' name='packetMapId' value='" + labelid + "'/></td><td class='tblrows' width='30%'><input class='noborder' type='text' name='condition' maxlength='200' size='70' value='" + cond + "'/><input type='hidden' name='orderNumber' value='0'/></td><td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
                            else
                            	$("#mapRes tr:last").after("<tr><td class='tblfirstcol' width='30%'>" + labelVal +"<input type='hidden' name='packetMapId' value='" + labelid + "'/></td><td class='tblrows' width='30%'><input class='noborder' type='text' name='condition' maxlength='200' size='70' value='" + cond + "'/><input type='hidden' name='orderNumber' value='0'/></td><td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
                            
                            selectedItems[i].checked=false;
                        }
    					
    					document.getElementById("cond").value = "";
         			
	         			$('#map td img.delete').on('click',function() {

	       				 //var $td= $(this).parents('tr').children('td');			
	       				var removalVal = $(this).closest('tr').find('td:eq(0)').text(); 							
	       				for(var d=0;d<count;d++){
	       					var currentVal = mainArray[d];					
	       					if(currentVal == removalVal){
	       						mainArray[d] = '  ';
	       						break;
	       					}
	       				}
	       				var removalRefVal = $(this).closest('tr').find('td:eq(1)').text();
	       				for(var f=0;f<count1;f++){
							var currentRefVal = refArray[f];
							if(currentRefVal == removalRefVal){
								refArray[f] = "";
								break;
							}
		       			}								
	       				 $(this).parent().parent().remove(); });		         						          					          	
		          		mainArray[count++] = name;						          					          		          					          							          					          		
		          		$(this).dialog('close');	
		          		 			          		         		
		         	}	
         		}
            },                
		    Cancel: function() {
            	$(this).dialog('close');
        	}
        },
    	open: function() {
        	
    	},
    	close: function() {
    		
    	}				
	});
	$( "#popupfeildMapping" ).dialog("open");
}

$(document).ready(function(){
	verifyName();
	supportedVendorList();
});

function addRow(type) {
	if(type=='static') 
		$('<tr><td class="allborder"><input class="noborder" type="text" name="attributeS" maxlength="200" size="20"/></td><td class="tblrows"><input class="plcKey" type="text" name="policyKeyS" maxlength="1024" size="20"/></td><td class="tblrows"><input class="noborder" type="text" name="defaultValueS" maxlength="1024" size="20"/></td><td class="tblrows"><input class="noborder" type="text" name="valueMappingS" maxlength="1024" size="25"/></td><td class="tblrows" align="left" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" /></td></tr>').appendTo('#'+type);
	else
		$('<tr><td class="allborder"><input class="noborder" type="text" name="attributeD" maxlength="200" size="20"/></td><td class="tblrows"><input class="plcKey" type="text" name="policyKeyD" maxlength="1024" size="20"/></td><td class="tblrows"><input class="noborder" type="text" name="defaultValueD" maxlength="1024" size="20"/></td><td class="tblrows"><input class="noborder" type="text" name="valueMappingD" maxlength="1024" size="25"/></td><td class="tblrows" align="left" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" /></td></tr>').appendTo('#'+type);		
	policyKey();
}

function policyKey() {
	var mappingTypeArray = new Array();	
	mappingTypeArray[0] = "<%=PCRFKeyType.PCC_RULE.getVal()%>";
	$.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.POLICY_KEY%>,propertyName:"serviceName",mappingTypeArray:mappingTypeArray}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		var dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split(", ");
		commonAutoCompleteUsingCssClass("td input.plcKey",dbFieldArray);
	 
		return dbFieldArray;
	});
}

var isValidName;
function validate(){
	if(!isValidName) {
		alert('Enter Valid Gateway Profile Name.');
		document.forms[0].name.focus();
		return;
	}else{
		document.forms[0].submit();
	}
}

function verifyFormat (){
	var searchName = document.getElementById("name").value;
	callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.PROFILE%>',searchName:searchName,mode:'update',id:'<%=gatewayProfileData.getProfileId()%>'},'verifyNameDiv');
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.PROFILE%>',searchName:searchName,mode:'update',id:'<%=gatewayProfileData.getProfileId()%>'},'verifyNameDiv');
}

</script> 	

<table cellpadding="0" cellspacing="0" border="0" width="828" >
  <tr> 
    <td width="10">&nbsp;</td> 
    <td colspan="2"> 
      <table width="100%" border="0" cellspacing="0" cellpadding="0"> 
        <tr> 
          <td width="26" valign="top" rowspan="2"><img src="<%=basePath%>/images/left-curve.jpg"></td> 
          <td background="<%=basePath%>/images/header-gradient.jpg" width="133" rowspan="2" align="center" class="page-header">
          	<bean:message bundle="gatewayResources" key="gatewy.profile.header"/></td> 
          <td width="32" rowspan="2"><img src="<%=basePath%>/images/right-curve.jpg"></td> 
          <td width="633"></td> 
        </tr> 
        <tr> 
          <td width="633" valign="bottom"><img src="<%=basePath%>/images/line.jpg"></td> 
        </tr> 
      </table> 
    </td> 
  </tr> 
  <tr> 
    <td width="10" class="small-gap">&nbsp;</td> 
    <td class="small-gap" colspan="2">&nbsp;</td> 
  </tr>
  <tr>
  <td width="10">&nbsp;</td> 
  <td>
<table width="828" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="10" class="small-gap">&nbsp;</td>
    <td class="small-gap" colspan="2">&nbsp;</td>
  </tr>
  <tr> 
    <td></td>
    <td width="773" valign="top" class="box">
    
    
<html:form action="/editGatewayProfile">

<div  style="margin-left: 2.0em;" class="tblheader-bold"><bean:message bundle="gatewayResources" key="gateway.profile.view" /></div>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
		<td valign="top" align="right"> 
<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right">
	<bean:define id="gatewayBean" name="gatewayProfileData" scope="request" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData" />
	<tr>
		<td align="right" class="labeltext" valign="top" class="box" > 
			<table cellpadding="0" cellspacing="0" border="0" width="100%" >	
	          <tr> 
	            <td class="tbllabelcol" width="30%" height="20%"><bean:message  bundle="gatewayResources" key="gateway.profile" /></td>
	            <td class="tblcol" width="20%" height="20%"><bean:write name="gatewayBean" property="profileName"/>&nbsp;</td>
	            <td class="tbllabelcol" width="20%" height="20%"><bean:message  bundle="gatewayResources" key="gateway.gatewaytype" /></td>
	            <td class="tblcol" width="20%" height="20%" ><bean:write name="gatewayBean" property="gatewayType"/>&nbsp;</td>
	          </tr>	          
	           <tr> 
	            <td class="tbllabelcol" width="30%" height="20%"><bean:message  bundle="gatewayResources" key="gateway.commprotocol" /></td>
	            <td class="tblcol" width="20%" height="20%" ><bean:write name="gatewayBean" property="commProtocolData.commProtocolName"/>&nbsp;</td>
	            <td class="tbllabelcol" width="20%" height="20%"><bean:message  bundle="gatewayResources" key="gateway.profile.vendor" /></td>
	            <td class="tblcol" width="20%" height="20%" ><bean:write name="gatewayBean" property="vendorData.vendorName"/>&nbsp;</td>
	          </tr>	         
	          <tr> 
	            <td class="tbllabelcol" width="30%" height="20%"><bean:message  bundle="gatewayResources" key="gateway.profile.firmware" /></td>
	            <td class="tblcol" width="20%" height="20%" ><bean:write name="gatewayBean" property="firmware"/>&nbsp;</td>
	            <td class="tbllabelcol" width="20%" height="20%"><bean:message  bundle="gatewayResources" key="gateway.profile.maxthroughput" /></td>
	            <td class="tblcol" width="20%" height="20%" ><bean:write name="gatewayBean" property="maxThroughput"/>&nbsp;</td>
	          </tr>
	          <tr>
	          	<td class="tbllabelcol" width="30%" height="20%"><bean:message  bundle="gatewayResources" key="gateway.profile.bufferbw" /></td>
	            <td class="tblcol" width="20%" height="20%"><bean:write name="gatewayBean" property="bufferBW"/>&nbsp;</td>
	             <td class="tbllabelcol" width="30%" height="20%"><bean:message  bundle="gatewayResources" key="gateway.profile.maxipcansession" /></td>
	            <td class="tblcol" width="20%" height="20%"><bean:write name="gatewayBean" property="maxIPCANSession"/>&nbsp;</td>
	          </tr>
	        </table>
		</td>
	</tr>
</table>
		</td>
    </tr>
    <tr> 
		<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
	</tr>
</table>

<bean:define id="gatewayProfileBean" name="gatewayProfileData" scope="request" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData" />
<div style="margin-left: 2.0em;" class="tblheader-bold"><bean:message bundle="gatewayResources" key="gateway.profile.updateprofile" /></div>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
		<td valign="top"> 
<table cellpadding="0" cellspacing="0" border="0" width="97%">
	<bean:define id="gatewayProfileBean" name="gatewayProfileData" scope="request" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData" />
	
	<tr>
		<td align="" class="labeltext" valign="top" class="box" > 
        <input type="hidden" name="strCreatedDate" value="<%=gatewayProfileBean.getCreatedDate()%>"/>

			<table width="97%" id="c_tblCrossProductList" border="0" > 				 
				
				<tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.profile"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="gatewayResources" key="gateway.profile"/>','<bean:message bundle="gatewayResources" key="gateway.profile" />')"/>	
					</td> 
					<sm:nvNameField maxLength="60" size="30" value="${gatewayProfileBean.gatewayProfileName}"/>
				 </tr>
				 <tr>
				  	<td align="left" class="labeltext" valign="top" >
				  		<bean:message bundle="gatewayResources" key="gateway.gatewaytype"/>
				  	</td> 
					<td align="left" class="labeltext" valign="top"  >
						<html:select  styleId="gatewayType"  property="gatewayType" tabindex="2"  style="width: 210px">
							<html:option value="Access Gateway">Access Gateway</html:option>
							<html:option value="Application Function">Application Function</html:option>
						</html:select><font color="#FF0000"> *</font> 
					</td>
				  </tr>				  
				 <tr> 
				 	<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.profile.vendor"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" style="width :265px"> 
				 		<html:select name="editGatewayProfileForm" styleId="vendor" property="vendorId" size="1" style="width: 206;" tabindex="2">
							<html:option value="0" bundle="gatewayResources" key="gateway.select" />
							<logic:iterate id="vendorlist"  name="editGatewayProfileForm" property="vendorList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.VendorData">
								<html:option value="<%=Long.toString(vendorlist.getVendorId())%>"><bean:write name="vendorlist" property="vendorName"/></html:option>
							</logic:iterate>
						</html:select>	
					</td>
				 </tr>
				 <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.commprotocol"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" >
						<html:hidden property="commProtocolId"></html:hidden>
						<html:text name="gatewayProfileBean" property="commProtocolData.commProtocolName" style="width :265px" maxlength="60" size="30" readonly="true"></html:text>							
					</td> 
				 </tr>
				 <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.description"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:textarea property="description" cols="50" rows="4" styleId="c_description" style="width :265px" styleClass="input-textarea" tabindex="3" /> 
					</td> 
				 </tr>			
				 <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.profile.firmware"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text property="firmware" maxlength="60" size="30" style="width :265px" styleId="firmware" tabindex="4" /> 
					</td> 
				 </tr>								  
				 <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.profile.maxthroughput"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text property="maxThroughtput" maxlength="60" size="30" style="width :265px" styleId="maxThroughtput" tabindex="5"/>
					</td>
				 </tr>
				 <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.profile.bufferbw"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text property="bufferBW" maxlength="60" size="30" style="width :265px" styleId="bufferBandwidth" tabindex="6"/>
					</td>
				 </tr>
				 <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.profile.maxipcansession"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text property="maxIPCANSession" maxlength="60" size="30" style="width :265px" styleId="maxIPCANSession" tabindex="7"/>
					</td>
				 </tr>		
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="gatewayResources" key="gateway.profile.usagereportingtime"/></td>
						<td align="left" class="labeltext" valign="top" width="32%" > 
					    	<html:select name="editGatewayProfileForm" styleId="usageReportingTime" property="usageReportingTime" style="width: 206;">
						   		<html:option value="NON-CUMULATIVE">Non-Cumulative</html:option>
						   		<html:option value="CUMULATIVE" >Cumulative</html:option> 
							</html:select>      
						</td>
				 	 </tr>	

				<tr id="diameter" >
					<td align="left" class="labeltext" valign="top" width="10%" >
						<bean:message bundle="gatewayResources" key="gateway.profile.supportedvendorlist"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:textarea name="editGatewayProfileForm" styleId="supportedVendorList" style="width :265px" property="diameterSupportedVendorList" rows="3" cols="31" tabindex="7"/></td>
				</tr>								
				
				<tr> 
					<td align="left" class="labeltext" valign="top" width="15%">
						<bean:message bundle="gatewayResources" key="gateway.profile.multichargingruleenabled" />
					</td> 
					<td align="left" class="labeltext" valign="top" width="20%" > 
						<html:select name="editGatewayProfileForm" styleId="multiChargingRuleEnabled" property="multiChargingRuleEnabled" tabindex="2" style="width:120px">
							<%
								ChargingRuleInstallMode[] values = ChargingRuleInstallMode.values();
								for(int i=0;i<values.length;i++){
							%>
								<html:option value='<%=String.valueOf(values[i].getVal())%>'><%=values[i].name()%></html:option>
							<%}%>
						</html:select>
					</td>					
				  </tr>				  					  
			   </table>  
			</td> 
		  </tr>
				  	
				<tr> 
					<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		 		</tr> 
		 
   		 		<tr> 
					<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td> 
		 		</tr>
        		<tr> 
	        		<td valign="middle" >&nbsp;
	        			<html:hidden property="profileId" styleId="profileId" name="editGatewayProfileForm" />
	        		</td> 
            		<td valign="middle" align="left"> 
		        		<input type="button" align="left" onclick="validate();" value="  Update  " class="light-btn" tabindex="10" />
		        		<input type="button" align="left" value="  Cancel  " class="light-btn" onclick="javascript:location.href='<%=basePath%>/initSearchProfile.do?/>'" tabindex="11"/>
		        	</td> 
   		 		</tr>
   		 		 				 
			 </table> 
		</td> 
	</tr>		 				 				  					 
</table>
		</td>
    </tr>
</table>	

</html:form>    	
	</td>
	<td width="168" class="grey-bkgd" valign="top">
      <%@  include file = "GatewayProfileNavigation.jsp" %> 
    </td>
  </tr>
  <tr> 
    <td colspan="3" class="small-gap">&nbsp;</td>
  </tr>
  <tr> 
    <td width="10">&nbsp;</td>
    <td colspan="2" valign="top" align="right"> 
    
      <table width="99%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td width="82%" valign="top"><img src="<%=basePath%>/images/btm-line.jpg"></td>	
          <td align="right" rowspan="2" valign="top"><img src="<%=basePath%>/images/btm-gradient.jpg" width="140" height="23"></td>
        </tr>
        <tr> 
          <td width="82%" valign="top" align="right" class="small-text-grey">Copyright 
            &copy; <a href="http://www.elitecore.com" target="_blank">Elitecore 
            Technologies Pvt.Ltd</a> </td>
        </tr>
      </table>      
    </td>
  </tr>
</table>
</table>

<%@ include file="/jsp/core/includes/common/Footer.jsp" %>