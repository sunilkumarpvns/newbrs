<%@ taglib uri="/struts-tags/ec" prefix="s" %>
<%@include file="routing-table-utility.jsp" %>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <s:text name="routingtable.update"/>
        </h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/routingtable" action="routing-table" method="post" cssClass="form-horizontal"
                validate="true" validator="validateRoutingTableForm('update','%{id}')" id="routingtableUpdateForm"
                labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9">
            <s:hidden name="_method" value="put"/>
            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-7">
                    <s:textfield name="name" key="routingtable.name" cssClass="form-control" id="name" type="text"
                                 maxlength="100" tabindex="1"/>
                    <s:textfield name="type" key="routingtable.type"
                                 cssClass="form-control" id="type"
                                 tabindex="3" readonly="true"/>
                    <s:select id="mccmncgroup" name="mccMncGroupData.id" key="routingtable.mccmncgroup"
                              cssClass="form-control select2" list="%{mccMncGroupList}" listKey="id" listValue="name"
                              cssStyle="width:100%" tabindex="3"/>
                    <s:textarea name="realmCondition" id="realmCondition" key="routingtable.realmCondition"
                                cssClass="form-control" type="text" maxlength="200" tabindex="4"/>
                    <s:select id="action" name="action" key="routingtable.action" listKey="getDisplayValue()"
                              listValue="getDisplayValue()" cssClass="form-control" onchange="setAction()"
                              list="@com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingAction@values()"
                              tabindex="5"/>
                </div>
            </div>

            <div class="row">
                <fieldset class="fieldSet-line">
                    <div class="row">
                        <div id="roleaccessgroup">
                            <div class="col-xs-12">
                                <table id="gatewayTable" class="table table-blue table-bordered">
                                    <caption class="caption-header">
                                            <s:text name="routingtable.gateways"/>
                                        <div align="right" class="display-btn">
											<span class="btn btn-group btn-group-xs defaultBtn" onclick="addGateway();"
                                                  id="addGateway"> <span
                                                    class="glyphicon glyphicon-plus"></span>
											</span>
                                        </div>
                                        <thead>
                                        <tr>
                                            <th><s:text name="gateway.name"/></th>
                                            <th><s:text name="gateway.weightage"/></th>
                                            <th style="width: 35px;">&nbsp;</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <s:iterator value="routingTableGatewayRelDataList" var="routingTableGatewayRel"
                                                    status="stat">
                                            <tr>
                                                <td><s:select value="%{#routingTableGatewayRel.diameterGatewayData.id}"
                                                              list="diameterGatewayList"
                                                              name="routingTableGatewayRelDataList[%{#stat.index}].diameterGatewayData.id"
                                                              listValue="name" cssClass="form-control" listKey="id"
                                                              elementCssClass="col-xs-12"
                                                              onblur="validateGateway(this)"/>
                                                </td>
                                                <td>
                                                    <s:textfield
                                                            name="routingTableGatewayRelDataList[%{#stat.index}].weightage"
                                                            cssClass="form-control" type="number" maxlength="512"
                                                            min="0" max="10" required="required"
                                                            elementCssClass="col-xs-12 weightage"/>
                                                </td>
                                                <td style='width:35px;'><span class='btn defaultBtn'
                                                                              onclick='$(this).parent().parent().remove();'><a><span
                                                        class='delete glyphicon glyphicon-trash' title='delete'></span></a></span>
                                                </td>
                                            </tr>
                                        </s:iterator>

                                        </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="col-xs-12" id="generalError"></div>
                </fieldset>
            </div>

            <div class="row">
                <div class="col-xs-12" align="center">
                    <button class="btn btn-sm btn-primary" type="submit" role="button"
                            formaction="${pageContext.request.contextPath}/sm/routingtable/routing-table/${id}"
                            tabindex="13"><span
                            class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="14"
                            style="margin-right:10px;"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/routingtable/routing-table/${id}'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.back">
                    </s:text>
                    </button>
                </div>
            </div>
        </s:form>
    </div>
</div>