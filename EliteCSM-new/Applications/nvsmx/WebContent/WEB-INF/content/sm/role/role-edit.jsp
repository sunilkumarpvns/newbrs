
<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<div class="panel panel-primary">
  <div class="panel-heading">
    <h3 class="panel-title"><s:text name="role.update" /></h3>
  </div>
  <div class="panel-body">
    <s:form namespace="/sm/role" action="role" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" >
    <s:hidden name="_method" value="put" />
    <s:token/>
    <div class="row">

      <div class="col-sm-9 col-lg-7">
        <s:hidden name="id" />

        <s:textfield name="name"
                     key="role.name"
                     id="roleName"
                     cssClass="form-control focusElement"
                     onblur="verifyUniqueness('roleName','update','%{id}','com.elitecore.corenetvertex.sm.acl.RoleData','','');" tabindex="1" />

        <s:textarea name="description"
                    key="role.description"
                    cssClass="form-control"
                    rows="2"
                    tabindex="2" />

        <s:hidden name="systemGenerated"/>

      </div>

      <fieldset class="fieldSet-line"> <legend><s:text name="role.access.rights"/></legend>
        <div class="row" >
          <div class="container-fluid">
            <ul class="nav nav-tabs tab-headings" id="tabs">
              <li class="active" >
                <a data-toggle="tab" href="#pd-tab"><s:text name="role.policydesigner"/></a>
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

      <div class="row">
        <div class="col-xs-12" align="center">
          <button class="btn btn-primary btn-sm" type="submit" role="submit" formaction="${pageContext.request.contextPath}/sm/role/role/${id}" tabindex="13"  ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
          <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="14" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/role/role/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.back" /></button>
        </div>
      </div>
      </s:form>
    </div>
  </div>
</div>
<script type="text/javascript">

  /*function validateForm(){
    return verifyUniquenessOnSubmit('groupName','update','<s:text name="id"/>','com.elitecore.corenetvertex.sm.acl.GroupData','','');
  }*/
</script>