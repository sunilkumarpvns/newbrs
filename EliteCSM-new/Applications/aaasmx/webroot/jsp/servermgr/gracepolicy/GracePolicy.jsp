<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="java.util.List"%>

<%
    List<GracepolicyData> gracePolicyList = (List<GracepolicyData>)request.getAttribute("gracePolicyList");
%>

<script language="javascript">
	var verifyNameBy = 'create';
	var verifyNameById = 0;
	var isValidName=false;
    function parseGracePolicyValue(){

        return true;
    	
    } 

    function  checkAll(){
    	
     	if( document.forms[0].toggleAll.checked == true) {
     		var selectVars = document.getElementsByName('select');
    	 	for (i = 0; i < selectVars.length;i++)
    			selectVars[i].checked = true ;
        } else if (document.forms[0].toggleAll.checked == false){
     		var selectVars = document.getElementsByName('select');	    
    		for (i = 0; i < selectVars.length; i++)
    			selectVars[i].checked = false ;
    	}
    }
     
	function popupCreate() {	
		$.fx.speeds._default = 1000;
		document.getElementById("popupContact").style.visibility = "visible";		
		$( "#popupContact" ).dialog({
			modal: true,
			autoOpen: false,		
			height: "auto",
			width: 500,		
			buttons:{					
                'Add': function() {
                	
				            var gracePolicyName=$("#gracePolicyName").val();
				            var gracePolicyValue=$("#gracePolicyValue").val();
            
                             if(isNull(gracePolicyName)){
                                 alert('Please Enter Grace Policy Name');
                             }else if(!isValidName){
                            	 alert('Please Enter Valid Grace Policy Name');
                        	 }else if(isNull(gracePolicyValue)){
                            	 alert('Please Enter Grace Policy Value');
                             }else if(!isCommaSeperatedNumString(gracePolicyValue)){
                               	alert('Grace Policy value must be comma seperated numeric value.');
                              }else{    
			                   document.gracePolicyForm.gracePolicyName.value=gracePolicyName;
			                   document.gracePolicyForm.gracePolicyValue.value=gracePolicyValue;
			                   document.gracePolicyForm.action.value = "Add";
			                   $("#gracePolicyName").val('');
	                           $("#gracePolicyValue").val('');	
			                   document.gracePolicyForm.submit();
			                   $(this).dialog('close');
                              }
			                   
                },                
    		    Cancel: function() {
                	$("#gracePolicyName").val('');
                    $("#gracePolicyValue").val('');
                	$(this).dialog('close');
            	}
	        },
        	open: function() {
        		verifyNameBy='create';
        		$("#gracePolicyName").val('');
                $("#gracePolicyValue").val('');
                $("#verifyNameDiv").html('');
        	},
        	close: function() {
        		
        	}				
		});
		$( "#popupContact" ).dialog("open");
		
	}	

 function removeData(){

	    var selectVar = false;
	    
	    for (i=0; i < document.forms[0].elements.length; i++){
	        if(document.forms[0].elements[i].name.substr(0,6) == 'select'){
	            if(document.forms[0].elements[i].checked == true)
	                selectVar = true;
	        }
	    }
	    if(selectVar == false){
	        alert('At least select one Grace Policy for remove process');
	    }else{
	        var msg;
	        msg = '<bean:message bundle="alertMessageResources" key="alert.searchgracepolicy.delete.query"/>';        
	        var agree = confirm(msg);

	        if(agree){
	            document.gracePolicyForm.action.value ='delete';
	        	document.gracePolicyForm.submit();
	        }
	    }
	}

  function update(gracePolicyId,gracePolicyName,gracePolicyValue){

	  $.fx.speeds._default = 1000;
		document.getElementById("popupContact").style.visibility = "visible";
		//set value
		$("#gracePolicyName").val(gracePolicyName);
		$("#gracePolicyValue").val(gracePolicyValue);
				
		$( "#popupContact" ).dialog({
			modal: true,
			autoOpen: false,		
			height: "auto",
			width: 500,		
			buttons:{					
              'Update': function() {

		  
          var gracePolicyName=$("#gracePolicyName").val();
          var gracePolicyValue=$("#gracePolicyValue").val();
          
        
                           if(isNull(gracePolicyName)){
                               alert('Please Enter Grace Policy Name');
                               
                           }else if(!isValidName){
                          	 alert('Please Enter Valid Grace Policy Name');
                      	  }else if(isNull(gracePolicyValue)){
                          	 alert('Please Enter Grace Policy Value');
                           }else if(!isCommaSeperatedNumString(gracePolicyValue)){
                          	alert('Grace Policy value must be comma seperated numeric value.');
                            }else{    
			                   document.gracePolicyForm.gracePolicyName.value=gracePolicyName;
			                   document.gracePolicyForm.gracePolicyValue.value=gracePolicyValue;
			                   document.gracePolicyForm.gracePolicyId.value=gracePolicyId;
			                   document.gracePolicyForm.action.value = "update";	
			                   $("#gracePolicyName").val('');
	                           $("#gracePolicyValue").val('');
			                   document.gracePolicyForm.submit();
			                   $(this).dialog('close');
                            }
			                   
           		
              },                
  		    Cancel: function() {
              	$(this).dialog('close');
          	}
	        },
      	open: function() {
    		verifyNameBy='update';
    		verifyNameById=gracePolicyId;
    		verifyName(verifyNameBy,verifyNameById);
      	},
      	close: function() {
      		
      	}				
		});
		$( "#popupContact" ).dialog("open");
	  
	  
		  
  }
  function verifyName(createOrUpdate,id) {
		var searchName = document.getElementById("gracePolicyName").value;
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.GRACE_POLICY%>',searchName,createOrUpdate,id,'verifyNameDiv');
	}
  
  setTitle('<bean:message bundle="servermgrResources" key="servermgr.gracepolicy"/>');
</script>



<html:form action="/gracePolicy" styleId="gracePolicy">

	<html:hidden name="gracePolicyForm" property="action" />
	<html:hidden name="gracePolicyForm" property="gracePolicyName" />
	<html:hidden name="gracePolicyForm" property="gracePolicyValue" />
	<html:hidden name="gracePolicyForm" property="gracePolicyId" />
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td>
										<table cellSpacing="0" cellPadding="0" width="99%" border="0">
											<!-- Grace policy List List Header-->
											<tr>
												<td class="btns-td" valign="middle" colspan="5">
													<input type="button" tabindex="1" name="Create" value="   Create   " class="light-btn" onclick="popupCreate()"> 
													<input type="button" tabindex="2" name="Delete" OnClick="removeData();" value="   Delete   " class="light-btn">
												</td>

											</tr>

											<tr>
												<td align="center" class="tblheader" valign="top" width="1%">
													<input type="checkbox" name="toggleAll" value="checkbox"
													onclick="checkAll()" />
												</td>
												<td align="center" class="tblheader" valign="top"
													width="40px">Sr. No.</td>
												<td align="left" class="tblheader" valign="top" width="*">Grace
													Policy Name</td>
												<td align="left" class="tblheader" valign="top" width="40%">Grace
													Policy Value</td>
												<td align="center" class="tblheader" valign="top"
													width="40px">Edit</td>

											</tr>
											<!-- Grace policy List List-->
											<tr>
												<%if(gracePolicyList != null && gracePolicyList.size() >0){ %>
												<%int index = 0; %>
												<logic:iterate id="obj" name="gracePolicyList"
													type="GracepolicyData">
													<tr>
														<td align="center" class="tblfirstcol"><input
															type="checkbox" name="select"
															value="<bean:write name="obj" property="gracePolicyId"/>" />
														</td>
														<td align="center" class="tblfirstcol"><%=(index+1)%></td>
														<td align="left" class="tblrows"><bean:write
																name="obj" property="name" /></td>
														<td align="left" class="tblrows"><bean:write
																name="obj" property="value" /></td>
														<td align="center" class="tblrows"><a href="#"> <img
																src="<%=basePath%>/images/edit.jpg" alt="Edit"
																border="0"
																onclick="update('<bean:write name="obj" property="gracePolicyId"/>','<bean:write name="obj" property="name"/>','<bean:write name="obj" property="value"/>');">
														</a></td>
													</tr>
													<% index++; %>
												</logic:iterate>
												<%}else{ %>
											
											<tr>
												<td align="center" class="tblfirstcol" colspan="8">No
													Records Found.</td>
											</tr>
											<%	}%>
											</tr>
											<tr>
												<td class="btns-td" valign="middle" colspan="5"><input
													type="button" name="Create" value="   Create   "
													class="light-btn" onclick="popupCreate()"> <input
													type="button" name="Delete" OnClick="removeData();"
													value="   Delete   " class="light-btn"></td>
											</tr>

										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>

	<!-- Pop Up window Design -->
	<div id="popupContact" style="display: none;"
		title="Add Grace Policy Detail">
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td align="left" class="labeltext" valign="top" width="25%">
					<bean:message bundle="servermgrResources" key="servermgr.gracepolicy.name" /> 
					<ec:elitehelp headerBundle="servermgrResources" 
					text="gracepolicy.name" header="servermgr.gracepolicy.name"/>	
				</td>

				<td align="left" class="labeltext" valign="top" width="75%"><html:text
						name="gracePolicyForm" styleId="gracePolicyName"
						property="gracePolicyName"
						onkeyup="verifyName(verifyNameBy,verifyNameById)" size="20"
						maxlength="50" style="width:96%" /><font color="#FF0000">
						*</font>
					<div id="verifyNameDiv" class="labeltext"></div></td>
			</tr>
			<tr>
				<td align="left" class="labeltext" valign="top" width="25%">
					<bean:message bundle="servermgrResources" key="servermgr.gracepolicy.value" />
					<ec:elitehelp headerBundle="servermgrResources" 
					text="gracepolicy.value" header="servermgr.gracepolicy.value"/>				
				</td>
				<td align="left" class="labeltext" valign="top" width="75%"><html:text
						name="gracePolicyForm" styleId="gracePolicyValue"
						property="gracePolicyValue" size="20" maxlength="50"
						style="width:96%" /><font color="#FF0000"> *</font></td>
			</tr>
		</table>
	</div>
</html:form>




