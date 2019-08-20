<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.ArrayList" />
<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page
	import="com.elitecore.elitecodegen.controller.form.TableBean" />
<jsp:directive.page
	import="com.elitecore.elitecodegen.controller.form.DbResourceForm" />
<jsp:directive.page
	import="com.elitecore.elitecodegen.base.EliteCodeGenConstant" />
<jsp:directive.page
	import="com.elitecore.elitecodegen.base.TableDataBean" />


<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"
	prefix="html"%>


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

<body>

	<table width="100%" height="100%">
		<tr>
			<td height="53" colspan="3">
				&nbsp;




			</td>
		</tr>
		<tr>
			<td width="2%">
				&nbsp;




			</td>
			<td width="96%" align="center">
				<form name="packageGenration" method="post"
					action="<%=path%>/packageGenrationAcion.do">
					<table width="100%" height="25%" border="0" align="center"
						cellpadding="0" cellspacing="0">
						<tr style="height: 20%">
							<td colspan="2">
							<td width="13%">
							<td colspan="3">
						</tr>
						<tr>
							<td height="16%" colspan="6">
								<div align="center" class="title">SmCodeGen 
								</div>
							</td>
						</tr>
						<tr>
							<%
							TableDataBean tableDataBean = (TableDataBean) session.getAttribute(EliteCodeGenConstant.TableDataBean);
							%>
							<td height="19%" colspan="6">
								<div align="center" class="sidebarHeader">
									<%=tableDataBean.getStrTableName().toUpperCase()%>
								</div>
							</td>
						</tr>
						<tr>
							<td height="19%" class="titlebar">
								&nbsp;
							</td>
							<td height="19%" class="titlebar">
								&nbsp;
							</td>
							<td height="19%" class="titlebar">
								Application Name
							</td>
							<td height="19%" class="titlebar" align="left">
								<input name="strApplicationName" type="text" size="45">
							</td>
							<td class="titlebar">&nbsp; 
								(Ex:&nbsp; elitesm) 
							</td>
							<td class="titlebar">
								&nbsp;
							</td>
						</tr>
						<tr>
							<td width="2%" height="19%" class="titlebar">
								&nbsp;
							</td>
							<td width="16%" height="19%" class="titlebar">
								&nbsp;
							</td>
							<td height="19%" class="titlebar"><span style='font-size: 12pt; font-family: "Times New Roman";'>Module</span> Name 
							</td>
							<td width="35%" height="19%" class="titlebar" align="left">

								<input name="strModualName" type="text" size="45">
							</td>
							<td width="27%" class="titlebar">&nbsp; 
								(Ex :&nbsp; LDAPDatasource) 
							</td>
							<td width="1%" class="titlebar">
								&nbsp;
							</td>
						</tr>
						<tr>
							<td height="19%" class="titlebar">
								&nbsp;
							</td>
							<td height="19%" class="titlebar">
								&nbsp;
							</td>
							<td height="19%" class="titlebar">
								Parent <span style='font-size: 12pt; font-family: "Times New Roman";'>Module</span> Name
							</td>
							<td height="19%" class="titlebar" align="left">

								<input name="strSubMoudalName" type="text" size="45">
							</td>
							<td height="19%" class="titlebar">&nbsp; 
								(Ex :&nbsp; radius.datasource) 
							</td>
							<td height="19%" class="titlebar">
								&nbsp;
							</td>
						</tr>
						<tr>
							<td height="19%" class="titlebar">
								&nbsp;
							</td>
							<td height="19%" class="titlebar">
								&nbsp;
							</td>
							<td height="19%" class="titlebar">
								Table Name
							</td>
							<td height="19%" class="titlebar" align="left">
								<input name="strTableName" type="text" size="45"
									value="<%=tableDataBean.getStrTableName()%>">
							</td>
							<td width="27%" class="titlebar">
								&nbsp;
							</td>
							<td width="1%" class="titlebar">
								&nbsp;
							</td>
						</tr>
					</table>


					<table width="100%" border="1" cellspacing="0" cellpadding="0">
						<tr>
							<td>
								&nbsp;
							</td>
							<td>
								&nbsp;
							</td>

							<td>
								&nbsp;
							</td>
							<td>
								&nbsp;
							</td>
							<td>
								&nbsp;
							</td>
							<td>
								&nbsp;
							</td>
						</tr>
						<tr>
							<td width="7%">
								&nbsp;

							</td>
							<td width="7%" align="center">

								<input type="checkbox" name="chkHibernet" value="1" checked>
							</td>
							<td width="37%">
								&nbsp;&nbsp;&nbsp;HiberNet (hbm+dataManager)
							</td>
							<td width="4%">
								&nbsp;
							</td>
							<td width="29%">
								&nbsp;

							</td>
							<td width="9%">
								&nbsp;

							</td>
						</tr>
						<tr>
							<td>
								&nbsp;

							</td>
							<td align="center">

								<input type="checkbox" name="chkBlManager" value="1" checked>
							</td>
							<td>
								&nbsp;&nbsp;&nbsp;BlManager
							</td>
							<td align="center">
								<input name="jspRadiol" type="radio" value="1" checked>

							</td>
							<td>
								&nbsp;&nbsp;&nbsp;Jsp (Search)
							</td>
							<td>
								&nbsp;

							</td>
						</tr>
						<tr>
							<td>
								&nbsp;

							</td>
							<td align="center">

								<input name="chkDataManager" type="checkbox" value="1" checked>
							</td>
							<td>
								&nbsp;&nbsp;&nbsp;DataManager
							</td>
							<td align="center">
								<input name="jspRadiol" type="radio" value="2">

							</td>
							<td>
								&nbsp;&nbsp;&nbsp;Jsp (List)
							</td>
							<td>
								&nbsp;

							</td>
						</tr>
						<tr>
							<td>
								&nbsp;

							</td>
							<td align="center" s>

								<input name="chkConfig" type="checkbox" value="1" checked>
							</td>
							<td>
								&nbsp;&nbsp;&nbsp;Config (cfg.xml)
							</td>
							<td>
								&nbsp;

							</td>
							<td>
								&nbsp;

							</td>
							<td>
								&nbsp;

							</td>
						</tr>
						<tr>
							<td>
								&nbsp;

							</td>
							<td align="center">

								<input name="chkStruts" type="checkbox" value="1" checked>
							</td>
							<td>
								&nbsp;&nbsp;&nbsp;struts
							</td>
							<td>
								&nbsp;

							</td>
							<td>
								&nbsp;

							</td>
							<td>
								&nbsp;

							</td>
						</tr>
						<tr>
							<td>
								&nbsp;
							</td>
							<td>
								&nbsp;
							</td>
							<td>
								&nbsp;
							</td>
							<td>
								&nbsp;
							</td>
							<td align="right">
								<input type="submit" name="Submit" value="Genrate Package">
							<td>
								&nbsp;
							</td>
						</tr>
						<tr>
							<td>
								&nbsp;
							</td>
							<td>
								&nbsp;
							</td>
							<td>
								&nbsp;
							</td>
							<td>
								&nbsp;
							</td>
							<td>
								&nbsp;
							</td>
							<td>
								&nbsp;
							</td>
						</tr>
						<tr>
							<td colspan="6" align="center">
								<FONT class="error"><html:errors />
								</FONT>
							</td>
						</tr>
					</table>
















				</form>
			</td>
			<td width="2%">
				&nbsp;




			</td>
		</tr>
		<tr style="height: 20%">
			<td></td>
		</tr>
		<tr>
			<td height="20%" colspan="3"></td>
		</tr>
	</table>



	<br>
	<br>
	<br>
	<br>
	<br>
</body>
</html:html>
