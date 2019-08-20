function initilizeEsiData(esiDetails) {
    var checkBoxId = 'esiDataCheckBoxId';
    var totalesi = esiDetails.length;
    $('#addEsiTbl').find("tr:gt(0)").remove();
    var esiTbl = $('#addEsiTbl');

    var sel = document.getElementById('redundancyMode');
    var selectedRedValue = sel.options[sel.selectedIndex].value;

    if(selectedRedValue === "N+M"){

        for (var i = 0; i < totalesi; i++) {
            var rowid = 'row' + checkBoxId + esiDetails[i] + i;
            var rowVal = esiDetails[i];
            var elementid = checkBoxId + esiDetails[i] + i;
            var selectElementId = checkBoxId + esiDetails[i] + i + 'w';

            var row = "<tr id='"+ rowid +"' class='"+ rowVal +" labeltext'>" + "<td class='tblfirstcol' width='5%'><input type='checkbox' name='"+ checkBoxId +"' id='"+ elementid +"' value='"
                + esiDetails[i] + "'/></td>" + "<td class='tblrows'> "
                + esiDetails[i] + "</td>"
                + "<td class='tblrows' width='5%'><select id='"
                + selectElementId +"'>" + getWeightageList()
                + "</select></td></tr>";
            $(esiTbl).append(row);
        }

    }else if(selectedRedValue === "ACTIVE-PASSIVE"){

        $('#addEsiTbl').find('tr').find('th').remove();
        $('#activePassiveEsiTbl').find("tr:gt(0)").remove();

        for (var i = 0; i < totalesi; i++) {
            var rowid = 'row' + checkBoxId + esiDetails[i] + i;
            var rowVal = esiDetails[i];
            var elementid = checkBoxId + esiDetails[i] + i;
            var selectElementId = checkBoxId + esiDetails[i] + i + 'w';
            var row = "<tr id='"+ rowid +"' class='"+ rowVal +" labeltext'>" + "<td class='tblfirstcol' width='5%'><input type='radio'  name='"+ checkBoxId +"' id='"+ elementid +"' value='"
                + esiDetails[i] + "'/></td>" + "<td class='tblrows'> "
                + esiDetails[i] + "</td></tr>";
            $(esiTbl).append(row);
        }
    }
}

function getWeightageList() {
    var optionList = '';
    for (var count = 1; count <= 10; count++) {
        optionList += "<option>" + count + "</option>";
    }
    return optionList;
}

function esiPopup(callerObject,selectedBoxId) {

    if($('#esiType :selected').text() === '--Select--'){
        alert("ESI type must be specified");
        return
    }

    var id = '#esiPopup';
    var selectObjIds = $(callerObject).attr('id');
    var selectObj;
    if(selectedBoxId === "selectedPrimaryEsiIds"){
        selectObj  = $('#' + selectObjIds).closest('form').find('.selectedPrimaryEsiIds');
    }else if (selectedBoxId === "selectedSecondaryEsiIds"){
        selectObj  = $('#' + selectObjIds).closest('form').find('.selectedSecondaryEsiIds');
    } else if(selectedBoxId === "activeesi"){
        selectObj  = $('#' + selectObjIds).parent().parent().find('.activeesi');
    } else if(selectedBoxId === "passiveesi"){
        selectObj  = $('#' + selectObjIds).parent().parent().find('.passiveesi');
    }
    var selectObjAttrIds = $(selectObj).attr('id');

    $('#' + selectObjAttrIds + ' option').each(function() {
        var optionIds = $(this).attr('id');
        $("#addEsiTbl #row esiDataCheckBoxId" + optionIds).hide();
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
                width : 400,
                buttons : {
                    'Add' : function() {
                        var selectedItems = $("input:checked");
                        if (selectedItems.length >= 1) {
                            for (var i = 0; i < selectedItems.length; i++) {
                                if (selectedItems[i].checked == true && selectedItems[i].id != "stickySession" && selectedItems[i].id != "switchBack") {
                                    var checkBoxId = $(selectedItems[i]).attr('id');
                                    var optionsval = $("#" + checkBoxId + "w").val();
                                    var labelVal = $("#" + checkBoxId).val();
                                    if(selectedBoxId === "activeesi" || selectedBoxId === "passiveesi"){
                                        if(selectObj.val() == ''){
                                            $(selectObj).val(labelVal);
                                            $(selectObj).text(labelVal);
                                        }else{
                                            removeActivePassiceEsiData(selectObj);
                                            $(selectObj).val(labelVal);
                                            $(selectObj).text(labelVal);
                                        }
                                    }else {
                                        $(selectObj).append("<option id="+ checkBoxId+ " value="+ labelVal+ "-W-"+ optionsval+ " class='"+labelVal+" labeltext' > "+ labelVal+ "-W-"+ optionsval+ " </option>");
                                    }
                                    $("#addEsiTbl #row" + checkBoxId).hide();
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

function removeData(componentId,checkboxid,currentObj){

    var id = "#" + componentId +" option:selected";
    $(id).each(function(){
        var mainValue = $(this).attr('class');
        var rowid='#addEsiTbl .' +mainValue.split(' ')[0];
        $(rowid).show();
        $(this).remove();
    });
}

function removeActivePassiceEsiData(selObj) {
    var esiName = selObj.val();
    $('#addEsiTbl .' + esiName).show();
    $(this).remove();
}

function selectAllEsi() {
    $('.selectedPrimaryEsiIds').find("option").each(function() {
        $(this).attr('selected', 'selected');
    });

    $('.selectedSecondaryEsiIds').find("option").each(function() {
        $(this).attr('selected', 'selected');
    });
}

function populateConfiguredEsi(esiType,configuredEsi){
    var esiGroupComponents = $(esiType);
    var configureEsiLength = configuredEsi.length;
    for(var count=0;count<configureEsiLength;count++){
        var checkBoxId = 'esiDataCheckBoxId'+configuredEsi[count].esiID;
        var esiName = configuredEsi[count].esiName;
        $(esiGroupComponents).append("<option id="+ checkBoxId +" value="+ configuredEsi[count].esiName + "-W-" + configuredEsi[count].weightage +" class="+esiName+"> "+configuredEsi[count].esiName+"-W-" + configuredEsi[count].weightage +" </option>");
        $('#addEsiTbl .' + esiName).hide();
    }
}

function deleteRow(currentRow) {
    var tblRow = $(currentRow).parent().parent();

    var activeEsiName = "";
    var passiveEsiName = "";

    $(tblRow).find('td').each(function () {
        if($(this).find('input').attr('name') == 'activeesi'){
            activeEsiName = $(this).text();
            activeEsiName = activeEsiName.trim();
        }
        if($(this).find('input').attr('name') == 'passiveesi'){
            passiveEsiName = $(this).text();
            passiveEsiName = passiveEsiName.trim();
        }
    });
    $(currentRow).parent().parent().remove();

    $('#addEsiTbl .' + activeEsiName).show();
    $('#addEsiTbl .' + passiveEsiName).show();
}

var rowId =0;
function AddEsiRow(mainTableId,hiddenTableId) {
    var tableRowStr = $("#"+hiddenTableId).find("tr");
    $("#"+mainTableId).append("<tr>"+$(tableRowStr).html()+"</tr>");

    $('#' + mainTableId + ' tr:gt(0)').find('td').each(function() {
        if(($(this).find('input').attr('id') == 'activeesi') || ($(this).find('input').attr('id') == 'passiveesi')){
            $(this).find('input').attr('id','esi'+rowId);
            rowId++;
        }
        if($(this).find('span').attr('id') == 'activebtn' || $(this).find("span").attr('id') == 'passivebtn'){
            $(this).find('span').attr('id','btn'+rowId);
            rowId++;
        }
    });
}

function setActivePassiveEsiData() {

    var activePassiveEsiList = [];
    var activeEsiName;
    var passiveEsiName;
    var commonWeightage;
    $('#activePassiveEsiTbl tr:gt(0)').find('td').each(function () {

        if(($(this).find('input').attr('name') == 'activeesi')) {
            activeEsiName = $(this).text();
            activeEsiName = activeEsiName.trim();
        }

        if($(this).find('input').attr('name') == 'passiveesi'){
            passiveEsiName = $(this).text();
            passiveEsiName = passiveEsiName.trim();
        }

        if($(this).find('select').attr('name') == 'weightage'){
            var weightage = $(this).find('select').get(0);
            commonWeightage = weightage.options[weightage.selectedIndex].value;

            activePassiveEsiList.push({'activeEsiName':activeEsiName,'passiveEsiName':passiveEsiName,'loadFactor':commonWeightage});
        }
    });

    var esiArray = [];
    var item = {};

    item["activePassiveEsiList"] = activePassiveEsiList;
    esiArray.push(item);
    return esiArray;
}

function populateActivePassiveEsi(activePassiveEsiData){

    var configureEsiLength = activePassiveEsiData.length;
    for(var count=0;count<configureEsiLength;count++) {

        AddEsiRow('activePassiveEsiTbl','activePassiveTbl');
        var activeEsiName = activePassiveEsiData[count].activeEsiName;
        var passiveEsiName = activePassiveEsiData[count].passiveEsiName;
        var weightage = activePassiveEsiData[count].weightage;

        $('#activePassiveEsiTbl tr:gt('+count+')').find('td').each(function() {
            if($(this).find('input').attr('name') == 'activeesi'){
                $(this).find('input').text(activeEsiName);
                $(this).find('input').val(activeEsiName);
            }
            if($(this).find('input').attr('name') == 'passiveesi'){
                $(this).find('input').text(passiveEsiName);
                $(this).find('input').val(passiveEsiName);
            }
            if($(this).find('select').attr('name') == 'weightage'){
                $(this).find('select').val(weightage);
            }
        });
        $('#addEsiTbl .' + activeEsiName).hide();
        $('#addEsiTbl .' + passiveEsiName).hide();
    }
}

function validateActivePassiveEsi() {

    var isValid = true;
    var activeEsiName;
    var passiveEsiName;
    $('#activePassiveEsiTbl tr:gt(0)').find('td').each(function () {

        if(($(this).find('input').attr('name') == 'activeesi')) {
            activeEsiName = $(this).text();
            activeEsiName = activeEsiName.trim();
            if(activeEsiName == ""){
                isValid = false;
            }
        }

        if($(this).find('input').attr('name') == 'passiveesi'){
            passiveEsiName = $(this).text();
            passiveEsiName = passiveEsiName.trim();
            if(passiveEsiName == ""){
                if(!isValid){
                    alert("Active-Passive Esi Name must be specified");
                    return isValid;
                }else {
                    alert("Passive Esi Name of " + activeEsiName + " must be specified");
                    $(this).find('input').focus();
                    isValid = false;
                    return isValid;
                }
            }else if(!isValid){
                alert("Active Esi Name of " + passiveEsiName +" must be specified");
                return isValid;
            }
        }
    });
    return isValid;
}

function validateCheckbox(selectedValue) {
    if(selectedValue == 'ACCT'){
        $('#stickySession').removeAttr('disabled');
    }else{
        $("#stickySession").attr('checked','checked');
        $('#stickySession').attr('disabled',true);
    }
}

