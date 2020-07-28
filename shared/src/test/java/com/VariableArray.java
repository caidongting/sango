package com;

import org.junit.Test;

import java.util.Arrays;

public class VariableArray {

    @Test
    public void test() {
        sout(1, 2, 3, 4, 5);

        Object[] a = {1, 2, 3, 4, 5};
        sout(a);
        sout(-1, 0, a);
    }

    public void sout(Object... param) {
        System.out.println(Arrays.toString(param));
    }
}
