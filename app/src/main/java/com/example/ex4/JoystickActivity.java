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


    public void onDestroy() {
        super.onDestroy();
        TcpClient.getInstance().stopClient();
    }

    public class JoyStickView extends View  {
        private float x = 0;
        private float y = 0;
        private final float radius = 100;
        private float startWid;
        private float endWid;
        private float startHei;
        private float endHei;
        private RectF oval;
        private Boolean playMoving = false;

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
            Paint myPaint = new Paint();
            myPaint.setColor(Color.rgb(109, 128, 127));
            myPaint.setStrokeWidth(10);

            Paint myPaint2 = new Paint();
            myPaint2.setColor(Color.rgb(245, 173, 137));
            myPaint2.setStrokeWidth(10);

            canvas.drawOval(this.oval, myPaint2);
            canvas.drawCircle(this.x, this.y, this.radius, myPaint);

        }
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            this.startWid = (float)getWidth()/8;
            this.endWid = (float)getWidth()-((float)getWidth()/8);
            this.startHei = (float)getHeight()/8;
            this.endHei = getHeight()-((float)getHeight()/8);
            this.oval = new RectF(this.startWid,this.startHei , this.endWid, this.endHei);
            returnDefault();
        }
        public void returnDefault() {
            this.x = (float)getWidth()/2;
            this.y = (float)getHeight()/2;
        }

        public boolean onTouchEvent(MotionEvent event) {
            int action = MotionEventCompat.getActionMasked(event);
            TcpClient mTcpClient = TcpClient.getInstance();
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    if(CheckIfInside(event.getX(), event.getY())) {
                        this.playMoving = true;
                    }
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (!this.playMoving)
                        return true;
                    if (CheckForLimit(event.getX(), event.getY())) {
                        this.x = event.getX();
                        this.y = event.getY();
                        invalidate();
                        float aileron = normelizeAilron(this.x);
                        float elevator = normelizeElevator(this.y);
                        //while(mTcpClient.mBufferIn == null || mTcpClient.mBufferOut == null) { }
                        mTcpClient.sendMessage(setAileron + Float.toString(aileron) + "\r\n");
                        mTcpClient.sendMessage(setElevator + Float.toString(elevator) + "\r\n");
                    }
                    break;
                }
                case MotionEvent.ACTION_UP :
                    this.playMoving = false;
                    returnDefault();
                    invalidate();
            }
            return true;
        }

        Boolean CheckIfInside(float xVal, float yVal) {
            double distance = Math.sqrt((this.x-xVal)*(this.x-xVal) + (this.y-yVal)*(this.y-yVal));
            return (distance <= this.radius);

        }

        Boolean CheckForLimit(float xVal, float yVal) {
            double xCalc = Math.pow(xVal - this.oval.centerX(),2) / Math.pow(this.oval.width() / 2, 2);
            double yCalc = Math.pow(yVal - this.oval.centerY(),2) / Math.pow(this.oval.height() / 2, 2);
            xCalc += yCalc;
            if (xCalc <= 1) {
                return true;
            }
            return false;
        }

        public float normelizeAilron(float x) {
            return (x-((this.startWid+this.endWid)/2))/((this.endWid-this.startWid)/2);
        }

        public float normelizeElevator(float y) {
            return (y-((this.startHei+this.endHei)/2))/((this.startHei-this.endHei)/2);
        }
    }
}

