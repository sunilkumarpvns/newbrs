<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.customizedmenu.CustomizedMenuData"%>
<%@ page import="com.elitecore.netvertexsm.web.customizedmenu.CustomizedMenuForm"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.customizedmenu.TitleData"%>

<%
		CustomizedMenuData customizedMenuData = (CustomizedMenuData)request.getAttribute("customizedMenuData");
		
%>

<script type="text/javascript">

</script>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
		<td valign="top" align="right"> 
<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<bean:define id="customizedMenuBean" name="customizedMenuData" scope="request" type="com.elitecore.netvertexsm.datamanager.customizedmenu.CustomizedMenuData" />

    <tr>
		<td valign="top" align="right"> 
			<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right" >
			 	<tr>
					<td colspan="2" class="tblheader-bold"  valign="top" ><bean:message  bundle="customizedMenuResources" key="csmmenumgmt.view.title"/> </td>
				</tr>			
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="customizedMenuResources" key="csmmenumgmt.titlename" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%"> <bean:write name="customizedMenuBean" property="title"/>&nbsp;</td> 
				  </tr>
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="customizedMenuResources" key="csmmenumgmt.order" /></td> 
					<td align="left" class="tblrows" valign="top"> <bean:write name="customizedMenuBean" property="order"/>&nbsp;</td> 
				  </tr>
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="customizedMenuResources" key="csmmenumgmt.isContainer" /></td> 
					<td align="left" class="tblrows" valign="top"> <bean:write name="customizedMenuBean" property="isContainer"/>&nbsp;</td> 
				  </tr>
				  <logic:equal name="customizedMenuBean" property="isContainer" value="No">
					  <tr> 
						<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="customizedMenuResources" key="csmmenumgmt.url" /></td> 
						<td align="left" class="tblrows" valign="top" width="70%"> <bean:write name="customizedMenuBean" property="url"/>&nbsp;</td> 
					  </tr>
				   	 			
					  <tr> 
						<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="customizedMenuResources" key="csmmenumgmt.openmethod" /></td> 
						<logic:equal name="customizedMenuBean" property="openMethod" value="self">
							<td align="left" class="tblrows"><bean:message bundle="customizedMenuResources" key="csmmenumgmt.openmethod.samewindow" />&nbsp;</td>
						</logic:equal> 
						<logic:notEqual name="customizedMenuBean" property="openMethod" value="self">
							<td align="left" class="tblrows"><bean:message bundle="customizedMenuResources" key="csmmenumgmt.openmethod.newwindow" />&nbsp;</td>
						</logic:notEqual> 
					  </tr>
					  
					  <tr> 
						<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="customizedMenuResources" key="csmmenumgmt.parameters" /></td> 
						<td align="left" class="tblrows" valign="top" > <bean:write name="customizedMenuBean" property="parameters"/>&nbsp;</td> 
					  </tr>
				 </logic:equal>
				  <!--<tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="customizedMenuResources" key="csmmenumgmt.parentID" /></td> 
					<td align="left" class="tblrows" valign="top"> <bean:write name="customizedMenuBean" property="parentID"/>&nbsp;</td>
				  </tr>
				  
				  -->
				  	
			</table>
		</td>
	</tr>
</table>
</td>
	</tr>
</table>