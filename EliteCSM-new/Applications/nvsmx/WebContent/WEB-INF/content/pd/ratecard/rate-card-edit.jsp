<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<%@include file="/WEB-INF/content/pd/ratecard/RateCard.jsp"%>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="ratecard.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/ratecard" action="rate-card" id="rateCardEdit" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validate('edit','%{id}')">
            <s:hidden name="_method" value="put" />
            <s:token/>
            <s:hidden name="rncPackageId"    value="%{rncPackageId}" />
          <div>
              <div class="col-sm-12 col-sm-6">
                  <s:textfield name="name" key="ratecard.name" id="ratecardName" cssClass="form-control focusElement" maxlength="50" tabindex="1"/>
                  <s:textarea name="description" key="ratecard.description" id="ratecardDescription" cssClass="form-control" rows="2" tabindex="2" maxlength="2000"/>
                 <%--  <s:select name="rateFileFormatDataId" key="ratecard.rateUploadFormat" id="rateUploadFormat" cssClass="form-control" list="rateFileFormatDataList" listKey="id" listValue="getName()" tabindex="3" /> --%>
               </div>
               <div class="col-sm-12 col-sm-6">
              	  <s:select name="rateUom" key="ratecard.rateUom" id="ratecardrateUom" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.Uom@values()" tabindex="5" />
              	  <s:select name="pulseUom" key="ratecard.pulseUom" id="ratecardpulseUom" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.Uom@values()" tabindex="6" />
              	  <s:textfield name="labelKey1" key="ratecard.labelOne" id="ratecardLabelOne" cssClass="form-control focusElement" maxlength="100" tabindex="7" onblur=" updateLabelsOnBlur('.labelKeyOne', '#ratecardLabelOne');"/>
	              <s:textfield name="labelKey2" key="ratecard.labelTwo" id="ratecardLabelTwo" cssClass="form-control focusElement" maxlength="100" tabindex="8" onblur=" updateLabelsOnBlur('.labelKeyTwo', '#ratecardLabelTwo');"/> 
	          </div>
              	
            <div id="versionHeaderDiv">
                    <div class="col-xs-12 col-sm-12">
                        <table id='ratecardVersions'  class="table table-blue table-bordered">
                            <caption class="caption-header">
                                <s:text name="ratecard.version" />
                                <div align="right" class="display-btn">
	                              <span class="btn btn-group btn-group-xs defaultBtn" onclick="addVersionData();" id="addRow"> <span class="glyphicon glyphicon-plus"></span></span>
                                </div>
                            </caption>
                            <tbody>
								<tr>
									<td>
										<div id="versionDivDetails">
											<table id='versionDetailTbl' class="col-xs-12 col-sm-12" style="border:0px;">
											</table>
										</div>
									</td>
								</tr>
							</tbody>
                        </table>
                        <div class="col-xs-12" id="generalError"></div>
                    </div>
                </div>
             
               <div class="row">
                   <div class="col-xs-12" align="center" >
                      <button type="submit" class="btn btn-primary btn-sm" id="btnSubmit" role="submit" formaction="${pageContext.request.contextPath}/pd/ratecard/rate-card/${id}" tabindex="5" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                      <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/ratecard/rate-card/${id}'" tabindex="21" ><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.back"/></button>
                      
                   </div>
               </div>
           </div>
	        </s:form>
	   </div
	</div>
	
	<%@include file="RateCardTemplate.jsp"%>
	
	<script type="text/javascript">
	$(".fromDates").datepicker();
	
	
	
	
	initializeFromDate();
	
	$(document).ready(function(){
		autoCompleteForLabelKey();
		
	});
	
	 var rateCardJson = '<s:text name="rateCardAsJson"/>';
	 generateRateCardTable(JSON.parse(rateCardJson));
	</script>
