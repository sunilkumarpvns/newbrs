<%--
<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="10">&nbsp;</td>
    <td colspan="2"> 
    
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
         <tr> 
          <td width="26" valign="top" rowspan="2"><img src="<%=basePath%>/images/left-curve.jpg"></td>
          <td background="<%=basePath%>/images/header-gradient.jpg" width="133" rowspan="2" class="page-header"><bean:message key="staff.staff" /></td>
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
					<td valign="top" >
						<%@ include file="ViewStaff.jsp"%>
					</td>
				</tr>
				<tr>
					<td valign="top">
						<%@ include file="UpdateStaffPassword.jsp"%>
					</td>
				</tr>
    		</table>
    	</td>	
		<td width="168" class="grey-bkgd" valign="top">
			<%@  include file="StaffNavigation.jsp"%>
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
						<td width="82%" valign="top" align="right" class="small-text-grey">Copyright&copy; <a href="http://www.elitecore.com" target="_blank">Elitecore Technologies Pvt. Ltd.</a></td>
				</tr>
			</table>
		</td>
	</tr>
</table>		  
<%@ include file="/jsp/core/includes/common/Footer.jsp" %>

--%>

<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<script type="text/javascript">
	$(document).ready(function(){
		setTitle('<bean:message key="staff.staff" />');
	});
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

	<tr>
		<td width="10" class="small-gap">&nbsp;</td>
		<td width="85%" class="box" >
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td valign="top" >
						<%@ include file="ViewStaff.jsp"%>
					</td>
				</tr>
				<tr>
					<td valign="top">
						<%@ include file="UpdateStaffPassword.jsp"%>
					</td>
				</tr>
			</table>
		</td>
		<td width="15%" class="grey-bkgd" valign="top">
			<%@  include file="StaffNavigation.jsp"%>
		</td>

	</tr>

	<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>

