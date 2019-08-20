package com.elitecore.nvsmx.ws.subscription.data;

import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;

@XmlType(propOrder={"rateType", "slab1","rate1","pulse1","slab2","rate2","pulse2","slab3","rate3","pulse3"})
public class RateCardVersionDetailInfo {

    private String rateType;
    private Long slab1;
    private Long slab2;
    private Long slab3;
    private Long pulse1;
    private Long pulse2;
    private Long pulse3;
    private BigDecimal rate1;
    private BigDecimal rate2;
    private BigDecimal rate3;

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public Long getSlab1() {
        return slab1;
    }

    public void setSlab1(Long slab1) {
        this.slab1 = slab1;
    }

    public Long getSlab2() {
        return slab2;
    }

    public void setSlab2(Long slab2) {
        this.slab2 = slab2;
    }

    public Long getSlab3() {
        return slab3;
    }

    public void setSlab3(Long slab3) {
        this.slab3 = slab3;
    }

    public Long getPulse1() {
        return pulse1;
    }

    public void setPulse1(Long pulse1) {
        this.pulse1 = pulse1;
    }

    public Long getPulse2() {
        return pulse2;
    }

    public void setPulse2(Long pulse2) {
        this.pulse2 = pulse2;
    }

    public Long getPulse3() {
        return pulse3;
    }

    public void setPulse3(Long pulse3) {
        this.pulse3 = pulse3;
    }

    public BigDecimal getRate1() {
        return rate1;
    }

    public void setRate1(BigDecimal rate1) {
        this.rate1 = rate1;
    }

    public BigDecimal getRate2() {
        return rate2;
    }

    public void setRate2(BigDecimal rate2) {
        this.rate2 = rate2;
    }

    public BigDecimal getRate3() {
        return rate3;
    }

    public void setRate3(BigDecimal rate3) {
        this.rate3 = rate3;
    }
}
