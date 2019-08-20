package com.elitecore.test.command.data;

import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.test.ScenarioContext;
import com.elitecore.test.command.Case;
import com.elitecore.test.command.Command;
import com.elitecore.test.command.ConditionCommand;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XmlRootElement(name = "condition")
public class ConditionCommandData implements CommandData {
	
	private List<CaseData> caseDatas = new ArrayList<CaseData>();
	private DefaultData defalutData;
	private String name ="no-name";
	
	
	@Override
	@XmlAttribute(name="name", required=true)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name="default", required=false)
	public DefaultData getDefalutData() {
		return defalutData;
	}


	public void setDefalutData(DefaultData defalutData) {
		this.defalutData = defalutData;
	}


	@XmlElement(name="case", required=true)
	public List<CaseData> getCaseDatas() {
		return caseDatas;
	}


	
	
	


	@Override
	public Command create(ScenarioContext context) throws Exception {
		
		Compiler compiler = Compiler.getDefaultCompiler();
		List<Case> cases = new ArrayList<Case>();
		for(CaseData caseData : caseDatas){
			LogicalExpression expression = compiler.parseLogicalExpression(caseData.getRuleSet());
			List<Command> commands = new ArrayList<Command>();
			for(CommandData commandData : caseData.getCommandDatas()){
				commands.add(commandData.create(context));
			}
			
			cases.add(new Case(expression, commands));
			
		}
		
		List<Command> commands = null;
		if(defalutData != null){
			commands = new ArrayList<Command>();
			for(CommandData commandData : defalutData.getCommandDatas()){
				commands.add(commandData.create(context));
			}
		} else {
			commands = Collections.emptyList();
		}
		
		
		return new ConditionCommand(cases,commands,name);
	}


	
	
	

}
