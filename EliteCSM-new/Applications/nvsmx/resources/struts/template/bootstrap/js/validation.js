/*!
 * validation.js
 *
 * Client Validation for Bootstrap Forms
 *
 * Requires use of jQuery.
 * Tested with jQuery 1.7
 *
 * Copyright (c) 2012 Johannes Geppert http://www.jgeppert.com
 *
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 *
 */


function bootstrapValidation(form, errors) {

    "use strict";

    // Clear existing errors on submit
    form.find("div.has-error").removeClass("has-error");
    form.find("div.has-feedback").removeClass("has-feedback");
    form.find("span.s2_help_inline").remove();
    form.find("span.s2_feedback").remove();
    form.find("div.s2_validation_errors").remove();

    //Handle non field errors
    if (errors.errors && errors.errors.length > 0) {
        var errorDiv = $("<div class='alert alert-danger s2_validation_errors'></div>");
        form.prepend(errorDiv);
        $.each(errors.errors, function(index, value) {
            errorDiv.append('<p>' + value + '</p>\n');
        });
    }

    //Handle field errors
    if (errors.fieldErrors) {
        $.each(errors.fieldErrors, function(index, value) {
        	alert(value);
            var element = form.find(":input[name=\"" + index + "\"]"), controlGroup, controls;
            if (element && element.length > 0) {

            // select first element
                element = $(element[0]);
                controlGroup = element.closest("div.form-group");
                controlGroup.addClass('has-error');
                controlGroup.addClass('has-feedback');
                controls = controlGroup.find("div.controls");
                if (controls) {
                    if(!(element.is(':radio') || element.is(':checkbox'))) {
                        controls.append("<span class='glyphicon glyphicon-remove form-control-feedback s2_feedback'></span>");
                    }
                    controls.append("<span class='help-block s2_help_inline'>" + value[0] + "</span>");
                }
            }
        });
    }
}
function clearErrorMessages(form){
/*	var formvar = $("#".concat(form.id));
    formvar.find("div.has-error").removeClass("has-error");
    formvar.find("div.has-feedback").removeClass("has-feedback");
    formvar.find("span.s2_help_inline").remove();
    formvar.find("span.s2_feedback").remove();
    formvar.find("div.s2_validation_errors").remove();*/
	$( ".has-error" ).removeClass("has-error");
	$( ".has-success" ).removeClass("has-success");
	$( ".has-feedback" ).removeClass("has-feedback");
	$( ".alert" ).remove();	$(".nv-input-error").removeClass("nv-input-error");
	$(".glyphicon-remove").remove();
	$(".alert-danger").remove();	
	$(".removeOnReset").remove();
}

function clearErrorLabels(form){}

function setError(elementid, errorText) {
	var curElement = $("#".concat(elementid)); 
	var parentElement = curElement.parent();
	if(parentElement.parent().hasClass("has-error has-feedback") == false){
		parentElement.parent().addClass("has-error has-feedback");
		parentElement.append("<span class=\"glyphicon glyphicon-remove form-control-feedback removeOnReset\"></span>");
		parentElement.append("<span class=\"help-block alert-danger removeOnReset\">".concat(errorText).concat("</span>"));
	}
}

function setErrorOnElement(element,errorText) {
    var curElement = element;
    var parentElement = curElement.parent();
    if(parentElement.parent().hasClass("has-error has-feedback") == false){
        parentElement.parent().addClass("has-error has-feedback");
        parentElement.append("<span class=\"help-block alert-danger removeOnReset\">".concat(errorText).concat("</span>"));
    }
}

function setSuccess(elementid) {
	var curElement = $("#".concat(elementid)); 
	var parentElement = curElement.parent();
	parentElement.addClass("has-success has-feedback");
	parentElement.append("<span class=\"glyphicon glyphicon-ok form-control-feedback removeOnReset\"></span>");	
}

function addRequired(element){
	var curElement = $("#lbl_".concat(element.id));
	if($("#lbl_".concat(element.id)).length){
		curElement.html(curElement.html().concat(" <span class=\"required\">*</span>"));
	}
}