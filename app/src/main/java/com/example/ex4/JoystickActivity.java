package com.example.ex4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;

import android.content.Context;
import android.os.Bundle;
import android.graphics.*;
import android.view.*;


public class JoystickActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new JoyStickView(this));
    }

    /**
     * onDestroy
     * destroy - stop the connection
     */
    public void onDestroy() {
        super.onDestroy();
        TcpClient.getInstance().stopClient();
    }

    public class JoyStickView extends View  {
        private float x = 0;
        private float y = 0;
        private final float radius = 100;
        private float startHeight;
        private float endHeight;
        private float startWidth;
        private float endWidth;
        private Boolean mouseMoving = false;
        private RectF oval;
        // the messages for the server
        String setAileron = "set controls/flight/aileron ";
        String setElevator = "set controls/flight/elevator ";


        public JoyStickView(Context v){
            super(v);
        }

        /**
         * onDraw
         * @param canvas
         * draw the oval and the circle inside it (graphics of the joystick)
         */
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            // circle
            Paint circlePaint = new Paint();
            circlePaint.setColor(Color.rgb(109, 128, 127));
            circlePaint.setStrokeWidth(10);
            // oval
            Paint ovalPaint = new Paint();
            ovalPaint.setColor(Color.rgb(245, 173, 137));
            ovalPaint.setStrokeWidth(10);
            // draw the oval and the circle inside
            canvas.drawOval(this.oval, ovalPaint);
            canvas.drawCircle(this.x, this.y, this.radius, circlePaint);
        }

        /**
         * onSizeChanged
         * @param width
         * @param height
         * @param oldWidth
         * @param oldHeight
         * set the proportions of the ovals
         */
        public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
            super.onSizeChanged(width, height, oldWidth, oldHeight);
            this.startWidth = (float)getWidth() / 8;
            this.startHeight = (float)getHeight() / 8;
            this.endWidth = (float)getWidth() - ((float)getWidth() / 8);
            this.endHeight = getHeight() - ((float)getHeight() / 8);
            this.oval = new RectF(this.startWidth, this.startHeight, this.endWidth, this.endHeight);
            setDefaultMiddle();
        }

        /**
         * setDefaultMiddle
         * set the x,y of the middle of the screen
         */
        public void setDefaultMiddle() {
            this.x = (float)getWidth()/2;
            this.y = (float)getHeight()/2;
        }


        /**
         * onTouchEvent
         * @param event
         * @return
         * dealing with mouse movement - down, move and up
         */
        public boolean onTouchEvent(MotionEvent event) {
            int action = MotionEventCompat.getActionMasked(event);
            // get the instance of the tcp client
            TcpClient mTcpClient = TcpClient.getInstance();
            switch (action) {
                // on press
                case MotionEvent.ACTION_DOWN: {
                    // check if the mouse is inside the inner circle
                    if(CheckIfInside(event.getX(), event.getY())) {
                        this.mouseMoving = true;
                    }
                    break;
                }
                // mouse if moving
                case MotionEvent.ACTION_MOVE: {
                    if (!this.mouseMoving)
                        return true;
                    // check if the circle is inside the oval
                    if (CheckForLimit(event.getX(), event.getY())) {
                        this.x = event.getX();
                        this.y = event.getY();
                        invalidate();
                        // normalize the values of the aileron and elevator
                        float aileron = normalizationAileron(this.x);
                        float elevator = normalizationElevator(this.y);
                        // send the aileron and elevator to the server
                        mTcpClient.sendMessage(setAileron + Float.toString(aileron) + "\r\n");
                        mTcpClient.sendMessage(setElevator + Float.toString(elevator) + "\r\n");
                    }
                    break;
                }
                // unpress the mouse
                case MotionEvent.ACTION_UP :
                    this.mouseMoving = false;
                    // goto the middle
                    setDefaultMiddle();
                    invalidate();
            }
            return true;
        }

        /**
         * check if the mouse is inside the inner circle
         * @param valueX
         * @param valueY
         * @return
         */
        Boolean CheckIfInside(float valueX, float valueY) {
            double distance = Math.sqrt((this.x - valueX)*(this.x - valueX) + (this.y - valueY)*(this.y - valueY));
            return (distance <= this.radius);

        }

        /**
         * CheckForLimit
         * @param xVal
         * @param yVal
         * @return
         * check if inside the oval, if it is return true otherwise return false
         */
        Boolean CheckForLimit(float xVal, float yVal) {
            double yCalc = Math.pow(yVal - this.oval.centerY(), 2) / Math.pow(this.oval.height() / 2, 2);
            double xCalc = Math.pow(xVal - this.oval.centerX(), 2) / Math.pow(this.oval.width() / 2, 2);
            xCalc += yCalc;
            if (xCalc <= 1) {
                return true;
            }
            return false;
        }


        /**
         * normalizationElevator
         * @param y
         * @return
         * normalize the y value to be between -1 to 1
         */
        public float normalizationElevator(float y) {
            return (y - ((this.startHeight+this.endHeight) / 2)) / ((this.startHeight-this.endHeight) / 2);
        }

        /**
         * normalizationAileron
         * @param x
         * @return
         * normalize the x value to be between -1 to 1
         */
        public float normalizationAileron(float x) {
            return (x - ((this.startWidth + this.endWidth) / 2)) / ((this.endWidth - this.startWidth) / 2);
        }
    }
}

