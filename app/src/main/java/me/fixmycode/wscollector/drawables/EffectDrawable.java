package me.fixmycode.wscollector.drawables;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.widget.TextView;


public class EffectDrawable extends Drawable {
    private float ascent;
    private float radius;
    private String text;
    private Rect textBounds;
    private Paint paint;
    private RectF rect;

    public EffectDrawable(TextView view, String text) {
        this.ascent = view.getPaint().ascent();
        float descent = view.getPaint().descent();
        float textSize = view.getTextSize();
        this.text = text;
        this.textBounds = new Rect();
        this.rect = new RectF();
        this.radius = 5.0f;
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Typeface typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
        this.paint.setTypeface(typeface);
        this.paint.setTextSize(textSize);
        this.paint.getTextBounds(this.text, 0, this.text.length(), textBounds);
        this.setBounds(0, 0, (int) (textBounds.width() * 1.2f), (int) (-ascent + descent));
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.rect.set(bounds);
    }

    @Override
    public void draw(Canvas canvas) {
        this.paint.setColor(Color.BLACK);
        canvas.drawRoundRect(rect, radius, radius, paint);
        this.paint.setColor(Color.WHITE);
        canvas.drawText(text, rect.centerX() - textBounds.centerX(), -ascent, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        this.paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        this.paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
