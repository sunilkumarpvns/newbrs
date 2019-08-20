/**
 * Created by Ishani on 21/3/17.
 */
function selectAll(){
    var selectedServicesArray = [];
    var selectedDatas = document.getElementsByClassName("removePadding");
    var isSelectAllOperation = false;
    if ($("#selectAll").attr("class") == "glyphicon glyphicon-check") {
        $("#selectAll").attr("class", "glyphicon glyphicon-unchecked");
    } else {
        isSelectAllOperation = true;
        $("#selectAll").attr("class", "glyphicon glyphicon-check");
    }
    for (var i = 0; i < selectedDatas.length; i++) {
        if (isSelectAllOperation) {
            selectedDatas[i].checked = true;
            selectedServicesArray.push(selectedDatas[i].value);
        } else {
            selectedDatas[i].checked = false;
            selectedServicesArray.pop();
        }
    }
    $("#selectedServiceTypes").val(selectedServicesArray.join());
    return isSelectAllOperation;

}
function getSelectedValuesForServiceType(){
    var selectedServicesArray = [];
    var selectedDatas = document.getElementsByClassName("removePadding");
    var isSelectAllOperation = false;
    for (var i = 0; i < selectedDatas.length; i++) {
        if(selectedDatas[i].checked == true) {
            selectedServicesArray.push(selectedDatas[i].value);
            isSelectAllOperation = true;
        }
    }
    $("#selectedServiceTypes").val(selectedServicesArray.join());
    return isSelectAllOperation;
}

function getSelectedValuesForServiceTypeUpdate(){
    var getSelectedServices = $("#selectedServiceTypes").val();
    var selectedServiceForUpdate = getSelectedServices.split(",");
    var selectedServicesArray = [];
    var selectedDatas = document.getElementsByClassName("removePadding");
    var isSelectAllOperation = false;
    var selectAllCnt = 0;
    for (var i = 0; i < selectedDatas.length; i++) {
        if(selectedServiceForUpdate.indexOf(selectedDatas[i].value) > -1){
            selectedDatas[i].checked = true;
            selectedServicesArray.push(selectedDatas[i].value);
            isSelectAllOperation = true;
            selectAllCnt = selectAllCnt + 1;
        }

    }
    if(selectedDatas.length == selectAllCnt){
        $("#selectAll").attr("class", "glyphicon glyphicon-check");
    }
    $("#selectedServiceTypes").val(selectedServicesArray.join());
    return isSelectAllOperation;
}

function fillRatingGroup(id, preferredGroups, otherGroups, selectedGroup){
    clearSelectTag(id);

    //Get prefered list from map and add all option in respective optgroup
    var preferredList = [];
    var preferedGroup = $("#"+id).children().eq(0); //first optgroup in select tag - preferedGroup
    var otherGroup = $("#"+id).children().eq(1);    //second optgroup in select tag - otherGroup
    for (var i = 0; i < preferredGroups.length; i++) {
        var ratingGroup = preferredGroups[i];
        preferredList.push(ratingGroup.id);
        preferedGroup.append(new Option(ratingGroup.name + ' (' + ratingGroup.identifier + ')', ratingGroup.id));
    }

    //Add all staff-belonging rating group in 'Other Charging Keys' optgroup, excluding preferred charging keys
    var otherList = [];
    for (var i = 0; i < otherGroups.length; i++) {
        var ratingGroup = otherGroups[i];
        if (preferredList.includes(ratingGroup.id) == false) {
            otherList.push(ratingGroup.id);
            otherGroup.append(new Option(ratingGroup.name + ' (' + ratingGroup.identifier + ')', ratingGroup.id));
        }
    }

    //Add previously selected charging key in case of update.
    if (selectedGroup != null) {
        if(preferredList.includes(selectedGroup.id) == false && otherList.includes(selectedGroup.id) == false){
            otherGroup.append(new Option(selectedGroup.name + ' (' + selectedGroup.identifier + ')', selectedGroup.id));
        }
        $("#"+id).find('option[value=' + selectedGroup.id + ']').attr("selected", "true");
    }
}


function clearSelectTag(id) {
    var elSel = $("#"+id)[0];
    for (var i = elSel.length - 1; i>=0; i--) {
        elSel.remove(i);
    }
}