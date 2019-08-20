package com.elitecore.netvertex.cli;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.monetaryrechargeplan.MonetaryRechargePlan;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.pm.sliceconfig.DataSliceConfiguration;
import com.elitecore.netvertex.core.NetVertexServerContext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PolicyCommand extends EliteBaseCommand {
	private NetVertexServerContext serverContext;


	private static final String COMMAND="policy";
	private static final String HELP = "-help";
	private static final String STATUS = "-status";
	private static final String VIEW = "-view";
	private static final String IMS = "-ims";
	private static final String TOPUP = "-topup";
	private static final String OFFER = "-offer";
	private static final String DATA = "-data";
	private static final String RNC = "-rnc";
	private static final String BOD = "-bod";
	private static final String MONETARY_RECHARGE_PLAN = "-monetaryRechargePlan";
	private static final String SLICE_CONFIGURATION = "-slice-configuration";
	private static final int WIDTH_POLICYNAME = 30;
	private static final int WIDTH_STATUS = 20;
	private static final int WIDTH_TYPE = 5;
	private static final int WIDTH_MODE = 20;
	private static final int WIDTH_REMARK = 50;

	private  Map<String ,DetailProvider> detailProviderMap ;

	private int[] widthStatus = {WIDTH_POLICYNAME,WIDTH_STATUS,WIDTH_TYPE, WIDTH_MODE ,WIDTH_REMARK};
	private String[] headerStatus = {"Policy Name","Status","Type", "Mode" ,"Remarks"};

	public PolicyCommand(NetVertexServerContext serverContext) {
		this.serverContext=serverContext;
		detailProviderMap=new LinkedHashMap<String, DetailProvider>(4,1);
	}

	public void registerDetailProvider(DetailProvider detailprovider) throws RegistrationFailedException{

		if(detailprovider.getKey() == null){
			throw new  RegistrationFailedException("Failed to register detail provider. Reason: key is not specified.");
		}

		if(detailProviderMap.containsKey(detailprovider.getKey().toLowerCase())){
			throw new  RegistrationFailedException("Failed to register detail provider. Reason: Policy Command already contains detail provider with Key : " + detailprovider.getKey());
		}
		detailProviderMap.put(detailprovider.getKey().toLowerCase() , detailprovider);
	}



	@Override
	public String getCommandName() {
		return COMMAND;
	}

	@Override
	public String getDescription() {
		return "Describes Policy";
	}

	@Override
	public String getHotkeyHelp() {
		StringWriter writer = new StringWriter();
		PrintWriter out= new PrintWriter(writer);
		out.print("{'"+ COMMAND +"':{'"+ HELP +"' :{}");


		List<String> dataPackages = serverContext.getPolicyRepository().getAllDataPackageNames();
		List<String> imsPackages = serverContext.getPolicyRepository().getAllIMSPackageNames();
		List<String> quotaTopUps = getAllQuotaTopUpNames();
		List<String> productOffers = getProductOfferNames();
		List<String> rncPackages = getRnCPackageNames();
		List<String> bodPackages = getBoDPackageNames();
		List<String> monetaryRechargePlans = getMonetaryRechargePlanNames();


		if(dataPackages.isEmpty()==false || imsPackages.isEmpty() == false
				|| quotaTopUps.isEmpty() == false || productOffers.isEmpty() == false
				|| rncPackages.isEmpty() == false || monetaryRechargePlans.isEmpty() == false
				|| bodPackages.isEmpty() == false) {

			out.print(",'"+ STATUS +"':{},'"+ VIEW +"':{");
			if(dataPackages.isEmpty() == false) {
				printFormattedNames(out, dataPackages, DATA);
			}

			if(imsPackages.isEmpty() == false) {
				printFormattedNames(out, imsPackages, IMS);
			}

			if(quotaTopUps.isEmpty() == false) {
				printFormattedNames(out, quotaTopUps, TOPUP);
			}

			if(productOffers.isEmpty() == false) {
				printFormattedNames(out, productOffers, OFFER);
			}

			if(rncPackages.isEmpty() == false) {
				printFormattedNames(out, rncPackages, RNC);
			}

			if(bodPackages.isEmpty() == false) {
				printFormattedNames(out, bodPackages, BOD);
			}

			if (monetaryRechargePlans.isEmpty() == false) {
				printFormattedNames(out, monetaryRechargePlans, MONETARY_RECHARGE_PLAN);
			}

			out.print("}");
		}


		for(DetailProvider provider : detailProviderMap.values()){
				out.print("," + provider.getHotkeyHelp());
		}
		out.print("}}");

		return writer.toString() ;

	}

	private void printFormattedNames(PrintWriter out, List<String> dataPackages, String type) {
		out.print("'"+ type +"':{");
		out.print("'" + dataPackages.get(0).replace(" ", "\\\\ ") +"':{}");
		for(int i=1; i < dataPackages.size(); i++ ){
            out.print(",'" + dataPackages.get(i).replace(" ", "\\\\ ") +"':{}");
        }
		out.print("},");
	}

	@Override
	public String execute(String parameter) {
		String[] parameters = parameter.split("(?<!\\\\)(\\s)");
		if (parameter.trim().length()==0 || "?".equals(parameters[0]) || "-help".equalsIgnoreCase(parameters[0])) {
			return getHelpMsg();
		}

		removeEscape(parameters);


		if (detailProviderMap.containsKey(parameters[0].toLowerCase())) {
			String[] destArray=new String[parameters.length-1];
			System.arraycopy(parameters, 1, destArray, 0, destArray.length);
			return detailProviderMap.get(parameters[0].toLowerCase()).execute(destArray);
		} else if (parameters[0].equalsIgnoreCase(STATUS)){
			List<PolicyDetail> listPolicy;
			if(parameters.length > 1){
				String[] destArray=new String[parameters.length-1];
				System.arraycopy(parameters, 1, destArray, 0, destArray.length);

				listPolicy = serverContext.getPolicyRepository().getPolicyDetail(destArray);
				return viewPolicyStatus(listPolicy);
			}
			listPolicy = serverContext.getPolicyRepository().getPolicyDetail();
			return viewPolicyStatus(listPolicy);
		} else if (parameters[0].equalsIgnoreCase(VIEW)) {

			if(parameters.length == 1) {
				String dataPkgStr = viewDataPackageNames(serverContext.getPolicyRepository().getAllDataPackageNames());
				String imsPkgStr = viewIMSPackageNames(serverContext.getPolicyRepository().getAllIMSPackageNames());
				String quotaTopUpStr = viewQuotaTopUpNames(getAllQuotaTopUpNames());
				String productOfferStr = viewProductOfferNames(getProductOfferNames());
				String rncPackageStr = viewRnCPackageNames(getRnCPackageNames());
				String bodPackageStr = viewBoDPackageNames(getBoDPackageNames());
				String monetaryRechargePlanStr = viewMonetaryRechargePlanNames(getMonetaryRechargePlanNames());

				return dataPkgStr + imsPkgStr + quotaTopUpStr + productOfferStr
						+ rncPackageStr + bodPackageStr + monetaryRechargePlanStr;
			} else {
				String[] destArray = new String[parameters.length-1];
				System.arraycopy(parameters, 1, destArray, 0, destArray.length);

				switch (destArray[0]){
					case  DATA :
						if (destArray.length == 1) {
							return viewDataPackageNames(serverContext.getPolicyRepository().getAllDataPackageNames());
						} else {
							return viewDataPolicy(serverContext.getPolicyRepository().getPkgDatasByName(destArray));
						}
					case IMS :
						if(destArray.length == 1) {
							return viewIMSPackageNames(serverContext.getPolicyRepository().getAllIMSPackageNames());
						} else {
							return viewIMSPackages(serverContext.getPolicyRepository().getIMSPackageByName(destArray));
						}
					case TOPUP :
						if(destArray.length == 1) {
							return viewQuotaTopUpNames(getAllQuotaTopUpNames());
						} else {
							return viewQuotaTopUps(getQuotaTopUpsByName(destArray));
						}
                    case OFFER :
                        if(destArray.length == 1) {
                            return viewProductOfferNames(getProductOfferNames());
                        } else {
                            return viewProductOffers(getProductOfferByName(destArray));
                        }
                    case RNC :
                        if(destArray.length == 1) {
                            return viewRnCPackageNames(getRnCPackageNames());
                        } else {
                            return viewRnCPackages(getRnCPackageByName(destArray));
                        }
					case BOD :
						if(destArray.length == 1) {
							return viewBoDPackageNames(getBoDPackageNames());
						} else {
							return viewBoDPackages(getBoDPackageByName(destArray));
						}

					case MONETARY_RECHARGE_PLAN :
						if (destArray.length == 1) {
							return viewMonetaryRechargePlanNames(getMonetaryRechargePlanNames());
						} else {
							return viewMonetaryRechargePlans(getMonetaryRechargePlanByName(destArray));
						}

					case SLICE_CONFIGURATION:
						return viewSliceConfiguration(getSliceConfiguration());

                    default:
						return getHelpMsgForView();
				}

			}

		}

		return "Invalid Argument" +  getHelpMsg();
	}

	private String viewSliceConfiguration(DataSliceConfiguration sliceConfiguration) {
		StringWriter stringWriter = new StringWriter();
		IndentingWriter out = new IndentingPrintWriter(stringWriter);

		if(Objects.isNull(sliceConfiguration) == false){
			out.print(sliceConfiguration);
		} else {
			out.println("No data slice configuration found");
		}

		out.close();
		return stringWriter.toString();
	}

	private List<QuotaTopUp> getQuotaTopUpsByName(String... packageNames) {

		List<String> requiredPackageNames = Arrays.asList(packageNames);

		return serverContext.getPolicyRepository().getActiveAllQuotaTopUpDatas().stream().filter(pkg -> requiredPackageNames.contains(pkg.getName())).collect(Collectors.toList());
	}

    private List<ProductOffer> getProductOfferByName(String... packageNames) {

        List<String> requiredPackageNames = Arrays.asList(packageNames);

        return serverContext.getPolicyRepository().getProductOffer().all().stream().filter(offer -> requiredPackageNames.contains(offer.getName())).collect(Collectors.toList());
    }

    private List<RnCPackage> getRnCPackageByName(String... packageNames) {

        List<String> requiredPackageNames = Arrays.asList(packageNames);

        return serverContext.getPolicyRepository().getRnCPackage().all().stream().filter(offer -> requiredPackageNames.contains(offer.getName())).collect(Collectors.toList());
    }

	private List<BoDPackage> getBoDPackageByName(String... packageNames) {
		List<String> requiredPackageNames = Arrays.asList(packageNames);
		return serverContext.getPolicyRepository().getBoDPackage().all().stream().filter(boDPackage -> requiredPackageNames.contains(boDPackage.getName())).collect(Collectors.toList());
	}

	private List<MonetaryRechargePlan> getMonetaryRechargePlanByName(String... packageNames) {

		List<String> requiredPackageNames = Arrays.asList(packageNames);

		return serverContext.getPolicyRepository().monetaryRechargePlan().all().stream().filter(plan -> requiredPackageNames.contains(plan.getName())).collect(Collectors.toList());
	}

	private List<String> getAllQuotaTopUpNames(){
		return serverContext.getPolicyRepository().getActiveAllQuotaTopUpDatas().stream().map(QuotaTopUp::getName).collect(Collectors.toList());
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

	private List<String> getMonetaryRechargePlanNames() {
		return serverContext.getPolicyRepository().monetaryRechargePlan().all().stream().map(MonetaryRechargePlan::getName).collect(Collectors.toList());
	}

	private void removeEscape(String[] parameters) {
		for(int parameterIndex = 0; parameterIndex < parameters.length; parameterIndex++) {
			parameters[parameterIndex] = parameters[parameterIndex].replace("\\ ", " ");
		}
	}

	private String getHelpMsgForView() {

		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println(" "+getCommandName()+"  " + VIEW);
		out.println(" Description: To view existing policies");
		out.println(" Possible Options: ");
		out.println();
		out.println(fillChar(" " + DATA,19) + " View data packages");
		out.println(fillChar(" " + IMS,19) + " View ims packages");
        out.println(fillChar(" " + TOPUP,19) + " View topup packages");
        out.println(fillChar(" " + RNC,19) + " View rnc packages");
		out.println(fillChar(" " + BOD,19) + " View BoD packages");
		out.println(fillChar(" " + OFFER,19) + " View product offers");
		out.println(fillChar(" " + MONETARY_RECHARGE_PLAN,19) + " View monetary recharge plans");
		out.println(fillChar(" " + SLICE_CONFIGURATION,25) + " View data slice configuration");
		out.close();
		return stringWriter.toString();
	}

	private String viewIMSPackageNames(List<String> imsPackages) {
		StringWriter stringWriter = new StringWriter();
		IndentingWriter out =  new IndentingPrintWriter(stringWriter);
		if(Collectionz.isNullOrEmpty(imsPackages) == false){
			Collections.sort(imsPackages);
			out.println();
			out.incrementIndentation();
			out.println("Select ims package name to view");
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

	private String viewDataPackageNames(List<String> dataPackages) {
		StringWriter stringWriter = new StringWriter();
		IndentingWriter out =  new IndentingPrintWriter(stringWriter);

		if(Collectionz.isNullOrEmpty(dataPackages) == false){
			Collections.sort(dataPackages);
			out.println();
			out.incrementIndentation();
			out.println("Select data package name to view");
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

	private String viewQuotaTopUpNames(List<String> quotaTopUps) {
		StringWriter stringWriter = new StringWriter();
		IndentingWriter out =  new IndentingPrintWriter(stringWriter);

		if(Collectionz.isNullOrEmpty(quotaTopUps) == false){
			Collections.sort(quotaTopUps);
			out.println();
			out.incrementIndentation();
			out.println("Select topup package name to view");
			out.println("---------------------------");
			out.println();
			for(String pkgName : quotaTopUps){
				out.println(pkgName.replace(" ", "\\ "));
			}
			out.decrementIndentation();
		} else {
			out.println("No topup packages configured");
		}

		out.println();
		out.close();
		return stringWriter.toString();
	}

    private String viewProductOfferNames(List<String> productOffers) {
        StringWriter stringWriter = new StringWriter();
        IndentingWriter out =  new IndentingPrintWriter(stringWriter);

        if(Collectionz.isNullOrEmpty(productOffers) == false){
            Collections.sort(productOffers);
            out.println();
            out.incrementIndentation();
            out.println("Select product offer name to view");
            out.println("---------------------------");
            out.println();
            for(String pkgName : productOffers){
                out.println(pkgName.replace(" ", "\\ "));
            }
            out.decrementIndentation();
        } else {
            out.println("No product offers configured");
        }

        out.println();
        out.close();
        return stringWriter.toString();
    }

    private String viewRnCPackageNames(List<String> productOffers) {
        StringWriter stringWriter = new StringWriter();
        IndentingWriter out =  new IndentingPrintWriter(stringWriter);

        if(Collectionz.isNullOrEmpty(productOffers) == false){
            Collections.sort(productOffers);
            out.println();
            out.incrementIndentation();
            out.println("Select rnc package name to view");
            out.println("---------------------------");
            out.println();
            for(String pkgName : productOffers){
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

	private String viewBoDPackageNames(List<String> bodPackages) {
		StringWriter stringWriter = new StringWriter();
		IndentingWriter out =  new IndentingPrintWriter(stringWriter);

		if(Collectionz.isNullOrEmpty(bodPackages) == false){
			Collections.sort(bodPackages);
			out.println();
			out.incrementIndentation();
			out.println("Select BoD Package name to view");
			out.println("---------------------------------");
			out.println();
			for(String pkgName : bodPackages){
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

    private String viewMonetaryRechargePlanNames(List<String> monetaryRechargePlans) {
		StringWriter stringWriter = new StringWriter();
		IndentingWriter out = new IndentingPrintWriter(stringWriter);

		if(Collectionz.isNullOrEmpty(monetaryRechargePlans) == false){
			Collections.sort(monetaryRechargePlans);
			out.println();
			out.incrementIndentation();
			out.println("Select monetary recharge plan name to view");
			out.println("---------------------------");
			out.println();
			for(String pkgName : monetaryRechargePlans){
				out.println(pkgName.replace(" ", "\\ "));
			}
			out.decrementIndentation();
		} else {
			out.println("No monetary recharge plan configured");
		}

		out.println();
		out.close();
		return stringWriter.toString();
	}

	private String viewDataPolicy(List<Package> pkgs) {

		StringWriter stringWriter = new StringWriter();
		IndentingWriter out =  new IndentingPrintWriter(stringWriter);

		if(Collectionz.isNullOrEmpty(pkgs) == false){
			out.println();
			for(UserPackage pkg : pkgs){
				out.println(pkg);
			}
		}else{
			out.println("NO POLICY FOUND");
		}
		out.println();
		out.close();
		return stringWriter.toString();
	}

	private String viewIMSPackages(List<IMSPackage> pkgs) {

		StringWriter stringWriter = new StringWriter();
		IndentingWriter out =  new IndentingPrintWriter(stringWriter);

		if(Collectionz.isNullOrEmpty(pkgs) == false){
			out.println();
			for(IMSPackage pkg : pkgs){
				out.println(pkg);
			}
		}else{
			out.println("NO POLICY FOUND");
		}
		out.println();
		out.close();
		return stringWriter.toString();
	}

	private String viewQuotaTopUps(List<QuotaTopUp> pkgs) {

		StringWriter stringWriter = new StringWriter();
		IndentingWriter out =  new IndentingPrintWriter(stringWriter);

		if(Collectionz.isNullOrEmpty(pkgs) == false){
			out.println();
			for(QuotaTopUp pkg : pkgs){
				out.println(pkg);
			}
		}else{
			out.println("NO POLICY FOUND");
		}
		out.println();
		out.close();
		return stringWriter.toString();
	}

    private String viewProductOffers(List<ProductOffer> productOffers) {

        StringWriter stringWriter = new StringWriter();
        IndentingWriter out =  new IndentingPrintWriter(stringWriter);

        if(Collectionz.isNullOrEmpty(productOffers) == false){
            out.println();
            for(ProductOffer offer : productOffers){
                out.println(offer);
            }
        }else{
            out.println("NO OFFER FOUND");
        }
        out.println();
        out.close();
        return stringWriter.toString();
    }

    private String viewRnCPackages(List<RnCPackage> rnCPackages) {

        StringWriter stringWriter = new StringWriter();
        IndentingWriter out =  new IndentingPrintWriter(stringWriter);

        if(Collectionz.isNullOrEmpty(rnCPackages) == false){
            out.println();
            for(RnCPackage rnCPackage : rnCPackages){
                out.println(rnCPackage);
            }
        }else{
            out.println("NO RnC PACKAGE FOUND");
        }
        out.println();
        out.close();
        return stringWriter.toString();
    }

	private String viewBoDPackages(List<BoDPackage> boDPackages) {

		StringWriter stringWriter = new StringWriter();
		IndentingWriter out =  new IndentingPrintWriter(stringWriter);

		if(Collectionz.isNullOrEmpty(boDPackages) == false){
			out.println();
			for(BoDPackage boDPackage : boDPackages){
				out.println(boDPackage);
			}
		}else{
			out.println("NO BoD PACKAGE FOUND");
		}
		out.println();
		out.close();
		return stringWriter.toString();
	}

	private String viewMonetaryRechargePlans(List<MonetaryRechargePlan> monetaryRechargePlans) {

		StringWriter stringWriter = new StringWriter();
		IndentingWriter out =  new IndentingPrintWriter(stringWriter);

		if(Collectionz.isNullOrEmpty(monetaryRechargePlans) == false){
			out.println();
			for(MonetaryRechargePlan monetaryRechargePlan : monetaryRechargePlans){
				out.println(monetaryRechargePlan);
			}
		}else{
			out.println("NO Monetary Recharge Plan FOUND");
		}
		out.println();
		out.close();
		return stringWriter.toString();
	}

	@Override
	public String getHelpMsg() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println(" "+getCommandName()+"  <option>");
		out.println(" Description: "+ getDescription());
		out.println(" Possible Options: ");
		out.println();
		for(Map.Entry<String, DetailProvider> idToDetailProvider: detailProviderMap.entrySet()){
			String provider = idToDetailProvider.getKey();
			out.println(" " +provider + "\t\t\t" + detailProviderMap.get(provider).getDescription());
		}
		out.println(fillChar(" " + VIEW,19) + " View policy details");
		out.println(fillChar(" " + STATUS,19) + " View policy status");
		out.close();
		return stringWriter.toString();
	}

	private String[] formatpolicy(PolicyDetail policy){
		String[] data = new String[widthStatus.length];
		data[0] = policy.getName().replace(" ", "\\ ");
		data[1] = policy.getStatus().status;
		data[2] = policy.getPackageType() +" - "+ policy.getEntityType();
		data[3] = policy.getPackageMode().name();

		if(Strings.isNullOrBlank(policy.getRemark()) == false) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);

			printWriter.print(policy.getRemark());
			data[4] = stringWriter.toString();
		} else {
			data[4] = "--";
		}

		return data;
	}


	private String viewPolicyStatus(List<PolicyDetail> listPolicy){
		TableFormatter formatter=new TableFormatter(headerStatus,widthStatus,TableFormatter.ALL_BORDER);
		if(listPolicy != null && listPolicy.isEmpty()==false){
			for(PolicyDetail policy : listPolicy){
				formatter.addRecord(formatpolicy(policy));
			}

			return formatter.getFormattedValues();
		}
		formatter.add("NO POLICY FOUND",TableFormatter.CENTER);
		return formatter.getFormattedValues();

	}



	public DataSliceConfiguration getSliceConfiguration() {
		return serverContext.getPolicyRepository().getSliceConfiguration();
	}
}
