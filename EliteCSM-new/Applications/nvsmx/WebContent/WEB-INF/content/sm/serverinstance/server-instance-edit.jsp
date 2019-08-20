<%@taglib uri="/struts-tags/ec" prefix="s"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<script type="text/javascript">
  $(function(){
      $( ".select2" ).select2();
      var fileLocationIds = "<s:property value='fileLocation'/>";
      var fileLocationIdsArray = fileLocationIds.trim().split(/\s*,\s*/);
      $("#fileLocationId").select2().val(fileLocationIdsArray).trigger("change");
  });
</script>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <s:text name="serverinstance.update" />
        </h3>
    </div>

    <div class="panel-body">

        <s:form namespace="/sm/serverinstance" action="server-instance"
                id="serverProfileEditForm"
                cssClass="form-horizontal form-group-sm "
                labelCssClass="col-xs-4 col-sm-5 text-right"
                elementCssClass="col-xs-8 col-sm-7"
                theme="bootstrap" validate="true">

            <s:hidden name="_method" value="put" />
            <s:hidden name="isCallFromWeb" value="true" />
            <s:token/>
            <input type="hidden" name="serverGroupId" value="${serverGroupId}"/>

            <div class="row">
                <div class="col-xs-12 col-sm-6 col-lg-6">
                    <s:hidden />
                    <s:textfield name="name"
                                 id="serverInstanceName"
                                 key="serverinstance.name"
                                 cssClass="form-control focusElement"
                                 maxlength="255"
                                 onblur="verifyUniqueness('serverInstanceName','update','%{id}','com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData','','');"
                                 tabindex="1"/>

                    <s:textarea name="description"
                                key="serverinstance.description"
                                cssClass="form-control"
                                cssStyle="height:60px;"
                                tabindex="3"/>

                </div>
                <div class="col-xs-12 col-sm-6 col-lg-6">

                    <s:textfield name="restApiUrl"
                                 id="restApiUrl"
                                 placeholder="IP:Port"
                                 key="serverinstance.rest.api.listener"
                                 cssClass="form-control" maxlength="100"
                                 tabindex="4"/>

                    <s:textfield name="snmpUrl"
                                 id="snmpUrl"
                                 placeholder="IP:Port"
                                 key="serverinstance.snmp.address" cssClass="form-control"
                                 maxlength="100" tabindex="5"/>

                </div>
            </div>

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend><s:text name="serverinstance.notification.services"/></legend>
                    <div class="row">
                        <div class="col-xs-12 col-sm-6 col-lg-6">
                            <s:select name="emailAgentId"
                                      key="serverinstance.emailagent"
                                      cssClass="form-control"
                                      headerKey=""
                                      headerValue="-select-"
                                      list="emailAgentDataList"
                                      listValue="name"
                                      listKey="id"
                                      tabindex="5"/>
                        </div>

                        <div class="col-xs-12 col-sm-6 col-lg-6">
                            <s:select name="smsAgentId"
                                      key="serverinstance.smsgateway"
                                      cssClass="form-control"
                                      headerKey=""
                                      headerValue="-select-"
                                      list="smsAgentDataList"
                                      listValue="name"
                                      listKey="id"
                                      tabindex="6"/>

                        </div>
                    </div>
                </fieldset>
            </div>

 			<s:if test="#session.serverGroupType == @com.elitecore.corenetvertex.constants.ServerGroups@PCC.getValue()">
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend><s:text name="serverinstance.diameter.stack"/></legend>
                    <div class="row">
                        <div class="col-xs-12 col-sm-6 col-lg-6">

                            <s:select name="diameterEnabled"
                                      id="diameterEnabled"
                                      key="serverinstance.diameter.listener.enable"
                                      cssClass="form-control"
                                      list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
                                      listKey="getStringNameBoolean()"
                                      listValue="getStringName()"
                                      onchange="enableDisableDiameterFields();"
                                      tabindex="7"/>

                            <s:textfield name="diameterOriginHost"
                                         id="diameterOriginHost"
                                         key="serverinstance.diameter.origin.host"
                                         cssClass="form-control"
                                         maxlength="255" tabindex="9"/>

                        </div>


                        <div class="col-xs-12 col-sm-6 col-lg-6">
                            <s:textfield name="diameterUrl"
                                         id="diameterUrl"
                                         placeholder="IP:Port"
                                         key="serverinstance.diameter.url"
                                         cssClass="form-control"
                                         maxlength="255" tabindex="8"/>
                                <%--Timestamp--%>
                            <s:textfield name="diameterOriginRealm"
                                         id="diameterOriginRealm"
                                         key="serverinstance.diameter.origin.realm" cssClass="form-control"
                                         maxlength="255" tabindex="10"/>

                        </div>

                    </div>
                </fieldset>
            </div>

            <%--Radius Listener--%>
                <div class="row">
                    <fieldset class="fieldSet-line">
                        <legend><s:text name="serverinstance.listener"/></legend>
                        <div class="row">
                            <div class="col-xs-12 col-sm-6 col-lg-6">

                                <s:select name="radiusEnabled"
                                          id="radiusEnabled"
                                          key="serverinstance.radius.listener.enable"
                                          list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
                                          listKey="getStringNameBoolean()"
                                          listValue="getStringName()"
                                          onchange="enableDisableRadiusFields();"
                                          cssClass="form-control" tabindex="11"/>

                            </div>
                            <div class="col-xs-12 col-sm-6 col-lg-6">
                                <s:textfield name="radiusUrl"
                                             id="radiusUrl"
                                             key="serverinstance.radius.url"
                                             placeholder="IP:Port"
                                             cssClass="form-control"
                                             maxlength="255" tabindex="12"/>
                            </div>

                        </div>
                    </fieldset>
                </div>
                <!-- groovy Script configuration -->
                <div class="row">
                    <fieldset class="fieldSet-line">
                        <legend><s:text name="serverinstance.groovy.script"/></legend>
                        <table id="groovyScriptTable" class="table table-blue table-bordered">
                            <caption class="caption-header"><s:text name="serverinstance.groovy.script"/>
                                <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn"
                                      onclick="addGroovyScript();" id="addGroovyRow"> <span
                                        class="glyphicon glyphicon-plus"></span></span>
                                </div>
                            </caption>
                            <thead>
                            <th class="min-width">
                                <s:text name="serverinstance.groovy.script.name"/>
                            </th>
                            <th class="min-width">
                                <s:text name="serverinstance.groovy.script.argument"/>
                            </th>
                            <th style='width:35px;'></th>
                            </thead>
                            <tbody>
                            <s:iterator value="groovyScriptDatas" status="i" var="groovyScript">
                                <tr>
                                    <td style="min-width: 50%;">

                                        <s:textfield cssClass="form-control" elementCssClass="col-xs-12"
                                                     maxLength="2048"
                                                     name="groovyScriptDatas[%{#i.count - 1}].scriptName"
                                                     value="%{#groovyScript.scriptName}"
                                                     id="scriptName%{#i.count - 1}"/>

                                    </td>
                                    <td style="min-width: 50%;">
                                        <s:textfield cssClass="form-control condition" elementCssClass="col-xs-12"
                                                     maxLength="2048"
                                                     name="groovyScriptDatas[%{#i.count - 1}].argument"
                                                     value="%{#groovyScript.argument}" id="argument%{#i.count - 1}"/>

                                    </td>
                                    <td style='width:35px;'>
                                    <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                                        <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
                                    </span>
                                    </td>
                                </tr>
                            </s:iterator>
                            </tbody>
                        </table>
                    </fieldset>
                </div>
            </s:if>
            
            <!-- Offline RnC  -->
			<s:if test="#session.serverGroupType == @com.elitecore.corenetvertex.constants.ServerGroups@OFFLINE_RNC.getValue()">
				<div class="row">
				<fieldset class="fieldSet-line">
	                   <legend><s:text name="serverinstance.offlinernc"/></legend>
					 <div class="row">
						<div class="col-xs-12 col-sm-6 col-lg-6">
								
							<s:select name="offlineRncService"
		                              id="offlineRncService"
		                              key="serverinstance.offlinernc.service"
		                              list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
		                              listKey="getStringNameBoolean()"
		                              listValue="getStringName()"
		                              value="False"
		                              cssClass="form-control" tabindex="11"/>	
		
						</div>
						<div class="col-xs-12 col-sm-6 col-lg-6">
						    <s:select  name="fileLocation"
							           id="fileLocationId"
				                       key="serverinstance.filelocation"
				                       cssClass="form-control select2"
				                       listKey="id"
				                       listValue="name"
				                       list="fileLocationList"
				                       multiple="true"/>        
						</div>
					</div>
	               </fieldset>
				</div>
			</s:if>

            <div align="center">
              <button class="btn btn-sm btn-primary" type="submit" onclick="return validateOnSubmit()" role="button" formaction="${pageContext.request.contextPath}/sm/serverinstance/server-instance/${id}" tabindex="13"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="14" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/servergroup/server-group'"><span class="glyphicon glyphicon-backward" title="Back"></span>
                    <s:text name="serverinstance.servergroups"/>
                </button>
            </div>

        </s:form>

    </div>
</div>



<script src="${pageContext.request.contextPath}/js/ServerInstanceValidation.js"></script>

<script type="text/javascript">
	function validateOnSubmit() {
		var flag = verifyUniquenessOnSubmit(
				'serverInstanceName',
				'update',
				'${id}',
				'com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData',
				'', '');

		if (flag) {
			flag = verifyMandatoryFieldsForDiameter();
			if (flag) {
				flag = verifyMandatoryFieldsForRadius();
			}
		}
		return flag && validateGroovyScriptDatas();
	}
</script>
<%@include file="server-instance-groovy-script-utility.jsp"%>

