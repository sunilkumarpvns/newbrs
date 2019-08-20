<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPBaseDnDetailData"%>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData"%>

<%
    String localBasePath = request.getContextPath();	
	String[] searchdns = (String[])session.getAttribute("searchdns");
 
	LDAPDatasourceData ldapData = (LDAPDatasourceData)request.getSession().getAttribute("ldapDatasourceData");
%>

<script language="javascript1.2" src="<%=localBasePath%>/js/checkpwdstrength.js" type="text/javascript"></script>
<script language="javascript1.2" src="<%=localBasePath%>/js/checkpwdstrength.js" type="text/javascript"></script>
 <script language = "javascript">
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
 	    }else if(document.getElementById('mappingtbl').getElementsByTagName('tr').length < 2){
 			alert('Atleast one DN must be specified.'); 			
 	    }
		else{ 	 			
			document.forms[0].checkAction.value = 'create';
			document.forms[0].submit();
		}	
		
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

	$(document).ready(function() {
		verifyName();
		$("#name").focus();
		var searchBaseDn =  new Array(<%=searchdns.length%>)		
		var length = searchBaseDn.length;
			
		<%int j =0;				
		for(j =0;j<searchdns.length;j++){%>								
			searchBaseDn[<%=j%>] = '<%=searchdns[j]%>';					

			var dnname = searchBaseDn[<%=j%>];
			mainArray[count++] = dnname;
			
			$("#mappingtbl tr:last").after("<tr><td class='tblfirstcol'>" + dnname + "<input type='hidden' name = 'dnname' value='" + dnname + "'/>" +"</td><td class='tblrows' align='center' ><img src='<%=localBasePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
			$('#mappingtbl td img.delete').on('click',function() {

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
			height: 200,
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
			         			$("#mappingtbl tr:last").after("<tr><td class='tblfirstcol'>" + name + "<input type='hidden' name = 'dnname' value='" + name + "'/>" +"</td><td class='tblrows' align='center' ><img src='<%=localBasePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
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
function verifyFormat (){
	var searchName = document.getElementById("name").value;
	callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.LDAP_DATASOURCE%>',searchName:searchName,mode:'update',id:'<%=ldapData.getLdapDsId()%>'},'verifyNameDiv');
}
function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.LDAP_DATASOURCE%>',searchName:searchName,mode:'update',id:'<%=ldapData.getLdapDsId()%>'},'verifyNameDiv');
}	 
</script>
<html:form action="/update" >

	<html:hidden property="checkAction"/>
 	<html:hidden property="itemIndex" />
 	<html:hidden property="searchDn" />
				<table cellpadding="0" cellspacing="0" border="0" width="100%" >
			        <tr>
						<td valign="top" align="right"> 
				 		 	<table cellpadding="0" cellspacing="0" border="0" width="97%" height="15%" > 
								 <tr > 
						              <td align="left" class="tblheader-bold" valign="top" colspan="4">
						              <bean:message bundle="datasourceResources" key="ldap.details"/></td>
					             </tr>
					             <tr > 
						              <td align="left" class="labeltext" valign="top" width="25%" >
						              <bean:message bundle="datasourceResources" key="ldap.name" />
						              <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="datasourceResources" key="ldap.name"/>','<bean:message bundle="datasourceResources" key="ldap.name" />')"/>
						              </td>
				  	                  <sm:nvNameField maxLength="60" size="40" value="<%=ldapData.getName()%>"/> 		
				 				 </tr>
				 				 <tr> 
					                 <td align="left" class="labeltext" valign="top" width="25%" >
					                 <bean:message bundle="datasourceResources" key="ldap.address" /></td>
					                     <td align="left" align="top" width="75%" > 
					                  <html:text styleId="address" property="address" size="40" maxlength="256" tabindex="2"/><font color="#FF0000"> *</font>
					                  </td>
	           					 </tr>
					              <tr > 
						              <td align="left" class="labeltext" valign="top" width="25%" >
						              <bean:message bundle="datasourceResources" key="ldap.timeout" /></td>
						                  <td align="left" align="top" width="75%"> 
						              <html:text styleId="timeout" property="timeout" size="40" maxlength="30" tabindex="4"/><font color="#FF0000"> *</font>
						              </td>   
	            				 </tr> 
	            				 <tr>
								<td align="left" class="labeltext" valign="top" width="30%">
									<bean:message bundle="datasourceResources" key="ldap.statuscheckduration" /></td>
								<td align="left" class="labeltext" valign="top" width="70%">
									<html:text styleId="statusCheckDuration" property="statusCheckDuration" size="40"
									maxlength="30" tabindex="5" /><font color="#FF0000"> *</font>
								</td>							
							   </tr>
	            				 <tr > 
						             <td align="left" class="labeltext" valign="top" width="25%" >
						             <bean:message bundle="datasourceResources" key="ldap.administrator" /></td>
						             <td align="left" align="top" width="75%"> 
						             <html:text styleId="administrator" property="administrator" size="40" maxlength="30" tabindex="6"/><font color="#FF0000"> *</font>
						             </td> 
					             </tr>
	            				 <tr > 
						            <td align="left" class="labeltext" valign="top" width="25%" >
						             <bean:message bundle="datasourceResources" key="ldap.pwd" /></td>
						              <td align="left" align="top" width="75%"> 
						             <html:password styleId="password" property="password" size="40" maxlength="30" tabindex="7"/><font color="#FF0000"> *</font>
						             </td> 
	            				 </tr>
	            				  <tr > 
						           <td align="left" class="labeltext" valign="top" width="25%" >
						            <bean:message bundle="datasourceResources" key="ldap.userdnprefix" /></td>
						            <td align="left" align="top" width="75%"> 
						             <html:text styleId="userDnprefix" property="userDNPrefix" size="40" maxlength="30" tabindex="8"/><font color="#FF0000"> *</font>
						             </td> 
	            				 </tr>
	            				  <tr> 
						               <td align="left" class="labeltext" valign="top" width="25%" >
						            <bean:message bundle="datasourceResources" key="ldap.ldapsizelimit" /></td>
						            <td align="left" align="top" width="75%"> 
						             <html:text styleId="ldapSizeLimit" property="ldapSizeLimit" size="40" maxlength="30" tabindex="9"/><font color="#FF0000"> *</font>
						             </td>  
	            				 </tr>
	            				  <tr> 
						             <td align="left" class="labeltext" valign="top" width="25%" >
						           <bean:message bundle="datasourceResources" key="ldap.minimumpool" /></td>
						           <td align="left" align="top" width="75%"> 
						             <html:text styleId="minimumpool" property="minimumPool" size="40" maxlength="30" tabindex="10"/><font color="#FF0000"> *</font>
						             </td>   
	            				 </tr>
	            				 <tr> 
						           <td align="left" class="labeltext" valign="top" width="25%" >
						           <bean:message bundle="datasourceResources" key="ldap.maximumpool" /></td>
						           <td align="left" align="top" width="75%"> 
						             <html:text styleId="maximumpool" property="maximumPool" size="40" maxlength="30" tabindex="11"/><font color="#FF0000"> *</font>
						             </td>  
	           					 </tr> 
	           					 <tr>
									<td  colspan="3">&nbsp;</td>
								</tr>
	           					 <tr > 
						              <td align="left" class="tblheader-bold" valign="top" colspan="4">
						              <bean:message bundle="datasourceResources" key="ldap.basedndetails"/></td>
					             </tr>					                      				
                   				<tr>                        				
                       				<td align="left" class="labeltext"  colspan="2" > 
                       				<input type="button" onclick="openPopup()" style="width: 140px;" value=" Add DN" align="left" class="light-btn" tabindex="12"> </td>
                         	   </tr>      
                         	                       	   	
                    			<tr>	
					 			  <td align="left" class="labeltext" colspan="3" valign="top">
						<table width="60%" cellpadding="0" cellspacing="0" border="0" id="mappingtbl" align="left">
							<tr>								
								<td align="left" class="tblheader" valign="top" width="90%" >BaseDn Name</td>								
								<td align="center" class="tblheader" valign="top" width="10%">Remove</td>  								  
							</tr>							
					  </table>
					   </td>	
					  </tr>	
                   				 <tr>						   
					 				<td class="btns-td" valign="middle" >&nbsp;</td>
				       				<td class="btns-td" valign="middle" >
				           				 <input type="button" name="c_btnCreate"  onclick="validateUpdate()"  value="Update"  class="light-btn" tabindex="13">                   
				            			 <input type="reset" tabindex="14" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=localBasePath%>/initSearchLDAPDS.do?/>'"  value="Cancel" class="light-btn"> 
					               </td>
                				</tr>
			       		</table>
 			     	  </td>  
			    	</tr>  
			     </table>
			     
<div id="popupdiv" style="display: none;">
	<table>
		<tr> 
			<td align="left" class="labeltext" valign="top" width="25%" >
				<bean:message bundle="datasourceResources" key="ldap.searchbasedn" />
			</td>
			<td align="left" align="top" width="75%"  class="labeltext"> 
				<html:text styleId="searchBaseDn" property="searchBaseDn" /><font color="#FF0000"> *</font>
			</td>  
	     </tr>
	</table> 						           
</div>			     
			     
</html:form>
<%@ include file="/jsp/core/includes/common/Footer.jsp" %>	
