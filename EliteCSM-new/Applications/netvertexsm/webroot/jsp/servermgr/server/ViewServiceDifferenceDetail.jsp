<%@ include file="/jsp/core/includes/common/Header.jsp"%>





<table width="100%" border="0" cellspacing="0" cellpadding="0">
	 <tr> 
    	 <td valign="top" class="box" colspan="8"> 
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
				   <tr> 
		 				  <td colspan="8"> 
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
			  						 <tr> 
			  
        									<td width="38%" class="blue-text-bold" valign="bottom">Service Configuration Differences</td>
			  								<td class="blue-text-bold" width="43%"><a href="#"><img src="<%=basePath%>/images/pdf.jpg" name="Image21" onMouseOver="MM_swapImage('Image21','','<%=basePath%>/images/pdf-hover.jpg',1)" border="0" alt="Save as PDF"></a><a href="#"><img src="<%=basePath%>/images/html.jpg" name="Image31" onMouseOver="MM_swapImage('Image31','','<%=basePath%>/images/html-hover.jpg',1)" border="0" alt="Save as HTML"></a></td>
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
			
    							<table width="100%" border="0" cellspacing="0" cellpadding="0" >
								      <tr> 
								        	<td height="17" colspan="2" class="labeltext">&nbsp;</td>
								      </tr>
					                  <tr>
					                    <td colspan="3">
					                      <table width="100%" cols="8" id = "listTable" type="tbl-list" border="0" cellpadding="0" cellspacing="0">
					                        <tr>
					                          <td align="center" class="tblheader" valign="top" width="4%">
					                            <input type="checkbox" value="checkbox" />
					                          </td>
					                          <td align="center" class="tblheader" valign="top" width="5%" >Sr.No.</td>
					                          <td align="left" class="tblheader" valign="top" width="30%" >Driver</td>
					                          <td align="left" class="tblheader" valign="top" width="20%" >Driver Type</td>                          
					                          <td align="center" class="tblheader" valign="top" width="15%" >Configured in Server</td>
					                          <td align="center" class="tblheader" valign="top" width="15%" >Configured in Database</td>
					                          <td align="center" class="tblheader" valign="top" width="15%">Configuration Matches</td>
					                        </tr>
					                        <tr>
					                          <td align="center" class="tblfirstcol" valign="top" >
					                            <input type="checkbox" value="checkbox" />
					                          </td>
					                          <td align="center" class="tblfirstcol" valign="top" >1</td>
					                          <td align="left" class="tblrows" valign="top" >IPPool Service Driver</td>
					                          <td align="left" class="tblrows" valign="top" >IPPool Service Driver</td>                          
					                          <td align="center" class="tblrows" valign="center" ><img src="<%=basePath%>/images/tick.jpg" name="new" border="0" /></td>
					                          <td align="center" class="tblrows" valign="center" ><img src="<%=basePath%>/images/tick.jpg" name="new" border="0" /></td>
					                          <td align="center" class="tblrows" valign="center" ><img src="<%=basePath%>/images/cross.jpg" name="new" border="0" /></td>                          
					                        </tr>
					                        <tr>
					                          <td align="center" class="tblfirstcol" valign="top" >
					                            <input type="checkbox" value="checkbox" />
					                          </td>
					                          <td align="center" class="tblfirstcol" valign="top" >2</td>
					                          <td align="left" class="tblrows" valign="top" >DB IPPool Driver</td>
					                          <td align="left" class="tblrows" valign="top" >DB IPPool Driver</td>                                                    
					                          <td align="center" class="tblrows" valign="center" ><img src="<%=basePath%>/images/tick.jpg" name="new" border="0" /></td>
					                          <td align="center" class="tblrows" valign="center" ><img src="<%=basePath%>/images/cross.jpg" name="new" border="0" /></td>
					                          <td align="center" class="tblrows" valign="center" ><img src="<%=basePath%>/images/cross.jpg" name="new" border="0" /></td>                          
					                        </tr>
					                        
					                      </table>
					                    </td>
					                  </tr>
								    <tr> 
								        	  <td colspan="2">&nbsp;</td>
								    </tr>
							      	<tr> 
							        	  <td width="10%">&nbsp;</td>
							              <td width="90%" class="btn-td"> 
          										<input type="button" name="c_btnUpdate"  onclick="window.open('<%=basePath%>/jsp/servermgr/ViewConfigDifferenceDetail.jsp','ConfigServiceDifference','top=0, left=0, height=600, width=700, scrollbars=yes, status')"  id="c_btnUPdate"  value="Show Difference"  class="light-btn">                   							              
          										<input type="button" name="c_btnUpdateServer" id="c_btnUPdate"  value="Update Server"  class="light-btn">                           							                        										
          										<input type="button" name="c_btnUpdateDatabase" id="c_btnUPdate"  value="Update Database"  class="light-btn">                                     	          										
							          			<input type="button" name="c_btnClose" value="Close" class="light-btn" onClick="window.close();"> 
							        	  </td>
							      	</tr>
								    <tr> 
								        	  <td colspan="2">&nbsp;</td>
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
		    <td class="small-gap" width="1%"><img src="<%=basePath%>/images/pbtm-line-end.jpg"></td>
	</tr>
</table>

