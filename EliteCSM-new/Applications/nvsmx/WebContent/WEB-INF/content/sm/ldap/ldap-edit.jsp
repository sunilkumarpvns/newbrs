<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="ldap.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/ldap" action="ldap" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()" >
            <s:hidden name="_method" value="put" />
            <s:token/>
            <div>
                <div class="col-xs-12 col-sm-6">
                    <s:textfield name="name" key="ldap.name" id="ldapName" cssClass="form-control focusElement" onblur="verifyUniqueness('ldapName','update','%{id}','com.elitecore.corenetvertex.ldap.LdapData','','');" />
                    <s:textfield name="address" key="ldap.address" cssClass="form-control" />
                    <s:textfield name="queryTimeout" type="number" key="ldap.querytimeout" cssClass="form-control" onkeypress="return isNaturalInteger(event);" id="queryTimeout" />
                    <s:textfield name="statusCheckDuration" type="number" key="ldap.statuscheckduration" cssClass="form-control" onkeypress="return isNaturalInteger(event);" id="statusCheckDuration" />
                    <s:textfield name="sizeLimit" type="number" key="ldap.sizelimit" cssClass="form-control" onkeypress="return isNaturalInteger(event);" id="sizeLimit" min="0" />
                    <s:select list="@com.elitecore.corenetvertex.ldap.LdapVersion@values()" listValue="name()" listKey="getVersion()" name="version" key="ldap.version" id="version" />
                </div>
                <div class="col-xs-12 col-sm-6">
                    <s:textfield name="administrator" key="ldap.administrator" cssClass="form-control" />
                    <s:hidden name="password"    type="password" key="ldap.password" cssClass="form-control"  id="password"/>
                    <s:textfield name="userDnPrefix" key="ldap.userdnprefix" cssClass="form-control" />
                    <s:textfield name="minimumPool" type="number" key="ldap.minimumpool" cssClass="form-control" onkeypress="return isNaturalInteger(event);" id="minimumPool" />
                    <s:textfield name="maximumPool" type="number" key="ldap.maximumpool" cssClass="form-control" onkeypress="return isNaturalInteger(event);" id="maximumPool" />
                </div>
                <div id="ldapBaseDnDiv">
                    <div class="col-xs-12 col-sm-12">
                        <table id='ldapBaseDnTable'  class="table table-blue table-bordered">
                            <caption class="caption-header"><s:text name="ldap.base.dns" />
                                <div align="right" class="display-btn">
                                    <span class="btn btn-group btn-group-xs defaultBtn" onclick="addBaseDN();" id="addRow"> <span class="glyphicon glyphicon-plus"></span></span>
                                </div>
                            </caption>
                            <thead>
                            <th><s:text name="ldap.base.dn.name"/></th>
                                <th style="width:35px;">&nbsp;</th>
                            </thead>
                            <tbody>
                            <s:iterator value="ldapBaseDns" status="i" var="ldapBaseDns">
                                <tr name='baseDnRow'>
                                    <td><s:textfield value="%{#ldapBaseDns.searchBaseDn}"	name="ldapBaseDns[%{#i.count - 1}].searchBaseDn" cssClass="form-control"  elementCssClass="col-xs-12" /></td>
                                    <td style="width:35px;"><span class="btn defaultBtn" onclick="$(this).parent().parent().remove();"><a><span class="delete glyphicon glyphicon-trash" title="delete"></span></a></span></td>
                                </tr>
                            </s:iterator>
                            </tbody>
                        </table>
                        <div class="col-xs-12" id="generalError"></div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-xs-12" align="center">
                        <button type="submit" class="btn btn-primary btn-sm"  role="submit" formaction="${pageContext.request.contextPath}/sm/ldap/ldap/${id}" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                        <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/ldap/ldap/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.back"/></button>
                    </div>
                </div>
            </div>
        </s:form>
    </div>
</div>
<script type="text/javascript">

    function validateForm(){
        clearErrorMessages();
        var minimumPool = $("#minimumPool").val();
        var maximumPool = $("#maximumPool").val();
        var password = $("#password").val();
        var baseDnTableBodyLength = $("#ldapBaseDnTable tbody tr").length;

        var isValidName = verifyUniquenessOnSubmit('ldapName','update','<s:property value="id"/>','com.elitecore.corenetvertex.ldap.LdapData','','');
        if (isValidName == false) {
            return false;
        } else if (parseInt(minimumPool) > parseInt(maximumPool)) {
            setError('minimumPool', '<s:text name="ldap.min.max.pool.value.invalid" />');
            return false;
        } else if(isNullOrEmpty(password)) {
            setError('password', "Password is required");
            return false;
        } else if(baseDnTableBodyLength < 1) {
            $("#generalError").addClass("bg-danger");
            $("#generalError").text('<s:text name="ldap.base.dn.required" />');
            return false;
        } else if(baseDnTableBodyLength >= 1) {
            //This will check that Base Dn value should not be null or empty
            var isValidBaseDnValue = true;
            $("#ldapBaseDnTable tbody tr").each(function () {
                var inputElement =$(this).children().first().find('input');
                if(isNullOrEmpty(inputElement.val())){
                    setErrorOnElement(inputElement,'<s:text name="ldap.data.search.base.dn" />');
                    isValidBaseDnValue = false;
                }
            });

            if(isValidBaseDnValue == false) {
                return false;
            }else {
                return true
            }

        }
    }

    var i = document.getElementsByName("baseDnRow").length;
    function addBaseDN(){
        var tableRow= "<tr name='baseDnRow'>"+
                "<td><input class='form-control' name='ldapBaseDns["+i+"].searchBaseDn'  type='text'></td>"+
                "<td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>"+
                "</tr>";
        $(tableRow).appendTo('#ldapBaseDnTable');
        i++;
    }
</script>