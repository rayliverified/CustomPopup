package stream.custompopup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class CustomPopupWindow extends PopupWindow {

    private final int animDuration = 250;
    //Animation direction constants, automatically calculated
    private final int TOP = 0;
    private final int BOTTOM = 1;
    private int direction = 0;
    private int arrowX = 0;
    private int offsetPadding = 2; //Padding for bg_bubble to not clip elevation.
    private boolean isAnimating; //Prevent popup window from being dismissed why animating.
    private boolean animateDismiss = false; //Flag to keep track of dismiss animation.
    private boolean isShow = false; //Flag to return popup status.

    private ViewGroup rootView;
    private ViewGroup relativeLayout;
    private ViewGroup bubbleContainer;
    private ImageView bubbleArrow;
    private Context mContext;

    public CustomPopupWindow(Context context){

        mContext = context;
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setFocusable(true);
        setOutsideTouchable(false);
        setClippingEnabled(false);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_transparent));
        }
    }

    public void initLayout(int layout){

        rootView = (ViewGroup) View.inflate(mContext, R.layout.item_popup, null);
        relativeLayout = rootView.findViewById(R.id.relativeLayout);
        bubbleContainer = rootView.findViewById(R.id.bubble_container);
        bubbleArrow = rootView.findViewById(R.id.bubble_arrow);
        View.inflate(mContext, layout, bubbleContainer);

        setContentView(rootView);
    }

    public void showPopupWindow(View targetView){

        animateDismiss = false;
        isShow = true;
        if(!isAnimating) {
            isAnimating = true;

            //TargetView measurements.
            int[] arr = new int[2];
            targetView.getLocationOnScreen(arr);
            int targetX = arr[0];
            int targetY = arr[1];
            int targetWidth = targetView.getWidth();
            int targetHeight = targetView.getHeight();

            //Screen dimensions.
            Rect frame = new Rect();
            ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int screenWidth = frame.width();
            int screenHeight = frame.height();

            //Popup Window measurements.
            this.relativeLayout.measure(0, 0);
            int popupWidth = relativeLayout.getMeasuredWidth();
            int popupHeight = relativeLayout.getMeasuredHeight();

            Log.d("Target X", String.valueOf(targetX));
            Log.d("Target Y", String.valueOf(targetY));
            Log.d("Frame Top", String.valueOf(frame.top));
            Log.d("Frame Bottom", String.valueOf(frame.bottom));
            Log.d("Popup Width", String.valueOf(popupWidth));
            Log.d("Popup Height", String.valueOf(popupHeight));
            Log.d("ScreenWidth", String.valueOf(screenWidth));
            Log.d("ScreenHeight", String.valueOf(screenHeight));
            Log.d("Calc", String.valueOf(screenHeight - targetY));
            Log.d("Navigation Bar", String.valueOf(getNavigationBarHeight()));

            int popupX;
            int popupY;
            int bubbleArrowSize = dpToPx(mContext, 20);
            int paddingOffset = dpToPx(mContext, offsetPadding);

            //Calculate vertical position. Position above or below target view.
            int calcHeight = frame.height() - getNavigationBarHeight() + frame.top;
            if (calcHeight - targetY < popupHeight)
            {
                //Position arrow logic
                //Switch arrow drawable to match elevation appearance and direction.
                bubbleArrow.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bubble_arrow));
                //Reset rule to prevent circular dependencies.
                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                relativeParams.addRule(RelativeLayout.BELOW, 0);
                //Create padding offset for drawable to prevent elevation from being clipped.
                bubbleContainer.setBackground(new InsetDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_bubble), paddingOffset, paddingOffset, paddingOffset, 0));
                bubbleContainer.setLayoutParams(relativeParams);
                RelativeLayout.LayoutParams relativeParams1 = new RelativeLayout.LayoutParams(bubbleArrowSize, bubbleArrowSize);
                relativeParams1.addRule(RelativeLayout.BELOW, bubbleContainer.getId());
                bubbleArrow.setLayoutParams(relativeParams1);
                relativeLayout.updateViewLayout(bubbleArrow, relativeParams1);
                relativeLayout.setClipChildren(false);
                //Set popup Y position.
                popupY = targetY - popupHeight;
                direction = BOTTOM;
            }
            else
            {
                bubbleArrow.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bubble_arrow_top));
                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(bubbleArrowSize, bubbleArrowSize);
                relativeParams.addRule(RelativeLayout.BELOW, 0);
                bubbleArrow.setLayoutParams(relativeParams);
                bubbleContainer.setBackground(new InsetDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_bubble), paddingOffset, 0, paddingOffset, paddingOffset));
                RelativeLayout.LayoutParams relativeParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                relativeParams1.addRule(RelativeLayout.BELOW, bubbleArrow.getId());
                bubbleContainer.setLayoutParams(relativeParams1);
                relativeLayout.updateViewLayout(bubbleContainer, relativeParams1);
                relativeLayout.setClipChildren(false);
                popupY = targetY + targetHeight;
                direction = TOP;
            }

            //Calculate horizontal position.
            //If targetview is wider than popup window, set arrow to middle of popup window.
            int targetCalcWidth = targetWidth;
            if (targetWidth > popupWidth)
            {
                targetCalcWidth = popupWidth;
            }
            //If targetview is narrower than arrow, offset x position of popup to fit arrow.
            int offset = 0;
            if (targetWidth < bubbleArrowSize * 2)
            {
                offset = targetWidth;
            }

            int calcX = targetX + targetWidth - popupWidth;
            if (calcX < 0) //Set Popup X position to 0 if centering would push popup off screen.
            {
                popupX = 0;
                arrowX = targetX + targetCalcWidth/2 - bubbleArrowSize/2;
            }
            else if (targetX + popupWidth >= screenWidth) //Do not offset if doing so would push popup off screen.
            {
                popupX = calcX;
                arrowX = popupWidth - targetCalcWidth/2 - bubbleArrowSize/2 - offset;
            }
            else
            {
                popupX = calcX + offset;
                arrowX = popupWidth - targetCalcWidth/2 - bubbleArrowSize/2 - offset;
            }
            bubbleArrow.setX(arrowX);

            showAtLocation(targetView, Gravity.NO_GRAVITY, popupX, popupY);
            showAnimation(relativeLayout, 0, 1, animDuration, direction, arrowX, true);
        }
    }

    private void showAnimation(final View view, float start, final float end, int duration, final int direction, final int xposition, final boolean isWhile) {

        ValueAnimator va = ValueAnimator.ofFloat(start, end).setDuration(duration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                int pivotY = 0;
                switch (direction)
                {
                    case TOP:
                        //Pivot Y = 0;
                        break;
                    case BOTTOM:
                        pivotY = view.getHeight();
                        break;
                }
                view.setPivotX(xposition);
                view.setPivotY(pivotY);
                view.setScaleX(value);
                view.setScaleY(value);
            }
        });
        //2nd bounce animation.
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isWhile) {
                    showAnimation(view, end, 0.95f, animDuration / 3, direction, arrowX, false);
                }else{
                    isAnimating = false;
                }
            }
        });
        va.start();
    }

    @Override
    public void dismiss() {

        isShow = false;
        //Run hide animation first. Then hide when animation is finished.
        if(animateDismiss && !isAnimating) {
            super.dismiss();
        }
        else
        {
            hideAnimation(relativeLayout, 0.95f, 1, animDuration / 3, direction, arrowX, true);
        }
    }

    public void hideAnimation(final View view, float start, final float end, int duration, final int direction, final int xposition, final boolean isWhile){

        ValueAnimator va = ValueAnimator.ofFloat(start, end).setDuration(duration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                int pivotY = 0;
                switch (direction)
                {
                    case TOP:
                        //Pivot Y = 0;
                        break;
                    case BOTTOM:
                        pivotY = view.getHeight();
                        break;
                }
                view.setPivotX(xposition);
                view.setPivotY(pivotY);
                view.setScaleX(value);
                view.setScaleY(value);
            }
        });
        //2nd bounce animation.
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(isWhile){
                    hideAnimation(view, end, 0f, animDuration, direction, arrowX, false);
                }else{
                    animateDismiss = true;
                    isAnimating = false;
                    try {
                        dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        va.start();
    }

    public ViewGroup getLayout(){
        return relativeLayout;
    }

    public boolean isShow() { return isShow; }

    //Display Utils.
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public int getNavigationBarHeight()
    {
        boolean hasMenuKey = ViewConfiguration.get(mContext).hasPermanentMenuKey();
        int resourceId = mContext.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && !hasMenuKey)
        {
            return mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
