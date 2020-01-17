package com.caidt;


import org.junit.Test;

public class TestSingle {

    @Test
    public void check() {
        Thread[] threads = new Thread[20];
        for (int i = 0; i < threads.length; i++) {
            Thread thread = new Thread(() -> System.out.println(Singleton.getInstance4().hashCode()));
            threads[i] = thread;
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
