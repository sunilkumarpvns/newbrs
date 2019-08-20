package com.elitecore.nvsmx.pd.controller.rncproductspec;

import static com.opensymphony.xwork2.Action.SUCCESS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.HttpHeaders;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pd.productoffer.ProductSpecServicePkgRelData;
import com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pd.productoffer.ProductSpecData;
import com.elitecore.corenetvertex.pd.service.ServiceData;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.pd.model.rncpackage.RncPackageDetailsWrapper;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

@ParentPackage(NVSMXCommonConstants.REST_PARENT_PKG_PD)
@Namespace("/pd/rncproductspec")
@Results({
        @Result(name = SUCCESS,
                type = RestGenericCTRL.REDIRECT_ACTION,
                params = {NVSMXCommonConstants.ACTION_NAME, "rnc-product-spec"})
})
public class RncProductSpecCTRL extends RestGenericCTRL<ProductSpecData> {

    private static final long serialVersionUID = -1520099473142841844L;
    private List<ServiceData> serviceDataList = new ArrayList<>();
    private List<RncPackageData> rncPkgDataList = new ArrayList<>();
    private List<PkgData> pkgDataList = new ArrayList<>();
    
	private String productSpecificationDataAsJson;

	private List<RncPackageDetailsWrapper> rncPackageDetailsWrapperData ;
	
	public List<RncPackageDetailsWrapper> getRncPackageDetailsWrapperData() {
		return rncPackageDetailsWrapperData;
	}

	public void setRncPackageDetailsWrapperData(List<RncPackageDetailsWrapper> rncPackageDetailsWrapperData) {
		this.rncPackageDetailsWrapperData = rncPackageDetailsWrapperData;
	}

    private static final Predicate<ProductSpecServicePkgRelData> EMPTY_PRDUCT_SPEC_SRV_DATA = productSpecServicePkgRelData -> {
        if (productSpecServicePkgRelData == null) {
            return false;
        }
        if (Strings.isNullOrBlank(productSpecServicePkgRelData.getRncPackageId()) || Strings.isNullOrBlank(productSpecServicePkgRelData.getServiceId())) {
            return false;
        }
        return true;
    };

    @Override
    public ACLModules getModule() {
        return ACLModules.RNCPRODUCTSPEC;
    }

    @Override
    public ProductSpecData createModel() {
        return new ProductSpecData();
    }


    @Override
    public HttpHeaders create() {
        ProductSpecData productSpecificationData = (ProductSpecData) getModel();
        setGroups(productSpecificationData);
        setProductSpecSrvRelData(productSpecificationData);
        return super.create();
    }

    @Override
    public HttpHeaders update() {
        ProductSpecData productSpecificationData = (ProductSpecData) getModel();
        setGroups(productSpecificationData);
        setProductSpecSrvRelData(productSpecificationData);
        return super.update();
    }
    
    
    
    @SuppressWarnings("unchecked")
	@Override
    public HttpHeaders show(){
    	ProductSpecData productSpecificationData = (ProductSpecData)getModel();
    	ProductSpecData resourceInDB = CRUDOperationUtil.get((Class<ProductSpecData>)productSpecificationData.getClass(),productSpecificationData.getId(),getAdditionalCriteria());
    	productSpecificationDataListwrapper(resourceInDB.getProductOfferServicePkgRelDataList());
    	return super.show();
    }
    
    @SkipValidation
	public String viewDetails(){
    	if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called viewDetails()");
		}

		String tableId = getRequest().getParameter(Attributes.TABLE_ID);
		String rowData = getRequest().getParameter(Attributes.ROW_DATA+tableId);
		
		
	    Gson gson = GsonFactory.defaultInstance();
	    JsonParser parser = new JsonParser();
	    JsonObject json = (JsonObject) parser.parse(rowData);
	    
		JsonElement rncPackageData = null;

	    Set<Entry<String, JsonElement>> entrySet = json.entrySet();
	    for(Map.Entry<String,JsonElement> entry : entrySet){
	     if (entry.getKey().equals("rncPackageData")) {
	    	 rncPackageData = entry.getValue();
	     }
	    }
	    
	    RncPackageData rncPackage = gson.fromJson(rncPackageData.toString(), RncPackageData.class);
        RncPackageData  rncPackageObj = CRUDOperationUtil.get(RncPackageData.class, rncPackage.getId());
        
        List<RncPackageDetailsWrapper> rncPackageDetailsWrappers = createRncPackageWrapper(rncPackageObj.getRateCardGroupData());
        setRncPackageDetailsWrapperData(rncPackageDetailsWrappers);
       
        setActionChainUrl("/WEB-INF/content/pd/rncpackage/rnc-package-view-sub-details.jsp");
        return NVSMXCommonConstants.SUBTABLEURL;
	}
	
	 private List<RncPackageDetailsWrapper> createRncPackageWrapper(List<RateCardGroupData>rateCardGroupDatas){
			List<RncPackageDetailsWrapper> rncPackageDetailsWrapper = Collectionz.newArrayList();
            //FIXME need to change once association of RateCard with Product Spec is been designed -- ishani.dave
			/*for(RateCardGroupData rateCardGroupData : rateCardGroupDatas){
				RncPackageDetailsWrapper wrapper = new RncPackageDetailsWrapper.RncPackageDetailWrapperBuilder(rateCardGroupData.getId()).withRateCardGroupdetail(rateCardGroupData).build();
				rncPackageDetailsWrapper.add(wrapper);
			}*/
	        return  rncPackageDetailsWrapper;
		}

	 
	private void productSpecificationDataListwrapper(List<ProductSpecServicePkgRelData> productSpecificationDatas) {
		Gson gson = GsonFactory.defaultInstance();
		JsonArray modelJson = gson.toJsonTree(productSpecificationDatas, new TypeToken<List<ProductSpecServicePkgRelData>>() {
		}.getType()).getAsJsonArray();
		
		setProductSpecificationDataAsJson(modelJson.toString());
	}
	 
    
    private void setProductSpecSrvRelData(ProductSpecData productSpecificationData) {

        if (Collectionz.isNullOrEmpty(productSpecificationData.getProductOfferServicePkgRelDataList())) {
            return;
        }
        Collectionz.filter(productSpecificationData.getProductOfferServicePkgRelDataList(), EMPTY_PRDUCT_SPEC_SRV_DATA);

        productSpecificationData.getProductOfferServicePkgRelDataList()
                                .forEach(productSpecServicePkgRelData -> productSpecServicePkgRelData.setProductOfferData(productSpecificationData
        ));

    }

    private void setGroups(ProductSpecData productSpecificationData) {
        if (Strings.isNullOrBlank(productSpecificationData.getGroups())) {
            productSpecificationData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
        }
    }

    public List<ServiceData> getServiceDataList() {
        return serviceDataList;
    }

    public void setServiceDataList(List<ServiceData> serviceDataList) {
        this.serviceDataList = serviceDataList;
    }

    public List<RncPackageData> getRncPkgDataList() {
        return rncPkgDataList;
    }

    public void setRncPkgDataList(List<RncPackageData> rncPkgDataList) {
        this.rncPkgDataList = rncPkgDataList;
    }

    public String getProductSpecificationDataAsJson() {
		return productSpecificationDataAsJson;
	}

	public void setProductSpecificationDataAsJson(String productSpecificationDataAsJson) {
		this.productSpecificationDataAsJson = productSpecificationDataAsJson;
	}

	public List<PkgData> getPkgDataList() {
        return pkgDataList;
    }

    public void setPkgDataList(List<PkgData> pkgDataList) {
        this.pkgDataList = pkgDataList;
    }

    @Override
    public void prepareValuesForSubClass() throws Exception {
        setRncPkgDataList(CRUDOperationUtil.findAll(RncPackageData.class));
        setServiceDataList(CRUDOperationUtil.findAll(ServiceData.class));
        setPkgDataList(CRUDOperationUtil.findAll(PkgData.class));
    }
}