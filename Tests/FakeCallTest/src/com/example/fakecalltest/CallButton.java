//package com.example.fakecalltest;
//
//import android.animation.ObjectAnimator;
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.util.TypedValue;
//import android.widget.ImageView;
//
//public class CallButton extends ImageView {
//	private int centerY;
//	private int centerX;
//	private int outerRadius;
//	private int pressedRingRadius;
//
//	private Paint circlePaint;
//	private Paint focusPaint;
//
//	private float animationProgress;
//
//	private int pressedRingWidth;
//	private int defaultColor = Color.BLACK;
//	private int pressedColor;
//	private ObjectAnimator pressedAnimator;
//
//	public CallButton(Context context) {
//		super(context);
//		
//		this.setFocusable(true);
//		this.setScaleType(ScaleType.CENTER_INSIDE);
//		setClickable(true);
//
//		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		circlePaint.setStyle(Paint.Style.FILL);
//
//		focusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		focusPaint.setStyle(Paint.Style.STROKE);
//
//		pressedRingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PRESSED_RING_WIDTH_DIP, getResources()
//				.getDisplayMetrics());
//
//		int color = Color.BLACK;
//		if (attrs != null) {
//			final TypedArray a = context.obtainStyledAttributes(null, R.styleable.CircleButton);
//			color = a.getColor(R.styleable.CircleButton_cb_color, color);
//			pressedRingWidth = (int) a.getDimension(R.styleable.CircleButton_cb_pressedRingWidth, pressedRingWidth);
//			a.recycle();
//		}
//
//		setColor(color);
//
//		focusPaint.setStrokeWidth(pressedRingWidth);
//		final int pressedAnimationTime = getResources().getInteger(ANIMATION_TIME_ID);
//		pressedAnimator = ObjectAnimator.ofFloat(this, "animationProgress", 0f, 0f);
//		pressedAnimator.setDuration(pressedAnimationTime);
//	}
//	
//	@Override
//	public void setPressed(boolean pressed) {
//		super.setPressed(pressed);
//	}
//
//}
