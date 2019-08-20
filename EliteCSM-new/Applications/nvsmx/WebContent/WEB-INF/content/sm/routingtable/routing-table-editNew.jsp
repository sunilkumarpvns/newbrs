<%@ taglib uri="/struts-tags/ec" prefix="s" %>
<%@include file="routing-table-utility.jsp" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <s:text name="routingtable.create"/>
        </h3>
    </div>

    <div class="panel-body">
        <s:form namespace="/sm/routingtable" id="routingtableCreateForm" action="routing-table" method="post"
                cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" validator="validateRoutingTableForm('create','')"
                elementCssClass="col-xs-12 col-sm-8 col-lg-9">

            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-7">
                    <s:textfield name="name" key="routingtable.name" cssClass="form-control" id="name" type="text" maxlength="100" tabindex="1"/>
                    <s:select id="type" name="type" key="routingtable.type" listKey="getValue()" listValue="getValue()"
                              cssClass="form-control" onchange="setType()"
                              list="@com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingType@values()"
                              tabindex="2"/>
                    <s:select id="mccmncgroup" name="mccMncGroupData.id" key="routingtable.mccmncgroup"
                              cssClass="form-control select2" list="%{mccMncGroupList}" listKey="id" listValue="name"
                              cssStyle="width:100%" tabindex="3"/>
                    <s:textarea name="realmCondition" id="realmCondition" key="routingtable.realmCondition" disabled="true"
                                cssClass="form-control" type="text" maxlength="200" tabindex="4"/>
                    <s:select id="action" name="action" key="routingtable.action" listKey="getDisplayValue()"
                              listValue="getDisplayValue()" cssClass="form-control" onchange="setAction()"
                              list="@com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingAction@values()"
                              tabindex="5"/>
                </div>
            </div>

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend>
                    </legend>
                    <div class="row">
                        <div id="roleaccessgroup">
                            <div class="col-xs-12">
                                <table id="gatewayTable" class="table table-blue table-bordered">
                                    <caption class="caption-header"><s:text name="routingtable.gateways" />
                                    <div align="right" class="display-btn">
											<span class="btn btn-group btn-group-xs defaultBtn" onclick="addGateway();"
                                                  id="addGateway" disabled="true"> <span class="glyphicon glyphicon-plus"></span>
											</span>
                                    </div>
                                    <thead>
                                    <tr>
                                        <th><s:text name="gateway.name"/></th>
                                        <th><s:text name="gateway.weightage"/></th>
                                        <th style="width: 35px;">&nbsp;</th>
                                    </tr>
                                    </thead>
                                    <tbody></tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="col-xs-12" id="generalError"></div>
                </fieldset>
            </div>


            <div class="row">
                <div class="col-xs-12" align="center">
                    <s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" tabindex="8"><span
                            class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="5"
                            style="margin-right:10px;"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/routingtable/routing-table'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.list"/>
                    </button>
                </div>
            </div>
        </s:form>
    </div>
</div>
</div>
