<div class="row">
    <div class="col-sm-6">

        <table cellpadding="0" cellspacing="0" border="0" id="moduleAction" class="table table-blue">
            <thead>
            <tr>
                <th style="width: 20px">#</th>
                <th><s:text name="role.module.name"/></th>
                <th style="width: 20px"></th>
            </tr>
            </thead>
            <s:iterator value="actionJsonRelationForPD" status="order">
                <tr>
                    <td align='left' valign='top' style="width: 20px;"><s:property
                            value="%{#order.count}"/>&nbsp;&nbsp;
                    </td>
                    <td><s:property value="key"/></td>
                    <td><a tabindex='3'
                           href="javascript:showAccessRightsForPolicy('<s:property value="key"/>','showPolicyFrameForPD')"><span
                            class='glyphicon glyphicon-pencil' style="width: 20px"></span>
                    </a>
                        <s:hidden name="%{key}" value="%{value}" id="%{key}"/>

                    </td>
                </tr>
            </s:iterator>

        </table>
    </div>
    <div class="col-sm-6">
        <table class="table table-blue">
            <thead>
            <th style="padding: 1px;">
                <input type="button" class="btn btn-primary btn-sm" onclick="expandAll('showPolicyFrameForPD')"
                       value="Expand All"/> &nbsp;
                <input type="button" class="btn btn-primary btn-sm" onclick="collapseAll('showPolicyFrameForPD')"
                       value="Collapse All"/>
            </th>
            </thead>
            <tbody>
            <tr>
                <td valign="top">
                    <div id="showPolicyFrameForPD"
                         style="height: 320px ;overflow:scroll;overflow-x:hidden;overflow-y:scroll;"/>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</div>

<script language=javascript" type="text/javascript"
        src="${pageContext.request.contextPath}/js/third-party/bootstrap-treeview.js"></script>
<%@include file="roleutitlity.jsp" %>
