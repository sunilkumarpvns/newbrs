package com.elitecore.netvertex.core.alerts.conf;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringCustomStyle;
import com.elitecore.corenetvertex.util.ToStringable;
import com.elitecore.netvertex.core.alerts.Alerts;

import java.util.concurrent.atomic.AtomicInteger;

public class AlertsConfiguration implements ToStringable{

	private final Alerts alert;
	private final boolean floodControlEnabled;

	public AlertsConfiguration(Alerts alert, boolean floodControlEnabled) {
		this.alert = alert;
		this.floodControlEnabled = floodControlEnabled;
	}

	public Alerts getAlert(){
		return alert;
	}
	public boolean isFloodControlEnabled() {
		return floodControlEnabled;
	}

	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {

        ToStringCustomStyle noClassNameStyle = new ToStringCustomStyle();
        noClassNameStyle.setFieldSeparator(", ");
        builder.pushStyle(noClassNameStyle);
        AtomicInteger currentIndentation = builder.getCurrentIndentation();
        builder.append(Strings.repeat("\t", currentIndentation.get()) + "Name", alert);
        builder.append("Flood Control", isFloodControlEnabled() ? "ENABLED" : "DISABLED");
        builder.newline();

        builder.popStyle();
	}
}
