<%@taglib uri="/struts-tags/ec" prefix="s" %>

<script type="text/javascript">

var id,
categories = ['Usage', 'Balance'],
colors = ['#4863A0','#7B96D3'],
details = [{
    y: 0,
    color: colors[0],
    name: categories[0]
},{
    y: 0,
    color: colors[1],
    name: categories[1]
}],
options = {
	  chart: {
   	  	  renderTo: null,
          type: 'pie',
          backgroundColor:'transparent'
      },
      legend: {
          enabled: false
      },
      credits: {
          enabled: false
      },
      title: {
          text: '<s:property value="%{@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@VOLUME.getValue()}" /> Balance',
        style: {"fontSize": "12px"}

      },
      plotOptions: {
          pie: {
              shadow: false,
              center: ['100%', '100%']
          }
      },
      plotOptions: {
          pie: {
              allowPointSelect: true,
              cursor: 'pointer',
              center: ["50%", "50%"],
              dataLabels: {
                  enabled: true
              },
              showInLegend: true
          }
      },tooltip: {
    	  style: {
    			fontSize: '10px',
    		},
    	    formatter: function() {
                if(options.chargingType == '<s:property value="%{@com.elitecore.corenetvertex.pkg.ChargingType@EVENT.name()}" />'){
                    return  "<b> " + this.point.name  + "</b> : " + this.y;
				}else if(options.type=='<s:property value="%{@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@TIME.getValue()}" />'){
                    return  "<b> " + this.point.name  + "</b> : " + convertTimeToSuitableUnit(this.y);
                } else {
                    return  "<b> " + this.point.name  + "</b> : " + convertToSuitableUnit(this.y);
                }
    	    }
      },
      series: [ {
      	name: 'Data',
          data: details,
          size: '100%',
          innerSize: '50%',
          dataLabels: {
        	  style: {
      			fontSize: '10px',
      		},
              formatter: function () {
                  if(options.chargingType == '<s:property value="%{@com.elitecore.corenetvertex.pkg.ChargingType@EVENT.name()}" />'){
                      return  "<b> " + this.point.name  + "</b> : " + this.y;
                  }else if(options.type=='<s:property value="%{@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@TIME.getValue()}" />'){
                      return '<b>' + this.point.name + ': </b> ' + convertTimeToSuitableUnit(this.y);
				  } else {
                      return '<b>' + this.point.name + ': </b> ' + convertToSuitableUnit(this.y);
				  }

              }
          }
      }]
};

function convertToSuitableUnit(point) {
    var KB = 1024;
    var MB = KB*1024;
    var GB = MB*1024;

    var value = "";
    if(point >= GB){
        value = (point/GB).toFixed(2) + " GB";
    }else if(point >= MB){
        value = (point/MB).toFixed(2) + " MB";
    }else if(point >= KB){
        value = (point/KB).toFixed(2) + " KB";
    }else{
        value = point.toFixed(2) + " Bytes";
    }
    return value;
}

function convertTimeToSuitableUnit(point) {

    if('${subscriptionInformation.packageType}' == "BASE"){

        var Minute = 60;
        var Hour = Minute*60;
        var Day = Hour*24;

        var value = "";
        if(point >= Day){
            value = (point/Day).toFixed(2) + " Day";
        }else if(point >= Hour){
            value = (point/Hour).toFixed(2) + " Hour";
        }else if(point >= Minute){
            value = (point/Minute).toFixed(2) + " Minute";
        }else{
            value = point.toFixed(2) + " Second";
        }
        return value;
    }

    if('${subscriptionInformation.packageType}' == '<s:property value="%{@com.elitecore.corenetvertex.constants.CommonConstants@RNC}"/>'){
        return convertTimeToHHMMSS(point);
    }
}

function convertTimeToHHMMSS(secondsCount) {
    if(secondsCount >= Number.MAX_SAFE_INTEGER){
        return '<s:property value="%{@com.elitecore.corenetvertex.constants.CommonConstants@UNLIMITED}"/>';
	}else{

        var hour, minute, second;
        //Calculate the seconds to display:
        var seconds = secondsCount % 60;
        secondsCount -= seconds;
        //Calculate the minutes:
        var minutesCount = secondsCount / 60;
        var minutes = minutesCount % 60;
        minutesCount -= minutes;
        //Calculate the hours:
        var hoursCount = minutesCount / 60;

        if (hoursCount < 10) {
            hour = "0" + hoursCount;
        }
        else {
            hour = "" + hoursCount;
        }

        if (minutes < 10) {
            minute = "0" + minutes;
        }
        else {
            minute = "" + minutes;
        }

        if (seconds < 10) {
            second = "0" + seconds;
        }
        else {
            second = "" + seconds;
        }

        return hour + " : " + minute + " : " + second;
    }
}


function drawChart(totalUsage, usageLimit, id, type){
	options.chart.renderTo = id;
	var totalUsageInt = parseFloat(totalUsage);
	var usageLimitInt = parseFloat(usageLimit);
	var balance = 0;
	if(totalUsageInt < usageLimitInt){
		balance = usageLimitInt - totalUsageInt;
	}
	details[0].y = totalUsageInt;
	details[1].y = balance;
    options.type = type;

    if(type== '<s:property value="%{@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@TIME.getValue()}" />'){
        options.title.text = '<s:property value="%{@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@TIME.getValue()}" /> Balance';
	}

	var chart = new Highcharts.Chart(options);
	chart.redraw();
}

    function drawRncChart(totalTime, availableTime, id, type, chargingType){
        options.chart.renderTo = id;
        var totalTimeInt = parseFloat(totalTime);
        var availableTimeInt = parseFloat(availableTime);
        var usage = 0;
        if(availableTimeInt < totalTimeInt) {
            usage = totalTimeInt - availableTimeInt;
        }
        details[0].y = usage;
        details[1].y = availableTimeInt;
        options.type = type;
        options.chargingType = chargingType;

        if(type== '<s:property value="%{@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@TIME.getValue()}" />'){
            options.title.text = '<s:property value="%{@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@TIME.getValue()}" /> Balance';
        }

        if(totalTimeInt < Number.MAX_SAFE_INTEGER){
            var chart = new Highcharts.Chart(options);
            chart.redraw();
        }
    }

    $(document).ready(function(){
    	if('${subscriptionInformation.packageType}' == '<s:property value="%{@com.elitecore.corenetvertex.constants.CommonConstants@RNC}"/>'){
            var containers_rnc = document.getElementsByName('container:${subscriptionInformation.packageName}_${subscriptionInformation.packageType}');
            for(var i=0; i<containers_rnc.length; i++){
                var id = containers_rnc[i].id;
                var pkgId = id.split(":")[0];
                var totalUsage = id.split(":")[1];
                var usageLimit = id.split(":")[2];
                var type = id.split(":")[3];
                var chargingType = id.split(":")[4];
                drawRncChart(totalUsage, usageLimit, id, type, chargingType);
            }
        }else{
            var containers = document.getElementsByName('container:${subscriptionInformation.addonSubscriptionId}');
            for(var i=0; i<containers.length; i++){
                var id = containers[i].id;
                var totalUsage = id.split(":")[2];
                var usageLimit = id.split(":")[3];
                var type = id.split(":")[4];
                drawChart(totalUsage, usageLimit, id, type);
            }
        }
    });

</script>
<s:if test="subscriptionInformation == null">
	<div style="text-align: center; line-height: 2em;"><s:property value="errorMessage"/></div>
</s:if>
<s:if test="subscriptionInformation.packageType == BASE && subscriptionInformation.quotaProfileBalances.size == 0">
	<s:if test="errorMessage == null">
		<div style="text-align: center; line-height: 2em;">No Usage Information Available</div>
	</s:if>
</s:if>
<s:else>
<s:iterator var="quotaProfile" value="subscriptionInformation.quotaProfileBalances" >
    <div>
         <fieldset class="fieldSet-line">
				<legend align="top" style="font-size: 12px;">
       			<s:text name="quotaProfileName"></s:text>
       		</legend>
       		<div>
       			<div class="col-sm-7">
       				<table class="table table-blue-stripped table-condensed table-bordered">
						<thead>
						<tr>
							<th colspan="5" style="text-align: center;"><s:text name="subscription.usage.information"></s:text></th>
						</tr>
						<tr>
							<th><s:text name="subscription.service"/></th>
							<th><s:text name="subscription.aggregationkey"/></th>
							<th><s:text name="subscription.balanceLevel"/></th>
							<th style="text-align: right"><s:text name="subscription.usage"/></th>
							<th style="text-align: right"><s:text name="subscription.usagelimit"/></th>
						</tr>
						</thead>
						<tbody>
							<s:if test="allServiceBalance != null">
							<tr>
								<td style="vertical-align: middle;"><a href="${pageContext.request.contextPath}/policydesigner/dataservicetype/DataServiceType/view?serviceTypeId=<s:property value="allServiceBalance.serviceId" />"> <s:property value="allServiceBalance.serviceName" /></a></td>
								<td style="vertical-align: middle;"><s:property value="allServiceBalance.aggregationKey.val"/></td>
								<td style="vertical-align: middle;"><s:property value="allServiceBalance.balanceLevelStr"/></td>
								<s:set var="currentUsage" value="allServiceBalance.usage"></s:set>
								<td style="text-align: right">${currentUsage}</td>
								<s:set var="usageLimit" value="allServiceBalance.usageLimitStr"></s:set>
								<td style="text-align: right">${usageLimit}</td>
							</tr>
							</s:if>
 							<s:iterator value="balanceInfos" var="balanceInfo">
 							<tr>
								<td style="vertical-align: middle;"><a href="${pageContext.request.contextPath}/policydesigner/dataservicetype/DataServiceType/view?serviceTypeId=<s:property value="serviceId" />"><s:property value="serviceName" /></a></td>
							<td style="vertical-align: middle;"><s:property value="aggregationKey.val"/></td>
							<td style="vertical-align: middle;"><s:property value="balanceLevelStr"/></td>
							<s:set var="currentUsage" value="usage"></s:set>
							<td style="text-align: right">${currentUsage}</td>
							<s:set var="usageLimit" value="usageLimitStr"></s:set>
							<td style="text-align: right">${usageLimit}</td>
						</tr>
							</s:iterator>
 						</tbody>
					</table>
				</div>

				<s:if test="%{allServiceBalance.usageLimit >= 0 && allServiceBalance.quotaUsageType == @com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@VOLUME.getValue() || allServiceBalance.quotaUsageType == @com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@HYBRID.getValue()}">
					<div class="col-sm-5">
						<div id="${subscriptionInformation.addonSubscriptionId}:${quotaProfileId}:${allServiceBalance.totalOctets}:${allServiceBalance.usageLimit}:<s:property value="%{@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@VOLUME.getValue()}" />" name="container:${subscriptionInformation.addonSubscriptionId}" class="col-xs-12" style="height: 170px"></div>
					</div>
				</s:if>
				<s:if test="%{allServiceBalance.timeLimit >= 0 && allServiceBalance.quotaUsageType == @com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@TIME.getValue()}">
					<div class="col-sm-5">
							<div id="${subscriptionInformation.addonSubscriptionId}:${quotaProfileId}:${allServiceBalance.time}:${allServiceBalance.timeLimit}:<s:property value="%{@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@TIME.getValue()}" />" name="container:${subscriptionInformation.addonSubscriptionId}" class="col-xs-12" style="height: 170px"></div>
					</div>
				</s:if>
       		</div>
       	</fieldset>
    </div>
</s:iterator>
</s:else>


<s:if test="subscriptionInformation.packageType == RNC && subscriptionInformation.rncBalances.size == 0">
	<s:if test="errorMessage == null">
		<div style="text-align: center; line-height: 2em;">No Usage Information Available</div>
	</s:if>
</s:if>
<s:else>
	<s:iterator var="rncBalance" value="subscriptionInformation.rncBalances" >
		<div>
			<fieldset class="fieldSet-line">
				<legend align="top" style="font-size: 12px;">
					<s:text name="ratecardName"></s:text>
				</legend>
				<div>
					<div class="col-sm-7">
						<table class="table table-blue-stripped table-condensed table-bordered">
							<thead>
							<tr>
								<s:if test="%{@com.elitecore.corenetvertex.pkg.ChargingType@EVENT.name() == subscriptionInformation.chargingType}" >
									<th colspan="5" style="text-align: center;"><s:text name="subscription.event.information_rnc"></s:text></th>
								</s:if>
								<s:else>
									<th colspan="5" style="text-align: center;"><s:text name="subscription.usage.information_rnc"></s:text></th>
								</s:else>
							</tr>
							<tr>
								<th><s:text name="subscription.aggregationkey"/></th>
								<th style="text-align: right"><s:text name="subscription.usage"/></th>
								<th style="text-align: right"><s:text name="subscription.usagelimit"/></th>
                                <th style="text-align: right"><s:text name="subscription.balance"/></th>
							</tr>
							</thead>
							<tbody>
							<tr>
								<td style="vertical-align: middle;"><s:text name="subscription.billing.cycle"/></td>
								<td style="vertical-align: middle;text-align: right"><s:property value="displayUsageTime"/></td>
								<td style="vertical-align: middle;text-align: right"><s:property value="displayTotalTime"/></td>
                                <td style="vertical-align: middle;text-align: right"><s:property value="displayAvailableTime"/></td>
							</tr>
							</tbody>
						</table>
					</div>
					<div class="col-sm-5">
						<div id="${id}:${billingCycleTotalTime}:${billingCycleAvailableTime}:<s:property value="%{@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@TIME.getValue()}" />:${subscriptionInformation.chargingType}" name="container:${subscriptionInformation.packageName}_${subscriptionInformation.packageType}" class="col-xs-12" style="height: 170px"></div>
					</div>
				</div>
			</fieldset>
		</div>
	</s:iterator>
</s:else>


