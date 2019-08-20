<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPBaseDnDetailData"%>
<%@page import="com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPDatasourceData"%>

<% 
	LDAPDatasourceData ldapData = (LDAPDatasourceData)request.getSession().getAttribute("ldapDatasourceData");
%>

<%
    String localBasePath = request.getContextPath();	
	String[] searchdns = (String[])session.getAttribute("searchdns"); 
%>

<script language="javascript1.2" src="<%=localBasePath%>/js/checkpwdstrength.js" type="text/javascript"></script>
<script language="javascript1.2" src="<%=localBasePath%>/js/checkpwdstrength.js" type="text/javascript"></script>
<script language="javascript">
 var isValidName;
	
 	var mainArray = new Array();
 	var count = 0;

	function validateUpdate(){
				
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
		}else if(isNull(document.forms[0].ldapSizeLimit.value)){
			alert('LDAP Size Limit must be specified');
		}else if(isNull(document.forms[0].administrator.value)){
			alert('Administrator must be specified');
		}else if(isNull(document.forms[0].password.value)){
			alert('Password must be specified');
		}else if(isNull(document.forms[0].userDnprefix.value)){
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
 	    }else if(minPoolSize > maxPoolSize){
 	    	alert('Minimum Pool value must be less than Maximum Pool value');
 	    	document.forms[0].minimumPool.focus();
 		}else if((isNull(document.forms[0].statusCheckDuration.value)) ||(!(isNumber(document.forms[0].statusCheckDuration.value)))){
  	       alert('Status Check Duration must be Numeric');
 	       document.forms[0].statusCheckDuration.focus();
 	    }else if (!isNull(document.forms[0].ldapVersion.value) && (!isNumber(document.forms[0].ldapVersion.value)) ){
 			alert('LDAP Version must be Numeric.');
 			document.forms[0].ldapVersion.focus();
 	    }else if(document.getElementById('mappingtbl').getElementsByTagName('tr').length < 2){
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
	function isNumber(val){
		nre= /^\d+$/;
		var regexp = new RegExp(nre);
		if(!regexp.test(val))
		{
			return false;
		}
		return true;
	}

	/*function validateHostName(hostName) {
		var validHostnameRegex = /^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\-]*[A-Za-z0-9])$/;
		if(hostName != null && hostName!="" && !validHostnameRegex.test(hostName)){
			return false;	
		}else{
			return true;
		}
	}*/
	
	$(document).ready(function() {		
		var searchBaseDn =  new Array(<%=searchdns.length%>)		
		var length = searchBaseDn.length;
			
		<%int j =0;				
		for(j =0;j<searchdns.length;j++){%>								
			searchBaseDn[<%=j%>] = '<%=searchdns[j]%>';					

			var dnname = searchBaseDn[<%=j%>];
			mainArray[count++] = dnname;
			
			$("#mappingtbl tr:last").after("<tr><td class='tblfirstcol'>" + dnname + "<input type='hidden' name = 'dnname' value='" + dnname + "'/>" +"</td><td class='tblrows'><img src='<%=localBasePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
			$('#mappingtbl td img.delete').live('click',function() {

				 var $td= $(this).parents('tr').children('td');			
				 var removalVal = $td.eq(0).text();

				for(var d=0;d<count;d++){
					var currentVal = mainArray[d];					
					if(currentVal == removalVal){
						mainArray[d] = '  ';
						break;
					}
				}
				 //mainArray[($(this).closest("tr").prevAll("tr").length) - 1] = '  ';
				 $(this).parent().parent().remove(); });
			
		<%}%>		  		
	   }
    );



	function openPopup(){	
		$.fx.speeds._default = 1000;
		document.getElementById("popupdiv").style.visibility = "visible";		
		$( "#popupdiv" ).dialog({
			modal: true,
			autoOpen: false,		
			height: "auto",
			width: 500,		
			buttons:{					
                'Add': function() {	
											
		          		var name = $('#searchBaseDn').val();		          		
		          				          		          	
		          		if(isNull($('#searchBaseDn').val())){
		         			alert('Search Base Dn must be specified.');
		         		}else{	
			     			var i = 0;							
							var flag = true;												 	
							if(document.getElementById('mappingtbl').getElementsByTagName('tr').length >= 2){								
								for(i=0;i<mainArray.length;i++){																
									var value = mainArray[i];																								
									if(value == name){
										alert("Mapping with this value is already present so add another value for attribute");
										flag = false;
										break;
									}
								}
							}								
			         		if(flag){
			         			$("#mappingtbl tr:last").after("<tr><td class='tblfirstcol'>" + name + "<input type='hidden' name = 'dnname' value='" + name + "'/>" +"</td><td class='tblrows'><img src='<%=localBasePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
								mainArray[count++] = name;			         							          					          						          						          	          	
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
		$( "#popupdiv" ).dialog("open");
	}

	function verifyName() {
		var searchName = document.getElementById("name").value;
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.LDAP_DATASOURCE%>',searchName,'update','<%=ldapData.getLdapDsId()%>','verifyNameDiv');
	}
</script>
<html:form action="/update">

	<html:hidden property="checkAction" />
	<html:hidden property="itemIndex" />
	<html:hidden property="searchDn" />
	<html:hidden styleId="auditUId" property="auditUId" />
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right">
				<table cellpadding="0" cellspacing="0" border="0" width="100%"
					height="15%">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="4">
							<bean:message bundle="datasourceResources" key="ldap.details" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="datasourceResources" key="ldap.name" /> 
							<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.name" header="ldap.name"/>
							<%-- <img src="<%=localBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.name"/>','<bean:message bundle="datasourceResources" key="ldap.name" />')" /> --%>
						</td>
						<td align="left" valign="top" width="75%">
							<html:text styleId="name" tabindex="1" onkeyup="verifyName();" property="name" size="40" maxlength="30" style="width:250px" />
							<font color="#FF0000"> *</font>
							<div id="verifyNameDiv" class="labeltext"></div>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="datasourceResources" key="ldap.address" />
							<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.ip" header="ldap.address"/>
							<%-- <img src="<%=localBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="esi.address"/>','<bean:message bundle="externalsystemResources" key="esi.address"/>')" /> --%>
						</td>
						<td align="left" class="labeltext" valign="top" width="60%">
							<html:text styleId="address" tabindex="2" property="address" size="40" maxlength="200" style="width:250px" /> 
							<font color="#FF0000"> *</font> <font color="#999999"> Host :Port </font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="datasourceResources" key="ldap.administrator" /> 
							<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.administrator" header="ldap.administrator"/>
							<%-- <img src="<%=localBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.administrator"/>','<bean:message bundle="datasourceResources" key="ldap.administrator" />')" /> --%>
						</td>
						<td align="left" valign="top" width="75%">
							<html:text styleId="administrator" tabindex="3" property="administrator" size="40" maxlength="30" style="width:250px" />
							<font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="datasourceResources" key="ldap.pwd" /> 
							<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.pwd" header="ldap.pwd"/>
							<%-- <img src="<%=localBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.pwd"/>','<bean:message bundle="datasourceResources" key="ldap.pwd" />')" /> --%>
						</td>
						<td align="left" valign="top" width="75%">
							<html:password styleId="password" tabindex="4" property="password" size="40" maxlength="30" style="width:250px" />
							<font color="#FF0000">*</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="datasourceResources" key="ldap.timeout" />
							<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.timeout" header="ldap.timeout"/>
							<%-- <img src="<%=localBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.timeout"/>','<bean:message bundle="datasourceResources" key="ldap.timeout" />')" /> --%>
						</td>
						<td align="left" valign="top" width="75%">
							<html:text styleId="timeout" tabindex="5" property="timeout" size="40" maxlength="30" style="width:250px" />
							<font color="#FF0000">*</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="datasourceResources" key="ldap.statuscheckduration" /> 
							<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.statuscheckduration" header="ldap.statuscheckduration"/>
							<%-- <img src="<%=localBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.statuscheckduration"/>','<bean:message bundle="datasourceResources" key="ldap.statuscheckduration" />')" /> --%>
						</td>
						<td align="left" valign="top" width="70%">
							<html:text styleId="statusCheckDuration" tabindex="6" property="statusCheckDuration" size="40" maxlength="30" style="width:250px" /><font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="datasourceResources" key="ldap.minimumpool" />
							<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.minpool" header="ldap.minimumpool"/>
							<%-- <img src="<%=localBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.minpool"/>','<bean:message bundle="datasourceResources" key="ldap.minimumpool" />')" /> --%>
						</td>
						<td align="left" valign="top" width="75%">
							<html:text styleId="minimumpool" tabindex="7" property="minimumPool" size="40" maxlength="30" style="width:250px" />
							<font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="datasourceResources" key="ldap.maximumpool" />
							<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.maxpool" header="ldap.maximumpool"/>
							<%-- <img src="<%=localBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.maxpool"/>','<bean:message bundle="datasourceResources" key="ldap.maximumpool" />')" /> --%>
						</td>
						<td align="left" valign="top" width="75%">
							<html:text styleId="maximumpool" tabindex="8" property="maximumPool" size="40" maxlength="30" style="width:250px" />
							<font color="#FF0000"> *</font></td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="datasourceResources" key="ldap.userdnprefix" />
							<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.dnprefix" header="ldap.userdnprefix"/> 
							<%-- <img src="<%=localBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.dnprefix"/>','<bean:message bundle="datasourceResources" key="ldap.userdnprefix" />')" /> --%>
						</td>
						<td align="left" valign="top" width="75%">
							<html:text styleId="userDnprefix" tabindex="9" property="userDNPrefix" size="40" maxlength="30" style="width:250px" />
							<font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="datasourceResources" key="ldap.ldapsizelimit" /> 
							<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.sizelimit" header="ldap.ldapsizelimit"/>
							<%-- <img src="<%=localBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.sizelimit"/>','<bean:message bundle="datasourceResources" key="ldap.ldapsizelimit" />')" /> --%>
						</td>
						<td align="left" valign="top" width="75%">
							<html:text styleId="ldapSizeLimit" tabindex="10" property="ldapSizeLimit" size="40" maxlength="30" style="width:250px" />
							<font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="datasourceResources" key="ldap.ldapversion" /> 
							<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.ldapversion" header="ldap.ldapversion"/>
							<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.ldapversion"/>','<bean:message bundle="datasourceResources" key="ldap.ldapversion" />')" /> --%>
						</td>
						<td align="left" valign="top" width="75%">
							<html:select property="ldapVersion" tabindex="11"  style="width:250px" >
								<html:option value="2">2</html:option>
								<html:option value="3">3</html:option>
							</html:select>
						</td>
					</tr>

					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="4">
							<bean:message bundle="datasourceResources" key="ldap.basedndetails" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" colspan="2">
							<input type="button" tabindex="12" onclick="openPopup()" style="width: 140px;" value=" Add DN" align="left" class="light-btn"> 
							<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.basedn" header="ldap.searchbasedn"/>
							<%-- <img src="<%=localBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldapdatasource.basedn"/>','Search Base DN')" /> --%>
						</td>
					</tr>
					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td align="left" class="labeltext" colspan="3" valign="top">
							<table width="60%" cellpadding="0" cellspacing="0" border="0" id="mappingtbl" class="captiontext">
								<tr>
									<td align="left" class="tblheader" valign="top" width="90%">BaseDn Name</td>
									<td align="left" class="tblheader" valign="top" width="10%">Remove</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle">
							<input type="button" name="c_btnCreate" tabindex="13" onclick="validateUpdate()" value="Update" class="light-btn"> 
							<input type="reset" name="c_btnDeletePolicy" tabindex="14" onclick="javascript:location.href='<%=localBasePath%>/initSearchLDAPDS.do?/>'" value="Cancel" class="light-btn">
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>

	<div id="popupdiv" style="display: none;">
		<table>
			<tr>
				<td align="left" class="labeltext" valign="top" width="25%">
					<bean:message bundle="datasourceResources" key="ldap.searchbasedn" />
				</td>
				<td align="left" align="top" width="75%" class="labeltext">
					<html:text styleId="searchBaseDn" property="searchBaseDn" tabindex="15" />
					<font color="#FF0000"> *</font>
				</td>
			</tr>
		</table>
	</div>

</html:form>