package com.lanouette.app.client;

import com.google.gwt.media.client.Audio;

public class Alerts {

    private static final Alerts instance = new Alerts();
    private final Audio audio;
    public static Alerts getInstance() {
        return instance;
    }

    public Alerts() {
        audio = Audio.createIfSupported();

    }

    public void newMessageAlert() {
        if(audio != null) {
            audio.setSrc("audio/served.mp3");
            audio.play();
        }
    }
}
