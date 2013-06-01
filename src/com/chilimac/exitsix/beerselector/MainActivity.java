package com.chilimac.exitsix.beerselector;
 
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

import com.google.analytics.tracking.android.EasyTracker;

/**
 *  * 
 * @author Jason McKee
 * @version 2.0
 * 
 *
 */
public class MainActivity extends Activity {
	
	private static Bitmap imageOriginal, imageScaled;
	private static Matrix matrix;
 
	private ImageView dialView;
	private int dialHeight, dialWidth;
	 
	private GestureDetector detector; 
  
	private boolean allowRotating;
	double m_Angle = 0;
	
	static final int MIN_DISTANCE = 20;
	private static final String TAG = "DEBUG_LOG";
	ArrayList<String> beerList = new ArrayList<String>();
	    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeEverybody();
        configureView();
        EasyTracker.getInstance().activityStart(this);         
        startService(new Intent(this,DataFeedService.class));
    
    }

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}
		

	
	private void configureView() {
        
		dialView = (ImageView) findViewById(R.id.imageView_spinner);
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
        	imageOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.spin);
        }
        
        if (matrix == null) {
        	matrix = new Matrix();
        } else {
        	// not needed, you can also post the matrix immediately to restore the old state
        	matrix.reset();
        }

        detector = new GestureDetector(this, new MyGestureDetector()); 
        
        allowRotating = true;

	}
	
	
	public void listBeers(View view)
	{
		EasyTracker.getTracker().sendEvent("ui_action", "button_press", "list_beers", (long) 1);
		Intent intent = new Intent(this,ViewListActivity.class);
		startActivity(intent);
	}

	/**
	 * Rotate the dialer.
	 * 
	 * @param degrees The degrees, the dialer should get rotated.
	 */
	private void rotateDialer(float degrees) {
		m_Angle += degrees;
		matrix.postRotate(degrees, dialWidth / 2, dialHeight / 2);
		
		dialView.setImageMatrix(matrix);
	}
	
	/**
	 * @return The angle of the unit circle with the image view's center
	 */
	private double getAngle(double xTouch, double yTouch) {
		double x = xTouch - (dialWidth / 2d);
		double y = dialHeight - yTouch - (dialHeight / 2d);

		return Math.atan2(y, x);
	}
	
	
	/**
	 * Simple implementation of an {@link OnTouchListener} for registering the dialer's touch events. 
	 */
	private class MyOnTouchListener implements OnTouchListener {
		
		private double startAngle;

		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction()) {
				
				case MotionEvent.ACTION_DOWN:
								
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

            float deltaX = e1.getX() - e2.getX();
            float deltaY = e1.getY() - e2.getY();            
            
            // horizontal
            if(Math.abs(deltaX) > MIN_DISTANCE){
               // left or right
                if(deltaX < 0) { dialView.post(new FlingRunnable(velocityX + velocityY));}  // left to right
                if(deltaX > 0) { dialView.post(new FlingRunnable(-1 * (velocityX + velocityY)));}  // right to left
            }

            // vertical
            else if(Math.abs(deltaY) > MIN_DISTANCE){
                // top or down
                if(deltaY < 0) { dialView.post(new FlingRunnable(velocityX + velocityY));}  // top to bottom
                if(deltaY > 0) { dialView.post(new FlingRunnable(-1 * (velocityX + velocityY)));}  // bottom to top
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
				velocity /= 1.125F;

				// post this instance again
				dialView.post(this);
			}
			else{
				int slotNumber = GetSlot();
				
				if(beerList.isEmpty())
				{
					Log.d(TAG,"List is empty.");
				}
				else
				{
				// Taking this out for now
				// The beer list on taplister is not in order so I can't map it to the numbers.
						//	TextView result = (TextView) findViewById(R.id.resultView);			
						//	
						//	result.setText(strSlotNumber);
					String strSlotNumber = beerList.get(slotNumber-1);
					strSlotNumber += " : Final Value";
					Log.d("Angle",strSlotNumber );
				}
				
			}
		}
		
		private int GetSlot()
		{
			float angleNormalize =  (float) normalize(m_Angle);
			String strNormal = String.valueOf(angleNormalize);
			strNormal += " : Normalized Number";
			Log.d("Angle",strNormal );
			
			int slices = 23;
			float offset = slices * angleNormalize / 360;
			
			int slot = (int)offset + 1;
			if (slot > 23)
			{
				slot -= 23;
			}
			
			String strSlotNumber = String.valueOf(slot);
			strSlotNumber += " : Slot Number";
			Log.d("Angle",strSlotNumber );
			
			return slot; 
		}
		
		private double normalize(double angle) 
		{ 
			double normalAngle = angle * -1;
			normalAngle = normalAngle % 360;

			if ((normalAngle < 0))
			{
				normalAngle += 360;
			}
			return normalAngle;
		} 
	}
}