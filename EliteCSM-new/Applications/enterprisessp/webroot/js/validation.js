function isInteger(s){   
	var i;
    for (i = 0; i < s.length; i++){   	       
        var c = s.charAt(i);
        if (((c < "0") || (c > "9"))) return false;
    }	   
    return true;
}

function isNull(value)
{
	myvalue1=righttrim(value);
	myvalue2=lefttrim(myvalue1);
	return (myvalue2.length == 0);
}
/**
 * This function trims string from the left
 */
function lefttrim(value){
	while( value.length != 0 ){
		mychar=value.substring(0,1);
		if( mychar == "\u0020" ){
			value=value.substr(1);
		}
		else
			break;
	}
	return value;
}

/**
 * This function trims string from the right
 */
function righttrim(value){
	while( value.length != 0 ){
		mychar=value.substring(value.length-1);
		if( mychar == "\u0020" ){
			value=value.substr(0,value.length-1);
		}
		else
			break;
	}
	return value;
}
function IsNumeric(strString){
//  check for valid numeric strings
	var strValidChars = "0123456789.-";
	var strChar;
	var blnResult = true;
	
	if (strString.length == 0) return false;
	
	//  test strString consists of valid characters listed above
	for (i = 0; i < strString.length && blnResult == true; i++){
	   strChar = strString.charAt(i);
	   if (strValidChars.indexOf(strChar) == -1){
	      blnResult = false;
	   }
	}
	return blnResult;
}