<%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData"%>
<%@page import="com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData" %>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerMappingData" %>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.SessionManagerFieldMappingData" %>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.List" %>

<%	
	List<IDatabaseDSData> lstdatasource = (List<IDatabaseDSData>) request.getAttribute("lstDatasource");
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
					<bean:message bundle="driverResources" key="driver.driverinstancedetails" /></td>
				</tr>  					
        		
        		<tr>
					<td class="tblfirstcol" width="20%" height="10%" >
						<bean:message bundle="sessionmanagerResources" key="sessionmanager.name" />
					</td>
					<td class="tblcol" width="80%" height="80%" >
						<bean:write name="diameterSessionManagerDataObj" property="name"/>&nbsp;
					</td>
				</tr>  
				<tr>
					<td class="tblfirstcol" width="20%" height="10%" >
						<bean:message bundle="sessionmanagerResources" key="sessionmanager.description" />
					</td>
					<td class="tblcol" width="80%" height="80%" >
						<bean:write name="diameterSessionManagerDataObj" property="description"/>&nbsp;
					</td>
				</tr>  
				<tr>
					<td class="tblfirstcol" width="20%" height="10%" >
						<bean:message bundle="sessionmanagerResources" key="sessionmanager.datasource" /> 
					</td>
					<td class="tblcol" width="80%" height="80%" >
						<logic:notEmpty name="diameterSessionManagerDataObj" property="databaseDatasourceId">
						    <span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="diameterSessionManagerDataObj" property="databaseDatasourceId"/>','<%=dataSourceName%>','<%=EliteViewCommonConstant.DATABASE_DATASOURCE%>');">
						    	<%=dataSourceName%>
						    </span>
						</logic:notEmpty>
						&nbsp;
					</td>
				</tr>   
			     <tr>
					<td class="tblfirstcol" width="20%" height="10%" >
						<bean:message bundle="sessionmanagerResources" key="sessionmanager.tablename" /> 
					</td>
					<td class="tblcol" width="80%" height="80%" >
						<bean:write name="diameterSessionManagerDataObj" property="tableName"/>&nbsp;
					</td>
				</tr> 
				 <tr>
					<td class="tblfirstcol" width="20%" height="10%" >
						<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.sequencename" /> 
					</td>
					<td class="tblcol" width="80%" height="80%" >
						<bean:write name="diameterSessionManagerDataObj" property="sequenceName"/>&nbsp;
					</td>
				</tr> 
				 <tr>
					<td class="tblfirstcol" width="20%" height="10%" >
						<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.starttimefield" /> 
					</td>
					<td class="tblcol" width="80%" height="80%" >
						<bean:write name="diameterSessionManagerDataObj" property="startTimeField"/>&nbsp;
					</td>
				</tr> 
				<tr>
					<td class="tblfirstcol" width="20%" height="10%" >
						<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.lastupdatetimefield" /> 
					</td>
					<td class="tblcol" width="80%" height="80%" >
						<bean:write name="diameterSessionManagerDataObj" property="lastUpdatedTimeField"/>&nbsp;
					</td>
				</tr> 
				<tr>
					<td class="tblfirstcol" width="20%" height="10%" >
						<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.dbquerytimeout" /> 
					</td>
					<td class="tblcol" width="80%" height="80%" >
						<bean:write name="diameterSessionManagerDataObj" property="dbQueryTimeout"/>&nbsp;
					</td>
				</tr> 
				<tr>
					<td class="tblfirstcol" width="20%" height="10%" >
						<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.multivaluedelimeter" /> 
					</td>
					<td class="tblcol" width="80%" height="80%" >
						<bean:write name="diameterSessionManagerDataObj" property="delimeter"/>&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="20%" height="10%" >
						<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.dbfailureaction" /> 
					</td>
					<td class="tblcol" width="80%" height="80%" >
						<bean:write name="diameterSessionManagerDataObj" property="dbFailureAction"/>&nbsp;
					</td>
				</tr>
				
				<tr>
					<td class="tblfirstcol" width="20%" height="10%" >
						<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.batchmode" /> 
					</td>
					<td class="tblcol" width="80%" height="80%" >
						<logic:equal value="true" name="diameterSessionManagerDataObj"  property="batchEnabled">
							Enabled
						</logic:equal>
						<logic:notEqual value="true" name="diameterSessionManagerDataObj"  property="batchEnabled">
							Disabled
						</logic:notEqual>
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="20%" height="10%" >
						<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.batchsize" /> 
					</td>
					<td class="tblcol" width="80%" height="80%" >
						<bean:write name="diameterSessionManagerDataObj" property="batchSize"/>&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="20%" height="10%" >
						<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.batchinterval" /> 
					</td>
					<td class="tblcol" width="80%" height="80%" >
						<bean:write name="diameterSessionManagerDataObj" property="batchInterval"/>&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="20%" height="10%" >
						<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.batchquerytimeout" /> 
					</td>
					<td class="tblcol" width="80%" height="80%" >
						<bean:write name="diameterSessionManagerDataObj" property="batchQueryTimeout"/>&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="20%" height="10%" >
						<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.batchoperation" /> 
					</td>
					<td class="tblcol" width="80%" height="80%" >
						<logic:equal value="true" property="batchedInsert" name="diameterSessionManagerDataObj">INSERT<br/></logic:equal>
						<logic:equal value="true" property="batchedUpdate" name="diameterSessionManagerDataObj">UPDATE<br/></logic:equal>
						<logic:equal value="true" property="batchedDelete" name="diameterSessionManagerDataObj">DELETE</logic:equal>
					</td>
				</tr>
				<tr>
					<td align="left" class="tblheader-bold" valign="top" colspan="2">
						<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.fieldmappings" />
					</td>
				</tr>  	
				<tr>
					<td colspan="2" class="tblcol" style="padding-left: 30px;">
						 <logic:iterate id="mappingObject" name="diameterSessionManagerDataObj" property="diameterSessionManagerMappingData" type="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerMappingData">
							<table width="80%" cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td class="labeltext tblcol tblheader-bold" width="15%" >
										Mapping Name
									</td>
									<td class="labeltext tblcol" width="85%" style="border-top-style: solid;border-top-color: #CCC;border-top-width: 1px;border-right-style: solid;border-right-color: #CCC;border-right-width: 1px;">
										<bean:write name="mappingObject" property="mappingName"/>
									</td>
								</tr>
								<tr>
									<td class="tblrow" colspan="2" width="100%">
										<table width="100%" cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td class="labeltext  tblheader-bold" width="25%">
													DB Field Name
												</td>
												<td  class="labeltext tblheader-bold" width="25%">
													Referring Attribute
												</td>
												<td  class="labeltext tblheader-bold" width="25%">
													Data Type
												</td>
												<td  class="labeltext tblheader-bold border-right-css" width="25%">
													Default value
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td colspan="2" width="100%">
										<logic:iterate id="dbFieldMapping" name="mappingObject" property="sessionManagerFieldMappingData" type="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.SessionManagerFieldMappingData">
											<table width="100%" cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td class="labeltext tblfirstcol" width="25%">
														<bean:write name="dbFieldMapping" property="dbFieldName"/>&nbsp;
													</td>
													<td  class="labeltext tblcol border-right-css" width="25%">
														<bean:write name="dbFieldMapping" property="referringAttr"/>&nbsp;
													</td>
													<td  class="labeltex tblcol border-right-css" width="25%">
														<logic:equal value="0" name="dbFieldMapping" property="dataType">
															String&nbsp;
														</logic:equal>
														<logic:equal value="1" name="dbFieldMapping" property="dataType">
															Timestamp&nbsp;
														</logic:equal>
													</td>
													<td  class="labeltext tblcol border-right-css" width="25%">
														<bean:write name="dbFieldMapping" property="defaultValue"/>&nbsp;
													</td>
												</tr>
											</table>
										</logic:iterate>
									</td>
								</tr>
							</table>
						</logic:iterate> 
					</td>
				</tr>
			</table> 
		</td>
	</tr>
</table>