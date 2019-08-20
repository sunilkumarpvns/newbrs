<#--
/*Author : Raj Kirpalsinh
 *This template is used to create closing tag's for the datatable's heading tag's .
 *and it also inject respected data into the respective columns of the datatable.   
 */
-->


</tr>
</thead>
</table> 

<script type="text/javascript">
	var tableId  = '${parameters.id?default("")?html}';
	var beanPropertyArray = [];	
	var mData = "mData";
	var dataHref = "dataHref";
	var mRender = "mRender";
	var bSortable = "bSortable";	
	var columnData = "";
	<#--
	Maintaining individual styling in an JSON object against individual table id  so that while applying same on td
	this will be maintained.  Style will be fetch against table id while applying.
	-->
	var tableStyleArray;
	if(typeof tableStyleArray == "undefined"){
     tableStyleArray = {};
	}
	
	var tdStyleArray = [];
	
	$('#'+tableId).attr('subtableurl','${parameters.subTableUrl?default("")?html}');
	
	$('#'+tableId+' tr').each(function() {
		
    	$.each(this.cells, function(){     
    				  
    		var titleData 	= $(this).html();    		
    		var hrefData	= $(this).attr('hrefurl');
    		var sortData 	= $(this).attr('sortable');
    		var iconSpan 	= $(this).attr('icon');
    		var hrefLength 	= $.trim(hrefData).length;
    		var tdStyle 	= $(this).attr('tdStyle');
    		var tdCssClass 	= $(this).attr('tdCssClass');
    		if(isNullOrEmpty(tdCssClass)){
    		    tdCssClass = 'word-break';
			}
			else{
    		    tdCssClass+=" word-break";
			}
    		var beanPropertyVal 	= $(this).attr('id');
    		
    		var hiddenElementVal = $(this).attr('hiddenElement');
    		var datatype = $(this).attr('type');
    		var format = $(this).attr('format');
    		var disableWhen = $(this).attr('disableWhen');
    		var renderFunction = $(this).attr('renderFunction');
    		
    		if(isNullOrEmpty(tdStyle)==false){    							
				tdStyleArray.push(tdStyle)				
			}else{
				tdStyleArray.push("");
			}
			
    		var hrefArray ;
    		if($.trim(titleData).length == 0){
    			hrefArray = hrefData.split(":");
    			titleData = hrefArray[0];
    			hrefData = hrefArray[1];
    			hrefLength = $.trim(hrefData).length;
    		}
    		    		
    		var columnNames = this.id;
    		columnNames = columnNames.toString();
    				
    		columnData = {	
    			mData : columnNames,  
    			"mRender":function(data, type, thisBean){
                    if(isNullOrEmpty(format)==false && datatype=="date"){
                        if(isNullOrEmpty(data) == true){
                            return "";
                        }
                        data = data.replace(/T/g," ");
                        var newDate = new Date(data);
                        var sdf = new SimpleDateFormat(format);
                        return sdf.format(newDate);
                    }else if(datatype == "map" ){
                    	return hiddenElementVal + " "+parseColumnNamesOfMap(columnNames,thisBean);
                    }
    				return hiddenElementVal + " "+parseColumnNames(data,columnNames,thisBean);
    			}
    		};
    		if(isNullOrEmpty(tdCssClass)==false){    							
    		    columnData["sClass"]=$.trim(tdCssClass);
			}
    	
    		if(sortData=="true"){
    			columnData [ bSortable] = true;
    		}else{
    			columnData [ bSortable] = false;
    		}
		
             if(isNullOrEmpty(renderFunction) == false){
              columnData [mRender] = eval(renderFunction);
			 }else if( beanPropertyVal == 'dataTable.RowNumber' ){
				var orderNumberCounter = 0;
    			columnData [ mRender] = function ( displayData, type, thisBean ) {
    					return ++orderNumberCounter;
    			}
			} else if(titleData.indexOf("checkbox")!=-1){
    			columnData [ mRender] = function ( displayData, type, thisBean ) {
    					 
    					if(disableWhen!=null && disableWhen!='undefined' && disableWhen!=""){
    						var parseWhenFlag = parseDisableWhen(thisBean,disableWhen);
    						if(parseWhenFlag==true){
    							return '<input type="checkbox" name="inactiveBox" disabled="disabled" value="" />';
    						}
    					}	 
    					return hiddenElementVal +" "+getGroupNameHiddenElement(thisBean)+" "+'<input type="checkbox" name="ids" value="'+displayData+'" />';
    			}     			
    		}else  if(tableId.indexOf("import") != -1 && titleData.indexOf("Name")!=-1){
                columnData [ mRender] = function ( displayData, type, thisBean ) {
                        return displayData + " " +'<input type="hidden" name="names" value="'+displayData+'" />';
                }
            }else if(titleData.indexOf("Mode")!=-1){
    			columnData [ mRender] = function ( displayData, type, thisBean ) {
    			        if(isNullOrEmpty(displayData) == false){
                            return displayData + hiddenElementVal +" "+'<input type="hidden" name="modes" value="'+displayData+'" />';
                        }
    			}
    		}else{
	    		if (hrefLength > 0){  	    			  	    				    			      	
			        columnData [ mRender ] = function ( displayData, type, thisBean ) {	     
	    					titleData = $.trim(titleData);
	    					if(titleData.toLowerCase() == "edit" || titleData.toLowerCase() == "delete"){
	    			 			return parseFunction(hrefArray,thisBean,iconSpan,titleData, disableWhen);    							                   	                  		                    	
	    					} else {
	    						var displayData = parseColumnNames(displayData,columnNames,thisBean);				    				             
	    						return hiddenElementVal +" "+parseHref(hrefData,thisBean,displayData,titleData, disableWhen);    							                   	                  		                    	
		                    }
	        		}
	    		}else if(hrefLength == 0 && $.trim(iconSpan).length>0){
	    		   columnData [ mRender ] = function ( displayData, type, thisBean ) {
	    				return hiddenElementVal+" "+iconSpan;
	    			}
	    		}else if($.trim(iconSpan).length>0){
	    		 	columnData [ mRender ] = function ( displayData, type, thisBean ) {
	    				return hiddenElementVal +" "+'<a href="#">'+iconSpan+'</a>';
	    			}   
	    		}
    		}    		    		    		    		    		  		 
    		beanPropertyArray.push(columnData);
    	});
	});
	
    tableStyleArray[tableId]=tdStyleArray;
	
	/*
	This Function is used to parse the map fields.
	example: beanProperty = "map[key]"
	created by :Dhyani.Raval
	*/
	function parseColumnNamesOfMap(columnNames,thisBean){
		var regExp = /\[(.*?)\]/g;
		var colVals = "";
		var matches = regExp.exec(columnNames);
		if(isNullOrEmpty(matches) == false){
			var length = columnNames.indexOf('[');
			var mapName = columnNames.substring(0,length);
			var obj = thisBean[mapName];
			if (obj.hasOwnProperty(matches[1])) {
				colVals = obj[matches[1]];
			}
		}
		if(isNullOrEmpty(colVals)){
			colVals = "";
		}
		return colVals;
	}
	 
	function parseColumnNames(data,columnNames,thisBean){
		if(isNullOrEmpty(data)){
			data = "";
		}
		if(columnNames.indexOf(",")!=-1){		
			var columnNamesArray = columnNames.split(",");	
	    	var colVals = "";			    		    	
	    	for(var i=0; i<columnNamesArray.length; i++){	    	
	    		if(columnNamesArray[i].indexOf(".")!=-1){
	    			var tempArray = columnNamesArray[i].split(".");	
	    			var tempThisBean =  thisBean;
	    			for(var k=0; k<tempArray.length; k++){
	    				var childObject = tempThisBean[tempArray[k]];
	    				tempThisBean = childObject; 
	    			}    				
	    			colVals += childObject + " ";				    					
	    		}
	    			    				    			    			    	
	    		if(thisBean[columnNamesArray[i]]!=null || $.trim(thisBean[columnNamesArray[i]]).length>0){
	    			colVals += thisBean[columnNamesArray[i].toString()] +" ";
	    		}
	    	}  	    	
    		return colVals;    		
    	}else{
    		return data;
    	} 	
	} 
	 
	function parseFunction(hrefArray, thisBean, displayData, titleData, disableWhen){
		
    				
    	var hrefData = hrefArray[1];		
		if(hrefData=="javascript"){
			var lookupFunction = hrefArray[2];
			console.log("Lookup Function Value : " + lookupFunction);
			var functionName = lookupFunction.split("(");
			var parameter = functionName[1].substring(0, functionName[1].length - 1);
			lookupFunction = functionName[0] + "('"+ thisBean[parameter] +"')";
			
			return '<a onclick="'+lookupFunction+'" href="#">'+displayData+'</a>';
		}else{
			return parseHref(hrefData, thisBean, displayData,titleData, disableWhen);
		}
	} 

	function parseObject(paramValue,thisBean){
	    var tempArray = paramValue.split(".");	
	    var tempThisBean =  thisBean;
	    for(var k=0; k<tempArray.length; k++){
	    	var childObject = tempThisBean[tempArray[k]];
	    	tempThisBean = childObject; 
	    }    				
	    return childObject;				    					
	}

	function getParamsOnly(paramsOnly,thisBean) {
        var restParams = "";
            var indexOfAmph = paramsOnly.indexOf('&');
            if (indexOfAmph == -1) {

                /* This block will execute when there is only one parameter passed in query string. */

                var splittedByEqual = paramsOnly.split('=');

                if (splittedByEqual[1].indexOf(".") != -1) {

                    /* This block will execute when there is nested object's value passed in query string */

                    restParams = splittedByEqual[0] + "=" + encodeURIComponent(parseObject(splittedByEqual[1], thisBean));

                } else {
                    var paramValue = encodeURIComponent(thisBean[splittedByEqual[1]]);

                    if (isNullOrEmpty(paramValue)) {

                        /* This block will execute when there is any boolean value or any other hardcoded string passed in query string*/

                        restParams = splittedByEqual[0] + '=' + splittedByEqual[1];
                    } else {
                        restParams = splittedByEqual[0] + '=' + paramValue;
                    }
                }
                return restParams;

            } else {

                /* This block will execute when there are multiple parameters passed in query string. */

                var lookupRestParams = paramsOnly.split('&');
                for (var i = 0; i < lookupRestParams.length; i++) {

                    var splittedByEqual = lookupRestParams[i].split('=');
                    if (splittedByEqual[1].indexOf('[') != -1) {

                        /* This block will execute when there is map property passed in query string */

                        restParams += '&' + splittedByEqual[0] + '=' + encodeURIComponent(parseColumnNamesOfMap(splittedByEqual[1], thisBean));

                    } else if (splittedByEqual[1].indexOf(".") != -1) {

                        /* This block will execute when there is nested object's value passed in query string */
                        restParams += '&' + splittedByEqual[0] + '=' + encodeURIComponent(parseObject(splittedByEqual[1], thisBean));

                    } else {
                        var paramValue = encodeURIComponent(thisBean[splittedByEqual[1]]);
                        if (isNullOrEmpty(paramValue)) {

                            /* This block will execute when there is any boolean value or any other hardcoded string passed in query string*/

                            restParams += '&' + splittedByEqual[0] + '=' + splittedByEqual[1];

                        } else {
                            restParams += '&' + splittedByEqual[0] + '=' + paramValue;
                        }
                    }
                }
                return restParams;
            }
	}

	function parseHref (hrefData, thisBean, displayData,titleData, disableWhen) {	
			if(hrefData.indexOf("$<") !=-1){
				var urlData = hrefData.split("/");
                $.each(urlData, function( index ,value) {
					if(value.startsWith("$<")){
                        value = value.substr(value.indexOf("$<"),value.indexOf(">")+1);
						var beanProperty = value;
                        value = value.replace("$<","");
                        value = value.replace(">","");
                        var beanValue = thisBean[value];
						hrefData = hrefData.replace(beanProperty,beanValue);
                    }
                });
			}

			var hrefDataArray = hrefData.split("?");
        	var tempHrefVal = hrefDataArray[0];
        	var onlyParams;
        	if(hrefDataArray[1] != undefined) {
                onlyParams = getParamsOnly(hrefDataArray[1], thisBean);
                if (onlyParams.charAt(0) === '&') {
                    onlyParams = onlyParams.substr(1);
                }
            }

			var temp = tempHrefVal;
			if(onlyParams != undefined){
				temp = temp + "?" + onlyParams;
			}
        if(disableWhen!=null && disableWhen!='undefined' && disableWhen!=""){
    			var parseWhenFlag = parseDisable(thisBean,disableWhen);

    			if(parseWhenFlag==true){
    			return displayData;
    			}
    		}
            
            if(titleData=="delete" || titleData =="export"){
				var groups = thisBean["groups"];
                var encodedURIValue ="";
             	if( typeof thisBean["groups"] != "undefined" || thisBean["groups"]!=null ){

                    if (onlyParams.indexOf('&') != -1) {
						var splitByAmpercent = onlyParams.split("&");
                        $.each(splitByAmpercent, function( index, value ) {
							var splitByEqual = this.split("=");
							if(splitByEqual != null && splitByEqual != "") {
								if(splitByEqual[0] == "ids")
                                    encodedURIValue = splitByEqual;
							}
                        });


                    } else {
                        encodedURIValue =  onlyParams.split("=");
					}

             	   if(encodedURIValue != null && encodedURIValue!= "") {
                       return  "deleteUrl:"+temp +"&"+encodedURIValue[1]+"="+thisBean["groups"];
             	   } else {
					   return  "deleteUrl:"+temp;
             	   }

            	}else{
            		return  "deleteUrl:"+temp;
            	}
           }else{
            	return  '<a href="'+temp+'" >'+displayData+'</a>';
           }            
	}	 
			
	function getGroupNameHiddenElement(thisBean){
		var groupNameProperty = "groups";
		var identityProperty = "id";		
		return '<input type="hidden" name="'+thisBean[identityProperty]+'" value="'+thisBean[groupNameProperty]+'" />';    	
	}

    function parseDisable(bean, disableWhen) {
        if (disableWhen.indexOf("||") == -1) {
            return parseDisableWhen(bean, disableWhen);
        }
        var disableWhenArray = disableWhen.split("||");
        var disable = false;
        for (var i = 0; i < disableWhenArray.length; i++) {
            var expression=disableWhenArray[i];
            if(isNullOrEmpty(expression) == false){
                var expression = expression.trim();
			}
            var parsingResult = parseDisableWhen(bean, expression);
            disable = disable || parsingResult;
        }
        return disable;
    }
	function parseDisableWhen(thisBean,disableWhen){
		var disableWhenArray = null;
		
		if(disableWhen.indexOf("==")!=-1){
			disableWhenArray = disableWhen.split("==");
					
		} else if(disableWhen.indexOf("<=")!=-1){
			disableWhenArray = disableWhen.split("<=");
			
		} else if(disableWhen.indexOf(">=")!=-1){
			disableWhenArray = disableWhen.split(">=");
			
		} else if(disableWhen.indexOf("!=")!=-1){
			disableWhenArray = disableWhen.split("!=");
			
		} else if(disableWhen.indexOf(">")!=-1){
			disableWhenArray = disableWhen.split(">");
			
		} else if(disableWhen.indexOf("<")!=-1){
			disableWhenArray = disableWhen.split("<");
		} 
		
		if(disableWhenArray!=null && disableWhenArray.length==2){
	    	var disableWhenPropertyName = disableWhenArray[0];
			var disableWhenPropertyValue = disableWhenArray[1];
	    	var propertyValueInBean = thisBean[disableWhenPropertyName];	
			if(propertyValueInBean == disableWhenPropertyValue){
				return true;
			}   					       				
		}
		return false;
	} 
	
	makeDataTable( tableId, beanPropertyArray,'${parameters.actionUrl?default("")?html}','${parameters.beanType?default("")?html}','${parameters.dataListName?default("dataList")?html}','${parameters.list?default("")}','${parameters.showPagination?default("true")}',
	'${parameters.showInfo?default("true")}','${parameters.subTableUrl?default("")?html}',${parameters.rows?default("")?html},${parameters.pages?default("")?html},'${parameters.showFilter?default("false")}', '${parameters.emptyTable?default("No records found")}');
</script>