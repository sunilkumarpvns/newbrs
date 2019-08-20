package com.elitecore.nvsmx.ws.subscription.data;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"actual", "remaining", "total", "remainingPulse"})
public class RncBalance {
    private Usage actual;
    private Usage remaining;
    private Usage total;
    private Long remainingPulse;


    public Usage getActual() {
        return actual;
    }

    public void setActual(Usage actual) {
        this.actual = actual;
    }

    public Usage getRemaining() {
        return remaining;
    }

    public void setRemaining(Usage remaining) {
        this.remaining = remaining;
    }

    public Usage getTotal() {
        return total;
    }

    public void setTotal(Usage total) {
        this.total = total;
    }

    public Long getRemainingPulse() {
        return remainingPulse;
    }

    public void setRemainingPulse(Long remainingPulse) {
        this.remainingPulse = remainingPulse;
    }
}
