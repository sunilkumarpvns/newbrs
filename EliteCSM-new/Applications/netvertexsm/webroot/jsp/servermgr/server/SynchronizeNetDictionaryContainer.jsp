<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<%

out.write("");
%>


<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
  <tr height=380px> 
    <td width="10">&nbsp;</td>
    <td width="85%" valign="top" class="box">
      <table width="100%">
    	<tr>
    	  <td>
				<%@ include file="ViewNetServerInstance.jsp" %>
		  </td>
	    </tr>
        <tr>
 		  <td>
				<%@ include file="NetServerReloadCache.jsp"%>
		  </td>
	    </tr>
      </table>		
	 </td>
    <td class="grey-bkgd" valign="top" width="15%">
      	<%@  include file="UpdateNetDictionarySynchronize.jsp"%>
    </td>
  </tr>
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>

