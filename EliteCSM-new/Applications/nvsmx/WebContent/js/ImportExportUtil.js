
function getSelectedValues(){
    var selectedData = false;
    var selectedDatas = document.getElementsByName("ids");
    var selectedIndexes = new Array();
    for (var i=0; i < selectedDatas.length; i++){
        if(selectedDatas[i].name == 'ids'){
            if(selectedDatas[i].checked == true){
                selectedData = true;
                selectedIndexes.push(i);
            }
        }
    }
    $("#selectedIndexes").val(selectedIndexes);
    return selectedData;
}

function importPackage(entityName) {
    var entity = "entity";
    if(isNullOrEmpty(entityName) == false ){
        entity = entityName;
    }
    var selectVar = getSelectedValues();
    if(selectVar == false){
        return addWarning(".popup","At least select one "+ entity +" for import");
    }else{
        importData();

    }
}
function importData(){
    $("#importEntityAction").modal('show');
}

function setValuesForIdAndName(){
    var selectedDatas = document.getElementsByName("ids");
    var selectedNames = document.getElementsByName("names");
    for (var i=0; i < selectedDatas.length; i++){

        if(selectedDatas[i].name == 'ids'){
            if(selectedDatas[i].checked == true) {
                if(selectedDatas[i].value != 'undefined' && selectedDatas[i].value.length == 0){
                    selectedDatas[i].value = "null";
                }
                selectedNames[i].value = selectedDatas[i].value + "|" + selectedNames[i].value;
            }
        }
    }
}
function showProgress(){
    var pleaseWait = $('#pleaseWaitDialog');
    $(pleaseWait).modal({backdrop: 'static', keyboard: false}).show();
}

function setUserAction(obj){
    $('#userAction').val($(obj).text());
    setValuesForIdAndName();
    showProgress();
     var form =$(obj.form);
     $(form).attr('action',"importData");
     $(form).submit();
}


function validateImportFile(uploadFileId){
    if($("#"+uploadFileId).val().length == 0){
        addWarning(".popup","At least configure file for Import");
        return false;
    };
    return true;
}
