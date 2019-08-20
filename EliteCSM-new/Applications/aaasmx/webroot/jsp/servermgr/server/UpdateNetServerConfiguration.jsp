<%@page import="com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard"%>
<%@page import="com.elitecore.core.commons.tls.TLSVersion"%>
<%@page import="java.util.Collection"%>
<%@ page import="com.elitecore.elitesm.web.servermgr.server.forms.UpdateNetServerConfigurationForm"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.data.NetConfigParamValuePoolData"%>
<%@ page import="java.util.StringTokenizer"%>
<%@ page import="java.util.List"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.elitecore.elitesm.web.core.system.profilemanagement.ProfileManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.core.commons.tls.cipher.CipherSuites" %>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>

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
	List clientProfileList  = (List)request.getAttribute("clientProfileList");
	List alertListenerList  = (List)request.getAttribute("alertListenerList");
	List sysLogNameValuePoolDataList  = (List)request.getAttribute("sysLogNameValuePoolDataList");
	List translationMappingConfDataList = (List)request.getAttribute("translationMappingConfDataList");
	List databaseDSList=(List)request.getAttribute("databaseDSList");
	List lstParameterValue = updateNetServerForm.getLstParameterValue();
	List diameterSessionManagerList = (List)request.getAttribute("diameterSessionManagerDataList");
	List pluginInstDataList = (List)request.getAttribute("pluginInstDataList");
	
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
	
  function getPopupData(index){
	  return index; 
  }
	
	function saveConfiguration(){
		var check = validateParameterValues();
		if(check == true){
		    document.forms[0].action.value='save';
		    elableAll();
		    document.forms[0].submit();
		}
	}

	function validateParameterValues(){  
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
		   
		   		   if(!isValidCipherSuitList()){
					   return false;
				   }else if(!isValidTLSVersion()){
					   return false;
				   }
		   	convertListToDigit();
			return true;
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
	
	 
	 var ajaxFlag = false; 
	 var minParentId="";
	 var maxParentId="";
	 var parent1;

	 $(document).ready(function(){
		 $('.minTLSClass').each( function(){
			 var $option = $(this);  
			 $option.siblings()
			       .filter( function(){ return $(this).val() == $option.val(); } )
			       .remove();
		});
		$('.maxTLSClass').each( function(){
			 var $option = $(this);  
			 $option.siblings()
			       .filter( function(){ return $(this).val() == $option.val(); } )
			       .remove();
		});
		$('.securityStandards').each( function(){
			 var $option = $(this);  
			 $option.siblings()
			       .filter( function(){ return $(this).val() == $option.val(); } )
			       .remove();
		}); 
		
		$('.minTLSClass').each(function() {
		    if($(this).is(':selected')){
		    	$('#minTLS').val($(this).val());
		    }
		});
		$('.maxTLSClass').each(function() {
		    if($(this).is(':selected')){
		    	$('#maxTLS').val($(this).val());
		    }
		});
		
		$('.minTLSClass').parent().attr("id", "minTLSId");
		$('.maxTLSClass').parent().attr("id", "maxTLSId");
		
		$("#minTLSId").change(function(){
			var minTlsVersionData=$(this).val();
			$("#minTLS").val(minTlsVersionData);
			ajaxFunCall();
		});
		$("#maxTLSId").change(function(){
			var maxTlsVersionData=$(this).val();
			$("#maxTLS").val(maxTlsVersionData);
			ajaxFunCall();
		});
		ajaxFunCall();
	 });
			
	
	 function ajaxFunCall(){
			minTLSValue=$('#minTLS').val();
			maxTLSValue=$('#maxTLS').val();
			if(minTLSValue !="" && maxTLSValue != ""){
				var dbFieldStr;
				var dbFieldArray;
				var valuesOfCipherSuites="";
				$(".fieldList").val("");
				$.post("RetriveCipherSuit", {minTLSVersion:minTLSValue,maxTLSVersion:maxTLSValue}, function(data){
					data=data.trim();
					dbFieldStr = data.substring(1,data.length-1);
					dbFieldArray = new Array();
					dbFieldArray = dbFieldStr.split(", ");
					for(var i=0;i<dbFieldArray.length;i++){
						if(valuesOfCipherSuites !=""){
							valuesOfCipherSuites=valuesOfCipherSuites+",\n"+dbFieldArray[i];
						}else{
							valuesOfCipherSuites=dbFieldArray[i];
						}
					}
					$(".fieldList").val(valuesOfCipherSuites);
				});	
			}
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
	   		var newValueArray= finalValue.replace(/(\r\n|\n|\r)/gm,"");
	   		var dbFieldStr;
				var dbFieldArray;
				
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
	   
	   function isValidTLSVersion(){
			var minTLS=$('#minTLS').val();
			var maxTLS=$('#maxTLS').val();
			
			if(minTLS > maxTLS){
				 alert("Minimum TLS Version should be less than or equal to maximum TLS Version");
				 return false;
			}
			return true;
		 }

</script>

<html:form action="/updateNetServerConfiguration">
	<html:hidden name="updateNetServerForm" styleId="action"
		property="action" />
	<html:hidden name="updateNetServerForm" styleId="nodeParameterId"
		property="nodeParameterId" />
	<html:hidden name="updateNetServerForm" styleId="nodeInstanceId"
		property="nodeInstanceId" />
	<html:hidden name="updateNetServerForm" styleId="confInstanceId"
		property="confInstanceId" />
	<html:hidden name="updateNetServerForm" styleId="netServerId"
		property="netServerId" />
	<html:hidden name="updateNetServerForm" styleId="childTotalInstanceVal"
		property="childTotalInstanceVal" />
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
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>&nbsp;</td>
		</tr>

		<tr>
			<td class="table-header" align="left"><bean:message
					bundle="servermgrResources"
					key="servermgr.serverconfigurationdetail" /></td>
		</tr>
		<tr>
			<td>

				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					height="15%" align="right">
					<tr>
						<td valign="top" align="right">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="tblheader-bold" colspan="2" height="20%"><bean:message
											bundle="resultMessageResources"
											key="servermgr.update.configuration.summary" /></td>
								</tr>
								<tr>
									<td class="tblfirstcol" width="30%" height="20%"><bean:message
											bundle="resultMessageResources"
											key="servermgr.update.configuration.name" /></td>
									<td class="tblcol" width="70%" height="20%"><bean:write
											name="updateNetServerForm" property="configurationName" /></td>
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
									<td valign="top" align="right" colspan="2">
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td class="small-gap" colspan="3">&nbsp;</td>
											</tr>
											<tr>
												<td colspan="3">
													<%
	                  int size = updateNetServerForm.getLstParameterValue().size();
					%>
													<table width="100%" id="listTable" type="tbl-list"
														border="0" cellpadding="0" cellspacing="0" class="box"
														height="15%">
														<!-- added by dhavan -->

														<logic:iterate id="updateServerBean"
															name="updateNetServerForm" property="lstParameterValue"
															type="com.elitecore.elitesm.web.servermgr.server.forms.NetConfParameterValueBean">

															<%if(index<size){ %>
															<logic:iterate id="closeDivList" name="updateServerBean"
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
                                                 
                                        if(updateNetServerForm.getMaxLength(j) > 2000){
                                        	strMaxSizeVal = "2000";
                                        }else if(updateNetServerForm.getMaxLength(j) > 60){
											strMaxSizeVal = "60";
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

												<html:hidden name="updateNetServerForm"
													styleId="<%=strParameterId      %>"
													property="<%=strParameterId      %>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strSerialNo         %>"
													property="<%=strSerialNo         %>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strName             %>"
													property="<%=strName             %>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strDisplayName      %>"
													property="<%=strDisplayName      %>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strAlias            %>"
													property="<%=strAlias            %>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strMaxInstances     %>"
													property="<%=strMaxInstances     %>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strMultipleInstances%>"
													property="<%=strMultipleInstances%>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strParentParameterId%>"
													property="<%=strParentParameterId%>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strParameterValueId %>"
													property="<%=strParameterValueId %>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strConfigInstanceId %>"
													property="<%=strConfigInstanceId %>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strInstanceId       %>"
													property="<%=strInstanceId       %>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strType             %>"
													property="<%=strType             %>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strCurrentInstanceNo%>"
													property="<%=strCurrentInstanceNo%>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strTotalInstance    %>"
													property="<%=strTotalInstance    %>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strDescription      %>"
													property="<%=strDescription      %>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strMaxLength        %>"
													property="<%=strMaxLength        %>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strEditable         %>"
													property="<%=strEditable         %>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strStartUpMode         %>"
													property="<%=strStartUpMode         %>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strStatus  %>" property="<%=strStatus  %>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strRegexp  %>" property="<%=strRegexp  %>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strConfigId  %>" property="<%=strConfigId  %>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strIsNotNull  %>"
													property="<%=strIsNotNull  %>" />
												<html:hidden name="updateNetServerForm"
													styleId="<%=strPoolExists  %>"
													property="<%=strPoolExists  %>" />
												<logic:notEqual name="updateNetServerForm"
													property="<%=strStatus%>" value="Y">
													<html:hidden name="updateNetServerForm"
														styleId="<%=strFieldValue  %>"
														property="<%=strFieldValue  %>" />
												</logic:notEqual>



												<logic:equal name="updateNetServerForm"
													property="<%=strStatus%>" value="Y">

													<logic:notEqual name="updateNetServerForm"
														property="<%=strType%>" value="P">
														<td align="center" valign="center" class="topHLine"
															width="2%"><img
															src="<%=localBasePath%>/images/<bean:write name="updateNetServerForm" property="<%=strStartUpMode%>" />.jpg">
														</td>
														<td align="left" valign="center" class="topHLine"
															width="30%" colspan="2">
													</logic:notEqual>

													<logic:equal name="updateNetServerForm"
														property="<%=strType%>" value="P">
														<td align="center" valign="center" class="tblheader-bold"
															width="2%"><img
															src="<%=localBasePath%>/images/<bean:write name="updateNetServerForm" property="<%=strStartUpMode%>" />.jpg" />
														</td>
														<td align="left" class="tblheader-bold" valign="center"
															colspan="4">
													</logic:equal>



													<%
				                           StringTokenizer strToken = new StringTokenizer(strInstanceIdVal,".");
				                           for(int i=1;i<strToken.countTokens();i++){
					                          //out.print("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				                          }
				                        %>

													<logic:equal name="updateNetServerForm"
														property="<%=strIsNewAdded%>" value="true">
														<span class="blue-text-bold"><bean:write
																name="updateNetServerForm" property="<%=strFieldname%>" /></span>
													</logic:equal>
													<logic:notEqual name="updateNetServerForm"
														property="<%=strIsNewAdded%>" value="true">
														<bean:write name="updateNetServerForm"
															property="<%=strFieldname%>" />
													</logic:notEqual>

													<%if(k< size ){%>
													<logic:greaterThan name="updateNetServerForm"
														property="<%=strChildMaxInstance%>" value="1">
														<logic:greaterThan name="updateNetServerForm"
															property="<%=strChildMaxInstance%>"
															value="<%=strChildTotalInstanceVal%>">
															<logic:match name="updateNetServerForm"
																property="<%=strChildInstatnceId%>"
																value="<%=strInstanceIdVal%>" location="start">
																<logic:equal name="updateNetServerForm"
																	property="<%=strEditable%>" value="true">
																	<input type="button" value="Add"
																		onclick="addChildNode('<bean:write name="updateNetServerForm" property="<%=strParameterId%>" />','<bean:write name="updateNetServerForm" property="<%=strInstanceId%>" />','<%=strChildTotalInstanceVal%>')"
																		class="light-btn" />
																</logic:equal>
															</logic:match>
														</logic:greaterThan>
													</logic:greaterThan>

													<%}%>

													<logic:equal name="updateNetServerForm"
														property="<%=strType%>" value="P">
														<logic:equal name="updateNetServerForm"
															property="<%=strMultipleInstances%>" value="Y">
															<logic:greaterThan name="updateNetServerForm"
																property="<%=strTotalInstance%>" value="1">
																<logic:equal name="updateNetServerForm"
																	property="<%=strEditable%>" value="true">
																	<img src="<%=localBasePath%>/images/minus.jpg"
																		onclick="deleteNode('<bean:write name="updateNetServerForm" property="<%=strInstanceId%>"/>')" />
																</logic:equal>
															</logic:greaterThan>
														</logic:equal>
													</logic:equal>


													<logic:notEmpty name="updateServerBean"
														property="startDivStatus">
			                            		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img
															id="i<bean:write name="updateServerBean" property="startDivStatus"/>"
															src="<%=localBasePath%>/images/top-level.jpg" align="top"
															border="0"
															onclick="show('<bean:write name="updateServerBean" property="startDivStatus"/>')" />
													</logic:notEmpty>


													<logic:notEqual name="updateNetServerForm"
														property="<%=strType%>" value="P">
														<td align="left" valign="center" class="topHLine">
															<%if(netConfigParamValuePool!=null && !netConfigParamValuePool.isEmpty()){
														
													List<NetConfigParamValuePoolData> list = new ArrayList<NetConfigParamValuePoolData>();
													list.addAll(netConfigParamValuePool);
													Collections.sort(list);
													 String styleClass="";
													
													
			                            		%> 
			                            		<logic:equal name="updateNetServerForm" property="<%=strAlias%>" value="log-level">
													 	<% styleClass = "logLevelClass";%>
												</logic:equal>
												
			                            		<html:select
																title="<%=descriptionVal%>" name="updateNetServerForm"
																styleId="<%=strFieldValue%>"
																property="<%=strFieldValue%>" size="1"
																disabled="<%=bEditable%>" styleClass="<%=styleClass%>">
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
															
							                   		<logic:equal name="updateNetServerForm" property="<%=strAlias%>" value="name">
														<logic:notEmpty name="diameterPeerList">
						                   					<html:optionsCollection name="diameterPeerList" label="name" value="name" styleClass="peerClass" />
						                   				</logic:notEmpty>	
							                   		</logic:equal>
							                   		
							                   		 <logic:equal name="updateNetServerForm" property="<%=strAlias%>" value="plugin-name">
														<logic:notEmpty name="pluginInstDataList">
															<html:options collection="pluginInstDataList" labelProperty="name" property="name"/> 
						                   				</logic:notEmpty>	
							                   		</logic:equal>
							                   		
							                   		<logic:equal name="updateNetServerForm" property="<%=strAlias%>" value="server-certificate-id">
						                   					<%if(serverCerficateList!=null && !serverCerficateList.isEmpty()){ %>
						                   						<html:options collection="serverCerficateList" labelProperty="serverCertificateName" property="serverCertificateName" styleClass="serverCertificate"/> 
						                   					<% } %>
							                   		</logic:equal>
							                   		<logic:equal name="updateNetServerForm" property="<%=strAlias%>" value="max-tls-version">
						                   					<logic:iterate id="tlsVersionInst"  collection="<%=TLSVersion.values() %>" >
																	<html:option value="<%=((TLSVersion)tlsVersionInst).version%>" styleClass="maxTLSClass"><%=((TLSVersion)tlsVersionInst).version%></html:option>
															</logic:iterate>
							                   		</logic:equal> 
							                   		<logic:equal name="updateNetServerForm" property="<%=strAlias%>" value="min-tls-version" >
						                   					 <logic:iterate id="tlsVersionInst"  collection="<%=TLSVersion.values() %>"> 
																	 <html:option value="<%=((TLSVersion)tlsVersionInst).version%>"  styleClass="minTLSClass" ><%=((TLSVersion)tlsVersionInst).version%></html:option> 
															 </logic:iterate> 
							                   		</logic:equal> 
							                   		
							                   		<logic:equal name="updateNetServerForm" property="<%=strAlias%>" value="enabled-cipher-suites">
						                   					<logic:iterate id="ciphersuitInst"  collection="<%=CipherSuites.values()%>" >
																	<html:option value="<%=String.valueOf(((CipherSuites)ciphersuitInst).code)%>"><%=((CipherSuites)ciphersuitInst).name()%></html:option>
															</logic:iterate>
							                   		</logic:equal> 
							                   		
							                   		<logic:equal name="updateNetServerForm" property="<%=strAlias%>" value="security-standard">
						                   					<logic:iterate id="securityStandardInst"  collection="<%=SecurityStandard.values()%>" >
																	<html:option value="<%=((SecurityStandard)securityStandardInst).val%>" styleClass="securityStandards"><%=((SecurityStandard)securityStandardInst).val%></html:option>
															</logic:iterate>
							                   		</logic:equal> 
							                   		
							                   		<logic:equal name="updateNetServerForm" property="<%=strAlias%>" value="datasource-name">
															<%if(databaseDSList!=null && !databaseDSList.isEmpty()){ %>
						                   						<html:options collection="databaseDSList" labelProperty="name" property="name"/> 
						                   					<% } %>
							                   		</logic:equal> 
							                   		
													</html:select> <%}else{%> <logic:equal name="updateNetServerForm"
																property="<%=strAlias%>" value="profile-name">
																<html:select name="updateNetServerForm"
																	styleId="<%=strFieldValue%>"
																	property="<%=strFieldValue%>" disabled="<%=bEditable%>"
																	style="width:150px">
																	<%if(clientProfileList!=null && !clientProfileList.isEmpty()){ %>
																	<html:options collection="clientProfileList"
																		labelProperty="profileName" property="profileName" />
																	<% } %>
																</html:select>
															</logic:equal> <logic:notEqual name="updateNetServerForm"
																property="<%=strAlias%>" value="profile-name">
																<logic:equal name="updateNetServerForm"
																	property="<%=strAlias%>" value="alert-listener">
																	<html:select name="updateNetServerForm"
																		styleId="<%=strFieldValue%>"
																		property="<%=strFieldValue%>"
																		disabled="<%=bEditable%>" style="width:150px">
																		<html:option value="">-None-</html:option>
																		<%if(alertListenerList!=null && !alertListenerList.isEmpty()){ %>
																		<html:options collection="alertListenerList"
																			labelProperty="name" property="name" />
																		<% } %>
																	</html:select>
																</logic:equal>
																<logic:notEqual name="updateNetServerForm"
																	property="<%=strAlias%>" value="alert-listener">

																	<logic:equal name="updateNetServerForm"
																		property="<%=strAlias%>" value="ws-mapping-name">
																		<html:select name="updateNetServerForm"
																			styleId="<%=strFieldValue%>"
																			property="<%=strFieldValue%>"
																			disabled="<%=bEditable%>" style="width:150px">
																			<html:option value="">-None-</html:option>
																			<%if(translationMappingConfDataList!=null && !translationMappingConfDataList.isEmpty()){ %>
																			<html:options
																				collection="translationMappingConfDataList"
																				labelProperty="name" property="name" />
																			<% } %>
																		</html:select>
																	</logic:equal>
																	<logic:notEqual name="updateNetServerForm"
																		property="<%=strAlias%>" value="ws-mapping-name">
																		<logic:equal name="updateNetServerForm"
																			property="<%=strAlias%>" value="facility">
																			<html:select name="updateNetServerForm"
																				styleId="<%=strFieldValue%>"
																				property="<%=strFieldValue%>"
																				disabled="<%=bEditable%>" style="width:150px">
																				<html:option value="">-None-</html:option>
																				<%if(sysLogNameValuePoolDataList!=null && !sysLogNameValuePoolDataList.isEmpty()){ %>
																				<html:options
																					collection="sysLogNameValuePoolDataList"
																					labelProperty="name" property="name" />
																				<% } %>
																			</html:select>
																		</logic:equal>

																		<logic:notEqual name="updateNetServerForm" property="<%=strAlias%>" value="facility">
																			 <logic:equal name="updateNetServerForm" property="<%=strAlias%>" value="password" >
																					<html:password  title="<%=descriptionVal%>" name="updateNetServerForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>"
																					size="<%=strMaxSizeVal%>" styleClass="flatfields" maxlength="<%=strMaxLengthVal%>" disabled="<%=bEditable%>"/>
																			</logic:equal>
																			<logic:notEqual name="updateNetServerForm" property="<%=strAlias%>" value="password">
																				<logic:equal name="updateNetServerForm" property="<%=strAlias%>" value="routing-table">
									                   								<html:select  name="updateNetServerForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" disabled="<%=bEditable%>" style="width:150px" tabindex="6">
									                   									<html:option value="">-None-</html:option>
										                   								<%
										                   								   List diameterRoutingTableList  = (List)request.getAttribute("diameterRoutingTableList");
										                   								   if(diameterRoutingTableList!=null && !diameterRoutingTableList.isEmpty()){
										                   								   for(int i=0;i<diameterRoutingTableList.size();i++)
										                   								   {	   %>							       
												                   						     <html:option value="<%=diameterRoutingTableList.get(i).toString() %>" ><%=diameterRoutingTableList.get(i).toString() %>  </html:option>
													                   					<%
											                   								}
	                                                                                    } %>
									                   								</html:select>
									                   						   </logic:equal>
																			  <%if(strMaxSizeVal == "2000"){ %>
										                   						     	<logic:equal name="updateNetServerForm" property="<%=strAlias%>" value="enabled-cipher-suites" >
										                   						     	     <html:textarea title="<%=descriptionVal%>"  name="updateNetServerForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" styleClass="fieldList"  disabled="<%=bEditable%>" rows="5" cols="60"/>
										                   						     	</logic:equal> 
										                   						     	<logic:equal value="argument" name="updateNetServerForm" property="<%=strAlias%>">
										                   						     	     <html:textarea title="<%=descriptionVal%>"  name="updateNetServerForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" disabled="<%=bEditable%>" rows="1" cols="1" style="height: 21px;width:300px;" />
										                   						     	</logic:equal>
										                   							<%}else{ %>
				  			                                    				   <logic:notEqual name="updateNetServerForm" property="<%=strAlias%>" value="routing-table">
					  			                                    				  	<logic:equal name="updateNetServerForm" property="<%=strAlias%>" value="session-manager-id">
											                   								<html:select  name="updateNetServerForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" disabled="<%=bEditable%>" style="width:150px" tabindex="6">
											                   									<html:option value="">-None-</html:option>
											                   									<%if(diameterSessionManagerList!=null && !diameterSessionManagerList.isEmpty()){ %>
																									<html:options collection="diameterSessionManagerDataList"
																									labelProperty="name" property="sessionManagerId" />
																								<% } %>
											                   									
											                   								</html:select>
										                   						   		</logic:equal>
											                   							<logic:notEqual name="updateNetServerForm" property="<%=strAlias%>" value="session-manager-id">
											                   								<html:text title="<%=descriptionVal%>"  name="updateNetServerForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>" size="<%=strMaxSizeVal%>" styleClass="flatfields" maxlength="<%=strMaxLengthVal%>" disabled="<%=bEditable%>" />
											                   							</logic:notEqual>
				  			                                    				    </logic:notEqual> 
				  			                                    				<%} %>
																			</logic:notEqual>
																		</logic:notEqual>
																	</logic:notEqual>
																</logic:notEqual>
															</logic:notEqual> <%}%> <logic:equal name="updateNetServerForm"
																property="<%=strIsNotNull%>" value="true">
																<font color="#FF0000">*</font>
															</logic:equal> <logic:equal name="updateNetServerForm"
																property="<%=strIsNewAdded%>" value="true">
																<a name="tip" />
															</logic:equal> <logic:equal name="updateNetServerForm"
																property="<%=strIsRemoved%>" value="true">
																<a name="tip" />
															</logic:equal> 
															<span onclick="popUpDesc('<%=j%>')" class="elitehelp" style="background-color: rgb(35, 105, 166);">?</span>
															<logic:equal
																name="updateNetServerForm"
																property="<%=strMultipleInstances%>" value="Y">
																<logic:greaterThan name="updateNetServerForm"
																	property="<%=strTotalInstance%>" value="1">
																	<img src="<%=localBasePath%>/images/minus.jpg"
																		onclick="deleteNode('<bean:write name="updateNetServerForm" property="<%=strInstanceId%>"/>')" />
																</logic:greaterThan>
															</logic:equal> &nbsp;&nbsp; <logic:equal name="updateNetServerForm"
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
												<logic:notEmpty name="updateServerBean"
													property="startDivStatus">
													<div
														id="<bean:write name="updateServerBean" property="startDivStatus"/>"
														style="display: block; color: blue">
														<table width="100%" cellspacing="0" cellpadding="0"
															border="0">
															</logic:notEmpty>

															<%if(index==size){ %>
															<logic:iterate id="closeDivList" name="updateServerBean"
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
									<td class="small-gap" colspan="2">&nbsp;</td>
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
							onclick="return saveConfiguration();" class="light-btn" /> <input
							type="button" name="Reset" onclick="reset();"
							value="   Reset    " class="light-btn"></td>
					</tr>
					<%} %>
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
