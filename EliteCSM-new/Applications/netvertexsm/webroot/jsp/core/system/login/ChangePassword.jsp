<%@page import="com.elitecore.netvertexsm.util.constants.SystemLoginConstants"%>
<%@page import="com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData"%>
<%@page import="com.elitecore.netvertexsm.blmanager.core.system.systemparameter.PasswordSelectionPolicyBLManager"%>
<%@page import="com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffData"%>
<%@page import="com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData"%>
<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm"%>
<%
    SystemLoginForm loginForm = (SystemLoginForm) request.getSession().getAttribute("radiusLoginForm");
    IStaffData staffData = (StaffData) request.getSession().getAttribute("staffData");
    PasswordSelectionPolicyBLManager passwordBLManager = new PasswordSelectionPolicyBLManager();
    PasswordPolicyConfigData passwordPolicySelectionData = passwordBLManager.getPasswordSelectionPolicy();
    boolean defaultPasswordPolicyFlag = passwordPolicySelectionData.isDefaultPasswordPolicy();

    int minTotal = 0, maxTotal = 0;
    String strProhChar = "";
    String url = request.getRequestURL().toString();
    String PASSWORD_EXPIRY_FLAG = (String) request.getSession().getAttribute(SystemLoginConstants.PASSWORD_EXPIRY_FLAG);
%>
<script>

if(<%=defaultPasswordPolicyFlag%>==true){
	alert("Invalid password policy ! Taking default password policy.");
} 

var validPassword=false;
var flagTrack=false;
$(document).ready(function(){
	setTitle('<bean:message key="login.user.user" />');
	$("#userId").focus();
	<%-- <%if(staffData.getPasswordChangeDate() == null){ %>
		alert("Please change your password");		
	<%}%> --%>
	<% if(passwordPolicySelectionData.getChangePwdOnFirstLogin().equalsIgnoreCase("true") && staffData.getPasswordChangeDate() == null){%>
		var originHost = document.referrer;
		if(originHost.indexOf("login.do") < 0){
			alert('You are required to change your password on your first login');		
		}
	<% } else if(staffData.getPasswordChangeDate()==null){ %>
			alert('Its better to change your password, change now');
	<% } %>
	
	<% if(SystemLoginConstants.YES.equalsIgnoreCase(PASSWORD_EXPIRY_FLAG)){ %>
		alert('Your password has expired , change now');		
	<%} %> 
	
	var checkflag=false;
	<%
		if(passwordPolicySelectionData.getMinPasswordLength() != null && passwordPolicySelectionData.getMaxPasswordLength() != null){
	%>
	 	var myPlugin = $("input[id$='txtPassword']").password_strength();
	 	checkflag=true;
	<%}else{%>
	<%
 
	if(passwordPolicySelectionData.getAlphabetRange() != null){
	    minTotal=passwordPolicySelectionData.getAlphabetRange();		
	}
 
	
	if(passwordPolicySelectionData.getDigitsRange() != null){
		minTotal=minTotal+passwordPolicySelectionData.getDigitsRange();
	}
 
	
	if(passwordPolicySelectionData.getSpecialCharRange() != null){
		minTotal=minTotal+passwordPolicySelectionData.getSpecialCharRange();		
	}
	
	if(minTotal !=0 && maxTotal != 0){%>
		if(checkflag == false){
			var myPlugin = $("input[id$='txtPassword']").password_strength();
		}
	<%}}%>	
});
function isValidPassword(){
	
	if(validPassword == false){
		<%if (passwordPolicySelectionData.getMinPasswordLength() == null && passwordPolicySelectionData.getMaxPasswordLength() == null) {%>
			return true;
		<%}%>
	}
	return validPassword;
}

function isValidProhibitedChar(){
	var pwd=$('#newPassword').val();
	
	<%
		strProhChar = passwordPolicySelectionData.getProhibitedChars();
		if(strProhChar!=null){
	    	strProhChar = strProhChar.replace("\\", "\\\\");
	    	strProhChar = strProhChar.replace("'", "\\'");
		}
	%>
	
	var policyPwd='<%=strProhChar%>';
	
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

var warningMessage = "";

function change(){

	var loginPassword = '';
	<%if (loginForm != null) {%>
		loginPassword = '<%=loginForm.getPassword()%>'
	<%}%>
	
	if(isNull(document.forms[0].oldPassword.value)){
		document.forms[0].oldPassword.focus();
		alert('Current User Password must be specified');
	}else if(document.forms[0].oldPassword.value != loginPassword){
		document.forms[0].oldPassword.focus();
		alert('Current User Password does not match current user Logged-In Password');
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
	}else if(!isValidPassword()){
		alert('Password policy not match !! '+warningMessage);
		document.forms[0].newPassword.focus(); 
	}else if(!isValidProhibitedChar()){
		document.forms[0].newPassword.focus(); 
	} else{
		document.forms[0].action.value = 'change';
		var staffList = $(".staffList").length;
		
		if(staffList > 0){
							
    		var staffId = $('#userId').val();
    		var index = document.getElementById("userId").selectedIndex;
    		var selectedUser = document.getElementById("userId").options[index].text;
    		var loggedInUsername = $('#userName').val();
			
    		if(staffId != 1 && jQuery.trim(loggedInUsername)!=jQuery.trim(selectedUser)){
    			document.getElementById("subStaffnewPassword").value=document.getElementById("newPassword").value;    		
    			document.getElementById("subStaffId").value=staffId;    		
    			document.forms[1].submit();
    		}else if(staffId == 1 && jQuery.trim(loggedInUsername)!=jQuery.trim(selectedUser)){
					alert('Admin password cannot be changed');
			}else{
				document.forms[0].submit();	
			} 
		}else{
			document.forms[0].submit();
		}
	}
}
function cancel(){
	document.forms[0].action.value = 'view';
	document.getElementById("newPassword").style.backgroundColor=""; 
	document.getElementById("passwordStrengthStatus").innerHTML = "";
}

function resetFields(){
    $('#PasswordStrengthBorder').hide();
    $('#PasswordStrengthBar').hide();
    $('#PasswordStrengthText').text('');
}

(function($) {

    var password_Strength = new function() {

	warningMessage = "";
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
    alphabets=this.countRegExp(passwordVal, /[a-zA-Z]/g),
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

		warningMessage = msg;
		
     //strong password
     if (pStrength == password_settings.minLength ) {
       msg = "Strong password!";
     }

      return msg + ';' + pStrength;
     }
   }

    <%
    	if (passwordPolicySelectionData.getProhibitedChars() == null) {
			strProhChar = "";
	  	}
	 %>
    
    //default setting
    var password_settings = {
        minLength: '<%=passwordPolicySelectionData.getMinPasswordLength()%>',
        maxLength:'<%=passwordPolicySelectionData.getMaxPasswordLength()%>',
        specialLength:'<%=passwordPolicySelectionData.getSpecialCharRange()%>',
        minalphabetsRange:'<%=passwordPolicySelectionData.getAlphabetRange()%>',
        numberLength: '<%=passwordPolicySelectionData.getDigitsRange()%>',
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
           _numsLength = '<%=passwordPolicySelectionData.getDigitsRange()%>',
           _specialLength = '<%=passwordPolicySelectionData.getSpecialCharRange()%>',
           _minalphabetsRange='<%=passwordPolicySelectionData.getAlphabetRange()%>',
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
						} else if (parseInt(strengthPercent) >= (barWidth * .68) && parseInt(strengthPercent) <= (barWidth * .99)) {
							backColor = "#FF8C00"; // DARK YELLOW
                        } else {
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
});
setTitle('<bean:message key="login.user.changepassword"/>');

</script>

<script language="javascript1.2" src="<%=basePath%>/js/checkpwdstrength.js" type="text/javascript"></script>
<script language = "javascript1.2">
    function checkPasswordStrength(password) {
   	  
   		var score = chkPass(password);
   		
   		if(score == '0')
   		{ 
    	 document.getElementById("newPassword").style.backgroundColor= "#ffffff";
		 document.getElementById("passwordStrengthStatus").innerHTML = "";
		}
		if(score == '1')
   		{
   		
   		document.getElementById("newPassword").style.backgroundColor= "#ff5555";
   		document.getElementById("passwordStrengthStatus").innerHTML = "Weak";
     	
		}
		if(score == '2')
   		{
     	document.getElementById("newPassword").style.backgroundColor= "#ffaa55";
		document.getElementById("passwordStrengthStatus").innerHTML = "Medium";
		}
		if(score == '3')
   		{
     	document.getElementById("newPassword").style.backgroundColor= "#d5ffaa";
		document.getElementById("passwordStrengthStatus").innerHTML = "Strong";
		}
		
		
		
  }
</script>  
<html:form styleId="changePasswordForm" action="/changePassword">
<%
    /* --- Start of Page Header --- only edit module name*/
%>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

	<%
	    /* --- End of Page Header --- 
			     --- Module specific code starts from below.*/
	%>
	<tr>
		<td width="1">&nbsp;</td>
		<td width="100%" valign="top" class="box" align="left">
		
			
				<html:hidden name="changePasswordForm" styleId="action"	
				   property="action" />
				<html:hidden name="changePasswordForm" styleId="password" 
				   property="password" />
				<html:hidden name="changePasswordForm"  styleId="userName"  property="userName" />
				
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="table-header" colspan="3">
						<bean:message key="login.user.changepassword" /></td>
					</tr>
					
					<tr>
						<td width="3%">&nbsp;</td>
						<td valign="top" width="97%">
						<table width="80%" border="0" cellspacing="0" cellpadding="0"
						    height="15%">

							<tr>
								<td class="labeltext" width="30%" height="20%"><bean:message
								    key="login.user.username" /></td>
								    
								<td class="labeltext" colspan="2"  height="20%">
									<html:text styleId="userName" property="userName" readonly="readonly" ></html:text>
									<html:hidden styleId="userId" property="userId"/>
								 </td>
							</tr>

							
	
							<tr>
								<td class="labeltext" width="30%" height="20%"><bean:message
								    key="login.user.currentpassword" /></td>
								<td class="labeltext" colspan="2" height="20%"><html:password 
								    name="changePasswordForm" property="oldPassword" maxlength="50" /><font color="#FF0000"> *</font></td>
							</tr>
	
							<tr>
								<td class="labeltext" width="30%" height="20%"><bean:message 
								    key="login.user.newpassword" /></td>
								<td class="labeltext"  height="20%"><html:password 
								    name="changePasswordForm"  property="newPassword" styleId="newPassword" maxlength="50" /><font color="#FF0000"> *</font>

							</tr>
	
							<tr>
								<td class="labeltext" width="30%" height="20%"><bean:message 
								    key="login.user.confirmnewpassword" /></td>
								<td class="labeltext" colspan="2"  height="20%"><html:password 
								    name="changePasswordForm" property="newConfirmPassword" maxlength="50" /><font color="#FF0000"> *</font></td>
							</tr>
							<tr> <td> <input type="hidden" name="skipPasswordCheck" value="1">  </td></tr>
							<tr>
								<td class="btns-td" valign="middle" width="30%">&nbsp;</td>
								<td class="btns-td" colspan="2" valign="middle" align="left" width="70%">
								<input type="button" name="c_btnCreate" onclick="change()"
									id="c_btnChange2" value="Change" class="light-btn"> <input
									type="reset" name="c_btnDeletePolicy" onclick="cancel()"
									value="Reset" class="light-btn"></td>
							</tr>
	
						</table>
						</td>
					</tr>
	
				</table>
			</html:form></td>
	</tr>
	<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
	
	<html:form action="/changeStaffPassword">
		<html:hidden 	name="changeStaffPasswordForm" 	styleId="subStaffAction" 			property="action" 		value="update" />
		<html:hidden 	name="changeStaffPasswordForm"  styleId="subStaffnewPassword" 		property="newPassword"  /> 	    
		<html:hidden 	name="changeStaffPasswordForm" 	styleId="subStaffId" 				property="staffId"		/>			
		<input type="hidden" name="skipPasswordCheck" value="1">
	</html:form>

</table>

