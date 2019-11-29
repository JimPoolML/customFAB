package appjpm4everyone.jpmfab;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.os.SystemClock;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;


public class MovableFloatingActionButton extends FloatingActionButton implements View.OnTouchListener {

    // Often, there will be a slight, unintentional, drag when the user taps the FAB, so we need to account for this.
    private static final float CLICK_DRAG_TOLERANCE = 40;
    // Avoid multiple clicks
    private static final int TIME_CLICKED = 2000;
    // to show and animate
    private static final int SMOOTH_DURATION = 200;
    private static final int HALF = 2;
    private static final float ADJUST_Y = 2.44f;

    private float downRawX;
    private float downRawY;
    private float dX;
    private float dY;
    private float xLarge;
    private float yLarge;

    //to constructor
    private Context context;
    private boolean onBorder;
    private int spaceFab;

    //Avoid rapid clicks on Button
    private long lastClickTime;

    private CommunicateFab communicateFab;

    public MovableFloatingActionButton(Context context, boolean onBorder, int spaceFab) {
        super(context);
        this.context = context;
        this.onBorder = onBorder;
        this.spaceFab = spaceFab;
        init();
    }

    public MovableFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MovableFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        xLarge = size.x-context.getResources().getDimension(R.dimen.fab_params);
        yLarge = size.y-(ADJUST_Y*spaceFab);
        this.setX(xLarge);
        this.setY(yLarge);
        //Set the callback
        this.communicateFab = (CommunicateFab) context;
        setOnTouchListener(this);



    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        switch (action) {
            // Code for move and drag
            // handle your MotionEvents here
            case MotionEvent.ACTION_DOWN:
                downRawX = motionEvent.getRawX();
                downRawY = motionEvent.getRawY();

                dX = view.getX() - downRawX;
                dY = view.getY() - downRawY;

                return true; // Consumed

            case MotionEvent.ACTION_MOVE:
                int viewWidth = view.getWidth();
                int viewHeight = view.getHeight();

                View viewParent = (View) view.getParent();
                int parentWidth = viewParent.getWidth();
                int parentHeight = viewParent.getHeight();

                float newX = motionEvent.getRawX() + dX;
                newX = Math.max(0, newX); // Don't allow the FAB past the left hand side of the parent
                newX = Math.min(parentWidth - viewWidth, newX); // Don't allow the FAB past the right hand side of the parent

                float newY = motionEvent.getRawY() + dY;
                newY = Math.max(0, newY); // Don't allow the FAB past the top of the parent
                newY = Math.min(parentHeight - viewHeight, newY); // Don't allow the FAB past the bottom of the parent

                //set duration in zero to show immediately
                viewAnimated(view, newX, newY, 0);
                return true; // Consumed

            case MotionEvent.ACTION_UP:
                float upRawX = motionEvent.getRawX();
                float upRawY = motionEvent.getRawY();

                float upDX = upRawX - downRawX;
                float upDY = upRawY - downRawY;

                if (Math.abs(upDX) < CLICK_DRAG_TOLERANCE && Math.abs(upDY) < CLICK_DRAG_TOLERANCE) { // A click
                    // code for single tap or onclick
                    preventMultipleClicks();
                    communicateFab.onClickFab(motionEvent.getRawX(), motionEvent.getRawY());
                    Log.d("Position", "Xpos : " + motionEvent.getRawX() + " Ypos : " + motionEvent.getRawY());
                    return performClick();
                } else {
                    // A drag
                    //set duration to show smoothly
                    if(onBorder) {
                        viewAnimated(view, xBorder(view.getX()), view.getY(), SMOOTH_DURATION);
                    }
                    return true; // Consumed
                }

            default:
                //No implementation needed
                return true;
        }//Final Switch

    }

    private void viewAnimated(View view, float xPos, float yPos, int duration) {
        view.animate()
                .x(xPos)
                .y(yPos)
                .setDuration(duration)
                .start();
    }

    private float xBorder(float dx) {
        float absoluteX;
        if (dx >= xLarge / HALF) {
            absoluteX = xLarge;
        } else {
            absoluteX = 0;
        }
        return absoluteX;
    }

    private void preventMultipleClicks() {
        // Preventing multiple clicks
        if (SystemClock.elapsedRealtime() - lastClickTime < TIME_CLICKED) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();
    }

    public boolean show(boolean b) {
        return false;
    }

    public void setResources(int imageResource, ImageView.ScaleType center, ColorStateList color, float elevation) {
        this.setImageResource(imageResource);
        this.setScaleType(center);
        this.setBackgroundTintList(color);
        this.setElevation(elevation);
    }
}
