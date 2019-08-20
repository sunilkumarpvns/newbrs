<%@page import="com.elitecore.elitesm.util.constants.SystemLoginConstants"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="java.net.InetAddress"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ page import="org.apache.struts.Globals"%> 
<%@ page import="org.apache.struts.taglib.html.Constants"%> 
<%@ page import="javax.servlet.http.HttpSession" %>
<%

    String basePath = request.getContextPath();
	Integer captchaCode =(Integer)request.getAttribute("enabledCaptcha");
	
	Boolean isUserLoggedIn = (Boolean)request.getSession().getAttribute("userLoggedIn");

	if( isUserLoggedIn != null && isUserLoggedIn.equals(true)){
		request.getRequestDispatcher("/welcomeHome.do").forward(request, response);
		return;
	}
%>
<html>
<head>
<title>EliteCSM - Server Manager</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<LINK REL="stylesheet" TYPE="text/css" HREF="<%=basePath%>/css/mllnbillling.css">
<link REL="SHORTCUT ICON" HREF="<%=basePath%>/images/sterlitetech-favicon.ico" type="image/x-icon" title="AAASMX">
<script src="<%=request.getContextPath()%>/jquery/development/jquery-1.4.2.js"></script>
<SCRIPT type="text/javascript">
    window.history.forward();
    function noBack() { window.history.forward(); }
</SCRIPT>
<script language="Javascript1.2">

var validityResult;

	function validateForm(){
		if(document.forms[0].systemUserName.value==''){
			alert('User Name must be specified.');
			return false;
		}else if(document.forms[0].password.value==''){
			alert('Password must be specified.');
			return false;
		}else if((document.getElementById('captchaText') !=null) && document.forms[0].captchaText.value=='Security Code'){
			alert('Security Code must be specified.');
			return false;
		}else{
			 var userName=$('#systemUserName').val();
			 var pwd=$('#systemPassword').val();
			 $.ajax({url:'<%=request.getContextPath()%>/CheckPasswordValidity',
		          type:'GET',
		          data:{userName:userName,pwd:pwd},
		          async:false,
		          success: function(transport){
		             validityResult=transport;
					 validityResult=validityResult.replace(/(\r\n|\n|\r)/gm,"");
					 if(validityResult == '<%=SystemLoginConstants.FIRSTLOGIN%>') {
						alert('You are required to change your password on your first login');
						document.forms[0].action.value='pwdexpired';
					 	document.forms[0].submit();
					}else if(validityResult == '<%=SystemLoginConstants.YES%>'){
						alert('Your password has expired , change now');	
						document.forms[0].action.value='pwdexpired';
						document.forms[0].submit();
	          		}else if(validityResult == '<%=SystemLoginConstants.INVALID%>'){
							document.forms[0].action.value='login';
				 			document.forms[0].submit();
					}else{
							document.forms[0].action.value='login';
					 		document.forms[0].submit();
					}
		        }
		   });
		   
		}
	}
	function refreshCaptcha(){
		document.forms[0].action.value='refresh';
		document.forms[0].submit();
	}
	function watermark(inputId,text){
	    var inputBox = document.getElementById(inputId);
	    document.getElementById('captchaText').style.color="black";
	    if (inputBox.value.length > 0){
	        if (inputBox.value == text)
	            inputBox.value = '';
	    }
	    else{
	    	 document.getElementById('captchaText').style.color="gray";
	    	 inputBox.value = text;
	    }
	}
	window.focus();
	  /** Break out of frames on redirect*/
	  function breakout_of_frame()
	  {
	    if (top.location != location) 
	    {
	      top.location.href = document.location.href ;
	    }
	  }
	  $(document).ready(function() {
			$('#systemPassword').attr('autocomplete', 'on');
			$('#loginFormId').attr('autocomplete', 'on');
		});
	  
	  function doChangeValue(){
		  document.forms[0].generateDm.value="true";
		  validateForm();
	  }
	</script>
</head>
<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" scroll="No" onpageshow="if (event.persisted) noBack();" onunload="" onload="breakout_of_frame();document.systemLoginForm.systemUserName.focus();noBack();" onhelp="return false;">
	<Script language='JavaScript'>
	tblheight=document.body.clientHeight;
	document.writeln("<TABLE border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#005197\" width=\"100%\" height=\"100%\" style=\"position:fixed\"> ");
   </Script>
	<%
	    Long minutes = 0L;
  		String errorCode = "";
	    Boolean disconnectOldSessions = false ;
	    String invalidConcurrentUser="";
	    String ipAddress="";
		try{
		 
		  errorCode = (String)request.getAttribute("errorCode");
		  disconnectOldSessions = (Boolean)request.getAttribute("disconnectOldSession");
		  minutes=(Long)request.getAttribute("ElapsedTime");
		  invalidConcurrentUser = (String)request.getAttribute("invalid_concurrent_user");
		  ipAddress=request.getRemoteHost();
		  if(minutes==null){
			  int time=Integer.parseInt((ConfigManager.get(ConfigConstant.LOGIN_BLOCK_INTERVAL)));
			  String strMinutes=""+time;
			  minutes=Long.parseLong(strMinutes);
			  minutes=minutes/60;
		  }
		  if(minutes<=0){
			  minutes=Long.parseLong("1");
		  }
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	%>
	<tr>
		<td bgcolor="#000000" height="10"></td>
	</tr>
	<tr>
		<td align="right" valign="top" height="20%" width="101%">
			<img src="<%=basePath%>/images/Sterlite_Tech_Product.jpg" width="263" height="54" usemap="#Map" border="0"> <map name="Map">
			<area shape="rect" coords="28,3,209,20" href="http://www.elitecore.com" target="_blank">
			</map>
		</td>
	</tr>
	<tr>
		<td height="10">&nbsp;</td>
	</tr>
	<tr>
		<td height="10">&nbsp;</td>
	</tr>
	<tr>
		<td height="10" align="center"><font class="login-error-message">
	<%
		if(errorCode!=null)
		{
			if(errorCode.equalsIgnoreCase("USERNAME")){
	%> <bean:message bundle="resultMessageResources" key="general.login.invalid.username" /> <%
			}else if(errorCode.equalsIgnoreCase("PASSWORD")){	
	%> <bean:message bundle="resultMessageResources" key="general.login.invalid.password" /> <%
			}else if(errorCode.equalsIgnoreCase("LOGINFAILED")){	
	%> <bean:message bundle="resultMessageResources" key="general.login.failed" /> <%
			}else if(errorCode.equalsIgnoreCase("INVALIDCAPTCHA")){
				%> <bean:message bundle="resultMessageResources" key="general.login.captcha.failed" /> <%
			}else if(errorCode.equalsIgnoreCase("BLOCKIP")){
				%> Your IP (<%=ipAddress %>) is Block for <%=minutes %> Minutes <%	
			}else if(errorCode.equalsIgnoreCase("BLOCKUSER")){
			%> Your ACCOUNT(<%=request.getAttribute("Account") %>)  is Block for <%=minutes %> Minutes <%	
			}else if(errorCode.equalsIgnoreCase("BLOCKUSERIP")){
			%> Your ACCOUNT(<%=request.getAttribute("Account") %>) and IP(<%=ipAddress %>) is Block for <%=minutes %> Minutes <%	
			}else if(errorCode.equalsIgnoreCase("CONCURRENT_USER")){%>
			   Login limit exceeded or stale session exists
			<%}
		}
	%>
		</font></td>
	</tr>
	<tr>
		<td height="10" align="center">&nbsp;</td>
	</tr>
	<tr align="center">
		<td align="center" valign="top">
			<html:form action="/login" styleId="loginFormId" method="POST">
		
			 <input id="tokenKey" type="hidden" name="<%=Constants.TOKEN_KEY %>" value="<%= session.getAttribute(Globals.TRANSACTION_TOKEN_KEY) %>"/> 
		 	 <html:hidden name="systemLoginForm" property="action" value="login"/>
			 <input type="hidden" id="hiddenField" name="hiddenField" value="" />
			
			<table>
			<tr>
				<td align="right" <%  
					if(captchaCode!=null && captchaCode>=3){
					%>
						style="padding-left: 160.5"
					<% 	
					}
				%>>
				<table width="272" border="0" cellspacing="0" cellpadding="0" align="center" >
					<tr>
						<td colspan="2"><img src="<%=basePath%>/images/Sterlite_Tech_EliteCSM.jpg" width="272" height="113"></td>
					</tr>
					<tr>
						<td width="72"><img src="<%=basePath%>/images/login.jpg" width="101" height="26"></td>
						<td width="200" background="<%=basePath%>/images/login-bkgd.jpg">
							<input type="text" name="systemUserName" id="systemUserName" size="15" maxlength="20" class="textbox-text textboxusername"  style="width: 120px; height: 22px;" tabindex="1">
						</td>
					</tr>
					<tr>
						<td width="72"><img src="<%=basePath%>/images/pwd.jpg" width="101" height="27"></td>
						<td width="200" background="<%=basePath%>/images/pwd-bkgd.jpg">
							<input type="password" id="systemPassword" name="password" size="15" maxlength="20" class="textbox-text textboxpassword" style="width: 120px; height: 22px;" tabindex="2" autocomplete="on">
						</td>
					</tr>
					<tr>
						<td width="72"><img src="<%=basePath%>/images/btm.jpg" width="101" height="48" ></td>
						<td width="200"><img src="<%=basePath%>/images/btm1.jpg" width="104" height="48"><input type="image" src="<%=basePath%>/images/go.jpg" class="go" width="49" height="48" onclick="validateForm();return false;"></td>
					</tr>
					<input type="hidden" name="loginMode" value="1">
					<!-- 1 is for Staff -->
					
					<% if( disconnectOldSessions != null && request.getSession().getAttribute(Globals.TRANSACTION_TOKEN_KEY) != null){ 
						if(disconnectOldSessions == true ){  %>
					<tr>
						<td align="center" colspan="2" class="disconnect_Old_Sessions">
							<span onclick="doChangeValue();">Disconnect Old Sessions</span>
							<input type="hidden" value="false" name="generateDm" id="generateDM"/>
						</td> 
					</tr>
					<%	}
					}else{ %>
						<input type="hidden" value="false" name="generateDm" id="generateDM"/>
					<%} %>
				</table>
				</td>
				<% 
					  if(captchaCode!=null && captchaCode>=3){
				 %>
				<td>
			<table >
			<tr>
			</tr>
			<tr>
				<td>
						<img src="Captcha.jpg" style="border: 1;border-radius: 0.5em" width="120" height="40" border="1">
				</td>
			</tr>
			<tr>
				<td valign="bottom">
						<input type="text" id="captchaText" name="captchaText"  size="15" maxlength="20"  value="Security Code"  onfocus="watermark('captchaText','Security Code');" tabindex="3"
						onblur="watermark('captchaText','Security Code');" class="textbox textboxcaptchatext" style="width: 120px; height: 25px;color: gray;font-size: small;font-style: italic;">&nbsp;
				</td>
			</tr>
			</table>
			</td>
				  <td>
				  	 <input type="image" src="<%=basePath%>/images/refresh.jpg" onclick="refreshCaptcha();">
				  </td>
				  	  <% 
				  }
				  %>
			</tr>
			</table>
			<input type="hidden" name="skipPasswordCheck" value="yes">
			</html:form>
		</td>
		
		<td>
		<table>
		<tr></tr>
		</table>
		</td>
	</tr>
	<Script type='text/javascript'>
	document.writeln("</TABLE>");
	document.systemLoginForm.systemUserName.focus(); 
</Script>
</body>
</html>
