<%@page import="com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>

<script language = "javascript">

	function popupExternalSystem(val) {	

		var elementid = "#"+val;
		document.getElementById(val).style.visibility = "visible";		
		$( elementid ).dialog({
			modal: false,
			autoOpen: false,		
			height: "auto",
			width: 500
		});	
		$(elementid).dialog("open");
		window.pageYOffset=position;
	}
	
	
	

</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<bean:define id="sessionManagerBean" name="sessionManagerInstanceData" type="com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData"></bean:define>
	<tr>
		<td valign="top" align="right">
		<table width="100%" border="0" cellspacing="0" cellpadding="0"
			height="15%" class="box">
			<tr>
				<td class="tblheader-bold" colspan="4" height="20%">
					<bean:message bundle="sessionmanagerResources" key="sessionmanager.viewsessionmanagerremotedetails" />
				</td>
			</tr>
     		<tr>
            <td class="tblfirstcol" width="25%" height="20%" align="left" valign="top">
            	<bean:message bundle="sessionmanagerResources" key="sessionmanager.sessionmanagementservers" />
            </td>
             <td class="tblcol" width="30%">
             	<table class="box" cellpadding="0" cellspacing="0" border="0" width="100%">
                		<tr>
                			<td class="tblfirstcol" width="50%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.translationmapping" /></td>
  			  				<td class="tblcol" width="50%">
  			  					<logic:notEmpty name="remoteTranslationMappingConfData" >
  			  						<bean:write name="remoteTranslationMappingConfData" property="name"/>
  			  					</logic:notEmpty>
  			  					<logic:notEmpty name="remoteCopyPacketMappingConfData" >
  			  						<bean:write name="remoteCopyPacketMappingConfData" property="name"/>
  			  					</logic:notEmpty>
  			  					&nbsp;
  			  				</td>	
                		</tr>
                		<tr>
                			<td class="tblfirstcol" width="50%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.script" /></td>
  			  				<td class="tblcol" width="50%">
  			  					<logic:notEmpty name="sessionManagerInstanceData" property="script">
  			  						<bean:write name="sessionManagerInstanceData" property="script"/>
  			  					</logic:notEmpty>
  			  					&nbsp;
  			  				</td>
                		</tr>
                	</table>
                	&nbsp;
                 <table border="0" cellspacing="0" cellpadding="0" height="15%" width="100%">
	              <tr>
                    <td class="tblheader"  width="50%"><bean:message  key="general.name" /></td>
                    <td class="tblheader"  width="50%"><bean:message  key="general.description" /></td>
                  </tr>
                  <logic:iterate id="serverInstanceBean" name="sessionManagerServerList" type="com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData">
                  <% String divName = "sessionmanager"+serverInstanceBean.getEsiInstanceId(); %>
                  <tr>
                    <td class="tblfirstcol" ><a onclick="popupExternalSystem('<%=divName%>')" style="cursor: pointer;"><bean:write name="serverInstanceBean" property="name"/></a></td>
                    <td class="tblcol" ><%=EliteUtility.formatDescription(serverInstanceBean.getDescription())%>&nbsp;
                     <div id="<%=divName%>" style="display: none;" title="<bean:message bundle="externalsystemResources"  key="esi.details" />">
                  	<table border="0" cellspacing="0" cellpadding="0" height="15%" width="100%" class="box">
		               <tr> 
            			<td class="tblcol" width="30%" height="20%"><bean:message bundle="externalsystemResources" key="esi.name" /></td>
            			<td class="tblcol" width="70%" height="20%" ><bean:write name="serverInstanceBean" property="name"/>&nbsp;</td>
          				</tr>
          				<tr> 
            			<td class="tblcol" width="30%" height="20%"><bean:message bundle="externalsystemResources" key="esi.address" /></td>
            			<td class="tblcol" width="70%" height="20%" ><bean:write name="serverInstanceBean" property="address"/>&nbsp;</td>
          				</tr>
          				<tr> 
            			<td class="tblcol" width="30%" height="20%"><bean:message bundle="externalsystemResources" key="esi.sharedsecret" /></td>
            			<td class="tblcol" width="70%" height="20%" ><bean:write name="serverInstanceBean" property="sharedSecret"/>&nbsp;</td>
          				</tr>
          				<tr> 
            			<td class="tblcol" width="30%" height="20%"><bean:message bundle="externalsystemResources" key="esi.timeout" /></td>
            			<td class="tblcol" width="70%" height="20%" ><bean:write name="serverInstanceBean" property="timeout"/>&nbsp;</td>
          				</tr>
          				<tr> 
            			<td class="tblcol" width="30%" height="20%"><bean:message bundle="externalsystemResources" key="esi.minlocalport" /></td>
            			<td class="tblcol" width="70%" height="20%" ><bean:write name="serverInstanceBean" property="minLocalPort"/>&nbsp;</td>
          				</tr>
          				<tr> 
            			<td class="tblcol" width="30%" height="20%"><bean:message bundle="externalsystemResources" key="esi.expiredrequestlimitcount" /></td>
            			<td class="tblcol" width="70%" height="20%" ><bean:write name="serverInstanceBean" property="expiredRequestLimitCount"/>&nbsp;</td>
          				</tr>
                  	</table>
  					</div>
                    </td>
                  </tr>                  
                  </logic:iterate>

                  </table>
                  
              </td>
          </tr>
		  <tr>
            <td class="tblfirstcol" width="25%" height="20%" align="left" valign="top">
            	<bean:message bundle="sessionmanagerResources" key="sessionmanager.acceptontimeout" />
            </td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="sessionManagerBean" property="acceptOnTimeout"/>&nbsp;</td>
          </tr>
		</table>
		</td>
	</tr>
</table>