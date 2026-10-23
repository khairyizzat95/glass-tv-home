package com.gathin.tvhome;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Resolves app icons from an ADW/Nova-format icon pack installed on this device.
 * Resource names follow the convention: ic_<package_name_with_underscores>.
 * Results are cached per launcher session.
 */
final class IconPackLoader {
    private static final String TAG = "TvHome.IconPack";
    private final PackageManager pm;
    private final String packPkg;
    private Resources packRes;
    private final Map<String, Drawable> cache = new HashMap<>();

    IconPackLoader(PackageManager pm, String iconPackPkg) {
        this.pm = pm;
        this.packPkg = iconPackPkg;
        if (iconPackPkg != null && !iconPackPkg.isEmpty()) {
            try {
                packRes = pm.getResourcesForApplication(iconPackPkg);
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, "icon pack not installed: " + iconPackPkg);
            }
        }
    }

    /** Converts a package name to the ADW icon resource name, e.g. "com.netflix.ninja" → "ic_com_netflix_ninja". */
    static String iconResourceName(String packageName) {
        return "ic_" + packageName.replace('.', '_').replace('/', '_');
    }

    /**
     * Returns the icon from the pack for the given package name, or null if not found.
     * Falls back gracefully — never throws.
     */
    Drawable getIcon(String packageName) {
        if (packRes == null || packageName == null) return null;
        if (cache.containsKey(packageName)) return cache.get(packageName);
        try {
            String name = iconResourceName(packageName);
            int id = packRes.getIdentifier(name, "drawable", packPkg);
            if (id == 0) {
                cache.put(packageName, null);
                return null;
            }
            Drawable d = packRes.getDrawableForDensity(id, DisplayMetrics.DENSITY_XHIGH, null);
            cache.put(packageName, d);
            return d;
        } catch (Exception e) {
            Log.w(TAG, "icon load failed for " + packageName + ": " + e.getMessage());
            cache.put(packageName, null);
            return null;
        }
    }

    boolean isActive() {
        return packRes != null;
    }

    public static class IconEntry {
        public final String drawableName;
        IconEntry(String drawableName) { this.drawableName = drawableName; }
    }

    public List<IconEntry> getAllIcons() {
        List<IconEntry> result = new ArrayList<>();
        if (packRes == null) return result;
        Set<String> seen = new LinkedHashSet<>();
        for (String assetName : new String[]{"appfilter.xml", "drawable.xml"}) {
            try (InputStream is = packRes.getAssets().open(assetName)) {
                org.xmlpull.v1.XmlPullParser parser = android.util.Xml.newPullParser();
                parser.setInput(is, "UTF-8");
                int event;
                while ((event = parser.next()) != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
                    if (event == org.xmlpull.v1.XmlPullParser.START_TAG && "item".equals(parser.getName())) {
                        String d = parser.getAttributeValue(null, "drawable");
                        if (d != null && !d.isEmpty()) seen.add(d);
                    }
                }
            } catch (Exception ignored) {}
        }
        for (String name : seen) result.add(new IconEntry(name));
        return result;
    }

    public Drawable getIconByName(String drawableName) {
        if (packRes == null || drawableName == null) return null;
        try {
            int id = packRes.getIdentifier(drawableName, "drawable", packPkg);
            if (id == 0) return null;
            return packRes.getDrawableForDensity(id, DisplayMetrics.DENSITY_XHIGH, null);
        } catch (Exception e) {
            return null;
        }
    }
}
