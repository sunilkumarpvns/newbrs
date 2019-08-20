<%@page import="com.elitecore.corenetvertex.spr.data.SPRFields"%>
<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map.Entry" %>


<%
String[] logicalNames = (String[])request.getAttribute("logicalNames");
String[] ldapAttributes = (String[])request.getAttribute("ldapAttributes");
String[] logicalValues = (String[])request.getAttribute("logicalValues");
%>

<script type="text/javascript">

function openPopup() {	
	$.fx.speeds._default = 1000;
	document.getElementById("popupfieldMapping").style.visibility = "visible";		
	$( "#popupfieldMapping" ).dialog({
		modal: true,
		autoOpen: false,		
		height: 150,
		width: 500,		
		buttons:{					
            'Add': function() {
            		var logicalName = $('#logicalName option:selected').text();
					var logicalValue = $('#logicalName option:selected').val();
					var ldapAttribute = $('#ldapAttribute').val();
					if(isNull(logicalValue)){
		     			alert('Logical Name must be specified.');
		     		}else if(isNull(ldapAttribute)){
		     			alert('LDAP Attribute must be specified.');
		     		}else{	
		     			var i = 0;							
						var flag = true;												 	
						if(document.getElementById('mappingtbl').getElementsByTagName('tr').length >= 2){
							
							var existingLogicalNames = document.getElementsByName("logicalNames");
							for(i=0;i<existingLogicalNames.length;i++){
								var value = existingLogicalNames[i].value;
								if(value == logicalValue){
									alert("Mapping with this Logical Name is already present so enter another value for Logical Name");
									flag = false;
									break;
								}
							}
						}								
		         	
						if(flag){
							$("#mappingtbl tr:last").after("<tr><td class='tblfirstcol'>" + logicalName + "<input type='hidden' name = 'logicalNames' value='" + logicalValue + "'/>" +"</td><td class='tblrows'>" + ldapAttribute + "<input type='hidden' name = 'ldapAttributes' value='" + ldapAttribute + "'/>" +"</td><td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");			         				          	
			          		$(this).dialog('close');
			         	}	         				    		         			   				         			          				          		
		         	}		         								
            },                
		    Cancel: function() {
            	$(this).dialog('close');
        	}
        },
    	open: function() {
    		$('#ldapAttribute').val(""); 
    	},
    	close: function() {
    		
    	}				
	});
	$( "#popupfieldMapping" ).dialog("open");
	
}	

$(document).ready(function(){
	setTitle('<bean:message bundle="driverResources" key="spinterface.title"/>');
	<%int j =0;	
	if(logicalNames != null){
	for(j =0;j<logicalNames.length;j++){%>								
		
	var logicalName  = '<%=logicalNames[j]%>';
	var ldapAttribute    = '<%=ldapAttributes[j]%>';								
	var logicalValue = '<%=logicalValues[j]%>';
	
		
		if(logicalName == 'null'){
			logicalName = '';
		}
		
		$("#mappingtbl tr:last").after("<tr><td class='tblfirstcol'>" + logicalName + "<input type='hidden' name = 'logicalNames' value='" + logicalValue + "'/>" +"</td><td class='tblrows'>" + ldapAttribute + "<input type='hidden' name = 'ldapAttributes' value='" + ldapAttribute + "'/>" +"</td><td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
		
		$('#mappingtbl td img.delete').on('click',function() {
			 $(this).parent().parent().remove(); 
		});
		
	<%}}%>	
});

function isMappingExist(){
	var existingLogicalNames = document.getElementsByName("logicalNames");
	if(existingLogicalNames!=null && existingLogicalNames.length>0){
		return true;
	}
	return false;
}

function subscriberIdentityFieldCheck(){
	var existingLogicalNames = document.getElementsByName("logicalNames");
	for(var i=0;i<existingLogicalNames.length;i++){
		if(existingLogicalNames[i].value == "<%=SPRFields.SUBSCRIBER_IDENTITY.name()%>"){
			return true;
		}
	}
	return false;
}

function validate() {
	if(document.forms[0].ldapDsId.value == 0){
		alert('Select atleast one datasource.');
		document.forms[0].ldapDsId.focus();
		return false;
	}else if(isNull(document.forms[0].queryMaxExecTime.value)){
		alert('Max Query Execution Time must be specified.');
		document.forms[0].queryMaxExecTime.focus();
		return false;
	}else if(isEcNaN(document.forms[0].queryMaxExecTime.value)){
		alert('Max Query Execution Time must be numeric.');
		document.forms[0].queryMaxExecTime.focus();
		return false;
	}else if(!isMappingExist()){
		alert('Atleast one LDAP Attribute Mapping must be needed.');
		return false;
	}else if(!subscriberIdentityFieldCheck()){
		alert('SubscriberIdentity Field is mandatory,so add it in LDAP Field Mapping.');
		return false;
	}else if(isNull(document.forms[0].expiryDatePattern.value)){
		alert('Expiry Date Pattern must be specified');
		document.forms[0].expiryDatePattern.focus();
		return false;
	}else{
		return true;
	}		
}


</script> 	 

 
<html:form action="/createLDAPSPInterface" onsubmit="return validate()"> 

<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
 <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
				<bean:message bundle="driverResources" key="spinterface.ldap.create"/>
			</td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="97%" align="right" border="0" > 
				  
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="30%">
						<bean:message bundle="driverResources" key="spinterface.ldap.ldapdatasource"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="spinterface.ldap.ldapdatasource"/>','<bean:message bundle="driverResources" key="spinterface.ldap.ldapdatasource" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="70%" > 
				 		<html:select name="ldapSPInterfaceForm" styleId="ldapDsId" property="ldapDSId" size="1" style="width: 206;" tabindex="1">
							<html:option value="0" bundle="driverResources" key="driver.select" />
							<logic:iterate id="ldapDatasources"  name="ldapSPInterfaceForm" property="ldapDatasourceList" 
								type="com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData">
								<html:option value="<%=Long.toString(ldapDatasources.getLdapDsId())%>">
									<bean:write name="ldapDatasources" property="name"/>
								</html:option>
							</logic:iterate>
						</html:select><font color="#FF0000"> *</font>	
					</td>
				  </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" >
						<bean:message bundle="driverResources" key="spinterface.ldap.expirydatapattern"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="spinterface.ldap.expirydatapattern"/>','<bean:message bundle="driverResources" key="spinterface.ldap.expirydatapattern" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" > 
					<html:text property="expiryDatePattern" styleId="expiryDatePattern" style="width:130px" tabindex="2" maxlength="50" >
					</html:text>
					<font color="#FF0000"> *</font>
					</td> 
				  </tr>				  
				  <tr> 
					<td align="left" class="labeltext" valign="top" >
						<bean:message bundle="driverResources" key="spinterface.ldap.passdecrypttype"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="spinterface.ldap.passdecrypttype"/>','<bean:message bundle="driverResources" key="spinterface.ldap.passdecrypttype" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" > 
					    <html:select property="passwordDecryptType" styleId="passwordDecryptType" style="width:50px" tabindex="3">
							<html:option value="0">0</html:option>
						</html:select><font color="#FF0000"> *</font> 
					</td> 
				  </tr>			
				  <tr> 
					<td align="left" class="labeltext" valign="top" >
						<bean:message bundle="driverResources" key="spinterface.ldap.querymaxexectime"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="spinterface.ldap.querymaxexectime"/>','<bean:message bundle="driverResources" key="spinterface.ldap.querymaxexectime" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top"  > 
					    <html:text property="queryMaxExecTime" maxlength="20" size="5" styleId="queryMaxExecTime" tabindex="4" /><font color="#FF0000"> *</font> 
					</td> 
				  </tr>						  				  				  					
			   </table>  
			</td> 
		  </tr>	 
		  <tr> 
			<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr>
		  <tr> 
          		<td class="tblheader-bold" colspan="4" height="20%" style="padding-left: 2em">
          			<bean:message bundle="driverResources" key="spinterface.ldap.fieldmapping"/>
          		</td>
          </tr>		
          <tr> 
				<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 		  			
				  		  			
	   <!-- add button  -->
		
		  <tr>				 
			<td style="padding-left: 1.6em">				
				<input type="button" id="button" value="   Add   " class="light-btn"  onclick="openPopup();" tabindex="5">
			</td>
		  </tr>
		  
       <!--  display added DB Field Mapping list -->
		            
   		  <tr> 
			<td class="btns-td" valign="middle" colspan="3"> 
			<table cellpadding="0" cellspacing="0" border="0" width="100%"> 
				<tr> 
					<td class="small-gap" colspan="3"> 
						&nbsp;
					</td> 
				</tr> 
				<tr> 
					<td colspan="3">
					<table width="70%" id="mappingtbl" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="tblheader" valign="top" width="15%">
								<bean:message bundle="driverResources" key="spinterface.ldap.logicalname" /></td>
							<td align="center" class="tblheader" valign="top" width="15%">
								<bean:message bundle="driverResources" key="spinterface.ldap.ldapattribute" /></td>
							<td align="center" class="tblheader" valign="top" width="4%">Remove</td>
						</tr>
					</table>
					</td> 
				</tr> 				
			</table> 			
			</td> 
		</tr>

          <tr> 
	        <td class="btns-td" valign="middle" >&nbsp;</td> 
            <td class="btns-td" valign="middle" align="left" > 
		        <input type="submit" align="left"  value="   Create   "  class="light-btn" tabindex="6"/>
		        <input type="button" tabindex="7" align="left" value=" Cancel " class="light-btn" onclick="javascript:location.href='<%=basePath%>/initSearchSPInterface.do?/>'"/>                  
	        </td> 
   		  </tr> 
		</table> 
	  </td> 
	</tr> 
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table> 
</html:form>

<div id="popupfieldMapping" title="<bean:message bundle="driverResources" key="spinterface.ldap.fieldmapping"/>" style="display: none;">	
	<table>	 	 
		<tr>
			<td align="left" class="labeltext" valign="top" >
				<bean:message bundle="driverResources" key="spinterface.ldap.logicalname"/></td>
			<td align="left" class="labeltext" valign="top" width="30%">
				<html:select name="ldapSPInterfaceForm" styleId="logicalName" property="logicalName" size="1" style="width: 180;">
					<html:option value="0" bundle="driverResources" key="driver.select" />
					<%
						for( Entry<String,SPRFields> entry  : SPRFields.getSPRFieldsEntrySet()){
					%>
					<html:option value="<%=entry.getValue().name()%>">
						<%=entry.getValue().displayName%>
					</html:option>
					<% } %>
				</html:select>								      											
			</td>
		</tr>			  	
		<tr>
			<td align="left" class="labeltext" valign="top" width="20%">
				<bean:message bundle="driverResources" key="spinterface.ldap.ldapattribute"/></td>
			<td align="left" class="labeltext" valign="top" width="32%">
				<html:text name="ldapSPInterfaceForm" styleId="ldapAttribute" property="ldapAttribute" size="23" maxlength="60"/>
			</td>
		</tr>
	</table>								
</div>

 
 <%@include file="/jsp/core/includes/common/Footer.jsp" %> 