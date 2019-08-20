
<script type="text/javascript">
  function openServerGroupCreateDialogBox() {
    clearErrorMessages();
    $("#serverGroupDialog").modal('show');
    $("#serverGroupName").val("");
    $('#btnAdd').unbind('click');
    $("#btnAdd").text("Create");
    $("#btnAdd").on('click', function () {
      $("#serverGroupDialog").modal('hide');
    });
    $("#serverGroupName").focus();
  }
  $(function(){
      $( ".select2" ).select2();
  });

  function verifyUniqueness(){
    return verifyUniquenessOnSubmit('serverGroupName','create','','com.elitecore.corenetvertex.sm.servergroup.ServerGroupData','','') ;
  }

</script>

<s:form namespace="/sm/servergroup" action="server-group" id="serverGroupCreateForm" method="post" cssClass="form-horizontal" validate="true"  labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9">
  <s:token />
<div class="modal" id="serverGroupDialog" tabindex="-1" role="dialog" aria-labelledby="serviceDialog" aria-hidden="true"  >

  <div class="modal-dialog" >

    <div class="modal-content" >

      <div class="modal-header" >
        <button type="button" class="close" data-dismiss="modal"
                aria-label="Close" onclick="clearDialog()">
          <span aria-hidden="true">&times;</span>
        </button>
        <h4 class="modal-title set-title" id="serviceDialogTitle">
          <s:text name="servergroup.create"/>
        </h4>
      </div>

      <div class="modal-body" style="padding-top: 15px !important; padding-bottom: 0px !important;" >
           <div class="row">
              <div class="col-xs-12">
                <s:textfield 	name="name"
                                key="servergroup.name"
                                id="serverGroupName"
                                maxlength="255"
                                cssClass="form-control focusElement"/>
              </div>
            </div>
            <div class="row">
               <div class="col-xs-12">
                   <s:textfield id="serverGroupTypeId"
                                name="serverGroupType"
                                key="serverinstance.serverinstance.type"
                                cssClass="form-control" value="@com.elitecore.corenetvertex.constants.ServerGroups@PCC.name()" readonly="true"/>
				 <%--<s:select
			            id="serverGroupTypeId"
			            name="serverGroupType"
                        key="serverinstance.serverinstance.type"
                        cssClass="form-control"
                        listKey="getValue()"
                        listValue="getValue()"
                        list="@com.elitecore.corenetvertex.constants.ServerGroups@values()" onchange="onServerGroupTypeChange('serverGroupTypeId','dataSourceId');onServerGroupTypeChange('serverGroupTypeId','notificationDataSourceId')"/>--%>
				</div>
            </div>
            <div class="row">
              <div class="col-xs-12">
                <s:select 	name="groups" 	key="servergroup.groups"    cssClass="form-control select2"	list="#session.staffBelongingGroups"  id="groupNamesCreate" multiple="true" listKey="id" listValue="name" cssStyle="width:100%" />
              </div>
            </div>
            <div class="row" id="dataSourceId">
              <div class="col-xs-12">
                  <s:select name="sessionDataSourceId"
                            key="serverinstance.sessiondatasource"
                            cssClass="form-control"
                            listKey="id"
                            listValue="name"
                            list="databaseDatas"
                  />
              </div>
            </div>
            <div class="row" id="notificationDataSourceId">
              <div class="col-xs-12">
                  <s:select name="notificationDataSourceId"
                            key="servergroup.notification.datasource"
                            cssClass="form-control"
                            listKey="id"
                            listValue="name"
                            list="databaseDatas"
                            headerKey=""
                            headerValue="SELECT"
                  />
              </div>
          </div>
      </div>

      <div class="modal-footer">
        <s:submit type="button" role="button" cssClass="btn btn-primary"  onclick="return verifyUniqueness();">
            <span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/>
        </s:submit>
        <button type="button" class="btn btn-primary" data-dismiss="modal"id="btnCancel" onclick="clearDialog()">Cancel</button>
      </div>

    </div>
  </div>
</div>
</s:form>

<script type="text/javascript">
	$(function() {
		$("#serverGroupTypeId").val("<s:property  value = '@com.elitecore.corenetvertex.constants.ServerGroups@PCC.getValue()'/>");
		onServerGroupTypeChange("serverGroupTypeId", "dataSourceId");
		onServerGroupTypeChange("serverGroupTypeId","notificationDataSourceId");
	});
</script>
