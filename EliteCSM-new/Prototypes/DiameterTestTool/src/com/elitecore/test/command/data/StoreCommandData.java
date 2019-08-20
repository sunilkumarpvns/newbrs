package com.elitecore.test.command.data;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.commons.base.Optional;
import com.elitecore.commons.base.Strings;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.test.ExecutionContext;
import com.elitecore.test.ScenarioContext;
import com.elitecore.test.command.Command;
import com.elitecore.test.command.ReceivePacketContext;
import com.elitecore.test.command.StoreCommand;
import com.elitecore.test.command.ValueStorer;
import com.elitecore.test.dependecy.diameter.packet.DiameterPacket;
import com.elitecore.test.dependecy.diameter.packet.avps.IDiameterAVP;

@XmlRootElement(name = "store")
public class StoreCommandData implements CommandData {
	
	private String attr;
	private String value;
	private String key;
	private int index;
	private String name ="no-name";
	
	
	@Override
	@XmlAttribute(name="name", required=true)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name = "index",required=false)
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		if(index >= 1) {			
			this.index = index-1;
		}
	}



	@XmlAttribute(name = "attr",required=true)
	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}


	@XmlAttribute(name = "key",required=true)
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@XmlAttribute(name = "val",required=false)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public Command create(ScenarioContext context) throws Exception {
		Expression expression = null;
		ValueStorer valueStoreStretegy;
		if(Strings.isNullOrBlank(attr) == false) {
			if(attr.startsWith("value(") == false){
				expression = Compiler.getDefaultCompiler().parseExpression("\""+ attr.trim() + "\"");
			} else {
				expression = Compiler.getDefaultCompiler().parseExpression(attr.trim());
			}
			valueStoreStretegy = new AttributeValueStoreStrategy();
		} else if(Strings.isNullOrBlank(value) == false){
			if(value.startsWith("value(") == false){
				expression = Compiler.getDefaultCompiler().parseExpression("\""+ value.trim() + "\"");
			} else {
				expression = Compiler.getDefaultCompiler().parseExpression(value.trim());
			}
			valueStoreStretegy = new DynamicValueStoreStrategy();
		} else {
			throw new Exception("Store should have either attr or val attribute with some value");
		}
		
		return new StoreCommand(key, expression, index, valueStoreStretegy,name);
	}
	
	private static class AttributeValueStoreStrategy implements ValueStorer{

		@Override
		public void storeValue(String key, Expression source,int index, ExecutionContext context) {
			Optional<Object> optional = context.get(ReceivePacketContext.RCVD_REQ);
			
			if(optional.isPresent() == false){
				return;
			}
			
			DiameterPacket diameterPacket = (DiameterPacket) optional.get();
			
			ArrayList<IDiameterAVP> diameterAVPs;
			try {
				diameterAVPs = diameterPacket.getAVPList(source.getStringValue(context.getValueProvider()), true);
			} catch (Exception e) {
				return;
			}
			
			if(diameterAVPs == null || diameterAVPs.isEmpty()){
				return;
			}
			
			if(diameterAVPs.size() < index+1) {
				return;
			}
			
			IDiameterAVP diameterAVP = diameterAVPs.get(index);
			
			if(diameterAVP.isGrouped()){
				for(IDiameterAVP childIDiameterAVP : diameterAVP.getGroupedAvp()){
					storeValue(childIDiameterAVP, context, key);
				}
			} else {
				context.set(key, diameterAVP.getStringValue());
			}
			
		}
		
		
		private void storeValue(IDiameterAVP diameterAVP, ExecutionContext context,String key){
			if(diameterAVP.isGrouped()){
				for(IDiameterAVP childIDiameterAVP : diameterAVP.getGroupedAvp()){
					storeValue(childIDiameterAVP, context, key+"."+diameterAVP.getAVPCode());
				}
			} else {
				context.set(key+"."+diameterAVP.getAVPCode(), diameterAVP.getStringValue());
			}
		}
		
	}
	
	private static class DynamicValueStoreStrategy implements ValueStorer{

		@Override
		public void storeValue(String key, Expression source,int index, ExecutionContext context) {
			try {
				context.set(key, source.getStringValue(context.getValueProvider()));
			} catch(Exception ex){
				
			}
		}
		
	}

}
