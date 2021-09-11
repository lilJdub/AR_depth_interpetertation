package com.google.ar.core.examples.java.helloar;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.examples.java.common.helpers.Point;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class OpenGLdemo extends Activity {

    private GraphicView graphicView;
    private float vertex[] = {
            30f,30f,30f,
            40f,50f,65f,
    };

    private DatabaseReference rootRef;
    private DatabaseReference childRef;

    private ValueEventListener valueEventListener;
    private static final String TAG ="reading save...";
    private TextView tv;
    //firebase
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //XYZ資料
    private  String stringArray[] = new String[3000];
    private float floatArray[] = new float[3000];
    Double X;
    Double Y;
    Double Z;

    private float dataXYZ[];

    //暫存
    private ArrayList<Point> points;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setContentView( R.layout.activity_main);
        setContentView( R.layout.activity_open_gldemo);

        graphicView = new GraphicView(this);



        onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_point_from_db);
        rootRef= FirebaseDatabase.getInstance().getReference();
        childRef=rootRef.child("arcore-f3cb4-default-rtdb");
        tv=findViewById(R.id.textViewSeeDB);
        points=new ArrayList<Point>();
        //Authentication
        mAuth= FirebaseAuth.getInstance();
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=mAuth.getCurrentUser();
                if(user!=null){
                    Log.d(TAG, "onAuthStateChanged:signed_in");
                }
                else{
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        //重點:用Valuelistener監聽資料庫狀態，再用datasnapshot(資料快照)抓下資料
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {



                int i = 0;
                for(DataSnapshot ds:snapshot.getChildren()){
                    X=ds.child("x").getValue(Double.class);
                    //   tv.append("X"+X+"\n");
                    Y=ds.child("y").getValue(Double.class);
                    //   tv.append("Y"+Y+"\n");
                    Z=ds.child("z").getValue(Double.class);
                    //   tv.append("Z"+Z+"\n");
                    stringArray[i]   = String.valueOf(X) ;
                    stringArray[i+1] = String.valueOf(Y) ;
                    stringArray[i+2] = String.valueOf(Z) ;

                    floatArray[i] = Float.parseFloat(stringArray[i]);
                    floatArray[i+1] = Float.parseFloat(stringArray[i+1]);
                    floatArray[i+2] = Float.parseFloat(stringArray[i+2]);

                    i = i+3;
                }


            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };
        rootRef.addListenerForSingleValueEvent(valueEventListener);




    }


    @Override
    protected void onResume() {
        super.onResume();
        if (graphicView != null) {
            graphicView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        graphicView.onPause();
        super.onDestroy();
    }
    public class CustomObject implements Serializable {
        private static final long serialVersionUID = -7060210544600464481L;

    }

    public class GraphicView extends  GLSurfaceView implements GLSurfaceView.Renderer {

        private float mAngCtr = 0; //for animation
        long mLastTime = SystemClock.elapsedRealtime();

        //for touch event - dragging
        float mDragStartX = -1;
        float mDragStartY = -1;
        float mDownX = -1;
        float mDownY = -1;
        //we add the .0001 to avoid divide by 0 errors
        //starting camera angles
        float mCamXang = 0.0001f;
        float mCamYang = 180.0001f;
        //starting camera position
        float mCamXpos = 0.0001f;
        float mCamYpos = 60.0001f;
        float mCamZpos = 180.0001f;
        //distance from camera to view target
        float mViewRad = 100;
        //target values will get set in constructor
        float mTargetY = 0;
        float mTargetX = 0;
        float mTargetZ = 0;
        //scene angles will get set in constructor
        float mSceneXAng = 0.0001f;
        float mSceneYAng = 0.0001f;

        float mScrHeight = 0; //screen height
        float mScrWidth  = 0; //screen width
        float mScrRatio  = 0; //width/height
        float mClipStart = 1; //start of clip region

        final double mDeg2Rad = Math.PI / 180.0; //Degrees To Radians
        final double mRad2Deg = 180.0 / Math.PI; //Radians To Degrees

        boolean mResetMatrix = false; //set to true when camera moves

        int[] mFrameTime = new int[20]; //frames used for avg fps
        int mFramePos = 0; //current fps frame position
        long mStartTime = SystemClock.elapsedRealtime(); //for fps
        int mFPSDispCtr = 0; //fps display interval
        float mFPS = 0; //actual fps value

        TextView mTxtMsg = null; //for displaying FPS
        final GraphicView mTagStore = this; //for SetTextMessage
        Handler mThreadHandler = new Handler(); //used in SetTextMessage

        //constants for scene objects in GPU buffer
        final int mPOINT = 1;

        //need to store length of each vertex buffer
        int[] mBufferLen = new int[] {0,0,0,0,0,0,0}; //0/Floor/Ball/Pool/Wall/Drop/Splash
        EGLDisplay mDisplay = null;
        EGLSurface mBufferSurface = null;
        EGLSurface mCurSurface = null;
        boolean mSurfaceToggle = true;

        //fountain parameters
        int mStreamCnt = 10; //should divide evenly into 360
        int mDropsPerStream = 30; //should divide evenly into 180
        int mRepeatLen = 180/mDropsPerStream; //distance between drops
        float mArcRad = 30; //stream arc radius
        //for storing drop positions //3 floats per vertex [x/y/z]
        float[][] dropCoords = new float[mStreamCnt*mDropsPerStream][3];

        //accelerometer value set by activity
        public float AccelZ = 0;
        public float AccelY = 0;
        int mOrientation = 0; //portrait\landscape

        //options menu defaults
        public boolean RotateScene = true;
        public boolean UseTiltAngle = false;
        public boolean ShowFPS = true;
        public boolean Paused = false;

        //add
        public boolean ShowPoint = true;

        public GraphicView(Activity pActivity)
        {
            super(pActivity);

            //use FrameLayout so we can put a TextView on top of the openGL screen
            FrameLayout layout = new FrameLayout(pActivity);

            //create view for text message (fps)
            mTxtMsg = new TextView(layout.getContext());
            mTxtMsg.setBackgroundColor(0x00FFFFFF); //transparent
            mTxtMsg.setTextColor(0xFF777777); //gray

            layout.addView(this); //add openGL surface
            layout.addView(mTxtMsg); //add text view
            pActivity.setContentView(layout);
            setRenderer(this); //initialize surface view

            //create listener for accelerometer sensor
            ((SensorManager)pActivity.getSystemService(Context.SENSOR_SERVICE)).registerListener(
                    new SensorEventListener() {
                        @Override
                        public void onSensorChanged(SensorEvent event) {
                            //accelerometer does not change orientation so need to switch sensors
                            if (mOrientation == Configuration.ORIENTATION_PORTRAIT)
                                AccelY = event.values[1]; //use Y sensor
                            else
                                AccelY = event.values[0]; //use X sensor
                            AccelZ = event.values[2]; //Z
                        }
                        @Override
                        public void onAccuracyChanged(Sensor sensor, int accuracy) {} //ignore this event
                    },
                    ((SensorManager)pActivity.getSystemService(Context.SENSOR_SERVICE))
                            .getSensorList(Sensor.TYPE_ACCELEROMETER).get(0),SensorManager.SENSOR_DELAY_NORMAL);
        }

        public GraphicView(Context context) {
            super(context);
        }

        public GraphicView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        //called once
        @Override
        public void onSurfaceCreated(GL10 gl1, EGLConfig pConfig)
        {
            float vtx[]=vertex;
//            Point receivedData = null;
//            receivedData = (Point) getIntent().getSerializableExtra("DataTest ");

            //every POINT has the same coordinates
            /*float vtx[] = {
                    // X,  Y, Z
                    15f, 15f, 20f,
                    10f,20f, 30f,
                    -10f,-20f, 10f,
                    20f,20f,20f,
                    60f,20f,30f,
            };*/


            GL11 gl = (GL11)gl1; //we need 1.1 functionality
            //set background frame color
            gl.glClearColor(0f, 0f, 0f, 1.0f); //black
            //generate vertex arrays for scene objects
            StoreVertexData(gl,floatArray,mPOINT);
            //BuildPoint(gl,vtx);
        }

    /*void BuildPoint(GL11 gl,float vertex[]){
        StoreVertexData(gl, vertex, mPOINT); //store in GPU buffer
    }*/




        void StoreVertexData(GL11 gl, float[] pVertices, int pObjectNum)
        {
            FloatBuffer buffer = ByteBuffer.allocateDirect(pVertices.length * 4) //float is 4 bytes
                    .order(ByteOrder.nativeOrder())// use the device hardware's native byte order
                    .asFloatBuffer()  // create a floating point buffer from the ByteBuffer
                    .put(pVertices);	// add the coordinates to the FloatBuffer

            (gl).glBindBuffer(GL11.GL_ARRAY_BUFFER, pObjectNum); //bind as current object
            buffer.position(0);
            //allocate memory and write buffer data
            (gl).glBufferData(GL11.GL_ARRAY_BUFFER, buffer.capacity()*4, buffer, GL11.GL_STATIC_DRAW);
            (gl).glBindBuffer(GL11.GL_ARRAY_BUFFER, 0); //unbind from buffer
            mBufferLen[pObjectNum] = buffer.capacity()/3; //store for drawing
        }

        //this is called when the user changes phone orientation (portrait\landscape)
        @Override
        public void onSurfaceChanged(GL10 gl, int pWidth, int pHeight)
        {
            gl.glViewport(0, 0, pWidth, pHeight); //the viewport is the screen
            // make adjustments for screen ratio, default would be stretched square
            mScrHeight = pHeight;
            mScrWidth = pWidth;
            mScrRatio = mScrWidth/mScrHeight;

            //set to projection mode to set up Frustum
            gl.glMatrixMode(GL11.GL_PROJECTION);		// set matrix to projection mode
            gl.glLoadIdentity();						// reset the matrix to its default state
            //calculate the clip region to minimize the depth buffer range (more precise)
            float camDist = (float)Math.sqrt(mCamXpos*mCamXpos+mCamYpos*mCamYpos+mCamZpos*mCamZpos);
            mClipStart = Math.max(2, camDist-185); //max scene radius is 185 points at corners
            //set up the perspective pyramid and clip points
            gl.glFrustumf(
                    -mScrRatio*.5f*mClipStart,
                    mScrRatio*.5f*mClipStart,
                    -1f*.5f*mClipStart,
                    1f*.5f*mClipStart,
                    mClipStart,
                    mClipStart+185+Math.min(185, camDist));

            //foreground objects are bigger and hide background objects
            gl.glEnable(GL11.GL_DEPTH_TEST);

            //set to ModelView mode to set up objects
            gl.glMatrixMode(GL11.GL_MODELVIEW);
            mOrientation = getResources().getConfiguration().orientation;
        }

        //this is called continuously
        @Override
        public void onDrawFrame(GL10 gl1)
        {
            GL11 gl = (GL11)gl1; //we need 1.1 functionality
            if (mResetMatrix) //camera distance changed
            {
                //recalc projection matrix and clip region
                onSurfaceChanged(gl, (int)mScrWidth, (int)mScrHeight);
                mResetMatrix = false;
            }

            //reset color and depth buffer
            gl.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            gl.glLoadIdentity();   //reset the matrix to its default state

            if (UseTiltAngle) //use phone tilt to determine X axis angle
            {
                //float hyp = (float)Math.sqrt(AccelY*AccelY+AccelZ*AccelZ);
                if (RotateScene) //rotate camera around 0,0,0
                {
                    //calculate new X angle
                    float HypLen = (float)Math.sqrt(mCamXpos*mCamXpos+mCamZpos*mCamZpos); //across floor
                    mSceneXAng = 90-(float)Math.atan2(AccelY,AccelZ)*(float)mRad2Deg;
                    // stop at 90 degrees or scene will go upside down
                    if (mSceneXAng > 89.9) mSceneXAng = 89.9f;
                    if (mSceneXAng < -89.9) mSceneXAng = -89.9f;

                    float HypZLen = (float)Math.sqrt(mCamXpos*mCamXpos+mCamYpos*mCamYpos+mCamZpos*mCamZpos); //across floor
                    //HypZLen stays same with new angle
                    //move camera to match angle
                    mCamYpos = HypZLen*(float)Math.sin(mSceneXAng*mDeg2Rad);
                    float HypLenNew = HypZLen*(float)Math.cos(mSceneXAng*mDeg2Rad); //across floor
                    mCamZpos *= HypLenNew/HypLen;
                    mCamXpos *= HypLenNew/HypLen;
                }
                else //rotate camera
                {
                    mCamXang = (float)Math.atan2(AccelY,AccelZ)*(float)mRad2Deg - 90;
                    //don't let scene go upside down
                    if (mCamXang > 89.9) mCamXang = 89.9f;
                    if (mCamXang < -89.9) mCamXang = -89.9f;
                    ChangeCameraAngle(0, 0); //set target position
                }
            }

            //gluLookAt tells openGL the camera position and view direction (target)
            //target is 0,0,0 for scene rotate
            //Y is up vector, so we set it to 100 (can be any positive number)
            GLU.gluLookAt(gl, mCamXpos, mCamYpos, mCamZpos, mTargetX, mTargetY, mTargetZ, 0f, 100.0f, 0.0f);


            //use clock to adjust animation angle for smoother motion
            //if frame takes longer, angle is greater and we catch up
            long now = SystemClock.elapsedRealtime();
            long diff = now - mLastTime;
            mLastTime = now;

            //if paused, animation angle does not change
            if (!Paused)
            {
                mAngCtr += diff/100.0;
                if (mAngCtr > 360) mAngCtr -= 360;
            }

            DrawObject(gl, GLES20.GL_POINTS,mPOINT,0f,0f,1f);

            if (ShowFPS) //average fps across last 20 frames
            {
                //elapsedRealtime() returns milliseconds since phone boot
                int thisFrameTime = (int)(SystemClock.elapsedRealtime()-mStartTime);
                //mFrameTime array stores times for last 20 frames
                mFPS = (mFrameTime.length)*1000f/(thisFrameTime-mFrameTime[mFramePos]);
                mFrameTime[mFramePos] = (int)(SystemClock.elapsedRealtime()-mStartTime);
                if (mFramePos < mFrameTime.length-1) //move pointer
                    mFramePos++;
                else //end of array, jump to start
                    mFramePos=0;
                if (++mFPSDispCtr == 10) //update fps display every 10 frames
                {
                    mFPSDispCtr=0;
                    SetStatusMsg(Math.round(mFPS*100)/100f+" fps"); //2 decimal places
                }
            }

        }

        void DrawObject(GL11 gl, int pShapeType, int pObjNum,float r,float g,float b)
        {
            //add 繪製點
            gl.glPushMatrix();
            gl.glColor4f(r, g, b, 1);
            //POINT SIZE : 用PIXEL定義該點大小
            gl.glPointSize(50);
            //activate vertex array type
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            //get vertices for this object id
            gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, pObjNum);
            //each vertex is made up of 3 floats [x\y\z]
            gl.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
            //draw points
            gl.glDrawArrays(pShapeType, 0, mBufferLen[pObjNum]);
            //unbind from memory
            gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);


            //add
            gl.glPopMatrix();
        }

        public void SetStatusMsg(String pMsg)
        {
            //mTagStore = this. We just need an object to pass text to the anonymous method
            mTagStore.setTag(pMsg);
            mThreadHandler.post(new Runnable() {
                public void run() {	mTxtMsg.setText(mTagStore.getTag().toString()); }
            });
        }


        //rotate camera around fountain
        void ChangeSceneAngle(float pChgXang, float pChgYang)
        {
            //hypotenuse using 2 dimensions
            float hypLen = (float)Math.sqrt(mCamXpos*mCamXpos+mCamZpos*mCamZpos); //across floor
            //process X and Y angles separately
            if (pChgYang != 0)
            {
                mSceneYAng += pChgYang;
                if (mSceneYAng < 0) mSceneYAng += 360;
                if (mSceneYAng > 360) mSceneYAng -= 360;
                //move camera according to new Y angle
                mCamXpos = hypLen*(float)Math.sin(mSceneYAng*mDeg2Rad);
                mCamZpos = hypLen*(float)Math.cos(mSceneYAng*mDeg2Rad);
            }

            if (pChgXang != 0)
            {
                //hypotenuse using all 3 dimensions
                float hypZLen = (float)Math.sqrt(hypLen*hypLen+mCamYpos*mCamYpos); // 0,0,0 to camera
                mSceneXAng += pChgXang;
                if (mSceneXAng > 89.9) mSceneXAng = 89.9f;
                if (mSceneXAng < -89.9) mSceneXAng = -89.9f;
                //hypZLen stays same with new angle
                //move camera according to new X angle
                mCamYpos = hypZLen*(float)Math.sin(mSceneXAng*mDeg2Rad);
                float HypLenNew = hypZLen*(float)Math.cos(mSceneXAng*mDeg2Rad); //across floor
                mCamZpos *= HypLenNew/hypLen;
                mCamXpos *= HypLenNew/hypLen;
            }

        }

        //change camera view direction
        void ChangeCameraAngle(float pChgXang, float pChgYang)
        {
            mCamXang += pChgXang;
            mCamYang += pChgYang;
            //keep angle within 360 degrees
            if (mCamYang > 360) mCamYang -= 360;
            if (mCamYang < 0) mCamYang += 360;
            //don't let view go upside down
            if (mCamXang > 89.9) mCamXang = 89.9f;
            if (mCamXang < -89.9) mCamXang = -89.9f;
            // move view target according to new angles
            mTargetY = mCamYpos+mViewRad*(float)Math.sin(mCamXang * mDeg2Rad);
            mTargetX = mCamXpos+mViewRad*(float)Math.cos(mCamXang * mDeg2Rad)*(float)Math.sin(mCamYang * mDeg2Rad);
            mTargetZ = mCamZpos+mViewRad*(float)Math.cos(mCamXang * mDeg2Rad)*(float)Math.cos(mCamYang * mDeg2Rad);
        }

        void MoveCamera(float pDist)
        {
            //move camera along line of sight toward target vertex
            if (RotateScene) //move towards\away from 0,0,0
            {
                //distance from 0,0,0
                float curdist = (float)Math.sqrt(
                        mCamXpos*mCamXpos +
                                mCamYpos*mCamYpos +
                                mCamZpos*mCamZpos);
                //if camera will pass center than reduce distance
                if (pDist<0 && curdist + pDist < 0.01) //can't go to exact center
                    pDist = 0.01f-curdist;//0.01 closest distance
                float ratio = pDist/curdist;
                float chgCamX = (mCamXpos)*ratio;
                float chgCamY = (mCamYpos)*ratio;
                float chgCamZ = (mCamZpos)*ratio;
                mCamXpos += chgCamX;
                mCamYpos += chgCamY;
                mCamZpos += chgCamZ;
            }
            else //move towards\away from target
            {
                //mViewRad is 100, so do percentage
                float ratio = pDist/mViewRad;
                float chgCamX = (mCamXpos-mTargetX)*ratio;
                float chgCamY = (mCamYpos-mTargetY)*ratio;
                float chgCamZ = (mCamZpos-mTargetZ)*ratio;
                mCamXpos += chgCamX;
                mCamYpos += chgCamY;
                mCamZpos += chgCamZ;
                mTargetX += chgCamX;
                mTargetY += chgCamY;
                mTargetZ += chgCamZ;
            }

            mResetMatrix = true; //recalc depth buffer range
        }

        public boolean onTouchEvent(final MotionEvent pEvent)
        {
            if (pEvent.getAction() == MotionEvent.ACTION_DOWN) //start drag
            {
                //store start position
                mDragStartX = pEvent.getX();
                mDragStartY = pEvent.getY();
                mDownX = pEvent.getX();
                mDownY = pEvent.getY();
                return true; //must have this
            }
            else if (pEvent.getAction() == MotionEvent.ACTION_UP) //drag stop
            {
                //if user did not move more than 5 pixels, assume screen tap
                if ((Math.abs(mDownX - pEvent.getX()) <= 5) && (Math.abs(mDownY - pEvent.getY()) <= 5))
                {
                    if (pEvent.getY() < mScrHeight/2.0) //top half of screen
                        MoveCamera(-5); //move camera forward
                    else if (pEvent.getY() > mScrHeight/2.0) //bottom half of screen
                        MoveCamera(5); //move camera back
                }
                return true; //must have this
            }
            else if (pEvent.getAction() == MotionEvent.ACTION_MOVE) //dragging
            {
                //to prevent constant recalcs, only process after 5 pixels
                //if user moves less than 5 pixels, we assume screen tap, not drag
                //we divide by 3 to slow down scene rotate
                if (Math.abs(pEvent.getX() - mDragStartX) > 5) //process Y axis rotation
                {
                    if (RotateScene) //rotate around fountain
                        ChangeSceneAngle(0, (mDragStartX - pEvent.getX())/3f); //Y axis
                    else //rotate camera
                        ChangeCameraAngle(0, (mDragStartX - pEvent.getX())/3f); //Y axis
                    mDragStartX = pEvent.getX();
                }
                if (Math.abs(pEvent.getY() - mDragStartY) > 5) //process X axis rotation
                {
                    if (RotateScene) //rotate around fountain
                        ChangeSceneAngle((pEvent.getY() - mDragStartY)/3f, 0); //X axis
                    else //rotate camera
                        ChangeCameraAngle((mDragStartY - pEvent.getY())/3f, 0); //X axis
                    mDragStartY = pEvent.getY();
                }
                return true; //must have this
            }
            return super.onTouchEvent(pEvent);
        }


    }




}