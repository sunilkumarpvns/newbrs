<%@ page contentType="text/html; charset=iso-8859-1"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.license.base.LicenseData"%>
<%@ page
	import="com.elitecore.license.base.commons.LicenseModuleConstants"%>
<%@ page import="com.elitecore.license.base.commons.LicenseDataManager"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Iterator"%>
<%@ page
	import="com.elitecore.license.base.commons.LicenseTypeConstants"%>

<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"
	prefix="html"%>

<%
String path = request.getContextPath();

String version = (String) session.getAttribute("version");
String additionalKey = (String) session.getAttribute("additionalKey");
String status = (String) session.getAttribute("status");
String publicKey = (String) session.getAttribute("publicKey");


/*Setting Values for lic genration will use Genrate License Module. */
LicenseData modualLicData = (LicenseData) request.getSession(false).getAttribute("modualLicData");
modualLicData.setVersion(version);
modualLicData.setAdditionalKey(additionalKey);
modualLicData.setStatus(status);
modualLicData.setType(LicenseTypeConstants.MODULE);
modualLicData.setValue((String)modualLicData.getModule());
%>

<script language="javascript" src="<%=request.getContextPath()%>/js/popcalendar.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/validation.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/commonfunctions.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/cookie.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/popcalendar.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/openhelp.js"></script>
<script>
	var dFormat;
	dFormat ='dd/MM/yyyy';
	
	function popUpCalendar(ctl,	ctl2)
	{
		var tmp = document.getElementById('strValue['+ctl2+']');
		jsPopUpCalendar( ctl, tmp, dFormat ); 
	}

	function popUpWindow(url)
	{
		window.open(url, 'mywin','left=20,top=20,width=350,height=400,toolbar=0,resizable=0,scrollbars=yes');
	}
	
	function checkModule(index,end)	{
	var baseModuleName = document.getElementById('module['+index+']');
	var name 		   = document.getElementById('name['+index +']');
	//	alert(baseModuleName.value);
	if(document.getElementById('select['+index+']').checked){
		for(i=0;i<end;i++){
			var modual = document.getElementById('module['+ i +']');
			var name = document.getElementById('name['+ i +']');
			if(modual.value.search(baseModuleName.value)==0){
					document.getElementById('select['+i+']').checked=true
			}
		}
	}else{
	 	 for(i=0;i<end;i++) {
	 	 	var modual = document.getElementById('module['+ i +']');
		 	var name = document.getElementById('name['+ i +']');
			if(modual.value.search(baseModuleName.value)==0){
					document.getElementById('select['+i+']').checked=false
			}
		}
	}
	
 }
</script>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="com.elitecore.license.base.commons.LicenseConstants"%><html>
	<head>
		<title>Elite License Generator</title>
		<meta http-equiv="Content-Type"	content="text/html; charset=iso-8859-1">
		<link href="<%=path%>/css/mllnstyles.css" rel="stylesheet"	type="text/css">
		<link href="<%=path%>/css/popcalendar.css" rel="stylesheet"	type="text/css">
		<link href="../css/popcalendar.css" rel="stylesheet"	type="text/css">
		<link href="../css/style.css" rel="stylesheet" type="text/css">
		<link href="../css/mllnstyles.css" rel="stylesheet" type="text/css">
	</head>

	<body>
		<table width="100%" border="0" align="right">
			<tr>
				<td height="26">&nbsp;
					
				</td>
			</tr>
		</table>
		<table width="100%" height="100%">
			<tr>
				<td width="3%"></td>
				<td width="94%">
				
				<table  width="100%">
				<tr>
					<td colspan="2" width="100%" height="16%" class="tblheader-bold">Common Input</td>
				</tr>
				<tr>
					<td width="20%" height="16%" class="tblrows">Public key :</td>
					<td><textarea rows="2" cols="104" readonly="readonly" ><%=publicKey %></textarea></td>
				</tr>
				<tr>
					<td width="20%" height="16%" class="tblrows">Version :</td>
					<td class="tblrows"><%=version %></td>
				</tr>
				<tr>
					<td width="20%" height="16%" class="tblrows">Status :</td>
					<td class="tblrows"><%=status %></td>
				</tr>
				<tr>
					<td width="20%" height="16%" class="tblrows">Additional Key :</td>
					<td class="tblrows"><%=additionalKey %></td>
				</tr>
					
				</table>
						<form action="<%=path%>/GenrateLicense.do" method="post" >
				
					<table width="100%" cellpadding="0" cellspacing="0" height="25%">
						<tr>
							<td width="86%" height="16%" class="tblheader-bold">
								System Licenses
							</td>
						</tr>
						<tr>
							<td width="86%" height="16%" class="tblheader-bold">
								Note: Configure the value -1 to allow unlimited access. If the set value is less than -1, then system will consider the default values relevant to the respective module.
							</td>
						</tr>
						<tr>
							<td>
								<table width="100%" cellpadding="0" cellspacing="0">
									<%
												int i = 0;
												int k =0;
								            	List<LicenseData> lstLicData = (List<LicenseData>)session.getAttribute("sysLiclist");
									            Iterator<LicenseData> itLstLicData = lstLicData.iterator();
									            while (itLstLicData.hasNext()) {
									            LicenseData tempLicData = itLstLicData.next();
									       		//This Condition is checked because for Resource Manager the COncurrent_Session and Subsciber 
									       		//License is not required and hence should not be displayed in the GUI and License Generation.
									       		//if(modualLicData.getModule().equalsIgnoreCase(LicenseModuleConstants.SYSTEM_RESOURCEMANAGER))
									            //{
									    		//	if(tempLicData.getType().equalsIgnoreCase(LicenseTypeConstants.CONCURRENT_SESSION) || 
									    		//		tempLicData.getType().equalsIgnoreCase(LicenseTypeConstants.SUBSCRIBERS) || 
									    		//		tempLicData.getType().equalsIgnoreCase(LicenseTypeConstants.VENDOR_TYPE)){ 
									    		//		continue;									    				
									    		//	}
									            //}
									            
							                String strName = "name[" + i + "]";
								            String strModule ="module[" + i + "]";
								            String strType = "type[" + i + "]";
								            String objValue = "strValue[" + i + "]";
								            String strAdditionalKey = "additionalKey[" + i + "]";
								            String strVersion = "version[" + i + "]";
								            String strStatus = "status[" + i + "]";
								            String strSelect = "select[" + i + "]";
								            String strDisplayName= "displayName[" + i + "]";
								            String strDescription= "description[" + i + "]";
								            String strOperator="operator["+ i + "]";
								            String strValueType="valueType[" + i + "]";
								            String value="";
								            if(tempLicData.getValue() != null) {
								            	value = tempLicData.getValue().toString();
								            }
								            i++;
								            k++;
									%>
									
									<tr>
										<td width="2%" height="2%" class="tblcol" align="right">
										<%=i%>&nbsp;&nbsp;
										</td>
										<td width="40%" height="25%" class="tblcol" align="left">
										<input name="<%=strSelect%>" align="left" checked="checked" type="hidden" value="1"></input>
										<%=tempLicData.getDisplayName()%>
										</td>
										<td width="40%" height="25%" class="tblcol" align="left">&nbsp;
										<%if (tempLicData.getType().equalsIgnoreCase(LicenseTypeConstants.EXPIRY_DATE)){%>
										<input type="text" name="<%=objValue%>" id="<%=objValue%>" value="<%=value%>"> 
										<a  href="javascript:void(0)"  onclick="popUpCalendar(this,<%=i-1%>)">
						                   		<img  src="<%=path%>/images/calendar.jpg" border="0" tabindex="6">
						                </a>
										<%} else if(tempLicData.getType().equalsIgnoreCase(LicenseTypeConstants.NODE)){ %>
										<input type="hidden" name="<%=objValue%>" id="<%=objValue%>" value="<%=publicKey %>"/>
										<%} else if(tempLicData.getType().equalsIgnoreCase(LicenseTypeConstants.MODULE)){ %>
										<input type="text" name="<%=objValue%>" id="<%=objValue%>" value="<%=tempLicData.getModule()%>" ></input>
										<%} else if(tempLicData.getType().equalsIgnoreCase(LicenseTypeConstants.VENDOR_TYPE)){ %>
										<textarea rows="2" cols="26" name="<%=objValue%>" id="vendortype" wrap="hard" readonly><%=value%></textarea>
										<a  href="javascript:void(0)"  onclick="popUpWindow('<%=path%>/vendorList.do?type=vendortype')">
						                   		<img  src="<%=path%>/images/lookup.jpg" border="0" tabindex="6"/>
						                </a>
						                <%} else if(tempLicData.getType().equalsIgnoreCase(LicenseTypeConstants.SUPPORTED_VENDORS)){ %>
						                <textarea rows="2" cols="26" name="<%=objValue%>" id="vendors" wrap="hard" readonly><%=value%></textarea>
										<!-- <input type="textarea" rows=2 cols=20 name="vendors" id="vendors"></input> -->
										<a  href="javascript:void(0)"  onclick="popUpWindow('<%=path%>/vendorList.do?type=vendors')">
						                   		<img  src="<%=path%>/images/lookup.jpg" border="0" tabindex="6"/>
						                </a>
										<%}else{ %>
											<input type="text" name="<%=objValue%>" id="<%=objValue%>" value="<%=tempLicData.getValue()%>"></input>
										<%}%>
										</td>
										<td td width="5%" height="25%" class="tblcol" align="left">&nbsp;</td>
									</tr>
									
									<input type="hidden" name="<%=strName%>" value="<%=tempLicData.getName()%>">
									<input type="hidden" name="<%=strModule%>" value="<%=tempLicData.getModule()%>"> 
									<input type="hidden" name="<%=strType%>" value="<%=tempLicData.getType()%>"> 
									<input type="hidden" name="<%=strDisplayName%>" value="<%=tempLicData.getDisplayName()%>">
									<input type="hidden" name="<%=strDescription%>" value="<%=tempLicData.getDescription()%>">
									<input type="hidden" name="<%=strAdditionalKey%>" value="<%=additionalKey%>">
									<input type="hidden" name="<%=strVersion%>" value="<%=version%>">
									<input type="hidden" name="<%=strStatus%>" value="<%=status%>">
									<input type="hidden" name="<%=strValueType%>" value="<%=tempLicData.getValueType()%>">
									<input type="hidden" name="<%=strOperator%>" value="<%=tempLicData.getOperator()%>">
									
									<%
									  }
									%>
								</table>
							</td>
						</tr>
						<tr>
							<td height="25%" align="center" class="tblrows">&nbsp;
								
							</td>
						</tr>
						<tr>
							<td height="25%" align="center" class="tblrows">
								<%
								            List<LicenseData> resultLicLst = (List<LicenseData>) session.getAttribute("resultLiclist");
								            Iterator itResultLicLst = resultLicLst.iterator();
								           
								%>
								
								<table width="100%" cellpadding="0" cellspacing="0"  >


									<tr>
										<td colspan="2" class="tblheader-bold-grey">
											<%=modualLicData.getDisplayName()%>	Licenses
										</td>
									</tr>

									<%	
											//i=0;
									        while (itResultLicLst.hasNext()) {
									                
									        String strName = "name[" + i + "]";
								            String strModule ="module[" + i + "]";
								            String strType = "type[" + i + "]";
								            String objValue = "strValue[" + i + "]";
								            String strAdditionalKey = "additionalKey[" + i + "]";
								            String strVersion = "version[" + i + "]";
								            String strStatus = "status[" + i + "]";
								            String strSelect = "select[" + i + "]";
								            String strDisplayName= "displayName[" + i + "]";
								            String strDescription= "description[" + i + "]";
								            String strOperator="operator["+ i + "]";
								            String strValueType="valueType[" + i + "]";
									        i++;        
									        LicenseData licData = (LicenseData) itResultLicLst.next();
									%>
									
									<%
										String cssClassName = "tblcol";
									  if(licData.getType().equalsIgnoreCase(LicenseTypeConstants.MODULE)){
									     cssClassName = "license";
								    	}%>
									
									<tr>
										<td width="2%" height="25%" class="<%=cssClassName%>" align="left">
										<%=i%>&nbsp;&nbsp;
										</td>
										
										<%if(licData.getType().equalsIgnoreCase(LicenseTypeConstants.MODULE)){%>
										
										<td width="98%" height="25%" class="<%=cssClassName%>" align="left">
										<%if(licData.getState().equalsIgnoreCase("true")){ %>
										<input type="checkbox"  name="<%=strSelect%>" align="left" value="1" checked="checked" onclick="checkModule(<%=i-1%>,<%=resultLicLst.size()+k%>)"></input>
										<% } else { %>
										<input type="checkbox"  name="<%=strSelect%>" align="left" value="1" onclick="checkModule(<%=i-1%>,<%=resultLicLst.size()+k%>)"></input>
										<%} %>
										<%=licData.getDisplayName()%>
										</td>
										<%}%>
										
									<!-- <td td width="80%" height="25%" class="<%=cssClassName%>" align="left">&nbsp;</td> -->
										
									</tr>
									<input type="hidden" name="<%=strName%>" value="<%=licData.getName()%>"> 
									<input type="hidden" name="<%=strModule%>" value="<%=licData.getModule()%>"> 
									<input type="hidden" name="<%=strType%>" value="<%=licData.getType()%>">
									<input type="hidden" name="<%=strDisplayName%>" value="<%=licData.getDisplayName()%>">
									<input type="hidden" name="<%=strDescription%>" value="<%=licData.getDescription()%>">
									<input type="hidden" name="<%=strAdditionalKey%>" value="<%=additionalKey%>">
									<input type="hidden" name="<%=strVersion%>" value="<%=version%>">
									<input type="hidden" name="<%=strStatus%>" value="<%=status%>">
									<input type="hidden" name="<%=strValueType%>" value="<%=licData.getValueType()%>">
									<input type="hidden" name="<%=strOperator%>" value="<%=licData.getOperator()%>">
									
									
									<%
									}
									%>
								
								<tr>
									<td height="25%" class="tblcol" align="right" colspan="2">&nbsp;</td>
								</tr>	
							<tr>
								<td></td>
								<td height="25%" align="center" class="tblrows">
									<input name="submit" type="submit" value="Genrate Licenses" class="light-btn"></input>
								</td>
							</tr>

								</table>
							</td>
						</tr>
						<tr>
							<td height="25%" align="center" class="tblrows">
								&nbsp;
								<font class="error"><html:errors /> </font>
							</td>
						</tr>
						
		        	  
					</table>
					</form>
				</td>
				<td width="3%"></td>
			</tr>
		</table>
		
		<table width="100%" border="0" height="53">
			<tr>
				<td height="53">&nbsp;
					
				</td>
			</tr>
		</table>
	</body>
</html>

 
