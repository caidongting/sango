package com.caidt;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3)
@Measurement(iterations = 10)
// @Threads(5)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class LoopTest {

    @Param(value = {"100", "1000", "10000", "100000", "1000000"})
    private int num;

    @Benchmark
    public void inside(Blackhole blackhole) {
        for (int i = 0; i < num; i++) {
            String s = String.valueOf(i);
            blackhole.consume(s);
        }
    }

    @Benchmark
    public void outside(Blackhole blackhole) {
        String s;
        for (int i = 0; i < num; i++) {
            s = String.valueOf(i);
            blackhole.consume(s);
        }
    }

}
