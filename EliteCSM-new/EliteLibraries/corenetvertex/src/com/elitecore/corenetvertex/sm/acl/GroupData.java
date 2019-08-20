package com.elitecore.corenetvertex.sm.acl;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.List;


/**
 * this class defines the group/circle related information
 * @author ishani.bhatt
 *
 */

@Entity(name = "com.elitecore.corenetvertex.sm.acl.GroupData")
@Table(name="TBLM_GROUP")
//FIXME rename acl group --ishani.dave
public class GroupData extends ResourceData implements Serializable {

	private static final long serialVersionUID = 1L;
	@SerializedName(FieldValueConstants.NAME)private String name;
	@SerializedName(FieldValueConstants.DESCRIPTION)private String description;
	private transient List<StaffGroupRoleRelData> staffGroupRoleRelDataList;


	public GroupData() {//Default No-Arg constructor if used as pojo with rest services
		staffGroupRoleRelDataList = Collectionz.newArrayList();

	}

	@Column(name="NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="DESCRIPTION")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupData other = (GroupData) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.NAME,name);
		jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);

		return jsonObject;
	}

	@Override
	@Column(name = "STATUS")
	public String getStatus() {
		return super.getStatus();
	}

	@Transient
	@XmlTransient
	@Override
	public String getHierarchy() {
		return getId() + "<br>" + name;
	}

	@Override
	@Transient
	public String getResourceName() {
		return getName();
	}


	@OneToMany(fetch = FetchType.LAZY, mappedBy = "groupData", cascade = {CascadeType.ALL}, orphanRemoval = false)
	@Fetch(FetchMode.SUBSELECT)
	@JsonIgnore
	public List<StaffGroupRoleRelData> getStaffGroupRoleRelDataList() {
		return staffGroupRoleRelDataList;
	}

	public void setStaffGroupRoleRelDataList(List<StaffGroupRoleRelData> staffGroupRoleRelDataList) {
		this.staffGroupRoleRelDataList = staffGroupRoleRelDataList;
	}


}
