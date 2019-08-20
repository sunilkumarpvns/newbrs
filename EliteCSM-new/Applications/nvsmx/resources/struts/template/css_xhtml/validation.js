/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var firstFieldErrorPosition = null;

function clearErrorMessagesCSS(form) {
    firstFieldErrorPosition = null;
    // clear out any rows with an "errorFor" attribute
    var i,
        divs = form.getElementsByTagName("div"),
        paragraphsToDelete = [];

    for(i = 0; i < divs.length; i++) {
        var p = divs[i];
        if (p.getAttribute("errorFor")) {
            paragraphsToDelete.push(p);
        }
    }

    // now delete the paragraphsToDelete
    for (i = 0; i < paragraphsToDelete.length; i++) {
        var r = paragraphsToDelete[i];
        var parent = r.parentNode;
        parent.removeChild(r);
    }
}

function clearErrorMessages(form) {
    clearErrorMessagesCSS(form);
}

function clearErrorLabelsCSS(form) {
    // set all labels back to the normal class
    var i,
        labels = form.getElementsByTagName("label");
    for (i = 0; i < labels.length; i++) {
        var label = labels[i];
        if (label) {
            if(label.getAttribute("class") === "errorLabel"){
                label.setAttribute("class", "label");//standard way.. works for ie mozilla
                label.setAttribute("className", "label"); //ie hack cause ie does not support setAttribute
            }
        }
    }
}

function clearErrorLabels(form) {
    clearErrorLabelsCSS(form);
}

function findWWGrpNode(elem) {
    while (elem.parentNode) {
        elem = elem.parentNode;

        if (elem.className && elem.className.indexOf("wwgrp") > -1) {
            return elem;
        }
    }
    return null;
}

function findWWCtrlNode(enclosingDiv) {
   var elems = enclosingDiv.getElementsByTagName("div");
   for(i = 0; i < elems.length; ++i ) {
       if (elems[i].className && ( elems[i].className.indexOf("wwlbl") > -1 || elems[i].className.indexOf("wwctrl") > -1 )) {
           return elems[i];
       }
   }

   elems = enclosingDiv.getElementsByTagName("span");
   for(i = 0; i < elems.length; ++i ) {
       if (elems[i].className && (elems[i].className.indexOf("wwlbl") > -1 || elems[i].className.indexOf("wwctrl") > -1 )) {
           return elems[i];
       }
   }
   return enclosingDiv.getElementsByTagName("span")[0];
}

//find field position in the form
function findFieldPosition(elem) {
    if (!elem.form) {
        alert("no form for " + elem);
    }

    var form = elem.form;
    for (i = 0; i < form.elements.length; i++) {
        if (form.elements[i].name === elem.name) {
            return i;
        }
    }
    return null;
}

function addErrorCSS(e, errorText) {
    try {
        if (!e) {
            return; //ignore errors for fields that are not in the form
        }
        var elem = (e.type ? e : e[0]); //certain input types return node list, while we single first node. I.e. set of radio buttons.
        var enclosingDiv = findWWGrpNode(elem); // find wwgrp div/span

        //try to focus on first field
        var fieldPos = findFieldPosition(elem);
        if (fieldPos !== null && (firstFieldErrorPosition === null || firstFieldErrorPosition > fieldPos)) {
            firstFieldErrorPosition = fieldPos;
        }

        if (!enclosingDiv) {
           alert(errorText);
           
            return;
        }

        var label = enclosingDiv.getElementsByTagName("label")[0];
        if (label) {
        /*	var labelClass = label.getAttribute("class");
            label.setAttribute("class", labelClass + "errorLabel"); //standard way.. works for ie mozilla
            label.setAttribute("className", "errorLabel"); //ie hack cause ie does not support setAttribute
        */}

        var firstCtrNode = findWWCtrlNode(enclosingDiv); // either wwctrl_ or wwlbl_

        var error = document.createTextNode(errorText);
        var errorDiv = document.createElement("div");

        
        errorDiv.setAttribute("class", "errorMessage");//standard way.. works for ie mozilla
        errorDiv.setAttribute("className", "errorMessage");//ie hack cause ie does not support setAttribute
        errorDiv.setAttribute("errorFor", elem.id);
        errorDiv.appendChild(error);
        var elementClass = e.getAttribute("class");
        e.setAttribute("class", elementClass);
        if(!firstCtrNode && navigator.appName === 'Microsoft Internet Explorer') {
          insertAfter(errorDiv, enclosingDiv);
        } else {
        	insertAfter(errorDiv, enclosingDiv);
        }
    } catch (err) {
        alert("An exception occurred: " + err.name + ". Error message: " + err.message);
    }
}

function addError(e, errorText) {
    addErrorCSS(e, errorText);
}

//focus first element
var StrutsUtils_showValidationErrors = StrutsUtils.showValidationErrors;
StrutsUtils.showValidationErrors = function(form, errors) {
    StrutsUtils_showValidationErrors(form, errors);
    if (firstFieldErrorPosition !== null && form.elements[firstFieldErrorPosition].focus) {
        form.elements[firstFieldErrorPosition].focus();
    }
};

function insertAfter(newElement,targetElement) {
    var parent = targetElement.parentNode;

    //if the parents lastchild is the targetElement...
    if(parent.lastchild == targetElement) {
        parent.appendChild(newElement);
    }else{
        parent.insertBefore(newElement, targetElement.nextSibling);
    }
}
