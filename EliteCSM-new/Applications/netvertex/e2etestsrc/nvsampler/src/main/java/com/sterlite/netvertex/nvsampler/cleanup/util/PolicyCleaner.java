package com.sterlite.netvertex.nvsampler.cleanup.util;

import java.util.ArrayList;
import java.util.List;
import com.sterlite.netvertex.nvsampler.cleanup.Result;


import static com.sterlite.netvertex.nvsampler.cleanup.util.CleanUpUtils.getGlobalVariable;
import static com.sterlite.netvertex.nvsampler.cleanup.util.CleanUpUtils.getSmContextPath;
import static javax.ws.rs.HttpMethod.DELETE;
import static javax.ws.rs.HttpMethod.GET;

public class PolicyCleaner {

	private static final String UPDATE_TBLM_PACKAGE_SET_STATUS_DELETED = "UPDATE TBLM_PACKAGE SET STATUS='DELETED' where ID IN (?)";
	private static final String UPDATE_TBLM_PRODUCT_OFFER_SET_PACKAGE_MODE_DESIGN = "UPDATE TBLM_PRODUCT_OFFER SET PACKAGE_MODE='DESIGN' where ID IN (?)";
	private static final String UPDATE_TBLM_BOD_SET_PACKAGE_MODE_DESIGN = "UPDATE TBLM_BOD SET PACKAGE_MODE='DESIGN' where ID IN (?)";

	private static final String REST_RESTFUL_POLICY_RELOAD_POLICY = "/rest/restful/policy/reload/policy";
	private static final String JSON_EXT = ".json";
	public static final String UPDATE_TBLM_RNC_PACKAGE_SET_PACKAGE_MODE_DESIGN_WHERE_ID_IN = "UPDATE TBLM_RNC_PACKAGE SET PACKAGE_MODE='DESIGN' where ID IN (?)";
	public static final String SLASH = "/";
	private DBQueryExecutor dbQueryExecutor;
	private HTTPConnector httpConnector;

	public PolicyCleaner(DBQueryExecutor dbQueryExecutor, HTTPConnector httpConnector) {
		this.dbQueryExecutor = dbQueryExecutor;
		this.httpConnector = httpConnector;
	}

	public List<Result> cleanDataPackage(String[] packageIds) {
		List<Result> results = new ArrayList<>();
		for (String packageId : packageIds) {
			Result result = new Result("Clean Data Package: " + packageId);
			result.addResult(updateDataPackageToDeleted(packageId));
			result.addResult(reloadPolicy());
			results.add(result);
		}
		return results;
	}

	private Result updateDataPackageToDeleted(String packageId) {
		return dbQueryExecutor.executeUpdate(UPDATE_TBLM_PACKAGE_SET_STATUS_DELETED, packageId);
	}

	public List<Result> cleanProductOffer(String[] poIds) {
		List<Result> results = new ArrayList<>();
		for (String poId : poIds) {
			Result result = new Result("Clean Product Offer: " + poId);
			result.addResult(updateProductOfferToDesign(poId));
			result.addResult(reloadPolicy());
			result.addResult(deletePO(poId));
			results.add(result);
		}
		return results;
	}

	private Result updateProductOfferToDesign(String poId) {
		Result result = new Result("Update Product Offer Mode to Design");
		result.addResult(dbQueryExecutor.executeUpdate(UPDATE_TBLM_PRODUCT_OFFER_SET_PACKAGE_MODE_DESIGN, poId));
		return result;
	}

	private Result reloadPolicy() {
		Result result = new Result("Reload Policy");
		String url = new StringBuilder(getSmContextPath()).append(REST_RESTFUL_POLICY_RELOAD_POLICY).toString();
		result.addResult(httpConnector.connect(url, GET));
		return result;
	}

	private Result deletePO(String poId) {
		Result result = new Result("Delete Product Offer");
		String url = new StringBuilder(getSmContextPath())
				.append("/pd/")
				.append(getGlobalVariable("product_offer_url_without_ext"))
				.append(SLASH).append(poId).append(JSON_EXT).toString();
		result.addResult(httpConnector.connect(url, DELETE));
		return result;
	}

	public List<Result> cleanRnCPackages(String[] packageIds) {
		List<Result> results = new ArrayList<>();
		for (String packageId : packageIds) {
			Result result = new Result("Clean RnC Package: " + packageId);
			result.addResult(updateRnCPackageToDESIGN(packageId));
			result.addResult(deleteRnCPackage(packageId));
			results.add(result);
		}
		return results;
	}

	private Result deleteRnCPackage(String packageId) {
		Result result = new Result("Delete RnC Package");
		String url = new StringBuilder(getSmContextPath())
				.append(SLASH)
				.append(getGlobalVariable("rnc_package_url_without_ext"))
				.append(SLASH)
				.append(packageId).append(JSON_EXT).toString();
		result.addResult(httpConnector.connect(url, DELETE));
		return result;
	}

	private Result updateRnCPackageToDESIGN(String packageId) {
		return dbQueryExecutor.executeUpdate(UPDATE_TBLM_RNC_PACKAGE_SET_PACKAGE_MODE_DESIGN_WHERE_ID_IN, packageId);
	}

	public List<Result> cleanDataTopUpIds(String[] packageIds) {
		List<Result> results = new ArrayList<>();
		for (String packageId : packageIds) {
			Result result = new Result("Clean Data TopUp: " + packageId);
			result.addResult(deleteTopUp(packageId));
			result.addResult(reloadPolicy());
			results.add(result);
		}
		return results;
	}

	private Result deleteTopUp(String packageId) {
		Result result = new Result("Delete TopUp");
		String url = new StringBuilder(getSmContextPath())
				.append(SLASH)
				.append(getGlobalVariable("data_topup_url_without_ext"))
				.append(SLASH)
				.append(packageId).append(JSON_EXT).toString();
		result.addResult(httpConnector.connect(url, DELETE));
		return result;
	}

	public List<Result> cleanBoDPackage(String[] bodIds) {
		List<Result> results = new ArrayList<>();
		for (String bodId : bodIds) {
			Result result = new Result("Clean BoD Package: " + bodId);
			result.addResult(updateBoDPackageToDesign(bodId));
			result.addResult(reloadPolicy());
			result.addResult(deleteBoDPackage(bodId));
			results.add(result);
		}
		return results;
	}

	private Result updateBoDPackageToDesign(String bodId) {
		Result result = new Result("Update BoD Package Mode to Design");
		result.addResult(dbQueryExecutor.executeUpdate(UPDATE_TBLM_BOD_SET_PACKAGE_MODE_DESIGN, bodId));
		return result;
	}

	private Result deleteBoDPackage(String bodId) {
		Result result = new Result("Delete BoD Package");
		String url = new StringBuilder(getSmContextPath())
				.append("/pd/")
				.append(getGlobalVariable("bod_url"))
				.append(SLASH).append(bodId).append(JSON_EXT).toString();
		result.addResult(httpConnector.connect(url, DELETE));
		return result;
	}
}