var functions = {"tolower(arg1)":"Converts to lowercase. Supports one argument."
    ,"toupper(arg1)":"Converts to uppercase. Supports one argument."
    ,"abs(arg1)":"Returns the absolute value for the provided argument"
    ,"trim(arg1)":"Returns trimed value for the provided argument"
    ,"concat(arg1)":"Concats the provided Strings"
    ,"strip(\"actual string\",\"L/T\",\"character\")":"Strips the actual string according to the option provided (L/T) and character"
    ,"subString(\"actual string\",\"begin index\", \"[end index]\")":"Returns a substring of the provided string. Supports 2 or 3 arguments"
};


var initialState = new State("Initial State");
var expression = new StateMachine(initialState);
var logicalExpression = new StateMachine(initialState);
var terminalExpression = new StateMachine(initialState);
var notExpression = new StateMachine(initialState);
var lhs = "";
var openBraces = new TerminalState("Opening Paren", "(");
var closeBraces = new TerminalState("Closing paren", ")");
var terminalState = new CompositeState("Terminal", terminalExpression);
var notState = new CompositeState("Not Exp", notExpression);
var notKeyword = new StaticValuedState("NOT", "Inverts the value of the expression", "NOT");
var expressionState = new CompositeState("Logical Exp",expression);


function createModelForDictionary(keys){
    var suggestionState = new DynamicValuedState("Key LHS", /^[a-zA-Z0-9][0-9A-Za-z\_.:\-]*$/,
        function(value){
            var suggestion = [];
            for(var val in keys){
                var attributeValue = keys[val]["attributeName"];
                if(attributeValue.toLowerCase().indexOf(value.toLowerCase()) != -1){
                    suggestion.push(new Suggestion(attributeValue, attributeValue, val));
                }
            }
            for(var functionName in functions){
                if(functionName.toLowerCase().lastIndexOf(value.toLowerCase(),0) === 0){
                    suggestion.push(new Suggestion(functionName, functions[functionName], functionName));
                }
            }
            return suggestion;
        },
        function(label){
            lhs = label;
        });

    var dotOperator = new TerminalState("Dot Operator", ".");
    terminalExpression.addTransition(initialState, suggestionState);
    terminalExpression.addTransition(suggestionState, dotOperator);
    terminalExpression.addTransition(dotOperator,suggestionState);

    notExpression.addTransition(initialState, notKeyword);
    notExpression.addTransition(notKeyword, openBraces);
    notExpression.addTransition(openBraces, suggestionState);
    notExpression.addTransition(suggestionState,dotOperator);
    notExpression.addTransition(dotOperator,suggestionState);
    notExpression.addTransition(suggestionState, closeBraces);
    notExpression.addAcceptingState(closeBraces);


    expression.addTransition(initialState, suggestionState);
    expression.addAcceptingState(suggestionState);
    expression.addTransition(suggestionState, openBraces);
    expression.addTransition(openBraces, suggestionState);
    expression.addTransition(suggestionState, dotOperator);
    expression.addTransition(dotOperator, suggestionState);
    expression.addTransition(suggestionState, closeBraces);
    expression.addAcceptingState(closeBraces);

    logicalExpression.addTransition(initialState, notState);
    logicalExpression.addAcceptingState(notState);
    logicalExpression.addTransition(initialState,expressionState);
    logicalExpression.addAcceptingState(expressionState);
    logicalExpression.addTransition(initialState, terminalState);
    logicalExpression.addAcceptingState(terminalState);

    var autocompleter = new Autocompleter(logicalExpression,null,[" ", "=","|","&","(",")", ",","."]);
    return autocompleter;
}


function createModelForRadiusDictionary(keys){
    var suggestionState = new DynamicValuedState("Key LHS", /^[a-zA-Z0-9][0-9A-Za-z\_.:\-]*$/,
        function(value){
            var suggestion = [];
            for(var val in keys){
                var attributeValue = keys[val].attributeName;
                if(attributeValue.toLowerCase().indexOf(value.toLowerCase()) != -1){
                    suggestion.push(new Suggestion(attributeValue, attributeValue, val));
                }
            }
            for(var functionName in functions){
                if(functionName.toLowerCase().lastIndexOf(value.toLowerCase(),0) === 0){
                    suggestion.push(new Suggestion(functionName, functions[functionName], functionName));
                }
            }
            return suggestion;
        },
        function(label){
            lhs = label;
        });
    terminalExpression.addTransition(initialState, suggestionState);

    notExpression.addTransition(initialState, notKeyword);
    notExpression.addTransition(notKeyword, openBraces);
    notExpression.addTransition(openBraces, suggestionState);
    notExpression.addTransition(suggestionState, closeBraces);
    notExpression.addAcceptingState(closeBraces);


    expression.addTransition(initialState, suggestionState);
    expression.addAcceptingState(suggestionState);
    expression.addTransition(suggestionState, openBraces);
    expression.addTransition(openBraces, suggestionState);
    expression.addTransition(suggestionState, closeBraces);
    expression.addAcceptingState(closeBraces);

    logicalExpression.addTransition(initialState, notState);
    logicalExpression.addAcceptingState(notState);
    logicalExpression.addTransition(initialState,expressionState);
    logicalExpression.addAcceptingState(expressionState);
    logicalExpression.addTransition(initialState, terminalState);
    logicalExpression.addAcceptingState(terminalState);

    var autocompleter = new Autocompleter(logicalExpression);
    return autocompleter;
}


function expressionAutoCompleteForDictionary(id, autocompleter) {
    $("#" + id).autocomplete("option", {
        minLength : 0,
        source : function(request, callback) {
            var context = autocompleter.suggestions(request.term,id);

            var suggestionsInJSON = [];
            var suggestion = Array.flatten(context.suggestions);
            for ( var i = 0; i < suggestion.length; i++) {
                suggestionsInJSON.push(suggestion[i].toJSON());
            }
            callback(suggestionsInJSON);
            if (context.isCompleted()) {
                $(this).removeClass("failure");
                $(this).addClass("success");
            } else {
                $(this).removeClass("success");
                $(this).addClass("failure");
            }
        },
        select : function(event, ui) {
            var cursorpos = $(this).getCursorPosition();
            console.log(" cursor position at select event --> "+cursorpos);
            var afterCursorValue = null;
            var terms = null;
            if(this.value.indexOf(" ") != -1){
                afterCursorValue = this.value.substring(0, this.value.length);
                terms = split_autocompleteBySpace(this.value.substring(0,cursorpos));
            }else{
                afterCursorValue = this.value.substring(0, this.value.length);
                terms = split_autocomplete(this.value.substring(0,cursorpos));
            }
            console.log(" value after cursor position in select block ----> "+afterCursorValue);
            console.log(" terms value in select block----> "+terms.toString());
            terms.pop();
            // add the selected item
            terms.push(ui.item.value);
            if(afterCursorValue.length>=1){
                if(afterCursorValue.indexOf(" ") != -1){
                    var updatedString=terms.join("");
                    this.value = updatedString;
                    $(this).setCursorPosition(updatedString.length);
                }else{
                    this.value = terms.join(".");
                    $(this).setCursorPosition(this.value.length);
                }

            }else{
                this.value = terms.join("");
            }
            console.log("  Expression value after adding suggestion----> "+this.value);
            var context = autocompleter.suggestions(this.value);
            if (context.isCompleted()) {
                $(this).removeClass("failure");
                $(this).addClass("success");
            } else {
                $(this).removeClass("success");
                $(this).addClass("failure");
            }

            return false;
        },
        focus : function(event, ui) {
            event.preventDefault();
        }
    });
    $("#" + id).keydown(function(e){
        console.log(' Key down event fired');
        if(isCtrlKey(e) && e.which===32){
            console.log("in side control");
            $(this).autocomplete("search");
            e.preventDefault();
        }else if(e.which===37 || e.which===39){
            $(this).autocomplete("search");
        }else {
            console.log(' for completion');
            var context = autocompleter.suggestions(this.value);
            if (context.isCompleted()) {
                $(this).removeClass("failure");
                $(this).addClass("success");
            } else {
                $(this).removeClass("success");
                $(this).addClass("failure");
            }
        }
    });

}

function isCtrlKey(e){
    var answer = e.ctrlKey || e.which === 17;
    console.log("Ctrl key pressed: " + answer);
    return answer;
}

function split_autocomplete( val ) {
    return val.split( /\.+/ );
}

function split_autocompleteBySpace( val ) {
    return val.split( /\s+/ );
}

