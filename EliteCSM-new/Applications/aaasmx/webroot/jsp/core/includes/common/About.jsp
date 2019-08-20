<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="com.elitecore.elitesm.Version"%>
<table width="506" border="0" align="center" cellpadding="0" cellspacing="0">
<%
    String basePath = request.getContextPath();
%>    
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
    		 <font color="#555555"><b>ELITECSM - CENTRAL SERVER MANAGER </b></font>
    		</td>
    	</tr>
    	<tr>
          <td class="grey-text" style ="padding-left:15px">
          <br/>
          <br/>
          <a href="<%=basePath%>/initLicense.do?">View License Details</a> <br/>
          <table width="100%">
           <tr>
          	<td colspan="2"/>
          </tr>
          <tr>
          	<td class="grey-text" width="30%">Version: </td>
          	<td class="grey-text"><%=Version.getVersion()%></td>
          </tr>
          <tr>
          	<td class="grey-text" width="30%">Date: </td>
          	<td class="grey-text"><%=Version.getReleaseDate()%></td>
          </tr>
          <tr>
          	<td class="grey-text" width="30%">Revision: </td>
          	<td class="grey-text"><%=Version.getSVNRevision()%></td>
          </tr>
           <tr>
          	<td colspan="2"/>
          </tr>
          </table>
          <br/>
         This product is Proprietary to <a href="http://www.elitecore.com" target="_blank">Sterlite Technologies Ltd.</a>
          
          <br/>
          <br/>
          </td>
        </tr>
      
      </table>
      </td>
  </tr>
  <%--<tr> 
    <td >&nbsp;</td>
    <td ><img src="<%=basePath%>/images/about-footer.jpg" width="351" height="48"></td>
  </tr--%>
</table>
