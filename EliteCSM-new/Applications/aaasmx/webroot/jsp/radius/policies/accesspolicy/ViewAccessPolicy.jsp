<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.elitecore.elitesm.web.radius.policies.accesspolicy.forms.ViewAccessPolicyForm" %>
<%@ page import="com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.IAccessPolicyDetailData" %>
<%@ page import="com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.IAccessPolicyData" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="java.util.List" %>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%   
    String basePath = request.getContextPath();
    String strDatePattern = "HH:mm";
    
    SimpleDateFormat dateForm = new SimpleDateFormat(strDatePattern);
    int index = 0;
    String accesspolicyid="";
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
setTitle('<bean:message bundle="radiusResources" key="accesspolicy.accesspolicy"/>');
</script>

<html:form action="/viewAccessPolicy" >
<html:hidden name="viewAccessPolicyForm" styleId="action" property="action"/>
<html:hidden name="viewAccessPolicyForm" styleId="accessPolicyId" property="accessPolicyId"/>
	<bean:define id="accessPolicyBean" name="AccessPolicyData" scope="request" type="com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.IAccessPolicyData"/>
	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
  			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">
  				&nbsp;
  			</td>
			<td>
   				<table cellpadding="0" cellspacing="0" border="0" width="100%">
  		  		<tr>
		    		<td  cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
					
						<tr>
							<td class="table-header">	
								<bean:message bundle="radiusResources" key="accesspolicy.viewaccesspolicy"/>
							</td>
				          </tr>
				          <tr > 
					            <td class="small-gap" colspan="3" >&nbsp;</td>
				          </tr>
				          <tr> 
					            <td colspan="3" valign="top"> 
										<table width="100%" border="0" cellspacing="0" cellpadding="0" name="c_tblCrossProductList">
							                <tr> 
								                  <td colspan="2" valign="top">
														  <table width="100%" border="0" cellspacing="0" cellpadding="0">
											                      <tr> 
												                        <td align="left" class="tblfirstcol" width="20%" ><bean:message key="general.name" /></td>
												                        <td align="left" class="tblcol" valign="top" width="29%" ><bean:write name="accessPolicyBean" property="name"/></td>
												                        
												                         <logic:equal name="accessPolicyBean" property="commonStatusId" value="CST01">
												                         		<td align="center" class="tblrows"><img src="<%=basePath%>/images/show.jpg">Show</td>
												                         </logic:equal>
												                         <logic:notEqual name="accessPolicyBean" property="commonStatusId" value="CST01">
																				<td align="center" class="tblrows"><img src="<%=basePath%>/images/hide.jpg">Hide</td>
												                         </logic:notEqual>
										                      	  </tr>
																  <tr > 
												                        <td align="left" class="tblfirstcol" ><bean:message bundle="radiusResources" key="accesspolicy.defaultaccess"/></td>
 												                        <td align="left" class="tblcol" valign="top" colspan="2" >
																		 
																		 <logic:equal name="accessPolicyBean" property="accessStatus" value="A">
												                         	Allowed
												                         </logic:equal>
												                         
												                         <logic:notEqual name="accessPolicyBean" property="accessStatus" value="A">
																			Denied
												                         </logic:notEqual>
												                         </td> 
											                      </tr>
												                  <tr > 
												                        <td align="left" class="tblfirstcol" ><bean:message key="general.description"/></td>
												                        
																		<td align="left" class="tblcol" valign="top" colspan="2" ><%=EliteUtility.formatDescription(accessPolicyBean.getDescription()) %>&nbsp;</td>
																		
											                      </tr>
											                	  <tr > 
												                        <td align="left" class="tblfirstcol" width="20%" ><bean:message bundle="radiusResources" key="accesspolicy.lastModified"/></td>
												                        <td align="left" class="tblcol" valign="top" colspan="2" ><bean:write name="accessPolicyBean" property="lastUpdated"/></td>
                      											  </tr>
                      											  <tr > 
												                        <td width="20%" >&nbsp;</td>
												                        <td colspan="2" >&nbsp;</td>
											                      </tr>
									                      </table>
									              </td>
                							</tr>
                							<tr> 
                  								  <td colspan="2" valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="0">
							                      <tr> 
							                       	<td align="left" class="tblheader-bold" valign="top" width="100%" >
							                        	 <logic:equal name="accessPolicyBean" property="accessStatus" value="A">
							                        	 	<bean:message bundle="radiusResources" key="accesspolicy.deniedtimeslaps"/>
							                        	 </logic:equal>
             
												          <logic:notEqual name="accessPolicyBean" property="accessStatus" value="A">
															<bean:message bundle="radiusResources" key="accesspolicy.allowedtimeslaps"/>
												          </logic:notEqual>
												    </td>
							                      </tr>
							                      <tr > 
							                        <td class="small-gap" >&nbsp;</td>
							                      </tr>
                     							  <tr> 
                        								<td> 
                        										<table width="100%" border="0" cellspacing="0" cellpadding="0" name="c_tblCrossProductList">
											                      	<% int count = 0; 
											                      	%>
														<tr>

															<td align="center" class="tblheader" valign="top" width="5%"><bean:message bundle="radiusResources" key="accesspolicy.serialno" />	</td>
															<td align="center" class="tblheader" valign="top" width="20%"><bean:message bundle="radiusResources" key="accesspolicy.startday" />	</td>
															<td align="center" class="tblheader" valign="top" width="20%"><bean:message bundle="radiusResources" key="accesspoilcy.starttime" /> </td>
														
															<td align="center" class="tblheader" valign="top" width="20%"><bean:message bundle="radiusResources" key="accesspolicy.endday" />	</td>
													 		<td align="center" class="tblheader" valign="top" width="20%"><bean:message bundle="radiusResources" key="accesspolicy.endtime" />	</td>
													
															<td align="center" class="tblheader" valign="top" width="15%"><bean:message bundle="radiusResources" key="accesspolicy.alloweddenied" /> </td>
															
															
														
														</tr>
														<logic:iterate id="radiusPolicyBean" name="AccessPolicyData" property="accessPolicyDetailDataList" type="com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.IAccessPolicyDetailData">
																
																		<tr> 
											                                 <td class="tblrows-center" width="5%"><%=(index + 1)%></td>
											                                 <td class="tblrows-center" width="20%"><bean:write name="radiusPolicyBean" property="startWeekDay"/></td>
											                                 <td class="tblrows-center" width="20%"><%=(radiusPolicyBean.getStartTime()==null?"-":dateForm.format(radiusPolicyBean.getStartTime()))%></td>
											                                 <td class="tblrows-center" width="20%"><bean:write name="radiusPolicyBean" property="endWeekDay"/></td>
											                                 <td class="tblrows-center" width="20%"><%=(radiusPolicyBean.getStopTime()==null?"-":dateForm.format(radiusPolicyBean.getStopTime()))%></td>
												                         	 <td class="tblrows-center" width="15%"><bean:write name="radiusPolicyBean" property="accessStatus"/></td>
											                            </tr>
																		 	
											                            	<%index++;%>
											                     </logic:iterate>
                           									    </table>
                           								</td>
                      							  </tr>
							                      <tr > 
							                        	<td >&nbsp;</td>
							                      </tr>
                   			 	        </table>
                   				</td>
                          </tr>
			              <tr > 
					                  <td width="20%" class="labeltext">&nbsp;</td>
			        		          <td width="80%" class="labeltext">                                                          
											<input type="button" name="c_btnCreate" onclick="javascript:location.href='<%=basePath%>/editAccessPolicy.do?action=list&accesspolicyid=<bean:write name="accessPolicyBean" property="accessPolicyId"/>'" value="  Edit  "  class="light-btn"/> 
							                <input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchAccessPolicy.do?/>'"  value="Cancel" class="light-btn"> 
				                      </td>
						  </tr>
						  <tr >
								<td class="small-gap" >&nbsp;</td>
						  </tr>
						  <tr >
								<td class="small-gap" >&nbsp;</td>
						  </tr>
                   </table>
        	    </td>
			</tr>
			</table>
		</td>
	</tr>
	<%@ include file="/jsp/core/includes/common/Footer.jsp" %>	
</table>
</td>
</tr>
</table>
</html:form>
