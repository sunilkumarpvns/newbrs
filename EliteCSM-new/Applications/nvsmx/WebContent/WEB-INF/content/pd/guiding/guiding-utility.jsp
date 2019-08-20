<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<%--
  User: Ajay pandey
  Date: 28/12/17
--%>
<script type="text/javascript">

function setPartnerName(){
	var accId=$('#accountNumberId').val();
	$.ajax({
		async : true,
		type : "POST",
		dataType: "text",
		url:"${pageContext.request.contextPath}/policydesigner/util/AjaxUtility/findPartnerNameById",
		data : { "id": accId},
	    success: function(data){
	    	$('#partnerNameId').text(data);
	    },fail:function(){
	    	console.log("call fail");
	    	}
		});
	}

	
function autoCompleteForTrafficType(){
		var list = ${TrafficTypeListAsJson};
			$('#trafficTypeId').autocomplete();
			commonAutoComplete("trafficTypeId",list);
			}; 	
			
			
	
function dateValidate(startDateId, endDateId){

	    var startDate = $(startDateId).val();
	    var endDate = $(endDateId).val();
	    
	    var updatdEndDate = [];
	    updatedEndDate= endDate.split("-");
	    
	    var updatedStartDate = [];
	    updatedStartDate= startDate.split("-");
	    
	    var sDate = updatedStartDate[1]+" "+updatedStartDate[0]+", "+updatedStartDate[2];
	    sDate = Date.parse(sDate);
	    
	    var eDate = updatedEndDate[1]+" "+updatedEndDate[0]+", "+updatedEndDate[2];
	    eDate = Date.parse(eDate);
	   
	    if (sDate >= eDate) {
	        setError('endDateId',"<s:text name='guiding.enddate.mustbe.greaterthan.startdate'/>");
	        return false;
	    }
	    return true;
	}	 
	
	
function validateForm(mode, id){
	  var isValidDate= dateValidate('#startDateId','#endDateId');
	  var isValidName = verifyUniquenessOnSubmit('guidingName',mode, id,'com.elitecore.corenetvertex.pd.guiding.GuidingData','','guidingName');
	  if (isValidName == false) {
       return false;
   }else if(isValidDate == false){
 	  return false
   }
   return true;
} 
	
</script>
