package com.elitecore.nvsmx.ws.subscription.data;

import java.util.List;

public class BoDQoSMultiplierData {
    private Integer fupLevel;
    private Double multiplier = 1.00;
    private List<BoDServiceMultiplierData> bodServiceMultiplierDatas;

    public BoDQoSMultiplierData(Integer fupLevel, Double multiplier, List<BoDServiceMultiplierData> bodServiceMultiplierDatas) {
        this.fupLevel = fupLevel;
        this.multiplier = multiplier;
        this.bodServiceMultiplierDatas = bodServiceMultiplierDatas;
    }

    public Integer getFupLevel() {
        return fupLevel;
    }

    public Double getMultiplier() {
        return multiplier;
    }

    public List<BoDServiceMultiplierData> getBodServiceMultiplierDatas() {
        return bodServiceMultiplierDatas;
    }
}
