package com.elitecore.corenetvertex.util.commons;

import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;

/**
 * Created by Ishani on 28/6/16.
 */
public class Spliters {

    public static final Splitter COMMA_SPLITER  = Strings.splitter(CommonConstants.COMMA).trimTokens();
}
