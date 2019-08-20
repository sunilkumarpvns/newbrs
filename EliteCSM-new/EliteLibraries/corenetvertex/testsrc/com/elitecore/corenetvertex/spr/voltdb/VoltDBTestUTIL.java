package com.elitecore.corenetvertex.spr.voltdb;

import org.apache.commons.lang3.RandomUtils;
import org.voltdb.client.ClientResponse;

public class VoltDBTestUTIL {

    public static byte generateResponseOtherThanSuccessForGetProfile() {
        int b = RandomUtils.nextInt(1, 128);
        b = b * -1;
        if (ClientResponse.SUCCESS == b || ClientResponse.CONNECTION_LOST == b ||
                ClientResponse.CONNECTION_TIMEOUT == b || ClientResponse.SERVER_UNAVAILABLE == b) {
            b = generateResponseOtherThanSuccess();

        }
        return (byte) b;
    }
    public static byte generateResponseOtherThanSuccess() {
        int b = RandomUtils.nextInt(1, 128);
        b = b * -1;
        if (ClientResponse.SUCCESS == b) {
            b = generateResponseOtherThanSuccess();
        }
        return (byte) b;
    }


}

