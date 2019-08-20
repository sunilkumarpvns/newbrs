<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page
	import="com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData"%>

<%@ page
	import="com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryData"%>
<%@ page
	import="com.elitecore.elitesm.web.servermgr.server.forms.SearchDictionaryPopUpForm"%>

<%@ page import="java.util.List"%>
<%@ page import="java.util.Iterator"%>

<%
    String basePath = request.getContextPath();
%>
<script>
	function  validateSearch()
	{
	document.forms[0].submit();
	}
	function  validateInsert()
	{
	//alert(document.forms[0].fieldValue.value);
	var field = window.opener.document.getElementById(document.forms[0].fieldValue.value);
	

	for (i=0; i<document.forms[0].selected.length; i++){

 		 if (document.forms[0].selected[i].checked == true){
 		 //alert(document.forms[0].selected[i].value);
 		 field.value = document.forms[0].selected[i].value;
 		 }
  			
		}
	
	//alert(field.value);
	window.close();
	}
</script>

<% 
	SearchDictionaryPopUpForm searchDictionaryForm = (SearchDictionaryPopUpForm)request.getAttribute("searchDictionaryPopUpForm");
	List dictionaryListInCombo = searchDictionaryForm.getDictionaryListInCombo();
	List dictionaryListByCriteria = searchDictionaryForm.getDictionaryListByCriteria();
	String action = searchDictionaryForm.getAction();
	int iIndex = 0;
%>

<html:form action="/searchDictionaryByCriteria.do">
	<html:hidden name="searchDictionaryPopUpForm" styleId="action"
		property="action" value="Insert" />
	<html:hidden name="searchDictionaryPopUpForm" styleId="fieldValue"
		property="fieldValue" />

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>

			<td colspan="2">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">

					<tr>
						<td width="81%" background="<%=basePath%>/images/popup-bkgd.jpg"
							valign="top">&nbsp;</td>
						<td width="3%"><img
							src="<%=basePath%>/images/popup-curve.jpg"></td>
						<td background="<%=basePath%>/images/popup-btn-bkgd.jpg"
							width="3%"><a href="#"><img
								src="<%=basePath%>/images/refresh.jpg" name="Image1"
								onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image1','','<%=basePath%>/images/refresh-hover.jpg',1)"
								border="0"></a></td>
						<td background="<%=basePath%>/images/popup-btn-bkgd.jpg"
							width="3%"><a href="#" onclick="window.print()"><img
								src="<%=basePath%>/images/print.jpg" name="Image2"
								onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image2','','<%=basePath%>/images/print-hover.jpg',1)"
								border="0"> </a></td>
						<td background="<%=basePath%>/images/popup-btn-bkgd.jpg"
							width="3%"><a href="#"><img
								src="<%=basePath%>/images/aboutus.jpg" name="Image3"
								onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image3','','<%=basePath%>/images/aboutus-hover.jpg',1)"
								border="0"></a></td>
						<td background="<%=basePath%>/images/popup-btn-bkgd.jpg"
							width="3%"><a href="#"><img
								src="<%=basePath%>/images/help.jpg" name="Image4"
								onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image4','','<%=basePath%>/images/help-hover.jpg',1)"
								border="0"></a></td>
						<td background="<%=basePath%>/images/popup-btn-bkgd.jpg"
							width="4%">&nbsp;</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<table name="MainTable" id="MainTable" cellSpacing="0" cellPadding="0"
		width="100%" border="0">

		<tr>
			<td width="10">&nbsp;</td>
			<td width="100%" colspan="3" valign="top" class="box">
				<table cellSpacing="0" cellPadding="0" width="100%" border="0">

					<tr>
						<td class="table-header" valign="bottom" colspan="3"><bean:message
								bundle="servermgrResources"
								key="netRadiusDictionary.dictionaryList.header" /></td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="3">
							<table width="97%" align="right" border="0" cellpadding="0"
								cellspacing="0">
								<tr>
									<td align="left" class="labeltext" valign="top" width="10%"><bean:message
											bundle="servermgrResources"
											key="netRadiusDictionary.dictionaryList.comboLabel" /></td>
									<td align="left" class="labeltext" valign="top" width="32%">
										<%if(dictionaryListInCombo!=null && !dictionaryListInCombo.isEmpty()){%>
										<html:select name="searchDictionaryPopUpForm"
											styleId="dictionaryId" property="dictionaryId">
											<html:option value="">
												<bean:message bundle="servermgrResources"
													key="netRadiusDictionary.dictionaryList.comboSelectLabel" />
											</html:option>
											<%
											 		Iterator itr = dictionaryListInCombo.iterator();
											 		while(itr.hasNext()){
											 		IDictionaryData netdictionaryData = (IDictionaryData)itr.next();
											 	%>
											<html:option value="<%=netdictionaryData.getDictionaryId()%>"><%=netdictionaryData.getName()%></html:option>
											<%
										 }
											 
										%>
										</html:select> <%} %>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="10%"><bean:message
											bundle="servermgrResources"
											key="netRadiusDictionary.name.label" /></td>
									<td align="left" class="labeltext" valign="top" width="32%">
										<html:text name="searchDictionaryPopUpForm"
											styleId="searchByName" property="searchByName"
											styleClass="flatfields" />
									</td>
								</tr>

								<tr>
									<%--<td class="btns-td" valign="middle">&nbsp;</td>  
									--%>
									<td align="left" class="labeltext" valign="top" width="5%">
										<input type="button" name="Search" width="5%"
										onclick="validateSearch()" value="   Search   "
										class="light-btn" />

									</td>
								</tr>


								<%--  Here to divide the rows	--%>

								<tr>
									<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
								</tr>


							</table>
						</td>
					</tr>

					<%
									if(action != null){
		
							    %>
					<tr>
						<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
					</tr>

					<tr>
						<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
					</tr>

					<tr>
						<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
					</tr>
					<tr>
						<td class="table-header" valign="bottom" colspan="8"><bean:message
								bundle="servermgrResources"
								key="netRadiusDictionary.listByCriteria.header" /></td>
					</tr>
					<tr>
						<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
					</tr>
					<tr>
						<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
					</tr>

					<tr>
						<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
					</tr>

					<tr>
						<td colspan="8">
							<table width="97%" border="0" border="0" cellpadding="0"
								cellspacing="0" align="right">

								<tr>
									<td align="left" class="labeltext" valign="top" colspan="3">
										<input type="button" name="Insert" onclick="validateInsert()"
										value="   Insert   " class="light-btn" />
									</td>
								</tr>

								<tr>
									<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
								</tr>

								<tr>
									<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
								</tr>

								<tr>
									<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
								</tr>

								<tr>
									<td align="left" class="tblheader" valign="top" width="1%"><bean:message
											bundle="servermgrResources"
											key="netRadiusDictionary.listByCriteria.radio.label" /></td>
									<td align="left" class="tblheader" valign="top" width="1%"><bean:message
											bundle="servermgrResources"
											key="netRadiusDictionary.listByCriteria.serial.label" /></td>
									<td align="left" class="tblheader" valign="top"><bean:message
											bundle="servermgrResources"
											key="netRadiusDictionary.listByCriteria.dictionary.name.label" /></td>
								</tr>

								<%
	    										if(dictionaryListByCriteria != null && dictionaryListByCriteria.size() > 0){
											%>
								<logic:iterate id="searchDictionaryPopUpBean"
									name="searchDictionaryPopUpForm"
									property="dictionaryListByCriteria"
									type="com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData">

									<tr>

										<td align="left" class="tblfirstcol" valign="top" width="1%">
											<%--<input type="radio" name="selected" id="<%=(iIndex+1) %>" value="<%=searchDictionaryPopUpBean.getVendorId()+":"+((searchDictionaryPopUpBean.getVendorParameterOveridden().equalsIgnoreCase("Y"))?searchDictionaryPopUpBean.getVendorParameterId():searchDictionaryPopUpBean.getRadiusRFCDictionaryParameterId())%>"/>
--%> <input type="radio" name="selected" id="<%=(iIndex+1) %>"
											value="<%=searchDictionaryPopUpBean.getVendorId()+":"+searchDictionaryPopUpBean.getVendorParameterId()%>" />

										</td>

										<td align="left" class="tblrows" valign="top" width="1%"><%=(iIndex+1) %></td>

										<td align="left" class="tblcol" valign="top"><%=searchDictionaryPopUpBean.getName()%></td>



									</tr>

									<% iIndex += 1; %>



								</logic:iterate>


								<%
											} else{
											%>

								<tr>
									<td align="center" class="tblfirstcol" colspan="8">No
										Records Found</td>

								</tr>



								<%
									    	
									    	}
									    	%>

								<tr>
									<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
								</tr>

								<tr>
									<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
								</tr>

								<tr>
									<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
								</tr>


								<tr>
									<td align="left" class="labeltext" valign="top" width="5%">
										<input type="button" name="Insert" width="5%"
										onclick="validateInsert()" value="   Insert   "
										class="light-btn" />
									</td>
								</tr>


								<tr>
									<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
								</tr>


							</table>
						</td>
					</tr>

					<%
							    }
							    %>

				</table>
			</td>
		<tr>
	</table>

	</td>
	</tr>
	</table>




	<table cellpadding="0" cellspacing="0" border="0">
		<tr>
			<td colspan="2" class="small-gap">&nbsp;</td>
		</tr>

		<tr>
			<td bgcolor="#00477F" class="small-gap" width="99%">&nbsp;</td>
			<td class="small-gap" width="1%"><img
				src="<%=basePath%>/images/pbtm-line-end.jpg"></td>
		</tr>
	</table>
</html:form>


