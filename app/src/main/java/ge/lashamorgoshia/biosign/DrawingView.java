package ge.lashamorgoshia.biosign;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import java.util.ArrayList;

public class DrawingView extends View {

    private static final String DEBUG_TAG = "Velocity";
    private VelocityTracker mVelocityTracker = null;

    private Paint mPaint;
    public int width;
    public  int height;
    public Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    Context context;
    private Paint circlePaint;
    private Path circlePath;
    public ArrayList<ActionInfo> Actions;

    public DrawingView(Context c) {
        super(c);
        context=c;

        // init mPaint
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        mPath = new Path();
        this.Actions = new ArrayList<>();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath( mPath,  mPaint);
        canvas.drawPath( circlePath,  circlePaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void addAction(String name, float x, float xVelocity, float y, float yVelocity, float pressure, float touchSize) {
        ActionInfo action = new ActionInfo();
        action.Name = name;
        action.NanoTime = System.nanoTime();
        action.X = x;
        action.XVelocity = xVelocity;
        action.Y = y;
        action.YVelocity = yVelocity;
        action.TouchSize = touchSize;
        action.Pressure = pressure;
        this.Actions.add(action);
    }

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;

            circlePath.reset();
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mPath,  mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);
        int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
                MotionEvent.ACTION_POINTER_INDEX_SHIFT;

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                // Start drawing
                touch_start(x, y);
                // Invalidate the whole view
                invalidate();

                if(mVelocityTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                    mVelocityTracker = VelocityTracker.obtain();
                }
                else {
                    // Reset the velocity tracker back to its initial state.
                    mVelocityTracker.clear();
                }
                // Add a user's movement to the tracker.
                mVelocityTracker.addMovement(event);
                Log.d(DEBUG_TAG, "Pressure: " + event.getPressure() + ", size: " + event.getSize());

                // Save action
                addAction("ACTION_DOWN", x, 0, y, 0, event.getPressure(), event.getSize());

                break;
            case MotionEvent.ACTION_MOVE:
                // Continue drawing on touch moving
                touch_move(x, y);
                // Invalidate the whole view
                invalidate();
                // Add touch event into the velocity tracker
                mVelocityTracker.addMovement(event);
                // Compute current velocity
                mVelocityTracker.computeCurrentVelocity(1000);

                // Log velocity of pixels per second best practice to use VelocityTrackerCompat where possible.
                //Log.d(DEBUG_TAG, "X: " + event.getX() + ", X velocity: " + mVelocityTracker.getXVelocity(pointerId));
                //Log.d(DEBUG_TAG, "Y: " + event.getY() + ", Y velocity: " + mVelocityTracker.getYVelocity(pointerId));
                // დაწოლის გაზომვა არ არის საჭირო, ყოველთვის 0-1 -ს აბრუნებს
                //
                Log.d(DEBUG_TAG, "Pressure: " + event.getPressure() + ", size: " + event.getSize() + ", time: " + event.getEventTime());

                // Save action
                addAction("ACTION_MOVE", x, mVelocityTracker.getXVelocity(pointerId), y, mVelocityTracker.getYVelocity(pointerId), event.getPressure(), event.getSize());
                break;
            case MotionEvent.ACTION_UP:
                // Finalize drawing on touch up event
                touch_up();
                // Invalidate the whole view
                invalidate();

                // Add touch event into the velocity tracker
                mVelocityTracker.addMovement(event);
                // Compute current velocity
                mVelocityTracker.computeCurrentVelocity(1000);

                // Save action
                addAction("ACTION_UP", x, mVelocityTracker.getXVelocity(pointerId), y, mVelocityTracker.getYVelocity(pointerId), event.getPressure(), event.getSize());
                break;
        }
        return true;
    }
}