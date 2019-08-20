
$.fn.dataTable.pipeline = function ( dataListName, opts ) {
    // Configuration options
    var conf = $.extend( {
        pages: 5,     // number of pages to cache
        url: '',      // script url
        data: null,   // function or object with parameters to send to the server
                      // matching how `ajax.data` works in DataTables
        method: 'GET' // Ajax HTTP method
    }, opts );

    // Private variables for storing the cache
    var cacheLower = -1;
    var cacheUpper = null;
    var cacheLastRequest = null;
    var cacheLastJson = null;
 
    return function ( request, drawCallback, settings ) {
        var ajax          = true;
        var requestStart  = request.start;
        var drawStart     = request.start;
        var requestLength = request.length;
        var requestEnd    = requestStart + requestLength;
		var pagesCached = conf.pages;
		if (pagesCached == undefined || pagesCached == 'undefined') {
			pagesCached = 0;
		}
		cacheUpper = requestStart + (requestLength * pagesCached);
         
        if ( settings.clearCache ) {
            // API requested that the cache be cleared
            ajax = true;
            settings.clearCache = false;
        }
        else if ( cacheLower < 0 || (pagesCached > 0 && requestStart <= cacheLower) || (pagesCached > 0 && requestEnd >= cacheUpper) ) {
            // outside cached data - need to make a request
            ajax = true;
        }
        else if ( JSON.stringify( request.order )   !== JSON.stringify( cacheLastRequest.order ) ||
                  JSON.stringify( request.columns ) !== JSON.stringify( cacheLastRequest.columns ) ||
                  JSON.stringify( request.search )  !== JSON.stringify( cacheLastRequest.search )
        ) {
            // properties changed (ordering, columns, searching)
            ajax = true;
        }
         
        // Store the request for checking next time around
        cacheLastRequest = $.extend( true, {}, request );
 
        if ( ajax ) {
            // Need data from the server
            if ( requestStart < cacheLower ) {
                requestStart = requestStart - (requestLength*(pagesCached-1));
 
                if ( requestStart < 0 ) {
                    requestStart = 0;
                }
            }
             
            cacheLower = requestStart;
            cacheUpper = requestStart + (requestLength * pagesCached);
 
            request.start = requestStart;
            request.length = requestLength*pagesCached;
            var dataArray = settings.aaSorting[0];
            if(dataArray!=null && dataArray.length>0){
            	request.sortColumnIndex = dataArray[0];
            	request.sortColumnOrder = dataArray[1];
            	var columnData = request.columns[dataArray[0]];
            	request.sortColumnName = columnData["data"];
            }
            // Provide the same `data` options as DataTables.
            if ( $.isFunction ( conf.data ) ) {
                // As a function it is executed with the data object as an arg
                // for manipulation. If an object is returned, it is used as the
                // data object to submit
                var d = conf.data( request );
                if ( d ) {
                    $.extend( request, d );
                }
            }
            else if ( $.isPlainObject( conf.data ) ) {
                // As an object, the data given extends the default
                $.extend( request, conf.data );
            }
 
            settings.jqXHR = $.ajax( {
                "type":     "POST",
                "url":      conf.url,
                "data":     request,
                "dataType": "json",
                "cache":    false,
                "success":  function ( json ) {
                	if(dataListName != undefined && dataListName != ''){
                		json["dataList"] = json[dataListName];
                	}
                	cacheLastJson = $.extend(true, {}, json);
                    if ( cacheLower != drawStart ) {
                        json.dataList.splice( 0, drawStart-cacheLower );
                    }
                    json.dataList.splice( requestLength, json.dataList.length );
                    drawCallback( json );
                }
            } );
        }
        else {
        	json = $.extend( true, {}, cacheLastJson );
            json.draw = request.draw; // Update the echo for each response
            json.dataList.splice( 0, requestStart-cacheLower );
            json.dataList.splice( requestLength, json.dataList.length );            
            drawCallback(json);
        }
    }
};

// Register an API method that will empty the pipelined data, forcing an Ajax
// fetch on the next draw (i.e. `table.clearPipeline().draw()`)
$.fn.dataTable.Api.register( 'clearPipeline()', function () {
    return this.iterator( 'table', function ( settings ) {
        settings.clearCache = true;
    } );
} );

/* Formatting function for row details - modify as you need */

var param;

if(typeof criteria == 'undefined'){
	var criteria;
}

function makeDataTable(tableId, columns, actionUrl, beanType, dataListName, list, showPagination, showInfo, subTableUrl, rows, pages, showFilter, emptyTable) {
	console.log("Table-Id	: "+tableId);
	console.log("Action-URL	: "+actionUrl);
	console.log("Columns	: "+columns);
	console.log("Bean Type	: "+beanType);
	console.log("Show Pagination	: "+showPagination);
	console.log("Show Info			: "+showInfo);
	console.log("SubTable Url 		: "+subTableUrl);
	console.log("rows 		: "+rows);
	console.log("Pages 		: "+pages);
	console.log("show Filter: "+showFilter);
	console.log("emptyTable : "+emptyTable);

	if(rows == 0) {
		rows = defaultRows;
	}

	var paginationFlag 	= true;
	var showInfoFlag 	= true;
	
	if(showInfo != null &&  showInfo == "false"){
		showInfoFlag = false;
	}

	var isParameterUrl = actionUrl.indexOf("&amp;");
	if(isParameterUrl != -1){
		actionUrl = actionUrl.replace(/&amp;/g, '&');
	}

    if(showFilter != null &&  showFilter == "false"){
        showFilter = false;
    } else if(showFilter == "true") {
    	showFilter = true;
	}

	var dataDefination={			
	    	"pagingType"	:"full_numbers",
	    	"processing"	:true,
	    	"serverSide"	:true,
			"bSort"			:true, // to hide column sorting
			"bFilter"		:showFilter, // to hide filter table
			"iDisplayLength":rows,
			"bPaginate"		:paginationFlag,
			"bInfo"			:showInfoFlag,
			"aaSorting"		:[],
			"responsive"	:true,
			"dom":
			"<'row'<'col-xs-6 col-sm-6 col-lg-8 dataTable-button'l><'col-xs-6 col-sm-6 col-lg-4'f>>" +
			"<'row'<'col-sm-12'tr>>" +
			"<'row'<'col-sm-3'i><'col-sm-9'p>>",
        	"language": {"search": "_INPUT_",
				"searchPlaceholder": "Filter",
                "emptyTable": emptyTable
			},
			"fnDrawCallback":function(oSettings){
				/*Not displaying the pagination if the table has no record*/
				var totalFoundRecords = oSettings._iRecordsDisplay;
				if((totalFoundRecords!=null && totalFoundRecords>0) == false && ( isNullOrEmpty(list)==true || list == '[]')){	

					$(oSettings.nTableWrapper).find('.dataTables_paginate').hide();
				}
			},
				
	        "fnRowCallback": function( nRow, aData, iDisplayIndex ) {
	        	/*This function is used to provide onclick functionality on whole td of delete/trash button.
	        	 *to achieve this functionality following  code will add require attribute to the last td of each row which
	        	 *contains the deleteUrl value.*/
                $('td:last-child',nRow).each(function(i,v){
                	var tempDataHref = $(v).html();  
                	var dataArray = tempDataHref.split(":");                	
                	if(dataArray!=null && dataArray.length>0 && dataArray[0]=="deleteUrl"){
                		$(v).empty();
                    	$(v).html("<a style='cursor:pointer'><span class='glyphicon glyphicon-trash'></span></a>");
                    	$(v).attr('data-toggle',"confirmation-singleton");
                    	if (dataArray[1].indexOf("&amp;") > -1) {
                    		dataArray[1] = dataArray[1].replace(/&amp;/g, "&");
                    	}
                    	$(v).attr('data-href',dataArray[1]);
                    	$(v).attr('onmousedown',"deleteConfirm()");
                	}                	                	            	                
                });
                
                /* This '$('td',nRow).each(function(i,v))' function will iterate over all the td's of a row and it will add the style attribute to
                 * the respective TD for which tdStyle is added in the jsp page*/
                
               $('td',nRow).each(function(i,v){
                	var tempStyle = tableStyleArray[tableId][i]; 
                	  if(isNullOrEmpty(tempStyle)==false){
                		$(v).attr('style',tempStyle);	
                	  }
                	});
	        },
        	"renderer": 'bootstrap'
	};	
	
	
	if(showPagination != null &&  showPagination== "false"){
		console.log("Show pagination is FALSE.");
		/* when showPagination attribute is set to 'false' then
		 * 1) Not displaying the pagination in the datatable.
		 * 2) Fetching all the record from the database 
		 * 3) Showing the vertical scroll bar*/
		paginationFlag = false;
		dataDefination["bPaginate"] 	 = false;
		dataDefination["iDisplayLength"] = 99999;
		/*dataDefination["scrollY"] 	 = "auto";*/
		dataDefination["scrollCollapse"] = true;
	}
	
	if(isNullOrEmpty(actionUrl)==false){	
		console.log("criteria: "+criteria);
		dataDefination["ajax"] = $.fn.dataTable.pipeline( dataListName, {
	    	url	 : contextPath + actionUrl,
	    	data : { beanType : beanType, showPagination : paginationFlag, "criteriaVal" : JSON.stringify(criteria) },
	    	pages: pages // number of pages to cache
		});

		//TODO : When PD will migrate in Rest flow, This code will be removed
        //DataTable default filter removed, because in ajax we will use custom filter.
        $(".dataTables_filter").remove();

	} else if(isNullOrEmpty(list)==false){
		dataDefination["dataList"] =  JSON.parse(list);
		dataDefination["processing"] =  false;
		dataDefination["serverSide"] =  false;

        //TODO : When PD will migrate in Rest flow, then This code will be removed
        //DataTable default filter removed, because in ajax we will use custom filter.
        $(".customFilter").remove();

	} else {
		dataDefination["dataList"] =  "";
		dataDefination["processing"] =  false;
		dataDefination["serverSide"] =  false;

        //TODO : When PD will migrate in Rest flow, then This code will be removed
        //DataTable default filter removed, because in ajax we will use custom filter.
        $(".customFilter").remove();
	}



	dataDefination["aoColumns"] = columns;
	
	var dataTableRef = $('#'+tableId).DataTable( dataDefination );

    var dataTable_button_groups = $("#"+tableId+"_wrapper").siblings(".dataTable-button-groups");
    $("#"+tableId+"_wrapper").find(".dataTable-button").html(dataTable_button_groups);



	var subTableUrl = $("#"+tableId).attr('subtableurl');
	
	if(isNullOrEmpty(subTableUrl)==false){
		$('#'+tableId +' tbody').on('click', 'tr td:not(:last-child)', function () {
				
				var subTableId = $(this).closest('table').attr('id');
				if(subTableId=='undefined' || subTableId==null){
					console.log("No Ajax call (Subtable has no sub-Table-URL)");	
					return;
				} else {
					console.log("subtableurl: "+subTableUrl);
					/*
					 * 	Enabling this code will work for the subtable to have subtable URL.
					 *  i.e. TABLE > SUB-TABLE-URL 
					 *  		   > SUBTABLE > SUB-TABLE-URL
						var subSubTableUrl = $("#"+subTableId).attr('subtableurl');				
						if(isNullOrEmpty(subTableUrl)==false){
							subTableUrl = subSubTableUrl;
						}
					*/
				}
				
				var regex = /^(?:<(\w+)(?:(?:\s+\w+(?:\s*=\s*(?:".*?"|'.*?'|[^'">\s]+))?)+\s*|\s*)>[^<>]*<\/\1+\s*>|<\w+(?:(?:\s+\w+(?:\s*=\s*(?:".*?"|'.*?'|[^'">\s]+))?)+\s*|\s*)\/>|<!--.*?-->|[^<>]+)*$/;
				var tdData = $(this).text();
				var regexFlag = regex.test(tdData);
				if(regexFlag==false){
					return;
				}
		        var tr = $(this).closest('tr');
		        var row = dataTableRef.row( tr );
		        if ( row.child.isShown() ) {
		            // This row is already open - close it
		            row.child.hide(1000);
		        } else {
		        		
		        	var rowJsonData = row.data();
		        	if(rowJsonData!=null && rowJsonData != 'undefinied'){
		        		for(var obj in rowJsonData){		        			
			        		if(obj!=null && rowJsonData.obj != 'undefinied'){
			        			if(rowJsonData[obj]!=null && rowJsonData[obj].createdByStaff!=null && rowJsonData[obj].createdByStaff != 'undefined'){
			        				var pictureDeleteStutus = delete rowJsonData[obj].createdByStaff.profilePicture;			        				
			        				break;
			        			}
			        		}
			        	}
		        	}
		        	
		        	console.log("Making AJAX call for Subtable URL with table id :"+tableId);
		        	var rowdata = JSON.stringify(row.data());

					if(isNullOrEmpty(rowdata)){
						console.log("Empty row Data found.So Preventing Ajax Call");
						return;
					}
		        	var param = "tableId="+tableId+"&rowData"+tableId+"="+encodeURIComponent(rowdata);		        			        	
		            // Open this row
		        	$.ajax({
		                url : contextPath + subTableUrl,
		                data :  param,
		                success : function(responseText) {		                	
		                    row.child(responseText).show(1000);
		                }
		            });
		        }	
		   } );
	}
}


$(document).ready(function() {

	$('.selectAllCheckBox').click(function (e) {
		$(this).closest('table').find('td:visible input:checkbox').not('input:disabled').prop('checked', this.checked);
		if(this.checked){
			$(this).closest('table').find('td input:checkbox').closest('tr').not('thead tr').addClass('selected');
		}else{
			$(this).closest('table').find('td input:checkbox').closest('tr').not('thead tr').removeClass('selected');
		}
	});


	    $('#selectAll').click(function(event) {  //on click

	        if(this.checked) { // check select status
	        	$("[name=ids]").prop("checked", true); 
	        	$('tr').not('thead tr').addClass('selected');  
	        }else{
	        	$("[name=ids]").prop("checked", false);
	        	$('tr').not('thead tr').removeClass('selected');
	        }
	        checkTotalSeleted();
	    });


    $('.selectAll').click(function(event) {  //on click
         var closestTableId = $(this).closest("table").attr("id");
         var idColumns = $("#"+closestTableId).find("input[name='ids']");

        if(this.checked) { // check select status
            $(idColumns).prop("checked", true);
            $('tr').not('thead tr').addClass('selected');
        }else{
            $(idColumns).prop("checked", false);
            $('tr').not('thead tr').removeClass('selected');
        }

    });


    $("table").on("change", ":checkbox", function() {
	    	if(this.checked){	    		
	    		if(this.id == "selectAll"){
	    			$(this).parents("tr").removeClass('selected');	
	    		}else{	    		
	    			$(this).parents('tr').not('thead tr').addClass('selected');	    		
	    		}
	    	}else{	    		
	    		if(this.id == "selectAll"){
	    			$(this).parents("tr").removeClass('selected');	
	    		}else{
	    			$(this).parents("tr").not('thead tr').removeClass('selected');
	    		}
	    	}
	    	checkTotalSeleted();
	    });
	    
	    function checkTotalSeleted(){
	    	var lengthTotal =  $('input[name=ids]:checked').length;
		    if(lengthTotal!="0"){
		    	$("#totalSelected").html("Selected Rows "+lengthTotal);
		    }else{
		    	$("#totalSelected").html("");
		    }
	    }	
});
/* Format Date-Time based on the defined date-format*/
/**
 * Copyright 2007 Tim Down.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
/**
 * simpledateformat.js
 *
 * A faithful JavaScript implementation of Java's SimpleDateFormat's format
 * method. All pattern layouts present in the Java implementation are
 * implemented here except for z, the text version of the date's time zone.
 *
 * Thanks to Ash Searle (http://hexmen.com/blog/) for his fix to my
 * misinterpretation of pattern letters h and k.
 * 
 * See the official Sun documentation for the Java version:
 * http://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html
 *
 * Author: Tim Down <tim@timdown.co.uk>
 * Last modified: 6/2/2007
 * Website: http://www.timdown.co.uk/code/simpledateformat.php
 */
 
/* ------------------------------------------------------------------------- */

var SimpleDateFormat;

(function() {
	function isUndefined(obj) {
		return typeof obj == "undefined";
	}

	var regex = /('[^']*')|(G+|y+|M+|w+|W+|D+|d+|F+|E+|a+|H+|k+|K+|h+|m+|s+|S+|Z+)|([a-zA-Z]+)|([^a-zA-Z']+)/;
	var monthNames = ["January", "February", "March", "April", "May", "June",
		"July", "August", "September", "October", "November", "December"];
	var dayNames = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
	var TEXT2 = 0, TEXT3 = 1, NUMBER = 2, YEAR = 3, MONTH = 4, TIMEZONE = 5;
	var types = {
		G : TEXT2,
		y : YEAR,
		M : MONTH,
		w : NUMBER,
		W : NUMBER,
		D : NUMBER,
		d : NUMBER,
		F : NUMBER,
		E : TEXT3,
		a : TEXT2,
		H : NUMBER,
		k : NUMBER,
		K : NUMBER,
		h : NUMBER,
		m : NUMBER,
		s : NUMBER,
		S : NUMBER,
		Z : TIMEZONE
	};
	var ONE_DAY = 24 * 60 * 60 * 1000;
	var ONE_WEEK = 7 * ONE_DAY;
	var DEFAULT_MINIMAL_DAYS_IN_FIRST_WEEK = 1;

	var newDateAtMidnight = function(year, month, day) {
		var d = new Date(year, month, day, 0, 0, 0);
		d.setMilliseconds(0);
		return d;
	}

	Date.prototype.getDifference = function(date) {
		return this.getTime() - date.getTime();
	};

	Date.prototype.isBefore = function(d) {
		return this.getTime() < d.getTime();
	};

	Date.prototype.getUTCTime = function() {
		return Date.UTC(this.getFullYear(), this.getMonth(), this.getDate(), this.getHours(), this.getMinutes(),
				this.getSeconds(), this.getMilliseconds());
	};

	Date.prototype.getTimeSince = function(d) {
		return this.getUTCTime() - d.getUTCTime();
	};

	Date.prototype.getPreviousSunday = function() {
		// Using midday avoids any possibility of DST messing things up
		var midday = new Date(this.getFullYear(), this.getMonth(), this.getDate(), 12, 0, 0);
		var previousSunday = new Date(midday.getTime() - this.getDay() * ONE_DAY);
		return newDateAtMidnight(previousSunday.getFullYear(), previousSunday.getMonth(),
				previousSunday.getDate());
	}

	Date.prototype.getWeekInYear = function(minimalDaysInFirstWeek) {
		if (isUndefined(this.minimalDaysInFirstWeek)) {
			minimalDaysInFirstWeek = DEFAULT_MINIMAL_DAYS_IN_FIRST_WEEK;
		}
		var previousSunday = this.getPreviousSunday();
		var startOfYear = newDateAtMidnight(this.getFullYear(), 0, 1);
		var numberOfSundays = previousSunday.isBefore(startOfYear) ?
			0 : 1 + Math.floor(previousSunday.getTimeSince(startOfYear) / ONE_WEEK);
		var numberOfDaysInFirstWeek =  7 - startOfYear.getDay();
		var weekInYear = numberOfSundays;
		if (numberOfDaysInFirstWeek < minimalDaysInFirstWeek) {
			weekInYear--;
		}
		return weekInYear;
	};

	Date.prototype.getWeekInMonth = function(minimalDaysInFirstWeek) {
		if (isUndefined(this.minimalDaysInFirstWeek)) {
			minimalDaysInFirstWeek = DEFAULT_MINIMAL_DAYS_IN_FIRST_WEEK;
		}
		var previousSunday = this.getPreviousSunday();
		var startOfMonth = newDateAtMidnight(this.getFullYear(), this.getMonth(), 1);
		var numberOfSundays = previousSunday.isBefore(startOfMonth) ?
			0 : 1 + Math.floor((previousSunday.getTimeSince(startOfMonth)) / ONE_WEEK);
		var numberOfDaysInFirstWeek =  7 - startOfMonth.getDay();
		var weekInMonth = numberOfSundays;
		if (numberOfDaysInFirstWeek >= minimalDaysInFirstWeek) {
			weekInMonth++;
		}
		return weekInMonth;
	};

	Date.prototype.getDayInYear = function() {
		var startOfYear = newDateAtMidnight(this.getFullYear(), 0, 1);
		return 1 + Math.floor(this.getTimeSince(startOfYear) / ONE_DAY);
	};

	/* ----------------------------------------------------------------- */

	SimpleDateFormat = function(formatString) {
		this.formatString = formatString;
	};

	/**
	 * Sets the minimum number of days in a week in order for that week to
	 * be considered as belonging to a particular month or year
	 */
	SimpleDateFormat.prototype.setMinimalDaysInFirstWeek = function(days) {
		this.minimalDaysInFirstWeek = days;
	};

	SimpleDateFormat.prototype.getMinimalDaysInFirstWeek = function(days) {
		return isUndefined(this.minimalDaysInFirstWeek)	?
			DEFAULT_MINIMAL_DAYS_IN_FIRST_WEEK : this.minimalDaysInFirstWeek;
	};

	SimpleDateFormat.prototype.format = function(date) {
		var formattedString = "";
		var result;

		var padWithZeroes = function(str, len) {
			while (str.length < len) {
				str = "0" + str;
			}
			return str;
		};

		var formatText = function(data, numberOfLetters, minLength) {
			return (numberOfLetters >= 4) ? data : data.substr(0, Math.max(minLength, numberOfLetters));
		};

		var formatNumber = function(data, numberOfLetters) {
			var dataString = "" + data;
			// Pad with 0s as necessary
			return padWithZeroes(dataString, numberOfLetters);
		};

		var searchString = this.formatString;
		while ((result = regex.exec(searchString))) {
			var matchedString = result[0];
			var quotedString = result[1];
			var patternLetters = result[2];
			var otherLetters = result[3];
			var otherCharacters = result[4];

			// If the pattern matched is quoted string, output the text between the quotes
			if (quotedString) {
				if (quotedString == "''") {
					formattedString += "'";
				} else {
					formattedString += quotedString.substring(1, quotedString.length - 1);
				}
			} else if (otherLetters) {
				// Swallow non-pattern letters by doing nothing here
			} else if (otherCharacters) {
				// Simply output other characters
				formattedString += otherCharacters;
			} else if (patternLetters) {
				// Replace pattern letters
				var patternLetter = patternLetters.charAt(0);
				var numberOfLetters = patternLetters.length;
				var rawData = "";
				switch (patternLetter) {
					case "G":
						rawData = "AD";
						break;
					case "y":
						rawData = date.getFullYear();
						break;
					case "M":
						rawData = date.getMonth();
						break;
					case "w":
						rawData = date.getWeekInYear(this.getMinimalDaysInFirstWeek());
						break;
					case "W":
						rawData = date.getWeekInMonth(this.getMinimalDaysInFirstWeek());
						break;
					case "D":
						rawData = date.getDayInYear();
						break;
					case "d":
						rawData = date.getDate();
						break;
					case "F":
						rawData = 1 + Math.floor((date.getDate() - 1) / 7);
						break;
					case "E":
						rawData = dayNames[date.getDay()];
						break;
					case "a":
						rawData = (date.getHours() >= 12) ? "PM" : "AM";
						break;
					case "H":
						rawData = date.getHours();
						break;
					case "k":
						rawData = date.getHours() || 24;
						break;
					case "K":
						rawData = date.getHours() % 12;
						break;
					case "h":
						rawData = (date.getHours() % 12) || 12;
						break;
					case "m":
						rawData = date.getMinutes();
						break;
					case "s":
						rawData = date.getSeconds();
						break;
					case "S":
						rawData = date.getMilliseconds();
						break;
					case "Z":
						rawData = date.getTimezoneOffset(); // This is returns the number of minutes since GMT was this time.
						break;
				}
				// Format the raw data depending on the type
				switch (types[patternLetter]) {
					case TEXT2:
						formattedString += formatText(rawData, numberOfLetters, 2);
						break;
					case TEXT3:
						formattedString += formatText(rawData, numberOfLetters, 3);
						break;
					case NUMBER:
						formattedString += formatNumber(rawData, numberOfLetters);
						break;
					case YEAR:
						if (numberOfLetters <= 3) {
							// Output a 2-digit year
							var dataString = "" + rawData;
							formattedString += dataString.substr(2, 2);
						} else {
							formattedString += formatNumber(rawData, numberOfLetters);
						}
						break;
					case MONTH:
						if (numberOfLetters >= 3) {
							formattedString += formatText(monthNames[rawData], numberOfLetters, numberOfLetters);
						} else {
							// NB. Months returned by getMonth are zero-based
							formattedString += formatNumber(rawData + 1, numberOfLetters);
						}
						break;
					case TIMEZONE:
						var isPositive = (rawData > 0);
						// The following line looks like a mistake but isn't
						// because of the way getTimezoneOffset measures.
						var prefix = isPositive ? "-" : "+";
						var absData = Math.abs(rawData);

						// Hours
						var hours = "" + Math.floor(absData / 60);
						hours = padWithZeroes(hours, 2);
						// Minutes
						var minutes = "" + (absData % 60);
						minutes = padWithZeroes(minutes, 2);

						formattedString += prefix + hours + minutes;
						break;
				}
			}
			searchString = searchString.substr(result.index + result[0].length);
		}
		return formattedString;
	};
})();


function filterTableRows(value,tableBodyID){
    if(isNullOrEmpty(tableBodyID) ==true){
        console.log("Filteration can't be performed.Reason: table id not found");
    }
    var data = value.split(" ");
    var tableRows = $("#"+tableBodyID).find("tbody>tr");
    var rows = $('tr');
    if (value == "") {
        tableRows.show();
        return;
    }
    tableRows.hide();
    tableRows.filter(function (i, v) {
        var $currentRow = $(this);
        var rowText = $currentRow.text().toLowerCase();
        value = value.toLowerCase();
        for (var d = 0; d < data.length; ++d) {
            if (rowText.toLowerCase().indexOf(value)>=0) {
                return true;
            };
        }
        $currentRow.find('input:checkbox').prop('checked', false);
        $currentRow.removeClass('selected');
        return false;
    }).show();
}