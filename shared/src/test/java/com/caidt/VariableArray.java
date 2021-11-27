package com.caidt;

import org.junit.Test;

import java.util.Arrays;

public class VariableArray {

    @Test
    public void test() {
        sout(1, 2, 3, 4, 5);

        Object[] a = {1, 2, 3, 4, 5};
        sout(a);
        Object[] objects = Arrays.copyOf(a, a.length + 2);
        sout(-1, 0, a);
    }

    public void sout(Object... param) {
//        for (Object o : param) {
//            System.out.println(o);
//        }
        System.out.println(Arrays.toString(param));
    }
}
