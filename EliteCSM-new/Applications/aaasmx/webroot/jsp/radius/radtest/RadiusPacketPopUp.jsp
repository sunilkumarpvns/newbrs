
<%@page import="java.util.Set"%>
<%@page import="java.util.SortedSet"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.ArrayList"%><%@ include
	file="/jsp/core/includes/common/Header.jsp"%>
<%@ page
	import="com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData"%>
<%@ page
	import="com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryData"%>

<%@ page
	import="com.elitecore.elitesm.web.radius.radtest.forms.RadiusTestPopUpForm"%>


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
	    
	    var len = document.forms[0].selected.length;
	    if(isNaN(len))
		{
		   var attributeName = document.forms[0].elements['selected'].value;
		   window.opener.document.forms[0].paramName.value = attributeName;
		}
		else
		{
		    for (i=0; i<len; i++)
		    {
				if (document.forms[0].selected[i].checked == true){
			    window.opener.document.forms[0].paramName.value = document.forms[0].selected[i].value;
		 	    }
		 	}    
		}
		
		
		
		window.opener.document.forms[0].checkAction.value = "";
		window.close();
	}
</script>

<% 
	RadiusTestPopUpForm radiusTestPopUpForm = (RadiusTestPopUpForm)request.getAttribute("radiusTestPopUpForm");
	List dictionaryListInCombo = radiusTestPopUpForm.getDictionaryListInCombo();
	List dictionaryListByCriteria = radiusTestPopUpForm.getDictionaryListByCriteria();
	String action = radiusTestPopUpForm.getAction();
	int iIndex = 0;
%>

<html:form action="/radiusPacketPopUpByCriteria.do">
	<html:hidden name="radiusTestPopUpForm" property="action"
		value="Insert" />
	<html:hidden name="radiusTestPopUpForm" property="fieldValue" />

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
										<html:select name="radiusTestPopUpForm"
											property="dictionaryId">
											<html:option value="">
												<bean:message bundle="servermgrResources"
													key="netRadiusDictionary.dictionaryList.comboSelectLabel" />
											</html:option>
											<%
											 		Iterator itr = dictionaryListInCombo.iterator();
											 		while(itr.hasNext()){
											 		IDictionaryData netdictionaryData = (IDictionaryData)itr.next();
											 		String strdicId=String.valueOf(netdictionaryData.getDictionaryId());
											 	%>
											<html:option value="<%=strdicId%>"><%=netdictionaryData.getName()%></html:option>
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
										<html:text name="radiusTestPopUpForm" property="searchByName"
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
								<logic:iterate id="radiusTestPopUpBean"
									name="radiusTestPopUpForm" property="dictionaryListByCriteria"
									type="com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryData">
									<%
			            						Set set = radiusTestPopUpBean.getDictionaryParameterDetail();
			            						if(set!=null && set.size()>0){
			            						List setList = new ArrayList(set);
			            						Collections.sort(setList);
			            						%>
									<tr>

										<td align="left" class="table-header" valign="top" colspan="3"><%=radiusTestPopUpBean.getName()%></td>
									</tr>
									<% 
									         		Iterator iterator = setList.iterator();
												%>
									<tr style="display: none">
										<td align="left" class="tblfirstcol" valign="top" width="1%"
											style><input type="radio" name="selected" id="selected"
											value="dummy" checked="checked" /></td>
										<td align="left" class="tblrows" valign="top" width="1%">0</td>
										<td align="left" class="tblcol" valign="top">dummy</td>
									</tr>
									<%
									         		while(iterator.hasNext()){
									         			IDictionaryParameterDetailData dictionaryParameterDetailData = (IDictionaryParameterDetailData) iterator.next();	
									         	%>
									<tr>
										<td align="left" class="tblfirstcol" valign="top" width="1%">
											<input type="radio" name="selected" id="selected"
											value="<%=dictionaryParameterDetailData.getName()%>" />
										</td>
										<td align="left" class="tblrows" valign="top" width="1%"><%=(iIndex+1) %></td>
										<td align="left" class="tblcol" valign="top"><%=dictionaryParameterDetailData.getName()%></td>
									</tr>

									<% iIndex += 1; 
												}
										     }
		                					%>
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


