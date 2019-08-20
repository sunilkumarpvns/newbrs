



<% 
	String localBasePath1 = request.getContextPath();
%>

<html:form action="/exportNetServerDetail">
<html:hidden name="exportNetServerConfigurationForm" styleId="netServerId" property="netServerId"/>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td valign="top" align="right"> 
      <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
		<tr> 
		  <td class="tblheader-bold" colspan="2"><bean:message bundle="servermgrResources" key="servermgr.exportservicedetails"/></td>
		</tr>
		<tr> 
		  <td align="left" class="labeltext" valign="top"  colspan="2">&nbsp;</td>
		</tr>	
      </table>
	</td>
  </tr>
  <tr> 
    <td>&nbsp;</td>
  </tr>
  <tr > 
    <td class="btns-td" valign="middle">
      <input type="button" name="c_btnSynchronize"  onclick="javascript:location.href='<%=localBasePath1%>/jsp/servermgr/ServerContainer.jsp'"  id="c_btnSynchronize"  value=" Export "  class="light-btn">                   
      <input type="reset" name="c_btnDeletePolicy" value="Cancel" class="light-btn"> 
    </td>
  </tr>
  <tr> 
    <td>&nbsp;</td>
  </tr>
</table>
</html:form>

<%@ include file="/jsp/core/includes/common/Footer.jsp" %>	
