/**
 * This js file contains the common code for diameter peer group
 * Author   : Chirag I. Prajapati
 * 
 */

/**
 * This is used to intialize content for popup(list of peers to be displayed while popup open firsttime)
 */
function initializeData(peerDetails, checkbxId) {
	var totalPeers = peerDetails.length;
	var peerTbl = $('#addPeersTbl');
	for (var i = 0; i < totalPeers; i++) {
		var rowid = 'row' + checkbxId + peerDetails[i].peerID;
		var elementid = checkbxId + peerDetails[i].peerID;
		var selectElementId = checkbxId + peerDetails[i].peerID + 'w';
		var rows = "<tr id='"
				+ rowid
				+ "'>"
				+ "<td class='tblfirstcol' width='5%'><input type='checkbox' name='"
				+ checkbxId + "' id='" + elementid + "' value='"
				+ peerDetails[i].peerName + "'/></td>" + "<td class='tblrows'>"
				+ peerDetails[i].peerName + "</td>"
				+ "<td class='tblrows' width='5%'><select id='"
				+ selectElementId + "'>" + getWeightageList()
				+ "</select></td></tr>";
		$(peerTbl).append(rows);
	}
}

function getWeightageList() {
	var optionList = '';
	for (var count = 1; count <= 10; count++) {
		optionList += "<option>" + count + "</option>";
	}
	return optionList;
}

/**
 * This is used to validate peer group
 * @returns true or false based on peer is configured in peer-group or not
 */
function validatePeerGroup() {
	var peerList = $('#selectedPeers option');
	if (peerList.length >= 1) {
		return true;
	} else {
		return false;
	}
}

/**
 *This is used to select all peers in peer-group before submit form 
 */
function selectAllPeers() {
	$('.selectedPeersIds').find("option").each(function() {
		$(this).attr('selected', 'selected');
	});
}

/**
 * This is used to open popup
 * @param callerObject component which is calling this method
 */
function peerPopup(callerObject) {
	var id = '#peerPopup';

	var selectObjIds = $(callerObject).attr('id');
	var selectObj = $('#' + selectObjIds).closest('form').find('.selectedPeersIds');
	var selectObjAttrIds = $(selectObj).attr('id');

	$('#' + selectObjAttrIds + ' option').each(function() {
		var optionIds = $(this).attr('id');
		$("#addPeersTbl #row peerDataCheckBoxId" + optionIds).hide();
	});

	$(id).show();
	$(id)
			.dialog(
					{
						modal : true,
						autoOpen : false,
						minHeight : 200,
						height : 'auto',
						position : 'top',
						width : 450,
						buttons : {
							'Add' : function() {
								var selectedItems = $("input:checked");
								if (selectedItems.length >= 1) {
									for (var i = 0; i < selectedItems.length; i++) {
										if (selectedItems[i].checked == true) {
											var checkBoxId = $(selectedItems[i]).attr('id');
											var optionsval = $("#" + checkBoxId + "w").val();
											var labelVal = $("#" + checkBoxId).val();
											$(selectObj).append("<option id="+ checkBoxId+ " value="+ labelVal+ "-W-"+ optionsval+ " class=labeltext> "+ labelVal+ "-W-"+ optionsval+ " </option>");
											$("#addPeersTbl #row" + checkBoxId).hide();
											selectedItems[i].checked = false;
										}
									}
								}
								$(this).dialog('close');
							},
							Cancel : function() {
								$(this).dialog('close');
							}
						},
						open : function() {
						},
						close : function() {
						}
					});
	$(id).dialog("open");
}	

/**
* This is used to remove peers from peer-group 
 */
function removeData(componentId,checkboxid,currentObj){
	var id = "#" + componentId +" option:selected";	
	
	$(id).each(function(){
		 var mainValue = $(this).attr('id');
		 var strippedVal = mainValue;
		 var rowid="#addPeersTbl #row"+strippedVal;
		 $(rowid).show();
		 $(this).remove();	
	});
}

/* This function will validate all form data */
function validateForm(){
		
	var transactionTimeout =$('#transactionTimeout').val()
	if(isEmpty($('#peerGroupName').val())){
			alert('Name must be specified');
			return;
		}else if(!isValidName) {
			alert('Peer Group Name must be valid');
			$('#peerGroupName').focus();
			return;
		}else if(isEmpty(transactionTimeout)) {
			alert('Transaction Timeout must be specified');
			$('#transactionTimeout').focus();
			return;
		}else if(!(isNumber(transactionTimeout))) {
			alert('Transaction Timeout must be numeric');
			$('#transactionTimeout').focus();
			return;
		}else if(!validatePeerGroup()){
			alert('At least one peer with weightage must be specified in Peer Group');
			return;
		}else{
			selectAllPeers();
			document.forms[0].submit();
		}	
}

/**
 * This is used to populate in peer group when already peer is configured
 */
function populateConfiguredPeers(){
	var peerGroupComponents = $(".selectedPeersIds");
	var configurePeersLength = configuredPeers.length;
	for(var count=0;count<configurePeersLength;count++){
		var checkBoxId = 'peerDataCheckBoxId'+configuredPeers[count].peerID;
		$(peerGroupComponents).append("<option id="+ checkBoxId +" value="+ configuredPeers[count].peerName + "-W-" + configuredPeers[count].weightage +" class=labeltext> "+configuredPeers[count].peerName+"-W-" + configuredPeers[count].weightage +" </option>");
		$("#addPeersTbl #row"+checkBoxId).hide();
	}
}