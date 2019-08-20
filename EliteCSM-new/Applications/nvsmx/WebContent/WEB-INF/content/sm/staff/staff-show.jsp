<%@taglib uri="/struts-tags/ec" prefix="s"%>
<style>
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
    }

    #updateStaff{
        float: right;
        margin: 10px 20px;
    }
</style>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <s:property value="name"/>
        </h3>
        <div class="nv-btn-group" align="right">
            <span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableResourceName=${name}&refererUrl=/sm/staff/staff/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/staff/staff/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
        </div>
    </div>

    <div class="panel-body">
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend>
                    <s:text name="staff.personal"/>
                </legend>
                <div class="row" id="personalDetailContent">
                    <div class="col-xs-4">
                        <div class="col-xs-12">
                            <s:url action="../../commons/staff/ProfilePicture/execute" var="profilePic" includeParams="true" >
                                <s:param name="staffId"><s:property value="%{profilePictureId}"/></s:param>
                            </s:url>
                            <img height="150" width="150" src='<s:property value="#profilePic"/>'/>
                        </div>
                    </div>
                    <div class="col-xs-8">
                        <s:label key="staff.name" value="%{name}" cssClass="control-label light-text" labelCssClass="col-xs-3" elementCssClass="col-xs-5"/>
                        <s:label key="staff.username" value="%{userName}" cssClass="control-label light-text" labelCssClass="col-xs-3 " elementCssClass="col-xs-5"/>
                        <s:label key="staff.status" value="%{@com.elitecore.corenetvertex.constants.CommonStatus@valueOf(status).statusName}" cssClass="control-label light-text" labelCssClass="col-xs-3 " elementCssClass="col-xs-5"/>
                        <s:label key="staff.authentication.mode" value="%{authenticationMode}" cssClass="control-label light-text" labelCssClass="col-xs-3 " elementCssClass="col-xs-5"/>
                        <s:label key="staff.email" value="%{emailAddress}" cssClass="control-label light-text" labelCssClass="col-xs-3" elementCssClass="col-xs-5"/>
                        <s:label key="staff.phone.no" value="%{phone}" cssClass="control-label light-text" labelCssClass="col-xs-3" elementCssClass="col-xs-5"/>
                        <s:label key="staff.mobile.no" value="%{mobile}" cssClass="control-label light-text" labelCssClass="col-xs-3" elementCssClass="col-xs-5"/>
                    </div>

                </div>
            </fieldset>


            <fieldset class="fieldSet-line">
                <legend>
                    <s:text name="staff.audit"/>
                </legend>
                <div class="row" id="auditDetailContent">
                    <div class="col-sm-6">
                        <%@include file="/WEB-INF/content/common/createdModifiedByUserDiv.jsp"%>
                    </div>
                    <div class="col-sm-6 leftVerticalLine">
                        <s:if test="%{lastLoginTime != null}">
                            <s:set var="lastLoginTimeForStaff">
                                <s:date name="%{lastLoginTime}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
                            </s:set>
                            <s:label key="getText('staff.lastlogindate')" value="%{lastLoginTimeForStaff}" cssClass="control-label light-text" labelCssClass="col-xs-7" elementCssClass="col-xs-5"/>
                        </s:if>
                        <s:else>
                            <s:label key="getText('staff.lastlogindate')" value="%{lastLoginTime}" cssClass="control-label light-text" labelCssClass="col-xs-7" elementCssClass="col-xs-5"/>
                        </s:else>


                        <s:if test="%{passwordChangeDate != null}">
                            <s:set var="passwordChangeDateForStaff">
                                <s:date name="%{passwordChangeDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
                            </s:set>
                            <s:label key="getText('staff.passwordchangedate')" value="%{passwordChangeDateForStaff}" cssClass="control-label light-text" labelCssClass="col-xs-7" elementCssClass="col-xs-5"/>
                        </s:if>
                        <s:else>
                            <s:label key="getText('staff.passwordchangedate')" value="%{passwordChangeDate}" cssClass="control-label light-text" labelCssClass="col-xs-7" elementCssClass="col-xs-5"/>
                        </s:else>

                        <s:if test="%{passwordExpiryDate != null}">
                            <s:set var="passwordExpiryDateForStaff">
                                <s:date name="%{passwordExpiryDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
                            </s:set>
                            <s:label key="getText('staff.passwordexpirydate')" value="%{passwordExpiryDateForStaff}" cssClass="control-label light-text" labelCssClass="col-xs-7" elementCssClass="col-xs-5"/>
                        </s:if>
                        <s:else>
                            <s:label key="getText('staff.passwordexpirydate')" value="%{passwordExpiryDate}" cssClass="control-label light-text" labelCssClass="col-xs-7" elementCssClass="col-xs-5"/>
                        </s:else>

                    </div>
                </div>
            </fieldset>
            <fieldset class="fieldSet-line">
                <legend><s:text name="staff.role.access.group.relations"/></legend>
                    <table class="table table-blue">
                        <thead>
                        <th><s:text name="staff.role"/></th>
                        <th><s:text name="staff.group"/></th>
                        </thead>
                        <tbody>
                        <s:iterator value="staffGroupRoleRelDataList" >
                            <tr>
                                <td><s:property value="roleData.name"/></td>
                                <td><s:property value="groupData.name"/></td>
                            </tr>
                        </s:iterator>
                        </tbody>
                    </table>
            </fieldset>
            <div class="row">
                <div class="col-xs-12 back-to-list" align="center">
                    <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/staff/staff'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text
                            name="button.list"/></button>
                </div>
            </div>
        </div>
    </div>
</div>
