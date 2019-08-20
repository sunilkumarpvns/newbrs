<%@ page import="java.util.Set"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Iterator"%>
<%@ page
	import="com.elitecore.elitesm.datamanager.servermgr.data.NetConfigParamValuePoolData"%>
<%@ page import="java.util.StringTokenizer"%>
<%@ page
	import="com.elitecore.elitesm.web.servermgr.server.forms.NetConfParameterValueBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Collections"%>
<%@page
	import="com.elitecore.elitesm.web.core.system.profilemanagement.ProfileManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page
	import="com.elitecore.elitesm.web.servermgr.plugin.forms.UpdatePluginConfigurationForm"%>

<%
	String localBasePath = request.getContextPath();
	int varIPNodes = 0;
	int varDriverNodes = 0;
	if(request.getParameter("ipNodes") != null )
		varIPNodes = Integer.parseInt(request.getParameter("ipNodes"));	
	if(request.getParameter("pluginNodes") != null )		
		varDriverNodes = Integer.parseInt(request.getParameter("pluginNodes"));	
		
	if(varIPNodes <= 0)		
		varIPNodes = 1;
		
	if(varDriverNodes <= 0)		
		varDriverNodes = 1;

	UpdatePluginConfigurationForm updatePluginForm = (UpdatePluginConfigurationForm)request.getAttribute("updatePluginForm");
	List lstParameterValue = updatePluginForm.getLstParameterValue();
			
 %>

<script type="text/javascript">
javascript:location.href="#tip";
	function addIP(){
		document.forms[0].addNode.value = 'addIP';
		alert('New IP is going to be added....Do you want to proceed');
		document.forms[0].submit();
	}
	function addDrivers(){
		document.forms[0].addNode.value = 'addPlugin';
		alert('New Plugin is going to be added....Do you want to proceed');		
		document.forms[0].submit();
	}
	
	function addChildNode(objectId,objectInstanceId,childTotalInstanceVal){
        elableAll();
	  //  alert('before');
		document.forms[0].action.value='addnode';
		//alert('middle');
		document.forms[0].nodeParameterId.value = objectId;
		document.forms[0].nodeInstanceId.value = objectInstanceId;
		document.forms[0].childTotalInstanceVal.value = childTotalInstanceVal;
		
		//alert('after');
		document.forms[0].submit(); 
	}
	
	function saveConfiguration(){
		var check = validateParameterValues();
		if(check == true){
       	    elableAll();	    
		    document.forms[0].action.value='save';
		    document.forms[0].submit();
		}    
	}
	
	function deleteNode(objectInstanceId){
        elableAll();    
    	document.forms[0].action.value='delete';
    	document.forms[0].nodeInstanceId.value = objectInstanceId;
    	document.forms[0].submit();
	}
	
	function togglediv(object)
	{
		document.getElementById(object).style.display='none';
	
	}
    
    function elableAll() {
    	<%for(int i=0;i<lstParameterValue.size();i++){ %>  
			var strValue=document.getElementById('value[<%=i%>]');			
			if(strValue != null){
				strValue.disabled = false;
			}
	   <% }  %>   
    }
    function popUpDesc(valIndex) {    
    	var strValue=document.getElementById('description['+valIndex+']');			
    	if(strValue != null){
        	var varWindow = window.open('<%=localBasePath%>/jsp/servermgr/server/NetDescriptionPopup.jsp?description='+strValue.value,'DescriptionWin','top=100, left=200, height=200, width=500, scrollbars=yes, status=1');
        	varWindow.focus();
        }else{
        	alert('Description does not exist');
        }
    }
    
 	function validateParameterValues(){  
 		<%
		for(int i=0;i<lstParameterValue.size();i++){ %>  
			var strRegexp=document.getElementById('regexp[<%=i%>]').value;
			if(document.getElementById('value[<%=i%>]') != null && document.getElementById('value[<%=i%>]').type == 'text'){
				var strValue = document.getElementById('value[<%=i%>]').value;
				var strType=document.getElementById('type[<%=i%>]');
				var strIsNotNull = document.getElementById('isNotNull[<%=i%>]'); 							
				var strEditable = document.getElementById('editable[<%=i%>]'); 	 
				if( strType.value != 'P' && strEditable.value=='true'){
					if(strIsNotNull.value == 'true'){
						if(strValue != null && strValue != ""){
								var regexpValue = strRegexp;
								if(regexpValue != null && regexpValue != "" && strRegexp != 'regexp'){
									var re = new RegExp(eval(strRegexp));
									if(!re.test(strValue)){
										var strName = document.getElementById('name[<%=i%>]').value; 
										alert('Please enter valid '+ strName);
										return false;
									}
								}
						}else{
							var strName = document.getElementById('name[<%=i%>]').value; 
							alert('Please enter valid '+ strName);
							return false;
						}
					}else{
						if(strValue != null && strValue != "" ){
								var regexpValue = strRegexp;
								if(regexpValue != null && regexpValue != "" && strRegexp != 'regexp'){				
									//var regexpValue = strRegexp;
									var re = new RegExp(eval(strRegexp));
									if(!re.test(strValue)){
										var strName = document.getElementById('name[<%=i%>]').value; 
										alert('Please enter valid '+ strName);
										return false;
									}
								}
						}
					}
				}
			}
	   <% }  %>  
		return true;
    }
    
    function popup(mylink, windowname)
			{
				if (! window.focus)return true;
					var href;
				if (typeof(mylink) == 'string')
   					href=mylink;
				else
   					href=mylink.href;
 				window.open(href, windowname, 'width=700,height=500,left=150,top=100,scrollbars=yes');
				return false;
			}      
			
 function show(component) {
		var srcElement = document.getElementById(component);
    	image = "i" + component;
    	if(srcElement != null) {
    	if(srcElement.style.display == "block") {
       		srcElement.style.display = 'none';
       		document.getElementById(image).src = '<%=localBasePath%>/images/bottom-level.jpg';
    	} else {
       		srcElement.style.display = 'block';
       		document.getElementById(image).src = '<%=localBasePath%>/images/top-level.jpg';
    	}
  	  }
    }			
			
			 
</script>

<html:form action="/updatePluginConfiguration">
	<html:hidden name="updatePluginForm" styleId="action" property="action" />
	<html:hidden name="updatePluginForm" styleId="nodeParameterId"
		property="nodeParameterId" />
	<html:hidden name="updatePluginForm" styleId="nodeInstanceId"
		property="nodeInstanceId" />
	<html:hidden name="updatePluginForm" styleId="confInstanceId"
		property="confInstanceId" />
	<html:hidden name="updatePluginForm" styleId="pluginInstanceId"
		property="pluginInstanceId" />
	<html:hidden name="updatePluginForm" styleId="childTotalInstanceVal"
		property="childTotalInstanceVal" />

	<%
int j=0;
int k=0;
int index=1;
String strFieldname;
String strFieldValue;
String strChildMultipleInstance;
String strInstanceId, strChildInstatnceId;
String strInstanceIdVal,strChildInstatnceIdVal;
String strChildMaxInstance=null ;
String strChildMaxInstanceVal = null ;
String strChildTotalInstance =null;

%>

	<input type="hidden" name="addNode" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">

		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>

				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					height="15%" align="right">
					<tr>
						<td valign="top" align="right" width="97%">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="tblheader-bold" colspan="2" height="20%"><bean:message
											bundle="servermgrResources"
											key="servermgr.pluginconfigurationdetail" /></td>
								</tr>
								<tr>
									<td class="tblfirstcol" width="30%" height="20%"><bean:message
											bundle="resultMessageResources"
											key="servermgr.update.configuration.name" /></td>
									<td class="tblcol" width="70%" height="20%"><bean:write
											name="updatePluginForm" property="configurationName" /></td>
								</tr>
								<tr>
									<td class="tblfirstcol" width="30%" height="20%"><bean:message
											bundle="resultMessageResources"
											key="servermgr.update.configuration.plugin.name" /></td>
									<td class="tblcol" width="70%" height="20%"><bean:write
											name="updatePluginForm" property="pluginName" /></td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td>
							<table width="100%" border="0" cellspacing="0" cellpadding="0"
								height="15%">
								<tr>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td valign="center" align="right" colspan="2">
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td class="small-gap" colspan="3">&nbsp;</td>
											</tr>
											<tr>
												<td colspan="3">
													<%
			                             int size = updatePluginForm.getLstParameterValue().size();
			                             System.out.println("plugin size : "+size);
%>
													<table width="100%" border="0" cellpadding="0"
														cellspacing="0" class="box">
														<!-- added by dhavan -->
														<logic:iterate id="updatePluginBean"
															name="updatePluginForm" property="lstParameterValue"
															type="com.elitecore.elitesm.web.servermgr.server.forms.NetConfParameterValueBean">

															<%if(index<size){ %>
															<logic:iterate id="closeDivList" name="updatePluginBean"
																property="closeDivStatusLst">
																<logic:notEmpty name="closeDivList">
																	</tr>
													</table>
													</div> </logic:notEmpty> </logic:iterate> <%} %>
												
											<tr>
												<%
			                            String strParameterId       = "parameterId["+j+"]";
										String strSerialNo          = "serialNo["+j+"]";
										String strName              = "name["+j+"]";
										String strDisplayName       = "displayName["+j+"]";
										String strAlias             = "alias["+j+"]";
										String strMaxInstances      = "maxInstances["+j+"]";
										String strMultipleInstances = "multipleInstances["+j+"]";
										String strParentParameterId = "parentParameterId["+j+"]";
										                                                        
										String strParameterValueId  = "parameterValueId["+j+"]";
										String strConfigInstanceId  = "configInstanceId["+j+"]";
										String strType				= "type["+j+"]";
										String strCurrentInstanceNo = "currentInstanceNo["+j+"]";
										String strTotalInstance     = "totalInstance["+j+"]";
										String strDescription		= "description["+j+"]";
										String strMaxLength         = "maxLength["+j+"]";
										String strEditable          = "editable["+j+"]";
                                        String strStartUpMode       = "startUpMode["+j+"]";                                                                                
										String strStatus			= "status["+j+"]";
										String strRegexp 			= "regexp["+j+"]";
										String strConfigId          = "configId["+j+"]"; 
										String strPoolExists 		= "poolExists["+j+"]";	
										String strIsNotNull          = "isNotNull["+j+"]";
										String strIsNewAdded             =  "isNewAdded["+j+"]";
										String strIsRemoved         = "isRemoved["+j+"]";
									
										
										String descriptionVal = updatePluginForm.getDescription(j);
										String strNetConfigParamValuePool = "netConfigParamValuePool["+j+"]";
										Set netConfigParamValuePool = updatePluginForm.getNetConfigParamValuePool(j);
										String strMaxLengthVal      = String.valueOf(updatePluginForm.getMaxLength(j));
										String strMaxSizeVal      = null;
                                        String strChildTotalInstanceVal = null;                                        
										if(updatePluginForm.getMaxLength(j) > 60){
											strMaxSizeVal = "60";
										}else{
											strMaxSizeVal = String.valueOf(updatePluginForm.getMaxLength(j)+5);        
										}
			                             
			                             boolean bEditable = !updatePluginForm.getEditable(j);
			                             
			                             strFieldname  = "displayName["+ j +"]"; 
			                             strFieldValue = "value["+ j +"]"; 

			                             k=j+1;
			                             strChildMultipleInstance= "multipleInstances["+ k +"]";
			                             strInstanceId= "instanceId["+ j +"]";
			                             strChildInstatnceId = "instanceId["+ k +"]";
			                             String strStartDivStatus=  "startDivStatus["+ k +"]";
			                             strInstanceIdVal= updatePluginForm.getInstanceId(j);
			                             if(k < size){
			                             	strChildInstatnceIdVal = updatePluginForm.getInstanceId(k);
			                            	strChildMaxInstance = 	"maxInstances["+k+"]";
			                            	strChildMaxInstanceVal = String.valueOf(updatePluginForm.getMaxInstances(k));
				                            strChildTotalInstance = "totalInstance["+k+"]";
				                            strChildTotalInstanceVal = String.valueOf(updatePluginForm.getTotalInstance(k));
			                             }
			                            %>
												<html:hidden name="updatePluginForm"
													styleId="<%=strParameterId      %>"
													property="<%=strParameterId      %>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strSerialNo         %>"
													property="<%=strSerialNo         %>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strName             %>"
													property="<%=strName             %>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strDisplayName      %>"
													property="<%=strDisplayName      %>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strAlias            %>"
													property="<%=strAlias            %>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strMaxInstances     %>"
													property="<%=strMaxInstances     %>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strMultipleInstances%>"
													property="<%=strMultipleInstances%>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strParentParameterId%>"
													property="<%=strParentParameterId%>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strParameterValueId %>"
													property="<%=strParameterValueId %>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strConfigInstanceId %>"
													property="<%=strConfigInstanceId %>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strInstanceId       %>"
													property="<%=strInstanceId       %>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strType             %>"
													property="<%=strType             %>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strCurrentInstanceNo%>"
													property="<%=strCurrentInstanceNo%>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strTotalInstance    %>"
													property="<%=strTotalInstance    %>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strDescription      %>"
													property="<%=strDescription      %>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strMaxLength        %>"
													property="<%=strMaxLength        %>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strEditable         %>"
													property="<%=strEditable         %>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strStartUpMode      %>"
													property="<%=strStartUpMode         %>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strStatus  			%>" property="<%=strStatus  %>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strRegexp  			%>" property="<%=strRegexp  %>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strConfigId  		%>"
													property="<%=strConfigId  %>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strPoolExists  		%>"
													property="<%=strPoolExists  %>" />
												<html:hidden name="updatePluginForm"
													styleId="<%=strIsNotNull 		%>"
													property="<%=strIsNotNull %>" />




												<logic:notEqual name="updatePluginForm"
													property="<%=strStatus%>" value="Y">
													<html:hidden name="updatePluginForm"
														styleId="<%=strFieldValue  %>"
														property="<%=strFieldValue  %>" />
												</logic:notEqual>
												<logic:equal name="updatePluginForm"
													property="<%=strStatus%>" value="Y">
													<logic:notEqual name="updatePluginForm"
														property="<%=strType%>" value="P">
														<td align="left" valign="center" class="topHLine"
															width="30%" colspan="2">
													</logic:notEqual>


													<logic:equal name="updatePluginForm"
														property="<%=strType%>" value="P">
														<td align="left" class="tblheader-bold" valign="center"
															colspan="4">
													</logic:equal>
													<%
				                             StringTokenizer strToken = new StringTokenizer(strInstanceIdVal,".");
				                           for(int i=1;i<strToken.countTokens();i++){
				                           %>
													<%
				                         }
				                           %>

													<logic:equal name="updatePluginForm"
														property="<%=strIsNewAdded%>" value="true">
														<span class="blue-text-bold"><bean:write
																name="updatePluginForm" property="<%=strFieldname%>" />
														</span>
													</logic:equal>
													<logic:notEqual name="updatePluginForm"
														property="<%=strIsNewAdded%>" value="true">
														<bean:write name="updatePluginForm"
															property="<%=strFieldname%>" />
													</logic:notEqual>

													<img
														src="<%=localBasePath%>/images/<bean:write name="updatePluginForm" property="<%=strStartUpMode%>" />.jpg" />
													<%if(k< size ){%>
													<logic:greaterThan name="updatePluginForm"
														property="<%=strChildMaxInstance%>" value="1">
														<logic:greaterThan name="updatePluginForm"
															property="<%=strChildMaxInstance%>"
															value="<%=strChildTotalInstanceVal%>">
															<logic:match name="updatePluginForm"
																property="<%=strChildInstatnceId%>"
																value="<%=strInstanceIdVal%>" location="start">
																<input type="button" value="Add"
																	onclick="addChildNode('<bean:write name="updatePluginForm" property="<%=strParameterId%>" />','<bean:write name="updatePluginForm" property="<%=strInstanceId%>" />',<%=strChildTotalInstanceVal %>)"
																	class="light-btn" />
															</logic:match>
														</logic:greaterThan>
													</logic:greaterThan>
													<%}%>
													<logic:equal name="updatePluginForm"
														property="<%=strType%>" value="P">
														<logic:equal name="updatePluginForm"
															property="<%=strMultipleInstances%>" value="Y">
															<logic:greaterThan name="updatePluginForm"
																property="<%=strTotalInstance%>" value="1">
																<img src="<%=localBasePath%>/images/minus.jpg"
																	onclick="deleteNode('<bean:write name="updatePluginForm" property="<%=strInstanceId%>"/>')" />
															</logic:greaterThan>
														</logic:equal>
													</logic:equal>

													<logic:notEmpty name="updatePluginBean"
														property="startDivStatus">
								  				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img
															id="i<bean:write name="updatePluginBean" property="startDivStatus"/>"
															src="<%=localBasePath%>/images/top-level.jpg" border="0"
															onclick="show('<bean:write name="updatePluginBean" property="startDivStatus"/>')" />
													</logic:notEmpty>


													<logic:notEqual name="updatePluginForm"
														property="<%=strType%>" value="P">
														<td align="left" valign="center" class="topHLine">
															<%if(netConfigParamValuePool!=null && !netConfigParamValuePool.isEmpty()){
			                            			List<NetConfigParamValuePoolData> list = new ArrayList<NetConfigParamValuePoolData>();
													list.addAll(netConfigParamValuePool);
													Collections.sort(list);
			                            		
			                            		%> <html:select
																title="<%=descriptionVal%>" name="updatePluginForm"
																styleId="<%=strFieldValue%>"
																property="<%=strFieldValue%>" size="1"
																disabled="<%=bEditable%>">
																<%
													 Iterator itr = list.iterator();
													 while(itr.hasNext()){
													 	NetConfigParamValuePoolData netConfigParamValuePoolData = (NetConfigParamValuePoolData)itr.next();
													 	%>
																<html:option
																	value="<%=netConfigParamValuePoolData.getValue()%>"><%=netConfigParamValuePoolData.getName()%></html:option>
																<%
													 }
													%>
															</html:select> <%}else{%> <html:text title="<%=descriptionVal%>"
																name="updatePluginForm" styleId="<%=strFieldValue%>"
																property="<%=strFieldValue%>" size="<%=strMaxSizeVal%>"
																styleClass="flatfields" maxlength="<%=strMaxLengthVal%>"
																disabled="<%=bEditable%>" /> <%}%> <logic:equal
																name="updatePluginForm" property="<%=strIsNotNull%>"
																value="true">
																<font color="#FF0000"> *</font>
															</logic:equal> <logic:equal name="updatePluginForm"
																property="<%=strIsNewAdded%>" value="true">
																<a name="tip" />
															</logic:equal> <logic:equal name="updatePluginForm"
																property="<%=strIsRemoved%>" value="true">
																<a name="tip" />
															</logic:equal> 
															<%-- <img src="<%=localBasePath%>/images/help-hover.jpg"
															height="12" width="12" style="cursor: pointer"
															onclick="popUpDesc('<%=j%>')" /> --%>
															<span onclick="popUpDesc('<%=j%>')" class="elitehelp" style="background-color: rgb(35, 105, 166);">?</span>
															 <logic:equal
																name="updatePluginForm"
																property="<%=strMultipleInstances%>" value="Y">
																<logic:greaterThan name="updatePluginForm"
																	property="<%=strTotalInstance%>" value="1">
																	<img src="<%=localBasePath%>/images/minus.jpg"
																		onclick="deleteNode('<bean:write name="updatePluginForm" property="<%=strInstanceId%>"/>')" />
																</logic:greaterThan>
															</logic:equal> &nbsp;&nbsp; <logic:equal name="updatePluginForm"
																property="<%=strAlias%>" value="attribute-id">
																<a href='#'
																	onClick="return popup('<%=localBasePath%>/searchDictionaryPopUp.do?fieldName=<%=strFieldValue%>','notes')"><img
																	src="<%=localBasePath%>/images/lookup.jpg"
																	name="Image521" border=0 id="Image5"
																	onMouseOver="MM_swapImage('Image521','','<%=localBasePath%>/images/lookup-hover.jpg',1)"
																	onMouseOut="MM_swapImgRestore()" /></a>
															</logic:equal>

														</td>
													</logic:notEqual>
												</logic:equal>
												<% j++;%>


												<logic:notEmpty name="updatePluginBean"
													property="startDivStatus">
													<div
														id="<bean:write name="updatePluginBean" property="startDivStatus"/>"
														style="display: block; color: blue">
														<table width="97%" cellspacing="0" cellpadding="0"
															border="0" class="box">
															</logic:notEmpty>

															<%if(index==size){ %>
															<logic:iterate id="closeDivList" name="updatePluginBean"
																property="closeDivStatusLst">
																<logic:notEmpty name="closeDivList">
																	</tr>
														</table>
													</div>
												</logic:notEmpty>
												</logic:iterate>
												<%}%>

												<% index++; %>

												</logic:iterate>
												<!-- dhavan -->
										</table>
									</td>
								</tr>
								<tr>
									<td>
										<table width="100%" border="0" cellpadding="0" cellspacing="0"
											height="15%">
											<logic:iterate id="startUpMode" name="startUpModeList"
												type="com.elitecore.elitesm.datamanager.radius.system.standardmaster.data.IStandardMasterData">
												<tr>
													<td width="5%" class="labeltext"><img
														src="<%=localBasePath%>/images/<bean:write name="startUpMode" property="masterId" />.jpg" /></td>
													<td widht="95%" class="labeltext"><bean:write
															name="startUpMode" property="name" /></td>
												</tr>
											</logic:iterate>
										</table>
									</td>
								</tr>
								<tr>
									<td class="small-gap" colspan="2">&nbsp;</td>
								</tr>

							</table>
						</td>
					</tr>
					<%if(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.UPDATE_CONFIGURATION_ACTION)){ %>
					<tr>
						<td width="50%" colspan="2">&nbsp;</td>
					</tr>
					<tr>
						<td align="center" class="labeltext" valign="top"><input
							type="button" name="Save" width="5%" name="" value="   Update   "
							onclick="saveConfiguration()" class="light-btn" /> <input
							type="button" name="Reset" onclick="reset();"
							value="   Reset    " class="light-btn"></td>
					</tr>
					<%}%>
					<tr>
						<td class="small-gap" width="50%" colspan="2">&nbsp;</td>
					</tr>
				</table>
			</td>
		</tr>

	</table>
	</td>
	</tr>

	</table>
</html:form>
