<%@ taglib uri="/struts-tags/ec" prefix="s" %>

<script type="text/javascript">

    function removeRecords(targetElement, formId) {
        var selectVar = isAnyEntitySelected(formId);
        console.log(selectVar);
        if (selectVar.resultCode == false) {
            return addWarning(".popup", '<s:text name="no.entity.selected.to.delete"/>');
        } else if(selectVar.resultCode == "WARNING"){
            return addWarning(".popup", selectVar.resultMessage);
        }else {
            return deleteConfirmMsg(targetElement, '<s:text name="delete.selected.confirmation"/>');
        }
    }

    function isAnyEntitySelected(formId) {
        var selectedData = false;
        var checkBoxes;
        if (isNullOrEmpty(formId)) {
            checkBoxes = $("input[name='ids']");
        } else {
            checkBoxes = $("form[id='" + formId + "']").find("input[name='ids']");
        }
        $(checkBoxes).each(function () {
            if ($(this).prop("checked")) {
                selectedData = true;
                return false;
            }
        });
        return {"resultCode":selectedData};
    }

    function removeData(formId, deleteUrl) {
        document.getElementById(formId).action = deleteUrl;
        document.getElementById(formId).submit();
    }

</script>