package com.sterlite.netvertex.nvsampler.cleanup.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import com.sterlite.netvertex.nvsampler.cleanup.Result;


import static com.sterlite.netvertex.nvsampler.cleanup.util.SamplerLogger.getLogger;

public class HTTPConnector {

	public Result connect(String url, String method) {

		Result result = new Result("HTTP Request: " + url);
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod(method);
			con.setRequestProperty ("Authorization", "Basic YWRtaW46YWRtaW4=");
			con.setRequestProperty("Content-Type", "application/json");
			con.getResponseCode();
			result.success();
		} catch (IOException e) {
			getLogger().error(e.getMessage(), e);
			result.notSuccess();
			result.setFailMessage(e.getMessage());
		}

		return result;
	}
}
