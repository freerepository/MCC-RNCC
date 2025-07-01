package com.sedulous.mccrnrccnagar.utilities;

public class Test {
    public static void main(String[] args){

      toBinary(2.3);
    }
    public static void toBinary(int int1){
        System.out.println(int1 + " in binary is");
        System.out.println(Integer.toBinaryString(int1));
    }
    public static void toBinary(Float float1){
        System.out.println(float1 + " in binary is");
        System.out.println(Integer.toBinaryString(Float.floatToIntBits(float1)));
    }
    public static void toBinary(Double d){
        System.out.println(d + " in binary is");
        //System.out.println((byte)d);
    }
}