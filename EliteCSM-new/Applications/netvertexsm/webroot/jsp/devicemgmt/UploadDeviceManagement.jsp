<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="java.util.List" %>


<script type="text/javascript">
function validate(){
	if(isNull(document.forms[0].formFile.value)){
		alert('CSV File must be specified.');
	}else{
		document.forms[0].submit();
	}
}
$(document).ready(function(){
	setTitle('<bean:message  bundle="deviceMgmtResources" key="devicemgmt.title"/>');
});
</script> 	

<html:form action="/deviceMgmt.do?method=uploadCSV"  enctype="multipart/form-data"> 

<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp" %>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
				<bean:message  bundle="deviceMgmtResources" key="devicemgmt.upload.title"/>
			</td>
		  </tr>
          <tr> 
				<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="97%"  align="left" border="0" > 	
			   	  <tr> 
					<td align="left" class="captiontext" valign="top" width="30%" >
						<bean:message bundle="deviceMgmtResources" key="devicemgmt.csvfile" />
					</td> 
					<td align="left" class="labeltext" valign="top" width="70%"> 
						<html:file name="deviceManagementForm" property="formFile" styleId="formFile" size="40"/><font color="#FF0000"> *</font>
						 <bean:message  key="general.csvformat"/>&nbsp;<a  href="javascript:void(0)"  onclick="window.open('<%=basePath%>/jsp/devicemgmt/DeviceManagementCSVFormat.jsp','CSVWin','top=0, left=0, height=350, width=700, scrollbars=yes, status')" ><img  src="<%=basePath%>/images/view.jpg"  name="Image6"  onmouseover="MM_swapImage('Image6','','<%=basePath %>/images/view-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  border="0"  id="Image6" ></a>
					</td> 
				  </tr>
				  <tr>
				  		<td align="left" valign="top" ></td>
						<td ><input type="button" onclick="validate();" value="  Upload  " class="light-btn" />
						<input type="button" value=" Cancel " tabindex="29" class="light-btn" onclick="javascript:location.href='<%=basePath%>/deviceMgmt.do?method=initSearch'"/></td>
				 </tr>
			   </table>  
			</td> 
		</tr>
		
		<tr><td>&nbsp;</td></tr>		
		</table> 
	  </td> 
	</tr>			
	 <%@ include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table>
</html:form>

