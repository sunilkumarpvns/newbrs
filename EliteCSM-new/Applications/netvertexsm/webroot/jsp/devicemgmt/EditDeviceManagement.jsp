<%@page import="com.elitecore.netvertexsm.web.devicemgmt.form.DeviceManagementForm"%>
<%@ page import="java.util.List" %>

<script type="text/javascript">
function validate(){
	if(isNull(document.forms[0].tac.value)){
		alert('TAC must be specified.');
	}else if(isEcNaN(document.forms[0].tac.value)){
		alert('TAC must be Numeric.');
	}else{
		document.forms[0].submit();
	}
}
</script> 	

<html:form action="/deviceMgmt.do?method=update"> 
	<html:hidden name="deviceManagementForm" property="tacDetailId"/>
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 

          <tr> 
				<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="97%"  align="right" border="0" > 
	 	  <tr> 
			<td colspan="2" class="tblheader-bold" colspan="3">
				<bean:message  bundle="deviceMgmtResources" key="devicemgmt.update.title"/>
			</td>
		  </tr>			   	
			   	  <tr> 
					<td align="left" class="labeltext" valign="top" width="30%" >
						<bean:message bundle="deviceMgmtResources" key="devicemgmt.tac" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="devicemgmt.tac"/>','<bean:message bundle="deviceMgmtResources" key="devicemgmt.tac" />')"/>
					</td> 
					<td align="left"  valign="top" width="70%"> 
						<html:text name="deviceManagementForm" property="tac" maxlength="8"  size="8" styleId="tac"/><font color="#FF0000"> *</font>
					</td> 
				  </tr>
			   	 			
				  <tr> 
					<td align="left" class="labeltext" valign="top" >
						<bean:message bundle="deviceMgmtResources" key="devicemgmt.brand" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="devicemgmt.brand"/>','<bean:message bundle="deviceMgmtResources" key="devicemgmt.brand" />')"/>
					</td> 
					<td align="left"  valign="top" > 
						<html:text name="deviceManagementForm" property="brand" maxlength="32"  size="32" styleId="brand"/>
					</td> 
				  </tr>
	
				  <tr> 
					<td align="left" class="labeltext" valign="top" >
						<bean:message bundle="deviceMgmtResources" key="devicemgmt.model" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="devicemgmt.model"/>','<bean:message bundle="deviceMgmtResources" key="devicemgmt.model" />')"/>
					</td> 
					<td align="left"  valign="top" > 
						<html:text name="deviceManagementForm" property="model" maxlength="32" size="32" styleId="model"/>
					</td> 
				  </tr>
				  
				  <tr> 
					<td align="left" class="labeltext" valign="top" >
						<bean:message bundle="deviceMgmtResources" key="devicemgmt.hardwaretype" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="devicemgmt.hardwaretype"/>','<bean:message bundle="deviceMgmtResources" key="devicemgmt.hardwaretype" />')"/>
					</td> 
					<td align="left"  valign="top" > 
						<html:text name="deviceManagementForm" property="hardwareType" maxlength="32" size="32" styleId="hardwareType"/>
					</td> 
				  </tr>
				  	
				  <tr> 
					<td align="left" class="labeltext" valign="top" >
						<bean:message bundle="deviceMgmtResources" key="devicemgmt.operatingsystem" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="devicemgmt.operatingsystem"/>','<bean:message bundle="deviceMgmtResources" key="devicemgmt.operatingsystem" />')"/>
					</td> 
					<td align="left"  valign="top" > 
						<html:text name="deviceManagementForm" property="operatingSystem" maxlength="32" size="32" styleId="operatingSystem"/>
					</td> 
				  </tr>	

				  <tr> 
					<td align="left" class="labeltext" valign="top" >
						<bean:message bundle="deviceMgmtResources" key="devicemgmt.year" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="devicemgmt.year"/>','<bean:message bundle="deviceMgmtResources" key="devicemgmt.year" />')"/>
					</td> 
					<td align="left"  valign="top" > 
						<html:text name="deviceManagementForm" property="year" maxlength="4" size="32" styleId="year"/>
					</td> 
				  </tr>	
				   <tr> 
					<td align="left" class="labeltext" valign="top" >
						<bean:message bundle="deviceMgmtResources" key="devicemgmt.additionalinfo" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="devicemgmt.additionalinfo"/>','<bean:message bundle="deviceMgmtResources" key="devicemgmt.additionalinfo" />')"/>
					</td> 
					<td align="left"  valign="top" > 
						<html:text name="deviceManagementForm" property="additionalInfo" maxlength="64" size="32" styleId="additionalInfo"/>
					</td> 
				  </tr>
				  <tr>
				  		<td align="left" class="labeltext" valign="top" ></td>
						<td ><input type="button" onclick="validate();" value="  Update  " class="light-btn" />
						<input type="button" value=" Cancel " tabindex="29" class="light-btn" onclick="javascript:location.href='<%=basePath%>/deviceMgmt.do?method=initSearch'"/></td>
				 </tr>
			   </table>  
			</td> 
		</tr>
		</table> 
</html:form>



