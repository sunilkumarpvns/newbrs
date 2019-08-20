<%@taglib uri="/struts-tags/ec" prefix="s" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<style>
    #add-alternate-ids {
        float: right;
        padding: 10px 20px;
        font-size: 15px;
    }
    .red {
        color: red;
    }

    .green {
        color: green;
    }
</style>

<script type="text/javascript">
    function setStatus(data, type, thisBean) {
        var statusString;
        if (data == 'ACTIVE') {
            statusString = '<span class="green">' + data + '</span>';
        } else {
            statusString = '<span class="red">' + data + '</span>';
        }
        return statusString + '<input type="hidden" value="' + data + '" name="providedStatus" />';

    }
    function updateAlternateIdentity(data, type, thisBean) {
        var notificationFunction = "javascript:updateAlternateIdentityDialog('" + thisBean.key + "','" + thisBean.value + "');";
        return "<a style='cursor:pointer' href=" + notificationFunction + "><span class='glyphicon glyphicon-pencil'></span></a>";
    }
</script>

<s:form  id="alternateIdListForm" method="post" enctype="multipart/form-data" cssClass="form-vertical">
<div class="row">
    <div id="add-alternate-ids">
        <s:if test="%{subscriber.status.equals(@com.elitecore.corenetvertex.constants.SubscriberStatus@DELETED.name())
                          || subscriber.status.equals(@com.elitecore.corenetvertex.constants.SubscriberStatus@INACTIVE.name())
                          || subscriber.getProfileExpiredHours() >= 0}">
            <button type="button" class="btn btn-primary btn-sm" role="button" id="btnAddAlternateId"
                    data-toggle="modal"
                    data-target="#notPermittedForDelete">
                <span class="glyphicon glyphicon-plus-sign" title="Add"></span> <s:text name="Add"/>
            </button>

            <button type="button" class="btn btn-primary btn-sm" role="button" id="btnAddAlternateId"
                    data-toggle="modal"
                    data-target="#notPermittedForDelete">
                <s:text name="button.active"/>
            </button>
            <button type="button" class="btn btn-primary btn-sm" role="button" id="btnAddAlternateId"
                    data-toggle="modal"
                    data-target="#notPermittedForDelete">
                <s:text name="button.inactive"/>
            </button>
            <button type="button" class="btn btn-primary btn-sm" role="button" id="btnAddAlternateId"
                    data-toggle="modal"
                    data-target="#notPermittedForDelete">
                <span class="glyphicon glyphicon-trash" title="Delete"></span>&nbsp;<s:text name="button.delete"/>
            </button>
        </s:if>
        <s:else>
            <button type="button" class="btn btn-primary btn-sm" role="button" id="btnAddAlternateId" onclick="openAddAlternateIdDialog();">
                <span class="glyphicon glyphicon-plus-sign" title="Add"></span> <s:text name="Add"/>
            </button>
            <button type="button" class="btn btn-primary btn-sm" role="button" id="btnActiveAlternateId"
                    data-toggle='confirmation-singleton' onclick='performAction(this,"changeStatus","ACTIVE");'
                    data-href="javascript:submitFormWithAction('changeStatus','ACTIVE');">
                <s:text name="button.active"/>
            </button>
            <button type="button" class="btn btn-primary btn-sm" role="button" role="button" id="btnInActiveAlternateId"
                    data-toggle='confirmation-singleton' onclick='performAction(this,"changeStatus","INACTIVE");'
                    data-href="javascript:submitFormWithAction('changeStatus','INACTIVE');">
                <s:text name="button.inactive"/>
            </button>
            <button type="button" class="btn btn-primary btn-sm" role="button" role="button" id="btnRemoveAlternateId"
                    data-toggle='confirmation-singleton' onclick='performAction(this,"remove");'
                    data-href="javascript:submitFormWithAction('remove');">
                <span class="glyphicon glyphicon-trash" title="Delete"></span>&nbsp;<s:text name="button.delete"/>
            </button>

        </s:else>
    </div>
    <div class="container-fluid" id="alternateIdsDiv">
        <div id="sub">
            <nv:dataTable
                    list="${subscriberAlternateIds}"
                    cssClass="table table-condensed table-blue no-footer" id="configuredAlternateIdentitiesTable"
                    width="100%"
                    showPagination="false" showInfo="false">
                <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />"  beanProperty="key"  style="min-width:1%;width:5px;"/>
                <nv:dataTableColumn title="Alternate Identities" beanProperty="key" style="min-width:50%; !important" />
                <nv:dataTableColumn title="Status" beanProperty="value" renderFunction="setStatus" style="min-width:45%; !important" />
                <s:if test="%{subscriber.status.equals(@com.elitecore.corenetvertex.constants.SubscriberStatus@DELETED.name())
                             || subscriber.status.equals(@com.elitecore.corenetvertex.constants.SubscriberStatus@INACTIVE.name())
                             || subscriber.getProfileExpiredHours() >= 0}">
                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil' disabled='disabled'></span>" style="min-width:2%;width:20px;border-right:0px;opacity: 0.50;" tdStyle="min-width:2%;width:20px;border-right:0px;opacity: 0.50;"  />
                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash' disabled='disabled'></span>" style="min-width:2%;width:20px;border-right:0px;opacity: 0.50;" tdStyle="min-width:2%;width:20px;border-right:0px;opacity: 0.50;" />
                </s:if>
                <s:else>
                    <nv:dataTableColumn title="" renderFunction="updateAlternateIdentity" style="min-width:2%;width:20px;border-right:0px;"/>
                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" style="min-width:2%;width:20px"
                                        hrefurl="delete:${pageContext.request.contextPath}/policydesigner/subscriber/Subscriber/removeAlternateIdentity?subscriberIdentity=${subscriberIdentity}&ids=key"/>
                </s:else>
            </nv:dataTable>
        </div>
    </div>
</div>
</s:form>
<%@ include file="addAlternateId.jsp" %>
<%@ include file="updateAlternateId.jsp" %>
<script type="text/javascript">
    function performAction(obj,action,status){
        var result = anyValueSelected(action,status);
        if (result.resultCode == false) {
            return addWarning(".popup", 'Please select at least one id?');
        }
        if(result.resultCode === "WARNING"){
            return addWarning(".popup", result.resultMessage);
        }
        if (action == "remove") {
            deleteConfirmMsg(obj, "Are you sure to remove Ids?");
        }
        if (action == "changeStatus") {
            deleteConfirmMsg(obj, "Are you sure to change status?");
        }
    }
    function submitFormWithAction(action,status){
            if (action == "remove") {
                $('#alternateIdListForm').attr('action', 'removeAlternateIdentity').submit();
            } else if (action == "changeStatus") {
                $('#alternateIdListForm').attr('action', 'changeAlternateIdentityStatus?status=' + status).submit();
            }
    }



    function validateAlternateIdLength(id,value,fieldName){
        if (value.length>255) {
            setError(id,'Maximum Length of '+fieldName+' can be 255');
            return false;
        }
        return true;
    }
    function openAddAlternateIdDialog(){
        $('#addAlternateIdForm')[0].reset();
        $('#addAlternateIdModal').modal('show');
        $('#alternateId').focus();
    }

    function updateAlternateIdentityDialog(oldAlternateId, status) {
        $('#updateAlternateIdentityForm')[0].reset();
        $("#oldAlternateIdentity").val(oldAlternateId);
        $("#updateAlternateIdentityDialog").modal('show');
        $("#updatedAlternateIdentity").focus();
    }

    function anyValueSelected(action,providedStatus){
        var resultCode = false;
        var resultMessage="";
        var form = $("form[id='alternateIdListForm");
        var checkBoxes =$(form).find("input[name='ids']");
        var status = $(form).find("input[name='providedStatus']");
        $(checkBoxes).each(function (index) {
            if ($(this).prop("checked")) {
                resultCode = true;
                if(action != 'remove' && $(status[index]).val() == providedStatus){
                    resultCode = "WARNING";
                    resultMessage = "Selected Id "+$(this).val()+" is already in "+providedStatus+" status";
                    return false;
                }
            }
        });
        return {"resultCode":resultCode,"resultMessage":resultMessage};
    }


</script>