
<script>
function onCountryChange(){

	   var countryKey = "<s:property value='countryId'/>";	
	   var countryId = $("#countryId").val();
		    var data = ${countryDataList};
		    var optionsAsString = "";
		    for(var i in data) {
		        if(countryKey == data[i].id && (typeof(countryId) == 'undefined' || countryId == 'countryId')) {
		        	 optionsAsString += "<option selected ='selected' value='"+data[i].id+"'>" + data[i].name + "</option>";
		        }
		        else if((typeof(countryId) != 'undefined' || countryId != 'countryId') && countryId == data[i].id){
		        	optionsAsString += "<option selected ='selected' value='"+data[i].id+"'>" + data[i].name + "</option>";
		        }
		        else{
		        	 optionsAsString += "<option value='"+data[i].id+"'>" + data[i].name + "</option>";
		        }
		    }
		$("#countryId").find('option').remove();
	    $("#countryId").append(optionsAsString);
	    $( ".select2" ).select2();//need to recreate
		

	    
		var regionKey = "<s:property value ='regionId'/>";	
	    var countryId = $("#countryId").val();
	    $("#regionId").find('option').remove();
	    $("#cityId").find('option').remove();
	    var data = ${regionDataList};
	
	    var optionsAsString = "";
	    for(var i in data) {
	        if(typeof regionKey != 'undefined' && regionKey == data[i].id && countryId == data[i].countryData.id) {
	            optionsAsString += "<option  selected ='selected' value='"+data[i].id+"'>" + data[i].name + "</option>";
	        }
	        else if(countryId == data[i].countryData.id){
	        	 optionsAsString += "<option value='"+data[i].id+"'>" + data[i].name + "</option>";
	        }
	    }
	    $("#regionId").append(optionsAsString);
	    $( ".select2" ).select2();//need to recreate
	    
	    
	    var cityKey = "<s:property value='cityId'/>";
	    var stateId = $("#regionId").val();
	    $("#cityId").find('option').remove();
	
	    var data = ${cityDataList};
	
	    var optionsAsString = "";
	    for(var i in data) {
	    	if(typeof cityKey != 'undefined' && cityKey == data[i].id && stateId == data[i].regionData.id ){
	    		optionsAsString += "<option selected ='selected' value='"+data[i].id+"'>" + data[i].name + "</option>";
	    	}
	    	else if(stateId == data[i].regionData.id) {
	            optionsAsString += "<option value='"+data[i].id+"'>" + data[i].name + "</option>";
	        }
	    }
	    $("#cityId").append(optionsAsString);
	    $( ".select2" ).select2();//need to recreate
	
	};
	
	function onStateChange(cityId){

		var cityKey = "<s:property value='cityId'/>";
	    var stateId = $("#regionId").val();
	    $("#cityId").find('option').remove();
	
	    var data = ${cityDataList};
	
	    var optionsAsString = "";
	    for(var i in data) {
	    	if(typeof cityKey != 'undefined' && cityKey == data[i].id && stateId == data[i].regionData.id ){
	    		optionsAsString += "<option selected ='selected' value='"+data[i].id+"'>" + data[i].name + "</option>";
	    	}
	    	else if(stateId == data[i].regionData.id) {
	            optionsAsString += "<option value='"+data[i].id+"'>" + data[i].name + "</option>";
	        }
	    }
	    $("#cityId").append(optionsAsString);
	    $( ".select2" ).select2();//need to recreate
	
	};
	
function autoCompleteForStatus(){
	$('#status').autocomplete();
	var list = [ <s:iterator value="@com.elitecore.corenetvertex.constants.PartnerStatus@values()" > 
					'<s:property value="name"/>',
				</s:iterator> ]; 
	commonAutoComplete("status",list);
	};
</script>