package com.gathin.tvhome;

import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class ConfigSaveTest {
    @Rule public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void saveCreatesFileWhenAbsent() throws Exception {
        File dir = tmp.newFolder();
        Config.save(dir, "columns", 6);
        File f = new File(dir, "config.json");
        assertTrue(f.exists());
        JSONObject j = new JSONObject(new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8));
        assertEquals(6, j.getInt("columns"));
    }

    @Test
    public void saveMergesExistingKeys() throws Exception {
        File dir = tmp.newFolder();
        File f = new File(dir, "config.json");
        Files.write(f.toPath(), "{\"columns\":5}".getBytes(StandardCharsets.UTF_8));
        Config.save(dir, "icon_pack", "tv.corebuilds.iconpack");
        JSONObject j = new JSONObject(new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8));
        assertEquals(5, j.getInt("columns"));
        assertEquals("tv.corebuilds.iconpack", j.getString("icon_pack"));
    }

    @Test
    public void saveIsAtomic() throws Exception {
        File dir = tmp.newFolder();
        Config.save(dir, "columns", 3);
        File tmp_file = new File(dir, "config.json.tmp");
        assertFalse("tmp file must not remain after save", tmp_file.exists());
    }

    @Test
    public void saveOverwritesKey() throws Exception {
        File dir = tmp.newFolder();
        Config.save(dir, "columns", 5);
        Config.save(dir, "columns", 7);
        File f = new File(dir, "config.json");
        JSONObject j = new JSONObject(new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8));
        assertEquals(7, j.getInt("columns"));
    }
}
