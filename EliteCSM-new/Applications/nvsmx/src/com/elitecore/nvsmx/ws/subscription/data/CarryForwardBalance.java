package com.elitecore.nvsmx.ws.subscription.data;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"volume", "time"})
public class CarryForwardBalance {
    private Long volume;
    private Long time;

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
