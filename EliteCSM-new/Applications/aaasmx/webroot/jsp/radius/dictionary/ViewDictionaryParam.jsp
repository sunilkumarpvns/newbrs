<%@ include file="/jsp/core/includes/common/Header.jsp"%>





<% 
	String basePath = request.getContextPath();
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	 <tr> 
    	 <td valign="top" class="box" colspan="8"> 
    	 <html:form action="/viewDictionaryParam" >
    	 <bean:define id="dictionaryParameterDetailBean" name="viewDictionaryParamForm" property="dictionaryParamDetailData" type="com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData" />
    	 <bean:define id="dictionaryBean" name="viewDictionaryParamForm" property="dictionaryData" type="com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryData" />    	 
			  <table width="100%" border="0" cellspacing="0" cellpadding="0" >
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
			  
        									<td width="38%" class="blue-text-bold" valign="bottom"><bean:message bundle="radiusResources" key="dictionary.view" /></td>
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
		 				  <td colspan="8" > 
			
    							<table width="100%" border="0" cellspacing="0" cellpadding="0" >
								      <tr> 
								        	<td height="17" colspan="2" class="labeltext">&nbsp;</td>
								      </tr>
      								  <tr>
        									<td colspan="2">
        										<table width="100%" border="0" cellspacing=0 cellpadding=0>
		
										            <tr bgcolor="#FFFFFF"> 
										              <td  class="labeltext" width="30%" valign="top"><bean:message bundle="radiusResources"  key="dictionary.param.dictionaryname" /></td>
										              <td  class="labeltext" width="70%" valign="top"><bean:write name="dictionaryBean" property="name" /></td>
										           </tr>
										            <tr bgcolor="#FFFFFF"> 
										              <td  class="labeltext" valign="top"><bean:message bundle="radiusResources" key="dictionary.param.attributename" /></td>
										              <td  class="labeltext" valign="top"><bean:write name="dictionaryParameterDetailBean" property="name" /></td>
										           </tr>
										            <tr bgcolor="#FFFFFF"> 
										              <td  class="labeltext" valign="top"><bean:message bundle="radiusResources" key="dictionary.param.rfc.dictionary.parameterid" /></td>
										              <td  class="labeltext" valign="top"><bean:write name="dictionaryParameterDetailBean" property="radiusRFCDictionaryParameterId" /></td>
										           </tr>
										            <tr bgcolor="#FFFFFF"> 
										              <td  class="labeltext" valign="top"><bean:message bundle="radiusResources" key="dictionary.param.predefinedvalues" /></td>
										              <td  class="labeltext" valign="top"><bean:write name="dictionaryParameterDetailBean" property="predefinedValues" /></td>
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
    		  </html:form>
		 </td>
  	 </tr>
	 <tr> 
		    <td bgcolor="#00477F" class="small-gap" width="99%">&nbsp;</td>
		    <td class="small-gap" width="1%"><img src="<%=basePath%>/images/pbtm-line-end.jpg"></td>
	</tr>
</table>

