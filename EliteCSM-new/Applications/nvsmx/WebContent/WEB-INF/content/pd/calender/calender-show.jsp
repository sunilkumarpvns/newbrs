<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<style type="text/css">
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
    }
</style>
<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <s:property value="name"/>
        </h3>
        <div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
				onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${id}&auditableId=${id}&auditPageHeadingName=${name}&refererUrl=/pd/calender/calender/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/pd/calender/calender/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/pd/calender/calender/${id}?_method=DELETE">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
        </div>
    </div>
    <div class="panel-body" >
     
        </button>
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="basic.detail" /></legend>
                <div class="row">
                    <div class="col-sm-6">
                        <div class="row">
                            <s:label key="calender.calenderList" value="%{calenderList}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="calender.calenderDescription" value="%{description}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                    <div class="col-sm-6 leftVerticalLine">
                        <div class="row">
                            <s:label key="getText('createdby')" value="%{createdByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 "/>
                            <s:if test="%{createdDate != null}">
                                <s:set var="createdByDate">
                                    <s:date name="%{createdDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
                                </s:set>
                                <s:label key="getText('createdon')" value="%{createdByDate}" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 "/>
                            </s:if>
                            <s:else>
                                <s:label key="getText('createdon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 " />
                            </s:else>
                            <s:label key="getText('modifiedby')" value="%{modifiedByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 "/>
                            <s:if test="%{modifiedDate != null}">
                                <s:set var="modifiedByDate">
                                    <s:date name="%{modifiedDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
                                </s:set>
                                <s:label key="getText('lastmodifiedon')" value="%{modifiedByDate}" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 "/>
                            </s:if>
                            <s:else>
                                <s:label key="getText('lastmodifiedon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 " />
                            </s:else>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-xs-12 back-to-list" align="center">
                        <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/calender/calender'" tabindex="5"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>
                    </div>
                </div>
            </fieldset>
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="calender.details"/></legend>
                <div class="row">
                    <div class="col-sm-12">
                        <nv:dataTable
                                id="calenderDetails"
                                list="${calenderDetailsDataAsJson}"
                                width="100%"
                                showPagination="false"
                                showInfo="false"
                                cssClass="table table-blue">
                               <nv:dataTableColumn title="Calender Name" beanProperty="Calendar Name" tdCssClass="text-left text-middle" tdStyle="width:25%"/>
                               <nv:dataTableColumn title="From Date" beanProperty="From Date" tdCssClass="text-left text-middle fromDate" tdStyle="width:25%"/>
                               <nv:dataTableColumn title="To Date" beanProperty="To Date" tdCssClass="text-left text-middle toDate" tdStyle="width:25%"/>
                        </nv:dataTable>
                    </div>
                </div>
            </fieldset>
           
           
        </div>
    </div>
</div>

<script>

var fromDates = $(".fromDate");
for(var index = 1 ; index < fromDates.length ; index++){
    var fromDatesDom = fromDates[index];
    var fromDatesText = fromDatesDom.textContent;
    fromDatesDom.textContent = fromDatesText.slice(0,-12);
}

var toDates = $(".toDate");
for(var index = 1 ; index < toDates.length ; index++){
    var toDatesDom = toDates[index];
    var toDatesText = toDatesDom.textContent;
    toDatesDom.textContent = toDatesText.slice(0,-12);
}

</script>


