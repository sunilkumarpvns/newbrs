<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.SessionConfData"%>
<%@ page import="java.util.*" %> 


<%
	SessionConfData sessionConfData= (SessionConfData)request.getAttribute("sessionConfData");
%>
<script type="text/javascript">

function show() {
	//document.getElementById("head").style.display = "block";
	//document.getElementById("tbl").style.display = "block";	
	//document.getElementById("gxFieldMap").style.display = "block";
			
}
	
</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
	<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right">
		<bean:define id="sessionInstanceBean" name="sessionConfData" scope="request" type="com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.SessionConfData" />
		
		<tr>
			<td class="tblheader-bold"  valign="top" colspan="3"><bean:message bundle="sessionMgrResources" key="session.viewsessionconf" /></td>
		</tr>
    	
 					<tr>
	       				<td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="sessionMgrResources" key="session.gx.datasource"/></td>
	       				<td class="tblcol" width="70%" height="20%">
	       					<%if(sessionInstanceBean.getDatabaseDS() != null){%>
	       					   <a href="<%=basePath%>/viewDatabaseDS.do?databaseId=<bean:write name ="sessionInstanceBean" property="databaseDS.databaseId"/>">
	       					   <bean:write name="sessionInstanceBean" property="databaseDS.name" />&nbsp;
	       					   </a>
	       					<%}else{%>
	       						&nbsp;
	       					<%}%>
	       				</td>
	    			</tr>
	    			<tr>
	       				<td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="sessionMgrResources" key="session.gx.secondary.datasource"/></td>
	       				<td class="tblcol" width="70%" height="20%">
	       					<%if(sessionInstanceBean.getSecondaryDatabaseDS() != null){%>
	       					    <a href="<%=basePath%>/viewDatabaseDS.do?databaseId=<bean:write name ="sessionInstanceBean" property="secondaryDatabaseDS.databaseId"/>">
	       						<bean:write name="sessionInstanceBean" property="secondaryDatabaseDS.name" />&nbsp;
	       					<%}else{%>
	       						&nbsp;
	       					<%}%>
	       				</td>
	    			</tr>
	    			<tr>
	       				<td class="tbllabelcol" width="30%" height="20%"><bean:message key="general.lastmodifieddate"/></td>
	       				<td class="tblcol" width="70%" height="20%"><%=EliteUtility.dateToString(sessionInstanceBean.getModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %> &nbsp;</td>
	    			</tr>
	    			<tr> 
                    	<td class="tblheader-bold"  height="20%" colspan="4"><bean:message bundle="sessionMgrResources" key="session.viewbatchupdateproperties"/></td>
                  	</tr>	    				    		    			
	    			<tr>
	       				<td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="sessionMgrResources" key="session.batchupdate"/></td>
	       				<td class="tblcol" width="70%" height="20%">
	       					<logic:equal name="sessionInstanceBean" property="batchUpdate" value="true"><bean:message bundle="sessionMgrResources" key="session.batchupdatevalue.true"/></logic:equal>
	       					<logic:equal name="sessionInstanceBean" property="batchUpdate" value="1"><bean:message bundle="sessionMgrResources" key="session.batchupdatevalue.true"/></logic:equal>
	       					<logic:equal name="sessionInstanceBean" property="batchUpdate" value="false"><bean:message bundle="sessionMgrResources" key="session.batchupdatevalue.false"/></logic:equal>
	       					<logic:equal name="sessionInstanceBean" property="batchUpdate" value="0"><bean:message bundle="sessionMgrResources" key="session.batchupdatevalue.false"/></logic:equal>
	       					<logic:equal name="sessionInstanceBean" property="batchUpdate" value="2"><bean:message bundle="sessionMgrResources" key="session.batchupdatevalue.other"/></logic:equal>
	       				</td>
	    			</tr>
	    			<tr>
	       				<td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="sessionMgrResources" key="session.batchsize"/></td>
	       				<td class="tblcol" width="70%" height="20%"><bean:write name="sessionInstanceBean" property="batchSize" /></td>
	    			</tr>
	    			<tr>
	       				<td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="sessionMgrResources" key="session.batchupdateinterval"/></td>
	       				<td class="tblcol" width="70%" height="20%"><bean:write name="sessionInstanceBean" property="batchUpdateInterval" /></td>
	    			</tr>
	    			<tr>
	       				<td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="sessionMgrResources" key="session.querytimeout"/></td>
	       				<td class="tblcol" width="70%" height="20%"><bean:write name="sessionInstanceBean" property="dbQueryTimeout" /></td>
	    			</tr>
	    			
   
</table>

<br>

	
<table width="100%" id="head"  border="0" cellspacing="0" cellpadding="0" height="*">
	<tr>
		<td valign="top" align="right" >		
			<table width="97%" align="right" cellpadding="0" cellspacing="0">
				<tr height="5px"> </tr>
				<tr>
					<td>
						<table id="tbl" class="c_tblCrossProductList" align="left" cellpadding="0" cellspacing="0" style=" width: 100%" >
					  		<tr> 
	     						<td class="tblheader" colspan="4" height="20%" style="font-size: 11px; line-height: 20px; padding-left: 10px; font-weight: bold"><bean:message bundle="sessionMgrResources" key="session.coresessionfieldmapping"/></td>
							</tr>
                  			<tr>
			       	 			<td width="10" class="small-gap">&nbsp;</td>
			       	 			<td class="small-gap" colspan="2">&nbsp;</td>
				  			</tr>			  			
							<tr>
								<td colspan="5">
									<table align="center" cellSpacing="0" cellPadding="0" width="90%" border="0" id="gxFieldMap" >
										<tr>								
											<td align="left" class="tblheaderfirstcol" valign="top" width="25%"><bean:message bundle="sessionMgrResources" key="session.gx.dbfieldname"/></td>								
											<td align="left" class="tblheader" valign="top" width="25%"><bean:message bundle="sessionMgrResources" key="session.gx.referringattr"/></td>
			                    			<td align="left" class="tblheaderlastcol" valign="top" width="25%"><bean:message bundle="sessionMgrResources" key="session.gx.datatype"/></td>    
										</tr>
										<%
											if(sessionConfData.getCoreSessionList()==null || sessionConfData.getCoreSessionList().size()==0){
										%>
										    <tr>
										    	<td colspan="3" align="center" class="tblfirstcol" ><bean:message bundle="datasourceResources" key="database.datasource.norecordfound"/> </td>
										    </tr>									
										<%												
											} else {
										%>
										
										<%for(int i=0;i<sessionConfData.getCoreSessionList().size();i++){ %>
			                			
											<tr>
						 						<td class="tblfirstcol" width="*" height="*"><%=sessionConfData.getCoreSessionList().get(i).getFieldName() %></td>
			 									<td class="tblrows" width="*" height="*"><%=sessionConfData.getCoreSessionList().get(i).getReferringAttr() %></td>
			 									<td class="tblrows" width="*" height="*"><%if(sessionConfData.getCoreSessionList().get(i).getDatatype() == 0){%>String<%	} else if(sessionConfData.getCoreSessionList().get(i).getDatatype() == 1){%>TimeStamp<%}%>
			 								</tr>
										<%} }%>
										<tr><td>&nbsp;</td></tr>
				            		</table>
		            			</td>            	
							</tr>
									
	 	   				</table>
	 	   				
	 	   				</td>
	 	   			</tr>
	 	   			<tr><td>&nbsp;</td></tr>
	 	   			<tr>
	 	   				<td>
	 	   					<table id="tbl" class="c_tblCrossProductList" align="left" cellpadding="0" cellspacing="0" style=" width: 100%" >
	 	   					<tr> 
	     					<td class="tblheader" colspan="4" height="20%" style="font-size: 11px; line-height: 20px; padding-left: 10px; font-weight: bold"><bean:message bundle="sessionMgrResources" key="session.subsessionfieldmapping"/></td>
							</tr>
							<tr>
			       	 			<td width="10" class="small-gap">&nbsp;</td>
			       	 			<td class="small-gap" colspan="2">&nbsp;</td>
				  			</tr>			  			
							<tr>
								<td colspan="5">
									<table align="center" cellSpacing="0" cellPadding="0" width="90%" border="0" id="gxFieldMap" >
										<tr>								
											<td align="left" class="tblheaderfirstcol" valign="top" width="25%"><bean:message bundle="sessionMgrResources" key="session.gx.dbfieldname"/></td>								
											<td align="left" class="tblheader" valign="top" width="25%"><bean:message bundle="sessionMgrResources" key="session.gx.referringattr"/></td>
			                    			<td align="left" class="tblheaderlastcol" valign="top" width="25%"><bean:message bundle="sessionMgrResources" key="session.gx.datatype"/></td>    
										</tr>
										<%
											if(sessionConfData.getSubSessionList()==null || (sessionConfData.getSubSessionList().size()==0)){
										%>
										    <tr>
										    	<td colspan="3" align="center" class="tblfirstcol" ><bean:message bundle="datasourceResources" key="database.datasource.norecordfound"/> </td>
										    </tr>									
										<%												
											} else {
										%>
										
										<%for(int i=0;i<sessionConfData.getSubSessionList().size();i++){ %>
			                			
											<tr>
						 						<td class="tblfirstcol" width="*" height="*"><%=sessionConfData.getSubSessionList().get(i).getFieldName() %></td>
			 									<td class="tblrows" width="*" height="*"><%=sessionConfData.getSubSessionList().get(i).getReferringAttr() %></td>
			 									<td class="tblrows" width="*" height="*"><%if(sessionConfData.getSubSessionList().get(i).getDatatype() == 0){%>String<%	} else if(sessionConfData.getSubSessionList().get(i).getDatatype() == 1){%>TimeStamp<%}%>
			 								</tr>
										<%} 
										}%>
										
				            		</table>
		            			</td>            	
							</tr>
							<tr> 
         						<td align="left" class="labeltext" colspan="3" valign="top"> &nbsp; </td> 
         					</tr>	
         					</table>
	 	   				</td>
	 	   			</tr>
	 	   			<tr height="15px"> </tr>
				</table>
			</td>
		</tr>
</table>	
		
		
		
		
		
		
		
		
		
