package com.gathin.tvhome;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IconPickerActivity extends Activity {
    static final String EXTRA_PACKAGE = "pkg";
    static final String EXTRA_DRAWABLE = "drawable";
    private static final int COLS = 6;

    private IconPackLoader loader;
    private List<IconPackLoader.IconEntry> allIcons = new ArrayList<>();
    private IconGridAdapter gridAdapter;
    private final ExecutorService pool = Executors.newFixedThreadPool(4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String pkg = getIntent().getStringExtra(EXTRA_PACKAGE);
        Config cfg = Config.load(this);
        loader = new IconPackLoader(getPackageManager(), cfg.iconPack);

        float dp = getResources().getDisplayMetrics().density;

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF0F0F1A);
        root.setPadding((int)(24*dp), (int)(16*dp), (int)(24*dp), 0);

        TextView title = new TextView(this);
        title.setText("Change Icon — " + appLabel(pkg));
        title.setTextColor(0xFFEEEEF5);
        title.setTextSize(18f);
        title.setPadding(0, 0, 0, (int)(12*dp));
        root.addView(title);

        EditText search = new EditText(this);
        search.setHint("Search icons…");
        search.setHintTextColor(0xFF606070);
        search.setTextColor(0xFFEEEEF5);
        search.setBackgroundColor(0xFF1E1E30);
        search.setPadding((int)(16*dp), (int)(10*dp), (int)(16*dp), (int)(10*dp));
        root.addView(search, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        View spacer = new View(this);
        root.addView(spacer, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (int)(8*dp)));

        RecyclerView grid = new RecyclerView(this);
        grid.setLayoutManager(new GridLayoutManager(this, COLS));
        gridAdapter = new IconGridAdapter(pkg);
        grid.setAdapter(gridAdapter);
        root.addView(grid, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        setContentView(root);

        pool.execute(() -> {
            List<IconPackLoader.IconEntry> icons = loader.getAllIcons();
            String defaultFilter = defaultFilter(pkg);
            List<IconPackLoader.IconEntry> filtered = filter(icons, defaultFilter);
            runOnUiThread(() -> {
                allIcons = icons;
                search.setText(defaultFilter);
                search.setSelection(search.getText().length());
                gridAdapter.setItems(filtered);
            });
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int b, int c) {
                String q = s.toString().trim().toLowerCase(Locale.ROOT);
                gridAdapter.setItems(q.isEmpty() ? allIcons : filter(allIcons, q));
            }
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onDestroy() {
        pool.shutdownNow();
        super.onDestroy();
    }

    private String appLabel(String pkg) {
        if (pkg == null) return "";
        try {
            return getPackageManager()
                    .getApplicationLabel(getPackageManager().getApplicationInfo(pkg, 0))
                    .toString();
        } catch (Exception e) { return pkg; }
    }

    private static String defaultFilter(String pkg) {
        if (pkg == null) return "";
        String[] parts = pkg.split("\\.");
        for (int i = parts.length - 1; i >= 0; i--) {
            String p = parts[i].toLowerCase(Locale.ROOT);
            if (p.length() > 2 && !p.equals("android") && !p.equals("google")
                    && !p.equals("app") && !p.equals("tv") && !p.equals("mobile")) {
                return p;
            }
        }
        return parts[parts.length - 1];
    }

    private static List<IconPackLoader.IconEntry> filter(List<IconPackLoader.IconEntry> all, String q) {
        List<IconPackLoader.IconEntry> out = new ArrayList<>();
        for (IconPackLoader.IconEntry e : all) {
            if (e.drawableName.toLowerCase(Locale.ROOT).contains(q)) out.add(e);
        }
        return out;
    }

    class IconGridAdapter extends RecyclerView.Adapter<IconGridAdapter.VH> {
        private List<IconPackLoader.IconEntry> items = new ArrayList<>();
        private final String targetPkg;

        IconGridAdapter(String pkg) { this.targetPkg = pkg; }

        void setItems(List<IconPackLoader.IconEntry> next) {
            items = next;
            notifyDataSetChanged();
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            float dp = parent.getResources().getDisplayMetrics().density;
            int iconSz = (int)(56 * dp);

            LinearLayout col = new LinearLayout(parent.getContext());
            col.setOrientation(LinearLayout.VERTICAL);
            col.setGravity(Gravity.CENTER);
            col.setPadding((int)(6*dp), (int)(10*dp), (int)(6*dp), (int)(10*dp));
            col.setFocusable(true);
            col.setClickable(true);
            StateListDrawable bg = new StateListDrawable();
            bg.addState(new int[]{android.R.attr.state_focused}, new ColorDrawable(0x44FFFFFF));
            bg.addState(new int[]{}, new ColorDrawable(0));
            col.setBackground(bg);

            ImageView iv = new ImageView(parent.getContext());
            iv.setLayoutParams(new LinearLayout.LayoutParams(iconSz, iconSz));
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            col.addView(iv);

            TextView lbl = new TextView(parent.getContext());
            lbl.setTextColor(0xFF909098);
            lbl.setTextSize(9f);
            lbl.setGravity(Gravity.CENTER);
            lbl.setMaxLines(2);
            col.addView(lbl, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            return new VH(col, iv, lbl);
        }

        @Override
        public void onBindViewHolder(VH h, int pos) {
            IconPackLoader.IconEntry e = items.get(pos);
            h.label.setText(e.drawableName);
            h.icon.setImageDrawable(null);
            h.itemView.setTag(e.drawableName);
            pool.execute(() -> {
                Drawable d = loader.getIconByName(e.drawableName);
                runOnUiThread(() -> {
                    if (e.drawableName.equals(h.itemView.getTag())) h.icon.setImageDrawable(d);
                });
            });
            h.itemView.setOnClickListener(v -> {
                Intent result = new Intent();
                result.putExtra(EXTRA_PACKAGE, targetPkg);
                result.putExtra(EXTRA_DRAWABLE, e.drawableName);
                setResult(RESULT_OK, result);
                finish();
            });
        }

        @Override public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            final ImageView icon;
            final TextView label;
            VH(View v, ImageView icon, TextView label) {
                super(v);
                this.icon = icon;
                this.label = label;
            }
        }
    }
}
