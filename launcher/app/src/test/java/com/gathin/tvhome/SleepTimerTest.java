package com.gathin.tvhome;

import org.junit.Test;
import static org.junit.Assert.*;

public class SleepTimerTest {

    @Test
    public void formatCountdownBelowOneHour() {
        assertEquals("29:59", SleepTimer.formatMs(29 * 60_000L + 59_000L));
    }

    @Test
    public void formatCountdownAtOneHour() {
        assertEquals("1:00:00", SleepTimer.formatMs(60 * 60_000L));
    }

    @Test
    public void formatCountdownAboveOneHour() {
        assertEquals("1:05:03", SleepTimer.formatMs(65 * 60_000L + 3_000L));
    }

    @Test
    public void formatCountdownAtZero() {
        assertEquals("0:00", SleepTimer.formatMs(0));
    }

    @Test
    public void nextPresetCyclesFrom0() {
        assertEquals(15, SleepTimer.nextPresetMinutes(0));
    }

    @Test
    public void nextPresetCyclesFrom15() {
        assertEquals(30, SleepTimer.nextPresetMinutes(15));
    }

    @Test
    public void nextPresetCyclesFrom30() {
        assertEquals(60, SleepTimer.nextPresetMinutes(30));
    }

    @Test
    public void nextPresetCyclesFrom60() {
        assertEquals(120, SleepTimer.nextPresetMinutes(60));
    }

    @Test
    public void nextPresetWrapsFrom120() {
        assertEquals(0, SleepTimer.nextPresetMinutes(120));
    }

    @Test
    public void nextPresetUnknownValueWrapsToFirst() {
        assertEquals(15, SleepTimer.nextPresetMinutes(99));
    }
}
