package com.ex.serialport.adapter;


public class FloatUtils {
    public static float floatFromBytes(byte[] bytes) {
        // convert bytes to int
        int intValue = 0;
        for (byte b : bytes) {
            intValue = (intValue << 8) + (b & 0xFF);
        }

        // convert int to float
        return Float.intBitsToFloat(intValue);
    }

    public static long gramsFromBytes(byte[] bytes) {
        if (bytes == null || bytes.length != 4) {
            return -1;
        }
        float tenGrams = floatFromBytes(bytes);
//        double grams = tenGrams * 100;
        double grams = tenGrams;
        return Math.round(grams);
    }



    public static void main(String[] strings) {
        int intBits = Float.floatToIntBits(5000);
        System.out.println(intBits);
        System.out.println(Integer.toHexString(intBits));


        float floatValue = Float.intBitsToFloat(intBits);
        System.out.println(floatValue);

        byte[] testBytes = new byte[] {0x45, (byte)0x9c, 0x40, 0x00};
        float result = floatFromBytes(testBytes);
        System.out.println(result);
    }
}
