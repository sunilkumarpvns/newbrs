<%@include file="/jsp/core/includes/common/Header.jsp" %>

<%@ page import="com.elitecore.netvertexsm.web.datasource.esiradius.form.CreateEsiRadiusForm" %>
<%@ page import="com.elitecore.netvertexsm.blmanager.datasource.EsiRadiusBLManager" %>

<%@ page import="java.util.List" %>
	

<html:form action="/createEsiRadius"> 
<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
				<bean:message bundle="datasourceResources" key="esiradius.create"/>
			</td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="97%" id="c_tblCrossProductList" align="right" border="0" >
			    
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="datasourceResources" key="esiradius.name"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text property="name" maxlength="60" size="30" styleId="name" />
					</td> 
				  </tr>				  											  				  
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="datasourceResources" key="esiradius.description"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
					    <html:textarea property="description" cols="50" rows="4" styleId="description" styleClass="input-textarea"/> 
					</td> 
				  </tr>		
				   <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="datasourceResources" key="esiradius.address"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text property="address" styleId="address" maxlength="60" size="30"/>
					</td> 
				  </tr>				  											  				  
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="datasourceResources" key="esiradius.sharedsecret"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
					    <html:text property="sharedSecret" styleId="sharedSecret" maxlength="60" size="30"/> 
					</td> 
				  </tr>		
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="datasourceResources" key="esiradius.timeout"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text property="timeout" maxlength="60" size="30" styleId="timeout" />
					</td> 
				  </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="datasourceResources" key="esiradius.minlocalport"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text property="minLocalPort" maxlength="60" size="30" styleId="minLocalPort" />
				  </td>					  											  				  
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="datasourceResources" key="esiradius.expiredreqlimitcnt"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
					    <html:text property="expiredReqLimitCnt" maxlength="60" size="30" styleId="expiredReqLimitCnt"/> 
					</td> 
				  </tr>				  											  				  
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="datasourceResources" key="esiradius.retrylimit"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
					    <html:text property="retryLimit" maxlength="60" size="30" styleId="retryLimit"/> 
					</td> 
				  </tr>		
				   <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="datasourceResources" key="esiradius.statuscheckduration"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text property="statusCheckDuration" maxlength="60" size="30" styleId="statusCheckDuration" />
					</td> 
				  </tr>				  											  				  
				  					 
			   </table>  
			</td> 
		  </tr>	 
		  <tr> 
				<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 		  			

		  <tr>
			<td style="padding-left: 1.6em">
				<input type="submit" value="  Create  " class="light-btn">
			</td>
		  	<td>&nbsp;</td>
		  </tr>
		<tr><td>&nbsp;</td></tr>		
		   		     		     		     		     		  
		</table> 
	  </td> 
	</tr>			
	
 <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>

</html:form> 

