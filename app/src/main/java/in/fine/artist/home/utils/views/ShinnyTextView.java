package in.fine.artist.home.utils.views;

/**
 * Created by rivigo on 6/2/17.
 */


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

/**
 * @author Pierre Degand
 */
public class ShinnyTextView extends Button {

    private float mGradientDiameter = 0.3f;

    private ValueAnimator mAnimator;
    private float mGradientCenter;
    private PaintDrawable mShineDrawable;

    public ShinnyTextView(final Context context) {
        this(context, null);
    }

    public ShinnyTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShinnyTextView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!isInEditMode()) {
            if (mAnimator != null) {
                mAnimator.cancel();
            }
            mShineDrawable = new PaintDrawable();
            mShineDrawable.setBounds(0, 0, w, h);
            mShineDrawable.getPaint().setShader(generateGradientShader(getWidth(), 0, 0, 0));
            mShineDrawable.getPaint().setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            mAnimator = ValueAnimator.ofFloat(0, 1);
            mAnimator.setDuration(3 * w); // custom
            mAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mAnimator.setRepeatMode(ValueAnimator.RESTART);
            mAnimator.setInterpolator(new LinearInterpolator()); // Custom
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(final ValueAnimator animation) {
                    final float value = animation.getAnimatedFraction();
                    mGradientCenter = (1 + 2 * mGradientDiameter) * value - mGradientDiameter;
                    final float gradientStart = mGradientCenter - mGradientDiameter;
                    final float gradientEnd = mGradientCenter + mGradientDiameter;
                    Shader shader = generateGradientShader(w, gradientStart, mGradientCenter, gradientEnd);
                    mShineDrawable.getPaint().setShader(shader);
                    invalidate();
                }
            });
            mAnimator.start();
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode() && mShineDrawable != null) {
            mShineDrawable.draw(canvas);
        }
    }

    private Shader generateGradientShader(int width, float... positions) {
        //int[] colorRepartition = {Color.WHITE, Color.parseColor("#96d3f0"),Color.WHITE};
        int[] colorRepartition = {Color.WHITE,Color.WHITE,Color.WHITE};
        return new LinearGradient(
                0,
                0,
                width,
                0,
                colorRepartition,
                positions,
                Shader.TileMode.REPEAT
        );
    }
}