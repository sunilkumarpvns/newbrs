package com.elitecore.ssp.web.bod.forms;

import com.elitecore.ssp.web.core.base.forms.BaseWebForm;

public class UnsubscribeBoDForm extends BaseWebForm{

	private static final long serialVersionUID = 1L;

	private Long bodId;

	public Long getBodId() {
		return bodId;
	}

	public void setBodId(Long bodId) {
		this.bodId = bodId;
	}
	
}
