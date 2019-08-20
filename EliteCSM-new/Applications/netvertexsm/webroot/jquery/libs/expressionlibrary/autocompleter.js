/*
TODOs:
1) Eat the starting spaces when the value is submitted to the stream - DONE
2) Logging of each and every call so that debugging becomes easier
3) Dynamic valued node implementation - DONE
4) Variable input state implementation - like string, Int where suggestions are not possible but you just want to validate the input - DONE
5) GUI builder
6) Handle backspace - DONE
7) Think about caching if possible
8) Syntax validation - DONE
9) Add the message in context if the state is in error like Parser gives
10) Dynamic Input state and variable input state should not accept registered keywords
*/

RegExp.quote = function(str) {
     return str.replace(/([.?*+^$[\]\\(){}|-])/g, "\\$1");
 };
 
Array.flatten = function(source){
    return $.map(source, function recurs(n) {
        return ($.isArray(n) ? $.map(n, recurs): n);
    });
};

var StateMachine = Class.create();
StateMachine.prototype = {
    initialize: function(initialState){
        this.table = {};
        this.initialState = initialState;
        this.acceptingStates = [];
    },
    addTransition:  function(state, nextState){
        if(this.table[state.getName()] == null){
            var transitions = {};
            transitions[nextState.getName()]= nextState;
            this.table[state.getName()] = transitions;
        }else{
            this.table[state.getName()][nextState.getName()] = nextState;
        }
    },
    
    suggestions:    function(presentContext, stream){
        this.process(presentContext, stream, this.nextTransitions(this.initialState));
    },
    
    process:    function(parentContext, stream, transitions){
        parentContext.log("Process of: " + parentContext.getName() + " called");
        var self = this;
        $.each(transitions, function(index, state) {
            var childContext = parentContext.newContext(state.getName());
            state.suggestions(childContext, stream);
            parentContext.addSuggestion(childContext.getSuggestion());
            if(childContext.isTokenConsumed()){
                parentContext.setTokenConsumed(true);
                if(childContext.isCompleted()) {
                    var accepted = $.inArray(state, self.acceptingStates) >= 0;
                    parentContext.setCompleted(accepted);
                    self.process(parentContext, stream, self.nextTransitions(state));
                }
                return false;
            }
        });
    },
    
    nextTransitions:    function(state){
        var possibleStates = [];
    
        for(var transition in this.table[state.getName()]){
            possibleStates.push(this.table[state.getName()][transition]);
        }
        
        return possibleStates;
    },
    
    addAcceptingState:      function(state){
        this.acceptingStates.push(state);
    }
};

var State = Class.create();

State.prototype = {
        initialize: function(name){
            this.name = name;
        },
        
        suggestions: function(myContext, stream){
            return "";
        },
        
        getName:       function(){
            return this.name;
        }
};


var StaticValuedState = Class.create();

StaticValuedState.prototype = Object.extend(new State(), {
    initialize: function(name, description, value){
        this.name = name;
        this.value = value;
        this.suggestion = new Suggestion(name,description,value);
    },
    
    suggestions: function(myContext, stream){
        myContext.log("Suggestions of: " + myContext.getName() + " called");
        
        stream.mark();
        
        for(var i = 0; i < this.value.length; i++){
            
            if(stream.hasNext() === false){
                myContext.addSuggestion(this.suggestion);
                stream.reset();
                return;
            }
            
            if(this.value[i] != stream.next()){
                stream.reset();   
                return;
            }
        }
        
        //control reaches here means the whole word is matched
        
        if(stream.hasNext() === false){
            myContext.addSuggestion(this.suggestion);
            stream.reset();
            return;
        }
        
        if(stream.isTerminal()){
            stream.eatWhitespace();
            myContext.setTokenConsumed(true);
            myContext.setCompleted(true);
        }else{
            stream.reset();
        }
    }
});

var TerminalState = Class.create();
TerminalState.prototype = Object.extend(new State(), {
    initialize:    function(name, terminal){
        this.name = name;
        this.terminal = terminal;
        this.suggestion = new Suggestion(this.terminal, undefined, this.terminal);
    },
    
    suggestions:   function(myContext, stream){
        myContext.log("Suggestions of: " + myContext.getName() + " called");
        
        if(stream.EOS()){
            myContext.addSuggestion(this.suggestion);
            return;
        }
        
        stream.mark();
        
        for(var i = 0; i < this.terminal.length; i++){
            
            if(stream.hasNext() === false){
                myContext.addSuggestion(this.suggestion);
                stream.reset();
                return;
            }
            
            if(this.terminal[i] != stream.next()){
                stream.reset();   
                return;
            }
        }
        
        //reaches here which means the terminal is matched
        stream.eatWhitespace();
        myContext.setTokenConsumed(true);
        myContext.setCompleted(true);
    }
});

//does not add any suggestion, just eats the input if validated by the regex
var VariableInputState = Class.create();
VariableInputState.prototype = Object.extend(new State(), {
    initialize: function(name, regx, terminals){
            this.name = name;
            this.regx = new RegExp(regx);
            this.terminals = terminals;
    },
        
    suggestions: function(myContext, stream){
        myContext.log("Suggestions of: " + myContext.getName() + " called");
        
        if(stream.EOS()){
            return;
        }
        
        stream.mark();
        
        var word = stream.next();
        while (stream.hasNext()) {
            if (stream.isTerminal(this.terminals)) {
                break;
            }
            word += stream.next();
        }
          
        if (this.regx.test(word)) {
            myContext.setTokenConsumed(true);
            myContext.setCompleted(true);
            stream.eatWhitespace();
        }
        else {
            //word incomplete here
            stream.reset();
        }
    } 
});

var CompositeState = Class.create();
CompositeState.prototype = Object.extend(new State(), {
    initialize: function(name, machine){
        this.name = name;
        this.statemachine = machine;
    },
    
    suggestions:   function(context, stream){
        context.log("Suggestions of: " + context.getName() + " called");
        this.statemachine.suggestions(context, stream);
    }
});

var DynamicValuedState = Class.create();
DynamicValuedState.prototype = Object.extend(new State(),{
      initialize:   function(name, regx, suggestionCallback,completionCallback){
          this.name = name;
          this.regx = new RegExp(regx);
          this.suggestionCallback = suggestionCallback;
          this.completionCallback = completionCallback;
      },
      
      suggestions: function(myContext, stream){
          myContext.log("Suggestions of: " + myContext.getName() + " called");
          
           if(stream.EOS()){
              myContext.addSuggestion(this.suggestionCallback.call(undefined, ""));
              return;
          }
          
          stream.mark();
          
          var word = stream.next();
          while(stream.hasNext()){
              if(stream.isTerminal()){
                  break;
              }
              word += stream.next();
          }
          
          if(stream.hasNext()){
            if(stream.isTerminal() === false){
                myContext.addSuggestion(this.suggestionCallback.call(undefined, word));
            }   
          }else{
            myContext.addSuggestion(this.suggestionCallback.call(undefined, word));
          }
          
            //word complete here
          if (this.regx.test(word)) {
              myContext.setTokenConsumed(true);
              myContext.setCompleted(true);
              stream.eatWhitespace();
              if(this.completionCallback != undefined){
                this.completionCallback.call(undefined, word);
              }
          }else {
              stream.reset();
          }
      }
});

var StringState = Class.create();
StringState.prototype = Object.extend(new State(), {
    initialize: function(name){
        this.name = name;
        this.suggestion = new Suggestion("\"\"", "A String literal", "\"\"");
    },
    
    suggestions:   function(myContext, stream){
        myContext.log("Suggestions of: " + myContext.getName() + " called");
        
        if(stream.EOS()){
              myContext.addSuggestion(this.suggestion);
              return;
        }
        
        stream.mark();
        
        if(stream.next() === "\""){
            var closed = false;
            while(stream.hasNext()){
                var next = stream.next();
                if(next === "\\"){
                    if(stream.hasNext()){
                        stream.next();
                    }
                }else if(next === "\""){
                    closed = true;
                    break;
                }
            }
            if(closed){
                myContext.setTokenConsumed(true);
                myContext.setCompleted(true);
                stream.eatWhitespace();
            }else{
                myContext.setTokenConsumed(true);
            }
        }else{
            stream.reset();
        }
    }
});

var Stream = Class.create();
Stream.prototype = {
    initialize: function(value, terminals){
        this.value = this.removeExtraWhiteSpaces(value);
        this.currentIndex = 0;
        this.valueLength = this.value.length;
        this.terminals = terminals;
        this.markPoint = 0;
        this.eatWhitespace();
    },
    removeExtraWhiteSpaces: function(value){
        //replaces all kinds of whitespaces including tab,newline with ' '
        return value.replace(/\s{2,}/g, ' ');
    },
    hasNext:    function(){
        return this.currentIndex < this.value.length;
    },
    next:   function(){
        return this.value[this.currentIndex++];
    },
    isTerminal: function(){
        
        var isTerminal = false;
        for(var i = 0; i < this.terminals.length; i++){
            if(this.terminals[i] === this.lookAhead()){
                isTerminal = true;
                return isTerminal;
            }
        }
    },
    lookAhead:  function(){
        return this.value[this.currentIndex];  
    },
    mark:   function(){
        this.markPoint = this.currentIndex;
    },
    reset:  function(){
        this.currentIndex = this.markPoint;
    },
    eatWhitespace:  function(){
        while(this.lookAhead() === " "){
            this.next();
        }
    },
    EOS:    function(){
        return this.hasNext() === false;
    }
};

var Context = Class.create();
Context.prototype = {
    initialize: function(name,debug){
        this.name = name;
        this.tokenConsumed = false;
        this.completed = false;
        this.suggestions = [];
        this.debug = debug;
    },
    
    getName:    function(){
        return this.name;
    },
    
    addSuggestion:  function(suggestion){
        this.suggestions.push(suggestion);
    },
    
    getSuggestion:  function(){
        return this.suggestions;
    },
    
    isTokenConsumed:  function(){
        return this.tokenConsumed;
    },
    
    setTokenConsumed:   function(value){
        this.tokenConsumed = value;
    },
    
    isCompleted:    function(){
        return this.completed;
    },
    
    setCompleted:   function(value){
        this.completed = value;
        this.log("completed of Context " + this.name + " is set to --> " + value); 
    },
    
    newContext: function(name){
        return new Context(name, this.debug);
    },
    
    log:    function(message){
        if(this.debug){
            console.log(message);
        }
    }
};

var Suggestion = Class.create();
Suggestion.prototype = {
    initialize: function(label, description, value){
        this.label = label;
        this.description = description; //will be shown if delay of 1 sec on any autocomplete suggestion, like eclipse
        this.value = value;
    },
    
    toJSON: function(){
        return {label: this.label, value: this.value, description: this.description};
    }
};
var cursorposition;
var Autocompleter = Class.create();
Autocompleter.prototype = {
    initialize: function(model,debug){
        this.statemachine = model;
        this.debug=debug;
    },
    suggestions: function(value,id){
     console.log(" Enter:------------------------------------->"+id);
     console.log("id---->"+id);
     if(typeof(id)!== 'undefined'){
    	 var cursorposition=0;
         var el = $('#'+id).get(0);
        var pos = 0;
        if('selectionEnd' in el) {
            pos = el.selectionEnd;
        } else if('selection' in document) {
            el.focus();
            var Sel = document.selection.createRange();
            var SelLength = document.selection.createRange().text.length;
            Sel.moveStart('character', -el.value.length);
            pos = Sel.text.length - SelLength;
        }
        cursorposition=pos;
        console.log('cursorposition-----------> '+cursorposition);
        value=value.substring(0,cursorposition);
        console.log('value before cursorposition in suggestion block-----------> '+value);
       }
       console.log("Value in suggestion block "+value);
       var stream = new Stream(value, [" ", "=","|","&","(",")", ","]);
        var smContext = new Context("Logical exp context");
        this.statemachine.suggestions(smContext, stream);
        console.log("Exit:-------------------------------------");
        return smContext;
    }
};

$.fn.getCursorPosition = function() {
	console.log('test inside cursor position function');
	 var el = $(this).get(0);
     var pos = 0;
     if('selectionEnd' in el) {
    	 console.log('test inside cursor position function if block');
      return   pos = el.selectionEnd;
     } else if('selection' in document) {
    	 console.log('test inside cursor position function else block');
         el.focus();
         var Sel = document.selection.createRange();
         var SelLength = document.selection.createRange().text.length;
         Sel.moveStart('character', -el.value.length);
         return pos = Sel.text.length - SelLength;
     }
};
   /* var input = this.get(0);
    if (!input) return; // No (input) element found
    if ('selectionEnd' in input) {
        // Standard-compliant browsers
        return input.selectionStart+1;
    } else if (document.selection) {
        // IE
        input.focus();
        var sel = document.selection.createRange();
        var selLen = document.selection.createRange().text.length;
        sel.moveStart('character', -input.value.length);
        return sel.text.length - selLen;
    }
*/
$.fn.setCursorPosition = function(position){
    if(this.length == 0) return this;
    return $(this).setSelection(position, position);
};

$.fn.setSelection = function(selectionStart, selectionEnd) {
    if(this.length == 0) return this;
    input = this[0];

    if (input.createTextRange) {
        var range = input.createTextRange();
        range.collapse(true);
        range.moveEnd('character', selectionEnd);
        range.moveStart('character', selectionStart);
        range.select();
    } else if (input.setSelectionRange) {
        input.focus();
        input.setSelectionRange(selectionStart, selectionEnd);
    }

    return this;
};