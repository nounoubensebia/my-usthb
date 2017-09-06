package com.example.nouno.locateme.Data;

/**
 * Created by nouno on 06/09/2017.
 */

public class WebResponse {
    private boolean error;
    private String responseString;

    public WebResponse(boolean error, String responseString) {
        this.error = error;
        this.responseString = responseString;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }
}
