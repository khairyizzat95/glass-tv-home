package com.gathin.tvhome;

import android.util.Log;
import org.json.JSONObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

final class CustomIconStore {
    private static final String TAG = "TvHome.CustomIcons";
    private static final String FILE = "custom_icons.json";

    static Map<String, String> load(File dir) {
        Map<String, String> map = new HashMap<>();
        if (dir == null) return map;
        File f = new File(dir, FILE);
        if (!f.isFile()) return map;
        try (InputStream in = new FileInputStream(f)) {
            byte[] buf = new byte[(int) f.length()];
            int n = 0, r;
            while (n < buf.length && (r = in.read(buf, n, buf.length - n)) > 0) n += r;
            JSONObject json = new JSONObject(new String(buf, 0, n, StandardCharsets.UTF_8));
            for (Iterator<String> it = json.keys(); it.hasNext(); ) {
                String pkg = it.next();
                map.put(pkg, json.getString(pkg));
            }
        } catch (Exception e) {
            Log.w(TAG, "load failed: " + e.getMessage());
        }
        return map;
    }

    static void save(File dir, String packageName, String drawableName) {
        if (dir == null) return;
        Map<String, String> map = load(dir);
        if (drawableName == null) map.remove(packageName);
        else map.put(packageName, drawableName);
        write(dir, map);
    }

    private static void write(File dir, Map<String, String> map) {
        File f = new File(dir, FILE);
        File tmp = new File(dir, FILE + ".tmp");
        try {
            JSONObject json = new JSONObject();
            for (Map.Entry<String, String> e : map.entrySet()) json.put(e.getKey(), e.getValue());
            byte[] out = json.toString(2).getBytes(StandardCharsets.UTF_8);
            java.nio.file.Files.write(tmp.toPath(), out);
            try {
                java.nio.file.Files.move(tmp.toPath(), f.toPath(),
                        java.nio.file.StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException ignored) {
                java.nio.file.Files.move(tmp.toPath(), f.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            Log.w(TAG, "write failed: " + e.getMessage());
            tmp.delete();
        }
    }
}
