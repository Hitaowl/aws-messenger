package de.fhbrandenburg.ristpr.util;

/**
 * Created by Psycho on 25.01.2016.
 */
public class Loger {


    public static void LOG(String msg, Object... args)
    {
        System.out.println("LOG: " + String.format(msg, args));
    }

    public static void ERR(String msg, Object... args) { System.out.println("ERR: " + String.format(msg, args)); }
}
