<%@taglib uri="/struts-tags/ec" prefix="s"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<script src="${pageContext.request.contextPath}/js/nvcommon.js"></script>
<style type="text/css">
  .danger{
    color: #d9534f !important;
  }
  .danger a{
    color:#d9534f !important;
    text-decoration: none;
  }
</style>

<div class="panel panel-primary">
  <div class="panel-heading">
    <h3 class="panel-title">
      <s:text name="servergroup.search"/>
    </h3>
  </div>

  <div class="panel-body" style="padding: 0px !important;">
    <div class="col-xs-12" style="padding: 15px;">
      <button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="openServerGroupCreateDialogBox()" >
        <span class="glyphicon glyphicon-plus-sign" title="Add Server"></span> <s:text name="servergroup.add.servergroup"/>
      </button>

      <s:set name="serverGroupList" value="list"/>
      <s:if test="%{#serverGroupList.isEmpty() || #request.isServerInstanceAvailable=='false' }">
        <button class="btn btn-primary btn-xs" disabled style="padding-top: 3px; padding-bottom: 3px">
          <span class="glyphicon glyphicon-refresh" title="Global Reload Configuration"></span> <s:text name="servergroup.global.reload"/>
        </button>
      </s:if>
      <s:else>
            <button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/servergroup/server-group/1/globalReloadServerGroupsConfiguration'">
              <span class="glyphicon glyphicon-refresh" title="Global Reload Configuration"></span> <s:text name="servergroup.global.reload"/>
            </button>
      </s:else>
    </div>
    <s:if test="%{#request.serverInstanceDbSyncStatusMap.isEmpty()==false}">
      <div class="col-xs-12 danger">Note: Configuration Database of all Highlighted instances is not in Sync with Server Manager</div>
    </s:if>
    
    <div class="panel-body">
	  <div class="row">
		<div class="container-fluid">
			<ul class="nav nav-tabs tab-headings" id="tabs">
				<li class="active" id="pcctab"><a data-toggle="tab" href="#pccsection"><s:text name="servergroup.tab.pcc" /></a></li>
				<%--<li id="offlinernctab"><a data-toggle="tab" href="#offlinerncsection"><s:text name="servergroup.tab.offlinernc" /></a></li>--%>
			</ul>
			<div class="tab-content">
				<div id="pccsection" class="tab-pane fade in" style="padding-top: 2%;">
					<div>
					  <div>
						<s:iterator value="list.{? #this.serverGroupType == @com.elitecore.corenetvertex.constants.ServerGroups@PCC.getValue()}" var="serverGroupDataObj">
							<jsp:include page="ServerGroupTemplate.jsp"/>
						</s:iterator>
					  </div>
					</div>
				</div>
				
				<%--<div id="offlinerncsection" class="tab-pane fade in" style="padding-top: 2%;">
					<div>
						<div>
						<s:iterator value="list.{? #this.serverGroupType == @com.elitecore.corenetvertex.constants.ServerGroups@OFFLINE_RNC.getValue()}" var="serverGroupDataObj">
							<jsp:include page="ServerGroupTemplate.jsp"/>
						</s:iterator>  
						</div>
					</div>
				</div>--%>
			</div>
		</div>
	  </div>
    </div>
  </div>
</div>

<script type="text/javascript">
$(document).ready(function() {
	var type = '${tabType}';
	if (type == "<s:property value='@com.elitecore.corenetvertex.constants.ServerGroups@.getValue()'/>") {
		$("#pcctab").attr("class", "active");
		$("#offlinernctab").removeAttr("class");
		$("#pccsection").attr("class", "tab-pane fade in active");
	}else if (type == "<s:property value='@com.elitecore.corenetvertex.constants.ServerGroups@OFFLINE_RNC.getValue()'/>") {
		$("#offlinernctab").attr("class", "active");
		$("#pcctab").removeAttr("class");
		$("#offlinerncsection").attr("class", "tab-pane fade in active");
	}else{
		$("#pcctab").attr("class", "active");
		$("#offlinernctab").removeAttr("class");
		$("#pccsection").attr("class", "tab-pane fade in active");
	}
});
</script>
<%@include file="/WEB-INF/content/sm/servergroup/ServerGroupValidation.jsp"%>
<%@include file="/WEB-INF/content/sm/servergroup/server-group-editNew.jsp"%>
<%@include file="/WEB-INF/content/sm/servergroup/server-group-edit.jsp"%>