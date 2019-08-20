<%@page import="com.elitecore.elitesm.web.plugins.forms.UserStatisticPostAuthPluginForm"%>
<%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>

<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" />
<script type="text/javascript"  src="<%=request.getContextPath()%>/js/plugin/user-stat-post-auth-plugin-view.js"></script>
<%
	UserStatisticPostAuthPluginForm userStatisticPostAuthPluginForm = (UserStatisticPostAuthPluginForm)request.getAttribute("userStatisticPostAuthPluginForm");
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >            						  	
	 <tr> 
		<td class="tblheader-bold" height="20%" colspan="2">
			<bean:message bundle="pluginResources" key="plugin.view"/>
		</td>
	</tr>  
	 <tr>
		<td align="left" class="tblheader-bold" valign="top" colspan="2">
			<bean:message bundle="pluginResources" key="plugin.plugininstancedetails" />
		</td>
	</tr>  					
	 <tr>
		<td class="tblfirstcol" width="13%" height="20%" >
			<bean:message bundle="pluginResources" key="plugin.instname" />
		</td>
		<td class="tblcol" width="30%" height="20%" >
			<bean:write name="userStatisticPostAuthPluginForm" property="pluginName" />&nbsp;
		</td>
	 </tr>   
	 <tr>
		<td class="tblfirstcol" width="13%" height="20%">
			<bean:message bundle="pluginResources" key="plugin.instdesc" />
		</td>
		<td class="tblcol" width="30%" height="20%" >
			<bean:write name="userStatisticPostAuthPluginForm" property="description"/>&nbsp;
		</td>
	</tr>
	<tr>
		<td class="tblfirstcol" width="13%" height="20%">
			<bean:message bundle="pluginResources" key="plugin.status" />
		</td>
		<td class="tblcol" width="30%" height="20%" valign="middle">
			<logic:equal name="userStatisticPostAuthPluginForm" property="status" value="1">
			    <img src="<%=basePath%>/images/active.jpg" class="status_class" />&nbsp;&nbsp;&nbsp;<bean:message bundle="servicePolicyProperties" key="servicepolicy.active" />
			</logic:equal>
			<logic:equal name="userStatisticPostAuthPluginForm" property="status" value="0">
			   <img src="<%=basePath%>/images/deactive.jpg" class="status_class"  />&nbsp;&nbsp;&nbsp;<bean:message bundle="servicePolicyProperties" key="servicepolicy.inactive" />
			</logic:equal>
		</td>
	</tr>
		<% if (userStatisticPostAuthPluginForm.getUserStatPostAuthJson() != null) { %>
	
	<tr>
		<td colspan="2">
		 	<table width="100%" cellspacing="0" cellpadding="0" border="0" class="parameterlist-pre-mapping-table"  id="plugin-mapping-table-first">
				<tr>
		 			<td class="tblheader-bold" height="20%" colspan="2">
						<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.title" />
					</td>
				</tr>
				
				<!--Datasource Name -->
				<tr>
					<td class="tblfirstcol" width="13%" height="20%">
						<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.datasourcename" />
					</td>
					<td width="30%" height="20%" class="tblcol" >
						<logic:notEmpty name="userStatisticPostAuthPluginForm" property="dataSourceName">
							<span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="userStatisticPostAuthPluginForm" property="databaseId"/>','<bean:write name="userStatisticPostAuthPluginForm" property="dataSourceName"/>','<%=EliteViewCommonConstant.DATABASE_DATASOURCE%>');">
								<bean:write name="userStatisticPostAuthPluginForm" property="dataSourceName" />
							</span>
						</logic:notEmpty>
					</td>
				</tr>
				
				<!--Table Name-->
				<tr>
					<td class="tblfirstcol"width="13%" height="20%">
						<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.tablename" />
					</td>
					<td width="30%" height="20%" class="tblcol" >
						<span class="tableName"></span>
					</td>
				</tr>
									
				<!--DB Query Time Out -->
				<tr>
					<td class="tblfirstcol" width="13%" height="20%">
						<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.dbquerytimeout" />
					</td>
					<td class="tblcol" width="30%" height="20%">
						<span class="dbQueryTimeoutInMs"></span>
					</td>
				</tr>
									
									
				<!-- Max Query Time Out -->
				<tr>
					<td class="tblfirstcol" width="13%" height="20%"> 
						<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.maxquerytimeoutcount" />
					</td>
					<td class="tblcol" width="30%" height="20%">
						<span class="maxQueryTimeoutCount"></span>
					</td>
				</tr>
									
				<!-- Batch Update Interval -->
				<tr>
					<td class="tblfirstcol" width="13%" height="20%">
						<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.batchupdateinterval" />
					</td>
					<td class="tblcol" width="30%" height="20%">
						<span class="batchUpdateIntervalInMs"></span>
			    	</td>
				</tr>
									
				<tr>
			 		<td class="tblheader-bold" height="20%" colspan="2">
						<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.mapping.title" />
					</td>
				</tr>
				<tr>
					<td>
						&nbsp;
					</td>
				</tr>
				<tr>
					<td  class="captiontext" width="100%" colspan="2">
						<table width="97%" cellspacing="0" cellpadding="0" border="0" id="plugin-mapping-table">
							<tbody>
								<tr>
								<!--Attribute Id -->
									<td class="tblheader-bold" width="16%">
										<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.attributeid"/>
									</td>
																
								<!--Packet Type -->			
									<td class="tblheader-bold" width="16%">
										<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.packettype"/>
									</td>
															
								<!-- DB Field -->
									<td class="tblheader-bold" width="16%">
										<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.dbfield"/>
									</td>
									
								<!-- Data Type -->
									<td class="tblheader-bold" width="16%">
										<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.datatype"/>
									</td>
																
								<!-- Default Value -->
									<td class="tblheader-bold" width="16%">
										<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.defaultvalue"/>
									</td>
																
								<!-- Used Dictionary Value -->
									<td class="tblheader-bold" width="16%">
										<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.usedictionaryvalue"/>
									</td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						&nbsp;
					</td>
				</tr>
			</table>
		</td>
	</tr>
  <%} %>
</table>


<table id="plugin-mapping-table-template" class="plugin-mapping-table-template" style="display: none;">
	
	<tr>
		<td class="tblfirstcol">
			<span class="attributeId" ></span>
		</td>
		<td class="tblrows">
			<span class="packetType"></span>
		</td>
		<td class="tblrows">
			<span class="dbField"></span>
		</td>
		<td class="tblrows">
			<span class="dataType"></span>
		</td>
		<td class="tblrows">
			<span class="defaultValue"></span>
		</td>
		<td class="tblrows">
			<span class="useDictionaryValue"></span>
		</td>	
	</tr>
</table>	
	
	<script type="text/javascript">
		/* Convert JSON format Data to UserStatisticPostAuth Plugin List */
		initializedUserStatPostAuthPlugins(JSON.stringify(<%=userStatisticPostAuthPluginForm.getUserStatPostAuthJson()%>));
	</script>
