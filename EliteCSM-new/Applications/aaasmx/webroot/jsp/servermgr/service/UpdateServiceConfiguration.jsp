<%@page import="com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard"%>
<%@page import="com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm"%>
<%@page import="com.elitecore.core.commons.tls.cipher.CipherSuites"%>
<%@ page import="com.elitecore.elitesm.web.servermgr.service.forms.UpdateServiceConfigurationForm"%>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.List" %>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.elitecore.core.commons.tls.TLSVersion" %>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.data.NetConfigParamValuePoolData" %>
<%@ page import="java.util.StringTokenizer" %>
<%@page import="java.util.Collections"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.elitecore.elitesm.web.core.system.profilemanagement.ProfileManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="java.util.Collection" %>
<%@page import="com.elitecore.core.commons.tls.cipher.CipherSuites" %>

 <%

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
	List pluginInstDataList = (List)request.getAttribute("pluginInstDataList");
	List classicCSVDriverList=(List)request.getAttribute("classicCSVDriverList");
	List datasourceList=(List)request.getAttribute("datasourceList");
	List localSessionManagerList=(List)request.getAttribute("localSessionManagerList");
	List lstParameterValue = updateServiceForm.getLstParameterValue(); 
	List sysLogNameValuePoolDataList  = (List)request.getAttribute("sysLogNameValuePoolDataList");	
	List diameterRoutingTableList  = (List)request.getAttribute("diameterRoutingTableList");
	List serverCerficateList=(List)request.getAttribute("serverCerficateList");
	
	ArrayList<String> cipherSuitArrayList=new ArrayList<String>();
	String strCipherSuit="";
	for(CipherSuites cipherSuites:CipherSuites.values()){
		cipherSuitArrayList.add(cipherSuites.name());
		if(strCipherSuit != ""){
			strCipherSuit=strCipherSuit+",\n"+cipherSuites.name();
		}else{
			strCipherSuit=cipherSuites.name();
		}
	}
	String minTLS="";
	String maxTLS="";
 %>
<script type="text/javascript">

$(document).ready(function(){
	$('.fieldList').attr('spellcheck',false);
});



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
        	var varWindow = window.open('<%=basePath%>/jsp/servermgr/server/NetDescriptionPopup.jsp?description='+strValue.value,'DescriptionWin','top=100, left=200, height=200, width=500, scrollbars=yes, status=1');
        	varWindow.focus();
        }else{
        	alert('Description does not exist');
        }
    }
     
   function validateParameterValues(){  
	   if(!isValidCipherSuitList()){
		   return false;
	   }else if(!isValidTLSVersion()){
		   return false;
	    }else if(!isValidDropDown("peerClass","Peer")){
			  return false;
	    }else if( !isValidDropDown("servicePolicyClass","ServicePolicy")){
		   return false;
	    }else{
	    	<%for(int i=0;i<lstParameterValue.size();i++){ %>  
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
	    }
   		
		return true;
    }
   function isValidCipherSuitList(){
	   if($('.fieldList').val()){
		   var finalValue=$('.fieldList').val();
		   var newValArray=finalValue.replace(/(\r\n|\n|\r)/gm,"");
		   var minTLSVersionValue;
		   var maxTLSVersionValue;
		   $('.minTLSClass').each(function() {
			    if($(this).is(':selected')){
			    	minTLSVersionValue=$(this).val();
			    }
		   });
		   $('.maxTLSClass').each(function() {
			    if($(this).is(':selected')){
			    	minTLSVersionValue=$(this).val();
			    }
		   });
		   
		   var data;
		    $.ajax({url:'<%=request.getContextPath()%>/FindInvalidCipherSuit',
		          type:'POST',
		          data:{ cipherList:newValArray,minTLSVersion:minTLSVersionValue,maxTLSVersion:maxTLSVersionValue },
		          async:false,
		          success: function(transport){
		             data=transport;
		        }
		   });
			if(data!=null){
				var dbFieldStr = data.substring(1,data.length-3);
				var dbFieldArray = new Array();
				dbFieldArray = dbFieldStr.split(", ");
				var newArray=dbFieldArray;
				var startIndex = data.indexOf('[');
				var endIndex = data.indexOf(']');
				if(endIndex === startIndex+1){		
					 var serverCertificateId='NONE';
					 $('.serverCertificate').each(function() {
						    if($(this).is(':selected')){
						    	serverCertificateId="";
						    	serverCertificateId=$(this).val();
						    }
					 });
					 var securityStandard='NONE';
					 
					 $('.securityStandards').each(function() {
						    if($(this).is(':selected')){
						    	securityStandard="";
						    	securityStandard=$(this).val();
						    }
					 });
					 if(serverCertificateId === 'NONE' && securityStandard !== 'NONE'){
							alert("TLS Connection may not be Possible");
					 }
						 
					ajaxFlag=true; 					
							
				}else{
					var newTable="\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
					for(var index=0;index<newArray.length;index++){
						newTable=newTable+"\n"+newArray[index];
					}
					alert("Invalid Ciphersuit List !!!  " +newTable);
					ajaxFlag = false;			
				}
				return ajaxFlag;
						
			}else{
		    	return false;
			}
	   }
	   
	   var securityStandard='NONE';
		 
	   $('.securityStandards').each(function() {
			    if($(this).is(':selected')){
			    	securityStandard="";
			    	securityStandard=$(this).val();
			    }
	   });
	   
	   if(securityStandard !== 'NONE'){
		   alert('TLS Connection may not be Possible');
	   }
	   return true;
   }
   function convertListToDigit(){
   	if($('.fieldList').val()){
   		var finalValue=$('.fieldList').val();
   		finalValue=finalValue.trim();
   		var newValueArray= finalValue.replace(/(\r\n|\n|\r)/gm,"");
   		var dbFieldStr;
			var dbFieldArray;
			$(".fieldList").val(newValArray);
			var data;
			    $.ajax({url:'<%=request.getContextPath()%>/ConvertListToCipherSuit',
			          type:'POST',
			          data:{listArray:newValueArray},
			          async:false,
			          success: function(transport){
			             data=transport;
			        }
			});
			    
			    
			if(data!=null){
					$('.fieldList').val("");
					dbFieldStr = data.substring(1,data.length-3);
					dbFieldArray = new Array();
					dbFieldArray = dbFieldStr.split(", ");
					$('.fieldList').val(dbFieldArray);
			}
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
       		document.getElementById(image).src = '<%=basePath%>/images/bottom-level.jpg';
    	} else {
       		srcElement.style.display = 'block';
       		document.getElementById(image).src = '<%=basePath%>/images/top-level.jpg';
    	}
  	  }
    }			

 function isValidTLSVersion(){
	var minTLS=$('#minTLS').val();
	var maxTLS=$('#maxTLS').val();
	
	if(minTLS > maxTLS){
		 alert("Minimum TLS Version should be less than or equal to maximum TLS Version");
		 return false;
	}
	return true;
 }

 function isValidDropDown(className,parameterName){
	 var selectNameArray = new Array();
	 var arrayIndex = 0;
	 
	 $("."+className).each(function() { 
				if($.inArray($(this).parent().attr("name"),selectNameArray) < 0){
					selectNameArray[arrayIndex++] = $(this).parent().attr("name");
					//alert($.trim($(this).parent().closest('td').prev('td').text())); get Previous element
				}
		});
	
	 var selectValueArray = new Array();
	 arrayIndex = 0;
	 var duplicateValue = "";
	 $.each(selectNameArray, function(index,value){
		//alert($('select[name='+value+']').val());
		var selectedValue = $('select[name='+value+']').val();
		if($.inArray(selectedValue,selectValueArray) >= 0) {
			duplicateValue = value;
		} 
		selectValueArray[arrayIndex++] = selectedValue;
	 });
	 
	 if(duplicateValue != ""){
		 alert(parameterName+" "+$('select[name='+duplicateValue+']  option:selected').text()+" configure multiple time.");
		 $('select[name='+duplicateValue+']').focus();
		 return false;
	 }
	 return true;		
 }

</script>
<html:form action="/updateServiceConfiguration" styleId="formId" >
<html:hidden name="updateServiceForm" styleId="action" property="action" />
<html:hidden name="updateServiceForm" styleId="nodeParameterId" property="nodeParameterId" />
<html:hidden name="updateServiceForm" styleId="nodeInstanceId" property="nodeInstanceId" />
<html:hidden name="updateServiceForm" styleId="confInstanceId" property="confInstanceId" />
<html:hidden name="updateServiceForm" styleId="netServiceId" property="netServiceId" />
<html:hidden name="updateServiceForm" styleId="childTotalInstanceVal" property="childTotalInstanceVal" />
<input type="hidden" name="minTLS" value="" id="minTLS"/>
<input type="hidden" name="maxTLS" value="" id="maxTLS"/>
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

<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" align="right">
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
              <td valign="center" align="right" colspan="2">
                <table cellpadding="0" cellspacing="0" border="0" width="100%" >
                  <tr>
                    <td class="small-gap" colspan="3">&nbsp;
                    </td>
                  </tr>
                  <tr>
                    <td colspan="3">
<%
			                       int size = updateServiceForm.getLstParameterValue().size();
%>
                      <table width="100%" border="0" cellpadding="0" cellspacing="0" class="box" >
                        <!-- added by dhavan -->
							<logic:iterate id="updateServiceBean" name="updateServiceForm" property="lstParameterValue" type="com.elitecore.elitesm.web.servermgr.server.forms.NetConfParameterValueBean" >
							
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
                                        if(updateServiceForm.getMaxLength(j) > 2000){
                                        	strMaxSizeVal = "2000";
                                        }else if(updateServiceForm.getMaxLength(j) > 60){
											strMaxSizeVal = "60";
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
			                               <td align="left"  valign="center" class="topHLine" width="35%" colspan="2"> 
			                           </logic:notEqual>
				                           

			                           <logic:equal name="updateServiceForm" property="<%=strType%>" value="P" >
			                          	 <td align="left" class="tblheader-bold" valign="center" colspan="4">
			                          	 				<%-- <img src="<%=basePath%>/images/drop1.gif" onclick="togglediv('<%="e"+j%>')" /> --%>
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
										</logic:notEqual>    
			                          
			                           
			                           
                                       <img src="<%=basePath%>/images/<bean:write name="updateServiceForm" property="<%=strStartUpMode%>" />.jpg"/>                                                                              
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
													<img src="<%=basePath%>/images/minus.jpg" onclick="deleteNode('<bean:write name="updateServiceForm" property="<%=strInstanceId%>"/>')" />
                                            </logic:equal>
										</logic:greaterThan>						                           
					                   </logic:equal>
					                   </logic:equal>
			                           
			                             <logic:notEmpty name="updateServiceBean" property="startDivStatus">
								  		         		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img id="i<bean:write name="updateServiceBean" property="startDivStatus"/>" src="<%=basePath%>/images/top-level.jpg" border="0" onclick="show('<bean:write name="updateServiceBean" property="startDivStatus"/>')"/>
         							 	</logic:notEmpty>
			                   
			                           
		                              <logic:notEqual name="updateServiceForm" property="<%=strType%>" value="P" >
			                            <td align="left"  valign="center" class="topHLine" >	
			                            		<%if(netConfigParamValuePool!=null && !netConfigParamValuePool.isEmpty()){
			                            		
			                            			List<NetConfigParamValuePoolData> list = new ArrayList<NetConfigParamValuePoolData>();
													list.addAll(netConfigParamValuePool);
													Collections.sort(list);
													String strClassName="";
			                            		%>
				                            		<logic:equal name="updateServiceForm" property="<%=strAlias%>" value="log-level">
														 	<% strClassName = "logLevelClass";%>
													</logic:equal>
			                            		    <html:select title="<%=descriptionVal%>" name="updateServiceForm" styleId = "<%=strFieldValue%>" styleClass="<%=strClassName%>" property="<%=strFieldValue%>" size="1" tabindex="1">
													<%
													 Iterator itr = list.iterator();
													 while(itr.hasNext()){
													 	NetConfigParamValuePoolData netConfigParamValuePoolData = (NetConfigParamValuePoolData)itr.next();
													 	String styleClass = "";
													 	%>
													 	<logic:equal name="updateServiceForm" property="<%=strAlias%>" value="service-policy">
													 		<% styleClass = "servicePolicyClass";%>
													 	</logic:equal>
													 	<logic:equal name="updateServiceForm" property="<%=strAlias%>" value="name">
													 		<% styleClass = "peerClass";%>
													 	</logic:equal>
													 	 <html:option  value="<%=netConfigParamValuePoolData.getValue()%>" styleClass="<%=styleClass%>" ><%=netConfigParamValuePoolData.getName()%></html:option>
													 	<%
													 }
													%>
							                   		
							                   		<logic:equal name="updateServiceForm" property="<%=strAlias%>" value="server-certificate-id">
						                   					<%if(serverCerficateList!=null && !serverCerficateList.isEmpty()){ %>
						                   						<html:options collection="serverCerficateList" labelProperty="serverCertificateName" property="serverCertificateName" styleClass="serverCertificate"/> 
						                   					<% } %>
							                   		</logic:equal>
							                   		<logic:equal name="updateServiceForm" property="<%=strAlias%>" value="max-tls-version">
						                   					<logic:iterate id="tlsVersionInst"  collection="<%=TLSVersion.values() %>" >
																	<html:option value="<%=((TLSVersion)tlsVersionInst).version%>" styleClass="maxTLSClass"><%=((TLSVersion)tlsVersionInst).version%></html:option>
															</logic:iterate>
							                   		</logic:equal> 
							                   		<logic:equal name="updateServiceForm" property="<%=strAlias%>" value="min-tls-version" >
						                   					 <logic:iterate id="tlsVersionInst"  collection="<%=TLSVersion.values() %>"> 
																	 <html:option value="<%=((TLSVersion)tlsVersionInst).version%>"  styleClass="minTLSClass" ><%=((TLSVersion)tlsVersionInst).version%></html:option> 
															 </logic:iterate> 
							                   		</logic:equal> 
							                   		
							                   		<logic:equal name="updateServiceForm" property="<%=strAlias%>" value="enabled-cipher-suites">
						                   					<logic:iterate id="ciphersuitInst"  collection="<%=CipherSuites.values()%>" >
																	<html:option value="<%=String.valueOf(((CipherSuites)ciphersuitInst).code)%>"><%=((CipherSuites)ciphersuitInst).name()%></html:option>
															</logic:iterate>
							                   		</logic:equal> 
							                   		
							                   		<logic:equal name="updateServiceForm" property="<%=strAlias%>" value="security-standard">
						                   					<logic:iterate id="securityStandardInst"  collection="<%=SecurityStandard.values()%>" >
																	<html:option value="<%=((SecurityStandard)securityStandardInst).val%>" styleClass="securityStandards"><%=((SecurityStandard)securityStandardInst).val%></html:option>
															</logic:iterate>
							                   		</logic:equal> 
							                   		
								                   	<logic:equal name="updateServiceForm" property="<%=strAlias%>" value="plugin-name">
							                   			<%if(pluginInstDataList!=null && !pluginInstDataList.isEmpty()){ %>
							                   				<html:options collection="pluginInstDataList" labelProperty="name" property="name"/> 
							                   			<% } %>
								                   	</logic:equal>
													
													<logic:equal name="updateServiceForm" property="<%=strAlias%>" value="service-policy">
						                   					<html:optionsCollection name="servicePolicyMap" label="value" value="value" styleClass="servicePolicyClass" />
							                   		</logic:equal>	
							                   							                   		
				                            	</html:select>
			                            		<%}else{ %>
			                            		 
								                   		<logic:equal name="updateServiceForm" property="<%=strAlias%>" value="prepaid-fallback-driver">
								                   				<html:select  name="updateServiceForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" disabled="<%=bEditable%>" style="width:200px" tabindex="2">
								                   				<html:option value="">-None-</html:option>
								                   					<%if(classicCSVDriverList!=null && !classicCSVDriverList.isEmpty()){ %>
								                   						<html:options collection="classicCSVDriverList" labelProperty="name" property="name"/> 
								                   					<% } %>
								                   				</html:select>
									                   		</logic:equal>
									                   		<logic:notEqual name="updateServiceForm" property="<%=strAlias%>" value="prepaid-fallback-driver">
										                   		<logic:equal name="updateServiceForm" property="<%=strAlias%>" value="datasource-name">
									                   				<html:select  name="updateServiceForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" disabled="<%=bEditable%>" style="width:200px" tabindex="3">
									                   					<%if(datasourceList!=null && !datasourceList.isEmpty()){ %>
									                   						<html:options collection="datasourceList" labelProperty="name" property="name"/> 
									                   					<% } %>
									                   				</html:select>
										                   		</logic:equal>
									                   			<logic:notEqual name="updateServiceForm" property="<%=strAlias%>" value="datasource-name">
									                   					<logic:equal name="updateServiceForm" property="<%=strAlias%>" value="session-manager-name">
									                   						<html:select  name="updateServiceForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" disabled="<%=bEditable%>" style="width:200px" tabindex="4">
									                   						<html:option value="">-None-</html:option>
									                   							<%if(localSessionManagerList!=null && !localSessionManagerList.isEmpty()){ %>
									                   							<html:options collection="localSessionManagerList" labelProperty="name" property="name"/> 
									                   							<% } %>
									                   						</html:select>
										                   				</logic:equal>
										                   				<logic:notEqual name="updateServiceForm" property="<%=strAlias%>" value="session-manager-name">
									                   						<logic:equal name="updateServiceForm" property="<%=strAlias%>" value="facility">
								                   								<html:select  name="updateServiceForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" disabled="<%=bEditable%>" style="width:150px" tabindex="5">
									                   								<html:option value="">-None-</html:option>
												                   					<%if(sysLogNameValuePoolDataList!=null && !sysLogNameValuePoolDataList.isEmpty()){ %>							       
												                   						<html:options collection="sysLogNameValuePoolDataList" labelProperty="name" property="name"/> 
												                   					<% } %>
								                   								</html:select>
									                   						</logic:equal>
											                   				<logic:notEqual name="updateServiceForm" property="<%=strAlias%>" value="facility">
											                   				    <logic:equal name="updateServiceForm" property="<%=strAlias%>" value="routing-table">
									                   								<html:select  name="updateServiceForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" disabled="<%=bEditable%>" style="width:150px" tabindex="6">
									                   									<html:option value="">-None-</html:option>
										                   								<%if(diameterRoutingTableList!=null && !diameterRoutingTableList.isEmpty()){
										                   								   for(int i=0;i<diameterRoutingTableList.size();i++)
										                   								   {	   %>							       
												                   						     <html:option value="<%=diameterRoutingTableList.get(i).toString() %>" ><%=diameterRoutingTableList.get(i).toString() %>  </html:option>
													                   					<%
											                   								}
	                                                                                    } %>
									                   								</html:select>
									                   						    </logic:equal>
									                   						    <%if(strMaxSizeVal == "2000"){ %>
					  			                                    				<logic:equal name="updateServiceForm" property="<%=strAlias%>" value="enabled-cipher-suites" >
										                   						    	     <html:textarea title="<%=descriptionVal%>"  name="updateServiceForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" styleClass="fieldList"  disabled="<%=bEditable%>" rows="5" cols="60"/>
										                   						    </logic:equal> 
										                   						    <logic:equal value="argument" name="updateServiceForm" property="<%=strAlias%>">
										                   						    	     <html:textarea title="<%=descriptionVal%>"  name="updateServiceForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" disabled="<%=bEditable%>" rows="1" cols="1" style="height: 21px;width:300px;" />
										                   						    </logic:equal>
				  			                                    				<%}else{ %>
				  			                                    				  <logic:notEqual name="updateServiceForm" property="<%=strAlias%>" value="routing-table">
				  			                                    				 	 <html:text title="<%=descriptionVal%>"  name="updateServiceForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" size="<%=strMaxSizeVal%>" styleClass="flatfields" maxlength="<%=strMaxLengthVal%>" disabled="<%=bEditable%>" />
				  			                                    				  </logic:notEqual> 
				  			                                    				<%} %>
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
				                           		<span onclick="popUpDesc('<%=j%>')" class="elitehelp" style="background-color: rgb(35, 105, 166);">?</span>
				                              <%--  <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="popUpDesc('<%=j%>')"  /> --%>
						                       <logic:equal name="updateServiceForm" property="<%=strMultipleInstances%>" value="Y" >
						                               <logic:greaterThan  name="updateServiceForm" property="<%=strTotalInstance%>" value="1" >
															<img src="<%=basePath%>/images/minus.jpg" onclick="deleteNode('<bean:write name="updateServiceForm" property="<%=strInstanceId%>"/>')" />
												       </logic:greaterThan>			                           
							                   </logic:equal>
							                   &nbsp;&nbsp;
							                   <logic:equal name="updateServiceForm" property="<%=strAlias%>" value="attribute-id">
						                   					
						                   					<a href='#' onClick="return popup('<%=basePath%>/searchDictionaryPopUp.do?fieldName=<%=strFieldValue%>','notes')"><img src="<%=basePath%>/images/lookup.jpg" name="Image521" border=0 id="Image5" onMouseOver="MM_swapImage('Image521','','<%=basePath%>/images/lookup-hover.jpg',1)" onMouseOut="MM_swapImgRestore()"/></a>
						                   					
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
                            <logic:iterate id="startUpMode" name="startUpModeList" type="com.elitecore.elitesm.datamanager.radius.system.standardmaster.data.IStandardMasterData">
                                    <tr>
                                        <td width="5%" class="labeltext"><img src="<%=basePath%>/images/<bean:write name="startUpMode" property="masterId" />.jpg"/></td>
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
					    <input type="button" name="Save" width="5%" name=""  value="   Update   " onclick="saveConfiguration()" class="light-btn" tabindex="7"/>       
						<input type="button" name="Reset" onclick="reset();" value="   Reset    " class="light-btn" tabindex="8">  
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
<!-- -------------------------------------------------- -->