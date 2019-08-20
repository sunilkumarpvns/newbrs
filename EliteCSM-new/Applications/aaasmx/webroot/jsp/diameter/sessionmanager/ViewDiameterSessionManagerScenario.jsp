<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData"%>
<%@page import="com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData" %>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerMappingData" %>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.SessionManagerFieldMappingData" %>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.List" %>

<%	List<IDatabaseDSData> lstdatasource = (List<IDatabaseDSData>) request.getAttribute("lstDatasource");
	String dataSourceName = (String)request.getAttribute("dataSourceName");
%>
<bean:define id="diameterSessionManagerDataObj" scope="request" name="diameterSessionManagerData" type="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData"></bean:define>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr> 
    	<td> 
    		<table width="100%" border="0" cellspacing="0" cellpadding="0">
        		<tr> 
          			<td class="tblheader-bold" height="10%" colspan="2">
          				<bean:message bundle="driverResources" key="driver.view"/>
          			</td>
        		</tr>  
				<tr>
					<td align="left" class="tblheader-bold" valign="top" colspan="2">
						<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.scenario" />
					</td>
				</tr>  	
				<tr>
					<td colspan="2" class="tblcol" style="padding-left: 30px;">
							<table width="80%" cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td class="" colspan="2" width="100%">
										<table width="100%" cellspacing="0" cellpadding="0" border="0" style="table-layout: fixed;">
											<tr>
												<td class="labeltext  tblheader-bold" width="20%">
													Name
												</td>
												<td class="labeltext  tblheader-bold" width="20%">
													Description
												</td>
												<td class="labeltext  tblheader-bold" width="20%">
													Ruleset
												</td>
												<td  class="labeltext tblheader-bold" width="20%">
													Criteria
												</td>
												<td  class="labeltext tblheader-bold" width="20%">
													Mapping
												</td>
											</tr>
											<logic:iterate id="scenarioMappings" name="diameterSessionManagerDataObj" property="scenarioMappingDataSet" type="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.ScenarioMappingData">
											<tr>
												<td class="labeltext tblfirstcol" width="20%">
													<bean:write name="scenarioMappings" property="name"/>&nbsp;
												</td>
												<td class="labeltext tblcol border-right-css" width="20%" style="word-wrap:break-word">
														<bean:write name="scenarioMappings" property="description"/>&nbsp;
												</td>
												<td class="labeltext tblcol border-right-css" width="20%">
													<bean:write name="scenarioMappings" property="ruleset"/>&nbsp;
												</td>
												<td  class="labeltext tblcol border-right-css" width="20%" style="word-wrap: break-word;">
													<bean:write name="scenarioMappings" property="criteria"/>&nbsp;
												</td>
												<td  class="labeltext tblcol border-right-css" width="20%">
													<bean:write name="scenarioMappings" property="mappingName"/>&nbsp;
												</td>
											</tr>
											</logic:iterate>
										</table>
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
								</tr>
							</table>
					</td>
				</tr>
				<tr>
					<td align="left" class="tblheader-bold" valign="top" colspan="2">
						<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.sessionoverideaction" /> 
					</td>
				</tr>  	
				<tr>
					<td colspan="2" class="tblcol" style="padding-left: 30px;">
							<table width="80%" cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td class="" colspan="2" width="100%">
										<table width="100%" cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td class="labeltext  tblheader-bold" width="25%">
													Name
												</td>
												<td class="labeltext  tblheader-bold" width="25%">
													Description
												</td>
												<td class="labeltext  tblheader-bold" width="25%">
													Ruleset
												</td>
												<td  class="labeltext tblheader-bold" width="25%">
													Action
												</td>
											</tr>
											<logic:iterate id="sessionOverideActionData" name="diameterSessionManagerDataObj" property="sessionOverideActionDataSet" type="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.SessionOverideActionData">
												<tr>
													<td class="labeltext tblfirstcol" width="25%">
														<bean:write name="sessionOverideActionData" property="name"/>&nbsp;
													</td>
													<td class="labeltext tblcol border-right-css" width="25%">
														<bean:write name="sessionOverideActionData" property="description"/>&nbsp;
													</td>
													<td class="labeltext tblcol border-right-css" width="25%">
														<bean:write name="sessionOverideActionData" property="ruleset"/>&nbsp;
													</td>
													<td  class="labeltext tblcol border-right-css" width="25%">
														<bean:write name="sessionOverideActionData" property="actions"/>&nbsp;
													</td>
												</tr>
											</logic:iterate>
										</table>
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
								</tr>
							</table>
					</td>
				</tr>
			</table> 
		</td>
	</tr>
</table>