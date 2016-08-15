package com.dancmc.pogoiv.utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Daniel on 4/08/2016.
 */
public class LevelAngle extends View {

    private static final String TAG = "LevelAngle";
    private RectF mRect;
    private int mTrainerLevel;
    private float mPokemonLevel;
    private Paint mPaint;

    double[] offset = {0.744255d, 0.658377d, 0.562338d, 0.481253d, 0.423859d, 0.361249d, 0.309478d, 0.250258d, 0.221299d, 0.205857d, 0.167893d, 0.139543d, 0.098993d, 0.073678d, 0.054947d, 0.044529d, 0.036407d, 0.026675d, 0.014264d, 1.25E-4d, -0.013633d, -0.016826d, -0.018831d, -0.020367d, -0.022146d, -0.024021d, -0.025923d, -0.027723d, -0.029214d, -0.031567d, -0.032933d, -0.033912d, -0.034878d, -0.035738d, -0.036692d, -0.037524d, -0.038115d, -0.038623d, -0.03912d, -0.039537d};

    public LevelAngle(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mTrainerLevel = 10;
        mPokemonLevel = 10;
        mRect = new RectF();


        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#1565C0"));
        mPaint.setStrokeWidth(4.0f);
    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = (float) getWidth();
        float height = (float) getHeight();

        float centreY = 0.952f * height;
        float centreX = width / 2.0f;
        float diameter = width * 0.9f;

        mRect.set(0.0f, 0.0f, width, height * 2.0f);
        float x = (float)((Pokemon.getCPMultipliers()[ (int)(mPokemonLevel* 2) - 2+1] - 0.094) * 202.037116 / Pokemon.getCPMultipliers()[(int)(mTrainerLevel*2 - 2+1)]);
        float y = 0.0f;
        if (mTrainerLevel < 25) {
            y = (float) (offset[mTrainerLevel - 1] * ((((double) mPokemonLevel) - 1.0d) / 0.5d));
        }


        canvas.drawLine(centreX - (diameter / 2.0f), centreY, centreX, centreY, mPaint);
        canvas.rotate(Math.min(x+y,180.0f), centreX, centreY);
        canvas.drawLine(centreX - (diameter / 2.0f), centreY, centreX, centreY, mPaint);
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