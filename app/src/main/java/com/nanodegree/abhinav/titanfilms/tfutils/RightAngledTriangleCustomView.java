package com.nanodegree.abhinav.titanfilms.tfutils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.nanodegree.abhinav.titanfilms.R;

/**
 * This class draws custom view using View parent class.
 *
 * Created by Abhinav Puri
 */
public class RightAngledTriangleCustomView extends View{

    Paint mPaint;
    int mColor;

    public RightAngledTriangleCustomView(Context context) {
        this(context, null);
    }

    public RightAngledTriangleCustomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RightAngledTriangleCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.RightAngledTriangleCustomView, defStyleAttr, 0);
        mColor = attr.getColor(R.styleable.RightAngledTriangleCustomView_ratcv_color,context.getResources().getColor(android.R.color.holo_blue_dark));
        Create(context);
    }

    private void Create(Context context) {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Path path = new Path();

        // Lets trace a triangle :
        path.moveTo(0,getHeight());
        path.lineTo(getWidth(),getHeight());
        path.lineTo(0, 0);
        path.lineTo(0, getHeight());

        // Finally Draw to convas :
        canvas.drawPath(path, mPaint);
    }
}
