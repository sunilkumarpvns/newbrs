<%@ page import="java.util.*" %>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<bean:define id="digestConfBean" name="digestConfData" scope="request" type="com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData" /> 
	<tr>
		<td align="right" valign="top" height="15%"> 
			<table cellpadding="0" cellspacing="0" border="0" width="100%" >

				<tr> 
		            <td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="digestconfResources" key="digestconf.view"/></td>
	            </tr>
	            <tr> 
	            <td class="tblfirstcol" width="30%" height="20%"><bean:message  bundle="digestconfResources" key="digestconf.digestconfname" /></td>
	            <td class="tblcol" width="70%" height="20%" ><bean:write name="digestConfBean" property="name"/>&nbsp;</td>
	           </tr>
	           <tr> 
	            <td class="tblfirstcol" width="30%" height="20%"><bean:message  bundle="digestconfResources" key="digestconf.description" /></td>
	            <td class="tblcol" width="70%" height="20%" ><bean:write name="digestConfBean" property="description"/>&nbsp;</td>
	           </tr>
         
               <tr> 
	            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="digestconfResources" key="digestconf.realm" /></td>
	            <td class="tblcol" width="70%" height="20%"><bean:write name="digestConfBean" property="realm"/>&nbsp;</td>
	          </tr>
	          
	          <tr> 
	             <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="digestconfResources" key="digestconf.defaultqop" /></td>
	             <td class="tblcol" width="70%" height="20%"><bean:write name="digestConfBean" property="defaultQoP"/>&nbsp;</td>
	          </tr>
	          <tr> 
	             <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="digestconfResources" key="digestconf.defaultalgo" /></td>
	             <td class="tblcol" width="70%" height="20%"><bean:write name="digestConfBean" property="defaultAlgo"/>&nbsp;</td>
	          </tr>
	          <tr> 
	             <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="digestconfResources" key="digestconf.opaque" /></td>
	             <td class="tblcol" width="70%" height="20%"><bean:write name="digestConfBean" property="opaque"/>&nbsp;</td>
	          </tr>
	          <tr> 
	             <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="digestconfResources" key="digestconf.defaultnoncelength" /></td>
	             <td class="tblcol" width="70%" height="20%"><bean:write name="digestConfBean" property="defaultNonceLength"/>&nbsp;</td>
	          </tr>
	          
	          <tr> 
	             <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="digestconfResources" key="digestconf.defaultnonce" /></td>
	             <td class="tblcol" width="70%" height="20%"><bean:write name="digestConfBean" property="defaultNonce"/>&nbsp;</td>
	          </tr>
	          
	          <tr> 
	             <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="digestconfResources" key="digestconf.drafaaasipenable" /></td>
	             <td class="tblcol" width="70%" height="20%"><bean:write name="digestConfBean" property="draftAAASipEnable"/>&nbsp;</td>
	          </tr>
	            				
			</table>
		</td>
	</tr>
</table>