<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.web.core.system.staff.forms.CreateStaffForm"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.blmanager.core.system.systemparameter.PasswordSelectionPolicyBLManager" %>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData" %>

<%
    String basePath = request.getContextPath();
	CreateStaffForm createStaffForm = new CreateStaffForm();
	Calendar c = Calendar.getInstance();
	String dateFormat = ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT);
	PasswordSelectionPolicyBLManager passwordBLManager=new PasswordSelectionPolicyBLManager();
	PasswordPolicyConfigData passwordPolicySelectionData=passwordBLManager.getPasswordSelectionPolicy();
	int minTotal=0,maxTotal=0;
	String strProhChar="";
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
    var isValidName;
    var validPassword=false;
    var flagTrack=false;
	var dFormat;
	dFormat = '<%=dateFormat%>';
		
	function popUpCalendar(ctl,	ctl2, datestyle)
	{
		//Get format from system parameter document.form[0].
		datestyle = dFormat;
		jsPopUpCalendar( ctl, ctl2, datestyle ); 

	}
	function isValidPassword(){
		
		if(validPassword == false){
			<%if(passwordPolicySelectionData.getMinPasswordLength() == null && passwordPolicySelectionData.getMaxPasswordLength() == null){%>
				return true;
			<%}%>
		}
		return validPassword;
	}

	function isValidProhibitedChar(){
		var pwd=$('#password').val();
		
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
	function echeck(str) {

		var at="@"
		var dot="."
		var lat=str.indexOf(at)
		var lstr=str.length
		var ldot=str.indexOf(dot)
		if (str.indexOf(at)==-1){
		   alert("Invalid E-mail ID")
		   return false;
		}

		if (str.indexOf(at)==-1 || str.indexOf(at)==0 || str.indexOf(at)==lstr){
		   alert("Invalid E-mail ID")
		   return false;
		}

		if (str.indexOf(dot)==-1 || str.indexOf(dot)==0 || str.indexOf(dot)==lstr){
		    alert("Invalid E-mail ID")
		    return false;
		}

		 if (str.indexOf(at,(lat+1))!=-1){
		    alert("Invalid E-mail ID")
		    return false;
		 }

		 if (str.substring(lat-1,lat)==dot || str.substring(lat+1,lat+2)==dot){
		    alert("Invalid E-mail ID")
		    return false;
		 }

		 if (str.indexOf(dot,(lat+2))==-1){
		    alert("Invalid E-mail ID")
		    return false;
		 }
		
		 if (str.indexOf(" ")!=-1){
		    alert("Invalid E-mail ID")
		    return false;
		 }

 		 return true;					
	}

function ValidateForm(){
	var emailID=document.forms[0].emailAddress
	
	if ((emailID.value==null)||(emailID.value=="")){
		alert("Please Enter your Email ID")
		emailID.focus()
		return false
	}
	if (echeck(emailID.value)==false){
		emailID.value=""
		emailID.focus()
		return false
	}
	
	return true
 }

function validateCreate()
{
    //var mobileNumber=document.forms[0].mobile.value;
   
	if(isNull(document.forms[0].name.value)){
		alert('Name must be specified');
		document.forms[0].name.focus();
	}else if(!isValidName) {
 			alert('Enter Valid USer Name');
 			document.forms[0].userName.focus();
 			return false;
 	}else if(isNull(document.forms[0].userName.value)){
		alert('UserName must be specified');
		document.forms[0].userName.focus();
	}else if(isNull(document.forms[0].password.value)){
		alert('Password must be specified');
		document.forms[0].password.focus();
	}else if(isNull(document.forms[0].newPassword.value)){
		alert('Confirm Password must be specified');
		document.forms[0].newPassword.focus();
	}else if(document.forms[0].password.value!=document.forms[0].newPassword.value){
		alert('Please confirm the password.');
		document.forms[0].newPassword.focus();
	}else if(isNull(document.forms[0].strStatusChangeDate.value)){
		alert('BirthDate must be specified');
		document.forms[0].strStatusChangeDate.focus();
	}else if(isNull(document.forms[0].address1.value)){
		alert('Address1 must be specified');
		document.forms[0].address1.focus();
	}else if(isNull(document.forms[0].city.value)){
		alert('City must be specified');
		document.forms[0].city.focus();
	}else if(isNull(document.forms[0].state.value)){
		alert('State must be specified');
		document.forms[0].state.focus();
	}else if(isNull(document.forms[0].country.value)){
		alert('Country must be specified');
		document.forms[0].country.focus();
    }else if(isNull(document.forms[0].zip.value)){
        alert('Pin Code must be specified');
        document.forms[0].zip.focus();
    }else if(isNull(document.forms[0].emailAddress.value)){
        alert('Email ID must be specified');
        document.forms[0].emailAddress.focus();
	}else if(!validatePhoneNumber(document.forms[0].phone)){
		alert('Enter valid Phone Number.');
		 document.forms[0].phone.focus();
	}else if(document.forms[0].mobile.value.length > 0 && !validateMobileNumber(document.forms[0].mobile.value)){
		alert('Enter valid Mobile Number.');
		 document.forms[0].mobile.focus();
	}else if(document.forms[0].zip.value.length > 0 &&  !IsNumeric(document.forms[0].zip.value)){
		alert('Enter valid Pin Code.');
		document.forms[0].zip.focus();
	}else if(!isValidPassword()){
		alert('Password policy not match !!');
		document.forms[0].password.focus();
	}else if(!isValidProhibitedChar()){
		document.forms[0].password.focus(); 
	}else if(ValidateForm() == false){}
	else
	{
		document.forms[0].submit();
	}
}

// mobile number validation

function validateMobileNumber(val){
	
	nre=/^\+?(\d)+$/;
	var regexp = new RegExp(nre);
	if(!regexp.test(val))
	{
		return false;
		
	}
	return true;
}
$(document).ready(function() {
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
                    
        return $('#password').each(function() {
            //bar position
            var barLeftPos = $("[id$='password']").position().left + $("[id$='password']").width();
            var barTopPos = $("[id$='password']").position().top + $("[id$='password']").height();

            //password indicator text container
            var container = $('<span></span>')
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
</script>
<style>
.tooltip {
    position:absolute;
    display:none;
    z-index:1000;
    background-color:black;
    color:white;
    border: 1px solid black;
}
</style>
<script language="javascript1.2"
	src="<%=basePath%>/js/checkpwdstrength.js" type="text/javascript"></script>
<script language="javascript1.2">
    function verifyName() {
    	var searchName = document.getElementById("userName").value;
    	isValidName = verifyInstanceName('<%=InstanceTypeConstants.STAFF%>',searchName,'create','','verifyNameDiv');
    }
    setTitle('<bean:message bundle="StaffResources" key="staff.staff"/>');
</script>


<html:form action="/createStaff">
	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header">
										<bean:message bundle="StaffResources" key="staff.createstaff" />
									</td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td valign="middle" colspan="3">
										<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
											<tr>
												<td align="left" class="tblheader-bold" valign="top" colspan="3">
													<bean:message bundle="StaffResources" key="staff.logindetails" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="StaffResources" key="staff.name" />
													<ec:elitehelp headerBundle="StaffResources" text="staff.name" header="staff.name"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="23%">
													<html:text styleId="name" property="name" size="30" tabindex="1" maxlength="60" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:checkbox name="createStaffForm" tabindex="2" styleId="status" property="status" value="1" /> Active
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="StaffResources" key="staff.username" />
													<ec:elitehelp headerBundle="StaffResources" text="staff.username" header="staff.username"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text styleId="userName" onkeyup="verifyName();" tabindex="3" property="username" size="20" maxlength="18" style="width:250px" />
													<font color="#FF0000"> *</font>
													<div id="verifyNameDiv" class="labeltext"></div>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="StaffResources" key="staff.password" />
													<ec:elitehelp headerBundle="StaffResources" text="staff.password" header="staff.password"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:password styleId="password" property="password" size="20" maxlength="50" style="width:250px" tabindex="4" /> 
													<font color="#ff0000"> * </font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="StaffResources" key="staff.confirmpassword" />
													<ec:elitehelp headerBundle="StaffResources" text="staff.confirmpassword" header="staff.confirmpassword"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:password styleId="newPassword" property="newPassword" size="20" tabindex="5" maxlength="50" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
										</table>
										<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
											<tr>
												<td align="left" class="tblheader-bold" valign="top"
													colspan="3"><bean:message bundle="StaffResources"  key="staff.personaldetails" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="StaffResources" key="staff.birthdate" />
													<ec:elitehelp headerBundle="StaffResources" text="staff.birthdate" header="staff.birthdate"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="strStatusChangeDate" property="strStatusChangeDate" size="10" tabindex="6" maxlength="15" style="width:250px" /> 
													<font color="#FF0000"> *</font> 
													<a href="javascript:void(0)" onclick="popUpCalendar(this, document.forms[0].strStatusChangeDate)">
														<img src="<%=basePath%>/images/calendar.jpg" border="0" tabindex="6">
													</a>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="StaffResources" key="staff.address1" />
													<ec:elitehelp headerBundle="StaffResources" text="staff.address1" header="staff.address1"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="address1" property="address1" size="60" tabindex="7" maxlength="60" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="StaffResources" key="staff.address2" />
													<ec:elitehelp headerBundle="StaffResources" text="staff.address2" header="staff.address2"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="address2" property="address2" size="60" tabindex="8" maxlength="60" style="width:250px" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="StaffResources" key="staff.city" />
													<ec:elitehelp headerBundle="StaffResources" text="staff.city" header="staff.city"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
													<html:text tabindex="9" styleId="city" property="city" size="30" maxlength="25" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="StaffResources" key="staff.state" />
													<ec:elitehelp headerBundle="StaffResources" text="staff.state" header="staff.state"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
													<html:text styleId="state" property="state" size="30" tabindex="10" maxlength="25" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="StaffResources" key="staff.country" />
													<ec:elitehelp headerBundle="StaffResources" text="staff.country" header="staff.country"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
													<html:text tabindex="11" styleId="country" property="country" size="30" maxlength="25" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="StaffResources" key="staff.pincode" />
													<ec:elitehelp headerBundle="StaffResources" text="staff.pincode" header="staff.pincode"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="zip" property="zip" tabindex="12" size="6" maxlength="6" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="StaffResources" key="staff.emailaddress" />
													<ec:elitehelp headerBundle="StaffResources" text="staff.emailaddress" header="staff.emailaddress"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="emailAddress" property="emailAddress" tabindex="13" size="40" maxlength="50" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
										</table>
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td align="left" class="tblheader-bold" valign="top" colspan="2">
													<bean:message bundle="StaffResources" key="staff.contactdetails" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="StaffResources" key="staff.phonenumber" />
													<ec:elitehelp headerBundle="StaffResources" text="staff.phonenumber" header="staff.phonenumber"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="82%">
													<html:text styleId="phone" property="phone" size="20" tabindex="14" maxlength="20" style="width:250px" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="StaffResources" key="staff.mobilenumber" />
													<ec:elitehelp headerBundle="StaffResources" text="staff.mobilenumber" header="staff.mobilenumber"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="82%">
													<html:text styleId="mobile" property="mobile" size="20" tabindex="15" maxlength="20" style="width:250px" />
												</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle" colspan="2">
													<input tabindex="16" type="button" name="c_btnCreate" onclick="validateCreate()" id="c_btnCreate2" value=" Next " class="light-btn"> 
													<input tabindex="17" type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchStaff.do?/>'" value="Cancel" class="light-btn">
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
</html:form>
<div class="tooltip">
<table>
	<tr>
		<td>5-4 Password Length</td>
		<td>2-4 Password Alphabets</td>
	</tr>
	
</table>
</div>

