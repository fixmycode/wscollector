package me.fixmycode.wscollector.drawables;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;


public class RoundImageDrawable extends Drawable {
    private final RectF rect;
    private final Paint paint;
    private final Paint background;
    private Bitmap bitmap;


    public RoundImageDrawable(Bitmap bitmap){
        this.bitmap = bitmap;
        rect = new RectF(0.0f, 0.0f, this.bitmap.getWidth(), this.bitmap.getHeight());
        Shader shader = new BitmapShader(this.bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        background = new Paint();
        background.setAntiAlias(true);
        background.setColor(Color.GRAY);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        bitmap = Bitmap.createScaledBitmap(bitmap, bounds.width(), bounds.height(), true);
        Shader shader = new BitmapShader(this.bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        rect.set(0.0f, 0.0f, bitmap.getWidth(), bitmap.getHeight());
    }

    @Override
    public void draw(Canvas canvas) {
        float minRadius = rect.height()/2;
        canvas.drawCircle(minRadius, minRadius, minRadius, background);
        canvas.drawCircle(minRadius, minRadius, minRadius - 0.5f, paint);

    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
