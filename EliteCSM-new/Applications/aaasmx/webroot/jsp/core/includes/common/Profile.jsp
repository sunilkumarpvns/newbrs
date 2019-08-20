<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<%	
    String basePath = request.getContextPath();
    String profile = ConfigManager.get(ConfigConstant.PROFILE);
%>

<table width="506" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td >&nbsp;</td>
    <td >&nbsp;</td>
  </tr>
  <tr>
    <td >&nbsp;</td>
    <td >&nbsp;</td>
  </tr>
  <tr>
    <td >&nbsp;</td>
    <td >&nbsp;</td>
  </tr>
  <tr>
    <td >&nbsp;</td>
    <td >&nbsp;</td>
  </tr>
  <tr>
    <td >&nbsp;</td>
    <td >&nbsp;</td>
  </tr>
  <tr> 
    
    <td align = "center" width="350" class="grey-text">
    <table width="350" cellspacing="0" cellpadding="0" border="1"  bordercolor="#dddddd" >
    	<tr>
   			<td colspan="2" align="center" height="25px" class="grey-text" bgcolor="#dddddd" >
    		 <font color="#555555"><b>UBIQUITOUS AAA SERVER MANAGER</b></font>
    		</td>
    	</tr>
    	
		<tr>
	
          <td class="grey-text" colspan="2" style= "padding-left:15px">
          <br/>
          <br/>
          
         
          Current Profile <%=profile %><br/>
          <a href="<%=basePath%>/initSwitchProfile.do">
        	  <bean:message key="configurationprofile.switchprofile"/>
          </a>
          <br/>
          <br/>
          <br/>
          <br/>
          <br/>
          <br/>
          </td>
        </tr> 
     
     
     <%--tr> 
    <td ><img src="<%=basePath%>/images/about-footer.jpg" width="351" height="48"></td>
  </tr--%>
     </table>
      </td>
  </tr>
 
</table>
<%@ include file="/jsp/core/includes/common/Footer.jsp" %>	
