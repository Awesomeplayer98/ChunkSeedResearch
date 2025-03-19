package me;

import java.util.concurrent.atomic.AtomicLong;
import me.RandomSource;

public class LegacyRandomSource implements BitRandomSource {
    private static final int MODULUS_BITS = 48;
    private static final long MODULUS_MASK = 281474976710655L;
    private static final long MULTIPLIER = 25214903917L;
    private static final long INCREMENT = 11L;
    private final AtomicLong seed = new AtomicLong();

    public LegacyRandomSource(long p_188578_) {
        this.setSeed(p_188578_);
    }

    @Override
    public RandomSource fork() {
        return new LegacyRandomSource(this.nextLong());
    }

    @Override
    public void setSeed(long p_188585_) {
        this.seed.compareAndSet(this.seed.get(), (p_188585_ ^ 25214903917L) & 281474976710655L);
    }

    @Override
    public double nextGaussian() {
        return 0;
    }

    @Override
    public int next(int p_188581_) {
        long i = this.seed.get();
        long j = i * 25214903917L + 11L & 281474976710655L;
        this.seed.compareAndSet(i, j);
        return (int)(j >> 48 - p_188581_);
    }
}