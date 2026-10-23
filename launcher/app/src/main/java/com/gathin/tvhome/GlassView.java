package com.gathin.tvhome;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * The selection highlight: a bright rim + subtle fill + outer white glow so the focused tile
 * is unmistakably visible against any background. One instance floats over the grid and is moved
 * with spring animations between tiles.
 */
public class GlassView extends View {

    private final GlassRim rim;
    private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private final RectF glowRect = new RectF();
    private final float density;
    private float radius;

    public GlassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = getResources().getDisplayMetrics().density;
        radius = 20f * density;
        rim = new GlassRim(2f * density, 0xFFFFFFFF, 0x66FFFFFF);

        // Subtle white fill so the tile interior is lightly brightened when focused.
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(0x22FFFFFF);

        // Outer glow: draw a larger rounded rect with a soft white shadow.
        glowPaint.setStyle(Paint.Style.STROKE);
        glowPaint.setStrokeWidth(3f * density);
        glowPaint.setColor(0xAAFFFFFF);
        glowPaint.setShadowLayer(18f * density, 0f, 0f, 0xCCFFFFFF);

        // Software layer needed for setShadowLayer to work on non-text draws.
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    void setRadius(float px) {
        radius = px;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float inset = 6f * density;
        rect.set(inset, inset, getWidth() - inset, getHeight() - inset);

        // Outer glow (slightly expanded rect so the shadow blooms outside the tile boundary).
        float glowInset = 2f * density;
        glowRect.set(inset - glowInset, inset - glowInset,
                getWidth() - inset + glowInset, getHeight() - inset + glowInset);
        canvas.drawRoundRect(glowRect, radius + glowInset, radius + glowInset, glowPaint);

        // Subtle fill.
        canvas.drawRoundRect(rect, radius, radius, fillPaint);

        // Bright rim.
        rim.fit(rect);
        canvas.drawRoundRect(rect, radius, radius, rim.paint);
    }
}
