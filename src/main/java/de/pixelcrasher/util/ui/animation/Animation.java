package de.pixelcrasher.util.ui.animation;

import com.google.common.util.concurrent.AtomicDouble;

import java.util.ArrayList;
import java.util.List;

public class Animation {

    public final static AnimationPositionModifier LINEAR = (time) -> time;
    public final static AnimationPositionModifier ACCELERATOR = (time) -> Math.pow(time, 2);
    public final static AnimationPositionModifier DECELERATOR = (time) -> -1 * Math.pow(time - 1, 2) + 1;

    private final int distance;
    private final long durationIn;
    private final List<AnimationPositionModifier> modifiers;

    protected Animation(int distance, List<AnimationPositionModifier> modifiers) {
        this(distance, 0L, modifiers);
    }

    protected Animation(int distance, long durationIn, List<AnimationPositionModifier> modifiers) {
        this.distance = distance;
        this.modifiers = modifiers;
        this.durationIn = durationIn;
    }

    public int animate(final long time) {
        final AtomicDouble mod = new AtomicDouble(this.distance);
        this.modifiers.forEach(modifier -> mod.getAndUpdate((current) -> (current*modifier.apply(time))));
        return (int) mod.get();
    }

    public boolean done(final long time) {
        return time > durationIn;
    }

    public int getDistance() {
        return this.distance;
    }
    public int getFinalDistance() {
        return this.animate(this.durationIn);
    }

    public long getDuration() { return this.durationIn; }

    public static Animation.Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private long durationIn;

        private int distance;

        private final List<AnimationPositionModifier> modifiers = new ArrayList<>();

        public Builder accelerate() {
            return this.scale(time -> ACCELERATOR.apply(((double) 1/this.durationIn)*time));
        }

        public Builder decelerate() {
            return this.scale(time -> DECELERATOR.apply(((double) 1/this.durationIn)*time));
        }

        public Builder linear() {
            return this.scale(time -> LINEAR.apply(((double) 1/this.durationIn)*time));
        }

        public Builder invert() {
            List<AnimationPositionModifier> modifiers = new ArrayList<>(this.modifiers);
            this.modifiers.clear();
            modifiers.forEach(modifier -> this.modifiers.add((time -> 1 - modifier.apply(time))));
            return this;
        }

        public Builder stay() {
            return this.distance(0);
        }

        public Builder stay(long durationIn) {
            return this
                    .stay()
                    .durationIn(durationIn);
        }

        public Builder scale(AnimationPositionModifier modifier) {
            this.modifiers.add(modifier);
            return this;
        }

        public Builder durationIn(long durationIn) {
            if(this.durationIn != 0) return this;
            this.durationIn = durationIn;
            return this;
        }

        public Builder distance(int distance) {
            this.distance = distance;
            return this;
        }

        public Animation build() {
            return new Animation(this.distance, this.durationIn, this.modifiers);
        }

    }

}
