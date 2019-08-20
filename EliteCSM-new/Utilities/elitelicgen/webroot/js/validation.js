
/**
This validation js file contains the functions for major client side validations.

Created Date : 23 June 2003
Author       : Vaibhavi Shah

**/

var formName;
var formNamePassed;
var checkboxName;
var serverDate = new Date();
/**
 *	This function will set the tile of the window.
 */
function setTitle(paramTitle)
{
	window.document.title = paramTitle;
}	

/**
 * This function will set the form name of the page.
 */
function setForm(paramFormName)
{
	formName = 'document.' + paramFormName + ".";
	formNamePassed = paramFormName;
}

/**
 * This function will set the combo box name for group toggle.
 */
function setParameters(paramCheckboxName)
{
	checkboxName = paramCheckboxName;
}

/**
 * This function will set the server date in this file.
 */
 function setServerDate(paramServerDate)
 {
	serverDate = new Date(paramServerDate);
 }

/**
 * This function trims string from the left
 */
function lefttrim(value)
{
	while( value.length != 0 )
	{
		mychar=value.substring(0,1);
		if( mychar == "\u0020" )
		{
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
function righttrim(value)
{
	while( value.length != 0 )
	{
		mychar=value.substring(value.length-1);
		if( mychar == "\u0020" )
		{
			value=value.substr(0,value.length-1);
		}
		else
			break;
		
	}
	return value;
}


function isNull(value)
{
	myvalue1=righttrim(value);
	myvalue2=lefttrim(myvalue1);
	return (myvalue2.length == 0)
}


function validate()
{
	var args=validate.arguments;

    // Space Not allowed   
	re =/^[a-zA-Z][\w-_.]*$/; //'RNotSpace'
	//Space Allowed
	sre=/^[a-zA-Z][\w-_./ ][^\\]*$/;
	//Space Allowed and first character can be a digit.
	asre=/^[a-zA-Z0-9][\w-_./ ][^\\]*$/;
	//sre = /[a-zA-Z0-9\u00A0]+/;//-'RSpace'
	cre=/^( )*(\w)+(,( )*(\w)+)*$/;//'RComma'
	mre=/^[ _@#$a-zA-Z\d]+$/;//'RMasterName'

	checkname=/^[ a-zA-Z\d\\/_]*$/;//'RComma'

	//User Name 3 to 15 characters,digits and underscore
	userre = /^[a-zA-Z0-9_]{3,15}$/;//'RUser'
	//Password 3 to 15 characters,digits and underscore
	passwordre = /^[a-zA-Z0-9_]{3,15}$/;//for username - 'RPassword'
	//File allows space,digits,characters,:,./ -filepath	
	filere= /^[a-zA-Z0-9\.:_\x20\-\\]+$/;//-'RFile'
	//Allows integer 0 or greater than 0
	nre= /^\d+$/;// 'RNum'
	//Allows integer greater than 0
	nzre= /^0*[1-9]\d*$/;// 'RNZNum'
	//Allows float 
	fre=/^((\d+(\.\d*)?)|((\d*\.)?\d+))$/;	//'RFloat'
	//Currency 13,2 which allows 0.0 also
	currencyre=/^\d{1,10}((\.\d{1,2}$)|$)/; //'RCurrency'
	//Currency 13,4 which is used to display 4 digit precision values
	currency4Precisionre=/^\d{1,10}((\.\d{1,4}$)|$)/; //'RCurrency4Precision'
	//Currency greater than 0.0
	nzcurrencyre=/^0*[1-9]\d{0,12}$|^0*[1-9]\d{0,12}\.\d?\d?|^0*\.\d?[1-9]$|^0*\.[1-9]\d?$/;//'RNZCurrency'	
 	//Percentage (5,2)which allows 0.0(till 100 only)
	percentre=/^\d{1,3}((\.\d{1,2}$)|$)/; //'RPercentage'
	//Percentage greater than 0.0
	nzpercentre=/^0*[1-9]\d?$|^0*[1-9]\d?\.\d?\d?$|^0*100\.0?0?$|^0*100$|^0*\.\d?[1-9]$|^0*\.[1-9]\d?$/;//RNZPercentage
	//Allows negative and float
	nfre = /(^-?\d\d*\.\d*$)|(^-?\d\d*$)|(^-?\.\d\d*$)/; // allows negative and float-
	//Checks Email
	emailre = /^\w+(\-\w+)*(\.\w+(\-\w+)*)*@\w+(\-\w+)*(\.\w+(\-\w+)*)+$/;//'REmail'
	//Allows days 1 to 365
	nzdayre=/^0*[1-2]\d?\d?$|^0*[1-9]\d?$|^0*[1-9]$|^0*3[1-5]\d?$|^0*36[1-5]$/;//'RNZDay'
	//Allows days 0 to 365
	dayre=/^0*[1-2]?\d?\d?$|^0*3[1-5]\d?$|^0*36[1-5]$|^0*$/;//'RDay'
	//Phone Number
	phonere = /^[1-9]\d{2}\-\s?\d{3}\-\d{4}$/;//'RPhone'
	ssnExp = /^(\d{3})-?\d{2}-?\d{4}$/;
	pincodere = /^\d{5,6}$/;
 	MinVal=0; //Minimum value to be accepted for RMinMax...change according to requirements
	MaxVal=3;

		if (args[0]!='RDropMenu' && args[0]!='RCheck'&& args[0]!='RRadio'&& args[0]!='RList'&& args[0]!='RPhone')
		{		
				FldExpVal=eval(formName + args[2]).value;
				FldExpFoc=eval(formName + args[2]).focus();
				FldExpSel=eval(formName + args[2]).select();
				FldExpLen=eval(formName + args[2]).value.length;
		}
		if(args[0]=='RNull')
		{
			if(isNull(FldExpVal))
			{
				alert(args[3] +' '+args[1]);
				setFocusSelection(args[2]);
				return false;
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RComma')
		{
			if (!cre.test(FldExpVal))
			{
				alert(args[3] +' '+args[1]);
				setFocusSelection(args[2]);
				return false;
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RMasterName')
		{
			if (!mre.test(FldExpVal))
			{
				alert(args[3] +' '+args[1]);
				setFocusSelection(args[2]);
				return false;
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RUser')
		{
			if (!userre.test(FldExpVal))
			{
				alert(args[3] +' '+args[1]);
				setFocusSelection(args[2]);
				return false;
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RPassword')
		{
			if (!passwordre.test(FldExpVal))
			{
				alert(args[3] +' '+args[1]);
				setFocusSelection(args[2]);
				return false;
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RNotSpace')
		{
			if (!re.test(FldExpVal))
			{
				alert(args[3] +' '+args[1]);
				setFocusSelection(args[2]);
				return false;
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RCheckName')
		{
			if (!checkname.test(FldExpVal))
			{
				alert(args[3] +' '+args[1]);
				setFocusSelection(args[2]);
				return false;
			}
			else
			{
				return true;
			}
		}

		else if (args[0]=='RFile')
		{
			if(!isNull(FldExpVal))
			{
				if (!filere.test(FldExpVal))
				{
					alert(args[3] +' '+args[1]);
					setFocusSelection(args[2]);
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}		
		else if (args[0]=='RSpace')
		{
			if (!sre.test(FldExpVal))
			{
				alert(args[3] +' '+args[1]);
				setFocusSelection(args[2]);
				return false;
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RFirstAlphabetSpace')
		{
			if (!asre.test(FldExpVal))
			{
				alert(args[3] +' '+args[1]);
				setFocusSelection(args[2]);
				return false;
			}
			else
			{
				return true;
			}
		}
		
		else if (args[0]=='RDropMenu')
		{
			FldExpValMenu=eval(formName + args[2]).selectedIndex;
			if (FldExpValMenu==0)
			{
				alert(args[3] +' '+args[1]);
			    eval(formName + args[2]).focus();
//				setFocusSelection(args[2]);
				return false;
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RNum')
		{
			if(!isNull(FldExpVal))
			{
				if (!nre.test(FldExpVal))
				{
					alert(args[3] +' '+args[1]);
					setFocusSelection(args[2]);
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}
		else if (args[0]=='RNZNum')
		{
			if(!isNull(FldExpVal))
			{
				if (!nzre.test(FldExpVal))
				{
					alert(args[3] +' '+args[1]);
					setFocusSelection(args[2]);
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}
		else if (args[0]=='RFloat')
		{
			if(!isNull(FldExpVal))
			{
				if (!fre.test(FldExpVal))
				{
					alert(args[3] +' '+args[1]);
					setFocusSelection(args[2]);
					return false;
				}
				else
				{
					return true;
				}
			}
			else 
				return true;
		}
		else if (args[0]=='RCurrency')
		{
			if(!isNull(FldExpVal))
			{
				if(!currencyre.test(FldExpVal))
				{
					alert(args[3]+' '+args[1]);
					setFocusSelection(args[2]);
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}
		else if (args[0] == 'RCurrency4Precision')
		{
			if(!isNull(FldExpVal))
			{
				if(!currency4Precisionre.test(FldExpVal))
				{
					alert(args[3]+' '+args[1]);
					setFocusSelection(args[2]);
					return false;
				}
				else
				{
					return true;
				}
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RNZCurrency')
		{
			if(!isNull(FldExpVal))
			{
				if(!nzcurrencyre.test(FldExpVal))
				{
					alert(args[3]+' '+args[1]);
					setFocusSelection(args[2]);
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}
		else if (args[0]=='RPercentage')
		{
			if(!isNull(FldExpVal))
			{
				if(!percentre.test(FldExpVal))
				{
					alert(args[3]+' '+args[1]);
					setFocusSelection(args[2]);
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}
		else if (args[0]=='RNZPercentage')
		{
			if(!isNull(FldExpVal))
			{
				if(!nzpercentre.test(FldExpVal))
				{
					alert(args[3]+' '+args[1]);
					setFocusSelection(args[2]);
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}
		else if (args[0]=='REmail')
		{
			if(!isNull(FldExpVal))
			{
				if (!emailre.test(FldExpVal))
				{
					alert(args[3] +' '+args[1]);
					setFocusSelection(args[2]);
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}
		else if (args[0]=='RNZDay')
		{
			if(!isNull(FldExpVal))
			{
				if (!nzdayre.test(FldExpVal))
				{
					alert(args[3] +' '+args[1]);
					setFocusSelection(args[2]);
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}
		else if (args[0]=='RDay')
		{
			if(!isNull(FldExpVal))
			{
				if (!dayre.test(FldExpVal))
				{
					alert(args[3] +' '+args[1]);
					setFocusSelection(args[2]);
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}
		else if (args[0]=='RPincode')
		{
			if (!pincodere.test(FldExpVal))
			{
				alert(args[3] +' '+args[1]);
				setFocusSelection(args[2]);
				return false;
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RPhone')
		{
			FldExpVal=eval(formName + args[2]).value;
			if(!phonere.test(FldExpVal))
			{
				alert(args[3] +' '+args[1]);
				setFocusSelection(args[2]);
				return false;
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RSSN')
		{
			if (isNull(FldExpVal))
			{
				alert(args[i+1] + requiredCaption);
				FldExpFoc;
				return false;
			}
			var matchArr = FldExpVal.match(/^(\d{3})-?\d{2}-?\d{4}$/);
			var numDashes = FldExpVal.split('-').length - 1;
			if (matchArr == null || numDashes == 1) 
			{
				alert("Invalid " + args[i+1] + ". Must be 9 digits.");
				FldExpFoc;
				return false;
			}
			else if (parseInt(matchArr[1],10)==0) 
			{
				alert("Invalid " + args[i+1] + " : SSN can\'t start with 000.");
				FldExpFoc;
				return false;
			}
		}
		else if (args[0]=='RMinMax')
		{
			if (isNull(FldExpVal))
			{
				alert(args[i+1] + requiredCaption);
				FldExpFoc;
				return false;
			}
			if((FldExpLen<MinVal)||(FldExpLen>MaxVal))
			{
				alert(args[i+1]+' should be of '+ MinVal +' to '+ MaxVal +' characters.');
				FldExpFoc;
				return false;
			}
		}		
		else if(args[0]=='RCheck')
		{
			isChecked=eval(formName + args[2]).checked;
			if(!isChecked)
			{
				alert(args[3] +' '+args[1]);
				return false;
			}
			else
			{
				return true;
			}
		}
		else if(args[0]=='RRadio')
		{
			radioLen=eval(formName + args[2]).length;
			isChecked=false;
			for(i=0;i<radioLen;i++)
			{
				if(eval((formName+args[2]+"["+i+"].checked")))
				{
					isChecked=true;
				}
			}
			if(!isChecked)
			{
				alert(args[3] +' '+args[1]);
				return false;
			}
			else
			{
				return true;
			}
		}
		else if(args[0]=='RList')
		{
			FldExpValMenu=eval(formName + args[2]).selectedIndex;
			if(FldExpValMenu==-1)
			{
				alert(args[3] +' '+args[1]);

				return false;
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RSysParamNum')
		{
			if(!isNull(FldExpVal))
			{
				if (!nre.test(FldExpVal))
				{
					alert(args[3] +' '+args[1]);
					setFocusSelection(args[2]);
					return false;
				}
				else
				{
					return true;
				}
			}
			else
			{
				alert(args[3]+' '+'is required');
				setFocusSelection(args[2]);
				return false;
			}
		}
		else if (args[0]=='RSysParamCurrency')
		{
			if(!isNull(FldExpVal))
			{
				if(!currencyre.test(FldExpVal))
				{
					alert(args[3]+' '+args[1]);
					setFocusSelection(args[2]);
					return false;
				}
				else
				{
					return true;
				}
			}
			else
			{
				alert(args[3]+' '+'is required');
				setFocusSelection(args[2]);
				return false;
			}
		}
		else if (args[0]=='RSysParamEmail')
		{
			
			if(!isNull(FldExpVal))
			{
			
				if (!emailre.test(FldExpVal))
				{
					alert(args[3] +' '+args[1]);
					setFocusSelection(args[2]);
					return false;
				}
				else
				{
					return true;
				}
			}
			else
			{
				alert(args[3]+' '+'is required');
				setFocusSelection(args[2]);
				return false;
			}
		}
}


function setFocusSelection(obj)
{
	if(eval(formName + obj).type == "text"){
		eval(formName + obj).select();	
		eval(formName + obj).focus();
	}
	else{
		eval(formName + obj).focus();
	}
}
function performValidate()
{
	
	var args=performValidate.arguments;
	// Space Not allowed   
	re =/^[a-zA-Z][\w-_.]*$/; //'RNotSpace'
	cre =/^( )*(\w)+(,( )*(\w)+)*$/;//'RComma'
	mre=/^[ _@#$a-zA-Z\d]+$/;//'RMasterName'

	//Space Allowed
	sre=/^[a-zA-Z][\w-_./ ]*$/;
	//User Name 3 to 15 characters,digits and underscore
	userre = /^[a-zA-Z0-9_]{3,15}$/;//'RUser'
	//Password 3 to 15 characters,digits and underscore
	passwordre = /^[a-zA-Z0-9_]{3,15}$/;//for username - 'RPassword'
	//File allows space,digits,characters,:,./ -filepath	
	filere= /^[a-zA-Z0-9\.:_\x20\-\\]+$/;//-'RFile'
	//Allows integer 0 or greater than 0
	nre= /^\d+$/;// 'RNum'
	//Allows integer greater than 0
	nzre= /^0*[1-9]\d*$/;// 'RNZNum'
	//Allows float 
	fre=/^((\d+(\.\d*)?)|((\d*\.)?\d+))$/;	//'RFloat'
	//Currency 13,2 which allows 0.0 also
	currencyre=/^\d{1,10}((\.\d{1,2}$)|$)/; //'RCurrency'
	//Currency 13,4 which allows 0.0 also
	currency4Precisionre=/^\d{1,10}((\.\d{1,4}$)|$)/; //'RCurrency4Precision'
	//Currency greater than 0.0
	nzcurrencyre=/^0*[1-9]\d{0,12}$|^0*[1-9]\d{0,12}\.\d?\d?|^0*\.\d?[1-9]$|^0*\.[1-9]\d?$/;//'RNZCurrency'	
 	//Percentage (5,2)which allows 0.0(till 100 only)
	percentre=/^\d{1,3}((\.\d{1,2}$)|$)/; //'RPercentage'
	//Percentage greater than 0.0
	nzpercentre=/^0*[1-9]\d?$|^0*[1-9]\d?\.\d?\d?$|^0*100\.0?0?$|^0*100$|^0*\.\d?[1-9]$|^0*\.[1-9]\d?$/;//RNZPercentage
	//Allows negative and float
	nfre = /(^-?\d\d*\.\d*$)|(^-?\d\d*$)|(^-?\.\d\d*$)/; // allows negative and float-
	//Checks Email
	emailre = /^\w+(\-\w+)*(\.\w+(\-\w+)*)*@\w+(\-\w+)*(\.\w+(\-\w+)*)+$/;//'REmail'
	//Allows days 1 to 365
	nzdayre=/^0*[1-2]?\d?\d?$|^0*3[1-5]\d?$|^0*36[1-5]$/;//'RNZDay'
	//Allows days 0 to 365
	dayre=/^0*[1-2]?\d?\d?$|^0*3[1-5]\d?$|^0*36[1-5]$|^0*$/;//'RDay'
	//Phone Number
	phonere = /^[1-9]\d{2}\-\s?\d{3}\-\d{4}$/;//'RPhone'
	ssnExp = /^(\d{3})-?\d{2}-?\d{4}$/;
	pincodere = /^[1-9]\d{5}$/;
 	MinVal=0; //Minimum value to be accepted for RMinMax...change according to requirements
	MaxVal=3;

		if (args[0]!='RDropMenu' && args[0]!='RCheck'&& args[0]!='RRadio'&& args[0]!='RList'&& args[0]!='RPhone')
		{		
				FldExpVal=eval(formName + args[2]).value;
				FldExpFoc=eval(formName + args[2]).focus();
				FldExpSel=eval(formName + args[2]).select();
				FldExpLen=eval(formName + args[2]).value.length;
		}
		if(args[0]=='RNull')
		{
			if(isNull(FldExpVal))
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RComma')
		{
			if (!cre.test(FldExpVal))
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RMasterName')
		{
			if (!mre.test(FldExpVal))
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RUser')
		{
			if (!userre.test(FldExpVal))
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RPassword')
		{
			if (!passwordre.test(FldExpVal))
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RNotSpace')
		{
			if (!re.test(FldExpVal))
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RFile')
		{
			if(!isNull(FldExpVal))
			{
				if (!filere.test(FldExpVal))
				{
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}		
		else if (args[0]=='RSpace')
		{
			if (!sre.test(FldExpVal))
			{
				
				return false;
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RDropMenu')
		{
			FldExpValMenu=eval(formName + args[2]).selectedIndex;
			if (FldExpValMenu==0)
			{
				return false;
			}
			else
			{
				return true;
			}
		}

		else if (args[0]=='RNum')
		{
			if(!isNull(FldExpVal))
			{
				if (!nre.test(FldExpVal))
				{
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}
		else if (args[0]=='RNZNum')
		{
			if(!isNull(FldExpVal))
			{
				if (!nzre.test(FldExpVal))
				{
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}
		else if (args[0]=='RFloat')
		{
			if(!isNull(FldExpVal))
			{
				if (!fre.test(FldExpVal))
				{
					return false;
				}
				else
				{
					return true;
				}
			}
			else 
				return true;
		}
		else if (args[0]=='RCurrency')
		{
			if(!isNull(FldExpVal))
			{
				if(!currencyre.test(FldExpVal))
				{
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}
		else if(args[0]=='RCurrency4Precision')
		{
			if(!isNull(FldExpVal))
			{
				if(!currency4Precisionre.test(FldExpVal))
				{
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}
		else if (args[0]=='RNZCurrency')
		{
			if(!isNull(FldExpVal))
			{
				if(!nzcurrencyre.test(FldExpVal))
				{
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}
		else if (args[0]=='RPercentage')
		{
			if(!isNull(FldExpVal))
			{
				if(!percentre.test(FldExpVal))
				{
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}
		else if (args[0]=='RNZPercentage')
		{
			if(!isNull(FldExpVal))
			{
				if(!nzpercentre.test(FldExpVal))
				{
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}
		else if (args[0]=='REmail')
		{
			if(!isNull(FldExpVal))
			{
				if (!emailre.test(FldExpVal))
				{
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}
		else if (args[0]=='RNZDay')
		{
			if(!isNull(FldExpVal))
			{
				if (!nzdayre.test(FldExpVal))
				{
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}
		else if (args[0]=='RDay')
		{
			if(!isNull(FldExpVal))
			{
				if (!dayre.test(FldExpVal))
				{
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return true;
		}
		else if (args[0]=='RPincode')
		{
			if (!pincodere.test(FldExpVal))
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RSSN')
		{
			if (isNull(FldExpVal))
			{
				alert(args[i+1] + requiredCaption);
				FldExpFoc;
				return false;
			}
			var matchArr = FldExpVal.match(/^(\d{3})-?\d{2}-?\d{4}$/);
			var numDashes = FldExpVal.split('-').length - 1;
			if (matchArr == null || numDashes == 1) 
			{
				alert("Invalid " + args[i+1] + ". Must be 9 digits.");
				FldExpFoc;
				return false;
			}
			else if (parseInt(matchArr[1],10)==0) 
			{
				alert("Invalid " + args[i+1] + " : SSN can\'t start with 000.");
				FldExpFoc;
				return false;
			}
		}
		else if (args[0]=='RMinMax')
		{
			if (isNull(FldExpVal))
			{
				alert(args[i+1] + requiredCaption);
				FldExpFoc;
				return false;
			}
			if((FldExpLen<MinVal)||(FldExpLen>MaxVal))
			{
				alert(args[i+1]+' should be of '+ MinVal +' to '+ MaxVal +' characters.');
				FldExpFoc;
				return false;
			}
		}		
		else if(args[0]=='RCheck')
		{
			isChecked=eval(formName + args[2]).checked;
			if(!isChecked)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else if(args[0]=='RRadio')
		{
			radioLen=eval(formName + args[2]).length;
			isChecked=false;
			for(i=0;i<radioLen;i++)
			{
				if(eval((formName+args[2]+"["+i+"].checked")))
				{
					isChecked=true;
				}
			}
			if(!isChecked)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else if(args[0]=='RList')
		{
			FldExpValMenu=eval(formName + args[2]).selectedIndex;
			if(FldExpValMenu==-1)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		
}


	/*
		This function check the length of the field especiallyrequired for textarea fields
	*/
	function isValidLength(fieldId, allowedLength, fieldName,alertMsg)
	{
		/*var lenCaption = " length should not exceed "; */

		var obj = eval(formName + fieldId);
		var len = obj.value.length;
		if(len>allowedLength)
		{
			obj.select();
			obj.focus();
	/*		alert(fieldName + lenCaption + allowedLength + "."); */
			alert(fieldName + alertMsg + allowedLength + ".");
			return false;	
		}
		else
			return true;
	}

//THIS FUNCTION WILL TRIM THE INPUT TEXT
function trimText(strtext)
{	
	if(strtext == null)
	 return null;
	if(strtext.length == 0)
		return strtext;				
		
	//GET RID OF LEADING SPACES
	while (strtext.substring(0,1) == ' ') 
		strtext= strtext.substring(1, strtext.length);
				
	//GET RID OF TRAILING SPACES 
	while (strtext.substring(strtext.length-1,strtext.length) == ' ')
		strtext = strtext.substring(0, strtext.length-1);	

	return strtext;
}

/**
	Compares two date and return the difference if 
	difference is positive means fromDate is less the toDate
	difference is negative means fromDate is greater the toDate
	difference is 0 means fromDate toDate are equal
**/
function compareDate(paramFirstDate,paramSecondDate)
{
	var fromDate = new Date(paramFirstDate);
//	var hour = fromDate.getHours();
	
	
	var toDate = new Date(paramSecondDate);
	var diff = toDate.getTime() - fromDate.getTime();
	return (diff);
}

/**
	Compares two date with from date time set as 00:00:00 a
	and to date time set as 23:59:59 .Return the difference if 
	difference is positive means fromDate is less the toDate
	difference is negative means fromDate is greater the toDate
	difference is 0 means fromDate toDate are equal
**/
function compareDateTime(paramFirstDate,paramSecondDate)
{
	var fromDate = new Date(paramFirstDate);
	var toDate = new Date(paramSecondDate);
	toDate.setHours(23);
	toDate.setMinutes(59);
	toDate.setSeconds(59);
	var diff = toDate.getTime() - fromDate.getTime();
	return (diff);
}
/**
Compares given two dates and the given format which can be MM-DD-YYYY and 
DD-MM-YYYY
returns 0 if both equal
negative value if first date < second date
positive valie if first date > second date
**/


function dateDiff(paramFirstDate, paramSecondDate, dateFormat)
{  
	var d1;
	var d2;
	var mmIndex;
	var ddIndex;
	var yyIndex;
	var separator;
	var inputEndDateArray;
	var inputStartDateArray;
	var inputEndDateStr;
	var inputStartDateStr;

	if(dateFormat !="" || trimText(dateFormat).length > 0)
	{
		mmIndex = (dateFormat.toUpperCase()).indexOf("MM");
		ddIndex = (dateFormat.toUpperCase()).indexOf("DD");
		yyIndex = (dateFormat.toUpperCase()).indexOf("YYYY");
		
		if(dateFormat.indexOf("/")!=-1)
			separator = "/";
		else
			separator = "-";
	}

	if(paramFirstDate !="" || trimText(paramFirstDate).length > 0)
	{
		inputEndDateArray = paramFirstDate.split(separator);	
		if(mmIndex==0 && ddIndex==3 && yyIndex==6)
		{	
			inputEndDateStr = inputEndDateArray[0] + separator + inputEndDateArray[1] + separator + inputEndDateArray[2];
		}
		else if(mmIndex==3 && ddIndex==0 && yyIndex==6)
		{
			inputEndDateStr = inputEndDateArray[1] + separator + inputEndDateArray[0] + separator + inputEndDateArray[2];
		}
		d1 = new Date(inputEndDateStr);
	}
	else
	{
		d1 = serverDate;
	}
	
	if(paramSecondDate != "" || trimText(paramSecondDate).length > 0)
	{
		inputStartDateArray = paramSecondDate.split(separator);
		if(mmIndex==0 && ddIndex==3 && yyIndex==6)
		{	
			inputStartDateStr = inputStartDateArray[0] + separator + inputStartDateArray[1] + separator + inputStartDateArray[2];
		}
		else if(mmIndex==3 && ddIndex==0 && yyIndex==6)
		{
			inputStartDateStr = inputStartDateArray[1] + separator + inputStartDateArray[0] + separator + inputStartDateArray[2];
		}
		d2 = new Date(inputStartDateStr);
	}
	else
	{
		d2 = serverDate;
	}
	//days = Math.floor(timediff / (1000 * 60 * 60 * 24)); 
	//alert("First Date : " + d1.getTime() + " ###### Second Date : " + d2.getTime());
	
	if(d1.getFullYear() == d2.getFullYear() && d1.getMonth() == d2.getMonth() && d1.getDate() == d2.getDate())	
		return 0;
	else
		return (d1.getTime() - d2.getTime());
} 

/**
Compares given two dates and time and the given format which can be MM-DD-YYYY and 
DD-MM-YYYY
returns 0 if both equal
negative value if first date < second date
positive valie if first date > second date
**/

//TimeFormat is assumed to be HH:MM (24 hours format)
function dateTimeDiff(paramFirstDate, paramFirstTime, paramSecondDate, paramSecondTime, dateFormat)
{  
	var d1;
	var d2;
	var mmIndex;
	var ddIndex;
	var yyIndex;
	var separator;
	var inputFirstDateArray;
	var inputSecondDateArray;
	var inputFirstDateStr;
	var inputSecondDateStr;
	var inputFirstTimeArray;
	var inputSecondTimeArray;

	if(dateFormat != "" || trimText(dateFormat).length > 0)
	{
		mmIndex = (dateFormat.toUpperCase()).indexOf("MM");
		ddIndex = (dateFormat.toUpperCase()).indexOf("DD");
		yyIndex = (dateFormat.toUpperCase()).indexOf("YYYY");
		
		if(dateFormat.indexOf("/")!=-1)
			separator = "/";
		else
			separator = "-";
	}

	if(paramFirstDate != "" || trimText(paramFirstDate).length > 0)
	{
		inputFirstDateArray = paramFirstDate.split(separator);
		if(mmIndex==0 && ddIndex==3 && yyIndex==6)
		{	
			//alert("MM : " + inputFirstDateArray[0] + " DD : " + inputFirstDateArray[1] + " YYYY : " + inputFirstDateArray[2]);
			inputFirstDateStr = inputFirstDateArray[0] + separator + inputFirstDateArray[1] + separator + inputFirstDateArray[2];
		}
		else if(mmIndex==3 && ddIndex==0 && yyIndex==6)
		{
			//alert("MM : " + inputFirstDateArray[0] + " DD : " + inputFirstDateArray[1] + " YYYY : " + inputFirstDateArray[2]);
			inputFirstDateStr = inputFirstDateArray[1] + separator + inputFirstDateArray[0] + separator + inputFirstDateArray[2];
		}
		d1 = new Date(inputFirstDateStr);
		//alert("D1 Time : " + d1.getTime());
		if(paramFirstTime.indexOf(":") != -1)
		{				
			var hours = parseInt(paramFirstTime.substring(0, paramFirstTime.indexOf(":")));
			var minutes = parseInt(paramFirstTime.substring(paramFirstTime.indexOf(":") + 1));
			d1.setHours(hours);
			d1.setMinutes(minutes);
		}
	}
	else
	{
		d1 = serverDate;
		if((paramFirstTime != "" || trimText(paramFirstTime).length > 0) && paramFirstTime.indexOf(":") != -1)
		{
			var hours = parseInt(paramFirstTime.substring(0, paramFirstTime.indexOf(":")));
			var minutes = parseInt(paramFirstTime.substring(paramFirstTime.indexOf(":") + 1));
			d1.setHours(hours);
			d1.setMinutes(minutes);
		}
	}
	if(paramSecondDate != "" || trimText(paramSecondDate).length > 0)
	{
		inputSecondDateArray = paramSecondDate.split(separator);
		if(mmIndex==0 && ddIndex==3 && yyIndex==6)
		{	
			inputSecondDateStr = inputSecondDateArray[0] + separator + inputSecondDateArray[1] + separator + inputSecondDateArray[2];
		}
		else if(mmIndex==3 && ddIndex==0 && yyIndex==6)
		{
			inputSecondDateStr = inputSecondDateArray[1] + separator + inputSecondDateArray[0] + separator + inputSecondDateArray[2];
		}
		d2 = new Date(inputSecondDateStr);

		if(paramSecondTime.indexOf(":") != -1)
		{
			var hours = parseInt(paramSecondTime.substring(0, paramSecondTime.indexOf(":")));
			var minutes = parseInt(paramSecondTime.substring(paramSecondTime.indexOf(":") + 1));
			d2.setHours(hours);
			d2.setMinutes(minutes);
		}
	}
	else
	{
		d2 = serverDate;
		if((paramSecondTime != "" || trimText(paramSecondTime).length > 0) && paramSecondTime.indexOf(":") != -1)
		{
			var hours = parseInt(paramSecondTime.substring(0, paramSecondTime.indexOf(":")));
			var minutes = parseInt(paramSecondTime.substring(paramSecondTime.indexOf(":") + 1));
			d2.setHours(hours);
			d2.setMinutes(minutes);
		}
	}
	return d1.getTime() - d2.getTime();
}

/*
	This function will check the extenstion for the uploaded file
*/
function checkExtension(fileFieldId, allowedExtensions)
{	
	var filename = eval(formName + fileFieldId).value;
	
	filename = trimText(filename);

	if(filename.length==0)
		return true;

	if(filename.indexOf(".")==-1)
		return false;
	
	var extension = filename.substr(filename.lastIndexOf(".")+1,(filename.length));
					
	if(allowedExtensions.indexOf("*.*")!=-1)
		return true;
	else
	{	
		var extArr = allowedExtensions.split(',');
		for(i=0;i<extArr.length;i++)
		{
			if(extension.toUpperCase()==extArr[i].toUpperCase())
				return true;
		}
	}
}

//added by vaibhavi
/** 
*	This function checks the value of the field against
*	given value
*	@param Label,element id,value
*/
function isValidValue(fieldId,allowedvalue,fieldName)
{
	var caption= " value should not exceed ";
	var obj = eval(formName + fieldId)
	if(parseInt(obj.value) > allowedvalue)
	{
		alert(fieldName+caption+allowedvalue);
		obj.focus();
		return false;
	}
	else
		return true;
}

//added by vaibhavi
/** 
*	This function checks the minimum value for the field against
*	given value
*	@param Label,element id,value
*/

function isValidMinValue(fieldId,minValue,fieldName)
{
	var caption= " value should be greater than " + minValue;
	var obj = eval(formName + fieldId)
	if(parseInt(obj.value) <= minValue)
	{
		alert(fieldName+caption);
		obj.focus();
		return false;
	}
	else
		return true;
}
//added by vaibhavi
/** 
*	This function checks the range for the field against
*	given min and max value
*	@param Label,element id,value
*/

function isValidRange(fieldId,minValue,maxValue,fieldName)
{
	var caption= " value should be between ";
	var obj = eval(formName + fieldId)
	if(parseInt(obj.value) > maxValue || parseInt(obj.value) < minValue)
	{
		alert(fieldName+caption+minValue+"-"+maxValue);
		obj.focus();
		return false;
	}
	else
		return true;
}

function trimSentense(strText) 
{ 
    // this will get rid of leading spaces 
    while (strText.substring(0,1) == ' ') 
        strText = strText.substring(1, strText.length);

    // this will get rid of trailing spaces 
    while (strText.substring(strText.length-1,strText.length) == ' ')
        strText = strText.substring(0, strText.length-1);

   return strText;
} 

//added by Novin Jaiswal
/** 
*	This function will trim the Specified feild value than remove inbetween spaces
*	and convert the first letter of every word in caps.
*	@param fieldId
*/


function sentenseCase(fieldId)
{
	 var strTextBox = eval(formName + fieldId) ;
	if(strTextBox != null)
	{
		 var strOriginalText = strTextBox.value;

		 strOriginalText = trimSentense(strOriginalText);
		 var strChangedText = "";
		 var flag = false;
		 var strTemp = "";

		  for(var i=0;i<strOriginalText.length;i++)
		  {
			strTemp = strOriginalText.charAt(i);

			if(!(flag && strTemp == ' '))
			{
				
				if(i==0 || (flag && strTemp != ' '))
				{
					strChangedText = strChangedText + strTemp.toUpperCase();
				}
				else if(strTemp == ' ')
				{
					flag = true;
					strChangedText = strChangedText + ' ';
				}
				else
				{
					strChangedText = strChangedText + strTemp.toLowerCase();
				}
				
				if(flag && strTemp != ' ')
				{
					flag = false;
				}
			}
		  }		//for loop over
		 strTextBox.value = strChangedText;
	}
//	 alert("return ="+strTextBox.value);
	 return true;

}

//Function is added by Amisha Patel

/*
THIS FUNCTION RETURNS A VALID DATE STRING TO CREATE A DATE OBJECT FROM THE GIVEN STRING AND ITS FORMAT
val : value of Date
format : date format of value date
isDateTimeformat : true argument include date with time ,false argument is only date 
*/
function getLongDateFromFormat(val,format,isDateTimeformat) {

	val=val+"";
	format=format+"";
	var i_val=0;
	var i_format=0;
	var c="";
	var token="";
	var token2="";
	var x,y;
	var now=new Date();
	var year=now.getYear();
	var month=now.getMonth()+1;
	var date=1;
	var hh=now.getHours();
	var mm=now.getMinutes();
	var ss=now.getSeconds();
	var ampm="";

	while(i_format < format.length) {

		c=format.charAt(i_format);
		token="";

		while((format.charAt(i_format)==c) &&(i_format < format.length)) {

			token += format.charAt(i_format++);
		}
		if(token=="yyyy" || token=="yy" || token=="y") {
			if(token=="yyyy") {
				x=4;
				y=4;
			}

			if(token=="yy"){
				x=2;
				y=2;
			}
			if(token=="y") {
				x=2;
				y=4;
			}
			year=_getInt(val,i_val,x,y);
			if(year==null){
				return 0;
			}
			i_val += year.length;
			if(year.length==2){
				if(year > 89){
					year=1900+(year-0);

				} else 	{
					year=2000+(year-0);
				}
			}
		}
		else if(token=="MMM"||token=="NNN") {
			month=0;
			for(var i=0; i<MONTH_NAMES.length;	i++) {
				var month_name=MONTH_NAMES[i];
				if(val.substring(i_val,i_val+month_name.length).toLowerCase()==month_name.toLowerCase()) {
					if(token=="MMM"||(token=="NNN"&&i>11)) {
						month=i+1;
						if(month>12) {
							month -= 12;
						}
						i_val += month_name.length;
						break;
					}
				}
			}
			if((month < 1)||(month>12)) {
				return 0;
			}

		} else if(token=="EE"||token=="E") 	{

			for(var i=0;i<DAY_NAMES.length;i++) {
				var day_name=DAY_NAMES[i];
				if(val.substring(i_val,i_val+day_name.length).toLowerCase()==day_name.toLowerCase()) {
					i_val += day_name.length;
					break;
				}
			}


		} else if(token=="MM"||token=="M") {
			month=_getInt(val,i_val,token.length,2);
			if(month==null||(month<1)||(month>12)) {
				return 0;
			}
			i_val+=month.length;

		} else if(token=="dd"||token=="d") {
			date=_getInt(val,i_val,token.length,2);
			if(date==null||(date<1)||(date>31)) {
				return 0;
			}
			i_val+=date.length;

		} else if(token=="hh"||token=="h") {
			hh=_getInt(val,i_val,token.length,2);
			if(hh==null||(hh<1)||(hh>12)) {
				return 0;
			}
			i_val+=hh.length;

		} else if(token=="HH"||token=="H") {
			hh=_getInt(val,i_val,token.length,2);
			if(hh==null||(hh<0)||(hh>23)) {
				return 0;
			}
			i_val+=hh.length;
		} else if(token=="KK"||token=="K") {
			hh=_getInt(val,i_val,token.length,2);
			if(hh==null||(hh<0)||(hh>11)) {
				return 0;
			}
			i_val+=hh.length;

		} else if(token=="kk"||token=="k") {
			hh=_getInt(val,i_val,token.length,2);
			if(hh==null||(hh<1)||(hh>24)) {
				return 0;
			}
			i_val+=hh.length;
			hh--;
		} else if(token=="mm"||token=="m") {
			mm=_getInt(val,i_val,token.length,2);
			if(mm==null||(mm<0)||(mm>59)) {
				return 0;
			}
			i_val+=mm.length;
		} else if(token=="ss"||token=="s") {
			ss=_getInt(val,i_val,token.length,2);
			if(ss==null||(ss<0)||(ss>59)) {
				return 0;
			}
			i_val+=ss.length;
		} else if(token=="a") {
			if(val.substring(i_val,i_val+2).toLowerCase()=="am") {
				ampm="AM";
			} else if(val.substring(i_val,i_val+2).toLowerCase()=="pm") {
				ampm="PM";
			} else {
				return 0;
			}
			i_val+=2;
		} else {

			if(val.substring(i_val,i_val+token.length)!=token) {
				return 0;
			} else {
				i_val+=token.length;
			}
		}
	}
	if(i_val != val.length) {
		return 0;
	}
	if(month==2) {
		if( ((year%4==0)&&(year%100 != 0) ) ||(year%400==0) ) {
			if(date > 29) {
				return 0;
			}
		} else {
			if(date > 28) {
				return 0;
			}
		}
	}
	if((month==4)||(month==6)||(month==9)||(month==11)) {
		if(date > 30) {
			return 0;
		}
	}

	if(hh<12 && ampm=="PM") {
		hh=hh-0+12;

	} else if(hh>11 && ampm=="AM") {
		hh-=12;
	}
	if(isDateTimeformat == "true"){
		var newdate=new Date(year,month-1,date,hh,mm,ss);
		return newdate.getTime();
	}else{
	
		var newdate=new Date(year,month-1,date,0,0,0);
		
		return newdate.getTime();
	}
}
/*
 Function is added By Amisha Patel
 Date 04 Sept 2006
*/
function isNullDate(dateObject,captionName) {

    if(!(dateObject.value.length >0)){
	alert(captionName + " is a compulsory field. Please enter the required details");
	return false;
    }
    return true;

}

/*
 validFor : must be update/create 
 currentDateValue : current system date 
 oldFirstDateValue : orignal from date fro update option only otherwise sould be '' 
 firstDateObject : object of From Date 
 secondDateObject : object of To Date
 Date Format : date fromat of System 
 captionName : for which field validation is for example From Date To Date etc.
*/
function validateDateDifference(validFor,currentDateValue,oldFirstDateValue,firstDateObject,secondDateObject,dateFormat,captionName) {

	if(validFor == "create"){
		if(firstDateObject.value.length > 0) {
			var currentDateLong = getLongDateFromFormat(currentDateValue,dateFormat,"false");
			var firstDateLong = getLongDateFromFormat(firstDateObject.value,dateFormat,"false");
			var dateFirstResultDiff = firstDateLong - currentDateLong;
			if(dateFirstResultDiff < 0){
				alert(captionName +" should be greater or equal to current date");
				return false;
			}
		}
	}else{
		if(firstDateObject.value.length > 0) {
			
			var fromDateLong = getLongDateFromFormat(firstDateObject.value,dateFormat,"false");
			var oldFromDateLong = getLongDateFromFormat(oldFirstDateValue,dateFormat,"false");
			
			var dateDiff = fromDateLong - oldFromDateLong;
			if(dateDiff != 0 ){
				var currentDateLong = getLongDateFromFormat(currentDateValue,dateFormat,"false");
				var firstDateLong = getLongDateFromFormat(firstDateObject.value,dateFormat,"false");
				var dateFirstResultDiff = firstDateLong - currentDateLong;
				if(dateFirstResultDiff < 0){
					alert(captionName +" should be greater or equal to current date");
					return false;
				}
			}
			
		}
    }

    if(firstDateObject.value.length > 0 ){
		if(secondDateObject){
			if(secondDateObject.value.length > 0) {
			
				var bool = "false"; 
				var currentDateLong = getLongDateFromFormat(currentDateValue,dateFormat,"false");
				var secondDateLong = getLongDateFromFormat(secondDateObject.value,dateFormat,"false");
				var dateSecondResultDiff = secondDateLong - currentDateLong;
				if(dateSecondResultDiff < 0){
					alert(captionName +" should be greater or equal to current date");
					bool = "true";
					return false;
				}
				if(bool == "false"){
					var firstDateLong = getLongDateFromFormat(firstDateObject.value,dateFormat,"false");
					var secondDateLong = getLongDateFromFormat(secondDateObject.value,dateFormat,"false");
					var dateDiff = secondDateLong - firstDateLong;
					if(dateDiff < 0){
						alert(captionName +" should be greater or equal to from date.");
						return false;
					}
				}	
			}
		}
     }
    return true;
}
/*
displayStatus : load or unload 
commaSeparatedList = comma separated division list 
*/
function loadDivision(displayStatus,commaSeparatedList){
	var divList = new Array();
	divList = commaSeparatedList.split(",");
	for(var i=0;i<divList.length;i++){
		var divName = trimText(divList[i]);
		if(displayStatus.toLowerCase() == "load"){
			document.getElementById(divName).style.visibility = "visible";
	    	}else{
	    		document.getElementById(divName).style.visibility = "hidden";
		}
	}
}
/*
radioDateObject : Radio Button object means on which radio button checked what divisions should visible
commaSeparatedList = comma separated division list 

*/
function loadDefaultDateDivision(radioDateObject,commaSeparatedList){
	var divList = new Array();
	var displayStatus = "load";
	divList = commaSeparatedList.split(",");
	if(radioDateObject.checked){
		displayStatus = "unload";	
	}
	
	for(var i=0;i<divList.length;i++){
		var divName = trimText(divList[i]);
		if(displayStatus.toLowerCase() == "load"){
			document.getElementById(divName).style.visibility = "visible";
	    	}else{
	    		document.getElementById(divName).style.visibility = "hidden";
		}
	}
}
/*

 validFor : must be update/create 
 specificRadioObject : specific radio button object
 fromDateObject : object of From Date 
 toDateObject : object of To Date
 oldFomDateValue : orignal from date fro update option only otherwise sould be '' 
 currentDateValue : current system date 
 Date Format : date fromat of System 
 
*/
function validateFromToDate(validFor,specificRadioObject,fromDateObject,toDateObject,oldFromDateValue,currentDateValue,dateFormat){
    
	
	if(!isNullDate(fromDateObject,"From Date")){

    		return false;
	}else{
		if(!validateDateDifference(validFor,currentDateValue,oldFromDateValue,fromDateObject,'',dateFormat,'From Date'))
     		{
     			return false;
     		}

   	}

	if(specificRadioObject.checked){
		
    	    if(!isNullDate(toDateObject,"To Date")){
    	        return false;
            }else{
        	   if(!validateDateDifference(validFor,currentDateValue,oldFromDateValue
                                    , fromDateObject
                                    , toDateObject
                                    , dateFormat,'To Date'))
    		    {

        		    return false;
    		    }
            }

	}
	return true;
}


function IsNumeric(strString)
   //  check for valid numeric strings	
   {
   var strValidChars = "0123456789.-";
   var strChar;
   var blnResult = true;

   if (strString.length == 0) return false;

   //  test strString consists of valid characters listed above
   for (i = 0; i < strString.length && blnResult == true; i++)
      {
      strChar = strString.charAt(i);
      if (strValidChars.indexOf(strChar) == -1)
         {
         blnResult = false;
         }
      }
   return blnResult;
}

function validatePort(txt){
	// check for valid numeric port	 
	if(IsNumeric(txt) == false){
			if(txt >= 0 && txt<=65535)
			return(true);
		}
		return(false);
}

   