package com.group4.lumos_api.sync.model;

import java.net.CookieManager;
import java.net.http.HttpClient;
import java.time.Duration;

/**
 * EDWARD SSO 로그인 후 유지되는 HTTP 세션. 자격증명은 포함하지 않는다.
 */
public class EdwardSession implements AutoCloseable {

    private final CookieManager cookieManager;
    private final HttpClient httpClient;
    private String wmonId;
    private String ibmId;

    public EdwardSession() {
        this.cookieManager = new CookieManager();
        this.httpClient = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    public HttpClient httpClient() {
        return httpClient;
    }

    public CookieManager cookieManager() {
        return cookieManager;
    }

    public String wmonId() {
        return wmonId;
    }

    public void setWmonId(String wmonId) {
        this.wmonId = wmonId;
    }

    public String ibmId() {
        return ibmId;
    }

    public void setIbmId(String ibmId) {
        this.ibmId = ibmId;
    }

    @Override
    public void close() {
        cookieManager.getCookieStore().removeAll();
        wmonId = null;
        ibmId = null;
    }
}
