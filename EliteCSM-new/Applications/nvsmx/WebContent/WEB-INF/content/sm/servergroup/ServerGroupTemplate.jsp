 <%@taglib uri="/struts-tags/ec" prefix="s"%>
 
 
 <fieldset class="fieldSet-line">
        <legend align="top" > <s:property value="%{#serverGroupDataObj.name}" /></legend>
        <div align="right" class="nv-btn-group">

          <div class="btn-group btn-group-xs" role="group">
            <button type="button" class="btn btn-default" data-toggle="tooltip" data-placement="bottom" title="Audit History"
                    onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${serverGroupDataObj.id}?auditableResourceName=${serverGroupDataObj.name}&refererUrl=/sm/servergroup/server-group'">
                <span class="glyphicon glyphicon-eye-open" ></span>
            </button>
          </div>
          <div class="btn-group btn-group-xs" role="group" data-toggle="tooltip" data-placement="bottom" title="Edit" >
            <button type="button" class="btn btn-default" onclick="openServerGroupUpdateDialogBox('${serverGroupDataObj.id}','${serverGroupDataObj.name}','${serverGroupDataObj.groups}','${serverGroupDataObj.databaseData.id}','${serverGroupDataObj.serverGroupType}','${serverGroupDataObj.notificationDataSourceData .id}')" >
              <span class="glyphicon glyphicon-pencil"></span>
            </button>
          </div>

          <s:if test="%{#serverGroupDataObj.serverInstances.isEmpty()}">

            <div class="btn-group btn-group-xs" role="group" disabled >
              <button type="button" disabled class="btn btn-default" data-toggle="tooltip" data-placement="bottom" title="Reload Configuration" >
                <span class="glyphicon glyphicon-refresh" ></span>
              </button>
            </div>
          </s:if>
          <s:else>
            <div class="btn-group btn-group-xs" role="group">
              <button type="button" class="btn btn-default" data-toggle="tooltip" data-placement="bottom" title="Reload Configuration"
                      onclick="javascript:location.href='${pageContext.request.contextPath}/sm/servergroup/server-group/${serverGroupDataObj.id}/reloadServerGroupConfiguration'">
                <span class="glyphicon glyphicon-refresh" ></span>
              </button>
            </div>
          </s:else>
           <div class="btn-group btn-group-xs" role="group" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/sm/servergroup/server-group/${serverGroupDataObj.id}?_method=DELETE">
              <button type="button" class="btn btn-default" data-toggle="tooltip" data-placement="bottom" title="Delete">
                <span class="glyphicon glyphicon-trash"></span>
              </button>
            </div>
        </div>

          <s:if test="%{#serverGroupDataObj.serverInstances.isEmpty() || #serverGroupDataObj.serverInstances.size()<2}">

            <button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/serverinstance/server-instance/new?serverGroupId=${serverGroupDataObj.id}&serverGroupType=${serverGroupDataObj.serverGroupType}'" >
                <span class="glyphicon glyphicon-plus-sign" title="Add Server"></span> <s:text name="servergroup.add.server"/>
            </button>

            <button class="btn btn-primary btn-xs" disabled="disabled" style="padding-top: 3px; padding-bottom: 3px" >
              <span class="glyphicon glyphicon-sort" title="Add Server"></span> <s:text name="servergroup.swap.role"/>
            </button>
          </s:if>

          <s:else>
              <button class="btn btn-primary btn-xs" disabled style="padding-top: 3px; padding-bottom: 3px" onclick="" >
                  <span class="glyphicon glyphicon-plus-sign" title="Add Server"></span> <s:text name="servergroup.add.server"/>
              </button>

              <button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px"  onclick="javascript:location.href='${pageContext.request.contextPath}/sm/servergroup/server-group/${serverGroupDataObj.id}/swapServerInstancesRole'" >
                <span class="glyphicon glyphicon-sort" title="Swap Role"></span> <s:text name="servergroup.swap.role"/>
              </button>
          </s:else>

        <table id="timeBaseCondition" style="width: 100%;" class="table table-blue dataTable no-footer" subtableurl="" role="grid" width="100%">

          <thead>
          <tr role="row">
            <th> <s:text name="servergroup.instance.role"/> </th>
            <th> <s:text name="servergroup.instance.name"/> </th>
            <th> <s:text name="servergroup.instance.description"/> </th>
            <th> <s:text name="servergroup.restApiUrl"/> </th>
            <th> </th>
            <th> </th>
            <th> </th>
          </tr>
          </thead>

          <tbody>
          <s:if test="%{#serverGroupDataObj.serverInstances.isEmpty()}">
            <tr>
              <td class="dataTables_empty" colspan="7" align="center" ><s:text name="servergroup.noinstance.available"/> </td>
            </tr>
          </s:if>
          <s:else>
            <s:iterator value="%{#serverGroupDataObj.serverInstances}" var="serverInstanceObj">
              <s:if test="#request.serverInstanceDbSyncStatusMap[#serverInstanceObj.id]==false">
                <tr class="danger" >
              </s:if>
              <s:else>
                <tr>
              </s:else>

                <s:if test="%{#serverInstanceObj.serverGroupServerInstanceRelData.serverWeightage==1}">
                  <td class="dataTables_empty" > <s:text name="servergroup.serverinstance.primary"/> </td>
                </s:if>
                <s:else>
                  <td class="dataTables_empty" > <s:text name="servergroup.serverinstance.secondary"/> </td>
                </s:else>

                <td class="dataTables_empty" >
                  <a href="${pageContext.request.contextPath}/sm/serverinstance/server-instance/${serverInstanceObj.id}?serverGroupId=${serverGroupDataObj.id}&serverGroupType=${serverGroupDataObj.serverGroupType}">
                  <s:property value="%{#serverInstanceObj.name}"/>
                  </a>
                </td>

                <td class="dataTables_empty" >
                  <s:property value="#serverInstanceObj.description"/>
                </td>
                <td class="dataTables_empty" ><s:property value="#serverInstanceObj.restApiUrl"/></td>
                <td class="dataTables_empty" style="text-align: center">
                  <a href="${pageContext.request.contextPath}/sm/serverinstance/server-instance/${serverInstanceObj.id}/edit?serverGroupId=${serverGroupDataObj.id}&serverGroupType=${serverGroupDataObj.serverGroupType}">
                    <span class="glyphicon glyphicon-pencil"></span>
                  </a>
                </td>
                <td class="dataTables_empty" style="text-align: center" >
                  <span data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/sm/serverinstance/server-instance/${serverInstanceObj.id}?_method=DELETE">
                      <a style="cursor:pointer"><span class="glyphicon glyphicon-trash"></span></a>
			      </span>
                </td>
                <td class="dataTables_empty" style="text-align: center" >
                  <s:if test="#request.serverInstanceDbSyncStatusMap[#serverInstanceObj.id]==false">
                  <span data-toggle="confirmation-singleton" title="Sync database detail" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/sm/serverinstance/server-instance/${serverInstanceObj.id}/synchDatabaseDetail">
                      <a style="cursor:pointer"><span class="glyphicon glyphicon-repeat"></span></a>
			      </span>
                  </s:if>
                  <s:else>
                    <span disabled="disabled" data-toggle="confirmation-singleton" title="Sync database detail" onmousedown="" data-href="">
                      <a style="cursor:pointer;color:darkgray"><span class="glyphicon glyphicon-repeat"></span></a>
			      </span>
                  </s:else>
                </td>
              </tr>
            </s:iterator>
          </s:else>
          </tbody>

        </table>
      </fieldset>