<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASResponseAttributes" %>
<%@ page import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData" %>
<script language = "javascript">

	$(document).ready(function(){
		  $("#resToggleImageElement").click(function(){
			  var imgElement = document.getElementById("resToggleImageElement");
			  if ($("#resToggleDivElement").is(':hidden')) {
		            imgElement.src="<%=request.getContextPath()%>/images/top-level.jpg";
		       } else {
		            imgElement.src="<%=request.getContextPath()%>/images/bottom-level.jpg";
		       }
		      $("#resToggleDivElement").slideToggle("normal");
		  });
	});
</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
    
    <tr> 
    
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%">
            	<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.responseattributes" />
            </td>
             <td class="tblheader-bold"  align="right" width="15px">
           		<img alt="bottom" id="resToggleImageElement" src="<%=request.getContextPath()%>/images/bottom-level.jpg"/>
            </td>
          </tr>
        </table>
        <div id="resToggleDivElement" style="display: none;padding: 20px;" align="left" >  
	        <table width="50%" border="0" cellspacing="0" cellpadding="0" height="15%" >
	         	<tr>
	         		<td class="tblheader-bold">
	         			Command Codes
	         		</td>
	         		<td class="tblheader-bold">
	         			Response Attributes
	         		</td>
	         	</tr>
	         	<bean:define id="nasPolicyInstDataBean" name="nasPolicyInstData" scope="request" type="com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData" />
	  			<logic:iterate id="responseAttributes" name="nasPolicyInstDataBean" property="nasResponseAttributesSet" type="com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASResponseAttributes">
	  			<tr>
					<td class="tblfirstcol">
	         			<bean:write name="responseAttributes" property="commandCodes"/>	&nbsp;
	             	</td>
	         		<td class="tblcol border-right-css">
	         			<bean:write name="responseAttributes" property="responseAttributes"/>	&nbsp;
	         		</td>
	         	</tr>
	  			</logic:iterate> 
	       </table>
       </div>
     </td>
 	</tr>
 </table>         