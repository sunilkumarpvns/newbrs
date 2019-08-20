package com.elitecore.test.diameter.jaxb;

import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.expression.Expression;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Tainted;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "attribute")
public class AttributeData {

	@SerializedName("id") @Nonnull private String id;
	@SerializedName("val") @Nullable @Tainted private String value;
	@SerializedName("val") @Nullable  private Expression expression;
	@SerializedName("sub-attribut") @Nullable @Tainted private List<AttributeData> attributeDatas;
	

	@XmlAttribute(name = "id",required=true)
	public String getId() { 
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute(name = "value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@XmlElement(name="attribute")
	public List<AttributeData> getAttributeDatas() {
		return attributeDatas;
	}

	public void setAttributeDatas(List<AttributeData> attributeDatas) {
		this.attributeDatas = attributeDatas;
	}
	
	public Expression getExpression() throws Exception {
		if(value == null){
			return null;
		}
		
		if(expression == null){
			if(value.startsWith("value(") == false){
				expression = Compiler.getDefaultCompiler().parseExpression("\""+ value + "\"");
			} else {
				expression = Compiler.getDefaultCompiler().parseExpression(value);
			}
			
		}
		
		
		return expression ;
		
	}
	
	@Override
	public String toString() {
		return "AttributeData [id=" + id + ", value=" + value
				+ ", attributeDatas=" + attributeDatas + "]";
	}

}
