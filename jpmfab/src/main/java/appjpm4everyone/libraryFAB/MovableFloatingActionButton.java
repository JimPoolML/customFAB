package appjpm4everyone.libraryFAB;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.ContentFrameLayout;

import java.util.Objects;


public class MovableFloatingActionButton extends FloatingActionButton implements View.OnTouchListener {

    // Often, there will be a slight, unintentional, drag when the user taps the FAB, so we need to account for this.
    private static final float CLICK_DRAG_TOLERANCE = 7;
    // Avoid multiple clicks
    private static final int TIME_CLICKED = 2000;
    // to show and animate
    private static final int SMOOTH_DURATION = 200;
    private static final int HALF = 2;
    private static final float ADJUST_Y = 2.44f;

    //Avoid null in set Resources
    private static final float ELEVATION = 1.0f;

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

    //The callBack
    private CommunicateFab communicateFab;

    public MovableFloatingActionButton(Context context, boolean onBorder, Integer spaceFab) {
        super(context);
        this.context = context;
        this.onBorder = onBorder;
        if(spaceFab == null){
            this.spaceFab = (int) getResources().getDimension(R.dimen.fab_params);
        }else {
            this.spaceFab = spaceFab;
        }
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
        Display display = Objects.requireNonNull(wm).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        xLarge = size.x-spaceFab;
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
                    preventMultipleClicks(view);
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

    private void preventMultipleClicks(final View view) {
        // Preventing multiple clicks
        view.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, TIME_CLICKED);
    }

    public void openFAB(Activity activity, Integer imageResource, ImageView.ScaleType center, ColorStateList color, Float elevation) {
        ContentFrameLayout rootLayout = activity.findViewById(android.R.id.content);
        if (rootLayout.findViewById(com.google.android.material.R.id.image) == null) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(spaceFab, spaceFab);
            setResources(imageResource, center, color, elevation);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            rootLayout.addView(this, params);
        }
        rootLayout.bringChildToFront(this);
        this.requestFocus();
    }

    public void setResources(Integer imageResource, ImageView.ScaleType center, ColorStateList color, Float elevation) {
        //Set imageResource
        if(imageResource == null){
            //This image you can find in "https://snipstock.com/image/radioactive-png-images-radiation-pngs-2-png-82140"
            this.setImageResource(R.drawable.radioactive_free);
        }else{
            this.setImageResource(imageResource);
        }
        //Set scaleType
        if(center == null){
            this.setScaleType(ImageView.ScaleType.CENTER);
        }else{
            this.setScaleType(center);
        }
        //Set backgroundTintList
        if(color == null){
            this.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        }else{
            this.setBackgroundTintList(color);
        }
        //Set elevation
        if(elevation == null){
            this.setElevation(ELEVATION);
        }else{
            this.setElevation(elevation);
        }
    }

}
