package com.sterlite.pcc.serverstatus;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class Caller {

    private Logger logger = new Logger();
    private OkHttpClient client;


    public Caller(OkHttpClient client) {
        this.client = client;
    }

    public HttpResponse run(String url) throws IOException {

        logger.debug("Calling url:" + url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        try(Response response = client.newCall(request).execute()) {
            return  HttpResponse.create(response);
        }
    }

    public boolean callWithRetry(String url,  Predicate<HttpResponse> responseConsumer) 
    throws IOException, InterruptedException {

        if(call(url, responseConsumer)) {
            logger.debug("Response check pass");
            return true;
        } else {
            logger.debug("Response check fail in attempt: 1");
        }


        for (int attempt = 2; attempt <= 3;  attempt++) {

            try {
                logger.debug("wait for " + attempt + " minutes");
                Thread.sleep(TimeUnit.MINUTES.toMillis(attempt));
            } catch(InterruptedException ex) {
                logger.debug("Interruption Occurred while calling reset");
                return false;
            }

            if(call(url, responseConsumer)) {
                logger.debug("Response check pass");
                return true;
            } else {
                logger.debug("Response check fail in attempt: 1");
            }
        }

        return false;

    }

    private boolean call(String url, Predicate<HttpResponse> responseConsumer) {
        try {
            HttpResponse response = run(url);

            logger.debug(response);

            String responseMessage = response.responseBody();
            logger.debug("Response:" + responseMessage);

            return responseConsumer.test(response);

        } catch(Exception ex) {
            logger.trace(ex);
        }
        return false;
    }
}
