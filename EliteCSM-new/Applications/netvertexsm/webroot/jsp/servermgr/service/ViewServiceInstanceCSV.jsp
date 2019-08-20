<%@ include file="/jsp/core/includes/common/Header.jsp"%>






<%@ page import="com.elitecore.netvertexsm.web.servermgr.server.form.ViewCSVForm"%>
<%@ page import="java.util.*"%>

<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceInstanceData"%>
<%@ page import="com.elitecore.netvertexsm.web.servermgr.server.form.LiveCSVData"%>



<%
	String localBasePath = request.getContextPath();
	INetServerInstanceData localNetServerInstanceData = (INetServerInstanceData)request.getAttribute("netServerInstanceData");
	INetServiceInstanceData localNetServiceInstanceData = (INetServiceInstanceData)request.getAttribute("netServiceInstanceData");
	ViewCSVForm viewCSVForm = (ViewCSVForm)request.getAttribute("viewCSVForm");
	List csvList = ((ViewCSVForm)request.getAttribute("viewCSVForm")).getListCSV();
	List lstNumberOfRecords = ((ViewCSVForm)request.getAttribute("viewCSVForm")).getLstNumberOfRecords();
	String actionCheck =  viewCSVForm.getAction();
	int iIndex = 0;
	
%>

<script>
	
	var dFormat;
	//dFormat = 'yyyy-MM-dd';
	dFormat = 'MM-dd-yyyy';
	function popUpCalendar(ctl,	ctl2, datestyle)
	{
		//Get format from system parameter document.form[0].
		datestyle = dFormat;
		jsPopUpCalendar( ctl, ctl2, datestyle ); 

	}
	
	function validateCreate(){
		
		
		
		if(document.forms[0].checkFilter.checked == true){
			if(isNull(document.forms[0].strStartDate.value)){
			alert('Start Date must be specified');
		}else if(isNull(document.forms[0].strEndDate.value)){
			alert('End Date must be specified');
		}
		else
		{
			if (Date.parse(document.forms[0].strStartDate.value) > Date.parse(document.forms[0].strEndDate.value)) {
				alert("Invalid Date Range!\nStart Date cannot be after End Date!")
			}
			else{
			document.forms[0].submit();
			}
		}
		
		}
		else{
			document.forms[0].submit();
		}
	
		
	
	}
	function popup(mylink, windowname)
	{
		
		if (! window.focus)
			return true;
		
		var href;
		if (typeof(mylink) == 'string')
			href=mylink;
		else
			href=mylink.href;
						
		windowname= windowname.substring(14,windowname.length - 4);					
		windowname='test';

		var winDic = window.open(href, windowname, 'width=700,height=500,left=150,top=100,scrollbars=yes');
		winDic.focus();
		
		return false;
	}

</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">


<html:form action="/viewServiceInstanceCSV.do">
<html:hidden name="viewCSVForm" styleId="action" property="action" value="search"/>	
<html:hidden name="viewCSVForm" styleId="netServerId" property="netServerId" />
<html:hidden name="viewCSVForm" styleId="netServiceId" property="netServiceId" />

	<tr> 
    	<td>&nbsp;</td>
  	</tr>
	<tr> 
	    <td class="table-header" >
        </td>
    </tr>	
    
    <logic:equal name="viewCSVForm" property="errorCode" value="0">	
	
	<tr> 
	    <td class="table-header" ><bean:message bundle="radiusResources" key="listCSV.label"/>
        </td>
    </tr>	

				<tr>
					<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
				</tr>	
				<tr>
					<td colspan="3">
						<table width="97%" align="right" border="0" cellpadding="0" cellspacing="0">
						
						
										<tr>
											<td align="left" class="labeltext" valign="bottom" width="20%"><bean:message bundle="radiusResources" key="radius.listCSV.username.label"/></td>
											<td align="left" class="labeltext" valign="top">
												<html:text styleId="user_name" property="user_name" size="30" maxlength="30"/>
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="bottom" width="20%"><bean:message bundle="radiusResources" key="radius.listCSV.nasPortType.label"/></td>
											<td align="left" class="labeltext" valign="top">
												<html:text styleId="nasPortType" property="nasPortType" size="30" maxlength="30"/>
											</td>
										</tr>
										
										<tr>
											<td colspan="4">
												<table width="100%" cellpadding="0" cellspacing="0" border="0">
													<tr>
														<td width="20%" class="labeltext" valign="bottom" align="left">
															<bean:message bundle="radiusResources" key="CSV.start.date"/>
														</td>
														<td class="labeltext" valign="top" align="left" width="30%">
															<html:text styleId="strStartDate" property="strStartDate"/>
									                    		<a  href="javascript:void(0)"  onclick="popUpCalendar(this, document.forms[0].strStartDate)" >
														          		<img src="<%=localBasePath%>/images/calendar.jpg" border="0" tabindex="6">
														        </a>
																<font color="#FF0000"> *</font>
														</td>
														<td width="10%" class="labeltext" valign="bottom" align="left">
															<bean:message bundle="radiusResources" key="CSV.end.date"/>
														</td>
														<td class="labeltext" valign="top" align="left">
															<html:text styleId="strEndDate" property="strEndDate"/>
					                    					<a  href="javascript:void(0)"  onclick="popUpCalendar(this, document.forms[0].strEndDate)" >
										                   		<img src="<%=localBasePath%>/images/calendar.jpg" border="0" tabindex="6">
										                   </a>
															<font color="#FF0000"> *</font>	
														</td>
													</tr>
												</table>
											</td>
										</tr>
										
										
										
										
										
										
										
										
										<tr>
											<%--<td colspan="2">
												<table width="100%" cellpadding="0" cellspacing="0" border="0">
													<tr>--%>
														<td width="20%" class="labeltext" valign="bottom" align="left">
															<bean:message bundle="radiusResources" key="radius.listCSV.applyFilter.label"/>
														</td>
														<td class="labeltext" valign="top" align="left">
															<html:checkbox name="viewCSVForm" styleId="checkFilter" property="checkFilter" value="1">
															</html:checkbox>
														</td>
														
													<%--</tr>
												</table>
											</td>--%>
										</tr>
										
										
										
										
										
										
										
										
										
										
										
										
										
										
										
										
										
										
										
										<%--<tr>
											<td align="left" class="labeltext" valign="bottom" width="15%"><bean:message bundle="radiusResources" key="radius.listCSV.applyFilter.label"/></td>
											<td align="left" class="labeltext" valign="top">
											
												<html:checkbox name="viewCSVForm" property="checkFilter" value="1">
												</html:checkbox>
												
											</td>
										</tr>
										
										--%><tr>
											<td>&nbsp;</td>
										</tr>
	            				 		 
										<tr>
 
											<td valign="middle" colspan="3">
											    <input type="button" name="Search" name="CSVSearch" Onclick="validateCreate()" value="   Search   " class="light-btn" />       
												<input type="button" name="Reset" onclick="reset();" value="   Reset    " class="light-btn" >  
											</td>
										</tr>	

										
										<tr> 
					    					<td class="small-gap" colspan="3">&nbsp;</td>
				  						</tr>
						  				<tr> 
						    				<td class="small-gap" colspan="3">&nbsp;</td>
						  				</tr>
						  				<tr> 
						    				<td class="small-gap" colspan="3">&nbsp;</td>
						  				</tr>
						  				
									
<%
if(actionCheck!=null){
%>	


	
	
	
	
	
	
	














	
								
		<%
				if(csvList != null && csvList.size() > 0){
		%>	
		
		<%
				if(lstNumberOfRecords != null && lstNumberOfRecords.size() > 0){
		%>
										<tr>
											<td align="left" colspan="4">
												<logic:iterate id="liveCSVDataForNoOfRecords" name="viewCSVForm" property="lstNumberOfRecords" type="com.elitecore.netvertexsm.web.servermgr.server.form.LiveCSVData">
													<tr>
														<td align="right" class="blue-text" colspan="3" >Total Files :
															<bean:write name="liveCSVDataForNoOfRecords" property="totalFiles"/>
														</td>
														<td align="right" class="blue-text">Filtered Files :
															<bean:write name="liveCSVDataForNoOfRecords" property="totalFilesFiltered"/>
														</td>
													</tr>
												</logic:iterate>
											</td>	
										</tr>	
		<%} %>								
															
										<tr>
											
			                				
						                    <td align="left" class="tblheader" valign="top" width="5%"><bean:message bundle="radiusResources" key="csv.list.serialnumber.label"/></td>
						                    <td align="left" class="tblheader" valign="top" width="46%"><bean:message bundle="radiusResources" key="csv.list.name.label"/></td>
						                	<%--<td align="left" class="tblheader" valign="top" width="24%"><bean:message bundle="radiusResources" key="csv.list.description.label"/></td>
						                	--%><td align="left" class="tblheader" valign="top" width="14%"><bean:message bundle="radiusResources" key="csv.list.totalRecords.label"/></td>
						                	<td align="left" class="tblheader" valign="top" width="34%"><bean:message bundle="radiusResources" key="csv.list.totalRecordsFiltered.label"/></td>
						                </tr>
					                
				
			
			
									  <logic:iterate id="liveCSVData" name="viewCSVForm" property="listCSV" type="com.elitecore.netvertexsm.web.servermgr.server.form.LiveCSVData">
						                <tr>
						                  
									      <td align="left" class="tblfirstcol" valign="top"><%=(iIndex+1) %></td>
									      
						                  <td align="left" class="tblrows" valign="top">
											<a href='<%=localBasePath%>/showServiceInstanceCSVFile.do?fileName=<bean:write name="liveCSVData" property="description"/>&netServerId=<%=localNetServerInstanceData.getNetServerId() %>&netServiceId=<%=localNetServiceInstanceData.getNetServiceId() %>&userName=<bean:write name="viewCSVForm" property="user_name"/>&strNasPortType=<bean:write name="viewCSVForm" property="nasPortType"/>' onClick="return popup(this, '<%=liveCSVData.getDescription()%>')"><bean:write name="liveCSVData" property="name"/></a>
						                  </td>
						                  <%--<td align="left" class="tblcol" valign="top">
						                  	<bean:write name="liveCSVData" property="description"/>
						                  </td>
						                  --%><td align="left" class="tblrows" valign="top">
						                  	<bean:write name="liveCSVData" property="totalRecords"/>
						                  </td>
						                  <td align="left" class="tblcol" valign="top">
						                  	<bean:write name="liveCSVData" property="totalRecordsFiltered"/>
						                  </td>
						                </tr>                
					                <% iIndex += 1; %>
								   </logic:iterate>
								   		<tr> 
						    				<td class="small-gap" colspan="3">&nbsp;</td>
						  				</tr>
						  				<tr> 
						    				<td class="small-gap" colspan="3">&nbsp;</td>
						  				</tr>
						  				
			<%
				}else{
				
			%>
								<tr>
											<td align="left" colspan="4">
												<logic:iterate id="liveCSVDataForNoOfRecords" name="viewCSVForm" property="lstNumberOfRecords" type="com.elitecore.netvertexsm.web.servermgr.server.form.LiveCSVData">
													<tr>
														<td align="right" class="blue-text" colspan="3" >Total Files :
															<bean:write name="liveCSVDataForNoOfRecords" property="totalFiles"/>
														</td>
														<td align="right" class="blue-text">Filtered Files :
															<bean:write name="liveCSVDataForNoOfRecords" property="totalFilesFiltered"/>
														</td>
													</tr>
												</logic:iterate>
											</td>	
										</tr>	
								<tr>	
			                	
				                    <td align="left" class="tblheader" valign="top" width="5%"><bean:message bundle="radiusResources" key="csv.list.serialnumber.label"/></td>
				                    <td align="left" class="tblheader" valign="top" width="46%"><bean:message bundle="radiusResources" key="csv.list.name.label"/></td>
				                	<%--<td align="left" class="tblheader" valign="top" width="48%"><bean:message bundle="radiusResources" key="csv.list.description.label"/></td>
				                --%>
				                	<td align="left" class="tblheader" valign="top" width="14%"><bean:message bundle="radiusResources" key="csv.list.totalRecords.label"/></td>
						                	<td align="left" class="tblheader" valign="top" width="34%"><bean:message bundle="radiusResources" key="csv.list.totalRecordsFiltered.label"/></td>
				                </tr>
							    <tr>
				                  <td align="center" class="tblfirstcol" colspan="4"><bean:message bundle="servermgrResources" key="servermgr.norecordsfound"/></td>
				                </tr>
<%
	}}
%>	
								<tr> 
					    			<td class="small-gap" colspan="3">&nbsp;</td>
				  				</tr>
				  				<tr> 
				    				<td class="small-gap" colspan="3">&nbsp;</td>
				  				</tr>
				  				<tr> 
						    		<td class="small-gap" colspan="3">&nbsp;</td>
						  		</tr>
				
						</table>
					</td>
				</tr>						
				
			</logic:equal>
			
			
			
		<logic:equal name="viewCSVForm" property="errorCode" value="1">	

			<tr> 
	    <td class="table-header" ><bean:message bundle="radiusResources" key="listCSV.label"/>
        </td>
    </tr>	

				<tr>
					<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
				</tr>	
				<tr>
					<td colspan="3">
						<table width="97%" align="right" border="0" cellpadding="0" cellspacing="0">
						
						
										<tr>
											<td align="left" class="labeltext" valign="bottom" width="20%"><bean:message bundle="radiusResources" key="radius.listCSV.username.label"/></td>
											<td align="left" class="labeltext" valign="top">
												<html:text styleId="user_name" property="user_name" size="30" maxlength="30"/>
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="bottom" width="20%"><bean:message bundle="radiusResources" key="radius.listCSV.nasPortType.label"/></td>
											<td align="left" class="labeltext" valign="top">
												<html:text styleId="nasPortType" property="nasPortType" size="30" maxlength="30"/>
											</td>
										</tr>
										
										<tr>
											<td colspan="4">
												<table width="100%" cellpadding="0" cellspacing="0" border="0">
													<tr>
														<td width="20%" class="labeltext" valign="bottom" align="left">
															<bean:message bundle="radiusResources" key="CSV.start.date"/>
														</td>
														<td class="labeltext" valign="top" align="left" width="30%">
															<html:text styleId="strStartDate" property="strStartDate"/>
									                    		<a  href="javascript:void(0)"  onclick="popUpCalendar(this, document.forms[0].strStartDate)" >
														          		<img src="<%=localBasePath%>/images/calendar.jpg" border="0" tabindex="6">
														        </a>
																<font color="#FF0000"> *</font>
														</td>
														<td width="10%" class="labeltext" valign="bottom" align="left">
															<bean:message bundle="radiusResources" key="CSV.end.date"/>
														</td>
														<td class="labeltext" valign="top" align="left">
															<html:text styleId="strEndDate" property="strEndDate"/>
					                    					<a  href="javascript:void(0)"  onclick="popUpCalendar(this, document.forms[0].strEndDate)" >
										                   		<img src="<%=localBasePath%>/images/calendar.jpg" border="0" tabindex="6">
										                   </a>
															<font color="#FF0000"> *</font>	
														</td>
													</tr>
													<tr>
														<%--<td colspan="2">
															<table width="100%" cellpadding="0" cellspacing="0" border="0">
																<tr>
																	--%><td width="20%" class="labeltext" valign="bottom" align="left">
																		<bean:message bundle="radiusResources" key="radius.listCSV.applyFilter.label"/>
																	</td>
																	<td class="labeltext" valign="top" align="left">
																		<html:checkbox name="viewCSVForm" styleId="checkFilter" property="checkFilter" value="1">
																		</html:checkbox>
																	</td>
																	
																<%--</tr>
															</table>
														</td>--%>
													</tr>
													
													
													
												</table>
											</td>
										</tr>
										
										<tr>
											<td>&nbsp;</td>
										</tr>
	            				 		 
										<tr>
 
											<td valign="middle" colspan="3">
											    <input type="button" name="Search" name="CSVSearch" Onclick="validateCreate()" value="   Search   " class="light-btn" />       
												<input type="button" name="Reset" onclick="reset();" value="   Reset    " class="light-btn" >  
											</td>
										</tr>	

										
										<tr> 
					    					<td class="small-gap" colspan="3">&nbsp;</td>
				  						</tr>
						  				<tr> 
						    				<td class="small-gap" colspan="3">&nbsp;</td>
						  				</tr>
						  				<tr> 
						    				<td class="small-gap" colspan="3">&nbsp;</td>
						  				</tr>
						  				
						  				<tr>
											<td align="left" colspan="4">
												<logic:iterate id="liveCSVDataForNoOfRecords" name="viewCSVForm" property="lstNumberOfRecords" type="com.elitecore.netvertexsm.web.servermgr.server.form.LiveCSVData">
													<tr>
														<td align="right" class="blue-text" colspan="3" >Total Files :
															<bean:write name="liveCSVDataForNoOfRecords" property="totalFiles"/>
														</td>
														<td align="right" class="blue-text">Filtered Files :
															<bean:write name="liveCSVDataForNoOfRecords" property="totalFilesFiltered"/>
														</td>
													</tr>
												</logic:iterate>
											</td>	
										</tr>	
						  				
						  				<tr>	
			                	
				                    		<td align="left" class="tblheader" valign="top" width="1%"><bean:message bundle="radiusResources" key="csv.list.serialnumber.label"/></td>
				                    		<td align="left" class="tblheader" valign="top" width="48%"><bean:message bundle="radiusResources" key="csv.list.name.label"/></td>
				                			<%--<td align="left" class="tblheader" valign="top" width="48%"><bean:message bundle="radiusResources" key="csv.list.description.label"/></td>
				                			--%>
				                			<td align="left" class="tblheader" valign="top" width="14%"><bean:message bundle="radiusResources" key="csv.list.totalRecords.label"/></td>
						                	<td align="left" class="tblheader" valign="top" width="34%"><bean:message bundle="radiusResources" key="csv.list.totalRecordsFiltered.label"/></td>
				                		</tr>
									    <tr>
						                  <td align="center" class="tblfirstcol" colspan="4"><bean:message bundle="servermgrResources" key="servermgr.norecordsfound"/></td>
						                </tr>
								
										<tr> 
							    			<td class="small-gap" colspan="3">&nbsp;</td>
						  				</tr>
						  				<tr> 
						    				<td class="small-gap" colspan="3">&nbsp;</td>
						  				</tr>
						  				<tr> 
								    		<td class="small-gap" colspan="3">&nbsp;</td>
								  		</tr>
				
						</table>
					</td>
				</tr>					

		</logic:equal>
			
			
			
			
			
			
			
			
			
				
			</html:form>
			<logic:equal name="viewCSVForm"
			property="errorCode" value="-1">
			
			
		<tr> 
			<td>&nbsp;</td>
		</tr>
  			
  		<tr>
     		<td valign="top" align="right"> 
       			<table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >	
					<tr>
						<td class="blue-text-bold">
							<bean:message bundle="servermgrResources"
								key="servermgr.connectionfailure" />
							<br>
							<bean:message bundle="servermgrResources"
								key="servermgr.admininterfaceip" />
							:
							<bean:write name="netServerInstanceData" property="adminHost" />
							<br>
							<bean:message bundle="servermgrResources"
								key="servermgr.admininterfaceport" />
							:
							<bean:write name="netServerInstanceData" property="adminPort" />
							&nbsp;
						</td>
					</tr>
				</table>
			</td>
		</tr>
									
		<tr>
			<td>&nbsp;</td>
		</tr>
		</logic:equal> 
		
</table>			
	
	
	
	
	
	
	
	
	
<%@ include file="/jsp/core/includes/common/Footer.jsp" %>