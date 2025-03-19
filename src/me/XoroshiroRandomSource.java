package me;

import me.RandomSource;

public class XoroshiroRandomSource implements RandomSource {
    private static final float FLOAT_UNIT = 5.9604645E-8F;
    private static final double DOUBLE_UNIT = 1.110223E-16F;
    private Xoroshiro128PlusPlus randomNumberGenerator;

    public XoroshiroRandomSource(long p_190102_) {
        this.randomNumberGenerator = new Xoroshiro128PlusPlus(RandomSupport.upgradeSeedTo128bit(p_190102_));
    }

    public XoroshiroRandomSource(RandomSupport.Seed128bit p_289014_) {
        this.randomNumberGenerator = new Xoroshiro128PlusPlus(p_289014_);
    }

    public XoroshiroRandomSource(long p_190104_, long p_190105_) {
        this.randomNumberGenerator = new Xoroshiro128PlusPlus(p_190104_, p_190105_);
    }

    private XoroshiroRandomSource(Xoroshiro128PlusPlus p_287656_) {
        this.randomNumberGenerator = p_287656_;
    }

    @Override
    public RandomSource fork() {
        return new XoroshiroRandomSource(this.randomNumberGenerator.nextLong(), this.randomNumberGenerator.nextLong());
    }

    @Override
    public void setSeed(long p_190121_) {
        this.randomNumberGenerator = new Xoroshiro128PlusPlus(RandomSupport.upgradeSeedTo128bit(p_190121_));
    }

    @Override
    public int nextInt() {
        return (int)this.randomNumberGenerator.nextLong();
    }

    @Override
    public int nextInt(int p_190118_) {
        if (p_190118_ <= 0) {
            throw new IllegalArgumentException("Bound must be positive");
        } else {
            long i = Integer.toUnsignedLong(this.nextInt());
            long j = i * (long)p_190118_;
            long k = j & 4294967295L;
            if (k < (long)p_190118_) {
                for (int l = Integer.remainderUnsigned(~p_190118_ + 1, p_190118_); k < (long)l; k = j & 4294967295L) {
                    i = Integer.toUnsignedLong(this.nextInt());
                    j = i * (long)p_190118_;
                }
            }

            long i1 = j >> 32;
            // FindRuinedPortals.getInstance().xoros.add("nextInt(" + p_190118_ + "): " + (int)i1);
            return (int)i1;
        }
    }

    @Override
    public long nextLong() {
        return this.randomNumberGenerator.nextLong();
    }

    @Override
    public boolean nextBoolean() {
        boolean e = (this.randomNumberGenerator.nextLong() & 1L) != 0L;
        //FindRuinedPortals.getInstance().xoros.add("nextBoolean(): " + e);
        return e;
    }

    @Override
    public float nextFloat() {
        float f = (float)this.nextBits(24) * 5.9604645E-8F;
        //FindRuinedPortals.getInstance().xoros.add("nextFloat(): " + f);
        return f;
    }

    @Override
    public double nextDouble() {
        double d = (double)this.nextBits(53) * 1.110223E-16F;
        //FindRuinedPortals.getInstance().xoros.add("nextDouble(): " + d);
        return d;
    }

    @Override
    public double nextGaussian() {
        return 0;
    }

    @Override
    public void consumeCount(int p_190111_) {
        //FindRuinedPortals.getInstance().xoros.add("consumeCount(" + p_190111_ + ")");
        for (int i = 0; i < p_190111_; i++) {
            this.randomNumberGenerator.nextLong();
        }
    }

    private long nextBits(int p_190108_) {
        return this.randomNumberGenerator.nextLong() >>> 64 - p_190108_;
    }
}