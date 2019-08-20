<%@page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.TrustedCertificateData"%>
<%@page import="java.util.*" %>
<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>

<table width="97%" border="0" cellspacing="0" cellpadding="0" align="right">
<bean:define id="trustedCertificateDataBean" name="trustedCertificateData" scope="request" type="com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.TrustedCertificateData" />
    <tr> 
    <%TrustedCertificateData data=trustedCertificateDataBean; %>
      <td valign="top" align="right"> 
       <table width="100%" border="0" cellspacing="0" cellpadding="0" >
	          <tr> 
	            <td class="tblheader-bold" colspan="2" height="20%">
	              <bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.view"/>
	            </td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" >
	            	<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.name" />
	            </td>
	            <td class="tblcol" width="70%" >
	            	<bean:write name="trustedCertificateDataBean" property="trustedCertificateName"/>&nbsp;
	            </td>
	          </tr>
	                <tr> 
	            <td class="tblfirstcol" width="30%" >
	            	<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.subject" />
	            </td>
	            <td class="tblcol" width="70%" >
	            	<div style="width: 603px; overflow: auto; word-wrap: break-word;">
						<%if(data.getSubject()!=null){%>
							<bean:define id="subjectData" name="trustedCertificateDataBean" property="subject" type="java.lang.String" />
							<% String[] subjectDetail = subjectData.split(",");
							for (String str : subjectDetail) {
								if (str.contains("CN=")) { %>
									<%=str.split("CN=")[1]%>
								<%}%>
							<%} %>
						<%} %>
					</div>						
	            </td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" >
	            	<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.issuer" />
	            </td>
	            <td class="tblcol" width="70%" >
					<div style="width: 603px; overflow: auto; word-wrap: break-word;">
						<%if(data.getIssuer()!=null){ %>
							<bean:define id="issuerData" name="trustedCertificateDataBean" property="issuer" type="java.lang.String" />
							<% String[] issuerDetail = issuerData.split(",");
							for (String str : issuerDetail) {
								if (str.contains("CN=")) { %>
									<%=str.split("CN=")[1]%>
								<% }%>
							<%} %>
						<%} %>
					</div> 
	            </td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" >
	            	<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.validfrom" />
	            </td>
	            <td class="tblcol" width="70%" >
	            	<bean:write name="trustedCertificateDataBean" property="validFrom"/>&nbsp;
	            </td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" >
	            	<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.expireddate" />
	            </td>
	            <td class="tblcol" width="70%" >
	            	<bean:write name="trustedCertificateDataBean" property="validTo"/>&nbsp;
	            </td>
	          </tr>	
	          <tr> 
	            <td class="tblfirstcol" width="30%" >
	            	<bean:message key="general.createddate" />
	            </td>
	            <td class="tblcol" width="70%" >
	            	<%=EliteUtility.dateToString(trustedCertificateDataBean.getCreateDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;
	            </td>
	          </tr>	
	          <tr> 
	            <td class="tblfirstcol" width="30%" >
	            	<bean:message key="general.lastmodifieddate" />
	            </td>
	            <td class="tblcol" width="70%" >
	            	<%=EliteUtility.dateToString(trustedCertificateDataBean.getModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;
	            </td>
	          </tr>	
		</table>
		</td>
    </tr>
    <tr>
		<td class="small-gap" colspan="2">&nbsp;</td>   
   </tr>
</table>