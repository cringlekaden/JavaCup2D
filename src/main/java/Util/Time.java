package Util;

public class Time {

    public static long startTime = System.nanoTime();

    public static float getTime() {
        return (float) ((System.nanoTime() - startTime) * 1E-9);
    }
}
