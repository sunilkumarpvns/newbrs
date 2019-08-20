/**
 *  Copyright (C) Elitecore Technologies LTD.
 *  @author raghug
 *  Created on: Nov 12, 2008
 */
package com.elitecore.classicrating.blmanager.base;

import java.sql.SQLException;

import com.elitecore.classicrating.base.IBaseConstant;
import com.elitecore.classicrating.base.IRatingAppContext;
import com.elitecore.classicrating.blmanager.customer.CustomerNotFoundException;
import com.elitecore.classicrating.blmanager.packages.PackageNotFoundException;
import com.elitecore.classicrating.commons.data.CDRData;
import com.elitecore.classicrating.datamanager.customer.CustomerBalanceDBHelper;
import com.elitecore.classicrating.datamanager.customer.CustomerBalanceData;
import com.elitecore.classicrating.datamanager.customer.CustomerServiceDBHelper;
import com.elitecore.classicrating.datamanager.customer.CustomerServiceData;
import com.elitecore.classicrating.datamanager.packages.PackageDefinitionDBHelper;
import com.elitecore.classicrating.datamanager.packages.PackageDefinitionData;
import com.elitecore.classicrating.datamanager.packages.SlabDefinitionDBHelper;
import com.elitecore.classicrating.datamanager.packages.SlabDefinitionData;
import com.elitecore.classicrating.datamanager.rating.PrefixGroupDefinitionDBHelper;
import com.elitecore.classicrating.datamanager.rating.PrefixGroupDefinitionData;
import com.elitecore.classicrating.datasource.ITransactionContext;

public class RatingBaseBLManager extends BaseBLManager {

    private static final String MODULE = "RatingBase BL Manager";

    public RatingBaseBLManager(IRatingAppContext ratingAppContext) {
        super(ratingAppContext);
    }

    public RatingBaseBLManager(IRatingAppContext ratingAppContext, ITransactionContext transactionContext) {
        super(ratingAppContext, transactionContext);
    }

    /**
     * <p>Performs the Guiding Process for Customer </p>
     * @param CDRData
     * @return CustomerServiceData
     * @throws RatingBLException*/
    protected CustomerServiceData doGuiding(CDRData cdrData) throws SQLException, CustomerNotFoundException, PackageNotFoundException {
        Logger.trace(MODULE, "Starting Guiding Process...");

        CustomerServiceData customerServiceData = null;
        try {
            customerServiceData = getCustomerServiceData(cdrData);
        } catch (SQLException e) {
            Logger.trace(MODULE, e);
            Logger.error(MODULE, "Exception caught while doing Guiding Process.");
            throw e;
        }
        Logger.trace(MODULE, "Completed Guiding Process");
        return customerServiceData;
    }

    /**
     * <p>Returns the Customer Service Data. </p>
     * @method getCustomerServiceData
     * @param customerIdentifier , serviceType
     * @return CustomerServiceData
     * @throws CustomerNotFoubdException, PackageNotFoundException, SQLException
     *
     */
    protected CustomerServiceData getCustomerServiceData(CDRData cdrData) throws CustomerNotFoundException, PackageNotFoundException, SQLException {

        Logger.trace(MODULE, "Into getCustomerServiceData( cdrData=" + cdrData.toString() + " )");

        CustomerServiceData customerData = null;
        CustomerBalanceData balanceData = null;

        CustomerServiceDBHelper customerHelper = new CustomerServiceDBHelper(super.getTransactionContext());
        CustomerBalanceDBHelper balanceHelper = new CustomerBalanceDBHelper(super.getTransactionContext());

        try {
            customerData = customerHelper.getCustomerByIdAndServiceType(cdrData.getCustomerIdentifier(), cdrData.getServiceType());

            if (customerData != null) {

                PackageDefinitionData packageData = null;

                PackageDefinitionDBHelper packageHelper = new PackageDefinitionDBHelper(super.getTransactionContext());
                packageData = packageHelper.getPackageByID(customerData.getPackageId());
                if (packageData != null) {
                    if (packageData.getPackageType().equalsIgnoreCase(IBaseConstant.USAGE_BASED_PACKAGE) ||
                            packageData.getPackageType().equalsIgnoreCase(IBaseConstant.STEP_WISE_PACKAGE)) {
                        SlabDefinitionData slabData = null;
                        SlabDefinitionDBHelper slabHelper = new SlabDefinitionDBHelper(super.getTransactionContext());
                        slabData = slabHelper.getSlabDefinitionForPackage(packageData.getPackageID());
                        if (slabData != null) {
                            packageData.setSlabDefinitionData(slabData);
                        }
                    }
                }
                if (customerData.getServiceType().equalsIgnoreCase(IBaseConstant.VOIP)) {
                    PrefixGroupDefinitionData prefixData = null;
                    PrefixGroupDefinitionDBHelper prefixDBHelper = new PrefixGroupDefinitionDBHelper(super.getTransactionContext());
                    prefixData = prefixDBHelper.getPrefixGroupDefinitionByNumber(cdrData.getCalledStationId());
                    if (prefixData != null) {
                        packageData.setPrefixGroupDefinitionData(prefixData);
                    }
                }
                customerData.setPackageDefinitionData(packageData);

                if (customerData.getCustomerType().equalsIgnoreCase(IBaseConstant.PREPAID)) {
                    balanceData = balanceHelper.getCustomerBalance(customerData.getCustomerIdentifier());
                    customerData.setCustomerBalanceData(balanceData);
                }
            } else {
                throw new CustomerNotFoundException("Customer not found for the Customer ID " + cdrData.getCustomerIdentifier());
            }
        } catch (CustomerNotFoundException e) {
            Logger.error(MODULE, "Exception caught while reading Customer Service Details.");
            throw e;
        } catch (SQLException e) {
            Logger.trace(MODULE, e);
            Logger.error(MODULE, "Exception caught while reading Customer Service Details.");
            throw e;
        } catch (Exception e) {
            Logger.trace(MODULE, e);
            Logger.error(MODULE, "Exception caught while reading Customer Service Details.");
        }

        Logger.trace(MODULE, "OutOf getCustomerServiceData( Customer Identifier = " + cdrData.getCustomerIdentifier() + " ) = ( " + customerData + " )");
        return customerData;

    }
}
