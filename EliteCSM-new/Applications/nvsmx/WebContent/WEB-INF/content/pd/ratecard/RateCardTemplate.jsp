<%@taglib uri="/struts-tags/ec" prefix="s"%>
<style type="text/css">
    
    .label-bold{
    	font-weight: bold;
    	margin-bottom: 0 !important;
    }
</style>
	
	<div style="display: none;" id="templateId">
		 <div id="ratecardVersionHeader">
                  <table class="table table-blue table-bordered version-detail">
                      <caption class="caption-header" >
                        <span class="version-text col-xs-12 col-sm-8" style="float:left;">
                        	<h4 style="width:20%; float:left;margin-top: 1px;margin-bottom: 1px;"></h4> 
                          	<s:textfield type="hidden" name="versionid" cssClass="form-control focusElement version" maxlength="50"/>
                         </span>
                          <div align="right" style="float:right;"  class="btn-group btn-group-sm">
                              <span class="btn btn-group btn-group-xs defaultBtn" onclick="addVersionDetail(this);" id="addRow"> <span class="glyphicon glyphicon-plus"></span></span>
                          	  <span class='btn btn-group btn-group-xs defaultBtn' onclick='$(this).closest("tr[name=versionRow]").remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span>
                          </div>
                      </caption>
                      <thead>
                      		<th><label class="light-text label-bold labelKeyOne">${labelKey1}</label></th>
                      		<th><label class="light-text label-bold labelKeyTwo">${labelKey2}</label></th>
                            <th>
                            <s:text name="ratecard.version.slab"/>
                             <%-- <span class="btn btn-group btn-group-xs defaultBtn" style="float: right;" onclick="showRows(this,'firstSection');" id="firstSectionShow"> <span class="glyphicon glyphicon-plus"></span></span> --%>
                             <%-- <span class="btn btn-group btn-group-xs defaultBtn" style="float: right;display:none;" onclick="hideRows(this,'firstSection', false);" id="firstSectionHide"> <span class="glyphicon glyphicon-minus"></span></span> --%>
                            
                            </th>
                            <th>
                            <s:text name="ratecard.version.pulse"/>
                             <%-- <span class="btn btn-group btn-group-xs defaultBtn" style="float: right;" onclick="showRows(this,'secondSection');" id="secondSectionShow"> <span class="glyphicon glyphicon-plus"></span></span> --%>
                             <%-- <span class="btn btn-group btn-group-xs defaultBtn" style="float: right;display:none;" onclick="hideRows(this,'secondSection', false);" id="secondSectionHide"> <span class="glyphicon glyphicon-minus"></span></span> --%>
                            
                            </th>
                            <th>
                            <s:text name="ratecard.version.rate"/>
                            <span class="btn btn-group btn-group-xs defaultBtn" style="float: right;" onclick="showRows(this,'firstSection', 'secondSection','thirdSection');" id="thirdSectionShow"> <span class="glyphicon glyphicon-plus"></span></span>
                            <span class="btn btn-group btn-group-xs defaultBtn" style="float: right;display:none;" onclick="hideRows(this,'firstSection', 'secondSection','thirdSection',false);" id="thirdSectionHide"> <span class="glyphicon glyphicon-minus"></span></span>
                            
                            </th>
                            <th><s:text name="ratecard.version.tier.rate.type"/></th>
                            <th><s:text name="ratecard.version.fromdate"/></th>
                            <th style="width:35px;">&nbsp;</th>
                      </thead>
                  </table>
              </div>
		</div>
	
	<table id="tableTemplate" style="display: none;">
		<tr name="upRow">
			<td class="col-xs-12 col-sm-2"><s:textfield name="label1" elementCssClass="col-xs-12" cssClass="form-control focusElement labelOne" maxlength="100"/></td>
			<td class="col-xs-12 col-sm-2"><s:textfield name="label2" elementCssClass="col-xs-12" cssClass="form-control focusElement labelTwo" maxlength="100"/></td>
			<td>
				<table id="firstSection">
					<tr>
						<td style="border: 0px !important;"><s:textfield name="slab1" type="number" step="any" elementCssClass="col-xs-12" cssClass="form-control slabOne" onkeypress="return isNaturalInteger(event);"  maxlength="50"/></td>
					</tr>
					<tr>
						<td style="border: 0px !important;"><s:textfield name="slab2" type="number" step="any" elementCssClass="col-xs-12" cssClass="form-control slabTwo" onkeypress="return isNaturalInteger(event);" maxlength="50"/></td>
					</tr>
					<tr>
						<td style="border: 0px !important;"><s:textfield name="slab3" type="number" step="any" elementCssClass="col-xs-12"  cssClass="form-control slabThree" onkeypress="return isNaturalInteger(event);" maxlength="20"/></td>
					</tr>
				</table>
			</td>
			<td>
				<table id="secondSection">
					<tr>
						<td style="border: 0px !important;"><s:textfield name="pulse1" type="number" step="any" elementCssClass="col-xs-12" cssClass="form-control pulseOne" onkeypress="return isNaturalInteger(event);"  maxlength="50"/></td>
					</tr>
					<tr>
						<td style="border: 0px !important;"><s:textfield name="pulse2" type="number" step="any" elementCssClass="col-xs-12" cssClass="form-control pulseTwo" onkeypress="return isNaturalInteger(event);" maxlength="50"/></td>
					</tr>
					<tr>
						<td style="border: 0px !important;"><s:textfield name="pulse3" type="number" step="any" elementCssClass="col-xs-12" cssClass="form-control pulseThree" onkeypress="return isNaturalInteger(event);" maxlength="20"/></td>
					</tr>
				</table>
			</td>
			<td>
				<table id="thirdSection">
					<tr>
						<td style="border: 0px !important;"><s:textfield name="rate1" type="number" step="any" elementCssClass="col-xs-12" cssClass="form-control rateOne" onkeypress="return isValidDecimalRate(this,event);"  maxlength="50"/></td>
					</tr>
					<tr>
						<td style="border: 0px !important;"><s:textfield name="rate2" type="number" step="any" elementCssClass="col-xs-12" cssClass="form-control rateTwo" onkeypress="return isValidDecimalRate(this,event);"  maxlength="50"/></td>
					</tr>
					<tr>
						<td style="border: 0px !important;"><s:textfield name="rate3" type="number" step="any" elementCssClass="col-xs-12" cssClass="form-control rateThree" onkeypress="return isValidDecimalRate(this,event);" maxlength="20"/></td>
					</tr>
				</table>
			</td>
			<td class="col-xs-12 col-sm-2"><s:select name="tierRateType" value="" elementCssClass="col-xs-12" cssClass="form-control rateCardTierRateType" list="@com.elitecore.corenetvertex.constants.TierRateType@values()" id="rateCardTierRateType"  /></td>
            <td class="col-xs-12 col-sm-2"><input name="fromDate" value="" elementCssClass="col-xs-12"  class="fromDate form-control" maxlength="20" displayFormat="dd-M-yy" readonly="readonly"/>
           </td>
			<td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>
		</tr>
	</table>
