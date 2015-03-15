package com.lanouette.app.client;

import java.util.Date;

import com.google.gwt.user.client.Cookies;

public class CookieData {
    private static final Long ONE_YEAR_COOKIE = 1000L * 60L * 60L * 24L * 365L;

    private static final CookieData instance = new CookieData();

    public static CookieData getInstance() {
        return instance;
    }

    public void setAudioMode(String value) {
        Cookies.setCookie("bffAudioMode", value,
                new Date(System.currentTimeMillis() + ONE_YEAR_COOKIE));
    }

    public String getAudioMode() {
        String audioEnabled = Cookies.getCookie("bffAudioMode");

        if(audioEnabled != null) {
            return audioEnabled;
        } else {
            return "disabled";
        }
    }
}
