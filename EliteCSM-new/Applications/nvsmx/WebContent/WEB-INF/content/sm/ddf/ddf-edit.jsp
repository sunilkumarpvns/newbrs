<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 14/9/17
  Time: 8:04 PM
  To change this template use File | Settings | File Templates.
--%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <s:text name="ddf.update" />
        </h3>
    </div>
    <div class="panel-body" >
        <s:form namespace="/sm/ddf" action="ddf" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
            <s:hidden name="_method" value="put" />
            <s:token/>
            <div class="row">
                <div class="col-sm-10 col-lg-8">
                    <s:hidden name="id" value="%{id}" />
                    <s:select name="defaultSprDataId" key="ddf.default.spr" cssClass="form-control focusElement" list="sprDataList" listKey="getId()" listValue="getName()" headerValue="SELECT" headerKey="" />
                    <s:textfield name="stripPrefixes" cssClass="form-control" key="ddf.strip.prefixes" maxlength="100" />
                </div>
            </div>
            <div id="ddfEntriesDiv" class="col-xs-12">
                <table id='ddfEntriesTable'  class="table table-blue table-bordered">
                    <caption class="caption-header"><s:text name="ddf.entries" />
                        <div align="right" class="display-btn">
                            <span class="btn btn-group btn-group-xs defaultBtn" onclick="addDdfEntries();" id="addRow"> <span class="glyphicon glyphicon-plus"></span></span>
                        </div>
                    </caption>
                    <thead>
                    <th><s:text name="ddf.identity.pattern"/></th>
                    <th><s:text name="ddf.spr"/></th>
                    <th style="width:35px;">&nbsp;</th>
                    </thead>
                    <tbody>
                    <s:iterator value="ddfSprRelDatas" status="i" var="ddfSprRelData">
                        <tr name='ddfEntriesRow'>
                            <td><s:textfield value="%{#ddfSprRelData.identityPattern}"	name="ddfSprRelDatas[%{#i.count - 1}].identityPattern" cssClass="form-control"  elementCssClass="col-xs-12" /></td>
                            <td><s:select list="sprDataList" listValue="getName()" listKey="getId()" value="%{#ddfSprRelData.sprData.id}"	name="ddfSprRelDatas[%{#i.count - 1}].sprDataId" cssClass="form-control"  elementCssClass="col-xs-12" headerValue="SELECT" headerKey="" ></s:select></td>
                            <td style="width:35px;"><span class="btn defaultBtn" onclick="$(this).parent().parent().remove();"><a><span class="delete glyphicon glyphicon-trash" title="delete"></span></a></span></td>
                        </tr>
                    </s:iterator>
                    </tbody>
                </table>
            </div>

            <div class="row">
                <div class="col-xs-12" align="center">

                    <button type="submit" class="btn btn-primary btn-sm"  role="submit" formaction="${pageContext.request.contextPath}/sm/ddf/ddf/${id}" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/ddf/ddf/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.back"/></button>
                </div>
            </div>
        </s:form>
    </div>
</div>
<table id="tempDdfEntries" style="display: none;">
    <tr>
        <td><s:textfield cssClass="form-control" elementCssClass="col-xs-12" maxlength="255"/></td>
        <td><s:select list="sprDataList" listValue="getName()" listKey="getId()"  cssClass="form-control"  elementCssClass="col-xs-12" headerValue="SELECT" headerKey=""></s:select></td>
        <td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>
    </tr>
</table>
<script type="text/javascript">
    var i = document.getElementsByName("ddfEntriesRow").length;
    if(isNullOrEmpty(i)) {
        i = 0;
    }
    function addDdfEntries() {
        $("#ddfEntriesTable tbody").append("<tr>" + $("#tempDdfEntries").find("tr").html() + "</tr>");

        $("#ddfEntriesTable").find("tr:last td:nth-child(1)").find("input").focus();
        var NAME = "name";
        $("#ddfEntriesTable").find("tr:last td:nth-child(1)").find("input").attr(NAME,'ddfSprRelDatas['+i+'].identityPattern');
        $("#ddfEntriesTable").find("tr:last td:nth-child(2)").find("select").attr(NAME,'ddfSprRelDatas['+i+'].sprDataId');
        i++;
    }

    function validateForm(){
        var ddfEntriesTableBodyLength = $("#ddfEntriesTable tbody tr").length;
        if(ddfEntriesTableBodyLength >= 1){
            //This will check that entry should not be null or empty
            var isValidateNullEntry = true;
            $("#ddfEntriesTable tbody tr").each(function () {
                var inputElement =$(this).children().first().find('input');
                var selectElement =$(this).children(':first-child').next().find('select');
                if(isNullOrEmpty(inputElement.val())){
                    setErrorOnElement(inputElement,"Identity Pattern can not be empty");
                    isValidateNullEntry = false;
                } else if (isNullOrEmpty(selectElement.val())) {
                    setErrorOnElement(selectElement,"SPR can not be empty");
                    isValidateNullEntry = false;
                }
            });

            return isValidateNullEntry;

        }
    }

</script>

