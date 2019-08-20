<%
List<String> lstString = (List<String>)request.getAttribute("cipherSuiteList");
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0" >
	          <tr width="100%"> 
	            <td class="tblheader-bold" colspan="5" height="20%">
	              <bean:message bundle="diameterResources" key="diameterpeerprofile.connectionparameters"/>
	            </td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.transportprotocol" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="transportProtocol"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.securitystandard" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="securityStandard"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.socketreceivebuffersize" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="socketReceiveBufferSize"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.socketsendbuffersize" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="socketSendBufferSize"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.tcpnagleAlgorithm" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="tcpNagleAlgorithm"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.dwrduration" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="dwrDuration"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.initconnection" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="initConnectionDuration"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.retrycount" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="retryCount"/>&nbsp;</td>
	          </tr>
	           <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.dproncertimeout" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="sendDPRCloseEvent"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblheader-bold" colspan="5" height="20%">
	              <bean:message bundle="diameterResources" key="diameterpeerprofile.securityparameters"/>
	            </td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.mintlsversion" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="minTlsVersion"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.maxtlsversion" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="maxTlsVersion"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.servercertificate" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="serverCertificateName"/>  &nbsp;</td>
	          </tr>
	           <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.clientcertificatevalidation" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="clientCertificateRequest"/>&nbsp;</td>
	          </tr>
	          <tr valign="top">
	           <td class="tblfirstcol" valign="top" width="30%" height="20%" > <bean:message bundle="servermgrResources" key="servermgr.eapconfig.header.ciphersuitelist" />&nbsp;</td>
	           <td class="tblcol" colspan="4">
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
	            <td class="tblfirstcol" width="30%" valign="top"><bean:message bundle="diameterResources" key="diameterpeerprofile.certificateexception" /></td>
	             <td class="tblcol" colspan="4">
	              <table>
				            <logic:equal value="true" name="diameterPeerProfileDataBean" property="validateCertificateExpiry">
				            	<tr>
			             	 		<td class="labeltext">
					            		<bean:message bundle="diameterResources" key="diameterpeerprofile.expirydate" />
					            	</td>
	            				</tr>
				           </logic:equal>
				            <logic:equal value="true" name="diameterPeerProfileDataBean" property="allowCertificateCA">
				            <tr>
		             	 		<td class="labeltext">
				            		<bean:message bundle="diameterResources" key="diameterpeerprofile.unknownca" />
				            	</td>
	            			</tr>
				            </logic:equal>
				            <logic:equal value="true" name="diameterPeerProfileDataBean" property="validateCertificateRevocation">
				            <tr>
		             	 		<td class="labeltext">
				            		<bean:message bundle="diameterResources" key="diameterpeerprofile.revokedcertificate" />
				            	</td>
	            			</tr>
				            </logic:equal>
				            <logic:equal value="true" name="diameterPeerProfileDataBean" property="validateHost">
				            <tr>
		             	 		<td class="labeltext">
				            		<bean:message bundle="diameterResources" key="diameterpeerprofile.unknownsubjectca" />
				            	</td>
	            			</tr>
				          </logic:equal>
	            </table>
	            </td>
	          </tr>
	          <tr> 
	            <td class="tblheader-bold" colspan="5" height="20%">
           			<bean:message bundle="diameterResources" key="diameterpeerprofile.3gppwimaxparameters" />
	            </td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.haipaddress"/></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="haIPAddress"/>&nbsp;</td>
	          </tr>
	           <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.dhcpipaddress"/></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="dhcpIPAddress"/>&nbsp;</td>
	          </tr>
</table>	