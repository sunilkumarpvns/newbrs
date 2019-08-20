
<%@ include file="/jsp/core/includes/common/Header.jsp"%>



<table width="828" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="10">&nbsp;</td>
    <td colspan="2"> 
    
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
         <tr> 
          <td width="26" valign="top" rowspan="2"><img src="<%=basePath%>/images/left-curve.jpg"></td>
          <td background="<%=basePath%>/images/header-gradient.jpg" width="133" rowspan="2" align="center" class="page-header">Server</td>
          <td width="32" rowspan="2"><img src="<%=basePath%>/images/right-curve.jpg"></td>
          <td width="633"><a href="#"><img src="<%=basePath%>/images/csv.jpg" name="Image1" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image1','','<%=basePath%>/images/csv-hover.jpg',1)" border="0" alt="Save as CSV"></a><a href="#"><img src="<%=basePath%>/images/pdf.jpg" name="Image2" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image2','','<%=basePath%>/images/pdf-hover.jpg',1)" border="0" alt="Save as PDF"></a><a href="#"><img src="<%=basePath%>/images/html.jpg" name="Image3" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image3','','<%=basePath%>/images/html-hover.jpg',1)" border="0" alt="Save as HTML"></a><a href="#"><img src="<%=basePath%>/images/filter.jpg" name="Image4" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image4','','<%=basePath%>/images/filter-hover.jpg',1)" border="0" alt="Apply Filter"></a></td>
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
    <td width="10" class="small-gap">&nbsp;</td>	
    	<td  width="773" class="box" >
    		<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td valign="top">
						<%@ include file="ViewNetServerInstance.jsp"%>
					</td>
				</tr>
				<tr>
					<td valign="top">
						<%@ include file="UpdateServerBasicDetails.jsp"%>
					</td>
				</tr>
    		</table>
    	</td>	
		<td width="168" class="grey-bkgd" valign="top">
			<%@  include file="NetServerInstanceNavigation.jsp"%>
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
            Technologies Pvt. Ltd.</a> </td>
        </tr>
      </table>
      
    </td>
  </tr>

</table>

<%@ include file="/jsp/core/includes/common/Footer.jsp" %>