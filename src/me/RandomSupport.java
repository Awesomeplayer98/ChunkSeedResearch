package me;

//import com.google.common.primitives.Longs;

import java.util.concurrent.atomic.AtomicLong;

public final class RandomSupport {
    public static final long GOLDEN_RATIO_64 = -7046029254386353131L;
    public static final long SILVER_RATIO_64 = 7640891576956012809L;
    private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(8682522807148012L);

    public static long mixStafford13(long p_189330_) {
        p_189330_ = (p_189330_ ^ p_189330_ >>> 30) * -4658895280553007687L;
        p_189330_ = (p_189330_ ^ p_189330_ >>> 27) * -7723592293110705685L;
        return p_189330_ ^ p_189330_ >>> 31;
    }

    public static RandomSupport.Seed128bit upgradeSeedTo128bitUnmixed(long p_289660_) {
        long i = p_289660_ ^ 7640891576956012809L;
        long j = i + -7046029254386353131L;
        return new RandomSupport.Seed128bit(i, j);
    }

    public static RandomSupport.Seed128bit upgradeSeedTo128bit(long p_189332_) {
        RandomSupport.Seed128bit u = upgradeSeedTo128bitUnmixed(p_189332_).mixed();
        /*if (FindRuinedPortals.getInstance().xoros.isEmpty() && p_189332_ != 0L) {
            FindRuinedPortals.getInstance().xoros.add("lo: " + u.seedLo);
            FindRuinedPortals.getInstance().xoros.add("hi: " + u.seedHi);
        }*/
        return u;
    }

    public static long generateUniqueSeed() {
        return SEED_UNIQUIFIER.updateAndGet(p_224601_ -> p_224601_ * 1181783497276652981L) ^ System.nanoTime();
    }

    public static record Seed128bit(long seedLo, long seedHi) {
        public RandomSupport.Seed128bit xor(long p_288963_, long p_288992_) {
            return new RandomSupport.Seed128bit(this.seedLo ^ p_288963_, this.seedHi ^ p_288992_);
        }

        public RandomSupport.Seed128bit xor(RandomSupport.Seed128bit p_289009_) {
            return this.xor(p_289009_.seedLo, p_289009_.seedHi);
        }

        public RandomSupport.Seed128bit mixed() {
            RandomSupport.Seed128bit s = new RandomSupport.Seed128bit(RandomSupport.mixStafford13(this.seedLo), RandomSupport.mixStafford13(this.seedHi));
            return s;
        }
    }
}