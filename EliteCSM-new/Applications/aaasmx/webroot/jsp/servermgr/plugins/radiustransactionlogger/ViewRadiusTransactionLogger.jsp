<%@page import="com.elitecore.elitesm.web.plugins.forms.TransactionLoggerForm"%>
<%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>

<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" />

	 <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >            						  	
	  	 <tr> 
			<td class="tblheader-bold" height="20%" colspan="2">
				<bean:message bundle="pluginResources" key="plugin.view"/>
			</td>
		</tr>  
	     <tr>
			<td align="left" class="tblheader-bold" valign="top" colspan="2">
			<bean:message bundle="pluginResources" key="plugin.plugininstancedetails" /></td>
		 </tr>  					
	   	 <tr>
			<td class="tblfirstcol" width="25%" height="20%" >
				<bean:message bundle="pluginResources" key="plugin.instname" />
			</td>
			<td class="tblcol" width="75%" height="20%" >
				<bean:write name="transactionLoggerForm" property="pluginName" />&nbsp;
			</td>
		 </tr>   
	 	 <tr>
			<td class="tblfirstcol" width="25%" height="20%">
				<bean:message bundle="pluginResources" key="plugin.instdesc" />
			</td>
			<td class="tblcol" width="75%" height="20%" >
				<bean:write name="transactionLoggerForm" property="description"/>&nbsp;
			</td>
		</tr>
		<tr>
			<td class="tblfirstcol" width="25%" height="20%">
				<bean:message bundle="pluginResources" key="plugin.status" />
			</td>
			<td class="tblcol" width="75%" height="20%" valign="middle">
				<logic:equal name="transactionLoggerForm" property="status" value="1">
				    <img src="<%=basePath%>/images/active.jpg"/>&nbsp;&nbsp;&nbsp;<bean:message bundle="servicePolicyProperties" key="servicepolicy.active" />
				</logic:equal>
				<logic:equal name="transactionLoggerForm" property="status" value="0">
				   <img src="<%=basePath%>/images/deactive.jpg" />&nbsp;&nbsp;&nbsp;<bean:message bundle="servicePolicyProperties" key="servicepolicy.inactive" />
				</logic:equal>
			</td>
		</tr>
		<tr>
			<td class="tblfirstcol" width="25%" height="20%">
				<bean:message bundle="pluginResources" key="plugin.transactionlogger.timeboundry" />
			</td>
			<td class="tblcol" width="75%" height="20%" >
				<logic:equal name="transactionLoggerForm" property="timeBoundry" value="1">
					<bean:message bundle="pluginResources" key="plugin.timeboundry.onemin" />
					&nbsp;
				</logic:equal>
				<logic:equal name="transactionLoggerForm" property="timeBoundry" value="2">
					<bean:message bundle="pluginResources" key="plugin.timeboundry.twomin" />
					&nbsp;
				</logic:equal>
				<logic:equal name="transactionLoggerForm" property="timeBoundry" value="3">
					<bean:message bundle="pluginResources" key="plugin.timeboundry.threemin" />
					&nbsp;
				</logic:equal>
				<logic:equal name="transactionLoggerForm" property="timeBoundry" value="5">
					<bean:message bundle="pluginResources" key="plugin.timeboundry.fivemin" />
					&nbsp;
				</logic:equal>
				<logic:equal name="transactionLoggerForm" property="timeBoundry" value="10">
					<bean:message bundle="pluginResources" key="plugin.timeboundry.tenmin" />
					&nbsp;
				</logic:equal>
				<logic:equal name="transactionLoggerForm" property="timeBoundry" value="15">
					<bean:message bundle="pluginResources" key="plugin.timeboundry.fifteenmin" />
					&nbsp;
				</logic:equal>
				<logic:equal name="transactionLoggerForm" property="timeBoundry" value="30">
					<bean:message bundle="pluginResources" key="plugin.timeboundry.thirtymin" />
					&nbsp;
				</logic:equal>
				<logic:equal name="transactionLoggerForm" property="timeBoundry" value="60">
					<bean:message bundle="pluginResources" key="plugin.timeboundry.hourly" />
					&nbsp;
				</logic:equal>
				<logic:equal name="transactionLoggerForm" property="timeBoundry" value="1440">
					<bean:message bundle="pluginResources" key="plugin.timeboundry.daily" />
					&nbsp;
				</logic:equal>
			</td>
		</tr>
		<tr>
			<td class="tblfirstcol" width="25%">
				<bean:message bundle="pluginResources" key="plugin.transactionlogger.logfile" />
			</td>
			<td class="tblcol" width="75%">
				<div style="word-break:break-all;"><bean:write name="transactionLoggerForm" property="logFile"/>&nbsp;</div>
			</td>
		</tr>
		<tr>
			<td class="tblfirstcol" width="25%">
				<bean:message bundle="pluginResources" key="plugin.range" />
			</td>
			<td class="tblcol" width="75%">
				<div><bean:write name="transactionLoggerForm" property="range"/>&nbsp;</div>
			</td>
		</tr>
		<tr>
			<td class="tblfirstcol" width="25%">
				<bean:message bundle="pluginResources" key="plugin.pos" />
			</td>
			<td class="tblcol" width="75%">
				<div class="capitalize"><bean:write name="transactionLoggerForm" property="pattern"/>&nbsp;</div>
			</td>
		</tr>
		<tr>
			<td class="tblfirstcol" width="25%">
				<bean:message bundle="pluginResources" key="plugin.global" />
			</td>
			<td class="tblcol" width="75%">
				<div  class="capitalize"><bean:write name="transactionLoggerForm" property="globalization"/>&nbsp;</div>
			</td>
		</tr>
		 <tr>
			<td class="tblheader-bold" height="20%" colspan="2">
				<bean:message bundle="pluginResources" key="plugin.transactionlogger.formatmappings" />
			</td>
		</tr>
		<tr>
			<td  height="20%" colspan="2" style="padding-top: 10px;padding-bottom: 10px;" class="captiontext">
				<table width="75%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td class="tblheader" width="30%">
						<bean:message bundle="pluginResources" key="plugin.transactionlogger.formatmappings.key" /> 
					</td>
					<td class="tblheader" style="border-right: 2px solid #D9E6F6;">
						<bean:message bundle="pluginResources" key="plugin.transactionlogger.formatmappings.format" /> 
					</td>
				</tr>
				<logic:iterate id="obj" name="transactionLoggerForm" property="formatMappingDataSet">
				<tr>
					<td class="tblfirstcol" width="20%">
						<bean:write name="obj" property="key" />
					</td>
					<td class="tblrows">
						<bean:write name="obj" property="format"  />
					</td>
				</tr>
				</logic:iterate>
			</table>
			</td>
		</tr>
	</table>


