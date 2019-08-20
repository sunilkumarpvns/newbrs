var functions = {"tolower(arg1)":"Converts to lowercase. Supports one argument."
                ,"toupper(arg1)":"Converts to uppercase. Supports one argument."
                ,"abs(arg1)":"Returns the absolute value for the provided argument"
                ,"trim(arg1)":"Returns trimed value for the provided argument"	
                ,"concat(arg1)":"Concats the provided Strings"
                ,"strip(\"actual string\",\"L/T\",\"character\")":"Strips the actual string according to the option provided (L/T) and character"
                ,"subString(\"actual string\",\"begin index\", \"[end index]\")":"Returns a substring of the provided string. Supports 2 or 3 arguments"
                };

function createModel(keys){
    var initialState = new State("Initial State");
    
    var equal = new TerminalState("Equals", "=");
    var lessThan = new TerminalState("Less Than", "<");
    var greaterThan = new TerminalState("Greater Than", ">");
    
    var comparisonOperators = new StateMachine(initialState);
    comparisonOperators.addTransition(initialState, equal);
    comparisonOperators.addTransition(initialState, lessThan);
    comparisonOperators.addTransition(initialState, greaterThan);
    comparisonOperators.addAcceptingState(equal);
    comparisonOperators.addAcceptingState(lessThan);
    comparisonOperators.addAcceptingState(greaterThan);
    
    var inOperator = new StaticValuedState("IN", "Checks the value within some multiple options", "IN");
    
    var commaOperator = new TerminalState("Comma Operator", ",");
    
    var andOperatorState = new StaticValuedState("AND", "Performs a logical AND among expressions", "AND");
    var orOperatorState = new StaticValuedState("OR", "Performs a logical OR among expressions", "OR");
    
    var stringValueState = new StringState("String");
    var lhs = "";
    
    var lhsIdState = new DynamicValuedState("Key LHS", /^[a-zA-Z_][0-9A-Za-z\._\-]*$/,
                        function(value){
                                var suggestion = [];
                                for(var i = 0; i < keys.length; i++){
                                    if(keys[i].toLowerCase().lastIndexOf(value.toLowerCase(),0) === 0){
                                        suggestion.push(new Suggestion(keys[i], i, keys[i]));
                                    }
                                }
                                for(var functionName in functions){
                                    if(functionName.toLowerCase().lastIndexOf(value.toLowerCase(),0) === 0){
                                        suggestion.push(new Suggestion(functionName, functions[functionName], functionName));
                                    }
                                }
                                return suggestion;
                        },
                        function(value){
                            lhs = value;           
                        });
    
    var rhsIdState = new DynamicValuedState("Key RHS", /^[a-zA-Z_][0-9A-Za-z\._\-]*$/,
                        function(value){
                               /* if(lhs === "CalledStationId"){
                                    return [
                                        new Suggestion("A-B-C-D",undefined,"A-B-C-D"),
                                        new Suggestion("1-2-3-4",undefined, "1-2-3-4")
                                    ];
                                }*/
                                var suggestion = [];
                                for(var functionName in functions){
                                    if(functionName.toLowerCase().lastIndexOf(value.toLowerCase(),0) === 0){
                                        suggestion.push(new Suggestion(functionName, functions[functionName], functionName));
                                    }
                                }
                                return suggestion;
                        });
                        
    
    var logicalExpression = new StateMachine(initialState);
    var unaryExpression = new StateMachine(initialState);
    var terminalExpression = new StateMachine(initialState);
    var notExpression = new StateMachine(initialState);
    var terminalComparisonExpression = new StateMachine(initialState);
    var terminalInExpression = new StateMachine(initialState);
    var bracedExpression = new StateMachine(initialState);
    var lhsExpression = new StateMachine(initialState);
    var rhsExpression = new StateMachine(initialState);
    
    var comparisonOperatorState = new CompositeState("Operators", comparisonOperators);
    var logicalExpState = new CompositeState("Logical Exp", logicalExpression);
    var unaryState = new CompositeState("Unary", unaryExpression);
    var terminalState = new CompositeState("Terminal", terminalExpression);
    var notState = new CompositeState("Not Exp", notExpression);
    var terminalComparisonState = new CompositeState("terminal comparison", terminalComparisonExpression);
    var terminalInState = new CompositeState("terminal in", terminalInExpression);
    var notKeyword = new StaticValuedState("NOT", "Inverts the value of the expression", "NOT");
    var openBraces = new TerminalState("Opening Paren", "(");
    var closeBraces = new TerminalState("Closing paren", ")");
    var bracedExpressionState = new CompositeState("Braced Expression", bracedExpression);
    var lhsExpressionState = new CompositeState("LHS Expression State", lhsExpression);
    var rhsExpressionState = new CompositeState("RHS Expression State", rhsExpression);
    
    lhsExpression.addTransition(initialState, stringValueState);
    lhsExpression.addTransition(initialState, lhsIdState);
    lhsExpression.addAcceptingState(lhsIdState);
    lhsExpression.addAcceptingState(stringValueState);
    lhsExpression.addTransition(lhsIdState, openBraces);
    lhsExpression.addTransition(openBraces, closeBraces);
    lhsExpression.addTransition(openBraces, lhsExpressionState);
    lhsExpression.addTransition(lhsExpressionState, commaOperator);
    lhsExpression.addTransition(commaOperator, lhsExpressionState);
    lhsExpression.addTransition(lhsExpressionState, closeBraces);
    lhsExpression.addAcceptingState(closeBraces);
    
    rhsExpression.addTransition(initialState, stringValueState);
    rhsExpression.addTransition(initialState, rhsIdState);
    rhsExpression.addAcceptingState(rhsIdState);
    rhsExpression.addAcceptingState(stringValueState);
    rhsExpression.addTransition(rhsIdState, openBraces);
    rhsExpression.addTransition(openBraces, closeBraces);
    rhsExpression.addTransition(openBraces, rhsExpressionState);
    rhsExpression.addTransition(rhsExpressionState, commaOperator);
    rhsExpression.addTransition(commaOperator, rhsExpressionState);
    rhsExpression.addTransition(rhsExpressionState, closeBraces);
    rhsExpression.addAcceptingState(closeBraces);
    
    
    terminalComparisonExpression.addTransition(initialState, comparisonOperatorState);
    terminalComparisonExpression.addTransition(comparisonOperatorState, rhsExpressionState);
    terminalComparisonExpression.addAcceptingState(rhsExpressionState);
    
    
    terminalInExpression.addTransition(initialState, inOperator);
    terminalInExpression.addTransition(inOperator, openBraces);
    terminalInExpression.addTransition(openBraces, rhsExpressionState);
    terminalInExpression.addTransition(rhsExpressionState, commaOperator);
    terminalInExpression.addTransition(commaOperator, rhsExpressionState);
    terminalInExpression.addTransition(rhsExpressionState, closeBraces);
    terminalInExpression.addAcceptingState(closeBraces);
    
    terminalExpression.addTransition(initialState, lhsExpressionState);
    terminalExpression.addTransition(lhsExpressionState, terminalComparisonState);
    terminalExpression.addTransition(lhsExpressionState, terminalInState);
    terminalExpression.addAcceptingState(terminalComparisonState);
    terminalExpression.addAcceptingState(terminalInState);
    
    notExpression.addTransition(initialState, notKeyword);
    notExpression.addTransition(notKeyword, openBraces);
    notExpression.addTransition(openBraces, logicalExpState);
    notExpression.addTransition(logicalExpState, closeBraces);
    notExpression.addAcceptingState(closeBraces);
    
    unaryExpression.addTransition(initialState, notState);
    unaryExpression.addAcceptingState(notState);
    unaryExpression.addTransition(initialState, terminalState);
    unaryExpression.addAcceptingState(terminalState);
    
    
    bracedExpression.addTransition(initialState, openBraces);
    bracedExpression.addTransition(openBraces, logicalExpState);
    bracedExpression.addTransition(logicalExpState,closeBraces);
    bracedExpression.addAcceptingState(closeBraces);
    
    
    
    logicalExpression.addTransition(initialState, unaryState);
    logicalExpression.addTransition(initialState, bracedExpressionState);
    logicalExpression.addTransition(unaryState, andOperatorState);
    logicalExpression.addTransition(unaryState, orOperatorState);
    logicalExpression.addTransition(andOperatorState, unaryState);
    logicalExpression.addTransition(orOperatorState, unaryState);
    
    logicalExpression.addTransition(bracedExpressionState, andOperatorState);
    logicalExpression.addTransition(bracedExpressionState, orOperatorState);
    logicalExpression.addTransition(andOperatorState, bracedExpressionState);
    logicalExpression.addTransition(orOperatorState, bracedExpressionState);
    
    logicalExpression.addAcceptingState(unaryState);
    logicalExpression.addAcceptingState(bracedExpressionState);
    var autocompleter = new Autocompleter(logicalExpression);
    return autocompleter;
}


function expresssionAutoComplete(id,autocompleter) {
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
	        var afterCursorValue = this.value.substring(cursorpos, this.value.length);
	        console.log(" value after cursor position in select block ----> "+afterCursorValue);
	        var terms = split_autocomplete(this.value.substring(0,cursorpos));
	        console.log(" terms value in select block----> "+terms.toString());
			//var terms = split(this.value);
			// remove the current input
			terms.pop();
			// add the selected item
			terms.push(ui.item.value);
			if(afterCursorValue.length>=1){
				var updatedString=terms.join(" ");
				this.value = updatedString + afterCursorValue;
				 $(this).setCursorPosition(updatedString.length);
			}else{
				this.value = terms.join(" ");
				// $(this).setCursorPosition(updateString.length);
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
    return val.split( /\s+/ );
}

