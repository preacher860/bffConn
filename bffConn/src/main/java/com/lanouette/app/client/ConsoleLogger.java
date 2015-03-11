package com.lanouette.app.client;

/**
 * Created with IntelliJ IDEA.
 * User: mathieu
 * Date: 11/03/15
 * Time: 4:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConsoleLogger {
    private static final ConsoleLogger instance = new ConsoleLogger();

    public static ConsoleLogger getInstance() {
        return instance;
    }

    public void log(String message) {
        nativeLog(message);
    }

    native void nativeLog(String message) /*-{
        console.log(message);
    }-*/;
}
