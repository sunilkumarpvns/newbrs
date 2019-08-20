package com.elitecore.nvsmx.pd.controller.partnergroup;

import static com.opensymphony.xwork2.Action.SUCCESS;

import java.util.List;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.pd.lob.LobData;
import com.elitecore.corenetvertex.pd.partnergroup.PartnerGroupData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;

/**
 * manage Partner Group related information.
 * Created by Saket on 14/12/17.
 */

@ParentPackage(value = "pd")
@Namespace("/pd/partnergroup")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","partner-group"}),

})
public class PartnerGroupCTRL extends RestGenericCTRL<PartnerGroupData> {

	private static final long serialVersionUID = -2097037846742104779L;
	
	private List<LobData> lobDataList = Collectionz.newArrayList();

    public List<LobData> getLobDataList() {
		return lobDataList;
	}

	public void setLobDataList(List<LobData> lobDataList) {
		this.lobDataList = lobDataList;
	}

	@Override
    public ACLModules getModule() {
        return ACLModules.PARTNERGROUP;
    }

    @Override
    public PartnerGroupData createModel() {
        return new PartnerGroupData();
    }
    
    @SkipValidation
	@Override
	public void prepareValuesForSubClass() throws Exception {
		setLobDataList(CRUDOperationUtil.findAll(LobData.class));
	}

    @Override
	public void validate() {
		PartnerGroupData partnerGroupData = (PartnerGroupData) getModel();
		LobData lobData = CRUDOperationUtil.get(LobData.class, partnerGroupData.getLobId());
		if (lobData == null) {
			addFieldError("lobId", getText("pg.lob.invalid"));
		}
		super.validate();
	}
}