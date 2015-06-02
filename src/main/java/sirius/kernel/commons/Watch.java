/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.kernel.commons;

import sirius.kernel.health.Microtiming;
import sirius.kernel.nls.NLS;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

/**
 * Provides a mechanism to measure the duration.
 * <p>
 * Measures the duration between <tt>start</tt> and <tt>elapsed</tt> or <tt>duration</tt> in
 * nanosecond resolution. The returned value is however not necessarily that exact: {@link System#nanoTime()}.
 * <p>
 * This is intended to replace code like:
 * <pre>
 * {@code
 *             long start = System.currentTimeMillis();
 *             //do something
 *             System.out.println("Duration: "+(System.currentTimeMillis() - start));
 * }
 * </pre>
 *
 * @see System#nanoTime()
 */
public class Watch {

    private long startTime = 0L;
    private long lastMicroTime = 0L;

    /**
     * Creates and starts a new watch.
     *
     * @return a new watch, started with the invocation of this method.
     */
    public static Watch start() {
        return new Watch();
    }

    /**
     * Use <tt>start</tt> to create a new watch.
     */
    private Watch() {
        super();
        reset();
    }

    /**
     * Resets the watch, so that every duration returned by this instance will be measured from this call on.
     */
    public void reset() {
        startTime = System.nanoTime();
        lastMicroTime = startTime;
    }

    /**
     * Returns the number of millis since the last call to <tt>reset</tt> or <tt>start</tt>.
     *
     * @return returns the number of milliseconds since the start of this watch.
     */
    public long elapsedMillis() {
        return elapsed(TimeUnit.MILLISECONDS, false);
    }

    /**
     * Returns the number of units since the last call to <tt>reset</tt> or <tt>start</tt>.
     *
     * @param unit  determines the desired output unit
     * @param reset determines if the watch should be reset, after the duration has be computed.
     * @return returns the duration since the start of this watch in the given <tt>TimeUnit</tt>
     */
    public long elapsed(TimeUnit unit, boolean reset) {
        long result = unit.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        if (reset) {
            reset();
        }

        return result;
    }

    /**
     * Returns the formatted duration.
     * <p>
     * This method tries to generate a string which is as short as possible. Therefore, if the duration is
     * less than one microsecond, the number of nanoseconds is returned. If the duration is less than one millisecond,
     * the number of microseconds (rounded) is returned. Otherwise, the value is rounded to milliseconds and returned
     * as
     * formatted by {@link NLS#convertDuration(long)}.
     *
     * @param reset determines if the watch should be reset, after the duration has be computed.
     * @return a string representing the duration since the last call to <tt>reset</tt> or <tt>start</tt>.
     */
    public String duration(boolean reset) {
        long elapsed = elapsed(TimeUnit.NANOSECONDS, reset);
        if (elapsed <= 1000) {
            return elapsed + " ns";
        }
        elapsed = elapsed / 1000;
        if (elapsed <= 1000) {
            return elapsed + " us";
        }
        return NLS.convertDuration(elapsed / 1000);
    }

    /**
     * Boilerplate method for calling <tt>duration(false)</tt>
     *
     * @return a string representation as generated by {@link #duration(boolean)}
     */
    public String duration() {
        return duration(false);
    }

    /**
     * Submits the value for this watch to the {@link Microtiming} framework using the given key
     *
     * @param category selects the category for the given key
     * @param key      the key used to store the elapsed time for
     */
    public void submitMicroTiming(@Nonnull String category, @Nonnull String key) {
        long newTime = System.nanoTime();
        Microtiming.submit(category, key, newTime - lastMicroTime);
        lastMicroTime = newTime;
    }

    @Override
    public String toString() {
        return duration();
    }
}
