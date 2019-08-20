<%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@page import="com.elitecore.elitesm.util.constants.SessionManagerConstant"%>
<%@page import="com.elitecore.coreradius.commons.util.constants.RadiusConstants"%>
<%@page import="com.elitecore.core.serverx.sessionx.FieldMapping"%>
<%@page import="com.elitecore.elitesm.datamanager.sessionmanager.data.ISMConfigInstanceData"%>

	<%
		ISMConfigInstanceData localSessionManagerInstanceData = (ISMConfigInstanceData)request.getAttribute("smConfigInstanceData");
	%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<bean:define id="smConfigInstanceBean" name="smConfigInstanceData" scope="request" type="com.elitecore.elitesm.datamanager.sessionmanager.data.ISMConfigInstanceData"></bean:define>
	<bean:define id="datasourceBean" name="databaseDSData" scope="request" type="com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData"></bean:define>
	<tr>
		<td valign="top" align="right">
		<table width="100%" border="0" cellspacing="0" cellpadding="0"
			height="15%">
			<tr>
				<td class="tblheader-bold" colspan="4" height="20%">
					<bean:message bundle="sessionmanagerResources" key="sessionmanager.viewsessionmanagerlocaldetails" />
				</td>
			</tr>
			
			<tr>
						<td class="tblfirstcol" width="35%" height="20%"><bean:message
							bundle="sessionmanagerResources" key="sessionmanager.datasource"/>
						</td>
						<td class="tblcol" width="15%" height="20%" colspan="3">
							<logic:notEmpty name="datasourceBean" property="name">
			            		<span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="datasourceBean" property="databaseId"/>','<bean:write name="datasourceBean" property="name"/>','<%=EliteViewCommonConstant.DATABASE_DATASOURCE%>');">
			            			<bean:write name="datasourceBean" property="name"/>
			            		</span>
			            	</logic:notEmpty>
						</td>
					    
			</tr>
			
			<tr>
						<td class="tblfirstcol"  height="20%"><bean:message
							bundle="sessionmanagerResources" key="sessionmanager.tablename"/>
						</td>
						<td class="tblcol"  height="20%" colspan="3"><bean:write name="smConfigInstanceBean" property="tablename" />&nbsp;
						</td>
					    
			</tr>
			<tr>
						<td class="tblfirstcol"  height="20%"><bean:message
							bundle="sessionmanagerResources" key="sessionmanager.starttimefield"/>
						</td>
						<td class="tblcol"  height="20%" colspan="3"><bean:write
							name="smConfigInstanceBean" property="startTimeField" />&nbsp;
						</td>
					    
			</tr>
			<tr>
						<td class="tblfirstcol"  height="20%">
						<bean:message bundle="sessionmanagerResources" key="sessionmanager.lastupdatetimefield"/>
						</td>
						<td class="tblcol"  height="20%" colspan="3"><bean:write
						name="smConfigInstanceBean" property="lastUpdatedTimeField" />&nbsp;
						</td>
					    
			</tr>
			<tr>
						<td class="tblfirstcol" width="35%" height="20%"><bean:message
					bundle="sessionmanagerResources" key="sessionmanager.sequencename"/>
						</td>
						<td class="tblcol" width="15%" height="20%" colspan="3"><bean:write
					name="smConfigInstanceBean" property="idSequenceName" />&nbsp;
						</td>
					    
			</tr>
			
			
			<tr>
						<td class="tblfirstcol"  height="20%"><bean:message
							bundle="sessionmanagerResources" key="sessionmanager.behaviour"/>
						</td>
						<%if(localSessionManagerInstanceData.getBehaviour() == 1){%>
							<td class="tblcol"  height="20%" colspan="3">							
								Acct
							</td>
					    <%}else{%>
						    <td class="tblcol"  height="20%" colspan="3">							
								Auth
							</td>
					    <%}%>
			</tr>
				<tr>
					<td class="tblfirstcol" height="20%"><bean:message
							bundle="sessionmanagerResources"
							key="sessionmanager.dboperationfailurebehaviour" /></td>
					<logic:equal name="smConfigInstanceBean"
						property="dbfailureaction"
						value="<%=ConfigConstant.IGNORE%>">
						<td class="tblcol" height="20%" colspan="3">Ignore(Default)</td>
					</logic:equal>
					<logic:equal name="smConfigInstanceBean"
						property="dbfailureaction"
						value="<%=ConfigConstant.REJECT%>">
						<td class="tblcol" height="20%" colspan="3">Reject</td>
					</logic:equal>
					<logic:equal name="smConfigInstanceBean"
						property="dbfailureaction"
						value="<%=ConfigConstant.DROP%>">
						<td class="tblcol" height="20%" colspan="3">Drop</td>
					</logic:equal>
				</tr>
				<tr>
					<td class="tblfirstcol" height="20%">	
						<bean:message bundle="sessionmanagerResources" key="sessionmanager.sessionstopaction" /> 
					</td>
					<logic:equal name="smConfigInstanceBean" property="sessionStopAction" value="DELETE">
						<td class="tblcol" height="20%" colspan="3">Delete(Default)&nbsp;</td>
					</logic:equal>
					<logic:equal name="smConfigInstanceBean" property="sessionStopAction" value="UPDATE">
						<td class="tblcol" height="20%" colspan="3">Update&nbsp;</td>
					</logic:equal>
				</tr>

				<tr>
					<td class="tblheader-bold" colspan="4" height="20%"><bean:message
							bundle="sessionmanagerResources"
							key="sessionmanager.viewsessionmanagerlocalbatchupdate" /></td>
				</tr>

				<tr>
						<td class="tblfirstcol" width="35%" height="20%"><bean:message
							bundle="sessionmanagerResources" key="sessionmanager.enabled"/>
						</td>
						<td class="tblcol" width="15%" height="20%" colspan="3"><bean:write name="smConfigInstanceBean" property="batchUpdateEnabled" />&nbsp;
						</td>	    
			</tr>
			
			<tr>
						<td class="tblfirstcol"  height="20%"><bean:message
							bundle="sessionmanagerResources" key="sessionmanager.size"/>
						</td>
						<td class="tblcol"  height="20%" colspan="3"><bean:write name="smConfigInstanceBean" property="batchSize" />&nbsp;
						</td>	    
			</tr>
			
			<tr>
						<td class="tblfirstcol" width="35%" height="20%"><bean:message
							bundle="sessionmanagerResources" key="sessionmanager.updateinterval"/>
						</td>
						<td class="tblcol" width="15%" height="20%" colspan="3"><bean:write name="smConfigInstanceBean" property="batchUpdateInterval" />&nbsp;
						</td>		    
			</tr>
			
			<tr>
						<td class="tblfirstcol"  height="20%"><bean:message
							bundle="sessionmanagerResources" key="sessionmanager.querytimeout"/>
						</td>
						<td class="tblcol"  height="20%" colspan="3"><bean:write name="smConfigInstanceBean" property="dbQueryTimeOut" />&nbsp;
						</td>		    
			</tr>
			
			<!-- header session closer properties -->
			<tr>
				<td class="tblheader-bold" colspan="4" height="20%"><bean:message
					bundle="sessionmanagerResources"
					key="sessionmanager.sessioncloserproperties" /></td>
			</tr>
			
			<tr>
				<td class="tblfirstcol" width="45%"  height="20%"><bean:message
					bundle="sessionmanagerResources" key="sessionmanager.autosessioncloser"/></td>
				<td class="tblcol" width="10%"  height="20%"><bean:write
					name="smConfigInstanceBean" property="autoSessionCloser" />&nbsp;</td>
			    <td class="tblfirstcol" width="45%" height="20%"><bean:message
					bundle="sessionmanagerResources" key="sessionmanager.sessiontimeout"/></td>
				<td class="tblcol" width="10%" height="20%"><bean:write
					name="smConfigInstanceBean" property="sessiontimeout" />&nbsp;</td>
			    
			</tr>
			
			<tr>
				<td class="tblfirstcol"  height="20%"><bean:message
					bundle="sessionmanagerResources" key="sessionmanager.closebatchcount"/></td>
				<td class="tblcol"  height="20%"><bean:write
					name="smConfigInstanceBean" property="closeBatchCount" />&nbsp;</td>
			    <td class="tblfirstcol"  height="20%"><bean:message
					bundle="sessionmanagerResources" key="sessionmanager.sessionthreadsleeptime"/></td>
				<td class="tblcol"  height="20%"><bean:write
					name="smConfigInstanceBean" property="sessionThreadSleepTime" />&nbsp;</td>
			    
			</tr>
			<tr>
				<td class="tblfirstcol"  height="20%"><bean:message
					bundle="sessionmanagerResources" key="sessionmanager.sessioncloseaction"/>
				</td>
				<logic:equal name="smConfigInstanceBean" property="sessionCloseAction" value="<%=Integer.toString(RadiusConstants.SESSION_CLOSE_ACTION_NONE)%>">
				<td class="tblcol"  height="20%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.sessioncloseaction.label.none"/>&nbsp;</td>
				</logic:equal>
				<logic:equal name="smConfigInstanceBean" property="sessionCloseAction" value="<%=Integer.toString(RadiusConstants.SESSION_CLOSE_ACTION_GENERATE_DISCONNECT)%>">
				<td class="tblcol"  height="20%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.sessioncloseaction.label.generatedisconnect"/>&nbsp;</td>
				</logic:equal>
				<logic:equal name="smConfigInstanceBean" property="sessionCloseAction" value="<%=Integer.toString(RadiusConstants.SESSION_CLOSE_ACTION_GENERATE_STOP)%>">
					<td class="tblcol"  height="20%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.sessioncloseaction.label.generatestop"/>&nbsp;
				    </td>
				</logic:equal>
				<logic:equal name="smConfigInstanceBean" property="sessionCloseAction" value="<%=Integer.toString(RadiusConstants.SESSION_CLOSE_ACTION_GENERATE_DM_AND_STOP)%>">
					<td class="tblcol"  height="20%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.sessioncloseaction.label.generatedisconnectstop"/>&nbsp;
				    </td>
				</logic:equal>
				<td class="tblfirstcol"  height="20%"><bean:message
					bundle="sessionmanagerResources" key="sessionmanager.sessionoverrideaction"/>
				</td>		
				<logic:equal name="smConfigInstanceBean" property="sessionOverrideAction" value="<%=Integer.toString(RadiusConstants.SESSION_OVERRIDE_ACTION_NONE)%>">
				<td class="tblcol"  height="20%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.sessioncloseaction.label.none"/>&nbsp;</td>
				</logic:equal>
				<logic:equal name="smConfigInstanceBean" property="sessionOverrideAction" value="<%=Integer.toString(RadiusConstants.SESSION_OVERRIDE_ACTION_GENERATE_DISCONNECT)%>">
				<td class="tblcol"  height="20%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.sessioncloseaction.label.generatedisconnect"/>&nbsp;</td>
				</logic:equal>
				<logic:equal name="smConfigInstanceBean" property="sessionOverrideAction" value="<%=Integer.toString(RadiusConstants.SESSION_OVERRIDE_ACTION_GENERATE_STOP)%>">
					<td class="tblcol"  height="20%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.sessioncloseaction.label.generatestop"/>&nbsp;
				    </td>
				</logic:equal>
				<logic:equal name="smConfigInstanceBean" property="sessionOverrideAction" value="<%=Integer.toString(RadiusConstants.SESSION_OVERRIDE_ACTION_GENERATE_DM_AND_STOP)%>">
					<td class="tblcol"  height="20%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.sessioncloseaction.label.generatedisconnectstop"/>&nbsp;
				    </td>
				</logic:equal>	
			</tr>
			<tr>
				<td class="tblfirstcol"  height="20%"><bean:message
					bundle="sessionmanagerResources" key="sessionmanager.sessionoverridecolumn"/>
				</td>
				<td colspan="4"  class="tblcol">
					<bean:write name="smConfigInstanceBean" property="sessionOverrideColumn" />&nbsp;</td>
				</td>
			</tr>
			
			<!-- Search Attribute Field -->
			<tr>
				<td class="tblheader-bold" colspan="4" height="20%"><bean:message
					bundle="sessionmanagerResources"
					key="sessionmanager.searchcolumn.field" /></td>
			</tr>
			<tr>
				<td class="tblfirstcol"  height="20%">
					<bean:message bundle="sessionmanagerResources" key="sessionmanager.concurrencyidentityfield"/>
				</td>
				<td class="tblcol"  height="20%" colspan="3">
					<bean:write name="smConfigInstanceBean" property="concurrencyIdentityField" />&nbsp;
				</td>
			</tr>
			<tr>
				<td class="tblfirstcol"  height="20%"><bean:message
					bundle="sessionmanagerResources" key="sessionmanager.searchcolumn"/></td>
				<td class="tblcol"  height="20%" colspan="3"><bean:write
					name="smConfigInstanceBean" property="searchAttribute" />&nbsp;</td>
			</tr>
					<!--  Header Mandatory DB Field Mapping -->
			<tr>
			   
			   <td width="100%" colspan="4" valign="top" class="box">
				<table cellSpacing="0" cellPadding="0" width="100%" border="0" align="left" class="box">
				
				<tr> 
                    <td class="tblheader-bold" colspan="5" height="20%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.mandatoryfields"/></td>
           		</tr>
					<tr>
					  <td class = "small-gap">&nbsp;</td>
					</tr>
				<tr>
					 <td align="left" class="tblheader" valign="top" width="12%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.field"/></td>
					<td align="left" class="tblheader" valign="top" width="17%" ><bean:message bundle="sessionmanagerResources" key="sessionmanager.dbfieldname"/></td>								
					<td align="left" class="tblheader" valign="top" width="16%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.referringentity"/></td>
		            <td align="left" class="tblheader" valign="top" width="18%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.datatype"/></td>    
                   <td align="left" class="tblheader" valign="top" width="12%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.defaultvalue"/></td>
          		 </tr>
                <bean:define id="additionalDBFieldBeans" name="smConfigInstanceBean" property="dbFieldMapDataList" type="java.util.List"/>
                
                <logic:iterate id="additionalDBFieldBean" name="additionalDBFieldBeans" type="com.elitecore.elitesm.datamanager.sessionmanager.data.SMDBFieldMapData">
       			<logic:notEqual name="additionalDBFieldBean" property="field" value="">
             
                <tr>
                				<td align="left" class="tblcol" valign="top" width="17%"><bean:write	name="additionalDBFieldBean" property="field" />&nbsp;</td>								
								<td align="left" class="tblcol" valign="top" width="17%"><bean:write	name="additionalDBFieldBean" property="dbFieldName" />&nbsp;</td>								
								<td align="left" class="tblcol" valign="top" width="16%"><bean:write	name="additionalDBFieldBean" property="referringEntity" />&nbsp;</td>
								
								<logic:equal name="additionalDBFieldBean" property="dataType" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">
								<td align="left" class="tblcol" valign="top" width="18%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.datatype.type.string"/>&nbsp;</td>
								</logic:equal>
								
								<logic:equal name="additionalDBFieldBean" property="dataType" value="<%=Integer.toString(FieldMapping.TIMESTAMP_TYPE)%>">
								<td align="left" class="tblcol" valign="top" width="18%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.datatype.type.timestamp"/>&nbsp;</td>
								</logic:equal>
								
                                    
                                <td align="left" class="tblcol" valign="top" width="12%"><bean:write	name="additionalDBFieldBean" property="defaultValue" />&nbsp;</td>
                </tr>
                </logic:notEqual>
                </logic:iterate>
                
            
				</table>
				</td>	
		</tr>
			
			<!--  Header Additional DB Field Mapping -->
			<tr>
			   
			   <td width="100%" colspan="4" valign="top" class="box">
				<table cellSpacing="0" cellPadding="0" width="100%" border="0" align="left" class="box">
				
				<tr> 
                    <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.dbplugin"/></td>
           		</tr>
					<tr>
					  <td class = "small-gap">&nbsp;</td>
					</tr>
				<tr>
					<td align="left" class="tblheader" valign="top" width="17%" ><bean:message bundle="sessionmanagerResources" key="sessionmanager.dbfieldname"/></td>								
					<td align="left" class="tblheader" valign="top" width="16%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.referringentity"/></td>
		            <td align="left" class="tblheader" valign="top" width="18%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.datatype"/></td>    
                   <td align="left" class="tblheader" valign="top" width="12%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.defaultvalue"/></td>
          		 </tr>
                <bean:define id="additionalDBFieldBeans" name="smConfigInstanceBean" property="dbFieldMapDataList" type="java.util.List"/>
                
                <logic:iterate id="additionalDBFieldBean" name="additionalDBFieldBeans" type="com.elitecore.elitesm.datamanager.sessionmanager.data.SMDBFieldMapData">
       			<logic:equal name="additionalDBFieldBean" property="field" value="">
             
                <tr>
								<td align="left" class="tblcol" valign="top" width="17%"><bean:write	name="additionalDBFieldBean" property="dbFieldName" />&nbsp;</td>								
								<td align="left" class="tblcol" valign="top" width="16%"><bean:write	name="additionalDBFieldBean" property="referringEntity" />&nbsp;</td>
								
								<logic:equal name="additionalDBFieldBean" property="dataType" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">
								<td align="left" class="tblcol" valign="top" width="18%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.datatype.type.string"/>&nbsp;</td>
								</logic:equal>
								
								<logic:equal name="additionalDBFieldBean" property="dataType" value="<%=Integer.toString(FieldMapping.TIMESTAMP_TYPE)%>">
								<td align="left" class="tblcol" valign="top" width="18%"><bean:message bundle="sessionmanagerResources" key="sessionmanager.datatype.type.timestamp"/>&nbsp;</td>
								</logic:equal>
								
                                    
                                <td align="left" class="tblcol" valign="top" width="12%"><bean:write	name="additionalDBFieldBean" property="defaultValue" />&nbsp;</td>
                </tr>
                </logic:equal>
                </logic:iterate>
                
            
				</table>
				</td>	
		</tr>
		   				
		</table>
		</td>
	</tr>
</table>
		