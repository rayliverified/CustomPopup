package stream.custompopup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class CustomPopupWindow {

    private final int animDuration = 250;
    //Animation direction constants, automatically calculated
    private final int TOP = 0;
    private final int BOTTOM = 1;
    private final int TOP_LEFT = 0;
    private final int TOP_RIGHT = 1;
    private final int BOTTOM_LEFT = 2;
    private final int BOTTOM_RIGHT = 3;
    private int DIRECTION = 0;
    private boolean isAnimating; //Prevent popup window from being dismissed why animating.

    private WindowManager.LayoutParams params;
    private boolean isShow;
    private WindowManager windowManager;
    private ViewGroup rootView;
    private View background;
    private ViewGroup relativeLayout;
    private ViewGroup bubbleContainer;
    private ImageView bubbleArrow;
    private Context mContext;

    public CustomPopupWindow(Context context){

        mContext = context;
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    public void initLayout(int layout){

        rootView = (ViewGroup) View.inflate(mContext, R.layout.item_popup, null);
        background = (View) rootView.findViewById(R.id.background);
        relativeLayout = (ViewGroup) rootView.findViewById(R.id.relativeLayout);
        bubbleContainer = (ViewGroup) rootView.findViewById(R.id.bubble_container);
        bubbleArrow = (ImageView) rootView.findViewById(R.id.bubble_arrow);
        View view = View.inflate(mContext, layout, bubbleContainer);

        params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.format = PixelFormat.RGBA_8888;
        params.gravity = Gravity.NO_GRAVITY;

        //Dismiss popup listeners
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopupWindow();
            }
        });
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Clicked", "RelativeLayout");
            }
        });
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK)
                {
                    dismissPopupWindow();
                }
                return isShow;
            }
        });
    }

    public void showPopupWindow(View locationView){
        Log.i("Log.i", "showPopupWindow: "+ isAnimating);
        if(!isAnimating) {
            isAnimating = true;
            int[] arr = new int[2];
            locationView.getLocationOnScreen(arr);
//            relativeLayout.measure(0, 0);
            Rect frame = new Rect();
            ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);//得到状态栏高度
            int targetX = arr[0];
            int targetY = arr[1];
            int targetWidth = locationView.getWidth();
            int screenWidth = frame.width();
            int screenHeight = frame.height();
            int popupWidth = relativeLayout.getMeasuredWidth();
            int popupHeight = relativeLayout.getMeasuredHeight();

//                Log.d("Target Y", String.valueOf(targetY));
//                Log.d("Frame Top", String.valueOf(frame.top));
//                Log.d("Frame Bottom", String.valueOf(frame.bottom));
//                Log.d("Popup Height", String.valueOf(popupHeight));
//                Log.d("ScreenWidth", String.valueOf(screenWidth));
//                Log.d("ScreenHeight", String.valueOf(screenHeight));
//                Log.d("Calc", String.valueOf(screenHeight - targetY));
//                Log.d("Navigation Bar", String.valueOf(getNavigationBarHeight()));

            int popupX;
            int popupY;
            int directionY;
            int bubbleArrowSize = dpToPx(mContext, 20);

            //Calculate vertical position. Position above or below target view.
            int calcHeight = frame.height() - getNavigationBarHeight() + frame.top;
            if (calcHeight - targetY < popupHeight)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    bubbleArrow.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bubble_arrow));
                }
                else
                {
                    bubbleArrow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bubble_arrow));
                }
                //Reset rule to prevent circular dependencies.
                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                relativeParams.addRule(RelativeLayout.BELOW, 0);
                bubbleContainer.setLayoutParams(relativeParams);
                RelativeLayout.LayoutParams relativeParams1 = new RelativeLayout.LayoutParams(bubbleArrowSize, bubbleArrowSize);
                relativeParams1.addRule(RelativeLayout.BELOW, bubbleContainer.getId());
                bubbleArrow.setLayoutParams(relativeParams1);
                relativeLayout.updateViewLayout(bubbleArrow, relativeParams1);
                popupY = targetY - popupHeight - bubbleArrowSize;
                //Save directionY.
                directionY = TOP;
            }
            else
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    bubbleArrow.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bubble_arrow_top));
                }
                else
                {
                    bubbleArrow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bubble_arrow_top));
                }
                //Reset rule to prevent circular dependencies.
                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(bubbleArrowSize, bubbleArrowSize);
                relativeParams.addRule(RelativeLayout.BELOW, 0);
                bubbleArrow.setLayoutParams(relativeParams);
                RelativeLayout.LayoutParams relativeParams1 = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                relativeParams1.addRule(RelativeLayout.BELOW, bubbleArrow.getId());
                bubbleContainer.setLayoutParams(relativeParams1);
                relativeLayout.updateViewLayout(bubbleContainer, relativeParams1);
                popupY = targetY - frame.top + locationView.getHeight();
                //Save directionY.
                directionY = BOTTOM;
            }

            //Calculate horizontal position. Position arrow in middle of target view.
            int arrowX;
            int calcX = targetX + targetWidth - popupWidth;
            int calcWidth = targetWidth;
            //If targetview is wider than popup window, set arrow to middle of popup window.
            if (targetWidth > popupWidth)
            {
                calcWidth = popupWidth;
            }
            if (calcX < 0)
            {
                popupX = 0;
                arrowX = targetX + calcWidth/2 - bubbleArrowSize/2;
                if (directionY == TOP)
                {
                    DIRECTION = BOTTOM_LEFT;
                }
                else
                {
                    DIRECTION = TOP_LEFT;
                }
            }
            else
            {
                popupX = targetX + locationView.getWidth() - popupWidth;
                arrowX = popupWidth - calcWidth/2 - bubbleArrowSize/2;
                if (directionY == TOP)
                {
                    DIRECTION = BOTTOM_RIGHT;
                }
                else
                {
                    DIRECTION = TOP_RIGHT;
                }
            }

            relativeLayout.setX(popupX);
            relativeLayout.setY(popupY);
            bubbleArrow.setX(arrowX);

            windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            windowManager.addView(rootView, params);

            showAnimation(relativeLayout, 0, 1, animDuration, DIRECTION, true);

            //Set focusable to listen to keypresses and handle back button behavior.
            rootView.requestFocus();
            rootView.setFocusable(true);
            rootView.setFocusableInTouchMode(true);
        }
    }

    public void dismissPopupWindow(){
        if(!isAnimating) {
            isAnimating = true;
            isShow = false;
            hideAnimation(relativeLayout, 0.95f, 1, animDuration / 3, DIRECTION, true);
        }
    }

    public WindowManager.LayoutParams getLayoutParams(){
        return params;
    }

    public ViewGroup getLayout(){
        return relativeLayout;
    }

    public boolean isShow(){
        return isShow;
    }

    private void showAnimation(final View view, float start, final float end, int duration, final int direction, final boolean isWhile) {

        ValueAnimator va = ValueAnimator.ofFloat(start, end).setDuration(duration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                int pivotX = 0;
                int pivotY = 0;
                switch (direction)
                {
                    case TOP_LEFT:
                        pivotX = 0;
                        break;
                    case TOP_RIGHT:
                        pivotX = view.getWidth();
                        break;
                    case BOTTOM_LEFT:
                        pivotY = view.getHeight();
                        break;
                    case BOTTOM_RIGHT:
                        pivotX = view.getWidth();
                        pivotY = view.getHeight();
                        break;
                }
                view.setPivotX(pivotX);
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
                    showAnimation(view, end, 0.95f, animDuration / 3, direction, false);
                }else{
                    isAnimating = false;
                }
            }
        });
        va.start();
    }

    public void hideAnimation(final View view, float start, final float end, int duration, final int direction, final boolean isWhile){

        ValueAnimator va = ValueAnimator.ofFloat(start, end).setDuration(duration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                int pivotX = 0;
                int pivotY = 0;
                switch (direction)
                {
                    case TOP_LEFT:
                        pivotX = 0;
                        break;
                    case TOP_RIGHT:
                        pivotX = view.getWidth();
                        break;
                    case BOTTOM_LEFT:
                        pivotY = view.getHeight();
                        break;
                    case BOTTOM_RIGHT:
                        pivotX = view.getWidth();
                        pivotY = view.getHeight();
                        break;
                }
                view.setPivotX(pivotX);
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
                    hideAnimation(view, end, 0f, animDuration, direction, false);
                }else{
                    try {
                        windowManager.removeViewImmediate(rootView);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    isAnimating = false;
                }
            }
        });
        va.start();
    }

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
