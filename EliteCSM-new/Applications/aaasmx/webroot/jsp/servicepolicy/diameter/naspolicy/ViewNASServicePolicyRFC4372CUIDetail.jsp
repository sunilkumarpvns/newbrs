<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<script language = "javascript">

	$(document).ready(function(){
		  $("#rfcToggleImageElement").click(function(){
			  var imgElement = document.getElementById("rfcToggleImageElement");
			  if ($("#rfcToggleDivElement").is(':hidden')) {
		            imgElement.src="<%=basePath%>/images/top-level.jpg";
		       } else {
		            imgElement.src="<%=basePath%>/images/bottom-level.jpg";
		       }
		      $("#rfcToggleDivElement").slideToggle("normal");
		  });
	});
	
</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr> 
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%">
            	<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.rfc4372cuiparameters"/>
            </td>
            <td class="tblheader-bold"  align="right" width="15px">
           		<img alt="bottom" id="rfcToggleImageElement" src="<%=basePath%>/images/bottom-level.jpg"/>
           </td>
           </tr>
          </table> 
          <div id="rfcToggleDivElement" style="display: none;">
          <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
           <tr>
          		<td class="tblfirstcol" width="30%" valign="top">
          			<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.cui" />
          		</td>
          		<td  width="70%" class="tblcol" >
         			<bean:write name="nasPolicyInstDataBean" property="cui"/>&nbsp;
         		</td>
           </tr>
           <tr>
          		<td class="tblfirstcol" width="30%" valign="top">
          			<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.advancedcuiexpression" />
          		</td>
          		<td  width="70%" class="tblcol" >
         			<bean:write name="nasPolicyInstDataBean" property="advancedCuiExpression"/>&nbsp;
         		</td>
           </tr>
           <tr>
          		<td class="tblfirstcol" width="30%" valign="top">
          			<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.cuiresattrs" />
          		</td>
          		<td  width="70%" class="tblcol" >
         			<bean:write name="nasPolicyInstDataBean" property="cuiResponseAttributes"/>&nbsp;
         		</td>
           </tr>
       </table>
       </div>
     </td>
 	</tr>
 </table>         