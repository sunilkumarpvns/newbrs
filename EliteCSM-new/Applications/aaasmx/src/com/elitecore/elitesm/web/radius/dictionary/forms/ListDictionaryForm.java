package com.elitecore.elitesm.web.radius.dictionary.forms;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData;
import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;
//import com.elitecore.radius.web.system.user.forms.base.BaseUserForm;

/*
<li><b>fromAddress</b> - The EMAIL address of the sender, to be included
*     on sent messages.  [REQUIRED]
*     
* <li><b>replyToAddress</b> - The "Reply-To" address to be included on
 *     sent messages.  [Same as from address]
*/                          

public class ListDictionaryForm extends BaseDictionaryForm {

	private String toggleAll = null;
	private String c_bSelected = null;
	private String c_strDictionaryId = null;
	private List stListDictionary = null;
	private String action;
	private String status;
	
	/*
	private String dictionaryid;
    private String name;
    private String description;
    private String modalnumber;
    private String editable;
    private String systemgenerated;
    private double dictionarynumber;
    private String commonstatusid;
    private Date lastmodifieddate;
    private String lastmodifiedbystaffid;
    private double ianapvtentnumber;
    private Date createdate;
    private String createdbystaffid;
    private Date statuschangeddate;

	 */

    public List getDictionaryList(){
        return stListDictionary;
    }
    public void setDictionaryList(List stListDictionary){
    	this.stListDictionary = stListDictionary;
    }

    /////////////////////////////////////////////////////////////////////////////////
    public String getDictionaryId(int index){
        return ((DictionaryData)stListDictionary.get(index)).getDictionaryId();
    }
    public void setDictionaryId(int index, String dictionaryId){
    	DictionaryData objDictionaryData = (DictionaryData)stListDictionary.get(index);
    	if(objDictionaryData != null){
    		objDictionaryData.setDictionaryId(dictionaryId);
    	}
    	stListDictionary.set(index,objDictionaryData);
    }
    ///////////////////////////////////////////////////////////////////////////////
    public String getName(int index){
        return ((DictionaryData)stListDictionary.get(index)).getName();
    }
    public void setName(int index, String strName){
    	DictionaryData objDictionaryData = (DictionaryData)stListDictionary.get(index);
    	if(objDictionaryData != null){
    		objDictionaryData.setName(strName);
    	}
    	stListDictionary.set(index,objDictionaryData);
    }
    ///////////////////////////////////////////////////////////////////////////////
    public String getDescription(int index){
        return ((DictionaryData)stListDictionary.get(index)).getDescription();
    }
    public void setDescription(int index, String strDescription){
    	DictionaryData objDictionaryData = (DictionaryData)stListDictionary.get(index);
    	if(objDictionaryData != null){
    		objDictionaryData.setDescription(strDescription);
    	}
    	stListDictionary.set(index,objDictionaryData);
    }
    ///////////////////////////////////////////////////////////////////////////////
    public String getModelNumber(int index){
        return ((DictionaryData)stListDictionary.get(index)).getModalNumber();
    }
    public void setModelNumber(int index, String strModelNumber){
    	DictionaryData objDictionaryData = (DictionaryData)stListDictionary.get(index);
    	if(objDictionaryData != null){
    		objDictionaryData.setModalNumber(strModelNumber);
    	}
    	stListDictionary.set(index,objDictionaryData);
    }
    ///////////////////////////////////////////////////////////////////////////////
    public String getEditable(int index){
        return ((DictionaryData)stListDictionary.get(index)).getEditable();
    }
    public void setEditable(int index, String strEditable){
    	DictionaryData objDictionaryData = (DictionaryData)stListDictionary.get(index);
    	if(objDictionaryData != null){
    		objDictionaryData.setEditable(strEditable);
    	}
    	stListDictionary.set(index,objDictionaryData);
    }
    ///////////////////////////////////////////////////////////////////////////////
    public String getSystemGenerated(int index){
        return ((DictionaryData)stListDictionary.get(index)).getSystemGenerated();
    }
    public void setSystemGenerated(int index, String strSystemGenerated){
    	DictionaryData objDictionaryData = (DictionaryData)stListDictionary.get(index);
    	if(objDictionaryData != null){
    		objDictionaryData.setSystemGenerated(strSystemGenerated);
    	}
    	stListDictionary.set(index,objDictionaryData);
    }
    ///////////////////////////////////////////////////////////////////////////////
    public long getDictionaryNumber(int index){
        return ((DictionaryData)stListDictionary.get(index)).getDictionaryNumber();
    }
    public void setDictionaryNumber(int index, long dblDictionaryNumber){
    	DictionaryData objDictionaryData = (DictionaryData)stListDictionary.get(index);
    	if(objDictionaryData != null){
    		objDictionaryData.setDictionaryNumber(dblDictionaryNumber);
    	}
    	stListDictionary.set(index,objDictionaryData);
    }
    ///////////////////////////////////////////////////////////////////////////////
    public String getCommonStatusId(int index){
        return ((DictionaryData)stListDictionary.get(index)).getCommonStatusId();
    }
    public void setCommonStatusId(int index, String strCommonStatusId){
    	DictionaryData objDictionaryData = (DictionaryData)stListDictionary.get(index);
    	if(objDictionaryData != null){
    		objDictionaryData.setCommonStatusId(strCommonStatusId);
    	}
    	stListDictionary.set(index,objDictionaryData);
    }
    ///////////////////////////////////////////////////////////////////////////////
    public Timestamp getLastModifiedDate(int index){
        return ((DictionaryData)stListDictionary.get(index)).getLastModifiedDate();
    }
    public void setLastModifiedDate(int index, Timestamp dtLastModifiedDate){
    	DictionaryData objDictionaryData = (DictionaryData)stListDictionary.get(index);
    	if(objDictionaryData != null){
    		objDictionaryData.setLastModifiedDate(dtLastModifiedDate);
    	}
    	stListDictionary.set(index,objDictionaryData);
    }
    ///////////////////////////////////////////////////////////////////////////////
    public String getLastModifiedByStaffId(int index){
        return ((DictionaryData)stListDictionary.get(index)).getLastModifiedByStaffId();
    }
    public void setLastModifiedByStaffId(int index, String strLastModifiedByStaffId){
    	DictionaryData objDictionaryData = (DictionaryData)stListDictionary.get(index);
    	if(objDictionaryData != null){
    		objDictionaryData.setLastModifiedByStaffId(strLastModifiedByStaffId);
    	}
    	stListDictionary.set(index,objDictionaryData);
    }
    ///////////////////////////////////////////////////////////////////////////////
    public String getVendorId(int index){
        return String.valueOf(((DictionaryData)stListDictionary.get(index)).getVendorId());
    }
    public void setVendorId(int index, String strVendorId){
    	DictionaryData objDictionaryData = (DictionaryData)stListDictionary.get(index);
    	if(objDictionaryData != null){
            try {
                objDictionaryData.setVendorId(Long.parseLong(strVendorId));
            }catch(NumberFormatException ne) {}
    	}
    	stListDictionary.set(index,objDictionaryData);
    }
    ///////////////////////////////////////////////////////////////////////////////
    public Timestamp getCreateDate(int index){
        return ((DictionaryData)stListDictionary.get(index)).getCreateDate();
    }
    public void setCreateDate(int index, Timestamp dtCreateDate){
    	DictionaryData objDictionaryData = (DictionaryData)stListDictionary.get(index);
    	if(objDictionaryData != null){
    		objDictionaryData.setCreateDate(dtCreateDate);
    	}
    	stListDictionary.set(index,objDictionaryData);
    }
    ///////////////////////////////////////////////////////////////////////////////
    public String getCreatedByStaffId(int index){
        return ((DictionaryData)stListDictionary.get(index)).getCreatedByStaffId();
    }
    public void setCreatedByStaffId(int index, String strCreatedByStaffId){
    	DictionaryData objDictionaryData = (DictionaryData)stListDictionary.get(index);
    	if(objDictionaryData != null){
    		objDictionaryData.setCreatedByStaffId(strCreatedByStaffId);
    	}
    	stListDictionary.set(index,objDictionaryData);
    }
    ///////////////////////////////////////////////////////////////////////////////
    public Timestamp getStatusChangedDate(int index){
        return ((DictionaryData)stListDictionary.get(index)).getStatusChangedDate();
    }
    public void setStatusChangedDate(int index, Timestamp dtStatusChangedDate){
    	DictionaryData objDictionaryData = (DictionaryData)stListDictionary.get(index);
    	if(objDictionaryData != null){
    		objDictionaryData.setStatusChangedDate(dtStatusChangedDate);
    	}
    	stListDictionary.set(index,objDictionaryData);
    }

	
	public String getC_strDictionaryId() {
		return c_strDictionaryId;
	}

	public void setC_strDictionaryId(String dictionaryId) {
		c_strDictionaryId = dictionaryId;
	}

	public String getC_bSelected() {
		return c_bSelected;
	}

	public void setC_bSelected(String selected) {
		c_bSelected = selected;
	}

	public String getToggleAll() {
		return toggleAll;
	}

	public void setToggleAll(String toggleAll) {
		this.toggleAll = toggleAll;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}