/*
 * Copyright (C) 2010 The Android Open Source Project Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.dongluh.rotateview.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

// A RotateLayout is designed to display a single item and provides the
// capabilities to rotate the item.
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class RotateLayout extends ViewGroup implements Rotatable {
    private int mOrientation;

    private boolean lowerHoneycomb = isLowerHoneycomb();

    protected View mChild;

    /**
     * 旋转接口
     * 
     * @param context
     *            Context
     */
    public RotateLayout(Context context) {
        super(context);
        setBackgroundResource(android.R.color.transparent);
    }

    /**
     * 构造函数
     * 
     * @param context
     *            Context
     * @param attrs
     *            AttributeSet
     */
    public RotateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(android.R.color.transparent);
    }

    /**
     * 构造函数
     * 
     * @param context
     *            Context
     * @param attrs
     *            AttributeSet
     * @param defStyle
     *            Style
     */
    public RotateLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setBackgroundResource(android.R.color.transparent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFinishInflate() {
        mChild = getChildAt(0);
        if (!lowerHoneycomb) {
            mChild.setPivotX(0);
            mChild.setPivotY(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onLayout(boolean change, int left, int top, int right,
            int bottom) {
        int width = right - left;
        int height = bottom - top;
        switch (mOrientation) {
        case 0:
        case 180:
            mChild.layout(0, 0, width, height);
            break;
        case 90:
        case 270:
            try{
                mChild.layout(0, 0, height, width);
            }catch(Exception e){
                e.printStackTrace();
            }
            break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {

        int w = 0, h = 0;
        switch (mOrientation) {
        case 0:
        case 180:
            measureChild(mChild, widthSpec, heightSpec);
            w = mChild.getMeasuredWidth();
            h = mChild.getMeasuredHeight();
            break;
        case 90:
        case 270:
            measureChild(mChild, heightSpec, widthSpec);
            w = mChild.getMeasuredHeight();
            h = mChild.getMeasuredWidth();
            break;
        }
        setMeasuredDimension(w, h);

        if (!lowerHoneycomb) {
            switch (mOrientation) {
            case 0:
                mChild.setTranslationX(0);
                mChild.setTranslationY(0);
                break;
            case 90:
                mChild.setTranslationX(0);
                mChild.setTranslationY(h);
                break;
            case 180:
                mChild.setTranslationX(w);
                mChild.setTranslationY(h);
                break;
            case 270:
                mChild.setTranslationX(w);
                mChild.setTranslationY(0);
                break;
            }
            mChild.setRotation(-mOrientation);
        } else {
            invalidate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (lowerHoneycomb) {
            switch (mOrientation) {
            case 0:
                canvas.rotate(-mOrientation, mChild.getWidth() / 2,
                        mChild.getHeight() / 2);
                break;
            case 180:
                canvas.rotate(-mOrientation, mChild.getWidth() / 2,
                        mChild.getHeight() / 2);
                break;
            case 90:
                canvas.rotate(-mOrientation, mChild.getWidth() / 2,
                        mChild.getWidth() / 2);
                break;
            case 270:
                canvas.rotate(-mOrientation, mChild.getHeight() / 2,
                        mChild.getHeight() / 2);
                break;
            }

            canvas.save();
            super.dispatchDraw(canvas);
            canvas.restore();
        } else {
            super.dispatchDraw(canvas);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mOrientation == 0) {
            return super.dispatchTouchEvent(event);
        }

        if (lowerHoneycomb) {
            float width = getWidth();
            float height = getHeight();
            int count = event.getPointerCount();
            if (count == 1) {
                float x = event.getX();
                float y = event.getY();
                switch (mOrientation) {
                case 180:
                    event.setLocation(width - x, height - y);
                    break;
                case 90:
                    event.setLocation(height - y, x);
                    break;
                case 270:
                    event.setLocation(y, width - x);
                    break;
                default:
                    event.setLocation(x, y);
                }
                return mChild.dispatchTouchEvent(event);
            }

            // 多点触控限API 9以上
            if (count == 2
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                MotionEvent newEvent = getMtionEvent(event, count, width,
                        height);
                return mChild.dispatchTouchEvent(newEvent);
            }

            return mChild.dispatchTouchEvent(event);
        }

        return super.dispatchTouchEvent(event);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOrientation(int orientation, boolean animation) {
        orientation = orientation % 360;
        if (mOrientation == orientation)
            return;
        mOrientation = orientation;
        requestLayout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
        if (lowerHoneycomb) {
            dirty.set(0, 0, getWidth(), getHeight());
        }
        return super.invalidateChildInParent(location, dirty);
    }

    private boolean isLowerHoneycomb() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * 取得转化后的MtionEvent，限API 9，API 10调用
     * 
     * @param event
     *            MotionEvent
     * @param count
     *            PointerCount
     * @param width
     *            width
     * @param height
     *            height
     * @return 转化后的MtionEvent
     */
    private MotionEvent getMtionEvent(MotionEvent event, int count,
            float width, float height) {
        // TODO DEBUG

        int[] pointerIds = new int[count];
        MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[count];

        for (int i = 0; i < count; i++) {
            pointerIds[i] = event.getPointerId(i);

            MotionEvent.PointerCoords coord = new PointerCoords();
            float x = event.getX(i);
            float y = event.getY(i);
            switch (mOrientation) {
            case 180:
                coord.x = width - x;
                coord.y = height - y;
                coord.orientation = -event.getOrientation(i);
                break;
            case 90:
                coord.x = height - y;
                coord.y = x;
                coord.orientation = getCoordOrientation(event.getOrientation(i));
                break;
            case 270:
                coord.x = y;
                coord.y = width - x;
                coord.orientation = getCoordOrientation(event.getOrientation(i));
                break;
            default:
                coord.x = x;
                coord.y = y;
                coord.orientation = event.getOrientation(i);
            }

            coord.pressure = event.getPressure(i);
            coord.size = event.getSize(i);
            coord.toolMajor = event.getToolMajor(i);
            coord.toolMinor = event.getToolMinor(i);
            coord.touchMajor = event.getTouchMajor(i);
            coord.touchMinor = event.getTouchMinor(i);

            pointerCoords[i] = coord;
        }

        @SuppressWarnings("deprecation")
        MotionEvent newEvent = MotionEvent.obtain(event.getDownTime(),
                event.getEventTime(), event.getAction(), count, pointerIds,
                pointerCoords, event.getMetaState(), event.getXPrecision(),
                event.getYPrecision(), event.getDeviceId(),
                event.getEdgeFlags(), event.getSource(), event.getFlags());

        return newEvent;
    }

    private float getCoordOrientation(float angel) {
        float result = angel;
        switch (mOrientation) {
        case 90:
            result = angel - (float) Math.PI / 2;
            break;
        case 270:
            result = angel + (float) Math.PI / 2;
            break;
        case 180:
            result = -angel;
            break;
        default:
            break;
        }

        if (result < -Math.PI / 2) {
            result += Math.PI;
        } else if (result > Math.PI / 2) {
            result -= Math.PI;
        }

        return result;
    }
}
