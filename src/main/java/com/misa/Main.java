package com.misa;

import com.misa.exceptions.GlobalExceptionHandler;

public class Main {

    public static void main(String[] args) {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);

        Thread t = new Thread(new ApplicationMain(), "Main application thread");
        t.start();
    }
}