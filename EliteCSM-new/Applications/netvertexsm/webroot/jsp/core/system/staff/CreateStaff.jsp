<%@page import="com.elitecore.netvertexsm.blmanager.core.system.systemparameter.PasswordSelectionPolicyBLManager"%>
<%@page import="com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData"%>
<%@page import="com.elitecore.commons.base.Collectionz"%>
<%@page import="com.elitecore.corenetvertex.sm.acl.GroupData"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.staff.forms.CreateStaffForm"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>

<%
	String contextPath = request.getContextPath();
	CreateStaffForm createStaffForm = (CreateStaffForm)request.getAttribute("createStaffForm");
	Calendar c = Calendar.getInstance();
	String dateFormat = ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT);	
	List listAccessGroupList = (List)request.getAttribute("listAccessGroupList");
	List listSubscribedGroup = (List)request.getAttribute("listSubscribedGroup");
	List<GroupData> groupDatas = (List<GroupData>)request.getAttribute("groupList");
	
	PasswordSelectionPolicyBLManager passwordBLManager = new PasswordSelectionPolicyBLManager();
	PasswordPolicyConfigData passwordPolicySelectionData = passwordBLManager.getPasswordSelectionPolicy();
	boolean defaultPasswordPolicyFlag = passwordPolicySelectionData.isDefaultPasswordPolicy();
	
	int minTotal=0,maxTotal=0;
	String strProhChar="";

%>
<script type="text/javascript" src="<%=contextPath%>/js/dualList.js"></script>
<link href = "<%=contextPath%>/css/staff.css" rel="stylesheet"></link>
<script type="text/javascript" src="<%=contextPath%>/js/staff.js"></script>
<link href = "<%=contextPath%>/css/cropper.css" rel="stylesheet"></link>
<script src="<%=contextPath%>/js/cropper.js"></script>

<script>

if(<%=defaultPasswordPolicyFlag%>==true){
	alert("Invalid password policy ! Taking default password policy.");
} 

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

function checkPassword() {
	var password = document.forms[0].password.value;
	var confirmPassword = document.forms[0].newPassword.value;
	
	if(password!=confirmPassword){
		alert("Confirm Password mismatch");
	}
}

function isValidProhibitedChar(){
	var pwd=$('#password').val();
	
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

function echeck(str) {

	var at="@"
	var dot="."
	var lat=str.indexOf(at)
	var lstr=str.length
	var ldot=str.indexOf(dot)
	if (str.indexOf(at)==-1){
	   alert("Invalid E-mail ID")
	   return false
	}

	if (str.indexOf(at)==-1 || str.indexOf(at)==0 || str.indexOf(at)==lstr){
	   alert("Invalid E-mail ID")
	   return false
	}

	if (str.indexOf(dot)==-1 || str.indexOf(dot)==0 || str.indexOf(dot)==lstr){
	    alert("Invalid E-mail ID")
	    return false
	}

	 if (str.indexOf(at,(lat+1))!=-1){
	    alert("Invalid E-mail ID")
	    return false
	 }

	 if (str.substring(lat-1,lat)==dot || str.substring(lat+1,lat+2)==dot){
	    alert("Invalid E-mail ID")
	    return false
	 }

	 if (str.indexOf(dot,(lat+2))==-1){
	    alert("Invalid E-mail ID")
	    return false
	 }
	
	 if (str.indexOf(" ")!=-1){
	    alert("Invalid E-mail ID")
	    return false
	 }

		 return true					
}

function ValidateForm(){
var emailID=document.forms[0].emailAddress
if(emailID.value != ""){
	if (echeck(emailID.value)==false){
		emailID.value=""
		emailID.focus()
		return false
	}
}
return true

}

var warningMessage = "";

var groupDataArray = new Array();
function validateCreate()
{
	if(fileSizeExceeded == true){
    	alert('File size must not exceed 2 MB');
    	document.forms[0].profilePicture.focus();
    }else if(isNull(document.forms[0].name.value)){
		alert('Name must be specified');
		document.forms[0].name.focus();
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
	}else if(!validatePhoneNumber(document.forms[0].phone)){
		alert("Not a valid phone Number");
		document.forms[0].phone.focus();
	}else if(document.forms[0].mobile.value.length > 0 && !validateMobileNumber(document.forms[0].mobile)){
		alert('Enter valid Mobile Number.');
		document.forms[0].mobile.focus();
	}else if(isNull(document.forms[0].emailAddress.value)){
		alert('Email address must be specified');
		document.forms[0].emailAddress.focus();
	}else if($('#roleGroupRelation tr').length <= 1){
		alert('Resource group relation must be specified');
		$("#btnResourceGroupRelation").focus();
	} else if(validateEmailAddress(document.forms[0].emailAddress) == false){
		alert("Invalid E-mail ID");
		document.forms[0].emailAddress.focus();
	}else if(!isValidPassword()){
		alert('Password policy not match !! '+warningMessage);
		document.forms[0].password.focus();
	}else if(!isValidProhibitedChar()){
		document.forms[0].password.focus(); 
	}else if(isDuplicateGroup()){
       return false;
	} else{
		document.forms[0].submit();
	}
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
 	if(passwordPolicySelectionData.getAlphabetRange() != null){
 		minTotal=passwordPolicySelectionData.getAlphabetRange(); 		
 	}
 	
 	if(passwordPolicySelectionData.getDigitsRange() != null ){
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
    
(function($) {

	warningMessage = "";

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
		warningMessage = msg;
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


</script>
<script language="javascript1.2" src="<%=basePath%>/js/checkpwdstrength.js" type="text/javascript"></script>
 <script language = "javascript1.2">
function checkPasswordStrength(password) {
   	  
	var score = chkPass(password);
   		
	if(score == '0') {
    	document.getElementById("password").style.backgroundColor= "#ffffff";
		document.getElementById("passwordStrengthStatus").innerHTML = "";
	}
	if(score == '1') {
		document.getElementById("password").style.backgroundColor= "#ff5555";
   		document.getElementById("passwordStrengthStatus").innerHTML = "Weak";
	}
	if(score == '2') {
     	document.getElementById("password").style.backgroundColor= "#ffaa55";
		document.getElementById("passwordStrengthStatus").innerHTML = "Medium";
	}
	if(score == '3') {
     	document.getElementById("password").style.backgroundColor= "#d5ffaa";
		document.getElementById("passwordStrengthStatus").innerHTML = "Strong";
	}
  }

$(document).ready(function(){
	setTitle('<bean:message key="staff.staff" />');
	$("#name").focus();
	loadCropper();
});

function isDuplicateGroup(){
	var selectedGroupDatas=[];
	var isDuplicateGroup = false;
	$("input[name='groupData']").each(function(){
		var group = $(this).val();
		if($.inArray(group, selectedGroupDatas) !== -1){
			var groupName = $(this).parent('td').text();
			alert("Duplicate mapping found for "+groupName);
			isDuplicateGroup = true;
			return true;
		}
		selectedGroupDatas.push(group);
	});
	return isDuplicateGroup;
}

var i = 0;
function addRow(tableId){
	$.fx.speeds._default = 1000;
	document.getElementById("roleGroupRelationPopUp").style.visibility = "visible";		
	$( "#roleGroupRelationPopUp" ).dialog({
		modal: true,
		autoOpen: false,		
		height: 300,
		width: 500,				
		buttons:{		
            'Add': function() {	
            	var idVal = "groupData" + i;
            	var accessGroup = $("#roles option:selected").text();
            	var accessGroupId = $("#roles option:selected").val();
            	var selectedGroups = document.getElementsByName('select');
            	for(var i=0;i<selectedGroups.length;i++){
            		if(selectedGroups[i].checked == true){
            			var groupId = $(selectedGroups[i]).val();
                    	var groupData = $.trim($(selectedGroups[i]).parent().next('td').text());
            			$("#"+tableId).append("<tr><td class='tblfirstcol' tabindex='1'>"
    	 					+"<input type='hidden'  name='accessGroupData' value="+accessGroupId+" />"+accessGroup+"</td>"
    	 					+"<td class='tblrows' tabindex='2'><input type='hidden' name='groupData' value="+groupId+" />"+groupData+"</td>"
    	 					+"<td class='tblrows' align='center' style='cursor:default' >"
    	 					+"<img tabindex='3' src='<%=basePath%>/images/minus.jpg'  class='delete' height='15' /></td></tr>");
						$('#'+tableId+' td img.delete').on('click',function() {
							$(this).parent().parent().remove();
						});		
        			}
            	}
 				i++;
 				disableSelect();
 				$("#roles").val(""); 
 				$(this).dialog('close');
         	
            },                
		    Cancel: function() {
            	$(this).dialog('close');
        	}
        },
    	open: function() {
			var selectedGroupDatas=[];
			//Currently selected groups
			$("input[name='groupData']").each(function(){
				selectedGroupDatas.push($(this).val());
			});

			//Groups in popup
			$(".groupCheckBox").each(function(){
				var group = $(this).val();
				if($.inArray(group,selectedGroupDatas) !== -1){
					$(this).attr('disabled','true');
					$(this).parent().next().children(0).css("color","gray");
				} else {
					$(this).removeAttr('disabled');
					$(this).parent().next().children(0).css("color","black");
				}
			});
    	},
    	close: function() {
    		$(this).dialog('close');
    	}				
	});
	$( "#roleGroupRelationPopUp" ).dialog("open");
	
}

function  checkAll(){
	var counter=0;
	var selectAllData = document.getElementById('toggleAll');
 	if( selectAllData.checked == true) {
 		var selectVars = document.getElementsByName('select');
	 	for (var i = 0; i < selectVars.length;i++){
	 		if(selectVars[i].disabled == false){
				selectVars[i].checked = true ;
				counter++;
	 		}
	 	}
    } else if (selectAllData.checked == false){
 		var selectVars = document.getElementsByName('select');	    
		for (var i = 0; i < selectVars.length; i++)
			if(selectVars[i].disabled == false){
				selectVars[i].checked = false ;
				counter++;
	 		}
	}
}
function disableSelect(){
	var selectAllData = document.getElementById('toggleAll');
 	if( selectAllData.checked == true) {
 		selectAllData.checked = false;
 	}
 	var selectedGroups = document.getElementsByName('select');
	for (var i = 0; i < selectedGroups.length;i++){
	 	if(selectedGroups[i].checked == true){
	 		selectedGroups[i].checked = false ;
	 	}
	 }
    
}
</script>  


<html:form action="/createStaff" enctype="multipart/form-data" >
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
		<tr>
			<td width="10">&nbsp;</td>
			<td class="box" cellpadding="0" cellspacing="0" border="0" width="100%">
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td class="table-header" colspan="5">
							<bean:message key="staff.createstaff" />
						</td>
					</tr>
					<tr>
						<td class="small-gap" colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle" colspan="3">
							<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
								<tr>
									<td align="left" class="tblheader-bold" valign="top"colspan="2">
										<bean:message key="staff.staffdetails" />
									</td>
								</tr>
								
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%">
										<bean:message key="staff.profilepicture" />
									</td>
									
									<td align="left" class="labeltext" valign="top" width="82%">
										<img height="100" width="100" id="croppedImage" alt="No Profile Picture" src="<%=contextPath%>/images/defaultProfilePicture.jpg">
										<br>
										<input type="hidden" id="profilePicture" name="profilePicture"/>
										<br> 
										<html:file property="profilePictureFile" accept="image/*" styleId="profilePictureFile" onchange="loadImage(event)" onclick="clearImage(this);"></html:file> 
										<label class="small-text-grey"> <bean:message key="staff.profilepicture.note" /></label>
										<label class="small-text-grey" id="warning" style="display: none; color: #FF0000;"> <bean:message key="staff.profilepicture.warning" /></label>
										<br>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%">
										<bean:message key="staff.name" />
									</td>
									<td align="left" class="labeltext" valign="top" width="82%">
										<html:text styleId="name" property="name" maxlength="60" /><font color="#FF0000"> *</font> 
										<html:checkbox	name="createStaffForm" styleId="status" property="status" value="1" /> Active
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%">
										<bean:message key="staff.username" />
									</td>
									<td align="left" class="labeltext" valign="top">
										<html:text styleId="userName" property="userName" size="20"	maxlength="18" /> <font color="#FF0000"> *</font>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%">
										<bean:message key="staff.password" />
									</td>
									<td align="left" class="labeltext" valign="top">
										<html:password styleId="password" property="password" size="20"	onkeyup="" maxlength="50" />
										<font color="#ff0000"> * </font> 
										<label id="passwordStrengthStatus" />
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%">
										<bean:message key="staff.confirmpassword" />
									</td>
									<td align="left" class="labeltext" valign="top">
										<html:password styleId="newPassword" property="newPassword" size="20" maxlength="50" onblur="checkPassword();"  /> <font color="#FF0000"> *</font>
									</td>
								</tr>
							</table>
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td align="left" class="tblheader-bold" valign="top" colspan="2">
										<bean:message key="staff.contactdetails" />
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%">
										<bean:message key="staff.phonenumber" />
									</td>
									<td align="left" class="labeltext" valign="top" width="82%">
										<html:text styleId="phone" property="phone" size="20" maxlength="20" />
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%">
										<bean:message key="staff.mobilenumber" />
									</td>
									<td align="left" class="labeltext" valign="top" width="82%">
										<html:text styleId="mobile" property="mobile" size="20"	maxlength="20" />
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%">
										<bean:message key="staff.emailaddress" />
									</td>
									<td align="left" class="labeltext" valign="top" width="82%">
										<html:text styleId="emailAddress" property="emailAddress" size="30" maxlength="50" /> <font color="#FF0000"> *</font>
									</td>
								</tr>
							</table>
							
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td align="left" class="tblheader-bold" colspan="3">
										<bean:message key="staff.assignaccessgroup" /></td>
								</tr>
								<tr> 
								        <td class="" colspan="4" height="20%" style="font-size: 11px; line-height: 20px; padding-left: 2.3em; font-weight: bold" >
								        <input type="button" id="btnResourceGroupRelation" name="add" value="Add Resource-Group" class="light-btn" onclick="addRow('roleGroupRelation')"></td>
								</tr>
								<tr>
									<td width="10" class="small-gap">&nbsp;</td>
									<td class="small-gap" colspan="2">&nbsp;</td>
								</tr>

								<tr width="100%">
									<td colspan="4">
										<table cellpadding="0" id="roleGroupRelation" cellspacing="0" border="0"
											width="97%" >
											<thead>
												<tr>
													<td align="center" class="tblheader" valign="top"
														width="45%"><bean:message key="staff.role" /></td>
													<td align="center" class="tblheader" valign="top"
														width="50%"><bean:message key="staff.group" /></td>
													<td align="center" class="tblheader" valign="top"
														width="5%"><bean:message key="staff.remove" /></td>
												</tr>
											</thead>
											<tbody>
											</tbody>
										</table>
									</td> 
									</tr>	
								</table>
						</td>
					</tr>
					<tr>
									<td class="btns-td" valign="middle">&nbsp;</td>
									<td class="btns-td" valign="middle" colspan="2">
										<input	type="button" name="c_btnCreate" onclick="validateCreate()" id="c_btnCreate2" value=" Create " class="light-btn"> 
										<input	type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/searchStaff.do?/>'" value="Cancel" class="light-btn"></td>
								</tr>
				</table>
			</td>
		</tr>
		<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
	</table>
	
<div class="container" id="container" style="display: none;">
 	<div class="img-container">
		<img src="<%=contextPath%>/images/defaultProfilePicture.jpg" alt="Picture" id="picture">
		<br>
	</div>
    <div class="preview-box">
    	 <div class="docs-preview clearfix">
          	<div class="img-preview preview-md"></div>
          	<div class="img-preview preview-md-round"></div>
          	<div class="img-preview preview-xs-round"></div>
        </div>
			
    </div>
</div>
<!-- Pop Up code -->
<div id="roleGroupRelationPopUp" title="Add Group Role Relation" style="display: none;" class="labeltext">
	<table id="roleGroupRelationPopUpTable" width="100%" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top" width="30%" class="labeltext"><bean:message key="staff.role" /></td>
			<td class="labeltext">
			<html:select styleId="roles" property="available" tabindex="1" >
				<html:options collection="listAccessGroupList"	property="roleId" labelProperty="name" />
			</html:select>
			</td>
		</tr>
		<tr>	
			<td>&nbsp;</td>
		</tr>
		<tr><td valign="top" width="100%"  class="tblheader-bold" colspan="6"><bean:message key="staff.group" /></td></tr>
		<tr>
								<td align="center" class="labeltext" width="3%" >
									<input type="checkbox" name="toggleAll" id="toggleAll" onclick="checkAll()" tabindex="8"/>
								</td>
								<td align="left" class="labeltext" width="30%">
									<b>Select All</b>
								</td>
							</tr>
							<tr>	
								<td>&nbsp;</td>
							</tr>	
		<tr>
			
			<td class="labeltext">
			<%	
							if(Collectionz.isNullOrEmpty(createStaffForm.getGroupDatas()) == false) {
								
								int index=0;
								
						%>		
									<logic:iterate id="groupDataBean" name="createStaffForm" property="groupDatas" type="com.elitecore.corenetvertex.sm.acl.GroupData">
									<%if(index%3==0){ %>	
									<tr>			
									<%}%>						
									
											<td align="center" valign="top" class="labeltext" width="3%" >
												<input type="checkbox" class="groupCheckBox" tabindex="9" name="select" value="<bean:write name="groupDataBean" property="id"/>"  />
											</td>
											<td align="left" valign="top" class="labeltext" width="30%">
													<lable><bean:write name="groupDataBean" property="name" />&nbsp;</lable>
											</td>
									<%if(index%3==2){ %>		
									</tr>
									<tr>	
										<td>&nbsp;</td>
									</tr>				
									<%}%>
									<%index++;%>																																																																																																																																																																										
									</logic:iterate>
									<%if(createStaffForm.getGroupDatas().size()%3!=0){%>
									</tr>
									<%}%>
											
																				
					<%}else{%>
						<tr>
							<td align="center" class="tblfirstcol" colspan="7">No Records Found.</td>
						</tr>
						
					<%}%>
			
			</td>
		</tr>
	</table>
</div>	
	
</html:form>

