<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv"%>

<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">${auditableResourceName} History</h3>
    </div>
    <div class="panel-body">
        <nv:dataTable id="historyAuditData"
                      list="${auditDataListAsJson}"
                      rows="10"
                      width="100%"
                      showPagination="false"
                      showInfo="false"
                      cssClass="table table-blue"
                      subTableUrl="/sm/audit/audit/${actualId}/viewDetail">
            <nv:dataTableColumn title="Staff" beanProperty="staffUserName" tdCssClass="text-left text-middle" style="width:100px" />
            <nv:dataTableColumn title="Log Message" beanProperty="message" tdCssClass="text-left text-middle" style="width:100px" />
            <nv:dataTableColumn title="Client IP" beanProperty="clientIp" tdCssClass="text-left text-middle" style="width:100px" />
            <nv:dataTableColumn title="TimeStamp" beanProperty="auditDate" tdCssClass="text-left text-middle" style="width:100px" />
        </nv:dataTable>
        <div class="row">
            <div class="col-xs-12" align="center">
                <button id="btnBack" class="btn btn-primary btn-sm"  value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}${refererUrl}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.back" /> </button>
            </div>
        </div>
    </div>
</div>
