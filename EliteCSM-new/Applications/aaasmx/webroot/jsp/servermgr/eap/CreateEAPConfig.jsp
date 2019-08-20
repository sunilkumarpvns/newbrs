<%@page import="com.elitecore.elitesm.util.constants.EAPConfigConstant"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.web.servermgr.eap.forms.*" %>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
	String basePath = request.getContextPath();
    String strMd5=(String)request.getParameter("md5");
	String strGTC=(String)request.getParameter("GTC");
	String strTls=(String)request.getParameter("tls");
	String strSim=(String)request.getParameter("sim");
	String strTtls=(String)request.getParameter("ttls");
	String strPeap=(String)request.getParameter("peap");
	String strAka=(String)request.getParameter("aka");
	String strAkaPrime=(String)request.getParameter("akaPrime");
	String strMschapv2=(String)request.getParameter("mschapv2");
	
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script type="text/javascript">
var isValidName;

$(document).ready(function() {
	$('.md5').attr('checked','checked');
});
  function disabledCreate(){

     
	  document.forms[0].c_btnNext.disabled=true;
	  document.forms[0].c_btnCreate.disabled=true;
	  if(document.forms[0].tls.checked==true || 
			  document.forms[0].ttls.checked==true || 
			  document.forms[0].peap.checked==true ||
			  document.forms[0].sim.checked==true || 
			  document.forms[0].aka.checked==true||
			  document.forms[0].akaPrime.checked==true)
	  {
		  document.forms[0].c_btnNext.disabled=false;
		  document.forms[0].c_btnCreate.disabled=true;

	     
		  if(navigator.appName=="Netscape"){
			  document.forms[0].c_btnCreate.style.color='grey';
			  document.forms[0].c_btnNext.style.color='white';
		  }  
            
	  }else if(document.forms[0].tls.checked==false 
			  && document.forms[0].ttls.checked==false 
			  && document.forms[0].peap.checked==false
			  && document.forms[0].sim.checked==false
			  && document.forms[0].aka.checked==false
			  && document.forms[0].akaPrime.checked==false){
		  document.forms[0].c_btnNext.disabled=true;
		  document.forms[0].c_btnCreate.disabled=false;
		
		  if(navigator.appName=="Netscape"){
			  document.forms[0].c_btnCreate.style.color='white';
			  document.forms[0].c_btnNext.style.color='grey';
		  }  
	  }		  
	  
  	  
  } 

  function validateOnNext(){

	  if(isNull(document.forms[0].name.value)){
			document.forms[0].name.focus();
			alert('Name must be specified');
	  }else if(!isValidName) {
			alert('Enter Valid Policy Name');
			document.forms[0].name.focus();
			return;
      }else if(isNull(document.forms[0].sessionCleanupInterval.value)){
		  document.forms[0].sessionCleanupInterval.focus();
		  alert('Session Cleanup Interaval must be specified');
				
	  }else if(!isNumber(document.forms[0].sessionCleanupInterval.value)){
		  document.forms[0].sessionCleanupInterval.focus();
		  alert('Session CleanUp Interval must be Numeric.');
				
	  }else if(isNull(document.forms[0].sessionDurationForCleanup.value)){
		  document.forms[0].sessionDurationForCleanup.focus();
		  alert('Session Duration For CleanUp  must be specified');
				
	  }else if(!isNumber(document.forms[0].sessionDurationForCleanup.value)){
		  document.forms[0].sessionDurationForCleanup.focus();
		  alert('Session Duration for CleanUp must be Numeric.');
				
	  }else if(isNull(document.forms[0].sessionTimeout.value)){
		  document.forms[0].sessionTimeout.focus();
		  alert('Session Timeout must be specified');
				
	  }else if(!isNumber(document.forms[0].sessionTimeout.value)){
		  document.forms[0].sessionTimeout.focus();
		  alert('Session Timeout must be Numeric.');
				
	  }else if(isNull(document.forms[0].treatInvalidPacketAsFatal.value)){
		  document.forms[0].treatInvalidPacketAsFatal.focus();
		  alert('Treat Invalid Packet As Fatal must be specified');
				
	  }else if(isNull(document.forms[0].defaultNegiotationMethod.value)){
		  document.forms[0].defaultNegiotationMethod.focus();
		  alert('DefaultNegiotationMethod must be selected');
				
	  }else if(isNull(document.forms[0].notificationSuccess.value)){
		  document.forms[0].notificationSuccess.focus();
		  alert('Notification Success must be selected.');
				
	  }else if(isNull(document.forms[0].notificationFailure.value)){
		  document.forms[0].notificationFailure.focus();
		  alert('Notification Failure must be selected.');
				
	  }else if(isNull(document.forms[0].maxEapPacketSize.value)){
		  document.forms[0].maxEapPacketSize.focus();
		  alert('Max EAP Packet Size must be specified');
				
	  }else if(!isNumber(document.forms[0].maxEapPacketSize.value)){
		  document.forms[0].maxEapPacketSize.focus();
		  alert('Max EAP Packet Size must be Numeric.');
				
	  }else{

         if(document.forms[0].defaultNegiotationMethod.value =='13'){
             
	         if(!document.forms[0].tls.checked)
		     {
                 alert("Please Select TLS Enabled Auth Method.");
                 return;
	         }   
		              
         }else if(document.forms[0].defaultNegiotationMethod.value =='21'){

        	 if(!document.forms[0].ttls.checked)
		     {
                 alert("Please Select TTLS Enabled Auth Method.");
                 return;
	         }

             
         }else if( document.forms[0].defaultNegiotationMethod.value =='25'){

        	 if(!document.forms[0].peap.checked)
		     {
                 alert("Please Select PEAP Enabled Auth Method.");
                 return;
	         }
         }else if(document.forms[0].defaultNegiotationMethod.value =='18'){

        	 if(!document.forms[0].sim.checked)
		     {
                 alert("Please Select SIM Enabled Auth Method.");
                 return;
	         }

             
         }else if(document.forms[0].defaultNegiotationMethod.value =='23'){

        	 if(!document.forms[0].aka.checked)
		     {
                 alert("Please Select AKA Enabled Auth Method.");
                 return;
	         }

             
         }else if(document.forms[0].defaultNegiotationMethod.value =='26'){

        	 if(!document.forms[0].mschapv2.checked)
		     {
                 alert("Please Select MS-CHAPv2 Enabled Auth Method.");
                 return;
	         }

             
         }else if(document.forms[0].defaultNegiotationMethod.value =='6'){

        	 if(!document.forms[0].gtc.checked)
		     {
                 alert("Please Select GTC Enabled Auth Method.");
                 return;
		     }        
         }else if(document.forms[0].defaultNegiotationMethod.value =='4'){
        	 if(!document.forms[0].md5.checked){
                 alert("Please Select MD5 Enabled Auth Method.");
                 return;
	         }             
         } else if(document.forms[0].defaultNegiotationMethod.value =='50'){
        	 if(!document.forms[0].akaPrime.checked){
                 alert("Please Select AKA' Enabled Auth Method.");
                 return;
	         }             
         } 

       	  document.forms[0].action.value="next"; 
          document.forms[0].submit();
		  
	  }
	  	  
	  
  }


  function validateOnCreate(){

	     
	  if(isNull(document.forms[0].name.value)){
			document.forms[0].name.focus();
			alert('Name must be specified');
	  }else if(!isValidName) {
			alert('Enter Valid Name');
			document.forms[0].name.focus();
			return;
      }else if(isNull(document.forms[0].sessionCleanupInterval.value)){
		  document.forms[0].sessionCleanupInterval.focus();
		  alert('Session CleanUp Interval must be specified');
				
	  }else if(!isNumber(document.forms[0].sessionCleanupInterval.value)){
		  document.forms[0].sessionCleanupInterval.focus();
		  alert('Session CleanUp Interval must be Numeric.');
				
	  }else if(isNull(document.forms[0].sessionDurationForCleanup.value)){
		  document.forms[0].sessionDurationForCleanup.focus();
		  alert('Session Duration For Cleanup must be specified');
				
	  }else if(!isNumber(document.forms[0].sessionDurationForCleanup.value)){
		  document.forms[0].sessionDurationForCleanup.focus();
		  alert('Session Duration for Cleanup must be Numeric.');
				
	  }else if(isNull(document.forms[0].sessionTimeout.value)){
		  document.forms[0].sessionTimeout.focus();
		  alert('Session Timeout must be specified');
				
	  }else if(!isNumber(document.forms[0].sessionTimeout.value)){
		  document.forms[0].sessionTimeout.focus();
		  alert('Session Timeout must be Numeric.');
				
	  }else if(isNull(document.forms[0].treatInvalidPacketAsFatal.value)){
		  document.forms[0].treatInvalidPacketAsFatal.focus();
		  alert('Treat Invalid Packet As Fatal must be specified');
				
	  }else if(isNull(document.forms[0].defaultNegiotationMethod.value)){
		  document.forms[0].defaultNegiotationMethod.focus();
		  alert('Default Negotiation Method must be specified');
				
	  }else if(isNull(document.forms[0].notificationSuccess.value)){
		  document.forms[0].notificationSuccess.focus();
		  alert('Notification Success must be specified');
				
	  }else if(isNull(document.forms[0].notificationFailure.value)){
		  document.forms[0].notificationFailure.focus();
		  alert('Notification Failure must be specified');
				
	  }else if(isNull(document.forms[0].maxEapPacketSize.value)){
		  document.forms[0].maxEapPacketSize.focus();
		  alert('Max EAP Packet Size must be specified');
				
	  }else if(!isNumber(document.forms[0].maxEapPacketSize.value)){
		  document.forms[0].maxEapPacketSize.focus();
		  alert('Max EAP Packet Size must be Numeric.');
				
	  }	
	  else{
         if(document.forms[0].defaultNegiotationMethod.value =='13'){
             
	         if(!document.forms[0].tls.checked)
		     {
                 alert("Please Select TLS Enabled Auth Method.");
                 return;
	         }   
		              
         }else if(document.forms[0].defaultNegiotationMethod.value =='21'){


        	 if(!document.forms[0].ttls.checked)
		     {
                 alert("Please Select TTLS Enabled Auth Method.");
                 return;
	         }

             
         }else if(document.forms[0].defaultNegiotationMethod.value =='25' ){


        	 if(!document.forms[0].peap.checked)
		     {
                 alert("Please Select PEAP Enabled Auth Method.");
                 return;
	         }

             
         }else if(document.forms[0].defaultNegiotationMethod.value =='18'){

        	 if(!document.forms[0].sim.checked)
		     {
                 alert("Please Select SIM Enabled Auth Method.");
                 return;
	         }

             
         }else if(document.forms[0].defaultNegiotationMethod.value =='23'){

        	 if(!document.forms[0].aka.checked)
		     {
                 alert("Please Select AKA Enabled Auth Method.");
                 return;
	         }

             
         }else if(document.forms[0].defaultNegiotationMethod.value =='50'){

        	 if(!document.forms[0].akaPrime.checked)
		     {
                 alert("Please Select AKA' Enabled Auth Method.");
                 return;
	         }

             
         }else if(document.forms[0].defaultNegiotationMethod.value =='26'){

        	 if(!document.forms[0].mschapv2.checked)
		     {
                 alert("Please Select MS-CHAPv2 Enabled Auth Method.");
                 return;
	         }

             
         }else if(document.forms[0].defaultNegiotationMethod.value =='6'){

        	 if(!document.forms[0].gtc.checked)
		     {
                 alert("Please Select GTC Enabled Auth Method.");
                 return;
	         }         
         }else if(document.forms[0].defaultNegiotationMethod.value =='4'){
        	 if(!document.forms[0].md5.checked){
                 alert("Please Select MD5 Enabled Auth Method.");
                 return;
	         }             
         }     			   		     
		   document.forms[0].action.value="create";   
	       document.forms[0].submit();		  
	  }	  
 }

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.EAP_CONFIG%>',searchName,'create','','verifyNameDiv');
}

setTitle('<bean:message bundle="servermgrResources" key="servermgr.eapconfig.header"/>');	
</script>

<%
	CreateEAPConfigForm createEAPConfigForm = (CreateEAPConfigForm)request.getAttribute("createEAPConfigForm");
%>

<html:form action="/createEAPConfig">
<html:hidden name="createEAPConfigForm" property="action" styleId="action"/>
<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
  			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">
  				&nbsp;
  			</td>
			<td>
   				<table cellpadding="0" cellspacing="0" border="0" width="100%">
  		  		<tr>
		    		<td  cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
					
						<tr>
							<td class="table-header">		
								<bean:message bundle="servermgrResources" key="servermgr.eapconfig.createeapconfiguration"/>
							</td>
						
						<td class="table-header" >
			            	<div name="forward" align="right" >
			            	   <!-- 
				   				<img  src="<%=basePath%>/images/steps.jpg"  border="0" >
								<img  src="<%=basePath%>/images/current-1.jpg"  border="0" > 
				    	        <img  src="<%=basePath%>/images/next-2.jpg"  border="0" > 
								<img  src="<%=basePath%>/images/next-3.jpg"  border="0" >
							    -->	 
			    	        </div>
			            </td>
			           
				</tr>
				<tr > 
						<td class="small-gap" colspan="3" >&nbsp;</td>
			    </tr>
			    <tr > 
					   <td align="left" class="captiontext"  valign="top" width="28%" >
					   		<bean:message bundle="servermgrResources" 
					  			key="servermgr.eapconfig.name"/>
					   				<ec:elitehelp headerBundle="servermgrResources" 
					   					text="eapconfig.name" 
					   						header="servermgr.eapconfig.name"/>
					  </td>
					   <td align="left" class="labeltext" valign="top" colspan="2" > 
						<html:text styleId="name" onkeyup="verifyName();" property="name" size="25" maxlength="50" style="width:250px" tabindex="1"/><font color="#FF0000"> *</font>
						<div id="verifyNameDiv" class="labeltext"></div>
					  </td>
		  		</tr>
		  		<tr> 
					  <td align="left" class="captiontext" valign="top" valign="top" >
					  		<bean:message bundle="servermgrResources" 
					  			key="servermgr.eapconfig.description"/>
					  				<ec:elitehelp headerBundle="servermgrResources" 
					  					text="eapconfig.desc" 
					  						header="servermgr.eapconfig.description"/>
					  </td>
					  <td align="left" class="labeltext" valign="top" colspan="2" > 
						<html:textarea styleId="description" property="description" cols="50" rows="2" style="width:250px" tabindex="2"/>
					  </td>
		        </tr>
		        <tr> 
					   <td align="left" class="captiontext" valign="top">
					   		<bean:message bundle="servermgrResources" 
					   			key="servermgr.eapconfig.sessioncleanupinterval"/>
					   				<bean:message key="general.seconds" />
					   					<ec:elitehelp headerBundle="servermgrResources" 
					   						text="eapconfig.cleanupinterval" 
					   							header="servermgr.eapconfig.sessioncleanupinterval"/>
					   </td>
					   <td align="left" class="labeltext" valign="top" colspan="2" > 
						<html:text styleId="sessionCleanupInterval" property="sessionCleanupInterval" size="15" maxlength="50" tabindex="3" style="width:250px"/><font color="#FF0000"> *</font>
					  </td>
		  		</tr>
		  		
		  		<tr> 
					   <td align="left" class="captiontext" valign="top">
					   		<bean:message bundle="servermgrResources" 
					   			key="servermgr.eapconfig.sessiondurationforcleanup"/>
					   				<bean:message key="general.seconds" />
					   					<ec:elitehelp headerBundle="servermgrResources" 
					   						text="eapconfig.cleanupduraiton" 
					   							header="servermgr.eapconfig.sessiondurationforcleanup"/>
					   	</td>
					   <td align="left" class="labeltext" valign="top" colspan="2" > 
						<html:text styleId="sessionDurationForCleanup" property="sessionDurationForCleanup" tabindex="4" size="15" maxlength="50" style="width:250px"/><font color="#FF0000"> *</font>
					  </td>
		  		</tr>
		  		<tr> 
					   <td align="left" class="captiontext" valign="top">
					   		<bean:message bundle="servermgrResources" 
					   			key="servermgr.eapconfig.sessiontimeout"/>
					   				<bean:message key="general.seconds" />
					    				<ec:elitehelp headerBundle="servermgrResources" 
					    					text="eapconfig.sessiontimeout" 
					    						header="servermgr.eapconfig.sessiontimeout"/>
					    </td>
					   <td align="left" class="labeltext" valign="top" colspan="2" > 
						<html:text styleId="sessionTimeout" property="sessionTimeout" size="15" maxlength="50" style="width:250px" tabindex="5"/><font color="#FF0000"> *</font>
					  </td>
		  		</tr>
		  		<tr> 
					   <td align="left" class="captiontext" valign="top">
					   		<bean:message bundle="servermgrResources" key="servermgr.eapconfig.mskrevalidationtime"/>
					   		<bean:message key="general.seconds" />
					    	<ec:elitehelp headerBundle="servermgrResources" text="servermgr.eapconfig.mskrevalidationtime" header="servermgr.eapconfig.mskrevalidationtime"/>
					    </td>
					   <td align="left" class="labeltext" valign="top" colspan="2" > 
							<html:text styleId="mskRevalidationTime" property="mskRevalidationTime" size="15" maxlength="50" style="width:250px" tabindex="5"/>
					  </td>
		  		</tr>
		        <tr> 
					   <td align="left" class="captiontext" valign="top">
					   		<bean:message bundle="servermgrResources" 
					   			key="servermgr.eapconfig.treatinvalidpacketasfatal"/>
					   				<ec:elitehelp headerBundle="servermgrResources" 
					   					text="eapconfig.invalidpacket" 
					   						header="servermgr.eapconfig.treatinvalidpacketasfatal"/>
					  </td>
					   <td align="left" class="labeltext" valign="top" colspan="2" > 
						<html:select name="createEAPConfigForm" styleId="treatInvalidPacketAsFatal" property="treatInvalidPacketAsFatal" size="1" style="width:130px" tabindex="6">
						   <html:option value="true" >True</html:option>
						   <html:option value="false" >False</html:option> 
						</html:select>
					  </td>
		  		</tr>
		        
		        <tr> 
					   <td align="left" class="captiontext" valign="top">
					   		<bean:message bundle="servermgrResources" 
					   			key="servermgr.eapconfig.defaultnegiotationmethod"/>
					   				<ec:elitehelp headerBundle="servermgrResources" 
					   					text="eapconfig.negotiationmethod" 
					   						header="servermgr.eapconfig.defaultnegiotationmethod"/>
					   </td>
					   <td align="left" class="labeltext" valign="top" colspan="2" > 
						<html:select name="createEAPConfigForm" styleId="defaultNegiotationMethod" property="defaultNegiotationMethod" size="1" style="width:130px" tabindex="7">
						   <html:option value="4" >MD5-Challenge</html:option>
						   <html:option value="6" >GTC</html:option>
						   <html:option value="13" >TLS</html:option>
						   <html:option value="18" >SIM</html:option>
						    <html:option value="21" >TTLS</html:option>
						    <html:option value="23">AKA</html:option>
						      <html:option value="50">AKA'</html:option>
						    <html:option value="25" >PEAP</html:option> 
						   <html:option value="26" >MS-CHAPv2</html:option>
		
						</html:select>
					  </td>
		  		</tr>
		  		
		  		<tr> 
					   <td align="left" class="captiontext" valign="top">
					   		<bean:message bundle="servermgrResources" 
					   			key="servermgr.eapconfig.notificationsuccess"/>
					   				<ec:elitehelp headerBundle="servermgrResources" 
					   					text="eapconfig.notificationsuccess" 
					   						header="servermgr.eapconfig.notificationsuccess"/>
					   </td>
					   <td align="left" class="labeltext" valign="top" colspan="2" > 
						<html:select name="createEAPConfigForm" styleId="notificationSuccess" property="notificationSuccess" size="1" style="width:130px" tabindex="8">
						   <html:option value="true" >True</html:option>
						   <html:option value="false" >False</html:option> 
						</html:select>
					  </td>
		  		</tr>                     
		  		<tr> 
					   <td align="left" class="captiontext" valign="top">
					   		<bean:message bundle="servermgrResources" 
					   			key="servermgr.eapconfig.notificationfailure"/>
					   				<ec:elitehelp headerBundle="servermgrResources" 
					   					text="eapconfig.notificationfailure" 
					   						header="servermgr.eapconfig.notificationfailure"/>
					  </td>
					   <td align="left" class="labeltext" valign="top" colspan="2" > 
						<html:select name="createEAPConfigForm" styleId="notificationFailure" property="notificationFailure" size="1" style="width:130px" tabindex="9">
						   <html:option value="true" >True</html:option>
						   <html:option value="false" >False</html:option> 
						</html:select>
					  </td>
		  		</tr>
		  		
		  		
		  		<tr> 
					   <td align="left" class="captiontext" valign="top">
					   		<bean:message bundle="servermgrResources" 
					   			key="servermgr.eapconfig.maxeappacketsize"/>
					   				<ec:elitehelp headerBundle="servermgrResources" 
					   					text="eapconfig.maxpacketsize" 
					   						header="servermgr.eapconfig.maxeappacketsize"/>
					  </td>
					   <td align="left" class="labeltext" valign="top" colspan="2" > 
						<html:text styleId="maxEapPacketSize" property="maxEapPacketSize" size="15" maxlength="50" style="width:250px" tabindex="10"/><font color="#FF0000"> *</font>
					  </td>
		  		</tr>
		  		<tr>
		  		    <td align="left" class="captiontext" valign="top">
		  		    	<bean:message bundle="servermgrResources" 
		  		    		key="servermgr.eapconfig.enabledauthmethod"/>
		  		    			<ec:elitehelp headerBundle="servermgrResources" 
		  		    				text="eapconfig.authmethods" 
		  		    					header="servermgr.eapconfig.enabledauthmethod"/>
		  		    </td>
		  		    <td align="left" class="labeltext" valign="top" colspan="2" > 
				         
                         <html:checkbox property="md5" value="<%=EAPConfigConstant.MD5_CHALLENGE_STR %>" styleId="md5" styleClass="md5" tabindex="11"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.md5"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        
                         <html:checkbox property="gtc" value="<%=EAPConfigConstant.GTC_STR%>" styleId="GTC" tabindex="12"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.gtc"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                       
                         <html:checkbox property="tls" value="<%=EAPConfigConstant.TLS_STR %>" styleId="tls" onclick="disabledCreate();" tabindex="13"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.tls"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					  
					     
                         <html:checkbox property="ttls" value="<%=EAPConfigConstant.TTLS_STR %>" styleId="ttls" onclick="disabledCreate();" tabindex="15"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.ttls"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                         
 	      				 <html:checkbox property="peap" value="<%=EAPConfigConstant.PEAP_STR %>" styleId="peap" onclick="disabledCreate();" tabindex="17"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.peap"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                         
					     <html:checkbox property="sim" value="<%=EAPConfigConstant.SIM_STR%>" styleId="sim" style="sim" onclick="disabledCreate();" tabindex="14"></html:checkbox>
					     <bean:message bundle="servermgrResources" key="servermgr.eapconfig.sim"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                         
                         <html:checkbox property="aka" value="<%=EAPConfigConstant.AKA_STR%>" styleId="aka" style="aka" onclick="disabledCreate();" tabindex="16"></html:checkbox>
					     <bean:message bundle="servermgrResources" key="servermgr.eapconfig.aka"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						
						<html:checkbox property="akaPrime" value="<%=EAPConfigConstant.AKA_PRIME_STR%>" styleId="akaPrime" style="akaPrime" onclick="disabledCreate();" tabindex="16"></html:checkbox>
					     <bean:message bundle="servermgrResources" key="servermgr.eapconfig.akaprime"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

                         <html:checkbox property="mschapv2" value="<%=EAPConfigConstant.MSCHAPv2_STR %>" styleId="mschapv2" tabindex="18"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.mschapv2"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                         
                          </td>
		  		</tr>
				 <tr>
			   		  <td align="left" class="labeltext" valign="top" >&nbsp;</td>
					  <td align="left" class="labeltext" valign="top" colspan="2" >&nbsp;</td>
			    </tr>
			    <tr>
			     	  <td class="small-gap" width="16%" >&nbsp;</td>
		     	      <td align="left" class="labeltext" valign="top" >
		     	       <input type="button" name="c_btnNext" id="c_btnNext" tabindex="19"  onclick=" validateOnNext()"  value=" Next "  class="light-btn" disabled="disabled" />
		     	        <input type="button" name="c_btnCreate"   onclick="validateOnCreate()"  tabindex="20" value=" Create "  class="light-btn" />
		     	        <input type="button" name="c_btnCancel"  tabindex="21" onclick="javascript:location.href='<%=basePath%>/initSearchEAPConfig.do?/>'"  value="   Cancel   " class="light-btn"  />
					  </td>
			    </tr>
			    
			    <tr>
			   		  <td align="left" class="labeltext" valign="top" >&nbsp;</td>
					  <td align="left" class="labeltext" valign="top" colspan="2" >&nbsp;</td>
			    </tr>
			</table>
		</td>
	</tr>
	<%@ include file="/jsp/core/includes/common/Footer.jsp" %>	
</table>	
</td>
</tr>
</table>	
</html:form>
<script>
disabledCreate();

</script>

