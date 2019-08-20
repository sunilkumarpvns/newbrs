package com.elitecore.aaa.core.conf.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * 
 * @author malav
 *
 */
@XmlType(propOrder = {})
public class ClusterGroupData implements Differentiable{
	
	private String name;
	private String passwd;
	private String description;
	private List<MemberData> memberDatas;
	
	public ClusterGroupData() {
		this.memberDatas = new ArrayList<MemberData>();
	}

	@XmlElement(name="name", type=String.class)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name="passwd", type=String.class)
	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	@XmlElementWrapper(name="members")
	@XmlElement(name="member")
	public List<MemberData> getMemberDatas() {
		return memberDatas;
	}

	public void setMemberDatas(List<MemberData> data) {
		this.memberDatas = data;
	}

	@XmlElement(name="description", type=String.class)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Group Name", name);
		jsonObject.put("Group Password", passwd);
		jsonObject.put("Description", description);
		
		if(Collectionz.isNullOrEmpty(memberDatas) == false) {
			JSONArray array = new JSONArray();
			for( MemberData memberData : memberDatas ) {
				array.add(memberData.toJson());
			}
			jsonObject.put("Mappings", array);
		}
		
		return jsonObject;
	}
	
}
