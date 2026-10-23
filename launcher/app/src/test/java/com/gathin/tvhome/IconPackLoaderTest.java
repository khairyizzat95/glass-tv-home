package com.gathin.tvhome;

import org.junit.Test;

import static org.junit.Assert.*;

public class IconPackLoaderTest {

    @Test
    public void iconResourceNameForSimplePackage() {
        assertEquals("ic_com_netflix_ninja", IconPackLoader.iconResourceName("com.netflix.ninja"));
    }

    @Test
    public void iconResourceNameReplacesDotsWithUnderscores() {
        assertEquals("ic_com_example_app", IconPackLoader.iconResourceName("com.example.app"));
    }

    @Test
    public void iconResourceNameEmptyInput() {
        assertEquals("ic_", IconPackLoader.iconResourceName(""));
    }

    @Test
    public void iconResourceNameSingleSegment() {
        assertEquals("ic_myapp", IconPackLoader.iconResourceName("myapp"));
    }
}
