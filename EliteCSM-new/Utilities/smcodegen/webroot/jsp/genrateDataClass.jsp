<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.ArrayList" />
<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page
	import="com.elitecore.elitecodegen.controller.form.TableBean" />
<jsp:directive.page
	import="com.elitecore.elitecodegen.controller.form.DbResourceForm" />
<jsp:directive.page
	import="com.elitecore.elitecodegen.base.EliteCodeGenConstant" />
<jsp:directive.page import="java.util.HashMap" />
<jsp:directive.page import="java.util.Map" />
<jsp:directive.page
	import="com.elitecore.elitecodegen.controller.form.DataClassBean" />
<jsp:directive.page
	import="com.elitecore.elitecodegen.base.TableDataBean" />
<jsp:directive.page import="com.elitecore.elitecodegen.base.ColumnBean" />


<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/taglibs-input.tld" prefix="elitecore"%>


<%
String path = request.getContextPath();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<html:html locale="true">
<head>
	<html:base />
	<title>EliteCodeGen</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<link href="<%=path%>/css/style.css" rel="stylesheet" type="text/css">
	<link href="../css/style.css" rel="stylesheet" type="text/css">
</head>

<script type="text/javascript" language="java">

function goNext() {
  	document.forms[0].op.value='1';
  	document.forms[0].submit();
 }
 function goBack() {
  	document.forms[0].op.value='-1';
  	document.forms[0].submit();


 }
 
 function goBack() {
  	document.forms[0].op.value='-1';
  	document.forms[0].submit();


 }
 
 
 function  checkBox()
 {

 
 if( document.forms[0].checkBoxCon.checked == true)
 {

 for (i = 0; i < document.forms[0].chioce.length;i++)
	document.forms[0].chioce[i].checked = true ;
}


else if (document.forms[0].checkBoxCon.checked == false)
{
 
	for (i = 0; i < document.forms[0].chioce.length; i++)
	document.forms[0].chioce[i].checked = false ;
 	
 }
}
 
 
 function genrateSource()
 {
 	document.forms[0].op.value='0';
  	document.forms[0].submit();
 	 
 }
</script>

<body>

	<%
		int seq = Integer.parseInt(request.getAttribute("seq").toString());
		Map dataClassMap = (HashMap) request.getSession(false)
				.getAttribute(EliteCodeGenConstant.DataClassMap);
		Integer i = new Integer(seq);
		TableDataBean tableDtaBean = (TableDataBean) dataClassMap.get(i);
	%>

	<table width="100%" height="100%">
		<tr>
			<td height="53" colspan="3">&nbsp;
				



			</td>
		</tr>
		<tr>
			<td width="1%">&nbsp;
				


			</td>
			<td width="99%" align="center">
				<form method="post" action="<%=path%>/navigateDataClassGen.do">
					<table width="100%" height="25%" border="0" align="center"
						cellpadding="0" cellspacing="0">

						<tr style="height: 20%">
							<td colspan="2">
							<td width="12%">
							<td colspan="2">
						</tr>
						<tr>
							<td height="16%" colspan="5">
								<div align="center" class="title">
									EliteCodeGen
								</div>
							</td>
						</tr>
						<tr>

							<td height="19%" colspan="5">
								<div align="center" class="sidebarHeader">
									<%=tableDtaBean.getStrTableName().trim()%>
								</div>
							</td>
						</tr>
						<tr>
							<td height="19%" class="titlebar">&nbsp;
								
							</td>
							<td width="8%" height="19%" class="titlebar">&nbsp;
								
							</td>
							<td height="19%" class="titlebar">
								Package Name
							</td>
							<td height="19%" colspan="2" class="titlebar">

								<%
								if (tableDtaBean.getStrPackageName() == null) {
								%>
								<div align="left">
									<input name="strPackageName" type="text" class="box1" size="50"%>
								</div>
								<%
								} else {
								%>
								<div align="left">
									<input name="strPackageName" type="text" class="box1" value="<%=tableDtaBean.getStrPackageName().trim()%>" size="50"%>
								</div>

<%} %>
							</td>
						</tr>
						<tr>
							<td height="19%" class="titlebar">&nbsp;
								
							</td>
							<td height="19%" class="titlebar">&nbsp;
								
							</td>
							<td height="19%" class="titlebar">
								ClassName
							</td>
							<td height="19%" colspan="2" class="titlebar">
								<div align="left">
									<input name="strClassName" type="text" class="box1" value="<%=tableDtaBean.getStrTableName().toLowerCase()%>" size="50"
										%>
								</div>

							</td>
						</tr>
						<tr>
							<td height="19%" colspan="5">&nbsp;
								
							</td>
						</tr>

						<tr>
							<td height="19%" colspan="5" align="center">
								<table width="100%" border="0" cellspacing="2" cellpadding="2">
									<tr class="subtitle">
										<td width="1%">
											<div align="left"></div>
										</td>
										<td width="98%">
											<table width="100%" border="0" cellspacing="0"
												cellpadding="0">
												<tr>
													<td>
														<table width="100%" border="1" cellpadding="0"
															cellspacing="0">
															<tr class="subtitle">
																<td width="5%">
																	<div align="center">
																	  <input type="checkbox" name="checkBoxCon" value="checkbox" checked="checked" onchange="checkBox()">
																	</div>
																</td>
																<td width="27%">
																	<div align="center">
																		Column Name
																	</div>
																</td>
																<td width="26%">
																	<div align="center">
																		Java Name
																	</div>
																</td>
																<td width="7%">
																	<div align="center">
																		Type
																	</div>
																</td>
																<td width="6%">
																	<div align="center">
																		Size
																	</div>
																</td>
																<td width="29%">
																	<div align="center">
																		Java Type
																	</div>
																</td>
															</tr>
															<%
																List tableDetillList = tableDtaBean.getCloumnList();
																Iterator itColumnList = tableDetillList.iterator();
																ColumnBean columnBean = null;
																int count = 0;
																while (itColumnList.hasNext()) {
																	columnBean = (ColumnBean) itColumnList.next();
															%>
															<tr>
																<td>
																	<%
																	if (columnBean.isSelectionStaus()) {
																	%>
																	<div align="center">
																		<input type="checkbox" name="chioce"
																			value="<%=count++%>" checked="checked">
																	</div>
																	<%
																	} else {
																	%>
																	<div align="center">
																		<input type="checkbox" name="chioce"
																			value="<%=count++%>">
																	</div>
																	<%
																	}
																	%>
																</td>
																<td>&nbsp;&nbsp;
																	<%=columnBean.getName()%>

																</td>
																<td>
																	<%
																	if (columnBean.isSelectionStaus()) {
																	%>

																	<div align="center">
																		<input name="strJavaName" type="text" class="box1"
																			value="<%=columnBean.getJavaName()%>" size="30" maxlength="30">
																	</div>

																	<%
																	} else {
																	%>

																	<div align="center">
																		<input name="strJavaName" type="text" class="box1"
																			value="<%=columnBean.getName().toLowerCase()%>"
																			size="30">
																	</div>

																	<%
																	}
																	%>




																</td>
																<td>
																	<div align="center">
																		<%=columnBean.getType()%>
																	</div>
																</td>
																<td align="center">
																	<%=columnBean.getSize()%>
																</td>
																<td align="center">
																	
																	<input name="strJavaType" type="text" class="box1"
																		value="<%=columnBean.getJavaType().trim()%>" size="45">
																	
																	
																</td>
															</tr>
															<%} %>
														</table>
														<input type="hidden" value="<%=seq%>" name="seq">
														<input type="hidden" value="1" name="op">

													</td>
												</tr>
											</table>
										</td>
										<td width="1%">&nbsp;
											
										</td>
									</tr>

								</table>
							</td>
						</tr>
						<tr>
							<td width="13%" align="center">
								<div align="right">
								</div>
								<div align="right">
								</div>
								<FONT class="error">&nbsp; </FONT><font class="error">&nbsp;
								</font>
								<div align="right">
									<font class="error"> </font>

								</div>
								<div align="left">
								</div>
							</td>


							<td width="8%" align="center">
								<%
								if (seq != 0) {
								%>
								<div id="backButton">
									<input type="button" name="Submit1" value="Back"
										onclick="goBack()">
								</div>
								<%
								}
								%>
							</td>


							<td width="12%" align="center">&nbsp;
								
							</td>


							<td width="48%" align="center">

								<%
								if (seq == dataClassMap.size() - 1) {
								%>
								<div align="right">
									<input type="button" name="Submit2" value="Genrate Source"
										onclick="genrateSource()">
								</div>
								<%
								} else {
								%>
								<div align="right">
									<input type="button" name="Submit2" value="Next"
										onclick="goNext()">
								</div>
								<%
								}
								%>
							</td>

							<td width="19%" align="center">&nbsp;
								
							</td>
						</tr>
						<tr>
							<td height="25%" colspan="5">
								&nbsp;
								<FONT class="error"> </FONT>
							</td>
						</tr>
						<tr style="height: 20%">
							<td colspan="2">
							<td>
							<td colspan="2">
						</tr>
					</table>
				</form>
			</td>
			<td width="0%">&nbsp;
				
			</td>
		</tr>
		<tr style="height: 20%">
			<td></td>
		</tr>
		<tr>
			<td height="20%" colspan="3"></td>
		</tr>
	</table>

</body>
</html:html>
