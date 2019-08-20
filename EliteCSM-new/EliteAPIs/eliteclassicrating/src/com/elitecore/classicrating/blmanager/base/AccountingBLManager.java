/**
 *      Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 20, 2008
 *	@author Raghu
 *
 */
package com.elitecore.classicrating.blmanager.base;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.elitecore.classicrating.api.IResponseObject;
import com.elitecore.classicrating.base.IBaseConstant;
import com.elitecore.classicrating.base.IRatingAppContext;
import com.elitecore.classicrating.commons.data.CDRData;
import com.elitecore.classicrating.commons.request.RequestParameters;
import com.elitecore.classicrating.commons.response.ResponseObject;
import com.elitecore.classicrating.commons.validators.RequestParameterValidator;
import com.elitecore.classicrating.datamanager.customer.CustomerBalanceDBHelper;
import com.elitecore.classicrating.datamanager.customer.CustomerServiceData;
import com.elitecore.classicrating.datamanager.packages.SlabDefinitionData;
import com.elitecore.classicrating.datamanager.rating.CDRChargeDBHelper;
import com.elitecore.classicrating.datamanager.rating.CDRChargeData;
import com.elitecore.classicrating.datamanager.rating.PrefixGroupDefinitionData;
import com.elitecore.classicrating.datamanager.rating.RateDefinitionDBHelper;
import com.elitecore.classicrating.datamanager.rating.RateDefinitionData;
import com.elitecore.classicrating.datamanager.rating.RateNotFoundException;
import com.elitecore.classicrating.datasource.ITransactionContext;

/**
 *
 * @author raghug
 */
public class AccountingBLManager extends RatingBaseBLManager {

    private String MODULE = "ACCOUNTING BL MANAGER";

    public AccountingBLManager(IRatingAppContext ratingAppContext) {
        super(ratingAppContext);
    }

    public AccountingBLManager(IRatingAppContext ratingAppContext, ITransactionContext transactionContext) {
        super(ratingAppContext, transactionContext);
    }

    /**
     *
     * @param request
     * @return
     * @throws com.elitecore.classicrating.blmanager.base.RatingBLException
     * @throws java.lang.Exception
     */
    public IResponseObject doAccounting(RequestParameters request) throws RatingBLException, Exception {
        Logger.info(MODULE, "Into doAccounting( " + request + " )");

        IResponseObject response = new ResponseObject();

        boolean doDBOperation = true;

        try {

            startTransaction();

            request.put(IBaseConstant.REQUEST_TYPE, IBaseConstant.ACCOUNTING);
            RequestParameterValidator.validateParameters(request);

            CDRData cdrData = new CDRData(request);

            CustomerServiceData customerData = null;
            customerData = doGuiding(cdrData);

            doRating(customerData, cdrData, doDBOperation, response);

            Logger.info(MODULE, "Customer " + request.get(IBaseConstant.USERID) + " is successfully authenticated");
        } catch (NumberFormatException e) {
            Logger.error(MODULE, "Exception caught while doing Authorization.");
            Logger.trace(MODULE, e);
            throw e;
        } catch (MandatoryParameterMissingException e) {
            Logger.error(MODULE, "Exception caught while doing Authorization.");
            Logger.trace(MODULE, e);
            throw e;

        } catch (ParseException e) {
            Logger.error(MODULE, "Exception caught while doing Authorization.");
            Logger.trace(MODULE, e);
            throw e;
        } catch (SQLException e) {
            super.getTransactionContext().setRollbackOnly();
            Logger.error(MODULE, "Exception caught while doing Authorization.");
            Logger.trace(MODULE, e);
            throw e;

        } catch (RatingBLException e) {

            super.getTransactionContext().setRollbackOnly();
            Logger.error(MODULE, "Exception caught while doing Authorization.");
            Logger.trace(MODULE, e);
            throw e;
        } finally {
            try {
                endTransaction();
            } catch (SQLException e) {
                Logger.info(MODULE, "Exception caught while handeling Transaction");
                throw e;
            }
        }
        Logger.info(MODULE, "OutOf doAccounting( " + request + " ) = " + response);
        return response;
    }

    /**
     * <P> This method will perform DBOperation i.e CDR Insertion and Usage and Balance Updation </P>
     * @method doDBOperation
     * @param CustomerServiceData, CDRData,ResponseObject
     * @return 
     * @throws SQLException
     * */
    private void doDBOperations(CustomerServiceData customerServiceData, CDRChargeData cdrChargeData, IResponseObject response) throws SQLException {
        Logger.trace(MODULE, "InTo doDBOperations( " + customerServiceData + ", " + cdrChargeData + ", " + response + " )");
        try {
            if (customerServiceData.getCustomerType().equalsIgnoreCase(IBaseConstant.PREPAID)) {
                double availBalance = updateCustomerBalance(customerServiceData, cdrChargeData.getUsageCost());
                response.put(IBaseConstant.BALANCE, String.valueOf((int) availBalance));
            } else if (customerServiceData.getCustomerType().equalsIgnoreCase(IBaseConstant.POSTPAID) &&
                    customerServiceData.getPackageDefinitionData().getPackageType().equalsIgnoreCase(IBaseConstant.USAGE_BASED_PACKAGE)) {

                long currentUsage = 0;
                if (customerServiceData.getServiceType().equalsIgnoreCase(IBaseConstant.VOIP)) {
                    currentUsage = cdrChargeData.getSessionTime();
                } else if (customerServiceData.getServiceType().equalsIgnoreCase(IBaseConstant.DATA)) {
                    Logger.debug(MODULE, "Volume from CDR is : " + cdrChargeData.getVolume());
                    currentUsage = (long) cdrChargeData.getVolume();

                }

                updateCustomerTotalUsage(customerServiceData, currentUsage);
            }

            insertCDRChargeDetails(cdrChargeData);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            Logger.trace(MODULE, e);
            Logger.error(MODULE, "Exception caught while Calculating Usage Cost for VIOP");
        }
        Logger.trace(MODULE, "OutOf doDBOperations( " + customerServiceData + ", " + cdrChargeData + ", " + response + " )");
    }

    /**
     * <p> This method will set CDR information into CDRCharge Object
     * @method setCDRChargeDetails
     * @param cdrData, customerServiceData
     * @return CDRChargeData
     * */
    private CDRChargeData setCDRChargeDetails(CDRData cdrData, CustomerServiceData customerServiceData) {
        CDRChargeData cdrChargeData = null;

        try {
            cdrChargeData = new CDRChargeData();
            Date now = new Date();
            Timestamp sysDate = new Timestamp(now.getTime());
            cdrChargeData.setAcctInputOctets(cdrData.getAcctInputOctets());
            cdrChargeData.setAcctOutputOctets(cdrData.getAcctOutputOctets());
            cdrChargeData.setCalledStationId(cdrData.getCalledStationId());
            cdrChargeData.setCallEnd(cdrData.getCallEnd());
            cdrChargeData.setCallStart(cdrData.getCallStart());
            cdrChargeData.setSessionId(cdrData.getSessionId());
            
            // store cost in paisa
            long usageCost = (long) cdrData.getUsageCost()*100;
            cdrChargeData.setCharge(usageCost);
            cdrChargeData.setCustomerIdentifier(customerServiceData.getCustomerIdentifier());
            cdrChargeData.setCustomerName(customerServiceData.getCustomerName());
            cdrChargeData.setServiceType(customerServiceData.getServiceType());
            cdrChargeData.setSessionTime(cdrData.getSessionTime());
            cdrChargeData.setVolume(cdrData.getVolume());
            cdrChargeData.setUsageCost(cdrData.getUsageCost());
            cdrChargeData.setChargedDate(sysDate);
            cdrChargeData.setEventType(cdrData.getEventType());

        } catch (Exception e) {
            Logger.trace(MODULE, e);
            Logger.error(MODULE, "Exception Caught while setting the CDRChargeData for insertion");
        }
        return cdrChargeData;
    }

    /**
     *<p> This method will insert CDR Details into Database
     * @method insertCDRChargeDetails
     * @param cdrChargeDetails
     * @return
     * @author sheetalsoni
     * @created on 10/7/2008
     */
    private void insertCDRChargeDetails(CDRChargeData cdrChargeData) throws SQLException, Exception {
        Logger.trace(MODULE, "InTo insertCDRChargeDetails( " + cdrChargeData + " )");
        CDRChargeDBHelper cdrChargeDBHelper = null;

        try {
            if (cdrChargeData != null) {
                cdrChargeDBHelper = new CDRChargeDBHelper(super.getTransactionContext());
                cdrChargeDBHelper.insertCDRChargeDetails(cdrChargeData);
            }
        } catch (SQLException e) {
            throw e;
        }
        Logger.trace(MODULE, "cdrChargeData: " + cdrChargeData + "  inserted successfuly.");
    }

    /**
     * <p> This method will update customer balance with usage cost
     * @method updateCustomerBalance
     * @param CustomerServiceData
     *            , usageCost
     * @return
     * @author sheetalsoni
     * @throws SQLException
     * 
     */
    private double updateCustomerBalance(CustomerServiceData customerServiceData, double usageCost) throws SQLException {
        Logger.trace(MODULE, "Into updateCustomerBalance( " + customerServiceData + ", Usage Cost = " + usageCost + " )");
        double availBalance = 0;
        double balance = 0;

        try {
            CustomerBalanceDBHelper customerBalanceDBHelper = null;
            customerBalanceDBHelper = new CustomerBalanceDBHelper(super.getTransactionContext());

            if (customerServiceData != null) {
                balance = customerServiceData.getCustomerBalanceData().getBalance();
                availBalance = balance - usageCost;
                customerBalanceDBHelper.updateCustomerBalance(customerServiceData.getCustomerIdentifier(), availBalance);
            }
        } catch (SQLException e) {
            throw e;
        }
        Logger.trace(MODULE, "Balance of customer:  " + customerServiceData + " updated");
        return availBalance;
    }

    /**
     * <p> This method will Update Customer Total Usage
     * @method updateCustomerBalanceUsageBased
     * @param CustomerServiceData , uasgeCost
     * @return
     * @throws SQLException
     * */
    private void updateCustomerTotalUsage(CustomerServiceData customerServiceData, long usage) throws SQLException {
        Logger.trace(MODULE, "InTo updateCustomerTotalUsage( " + customerServiceData + ", Usage is = " + usage + " )");
        CustomerBalanceDBHelper customerBalanceDBHelper = null;
        double totalUsage = 0;
        double balance = 0;
        try {
            if (customerServiceData != null) {
                balance = customerServiceData.getTotalUsage();
                totalUsage = balance + usage;
                customerBalanceDBHelper = new CustomerBalanceDBHelper(super.getTransactionContext());
                customerBalanceDBHelper.updateTotalUsage(customerServiceData.getCustomerIdentifier(), totalUsage);
            }
        } catch (SQLException e) {
            throw e;
        }
        Logger.trace(MODULE, "Total usage of customer: " + customerServiceData + "  is updated.");
    }

    /**
     * <p> This method will do Rating basis on Package Type i.e Segmented, Individual, Stepwise etc.
     * @method doRating
     * @return IResponseObject
     * @param CustomerServiceData, CDRData and boolean value to perform DBOperation
     * @throws RatingBLException, SQLException
     */
    private void doRating(CustomerServiceData customerData, CDRData cdrData, boolean doDBOperation, IResponseObject response) throws RateNotFoundException, RatingBLException, SQLException {
        Logger.trace(MODULE, "Into doRating( " + customerData + ", " + cdrData + ", doDBOperation = " + doDBOperation + " )");

        try {

            CDRChargeData cdrChargeData = getCDRChargeData(customerData, cdrData);
            if (doDBOperation) {
                doDBOperations(customerData, cdrChargeData, response);
            }
            response.put(IBaseConstant.COST, String.valueOf(cdrData.getUsageCost()));
            response.setResponseCode(IBaseConstant.ACCOUNTING_SUCCESS);
            response.setResponseMessage("Customer " + customerData.getCustomerName() + " is successfully rated.");
        } catch (SQLException e) {
            throw e;
        }
        Logger.trace(MODULE, "OutOf doRating( " + customerData + ", " + cdrData + ", doDBOperation = " + doDBOperation + " ) = " + response);
    }

    /**
     * <p> This method will Return List of Rate Definition Information
     * @method getRateDefinitionData
     * @param CustomerServiceData, CDRData, CDRDataList,RateDefinitionData
     * 
     * */
    private CDRChargeData getCDRChargeData(CustomerServiceData customerData, CDRData cdrData) throws RateNotFoundException, SQLException, RatingBLException {

        Logger.trace(MODULE, "Into getCDRChargeData( " + customerData + ", " + cdrData + " )");
        RateDefinitionData rateDefinitionData = new RateDefinitionData();
        rateDefinitionData.setPackageId(customerData.getPackageId());
        rateDefinitionData.setServiceType(customerData.getServiceType());
        // Setting ANY as default value for all rate definition conditions
        rateDefinitionData.setPrefixGroup(IBaseConstant.ANY);
        rateDefinitionData.setCustomerSubType(IBaseConstant.ANY);
        rateDefinitionData.setEventType(IBaseConstant.ANY);
        rateDefinitionData.setVersion(IBaseConstant.ANY);

        if (customerData.getServiceType().equalsIgnoreCase(IBaseConstant.VOIP)) {
            PrefixGroupDefinitionData prefixData = customerData.getPackageDefinitionData().getPrefixGroupDefinitionData();
            if (prefixData != null) {
                rateDefinitionData.setPrefixGroup(prefixData.getPrefixGroupName());
            }
        }

        String packageType = customerData.getPackageDefinitionData().getPackageType();

        if (cdrData.getEventType() != null && cdrData.getEventType().equalsIgnoreCase(IBaseConstant.EVENT)) {
            rateDefinitionData.setEventType(IBaseConstant.EVENT);
            calculateRating(customerData, cdrData, rateDefinitionData);

        } else if (packageType.equalsIgnoreCase(IBaseConstant.FLAT_PACKAGE)) {
            // no need to set any parameters
            calculateRating(customerData, cdrData, rateDefinitionData);

        } else if (packageType.equalsIgnoreCase(IBaseConstant.SEGMENTED)) {
            rateDefinitionData.setCustomerSubType(customerData.getCustomerSubType());
            calculateRating(customerData, cdrData, rateDefinitionData);

        } else if (packageType.equalsIgnoreCase(IBaseConstant.INDIVIDUAL)) {
            if (customerData.getVersion() != null && customerData.getVersion().length() > 0) {
                rateDefinitionData.setVersion(customerData.getVersion());
            }
            calculateRating(customerData, cdrData, rateDefinitionData);

        } else if (packageType.equalsIgnoreCase(IBaseConstant.USAGE_BASED_PACKAGE)) {
            long totalUsage = 0;
            SlabDefinitionData slabData = customerData.getPackageDefinitionData().getSlabDefinitionData();

            if (slabData != null) {
            	
            	if ( customerData.getPackageDefinitionData().getUnitType().equalsIgnoreCase(IBaseConstant.MB)){
            		totalUsage =(long) customerData.getTotalUsage() / IBaseConstant.MB_CONST;
            	}
            	else if ( customerData.getPackageDefinitionData().getUnitType().equalsIgnoreCase(IBaseConstant.SEC)){
            		totalUsage = (long) customerData.getTotalUsage() / IBaseConstant.SECONDS_OF_MINUTE;
            	} 
            	
                if (slabData.getUnit1() > 0 && totalUsage <= slabData.getUnit1()) {
                    rateDefinitionData.setEventType(IBaseConstant.SLAB_1);
                } else if (slabData.getUnit2() > 0 && totalUsage <= slabData.getUnit2()) {
                    rateDefinitionData.setEventType(IBaseConstant.SLAB_2);
                } else if (slabData.getUnit3() > 0 && totalUsage <= slabData.getUnit3()) {
                    rateDefinitionData.setEventType(IBaseConstant.SLAB_3);
                }
            }
            calculateRating(customerData, cdrData, rateDefinitionData);

        } else if (packageType.equalsIgnoreCase(IBaseConstant.STEP_WISE_PACKAGE)) {
            SlabDefinitionData slabData = customerData.getPackageDefinitionData().getSlabDefinitionData();
            if (slabData != null) {

                // Break CDR InTo Slabs
                ArrayList<CDRData> cdrDataList = breakCDRIntoSegments(customerData, cdrData);
                // accumulate the cost 
                double usageCost = 0.0;
                Iterator<CDRData> cdrListIterator = cdrDataList.iterator();
                while (cdrListIterator.hasNext()) {
                    CDRData currentCDRData = cdrListIterator.next();
                    rateDefinitionData.setEventType(currentCDRData.getEventType());
                    calculateRating(customerData, currentCDRData, rateDefinitionData);
                    usageCost += currentCDRData.getUsageCost();
                }
                cdrData.setUsageCost(usageCost);   // set the accumulated cost as usage cost
            }
        }
        CDRChargeData cdrChargeData = setCDRChargeDetails(cdrData, customerData);
        Logger.trace(MODULE, "OutOf getRateDefinitionData( " + customerData + ", " + cdrData + ", " + rateDefinitionData);
        return cdrChargeData;
    }

    /**
     * <p> This method will calculate Rate from given parameters
     * @method calculateRating
     * @param customerServiceData, RequestParameters
     * @return
     * @throws SQLException 
     * @throws RateNotFoundException 
     * @throws Exception 
     * 
     * */
    private void calculateRating(CustomerServiceData customerServiceData, CDRData cdrData, RateDefinitionData rateDefinitionData) throws RateNotFoundException, SQLException {

        Logger.trace(MODULE, "Into calculateRating( " + customerServiceData + ", " + cdrData + ", " + rateDefinitionData + " )");

        long accountedTime = 0;
        long accountedVolume = 0;
        long totalDataTransfer = 0;

        double usageCost = 0.0;
        double rate = 0.0;

        RateDefinitionDBHelper rateDefinitionDBHelper = new RateDefinitionDBHelper(super.getTransactionContext());

        if (cdrData != null && customerServiceData != null && rateDefinitionData != null) {
            if (rateDefinitionData.getEventType().equalsIgnoreCase(IBaseConstant.EVENT)) {
                rate = rateDefinitionDBHelper.getRate(rateDefinitionData);

                cdrData.setUsageCost(rate);
            } else if (customerServiceData.getServiceType().equalsIgnoreCase(IBaseConstant.VOIP)) {
                accountedTime = calculateAccountedTime(cdrData.getSessionTime());
                rate = rateDefinitionDBHelper.getRate(rateDefinitionData);
                Logger.debug(MODULE, "Rate Value is : " + rate);

                usageCost = calculateVoIPUsageCost(accountedTime, rate, customerServiceData.getPackageDefinitionData().getMinCap(), customerServiceData.getPackageDefinitionData().getMaxCap());
                Logger.debug(MODULE, "Usage Cost Value is : " + usageCost);
                cdrData.setUsageCost(usageCost);
            } else { //DATA SERVICE
                totalDataTransfer = cdrData.getVolume();
                rate = rateDefinitionDBHelper.getRate(rateDefinitionData);
                if (rate == -1) {
                    throw new RateNotFoundException("Rate Not Found for Values : " + rateDefinitionData);
                }

                accountedVolume = calculateAccountedVolume(totalDataTransfer);
                Logger.debug(MODULE, "Accounted Volume is : " + accountedVolume);
                usageCost = calculateDataUsageCost(accountedVolume, rate, customerServiceData.getPackageDefinitionData().getMinCap(), customerServiceData.getPackageDefinitionData().getMaxCap());
                Logger.debug(MODULE, "Usage Cost is : " + usageCost);
                cdrData.setUsageCost(usageCost);
            }

        }

        Logger.trace(MODULE, "OutOf calculateRating( " + customerServiceData + ", " + cdrData + ", " + rateDefinitionData);

    }

    /**
     * 
     * @param customerData
     * @param cdrData
     * @return
     * @throws com.elitecore.classicrating.datamanager.rating.RateNotFoundException
     * @throws java.sql.SQLException
     */
    private ArrayList<CDRData> breakCDRIntoSegments(CustomerServiceData customerData, CDRData cdrData) throws RateNotFoundException, SQLException {

        Logger.trace(MODULE, "Breaking CDR Into Segments");
        Logger.debug(MODULE, "CDR Data : " + cdrData.toString());

        ArrayList<CDRData> cdrArrayList = new ArrayList<CDRData>();
        final String slabName[] = {IBaseConstant.SLAB_1, IBaseConstant.SLAB_2, IBaseConstant.SLAB_3, IBaseConstant.ANY};
        long splitUsageValues[] = {0, 0, 0, 0};
        long usageValue = 0;

        if (customerData.getServiceType() != null && customerData.getServiceType().equalsIgnoreCase(IBaseConstant.VOIP)) {
            usageValue = (long) cdrData.getSessionTime();  // Math.ceil((double)  / IBaseConstant.PULSE);
        } else if (customerData.getServiceType() != null && customerData.getServiceType().equalsIgnoreCase(IBaseConstant.DATA)) {
            usageValue = cdrData.getVolume();
            usageValue = (long) Math.ceil((double) usageValue / IBaseConstant.MB_CONST);
            Logger.debug(MODULE, "Rounded Usage data volume : " + usageValue);
        }
        SlabDefinitionData slabData = customerData.getPackageDefinitionData().getSlabDefinitionData();
        if (slabData != null) {

            long tempUsageValue = usageValue;
            if (slabData.getUnit3() > 0 && tempUsageValue > slabData.getUnit3()) {
                splitUsageValues[3] = tempUsageValue - slabData.getUnit3();
                tempUsageValue -= splitUsageValues[3];
            }
            if (slabData.getUnit2() > 0 && tempUsageValue > slabData.getUnit2()) {
                splitUsageValues[2] = tempUsageValue - slabData.getUnit2();
                tempUsageValue -= splitUsageValues[2];
            }
            if (slabData.getUnit1() > 0 && tempUsageValue > slabData.getUnit1()) {
                splitUsageValues[1] = tempUsageValue - slabData.getUnit1();
                tempUsageValue -= splitUsageValues[1];
            }
            splitUsageValues[0] = tempUsageValue;
            tempUsageValue -= splitUsageValues[0];

            for (int i = 0; i < splitUsageValues.length; i++) {
                if (splitUsageValues[i] <= 0) {
                    continue;
                }
                CDRData localCdrData = new CDRData();

                /* copy basic details of original cdr into new cdr */
                localCdrData.setCDRId(cdrData.getCDRId());
                localCdrData.setCustomerIdentifier(cdrData.getCustomerIdentifier());
                localCdrData.setCustomerName(cdrData.getCustomerName());
                localCdrData.setNASIPAddress(cdrData.getNASIPAddress());
                localCdrData.setServiceType(cdrData.getServiceType());
                localCdrData.setEventType(cdrData.getEventType());
                localCdrData.setCallStart(cdrData.getCallStart());
                localCdrData.setCallEnd(cdrData.getCallEnd());

                /*  Depending on service type copy the appropriate values */
                if (customerData.getServiceType().equalsIgnoreCase(IBaseConstant.VOIP)) {
                    /* copy call details  */
                    localCdrData.setSessionTime(splitUsageValues[i]);
                    localCdrData.setCalledStationId(cdrData.getCalledStationId());
                } else if (customerData.getServiceType().equalsIgnoreCase(IBaseConstant.DATA)) {
                    /*  copy data usage details */
                    localCdrData.setAcctInputOctets(cdrData.getAcctInputOctets());
                    localCdrData.setAcctOutputOctets(cdrData.getAcctOutputOctets());
                    localCdrData.setVolume(splitUsageValues[i] * IBaseConstant.MB_CONST);
                }
                /* set the slab name as event type */
                localCdrData.setEventType(slabName[i]);
                /*  add the cdrData to arraylist */
                cdrArrayList.add(localCdrData);
            }
        }
        Logger.debug(MODULE, "Breaked CDR List(s): " + cdrArrayList.toString());
        return cdrArrayList;
    }

    /**
     * <p> Calculates the Accounted Time based on Session-Time and Pulse.
     * 
     * @method calculateAccountedTime
     * @param long sessionTime
     * @return long accountedTime
     * @author sheetalsoni
     * @created on 10/5/2008
     */
    private long calculateAccountedTime(long sessionTime) {
        long accountedTime = 0;
        if (sessionTime % IBaseConstant.SECONDS_OF_MINUTE == 0) {
            sessionTime = sessionTime + 0;
        } else {
            accountedTime = IBaseConstant.SECONDS_OF_MINUTE - sessionTime % IBaseConstant.SECONDS_OF_MINUTE;
            sessionTime = sessionTime + accountedTime;
        }
        return sessionTime;
    }

    /**
     * calculaAccountedVolume
     *
     * @method calculateAccountedVolume
     * @param sessionTime
     * @return accountedVolume
     * @author sheetalsoni
     * @created on 10/5/2008
     */
    private long calculateAccountedVolume(long accountedVolume) {
        try {
            if (accountedVolume % IBaseConstant.MB_CONST == 0) {
                accountedVolume = accountedVolume + 0;
            } else {
                accountedVolume = accountedVolume + IBaseConstant.MB_CONST - (accountedVolume % IBaseConstant.MB_CONST);
            }
        } catch (Exception e) {
            Logger.trace(MODULE, e);
            Logger.error(MODULE, "Exception caught while calculating accounted Volume");
        }

        return accountedVolume;
    }

    /**
     * Returns Cost calculation for access service
     * 
     * @method calculateCostForData
     * @param long accounted_volume, double rate
     * @return totalCost
     * @author sheetalsoni
     * @created on 10/5/2008
     */
    private double calculateDataUsageCost(long accountedVolume, double rate, double minCap, double maxCap) {

        double usageCost = 0;

        try {
            usageCost = (accountedVolume / IBaseConstant.MB_CONST) * rate;
            if (usageCost < minCap) {
                usageCost = minCap;
            } else if (usageCost > maxCap) {
                usageCost = maxCap;
            }
        } catch (Exception e) {
            Logger.trace(MODULE, e);
            Logger.error(MODULE, "Exception caught while calculating Usage Cost for Data");
        }
        Logger.trace(MODULE, "OutOf calculateDataUsageCost( Accounted Volume = " + accountedVolume + ", " +
                "Rate = " + rate + ", " +
                "Min Cap = " + minCap + ", " +
                "Max Cap = " + maxCap + " ) = " + usageCost);
        return usageCost;
    }

    /**
     * costCalculation according to rate,pulse and accounted_Time would be
     * calculated. VOIP Service
     * @method calculateCost
     * @param accountedTime , double rate
     * @return double usageCost
     * @author sheetalsoni
     * @created on 10/5/2008
     */
    private double calculateVoIPUsageCost(long accountedTime, double rate, double minCap, double maxCap) {
        double usageCost = 0;

        try {
            usageCost = (accountedTime / IBaseConstant.SECONDS_OF_MINUTE) * rate;
            if (usageCost < minCap) {
                usageCost = minCap;
            } else if (usageCost > maxCap) {
                usageCost = maxCap;
            }
        } catch (Exception e) {
            Logger.trace(MODULE, e);
            Logger.error(MODULE, "Exception caught while Calculating Usage Cost for VIOP");
        }
        Logger.trace(MODULE, "OutOf calculateVoIPUsageCost( Accounted Volume = " + accountedTime + ", " +
                "Rate = " + rate + ", " + "Min Cap = " + minCap + ", " +
                "Max Cap = " + maxCap + " ) = " + usageCost);

        return usageCost;
    }
}
