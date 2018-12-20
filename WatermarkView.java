package com.jwd.terminal.defineview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.jwd.terminal.R;


/**
 * 水印效果
 * created by dongliang
 * 2018/11/28  10:39
 */
public class WatermarkView extends View {
    private static String tag = "watermarkView";
    private int bgColor = 0xFFdddddd;     //背景色
    private int textColor = 0xddCCFF99;   //文字颜色
    private float strokeWidth = 2f;
    private Paint mPaint;
    private float textSize = 20f;
    private String content = "test";//水印内容
    private int width, height;
    private int showType = 0; //0重复排列 1只有一个居中
    private int rotate=30;//旋转角度  -90到90度 默认30度

    public WatermarkView(@NonNull Context context) {
        super(context);
        initPaint();

    }

    public WatermarkView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context,attrs);
        initPaint();
    }

    public WatermarkView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }



    private  void  initAttributes(Context context,  AttributeSet attrs){
        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.WatermarkView);// 获取自定义的属性集
        showType = typedArray.getInt(R.styleable.WatermarkView_showType, 0);
        bgColor=typedArray.getInt(R.styleable.WatermarkView_bgColor,0xFFdddddd);
        textColor = typedArray.getColor(R.styleable.WatermarkView_textColor, 0xffffffcc);
        textSize = typedArray.getDimension(R.styleable.WatermarkView_textSize, 20f);
        Log.e(tag,"textsize"+textSize);
        int degree = typedArray.getInt(R.styleable.WatermarkView_rotate, 30);
        if (degree < -90) {
            degree = -90;
        } else if (degree > 90) {
            degree = 90;
        }
        rotate = degree;
        typedArray.recycle();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(textColor);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

//        createBgBitmap();
    }

    public void createBgBitmap() {

        switch (showType) {
            case 0:
                createRepeatBitmap();
                break;
            case 1:
                createSingleBitmap();
                break;

            default:
                createRepeatBitmap();
                break;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
        createBgBitmap();
    }

    /**
     * showType=1 只有一个居中水印
     * created by dongliang
     * 2018/11/28  13:31
     */
    private void createSingleBitmap() {

        float textWidth = mPaint.measureText(content);     //文字宽度
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float textHeight = (-fontMetrics.ascent - fontMetrics.descent) / 2+textSize;
        //计算斜边长度
        double radians=Math.toRadians(Math.abs(rotate));
        Bitmap bitmap = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(bgColor);
        Point pointStart=new Point();
        Point pointEnd=new Point();

        if (rotate>=0){
            pointStart.x=(int)(width/2-textWidth/2*Math.cos(radians));
            pointStart.y=(int)(height/2+textWidth/2*Math.sin(radians));
            pointEnd.x=(int) (width/2+textWidth/2*Math.cos(radians));
            pointEnd.y=(int) (height/2-textWidth/2*Math.sin(radians));
        }else {
            pointStart.x=(int)(width/2-textWidth/2*Math.cos(radians));
            pointStart.y=(int)(height/2-textWidth/2*Math.sin(radians));
            pointEnd.x=(int) (width/2+textWidth/2*Math.cos(radians));
            pointEnd.y=(int) (height/2+textWidth/2*Math.sin(radians));
        }
        Path path = new Path();
        path.moveTo(pointStart.x, pointStart.y);
        path.lineTo(pointEnd.x, pointEnd.y);
        canvas.drawTextOnPath(content, path, 0, 0, mPaint);

        BitmapDrawable bitmapDrawable = new BitmapDrawable(null, bitmap);

        bitmapDrawable.setDither(true);
        setBackground(bitmapDrawable);
    }

    /**
     * showType=0 重复绘制水印
     * created by dongliang
     * 2018/11/28  13:31
     */
    private void createRepeatBitmap() {
        float textWidth = mPaint.measureText(content);     //文字宽度
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float textHeight = (-fontMetrics.ascent - fontMetrics.descent) / 2+textSize;//文字高度
        //计算斜边长度
        double radians=Math.toRadians(Math.abs(rotate));
        Bitmap bitmap = Bitmap.createBitmap((int) (textWidth+textHeight*2), (int) (textWidth+textHeight*2), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(bgColor);
        Point pointStart=new Point();
        Point pointEnd=new Point();

        if (rotate>=0){
            pointStart.x=(int) textHeight;
            pointStart.y=(int)(textWidth+textHeight);
            pointEnd.x=(int) (textHeight+textWidth*Math.cos(radians));
            pointEnd.y=(int) textHeight;
        }else {
            pointStart.x=(int) textHeight;
            pointStart.y=(int)textHeight;
            pointEnd.x=(int)(textWidth*Math.cos(radians)+pointStart.x);
            pointEnd.y=(int)(textWidth+textHeight);
        }
        Path path = new Path();
        path.moveTo(pointStart.x, pointStart.y);
        path.lineTo(pointEnd.x, pointEnd.y);
        canvas.drawTextOnPath(content, path, 0, 0, mPaint);

        BitmapDrawable bitmapDrawable = new BitmapDrawable(null, bitmap);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        bitmapDrawable.setDither(true);
        setBackground(bitmapDrawable);
    }

    public int sp2px(Context context,float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
        invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        mPaint.setColor(textColor);
        invalidate();
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        this.mPaint.setStrokeWidth(strokeWidth);
        invalidate();
    }

    public Paint getmPaint() {
        return mPaint;
    }

    public void setmPaint(Paint mPaint) {
        this.mPaint = mPaint;
        invalidate();
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        mPaint.setTextSize(textSize);
        invalidate();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        invalidate();
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
        invalidate();
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
        invalidate();
    }
}
