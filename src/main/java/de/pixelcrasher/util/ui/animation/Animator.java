package de.pixelcrasher.util.ui.animation;

public class Animator {

    private final Animation[] animations;

    private boolean repeating = false;

    private int index = 0;
    private int lastPosition = 0;
    private long lastDuration = 0;
    private boolean casting = false;
    private boolean relative = false;
    private long time = -1;
    private long relativeTime = -1;

    public Animator(Animation... animation) {
        this.animations = animation;
    }

    public void cast() {
        if(this.casting || this.relative) return;
        this.casting = true;
        this.time = System.currentTimeMillis();
    }

    public void cast(float deltaTime) {
        if(this.relative) {
            if(this.casting) this.relativeTime += (long) (20 * deltaTime);
            return;
        }
        this.casting = true;
        this.relative = true;
        this.relativeTime = 0;
    }

    public void pause() {
        if(this.relative) this.casting = false;
    }

    public void resume() {
        if(this.relative) this.casting = true;
    }

    public void reset() {
        this.casting = false;
        this.relative = false;
        this.index = 0;
        this.time = -1;
        this.relativeTime = -1;
        this.lastPosition = 0;
        this.lastDuration = 0;
    }

    public boolean isCasting() {
        return casting;
    }

    public boolean done() {
        if(!casting || this.animations.length - 1 > this.index) return false;
        return this.animations[this.index].done(passed());
    }

    public int animate(int pos) {
        Animation current = this.animations[this.index];
        long passed = passed();
        if(current.done(passed)) {
            if(this.index != this.animations.length - 1) {
                this.index++;
                this.lastPosition += current.getFinalDistance();
                this.lastDuration += current.getDuration();
                passed = passed();
                current = this.animations[this.index];
            } else if(!this.repeating) return this.lastPosition + pos + current.getFinalDistance();
            else {
                this.reset();
                this.cast();
            }

        }

        return this.lastPosition + pos + current.animate(passed);
    }

    public long passed() {
        if(this.relative) return this.relativeTime - this.lastDuration;
        return (System.currentTimeMillis() - this.time) - this.lastDuration;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }
}
