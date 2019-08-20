package com.elitecore.test.command.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.commons.base.Optional;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.test.ScenarioContext;
import com.elitecore.test.command.Command;
import com.elitecore.test.command.LoopCommand;

@XmlRootElement(name = "loop")
public class LoopCommandData implements CommandData {
	
	private String ruleSet;
	private Integer count;
	@XmlElementRefs({
		@XmlElementRef(name = "receive-request", type = ReceiveRequestCommandData.class),
		@XmlElementRef(name = "send", type = SendCommandData.class),
		@XmlElementRef(name = "validate", type = ValidationData.class),
		@XmlElementRef(name = "any-order", type = AnyOrderedCommandData.class),
		@XmlElementRef(name = "parallel", type = ParallelCommandData.class),
		@XmlElementRef(name = "loop", type = LoopCommandData.class),
		@XmlElementRef(name = "wait", type = WaitCommandData.class),
		@XmlElementRef(name = "condition", type = ConditionCommandData.class)
	})
	private List<CommandData> commandDatas = new ArrayList<CommandData>();
	private String name ="no-name";
	
	
	@Override
	@XmlAttribute(name="name", required=true)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<CommandData> getCommandDatas() {
		return commandDatas;
	}


	@XmlAttribute(name="ruleset")
	public String getRuleSet() {
		return ruleSet;
	}


	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}


	@XmlAttribute(name="count")
	public Integer getCount() {
		return count;
	}


	public void setCount(Integer count) {
		this.count = count;
	}


	@Override
	public Command create(ScenarioContext context) throws Exception {
		
		List<Command> commands = new ArrayList<Command>();
		for(CommandData commandData : commandDatas){
			commands.add(commandData.create(context));
		}
		
		
		Optional<Integer> count = null;
		if(this.count != null){
			if(this.count < 0){
				throw new IllegalArgumentException("Invalid count value"+count + ", count must be positive numeric value");
			}
			count = Optional.of(this.count);
		} else {
			count = Optional.absent();
		}
		
		Optional<LogicalExpression> expression = null;
		if(this.ruleSet != null){
			expression = Optional.of(Compiler.getDefaultCompiler().parseLogicalExpression(ruleSet));
		} else {
			if(count.isPresent() == false){
				throw new IllegalArgumentException("Invalid count value"+count + ", count must be positive numeric value");
			}
			expression = Optional.absent();
		}
		  
		return new LoopCommand(commands, new LoopCondition(count,expression),name);
	}
	
	public static class LoopCondition {
		
		private Optional<LogicalExpression> expression;
		private Optional<Integer> count;

		public LoopCondition(Optional<Integer> count, Optional<LogicalExpression> expression){
			this.count = count;
			this.expression = expression;
		}
		
		public boolean cotinuee(int count, ValueProvider valueProvider){
			if(this.count.isPresent()){
				if(this.count.get() <= count){
					
					return false;
				}
			}
			
			if(this.expression.isPresent()){
				if(expression.get().evaluate(valueProvider)){
					return true;
				} else {
					return false;
				}
			}
			
			return true;
			
		}

	}

}
