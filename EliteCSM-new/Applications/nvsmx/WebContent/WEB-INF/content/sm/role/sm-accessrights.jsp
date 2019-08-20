<div class="row">
    <div class="col-sm-6">
        <table cellpadding="0" cellspacing="0" border="0" width="100%" id="moduleAction" class="table table-blue">
            <thead>
            <tr>
                <th>#</th>
                <th><s:text name="role.module.name"/></th>
                <th></th>
            </tr>
            </thead>
            <s:iterator value="actionJsonRelationForSM" status="order">
                <tr>
                    <td align='left' valign='top'><s:property
                            value="%{#order.count}"/>&nbsp;&nbsp;
                    </td>
                    <td><s:property value="key"/></td>
                    <td><a tabindex='3'
                           href="javascript:showAccessRightsForPolicy('<s:property value="key"/>','showPolicyFrameForSM')"><span
                            class='glyphicon glyphicon-pencil'></span>
                    </a>
                        <s:hidden name="%{key}" value="%{value}" id="%{key}"/>

                    </td>
                </tr>
            </s:iterator>

        </table>
    </div>
    <div class="col-sm-6"/>

    <table class="table table-blue">
        <thead>
        <th style="padding: 1px;">
            <input type="button" class="btn btn-primary btn-sm" onclick="expandAll('showPolicyFrameForSM')" value="Expand All"/> &nbsp;
                <input type="button" class="btn btn-primary btn-sm" onclick="collapseAll('showPolicyFrameForSM')" value="Collapse All"/>
        </th>
        </thead>
        <tbody>
        <tr>

                <%--<input type="button" class="btn btn-primary btn-sm" onclick="expandAll('showPolicyFrameForSM')" value="Expand All"/> &nbsp;
                <input type="button" class="btn btn-primary btn-sm" onclick="collapseAll('showPolicyFrameForSM')" value="Collapse All"/>--%>
        </tr>

        <tr>
            <td valign="top">
                <div id="showPolicyFrameForSM"
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
