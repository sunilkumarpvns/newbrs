package com.elitecore.nvsmx.sm.model.alert;


import com.elitecore.corenetvertex.core.alerts.Alerts;

public class AlertDisplayData {

    private Alerts alert;
    private boolean alertChecked;
    private boolean floodControl;

    public AlertDisplayData(Alerts alert, boolean alertChecked, boolean floodControl) {
        this.alert = alert;
        this.alertChecked = alertChecked;
        this.floodControl = floodControl;
    }

    public AlertDisplayData() {

    }

    public Alerts getAlert() {
        return alert;
    }

    public void setAlert(Alerts alert) {
        this.alert = alert;
    }

    public boolean isAlertChecked() {
        return alertChecked;
    }

    public void setAlertChecked(boolean alertChecked) {
        this.alertChecked = alertChecked;
    }

    public boolean isFloodControl() {
        return floodControl;
    }

    public void setFloodControl(boolean floodControl) {
        this.floodControl = floodControl;
    }
}
