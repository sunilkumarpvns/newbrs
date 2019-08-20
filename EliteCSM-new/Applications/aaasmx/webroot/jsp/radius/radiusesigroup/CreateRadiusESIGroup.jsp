<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.web.radius.radiusesigroup.RadiusEsiType"%>
<%@ page import="com.elitecore.elitesm.web.radius.radiusesigroup.RedundancyMode"%>
<%@ page import="com.elitecore.elitesm.web.radius.radiusesigroup.form.RadiusESIGroupForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%
	String basePath = request.getContextPath();
	RadiusESIGroupForm radiusESIGroupForm = (RadiusESIGroupForm)request.getAttribute("radiusESIGroupForm");
%>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<script type="text/javascript" src="<%=basePath%>/js/radius/radius-esigroup.js"></script>

<script type="text/javascript">


    var isValidName;
    var previousSelectedVal;

    function verifyName() {
        var searchName = document.getElementById("esiGroupName").value;
        isValidName = verifyInstanceName('<%=InstanceTypeConstants.RADIUS_ESI_GROUP%>',searchName,'create','','verifyNameDiv');
    }
    $( document ).ready(function() {

        $("#stickySession").attr('checked','checked');

        var selectedVal = $("#esiType");
        selectedVal.data("prev",selectedVal.val());

        selectedVal.change(function(data){
            previousSelectedVal = $(this);
            previousSelectedVal.data("prev",previousSelectedVal.val());
        });

        var selectedRedundacyMode = document.getElementById('redundancyMode');
        changeRedundancyMode(selectedRedundacyMode);

        $("#esiGroupName").focus();
        verifyName();
    });

    setTitle('<bean:message bundle="radiusResources" key="radiusesigroup.title"/>');

    function configureEsiData(sel) {
        var selectedValue = sel.options[sel.selectedIndex].value;

        validateCheckbox(selectedValue);

        if(selectedValue == '<%= RadiusEsiType.AUTH.name %>'){
            var esiDetails = [];
            <% for(String esiData : radiusESIGroupForm.getAuthTypeEsiDataList()) { %>
            esiDetails.push('<%=esiData%>');
            <% } %>
            var selectBox =document.getElementById('selectedPrimaryEsi');

            if(selectBox.options.length == 0){
                initilizeEsiData(esiDetails);
            } else if(previousSelectedVal.data("prev") != '0'){
                $("#selectedPrimaryEsi").empty();
                $("#selectedSecondaryEsi").empty();
                initilizeEsiData(esiDetails);
            }

        }else if(selectedValue == '<%= RadiusEsiType.ACCT.name %>'){
            var esiDetails = [];
            <% for(String esiData : radiusESIGroupForm.getAcctTypeEsiDataList()) { %>
            esiDetails.push('<%=esiData%>');
            <% } %>

            var selectBox =document.getElementById('selectedPrimaryEsi');

            if(selectBox.options.length == 0){
                initilizeEsiData(esiDetails);
            } else if(previousSelectedVal.data("prev") != '0'){
                $("#selectedPrimaryEsi").empty();
                $("#selectedSecondaryEsi").empty();
                initilizeEsiData(esiDetails);
            }
        }else if(selectedValue == '<%= RadiusEsiType.CHARGING_GATEWAY.name %>'){
            var esiDetails = [];
            <% for(String esiData : radiusESIGroupForm.getChargingGatewayEsiDataList()) { %>
            esiDetails.push('<%=esiData%>');
            <% } %>

            var selectBox =document.getElementById('selectedPrimaryEsi');

            if(selectBox.options.length == 0){
                initilizeEsiData(esiDetails);
            } else if(previousSelectedVal.data("prev") != '0'){
                $("#selectedPrimaryEsi").empty();
                $("#selectedSecondaryEsi").empty();
                initilizeEsiData(esiDetails);
            }
        }else if(selectedValue == '<%= RadiusEsiType.CORRELATED_RADIUS.name %>'){
            var esiDetails = [];
            <% for(String esiData : radiusESIGroupForm.getCorrelatedRadiusEsiDataList()) { %>
            esiDetails.push('<%=esiData%>');
            <% } %>

            var selectBox =document.getElementById('selectedPrimaryEsi');

            if(selectBox.options.length == 0){
                initilizeEsiData(esiDetails);
            } else if(previousSelectedVal.data("prev") != '0'){
                $("#selectedPrimaryEsi").empty();
                $("#selectedSecondaryEsi").empty();
                initilizeEsiData(esiDetails);
            }
        }
    }

    function validateForm(){

        if(isEmpty($('#esiGroupName').val())){
            alert('Name must be specified');
            $('#esiGroupName').focus();
            return;
        }else if(!isValidName) {
            alert('Enter Valid esi Name');
            $('#esiGroupName').focus();
            return;
        }else if($("#redundancyMode option:selected" ).val() == "0") {
            alert("Redundancy Mode must be specified");
            $('#redundancyMode').focus();
            return;
        }else if($("#esiType option:selected" ).val() == "0") {
            alert("ESI Type must be specified");
            $('#esiType').focus();
            return;
        }else if($("#switchBack").attr('checked') && ($("#stickySession").attr('checked') == false)){
            alert("SwitchBack can not be enable without StickySession");
            return;
        }else if("<%=RedundancyMode.NM.redundancyModeName%>" === $("#redundancyMode option:selected" ).val() && document.getElementById('selectedPrimaryEsi').options.length == 0) {
            alert("Primary ESI must be specified");
            $('#selectedPrimaryEsi').focus();
            return;
        }else if("<%=RedundancyMode.ACTIVE_PASSIVE.redundancyModeName%>" === $("#redundancyMode option:selected" ).val() && !validateActivePassiveEsi()){
			return;
		}else if("<%=RedundancyMode.ACTIVE_PASSIVE.redundancyModeName%>" === $("#redundancyMode option:selected" ).val() && $('#activePassiveEsiTbl tr:gt(0)').length == 0){
            alert("Active-Passive Esi must be specified");
            return;
        } else{
            if("<%=RedundancyMode.NM.redundancyModeName%>" === $("#redundancyMode option:selected").val()){
                selectAllEsi();
            }else if("<%=RedundancyMode.ACTIVE_PASSIVE.redundancyModeName%>" === $("#redundancyMode option:selected").val()){
                var jsonData = setActivePassiveEsiData();
                $('#activePassiveEsiDataList').val(JSON.stringify(jsonData));
            }
            document.forms[0].action.value="create";
            document.forms[0].submit();
        }
    }

    function changeRedundancyMode(sel) {
        var selectedValue = sel.options[sel.selectedIndex].value;

        if(selectedValue == '<%= RedundancyMode.NM.redundancyModeName %>'){
			$("#c_tblCreateRadiusESIGroup tr.esidropdownbox").hide();
			$("#c_tblCreateRadiusESIGroup tr.esibtn").hide();

            $("#c_tblCreateRadiusESIGroup tr.nplusmesi").show();
            $("#c_tblCreateRadiusESIGroup tr.nplusmesilable").show();

            $('#activePassiveEsiTbl').find("tr:gt(0)").remove();

            var selectedEsiType = document.getElementById('esiType');
            configureEsiData(selectedEsiType);

        } else if(selectedValue == '<%= RedundancyMode.ACTIVE_PASSIVE.redundancyModeName %>'){
			$("#c_tblCreateRadiusESIGroup tr.nplusmesi").hide();
            $("#c_tblCreateRadiusESIGroup tr.nplusmesilable").hide();

            $("#c_tblCreateRadiusESIGroup tr.esidropdownbox").show();
            $("#c_tblCreateRadiusESIGroup tr.esibtn").show();

            $("#selectedPrimaryEsi").empty();
            $("#selectedSecondaryEsi").empty();

            var selectedEsiType = document.getElementById('esiType');
            configureEsiData(selectedEsiType);
		}else{
            $("#c_tblCreateRadiusESIGroup tr.esidropdownbox").hide();
            $("#c_tblCreateRadiusESIGroup tr.esibtn").hide();

            $("#c_tblCreateRadiusESIGroup tr.nplusmesi").hide();
            $("#c_tblCreateRadiusESIGroup tr.nplusmesilable").hide();
		}
    }

</script>
<html:form action="/createRadiusESIGroup">
	<html:hidden name="radiusESIGroupForm" styleId="action" property="action" value="create" />
	<html:hidden property="activePassiveEsiJson" styleId="activePassiveEsiDataList" ></html:hidden>
 	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header">
										<bean:message bundle="radiusResources" key="radiusesigroup.create" />
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
								</tr>

								<tr>
									<td colspan="3" class="btns-td">
										<table width="70%" name="c_tblCreateRadiusESIGroup" id="c_tblCreateRadiusESIGroup" align="left" cellSpacing="0" cellPadding="0" border="0">
											<tr>
												<td align="left" class="labeltext" valign="top" width="25%">
													<bean:message bundle="radiusResources" key="radiusesigroup.name"/>
													<ec:elitehelp headerBundle="radiusResources" text="radiusesigroup.name" header="radiusesigroup.name"/>
												</td>
												<td align="left" class="labeltext" valign="top" nowrap="nowrap">
													<html:text name="radiusESIGroupForm" tabindex="1" styleId="esiGroupName" property="esiGroupName" size="30" onblur="verifyName();"  style="width:250px" />
													<font color="#FF0000"> *</font>
													<div id="verifyNameDiv" class="labeltext"></div></td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top" width="25%">
													<bean:message bundle="radiusResources" key="radiusesigroup.description"/>
													<ec:elitehelp headerBundle="radiusResources" text="radiusesigroup.description" header="radiusesigroup.description"/>
												</td>
												<td align="left" class="labeltext" valign="top" nowrap="nowrap">
													<html:textarea property="description" styleId="description" name="radiusESIGroupForm" rows="2" cols="32.5" tabindex="3"></html:textarea>
												</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top" width="25%">
													<bean:message bundle="radiusResources" key="radiusesigroup.redundancymode" />
													<ec:elitehelp headerBundle="radiusResources" text="radiusesigroup.redundancymode" header="radiusesigroup.redundancymode"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:select property="redundancyMode" styleId="redundancyMode" tabindex="10" style="width:250px" onchange="changeRedundancyMode(this);">
														<html:option value="0">--Select--</html:option>
														<logic:iterate id="radRedMod" collection="<%=RedundancyMode.values() %>" >
															<html:option value="<%=String.valueOf(((RedundancyMode)radRedMod).redundancyModeName)%>"><%=((RedundancyMode)radRedMod).redundancyModeName%></html:option>
														</logic:iterate>
													</html:select>
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top" width="25%">
													<bean:message bundle="radiusResources" key="radiusesigroup.esitype" />
													<ec:elitehelp headerBundle="radiusResources" text="radiusesigroup.esitype" header="radiusesigroup.esitype"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:select property="esiType" styleId="esiType" tabindex="10" style="width:250px" onchange="configureEsiData(this);">
														<html:option value="0">--Select--</html:option>
														<logic:iterate id="radEsiType" collection="<%=RadiusEsiType.values() %>" >
															<html:option value="<%=String.valueOf(((RadiusEsiType)radEsiType).name)%>"><%=((RadiusEsiType)radEsiType).name%></html:option>
														</logic:iterate>
													</html:select>
													<font color="#FF0000"> *</font>
												</td>
											</tr>

											<tr>
												<td align="left" class="labeltext" width="25%">
												</td>
												<td class="labeltext">
													<html:checkbox property="stickySession" styleId="stickySession" disabled="true">
													</html:checkbox>
													<bean:message bundle="radiusResources" key="radiusesigroup.stickysession" />
													<ec:elitehelp headerBundle="radiusResources" text="radiusesigroup.stickysession" header="radiusesigroup.stickysession"/>

													<html:checkbox property="switchBack" styleId="switchBack">
													</html:checkbox>
													<bean:message bundle="radiusResources" key="radiusesigroup.switchback" />
													<ec:elitehelp headerBundle="radiusResources" text="radiusesigroup.switchback" header="radiusesigroup.switchback"/>
												</td>
											</tr>
											<tr>
												<td>&nbsp;</td>
											</tr>
											<tr class="nplusmesilable">
												<td align="left" class="labeltext" width="25%">
												</td>
												<td class="labeltext">
													<bean:message name="testIt" bundle="radiusResources" key="radiusesigroup.primaryesi"/>
													<ec:elitehelp headerBundle="radiusResources" text="radiusesigroup.primaryesi" header="radiusesigroup.primaryesi"/>
													<font color="#FF0000"> *</font>
												</td>
												<td class="labeltext">
													<bean:message bundle="radiusResources" key="radiusesigroup.secondaryesi"/>
													<ec:elitehelp headerBundle="radiusResources" text="radiusesigroup.secondaryesi" header="radiusesigroup.secondaryesi"/>
												</td>
											</tr>

											<tr style="margin-top: 5px" class="nplusmesi">
												<td align="left" class="labeltext" width="25%">
													<bean:message bundle="radiusResources" key="radiusesigroup.esi"/>
													<ec:elitehelp headerBundle="radiusResources" text="radiusesigroup.esi" header="radiusesigroup.esi"/>
												</td>
												<td class="labeltext" valign="top" nowrap="nowrap">
													<table width="auto" cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td align="middle" valign="top">
																<select class="labeltext select-box-style selectedPrimaryEsiIds" name="selectedPrimaryEsi" id="selectedPrimaryEsi" multiple="multiple" style="width: 200px;"></select>
															</td>
															<td align="middle" class="labeltext" valign="top">
																<table cellspacing="0" cellpadding="0" border="0" width="15%">
																	<tr>
																		<td>
																			<input type="button" id="addPrimaryEsiBtn" value="Add" onClick="esiPopup(this,'selectedPrimaryEsiIds');" class="light-btn esi-primary-popup" style="width: 75px" tabindex="2" /><br />
																		</td>
																	</tr>
																	<tr>
																		<td style="padding-top: 5px;">
																			<input type="button" id="removePrimaryEsiBtn"  value="Remove" onclick="removeData('selectedPrimaryEsi','esiDataCheckBoxId',this);" class="light-btn" style="width: 75px" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
													</table>
												</td>
												<td class="labeltext" valign="top" nowrap="nowrap">
													<table width="auto" cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td align="middle" valign="top">
																<select class="labeltext select-box-style selectedSecondaryEsiIds" name="selectedSecondaryEsi" id="selectedSecondaryEsi" multiple="multiple" style="width: 200px;"></select>
															</td>
															<td align="middle" class="labeltext" valign="top">
																<table cellspacing="0" cellpadding="0" border="0" width="15%">
																	<tr>
																		<td>
																			<input type="button" id="addSecondaryEsiBtn" value="Add" onClick="esiPopup(this,'selectedSecondaryEsiIds');" class="light-btn peer-popup" style="width: 75px" tabindex="2" /><br />
																		</td>
																	</tr>
																	<tr>
																		<td style="padding-top: 5px;">
																			<input type="button" id="removeSecondaryEsiBtn"  value="Remove" onclick="removeData('selectedSecondaryEsi','esiDataCheckBoxId',this);" class="light-btn" style="width: 75px" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
													</table>
												</td>
											</tr>

											<tr>
												<td>&nbsp;</td>
											</tr>
											<tr class="esibtn">
												<td align="left" class="labeltext" width="25%">
												</td>
												<td>
													<input type="button" class="light-btn" id="configureEsi" value="Add ESI" onclick="AddEsiRow('activePassiveEsiTbl','activePassiveTbl')">
												</td>
											</tr>

											<tr class="esidropdownbox">
												<td align="left" class="labeltext" width="15%">
													<bean:message bundle="radiusResources" key="radiusesigroup.esi"/>
													<ec:elitehelp headerBundle="radiusResources" text="radiusesigroup.esi" header="radiusesigroup.esi"/>
												</td>
												<td width="*" align="left" colspan="2" style="padding-top: 5px;">
													<table id="activePassiveEsiTbl" cellpadding="0" cellspacing="0" border="0" width="80%">
														<tr>
															<td class="tblheader" width="20%" align="middle">
																<bean:message bundle="radiusResources" key="radiusesigroup.activeesi" />
															</td>
															<td class="tblheader" width="5%" align="middle">

															</td>
															<td class="tblheader" width="20%" align="middle">
																<bean:message bundle="radiusResources" key="radiusesigroup.passiveesi" />
															</td>
															<td class="tblheader" width="5%" align="middle">

															</td>
															<td class="tblheader" width="10%" align="middle">
																<bean:message bundle="radiusResources" key="radiusesigroup.esiweightage" />
															</td>
															<td class="tblheader" width="3%" align="middle">
																<bean:message bundle="radiusResources" key="radiusesigroup.remove" />
															</td>
														</tr>

													</table>
												</td>
											</tr>


											<tr>
												<tr>
													<td>&nbsp;</td>
												</tr>
												<tr>
													<td class="btns-td" valign="middle">&nbsp;</td>
													<td class="btns-td" valign="middle" align="middle">
														<input type="button" class="light-btn" value="  Create  " onclick="validateForm();" tabindex="5"/>
														<input type="button" name="c_btnCancelPeer" tabindex="6" onclick="javascript:location.href='searchRadiusESIGroup.do'" value="  Cancel  " class="light-btn" />
													</td>
												</tr>
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

	<div id="esiPopup"  title="Add ESI" style="display: none">
		<table id="addEsiTbl" class="addEsiTblName box" cellpadding="0" cellspacing="0" width="100%">
			<tr>
				<th class="tblheader-bold"></th>
				<th class="tblheader-bold"><bean:message bundle="radiusResources" key="radiusesigroup.esi.name"/></th>
				<th class="tblheader-bold"><bean:message bundle="radiusResources" key="radiusesigroup.weightage"/></th>
			</tr>
		</table>
	</div>

	<table id="activePassiveTbl" class="activePassiveTbl" cellpadding="0" cellspacing="0" width="100%" style="display: none">
			<tr>
			<td class="tblfirstcol" width="20%">
				<input type="text" id="activeesi" class="activeesi" name="activeesi" readonly="readonly" style="width: 100%"/>
			</td>
			<td class="tblrows" width="5%">
				<span id="activebtn" class='add-proxy-server activebtn' onclick="esiPopup(this,'activeesi');" style="margin-left: 25%">&nbsp;</span>
			</td>
			<td class="tblrows" width="20%">
				<input type="text" id="passiveesi" class="passiveesi" name="passiveesi" readonly="readonly" style="width: 100%"/>
			</td>
			<td class="tblrows" width="5%">
				<span id="passivebtn" class='add-proxy-server passivebtn' onclick="esiPopup(this,'passiveesi');" style="margin-left: 25%">&nbsp;</span>
			</td>
			<td class="tblrows" width="10%" align="middle">
				<select  id="weightage" name="weightage" style="width: 100%">
					<option value="1" selected="selected">1</option>
					<option value="2">2</option>
					<option value="3">3</option>
					<option value="4">4</option>
					<option value="5">5</option>
					<option value="6">6</option>
					<option value="7">7</option>
					<option value="8">8</option>
					<option value="9">9</option>
					<option value="10">10</option>
				</select>
			</td>
			<td class="tblrows" width="3%" align="middle">
				<span class='remove-proxy-server' onclick="deleteRow(this)"/>&nbsp;
			</td>
		</tr>
	</table>
</html:form>




