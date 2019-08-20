<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@ page import="com.elitecore.netvertexsm.web.servermgr.service.form.UpdateServiceConfigurationForm"%>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.NetConfigParamValuePoolData" %>
<%@ page import="java.util.StringTokenizer" %>
<%@page import="java.util.Collections"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.profilemanagement.ProfileManager"%>
<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
 <%
	String localBasePath1 = request.getContextPath();
 	int counter = 0;
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

	UpdateServiceConfigurationForm updateServiceForm = (UpdateServiceConfigurationForm)request.getAttribute("updateServiceForm");
	List lstParameterValue = updateServiceForm.getLstParameterValue();
	List driverInstanceList = updateServiceForm.getDriverInstanceList();
	List sprList = (List)request.getAttribute("sprList");
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
        	elableAll();
		    document.forms[0].action.value='save';
		    document.forms[0].submit();
		}    
	}
	
	
	function checkBatchSize(batchvalueArg, idArg){		
		var batchSizeValue = batchvalueArg;
		
		if(isNull(batchSizeValue)){
 			alert('Batch Size must be specified.');
 			document.getElementById(idArg).focus();
 			return  false;
 			
		} else if(isEcNaN(batchSizeValue)){
			alert('Batch Size must be Numeric');
			document.getElementById(idArg).focus();
			return  false;
			
		} else if(batchSizeValue < 10 || batchSizeValue > 1000){
			alert('Batch Size must be between 10 to 1000');
			document.getElementById(idArg).focus();
			return  false;	
			 
		}else{
			return true;
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
       <%}%>
    }
     function popUpDesc(valIndex) { 
    	var strValue=document.getElementById('description['+valIndex+']');		
    	if(strValue != null){
        	var varWindow = window.open('<%=localBasePath1%>/jsp/servermgr/server/NetDescriptionPopup.jsp?description='+strValue.value,'DescriptionWin','top=100, left=200, height=200, width=500, scrollbars=yes, status=1');
        	varWindow.focus();
        }else{
        	alert('Description does not exist');
        }
    }
    
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
       		document.getElementById(image).src = '<%=localBasePath1%>/images/bottom-level.jpg';
    	} else {
       		srcElement.style.display = 'block';
       		document.getElementById(image).src = '<%=localBasePath1%>/images/top-level.jpg';
    	}
  	  }
    }			
	
 $(document).ready(function(){
	 $('select[title="Enabled"]').each(function(){
			disableAll(this);
		}); 
	 $('select[title="email-Enabled"]').each(function(){
			disableAll(this);
		}); 
		 $('select[title="sms-Enabled"]').each(function(){
			disableAll(this);
		}); 
	});
 
 	function disableAll(index){
		var enabledValue = $(index).val();
		var enabledTitle =  $(index).attr('title');
		var obj = $(index).closest('table');
		var paramValue = obj.find("tr input[type='hidden']").attr("value");
		obj = obj.find('tbody').children('tr');
		if(enabledTitle == "sms-Enabled" || enabledTitle == "Sms Protocol"){
			disableSMSConfig(obj,enabledValue,paramValue);
			
		}
		if(enabledTitle == "email-Enabled"){
			disableEmailConfig(obj ,enabledValue,paramValue);
			
		}
	}
	function disableEmailConfig(obj,enabledValue,paramValue){
		 if(enabledValue == 'false'){
			obj.next().each(function(){
				$(this).children('td').eq(1).children().eq(0).attr('disabled', 'disabled');
			});
		}else{
			obj.next().each(function(){
				$(this).children('td').eq(1).children().eq(0).attr('disabled', false);
			});
		}
	}
	function disableSMSConfig(obj,enabledValue,paramValue) {
		if(enabledValue == 'false'){
			$('[title="Sms Protocol"]').attr('disabled', 'disabled');
			obj.eq(2).children('td').eq(0).children("div").children("table").children("tbody").children("tr").each(function(){
				$(this).children('td').eq(1).children().eq(0).attr('disabled', 'disabled');
			});
			obj.eq(3).children('td').eq(0).children("div").children("table").children("tbody").children("tr").each(function(){
				$(this).children('td').eq(1).children().eq(0).attr('disabled', 'disabled');
			});
			
		}else{
			$('[title="Sms Protocol"]').attr('disabled', false);
			var smsProtocolValue = obj.eq(1).children('td').eq(1).children().eq(0).val();
			
			if(smsProtocolValue == 'HTTP'){
				obj.eq(2).children('td').eq(0).children("div").children("table").children("tbody").children("tr").each(function(){
					$(this).children('td').eq(1).children().eq(0).attr('disabled','disabled');
				});
				obj.eq(3).children('td').eq(0).children("div").children("table").children("tbody").children("tr").each(function(){
					$(this).children('td').eq(1).children().eq(0).attr('disabled', false);
				});
			}else{
				obj.eq(2).children('td').eq(0).children("div").children("table").children("tbody").children("tr").each(function(){
					$(this).children('td').eq(1).children().eq(0).attr('disabled', false);
				});
				obj.eq(3).children('td').eq(0).children("div").children("table").children("tbody").children("tr").each(function(){
					$(this).children('td').eq(1).children().eq(0).attr('disabled', 'disabled');
				});
			}
			
		} 
	}
	
    
</script>

<html:form action="/updateServiceConfiguration">
<html:hidden name="updateServiceForm" styleId="action" property="action" />
<html:hidden name="updateServiceForm" styleId="nodeParameterId" property="nodeParameterId" />
<html:hidden name="updateServiceForm" styleId="nodeInstanceId" property="nodeInstanceId" />
<html:hidden name="updateServiceForm" styleId="confInstanceId" property="confInstanceId" />
<html:hidden name="updateServiceForm" styleId="netServiceId" property="netServiceId" />
<html:hidden name="updateServiceForm" styleId="childTotalInstanceVal" property="childTotalInstanceVal" />


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
<table width="100%" border="0" cellspacing="0" cellpadding="0" align="right">

    <tr>     
        <td>

<table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" align="right">
    <tr> 
      <td valign="top" align="right" > 
        <table width="100%" border="0" cellspacing="0" cellpadding="0"  >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="servermgrResources" key="servermgr.serviceconfigurationdetail"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="resultMessageResources" key="servermgr.update.configuration.name"/></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="updateServiceForm" property="configurationName" /></td>
          </tr>

        </table>
		</td>
    </tr>

	<tr> 
      <td >
          <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%">
                  <tr>
                    <td >&nbsp;</td>
                  </tr>
            <tr>
              <td valign="center" align="right" colspan="2">
                <table cellpadding="0" cellspacing="0" border="0" width="100%" >
                  <tr>
                    <td class="small-gap" colspan="3">&nbsp;</td>
                  </tr>
                  <tr>
                    <td colspan="3">
<%
			                             int size = updateServiceForm.getLstParameterValue().size();
%>
                      <table width="100%" border="0" cellpadding="0" cellspacing="0" class="box" >
                        <!-- added by dhavan -->
							<logic:iterate id="updateServiceBean" name="updateServiceForm" property="lstParameterValue" type="com.elitecore.netvertexsm.web.servermgr.server.form.NetConfParameterValueBean" >
							
							   <%if(index<size){ %>
						  		<logic:iterate id="closeDivList" name="updateServiceBean" property="closeDivStatusLst">
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
										String strConfigId 			= "configId["+j+"]";
										String strPoolExists 		= "poolExists["+j+"]";										
										String strIsNotNull         = "isNotNull["+j+"]";
										String strIsNewAdded        =  "isNewAdded["+j+"]";
										String strIsRemoved         = "isRemoved["+j+"]";
									
																	
										String descriptionVal = updateServiceForm.getDescription(j);
										Set netConfigParamValuePool = updateServiceForm.getNetConfigParamValuePool(j);
										String strMaxLengthVal      = String.valueOf(updateServiceForm.getMaxLength(j));
										String strMaxSizeVal      = null;
                                        String strChildTotalInstanceVal = null;
                                        
                                        if(updateServiceForm.getMaxLength(j) > 45){
											strMaxSizeVal = "40";
										}else if(updateServiceForm.getMaxLength(j) > 25) {
											strMaxSizeVal = "30";
										}else if(updateServiceForm.getMaxLength(j) > 20) {
											strMaxSizeVal = "20";
										}else if(updateServiceForm.getMaxLength(j) > 9) {
											strMaxSizeVal = "10";
										}else{
											strMaxSizeVal = String.valueOf(updateServiceForm.getMaxLength(j)+5);        
										}
				                           
			                            boolean bEditable = !updateServiceForm.getEditable(j);
			                           
			                             strFieldname  = "displayName["+ j +"]"; 
			                             strFieldValue = "value["+ j +"]"; 

				                         k=j+1;
				                         
			                             strChildMultipleInstance= "multipleInstances["+ k +"]";
			                             strInstanceId= "instanceId["+ j +"]";
			                             strChildInstatnceId = "instanceId["+ k +"]";
			                             String strChildParameterId = "parameterId["+ k +"]";
			                             String strStartDivStatus=  "startDivStatus["+ k +"]";
			                             strInstanceIdVal= updateServiceForm.getInstanceId(j);
			                             if(k < size){
			                             	strChildInstatnceIdVal = updateServiceForm.getInstanceId(k);
			                            	strChildMaxInstance = 	"maxInstances["+k+"]";
			                            	strChildMaxInstanceVal = String.valueOf(updateServiceForm.getMaxInstances(k));
				                            strChildTotalInstance = "totalInstance["+k+"]";
				                            strChildTotalInstanceVal = String.valueOf(updateServiceForm.getTotalInstance(k));
			                             }
			                            %>
			                            <html:hidden name="updateServiceForm" styleId="<%=strParameterId      %>" property="<%=strParameterId      %>"/>
										<html:hidden name="updateServiceForm" styleId="<%=strSerialNo         %>" property="<%=strSerialNo         %>"/>
										<html:hidden name="updateServiceForm" styleId="<%=strName             %>" property="<%=strName             %>"/>
										<html:hidden name="updateServiceForm" styleId="<%=strDisplayName      %>" property="<%=strDisplayName      %>"/>
										<html:hidden name="updateServiceForm" styleId="<%=strAlias            %>" property="<%=strAlias            %>"/>
										<html:hidden name="updateServiceForm" styleId="<%=strMaxInstances     %>" property="<%=strMaxInstances     %>"/>
										<html:hidden name="updateServiceForm" styleId="<%=strMultipleInstances%>" property="<%=strMultipleInstances%>"/>
										<html:hidden name="updateServiceForm" styleId="<%=strParentParameterId%>" property="<%=strParentParameterId%>"/>
										<html:hidden name="updateServiceForm" styleId="<%=strParameterValueId %>" property="<%=strParameterValueId %>"/>
										<html:hidden name="updateServiceForm" styleId="<%=strConfigInstanceId %>" property="<%=strConfigInstanceId %>"/>
										<html:hidden name="updateServiceForm" styleId="<%=strInstanceId       %>" property="<%=strInstanceId       %>"/>
			                            <html:hidden name="updateServiceForm" styleId="<%=strType             %>" property="<%=strType             %>"/>
			                            <html:hidden name="updateServiceForm" styleId="<%=strCurrentInstanceNo%>" property="<%=strCurrentInstanceNo%>"/>
			                            <html:hidden name="updateServiceForm" styleId="<%=strTotalInstance    %>" property="<%=strTotalInstance    %>"/>
			                            <html:hidden name="updateServiceForm" styleId="<%=strDescription      %>" property="<%=strDescription      %>"/>
			                            <html:hidden name="updateServiceForm" styleId="<%=strMaxLength        %>" property="<%=strMaxLength        %>"/>
			                            <html:hidden name="updateServiceForm" styleId="<%=strEditable         %>" property="<%=strEditable         %>"/>
                                        <html:hidden name="updateServiceForm" styleId="<%=strStartUpMode      %>" property="<%=strStartUpMode      %>"/> 
                                        <html:hidden name="updateServiceForm" styleId="<%=strStatus  %>" property="<%=strStatus  %>"/>   
                                        <html:hidden name="updateServiceForm" styleId="<%=strRegexp  %>" property="<%=strRegexp  %>"/>
                                        <html:hidden name="updateServiceForm" styleId="<%=strConfigId  %>" property="<%=strConfigId  %>"/>                                                                              
                                        <html:hidden name="updateServiceForm" styleId="<%=strPoolExists  %>" property="<%=strPoolExists  %>"/>                                                                              
										<html:hidden name="updateServiceForm" styleId="<%=strIsNotNull %>" property="<%=strIsNotNull %>"/> 
                         			 <logic:notEqual name="updateServiceForm" property="<%=strStatus%>" value="Y">  
                                        <html:hidden name="updateServiceForm" styleId="<%=strFieldValue  %>" property="<%=strFieldValue  %>"/>                                                                                                         
                         			 </logic:notEqual>
                
					
                         			 <logic:equal name="updateServiceForm" property="<%=strStatus%>" value="Y">  
									   <logic:notEqual name="updateServiceForm" property="<%=strType%>" value="P" >
			                               <td align="left"  valign="center" class="topHLine" width="30%" colspan="2"> 
			                           </logic:notEqual>
				                           

			                           <logic:equal name="updateServiceForm" property="<%=strType%>" value="P" >
			                          	 <td align="left" class="tblheader-bold" valign="center" colspan="4">
			                          	 				<%-- <img src="<%=localBasePath%>/images/drop1.gif" onclick="togglediv('<%="e"+j%>')" /> --%>
			                            </logic:equal>
			                            <%
				                           //System.out.println("Length : "+strInstanceIdVal.trim().length());
                                           StringTokenizer strToken = new StringTokenizer(strInstanceIdVal,".");
				                           for(int i=1;i<strToken.countTokens();i++){
				                           %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%
				                         }
				                           %>
				                           
				                        <logic:equal name="updateServiceForm" property="<%=strIsNewAdded%>" value="true">
														<span class="blue-text-bold"> 	 <bean:write name="updateServiceForm" property="<%=strFieldname%>"  /> </span>																	
										</logic:equal>    
			                          
			                            <logic:notEqual name="updateServiceForm" property="<%=strIsNewAdded%>" value="true">
															 <bean:write name="updateServiceForm" property="<%=strFieldname%>"  /> 																		
											 <logic:equal name="updateServiceForm" property="<%=strFieldname%>" value="Mail Host">
											 	<br><font color=red>&nbsp; [ IP/SMTP Server:Port ] &nbsp;</font>
											 </logic:equal>																						   																	
										</logic:notEqual>    
			                          
			                           
			                           
                                       <img src="<%=localBasePath1%>/images/<bean:write name="updateServiceForm" property="<%=strStartUpMode%>" />.jpg"/>                                                                              
			                           <%if(k< size ){%>
			                      	<logic:greaterThan  name="updateServiceForm" property="<%=strChildMaxInstance%>" value="1" >
									      <logic:greaterThan  name="updateServiceForm" property="<%=strChildMaxInstance%>" value="<%=strChildTotalInstanceVal%>" >                                        
                                     	     <logic:match name="updateServiceForm" property="<%=strChildInstatnceId%>" value="<%=strInstanceIdVal%>" location="start">
										        <logic:equal name="updateServiceForm" property="<%=strEditable%>" value="true" >                                           
			                               		    <input  type="button" value="Add" onclick="addChildNode('<bean:write name="updateServiceForm" property="<%=strParameterId%>" />','<bean:write name="updateServiceForm" property="<%=strInstanceId%>" />',<%=strChildTotalInstanceVal%>)" class="light-btn" />
                                                </logic:equal>
			                               </logic:match>
			                              </logic:greaterThan> 
                                          </logic:greaterThan>                                           
			                            <%}%>
			                            <logic:equal name="updateServiceForm" property="<%=strType%>" value="P" >
			                            <logic:equal name="updateServiceForm" property="<%=strMultipleInstances%>" value="Y" >
			                            <logic:greaterThan  name="updateServiceForm" property="<%=strTotalInstance%>" value="1" >
                                            <logic:equal name="updateServiceForm" property="<%=strEditable%>" value="true" >                                                                                   
													<img src="<%=localBasePath1%>/images/minus.jpg" onclick="deleteNode('<bean:write name="updateServiceForm" property="<%=strInstanceId%>"/>')" />
                                            </logic:equal>
										</logic:greaterThan>						                           
					                   </logic:equal>
					                   </logic:equal>
			                           
			                             <logic:notEmpty name="updateServiceBean" property="startDivStatus">
								  		         		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img id="i<bean:write name="updateServiceBean" property="startDivStatus"/>" src="<%=localBasePath1%>/images/top-level.jpg" border="0" onclick="show('<bean:write name="updateServiceBean" property="startDivStatus"/>')"/>
         							 	</logic:notEmpty>
			                   
			                           
		                              <logic:notEqual name="updateServiceForm" property="<%=strType%>" value="P" >
			                            <td align="left"  valign="center" class="topHLine" >	
			                            		<%if(netConfigParamValuePool!=null && !netConfigParamValuePool.isEmpty()){
			                            		
			                            			List<NetConfigParamValuePoolData> list = new ArrayList<NetConfigParamValuePoolData>();
													list.addAll(netConfigParamValuePool);
													Collections.sort(list);
			                            		%>													
			                            		   <html:select title="<%=descriptionVal%>" name="updateServiceForm" styleId = "<%=strFieldValue%>" property="<%=strFieldValue%>" size="1" onchange="disableAll(this);">
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
			                            					<logic:equal name="updateServiceForm" property="<%=strAlias%>" value="spr">		
															<html:select  name="updateServiceForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" disabled="<%=bEditable%>">
							                   					<html:option value="">-None-</html:option>
							                   					<%if(sprList!=null && !sprList.isEmpty()){ %>							       
						                   						<html:options collection="sprList" labelProperty="sprName" property="sprId"/> 
							                   					<%}%>
							                   				</html:select>
							                   				
					                                   	</logic:equal>	
					                                   	<logic:notEqual name="updateServiceForm" property="<%=strAlias%>" value="spr">	
			                            				<logic:equal name="updateServiceForm" property="<%=strAlias%>" value="driver-id">		
															<html:select  name="updateServiceForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" disabled="<%=bEditable%>" style="width:150px">
							                   					<html:option value="">-None-</html:option>
							                   					<%if(driverInstanceList!=null && !driverInstanceList.isEmpty()){ %>							       
						                   						<html:options collection="driverInstanceList" labelProperty="name" property="driverInstanceId"/> 
							                   					<%}%>
							                   				</html:select>
							                   				
					                                   	</logic:equal>
					                                   	 <logic:notEqual name="updateServiceForm" property="<%=strAlias%>" value="driver-id">
							                   				<logic:notEqual name="updateServiceForm" property="<%=strAlias%>" value="datasource">
																  <logic:equal name="updateServiceForm" property="<%=strFieldname%>" value="Batch Size">
																 	<html:text title="<%=descriptionVal%>"  name="updateServiceForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" size="<%=strMaxSizeVal%>" styleClass="flatfields" maxlength="<%=strMaxLengthVal%>"  disabled="<%=bEditable%>" />
																 </logic:equal>
																 <logic:notEqual name="updateServiceForm" property="<%=strFieldname%>" value="Batch Size">
																	 <logic:equal name="updateServiceForm" property="<%=strFieldname%>" value="Password">
																		 <html:password title="<%=descriptionVal%>"  name="updateServiceForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" size="<%=strMaxSizeVal%>" styleClass="flatfields" maxlength="<%=strMaxLengthVal%>"  disabled="<%=bEditable%>" />
																	 </logic:equal>
																	 <logic:notEqual name="updateServiceForm" property="<%=strFieldname%>" value="Password">
																		 <html:text title="<%=descriptionVal%>"  name="updateServiceForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" size="<%=strMaxSizeVal%>" styleClass="flatfields" maxlength="<%=strMaxLengthVal%>"  disabled="<%=bEditable%>" />
																	 </logic:notEqual>

																 </logic:notEqual>		
					                                   								                                   									                                   			
					                                   		</logic:notEqual>						                   									                                   									                                   		
					                                   	</logic:notEqual>	 
					                                   	</logic:notEqual>					                                   						                                   		                          
				                                     <%}%>
				                                     
				                                      <logic:equal name="updateServiceForm" property="<%=strIsNotNull%>" value="true">
															<font color="#FF0000"> *</font>																			
										       			</logic:equal>
				         
				                                     
				                                    <logic:equal name="updateServiceForm" property="<%=strIsNewAdded%>" value="true">
															<a name="tip"/>																			
													</logic:equal>
													
													<logic:equal name="updateServiceForm" property="<%=strIsRemoved%>" value="true">
															<a name="tip" />																			
													</logic:equal>
				                           
				                              <img src="<%=localBasePath1%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="popUpDesc('<%=j%>')"  />
						                       <logic:equal name="updateServiceForm" property="<%=strMultipleInstances%>" value="Y" >
						                               <logic:greaterThan  name="updateServiceForm" property="<%=strTotalInstance%>" value="1" >
															<img src="<%=localBasePath1%>/images/minus.jpg" onclick="deleteNode('<bean:write name="updateServiceForm" property="<%=strInstanceId%>"/>')" />
												       </logic:greaterThan>			                           
							                   </logic:equal>
							                   &nbsp;&nbsp;
							                   <logic:equal name="updateServiceForm" property="<%=strAlias%>" value="attribute-id">
						                   					
						                   					<a href='#' onClick="return popup('<%=localBasePath1%>/searchDictionaryPopUp.do?fieldName=<%=strFieldValue%>','notes')"><img src="<%=localBasePath1%>/images/lookup.jpg" name="Image521" border=0 id="Image5" onMouseOver="MM_swapImage('Image521','','<%=localBasePath1%>/images/lookup-hover.jpg',1)" onMouseOut="MM_swapImgRestore()"/></a>
						                   					
							                   </logic:equal>
										</td>
					                   </logic:notEqual>
					                   
		              </logic:equal>      
		              
		              
		              
			                   
			                 
			                   <% j++;%>
			                     
					       	   	<logic:notEmpty name="updateServiceBean" property="startDivStatus">
						 			<div id="<bean:write name="updateServiceBean" property="startDivStatus"/>" style="display:block;color: blue" >
						 				<table width="100%" cellspacing="0" cellpadding="0" border="0">
							 </logic:notEmpty>
							 
							 <%if(index==size){ %>
						  		<logic:iterate id="closeDivList" name="updateServiceBean" property="closeDivStatusLst">
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
                        <table width="100%" border="0" cellpadding="0" cellspacing="0" height="15%" >                                        
                            <logic:iterate id="startUpMode" name="updateServiceForm" property="startUpModeList" type="com.elitecore.netvertexsm.datamanager.radius.system.standardmaster.data.IStandardMasterData">
                                    <tr>
                                        <td width="5%" class="labeltext"><img src="<%=localBasePath1%>/images/<bean:write name="startUpMode" property="masterId" />.jpg"/></td>
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
              <td colspan="2" >&nbsp;</td>
            </tr>
				<tr>
					<td align="center" class="labeltext" valign="top" colspan="2">
					    <input type="button" name="Save" width="5%" name=""  value="   Update   " onclick="saveConfiguration()" class="light-btn" />       
						<input type="button" name="Reset" onclick="reset();" value="   Reset    " class="light-btn" >  
					</td>
				</tr>
                    
           <%} %>
            <tr>
              <td class="small-gap" colspan="2" >&nbsp;</td>
            </tr>
          </table>
      </td>
    </tr>
	
</table>
        </td>
    </tr>

</table>

</html:form>
<tr>

<%@ include file="/jsp/core/includes/common/Footer.jsp" %>

<!-- -------------------------------------------------- -->