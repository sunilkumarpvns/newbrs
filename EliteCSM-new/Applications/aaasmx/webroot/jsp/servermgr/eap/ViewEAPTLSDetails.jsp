<%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
<%@page import="com.elitecore.elitesm.web.servermgr.eap.VendorSpecificServerCertificateInfo"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData" %>

<%
	List<String> lstString = (List<String>)request.getAttribute("cipherSuiteList");
	List<ServerCertificateData> serverCertificateList=(List<ServerCertificateData>)request.getAttribute("serverCertificateList");
	Map<String,VendorSpecificServerCertificateInfo> vendorSpecoficServerInformationmap=(LinkedHashMap<String,VendorSpecificServerCertificateInfo>)request.getAttribute("vendorSpecoficServerInformation");
%>

<html:form action="viewEAPTLSConfig">
<table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
		<td valign="top" align="right"> 
<table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">
	<bean:define id="eapTlsBean" name="eapTlsConfigData" scope="request" type="com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPTLSConfigData" />
	<bean:define id="eapBean" name="eapConfigData" scope="request" type="com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData" />
	<bean:define id="typeValue" name="viewEAPTLSConfigForm" property="type"></bean:define>
	<tr>
		<td align="right" valign="top"> 
			<table cellpadding="0" cellspacing="0" border="0" width="100%" >
 				<tr> 
				  	<td align="left" valign="top" width="100%" colspan="4" >&nbsp;</td>
				</tr>
				<%if("ttls".equals(typeValue)){ %>
				<tr> 
		            <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.viewttlsdetails"/></td>
	            </tr>
	            <%} else if("peap".equals(typeValue)){ %>
				<tr> 
		            <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.viewpeapdetails"/></td>
	            </tr>
	            <%}else{%>
	            
	            <tr> 
		            <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.viewtlsdetails"/></td>
	            </tr>
	            
	            <%} %>
	             <tr>
					<td align="left" class="tblfirstcol" valign="top" width="10%"><bean:message bundle="diameterResources" key="diameterpeerprofile.mintlsversion" /></td>
					<td class="tblcol" width="30%" height="20%" colspan="3" ><bean:write name="eapTlsBean" property="minTlsVersion" />&nbsp;</td>
				</tr>
	            <tr>
					<td align="left" class="tblfirstcol" valign="top" width="10%"><bean:message bundle="diameterResources" key="diameterpeerprofile.maxtlsversion" /></td>
					<td class="tblcol" width="30%" height="20%" colspan="3" ><bean:write name="eapTlsBean" property="maxTlsVersion" />&nbsp;</td>
				</tr>
				 <tr>
					<td align="left" class="tblfirstcol" valign="top" width="10%"><bean:message bundle="diameterResources" key="diameterpeerprofile.servercertificate" /></td>
					<td class="tblcol" width="30%" height="20%" colspan="3" >
						<logic:notEmpty name="eapTlsBean" property="serverCertificateProfileName"> 
						 <span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="eapTlsBean" property="serverCertificateId" />','<bean:write name="eapTlsBean" property="serverCertificateProfileName" />','<%=EliteViewCommonConstant.SERVER_CERTIFICATE%>');">
							 <bean:write name="eapTlsBean" property="serverCertificateProfileName" />
						 </span>
							&nbsp;
						</logic:notEmpty>
						<logic:empty name="eapTlsBean" property="serverCertificateProfileName">
							NONE
						</logic:empty>
						
					</td>
				</tr>
				<tr>
					<td align="left" class="tblfirstcol" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.tlscertificaterequest" /></td>
					<td class="tblcol" width="30%" height="20%" colspan="3" ><bean:write name="eapTlsBean" property="certificateRequest" />&nbsp;</td>
				</tr>
				<tr>
					<td align="left" class="tblfirstcol" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.sessionresumptionlimit"/></td>
					<td class="tblcol" width="30%" height="20%" colspan="3" ><bean:write name="eapTlsBean" property="sessionResumptionLimit" />&nbsp;</td>
				</tr>
				<tr>
					<td align="left" class="tblfirstcol" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.sessionresumptionduration"/></td>
					<td class="tblcol" width="30%" height="20%" colspan="3" ><bean:write name="eapTlsBean" property="sessionResumptionDuration" />&nbsp;</td>
				</tr>
				
           <!-- header for certificate type list  -->
			 <tr> 
	             <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.header.certificatetypelist"/></td>
	         </tr>    
		     
		    <tr>
					<td align="left" class="tblfirstcol" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.certificatetypelist"/></td>
					<td class="tblcol" width="30%" height="20%" colspan="3" ><bean:write	name="eapTlsBean" property="certificateTypesList" />&nbsp;</td>
			</tr>
		   <!-- end -->
		   
		   <tr> 
             <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.header.ciphersuitelist"/></td>
           </tr>
           <tr>
	           <td class="tblfirstcol" valign="top" width="14%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.ciphersuitelist" />&nbsp;</td>
	           <td class="tblcol" colspan="3">
	               <table>
		           <%if(lstString != null && lstString.size()>0){ 
		        	  int index=1;
		        	  for(String strCipherSuites:lstString){
		        	  %>
						<tr>
							<td align="left" class="labeltext" width="5%"><%=index%>.</td>
							<td class="labeltext" width="70%" height="20%"><%=strCipherSuites %>&nbsp;</td>
						</tr>
					 <%index++; %>	 
					 <%}} %>
				  </table>
	           </td>
           </tr>
            <tr> 
   	                <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.certificateexceptionlist"/></td>
	        	 </tr> 
			     
			     <tr>
						<td align="left" class="tblfirstcol" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.certificateexception"/></td>
						<td class="tblcol" width="30%" height="20%" colspan="3">
							<table>
						            <logic:equal value="true" name="eapTlsBean" property="expiryDate">
						            	<tr>
					             	 		<td class="labeltext">
							            	<bean:message bundle="servermgrResources" key="servermgr.eapconfig.expirydate" />
							            	</td>
			            				</tr>
						           </logic:equal>
						            <logic:equal value="true" name="eapTlsBean" property="revokedCertificate">
						            <tr>
				             	 		<td class="labeltext">
						            		<bean:message bundle="servermgrResources" key="servermgr.eapconfig.revokedcertificate" />
						            	</td>
			            			</tr>
						            </logic:equal>
						            <logic:equal value="true" name="eapTlsBean" property="missingClientCertificate">
						            <tr>
				             	 		<td class="labeltext">
						            		<bean:message bundle="servermgrResources" key="servermgr.eapconfig.missingclientcertificate" />
						            	</td>
			            			</tr>
						            </logic:equal>
						            <logic:equal value="true" name="eapTlsBean" property="macValidation">
						            <tr>
				             	 		<td class="labeltext">
						            			<bean:message bundle="servermgrResources" key="servermgr.eapconfig.macvalidation" />
						            	</td>
			            			</tr>
						          </logic:equal>
			            </table>
								
						</td>
			    </tr>
           <%if("ttls".equals(typeValue)){ %>
			     <tr> 
   	                <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.header.ttlscertificaterequest"/></td>
	        	 </tr> 
			     
			     <tr>
						<td align="left" class="tblfirstcol" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.tltscertificaterequest"/></td>
						<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write	name="eapBean" property="eapTtlsCertificateRequest" />&nbsp;</td>
			    </tr>
		   <%} %> 
		  <%if("peap".equals(typeValue)){ %>
			     <tr> 
   	                <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.header.peapcertificaterequest"/></td>
	        	 </tr> 
			     
			     <tr>
						<td align="left" class="tblfirstcol" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.peapcertificaterequest"/></td>
						<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write	name="eapBean" property="eapPeapCertificateRequest" />&nbsp;</td>
			    </tr>
			    <tr>
						<td align="left" class="tblfirstcol" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.peapversion"/></td>
						<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write	name="eapBean" property="peapVersion" />&nbsp;</td>
			    </tr>
		   <%} %> 
		   <!-- vendor specific certificate list header -->
		   
           
           
           <tr>
			   
			   <td width="100%" colspan="4" valign="top">
				<table cellSpacing="0" cellPadding="0" width="100%" border="0" align="left">
				
				<tr> 
                    <td class="tblheader-bold" colspan="4" height="7%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.vendorspecificcertificate"/></td>
           		</tr>
					<tr>
					  <td class = "small-gap">&nbsp;</td>
					</tr>
				<tr>
					<td align="left" class="tblheader" valign="top" width="7%" ><bean:message bundle="servermgrResources" key="servermgr.eapconfig.vendoridentifier"/></td>								
				 	<td align="left" class="tblheader" valign="top" width="16%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.servercertificatename"/></td>
          		 </tr>
                <bean:define id="vendorSpecificeCertificateList" name="eapTlsBean" property="vendorSpecificList" type="java.util.List"/>
                <tr>
                <%for (Map.Entry<String, VendorSpecificServerCertificateInfo> entry : vendorSpecoficServerInformationmap.entrySet()) {
                %>
                	<td align="left" class="tblcol" valign="top" width="7%">
                		<%=entry.getKey() %>
                	</td>
                	<td align="left" class="tblcol" valign="top" width="16%">
                		<% if(entry.getValue() != null){ %>
                			<%
                			VendorSpecificServerCertificateInfo vendorSpecificServerCertificateInfo = (VendorSpecificServerCertificateInfo)entry.getValue();
                			if(vendorSpecificServerCertificateInfo.getServerCertificateId() == null){%>
                				<%=vendorSpecificServerCertificateInfo.getServerCertificateName() %>
                			<%}else{%>
                				 <span class="view-details-css" onclick="openViewDetails(this,'<%=vendorSpecificServerCertificateInfo.getServerCertificateId() %>','<%=vendorSpecificServerCertificateInfo.getServerCertificateName() %>','<%=EliteViewCommonConstant.SERVER_CERTIFICATE%>');">
										    <%=vendorSpecificServerCertificateInfo.getServerCertificateName() %>
								  </span>
                			<%}
                			%>
                		<%} %>
                	</td>
                </tr>
                <%} %>
            
				</table>
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