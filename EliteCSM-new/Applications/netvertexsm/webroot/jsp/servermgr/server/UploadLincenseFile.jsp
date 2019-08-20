
<%
	String localBasePath1 = request.getContextPath();
%>
<script>
function validateCreate(){
	if(isNull(document.forms[0].licenseFile.value)){
		alert('License file must be specified');
	}else{
		document.forms[0].action.value='upload';	
		document.forms[0].submit();
	}
}
function cancel(){
	var netServerId = document.getElementById("netServerId").value;
	javascript:location.href = '<%=localBasePath1%>/initNetServerLicense.do?netServerId='+netServerId;
}
</script>
  <tr> 
    <td width="100%" valign="top" align="right">
	<html:form action="/uploadLicenseAction" enctype="multipart/form-data">    
	<html:hidden name="uploadLicenseForm" styleId="action" property="action"/>
	<html:hidden name="uploadLicenseForm" styleId="netServerId" property="netServerId"/> 
  <table width="97%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td colspan="2" >&nbsp;</td>
  </tr>
		    <tr> 
		      <td  class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="servermgrResources" key="servermgr.uploadlicense" /></td>
		    </tr>
		    <tr> 
		      <td  width="3%" colspan="2">&nbsp;</td>			
		    </tr>
		    
		    <tr> 
		      <td valign="top" width="97%"> 
		        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
    		  <tr> 
		            <td class="labeltext" valign="top" width="15%" height="15%"><bean:message bundle="servermgrResources" key="servermgr.license.file" /></td>
		            <td class="labeltext" width="85%" valign="top" height="15%" ><html:file name="uploadLicenseForm" property="licenseFile" size="35" /></td>
		      </tr>
		      <tr>
		      <td colspan="2">
		      &nbsp;
		      </td>
		      </tr>

		        </table>
				</td>
		    </tr>
   				 <tr > 
	         <td width="97%"  valign="left" >
	                      	<input type="button" name="c_btnCreate"  onclick="validateCreate()"  id="c_btnCreate2"  value="Upload"  class="light-btn">                   
	                      	<input type="reset" name="c_btnDeletePolicy" onclick="cancel()"  value="Cancel" class="light-btn"> 
		                </td>
    			 </tr>
		
		</table>
		</html:form>
    </td>
  </tr>
  <tr> 
    <td colspan="3" class="small-gap">&nbsp;</td>
  </tr>
 <%@ include file="/jsp/core/includes/common/Footer.jsp" %>