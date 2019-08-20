package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.config.exception.LoadConfigurationException;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.data.PolicyCacheDetail;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.pm.sliceconfig.DataSliceConfiguration;
import com.elitecore.corenetvertex.pm.util.BoDDataPredicates;
import com.elitecore.corenetvertex.pm.util.ProductOfferPredicates;
import com.elitecore.corenetvertex.pm.util.RnCPackagePredicates;
import com.elitecore.netvertex.core.NetVertexServerContext;

import static com.elitecore.commons.logging.LogManager.getLogger;


public class ReloadDetailProvider extends DetailProvider {
	private static final String MODULE = "RELOAD-DTL-PROVIDER";
	private static final String RELOAD = "-reload";
	private static final String HISTORY = "-history";
	private static final String HELP = "-help";
	private static final String DATA = "-data";
	private static final String IMS = "-ims";
	private static final String RNC = "-rnc";
	private static final String BOD = "-bod";
	private static final String OFFER = "-offer";
	public static final String SLICE_CONFIGURATION = "-slice-configuration";
	private static final int WIDTH_STATUS = 20;
	private static final int WIDTH_TYPE = 5;
	private static final int WIDTH_REMARK = 50;
	private static final int WIDTH_DATE = 30;
	private static final int WIDTH_MODE = 20;
	private static final int WIDTH_POLICY=30;

	private int[] width = {WIDTH_POLICY,WIDTH_STATUS,WIDTH_TYPE, WIDTH_MODE ,WIDTH_REMARK};
	private String[] header = {"Policy Name","Status","Type", "Mode" ,"Remarks"};


	//for reload history
	private int[] widthHistory = {WIDTH_STATUS,WIDTH_REMARK,WIDTH_DATE};
	private String[] headerHistory = {"Status","Remarks","TimeStamp"};
	
	
	private  HashMap<String ,DetailProvider> detailProviderMap;
	
	private NetVertexServerContext serverContext;


	public ReloadDetailProvider(NetVertexServerContext netVertexServerContext){
		this.serverContext=netVertexServerContext;
		detailProviderMap = new LinkedHashMap<String, DetailProvider>(4,1);
	}

	@Override
	public  void registerDetailProvider(DetailProvider detailprovider) throws RegistrationFailedException{

		if(detailprovider.getKey() == null){
			throw new  RegistrationFailedException("Failed to register detail provider. Reason : key is not specified.");
		}

		if(detailProviderMap.containsKey(detailprovider.getKey().toLowerCase())){
			throw new  RegistrationFailedException("Failed to register detail provider. Reason : Policy Command already contains detail provider with Key : " + detailprovider.getKey());
		}

		detailProviderMap.put(detailprovider.getKey().toLowerCase() , detailprovider);	
	}
	
	
	
	@Override
	public String execute(String[] parameters) {
		PolicyRepository repository = serverContext.getPolicyRepository();
		if(parameters.length == 0){
			return displayPolicyCacheDetails(repository.reload());
		}

		if (parameters[0].equalsIgnoreCase(SLICE_CONFIGURATION)) {
			try {
				repository.reloadSliceConfiguration();
			} catch (LoadConfigurationException ex) {
				getLogger().error(MODULE, "Fail to reload Data Slice Configuration. Reason: " + ex.getMessage());
				getLogger().trace(MODULE, ex);
			}
			return displaySliceConfigurationDetails(repository.getSliceConfiguration());
		}

		if("?".equals(parameters[0]) || parameters[0].equalsIgnoreCase(HELP)){
			return getHelpMsg();
		}
		
		if(HISTORY.equalsIgnoreCase(parameters[0])){
			return viewHistory();
		}
		
		if(detailProviderMap.containsKey(parameters[0].toLowerCase())){
			String[] destArray=new String[parameters.length-1];
			System.arraycopy(parameters, 1, destArray, 0, destArray.length);
			return detailProviderMap.get(parameters[0].toLowerCase()).execute(destArray);
		}
			
		String[] destArray = new String[parameters.length-1];
		System.arraycopy(parameters, 1, destArray, 0, destArray.length);
		
		if(parameters[0].equalsIgnoreCase(DATA)) {
			if(destArray.length == 0) {
				return viewDataPackageNames(repository.getAllDataPackageNames());
			} else {						
				return reloadDataPolicies(destArray);
			}
		} else if(parameters[0].equalsIgnoreCase(IMS)) {
			if(destArray.length == 0) {
				return viewIMSPackageNames(repository.getAllIMSPackageNames());
			} else {
				return reloadIMSPolicies(destArray);
			}
		} else if(OFFER.equalsIgnoreCase(parameters[0])) {
			if(destArray.length == 0) {
				return viewProductOffers(repository.getProductOffer().all().stream().map(ProductOffer::getName).collect(Collectors.toList()));
			} else {
				return reloadProductOffers(destArray);
			}
		} else if(RNC.equalsIgnoreCase(parameters[0])) {
			if(destArray.length == 0) {
				return viewRnCPackages(repository.getRnCPackage().all().stream().map(RnCPackage::getName).collect(Collectors.toList()));
			} else {
				return reloadRnCPackages(destArray);
			}
		} else if(BOD.equalsIgnoreCase(parameters[0])) {
			if(destArray.length == 0) {
				return viewBoDPackages(getBoDPackageNames());
			} else {
				return reloadBoDPackages(destArray);
			}
		} else {
			return "Invalid argument " + parameters[0] + "\n" + getHelpMsg();
		}
		
	}

	public String displaySliceConfigurationDetails(DataSliceConfiguration dataSliceConfiguration) {
		StringBuilder builder = new StringBuilder("Reload Status Of Data Slice Configuration: ");
		builder.append(PolicyStatus.SUCCESS);
		builder.append("\n\n\n");
		builder.append(dataSliceConfiguration);
		return builder.toString();
	}

	private String displayPolicyCacheDetails(PolicyCacheDetail policyCacheDetail) {
		
		List<PolicyDetail> failureListPolicy = policyCacheDetail.getFailurePolicyList();
		
		/*
		 * In case of db connection failure or failure while reloading and getting policy cache detail if the operation fails,
		 * the below code will be executed to skip all counters. 
		 */
		if(PolicyStatus.FAILURE == policyCacheDetail.getStatus()
				&& (failureListPolicy == null || failureListPolicy.isEmpty() == true)){
			StringBuilder builder = new StringBuilder("Reload Status: ");
			builder.append(policyCacheDetail.getStatus().status);
			builder.append("\nRemark: ");
			builder.append(policyCacheDetail.getRemark());
			
			builder.append("\n");
			return builder.toString();
		}
		
		StringBuilder builder = new StringBuilder("\n");
		List<PolicyDetail> partialSuccessPolicyList = policyCacheDetail.getPartialSuccessPolicyList();
		List<PolicyDetail> lastKnownGoodPolicyList = policyCacheDetail.getLastKnownGoodPolicyList();
		
		if (Collectionz.isNullOrEmpty(failureListPolicy) == false || Collectionz.isNullOrEmpty(partialSuccessPolicyList) == false) {
    		TableFormatter formatter=new TableFormatter(header, width,TableFormatter.ALL_BORDER);
    		if(Collectionz.isNullOrEmpty(partialSuccessPolicyList) == false){
    			for(PolicyDetail policy : partialSuccessPolicyList){
    				formatter.addRecord(formatpolicy(policy));
    			}
    		}
    		
    		if(Collectionz.isNullOrEmpty(failureListPolicy) == false){
    			for(PolicyDetail policy : failureListPolicy){
    				formatter.addRecord(formatpolicy(policy));
    			}
    		}

			if(Collectionz.isNullOrEmpty(lastKnownGoodPolicyList) == false){
				for(PolicyDetail policy : lastKnownGoodPolicyList){
					formatter.addRecord(formatpolicy(policy));
				}
			}
    		builder.append("\n");
    		builder.append(formatter.getFormattedValues());
		}
		builder.append("Reload Status: ");
		builder.append(policyCacheDetail.getStatus().status);
		builder.append("\n");
	
		builder.append("Success Policy Count         :  " + policyCacheDetail.getSuccessCounter() + "\n");
		builder.append("Partial Success Policy Count :  " + policyCacheDetail.getPartialSuccessCounter() + "\n");
		builder.append("Failure Policy Count         :  " + policyCacheDetail.getFailureCounter()+"\n");
		builder.append("Last Known Good Policy Count :  " + policyCacheDetail.getLastKnownGoodCounter()+"\n");

		return builder.toString(); 
		
		
	}
	
	private String viewHistory() {
		Iterator<PolicyCacheDetail> policyCacheDetails=serverContext.getPolicyRepository().reloadHistory();
		TableFormatter formatter=new TableFormatter(headerHistory,widthHistory,TableFormatter.OUTER_BORDER);
		if(policyCacheDetails == null || policyCacheDetails.hasNext()==false){
			formatter.add("NO HISTORY FOUND",TableFormatter.CENTER);
			return formatter.getFormattedValues();
		}
		while(policyCacheDetails.hasNext()){
				formatter.addRecord(formatReloadCachedPolicy(policyCacheDetails.next()));
		}
		return formatter.getFormattedValues();
	}
	@Override
	public String getHelpMsg() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("   "+"reload"+" <option>");
		out.println(" Description: Describes reload options");
		out.println(" Possible Options: ");
		out.println();
		for(Map.Entry<String, DetailProvider> provider : detailProviderMap.entrySet()){
			out.println(" " +provider + provider.getValue().getDescription());
		}
		out.println(EliteBaseCommand.fillChar(" " + DATA,19) + " Reload data packages");
		out.println(EliteBaseCommand.fillChar(" " + IMS,19) + " Reload ims packages");
		out.println(EliteBaseCommand.fillChar(" " + RNC,19) + " Reload rnc packages");
		out.println(EliteBaseCommand.fillChar(" " + OFFER,19) + " Reload product offers");
		out.println(EliteBaseCommand.fillChar(" " + HISTORY,19) + " Displays History of Reloaded Policies");
		out.close();
		return stringWriter.toString();
	}
	
	@Override
	public String getDescription() {
		return "Reload policy";
	}


	@Override
	public String getKey() {
		return RELOAD;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
	private String[] formatReloadCachedPolicy(PolicyCacheDetail policyCache){
			String[] data = new String[widthHistory.length];
			data[0] = policyCache.getStatus().status;
			data[1] = policyCache.getRemark()==null ? " -- " : policyCache.getRemark();
			data[2] = policyCache.getDate().toString();
			return data;
		}
		
	@Override
	public String getHotkeyHelp() {
		StringWriter writer = new StringWriter();
		PrintWriter out= new PrintWriter(writer);
		out.print("'"+RELOAD+"':{'"+HISTORY+"':{},'"+HELP+"':{}");
		
		List<String> dataPackages = serverContext.getPolicyRepository().getAllDataPackageNames();
		List<String> imsPackages = serverContext.getPolicyRepository().getAllIMSPackageNames();
		List<String> offerNames = getProductOfferNames();
		List<String> rncPackageNames = getRnCPackageNames();
		List<String> bodPackageNames = getBoDPackageNames();
		
		
		if(Collectionz.isNullOrEmpty(dataPackages) == false
				|| Collectionz.isNullOrEmpty(imsPackages) == false
				|| Collectionz.isNullOrEmpty(offerNames) == false
				|| Collectionz.isNullOrEmpty(rncPackageNames) == false
				|| Collectionz.isNullOrEmpty(bodPackageNames) == false) {
			
			if(Collectionz.isNullOrEmpty(dataPackages) == false) {
				printFormattedNames(out, dataPackages, DATA);
			}

			if(Collectionz.isNullOrEmpty(imsPackages) == false) {
				printFormattedNames(out, imsPackages, IMS);
			}

			if(Collectionz.isNullOrEmpty(offerNames) == false) {
				printFormattedNames(out, offerNames, OFFER);
			}

			if(Collectionz.isNullOrEmpty(rncPackageNames) == false) {
				printFormattedNames(out, rncPackageNames, RNC);
			}

			if(Collectionz.isNullOrEmpty(bodPackageNames) == false) {
				printFormattedNames(out, bodPackageNames, BOD);
			}
			
		}

		for(DetailProvider provider : detailProviderMap.values()){
				out.print("," + provider.getHotkeyHelp());
		}
		
		out.print("}");
		return writer.toString() ;
	}

	private void printFormattedNames(PrintWriter printWriter, List<String> packages, String type) {
		printWriter.print(",'" + type + "':{");

		printWriter.print("'" + packages.get(0).replace(" ", "\\\\ ") + "':{}");
		for (int i = 1; i < packages.size(); i++) {
			printWriter.print(",'" + packages.get(i).replace(" ", "\\\\ ") + "':{}");
		}
		printWriter.print("}");
	}

	private List<String> getProductOfferNames(){
		return serverContext.getPolicyRepository().getProductOffer().all().stream().map(ProductOffer::getName).collect(Collectors.toList());
	}

	private List<String> getRnCPackageNames(){
		return serverContext.getPolicyRepository().getRnCPackage().all().stream().map(RnCPackage::getName).collect(Collectors.toList());
	}

	private List<String> getBoDPackageNames(){
		return serverContext.getPolicyRepository().getBoDPackage().all().stream().map(BoDPackage::getName).collect(Collectors.toList());
	}

	private String[] formatpolicy(PolicyDetail policy){
		String[] data = new String[width.length];
		data[0] = policy.getName().replace(" ", "\\ ");
		data[1] = policy.getStatus().status;
		data[2] = policy.getPackageType();
		data[3] = policy.getPackageMode().name();
		
		if(Strings.isNullOrBlank(policy.getRemark()) == false){
			data[4] = policy.getRemark();
		} else {
			data[4] = "--";
		}
		return data; 		
	}
	
	private String viewDataPackageNames(List<String> dataPackages) {
		StringWriter stringWriter = new StringWriter();
		IndentingWriter out =  new IndentingPrintWriter(stringWriter);

		if(Collectionz.isNullOrEmpty(dataPackages) == false){
			Collections.sort(dataPackages);
			out.println();
			out.incrementIndentation();
			out.println("Select data package name to reload");
			out.println("---------------------------");
			out.println();
			for(String pkgName : dataPackages){
				out.println(pkgName.replace(" ", "\\ "));
			}
			out.decrementIndentation();
		} else {
			out.println("No data packages configured");
		}

		out.println();
		out.close();
		return stringWriter.toString();
	}

	private String reloadDataPolicies(String[] parameters) {
		return displayPolicyCacheDetails(serverContext.getPolicyRepository().reloadDataPackages(parameters));
	}

	private String reloadIMSPolicies(String[] parameters) {
		return displayPolicyCacheDetails(serverContext.getPolicyRepository().reloadIMSPackages(parameters));
	}

	private String reloadProductOffers(String[] parameters) {
		return displayPolicyCacheDetails(serverContext.getPolicyRepository().reloadProductOffers(ProductOfferPredicates.createNameFilter(parameters)));
	}

	private String reloadRnCPackages(String[] parameters) {
		return displayPolicyCacheDetails(serverContext.getPolicyRepository().reloadRnCPackages(RnCPackagePredicates.createNameFilter(parameters)));
	}

	private String reloadBoDPackages(String[] parameters) {
		return displayPolicyCacheDetails(serverContext.getPolicyRepository().reloadBoDPackages(BoDDataPredicates.createNameFilter(parameters)));
	}

	private String viewIMSPackageNames(List<String> imsPackages) {
		StringWriter stringWriter = new StringWriter();
		IndentingWriter out =  new IndentingPrintWriter(stringWriter);
		if(Collectionz.isNullOrEmpty(imsPackages) == false){
			Collections.sort(imsPackages);
			out.println();
			out.incrementIndentation();
			out.println("Select ims package name to reload");
			out.println("---------------------------");
			out.println();
			for(String pkgName : imsPackages){
				out.println(pkgName.replace(" ", "\\ "));
			}
			out.decrementIndentation();
		} else {
			out.println("No ims packages configured");
		}
		out.println();
		out.close();
		return stringWriter.toString();
	}

	private String viewProductOffers(List<String> productOffers) {
		StringWriter stringWriter = new StringWriter();
		IndentingWriter out =  new IndentingPrintWriter(stringWriter);
		if(Collectionz.isNullOrEmpty(productOffers) == false){
			Collections.sort(productOffers);
			out.println();
			out.incrementIndentation();
			out.println("Select product offer name to reload");
			out.println("---------------------------");
			out.println();
			for(String pkgName : productOffers){
				out.println(pkgName.replace(" ", "\\ "));
			}
			out.decrementIndentation();
		} else {
			out.println("No product offer configured");
		}
		out.println();
		out.close();
		return stringWriter.toString();
	}

	private String viewRnCPackages(List<String> rncPackages) {
		StringWriter stringWriter = new StringWriter();
		IndentingWriter out =  new IndentingPrintWriter(stringWriter);
		if(Collectionz.isNullOrEmpty(rncPackages) == false){
			Collections.sort(rncPackages);
			out.println();
			out.incrementIndentation();
			out.println("Select rnc package name to reload");
			out.println("---------------------------");
			out.println();
			for(String pkgName : rncPackages){
				out.println(pkgName.replace(" ", "\\ "));
			}
			out.decrementIndentation();
		} else {
			out.println("No rnc package configured");
		}
		out.println();
		out.close();
		return stringWriter.toString();
	}

	private String viewBoDPackages(List<String> boDPackages) {
		StringWriter stringWriter = new StringWriter();
		IndentingWriter out =  new IndentingPrintWriter(stringWriter);
		if(Collectionz.isNullOrEmpty(boDPackages) == false){
			Collections.sort(boDPackages);
			out.println();
			out.incrementIndentation();
			out.println("Select BoD Package name to reload");
			out.println("---------------------------------");
			out.println();
			for(String pkgName : boDPackages){
				out.println(pkgName.replace(" ", "\\ "));
			}
			out.decrementIndentation();
		} else {
			out.println("No BoD Package configured");
		}
		out.println();
		out.close();
		return stringWriter.toString();
	}
}


