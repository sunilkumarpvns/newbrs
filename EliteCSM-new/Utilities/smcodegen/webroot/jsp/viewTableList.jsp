<%@ page contentType="text/html; charset=iso-8859-1" language="java"
	import="java.sql.*" errorPage=""%>

<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.ArrayList" />
<jsp:directive.page import="java.util.Iterator" />
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<script language="javascript">


function selectList()
 {
 	
 	var list = 	document.dispatchGenrate.strTableName.options;
 	var count =0;
 	for(var index=0; index<list.length;index++) {
 		
 		var option = list[index].selected ;
 		if( option == true)
 		{
 			count++;
 			
 			if(count>1)
 			{
 				document.getElementById('package').innerHTML='';
 			
 			}
 			else
 			{
 				document.getElementById('package').innerHTML='<html:submit property="method" >'+
										'<bean:message key="button.test" />'+
									'</html:submit>';
 			}
 		}
 		
 		
 	}
 
 	
 }

</script>



<%
String path = request.getContextPath();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html:html locale="true">
<head>
	<title>EliteCodeGen</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<link href="<%=path%>/css/style.css" rel="stylesheet" type="text/css">
	<link href="../css/style.css" rel="stylesheet" type="text/css">
</head>

<body onload="selectList()">

	<table width="100%" height="100%">
		<tr>
			<td height="53" colspan="3">
				&nbsp;



			</td>
		</tr>
		<tr>
			<td width="20%">
				&nbsp;



			</td>
			<td width="60%" align="center">


				<html:form action="/dispatchGenration.do" method="get">

					<table width="100%" height="25%" border="0" align="center"
						cellpadding="0" cellspacing="0">

						<tr style="height: 20%">
							<td width="13%">
							<td width="38%">
							<td width="49%">
						</tr>
						<tr>
							<td height="16%" colspan="3">
								<div align="center" class="title">
									EliteCodeGen
								</div>
							</td>
						</tr>
						<tr>
							<td height="19%" colspan="3">
								<div align="center" class="sidebarHeader">
									List Of Tables
								</div>
							</td>
						</tr>
						<tr>
							<td height="19%" colspan="3">
								&nbsp;
							</td>
						</tr>

						<tr>
							<td height="19%" colspan="3" align="center">
								&nbsp;
								<html:select property="strTableName" name="dispatchGenrate"
									multiple="true" onchange="selectList()">
									<%
									            List tableList = (ArrayList) request.getAttribute("tableList");
									            Iterator itTableList = tableList.iterator();
									            while (itTableList.hasNext()) {
									                String tableName = (String) itTableList.next();
									%>
									<html:option value="<%=tableName%>">
										<%=tableName%>
									</html:option>
									<%
									}
									%>
								</html:select>
							</td>
						</tr>

						<tr>
							<td height="25%" colspan="2" align="center">

								<html:submit property="method">
									<bean:message key="button.data" />
								</html:submit>

							</td>

							<td height="25%" align="center">
								<div align="center" id="package">
									<html:submit property="method">
										<bean:message key="button.test" />
									</html:submit>
								</div>
							</td>
						</tr>

						<tr>
							<td height="25%" colspan="3" align="center">
								<FONT class="error"><html:errors /> </FONT>
							</td>
						</tr>
						<tr>
							<td height="25%" colspan="3">
								&nbsp;
							</td>
						</tr>
						<tr style="height: 20%">
							<td>
							<td>
							<td>
						</tr>

					</table>
				</html:form>
			</td>
			<td width="20%">
				&nbsp;



			</td>
		</tr>
		<tr style="height: 20%">
			<td></td>
		</tr>
		<tr>
			<td height="20%" colspan="3">
				<br>
				<br>
			</td>
		</tr>
	</table>


</body>
</html:html>

