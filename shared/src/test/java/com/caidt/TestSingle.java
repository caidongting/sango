package com.caidt;

import org.openjdk.jmh.annotations.*;

@BenchmarkMode(Mode.AverageTime)
public class TestSingle {

    @Benchmark
    @Warmup(iterations = 3)
    @Measurement(iterations = 5)
    public void check() {
        Thread[] threads = new Thread[1];
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
