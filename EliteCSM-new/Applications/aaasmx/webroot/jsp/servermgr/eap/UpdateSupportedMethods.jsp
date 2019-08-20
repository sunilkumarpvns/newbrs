
<%@ include file="/jsp/core/includes/common/Header.jsp"%>





<%@page import="com.elitecore.elitesm.web.servermgr.eap.forms.UpdateSupportedMethodsForm"%>
<%
    
   UpdateSupportedMethodsForm updateSupportedMethodsForm=(UpdateSupportedMethodsForm)request.getAttribute("updateSupportedMethodsForm");

%>



<script>

function validateUpdate()
{

	if(document.forms[0].defaultNegiotationMethod.value =='13'){
        
        if(document.forms[0].tlsBool.checked==false)
	     {
            alert("Please Select TLS Enabled Auth Method.");
            return;
        }   
	              
    }else if(document.forms[0].defaultNegiotationMethod.value =='21'){
        
    	if(document.forms[0].ttlsBool.checked==false){   
            alert("Please Select TTLS Enabled Auth Method.");
            return;
        }
        
    }else if(document.forms[0].defaultNegiotationMethod.value =='25'){

      	 if(document.forms[0].peapBool.checked==false){   
              alert("Please Select PEAP Enabled Auth Method.");
              return;
         }
           
    }else if(document.forms[0].defaultNegiotationMethod.value =='18'){

	   	 if(document.forms[0].simBool.checked==false){
            alert("Please Select SIM Enabled Auth Method.");
            return;
    	 }
        
    }else if(document.forms[0].defaultNegiotationMethod.value =='23'){

	   	 if(document.forms[0].akaBool.checked==false){
           alert("Please Select AKA Enabled Auth Method.");
           return;
   	 }
       
   }else if(document.forms[0].defaultNegiotationMethod.value =='26'){

   	 	if(!document.forms[0].mschapv2Bool.checked){
            alert("Please Select MS-CHAPv2 Enabled Auth Method.");
            return;
        }
        
    }else if(document.forms[0].defaultNegiotationMethod.value =='6'){

	   	 if(!document.forms[0].gtcBool.checked){
            alert("Please Select GTC Enabled Auth Method.");
            return;
         }
    }else if(document.forms[0].defaultNegiotationMethod.value =='4'){
	   	 if(!document.forms[0].md5Bool.checked){
	         alert("Please Select MD5 Enabled Auth Method.");
	         return;
	     }             
 	} else if(document.forms[0].defaultNegiotationMethod.value =='50'){
	   	 if(!document.forms[0].akaPrimeBool.checked){
	         alert("Please Select AKA' Enabled Auth Method.");
	         return;
	     }             
 	} 

    document.forms[0].action.value="Update";
    document.forms[0].submit();
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

function confirmTls(){

  if(!document.forms[0].tlsBool.checked){

	  var confirmState = confirm("TLS Configuration will be deleted. Would you like to continue?");
		if(confirmState){
			
			document.forms[0].tlsBool.checked=false;
			return;
		}else{
			
			document.forms[0].tlsBool.checked=true;
			return;
		}
						
  }	  

	
}

function confirmTtls(){

	  if(document.forms[0].ttlsBool.checked==false){

		  var confirmState = confirm("TTLS Configuration will be deleted. Would you like to continue?");
			if(confirmState){
				
				document.forms[0].ttlsBool.checked=false;
				return;
			}else{
				
				document.forms[0].ttlsBool.checked=true;
				return;
			}
							
	  }	  

		
	}
function confirmPeap(){

	  if(document.forms[0].peapBool.checked==false){

		  var confirmState = confirm("PEAP Configuration will be deleted. Would you like to continue?");
		  
			if(confirmState){ 
				
				document.forms[0].peapBool.checked=false;
				return;
			}else{
				
				document.forms[0].peapBool.checked=true;
				return;
			}
							
	  }	  

		
	}

function confirmSim(){
	 if(document.forms[0].simBool.checked==false){

		  var confirmState = confirm("Sim Configuration will be deleted. Would you like to continue?");
		  
			if(confirmState){ 
				
				document.forms[0].simBool.checked=false;
				return;
			}else{
				
				document.forms[0].simBool.checked=true;
				return;
			}
							
	  }	
}
function confirmAka(){
	 if(document.forms[0].akaBool.checked==false){

		  var confirmState = confirm("Aka Configuration will be deleted. Would you like to continue?");
		  
			if(confirmState){ 
				
				document.forms[0].akaBool.checked=false;
				return;
			}else{
				
				document.forms[0].akaBool.checked=true;
				return;
			}
							
	  }	
}
function confirmAkaPrime(){
	 if(document.forms[0].akaPrimeBool.checked==false){

		  var confirmState = confirm("Aka' Configuration will be deleted. Would you like to continue?");
		  
			if(confirmState){ 
				
				document.forms[0].akaPrimeBool.checked=false;
				return;
			}else{
				
				document.forms[0].akaPrimeBool.checked=true;
				return;
			}
							
	  }	
}

</script>

<html:form action="/updateSupportedMethods">

<html:hidden name="updateSupportedMethodsForm" styleId="action" property="action" value="update"/>
<html:hidden name="updateSupportedMethodsForm" styleId="eapId" property="eapId"/>
<html:hidden name="updateSupportedMethodsForm" styleId="auditUId" property="auditUId"/>
		<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
						        <tr>
									<td class="tblheader-bold" colspan="2" >
										<bean:message bundle="servermgrResources" key="servermgr.eapconfig.updatesupportedmethods"/>
									</td>
						       </tr>
						       <tr > 
						<td class="small-gap" colspan="3" >&nbsp;</td>
			    </tr>
			    <tr>
			    <td class="small-gap">&nbsp;</td>
			    </tr>
			    <tr> 
					   <td align="left" class="captiontext" valign="top" width="30%" >
					   		<bean:message bundle="servermgrResources" 
					   			key="servermgr.eapconfig.defaultnegiotationmethod"/>
					     			<ec:elitehelp headerBundle="servermgrResources" 
					     				text="eapconfig.negotiationmethod" 
					     					header="servermgr.eapconfig.defaultnegiotationmethod"/>
					   </td>
					   <td align="left" class="labeltext" valign="top" colspan="2" > 
						<html:select name="updateSupportedMethodsForm" styleId="defaultNegiotationMethod" property="defaultNegiotationMethod" size="1" tabindex="1">
						   <html:option value="4" >MD5-Challenge</html:option>
						   <html:option value="6" >GTC</html:option>
						   <html:option value="13" >TLS</html:option>
						   <html:option value="18" >SIM</html:option>
						    <html:option value="21" >TTLS</html:option>
						    <html:option value="23" >AKA</html:option>
						      <html:option value="50" >AKA'</html:option>
						    <html:option value="25" >PEAP</html:option> 
						   <html:option value="26" >MS-CHAPv2</html:option>
		
						</html:select>
					  </td>
		  		</tr>
		  		
		  		<tr>
			   		  <td align="left" class="labeltext" valign="top" >&nbsp;</td>
					  <td align="left" class="labeltext" valign="top" >&nbsp;</td>
			    </tr>
		  		
			    <tr>
		  		    <td align="left" class="captiontext" valign="top" width="25%" >
		  		    	<bean:message bundle="servermgrResources" 
		  		    		key="servermgr.eapconfig.enabledauthmethod"/>
		  		     			<ec:elitehelp headerBundle="servermgrResources" 
		  		     				text="eapconfig.authmethods" 
		  		     					header="servermgr.eapconfig.enabledauthmethod"/>
		  		     </td>
		  		    <td align="left" class="labeltext" valign="top"> 
				         
                         <html:checkbox property="md5Bool"  styleId="md5Bool" tabindex="2" ></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.md5"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        
                         <html:checkbox property="gtcBool"  styleId="gtcBool" tabindex="3"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.gtc"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                       
                         <html:checkbox property="tlsBool"  styleId="tlsBool" onchange="confirmTls();" tabindex="4"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.tls"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					  
                         <html:checkbox property="ttlsBool"  styleId="ttlsBool" onchange="confirmTtls();" tabindex="6"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.ttls"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                         
                         <html:checkbox property="peapBool"  styleId="peapBool" onchange="confirmPeap();" tabindex="8"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.peap"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                       
					     <html:checkbox property="simBool"  styleId="simBool" onchange="confirmSim();" tabindex="5"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.sim"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                         
                         <html:checkbox property="akaBool"  styleId="akaBool" onchange="confirmAka();" tabindex="7"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.aka"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                         
                          <html:checkbox property="akaPrimeBool"  styleId="akaPrimeBool" onchange="confirmAkaPrime();" tabindex="7"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.akaprime"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                         
                          </td>
		  		</tr>
		  		<tr>
		  		 <td align="left" class="labeltext" valign="top" width="25%" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			     <td align="left" class="labeltext" valign="top">
		  		 <html:checkbox property="mschapv2Bool"  styleId="mschapv2Bool" tabindex="9"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.mschapv2"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                  </td>       
		  		
		  		</tr>
		  		
				 <tr>
			   		  <td align="left" class="labeltext" valign="top" >&nbsp;</td>
					  <td align="left" class="labeltext" valign="top" colspan="2" >&nbsp;</td>
			    </tr>
			    <tr>
			     	  <td class="small-gap" width="16%" >&nbsp;</td>
		     	      <td align="left" class="labeltext" valign="top" >
		     	        <input type="button" name="c_btnUpdate"   onclick="validateUpdate()"  value=" Update "  class="light-btn" tabindex="10"/>
		     	        <input type="button" name="c_btnCancel" tabindex="11" onclick="javascript:location.href='<%=basePath%>/viewEAPConfig.do?viewType=basic&eapId=<%=updateSupportedMethodsForm.getEapId()%>'" value="   Cancel   "	class="light-btn" />
					  </td>
			    </tr>
			    
			    <tr>
			   		  <td align="left" class="labeltext" valign="top" >&nbsp;</td>
					  <td align="left" class="labeltext" valign="top" colspan="2" >&nbsp;</td>
			    </tr>
			</table>
		
						
</html:form>

<script>

</script>