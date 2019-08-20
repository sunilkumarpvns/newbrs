<%@ page import="java.text.*,java.util.*"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>

<%
    String basePath = request.getContextPath();
    String forwardPath = request.getContextPath()+"/viewDictionary.do";
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<%@page import="com.elitecore.elitesm.web.diameter.dictionary.forms.ListDiameterdicForm"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.dictionary.data.DiameterdicData"%>

<script>
	function show(){
		document.forms[0].action.value = 'show';
		
		
		var selectArray = document.getElementsByName('select');
	
	if(selectArray.length>0){
	
			var b = true;
			for (i=0; i<selectArray.length; i++){
		
			 		 if (selectArray[i].checked == false){  			
			 		 	b=false;
			 		 }
			 		 else{
			 		 	
						b=true;
						break;
					}
				}
			if(b==false){
			alert("Please select atleast one dictionary to Show.")
			
			}else{
			
					document.forms[0].submit();
			}
	}
	else{
		alert("No Records Found For Show Operation! ")
	}
		
		
		
		
	}
	function hide(){
		document.forms[0].action.value = 'hide';
		
				var selectArray = document.getElementsByName('select');
	
	if(selectArray.length>0){
	
			var b = true;
			for (i=0; i<selectArray.length; i++){
		
			 		 if (selectArray[i].checked == false){  			
			 		 	b=false;
			 		 }
			 		 else{
			 		 	
						b=true;
						break;
					}
				}
			if(b==false){
			
					alert("Please select atleast one dictionary to Hide.")
			
			}else{
			
					document.forms[0].submit();
			}
	}
	else{
		alert("No Records Found For Hide Operation! ")
	}
	}
	
	
	
	function removeData(){
	document.forms[0].action.value = 'delete';
		
	var selectArray = document.getElementsByName('select');
	
	if(selectArray.length>0){
	var b = true;
	for (i=0; i<selectArray.length; i++){

	 		 if (selectArray[i].checked == false){  			
	 		 	b=false;
	 		 }
	 		 else{
	 		 	
				b=true;
				break;
			}
		}
	if(b==false){
	alert("Selection Required To Perform Delete Operation.")
	
	}else{
		var r=confirm("This will delete the selected items. Do you want to continue ?");
			if (r==true)
  			{
  				document.forms[0].submit();
  			}
		
	}
	}
	else{
		alert("No Records Found For Delete Operation! ")
	}
}
	

	function create(){
		
		document.forms[0].action.value = 'create';
		document.forms[0].submit();
	}
	function download(){
		count =0;
		var selectVar = document.getElementsByName('select');
		for(i=0;i<selectVar.length;i++){
			if(selectVar[i].checked == true){
				count++;			
			}
		}
		if(count == 1){
			document.forms[0].action.value = 'download';		
			document.forms[0].submit();
		}else{
			if(count < 1)
				alert('Atleast one Dictionary must be selected');
			else
				alert('Only one Dictionary can be downloaded at a time');			
		}
	}
	
	function  checkAll(){
	var selectVar = document.getElementsByName('select');
	 	if( document.forms[0].toggleAll.checked == true) {
		 	for (i = 0; i < selectVar.length;i++)
				selectVar[i].checked = true ;
	    } else if (document.forms[0].toggleAll.checked == false){
			for (i = 0; i < selectVar.length; i++)
				selectVar[i].checked = false ;
		}
	}
    setTitle('<bean:message bundle="diameterResources" key="dictionary.module.name"/>');
</script>


<%

    String strDatePattern = "dd MMM, yyyy hh:mm:ss";
    SimpleDateFormat dateForm = new SimpleDateFormat(strDatePattern);
    List lstDictionaryData = ((ListDiameterdicForm)request.getAttribute("listDiameterdicForm")).getDictionaryList();
    System.out.println("lstDictionaryData size is:"+lstDictionaryData.size());
    int iIndex =0;
    int h=6;
%>

<html:form action="/miscDiameterdic">
	<html:hidden styleId="checkboxname" property="checkboxname"
		value="c_bSelected" />
	<html:hidden styleId="action" name="listDiameterdicForm"
		property="action" />
	<html:hidden styleId="colNo" property="colNo" value="3" />
	<html:hidden styleId="sortOrder" property="sortOrder" value="false" />
	<html:hidden styleId="c_strActionMode" property="c_strActionMode"
		value="" />

	<table cellpadding="0" cellspacing="0" border="0"
		width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td class="table-header" colspan="4">DICTIONARY LIST</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle"><input type="button"
										name="c_btnCreate" tabindex="1"
										onclick="javascript:location.href='<%=basePath%>/initCreateDiameterdic.do'"
										value="Create" class="light-btn" /> <html:button
											property="c_btnShow" onclick="show()" value="Show"
											styleClass="light-btn" tabindex="2"/> <html:button property="c_btnHide"
											onclick="hide()" value="Hide" styleClass="light-btn"  tabindex="3"/> <html:button
											property="c_btnDownload" onclick="download()"
											value="Download" styleClass="light-btn" tabindex="4"/> <html:button
											property="c_btnDelete" onclick="removeData()" value="Delete"
											styleClass="light-btn" tabindex="5" /></td>
									<td width="50%">&nbsp;</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle" colspan="3">
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td class="small-gap" colspan="3">&nbsp;</td>
											</tr>
											<tr>
												<td colspan="3">
													<table width="99%" cols="9" id="listTable" type="tbl-list"
														border="0" cellpadding="0" cellspacing="0">
														<tr>
															<td align="center" class="tblheader" valign="top"
																width="4%"><input type="checkbox" name="toggleAll"
																onclick="checkAll()"  tabindex="6"/></td>
															<td align="center" class="tblheader" valign="top"
																width="40px"><bean:message
																	key="general.serialnumber" /></td>
															<td align="left" class="tblheader" valign="top"
																width="10%"><bean:message key="general.vendorid" /></td>
															<td align="center" class="tblheader" valign="top"
																width="10%"><bean:message
																	bundle="diameterResources" key="dictionary.vendorname" /></td>
															<td align="center" class="tblheader" valign="top"
																width="10%"><bean:message
																	key="general.applicationname" /></td>
															<td align="center" class="tblheader" valign="top"
																width="*"><bean:message key="general.description" /></td>
															<td align="center" class="tblheader" valign="top"
																width="17%"><bean:message
																	key="general.lastmodifieddate" /></td>
															<td align="center" class="tblheader" valign="top"
																width="40px"><bean:message key="general.status" /></td>
															<td align="center" class="tblheader" valign="top"
																width="40px"><bean:message key="general.edit" /></td>
														</tr>
														<%
    if(lstDictionaryData != null && lstDictionaryData.size() > 0){
%>
														<logic:iterate id="dictionaryData"
															name="listDiameterdicForm" property="dictionaryList"
															type="com.elitecore.elitesm.datamanager.diameter.dictionary.data.DiameterdicData">
															<%
        DiameterdicData dData = (DiameterdicData)lstDictionaryData.get(iIndex);
        String strDictionaryId = dData.getDictionaryId();
        String strDescription = dData.getDescription();
        Date dtLastModifiedDate = dData.getLastModifiedDate(); 
%>
															<tr>
																<td align="center" class="tblfirstcol"><input
																	type="checkbox" name="select" tabindex="<%=h++ %>"
																	value="<bean:write name="dictionaryData" property="dictionaryId"/>" />
																</td>
																<td align="center" class="tblrows"><%=(iIndex+1) %></td>
																<td align="left" class="tblrows"><bean:write
																		name="dictionaryData" property="vendorId" /></td>
																<td align="left" class="tblrows"><bean:write
																		name="dictionaryData" property="vendorName" /></td>
																<td align="left" class="tblrows"><bean:write
																		name="dictionaryData" property="applicationName" /></td>
																<td align="left" class="tblrows"><%=(strDescription == null ? "-" : (strDescription.trim().equalsIgnoreCase("") == true ? "-" : strDescription))%></td>
																<td class="tblrows"><%=EliteUtility.dateToString(dtLastModifiedDate, ConfigManager.get(ConfigConstant.DATE_FORMAT))%></td>

																<logic:equal name="dictionaryData"
																	property="commonStatusId" value="CST01">
																	<td align="center" class="tblrows"><img tabindex="<%=h++ %>"
																		src="<%=basePath%>/images/show.jpg" /></td>
																</logic:equal>
																<logic:notEqual name="dictionaryData"
																	property="commonStatusId" value="CST01">
																	<td align="center" class="tblrows"><img
																		src="<%=basePath%>/images/hide.jpg" /></td>
																</logic:notEqual>
																<td align="center" class="tblrows"><a
																	href="<%=basePath%>/jsp/diameter/dictionary/DiameterdicMgmt.jsp?dictionaryId=<bean:write name="dictionaryData" property="dictionaryId"/>" tabindex="<%=h++ %>">
																		<img src="<%=basePath%>/images/edit.jpg" border="0" />
																</a></td>
															</tr>
															<% iIndex += 1; %>
														</logic:iterate>
														<%
    }else{
%>
														<tr>
															<td align="center" class="tblfirstcol" colspan="9">No
																Records Found.</td>
														</tr>
														<%
    }
%>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>

								<tr>
									<td class="btns-td" valign="middle"><input type="button" tabindex="<%=h++ %>"
										name="c_btnCreate" tabindex="9"
										onclick="javascript:location.href='<%=basePath%>/initCreateDiameterdic.do'"
										value="Create" class="light-btn" /> <html:button tabindex="<%Integer.toString(h+1) %>"
											property="c_btnShow" onclick="show()" value="Show"
											styleClass="light-btn" /> <html:button property="c_btnHide"
											onclick="hide()" value="Hide" styleClass="light-btn"  tabindex="<%Integer.toString(h+1) %>"/> <html:button
											property="c_btnDownload" onclick="download()" tabindex="<%Integer.toString(h+1) %>"
											value="Download" styleClass="light-btn" /> <html:button tabindex="<%Integer.toString(h+1) %>"
											property="c_btnDelete" onclick="removeData()" value="Delete"
											styleClass="light-btn" /></td>

									<td width="50%">&nbsp;</td>
								</tr>
								<tr>
									<td class="small-gap" width="50%" colspan="2">&nbsp;</td>
								</tr>
							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>
</html:form>