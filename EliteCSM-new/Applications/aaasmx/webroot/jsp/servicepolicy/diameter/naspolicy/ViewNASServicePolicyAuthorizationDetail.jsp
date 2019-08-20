<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<script language = "javascript">

	$(document).ready(function(){
		  $("#authorizationToggleImageElement").click(function(){
			  var imgElement = document.getElementById("authorizationToggleImageElement");
			  if ($("#authorizationToggleDivElement").is(':hidden')) {
		            imgElement.src="<%=basePath%>/images/top-level.jpg";
		       } else {
		            imgElement.src="<%=basePath%>/images/bottom-level.jpg";
		       }
		      $("#authorizationToggleDivElement").slideToggle("normal");
		  });
	});
	
	$(document).ready(function(){
		  $("#fieldMapingToggleImageElementAuthorize").click(function(){
			  var imgElement = document.getElementById("fieldMapingToggleImageElementAuthorize");
			  if ($("#fieldMapingToggleDivElementAuthorize").is(':hidden')) {
		            imgElement.src="<%=basePath%>/images/top-level.jpg";
		       } else {
		            imgElement.src="<%=basePath%>/images/bottom-level.jpg";
		       }
		      $("#fieldMapingToggleDivElementAuthorize").slideToggle("normal");
		  });
	});
</script>



<table width="100%" border="0" cellspacing="0" cellpadding="0">
    
    <tr> 
    
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%">
            	<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.authorizationdetails"/>
            </td>
            <td class="tblheader-bold"  align="right" width="15px">
           		<img alt="bottom" id="authorizationToggleImageElement" src="<%=basePath%>/images/bottom-level.jpg"/>
           </td>
           </tr>
          </table> 
          <div id="authorizationToggleDivElement" style="display: none;">
          <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
           <tr>
          		<td class="tblfirstcol" width="30%" valign="top"><bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.wimax" /> </td>
          		<td  width="70%" class="tblcol" >
         			<logic:equal value="true" name="nasPolicyInstDataBean" property="wimax">
         				Enabled
         			</logic:equal>
         			<logic:equal value="false" name="nasPolicyInstDataBean" property="wimax">
         				Disabled
         			</logic:equal>
         			&nbsp;
         		</td>
           </tr>
          <tr>
          	<td class="tblfirstcol" width="30%" valign="top"> Diameter Policy </td>
          	<td  width="70%" >
          		<table width="100%" border="0" cellspacing="0" cellpadding="0" >
         		<tr>
         			<td class="tblfirstcol" width="33%" >
         				<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.rejectoncheckitemnotfound" />
         			</td>
         			<td class="tblcol">	
         				<bean:write name="nasPolicyInstDataBean" property="rejectOnCheckItemNotFound"/>&nbsp;
         			</td>
         		</tr>
         		<tr>	
         			<td class="tblfirstcol" >
         				<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.rejectonrejectitemnotfound" />
         			</td>
         			<td class="tblcol">	
         				<bean:write name="nasPolicyInstDataBean" property="rejectOnRejectItemNotFound"/>&nbsp;
         			</td>
         		</tr>
         		<tr>	
         			<td class="tblfirstcol" >
         				<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.acceptonpolicynotfound" />
         			</td>
         			<td class="tblcol">	
         				<logic:equal value="1" name="nasPolicyInstDataBean" property="actionOnPolicyNotFound">
	           				true
	        			</logic:equal>
	        			<logic:equal value="2" name="nasPolicyInstDataBean" property="actionOnPolicyNotFound">
	           				false
	        			</logic:equal>
         				
         			</td>
         		</tr>
         		</table>
         	
          	</td>
          </tr>
          <tr>
          		<td class="tblfirstcol" width="30%" valign="top"><bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.gracepolicy" /> 
			    </td>
          		<td  width="70%" class="tblcol" >
         				<bean:write name="nasPolicyInstDataBean" property="gracePolicy"/>&nbsp;
         		</td>
           </tr>
           <tr>
          		<td class="tblfirstcol" width="30%" valign="top">
			   		<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.diameterconcurrency" /> 
			    </td>
          		<td  width="70%" class="tblcol" >
         			<bean:write name="diameterConcurrencyBean" property="name"/>&nbsp;
         		</td>
           </tr>
           <tr>
          		<td class="tblfirstcol" width="30%" valign="top">
			   		<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.additionaldiameterconcurrency" /> 
			    </td>
          		<td  width="70%" class="tblcol" >
         			<bean:write name="additionalDiameterConcurrencyData" property="name"/>&nbsp;
         		</td>
           </tr>
           <tr>
          		<td class="tblfirstcol" width="30%" valign="top">
          			<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.defaultsessiontimeout" /> 
			    </td>
          		<td  width="70%" class="tblcol" >
         			<bean:write name="nasPolicyInstDataBean" property="defaultSessionTimeout"/>&nbsp;
         		</td>
           </tr>
       </table>
       </div>
     </td>
 	</tr>
 </table>         