<!-- Unicode Supported -->






/**
This validation js file contains the functions for major client side validations.

Created Date : 23 June 2003
Author       : Vaibhavi Shah

**/


/**** Added by Dhaval Patel for support of Locale specific Regular Regular Expression ******/
/**** Start of Local Specific Regular Expressions *******/
function DefaultRegularExpression() 
{

		 this.txt_isdisfact = /^\d+$/ ;

		 this.txt_isnotspace = /^[a-zA-Z][\w-_.]*$/ ;

		 this.txt_limit1_999 = /^0*[0-9]$|^0*[1-9][0-9]$|^0*[1-9][0-9][0-9]$/ ;

		 this.txt_numeric = /[0-9][0-9]*/ ;

		 this.txt_checkinteger = /[0-9]*/ ;

		 this.limit0_100 = /^[0-9]$|^0*[1-9]\d?$|^0*[1-9]\d?\\.\d?\d?$|^0*100\.0?0?$|^0*100$|^0*\.\d?[1-9]$|^0*\.[1-9]\d?$/ ;

		 this.txt_isdigit = /(^\d+$)|^$/ ;

		 this.txt_location = /^[0-9]+$/ ;

		 this.txt_iszpercentage = /^0*[0-9]\d?$|^0*[0-9]\d?\.\d?\d?$|^0*100\.0?0?$|^0*100$|^0*\.\d?[0-9]$|^0*\.[0-9]\d?$/ ;

		 this.txt_isnumber4digit = /^\d{1,10}((\.\d{1,4}$)|$)/ ;

		 this.txt_ispolicyname = /^[a-zA-Z][a-zA-Z0-9]{2,150}$/ ;

		 this.txt_iscontactperson = /^[a-zA-Z0-9][a-zA-Z0-9]{2,100}$/ ;

		 this.txt_limit1_30 = /^0*[1-9]$|^0*[1-2][0-9]$|^0*[3][0]$/ ;

		 this.txt_ispassword = /^[a-zA-Z0-9_]{3,15}$/ ;

		 this.txt_issysparamcurrency = /^\d{1,10}((\.\d{1,2}$)|$)/ ;

		 this.txt_daysafterbilldate = /^0*[0-9]$|^0*[1-2][0-8]$|^0*[1][9]$/ ;

		 this.txt_alphanumeric = /^[\w$-]+$/ ;

		 this.txt_iscomma = /^( )*(\w)+(,( )*(\w)+)*$/ ;

		 this.txt_isvalidipaddr = /^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/ ;

		 this.txt_sysint = /^0*\d+$/ ;

		 this.txt_limit1_28 = /^0*[1-9]$|^0*[1-2][0-8]$/ ;

		 this.txt_isday = /^0*[1-2]?\d?\d?$|^0*3[1-5]\d?$|^0*36[1-5]$|^0*$/ ;

		 this.txt_isfilerequired = /^[a-zA-Z0-9\.:_\x20\-\\]+$/ ;

		 this.txt_isemailforAlert = /^\w+(\-\w+)*(\.\w+(\-\w+)*)*@\w+(\-\w+)*(\.\w+(\-\w+)*)+$/ ;

		 this.txt_checkname = /^[ a-zA-Z\d\\/_]*$/ ;

		 this.txt_stblogin = /^[0-9A-Za-z]+$/ ;

		 this.txt_isinterestrate = /^\d{0,3}(\.\d{0,2})?$/ ;

		 this.txt_WSCAlpha = /^[a-zA-Z]*$/ ;

		 this.txt_isnewusername = /^[a-zA-Z0-9][a-zA-Z0-9_@.-]{2,30}$/ ;

		 this.txt_numberprefix2 = /^[0-9]+$/ ;

		 this.txt_ishour = /^\d+$/ ;

		 this.txt_greaterthan_1 = /(^\-1$)|(^\d{1,10}((\.\d{1,2}$)|$))/ ;

		 this.txt_limit0_99 = /^0*[0-9]$|^0*[1-9]\d?$/ ;

		 this.txt_isAlphanumeric = /[A-Za-z0-9]+/ ;

		 this.txt_max20 = /^([^\\]{1,20})$/ ;

		 this.txt_numberprefix3 = /^[0-7]+$/ ;

		 this.txt_name = /^[0-9A-Za-z\s\.']+$/ ;

		 this.txt_isusername = /^[a-zA-Z0-9_]{3,20}$/ ;

		 this.txt_numeric19 = /^[1-9]\d*$/ ;

		 this.txt_ispincode = /[.]*/ ;

		 this.txt_isexenum = /^0*[1-9]\d*$/ ;

		 this.txt_isphonenumber = /^[1-9]\d{2}\-\s?\d{3}\-\d{4}$/ ;

		 this.txt_checkreason = /^[0-9A-Za-z ]+$/ ;

		 this.txt_digits = /\d/ ;

		 this.txt_formaterror = /\p{Punct}\p{Punct}[0-9]\p{Punct}/ ;

		 this.txt_alphabetic = /([A-Z]|[a-z])([A-Z]|[a-z])*/ ;

		 this.txt_isemail = /^\w+(\-\w+)*(\.\w+(\-\w+)*)*@\w+(\-\w+)*(\.\w+(\-\w+)*)+$/ ;

		 this.txt_isspace = /^[a-zA-Z][\w-_./ ][^\\]*$/ ;

		 this.txt_isnzint = /^0*[1-9]\d*$/ ;

		 this.txt_limit_1_10 = /(^\-1$)|(^[0-9]$)|(^10$)/ ;

		 this.txt_amttrans = /(^\d{1,10}((\.\d{1,2}$)|$))/ ;

		 this.txt_exchangehours = /(^\d+$)|(^\d+\.\d+$)/ ;

		 this.txt_limit1_15 = /^[1-9]$|^0*[1][0-5]$/ ;

		 this.txt_limit1_10000 = /(^\d{1,4}$)|(^10000$)/ ;

		 this.txt_isnumber = /^\d{1,10}((\.\d{1,4}$)|$)/ ;

		 this.txt_allowallmax255 = /^([\w]|[\W]){1,255}$/ ;

		 this.txt_isHexadecimal = /((0(x|X))?)([0-9A-Fa-f]+)/ ;

		 this.txt_isvalidhomephone = /^[0-9][\d,-]*$/ ;

		 this.txt_numberprefix4 = /^[0-9A-Fa-f]+$/ ;

		 this.txt_WSCAlphaNum = /^[a-zA-Z0-9]*$/ ;

		 this.txt_max30 = /^([^\\]{1,30})$/ ;

		 this.txt_isfirstalphabet = /^[a-zA-Z0-9][\w-_./ ][^\\]*$/ ;

		 this.txt_alphanumerical = /([A-Z]|[a-z]|[0-9])([A-Z]|[a-z]|[0-9])*/ ;

		 this.txt_feedbackmsg = /\w{1,}/ ;

		 this.txt_limit1440 = /^(0*[0-9]$)|^(0*[1-9][0-9]$)|^(0*[1-9][0-9][0-9]$)|^(0*[0-1][0-4][0-3][0-9]$)|^0*1440$/ ;

		 this.txt_issysparamemail = /^\w+(\-\w+)*(\.\w+(\-\w+)*)*@\w+(\-\w+)*(\.\w+(\-\w+)*)+$/ ;

		 this.txt_floatamt = /^((\d*(\.\d*)?)|((\d*\.)?\d+))$/ ;

		 this.txt_isBinary = /[0-1]+/ ;

		 this.txt_isnotdot = /^[a-zA-Z][\w-_]*$/ ;

		 this.txt_nospace = /^\s+$/ ;

		 this.txt_limit1_48 = /^0*[1-9]$|^0*[1-4][0-8]$/ ;

		 this.txt_isvalidname = /^[a-zA-Z0-9_]{}$/ ;

		 this.txt_checkval = /^\d{1,3}((\.\d{1,2}$)|$)/ ;

		 this.txt_allowallmax100 = /^([\w]|[\W]){1,100}$/ ;

		 this.txt_entervalue = /^[0-9][0-9,]*[0-9]+$/ ;

		 this.txt_ZerotoNineP = /[0-9]+/ ;

		 this.txt_matcheid = /.+@.+\.[a-z]+/ ;

		 this.txt_limit1_4 = /^[1-4]$/ ;

		 this.txt_isValidGrpName = /^[^\x27\x22\x3e\x3c]+$/ ;

		 this.txt_isnzpercentage = /^0*[1-9]\d?$|^0*[1-9]\d?\.\d?\d?$|^0*100\.0?0?$|^0*100$|^0*\.\d?[1-9]$|^0*\.[1-9]\d?$/ ;

		 this.txt_RTaxRate = /^\d{0,2}(\.\d{0,2})?$/ ;

		 this.txt_isconfname = /^[ a-zA-Z\d_]*$/ ;

		 this.txt_ischequeno = /^0*[1-9]\d*$/ ;

		 this.txt_isintrequired = /\d/ ;

		 this.txt_limitpackageexpire = /(^\-1$)|(^\d{1,10}$)/ ;

		 this.txt_pinprefix = /^([A-Z]{4})$|([A-Z]{4}[,]{1})*([A-Z]{4})$/ ;

		 this.txt_numberprefix = /^[0-9A-Za-z]+$/ ;

		 this.txt_limit0_20 = /^0*[1-9]$|^0*[0-1][0-9]$|[0-2]0$/ ;

		 this.txt_isinteger = /^\d+$/ ;

		 this.txt_path = /(^((\.$)|(\.\.$)|(\.(\/|\\))|(\.\.(\/|\\))|(\b[a-zA-Z]:(\/|\\))|(\\|\/)))($|([A-Za-z_]\w*)((((\/|\\)[A-Za-z_]\w*)*(\/|\\|$))|$))$/ ;

		 this.txt_isfloatrequired = /\f/ ;

		 this.txt_checkcurrency = /^\d{0,10}((\.\d{1,4}$)|$)/ ;

		 this.txt_limit0_3 = /^[0-3]$/ ;

		 this.txt_smtpport = /^(([\d]{1,4})|([0-5][\d]{1,4})|(6[0-4][\d]{1,3})|(65[0-4][\d]{1,2})|(655[0-2][\d]{1})|(6553[0-6]))$/ ;

		 this.txt_isint = /^\d+$/ ;

		 this.txt_invalidpercent = /^((\d+(\.\d*)?)|((\d*\.)?\d+))$/ ;

		 this.txt_isOctect = /[0-7]+/ ;

		 this.txt_ispercentage = /^\d{1,3}((\.\d{1,2}$)|$)/ ;

		 this.txt_forsearch = /^[a-zA-Z%]*[\w-_%./ ][^\\]*$/ ;

		 this.txt_limit1_100000 = /^0*[123456789]{1}\\d{0,4}$|100000$/ ;

		 this.txt_isminute = /^\d+$/ ;

		 this.txt_isnzcurrencyrate = /^0*[1-9]\d{0,12}$|^0*[1-9]\d{0,12}\.\d?\d?|^0*\.\d?[1-9]$|^0*\.[1-9]\d?$/ ;

		 this.txt_limit3_90 = /^0*[3-9]$|^0*[1-8][0-9]$|^0*[9][0]$/ ;

		 this.txt_username = /^[a-zA-Z0-9\_\\]+$/ ;

		 this.txt_limit3_16 = /^0*[3-9]$|^0*[1][0-6]$/ ;

		 this.txt_limit1_99999 = /^0*[1-9]$|^0*[1-9][0-9]$|^0*[1-9][0-9][0-9]$|^0*[1-9][0-9][0-9][0-9]$|^0*[1-9][0-9][0-9][0-9][0-9]$/ ;

		 this.txt_limit0_9 = /^[1-9]$|^0*[1][0]$/ ;

		 this.txt_numberprefix1 = /^[0-1]+$/ ;

		 this.txt_WSCPwd = /^[0-9]*$/ ;

		 this.txt_isznumber = /^0*[0-9]\d{0,12}$|^0*[0-9]\d{0,12}\.\d?\d?|^0*\.\d?[0-9]$|^0*\.[0-9]\d?$/ ;

		 this.txt_pulse = /^([0-9:;])*([0-9:;])$/ ;

		 this.txt_validateuser = /(^[a-zA-Z0-9][a-zA-Z.\p{Punct}/\@-_0-9]{2,25})+$/ ;

		 this.txt_minmaxdefault = /^\d{1,3}((\.\d{1,2}$)|$)/ ;

		 this.txt_isposnegint = /(^-?\d\d*$)/ ;

		 this.txt_isnznumber = /^0*[1-9]\d{0,12}$|^0*[1-9]\d{0,12}\.\d?\d?|^0*\.\d?[1-9]$|^0*\.[1-9]\d?$/ ;

		 this.txt_limit_1_10000 = /(^\-1$)|(^\d{1,4}((\.\d{1,2}$)|$))|(^10000(\.[0]{1,2}$|$))/ ;

		 this.text_ischeckint = /^\d+$/ ;

		 this.txt_isnzday = /^0*[1-2]\d?\d?$|^0*[1-9]\d?$|^0*[1-9]$|^0*3[1-5]\d?$|^0*36[1-5]$/ ;

		 this.txt_isvalidpincode = /^[0-9][0-9]{5}$/ ;

		 this.txt_creditcardprefix = /^([^\\]{1,40})$/ ;

		 this.txt_ZerotoNine = /[0-9]+/ ;

		 this.txt_isvalidftpaddr = /^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/ ;

		 this.txt_isvalidfaxnum = /^[0-9][\d,-]*$/ ;

		 this.txt_issysparamnumeric = /^\d+$/ ;

		 this.txt_validateusername = /(^[a-zA-Z0-9][a-zA-Z.\p{Punct}/\@-_0-9]{2,25})+$/ ;

		 this.txt_mastername = /^[ @_#$a-zA-Z\d]+$/ ;

		 this.txt_limit0_200 = /^0*\d{0,2}$|^0*1\d{0,2}$|^0*200$/ ;

		 this.txt_greaterthan_equalto_0 = /^\d{1,10}((\.\d{1,2}$)|$)$/ ;

		 this.txt_filetypecsv = /^(([a-zA-Z]:)|(\\[\s-_]*\w+)\$?)*(\\(\w[\w\s-_]*))+\.(csv|CSV)$/ ;

		 this.txt_limit0_10000 = /(^\d{1,4}((\.\d{1,2}$)|$))|(^10000(\.[0]{1,2}$|$))/ ;

		 this.txt_allowallmax20 = /^([\w]|[\W]){1,20}$/ ;

		 this.txt_ispinprefix = /^([A-Z0-9]{0,5}[A-Z0-9]{1}[A-Z]{1}[,]{1})*([A-Z0-9]{0,5}[A-Z0-9]{1}[A-Z]{1})$/ ;

		 this.txt_limit0_100 = /^0*[0-9]$|^0*[1-9]\d?$|^0*100$/ ;

		 this.txt_checkpinprefix = /^([A-Za-z0-9]{0,5}[A-Za-z0-9]{1}[A-Za-z]{1}[,]{1})*([A-Za-z0-9]{0,5}[A-Za-z0-9]{1}[A-Za-z]{1})$/ ;

		 this.txt_cpeid = /^[a-fA-F0-9]{1,8}$/ ;

	this.getRegEx = getRegEx
	
	function getRegEx()
	{
		var args = getRegEx.arguments;
		
		if(args[0] == 'RDisfact' )
		{
			return this.txt_isdisfact;
		}	
		
		if(args[0] == 'RNotSpace' )
		{
			return this.txt_isnotspace;
		}	
		
		if(args[0] == 'RLimit1_999' )
		{
			return this.txt_limit1_999;
		}	
		
		if(args[0] == 'RNumeric' )
		{
			return this.txt_numeric;
		}	
		
		if(args[0] == 'Rcheckint' )
		{
			return this.txt_checkinteger;
		}	
		
		if(args[0] == 'RLimitbtw0_100' )
		{
			return this.limit0_100;
		}	
		
		if(args[0] == 'RCheckDigit' )
		{
			return this.txt_isdigit;
		}	
		
		if(args[0] == 'RLocation' )
		{
			return this.txt_location;
		}	
		
		if(args[0] == 'ZPercentage' )
		{
			return this.txt_iszpercentage;
		}	
		
		if(args[0] == 'RCurrency4Precision' )
		{
			return this.txt_isnumber4digit;
		}	
		
		if(args[0] == 'RPolicyName' )
		{
			return this.txt_ispolicyname;
		}	
		
		if(args[0] == 'RName1' )
		{
			return this.txt_iscontactperson;
		}	
		
		if(args[0] == 'RLimit1_30' )
		{
			return this.txt_limit1_30;
		}	
		
		if(args[0] == 'RPassword' )
		{
			return this.txt_ispassword;
		}	
		
		if(args[0] == 'RSysParamCurrency' )
		{
			return this.txt_issysparamcurrency;
		}	
		
		if(args[0] == 'RDaysOnAfterBillDate' )
		{
			return this.txt_daysafterbilldate;
		}	
		
		if(args[0] == 'RAlphaNumeic' )
		{
			return this.txt_alphanumeric;
		}	
		
		if(args[0] == 'RComma' )
		{
			return this.txt_iscomma;
		}	
		
		if(args[0] == 'RIPaddrCheck' )
		{
			return this.txt_isvalidipaddr;
		}	
		
		if(args[0] == 'RSysInt' )
		{
			return this.txt_sysint;
		}	
		
		if(args[0] == 'RLimit1_28' )
		{
			return this.txt_limit1_28;
		}	
		
		if(args[0] == 'RDay' )
		{
			return this.txt_isday;
		}	
		
		if(args[0] == 'RFile' )
		{
			return this.txt_isfilerequired;
		}	
		
		if(args[0] == 'REmailforAlert' )
		{
			return this.txt_isemailforAlert;
		}	
		
		if(args[0] == 'RCheckName' )
		{
			return this.txt_checkname;
		}	
		
		if(args[0] == 'RCheckAlpNum' )
		{
			return this.txt_stblogin;
		}	
		
		if(args[0] == 'RInterestRate' )
		{
			return this.txt_isinterestrate;
		}	
		
		if(args[0] == 'WSCCheckAlpha' )
		{
			return this.txt_WSCAlpha;
		}	
		
		if(args[0] == 'RNewUserName' )
		{
			return this.txt_isnewusername;
		}	
		
		if(args[0] == 'RNumberPrefix2' )
		{
			return this.txt_numberprefix2;
		}	
		
		if(args[0] == 'RHour' )
		{
			return this.txt_ishour;
		}	
		
		if(args[0] == 'RGreaterthan_1' )
		{
			return this.txt_greaterthan_1;
		}	
		
		if(args[0] == 'RLimit0_99' )
		{
			return this.txt_limit0_99;
		}	
		
		if(args[0] == 'RAlphanum' )
		{
			return this.txt_isAlphanumeric;
		}	
		
		if(args[0] == 'RMax20' )
		{
			return this.txt_max20;
		}	
		
		if(args[0] == 'RNumberPrefix3' )
		{
			return this.txt_numberprefix3;
		}	
		
		if(args[0] == 'RName' )
		{
			return this.txt_name;
		}	
		
		if(args[0] == 'RUser' )
		{
			return this.txt_isusername;
		}	
		
		if(args[0] == 'Rnumeric19' )
		{
			return this.txt_numeric19;
		}	
		
		if(args[0] == 'RPincode' )
		{
			return this.txt_ispincode;
		}	
		
		if(args[0] == 'RExeNum' )
		{
			return this.txt_isexenum;
		}	
		
		if(args[0] == 'RPhone' )
		{
			return this.txt_isphonenumber;
		}	
		
		if(args[0] == 'RAlphaNumSpace' )
		{
			return this.txt_checkreason;
		}	
		
		if(args[0] == 'RDigits' )
		{
			return this.txt_digits;
		}	
		
		if(args[0] == 'RFormaterror' )
		{
			return this.txt_formaterror;
		}	
		
		if(args[0] == 'RAlphabetic' )
		{
			return this.txt_alphabetic;
		}	
		
		if(args[0] == 'REmail' )
		{
			return this.txt_isemail;
		}	
		
		if(args[0] == 'RSpace' )
		{
			return this.txt_isspace;
		}	
		
		if(args[0] == 'RNZNum' )
		{
			return this.txt_isnzint;
		}	
		
		if(args[0] == 'RLimit_1_10' )
		{
			return this.txt_limit_1_10;
		}	
		
		if(args[0] == 'RAmtTransCheck' )
		{
			return this.txt_amttrans;
		}	
		
		if(args[0] == 'RExchangeHours' )
		{
			return this.txt_exchangehours;
		}	
		
		if(args[0] == 'RLimit1_15' )
		{
			return this.txt_limit1_15;
		}	
		
		if(args[0] == 'RLimit1_10000' )
		{
			return this.txt_limit1_10000;
		}	
		
		if(args[0] == 'RCurrency' )
		{
			return this.txt_isnumber;
		}	
		
		if(args[0] == 'RAllowAllMax255' )
		{
			return this.txt_allowallmax255;
		}	
		
		if(args[0] == 'RHexadecimal' )
		{
			return this.txt_isHexadecimal;
		}	
		
		if(args[0] == 'RHomePhoneCheck' )
		{
			return this.txt_isvalidhomephone;
		}	
		
		if(args[0] == 'RNumberPrefix4' )
		{
			return this.txt_numberprefix4;
		}	
		
		if(args[0] == 'WSCCheckAlphaNum' )
		{
			return this.txt_WSCAlphaNum;
		}	
		
		if(args[0] == 'RMax30' )
		{
			return this.txt_max30;
		}	
		
		if(args[0] == 'RFirstAlphabetSpace' )
		{
			return this.txt_isfirstalphabet;
		}	
		
		if(args[0] == 'RAlphanum1' )
		{
			return this.txt_alphanumerical;
		}	
		
		if(args[0] == 'RFeedbackmsg' )
		{
			return this.txt_feedbackmsg;
		}	
		
		if(args[0] == 'RLimit1440' )
		{
			return this.txt_limit1440;
		}	
		
		if(args[0] == 'RSysParamEmail' )
		{
			return this.txt_issysparamemail;
		}	
		
		if(args[0] == 'RFloatAmt' )
		{
			return this.txt_floatamt;
		}	
		
		if(args[0] == 'RBinary' )
		{
			return this.txt_isBinary;
		}	
		
		if(args[0] == 'RNotDot' )
		{
			return this.txt_isnotdot;
		}	
		
		if(args[0] == 'RNoSpace' )
		{
			return this.txt_nospace;
		}	
		
		if(args[0] == 'RLimit1_48' )
		{
			return this.txt_limit1_48;
		}	
		
		if(args[0] == 'RCheckName' )
		{
			return this.txt_isvalidname;
		}	
		
		if(args[0] == 'RCompCheck' )
		{
			return this.txt_checkval;
		}	
		
		if(args[0] == 'RAllowAllMax100' )
		{
			return this.txt_allowallmax100;
		}	
		
		if(args[0] == 'REnterValue' )
		{
			return this.txt_entervalue;
		}	
		
		if(args[0] == 'RZerotoNineP' )
		{
			return this.txt_ZerotoNineP;
		}	
		
		if(args[0] == 'RMatchEid' )
		{
			return this.txt_matcheid;
		}	
		
		if(args[0] == 'RLimit1_4' )
		{
			return this.txt_limit1_4;
		}	
		
		if(args[0] == 'RValidGrpName' )
		{
			return this.txt_isValidGrpName;
		}	
		
		if(args[0] == 'RNZPercentage' )
		{
			return this.txt_isnzpercentage;
		}	
		
		if(args[0] == 'RTaxRate' )
		{
			return this.txt_RTaxRate;
		}	
		
		if(args[0] == 'RCheckConfName' )
		{
			return this.txt_isconfname;
		}	
		
		if(args[0] == 'RChequeNo' )
		{
			return this.txt_ischequeno;
		}	
		
		if(args[0] == 'RINTCHECK' )
		{
			return this.txt_isintrequired;
		}	
		
		if(args[0] == 'RLimitPackageExpire' )
		{
			return this.txt_limitpackageexpire;
		}	
		
		if(args[0] == 'RPinPrefix' )
		{
			return this.txt_pinprefix;
		}	
		
		if(args[0] == 'RNumberPrefix' )
		{
			return this.txt_numberprefix;
		}	
		
		if(args[0] == 'RLimit0_20' )
		{
			return this.txt_limit0_20;
		}	
		
		if(args[0] == 'RInteger' )
		{
			return this.txt_isinteger;
		}	
		
		if(args[0] == 'RPath' )
		{
			return this.txt_path;
		}	
		
		if(args[0] == 'RFLOATCHECK' )
		{
			return this.txt_isfloatrequired;
		}	
		
		if(args[0] == 'RCurrencyRate' )
		{
			return this.txt_checkcurrency;
		}	
		
		if(args[0] == 'RLimit0_3' )
		{
			return this.txt_limit0_3;
		}	
		
		if(args[0] == 'RSmtpPort' )
		{
			return this.txt_smtpport;
		}	
		
		if(args[0] == 'RNum' )
		{
			return this.txt_isint;
		}	
		
		if(args[0] == 'RFLOAT' )
		{
			return this.txt_invalidpercent;
		}	
		
		if(args[0] == 'ROctect' )
		{
			return this.txt_isOctect;
		}	
		
		if(args[0] == 'RPercentage' )
		{
			return this.txt_ispercentage;
		}	
		
		if(args[0] == 'RSearch' )
		{
			return this.txt_forsearch;
		}	
		
		if(args[0] == 'RLimit1_100000' )
		{
			return this.txt_limit1_100000;
		}	
		
		if(args[0] == 'RMinute' )
		{
			return this.txt_isminute;
		}	
		
		if(args[0] == 'RNZCurrency' )
		{
			return this.txt_isnzcurrencyrate;
		}	
		
		if(args[0] == 'RLimit3_90' )
		{
			return this.txt_limit3_90;
		}	
		
		if(args[0] == 'RUserName' )
		{
			return this.txt_username;
		}	
		
		if(args[0] == 'RLimit3_16' )
		{
			return this.txt_limit3_16;
		}	
		
		if(args[0] == 'RLimit1_99999' )
		{
			return this.txt_limit1_99999;
		}	
		
		if(args[0] == 'RLimit0_9' )
		{
			return this.txt_limit0_9;
		}	
		
		if(args[0] == 'RNumberPrefix1' )
		{
			return this.txt_numberprefix1;
		}	
		
		if(args[0] == 'WSCCheckPwd' )
		{
			return this.txt_WSCPwd;
		}	
		
		if(args[0] == 'RZCurrency' )
		{
			return this.txt_isznumber;
		}	
		
		if(args[0] == 'RPULSE' )
		{
			return this.txt_pulse;
		}	
		
		if(args[0] == 'RValidUser' )
		{
			return this.txt_validateuser;
		}	
		
		if(args[0] == 'Rminmaxdefault' )
		{
			return this.txt_minmaxdefault;
		}	
		
		if(args[0] == 'RPosNegNum' )
		{
			return this.txt_isposnegint;
		}	
		
		if(args[0] == 'RNZCurrency' )
		{
			return this.txt_isnznumber;
		}	
		
		if(args[0] == 'RLimit_1_10000' )
		{
			return this.txt_limit_1_10000;
		}	
		
		if(args[0] == 'RCheckNumeric' )
		{
			return this.text_ischeckint;
		}	
		
		if(args[0] == 'RNZDay' )
		{
			return this.txt_isnzday;
		}	
		
		if(args[0] == 'RPinCodeCheck' )
		{
			return this.txt_isvalidpincode;
		}	
		
		if(args[0] == 'RCCPrefix' )
		{
			return this.txt_creditcardprefix;
		}	
		
		if(args[0] == 'RZerotoNine' )
		{
			return this.txt_ZerotoNine;
		}	
		
		if(args[0] == 'RIPaddrCheck' )
		{
			return this.txt_isvalidftpaddr;
		}	
		
		if(args[0] == 'RFaxPhoneCheck' )
		{
			return this.txt_isvalidfaxnum;
		}	
		
		if(args[0] == 'RSysParamNum' )
		{
			return this.txt_issysparamnumeric;
		}	
		
		if(args[0] == 'RValidUsernm' )
		{
			return this.txt_validateusername;
		}	
		
		if(args[0] == 'RConfName' )
		{
			return this.txt_mastername;
		}	
		
		if(args[0] == 'RLimit0_200' )
		{
			return this.txt_limit0_200;
		}	
		
		if(args[0] == 'RGreaterthan_equalto_0' )
		{
			return this.txt_greaterthan_equalto_0;
		}	
		
		if(args[0] == 'RFileTypeCSV' )
		{
			return this.txt_filetypecsv;
		}	
		
		if(args[0] == 'RLimit0_10000' )
		{
			return this.txt_limit0_10000;
		}	
		
		if(args[0] == 'RAllowAllMax20' )
		{
			return this.txt_allowallmax20;
		}	
		
		if(args[0] == 'RPinPrefixCheck' )
		{
			return this.txt_ispinprefix;
		}	
		
		if(args[0] == 'RLimit0_100' )
		{
			return this.txt_limit0_100;
		}	
		
		if(args[0] == 'RPINPrefix1' )
		{
			return this.txt_checkpinprefix;
		}	
		
		if(args[0] == 'RCpeIdRe' )
		{
			return this.txt_cpeid;
		}	
		
	}
}

var regex = new DefaultRegularExpression();
/**** End of Local Specific Regular Expressions *******/



/**
*
*  UTF-8 data encode / decode
*  http://www.webtoolkit.info/
*
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
function validateOnKeyPress(e,reg)
{
	var key = window.event ? e.keyCode : e.which;
	var keychar = String.fromCharCode(key);
	return reg.test(keychar);
}

function isNull(value)
{
	myvalue1=righttrim(value);
	myvalue2=lefttrim(myvalue1);
	return (myvalue2.length == 0)
}

function validateRegEx()
{
	var args=validateRegEx.arguments;
	data = args[1];
			
	if (!args[0].test(data))
	{
		return false;
	}
	else
		return true;
}
function isExists(arr,str)
{
	found = false;
	for(var cnt=0;cnt<arr.length;cnt++)
	{
		if(arr[cnt] == str)
			found = true;
	}		
	return found;
}

function errorMessage(str,flag)
{
	if(flag==true)	
		alert(str);
}

/*
	validate()
	arg1 : Validation criteria like 'RDropMenu','RCheck'	
	arg2 : Error Message
	arg3 : Form Field Name		
	arg4 : Field Caption 
	arg5 : Give Alert or not (True/False)
	       If True give alert
	       If False no alert
	       By Default True 	
	arg6 : Field or Variable(true/false)
	       If True the arg3 is Field name in Form
	       If False arg3 is variable in java script
	       By Default arg5 is true 	       
*/
function validate()
{
	var args=validate.arguments;
	//Allows integer 0 or greater than 0
	nre= /^\d+$/;// 'RNum'
	//Currency 13,2 which allows 0.0 also
	currencyre=/^\d{1,10}((\.\d{1,2}$)|$)/; //'RCurrency'
	//Checks Email
	emailre = /^\w+(\-\w+)*(\.\w+(\-\w+)*)*@\w+(\-\w+)*(\.\w+(\-\w+)*)+$/;//'REmail'
	//Phone Number
	phonere = /^[1-9]\d{2}\-\s?\d{3}\-\d{4}$/;//'RPhone'
 	MinVal=0; //Minimum value to be accepted for RMinMax...change according to requirements
	MaxVal=3;
	doAlert = true ;
	isField = true ;
	
	if(args[4] != null)
	{
		if(args[4] == false)
		{
			doAlert = false;
		}	
	}
	else
		doAlert = true;				

	if(args[5] != null)
	{
		if(args[5] == false)
			isField = false;
	}		
	else
		isField = true;
	
	
	criteria = args[0];		
	var customValidations = new Array( 'RDropMenu' , 'RCheck' ,'RRadio','RList','RNull');
	if(isExists(customValidations,criteria))	
	{
		if (args[0]!='RDropMenu' && args[0]!='RCheck'&& args[0]!='RRadio'&& args[0]!='RList' && args[0]!='RPhone')
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
				errorMessage(args[3] +' '+args[1] , doAlert);					
				setFocusSelection(args[2]);
				return false;
			}
			else
			{
				return true;
			}
		}
		else if (args[0]=='RDropMenu')  //Check for focus
		{
			FldExpValMenu=eval(formName + args[2]).selectedIndex;
			if (FldExpValMenu==0)
			{
				errorMessage(args[3] +' '+args[1] , doAlert);					
				setFocusSelection(args[2]); 
				return false;
			}
			else
			{
				return true;
			}
		}
		else if(args[0]=='RCheck')
		{
			isChecked=eval(formName + args[2]).checked;
			if(!isChecked)
			{
				errorMessage(args[3] +' '+args[1] , doAlert);					
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
				errorMessage(args[3] +' '+args[1] , doAlert);					
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
				errorMessage(args[3] +' '+args[1] , doAlert);					
				return false;
			}
			else
			{
				return true;
			}
		}	
		/* Not in Use */
		else if (args[0]=='RPhone')
		{
			FldExpVal=eval(formName + args[2]).value;
			if(!phonere.test(FldExpVal))
			{
				errorMessage(args[3] +' '+args[1] , doAlert);									
				setFocusSelection(args[2]);
				return false;
			}
			else
			{
				return true;
			}
		}	
		/* No in Use */
		else if (args[0]=='RSSN')
		{
			if (isNull(FldExpVal))
			{
				errorMessage(args[i+1] + requiredCaption , doAlert);									
				FldExpFoc;
				return false;
			}
			var matchArr = FldExpVal.match(/^(\d{3})-?\d{2}-?\d{4}$/);
			var numDashes = FldExpVal.split('-').length - 1;
			if (matchArr == null || numDashes == 1) 
			{
				errorMessage("Invalid " + args[i+1] + ". Must be 9 digits.", doAlert);													
				FldExpFoc;
				return false;
			}
			else if (parseInt(matchArr[1],10)==0) 
			{
				errorMessage("Invalid " + args[i+1] + " : SSN can\'t start with 000.", doAlert);
				FldExpFoc;
				return false;
			}
		}
		
		/* Not in Use */
		else if (args[0]=='0')
		{
			if (isNull(FldExpVal))
			{
				errorMessage(args[i+1] + requiredCaption, doAlert);				
				FldExpFoc;
				return false;
			}
			if((FldExpLen<MinVal)||(FldExpLen>MaxVal))
			{
				errorMessage(args[i+1]+' should be of '+ MinVal +' to '+ MaxVal +' characters.', doAlert);								
				FldExpFoc;
				return false;
			}
		}
		/* Not in Use */
		else if (args[0]=='RSysParamNum')
		{
			if(!isNull(FldExpVal))
			{
				if (!nre.test(FldExpVal))
				{
					errorMessage(args[3] +' '+args[1] , doAlert);										
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
				errorMessage(args[3] +'is required' , doAlert);
				setFocusSelection(args[2]);
				return false;
			}
		}
		/* Not in Use */
		else if (args[0]=='RSysParamCurrency')
		{
			if(!isNull(FldExpVal))
			{
				if(!currencyre.test(FldExpVal))
				{
					errorMessage(args[3] +' '+args[1] , doAlert);										
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
				errorMessage(args[3] +' '+args[1] , doAlert);									
				setFocusSelection(args[2]);
				return false;
			}
		}
		/* Not in Use */
		else if (args[0]=='RSysParamEmail')
		{
			
			if(!isNull(FldExpVal))
			{
			
				if (!emailre.test(FldExpVal))
				{
					errorMessage(args[3] +' '+args[1] , doAlert);					
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
				errorMessage(args[3] +' '+'is required' , doAlert);
				setFocusSelection(args[2]);
				return false;
			}
		}		
	}
	else
	{
		
		//If Field is passed for validation
		if(isField)
		{
			FldExpVal=eval(formName + args[2]).value;
			//FldExpFoc=eval(formName + args[2]).focus();
			//if(document.getElementById(args[2]).nodeName.toUpperCase() != 'SELECT'){
		//	FldExpSel=eval(formName + args[2]).select();
		//	}
			FldExpLen=eval(formName + args[2]).value.length;
		}
		else
			FldExpVal = args[2] + '';	
		
		/* Edited by dhavalpatel					  */
		/* If Field is null then just return without validating any thing */
		if(isNull(FldExpVal))
			return true;

		
		regEx = regex.getRegEx(args[0]);

		if(regEx != null)
		{
			if (!regEx.test(FldExpVal))
			{
				
				errorMessage(args[3] +' '+args[1] , doAlert);
				//If Field is passed for validation 
				if(isField)
				{
					setFocusSelection(args[2]);
					FldExpFoc=eval(formName + args[2]).focus();
					if(document.getElementById(args[2]).nodeName.toUpperCase() != 'SELECT')
					{
						FldExpSel=eval(formName + args[2]).select();
					}
				}	
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
/*
	performValidate()
	arg1 : Validation criteria like 'RDropMenu','RCheck'	
	arg2 : Error Message
	arg3 : Form Field Name		
	arg4 : Field Caption 
	arg5 : Give Alert or not (True/False)
	       If True give alert
	       If False no alert
	       By Default True 	
	arg6 : Field or Variable(true/false)
	       If True the arg3 is Field name in Form
	       If False arg3 is variable in java script
	       By Default arg5 is true 	   
*/
	       
function performValidate()
{
	
	var args=performValidate.arguments;
	
	return validate(args[0],args[1],args[2],args[3],false,true);	
	
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
	//Allows integer 0 or greater than 0 or less than 0
	npnre= /(^-?\d\d*$)/;// 'RPosNegNum'
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
	//amisha patel 
	//Currency greater than or equal to 0.0
	zcurrencyre=/^0*[0-9]\d{0,12}$|^0*[0-9]\d{0,12}\.\d?\d?|^0*\.\d?[0-9]$|^0*\.[0-9]\d?$/;//'RZCurrency'	
	
	
 	//Percentage (5,2)which allows 0.0(till 100 only)
	percentre=/^\d{1,3}((\.\d{1,2}$)|$)/; //'RPercentage'
	//Percentage greater than 0.0
	nzpercentre=/^0*[1-9]\d?$|^0*[1-9]\d?\.\d?\d?$|^0*100\.0?0?$|^0*100$|^0*\.\d?[1-9]$|^0*\.[1-9]\d?$/;//RNZPercentage
	//amisha patel percentage >=0 and <= 100 
	zpercentre=/^0*[0-9]\d?$|^0*[0-9]\d?\.\d?\d?$|^0*100\.0?0?$|^0*100$|^0*\.\d?[0-9]$|^0*\.[0-9]\d?$/;//ZPercentage
	
	
	
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
		else if (args[0]=='RPosNegNum')
		{
			if(!isNull(FldExpVal))
			{
				if (!npnre.test(FldExpVal))
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
		else if (args[0]=='RZCurrency')
		{
			if(!isNull(FldExpVal))
			{
				if(!zcurrencyre.test(FldExpVal))
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
		else if (args[0]=='ZPercentage')
		{//amisha patel 
			if(!isNull(FldExpVal))
			{
				if(!zpercentre.test(FldExpVal))
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
	if(document.all){
		dateObject.focus();
	}
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
				if(document.all){
					firstDateObject.focus();
				}
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
					if(document.all){
						firstDateObject.focus();
					}
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
					if(document.all){
						secondDateObject.focus();
					}
					bool = "true";
					return false;
				}
				if(bool == "false"){
					var firstDateLong = getLongDateFromFormat(firstDateObject.value,dateFormat,"false");
					var secondDateLong = getLongDateFromFormat(secondDateObject.value,dateFormat,"false");
					var dateDiff = secondDateLong - firstDateLong;
					if(dateDiff < 0){
						alert(captionName +" should be greater or equal to from date.");
						if(document.all){
							secondDateObject.focus();
						}
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
