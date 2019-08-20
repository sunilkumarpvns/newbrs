package com.elitecore.corenetvertex.pd.calender;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "com.elitecore.corenetvertex.pd.calender.CalenderData")
@Table(name = "TBLM_CALENDER")
public class CalenderData extends DefaultGroupResourceData implements Serializable {
	
	private static final long serialVersionUID = -6773217511899504552L;
	private String calenderList;
	private String description;
	
    private List<CalenderDetails> calenderDetails;
    
    public CalenderData() {
    	calenderDetails = Collectionz.newArrayList();
    }

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "calenderData" ,cascade = {CascadeType.ALL},orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
	public List<CalenderDetails> getCalenderDetails() {
		return calenderDetails;
	}

	public void setCalenderDetails(List<CalenderDetails> calenderDetails) {
		this.calenderDetails = calenderDetails;
	}

	
	@Column(name="CALENDER_LIST")
	public String getCalenderList() {
		return calenderList;
	}

	public void setCalenderList(String calenderList) {
		this.calenderList = calenderList;
	}

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	@Column(name = "STATUS")
	public String getStatus() {
		return super.getStatus();
	}

	@Transient
	@Override
	public String getResourceName() {
		return getCalenderList();
	}
	
	
	@Override
	public JsonObject toJson() {

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.CALENDER_LIST, calenderList);
		jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
		jsonObject.addProperty(FieldValueConstants.GROUPS, getGroups());
		jsonObject.addProperty(FieldValueConstants.STATUS, getStatus());
		
		if(calenderDetails != null){
            JsonArray jsonArray = new JsonArray();
            for(CalenderDetails calennderDetail : calenderDetails){
                jsonArray.add(calennderDetail.toJson());
            }
            jsonObject.add("Calender Details", jsonArray);
        }

		return jsonObject;
	}

	public CalenderData copyModel(){
    	CalenderData calenderData = new CalenderData();
    	calenderData.setId(null);
    	calenderData.setDescription(this.description);
    	calenderData.setStatus(this.getStatus());
    	calenderData.setCalenderList(this.calenderList);
    	List<CalenderDetails> calenderDetailsCopy = new ArrayList<>();
    	for(CalenderDetails calenderDetail: calenderDetailsCopy){
			CalenderDetails calenderDetailCopy = (CalenderDetails) calenderDetail.copyModel();
			calenderDetailCopy.setCalenderData(calenderData);
			calenderDetailsCopy.add(calenderDetailCopy);
		}
		calenderData.setCalenderDetails(calenderDetailsCopy);
    	return calenderData;
	}

}
