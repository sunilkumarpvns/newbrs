<%@page import="com.elitecore.commons.base.Collectionz"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.spr.ddf.DDFDataManager"%>
<%@page import="com.elitecore.netvertexsm.web.servermgr.spr.form.DDFTableDataForm"%>
<%@page import="com.elitecore.corenetvertex.spr.data.SubscriberRepositoryData"%>
<%@page import="com.elitecore.corenetvertex.spr.ddf.DDFEntryData"%>
<%@ page import="java.util.*" %>
<%@ page import="java.util.Map.Entry" %>

<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>

<%
		DDFTableDataForm ddfTableDataForm = (DDFTableDataForm)request.getAttribute("ddfTableDataForm");
%>
<script type="text/javascript">
var isValidName;
function validate() {
	var defaultSPRId = $("#defaultSPRId").val();
	if(defaultSPRId == 0){
		alert("Please Select Default SPR");
		return false;
	}
}

function openPopup() {	
	$.fx.speeds._default = 1000;
	document.getElementById("popupNewDDFEntry").style.visibility = "visible";		
	$( "#popupNewDDFEntry" ).dialog({
		modal: true,
		autoOpen: false,		
		height: 150,
		width: 500,		
		buttons:{					
            'Add': function() {
					
            	var newSprId = $('#sprId option:selected').val();
            	var newSprName = $('#sprId option:selected').text();
				var newIdentityPattern = $('#idPattern').val(); 
					if(isNull(newIdentityPattern) || newIdentityPattern==''){
		     			alert('Identity Pattern must be specified.');
		     		} else if (isNull(newSprId) || newSprId==0){
		     			alert('SPR must be specified.');
		     		} else {	
		     			$("#mappingtbl tr:last").after("<tr><td align='center' valign='top' width='15%' class='tblfirstcol'>" + newIdentityPattern 
		     					+ "<input type='hidden' name = 'identityPatterns' value='" + newIdentityPattern + "'/></td>" 
		     					+ "<td align='center' valign='top' width='15%' class='tblrows'>" + newSprName 
		     					+ "<input type='hidden' name = 'sprIds' value='" + newSprId + "'/></td>" 
		     					+ "<td align='center' class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' onclick='$(this).parent().parent().remove();'/></td></tr>");			         				          	
		          		$(this).dialog('close');         				    		         			   				         			          				          		
		         	}	
					$("#update").focus();
            },                
		    Cancel: function() {
            	$(this).dialog('close');
            	$("#update").focus();
        	}
        },
        open: function() {
    		$('#idPattern').val(""); 
    		$('#sprId ').val("0"); 
    	},
    	close: function() {
    		$("#update").focus();
    	}				
	});
	$( "#popupNewDDFEntry" ).dialog("open");
}	
setTitle('<bean:message bundle="ddfResources" key="ddf.title"/>');
	
</script>
<html:form action="/ddfTableData.do?method=update" onsubmit="return validate();"> 
<html:hidden name="ddfTableDataForm" property="id"/>

 		<table cellSpacing="0" cellPadding="0" width="97%" border="0" >
	 	  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="97%" id="c_tblCrossProductList" align="right" border="0" >
						<tr>
							<td class="tblheader-bold" colspan="2">
								<bean:message key="general.update.basicdetails"/>
							</td>
		  				</tr> 
						<tr>
							<td align="left" class="labeltext" valign="top" width="30%">
							<bean:message bundle="ddfResources" key="ddf.defaultSPR" />
							</td>
							<td align="left" class="labeltext" valign="top" width="32%">
								 <html:select name="ddfTableDataForm" styleId="defaultSPRId" property="defaultSprId" size="1" style="width: 206;" tabindex="1">
									<html:option value="0" bundle="ddfResources" key="ddf.select"/>
									<logic:iterate id="subscriberRepositoryData"  name="ddfTableDataForm" property="subscriberRepositoryDatas" 
										type="SubscriberRepositoryData">
										<html:option value="<%=String.valueOf(subscriberRepositoryData.getId())%>" >
											<bean:write name="subscriberRepositoryData" property="name"/>
										</html:option>
									</logic:iterate>
								</html:select><font color="#FF0000"> *</font>
							</td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" >
							<bean:message bundle="ddfResources" key="ddf.stripPrefixes" />
							<td align="left" class="labeltext" valign="top">
								<html:text name="ddfTableDataForm" styleId="stripPrefixes" property="stripPrefixes" size="24" maxlength="100" tabindex="2"/>
							</td>
						</tr>
			   </table>
			</td>
		</tr>

	   <!--  display added DB Field Mapping list -->
		            
		  <tr> 
			<td colspan="3"> 
				<table width="97%" id="c_tblCrossProductList" align="right" border="0" >
			 		 <tr> 
          				<td class="tblheader-bold" colspan="2">
          					<bean:message bundle="ddfResources" key="ddf.table.title"/>
          				</td>
         			 </tr>		
          			<tr> 
						<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  			</tr> 		  			
		  			<tr>				 
						<td >				
							<input type="button" id="button" value="   Add   " class="light-btn"  onclick="openPopup();" tabindex="3" />
						</td>
					</tr>
			   		<tr> 
						<td class="small-gap" colspan="3"> 
							&nbsp;
						</td> 
					</tr> 
					<tr> 
						<td colspan="3">
						<table width="70%" id="mappingtbl" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center" class="tblheader" valign="top" width="15%">
									<bean:message bundle="ddfResources" key="ddf.table.identityPatternHeading" /></td>
								<td align="center" class="tblheader" valign="top" width="15%">
									<bean:message bundle="ddfResources" key="ddf.table.sprHeading" /></td>
								<td align="center" class="tblheader" valign="top" width="4%">Remove</td>
							</tr>
							
							<%
								if(Collectionz.isNullOrEmpty(ddfTableDataForm.getDdfEntries()) == false) {
							%>
							<logic:iterate name="ddfTableDataForm"  scope="request" id="ddfIdentity" property="ddfEntries" type="com.elitecore.corenetvertex.spr.ddf.DDFEntryData">
								<tr>
								<td align="center" class="tblfirstcol" valign="top" width="15%">
									<bean:write name="ddfIdentity" property="identityPattern"/>
									<input type='hidden' name='identityPatterns' value='<bean:write name="ddfIdentity" property="identityPattern"/>' />
																
								</td>
								<td align="center" class="tblrows" valign="top" width="15%">
									<bean:write name="ddfIdentity" property="subscriberRepositoryData.name"/>
									<input type='hidden' name='sprIds' value='<bean:write name="ddfIdentity" property="subscriberRepositoryData.id"/>' />
								</td>
								<td class="tblrows" align="center" valign="middle">
									<img src="<%=basePath%>/images/minus.jpg" class="delete" height="14" onclick="$(this).parent().parent().remove();" tabindex="4"/>
								</td>
								</tr>
							</logic:iterate>
							<%} %>
						</table>
						</td> 
					</tr> 				
				</table> 			
			</td> 
		</tr>

          <tr> 
	        <td class="btns-td" valign="middle" >&nbsp;</td> 
            <td class="btns-td" valign="middle"  > 
		        <input type="submit"  id="update" value="   Update   "  class="light-btn" tabindex="5">
		        <input type="button" align="left" value=" Cancel " class="light-btn" onclick="javascript:location.href='<%=basePath%>/ddfTableData.do?method=view'" tabindex="6"/>                  
	        </td> 
   		  </tr> 
		</table> 
		
	<div id="popupNewDDFEntry" title="New DDF Entry" style="display: none;">	
	<table>
			<tr>
				<td align="left" class="labeltext" valign="top">
					<bean:message bundle="ddfResources" key="ddf.table.identityPatternHeading" />
				</td>
				<td align="left" class="labeltext" valign="top" width="32%">
					<input id="idPattern" size="24" maxlength="255" tabindex="7" />
				</td>
			</tr>
			<tr>
				<td align="left" class="labeltext" valign="top" width="20%">
					<bean:message bundle="ddfResources" key="ddf.table.sprHeading" />
				</td>

				<td align="left" class="labeltext" valign="top" width="30%">
					 <html:select name="ddfTableDataForm" styleId="sprId" property="id" size="1" style="width: 206;" tabindex="8">
						<html:option value="0" bundle="ddfResources" key="ddf.select"/>
							<logic:iterate id="subscriberRepositoryData"  name="ddfTableDataForm" property="subscriberRepositoryDatas" 
								type="SubscriberRepositoryData">
								<html:option value="<%=String.valueOf(subscriberRepositoryData.getId())%>" >
									<bean:write name="subscriberRepositoryData" property="name"/>
								</html:option>
							</logic:iterate>
					</html:select>
				</td>
			</tr>
		</table>								
</div>
</html:form>



