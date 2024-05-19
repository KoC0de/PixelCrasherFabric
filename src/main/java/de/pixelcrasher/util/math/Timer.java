package de.pixelcrasher.util.math;

public class Timer {

    private long time;

    public void reset() {
        this.time = System.currentTimeMillis();
    }

    public long passed() {
        return System.currentTimeMillis() - this.time;
    }

    public boolean hasPassed(long time) {
        return this.hasPassed(time, false);
    }

    public boolean hasPassed(long time, boolean reset) {
        boolean passed = time - this.passed() <= 0L;
        if (passed && reset) this.reset();
        return passed;
    }
}
