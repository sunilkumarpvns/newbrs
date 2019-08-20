<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page contentType="text/html; charset=iso-8859-1" language="java"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"	prefix="html"%>

<% String path = request.getContextPath(); %>

<html>
	<head>
		<title>Elite License Generator</title>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<link href="<%=path%>/css/mllnstyles.css" rel="stylesheet" type="text/css">
		<link href="../css/style.css" rel="stylesheet" type="text/css">
		<link href="../css/mllnstyles.css" rel="stylesheet" type="text/css">
	</head>

	<body>
		<table width="100%" border="0" height="53">
		 	<tr>
		    	<td height="100">&nbsp;</td>
		  	</tr>
		</table>
		<table width="100%" height="100%">
			<tr>
				<td width="30%"></td>
				<td>
					<table width="100%"   border="1" align="center" cellpadding="0" cellspacing="0">
						<form action="<%=path%>/UploadLicenseFile.do" method="post" enctype="multipart/form-data">		
	      					<tr>
            					<td class="tblheader-bold">Upload Your Existing License</td>
          					</tr>
						  	<tr>
				            	<td align="center" class="tblrows">&nbsp;</td>
				          	</tr>
          					<tr>
            					<td align="center" class="tblrows" >License File&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            						<input size="30" name="licenseFile" type="file"></input>
								</td>
		 					</tr>
							<tr>
					            <td align="center" class="tblrows">&nbsp;</td>
					        </tr>
							<tr>
					            <td align="center" class="tblrows"><input type="submit" name="upload" value="  Upload  " class="large_button"></input></td>
					        </tr>
					        <tr>
            					<td align="center" class="tblrows">&nbsp;<font class="error"><html:errors/></font></td>
          					</tr>
          				</form>	
			   		</table>
				</td>
				<td width="30%"></td>
			</tr> 
		</table>
		<table width="100%" border="0" height="53">
			 <tr>
			    <td height="53">&nbsp;</td>
			  </tr>
		</table>

	</body>
</html>
