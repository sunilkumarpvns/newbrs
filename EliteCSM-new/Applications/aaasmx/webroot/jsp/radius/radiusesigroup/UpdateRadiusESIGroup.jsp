<%@page import="com.elitecore.commons.base.Collectionz"%>
<%@page import="com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.ActivePassiveCommunicatorData"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.CommunicatorData"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.web.radius.radiusesigroup.RadiusEsiType"%>
<%@ page import="com.elitecore.elitesm.web.radius.radiusesigroup.RedundancyMode"%>
<%@ page import="com.elitecore.elitesm.web.radius.radiusesigroup.form.RadiusESIGroupForm" %>
<%
	RadiusESIGroupForm radiusESIGroupForm = (RadiusESIGroupForm)request.getAttribute("radiusESIGroupForm");
	String esiTypeValue = radiusESIGroupForm.getEsiType();
	String redModValue =  radiusESIGroupForm.getRedundancyMode();
	Boolean stickyMessage = radiusESIGroupForm.isStickySession();
%>
<script type="text/javascript" src="<%=basePath%>/js/radius/radius-esigroup.js"></script>
<script type="text/javascript">
    var isValidName;
    var configuredPrimaryEsi = [];
    var configuredSecondaryEsi = [];
    var activePassiveEsi = [];

    function verifyName() {
        var searchName = document.getElementById("esiGroupName").value;
        isValidName = verifyInstanceName('<%=InstanceTypeConstants.RADIUS_ESI_GROUP%>',searchName,'update','<%=radiusESIGroupForm.getEsiGroupId()%>','verifyNameDiv');
    }

    $( document ).ready(function() {

        <%if(stickyMessage && RadiusEsiType.ACCT.name.equalsIgnoreCase(esiTypeValue)){%>
        	$("#stickySession").attr('checked','checked');
		<%} else if(!RadiusEsiType.ACCT.name.equalsIgnoreCase(esiTypeValue)){%>
        	$("#stickySession").attr('checked','checked');
        <%}%>

        var selectedRedMod = document.getElementById('redundancyMode');

        <% if(RedundancyMode.NM.redundancyModeName.equalsIgnoreCase(redModValue)){%>
        selectedRedMod.selectedIndex = 0;
        <% } else if(RedundancyMode.ACTIVE_PASSIVE.redundancyModeName.equalsIgnoreCase(redModValue)){%>
        selectedRedMod.selectedIndex = 1;
        <%}%>

        changeRedundancyMode(selectedRedMod);

        var selectedEsiType = document.getElementById('esiType');

        <% if(RadiusEsiType.AUTH.name.equalsIgnoreCase(esiTypeValue)){%>
        selectedEsiType.selectedIndex = 0;
        <% } else if(RadiusEsiType.ACCT.name.equalsIgnoreCase(esiTypeValue)) {%>
        selectedEsiType.selectedIndex = 1;
        <%} else if(RadiusEsiType.CHARGING_GATEWAY.name.equalsIgnoreCase(esiTypeValue)) {%>
        selectedEsiType.selectedIndex = 2;
        <%} else if(RadiusEsiType.CORRELATED_RADIUS.name.equalsIgnoreCase(esiTypeValue)) {%>
        selectedEsiType.selectedIndex = 3;
        <%}%>

        configureEsiData(selectedEsiType);

        var esiIdSequence = 0;
        <% if(Collectionz.isNullOrEmpty(radiusESIGroupForm.getPrimaryEsiValues()) == false){%>
            <% for(CommunicatorData esiDeta : radiusESIGroupForm.getPrimaryEsiValues()) { %>
            var esiDetail = {
                esiName: '<%=esiDeta.getName() %>',
                esiID: '<%=esiDeta.getName() %>',
                weightage:'<%=esiDeta.getLoadFactor()%>'
            }
            esiDetail.esiID = esiDetail.esiID + esiIdSequence;
            esiIdSequence++;
            configuredPrimaryEsi.push(esiDetail);
            <% } %>

            <% for(CommunicatorData esiDeta : radiusESIGroupForm.getSecondaryEsiValues()) { %>
            	var esiDetail = {
                	esiName: '<%=esiDeta.getName() %>',
                	esiID: '<%=esiDeta.getName() %>',
                	weightage:'<%=esiDeta.getLoadFactor()%>'
            	}
            	esiDetail.esiID = esiDetail.esiID + esiIdSequence;
            	esiIdSequence++;
            	configuredSecondaryEsi.push(esiDetail);
            <% } %>

        	populateConfiguredEsi(".selectedPrimaryEsiIds",configuredPrimaryEsi);
        	populateConfiguredEsi(".selectedSecondaryEsiIds",configuredSecondaryEsi);
        <%} else if(Collectionz.isNullOrEmpty(radiusESIGroupForm.getActivePassiveEsiList()) == false){%>
        <% for(ActivePassiveCommunicatorData esiDeta : radiusESIGroupForm.getActivePassiveEsiList()) { %>
        	var esiDetail = {
            	activeEsiName: '<%=esiDeta.getActiveEsiName() %>',
            	passiveEsiName: '<%=esiDeta.getPassiveEsiName() %>',
            	weightage:'<%=esiDeta.getLoadFactor()%>'
        	}
        		activePassiveEsi.push(esiDetail);
		<% } %>
        	populateActivePassiveEsi(activePassiveEsi);
		<%}%>

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
            } else {
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
            } else {
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
            } else {
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
            } else {
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
            document.forms[0].action.value="update";
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
        }
    }

</script>
<html:form action="/updateRadiusESIGroup">

	<!-- Required Hidden Parameters -->
	<html:hidden name="radiusESIGroupForm" styleId="esiGroupId" property="esiGroupId" />
	<html:hidden name="radiusESIGroupForm" styleId="auditUId" property="auditUId" />
	<html:hidden name="radiusESIGroupForm" styleId="action" property="action"  value="update" />
	<html:hidden property="activePassiveEsiJson" styleId="activePassiveEsiDataList" name="radiusESIGroupForm"></html:hidden>
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td cellpadding="0" cellspacing="0" border="0" width="100%">
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td class="tblheader-bold">
							<bean:message bundle="radiusResources" key="radiusesigroup.update" />
						</td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
					</tr>

					<tr>
						<td colspan="3" class="btns-td">
							<table width="82%" name="c_tblCreateRadiusESIGroup" id="c_tblCreateRadiusESIGroup" align="left" cellSpacing="0" cellPadding="0" border="0">
								<tr>
									<td align="left" class="labeltext" valign="top" width="25%">
										<bean:message bundle="radiusResources" key="radiusesigroup.name" />
										<ec:elitehelp headerBundle="radiusResources"  text="radiusesigroup.esiname" header="radiusesigroup.esiname"/>
									</td>
									<td align="left" class="labeltext" valign="top" nowrap="nowrap">
										<html:text name="radiusESIGroupForm" tabindex="1" styleId="esiGroupName" property="esiGroupName" size="30"  onblur="verifyName();" maxlength="256" style="width:250px" />
										<font color="#FF0000">*</font>
										<div id="verifyNameDiv" class="labeltext"></div>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="25%">
										<bean:message bundle="radiusResources" key="radiusesigroup.description"/>
										<ec:elitehelp headerBundle="radiusResources" text="radiusesigroup.description" header="radiusesigroup.description"/>
									</td>
									<td align="left" class="labeltext" valign="top" nowrap="nowrap">
										<html:textarea property="description" styleId="description" name="radiusESIGroupForm" rows="2" cols="40" tabindex="3"></html:textarea>
									</td>
								</tr>

								<tr>
									<td align="left" class="labeltext" valign="top" width="25%">
										<bean:message bundle="radiusResources" key="radiusesigroup.redundancymode" />
										<ec:elitehelp headerBundle="radiusResources" text="radiusesigroup.redundancymode" header="radiusesigroup.redundancymode"/>
									</td>
									<td align="left" class="labeltext" valign="top">
										<html:select property="redundancyMode" name="radiusESIGroupForm" styleId="redundancyMode" tabindex="10" style="width:250px" onchange="changeRedundancyMode(this);">
											<logic:iterate id="radRedMod" collection="<%=RedundancyMode.values() %>">
												<%if(radiusESIGroupForm.getRedundancyMode().equalsIgnoreCase(((RedundancyMode)radRedMod).redundancyModeName)) {%>
												<option selected="selected" value="<%=((RedundancyMode)radRedMod).redundancyModeName%>"><%=((RedundancyMode)radRedMod).redundancyModeName%></option>
												<%} else {%>
												<option value="<%=((RedundancyMode)radRedMod).redundancyModeName%>"><%=((RedundancyMode)radRedMod).redundancyModeName%></option>
												<%}%>
											</logic:iterate>
										</html:select>
									</td>
								</tr>

								<tr>
									<td align="left" class="labeltext" valign="top" width="25%">
										<bean:message bundle="radiusResources" key="radiusesigroup.esitype" />
										<ec:elitehelp headerBundle="radiusResources" text="radiusesigroup.esitype" header="radiusesigroup.esitype"/>
									</td>
									<td align="left" class="labeltext" valign="top">
										<html:select property="esiType" name="radiusESIGroupForm" styleId="esiType" tabindex="10" style="width:250px" onchange="configureEsiData(this);">
											<logic:iterate id="radEsiType" collection="<%=RadiusEsiType.values() %>" >
												<%if(radiusESIGroupForm.getEsiType().equalsIgnoreCase(((RadiusEsiType)radEsiType).name)) {%>
												<option selected="selected" value="<%=((RadiusEsiType)radEsiType).name%>"><%=((RadiusEsiType)radEsiType).name%></option>
												<%} else {%>
												<option value="<%=((RadiusEsiType)radEsiType).name%>"><%=((RadiusEsiType)radEsiType).name%></option>
												<%}%>
											</logic:iterate>
										</html:select>
									</td>
								</tr>

								<tr>
									<td align="left" class="labeltext" width="25%">
									</td>
									<td class="labeltext">
										<html:checkbox property="stickySession" name="radiusESIGroupForm" styleId="stickySession" disabled="true">
										</html:checkbox>
										<bean:message bundle="radiusResources" key="radiusesigroup.stickysession" />
										<ec:elitehelp headerBundle="radiusResources" text="radiusesigroup.stickysession" header="radiusesigroup.stickysession"/>

										<html:checkbox property="switchBack" name="radiusESIGroupForm" styleId="switchBack">
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
									<td colspan="2">&nbsp;</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle">&nbsp;</td>
									<td class="btns-td" valign="middle" colspan="2">
										<input type="button" class="light-btn" value="  Update  " onclick="validateForm();" tabindex="5" />
										<input type="button" name="c_btnCancelBtn" tabindex="6" onclick="javascript:location.href='searchRadiusESIGroup.do'" value="  Cancel  " class="light-btn" />
									</td>
								</tr>
								<tr>
									<td colspan="2">&nbsp;</td>
								</tr>
							</table>
						</td>
					</tr>
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
			<td class="tblrows" align="middle" width="10%">
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