package com.example.traveltools.control;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class LoadingButton extends View {
    private static final int STATE_BUTTON = 0;
    private static final int STATE_ANIMATION_STEP1 = 1;
    private static final int STATE_ANIMATION_STEP2 = 2;
    private static final int STATE_ANIMATION_LOADING = 3;
    private static final int STATE_STOP_LOADING = 4;
    private static final int STATE_ANIMATION_SUCCESS = 5;
    private static final int STATE_ANIMATION_FAILED = 6;
    private LoadingButton.AnimationEndListener mAnimationEndListener;
    private int mColorPrimary;
    private int mTextColor;
    private int mRippleColor;
    private float mRippleAlpha;
    private boolean resetAfterFailed;
    private String mText;
    private float mPadding;
    private Paint mPaint;
    private Paint ripplePaint;
    private Paint mStrokePaint;
    private Paint mTextPaint;
    private Paint mPathEffectPaint;
    private Paint mPathEffectPaint2;
    private Path mPath;
    private int mScaleWidth;
    private int mScaleHeight;
    private int mDegree;
    private int mAngle;
    private int mEndAngle;
    private int mCurrentState;
    private float mDensity;
    private float mButtonCorner;
    private int mRadius;
    private int width;
    private int height;
    private Matrix mMatrix = new Matrix();
    private float mTextWidth;
    private float mTextHeight;
    private Path mSuccessPath;
    private float mSuccessPathLength;
    private float[] mSuccessPathIntervals;
    private Path mFailedPath;
    private Path mFailedPath2;
    private float mFailedPathLength;
    private float[] mFailedPathIntervals;
    private float mTouchX;
    private float mTouchY;
    private float mRippleRadius;
    private RectF mButtonRectF;
    private RectF mArcRectF;
    private AnimatorSet mLoadingAnimatorSet;

    public LoadingButton(Context context) {
        super(context);
        this.init(context, (AttributeSet)null);
    }

    public LoadingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }

    public LoadingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            int defaultColor = -16776961;
            TypedArray ta = context.obtainStyledAttributes(attrs, com.dx.dxloadingbutton.lib.R.styleable.LoadingButton, 0, 0);
            this.mColorPrimary = ta.getInt(com.dx.dxloadingbutton.lib.R.styleable.LoadingButton_lb_btnColor, defaultColor);
            String text = ta.getString(com.dx.dxloadingbutton.lib.R.styleable.LoadingButton_lb_btnText);
            this.mText = text == null ? "" : text;
            this.mTextColor = ta.getColor(com.dx.dxloadingbutton.lib.R.styleable.LoadingButton_lb_textColor, -1);
            this.resetAfterFailed = ta.getBoolean(com.dx.dxloadingbutton.lib.R.styleable.LoadingButton_lb_resetAfterFailed, true);
            this.mRippleColor = ta.getColor(com.dx.dxloadingbutton.lib.R.styleable.LoadingButton_lb_btnRippleColor, -16777216);
            this.mRippleAlpha = ta.getFloat(com.dx.dxloadingbutton.lib.R.styleable.LoadingButton_lb_btnRippleAlpha, 0.3F);
            ta.recycle();
        }

        this.mCurrentState = 0;
        this.mScaleWidth = 0;
        this.mScaleHeight = 0;
        this.mDegree = 0;
        this.mAngle = 0;
        this.mDensity = this.getResources().getDisplayMetrics().density;
        this.mButtonCorner = 2.0F * this.mDensity;
        this.mPadding = 6.0F * this.mDensity;
        this.mPaint = new Paint();
        this.setLayerType(1, this.mPaint);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(this.mColorPrimary);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.setShadowDepth1();
        this.ripplePaint = new Paint();
        this.ripplePaint.setAntiAlias(true);
        this.ripplePaint.setColor(this.mRippleColor);
        this.ripplePaint.setAlpha((int)(this.mRippleAlpha * 255.0F));
        this.ripplePaint.setStyle(Paint.Style.FILL);
        this.mStrokePaint = new Paint();
        this.mStrokePaint.setAntiAlias(true);
        this.mStrokePaint.setColor(this.mColorPrimary);
        this.mStrokePaint.setStyle(Paint.Style.STROKE);
        this.mStrokePaint.setStrokeWidth(2.0F * this.mDensity);
        this.mTextPaint = new Paint();
        this.mTextPaint.setAntiAlias(true);
        this.mTextPaint.setColor(this.mTextColor);
        this.mTextPaint.setTextSize(16.0F * this.mDensity);
        this.mTextPaint.setFakeBoldText(true);
        this.mTextWidth = this.mTextPaint.measureText(this.mText);
        Rect bounds = new Rect();
        this.mTextPaint.getTextBounds(this.mText, 0, this.mText.length(), bounds);
        this.mTextHeight = (float)bounds.height();
        this.mPathEffectPaint = new Paint();
        this.mPathEffectPaint.setAntiAlias(true);
        this.mPathEffectPaint.setColor(this.mColorPrimary);
        this.mPathEffectPaint.setStyle(Paint.Style.STROKE);
        this.mPathEffectPaint.setStrokeWidth(2.0F * this.mDensity);
        this.mPathEffectPaint2 = new Paint();
        this.mPathEffectPaint2.setAntiAlias(true);
        this.mPathEffectPaint2.setColor(this.mColorPrimary);
        this.mPathEffectPaint2.setStyle(Paint.Style.STROKE);
        this.mPathEffectPaint2.setStrokeWidth(2.0F * this.mDensity);
        this.mPath = new Path();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = this.measureDimension((int)(88.0F * this.mDensity), widthMeasureSpec);
        int height = this.measureDimension((int)(56.0F * this.mDensity), heightMeasureSpec);
        this.setMeasuredDimension(width, height);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
        this.mRadius = (int)((float)this.height - this.mPadding * 2.0F) / 2;
        if (this.mButtonRectF == null) {
            this.mButtonRectF = new RectF();
            this.mButtonRectF.top = this.mPadding;
            this.mButtonRectF.bottom = (float)this.height - this.mPadding;
            this.mArcRectF = new RectF((float)(this.width / 2 - this.mRadius), this.mPadding, (float)(this.width / 2 + this.mRadius), (float)this.height - this.mPadding);
        }

    }

    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case 0:
                this.mTouchX = event.getX();
                this.mTouchY = event.getY();
                this.playRippleAnimation(true);
            case 2:
            default:
                return super.onTouchEvent(event);
            case 1:
            case 3:
                this.playRippleAnimation(false);
                return true;
        }
    }

    private int measureDimension(int defaultSize, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result;
        if (specMode == 1073741824) {
            result = specSize;
        } else if (specMode == -2147483648) {
            result = Math.min(defaultSize, specSize);
        } else {
            result = defaultSize;
        }

        return result;
    }

    public void startLoading() {
        if (this.mCurrentState == 6 && !this.resetAfterFailed) {
            this.scaleFailedPath();
        } else {
            if (this.mCurrentState == 0) {
                this.mCurrentState = 1;
                this.removeShadow();
                this.playStartAnimation(false);
            }

        }
    }

    public void loadingSuccessful() {
        if (this.mLoadingAnimatorSet != null && this.mLoadingAnimatorSet.isStarted()) {
            this.mLoadingAnimatorSet.end();
            this.mCurrentState = 4;
            this.playSuccessAnimation();
        }

    }

    public void loadingFailed() {
        if (this.mLoadingAnimatorSet != null && this.mLoadingAnimatorSet.isStarted()) {
            this.mLoadingAnimatorSet.end();
            this.mCurrentState = 4;
            this.playFailedAnimation();
        }

    }

    public void cancelLoading() {
        if (this.mCurrentState == 3) {
            this.cancel();
        }
    }

    public void reset() {
        if (this.mCurrentState == 5) {
            this.scaleSuccessPath();
        }

        if (this.mCurrentState == 6) {
            this.scaleFailedPath();
        }

    }

    public void setTypeface(Typeface typeface) {
        if (typeface != null) {
            this.mTextPaint.setTypeface(typeface);
            this.invalidate();
        }

    }

    public void setText(String text) {
        if (!TextUtils.isEmpty(text)) {
            this.mText = text;
            this.mTextWidth = this.mTextPaint.measureText(this.mText);
            Rect bounds = new Rect();
            this.mTextPaint.getTextBounds(this.mText, 0, this.mText.length(), bounds);
            this.mTextHeight = (float)bounds.height();
            this.invalidate();
        }
    }

    public void setResetAfterFailed(boolean resetAfterFailed) {
        this.resetAfterFailed = resetAfterFailed;
    }

    public boolean isResetAfterFailed() {
        return this.resetAfterFailed;
    }

    public int getCurrentState() {
        return this.mCurrentState;
    }

    public void setAnimationEndListener(LoadingButton.AnimationEndListener animationEndListener) {
        this.mAnimationEndListener = animationEndListener;
    }

    private void createSuccessPath() {
        if (this.mSuccessPath != null) {
            this.mSuccessPath.reset();
        } else {
            this.mSuccessPath = new Path();
        }

        float mLineWith = 2.0F * this.mDensity;
        float left = (float)(this.width / 2 - this.mRadius + this.mRadius / 3) + mLineWith;
        float top = this.mPadding + (float)(this.mRadius / 2) + mLineWith;
        float right = (float)(this.width / 2 + this.mRadius) - mLineWith - (float)(this.mRadius / 3);
        float bottom = (mLineWith + (float)this.mRadius) * 1.5F + this.mPadding / 2.0F;
        float xPoint = (float)(this.width / 2 - this.mRadius / 6);
        this.mSuccessPath = new Path();
        this.mSuccessPath.moveTo(left, this.mPadding + (float)this.mRadius + mLineWith);
        this.mSuccessPath.lineTo(xPoint, bottom);
        this.mSuccessPath.lineTo(right, top);
        PathMeasure measure = new PathMeasure(this.mSuccessPath, false);
        this.mSuccessPathLength = measure.getLength();
        this.mSuccessPathIntervals = new float[]{this.mSuccessPathLength, this.mSuccessPathLength};
    }

    private void createFailedPath() {
        if (this.mFailedPath != null) {
            this.mFailedPath.reset();
            this.mFailedPath2.reset();
        } else {
            this.mFailedPath = new Path();
            this.mFailedPath2 = new Path();
        }

        float left = (float)(this.width / 2 - this.mRadius + this.mRadius / 2);
        float top = (float)(this.mRadius / 2) + this.mPadding;
        this.mFailedPath.moveTo(left, top);
        this.mFailedPath.lineTo(left + (float)this.mRadius, top + (float)this.mRadius);
        this.mFailedPath2.moveTo((float)(this.width / 2 + this.mRadius / 2), top);
        this.mFailedPath2.lineTo((float)(this.width / 2 - this.mRadius + this.mRadius / 2), top + (float)this.mRadius);
        PathMeasure measure = new PathMeasure(this.mFailedPath, false);
        this.mFailedPathLength = measure.getLength();
        this.mFailedPathIntervals = new float[]{this.mFailedPathLength, this.mFailedPathLength};
        PathEffect PathEffect = new DashPathEffect(this.mFailedPathIntervals, this.mFailedPathLength);
        this.mPathEffectPaint2.setPathEffect(PathEffect);
    }

    private void setShadowDepth1() {
        this.mPaint.setShadowLayer(1.0F * this.mDensity, 0.0F, 1.0F * this.mDensity, 520093696);
    }

    private void setShadowDepth2() {
        this.mPaint.setShadowLayer(2.0F * this.mDensity, 0.0F, 2.0F * this.mDensity, 520093696);
    }

    private void removeShadow() {
        this.mPaint.reset();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(this.mColorPrimary);
        this.mPaint.setStyle(Paint.Style.FILL);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch(this.mCurrentState) {
            case 0:
            case 1:
                float cornerRadius = ((float)this.mRadius - this.mButtonCorner) * ((float)this.mScaleWidth / (float)(this.width / 2 - this.height / 2)) + this.mButtonCorner;
                this.mButtonRectF.left = (float)this.mScaleWidth;
                this.mButtonRectF.right = (float)(this.width - this.mScaleWidth);
                canvas.drawRoundRect(this.mButtonRectF, cornerRadius, cornerRadius, this.mPaint);
                if (this.mCurrentState == 0) {
                    canvas.drawText(this.mText, ((float)this.width - this.mTextWidth) / 2.0F, ((float)this.height - this.mTextHeight) / 2.0F + this.mPadding * 2.0F, this.mTextPaint);
                    if (this.mTouchX > 0.0F || this.mTouchY > 0.0F) {
                        canvas.clipRect(0.0F, this.mPadding, (float)this.width, (float)this.height - this.mPadding);
                        canvas.drawCircle(this.mTouchX, this.mTouchY, this.mRippleRadius, this.ripplePaint);
                    }
                }
                break;
            case 2:
                canvas.drawCircle((float)(this.width / 2), (float)(this.height / 2), (float)(this.mRadius - this.mScaleHeight), this.mPaint);
                canvas.drawCircle((float)(this.width / 2), (float)(this.height / 2), (float)this.mRadius - this.mDensity, this.mStrokePaint);
                break;
            case 3:
                this.mPath.reset();
                this.mPath.addArc(this.mArcRectF, (float)(270 + this.mAngle / 2), (float)(360 - this.mAngle));
                if (this.mAngle != 0) {
                    this.mMatrix.setRotate((float)this.mDegree, (float)(this.width / 2), (float)(this.height / 2));
                    this.mPath.transform(this.mMatrix);
                    this.mDegree += 10;
                }

                canvas.drawPath(this.mPath, this.mStrokePaint);
                break;
            case 4:
                this.mPath.reset();
                this.mPath.addArc(this.mArcRectF, (float)(270 + this.mAngle / 2), (float)this.mEndAngle);
                if (this.mEndAngle != 360) {
                    this.mMatrix.setRotate((float)this.mDegree, (float)(this.width / 2), (float)(this.height / 2));
                    this.mPath.transform(this.mMatrix);
                    this.mDegree += 10;
                }

                canvas.drawPath(this.mPath, this.mStrokePaint);
                break;
            case 5:
                canvas.drawPath(this.mSuccessPath, this.mPathEffectPaint);
                canvas.drawCircle((float)(this.width / 2), (float)(this.height / 2), (float)this.mRadius - this.mDensity, this.mStrokePaint);
                break;
            case 6:
                canvas.drawPath(this.mFailedPath, this.mPathEffectPaint);
                canvas.drawPath(this.mFailedPath2, this.mPathEffectPaint2);
                canvas.drawCircle((float)(this.width / 2), (float)(this.height / 2), (float)this.mRadius - this.mDensity, this.mStrokePaint);
        }

    }

    private void playStartAnimation(final boolean isReverse) {
        ValueAnimator animator = ValueAnimator.ofInt(new int[]{isReverse ? this.width / 2 - this.height / 2 : 0, isReverse ? 0 : this.width / 2 - this.height / 2});
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                LoadingButton.this.mScaleWidth = (Integer)valueAnimator.getAnimatedValue();
                LoadingButton.this.invalidate();
            }
        });
        animator.setDuration(400L);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setStartDelay(100L);
        animator.addListener(new LoadingButton.AnimatorEndListener() {
            public void onAnimationEnd(Animator animator) {
                LoadingButton.this.mCurrentState = isReverse ? 0 : 2;
                if (LoadingButton.this.mCurrentState == 0) {
                    LoadingButton.this.setShadowDepth1();
                    LoadingButton.this.invalidate();
                }

            }
        });
        ValueAnimator animator2 = ValueAnimator.ofInt(new int[]{isReverse ? this.mRadius : 0, isReverse ? 0 : this.mRadius});
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                LoadingButton.this.mScaleHeight = (Integer)valueAnimator.getAnimatedValue();
                LoadingButton.this.invalidate();
            }
        });
        animator2.setDuration(240L);
        animator2.setInterpolator(new AccelerateDecelerateInterpolator());
        animator2.addListener(new LoadingButton.AnimatorEndListener() {
            public void onAnimationEnd(Animator animator) {
                LoadingButton.this.mCurrentState = isReverse ? 1 : 3;
            }
        });
        ValueAnimator loadingAnimator = ValueAnimator.ofInt(new int[]{30, 300});
        loadingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                LoadingButton.this.mAngle = (Integer)valueAnimator.getAnimatedValue();
                LoadingButton.this.invalidate();
            }
        });
        loadingAnimator.setDuration(1000L);
        loadingAnimator.setRepeatCount(-1);
        loadingAnimator.setRepeatMode(2);
        loadingAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        this.mLoadingAnimatorSet = new AnimatorSet();
        if (isReverse) {
            this.mLoadingAnimatorSet.playSequentially(new Animator[]{animator2, animator});
        } else {
            this.mLoadingAnimatorSet.playSequentially(new Animator[]{animator, animator2, loadingAnimator});
        }

        this.mLoadingAnimatorSet.start();
    }

    private void playSuccessAnimation() {
        this.createSuccessPath();
        ValueAnimator animator = ValueAnimator.ofInt(new int[]{360 - this.mAngle, 360});
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                LoadingButton.this.mEndAngle = (Integer)valueAnimator.getAnimatedValue();
                LoadingButton.this.invalidate();
            }
        });
        animator.setDuration(240L);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addListener(new LoadingButton.AnimatorEndListener() {
            public void onAnimationEnd(Animator animator) {
                LoadingButton.this.mCurrentState = 5;
            }
        });
        ValueAnimator successAnimator = ValueAnimator.ofFloat(new float[]{0.0F, 1.0F});
        successAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (Float)valueAnimator.getAnimatedValue();
                PathEffect PathEffect = new DashPathEffect(LoadingButton.this.mSuccessPathIntervals, LoadingButton.this.mSuccessPathLength - LoadingButton.this.mSuccessPathLength * value);
                LoadingButton.this.mPathEffectPaint.setPathEffect(PathEffect);
                LoadingButton.this.invalidate();
            }
        });
        successAnimator.setDuration(500L);
        successAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(new Animator[]{animator, successAnimator});
        set.addListener(new LoadingButton.AnimatorEndListener() {
            public void onAnimationEnd(Animator animator) {
                if (LoadingButton.this.mAnimationEndListener != null) {
                    LoadingButton.this.mAnimationEndListener.onAnimationEnd(LoadingButton.AnimationType.SUCCESSFUL);
                }

            }
        });
        set.start();
    }

    private void playFailedAnimation() {
        this.createFailedPath();
        ValueAnimator animator = ValueAnimator.ofInt(new int[]{360 - this.mAngle, 360});
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                LoadingButton.this.mEndAngle = (Integer)valueAnimator.getAnimatedValue();
                LoadingButton.this.invalidate();
            }
        });
        animator.setDuration(240L);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addListener(new LoadingButton.AnimatorEndListener() {
            public void onAnimationEnd(Animator animator) {
                LoadingButton.this.mCurrentState = 6;
            }
        });
        ValueAnimator failedAnimator = ValueAnimator.ofFloat(new float[]{0.0F, 1.0F});
        failedAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (Float)valueAnimator.getAnimatedValue();
                PathEffect PathEffect = new DashPathEffect(LoadingButton.this.mFailedPathIntervals, LoadingButton.this.mFailedPathLength - LoadingButton.this.mFailedPathLength * value);
                LoadingButton.this.mPathEffectPaint.setPathEffect(PathEffect);
                LoadingButton.this.invalidate();
            }
        });
        failedAnimator.setDuration(300L);
        failedAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        ValueAnimator failedAnimator2 = ValueAnimator.ofFloat(new float[]{0.0F, 1.0F});
        failedAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (Float)valueAnimator.getAnimatedValue();
                PathEffect PathEffect = new DashPathEffect(LoadingButton.this.mFailedPathIntervals, LoadingButton.this.mFailedPathLength - LoadingButton.this.mFailedPathLength * value);
                LoadingButton.this.mPathEffectPaint2.setPathEffect(PathEffect);
                LoadingButton.this.invalidate();
            }
        });
        failedAnimator2.setDuration(300L);
        failedAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(new Animator[]{animator, failedAnimator, failedAnimator2});
        set.addListener(new LoadingButton.AnimatorEndListener() {
            public void onAnimationEnd(Animator animator) {
                if (LoadingButton.this.resetAfterFailed) {
                    LoadingButton.this.postDelayed(new Runnable() {
                        public void run() {
                            LoadingButton.this.scaleFailedPath();
                        }
                    }, 1000L);
                } else {
                    if (LoadingButton.this.mAnimationEndListener != null) {
                        LoadingButton.this.mAnimationEndListener.onAnimationEnd(LoadingButton.AnimationType.FAILED);
                    }

                }
            }
        });
        set.start();
    }

    private void cancel() {
        this.mCurrentState = 4;
        ValueAnimator animator = ValueAnimator.ofInt(new int[]{360 - this.mAngle, 360});
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                LoadingButton.this.mEndAngle = (Integer)valueAnimator.getAnimatedValue();
                LoadingButton.this.invalidate();
            }
        });
        animator.setDuration(240L);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addListener(new LoadingButton.AnimatorEndListener() {
            public void onAnimationEnd(Animator animator) {
                LoadingButton.this.mCurrentState = 2;
                LoadingButton.this.playStartAnimation(true);
            }
        });
        animator.start();
    }

    private void scaleSuccessPath() {
        final Matrix scaleMatrix = new Matrix();
        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(new float[]{1.0F, 0.0F});
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (Float)valueAnimator.getAnimatedValue();
                scaleMatrix.setScale(value, value, (float)(LoadingButton.this.width / 2), (float)(LoadingButton.this.height / 2));
                LoadingButton.this.mSuccessPath.transform(scaleMatrix);
                LoadingButton.this.invalidate();
            }
        });
        scaleAnimator.setDuration(300L);
        scaleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleAnimator.addListener(new LoadingButton.AnimatorEndListener() {
            public void onAnimationEnd(Animator animator) {
                LoadingButton.this.mCurrentState = 2;
                LoadingButton.this.playStartAnimation(true);
            }
        });
        scaleAnimator.start();
    }

    private void scaleFailedPath() {
        final Matrix scaleMatrix = new Matrix();
        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(new float[]{1.0F, 0.0F});
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (Float)valueAnimator.getAnimatedValue();
                scaleMatrix.setScale(value, value, (float)(LoadingButton.this.width / 2), (float)(LoadingButton.this.height / 2));
                LoadingButton.this.mFailedPath.transform(scaleMatrix);
                LoadingButton.this.mFailedPath2.transform(scaleMatrix);
                LoadingButton.this.invalidate();
            }
        });
        scaleAnimator.setDuration(300L);
        scaleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleAnimator.addListener(new LoadingButton.AnimatorEndListener() {
            public void onAnimationEnd(Animator animator) {
                LoadingButton.this.mCurrentState = 2;
                LoadingButton.this.playStartAnimation(true);
            }
        });
        scaleAnimator.start();
    }

    private void playRippleAnimation(boolean isTouchDown) {
        this.setShadowDepth2();
        ValueAnimator rippleAnimator = ValueAnimator.ofFloat(new float[]{isTouchDown ? 0.0F : (float)(this.width / 2), isTouchDown ? (float)(this.width / 2) : (float)this.width});
        rippleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                LoadingButton.this.mRippleRadius = (Float)valueAnimator.getAnimatedValue();
                LoadingButton.this.invalidate();
            }
        });
        rippleAnimator.setDuration(240L);
        rippleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        if (!isTouchDown) {
            rippleAnimator.addListener(new LoadingButton.AnimatorEndListener() {
                public void onAnimationEnd(Animator animator) {
                    LoadingButton.this.performClick();
                    LoadingButton.this.mTouchX = 0.0F;
                    LoadingButton.this.mTouchY = 0.0F;
                    LoadingButton.this.mRippleRadius = 0.0F;
                    LoadingButton.this.invalidate();
                }
            });
        }

        rippleAnimator.start();
    }

    public interface AnimationEndListener {
        void onAnimationEnd(LoadingButton.AnimationType var1);
    }

    private abstract class AnimatorEndListener implements Animator.AnimatorListener {
        private AnimatorEndListener() {
        }

        public void onAnimationStart(Animator animator) {
        }

        public void onAnimationCancel(Animator animator) {
        }

        public void onAnimationRepeat(Animator animator) {
        }
    }

    public static enum AnimationType {
        SUCCESSFUL,
        FAILED;

        private AnimationType() {
        }
    }
}
