<%@page import="com.elitecore.elitesm.datamanager.radius.bwlist.data.BWListData"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page  import="java.sql.Timestamp"%>
<%@page  import="java.text.DateFormat"%>
<%@page  import="java.text.SimpleDateFormat"%>
<%

	BWListData bwListData =(BWListData)request.getAttribute("bwListData");

	String formatedDate = "";
	if( bwListData.getValidity() != null ){
		formatedDate = EliteUtility.getValidity(bwListData.getValidity());
		formatedDate = formatedDate.replaceAll("-", "/");
	} 
%>

<script>
	setTitle('<bean:message bundle="radiusResources" key="radius.bwlist.title"/>');
</script>

	
<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td>
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td class="table-header">
									<bean:message bundle="radiusResources" key="radius.bwlist.view" />
								</td>
							</tr>
							<tr>
								<td>
									<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
										<tr>
											<td align="left" class="tblfirstcol" valign="top" width="18%">
												<bean:message bundle="radiusResources" key="radius.bwlist.attribute" /> 
											</td>
											<td align="left" class="tblcol" valign="top" width="250px">
												<bean:write name="bwListData" property="attributeId"/>
											</td>
										</tr>

										<tr>
											<td align="left" class="tblfirstcol" valign="top" width="18%">
												<bean:message bundle="radiusResources" key="radius.bwlist.attributevalue" /> 
											</td>
											<td align="left" class="tblcol" valign="top">
												<bean:write name="bwListData" property="attributeValue"/>
											</td>
										</tr>
										
										<tr>
											<td align="left" class="tblfirstcol" valign="top" width="18%">
												<bean:message bundle="radiusResources" key="radius.bwlist.validity" /> 
											</td>
											<td align="left" class="tblcol" valign="top">
												<%=formatedDate %>
											</td>
										</tr>
										
										<tr>
											<td align="left" class="tblfirstcol" valign="top" width="18%">
												<bean:message bundle="radiusResources" key="radius.bwlist.type" /> 
											</td>
											<td align="left" class="tblcol" valign="top">
												<logic:notEmpty property="typeName" name="bwListData">
													<logic:equal value="B" name="bwListData" property="typeName">
														<bean:message bundle="radiusResources" key="radius.bwlist.blacklist" /> 
													</logic:equal>
													<logic:equal value="W" name="bwListData" property="typeName">
														<bean:message bundle="radiusResources" key="radius.bwlist.whitelist" /> 
													</logic:equal>
												</logic:notEmpty>&nbsp;
											</td>
										</tr>
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
