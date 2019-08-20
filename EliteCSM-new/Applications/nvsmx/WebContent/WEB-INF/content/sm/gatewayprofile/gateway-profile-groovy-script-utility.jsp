<%@ taglib uri="/struts-tags/ec" prefix="s" %>

<script type="text/javascript">
    var NAME = "name"
    var groovyScriptIndex = $("#groovyScriptTable tbody tr").length;
    function addGroovyScript() {
        var groovyScript = "groovyScriptDatas["+groovyScriptIndex+"]";
        $("#groovyScriptTable tbody").append("<tr>" + $("#groovyScriptConfigTable").find("tr").html() + "</tr>");
        $("#groovyScriptTable").find("tr:last td:nth-child(1)").find("input").attr(NAME, groovyScript + '.scriptName');
        $("#groovyScriptTable").find("tr:last td:nth-child(2)").find("input").attr(NAME, groovyScript + '.argument');
        groovyScriptIndex++;
    }

    function validateGroovyScriptDatas(){
        var groovyScripts = $("#groovyScriptTable tbody tr").length;
        var isValidateNullEntry = true;
        if (groovyScripts >= 1) {
            //This will check that entry should not be null or empty
            $("#groovyScriptTable tbody tr").each(function () {
                var scriptNameElement = $(this).children().first().find('input');
                if (isNullOrEmpty(scriptNameElement.val())) {
                    setErrorOnElement(scriptNameElement, "Groovy Script Name can not be empty");
                    isValidateNullEntry = false;
                }
            });
        }
        return isValidateNullEntry;
    }
</script>

<table id="groovyScriptConfigTable" class="table table-blue table-bordered" style="display: none;">
    <tr>
        <td><s:textfield cssClass="form-control scriptname" elementCssClass="col-xs-12" maxlength="2048"/></td>
        <td>
            <s:textfield  cssClass="form-control argument" elementCssClass="col-xs-12" maxlength="2048"  />
        </td>
        <td style='width:35px;'>
            <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
        </span>
        </td>
    </tr>
</table>
