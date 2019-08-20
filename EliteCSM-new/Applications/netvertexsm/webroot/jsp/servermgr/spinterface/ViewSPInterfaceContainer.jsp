<%@ include file="/jsp/core/includes/common/Header.jsp"%>


<table width="828" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="10">&nbsp;</td>
    <td colspan="2"> 
    
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
         <tr> 
	      <td width="26" valign="top" rowspan="2"><img src="<%=basePath%>/images/left-curve.jpg"></td>
          <td background="<%=basePath%>/images/header-gradient.jpg" width="133" rowspan="2" class="page-header">
          		<bean:message bundle="driverResources" key="driver.driverinstance.summary" /></td>
          <td width="32" rowspan="2"><img src="<%=basePath%>/images/right-curve.jpg"></td>
          <td width="633"></td>
        </tr>
        <tr> 
          <td width="633" valign="bottom"><img src="<%=basePath%>/images/line.jpg"></td>
        </tr>
      </table>
     
    </td>
  </tr>
  <tr> 
    <td width="10" class="small-gap">&nbsp;</td>
    <td class="small-gap" colspan="2">&nbsp;</td>
  </tr>
  <tr> 
    <td width="10">&nbsp;</td>
    <td width="773" valign="top" class="box">
    	<div style="margin-left: 1.7em;" class="tblheader-bold"><bean:message bundle="driverResources" key="driver.driverinstance.information" /></div>        
		<%@ include file = "ViewSPInterface.jsp" %> 
	</td>
    <td width="168" class="grey-bkgd" valign="top">
      <%@ include file = "SPInterfaceNavigation.jsp" %> 
    </td>
  </tr>
  <tr> 
    <td colspan="3" class="small-gap">&nbsp;</td>
  </tr>
  <tr> 
    <td width="10">&nbsp;</td>
    <td colspan="2" valign="top" align="right"> 
    
      <table width="99%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td width="82%" valign="top"><img src="<%=basePath%>/images/btm-line.jpg"></td>	
          <td align="right" rowspan="2" valign="top"><img src="<%=basePath%>/images/btm-gradient.jpg" width="140" height="23"></td>
        </tr>
        <tr> 
          <td width="82%" valign="top" align="right" class="small-text-grey">Copyright 
            &copy; <a href="http://www.elitecore.com" target="_blank">Elitecore 
            Technologies Pvt.Ltd</a> </td>
        </tr>
      </table>
      
    </td>
  </tr>

</table>

<%@ include file="/jsp/core/includes/common/Footer.jsp" %>
