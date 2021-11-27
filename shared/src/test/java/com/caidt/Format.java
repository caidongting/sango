package com.caidt;

import java.util.Arrays;
import java.util.List;

public class Format {

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        String s = String.format("%s", list);
        System.out.println(s);
    }

}
