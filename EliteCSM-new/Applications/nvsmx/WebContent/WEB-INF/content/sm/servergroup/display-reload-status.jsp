<%@ taglib prefix="s" uri="/struts-tags/ec"%>
<style type="text/css">
  .successs{
    color: #3c763d !important;
    font-weight: bold;
  }
  .failure{
    color: #d9534f;
    font-weight: bold;
  }
</style>
<div class="panel panel-primary">
  <div class="panel-heading">
    <h3 class="panel-title"> <s:text name="servergroup.serverinstance.reloadstatus"/> </h3>
  </div>
  <div class="panel-body" >
    <div class="col-sm-12">
    <table class="table table-blue dataTable no-footer" >
      <thead>
        <tr role="row">
          <th> <s:text name="servergroup" /></th>
          <th> <s:text name="servergroup.serverinstance"/> </th>
          <th> <s:text name="serverinstance.reload.status"/> </th>
          <th> <s:text name="serverinstance.reload.remarks"/> </th>
        </tr>
      </thead>
      <tbody>
        <s:iterator value="#request.reloadResponses" var="rmiResponse">
          <tr>
            <td class="dataTables_empty" > <s:property value="%{#rmiResponse.instanceGroupData.name}"/> </td>
            <td class="dataTables_empty" > <s:property value="%{#rmiResponse.instanceData.name}" /> </td>
            <s:if test="%{#rmiResponse.isSuccess()}">
              <td class="dataTables_empty successs" > <s:property value="%{#rmiResponse.response}"/> </td>
              <td class="dataTables_empty" > </td>
            </s:if>
            <s:else>
              <td class="dataTables_empty failure" > <s:text name="serverinstance.reload.failed"/> </td>
              <td class="dataTables_empty" > <s:property value="%{#rmiResponse.error.message}"/> </td>
            </s:else>

          </tr>
        </s:iterator>
      </tbody>
    </table>
      <div align="center">
        <button type="button"class="btn btn-sm btn-primary" data-toggle="tooltip" data-placement="bottom" title="Server Groups"
                onclick="javascript:location.href='${pageContext.request.contextPath}/${backUrl}'">
          <span class="glyphicon glyphicon-backward" title="Back"></span>
          <s:text name="button.back"/>
        </button>
      </div>
    </div>
  </div>
</div>

