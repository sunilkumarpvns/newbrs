<%@page import="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData"%>
<%@page import="com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@ page import="com.elitecore.netvertexsm.web.servermgr.server.form.UpdateNetServerConfigurationForm" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.NetConfigParamValuePoolData" %>
<%@ page import="java.util.StringTokenizer" %>
<%@ page import="java.util.List" %>
<%@page import="java.util.Collections"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.profilemanagement.ProfileManager"%>
<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>


<%
	String localBasePath = request.getContextPath();
	int varIPNodes = 0;
	int varDriverNodes = 0;
	if(request.getParameter("ipNodes") != null )
		varIPNodes = Integer.parseInt(request.getParameter("ipNodes"));	
	if(request.getParameter("driverNodes") != null )		
		varDriverNodes = Integer.parseInt(request.getParameter("driverNodes"));	
		
	if(varIPNodes <= 0)		
		varIPNodes = 1;
		
	if(varDriverNodes <= 0)		
		varDriverNodes = 1;
	UpdateNetServerConfigurationForm updateNetServerForm = (UpdateNetServerConfigurationForm)request.getAttribute("updateNetServerForm");
	List driverInstanceList = updateNetServerForm.getDriverInstanceList();
	List lstParameterValue = updateNetServerForm.getLstParameterValue();
	List<DatabaseDSData> datasourceList = updateNetServerForm.getDatasourceList();
	List<GatewayData> gatewayDataList = updateNetServerForm.getGatewayDataLists();
%>
 
<script type="text/javascript">
javascript:location.href="#tip";

	function addIP(){
		document.forms[0].addNode.value = 'addIP';
		alert('New IP is going to be added....Do you want to proceed');
		document.forms[0].submit();
	}
	function addDrivers(){
		document.forms[0].addNode.value = 'addDrivers';
		alert('New Driver is going to be added....Do you want to proceed');		
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
		    document.forms[0].action.value='save';
		    elableAll();
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
		alert('togglediv called'+ object);
		alert(document.getElementById(object));
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
    
    var originHostFlag = false;
	var originRealmFlag = false;
	var diameterStackModule = false;
	
    function validateParameterValues(){  
		<%for(int i=0;i<lstParameterValue.size();i++){ %>  
			var strRegexp=document.getElementById('regexp[<%=i%>]').value;
			if(document.getElementById('value[<%=i%>]') != null && document.getElementById('value[<%=i%>]').type == 'text'){
				var strValue = document.getElementById('value[<%=i%>]').value;
				document.getElementById('value[<%=i%>]').value = $.trim(strValue);
				strValue = document.getElementById('value[<%=i%>]').value;
				
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
				
				var strNameTemp = document.getElementById('name[<%=i%>]').value;
				var tempId = 'value[<%=i%>]';
				if($.trim(strNameTemp)=="Batch Size"){					
					var flag = checkBatchSize(strValue,tempId);
					return flag;
				}
				
				
				var originHostVal = document.getElementById('name[<%=i%>]').value;
				var originHostId = 'value[<%=i%>]';
				if($.trim(originHostVal)=="Origin Host"){
					originHostFlag = validateOriginHost(strValue,originHostId);
					diameterStackModule = true;
				}
				
				var originRealmVal = document.getElementById('name[<%=i%>]').value;
				var originRealmId = 'value[<%=i%>]';
				if($.trim(originRealmVal)=="Origin Realm"){					
					originRealmFlag= validateOriginRealm(strValue,originRealmId);
					diameterStackModule = true;
				}
				
				
			}
	   <% }  %>  
		if(diameterStackModule == true){
	   		return (originHostFlag && originRealmFlag);
		}else{
			return true;
		}
    }
    function popup(mylink, windowname)
			{
				if (! window.focus)return true;
					var href;
				if (typeof(mylink) == 'string')
   					href=mylink;
				else
   					href=mylink.href;
   					
   				//alert(mylink)
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
			
			
	function checkBatchSize(batchvalueArg, idArg){		
		var batchSizeValue = batchvalueArg;
		
		if(isNull(batchSizeValue)){
 			alert('Batch Size must be specified');
 			document.getElementById(idArg).focus();
 			return  false;
 			
		} else if(isEcNaN(batchSizeValue)){
			alert('Batch Size must be Numeric');
			document.getElementById(idArg).focus();
			return  false;
			
		} else if(batchSizeValue < 10 || batchSizeValue > 1000){
			alert('Batch Size must be between 10 to 1000 ');
			document.getElementById(idArg).focus();
			return  false;	
			 
		}else{
			return true;
		}
	}
	

	function disableValues(){
		var batchUpdateEnabled = $('select[title="Batch Update Enabled"]').val();
		var diameterListenerEnable = $('select[title="Diameter Listener Enable"]').val();
		var radiusListenerEnabled = $('select[title="Radius Listener Enabled"]').val();
		var duplicateReqCheckEnabled = $('select[title="Duplicate Request Check Enabled"]').val();
		var duplicateReqCheckEnabled = $('select[title="Duplicate Request Check Enabled"]').val();
		var duplicateReqCheckForDiameter=$('select[title*="check for incoming duplicate request"]').val();
		if( batchUpdateEnabled != null){
			if(batchUpdateEnabled == "false"){
				$('[title="Batch Size"]').attr('disabled', 'disabled');
				$('[title="Batch Update Interval(Sec)"]').attr('disabled', 'disabled');
				$('[title="Batch Query Timeout(Sec)"]').attr('disabled', 'disabled');
			}else{
				$('[title="Batch Size"]').removeAttr('disabled');
				$('[title="Batch Update Interval(Sec)"]').removeAttr('disabled');
				$('[title="Batch Query Timeout(Sec)"]').removeAttr('disabled');
			}
		}
		if(diameterListenerEnable != null){
			if(diameterListenerEnable == "false"){
				$('[title="Address"]').attr('disabled', 'disabled');
			}else{
				$('[title="Address"]').removeAttr('disabled');
			}
		}
		if(radiusListenerEnabled != null){
			if(radiusListenerEnabled == "false"){
				$('[title="Address"]').attr('disabled', 'disabled');
			}else{
				$('[title="Address"]').removeAttr('disabled');
			}
		}
		if(duplicateReqCheckEnabled != null){
			if(duplicateReqCheckEnabled == "false"){
				$('[title="Duplicate Request Purge Interval"]').attr('disabled', 'disabled');
			}else{
				$('[title="Duplicate Request Purge Interval"]').removeAttr('disabled');
			}
		}
		if(duplicateReqCheckForDiameter != null){
			if(duplicateReqCheckForDiameter == "false"){
				$('[title*="Specifies interval in seconds for purging the requests"]').attr('disabled', 'disabled');
			}else{
				$('[title*="Specifies interval in seconds for purging the requests"]').removeAttr('disabled');
			}
		}
		
				
	}
		
	function disablePCRFService(){
		var serviceEnabled = $(".selected");
		var data = $(".flatfields");
		var strData = '';
		for(var i=0;i<data.length;i++){
			strData = data[i].value;
			if(strData == "PCRF_SERVICE"){
				serviceEnabled[i].disabled = "true";
			}
		}
	}
	function toggleDiameterListenerValues(){
		var diameterListenerEnable = $('select[title="Diameter Listener Enable"]').val();
		alert(diameterListenerEnable);
		if(diameterListenerEnable == "false"){
			$('[title="Address"]').attr('disabled', 'disabled');
		}else{
			$('[title="Address"]').removeAttr('disabled');
		}
	}
	
	function validateOriginHost(originHostValue,originHostId){
		var flag = true;
		if(isNull(originHostValue)){
			alert("Origin Host must be configured");
			document.getElementById(originHostId).focus();
			flag = false;
		}
		return flag;
	}
	
	function validateOriginRealm(originRealmValue,originRealmId){
		var flag = true;
		if(isNull(originRealmValue)){
			alert("Origin Realm must be configured");
			document.getElementById(originRealmId).focus();
			flag = false;
		}
		return flag;
	}
	
	$(document).ready(function() {
		disableValues();
		disablePCRFService();
	});
</script>

<html:form action="/updateNetServerConfiguration">
<html:hidden name="updateNetServerForm" styleId="action" property="action" />
<html:hidden name="updateNetServerForm" styleId="nodeParameterId" property="nodeParameterId" />
<html:hidden name="updateNetServerForm" styleId="nodeInstanceId" property="nodeInstanceId" />
<html:hidden name="updateNetServerForm" styleId="confInstanceId" property="confInstanceId" />
<html:hidden name="updateNetServerForm" styleId="netServerId" property="netServerId" />
<html:hidden name="updateNetServerForm" styleId="childTotalInstanceVal" property="childTotalInstanceVal" />


<%
int j = 0;
int k = 0;
int index = 1;
String strFieldname;
String strFieldValue;
String strChildMultipleInstance;
String strInstanceId, strChildInstatnceId;
String strInstanceIdVal,strChildInstatnceIdVal;
String strChildMaxInstance		= null;
String strChildMaxInstanceVal 	= null;
String strChildTotalInstance 	= null;
boolean isVolumeThresholdExist 	= false;
boolean isTimeThresholdExist	= false;
boolean isLoggingNode			= false;     
boolean isSubstituteServerDetailNode =  false;
%>

<input type="hidden" name="addNode" />
<table width="100%" border="0" cellspacing="0" cellpadding="0" >
    <tr> 
      <td  >&nbsp;</td>
    </tr> 

    <tr> 
        <td class="table-header" align="left" >
                    <bean:message bundle="servermgrResources" key="servermgr.serverconfigurationdetail"/>
        </td>
    </tr>
    <tr>     
        <td  >

<table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" align="right">
    <tr> 
      <td valign="top" align="right" > 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="resultMessageResources" key="servermgr.update.configuration.summary"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="resultMessageResources" key="servermgr.update.configuration.name"/></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="updateNetServerForm" property="configurationName" /></td>
          </tr>
        </table>
		</td>
    </tr>
	<tr> 
      <td>
          <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%">
                  <tr>
                    <td >&nbsp;</td>
                  </tr>
            <tr>
               <td valign="top" align="right" colspan="2">
                <table cellpadding="0" cellspacing="0" border="0" width="100%" >
                  <tr>
                    <td class="small-gap" colspan="3">&nbsp;</td>
                  </tr>
                  <tr>
                    <td colspan="3">
					<%
	                  int size = updateNetServerForm.getLstParameterValue().size();
					%>
				     <table width="100%" id = "listTable" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" height="15%" >
                         <!-- added by dhavan -->
                         
						<logic:iterate id="updateServerBean" name="updateNetServerForm" property="lstParameterValue"  type="com.elitecore.netvertexsm.web.servermgr.server.form.NetConfParameterValueBean" >
							
						   <%if(index<size){ %>
						  		<logic:iterate id="closeDivList" name="updateServerBean" property="closeDivStatusLst">
				       	 			<logic:notEmpty name="closeDivList">
						       			 </tr>      
		             				   </table>
									</div>
					      		</logic:notEmpty>
					      		</logic:iterate>    
					     	<%} %>	          
					     		
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
										String strConfigId			= "configId["+j+"]";
										String strPoolExists 		= "poolExists["+j+"]";
										String strIsNotNull         = "isNotNull["+j+"]";		
										String strIsNewAdded        = "isNewAdded["+j+"]";
										String strIsRemoved         = "isRemoved["+j+"]";
																		
										String descriptionVal = updateNetServerForm.getDescription(j);
										String strNetConfigParamValuePool = "netConfigParamValuePool["+j+"]";
										Set netConfigParamValuePool = updateNetServerForm.getNetConfigParamValuePool(j);
										String strMaxLengthVal      = String.valueOf(updateNetServerForm.getMaxLength(j));
										String strMaxSizeVal      = null;
                                        String strChildTotalInstanceVal = null;
                                                                                
										if(updateNetServerForm.getMaxLength(j) > 45){
											strMaxSizeVal = "40";
										}else if(updateNetServerForm.getMaxLength(j) > 25) {
											strMaxSizeVal = "30";
										}else if(updateNetServerForm.getMaxLength(j) > 20) {
											strMaxSizeVal = "20";
										}else if(updateNetServerForm.getMaxLength(j) > 9) {
											strMaxSizeVal = "10";
										}else{
											strMaxSizeVal = String.valueOf(updateNetServerForm.getMaxLength(j)+5);        
										}
								           
			                             boolean bEditable = !updateNetServerForm.getEditable(j);
			                             
			                             strFieldname  = "displayName["+ j +"]"; 
			                             strFieldValue = "value["+ j +"]"; 

			                             k=j+1;
			                             strChildMultipleInstance= "multipleInstances["+ k +"]";
			                             strInstanceId= "instanceId["+ j +"]";
			                             strChildInstatnceId = "instanceId["+ k +"]";
			                             String strStartDivStatus=  "startDivStatus["+ k +"]";
			                             strInstanceIdVal= updateNetServerForm.getInstanceId(j);
			                              if(k < size){
			                             	strChildInstatnceIdVal = updateNetServerForm.getInstanceId(k);
			                            	strChildMaxInstance = 	"maxInstances["+k+"]";
			                            	strChildMaxInstanceVal = String.valueOf(updateNetServerForm.getMaxInstances(k));
				                            strChildTotalInstance = "totalInstance["+k+"]";
				                            strChildTotalInstanceVal = String.valueOf(updateNetServerForm.getTotalInstance(k));
			                             }
			                            %>
			                            
			                            <html:hidden name="updateNetServerForm" styleId="<%=strParameterId      %>" property="<%=strParameterId      %>"/>
										<html:hidden name="updateNetServerForm" styleId="<%=strSerialNo         %>" property="<%=strSerialNo         %>"/>
										<html:hidden name="updateNetServerForm" styleId="<%=strName             %>" property="<%=strName             %>"/>
										<html:hidden name="updateNetServerForm" styleId="<%=strDisplayName      %>" property="<%=strDisplayName      %>"/>
										<html:hidden name="updateNetServerForm" styleId="<%=strAlias            %>" property="<%=strAlias            %>"/>
										<html:hidden name="updateNetServerForm" styleId="<%=strMaxInstances     %>" property="<%=strMaxInstances     %>"/>
										<html:hidden name="updateNetServerForm" styleId="<%=strMultipleInstances%>" property="<%=strMultipleInstances%>"/>
										<html:hidden name="updateNetServerForm" styleId="<%=strParentParameterId%>" property="<%=strParentParameterId%>"/>
										<html:hidden name="updateNetServerForm" styleId="<%=strParameterValueId %>" property="<%=strParameterValueId %>"/>
										<html:hidden name="updateNetServerForm" styleId="<%=strConfigInstanceId %>" property="<%=strConfigInstanceId %>"/>
										<html:hidden name="updateNetServerForm" styleId="<%=strInstanceId       %>" property="<%=strInstanceId       %>"/>
			                            <html:hidden name="updateNetServerForm" styleId="<%=strType             %>" property="<%=strType             %>"/>
			                            <html:hidden name="updateNetServerForm" styleId="<%=strCurrentInstanceNo%>" property="<%=strCurrentInstanceNo%>"/>
			                            <html:hidden name="updateNetServerForm" styleId="<%=strTotalInstance    %>" property="<%=strTotalInstance    %>"/>
			                            <html:hidden name="updateNetServerForm" styleId="<%=strDescription      %>" property="<%=strDescription      %>"/>
			                            <html:hidden name="updateNetServerForm" styleId="<%=strMaxLength        %>" property="<%=strMaxLength        %>"/>
			                            <html:hidden name="updateNetServerForm" styleId="<%=strEditable         %>" property="<%=strEditable         %>"/>
                                        <html:hidden name="updateNetServerForm" styleId="<%=strStartUpMode         %>" property="<%=strStartUpMode         %>"/>                                        
                                        <html:hidden name="updateNetServerForm" styleId="<%=strStatus  %>" property="<%=strStatus  %>"/>
										<html:hidden name="updateNetServerForm" styleId="<%=strRegexp  %>" property="<%=strRegexp  %>"/> 
										<html:hidden name="updateNetServerForm" styleId="<%=strConfigId  %>" property="<%=strConfigId  %>"/> 
										<html:hidden name="updateNetServerForm" styleId="<%=strIsNotNull  %>" property="<%=strIsNotNull  %>" />
										<html:hidden name="updateNetServerForm" styleId="<%=strPoolExists  %>" property="<%=strPoolExists  %>" />
										<logic:notEqual name="updateNetServerForm" property="<%=strStatus%>" value="Y">  
                        		            <html:hidden name="updateNetServerForm" styleId="<%=strFieldValue  %>" property="<%=strFieldValue  %>"/>                                                                                                         
				                        </logic:notEqual>
				                        
				                        
				                        
						   				<logic:equal name="updateNetServerForm" property="<%=strStatus%>" value="Y">     
						   					
			                         	  <logic:notEqual name="updateNetServerForm" property="<%=strType%>" value="P" >
											<td align="center"  valign="center" class="topHLine" width="2%">
												<img src="<%=localBasePath%>/images/<bean:write name="updateNetServerForm" property="<%=strStartUpMode%>" />.jpg">							
											</td>
			                              <td align="left"  valign="center" class="topHLine" width="40%" colspan="2"> 
			                          	 </logic:notEqual>
				                           
			                           <logic:equal name="updateNetServerForm" property="<%=strType%>" value="P" >
											<td align="center"  valign="center" class="tblheader-bold" width="2%"> 
												<img src="<%=localBasePath%>/images/<bean:write name="updateNetServerForm" property="<%=strStartUpMode%>" />.jpg"/>							
											</td>
			                          	 	<td align="left" class="tblheader-bold" valign="center" colspan="4">
     		                            </logic:equal>
     		                            
     		                    
     		                            
			                            <%
				                           StringTokenizer strToken = new StringTokenizer(strInstanceIdVal,".");
				                           for(int i=1;i<strToken.countTokens();i++){
					                          //out.print("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				                          }
				                        %>
				                          
				                        <logic:equal name="updateNetServerForm" property="<%=strIsNewAdded%>" value="true">
				                      				<span class="blue-text-bold"><bean:write name="updateNetServerForm" property="<%=strFieldname%>"/></span>																	
										</logic:equal>    
			                            <logic:notEqual name="updateNetServerForm" property="<%=strIsNewAdded%>" value="true">
			                            			<!-- Start :  code to display image infront of respective field -->
			                            			
													<logic:equal name="updateNetServerForm" property="<%=strFieldname%>" value="Volume Threshold">
														<%isVolumeThresholdExist = true;%>			                            					
			                            			</logic:equal>	
			                            					                            					                            						                            		
													<logic:equal name="updateNetServerForm" property="<%=strFieldname%>" value="Time Threshold">
														<%isTimeThresholdExist=true;%>			                            				                            					
			                            			</logic:equal>	

													<logic:equal name="updateNetServerForm" property="<%=strFieldname%>" value="Logging">
														<%isLoggingNode=true;%>			                            				                            					
			                            			</logic:equal>

													<logic:equal name="updateNetServerForm" property="<%=strFieldname%>" value="Substitute Server Detail">
														<%isSubstituteServerDetailNode=true;%>			                            				                            					
			                            			</logic:equal>

			                            						                            						                            					       
													<%if(isVolumeThresholdExist || isTimeThresholdExist){%>                            			
				                            			<logic:equal name="updateNetServerForm" property="<%=strFieldname%>" value="Session">
				                            					<img src="<%=localBasePath%>/images/SMS0162.jpg">&nbsp;
				                            			</logic:equal>			                            			
				                            			<logic:equal name="updateNetServerForm" property="<%=strFieldname%>" value="Service">
				                            					<img src="<%=localBasePath%>/images/SMS0162.jpg">&nbsp;
				                            			</logic:equal>
			                            			<%}else if(isLoggingNode){%>
			                            				<logic:notEqual name="updateNetServerForm" property="<%=strFieldname%>" value="Logging">
			                            					<img src="<%=localBasePath%>/images/SMS0162.jpg">&nbsp;
			                            				</logic:notEqual>
			                            				<logic:equal name="updateNetServerForm" property="<%=strFieldname%>" value="Compress Rolled Unit">
			                            					<%isLoggingNode=false;%>
			                            				</logic:equal>			                            						
			                            			<%}else if(isSubstituteServerDetailNode){%>
			                            				<logic:notEqual name="updateNetServerForm" property="<%=strFieldname%>" value="Substitute Server Detail">
			                            					<img src="<%=localBasePath%>/images/SMS0162.jpg">&nbsp;
			                            				</logic:notEqual>
			                            				<logic:equal name="updateNetServerForm" property="<%=strFieldname%>" value="Weight">
			                            					<%isSubstituteServerDetailNode=false;%>
			                            				</logic:equal>			                            									                            			
			                            			<%}%>			                            					                            						                            		
			                            			<!-- End :  code to display image infront of respective field -->
													<bean:write name="updateNetServerForm" property="<%=strFieldname%>"  />
										</logic:notEqual>    
				                           
			                            <%if(k< size ){%>
			                           	<logic:greaterThan  name="updateNetServerForm" property="<%=strChildMaxInstance%>" value="1" >
									    <logic:greaterThan  name="updateNetServerForm" property="<%=strChildMaxInstance%>" value="<%=strChildTotalInstanceVal%>" >                                                                                
			                           	     <logic:match name="updateNetServerForm" property="<%=strChildInstatnceId%>" value="<%=strInstanceIdVal%>" location="start">
										         <logic:equal name="updateNetServerForm" property="<%=strEditable%>" value="true" >                                           
			                               		    <input  type="button" value="Add" onclick="addChildNode('<bean:write name="updateNetServerForm" property="<%=strParameterId%>" />','<bean:write name="updateNetServerForm" property="<%=strInstanceId%>" />','<%=strChildTotalInstanceVal%>')" class="light-btn" />
                                                </logic:equal>
			                              </logic:match>
			                              </logic:greaterThan> 
                                          </logic:greaterThan>  
                                          
                                        <%}%>
			                            
			                            <logic:equal name="updateNetServerForm" property="<%=strType%>" value="P" >
			                            <logic:equal name="updateNetServerForm" property="<%=strMultipleInstances%>" value="Y" >
			                            <logic:greaterThan  name="updateNetServerForm" property="<%=strTotalInstance%>" value="1" >
                                            <logic:equal name="updateNetServerForm" property="<%=strEditable%>" value="true" >                                           
											     <img src="<%=localBasePath%>/images/minus.jpg" onclick="deleteNode('<bean:write name="updateNetServerForm" property="<%=strInstanceId%>"/>')" />			                           
                                            </logic:equal>
										</logic:greaterThan>			
					                   </logic:equal>
					                   </logic:equal>
			                            
			                            
			                            <logic:notEmpty name="updateServerBean" property="startDivStatus">
			                            		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img id="i<bean:write name="updateServerBean" property="startDivStatus"/>" src="<%=localBasePath%>/images/top-level.jpg" align="top" border="0" onclick="show('<bean:write name="updateServerBean" property="startDivStatus"/>')"/>
			                         	</logic:notEmpty>
			                   
 			                             
			                           <logic:notEqual name="updateNetServerForm" property="<%=strType%>" value="P" >
			                            <td align="left"  valign="center" class="topHLine">
			                            
			                            		<%if(netConfigParamValuePool!=null && !netConfigParamValuePool.isEmpty()){
														
													List<NetConfigParamValuePoolData> list = new ArrayList<NetConfigParamValuePoolData>();
													list.addAll(netConfigParamValuePool);
													Collections.sort(list);
													 
			                            		%>
			                            			<% if(descriptionVal.equalsIgnoreCase("Batch Update Enabled") || descriptionVal.equalsIgnoreCase("Diameter Listener Enable") || descriptionVal.equalsIgnoreCase("Radius Listener Enabled") || descriptionVal.equalsIgnoreCase("Duplicate Request Check Enabled")||descriptionVal.contains("check for incoming duplicate request")){
			                            			%>
				                            		   
				                            		    <html:select onchange="disableValues();" title="<%=descriptionVal%>" name="updateNetServerForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" size="1" disabled="<%=bEditable%>" styleClass="selected" >
														<%
														
														 Iterator itr = list.iterator();
														 while(itr.hasNext()){
														 	NetConfigParamValuePoolData netConfigParamValuePoolData = (NetConfigParamValuePoolData)itr.next();
														 	%>
																<html:option value="<%=netConfigParamValuePoolData.getValue()%>" ><%=netConfigParamValuePoolData.getName()%></html:option> 
														 	<%
														 }
														%>
				                            		    </html:select> 
			                            		    <%}else{%>	
					                            		 
					                            		    <html:select title="<%=descriptionVal%>" name="updateNetServerForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" size="1" disabled="<%=bEditable%>" styleClass="selected" >
															<%
															
															 Iterator itr = list.iterator();
															 while(itr.hasNext()){
															 	NetConfigParamValuePoolData netConfigParamValuePoolData = (NetConfigParamValuePoolData)itr.next();
															 	%>
															 	 	<html:option value="<%=netConfigParamValuePoolData.getValue()%>" ><%=netConfigParamValuePoolData.getName()%></html:option> 
															 	<%
															 }
															%>
					                            		    </html:select>
													<% }%>						                            		    
			                            		    
			                             		<%}else{%>
			                             		  		<logic:equal name="updateNetServerForm" property="<%=strAlias%>" value="driver-id">		
															<html:select  name="updateNetServerForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" disabled="<%=bEditable%>" style="width:150px"  >
							                   					<html:option value="">-None-</html:option>
							                   					<%if(driverInstanceList!=null && !driverInstanceList.isEmpty()){ %>							       
						                   						<html:options collection="driverInstanceList" labelProperty="name" property="driverInstanceId"/> 
							                   					<%}%>
							                   				</html:select>							                   				
					                                   	</logic:equal>
					                                   	<logic:notEqual name="updateNetServerForm" property="<%=strAlias%>" value="driver-id">
					                                   			<logic:equal name="updateNetServerForm" property="<%=strAlias%>" value="data-source-id">
									                   				<html:select  name="updateNetServerForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" disabled="<%=bEditable%>" style="width:200px" >
									                   					<html:option value="">SELECT</html:option>
									                   					<%if(datasourceList!=null && !datasourceList.isEmpty()){ %>
									                   						<html:options collection="datasourceList" labelProperty="name" property="databaseId"/> 
									                   					<% } %>
									                   				</html:select>
										                   		</logic:equal>
									                   			<logic:notEqual name="updateNetServerForm" property="<%=strAlias%>" value="data-source-id">					                                   				
						                                   			<logic:equal name="updateNetServerForm" property="<%=strAlias%>" value="gateway">
										                   				<html:select  name="updateNetServerForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" disabled="<%=bEditable%>" style="width:200px" >
										                   					<html:option value="ALL">-ALL-</html:option>
										                   					<%if(gatewayDataList!=null && !gatewayDataList.isEmpty()){ %>
										                   						<html:options collection="gatewayDataList" labelProperty="gatewayName" property="gatewayName"/> 
										                   					<% } %>
										                   				</html:select>
											                   		</logic:equal>
											                   	<logic:notEqual name="updateNetServerForm" property="<%=strAlias%>" value="gateway">
											                   		<logic:equal name="updateNetServerForm" property="<%=strAlias%>" value="primary">
										                   				<html:select  name="updateNetServerForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" disabled="<%=bEditable%>" style="width:200px" >
										                   					<html:option value="">-NONE-</html:option>
										                   					<%if(datasourceList!=null && !datasourceList.isEmpty()){ %>
										                   						<html:options collection="datasourceList" labelProperty="name" property="databaseId"/> 
										                   					<% } %>
										                   				</html:select>
										                   			</logic:equal>
											                   	<logic:notEqual name="updateNetServerForm" property="<%=strAlias%>" value="primary">
											                   		<logic:equal name="updateNetServerForm" property="<%=strAlias%>" value="secondary">
										                   				<html:select  name="updateNetServerForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" disabled="<%=bEditable%>" style="width:200px" >
										                   					<html:option value="">-NONE-</html:option>
										                   					<%if(datasourceList!=null && !datasourceList.isEmpty()){ %>
										                   						<html:options collection="datasourceList" labelProperty="name" property="databaseId"/> 
										                   					<% } %>
										                   				</html:select>
										                   			</logic:equal>
											                   		<logic:notEqual name="updateNetServerForm" property="<%=strAlias%>" value="secondary">
												                   		
							                                   			<logic:notEqual name="updateNetServerForm" property="<%=strFieldname%>" value="Password">
							                                   				<html:text title="<%=descriptionVal%>"  name="updateNetServerForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" size="<%=strMaxSizeVal%>" styleClass="flatfields" maxlength="<%=strMaxLengthVal%>"  disabled="<%=bEditable%>" />
						                                   				</logic:notEqual>
												                   		<logic:equal name="updateNetServerForm" property="<%=strFieldname%>" value="Password">
							                                   				<html:password title="<%=descriptionVal%>"  name="updateNetServerForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" size="<%=strMaxSizeVal%>" styleClass="flatfields" maxlength="<%=strMaxLengthVal%>"  disabled="<%=bEditable%>" />
							                                   			</logic:equal>						                                   				
							                                   			
					                                   			</logic:notEqual>
					                                   		</logic:notEqual>
					                                   		</logic:notEqual>
					                                   	</logic:notEqual>					                                   							                                   								                                   							                                   			
							                   		</logic:notEqual>	                                
				                                 <%}%>
				                                     

				                                     
				                                    <logic:equal name="updateNetServerForm" property="<%=strIsNotNull%>" value="true">
															<font color="#FF0000">*</font>																			
													</logic:equal>
													
											        <logic:equal name="updateNetServerForm" property="<%=strIsNewAdded%>" value="true">
															<a name="tip"/>																			
													</logic:equal>
													
				                         			<logic:equal name="updateNetServerForm" property="<%=strIsRemoved%>" value="true">
															<a name="tip"/>																			
													</logic:equal>
				                           
				                                 <img src="<%=localBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="popUpDesc('<%=j%>')"  />
			                                     <logic:equal name="updateNetServerForm" property="<%=strMultipleInstances%>" value="Y" >
						                               <logic:greaterThan  name="updateNetServerForm" property="<%=strTotalInstance%>" value="1" >
															<img src="<%=localBasePath%>/images/minus.jpg" onclick="deleteNode('<bean:write name="updateNetServerForm" property="<%=strInstanceId%>"/>')" />
												       </logic:greaterThan>			                           
							                   </logic:equal>
							                   &nbsp;&nbsp;
							                   <logic:equal name="updateNetServerForm" property="<%=strAlias%>" value="attribute-id">
						                   					<a href='#' onClick="return popup('<%=localBasePath%>/searchDictionaryPopUp.do?fieldName=<%=strFieldValue%>','notes')"><img src="<%=localBasePath%>/images/lookup.jpg" name="Image521" border=0 id="Image5" onMouseOver="MM_swapImage('Image521','','<%=localBasePath%>/images/lookup-hover.jpg',1)" onMouseOut="MM_swapImgRestore()"/></a>
							                   </logic:equal>
							                   
							                   
							                  
										</td>
					                   </logic:notEqual>

			               </logic:equal>      
					           <% j++;%>
					       	   	<logic:notEmpty name="updateServerBean" property="startDivStatus">
						 			<div id="<bean:write name="updateServerBean" property="startDivStatus"/>" style="display:block;color: blue" >
						 				<table width="100%" cellspacing="0" cellpadding="0" border="0">
							 </logic:notEmpty>
							 
							 <%if(index==size){ %>
						  		<logic:iterate id="closeDivList" name="updateServerBean" property="closeDivStatusLst">
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
              <td class="small-gap" colspan="2" >&nbsp;</td>
            </tr>
                 <tr>
                    <td>
                        <table width="100%" border="0" cellpadding="0" cellspacing="0" height="15%" >                                        
                            <logic:iterate id="startUpMode" name="updateNetServerForm" property="startUpModeList" type="com.elitecore.netvertexsm.datamanager.radius.system.standardmaster.data.IStandardMasterData">
                                    <tr>
                                        <td width="5%" class="labeltext"><img src="<%=localBasePath%>/images/<bean:write name="startUpMode" property="masterId" />.jpg"/></td>
                                        <td widht="95%" class="labeltext"><bean:write name="startUpMode" property="name" /> </td>                                        
                                    </tr>
                            </logic:iterate>
                        </table>                    
                    </td>
                </tr>  
                <tr>
                  <td class="small-gap" colspan="2" >&nbsp;</td>
                </tr>
                
                </table>
              </td>
            </tr>
            <%if(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.UPDATE_CONFIGURATION_ACTION)){ %>
            <tr>
              <td width="50%" colspan="2" >&nbsp;</td>
            </tr>
				<tr>
					<td align="center" class="labeltext" valign="top" >
					    <input type="button" name="Save" width="5%" name=""  value="   Update   " onclick="return saveConfiguration();" class="light-btn" />       
						<input type="button" name="Reset" onclick="reset();" value="   Reset    " class="light-btn" >  
					</td>
				</tr>
              <%} %>       
            <tr>
              <td class="small-gap" width="50%" colspan="2" >&nbsp;</td>
            </tr>
          </table>
      </td>
    </tr>
	
</table>
        </td>
    </tr>

</table>
</html:form>
