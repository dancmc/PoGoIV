package com.dancmc.pogoiv.utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Daniel on 4/08/2016.
 */
public class LevelAngle extends View {

    private RectF mRect;
    private int mTrainerLevel;
    private float mPokemonLevel;
    private Paint mPaint;


    public LevelAngle(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mTrainerLevel = 10;
        mPokemonLevel = 10;
        mRect = new RectF();


        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#1565C0"));
        mPaint.setStrokeWidth(4.5f);
    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = (float) getWidth();
        float height = (float) getHeight();

        float f2 = 0.9f * height;
        float centre = width / 2.0f;
        float diameter = width * 0.9f;

        mRect.set(0.0f, 0.0f, width, height * 2.0f);
        float min = (float)((Pokemon.getCPMultipliers()[ (int)(mPokemonLevel* 2) - 2+1] - 0.094) * 202.037116 / Pokemon.getCPMultipliers()[(int)(mTrainerLevel*2 - 2+1)]);
        canvas.drawLine(centre - (diameter / 2.0f), f2, centre, f2, mPaint);
        canvas.rotate(min, centre, f2);
        canvas.drawLine(centre - (diameter / 2.0f), f2, centre, f2, mPaint);
    }


    public void setTrainerLevel(int trainerLevel) {
        mTrainerLevel = trainerLevel;
        invalidate();
    }

    public void setPokemonLevel(float pokemonLevel) {
        mPokemonLevel = pokemonLevel;
        invalidate();
    }

}