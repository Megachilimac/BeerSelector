package com.chilimac.exitsix.beerselector;
 
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

/**
 *  * 
 * @author Jason McKee
 * @version 1.0
 * 
 * adapted from Ralf Wondratschek's dialer tutorial
 *
 */
public class SelectorActivity extends Activity {
	
	private static Bitmap imageOriginal, imageScaled;
	private static Matrix matrix;
 
	private ImageView dialView;
	private int dialHeight, dialWidth;
	 
	private GestureDetector detector; 
	
	// needed for detecting the inversed rotations
	private boolean[] quadrantTouched;
 
	private boolean allowRotating;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        initializeEverybody();
        configureView();
        
    }

	private void configureView() {
        
		dialView = (ImageView) findViewById(R.id.imageView_ring);
		dialView.setOnTouchListener(new MyOnTouchListener());
        
        dialView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

        	public void onGlobalLayout() {
        		// method called more than once, but the values only need to be initialized one time
        		if (dialHeight == 0 || dialWidth == 0) {
        			dialHeight = dialView.getHeight();
        			dialWidth = dialView.getWidth();

        			// resize
					Matrix resize = new Matrix();
					resize.postScale((float)Math.min(dialWidth, dialHeight) / (float)imageOriginal.getWidth(), (float)Math.min(dialWidth, dialHeight) / (float)imageOriginal.getHeight());
					imageScaled = Bitmap.createBitmap(imageOriginal, 0, 0, imageOriginal.getWidth(), imageOriginal.getHeight(), resize, false);
					
					// translate to the image view's center
					float translateX = dialWidth / 2 - imageScaled.getWidth() / 2;
					float translateY = dialHeight / 2 - imageScaled.getHeight() / 2;
					matrix.postTranslate(translateX, translateY);
					
					dialView.setImageBitmap(imageScaled);
					dialView.setImageMatrix(matrix);
        		}
			} 


		});
	}

	private void initializeEverybody() {

        if (imageOriginal == null) {
        	imageOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.wheel);
        }
        
        if (matrix == null) {
        	matrix = new Matrix();
        } else {
        	// not needed, you can also post the matrix immediately to restore the old state
        	matrix.reset();
        }

        detector = new GestureDetector(this, new MyGestureDetector()); 
        
        // there is no 0th quadrant, to keep it simple the first value gets ignored
        quadrantTouched = new boolean[] { false, false, false, false, false };
        
        allowRotating = true;

	}
	

	/**
	 * Rotate the dialer.
	 * 
	 * @param degrees The degrees, the dialer should get rotated.
	 */
	private void rotateDialer(float degrees) {
		matrix.postRotate(degrees, dialWidth / 2, dialHeight / 2);
		
		dialView.setImageMatrix(matrix);
	}
	
	/**
	 * @return The angle of the unit circle with the image view's center
	 */
	private double getAngle(double xTouch, double yTouch) {
		double x = xTouch - (dialWidth / 2d);
		double y = dialHeight - yTouch - (dialHeight / 2d);

		switch (getQuadrant(x, y)) {
			case 1:
				return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
			
			case 2:
			case 3:
				return 180 - (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
			
			case 4:
				return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
			
			default:
				// ignore, does not happen
				return 0;
		}
	}
	
	/**
	 * @return The selected quadrant.
	 */
	private static int getQuadrant(double x, double y) {
		if (x >= 0) {
			return y >= 0 ? 1 : 4;
		} else {
			return y >= 0 ? 2 : 3;
		}
	}
	
	/**
	 * Simple implementation of an {@link OnTouchListener} for registering the dialer's touch events. 
	 */
	private class MyOnTouchListener implements OnTouchListener {
		
		private double startAngle;

		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction()) {
				
				case MotionEvent.ACTION_DOWN:
					
					// reset the touched quadrants
					for (int i = 0; i < quadrantTouched.length; i++) {
						quadrantTouched[i] = false;
					}
					
					allowRotating = false;
					
					startAngle = getAngle(event.getX(), event.getY());
					break;
					
				case MotionEvent.ACTION_MOVE:
					double currentAngle = getAngle(event.getX(), event.getY());
					rotateDialer((float) (startAngle - currentAngle));
					startAngle = currentAngle;
					break;
					
				case MotionEvent.ACTION_UP:
					allowRotating = true;
					break;
			}
			
			// set the touched quadrant to true
			quadrantTouched[getQuadrant(event.getX() - (dialWidth / 2), dialHeight - event.getY() - (dialHeight / 2))] = true;
			
			detector.onTouchEvent(event);
			
			return true;
		}
	}
	
	/**
	 * Simple implementation of a {@link SimpleOnGestureListener} for detecting a fling event. 
	 */
	private class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			
			// get the quadrant of the start and the end of the fling
			int q1 = getQuadrant(e1.getX() - (dialWidth / 2), dialHeight - e1.getY() - (dialHeight / 2));
			int q2 = getQuadrant(e2.getX() - (dialWidth / 2), dialHeight - e2.getY() - (dialHeight / 2));

			// the inversed rotations
			if ((q1 == 2 && q2 == 2 && Math.abs(velocityX) < Math.abs(velocityY))
					|| (q1 == 3 && q2 == 3)
					|| (q1 == 1 && q2 == 3)
					|| (q1 == 4 && q2 == 4 && Math.abs(velocityX) > Math.abs(velocityY))
					|| ((q1 == 2 && q2 == 3) || (q1 == 3 && q2 == 2))
					|| ((q1 == 3 && q2 == 4) || (q1 == 4 && q2 == 3))
					|| (q1 == 2 && q2 == 4 && quadrantTouched[3])
					|| (q1 == 4 && q2 == 2 && quadrantTouched[3])) {
			
				dialView.post(new FlingRunnable(-1 * (velocityX + velocityY)));
			} else {
				// the normal rotation
				dialView.post(new FlingRunnable(velocityX + velocityY));
			}

			return true;
		}
	}
	
	/**
	 * A {@link Runnable} for animating the the dialer's fling.
	 */
	private class FlingRunnable implements Runnable {

		private float velocity;

		public FlingRunnable(float velocity) {
			this.velocity = velocity;
		}

		public void run() {
			if (Math.abs(velocity) > 5 && allowRotating) {
				rotateDialer(velocity / 75);
				velocity /= 1.0666F;

				// post this instance again
				dialView.post(this);
			}
		}
	}
}