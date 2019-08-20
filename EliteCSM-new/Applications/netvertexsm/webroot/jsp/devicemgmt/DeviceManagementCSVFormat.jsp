<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<% 
	String localBasePath = request.getContextPath();
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	 <tr> 
    	 <td valign="top" class="box" colspan="8"> 
			  <table width="100%" border="0" cellspacing="0" cellpadding="0">
				   <tr> 
					      <td width="*" background="<%=localBasePath%>/images/popup-bkgd.jpg" valign="top">&nbsp; </td>
				          <td width="20px"><img src="<%=localBasePath%>/images/popup-curve.jpg"></td>
					      <td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg" width="30px"><a href="#"><img src="<%=localBasePath%>/images/refresh.jpg" name="Image1" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image1','','<%=localBasePath%>/images/refresh-hover.jpg',1)" border="0"></a></td>
					      <td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg" width="30px"><a href="#" onclick="window.print()"><img src="<%=localBasePath%>/images/print.jpg" name="Image2" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image2','','<%=localBasePath%>/images/print-hover.jpg',1)" border="0" > </a></td>
					      <td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg" width="30px"><a href="#"><img src="<%=localBasePath%>/images/aboutus.jpg" name="Image3" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image3','','<%=localBasePath%>/images/aboutus-hover.jpg',1)" border="0"></a></td>
					      <td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg" width="30px"><a href="#"><img src="<%=localBasePath%>/images/help.jpg" name="Image4" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image4','','<%=localBasePath%>/images/help-hover.jpg',1)" border="0"></a></td>
					      <td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg" width="30px">&nbsp;</td>
      			   </tr>
				   <tr> 
		 				  <td colspan="8"> 
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
			  						 <tr> 
			  
        									<td width="38%" class="blue-text-bold" valign="bottom"><bean:message key="general.csvformat"/></td>
			  								<td class="blue-text-bold" width="43%"></td>
											<td width="3%" class="blue-text-bold">&nbsp;</td>
											<td width="3%" class="blue-text-bold">&nbsp;</td>
											<td width="3%" class="blue-text-bold">&nbsp;</td>
											<td width="3%" class="blue-text-bold">&nbsp;</td>
											<td width="3%" class="blue-text-bold">&nbsp;</td>
											<td width="4%" class="blue-text-bold">&nbsp;</td>
									 </tr>
							    </table>
		 				  </td>
				   </tr>
				   <tr> 
				  		  <td colspan="8" class="small-gap">&nbsp;</td>
				   </tr>
				   <tr align="left"> 
		 				  <td colspan="8" class="top-btmlines"> 
			
    							<table width="100%" border="0" cellspacing="0" cellpadding="0" class="box">
								      <tr> 
								        	<td height="17" colspan="2" class="labeltext">&nbsp;</td>
								      </tr>
      								  <tr>
        									<td colspan="2">
        										<table width="100%" border="0" cellspacing=0 cellpadding=0>
										            <tr bgcolor="#FFFFFF"> 
										              <td class="tblheader">TAC</td>
										   		      <td class="tblheader">BRAND</td>
										   		      <td class="tblheader">MODEL</td>
										   		      <td class="tblheader">HWTYPE</td>
										   		      <td class="tblheader">OS</td>
										   		      <td class="tblheader">YEAR</td>
										           </tr>
										            <tr bgcolor="#FFFFFF"> 
										              <td  class="tblrows"> 11110001 </td>
										              <td  class="tblrows">Brand1</td>
										              <td  class="tblrows">Model1</td>
										              <td  class="tblrows">Hardware Type1</td>
										              <td  class="tblrows">OperatingSystem1</td>
										              <td  class="tblrows">1999</td>
										           </tr>
										            <tr bgcolor="#FFFFFF"> 
										              <td  class="tblrows"> 11110002 </td>
										              <td  class="tblrows">Brand2</td>
										              <td  class="tblrows">Model2</td>
										              <td  class="tblrows">Hardware Type2</td>
										              <td  class="tblrows">OperatingSystem2</td>
										              <td  class="tblrows">1999</td>
										           </tr>
										            <tr bgcolor="#FFFFFF"> 
										              <td  class="tblrows"> 11110003 </td>
										              <td  class="tblrows">Brand3</td>
										              <td  class="tblrows">Model3</td>
										              <td  class="tblrows">Hardware Type3</td>
										              <td  class="tblrows">OperatingSystem3</td>
										              <td  class="tblrows">1999</td>
										           </tr>
										            <tr bgcolor="#FFFFFF"> 
										              <td  class="tblrows"> 11110004 </td>
										              <td  class="tblrows">Brand4</td>
										              <td  class="tblrows">Model4</td>
										              <td  class="tblrows">Hardware Type4</td>
										              <td  class="tblrows">OperatingSystem4</td>
										              <td  class="tblrows">1999</td>
										           </tr>
										            <tr bgcolor="#FFFFFF"> 
										              <td  class="tblrows"> 11110005 </td>
										              <td  class="tblrows">Brand5</td>
										              <td  class="tblrows">Model5</td>
										              <td  class="tblrows">Hardware Type5</td>
										              <td  class="tblrows">OperatingSystem5</td>
										              <td  class="tblrows">1999</td>
										           </tr>
          									</table>
          				  				</td>
      			   					</tr>
								    <tr> 
								        	  <td colspan="2">&nbsp;</td>
								    </tr>
							      	<tr> 
							        	  <td width="42%">&nbsp;</td>
							              <td width="58%" class="btn-td"> 
							          			<input type="button" name="c_btnClose" value="Close" class="light-btn" onClick="window.close();"> 
							        	  </td>
							      	</tr>
    						    </table>
		 				  </td>
     			   </tr>
	 			   <tr> 
				        <td colspan="8" class="small-gap">&nbsp;</td>
			       </tr>
    		  </table>	
		 </td>
  	 </tr>
	 <tr> 
		    <td bgcolor="#00477F" class="small-gap" width="99%">&nbsp;</td>
		    <td class="small-gap" width="1%"><img src="<%=localBasePath%>/images/pbtm-line-end.jpg"></td>
	</tr>
</table>
</body>
</html>