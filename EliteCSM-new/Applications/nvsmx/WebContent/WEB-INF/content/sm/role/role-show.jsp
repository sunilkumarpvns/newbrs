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
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableResourceName=${name}&refererUrl=/sm/role/role/${id}'">
                  <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/role/role/${id}/edit'">
                  <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/sm/role/role/${id}?_method=DELETE">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
                  <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
    </div>
  </div>
  <div class="panel-body" >
    <div class="row">
      <fieldset class="fieldSet-line">
        <legend align="top"><s:text name="basic.detail" /></legend>
        <div class="row">
          <div class="col-sm-8">
            <div class="row">
              <s:label key="role.type" value="%{@com.elitecore.corenetvertex.sm.acl.RoleType@getFromName(roleType)}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
              <s:label key="role.description" value="%{description}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
              <s:hrefLabel key="getText('createdby')" value="%{createdByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9"
                           url="/sm/staff/staff/%{createdByStaff.id}"/>

              <s:if test="%{createdDate != null}">
                <s:set var="createdByDate">
                  <s:date name="%{createdDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
                </s:set>
                <s:label key="getText('createdon')" value="%{createdByDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9"/>
              </s:if>
              <s:else>
                <s:label key="getText('createdon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
              </s:else>
            </div>
          </div>
          <div class="col-sm-4 leftVerticalLine">
            <div class="row">
              <s:hrefLabel key="getText('modifiedby')" value="%{modifiedByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
                           url="/sm/staff/staff/%{modifiedByStaff.id}"/>
              <s:if test="%{modifiedDate != null}">
                <s:set var="modifiedByDate">
                  <s:date name="%{modifiedDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
                </s:set>
                <s:label key="getText('lastmodifiedon')" value="%{modifiedByDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
              </s:if>
              <s:else>
                <s:label key="getText('lastmodifiedon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
              </s:else>
            </div>
          </div>
        </div>
      </fieldset>
    </div>
    <div class="row">
    <fieldset class="fieldSet-line"> <legend><s:text name="role.access.rights"/></legend>
      <div class="row" >
        <div class="container-fluid">
          <ul class="nav nav-tabs tab-headings" id="tabs">
            <li class="active" >
              <a data-toggle="tab" href="#pd-tab"><s:text name="role.policydesigner"/> </a>
            </li>
            <li >
              <a data-toggle="tab" href="#sm-tab"><s:text name="role.servermanager"/></a>
            </li>

          </ul>
          <div class="tab-content">
            <div id="pd-tab" class="tab-pane fade in active">
              <%@include file="pd-accessrights.jsp"%>
            </div>
            <div id="sm-tab" class="tab-pane fade">
              <%@include file="sm-accessrights.jsp"%>
            </div>
          </div>
        </div>
      </div>
    </fieldset>
    </div>
    <div class="row">
      <div class="col-xs-12 back-to-list" align="center">
        <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel"
                onclick="javascript:location.href='${pageContext.request.contextPath}/sm/role/role'">
          <span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text
                name="button.list"/></button>
      </div>
    </div>
  </div>
</div>