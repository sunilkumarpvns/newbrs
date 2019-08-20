<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPBaseDnDetailData"%>
<%@ page import="java.util.List"%>

<%	
	List baseDnDetailList = (List)request.getAttribute("baseDnDetailList");
%>

<script language="javascript1.2" src="<%=basePath%>/js/checkpwdstrength.js" type="text/javascript"></script>

 <script language = "javascript1.2">
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
 		}else if(document.getElementById('mappingtbl').rows.length <= 1){
 	    	alert('Atleast one base dn must be specified.'); 	    	
 		}else{ 	 	
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

	function popup() {	
		$.fx.speeds._default = 1000;
		document.getElementById("popupfeildMapping").style.visibility = "visible";		
		$( "#popupfeildMapping" ).dialog({
			modal: true,
			autoOpen: false,		
			height: 200,
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
			         			$("#mappingtbl tr:last").after("<tr><td class='tblfirstcol'>" + name + "<input type='hidden' name = 'dnnameVal' value='" + name + "'/>" +"</td><td class='tblrows' align='center' ><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
			         			$('#mappingtbl td img.delete').on('click',function() {

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
	
function verifyFormat (){
	var searchName = document.getElementById("name").value;
	callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.LDAP_DATASOURCE%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}
function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.LDAP_DATASOURCE%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}	
$(document).ready(function(){
	setTitle('<bean:message bundle="datasourceResources" key="ldap.ldap" />');
	$("#name").focus();
});

	
</script>



<html:form action="/createLDAPDS">
	<html:hidden property="checkAction"/>
 	<html:hidden property="itemIndex" />

	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
		<tr>
			<td width="10">&nbsp;</td>
			<td class="box" cellpadding="0" cellspacing="0" border="0"
				width="100%">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td class="table-header" colspan="5">
					<bean:message bundle="datasourceResources" key="ldap.createldap" /></td>
				</tr>
				<tr>
					<td class="small-gap" colspan="3">&nbsp;</td>
				</tr>
				<tr>
					<td class="btns-td" valign="middle" colspan="3">
					<table cellpadding="0" cellspacing="0" border="0" width="100%" 
					       height="30%">
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="datasourceResources" key="ldap.ldapdsdetails" /></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%">
							<bean:message bundle="datasourceResources" key="ldap.name" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="datasourceResources" key="ldap.name"/>','<bean:message bundle="datasourceResources" key="ldap.name" />')"/>
							</td>
									
							<sm:nvNameField maxLength="60" size="30" />					
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%">
								<bean:message bundle="datasourceResources" key="ldap.address" /> 
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldap.address"/>','<bean:message bundle="datasourceResources" key="ldap.address" />')" />
							</td>
							<td align="left" class="labeltext" valign="top" width="66%">
								<html:text styleId="address" property="address" maxlength="256" size="30" tabindex="2" /> <font color="#FF0000"> *</font>
								<font color="#999999"> Host : Port </font>
							</td>
						</tr>
						

						<tr>
							<td align="left" class="labeltext" valign="top" width="18%">
							<bean:message bundle="datasourceResources" key="ldap.timeout" /></td>
							<td align="left" class="labeltext" valign="top" width="66%">
								<html:text styleId="timeout" property="timeout" size="20"
								maxlength="50" tabindex="4" /><font color="#FF0000"> *</font>
							</td>							
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%">
							<bean:message bundle="datasourceResources" key="ldap.statuscheckduration" /></td>
							<td align="left" class="labeltext" valign="top" width="66%">
								<html:text styleId="statusCheckDuration" property="statusCheckDuration" size="20"
								maxlength="50" tabindex="5" /><font color="#FF0000"> *</font>
							</td>							
						</tr>
						
						
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%">
							<bean:message bundle="datasourceResources" key="ldap.ldapsizelimit" /></td>
							<td align="left" class="labeltext" valign="top" width="66%">
								<html:text styleId="ldapSizelimit" property="ldapSizeLimit" size="20"
								maxlength="50" tabindex="6" /><font color="#FF0000"> *</font>
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%">
							<bean:message bundle="datasourceResources" key="ldap.administrator" /></td>
							<td align="left" class="labeltext" valign="top" width="66%">
								<html:text styleId="administrator" property="administrator" size="20"
								maxlength="50" tabindex="7" /><font color="#FF0000"> *</font>
							</td>							
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%">
							<bean:message bundle="datasourceResources" key="ldap.pwd" /></td>
							<td align="left" class="labeltext" valign="top" width="66%">
								<html:password styleId="pwd" property="password" size="20"
								maxlength="50" tabindex="8" /><font color="#FF0000"> *</font>
							</td>							
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%">
							<bean:message bundle="datasourceResources" key="ldap.userdnprefix" /></td>
							<td align="left" class="labeltext" valign="top" width="66%">
								<html:text styleId="userDnPrefix" property="userDnPrefix" size="20"
								maxlength="50" tabindex="9" /><font color="#FF0000"> *</font>
							</td>							
						</tr>						
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%">
							<bean:message bundle="datasourceResources" key="ldap.minimumpool" /></td>
							<td align="left" class="labeltext" valign="top" width="66%">
								<html:text styleId="minimumPool" property="minimumPool" size="20"
								maxlength="50" tabindex="10" /><font color="#FF0000"> *</font>
							</td>							
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%">
							<bean:message bundle="datasourceResources" key="ldap.maximumpool" /></td>
							<td align="left" class="labeltext" valign="top" width="66%">
								<html:text styleId="maximumPool" property="maximumPool" size="20"
								maxlength="50" tabindex="11" /><font color="#FF0000"> *</font>
							</td>							
						</tr>	
						
										
					</table>
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
					
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
						<bean:message bundle="datasourceResources" key="ldap.basedndetails" /></td>
					</tr>
					<tr>
						<td class="small-gap" colspan="3">&nbsp;</td>
					</tr>					
                    <tr>                       
                       <td align="left" class="labeltext" valign="top" colspan="2" > 
                       <input type="button" onclick="popup()"  value=" Add Base Dn" style="size: 140px" class="light-btn" tabindex="12"> </td>
                    </tr> 
                    <tr>
						<td class="small-gap" colspan="3">&nbsp;</td>
					</tr>
                    <tr>	
					   <td align="left" class="labeltext" colspan="3" valign="top">
						<table width="60%" cellpadding="0" cellspacing="0" border="0" id="mappingtbl">
							<tr>								
								<td align="left" class="tblheader" valign="top" width="50%" >BaseDn Name</td>								
								<td align="center" class="tblheader" valign="top" width="15%">Remove</td>  								  
							</tr>
					  	</table>
					   </td>	
					  </tr>			
                      <tr>
							<td class="btns-td" valign="middle">&nbsp;</td>
							<td class="btns-td" valign="middle" colspan="2">
							<input type="button" name="c_btnCreate" 
								id="c_btnCreate2" value=" Create " class="light-btn" onclick="validateForm()" tabindex="13"> 
							<input type="reset" tabindex="14" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchLDAPDS.do?/>'"
								value="Cancel" class="light-btn">
							</td>
						</tr>	
					</table>
					
					</td>
				</tr></table>
			<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
	</table>
	
	<div id="popupfeildMapping" style="display: none;">
	
	<table>
					<tr>
						<td align="left" class="labeltext" valign="top" width="18%">
							<bean:message bundle="datasourceResources" key="ldap.searchbasedn" /></td>
						<td align="left" class="labeltext" valign="top" width="66%">
								<html:text styleId="seacrhbasedn" property="searcBaseDn" size="20"
								maxlength="50" /><font color="#FF0000"> *</font>
						</td>							
					</tr>
			
	</table>
	
	
	
	</div>
	
	
</html:form>
			
