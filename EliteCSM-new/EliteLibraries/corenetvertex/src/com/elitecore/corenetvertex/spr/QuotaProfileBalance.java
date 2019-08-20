package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.io.IndentingPrintWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuotaProfileBalance {

    private ArrayList<NonMonetoryBalance> hsqBalance;
    private ArrayList<NonMonetoryBalance> fupLevel1Balance;
    private ArrayList<NonMonetoryBalance> fupLevel2Balance;

    public void addBalance(NonMonetoryBalance nonMonetoryBalance) {
        switch(nonMonetoryBalance.getLevel()){

            case 0 :
                if(hsqBalance == null) {
                    hsqBalance = new ArrayList<NonMonetoryBalance>();
                }
                hsqBalance.add(nonMonetoryBalance);
                break;
            case 1 :
                if(fupLevel1Balance == null) {
                    fupLevel1Balance = new ArrayList<NonMonetoryBalance>();
                }
                fupLevel1Balance.add(nonMonetoryBalance);
                break;
            case 2 :
                if(fupLevel2Balance == null) {
                    fupLevel2Balance = new ArrayList<NonMonetoryBalance>();
                }
                fupLevel2Balance.add(nonMonetoryBalance);
                break;
            default:
                break;
        }
    }

    public void toString(IndentingPrintWriter stringWriter) {

        if(Collectionz.isNullOrEmpty(hsqBalance) == false) {
            stringWriter.incrementIndentation();
            stringWriter.println("HSQ Balance: ");
            hsqBalance.forEach(nonMonetoryBalance -> nonMonetoryBalance.toString(stringWriter));
            stringWriter.decrementIndentation();
        }

        if(Collectionz.isNullOrEmpty(fupLevel1Balance) == false) {
            stringWriter.incrementIndentation();
            stringWriter.println("FUP Level 1 Balance: ");
            fupLevel1Balance.forEach(nonMonetoryBalance -> nonMonetoryBalance.toString(stringWriter));
            stringWriter.decrementIndentation();
        }

        if(Collectionz.isNullOrEmpty(fupLevel2Balance) == false) {
            stringWriter.incrementIndentation();
            stringWriter.println("FUP Level 2 Balance: ");
            fupLevel2Balance.forEach(nonMonetoryBalance -> nonMonetoryBalance.toString(stringWriter));
            stringWriter.decrementIndentation();
        }
    }

    public NonMonetoryBalance getBalance(long serviceId, long ratingGroup, int level) {

        List<NonMonetoryBalance> balances;
        if(level == 0) {
            balances = hsqBalance;
        } else if(level == 1) {
            balances = fupLevel1Balance;
        } else {
            balances = fupLevel2Balance;
        }

        if(Objects.isNull(balances)) {
            return null;
        }

        for(int index = 0; index < balances.size(); index++) {
            NonMonetoryBalance nonMonetoryBalance = balances.get(index);

            if(nonMonetoryBalance.getRatingGroupId() != ratingGroup) {
                continue;
            }

            if (nonMonetoryBalance.getServiceId() != serviceId) {
                continue;
            }

            return nonMonetoryBalance;
        }

        return null;
    }

    public ArrayList<NonMonetoryBalance> getHsqBalance() {
        return hsqBalance;
    }

    public ArrayList<NonMonetoryBalance> getFupLevel1Balance() {
        return fupLevel1Balance;
    }

    public ArrayList<NonMonetoryBalance> getFupLevel2Balance() {
        return fupLevel2Balance;
    }

    public ArrayList<NonMonetoryBalance> getBalanceByLevel(int level){
        if(level == 0) {
            return hsqBalance;
        } else if(level == 1) {
            return fupLevel1Balance;
        } else {
            return fupLevel2Balance;
        }
    }
}
