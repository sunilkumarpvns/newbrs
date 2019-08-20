<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPBaseDnDetailData"%>
<%@ page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
	String basePath = request.getContextPath();
	List baseDnDetailList = (List)request.getAttribute("baseDnDetailList");
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script language="javascript1.2" src="<%=basePath%>/js/checkpwdstrength.js" type="text/javascript"></script>

<script language="javascript1.2">
 var isValidName;

 	function validateForm(){

 		var minPoolSize=parseInt(document.forms[0].minimumPool.value);
 	    var maxPoolSize=parseInt(document.forms[0].maximumPool.value);	
	
 		if(isNull(document.forms[0].name.value)){
 			alert('Name must be specified');
 		}else if(!isValidName) {
 			alert('Enter Valid Name');
 			document.forms[0].name.focus();
 			return;
 		}else if(isNull(document.forms[0].address.value)){
 			alert('Address must be specified');
 		}else if(isNull(document.forms[0].timeout.value)){
 			alert('Timeout must be specified');
 		}else if(isNull(document.forms[0].ldapSizelimit.value)){
 			alert('LDAP Size Limit must be specified');
 		}else if(isNull(document.forms[0].administrator.value)){
 			alert('Administrator must be specified');
 		}else if(isNull(document.forms[0].pwd.value)){
 			alert('Password must be specified');
 		}else if(isNull(document.forms[0].userDnPrefix.value)){
 			alert('User DN Prefix must be specified');
 		}else if(isNull(document.forms[0].maximumPool.value)){
 			alert('Maximum Pool must be specified');
 			document.forms[0].maximumPool.focus();
 	    }else if(!(isNumber(document.forms[0].maximumPool.value))){
 	       alert('Maximum Pool value must be Numeric');
 	       document.forms[0].maximumPool.focus();
 	    }else if(isNull(document.forms[0].minimumPool.value)){
 			alert('Minimum Pool must be specified');
 	    	document.forms[0].minimumPool.focus();
 	    }else if(!(isNumber(document.forms[0].minimumPool.value))){
  	       alert('Minimum Pool value must be Numeric');
 	       document.forms[0].minimumPool.focus();
 	    }else if((isNull(document.forms[0].statusCheckDuration.value)) ||(!(isNumber(document.forms[0].statusCheckDuration.value)))){
  	       alert('Status Check Duration must be Numeric');
 	       document.forms[0].statusCheckDuration.focus();
 	    }else if(minPoolSize > maxPoolSize){
 	    	alert('Minimum Pool value must be less than Maximum Pool value');
 	    	document.forms[0].minimumPool.focus();
 		}else if (!isNull(document.forms[0].ldapVersion.value) && (!isNumber(document.forms[0].ldapVersion.value)) ){
 			alert('LDAP Version must be Numeric.');
 			document.forms[0].ldapVersion.focus();
 		}else if(document.getElementById('mappingtbl').rows.length <= 1){
 			alert('Atleast one DN must be specified.');     	
 		}else{
 			var validIp=validateIpAddress();
 			if(validIp==true){
 	 		document.forms[0].checkAction.value = 'create';	
 			document.forms[0].submit();
 			}else{
 				alert('Please Insert Valid Address(HOST:PORT)');
 				document.forms[0].ipaddress.focus();
 		}		
	}
	}

 	function validatePort(txt){
		// check for valid numeric port	 		
		if(IsNumeric(txt) == true){
			if(txt >= 0 && txt<=65535)
				return(true);
		}else
				return(false);
			}				
	
	function validateIP(ipaddress){
		var ip=/((^\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\s*$)|(^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$))|(^\s*((?=.{1,255}$)(?=.*[A-Za-z].*)[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?(?:\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?)*)\s*$)/;
			if(ip.test(ipaddress)){
				   return true;
		}else{			
				   return false;
		}				
	}

	function validateIpAddress(){
		var flagIp=false,flagPort=false,validIP=false;
		var ipAddress=document.getElementById("address").value;
		
		var ipAds = ipAddress.split(":").length - 1;
		if(ipAds==1){
			 var strIpAddress=ipAddress.split(":");
			 if(! strIpAddress[0]){
					flag=false;
			 }else if(! strIpAddress[1]){
				    	flag=false;
			 }else{
			    	flagIp=validateIP(strIpAddress[0]);
			    	flagPort=validatePort(strIpAddress[1]);
			 }
		}else if(ipAds>1){
			var firstCut=null,secondCut=null,finalResult=null,validPort=null;
			
			firstCut = ipAddress.split('[');
			    
			if(typeof firstCut[1] != 'undefined'){
			   	 secondCut = firstCut[1].split(']:'); 
			   	 finalResult = secondCut[0],validPort=secondCut[1];
		    }else{
			   	flagIp=false;
				flagPort=false;
			}
			
			 if(typeof firstCut[0] != 'undefined' && typeof firstCut[1] != 'undefined'  && typeof secondCut[0] != 'undefined' && typeof secondCut[1] != 'undefined'){
				flagIp=validateIP(finalResult);
		    	flagPort=validatePort(validPort);
			}else{
				flagIp=false;
				flagPort=false;
			}
		}
		 if(flagIp==false && flagPort==false){
		     alert('Please Enter Valid Address (HOST:PORT)');
		     document.forms[0].ipaddress.focus();
		}else{
		    if(flagIp==true && flagPort==true){
		    	validIP=true;
		    }
	     }
		   return validIP;
	}

/*	function validateHostName(hostName) {
		var validHostnameRegex = /^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\-]*[A-Za-z0-9])$/;
		if(hostName != null && hostName!="" && !validHostnameRegex.test(hostName)){
			return false;	
		}else{
			return true;
		}
	} */

	function isNumber(val){
		nre= /^\d+$/;
		var regexp = new RegExp(nre);
		if(!regexp.test(val))
		{
			return false;
		}
		return true;
	}

/*	function validateIpAddress(ipAddress)
    {
		var ipre=/^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/;
		if(ipAddress != null && ipAddress!="" && !ipre.test(ipAddress))	{
			//alert("IP Address is not valid. Please enter valid data.");
			return false;	
		}
		else
		{
			return true;
		}
}	*/
	
	function popup() {	
		$.fx.speeds._default = 1000;
		document.getElementById("popupfeildMapping").style.visibility = "visible";		
		$( "#popupfeildMapping" ).dialog({
			modal: true,
			autoOpen: false,		
			height: "auto",
			width: 500,		
			buttons:{					
                'Add': function() {

						var name = $('#seacrhbasedn').val();
						
						if(typeof popup.myarray == 'undefined'){							
							popup.myarray  = new Array();
						}
						if(typeof popup.counter == 'undefined'){
							popup.counter = 0;
						}
			      				          		
			      		if(isNull($('#seacrhbasedn').val())){
			     			alert('Atleast one base dn name must be specified.');
			     		}else{	
			     			var i = 0;							
							var flag = true;												 	
							if(document.getElementById('mappingtbl').getElementsByTagName('tr').length >= 2){								
								for(i=0;i<popup.myarray.length;i++){									
									var value = popup.myarray[i];																		
									if(value == name){
										alert("The base dn is already added.");
										flag = false;
										break;
									}
								}
							}								
			         		if(flag){
			         			$("#mappingtbl tr:last").after("<tr><td class='tblfirstcol'>" + name + "<input type='hidden' name = 'dnnameVal' value='" + name + "'/>" +"</td><td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
			         			$('#mappingtbl td img.delete').live('click',function() {

			       				 //var $td= $(this).parents('tr').children('td');			
			       				 //var removalVal = $td.eq(0).text();

			       				 var removalVal = $(this).closest('tr').find('td:eq(0)').text();

			       				 for(var d=0;d<popup.counter;d++){
			       					var currentVal = popup.myarray[d];					
			       					if(currentVal == removalVal){
			       						popup.myarray[d] = '  ';
			       						break;
			       					}
			       				}
			       				 //mainArray[($(this).closest("tr").prevAll("tr").length) - 1] = '  ';
			       				 $(this).parent().parent().remove(); 
			       			  });	          		
				          		popup.myarray[popup.counter] = name;				          		
				          		popup.counter = popup.counter + 1;					          		
				          		$(this).dialog('close');
				         	}	         				    		         			   				         			          				          		
			         	}		         								
                },                
    		    Cancel: function() {
                	$(this).dialog('close');
            	}
	        },
        	open: function() {
            	
        	},
        	close: function() {
        		
        	}				
		});
		$( "#popupfeildMapping" ).dialog("open");
		
	}	
	

	function verifyName() {
		var searchName = document.getElementById("name").value;
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.LDAP_DATASOURCE%>',searchName,'create','','verifyNameDiv');
	}
	setTitle('<bean:message bundle="datasourceResources" key="ldap.ldap"/>');
</script>



<html:form action="/createLDAPDS">
	<html:hidden property="checkAction" />
	<html:hidden property="itemIndex" />

	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header"><bean:message bundle="datasourceResources" key="ldap.createldap" /></td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td valign="middle" colspan="3">
										<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
											<tr>
												<td align="left" class="tblheader-bold" valign="top" colspan="3">
													<bean:message bundle="datasourceResources" key="ldap.ldapdsdetails" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="datasourceResources" key="ldap.name" /> 
													<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.name" header="ldap.name"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.name"/>','<bean:message bundle="datasourceResources" key="ldap.name" />')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top" width="66%">
													<html:text styleId="name" onkeyup="verifyName();" property="name" size="30" tabindex="1" maxlength="60" style="width:250px" />
													<font color="#FF0000"> *</font> 
													<div id="verifyNameDiv" class="labeltext"></div>
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="datasourceResources" key="ldap.address" /> 
													<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.ip" header="ldap.address"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="esi.address"/>','<bean:message bundle="externalsystemResources" key="esi.address"/>')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top" width="60%">
													<html:text styleId="address" property="address" size="40" maxlength="200" style="width:250px" tabindex="2" /> 
													<font color="#FF0000"> *</font> 
													<font color="#999999">Host : Port </font>
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="datasourceResources" key="ldap.administrator" /> 
													<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.administrator" header="ldap.administrator"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.administrator"/>','<bean:message bundle="datasourceResources" key="ldap.administrator" />')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top" width="66%">
													<html:text styleId="administrator" property="administrator" size="20" tabindex="3" maxlength="50" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="datasourceResources" key="ldap.pwd" />
													<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.pwd" header="ldap.pwd"/> 
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.pwd"/>','<bean:message bundle="datasourceResources" key="ldap.pwd" />')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top" width="66%">
													<html:password styleId="pwd" property="password" size="20" tabindex="4" maxlength="50" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="datasourceResources" key="ldap.timeout" />
													<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.timeout" header="ldap.timeout"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.timeout"/>','<bean:message bundle="datasourceResources" key="ldap.timeout" />')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top" width="66%">
													<html:text styleId="timeout" property="timeout" size="20" tabindex="5" maxlength="50" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="datasourceResources" key="ldap.statuscheckduration" /> 
													<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.statuscheckduration" header="ldap.statuscheckduration"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.statuscheckduration"/>','<bean:message bundle="datasourceResources" key="ldap.statuscheckduration" />')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top" width="66%">
													<html:text styleId="statusCheckDuration" property="statusCheckDuration" size="20" tabindex="6" maxlength="50" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="datasourceResources" key="ldap.minimumpool" /> 
													<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.minpool" header="ldap.minimumpool"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.minpool"/>','<bean:message bundle="datasourceResources" key="ldap.minimumpool" />')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top" width="66%">
													<html:text styleId="minimumPool" property="minimumPool" size="20" tabindex="7" maxlength="50" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="datasourceResources" key="ldap.maximumpool" /> 
													<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.maxpool" header="ldap.maximumpool"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.maxpool"/>','<bean:message bundle="datasourceResources" key="ldap.maximumpool" />')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top" width="66%">
													<html:text styleId="maximumPool" property="maximumPool" size="20" tabindex="8" maxlength="50" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="datasourceResources" key="ldap.userdnprefix" />
													<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.dnprefix" header="ldap.userdnprefix"/> 
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.dnprefix"/>','<bean:message bundle="datasourceResources" key="ldap.userdnprefix" />')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top" width="66%">
													<html:text styleId="userDnPrefix" property="userDnPrefix" size="20" tabindex="9" maxlength="50" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="datasourceResources" key="ldap.ldapsizelimit" /> 
													<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.sizelimit" header="ldap.ldapsizelimit"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.sizelimit"/>','<bean:message bundle="datasourceResources" key="ldap.ldapsizelimit" />')" /> --%> 
												</td>
												<td align="left" class="labeltext" valign="top" width="66%">
													<html:text styleId="ldapSizelimit" property="ldapSizeLimit" size="20" tabindex="10" maxlength="50" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="datasourceResources" key="ldap.ldapversion" /> 
													<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.ldapversion" header="ldap.ldapversion"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.ldapversion"/>','<bean:message bundle="datasourceResources" key="ldap.ldapversion" />')" /> --%> 
												</td>
												<td align="left" class="labeltext" valign="top" width="66%">
													<html:select property="ldapVersion" tabindex="11"  style="width:250px" >
														<html:option value="2">2</html:option>
														<html:option value="3">3</html:option>
													</html:select>
												</td>
											</tr>

										</table>
										<table cellpadding="0" cellspacing="0" border="0" width="100%">

											<tr>
												<td align="left" class="tblheader-bold" valign="top" colspan="3">
													<bean:message bundle="datasourceResources" key="ldap.basedndetails" />
												</td>
											</tr>
											<tr>
												<td class="small-gap" colspan="3">&nbsp;</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" colspan="2">
													<input type="button" onclick="popup()" tabindex="12" value=" Add Base Dn" style="size: 140px" class="light-btn"> 
													<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.basedn" header="ldap.searchbasedn"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.basedn"/>','<bean:message bundle="datasourceResources" key="ldap.searchbasedn" />')" /> --%>
												</td>
											</tr>
											<tr>
												<td class="small-gap" colspan="3">&nbsp;</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" colspan="3" valign="top">
													<table width="60%" cellpadding="0" cellspacing="0" border="0" id="mappingtbl">
														<tr>
															<td align="left" class="tblheader" valign="top" width="50%">BaseDn Name</td>
															<td align="left" class="tblheader" valign="top" width="25%">Remove</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr align="center">

												<td class="btns-td" valign="left">
													<input type="button" name="c_btnCreate" tabindex="13" id="c_btnCreate2" value=" Create " class="light-btn" onclick="validateForm()">
													<input type="reset" tabindex="14" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchLDAPDS.do?/>'" value="Cancel" class="light-btn">
												</td>
												<td class="btns-td" valign="left">&nbsp;</td>
												<td class="btns-td" valign="left">&nbsp;</td>
											</tr>
										</table>

									</td>
								</tr>
							</table>

							<div id="popupfeildMapping" style="display: none;" title="LDAP Base DN">

								<table width="100%" cellpadding="0" cellspacing="0" border="0">
									<tr>
										<td align="left" class="labeltext" valign="top" width="30%">
											<bean:message bundle="datasourceResources" key="ldap.searchbasedn" />
										</td>
										<td align="left" class="labeltext" valign="top" width="70%">
											<html:text styleId="seacrhbasedn" property="searcBaseDn" size="20" tabindex="15" maxlength="50" style="width:250px" />
											<font color="#FF0000"> *</font>
										</td>
									</tr>
								</table>
							</div>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>
</html:form>


