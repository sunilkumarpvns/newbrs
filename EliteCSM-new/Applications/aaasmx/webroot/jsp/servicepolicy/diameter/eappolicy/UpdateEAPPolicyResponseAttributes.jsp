<%@ page import="com.elitecore.diameterapi.diameter.common.util.constant.CommandCode" %>
<%@ page import="com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy.forms.UpdateEAPPolicyForm" %>
<%@ page import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPResponseAttributes"  %>
<%
	UpdateEAPPolicyForm updateEAPPolicyForm =(UpdateEAPPolicyForm)request.getAttribute("updateEAPPolicyForm");
	Set<EAPResponseAttributes> eapResponseAttributes = updateEAPPolicyForm.getEapResponseSet();
%>
<script>
var commandCodeList = [];

<%for(CommandCode commandCode:CommandCode.VALUES){%>
	commandCodeList.push({'value':'<%=commandCode.code%>','label':'[<%=commandCode.code%>] <%=commandCode.name()%>','id':'<%=commandCode.code%>'});
<%}%>

$(document).ready(function() {		
	$('.responseAttributeTable td img.delete').live('click',function() {
		 $(this).parent().parent().remove(); 
	});	
	setCommandCodeAutocompleteData();
});

function addResponseAttributesTable(tableId,templateId){
	var tableRowStr = $("#"+templateId).find("tr");
	$("#"+tableId+" tr:last").after("<tr>"+$(tableRowStr).html()+"</tr>");
	$("#"+tableId+" tr:last").find("input:first").focus();
	setCommandCodeAutocompleteData();
}
function splitData( val ) {
	return val.split( /[,;]\s*/ );
}

function extractLastItems( term ) {
	return splitData( term ).pop();
}
function setCommandCodeAutocompleteData(){
	$( '.commandCode' ).bind( "keydown", function( event ) {
		if ( event.keyCode === $.ui.keyCode.TAB && $( this ).autocomplete( "instance" ).menu.active ) {
			event.preventDefault();
		}
	}).autocomplete({
	minLength: 0,
	source: function( request, response ) {
		response( $.ui.autocomplete.filter(
			commandCodeList, extractLastItems( request.term ) ) );
		},
	focus: function() {
		return false;
	},
	select: function( event, ui ) {
			var val = this.value;
			var commaIndex = val.lastIndexOf(",") == -1 ? 0 : val.lastIndexOf(",");
			var semiColonIndex = val.lastIndexOf(";") == -1 ? 0 : val.lastIndexOf(";");
			 if(commaIndex == semiColonIndex) {
					val = "";
			}  else if(commaIndex > semiColonIndex) {
					val = val.substring(0,commaIndex+1); 
			} else if(semiColonIndex !=0 && semiColonIndex > commaIndex){
				val = val.substring(0,semiColonIndex+1); 
			}	 
			this.value = val + ui.item.value ;
			return false;
	}
	});
}

function setCommandCodeData(commandCodeObj){
	var commandCodevalue=$(commandCodeObj).val();
	commandCodevalue=commandCodevalue.trim();
	var lastChar = commandCodevalue.charAt(commandCodevalue.length - 1);
	if(lastChar == ","){
		var result = commandCodevalue.substring(0, commandCodevalue.length-1);
		$(commandCodeObj).val(result);
	}
}

function validateForm(){
	if(!isValidMappings()){
		return false;
	}else{
		document.forms[0].action.value="update";
		document.forms[0].submit();
	}
}

function isValidMappings(){
	var isValidMapping = true;
	$('.responseAttributeTable').find('.commandCode').each(function(){
		var nameValue = $.trim($(this).val());
		if(nameValue.length == 0) {
			alert("Command Code must be Specified");
			isValidMapping = false;
			$(this).focus();
			return false;
		}
	});
	return isValidMapping;
}

</script>

<html:form action="/updateEAPPolicyResponseAttributesDetails">
	<html:hidden name="updateEAPPolicyForm" styleId="action" property="action" value="update"/>
	<html:hidden name="updateEAPPolicyForm" styleId="policyId" property="policyId"/>
	<html:hidden name="updateEAPPolicyForm" styleId="auditUId" property="auditUId"/>
	<html:hidden name="updateEAPPolicyForm" styleId="name" property="name"/>
	<table width="100%">
		<tr>
			<td  class="tblheader-bold" valign="top" colspan="4">
				 <bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.update.responseattribute"/>
			</td>									
		</tr>
		<tr>
			<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
				<input type="button" value=" Add Mapping " class="light-btn" onclick="addResponseAttributesTable('responseAttributeTable','responseAttributesTemplate');" tabindex="25" />
			</td>
		</tr>
		<tr>
			<td align="left" class="captiontext" valign="top" nowrap="nowrap" colspan="4">
					<!-- Attributes Table -->
					<table width="60%" cellspacing="0" cellpadding="0" border="0" id="responseAttributeTable" class="responseAttributeTable">
						<tr>
							<td align="left" class="tblheader" valign="top" width="35%">
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.commandcode" />
								<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.commandcode" header="servicepolicy.naspolicy.commandcode"/>
							</td>
							<td align="left" class="tblheader" valign="top" width="60%">
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.responseattribute" />
								<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.responseattribute" header="servicepolicy.naspolicy.responseattribute"/>
							</td>
							<td align="left" class="tblheader" valign="top"width="5%">Remove</td>
						</tr>
						
						<%if(eapResponseAttributes != null){ %>
							<%for(EAPResponseAttributes eapResAttribute : eapResponseAttributes){ %>
								<tr>
									<td class="allborder" width="35%">
										<input  autocomplete="off" class="commandCode noborder" type="text" name="commandCode" maxlength="1000" size="28" value="<%=(eapResAttribute.getCommandCodes() == null)?"":eapResAttribute.getCommandCodes() %>" onkeyup="setCommandCode(this);" style="width:200px;" onblur="setCommandCodeData(this);"/></td>
									<td class="tblrows" width="60%">
										<textarea rows="1" class="responseAttributes noborder" name="responseAttributes"   id="responseAttributes"  style="min-width:100%;min-height:10px;height:18px;"><%=(eapResAttribute.getResponseAttributes()==null)?"":eapResAttribute.getResponseAttributes()%></textarea>
									</td>
									<td class="tblrows" align="center" width="5%"><img value="top" src="<%=request.getContextPath()%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  /></td>
								</tr>
						<%}} %>
					</table>
				</td>
		</tr>
		<tr>
			<td class="btns-td" valign="middle">&nbsp;</td>
			<td class="btns-td" valign="middle" colspan="2">
				<input type="button" name="c_btnCreate" onclick="validateForm()" id="c_btnCreate2" value="Update" class="light-btn">
				<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchEAPPolicy.do?'" value="Cancel" class="light-btn"> 
			</td>
		</tr>
</table>
</html:form>
<table style="display:none;" id="responseAttributesTemplate">
	<tr>
		<td class="allborder" width="35%">
			<input  autocomplete="off" class="commandCode noborder" type="text" name="commandCode" maxlength="1000" size="28"  style="width:200px;" onblur="setCommandCodeData(this);"/></td>
		<td class="tblrows" width="60%">
			<textarea rows="1" class="responseAttributes noborder" name="responseAttributes"  id="responseAttributes"  style="min-width:100%;min-height:10px;height:18px;"></textarea>
		</td>
		<td class="tblrows" align="center" width="5%"><img value="top" src="<%=request.getContextPath()%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  /></td>
	</tr>
</table> 
