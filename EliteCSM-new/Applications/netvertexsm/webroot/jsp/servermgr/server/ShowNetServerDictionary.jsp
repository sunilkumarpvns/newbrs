<%@ include file="/jsp/core/includes/common/Header.jsp"%>


<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
	<%--<td width="10">&nbsp;</td>
	--%>
	<td> 
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
      
      	<tr> 
					      <td width="81%" background="<%=basePath%>/images/popup-bkgd.jpg" valign="top">&nbsp; </td>
				          <td width="3%"><img src="<%=basePath%>/images/popup-curve.jpg"></td>
					      <td background="<%=basePath%>/images/popup-btn-bkgd.jpg" width="3%"><a href="#"><img src="<%=basePath%>/images/refresh.jpg" name="Image1" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image1','','<%=basePath%>/images/refresh-hover.jpg',1)" border="0"></a></td>
					      <td background="<%=basePath%>/images/popup-btn-bkgd.jpg" width="3%"><a href="#" onclick="window.print()"><img src="<%=basePath%>/images/print.jpg" name="Image2" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image2','','<%=basePath%>/images/print-hover.jpg',1)" border="0" > </a></td>
					      <td background="<%=basePath%>/images/popup-btn-bkgd.jpg" width="3%"><a href="#"><img src="<%=basePath%>/images/aboutus.jpg" name="Image3" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image3','','<%=basePath%>/images/aboutus-hover.jpg',1)" border="0"></a></td>
					      <td background="<%=basePath%>/images/popup-btn-bkgd.jpg" width="3%"><a href="#"><img src="<%=basePath%>/images/help.jpg" name="Image4" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image4','','<%=basePath%>/images/help-hover.jpg',1)" border="0"></a></td>
					      <td background="<%=basePath%>/images/popup-btn-bkgd.jpg" width="4%">&nbsp;</td>
      			   </tr>
        <%--<tr> 
          <td width="26" valign="top" rowspan="2"><img src="<%=basePath%>/images/left-curve.jpg"></td>
          <td background="<%=basePath%>/images/header-gradient.jpg" width="133" rowspan="2" align="center" class="page-header">Server</td>
          <td width="32" rowspan="2"><img src="<%=basePath%>/images/right-curve.jpg"></td>
          <td width="633"><a href="#"><img src="<%=basePath%>/images/csv.jpg" name="Image1" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image1','','<%=basePath%>/images/csv-hover.jpg',1)" border="0" alt="Save as CSV"></a><a href="#"><img src="<%=basePath%>/images/pdf.jpg" name="Image2" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image2','','<%=basePath%>/images/pdf-hover.jpg',1)" border="0" alt="Save as PDF"></a><a href="#"><img src="<%=basePath%>/images/html.jpg" name="Image3" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image3','','<%=basePath%>/images/html-hover.jpg',1)" border="0" alt="Save as HTML"></a><a href="#"><img src="<%=basePath%>/images/filter.jpg" name="Image4" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image4','','<%=basePath%>/images/filter-hover.jpg',1)" border="0" alt="Apply Filter"></a></td>
        </tr>
        <tr> 
          <td width="633" valign="bottom"><img src="<%=basePath%>/images/line.jpg"></td>
        </tr>--%>
         <tr> 
		 				  <td colspan="8"> 
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr> 
			  
        									<td width="38%" class="blue-text-bold" valign="bottom"><bean:write name="showNetServerDictionaryForm" property="fileName"/></td>
        							</tr>		
								</table>
							</td>
		</tr>						
      </table>
    </td>
  </tr>
  <tr> 
    <td width="10" class="small-gap">&nbsp;</td>
    <td class="small-gap" colspan="2">&nbsp;</td>
  </tr>
  <tr height=380px>
  <td colspan="3" class="small-gap">
	<iframe src="/showNetServerDictionary.do?fileName=<bean:write name="showNetServerDictionaryForm" property="fileName"/>&netServerId=<bean:write name="showNetServerDictionaryForm" property="netServerId"/>" frameborder="1" height="100%" width="100%" >
	</iframe>
	</td>
	</tr>
	<tr> 
      <td colspan="3" class="small-gap">&nbsp;</td>
    </tr>
    <%--<tr> 
      <td width="10">&nbsp;</td>
      <td colspan="2" valign="top" align="right"> 
        <table width="99%" border="0" cellspacing="0" cellpadding="0">
          <tr> 
            <td width="82%" valign="top"><img src="<%=basePath%>/images/btm-line.jpg"></td>
            <td align="right" rowspan="2" valign="top"><img src="<%=basePath%>/images/btm-gradient.jpg" width="140" height="23"></td>
          </tr>
          <tr> 
            <td width="82%" valign="top" align="right" class="small-text-grey">Copyright &copy; <a href="http://www.elitecore.com" target="_blank">Elitecore Technologies Pvt. Ltd.</a> </td>
          </tr>
        </table>
      </td>
    </tr>--%>
</table>

<table cellpadding="0" cellspacing="0" border="0">	
	<tr> 
      <td colspan="2" class="small-gap">&nbsp;</td>
    </tr>
    
	 <tr> 
		    <td bgcolor="#00477F" class="small-gap" width="99%">&nbsp;</td>
		    <td class="small-gap" width="1%"><img src="<%=basePath%>/images/pbtm-line-end.jpg"></td>
	</tr>
</table>