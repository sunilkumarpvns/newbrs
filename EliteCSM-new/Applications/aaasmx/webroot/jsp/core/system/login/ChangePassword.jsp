<%@page import="com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.blmanager.core.system.systemparameter.PasswordSelectionPolicyBLManager" %>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData" %>

<%
    String basePath = request.getContextPath();
    SystemLoginForm loginForm = (SystemLoginForm)request.getSession().getAttribute("radiusLoginForm");
    PasswordSelectionPolicyBLManager passwordBLManager=new PasswordSelectionPolicyBLManager();
	PasswordPolicyConfigData passwordPolicySelectionData=passwordBLManager.getPasswordSelectionPolicy();
	IStaffData staffData = (IStaffData)request.getSession().getAttribute("staffData");
	int minTotal=0,maxTotal=0;
	String strProhChar="";
	String url = request.getRequestURL().toString();
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<style type="text/css">
.paragraph {
	margin:0 auto;
}

</style>

<script type="text/javascript">

var validPassword=false;
var flagTrack=false;
$(document).ready(function() {
	<% if(passwordPolicySelectionData.getChangePwdOnFirstLogin().equalsIgnoreCase("true") && staffData.getLastChangePasswordDate() == null){%>
		var originHost = document.referrer;
		if(originHost.indexOf("login.do") < 0){
			alert('You are required to change your password on your first login');
		}
	<%}%>
	
	var checkflag=false;
	<%
	if(passwordPolicySelectionData.getMinPasswordLength() != null && passwordPolicySelectionData.getMaxPasswordLength() != null){
	%>
 	 	var myPlugin = $("input[id$='txtPassword']").password_strength();
 	 	checkflag=true;
 	<%}else{%>
 	<%
 	if(passwordPolicySelectionData.getMinAlphabates() != null && passwordPolicySelectionData.getMaxAlphabates() != null){
 		minTotal=passwordPolicySelectionData.getMinAlphabates();
 		maxTotal=passwordPolicySelectionData.getMaxAlphabates();
 	}
 	if(passwordPolicySelectionData.getMinDigits() != null && passwordPolicySelectionData.getMaxDigits() != null){
 		minTotal=minTotal+passwordPolicySelectionData.getMinDigits();
 		maxTotal=maxTotal+passwordPolicySelectionData.getMaxDigits();
 	}
 	if(passwordPolicySelectionData.getMinSpecialChars() != null && passwordPolicySelectionData.getMaxSpecialChars() != null){
 		minTotal=minTotal+passwordPolicySelectionData.getMinSpecialChars();
 		maxTotal=maxTotal+passwordPolicySelectionData.getMaxSpecialChars();
 	}
 	if(minTotal !=0 && maxTotal != 0){%>
 		if(checkflag == false){
 			var myPlugin = $("input[id$='txtPassword']").password_strength();
 		}
 	<%}}%>
});
function isValidPassword(){
	
	if(validPassword == false){
		<%if(passwordPolicySelectionData.getMinPasswordLength() == null && passwordPolicySelectionData.getMaxPasswordLength() == null){%>
			return true;
		<%}%>
	}
	return validPassword;
}

function isValidProhibitedChar(){
	var pwd=$('#newPassword').val();
	
	var policyPwd='<%=passwordPolicySelectionData.getProhibitedChars()%>';
	
	if(policyPwd != null && policyPwd != 'null'){
		for(var i=0;i<policyPwd.length;i++){
			for(var j=0;j<pwd.length;j++){
				if(policyPwd.charCodeAt(i) == pwd.charCodeAt(j)){
					alert("Prohibited character '"+pwd.charAt(j)+"' found in your Password");
					return false;
				}
			}
		}
	}
	return true;
}
function change(){
	
	var loginPassword = '';
	<% if(loginForm != null){ %>
	loginPassword = '<%= loginForm.getPassword() %>';
	<%}%>
	
	if(isNull(document.forms[0].oldPassword.value)){
		document.forms[0].oldPassword.focus();
		alert('Old Password must be specified');
	}else if(document.forms[0].newPassword.value == loginPassword){
		document.forms[0].newPassword.focus();
		alert('New Password must not match with your current Password');
	}else if(isNull(document.forms[0].newPassword.value)){
		document.forms[0].newPassword.focus();
		alert('New Password must be specified');
	}else if(isNull(document.forms[0].newPassword.value)){
		document.forms[0].newPassword.focus();
		alert('New Password must be specified');
	}else if(isNull(document.forms[0].newConfirmPassword.value)){
		document.forms[0].newConfirmPassword.focus();
		alert('Confirm Password must be specified');
	}else if(document.forms[0].newPassword.value != document.forms[0].newConfirmPassword.value){
		alert('New Password and Confirm Password not Match');
	}else if(document.forms[0].newPassword.value == document.forms[0].oldPassword.value){
		alert('New Password and Old Password cannot be same');
	}else if(!isValidPassword()){
		alert('Password policy not match !!');
		document.forms[0].newPassword.focus(); 
	}else if(!isValidProhibitedChar()){
		document.forms[0].newPassword.focus(); 
	}else if(isHistoricalPassword()){
		document.forms[0].oldPassword.focus();
	}else{
		document.forms[0].action.value = 'change';
		document.forms[0].submit();
	}
}
function cancel(){
	document.forms[0].action.value = 'changelater';
	document.forms[0].submit();
	
}
function resetFields(){
    $('#PasswordStrengthBorder').hide();
    $('#PasswordStrengthBar').hide();
    $('#PasswordStrengthText').text('');
}

function isHistoricalPassword() {
	var ajaxUrl = "<%=basePath%>/checkHistoricalPassword.do";
	var param = "userId=" + document.forms[0].userId.value;
	param += "&newPassword=" + document.forms[0].newPassword.value;
	
	var isHistoricalPassword = $.ajax({ type: "POST",   
        url: ajaxUrl,
        data: param,
        async: false
      }).responseText;
	
	if(isHistoricalPassword.indexOf('-1')>-1){
		return false;
	}else{
		alert('You can not reuse any of the previous '+isHistoricalPassword+' passwords.');
		return true;
	}
}
(function($) {

    var password_Strength = new function() {

    //return count that match the regular expression
    this.countRegExp = function(passwordVal, regx) {
         var match = passwordVal.match(regx);
         return match ? match.length : 0;
    }

    this.getStrengthInfo = function(passwordVal) {
    var len = passwordVal.length;
    var pStrength = 0; //password strength
    var msg = "", inValidChars = ""; //message

    var isSpecialChar="*|,\":<>[]{}`\';()@&$#%!~+-_=?^.";
    var isSpecilaChars = new RegExp("[" + isSpecialChar + "]", "g");
           
    var nums = this.countRegExp(passwordVal, /\d/g), //numbers
    alphabets=this.countRegExp(passwordVal, /[a-zA-Z ]/g),
	specials = this.countRegExp(passwordVal, isSpecilaChars); //special characters
    inValidChars = passwordVal.replace(/[a-z]/gi, "") + inValidChars.replace(/\d/g, "");
    inValidChars = inValidChars.replace(/\d/g, "");
    specials=inValidChars.length;
        	
   if(password_settings.specialChars.length > 0){
           	var strChars=password_settings.specialChars;
               for (var i = 0; i <strChars.length; i++) {
            	   for(var j=0 ;j < passwordVal.length;j++){
            		   if(strChars.charCodeAt(i) == passwordVal.charCodeAt(j)){
            			   return "Prohibited character: " + passwordVal.charAt(j);
            		   }
            	   }
               }
   }
    //max length
    if (len > (password_settings.maxLength)) {
          return "Password too long!";
    }

   if(isNaN(password_settings.minLength) == false && flagTrack==false){
	    if ((specials +alphabets+ nums ) < password_settings.minLength) {
	       msg += password_settings.minLength - (specials + alphabets + nums) + " more characters(Length), ";
	    } 
    }

    if(isNaN(password_settings.numberLength) == false){	 
     //GET NUMBERS
         if (nums >= password_settings.numberLength) {
             nums = password_settings.numberLength;
         }else {
        	 if(nums == 0){
        		 msg += "At least ";
        	 }
             msg += (password_settings.numberLength - nums) + " more numbers, ";
         }
    }else{
    	password_settings.numberLength=0;
    }
    
    if(isNaN(password_settings.specialLength) == false){	 
	     //special characters
	     if (specials >= password_settings.specialLength) {
	         specials = password_settings.specialLength;
	     }else {
	    	 if(specials == 0){
	    		 msg += "At least ";
	    	 }
	         msg += (password_settings.specialLength - specials) + " more symbol, ";
	     }
    }else{
    	password_settings.specialLength=0;
    }
    
    if(isNaN(password_settings.minalphabetsrange) == false){
	      //letter
	     if (alphabets >= password_settings.minalphabetsrange) {
	     	alphabets = password_settings.minalphabetsrange;
	     }else {
	    	if(alphabets == 0){
	    		 msg += "At least ";
	    	}
	        msg += (password_settings.minalphabetsrange - alphabets) + "  more alphabets, ";
	     }
    }else{
    	password_settings.minalphabetsrange=0;
    }
     //strength for length
     if ((len - (alphabets + specials + nums)) >= (password_settings.minLength - password_settings.numberLength - password_settings.specialLength - password_settings.minalphabetsrange)) {
       pStrength += (password_settings.minLength - password_settings.numberLength - password_settings.specialLength - password_settings.minalphabetsrange);
     }else {
       pStrength += (len - (alphabets + specials + nums));
     }

      if(password_settings.minalphabetsrange == 0){
    	 alphabets=0;
     }else if(password_settings.specialLength == 0){
    	 specials=0;
     }else if(password_settings.numberLength == 0){
    	 nums=0;
     } 
     //password strength
      pStrength += alphabets + specials + nums;
     
     //strong password
     if (pStrength == password_settings.minLength ) {
       msg = "Strong password!";
     }
      return msg + ';' + pStrength;
     }
   }

    <%
   
    if(passwordPolicySelectionData.getProhibitedChars() == null){
    	strProhChar="";
    }else{
    	strProhChar=passwordPolicySelectionData.getProhibitedChars();
    }
    %>
    
    //default setting
    var password_settings = {
        minLength: '<%=passwordPolicySelectionData.getMinPasswordLength()%>',
        maxLength:'<%=passwordPolicySelectionData.getMaxPasswordLength()%>',
        specialLength:'<%=passwordPolicySelectionData.getMinSpecialChars()%>',
        minalphabetsRange:'<%=passwordPolicySelectionData.getMinAlphabates()%>',
        numberLength: '<%=passwordPolicySelectionData.getMinDigits()%>',
        barWidth: 200,
        barColor: '#EC5D55', //red
        specialChars: '<%=strProhChar%>', 
        metRequirement: false,
        useMultipleColors: 0
    };

    //password strength plugin 
    $.fn.password_strength = function(options) {
        //check if password met requirement

       var _minLength = '<%=passwordPolicySelectionData.getMinPasswordLength()%>',
           _maxLength = '<%=passwordPolicySelectionData.getMaxPasswordLength()%>',
           _numsLength = '<%=passwordPolicySelectionData.getMinDigits()%>',
           _specialLength = '<%=passwordPolicySelectionData.getMinSpecialChars()%>',
           _minalphabetsRange='<%=passwordPolicySelectionData.getMinAlphabates()%>',
           _barWidth = "200",
           _barColor = '#32A746',  //green
           _specialChars = '<%=strProhChar%>',
           _useMultipleColors = "1";

            //set variables
            password_settings.minLength = parseInt(_minLength);
            password_settings.maxLength = parseInt(_maxLength);
            password_settings.specialLength = parseInt(_specialLength);
            password_settings.numberLength = parseInt(_numsLength);
            password_settings.minalphabetsrange=parseInt(_minalphabetsRange);
            password_settings.barWidth = parseInt(_barWidth);
            password_settings.barColor = _barColor;
            password_settings.specialChars = _specialChars;
            password_settings.useMultipleColors = _useMultipleColors;
           
            if(isNaN(password_settings.minLength) == true){
            	password_settings.minLength = parseInt(<%=minTotal%>);
            	password_settings.maxLength = parseInt(<%=maxTotal%>);
            	flagTrack=true;
            }
                    
        return $('#newPassword').each(function() {
            //bar position
            var barLeftPos = $("[id$='newPassword']").position().left + $("[id$='newPassword']").width();
            var barTopPos = $("[id$='newPassword']").position().top + $("[id$='newPassword']").height();

            //password indicator text container
            var container = $('<span id="PasswordStrengthText"></span>')
            .css({ position: 'absolute', top: barTopPos - 6, left: barLeftPos + 15, 'font-size': '10px', display: 'inline-block', width: password_settings.barWidth + 40 });

            //add the container next to textbox
            $(this).after(container);
            //bar border and indicator div
            var passIndi = $('<div id="PasswordStrengthBorder"></div><div id="PasswordStrengthBar" class="BarIndicator"></div>')
            .css({ position: 'absolute', display: 'none' })
            .eq(0).css({ height: 3, top: barTopPos - 16, left: barLeftPos + 15,'border-style': 'solid','font-size':'3px','border-width': 1, padding: 2 }).end()
            .eq(1).css({ height: 5, top: barTopPos - 14, left: barLeftPos + 17 }).end();


            //add the boder and div
            container.before(passIndi);

            $(this).keyup(function() {
                var passwordVal = $(this).val(); //get textbox value
                
                //set met requirement to false
                password_settings.metRequirement = false;
                validPassword=false;

                if (passwordVal.length > 0) {

                    var msgNstrength = password_Strength.getStrengthInfo(passwordVal);
                    var msgNstrength_array = msgNstrength.split(";"), strengthPercent = 0,
                    barWidth = password_settings.barWidth, backColor = password_settings.barColor;
                    //calculate the bar indicator length
                    if (msgNstrength_array.length > 1) {
                        strengthPercent = (msgNstrength_array[1] / password_settings.minLength) * barWidth;
                        //check here....
                        //alert("strengthPercent " +strengthPercent +"msgNstrength_array[1] " +msgNstrength_array[1]+"password_settings.minLength"+password_settings.minLength+"barwidth"+barWidth);
                    }
                    
                    $("[id$='PasswordStrengthBorder']").css({ display: 'inline', width: barWidth });

                    //use multiple colors
                    if (password_settings.useMultipleColors === "1") {
                        //first 33% is red
                        if (parseInt(strengthPercent) >= 0 && parseInt(strengthPercent) <= (barWidth * .33)) {
                            backColor = "#EC5D55";
                        }
                        //33% to 66% is blue
                        else if (parseInt(strengthPercent) >= (barWidth * .33) && parseInt(strengthPercent) <= (barWidth * .67)) {
                            backColor = "#3583C3";
                        }
                        else {
                            backColor = password_settings.barColor;
                        }
                    }
                    
                    $("[id$='PasswordStrengthBar']").css({ display: 'inline', width: strengthPercent, 'background-color': backColor,'font-size':'5px' });

                    //remove last "," character
                    if (msgNstrength_array[0].lastIndexOf(",") !== -1) {
                        container.text(msgNstrength_array[0].substring(0, msgNstrength_array[0].length - 2));
                    }
                    else {
                        container.text(msgNstrength_array[0]);
                    }

                    if (strengthPercent == barWidth) {
                        password_settings.metRequirement = true;
                        validPassword=true;
                    }
                }
                else {
                    container.text('');
                    $("[id$='PasswordStrengthBorder']").css("display", "none"); //hide
                    $("[id$='PasswordStrengthBar']").css("display", "none"); //hide
                }
            });
        });
    };

})(jQuery);
$(document).ready(function() {
	$('#newPassword').attr('autocomplete', 'off');
	$('#newConfirmPassword').attr('autocomplete', 'off');
	
	$("input").keypress(function(event) {
		if (event.which == 13) {
			event.preventDefault();
		    $("#loginForm").submit();
		}
	});
	
	document.forms[0].oldPassword.value ='';
});
setTitle('<bean:message key="login.user.changepassword"/>');

</script>
<html:form action="/changePassword" styleId="changePasswordId">
	<html:hidden name="changePasswordForm" styleId="action" property="action" />
	<html:hidden name="changePasswordForm" styleId="password" property="password" />
	<html:hidden name="changePasswordForm" styleId="userId" property="userId" />
	<html:hidden name="changePasswordForm" styleId="auditUId" property="auditUId" />
	<html:hidden name="changePasswordForm" styleId="name" property="name" />
	<table width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>" cellspacing="0" cellpadding="0" border="0">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header" colspan="4">
										<bean:message key="login.user.changepassword" />
									</td>
								</tr>
								<tr>
									<td>
										<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%">
											<tr>
												<td class="captiontext" width="30%" height="20%">
													<bean:message key="login.user.username" />
													<ec:elitehelp headerBundle="StaffResources" text="changepwd.username" header="changepwd.username"/>	
												</td>
												<td class="labeltext" colspan="2" height="20%" style="font-size: 12px;">
													<bean:write name="changePasswordForm" property="userName" />
												</td>
											</tr>

											<tr>
												<td class="captiontext" width="30%" height="20%">
													<bean:message key="login.user.currentpassword" />
													<ec:elitehelp headerBundle="StaffResources" text="changepwd.currentpassword" header="changepwd.currentpassword"/>	
												</td>
												<td class="labeltext" colspan="2" height="20%">
													<html:password tabindex="1" name="changePasswordForm" property="oldPassword" style="width:250px" />
												</td>
											</tr>

											<tr>
												<td class="captiontext" width="30%" height="20%">
													<bean:message key="login.user.newpassword" />
													<ec:elitehelp headerBundle="StaffResources" text="changepwd.newpassword" header="changepwd.newpassword"/>	
												</td>
												<td class="labeltext">
												    <html:password property="newPassword" name="changePasswordForm" styleId="newPassword" style="width:250px" tabindex="2"></html:password>
												 </td>
											</tr>

											<tr>
												<td class="captiontext" width="30%" height="20%">
													<bean:message key="login.user.confirmnewpassword" />
													<ec:elitehelp headerBundle="StaffResources" text="changepwd.confirmnewpassword" header="changepwd.confirmnewpassword"/>	
												</td>
												<td class="labeltext" colspan="2" height="20%">
													<html:password tabindex="3" name="changePasswordForm" property="newConfirmPassword" styleId="newConfirmPassword" style="width:250px" />
												</td>
											</tr>

											<tr>
												<td class="btns-td" valign="middle" width="30%">&nbsp;</td>
												<td class="btns-td" colspan="2" valign="middle" align="left" width="70%">
													<input type="button" name="c_btnCreate" onclick="change()" tabindex="4" id="c_btnChange2" value="Change" class="light-btn"/>
													
												    <% 
												    	if(passwordPolicySelectionData.getChangePwdOnFirstLogin().equalsIgnoreCase("true") && staffData.getLastChangePasswordDate() == null){%>
												    		&nbsp; <input tabindex="5" type="reset" id="resetButton" name="c_btnDeletePolicy" onclick="resetFields();" value="Reset" class="light-btn"/> 	 
															
													<%}else{%>
															<input tabindex="5" type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='listNetServerInstance.do'" value="Change Later" class="light-btn"/>
													<%} %>
											    </td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>
	<input type="hidden" name="skipPasswordCheck" value="yes">
</html:form>

