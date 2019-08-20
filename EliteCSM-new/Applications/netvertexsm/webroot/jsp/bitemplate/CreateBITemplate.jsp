<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@page import="com.elitecore.netvertexsm.web.bitemplate.form.BITemplateForm"%>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="java.util.List" %>

<style> 
.allborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.bottomborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 1px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.topbottomborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 1px; border-right-width: 0px; border-bottom-width: 1px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.noborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.plcKey { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
</style> 


<script type="text/javascript">

$(document).ready(function(){
	setTitle('<bean:message bundle="biTemplateResources" key="bitemplate.header" />');
	$("#name").focus();
	$("#description").attr('maxlength','255');
});

var isValidName;
function validate(){
	if(isNull(document.forms[0].name.value)){
		alert('BI Template Name must be specified.');
	}else if(!isValidName) {
		alert('Enter Valid BI Template Name');
		document.forms[0].name.focus();
		return;
	}else if(isNull(document.forms[0].bikey.value)){
		alert('BI Template Key must be specified.');
		document.forms[0].bikey.focus();
		return;
	}else if(!validateSubKey()){
		return;
	}else if(!findDuplicateSubKey()){
		return;
	}else{
		document.forms[0].submit();
	}
}


function validateSubKey(){	
	var flag=true;		
	$("input[name=key]").each(function() {		
		if($(this).val().trim().length<1 && flag==true){			
			alert("Invalid Sub Key.");
			this.focus();
			flag= false;			
		}
	});
	return flag;
}


function findDuplicateSubKey(){	
	var flag=true;		
	var keyArray= document.getElementsByName("key");
	if(keyArray!=null && keyArray.length>1){
		for(var i=0; i<keyArray.length; i++){
			for(var j=i+1; j<keyArray.length; j++){				
				if(keyArray[i].value.trim()==keyArray[j].value.trim()){
					alert("Duplicate Sub Key : "+keyArray[i].value.trim());
					$("input[name=key]").each(function() {		
						if($(this).val().trim()==keyArray[i].value.trim()){										
							this.focus();							
						}
					});										
					flag=false;
					return;
				}
			}
		}
	}
	return flag;
}

//bkLib.onDomLoaded(function() { nicEditors.allTextAreas() });
function verifyFormat (){
	var searchName = document.getElementById("name").value;
	callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.BI_TEMPLATE%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}
function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.BI_TEMPLATE%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}

function addRow() {
	$('<tr><td class="allborder"><input class="noborder" tabindex="5"  type="text" name="key" id="key" maxlength="200" size="100"/></td><td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 5px; padding-top: 5px;" tabindex="5" height="14" /></td></tr>').appendTo('#bi');
	
	$("input[name=key]").each(function() {		
		if($(this).val().trim().length<1){						
			this.focus();			
}
	});
	$('table td img.delete').on('click',function() {
		$(this).parent().parent().remove();
	});
}

$('table td img.delete').on('click',function() {
	$(this).parent().parent().remove(); 
});


</script> 	

<html:form action="/createBITemplate"> 

<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp" %>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
				<bean:message bundle="biTemplateResources" key="bitemplate.create" />
			</td>
		  </tr>
		  
          <tr> 
				<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="97%" id="c_tblCrossProductList" align="right" border="0" > 				
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="biTemplateResources" key="bitemplate.name" /></td> 
					<sm:nvNameField maxLength="60" size="28"/> 
				  </tr>
				  
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="biTemplateResources" key="bitemplate.key" /></td> 
					<td align="left" class="labeltext" valign="top" width="20%" > 
						<html:text name="biTemplateForm" property="bikey" maxlength="100" size="28" styleId="bikey" tabindex="2" /><font color="#FF0000"> *</font>
					</td> 
				  </tr>	
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="biTemplateResources" key="bitemplate.description" /></td> 
					<td align="left" class="labeltext" valign="top" width="20%"> 
						<html:textarea name="biTemplateForm" property="description" cols="40" rows="3" styleId="description" tabindex="2"/>
					</td> 
				  </tr>				
				  
			   </table>  
			</td> 
		  </tr>	 
		  
	   <tr><td width="10" class="">&nbsp;</td></tr>
		<tr><td class=tblheader-bold colspan="5" style="padding-left: 2em"><bean:message bundle="biTemplateResources" key="bitemplate.biceasubkey" /></td></tr>
		<tr><td width="10" class="small-gap">&nbsp;</td></tr><tr><td width="10" class="small-gap">&nbsp;</td></tr> 
		  
		  <tr> 
          	<td colspan="4" height="20%" style="font-size: 11px; line-height: 20px; padding-left: 2.3em; font-weight: bold" >
            	<input type="button" name="add" value="Add Mapping" tabindex="4" class="light-btn" onclick="addRow()"></td>
          </tr>
          <tr>
			<td width="10" class="small-gap">&nbsp;</td>
			<td class="small-gap" colspan="2">&nbsp;</td>
		  </tr>	
				 
		  <tr> 
		  	<td class="btns-td" valign="middle" colspan="3">  
				<table id="bi" cellpadding="0" cellspacing="0" border="0" width="60%">
					<tr>
						<td align="center" class="tblheaderfirstcol" valign="top" width="95%"><bean:message bundle="biTemplateResources" key="bitemplate.subkey" /></td>
						<!-- td align="center" class="tblheader" valign="top" width="45%">Value</td<td class="tblrows"><input class="plcKey" type="text" name="value" maxlength="1024" size="40"/></td>-->
						<td align="center" class="tblheaderlastcol" valign="top" width="5%"><bean:message bundle="biTemplateResources" key="bitemplate.remove" /></td>
					</tr>
				</table>
			</td> 
		  </tr>	
		
		<tr> 
				<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		
		<tr align="center">
			<td style="padding-left: 1.6em" width="100%"><input type="button" onclick="validate();" value="  Create  " class="light-btn" tabindex="5"/>
			<input type="button" value=" Cancel " tabindex="29" class="light-btn" onclick="javascript:location.href='<%=basePath%>/searchBITemplate.do?/>'"/></td>
		</tr>
		<tr><td>&nbsp;</td></tr>		
		   		     		     		     		     		  
		</table> 
	  </td> 
	</tr>			
		 
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table>
</html:form>

