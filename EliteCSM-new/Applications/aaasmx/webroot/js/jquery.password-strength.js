/*
Password Strength Indicator using jQuery and XML

By: Bryian Tan (bryian.tan at ysatech.com)
01122011 - v01.01.00

Description:
Password Strength Indicator somewhat similar to ASP.NET AJAX PasswordStrength extender control behavior 
and implemented by using jQuery and XML. The password information is stored in an XML file. 
Sample XML file contents:
<PasswordPolicy>
<Password>
<duration>180</duration> //password age, expired in xxx days
<minLength>14</minLength> //password minimum length
<maxLength>25</maxLength> //password maximum length
<numsLength>2</numsLength> //number of required digits  
<upperLength>1</upperLength> //number of required upper case  
<specialLength>1</specialLength> //number of required special characters 
<barWidth>200</barWidth> //the bar indicator width
<barColor>Green</barColor> //the bar indicator colors
<specialChars>!@#\$%*()_+^&amp;}{:;?.</specialChars> //allowable special characters
</Password>
</PasswordPolicy>

Resources:
http://fyneworks.blogspot.com/2007/04/dynamic-regular-expressions-in.html
http://projects.sharkmediallc.com/pass/
http://docs.jquery.com/Plugins/Authoring
http://stackoverflow.com/questions/1034306/public-functions-from-within-a-jquery-plugin
*/


(function($) {

	    var password_Strength = new function() {

	        //return count that match the regular expression
	        this.countRegExp = function(passwordVal, regx) {
	            var match = passwordVal.match(regx);
	            return match ? match.length : 0;
	        }

	        this.getStrengthInfo = function(passwordVal) {
	            var len = passwordVal.length;
	            var pStrength = 0; //password strength
	            var msg = "", inValidChars = ""; //message

	            var isSpecialChar="*|,\":<>[]{}`\';()@&$#%!~+-_=?^.";
	            var isSpecilaChars = new RegExp("[" + isSpecialChar + "]", "g");
	           
	            var nums = this.countRegExp(passwordVal, /\d/g), //numbers
	            alphabets=this.countRegExp(passwordVal, /[a-zA-Z ]/g),
				specials = this.countRegExp(passwordVal, isSpecilaChars); //special characters
	           
	            inValidChars = passwordVal.replace(/[a-z]/gi, "") + inValidChars.replace(/\d/g, "");
	            inValidChars = inValidChars.replace(/\d/g, "");
	            specials=inValidChars.length;
	        	
	            //invalid characters
	            if (inValidChars !== '') {
	            	var strChars=password_settings.specialChars;
	            	for (var i = 0; i <passwordVal.length; i++) {
	            	    if (strChars.indexOf(passwordVal.charAt(i)) != -1){
	            	    	return "Prohibited character: " + passwordVal.charAt(i);
	            	    	specials=specials-1;
	            	    }
	            	}
	            	
	            }

	            //max length
	            if (len > password_settings.maxLength) {
	                return "Password too long!";
	            }

	            //GET NUMBER OF CHARACTERS left
	            if ((specials +alphabets+ nums ) < password_settings.minLength) {
	                msg += password_settings.minLength - (specials + alphabets + nums) + " more characters(Length), ";
	            }

	            //at the "at least" at the front
	            if (specials == 0 || alphabets == 0 || nums == 0) {
	                msg += "At least ";
	            }

	            //GET NUMBERS
	            if (nums >= password_settings.numberLength) {
	                nums = password_settings.numberLength;
	            }
	            else {
	                msg += (password_settings.numberLength - nums) + " more numbers, ";
	            }

	            //special characters
	            if (specials >= password_settings.specialLength) {
	                specials = password_settings.specialLength;
	            }
	            else {
	                msg += (password_settings.specialLength - specials) + " more symbol, ";
	            }

	            //upper case letter
	            if (alphabets >= password_settings.minalphabetsrange) {
	            	alphabets = password_settings.minalphabetsrange;
	            }
	            else {
	                msg += (password_settings.minalphabetsrange - alphabets) + "  more alphabets, ";
	            }

	            //strength for length
	            if ((len - (alphabets + specials + nums)) >= (password_settings.minLength - password_settings.numberLength - password_settings.specialLength - password_settings.minalphabetsrange)) {
	                pStrength += (password_settings.minLength - password_settings.numberLength - password_settings.specialLength - password_settings.minalphabetsrange);
	            }
	            else {
	                pStrength += (len - (alphabets + specials + nums));
	            }

	            //password strength
	            pStrength += alphabets + specials + nums;

	            //strong password
	            if (pStrength == password_settings.minLength ) {
	                msg = "Strong password!";
	            }

	            return msg + ';' + pStrength;
	        }
	    }

	    //default setting
	    var password_settings = {
	        minLength: 12,
	        maxLength: 25,
	        specialLength: 1,
	        minalphabetsRange:4,
	        numberLength: 1,
	        barWidth: 200,
	        barColor: 'Red',
	        specialChars: '()^', //allowable special characters
	        metRequirement: false,
	        useMultipleColors: 0
	    };

	    //password strength plugin 
	    $.fn.password_strength = function(options) {
	        //check if password met requirement
	        this.metReq = function() {
	            return password_settings.metRequirement;
	        }

	                    var _minLength = "10",
	                    _maxLength = "20",
	                    _numsLength = "5",
	                    _specialLength = "2",
	                    _minalphabetsRange="3",
	                    _barWidth = "200",
	                    _barColor = "Green",
	                    _specialChars = "!?.",
	                    _useMultipleColors = "1";

	                    //set variables
	                    password_settings.minLength = parseInt(_minLength);
	                    password_settings.maxLength = parseInt(_maxLength);
	                    password_settings.specialLength = parseInt(_specialLength);
	                    password_settings.numberLength = parseInt(_numsLength);
	                    password_settings.minalphabetsrange=parseInt(_minalphabetsRange);
	                    password_settings.barWidth = parseInt(_barWidth);
	                    password_settings.barColor = _barColor;
	                    password_settings.specialChars = _specialChars;
	                    password_settings.useMultipleColors = _useMultipleColors;
	                    
	        return $('#newPassword').each(function() {
	            //bar position
	            var barLeftPos = $("[id$='newPassword']").position().left + $("[id$='newPassword']").width();
	            var barTopPos = $("[id$='newPassword']").position().top + $("[id$='newPassword']").height();

	            //password indicator text container
	            var container = $('<span></span>')
	            .css({ position: 'absolute', top: barTopPos - 6, left: barLeftPos + 15, 'font-size': '75%', display: 'inline-block', width: password_settings.barWidth + 40 });

	            //add the container next to textbox
	            $(this).after(container);
	            //bar border and indicator div
	            var passIndi = $('<div id="PasswordStrengthBorder"></div><div id="PasswordStrengthBar" class="BarIndicator"></div>')
	            .css({ position: 'absolute', display: 'none' })
	            .eq(0).css({ height: 3, top: barTopPos - 16, left: barLeftPos + 15, 'border-style': 'solid', 'border-width': 1, padding: 2 }).end()
	            .eq(1).css({ height: 5, top: barTopPos - 14, left: barLeftPos + 17 }).end();


	            //add the boder and div
	            container.before(passIndi);

	            $(this).keyup(function() {
	                var passwordVal = $(this).val(); //get textbox value
	                //set met requirement to false
	                password_settings.metRequirement = false;

	                if (passwordVal.length > 0) {

	                    var msgNstrength = password_Strength.getStrengthInfo(passwordVal);

	                    var msgNstrength_array = msgNstrength.split(";"), strengthPercent = 0,
	                    barWidth = password_settings.barWidth, backColor = password_settings.barColor;

	                    //calculate the bar indicator length
	                    if (msgNstrength_array.length > 1) {
	                        strengthPercent = (msgNstrength_array[1] / password_settings.minLength) * barWidth;
	                    }

	                    $("[id$='PasswordStrengthBorder']").css({ display: 'inline', width: barWidth });

	                    //use multiple colors
	                    if (password_settings.useMultipleColors === "1") {
	                        //first 33% is red
	                        if (parseInt(strengthPercent) >= 0 && parseInt(strengthPercent) <= (barWidth * .33)) {
	                            backColor = "red";
	                        }
	                        //33% to 66% is blue
	                        else if (parseInt(strengthPercent) >= (barWidth * .33) && parseInt(strengthPercent) <= (barWidth * .67)) {
	                            backColor = "blue";
	                        }
	                        else {
	                            backColor = password_settings.barColor;
	                        }
	                    }
	                    
	                    $("[id$='PasswordStrengthBar']").css({ display: 'inline', width: strengthPercent, 'background-color': backColor });

	                    //remove last "," character
	                    if (msgNstrength_array[0].lastIndexOf(",") !== -1) {
	                        container.text(msgNstrength_array[0].substring(0, msgNstrength_array[0].length - 2));
	                    }
	                    else {
	                        container.text(msgNstrength_array[0]);
	                    }

	                    if (strengthPercent == barWidth) {
	                        password_settings.metRequirement = true;
	                    }

	                }
	                else {
	                    container.text('');
	                    $("[id$='PasswordStrengthBorder']").css("display", "none"); //hide
	                    $("[id$='PasswordStrengthBar']").css("display", "none"); //hide
	                }
	            });
	        });
	    };

	})(jQuery);
