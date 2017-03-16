package com.platypus.android.tablet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import org.jscience.geography.coordinates.LatLong;
import org.jscience.geography.coordinates.UTM;
import org.jscience.geography.coordinates.crs.ReferenceEllipsoid;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.spi.LocationAwareLogger;
import org.w3c.dom.Text;

import com.mapbox.mapboxsdk.annotations.Annotation;
import com.mapbox.mapboxsdk.annotations.Polygon;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.ILatLng;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionDefinition;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Matrix;

import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapzen.android.lost.api.LocationServices;

import com.platypus.crw.CrwNetworkUtils;
import com.platypus.crw.SensorListener;
import com.platypus.crw.VehicleServer;
import com.platypus.crw.data.SensorData;
import com.platypus.crw.data.Pose3D;
import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ProgressBar;

import java.util.Map;

import com.platypus.crw.FunctionObserver;
import com.platypus.crw.ImageListener;
import com.platypus.crw.PoseListener;
import com.platypus.crw.VehicleServer.WaypointState;
import com.platypus.crw.VelocityListener;
import com.platypus.crw.WaypointListener;
import com.platypus.crw.data.Twist;
import com.platypus.crw.data.Utm;
import com.platypus.crw.data.UtmPose;

import android.app.Dialog;

import android.view.View.OnClickListener;
import com.platypus.android.tablet.Joystick.*;


public class TeleOpPanel extends Activity implements SensorEventListener {
    final Context context = this;
    final double GPSDIST = 0.0000449;
    SeekBar thrust = null;
    SeekBar rudder = null;
    TextView ipAddressBox = null;
    TextView mapInfo = null;
    RelativeLayout linlay = null;
    CheckBox autonomous = null;

    static TextView testIP = null;
    AsyncTask networkThread;
    TextView test = null;
    ToggleButton tiltButton = null;
    ToggleButton waypointButton = null;
    ToggleButton pauseWP = null;
    ToggleButton setHome = null;
    ImageButton goHome = null;
    ImageButton drawPoly = null;



    Button deleteWaypoint = null;
    Button connectButton = null;
    Button saveMap = null;
    Button loadMap = null;
    Button removeMap = null;
    Button refreshMap = null;
    Button saveWaypoints = null;
    Button loadWaypoints = null;
    Button advancedOptions = null;
    Button makeConvex = null; //for testing remove later
    Button preimeter = null;
    Button mapButton = null;
    Button centerToBoat = null;
    Button startRegion = null;
    Button clearRegion = null;

    //TextView log = null;
    Handler network = new Handler();
    ImageView cameraStream = null;
    Button loadWPFile = null;

    TextView sensorData1 = null;
    TextView sensorData2 = null;
    TextView sensorData3 = null;

    TextView sensorType1 = null;
    TextView sensorType2 = null;
    TextView sensorType3 = null;
    TextView battery = null;
    TextView Title = null;
    TextView waypointInfo = null;


    LinearLayout regionlayout = null;
    LinearLayout waypointlayout = null;
    View waypointregion = null;

    ToggleButton sensorvalueButton = null;
    JoystickView joystick;
    ProgressBar progressBar;
    Canvas canvas;
    Switch speed = null;


    boolean checktest;
    boolean firstmove = false;
    boolean waypointLayoutEnabled = true; //if false were on region layout
    int a = 0;

    double xValue;
    double yValue;
    double zValue;
    LatLong latlongloc;
    LatLng boatLocation;

    MapView mv;
    MapboxMap mMapboxMap;
    String zone;
    String rotation;

    //Marker boat;
    Marker boat2;
    Marker home_M;
    Marker userloc;

    Location userlocation;

    int currentselected = -1; //which element selected
    String saveName; //shouldnt be here?
    LatLng pHollowStartingPoint = new LatLng((float) 40.436871,
            (float) -79.948825);
    LatLng UCMerced = new LatLng((float) 37.400732, (float) -120.487372);
    LatLng Mapcenter;
    long lastTime = -1;
    double lat = 10;
    double lon = 10;
    String waypointStatus = "";
    Handler handlerRudder = new Handler();
    int thrustCurrent;
    int rudderCurrent;
    double heading = Math.PI / 2.;
    double rudderTemp = 0;
    double thrustTemp = 0;
    double old_rudder = 0;
    double old_thrust = 0;
    double temp;
    double rot;
    String boatwaypoint;
    double tempThrustValue = 0; //used for abs value of thrust
    Twist twist = new Twist();
    boolean networkConnection = true;

    float tempX = 0;
    float tempY = 0;

    Bitmap currentImage = null;
    boolean isAutonomous;
    boolean isCurrentWaypointDone = true;
    boolean isWaypointsRunning = false;

    SensorManager senSensorManager;
    Sensor senAccelerometer;
    public boolean stopWaypoints = true;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

    public static final double THRUST_MIN = -1.0;
    public static final double THRUST_MAX = 1.0;
    public static final double RUDDER_MIN = 1.0;
    public static final double RUDDER_MAX = -1.0;

    public EditText ipAddress = null;
    public EditText color = null;
    public RadioButton actualBoat = null;
    public RadioButton simulation = null;
    public Button startWaypoints = null;

    public RadioButton direct = null;
    public RadioButton reg = null;

    public Button submitButton = null;
    public static RadioGroup simvsact = null;
    public static String textIpAddress;
    public static boolean simul = false;
    public static boolean actual;
    public static Boat currentBoat;
    public static InetSocketAddress address;
    public CheckBox autoBox;
    private final Object _waypointLock = new Object(); //deadlock?!??
    boolean failedwp = true;

    public int wpcount = 0;
    public String wpstirng = "";
    public int channel = 0;
    public double[] data;
    SensorData Data;
    public String sensorV = "Loading...";
    public static int counter = 0;
    public TextView sensorValueBox;
    boolean dialogClosed = false;
    boolean sensorReady = false;
    public static TextView log;
    public boolean Auto = false;

    Dialog connectDialog;
    private PoseListener pl;
    private SensorListener sl;
    private WaypointListener wl;
    private int WPnum = 0;
    private boolean longClick = false;
    private long startTime, endTime;
    private boolean startDraw = false;

    boolean Mapping_S = false;
    //double[] low_tPID = {.2, .0, .0};
    double[] low_tPID = {.06, .0, .0};
    //double[] tPID = {.6, .0, .0};
    double[] tPID = {.2, .0, .0};

    //double[] low_rPID = {.4, 0, .1};
    double[] low_rPID = {.35, 0, .15};
    double[] rPID = {1, 0, .2};

    private UtmPose _pose;
    private UtmPose[] wpPose = null, tempPose = null;
    private int N_waypoint = 0;
    private boolean waypointlistener = false;
    private int counter_M = 0;
    private int[] pointarrow = {R.drawable.pointarrow, R.drawable.pointarrow_45, R.drawable.pointarrow_90,
            R.drawable.pointarrow_135, R.drawable.pointarrow_180, R.drawable.pointarrow_225, R.drawable.pointarrow_270, R.drawable.pointarrow_315};

    Icon Ihome;

    ArrayList<LatLng> touchpointList = new ArrayList<LatLng>();
    ArrayList<LatLng> waypointList = new ArrayList<LatLng>();
    ArrayList<Marker> markerList = new ArrayList(); //List of all the
    ArrayList<Marker> boundryList = new ArrayList();
    ArrayList<LatLng> lastAdded = new ArrayList<LatLng>();
    ArrayList<ArrayList<LatLng>> spiralWaypoints = new ArrayList<ArrayList<LatLng>>();
    ArrayList<UtmPose> allWaypointsSent = new ArrayList<UtmPose>();
    ArrayList<Polygon> spiralList = new ArrayList<Polygon>();
    private Polyline Waypath;
    private Polygon Boundry;
    Polyline boatToWP = null;
    boolean isFirstWaypointCompleted = false;
    public static final String PREF_NAME = "DataFile";
    private TabletLogger mlogger;


    List<Float> touchList = new ArrayList<Float>();
    //markers on the map
    //corresponding to the
    //given way

    // OfflineMapDownloader offlineMapDownloader;
    //  TilesOverlay offlineMapOverlay;
    LatLng OfflineCenter = null;
    // Projection MapProj;
    LatLng home = null;
    Timer timer;
    TimerTask timerTask;
    Date d = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

    private static final String logTag = TeleOpPanel.class.getName();
    String sensorLogTag = "Sensor";
    String waypointLogTag = "Sensor";
    String mapLogTag = "Sensor";

    //static final String

    /* TODO
    * Boat speed on that toggle button thing (gains)
    * make bottom area smaller
    * Transect distance spacing parameter
    * IMPORTANT STUFF
    * Moving joystick should turn autonomy off then releasing should turn back on. Joystick click listener doesnt seem to work though
    * Get back to that later ^
    * polygon region
    * */

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.setContentView(R.layout.tabletlayout_nexus7);  // layout for LG GpadF 8
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.setContentView(R.layout.tabletlayout); // layout for Nexus 10
        this.setContentView(R.layout.tabletlayoutswitch);

        ipAddressBox = (TextView) this.findViewById(R.id.printIpAddress);
        linlay = (RelativeLayout) this.findViewById(R.id.linlay);
        //tiltButton = (ToggleButton) this.findViewById(R.id.tiltButton);

        connectButton = (Button) this.findViewById(R.id.connectButton);
        log = (TextView) this.findViewById(R.id.log);
        // loadWPFile = (Button)this.findViewById(R.id.loadFileButton);
        autoBox = (CheckBox) this.findViewById(R.id.autonomousBox);
        makeConvex = (Button) this.findViewById(R.id.makeconvex);
        sensorData1 = (TextView) this.findViewById(R.id.SValue1);
        sensorData2 = (TextView) this.findViewById(R.id.SValue2);
        sensorData3 = (TextView) this.findViewById(R.id.SValue3);
        sensorType1 = (TextView) this.findViewById(R.id.sensortype1);
        sensorType2 = (TextView) this.findViewById(R.id.sensortype2);
        sensorType3 = (TextView) this.findViewById(R.id.sensortype3);
        sensorvalueButton = (ToggleButton) this.findViewById(R.id.SensorStart);
        sensorvalueButton.setClickable(sensorReady);
        sensorvalueButton.setTextColor(Color.GRAY);
        battery = (TextView) this.findViewById(R.id.batteryVoltage);
        joystick = (JoystickView) findViewById(R.id.joystickView);
        Title = (TextView) this.findViewById(R.id.controlScreenEnter);
        advancedOptions = (Button) this.findViewById(R.id.advopt);
        centerToBoat = (Button) this.findViewById(R.id.centermap);
        mapInfo = (TextView) this.findViewById(R.id.mapinfo);
        final ToggleButton switchView = (ToggleButton) this.findViewById(R.id.switchviewbutton);

        mapInfo.setText("Map Information \n Nothing Pending");


        //load inital waypoint menu
        onLoadWaypointLayout();
        switchView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view1) {
                if (switchView.isChecked()) {
                    waypointlayout.removeAllViews();
                    onLoadRegionLayout();
                    waypointLayoutEnabled = false;
                }
                else
                {
                    regionlayout.removeAllViews();
                    onLoadWaypointLayout();
                    waypointLayoutEnabled = true;
                }
            }
        });



        if (mlogger != null) {
            mlogger.close();
        }
        mlogger = new TabletLogger();


        //**********************************************************************
        //  faked sensor data
        //***********************************************************************
//                sensorData1.setText("6.56");
//                sensorType1.setText("ATLAS_PH \n pH");
//                sensorData2.setText("9.56");
//                sensorType2.setText("ATLAS_DO \n mg/L");
//                sensorData3.setText("305\n19.0");
//                sensorType3.setText("ES2 \nEC(µS/cm)\nTE(°C)");
//                battery.setText("16.566");

        //Center map button
//        startRegion.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Thread thread = new Thread() {
//                    public void run() {
//                        //if (currentBoat.isConnected() == true) {
//                        ArrayList<LatLng> flatlist = new ArrayList<LatLng>();
//                        System.out.println("wpsize + " + spiralWaypoints.size());
//                        for (ArrayList<LatLng> list : spiralWaypoints)
//                        {
//                            for (LatLng wpoint : list)
//                            {
//                                flatlist.add(wpoint);
//
//                            }
//                        }
//                        if (currentBoat.getConnected() == true) {
//                            checktest = true;
//                            JSONObject JPose = new JSONObject();
//                            if (flatlist.size() > 0) {
//
//                                //Convert all UTM to latlong
//                                UtmPose tempUtm = convertLatLngUtm(flatlist.get(flatlist.size() - 1));
//
//                                waypointStatus = tempUtm.toString();
//
//                                //Confused now, this does same thing as whats below uncomment if needed
//                                //currentBoat.addWaypoint(tempUtm.pose, tempUtm.origin);
//                                wpPose = new UtmPose[flatlist.size()];
//                                synchronized (_waypointLock) {
//                                    //wpPose[0] = new UtmPose(tempUtm.pose, tempUtm.origin);
//                                    for (int i = 0; i < flatlist.size(); i++) {
//                                        wpPose[i] = convertLatLngUtm(flatlist.get(i));
//                                    }
//                                    tempPose = wpPose;
//                                }
//
//                                currentBoat.returnServer().setAutonomous(true, new FunctionObserver<Void>() {
//                                    @Override
//                                    public void completed(Void aVoid) {
//                                        Log.i(logTag, "Autonomy set to true");
//                                    }
//
//                                    @Override
//                                    public void failed(FunctionError functionError) {
//                                        Log.i(logTag, "Failed to set autonomy");
//                                    }
//                                });
//                                checkAndSleepForCmd();
//                                currentBoat.returnServer().isAutonomous(new FunctionObserver<Boolean>() {
//                                    @Override
//                                    public void completed(Boolean aBoolean) {
//                                        isAutonomous = aBoolean;
//                                        Log.i(logTag, "isAutonomous: " + isAutonomous);
//                                    }
//
//                                    @Override
//                                    public void failed(FunctionError functionError) {
//
//                                    }
//                                });
//                                currentBoat.returnServer().startWaypoints(wpPose, "POINT_AND_SHOOT", new FunctionObserver<Void>() {
//                                    @Override
//                                    public void completed(Void aVoid) {
//
//                                        isWaypointsRunning = true;
//                                        System.out.println("startwaypoints - completed");
//                                    }
//
//                                    @Override
//                                    public void failed(FunctionError functionError) {
//                                        isCurrentWaypointDone = false;
//                                        System.out.println("startwaypoints - failed");
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                Toast.makeText(getApplicationContext(), "Failed to start waypoints, you may be using a too large region", Toast.LENGTH_LONG).show();
//                                            }
//                                        });
//
//                                        // = waypointStatus + "\n" + functionError.toString();
//                                        // System.out.println(waypointStatus);
//                                    }
//                                });
//                                currentBoat.returnServer().getWaypoints(new FunctionObserver<UtmPose[]>() {
//                                    @Override
//                                    public void completed(UtmPose[] wps) {
//                                        for (UtmPose i : wps) {
//                                            System.out.println("waypoints" + i.toString());
//                                        }
//                                    }
//
//                                    @Override
//                                    public void failed(FunctionError functionError) {
//
//                                    }
//                                });
//                            } else {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(getApplicationContext(), "Please Select Waypoints", Toast.LENGTH_LONG).show();
//                                    }
//                                });
//                            }
//                            try {
//
//                                mlogger.info(new JSONObject()
//                                        .put("Time", sdf.format(d))
//                                        .put("startWP", new JSONObject()
//                                                .put("WP_num", wpPose.length)
//                                                .put("AddWaypoint", Auto)));
//                            } catch (JSONException e) {
//                                Log.w(logTag, "Failed to log startwaypoint");
//                            } catch (Exception e) {
//
//                            }
//
//                        }
//                    }
//                };
//                thread.start();
//
//            }
//        });


        centerToBoat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMapboxMap == null) {
                    Toast.makeText(getApplicationContext(), "Please wait for the map to load", Toast.LENGTH_LONG).show();
                    return;
                }
                if (currentBoat == null) {
                    Toast.makeText(getApplicationContext(), "Please Connect to a boat first", Toast.LENGTH_LONG).show();
                    return;
                }
                if (currentBoat.getLocation() == null) {
                    Toast.makeText(getApplicationContext(), "Boat still finding GPS location", Toast.LENGTH_LONG).show();
                    return;
                }
                mMapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                                .target(currentBoat.getLocation())
                                .zoom(19)
                                .build()
                ));
            }
        });
        //Options menu
        advancedOptions.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(TeleOpPanel.this, advancedOptions);
                popup.getMenuInflater().inflate(R.menu.dropdownmenu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.toString()) {
                            case "Save Map": {
                                Thread thread = new Thread()
                                {
                                    @Override
                                    public void run() {
                                        saveMap();
                                    }
                                };
                                thread.start();
                                break;
                            }
                            case "Satellite Map": {
                                if (mMapboxMap != null)
                                {
                                    mMapboxMap.setStyle(Style.SATELLITE);
                                }
                                break;
                            }
                            case "Vector Map": {
                                if (mMapboxMap != null)
                                {
                                    mMapboxMap.setStyle(Style.MAPBOX_STREETS);
                                }
                                break;
                            }
                            case "Set Home":
                            {
                                setHome();
                                break;
                            }
                            case "Go Home": {
                                goHome();
                                break;
                            }
                            case "Set PID": {
                                setPID();
                                break;
                            }
                            case "Save Waypoints": {
                                {
                                    try {
                                        SaveWaypointsToFile();
                                    } catch (Exception e) {

                                    }
                                    break;
                                }
                            }
                            case "Load Waypoints": {
                                try {
                                    LoadWaypointsFromFile();
                                } catch (Exception e) {

                                }
                                break;
                            }
                        }
                        return true;
                    }
                });
                popup.show(); //showing popup menu
            }
        });


        // *****************//
        //      Joystick   //
        // ****************//

        joystick.setYAxisInverted(false);

        //*****************************************************************************
        //  Initialize Poselistener
        //*****************************************************************************
        pl = new PoseListener() { //gets the location of the boat
            public void receivedPose(UtmPose upwcs) {

                _pose = upwcs.clone();
                {
                    xValue = _pose.pose.getX();
                    yValue = _pose.pose.getY();
                    zValue = _pose.pose.getZ();
                    rotation = String.valueOf(Math.PI / 2
                            - _pose.pose.getRotation().toYaw());
                    rot = Math.PI / 2 - _pose.pose.getRotation().toYaw();

                    zone = String.valueOf(_pose.origin.zone);

                    latlongloc = UTM.utmToLatLong(UTM.valueOf(
                            _pose.origin.zone, 'T', _pose.pose.getX(),
                            _pose.pose.getY(), SI.METER),
                            ReferenceEllipsoid.WGS84);
                    // Log.i(logTag, "Pose listener called");
                    //Log.i(logTag, "rot:" + rot);
                }
            }
        };

        //*******************************************************************************
        //  Initialize Sensorlistener
        //*******************************************************************************
        sl = new SensorListener() {
            @Override
            public void receivedSensor(SensorData sensorData) {
                Data = sensorData;

                sensorV = Arrays.toString(Data.data);
                sensorV = sensorV.substring(1, sensorV.length() - 1);
                sensorReady = true;
                //Log.i("Platypus","Get sensor Data");
                //Log.i(logTag, "Sensor listener called");
            }
        };
        //*******************************************************************************
        //  Initialize Waypointlistener
        //*******************************************************************************
        wl = new WaypointListener() {
            @Override
            public void waypointUpdate(WaypointState waypointState) {
                boatwaypoint = waypointState.toString();
                System.out.println(waypointState.toString());
                //  Log.i(logTag, "Waypoint listener called");
            }
        };

        //*******************************************************************************
        //  Initialize Waypointlistener
        //*******************************************************************************

        wl = new WaypointListener() {
            @Override
            public void waypointUpdate(WaypointState waypointState) {
                boatwaypoint = waypointState.toString();
            }
        };

        //***********************************************************************
        // Initialize save and load waypoint buttons
        // **********************************************************************
//



        //****************************************************************************
        //  Initialize the Boat
        // ****************************************************************************
        currentBoat = new Boat(pl, sl);

//        currentBoat.returnServer().addWaypointListener(wl, new FunctionObserver<Void>() {
//            @Override
//            public void completed(Void aVoid) {
//                waypointlistener = true;
//            }
//
//            @Override
//            public void failed(FunctionError functionError) {
//                waypointlistener = false;
//            }
//        });



        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);

        final IconFactory mIconFactory = IconFactory.getInstance(this);
        Drawable mhome = ContextCompat.getDrawable(this, R.drawable.home1);
        Ihome = mIconFactory.fromDrawable(mhome);

        mv = (MapView) findViewById(R.id.mapview);
        //mv.setAccessToken(ApiAccess.getToken(this));
        mv.setAccessToken(getString(R.string.mapbox_access_token));//"pk.eyJ1Ijoic2hhbnRhbnV2IiwiYSI6ImNpZmZ0Zzd5Mjh4NW9zeG03NGMzNDI4ZGUifQ.QJgnm41kA9Wo3CJU-xZLTA");
        mv.onCreate(savedInstanceState);
        mv.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                System.out.println("mapboxmap ready");
                mMapboxMap = mapboxMap;

                mMapboxMap.setStyle(Style.MAPBOX_STREETS); //vector map
                //mMapboxMap.setStyle(Style.SATELLITE_STREETS); //satalite
                mMapboxMap.setMyLocationEnabled(true); //show current location
                mMapboxMap.getUiSettings().setRotateGesturesEnabled(false); //broken on mapbox side, currently fixing issue 4635 https://github.com/mapbox/mapbox-gl-native/issues/4635
                mMapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        final int index = markerList.indexOf(marker);
                        final Marker mark = marker;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Delete this waypoint?")
                                        .setCancelable(false)
                                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                if (markerList.size() == 0)
                                                {
                                                    return;
                                                }
                                                markerList.remove(index);
                                                waypointList.remove(index);
                                                mMapboxMap.removeMarker(mark);
                                                mMapboxMap.removePolyline(Waypath);
                                                Waypath = mMapboxMap.addPolyline(new PolylineOptions().addAll(waypointList).color(Color.GREEN).width(5));

                                                for (int i = 0; i < markerList.size(); i++)
                                                {
                                                    //edit snipped, cant do this atm
                                                }
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                return;
                                            }
                                        });

                                AlertDialog alert = builder.create();
                                alert.show();

                            }
                        });
                        return false;
                    }
                });

                mMapboxMap.setOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng point) {
                        Drawable mboundry = ContextCompat.getDrawable(context, R.drawable.boundary);
                        final Icon Iboundry = mIconFactory.fromDrawable(mboundry);
                        if (waypointButton.isChecked() && startDraw == false) {
                            //System.out.println("waypoint button");
                            LatLng wpLoc = point;
                            if (Waypath != null) {
                                Waypath.remove();
                            }
                            WPnum += 1;
                            waypointList.add(wpLoc);
                            markerList.add(mMapboxMap.addMarker(new MarkerOptions().position(wpLoc).title(Integer.toString(WPnum))));
                            Waypath = mMapboxMap.addPolyline(new PolylineOptions().addAll(waypointList).color(Color.GREEN).width(5));
                        }
                        ArrayList<ArrayList<LatLng>> spirals = new ArrayList<ArrayList<LatLng>>();
                        if (startDraw == true) {
                            drawPolygon(point, Iboundry);
                        }
                    }
                });
                boat2 = mMapboxMap.addMarker(new MarkerOptions().position(pHollowStartingPoint).title("Boat")
                        .icon(mIconFactory.fromResource(R.drawable.pointarrow)));
                //userloc = mMapboxMap.addMarker(new MarkerOptions().position(pHollowStartingPoint).title("Your Location"));

            }
        });
//        try {
//            boat2 = mMapboxMap.addMarker(new MarkerOptions().position(pHollowStartingPoint).title("Boat")
//                    .icon(mIconFactory.fromResource(R.drawable.pointarrow)));
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }
        connectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(context)
                        .setTitle("Connect")
                        .setMessage("You are already connected,\n do you want to reconnect?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                currentBoat = new Boat(pl, sl);
                                currentBoat.returnServer().addWaypointListener(wl, new FunctionObserver<Void>() {
                                    @Override
                                    public void completed(Void aVoid) {

                                    }

                                    @Override
                                    public void failed(FunctionError functionError) {

                                    }
                                });
                                connectBox();
                                Log.i(logTag, "Reconnect");
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(logTag, "Nothing");
                            }
                        })
                        .show();

            }
        });


        connectBox();

        //        loadWPFile.setOnClickListener(
        //                new OnClickListener() {
        //                    @Override
        //                    public void onClick(View view) {
        //                        try {
        //
        //                            if(setWaypointsFromFile()==false) {
        //                                failedwp = true;
        ////
        //                            }
        //                            else
        //                            {
        //                                failedwp = false;
        //                            }
        //                        }
        //                        catch(Exception e)
        //                        {
        //
        //                        }
        //                    }
        //                });

        //        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        //        alertDialog.setTitle("Add Waypoints from File");
        //        if (failedwp == true)
        //        {
        //            alertDialog.setMessage("Waypoint File was in the incorrect formatting. \n No Current Waypoints");
        //            waypointList.clear();
        //            for (Marker i : markerList) {
        //                i.remove();
        //            }
        //        }
        //        else {
        //            alertDialog.setMessage("Waypoints Added and Started");
        //        }

        //        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
        //            public void onClick(DialogInterface dialog, int which) {
        //                alertDialog.dismiss();
        //            }
        //        });
        //alertDialog.show();
        //actual = true;

        /*
         * This gets called when a boat is connected
         * Note it has to draw the boat somewhere initially until it gets a gps loc so it draws it
         * on PantherHollow lake until it gets a new gps loc and will then update to the current
         * position
         */
        //start comment


        //end uncomment
        //        speed.setOnClickListener(new OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                if(speed.isChecked()){
        //                    Mapping_S = true;
        //                    Log.i(logTag, "Mapping Speed");
        //                }
        //                else{
        //                    Mapping_S = false;
        //                }
        //                Thread thread = new Thread(){
        //                    public void run(){
        //                        if(Mapping_S){
        //                            currentBoat.returnServer().setGains(0, low_tPID, null);
        //                        }
        //                        else{
        //                            currentBoat.returnServer().setGains(0,tPID, null);
        //                        }
        //                    }
        //                };
        //            }
        //        });


//        drawPoly.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (startDraw == false) {
//                    startDraw = true;
//                    drawPoly.setBackgroundResource(R.drawable.draw_icon2);
//                } else {
//                    startDraw = false;
//                    drawPoly.setBackgroundResource(R.drawable.draw_icon);
//                }
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
  //      mv.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mv.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mv.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mv.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mv.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mv.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mv.onLowMemory();
    }

//    private void saveOfflineMap (LatLng Mapcenter){
//        mv.setDiskCacheEnabled(true);
//        offlineMapDownloader = OfflineMapDownloader.getOfflineMapDownloader(this);
//        // mv.setCenter(pHollowStartingPoint);
//        // mv.setZoom(17);
//
//        //BoundingBox boundingBox = new BoundingBox(new LatLng(40.435203, -79.951636), new LatLng(40.439345, -79.944796));
//
//        BoundingBox boundingBox = new BoundingBox(new LatLng(Mapcenter.getLatitude() - 0.0015,Mapcenter.getLongitude() - 0.0015), new LatLng(Mapcenter.getLatitude() +0.003,Mapcenter.getLongitude()+0.003));
//        CoordinateSpan span = new CoordinateSpan(boundingBox.getLatitudeSpan(), boundingBox.getLongitudeSpan());
//        CoordinateRegion coordinateRegion = new CoordinateRegion(Mapcenter, span);
//        offlineMapDownloader.beginDownloadingMapID(MapID, coordinateRegion, 17, 20);
//
//        OfflineMapDownloaderListener listener = new OfflineMapDownloaderListener() {
//                @Override
//                public void stateChanged(OfflineMapDownloader.MBXOfflineMapDownloaderState newState) {
//                    Log.i(logTag, String.format(MapboxConstants.MAPBOX_LOCALE, "stateChanged to %s", newState));
//                }
//
//                @Override
//                public void initialCountOfFiles(Integer numberOfFiles) {
//                    Log.i(logTag, String.format(MapboxConstants.MAPBOX_LOCALE, "File number = %d", numberOfFiles));
//                }
//
//                @Override
//                public void progressUpdate(final Integer numberOfFilesWritten, final Integer numberOfFilesExcepted) {
//                    runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if(progressBar.getVisibility() == View.GONE){
//                                    progressBar.setVisibility(View.VISIBLE);
//                                }
//                                progressBar.setMax(numberOfFilesExcepted);
//                                progressBar.setProgress(numberOfFilesWritten);
//
//                                if(numberOfFilesExcepted == numberOfFilesWritten){
//                                    progressBar.setVisibility(View.GONE);
//                                }
//                            }
//                        });
//
//                }
//
//                @Override
//                public void networkConnectivityError(Throwable error) {
//
//                }
//
//                @Override
//                public void sqlLiteError(Throwable error) {
//
//                }
//
//                @Override
//                public void httpStatusError(Throwable error) {
//
//                }
//
//                @Override
//                public void completionOfOfflineDatabaseMap(OfflineMapDatabase offlineMapDatabase) {
//                    runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getApplicationContext(), "Finish Saving Map", Toast.LENGTH_LONG).show();
//                                progressBar.setVisibility(View.GONE);
//                            }
//                        });
//
//
//                }
//            };
//        offlineMapDownloader.addOfflineMapDownloaderListener(listener);
//    }
//
//    private void loadOfflineMap(){
//        mv.setDiskCacheEnabled(true);
//        ArrayList<OfflineMapDatabase> offlineMapDatabases = offlineMapDownloader.getMutableOfflineMapDatabases();
//        if(offlineMapDatabases != null & offlineMapDatabases.size()>0) {
//
//            runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Marker marker = new Marker("Test", "", pHollowStartingPoint);
//                        String mapcenter = readFromFile();
//                        String[] mc  = mapcenter.split(",", 3);
//                        double lat = Double.parseDouble(mc[0]);
//                        double lon = Double.parseDouble(mc[1]);
//                        mv.setCenter(new LatLng(lat, lon));
//                        mv.setZoom(17);
//                        mv.setTileSource(new MBTilesLayer(getApplicationContext(), "shantanuv.nkob79p0.mblite"));
//
//                        mv.animate();
//                        Toast.makeText(getApplicationContext(), "Loading OfflineMap", Toast.LENGTH_SHORT).show();
//                    }
//                });
//        }
//        else{
//            runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "OfflineMap is Unavailable", Toast.LENGTH_LONG).show();
//                    }
//                });
//
//        }
//    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("Mapcenter.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput("Mapcenter.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                bufferedReader.close();

                inputStream.close();

                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    // This method checks the wifi connection but not Internet access
    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    // This method need to run in another thread except UI thread(main thread)
    public static boolean hasActiveInternetConnection(Context context) {

        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e(logTag, "Error checking internet connection", e);
            }
        } else {
            Log.d(logTag, "No network available!");
        }
        return false;
    }

    // Really Check Internet access
    public Boolean isInternetAvailable() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1    www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal == 0);
            if (reachable) {
                Log.i(logTag, "Internet access");
                return reachable;
            } else {
                Log.i(logTag, "No Internet access");
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        return false;
    }


    // *******************************
    //  JoystickView listener
    // *******************************

    private JoystickMovedListener _listener = new JoystickMovedListener() {
        @Override
        public void OnMoved(int x, int y) {
            thrustTemp = fromProgressToRange(y, THRUST_MIN, THRUST_MAX);
            rudderTemp = fromProgressToRange(x, RUDDER_MIN, RUDDER_MAX);
            if (currentBoat != null) {
                //does this need to be in seperate thread?
                Thread thread = new Thread() {
                    public void run() {
                        if (currentBoat.getConnected() == true) {
                            updateVelocity(currentBoat, null);
                        }
                    }
                };
                thread.start();
            }
            //Log.i(logTag, "Y:" + y + "\tX:" + x);
            //Log.i(logTag, "Thrust" + thrustTemp + "\t Rudder" + rudderTemp);
            try {
                mlogger.info(new JSONObject()
                        .put("Time", sdf.format(d))
                        .put("Joystick", new JSONObject()
                                .put("Thrust", thrustTemp)
                                .put("Rudder", rudderTemp)));
            } catch (JSONException e) {

            }
        }

        @Override
        public void OnReleased() {

        }

        @Override
        public void OnReturnedToCenter() {
            thrustTemp = 0;
            rudderTemp = 0;
            if (currentBoat != null) {
                Thread thread = new Thread() {
                    public void run() {
                        updateVelocity(currentBoat, new FunctionObserver<Void>() {
                            @Override
                            public void completed(Void aVoid) {

                            }

                            @Override
                            public void failed(FunctionError functionError) {

                            }
                        });
                    }
                };
                thread.start();
            }
        }
    };

    public void dialogClose() {
        IconFactory mIconFactory = IconFactory.getInstance(this);
        Drawable mboundry = ContextCompat.getDrawable(this, R.drawable.boundary);
        final Icon Iboundry = mIconFactory.fromDrawable(mboundry);
        Drawable mboat = ContextCompat.getDrawable(this, R.drawable.pointarrow);
        Icon Iboat = mIconFactory.fromDrawable(mboat);

        if (getBoatType() == true) {
            //
            //
            //log.append("asdf");

            //waypoint on click listener
            /*
             * if the add waypoint button is pressed and new marker where ever they click
             */


            //thread.start();

            //System.out.println("finally");

            /*
             * If they press delete wayponts delete all markers off the map and delete waypoints
             */
//            if (mMapboxMap != null) {
//                mMapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(
//                        new CameraPosition.Builder()
//                                .target(pHollowStartingPoint)
//                                .zoom(14)
//                                .build()
//                ));
//
//            }
            //uncomment this

//            removeMap.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try {
//                        touchpointList.remove(touchpointList.indexOf(lastAdded.get(lastAdded.size() - 1)));
//                        lastAdded.remove(lastAdded.size() - 1);
//
//                        Boundry.remove();
//                        for (Marker a : boundryList) {
//                            a.remove(); //remove all markers in boundry list from map
//                        }
//                        boundryList.clear();
//                        for (LatLng i : touchpointList) {
//                            boundryList.add(mMapboxMap.addMarker(new MarkerOptions().position(i).icon(Iboundry))); //add all elements to boundry list
//                        }
//
//                        if (touchpointList.size() > 2) {
//                            PolygonOptions poly = new PolygonOptions().addAll(touchpointList).strokeColor(Color.BLUE).fillColor(Color.parseColor("navy"));
//                            poly.alpha((float) .6);
//                            Boundry = mMapboxMap.addPolygon(poly);
//                        }
//                        PolyArea area = new PolyArea();
//
//
//                        if (touchpointList.size() > 0) {
//                            System.out.println("spiral called");
//                            spiralWaypoints = area.createSmallerPolygonsFlat(touchpointList);
//                            System.out.println("spiral " + spiralWaypoints.size());
//                            drawSmallerPolys(spiralWaypoints);
//                        }
//                    } catch (Exception e) {
//                        //System.out.println(e.toString());
//                    }
//                }
//            });
//
//            preimeter.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (latlongloc != null) {
//                        LatLng point = new LatLng(latlongloc.latitudeValue(SI.RADIAN) * 180 / Math.PI, latlongloc.longitudeValue(SI.RADIAN) * 180 / Math.PI);
//                        drawPolygon(point, Iboundry);
//                    }
//                }
//            });

            networkThread = new NetworkAsync().execute(); //launch networking asnyc task

        } else if (getBoatType() == false) {
            log.append("Simulated Boat");
            ipAddressBox.setText("Simulated Phone");
            simulatedBoat();
        } else {
            log.append("fail");
        }

        try {
            //boat2 = new Marker(currentBoat.getIpAddress().toString(), "Boat", new LatLng(pHollowStartingPoint.getLatitude(), pHollowStartingPoint.getLongitude()));


        } catch (Exception e) {

        }
    }


    //    @Override
    //    public void onResume() {
    //        super.onResume();
    //        //Intent intent = new Intent(this, TeleOpPanel.class);
    //        //startActivity(intent);
    //        if (networkThread.isCancelled()) //figure out how to resume asnyc task?
    //        {
    //            //    networkThread.execute();
    //        }
    //    }

    public static boolean validIP(String ip) {
        if (ip == null || ip == "")
            return false;
        ip = ip.trim();
        if ((ip.length() < 6) & (ip.length() > 15))
            return false;

        try {
            Pattern pattern = Pattern
                    .compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
            Matcher matcher = pattern.matcher(ip);
            return matcher.matches();
        } catch (PatternSyntaxException ex) {
            return false;
        }
    }

    /* Function observer is passed as parameter since it is needed when
     * the joystick is being moved but not when it is released  */
    public void updateVelocity(Boat a, FunctionObserver<Void> fobs) { //taken right from desktop client for updating velocity
        // ConnectScreen.boat.setVelocity(thrust.getProgress(),
        // rudder.getProgress());
        if (a.returnServer() != null) {
            //Twist twist = new Twist();
            twist.dx(thrustTemp >= -1 & thrustTemp <= 1 ? thrustTemp : 0);
            if (Math.abs(rudderTemp - 0) < .05) {
                tempThrustValue = 0;
                twist.drz(fromProgressToRange((int) tempThrustValue, RUDDER_MIN,
                        RUDDER_MAX));

            } else {
                twist.drz(rudderTemp >= -1 & rudderTemp <= 1 ? rudderTemp : 0);
            }
            a.returnServer().setVelocity(twist, fobs);

//            a.returnServer().setVelocity(twist, new FunctionObserver<Void>() {
//                @Override
//                public void completed(Void aVoid) {
//                    Log.w(logTag, "updated velocity");
//                }
//
//                @Override
//                public void failed(FunctionError functionError) {
//                    Log.w(logTag, "failed to update velocity");
//                }
//            });
        }
    }

    /*
     * Rotate the bitmap
     */
    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /*
     * this async task handles all of the networking on the boat since networking has to be done on
     * a different thread and since gui updates have to be updated on the main thread ....
     */

    private class NetworkAsync extends AsyncTask<String, Integer, String> {
        long oldTime = 0;
        //long oldTime1 = 0;
        String tester = "done";
        boolean connected = false;
        boolean firstTime = true;
        boolean isconvex = true;
        Context context;
        // IconFactory mIconFactory = IconFactory.getInstance(context);

        int tempnum = 100;
        Pointarrow Arrow = new Pointarrow();
        int icon_Index;
        int icon_Index_old = -1;
        IconFactory mIconFactory = IconFactory.getInstance(getApplicationContext());
        BitmapFactory.Options options = new BitmapFactory.Options();

        public void setOptions(BitmapFactory.Options options) {
            this.options = options;
            this.options.inDither = false;
            this.options.inTempStorage = new byte[18 * 23];
        }

        @Override
        protected void onPreExecute() {

            setOptions(options);

        }

        @Override
        protected String doInBackground(String... arg0) {
            updateMarkers(); //Launch update markers thread
            currentBoat.isConnected();
            Runnable networkRun = new Runnable() {
                @Override
                public void run() {
                    if (currentBoat != null) {
                        connected = currentBoat.getConnected();


                        if (old_thrust != thrustTemp) { //update velocity
                            //updateVelocity(currentBoat);
                        }

                        if (old_rudder != rudderTemp) { //update rudder
                            //updateVelocity(currentBoat);
                        }

                        if (stopWaypoints == true) {
                            currentBoat.returnServer().stopWaypoints(null);
                            stopWaypoints = false;
                        }
                        old_thrust = thrustTemp;
                        old_rudder = rudderTemp;

                        if (tempPose != null) {
                            try {
                                Pose3D waypoint = tempPose[0].pose;
                                double distanceSq = planarDistanceSq(_pose.pose, waypoint);

                                if (distanceSq <= 25) {
                                    //UtmPose[] queuedWaypoints = new UtmPose[tempPose.length - 1];

                                    if (N_waypoint < waypointList.size()) {
                                        N_waypoint += 1;
                                        tempPose = Arrays.copyOfRange(tempPose, 1, tempPose.length);
                                    }

                                }
                            } catch (Exception e) {
                                System.out.println(e.toString());
                                // Log.i(logTag, "PlanarDistanceSq Error");
                            }
                        }

                        publishProgress();
                    }
                }
            };

            ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
            exec.scheduleAtFixedRate(networkRun, 0, 500, TimeUnit.MILLISECONDS);
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... result) {
            LatLng curLoc;
            counter_M += 1;
            //if (latlongloc != null & counter_M > 5) {
            if (latlongloc != null) {

                curLoc = new LatLng(latlongloc.latitudeValue(SI.RADIAN) * 180 / Math.PI, latlongloc.longitudeValue(SI.RADIAN) * 180 / Math.PI);
                float degree = (float) (rot * 180 / Math.PI);  // degree is -90 to 270
                degree = (degree < 0 ? 360 + degree : degree); // degree is 0 to 360

                float bias = mv.getRotation();  // bias is the map orirentation

                if (currentBoat != null && curLoc != null) {
                    currentBoat.setLocation(curLoc);
                }

                if (mMapboxMap != null) {

                    icon_Index = Arrow.getIcon(degree);
                    if (icon_Index != icon_Index_old) {
                        boat2.setIcon(mIconFactory.fromResource(pointarrow[icon_Index]));
                        icon_Index_old = icon_Index;
                    } else {
                    }
                }
                counter_M = 0;
            }


            //if (mMapboxMap != null && false && isFirstWaypointCompleted == false && isWaypointsRunning == false && reachedWaypoint(currentBoat.getLocation(),markerList.get(0).getPosition()) == false)
            //get back to this later
//            if (mMapboxMap != null && markerList.size() > 0)
//            {
//                System.out.println("getting called");
//                //mMapboxMap.removeAnnotation(boatToWP);
//                if (boatToWP != null) //if there is a previous line remove it
//                {
//                    mMapboxMap.removeAnnotation(boatToWP);
//                }
//                boatToWP = mMapboxMap.addPolyline(new PolylineOptions().add(currentBoat.getLocation()).add(markerList.get(0).getPosition()).color(Color.GREEN).width(5));
//            }
//            else
//            {
//                isFirstWaypointCompleted = true;
//                if (mMapboxMap != null && boatToWP != null) {
//                    mMapboxMap.removeAnnotation(boatToWP);
//                    boatToWP = null;
//                }
            //}



                if (firstTime == true) {
//
            }
            if (connected == true) {
                ipAddressBox.setBackgroundColor(Color.GREEN);
            }
            if (connected == false) {
                ipAddressBox.setBackgroundColor(Color.RED);
            }


            if (sensorReady == true) {
                try {

                    sensorvalueButton.setClickable(sensorReady);
                    sensorvalueButton.setTextColor(Color.BLACK);
                    sensorvalueButton.setText("Show SensorData");
                    SharedPreferences settings = getSharedPreferences(PREF_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();


                    if (Data.channel == 4) {
                        String[] batteries = sensorV.split(",");
                        battery.setText(batteries[0]);
                        battery.setTextColor(isAverage(Data, batteries[0]));
                        double value = (Double.parseDouble(batteries[0]) + getAverage(Data)) / 2;
                        //Log.i(logTag,"Average = "+ value);

                        editor.putString(Data.type.toString(), Double.toString(value));
                        editor.commit();

                    }

                    if (sensorvalueButton.isChecked()) {
                        //  sensorValueBox.setBackgroundColor(Color.GREEN);
                        double value;
                        switch (Data.channel) {
                            case 4:
                                //                        String[] batteries = sensorV.split(",");
                                //                        battery.setText(batteries[0]);
                                break;
                            case 1:
                                sensorData1.setText(sensorV);
                                //sensorType1.setText(Data.type + "\n" + unit(Data.type));
                                sensorType1.setText("ATLAS_DO \n mg/L");
                                sensorData1.setTextColor(isAverage(Data, sensorV));
                                value = (Double.parseDouble(sensorV) + getAverage(Data)) / 2;

                                editor.putString(Data.type.toString(), Double.toString(value));
                                editor.commit();

                                break;
                            case 2:
                                sensorData2.setText(sensorV);
                                //sensorType2.setText(Data.type+ "\n"+unit(Data.type));
                                sensorType2.setText("ATLAS_PH");
                                sensorData2.setTextColor(isAverage(Data, sensorV));
                                value = (Double.parseDouble(sensorV) + getAverage(Data)) / 2;

                                editor.putString(Data.type.toString(), Double.toString(value));
                                editor.commit();

                                break;
                            case 3:
                                sensorData3.setText(sensorV);
                                // sensorType3.setText(Data.type+ "\n"+unit(Data.type));
                                sensorType3.setText("ES2 \nEC(µS/cm)\nT(°C)");
//                            sensorData3.setTextColor(isAverage(Data, sensorV));
//                            value = (Double.parseDouble(sensorV) + getAverage(Data))/2;
//
//                            editor.putString(Data.type.toString(), Double.toString(value));
//                            editor.commit();
                                break;
                            case 9:
                                break;
                            default:
                                sensorData1.setText("Waiting");
                                sensorData2.setText("Waiting");
                                sensorData3.setText("Waiting");
                        }

                    }
                }
                catch(Exception e)
                {
                    Log.i(sensorLogTag, e.toString());
                    System.out.println("Sensor error " + e.toString());
                }
                if (!sensorvalueButton.isChecked()) {
                    //sensorV = "";
                    sensorData1.setText("----");
                    sensorData2.setText("----");
                    sensorData3.setText("----");
                    //sensorValueBox.setBackgroundColor(Color.DKGRAY);
                }
            } else {
                sensorvalueButton.setText("Sensor Unavailable");
                sensorData1.setText("----");
                sensorData2.setText("----");
                sensorData3.setText("----");
            }
            //********************************//
            // Adding Joystick move listener//
            // ******************************//
            joystick.setOnJostickMovedListener(_listener);
            joystick.setOnJostickClickedListener(new JoystickClickedListener() {
                @Override
                public void OnClicked() {
                    System.out.println("joystick clicked");
                }

                @Override
                public void OnReleased() {
                    System.out.println("joystick released");
                }
            });
            DecimalFormat velFormatter = new DecimalFormat("####.###");

            //thrustTemp = fromProgressToRange(thrust.getProgress(), THRUST_MIN, THRUST_MAX);
            // rudderTemp = fromProgressToRange(rudder.getProgress(), RUDDER_MIN, RUDDER_MAX);

//            thrustProgress.setText(velFormatter.format(thrustTemp * 100.0) + "%");
//            rudderProgress.setText(velFormatter.format(rudderTemp * -100.0) + "%");

            //uncomment this
            if (waypointLayoutEnabled == true) {
                waypointInfo.setText("Waypoint Status: \n" + boatwaypoint);
                if (speed.isChecked()) {
                    Mapping_S = true;
                } else {
                    Mapping_S = false;
                }

            }

            //uncomment this
            //waypointsCompleted();
        }
    }

    public void simulatedBoat() {
        //        boat2 = map.addMarker(new MarkerOptions().anchor(.5f, .5f) //add boat to panther hollow
        //                .rotation(270).title("Boat 1")
        //                .snippet("IP Address: 192.168.1.1")
        //                .position(pHollowStartingPoint).title("Boat 1")
        //                .snippet("127.0.0.1 (localhost)")
        //                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.airboat))
        //                .flat(true));
        //
        //        lat = pHollowStartingPoint.latitude;
        //        lon = pHollowStartingPoint.longitude;
        //        map.setMyLocationEnabled(true);
        //        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pHollowStartingPoint,
        //                15));
        //        map.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
        //
        //        boat2.setRotation((float) (heading * (180 / Math.PI)));
        //        handlerRudder.post(new Runnable() { //control the boat
        //            @Override
        //            public void run() {
        //                if (thrust.getProgress() > 0) {
        //                    lat += Math.cos(heading) * (thrust.getProgress() - 50)
        //                            * .0000001;
        //                    lon += Math.sin(heading) * (thrust.getProgress())
        //                            * .0000001;
        //                    heading -= (rudder.getProgress() - 50) * .001;
        //                    boat2.setRotation((float) (heading * (180 / Math.PI)));
        //                }
        //                boat2.setPosition(new LatLng(lat, lon));
        //                handlerRudder.postDelayed(this, 200);
        //            }
        //        });
    }

    private String unit(VehicleServer.SensorType Type) {
        String unit = "";

        if (Type.toString().equalsIgnoreCase("ATLAS_PH")) {
            unit = "pH";
        } else if (Type.toString().equalsIgnoreCase("ATLAS_DO")) {
            unit = "mg/L";
        } else if (Type.toString().equalsIgnoreCase("ES2")) {
            unit = "EC(µS/cm)\n" +
                    "TE(°C)";
        } else if (Type.toString().equalsIgnoreCase("HDS_DEPTH")) {
            unit = "m";
        } else {
            unit = "";
        }


        return unit;
    }

    public void setVelListener() {
        currentBoat.returnServer().addVelocityListener(
                new VelocityListener() {
                    public void receivedVelocity(Twist twist) {
                        thrust.setProgress(fromRangeToProgress(twist.dx(),
                                THRUST_MIN, THRUST_MAX));
                        rudder.setProgress(fromRangeToProgress(twist.drz(),
                                RUDDER_MIN, RUDDER_MAX));
                    }
                }, null);

    }

    // Converts from progress bar value to linear scaling between min and
    // max
    private double fromProgressToRange(int progress, double min, double max) {
        return ((max - min) * ((double) progress) / 20.0);
    }

    // Converts from progress bar value to linear scaling between min and
    // max
    private int fromRangeToProgress(double value, double min, double max) {
        return (int) (20.0 * (value) / (max - min));
    }

    /* accelerometer controls */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

        }
    }

    public void updateViaAcceleration(float xval, float yval, float zval) { //update the thrust via accelerometers
        if (Math.abs(tempX - last_x) > 2.5) {

            if (last_x > 2) {
                thrust.setProgress(thrust.getProgress() - 3);
            }
            if (last_x < 2) {
                thrust.setProgress(thrust.getProgress() + 3);
            }
        }
        if (Math.abs(tempY - last_y) > 1) {
            if (last_y > 2) {
                rudder.setProgress(rudder.getProgress() - 3);
            }
            if (last_y < -2) {
                rudder.setProgress(rudder.getProgress() + 3);
            }
        }
    }

    public void addWayPointFromMap() {
        // when you click you make utm pose... below is fake values
        Pose3D pose = new Pose3D(1, 1, 0, 0.0, 0.0, 10);
        Utm origin = new Utm(17, true);
        // ConnectScreen.boat.addWaypoint(pose, origin);
        UtmPose[] wpPose = new UtmPose[1];
        wpPose[0] = new UtmPose(pose, origin);
        currentBoat.returnServer().startWaypoints(wpPose,
                "POINT_AND_SHOOT", new FunctionObserver<Void>() {
                    public void completed(Void v) {
                        //log.setText("completed"); UNCOMMENT THESE
                    }

                    public void failed(FunctionError fe) {
                        ///log.setText("failed");
                    }
                });

    }

    public LatLng convertUtmLatLng(Pose3D _pose, Utm _origin) {
        LatLong temp = UTM
                .utmToLatLong(
                        UTM.valueOf(_origin.zone, 'T', _pose.getX(),
                                _pose.getY(), SI.METER),
                        ReferenceEllipsoid.WGS84);
        return new LatLng(temp.latitudeValue(SI.RADIAN),
                temp.longitudeValue(SI.RADIAN));
    }

    public UtmPose convertLatLngUtm(ILatLng point) {

        UTM utmLoc = UTM.latLongToUtm(LatLong.valueOf(point.getLatitude(),
                point.getLongitude(), NonSI.DEGREE_ANGLE), ReferenceEllipsoid.WGS84);

        // Convert to UTM data structure
        Pose3D pose = new Pose3D(utmLoc.eastingValue(SI.METER), utmLoc.northingValue(SI.METER), 0.0, 0, 0, 0);
        Utm origin = new Utm(utmLoc.longitudeZone(), utmLoc.latitudeZone() > 'O');
        UtmPose utm = new UtmPose(pose, origin);
        return utm;
    }


    public void testCamera() {
        //log.setText("test camera");
        currentBoat.returnServer().addImageListener(new ImageListener() {
            public void receivedImage(byte[] imageData) {
                // Take a picture, and put the resulting image into the panel
                //log.setText("image taken");

                try {
                    Bitmap image1 = BitmapFactory.decodeByteArray(imageData, 0, 15);
                    if (image1 != null) {
                        // a++;
                        //System.out.println("image made");
                        currentImage = image1;

                    }
                } catch (Exception e) {
                    //log.setText(e.toString()); uncomment this
                    e.printStackTrace();
                }
            }
        }, null);
    }

    public void connectBox() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.connectdialog);
        dialog.setTitle("Connect To A Boat");
        ipAddress = (EditText) dialog.findViewById(R.id.ipAddress1);

        Button submitButton = (Button) dialog.findViewById(R.id.submit);
        simvsact = (RadioGroup) dialog.findViewById(R.id.simvsactual);
        actualBoat = (RadioButton) dialog.findViewById(R.id.actualBoatRadio);
        simulation = (RadioButton) dialog.findViewById(R.id.simulationRadio);

        direct = (RadioButton) dialog.findViewById(R.id.wifi);
        reg = (RadioButton) dialog.findViewById(R.id.reg);
        ipAddress.setText("192.168.1.250");
        //ipAddress.setText("127.0.0.1");

        direct.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (direct.isChecked()) {
                    ipAddress.setText("192.168.1.20");
                }
            }
        });
        reg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reg.isChecked()) {
                    ipAddress.setText("tunnel.senseplatypus.com");
                } else {
                    ipAddress.setText("192.168.1.20");
                }
            }
        });


        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // int selectedId = simvsact.getCheckedRadioButtonId();
                //int selectedOption = actvsim.getCheckedRadioButtonId();
                //log.append("asdf" + selectedOption);
                //                if (boat2 != null) {
                //boat2.remove();
                //                }

                if (ipAddress.getText() == null || ipAddress.getText().equals("") || ipAddress.getText().length() == 0) {
                    ipAddressBox.setText("IP Address: 127.0.0.1 (localhost)");
                } else {
                    ipAddressBox.setText("IP Address: " + ipAddress.getText());
                }
                markerList = new ArrayList<Marker>();
                actual = actualBoat.isChecked();

                textIpAddress = ipAddress.getText().toString();
                //System.out.println("IP Address entered is: " + textIpAddress);
                if (direct.isChecked()) {
                    if (ipAddress.getText() == null || ipAddress.getText().equals("")) {
                        address = CrwNetworkUtils.toInetSocketAddress("127.0.0.1" + ":11411");
                    }
                    address = CrwNetworkUtils.toInetSocketAddress(textIpAddress + ":11411");
                    // address = CrwNetworkUtils.toInetSocketAddress(textIpAddress + ":6077");
                    //                    log.append("\n" + address.toString());
                    //currentBoat = new Boat(address);
                    currentBoat.setAddress(address);
                } else if (reg.isChecked()) {
                    Log.i(logTag, "finding ip");
                    FindIP();
                }

                dialog.dismiss();
                dialogClose();
            }
        });
        dialog.show();

    }

    public static InetSocketAddress getAddress() {
        return address;
    }

    public static String getIpAddress() {
        return textIpAddress;
    }

    public static boolean getBoatType() {
        return actual;
    }

    public void waypointListenerTest() {
        currentBoat.returnServer().addWaypointListener(new WaypointListener() {
            @Override
            public void waypointUpdate(WaypointState waypointState) {
                System.out.println("waypontstate: " + waypointState.toString());
            }
        }, null);
    }

    public void testWaypointListener() {
        //this gets called on doInBackground() in the async task
        currentBoat.returnServer().addWaypointListener(new WaypointListener() {
            public void waypointUpdate(WaypointState ws) {
                boatwaypoint = ws.toString();
                //                currentBoat.returnServer().isAutonomous(new FunctionObserver<Boolean>() {
                //                    @Override
                //                    public void completed(Boolean aBoolean) {
                //                        isAutonomous = aBoolean;
                //                        Log.i(logTag, "isAutonomous: "+ isAutonomous);
                //                    }
                //
                //                    @Override
                //                    public void failed(FunctionError functionError) {
                //
                //                    }
                //});
                //System.out.println(boatwaypoint);
            }
        }, null);
    }

    private void checkAndSleepForCmd() {
        if (lastTime >= 0) {
            long timeGap = 1000 - (System.currentTimeMillis() - lastTime);
            if (timeGap > 0) {
                try {
                    Thread.sleep(timeGap);
                } catch (InterruptedException ex) {
                }
            }
        }
        lastTime = System.currentTimeMillis();
    }

    public void fromFiletoWPList() throws IOException {
        //code for opening window for meantime have tmep folder with one file it accepts for wp list
        File readFile = new File("");
        Scanner fileReader = new Scanner(readFile);
        //set delimeter
        //parse text into latlong
        //waypointList.add(fileReader.next());
    }

    /* at the moment does not validate files! make sure your waypoint file is correctly matched this will be implemented later..*/
    public boolean setWaypointsFromFile() throws IOException {
        File wpFile = null;
        try {
            wpFile = new File("./waypoints.txt");
        } catch (Exception e) {
            System.out.println("waypoint file not found" + e.toString());
        }
        Scanner fileScanner;
        int valueCounter = 0;
        //first make sure even number of elements

        if (wpFile.exists()) {
            fileScanner = new Scanner(wpFile);
            //first make sure even number of element
            while (fileScanner.hasNext()) {
                try {
                    LatLng temp = new LatLng(Double.parseDouble(fileScanner.next()), Double.parseDouble(fileScanner.next()));
                    waypointList.add(temp);
                    Marker tempMarker = mMapboxMap.addMarker(new MarkerOptions().position(temp));
                    markerList.add(tempMarker);
                } catch (Exception e) {
                    System.out.println("Invalid LAT/LNG in file");
                }
                System.out.println(fileScanner.next() + " " + fileScanner.next());
                valueCounter += 2;
            }
            System.out.println("amount of elements: " + valueCounter);
            if ((valueCounter % 2) != 0) {
                System.out.println("Mismatching lat long vals");
                return false;
            } else {
                System.out.println("Valid");
            }
        } else {
            System.out.println("File not found");
        }
        return true;
    }

    //    public void FindIP() {
    //
    //

    //        address = CrwNetworkUtils.toInetSocketAddress(textIpAddress + ":6077");
    //
    //        Thread thread = new Thread() {
    //            public void run() {
    //
    //                currentBoat = new Boat();
    //                UdpVehicleServer tempserver = new UdpVehicleServer();
    //                currentBoat.returnServer().setRegistryService(address);
    //                currentBoat.returnServer().getVehicleServices(new FunctionObserver<Map<SocketAddress, String>>() {
    //                    @Override
    //                    public void completed(Map<SocketAddress, String> socketAddressStringMap) {
    //                        System.out.println("Completed");
    //                        for (Map.Entry<SocketAddress, String> entry : socketAddressStringMap.entrySet()) {
    //
    //
    //                            //newaddressstring = entry.getKey().toString();
    //                            //System.out.println(newaddressstring);
    //                            currentBoat.returnServer().setVehicleService(entry.getKey());
    //
    //                            System.out.println(entry.getKey().toString());
    //                            System.out.println(entry.getValue().toString());
    //
    //                        }
    //                    }
    //
    //                    @Override
    //                    public void failed(FunctionError functionError) {
    //                        System.out.println("No Response");
    //                    }
    //                });
    //                //currentBoat = new Boat(CrwNetworkUtils.toInetSocketAddress(newaddressstring));
    //                //System.out.println("Boat address" + currentBoat.getIpAddress());
    //                // regcheck.show();
    //                //}
    //            }
    //        };
    //
    //        thread.start();
    //        //System.out.println("print here: " + newaddressstring);
    //        //currentBoat = new Boat(CrwNetworkUtils.toInetSocketAddress(newaddressstring));
    //
    //    }
    public void FindIP() {


        Thread thread = new Thread() {

            public void run() {
                address = CrwNetworkUtils.toInetSocketAddress(textIpAddress + ":6077");
                //address = CrwNetworkUtils.toInetSocketAddress(textIpAddress);
                //currentBoat = new Boat(pl,null);
                // currentBoat = new Boat();
                // UdpVehicleServer tempserver = new UdpVehicleServer();
                currentBoat.returnServer().setRegistryService(address);
                currentBoat.returnServer().getVehicleServices(new FunctionObserver<Map<SocketAddress, String>>() {
                    @Override
                    public void completed(Map<SocketAddress, String> socketAddressStringMap) {
                        Log.i(logTag, "Completed");
                        for (Map.Entry<SocketAddress, String> entry : socketAddressStringMap.entrySet()) {
                            //newaddressstring = entry.getKey().toString();
                            //System.out.println(newaddressstring);
                            // currentBoat.returnServer().setVehicleService(entry.getKey());
                            //                        adapter.add(entry);
                            //                        adapter.notifyDataSetChanged();

                            Log.i(logTag, entry.toString());
                            currentBoat.returnServer().setVehicleService(entry.getKey());


                            //

                        }
                        //                    if(currentBoat.isConnected() == true){
                        //                        Log.i(logTag, "Connected");
                        //                    }
                        //                    else{
                        //                        Log.i(logTag, "Disconnected");
                        //                    }
                    }

                    @Override
                    public void failed(FunctionError functionError) {
                        Log.i(logTag, "No Response");
                    }
                });

            }
        };
        thread.start();

    }

    public void SendEmail() {
        Thread thread = new Thread() {
            public void run() {
                Email mail = new Email("platypuslocation@gmail.com", "airboats");
                try {
                    //   mail.sendMail("nameboat", wpstirng, "shantanu@gmail.com", "platypuslocation@gmail.com");
                } catch (Exception e) {
                    System.out.println(e.toString());
                }

            }
        };
        thread.start();
    }

    public void InitSensorData() {
        while (currentBoat == null) {
        }

        final SensorListener sensorListener = new SensorListener() {
            @Override
            public void receivedSensor(SensorData sensorData) {

                Data = sensorData;
                //data = Data.data;
                //channel = Data.channel;
                sensorV = Arrays.toString(Data.data);
                //sensorV = Integer.toString(Data.channel);
                //                if(Data.toString()==null){
                //                    sensorV = "No sensor value";
                //                }
                //                else {
                //                    sensorV = Data.toString();
                //                }
            }
        };

        //currentBoat.returnServer().getNumSensors(new FunctionObserver<Integer>() {
        // @Override
        //  public void completed(Integer numSensors) {
        //    System.out.println("Sensor num:" + numSensors);
        //  for (int channel = 0; channel < numSensors; ++channel) {
        currentBoat.returnServer().addSensorListener(3, sensorListener, new FunctionObserver<Void>() {
            @Override
            public void completed(Void aVoid) {
                System.out.println("Add Sensorlistener");
            }

            @Override
            public void failed(FunctionError functionError) {
                sensorV = "Failed to get sensor value";
            }
        });
    }

    //  Make return button same as home button
    @Override
    public void onBackPressed() {
        //Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    /*
     * format of waypoint file
     * x x x x x x (first save)
     * x x x x x x (second save) ..etc
     * */
    public void SaveWaypointsToFile() throws IOException {
        //nothing to
        // save if no waypoints
        if (waypointList.isEmpty() == true) {
            return;
        }


        final BufferedWriter writer;
        try {
            File waypointFile = new File(getFilesDir() + "/waypoints.txt");
            writer = new BufferedWriter(new FileWriter(waypointFile, true));
        } catch (Exception e) {
            return;
        }

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.wpsavedialog);
        dialog.setTitle("Save Waypoint Set");
        final EditText input = (EditText) dialog.findViewById(R.id.newname);
        Button submit = (Button) dialog.findViewById(R.id.savebutton);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                //NO NUMBERS OR SPACES LOL
                saveName = input.getText().toString();

                //if (!(saveName.contains(" ") || saveName.matches(".*\\d+.*"))) {
                if (!(saveName.contains("\""))) {

                    //                if (!(saveName.contains(" ") || saveName.matches("^.*[^a-zA-Z0-9._-].*$"))) {

                    try {
                        writer.append("\n\" " + input.getText() + " \"");
                        writer.flush();
                        //writer.append(input.getText());
                        for (ILatLng i : waypointList) {
                            writer.append(" " + i.getLatitude() + " " + i.getLongitude());
                            writer.flush();
                        }
                        //writer.write("\n");

                        writer.close();
                    } catch (Exception e) {
                    }
                    dialog.dismiss();
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("No Quotation Marks in Title");
                    alertDialog.show();
                    alertDialog.setCancelable(true);
                    alertDialog.setCanceledOnTouchOutside(true);
                }

            }
        });
        dialog.show();
        File waypointFile = new File(getFilesDir() + "/waypoints.txt");
        //if (waypointFile.exists())
        //{
        //System.out.println("made new file");
        //}

    }

    public void LoadWaypointsFromFile() throws IOException {
        final File waypointFile = new File(getFilesDir() + "/waypoints.txt");
        //waypointFile.delete();
        Scanner fileScanner = new Scanner(waypointFile); //Scans each
        //line of the file
        final ArrayList<ArrayList<ILatLng>> waypointsaves = new ArrayList<ArrayList<ILatLng>>();
        final ArrayList<String> saveName = new ArrayList<String>();
        /* scans each line of the file as a waypoint save
         * then scans each line every two elements makes a latlng
         * adds all saves to arraylist
         * chose between arraylist later on
         */

        if (waypointFile.exists()) {
            while (fileScanner.hasNext()) {
                final ArrayList<ILatLng> currentSave = new ArrayList<ILatLng>();
                String s = fileScanner.nextLine();
                System.out.println(s);
                final Scanner stringScanner = new Scanner(s);

                //get save name
                if (stringScanner.hasNext()) {
                    if (stringScanner.next().equals("\"")) { //found first "
                        String currentdata = stringScanner.next();
                        String name = currentdata;
                        while (!currentdata.equals("\"")) {
                            currentdata = stringScanner.next();
                            if (!currentdata.equals("\"")) {
                                name = name + " " + currentdata;
                            }
                        }

                        saveName.add(name);
                    }
                }
                while (stringScanner.hasNext()) {
                    // System.out.println(stringScanner.next());
                    //                    System.out.println(Double.parseDouble(stringScanner.next()) + " " + Double.parseDouble(stringScanner.next()));

                    final double templat = Double.parseDouble(stringScanner.next());
                    final double templon = Double.parseDouble(stringScanner.next());
                    ILatLng temp = new ILatLng() {
                        @Override
                        public double getLatitude() {
                            return templat;

                        }

                        @Override
                        public double getLongitude() {
                            return templon;
                        }

                        @Override
                        public double getAltitude() {
                            return 0;
                        }
                    };

                    currentSave.add(temp);

                }
                if (currentSave.size() > 0) { //make sure no empty arrays (throws offset of wpsaves also why this?!?!)
                    waypointsaves.add(currentSave);
                }
                stringScanner.close();
            }
            fileScanner.close();

            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.waypointsavelistview);
            dialog.setTitle("List of Waypoint Saves");
            final ListView wpsaves = (ListView) dialog.findViewById(R.id.waypointlistview);
            Button submitButton = (Button) dialog.findViewById(R.id.submitsave);


            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    TeleOpPanel.this,
                    android.R.layout.select_dialog_singlechoice);
            wpsaves.setAdapter(adapter);
            for (String s : saveName) {
                adapter.add(s);
                adapter.notifyDataSetChanged();
            }
            final int chosensave;
            wpsaves.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    System.out.println("on long click");
                    final Dialog confirmdialog = new Dialog(context);
                    confirmdialog.setContentView(R.layout.confirmdeletewaypoints);
                    confirmdialog.setTitle("Delete This Waypoint Path?");
                    Button deletebutton = (Button) confirmdialog.findViewById(R.id.yesbutton);
                    Button cancel = (Button) confirmdialog.findViewById(R.id.nobutton);
                    deletebutton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //delete line from file

                            //delete object from list since update wont occur until you press load wp again
                            adapter.remove(adapter.getItem(position));
                            try {
                                File inputFile = new File(getFilesDir() + "/waypoints.txt");
                                File tempFile = new File(getFilesDir() + "/tempwaypoints.txt");

                                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

                                String lineToRemove = "\" " + saveName.get(position) + " \"";
                                String currentLine;

                                while ((currentLine = reader.readLine()) != null) {
                                    int index = currentLine.indexOf(' ');
                                    String tempasdf = currentLine;

                                    String trimmedLine = currentLine.trim();
                                    if (trimmedLine.contains(lineToRemove)) {
                                        continue;
                                    }
                                    writer.write(currentLine + System.getProperty("line.separator"));
                                }
                                writer.close();
                                reader.close();
                                tempFile.renameTo(inputFile);
                            } catch (Exception e) {
                            }
                            confirmdialog.dismiss();
                        }
                    });
                    cancel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmdialog.dismiss();
                        }
                    });
                    confirmdialog.show();

                    return false;
                }
            });
            wpsaves.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    currentselected = position;
                }
            });
            submitButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Object checkedItem = wpsaves.getAdapter().getItem(wpsaves.getCheckedItemPosition());
                    //System.out.println(chosensave);

                    if (currentselected == -1) {
                        dialog.dismiss();
                        //write no selected box
                    }
                    waypointList.clear();
                    markerList.clear();
                    //System.out.println(currentselected);
                    //for (ArrayList<ILatLng> i : waypointsaves)
                    {
                        //System.out.println(i.size());
                    }

                    int num = 1;
                    for (ILatLng i : waypointsaves.get(currentselected)) //tbh not sure why there is a 1 offset but there is
                    {
                        //System.out.println(i.getLatitude() + " " + i.getLongitude());
                        markerList.add(mMapboxMap.addMarker(new MarkerOptions().position(new LatLng(i.getLatitude(), i.getLongitude())).title(Integer.toString(num))));
                        waypointList.add(new LatLng(i.getLatitude(), i.getLongitude()));
                        num++;
                    }
                    Waypath = mMapboxMap.addPolyline(new PolylineOptions().addAll(waypointList).color(Color.GREEN).width(5));

                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    public static double planarDistanceSq(Pose3D a, Pose3D b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return dx * dx + dy * dy;
    }

    public void setHome() {
        final JSONObject Jhome = new JSONObject();
        final JSONObject JPhone = new JSONObject();
        final JSONObject JTablet = new JSONObject();
        if (home == null) {
            new AlertDialog.Builder(context)
                    .setTitle("Set Home")
                    .setMessage("Which position do you want to use?")
                    .setPositiveButton("Phone", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (latlongloc != null) {
                                home = new LatLng(latlongloc.latitudeValue(SI.RADIAN) * 180 / Math.PI, latlongloc.longitudeValue(SI.RADIAN) * 180 / Math.PI);
                                try {
                                    JPhone.put("Lat", home.getLatitude());
                                    JPhone.put("Lng", home.getLongitude());
                                    Jhome.put("Phone", JPhone);
                                    mlogger.info(new JSONObject()
                                            .put("Time", sdf.format(d))
                                            .put("Home", Jhome));
                                } catch (JSONException e) {

                                }
                                MarkerOptions home_MO = new MarkerOptions()
                                        .position(home)
                                        .title("Home")
                                        .icon(Ihome);
                                home_M = mMapboxMap.addMarker(home_MO);
                                //Bitmap home_B = BitmapFactory.decodeResource(getResources(), R.drawable.home);
                                //Drawable d = new BitmapDrawablegit (getResources(), home_B);


                                mMapboxMap.moveCamera(CameraUpdateFactory.newLatLng(home));
                            } else {

                                Toast.makeText(getApplicationContext(), "Phone doesn't have GPS Signal", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Tablet", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            LatLng loc = new LatLng(mMapboxMap.getMyLocation());

                            if (loc != null) {
                                home = loc;
                                MarkerOptions home_MO = new MarkerOptions()
                                        .position(home)
                                        .title("Home")
                                        .icon(Ihome);
                                home_M = mMapboxMap.addMarker(home_MO);
                                mMapboxMap.moveCamera(CameraUpdateFactory.newLatLng(home));
                                try {
                                    JTablet.put("Lat", home.getLatitude());
                                    JTablet.put("Lng", home.getLongitude());
                                    Jhome.put("Tablet", JTablet);
                                    mlogger.info(new JSONObject()
                                            .put("Time", sdf.format(d))
                                            .put("Home", Jhome));
                                } catch (JSONException e) {

                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "Tablet doesn't have GPS Signal", Toast.LENGTH_SHORT).show();
                            }


                        }
                    })
                    .show();


        } else {
            new AlertDialog.Builder(context)
                    .setTitle("Set Home")
                    .setMessage("Do you want to remove the home?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            try {
                                Jhome.put("Lat", home.getLatitude());
                                Jhome.put("Lng", home.getLongitude());
                                mlogger.info(new JSONObject()
                                        .put("Time", sdf.format(d))
                                        .put("Removed home", Jhome));
                            } catch (JSONException e) {

                            }
                            mMapboxMap.removeMarker(home_M);
                            home = null;
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }


    }

    public void goHome() {
        if (home == null) {
            Toast.makeText(getApplicationContext(), "Set home first!", Toast.LENGTH_LONG).show();
            //home = pHollowStartingPoint;
        } else {
            //stopWaypoints = true;

            //if(currentBoat.isConnected()){
            new AlertDialog.Builder(context)
                    .setTitle("Go Home")
                    .setMessage("Let the boat go home ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Thread threadhome = new Thread() {
                                public void run() {
                                    //if (currentBoat.isConnected()) {
                                    if (currentBoat.isConnected()) {
                                        //                                    currentBoat.returnServer().stopWaypoints(null);
                                        //                                    checkAndSleepForCmd();
                                        if (!isAutonomous) {
                                            currentBoat.returnServer().setAutonomous(true, null);
                                            isAutonomous = true;
                                        }


                                        UtmPose homeUTM = convertLatLngUtm(home);
                                        currentBoat.addWaypoint(homeUTM.pose, homeUTM.origin);
                                        Log.i(logTag, "Go home");
                                    }
                                }
                            };
                            threadhome.start();
                            Log.i(logTag, "Go home");
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i(logTag, "Nothing");
                        }
                    })
                    .show();

            //}

        }
    }

    public void setPID() {
        if (currentBoat == null) {
            Log.i(logTag, "No boat connected, cant set PID");
            return;
        }

        final int THRUST_GAIN_AXIS = 0;
        final int RUDDER_GAIN_AXIS = 5;

        final Dialog piddialog = new Dialog(context);
        piddialog.setContentView(R.layout.setpid);
        piddialog.setTitle("Set PID Gains");

        Button setPID = (Button) piddialog.findViewById(R.id.pidsubmit);

        final EditText thrustP = (EditText) piddialog.findViewById(R.id.thrustfirst);
        final EditText thrustI = (EditText) piddialog.findViewById(R.id.thrustsecond);
        final EditText thrustD = (EditText) piddialog.findViewById(R.id.thrustthird);

        final EditText rudderP = (EditText) piddialog.findViewById(R.id.rudderfirst);
        final EditText rudderI = (EditText) piddialog.findViewById(R.id.ruddersecond);
        final EditText rudderD = (EditText) piddialog.findViewById(R.id.rudderthird);

        //there is probably a better way to do this (without setting them to 0 in xml page)
        setPID.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final double thrustPID[] = {Double.parseDouble(thrustP.getText().toString()), Double.parseDouble(thrustI.getText().toString()), Double.parseDouble(thrustD.getText().toString())};
                final double rudderPID[] = {Double.parseDouble(rudderP.getText().toString()), Double.parseDouble(rudderI.getText().toString()), Double.parseDouble(rudderD.getText().toString())};

                final Thread thread = new Thread() {
                    public void run() {
                        System.out.println("pids");
                        for (double i : thrustPID) {
                            System.out.println(i);
                        }
                        currentBoat.returnServer().setGains(THRUST_GAIN_AXIS, thrustPID, new FunctionObserver<Void>() {
                            @Override
                            public void completed(Void aVoid) {
                                Log.i(logTag, "Setting thrust PID completed.");
                            }

                            @Override
                            public void failed(FunctionError functionError) {
                                Log.i(logTag, "Setting thrust PID failed: " + functionError);
                            }
                        });
                        currentBoat.returnServer().setGains(RUDDER_GAIN_AXIS, rudderPID, new FunctionObserver<Void>() {
                            @Override
                            public void completed(Void aVoid) {
                                Log.i(logTag, "Setting rudder PID completed ");
                            }

                            @Override
                            public void failed(FunctionError functionError) {
                                Log.i(logTag, "Setting rudder PID failed: " + functionError);
                            }
                        });
                    }
                };
                thread.start();
                piddialog.dismiss();
            }

        });
        piddialog.show();
    }

    public void GetTabletHeading() {
        float[] r;
        float[] values;
        //SensorManager.getOrientation(r,values);
    }

    public void drawSmallerPolys(ArrayList<ArrayList<LatLng>> spirals) {
        for (Polygon t : spiralList) {
            t.remove();
        }
        spiralList.clear();
        for (ArrayList<LatLng> a : spirals) {
            spiralList.add(mMapboxMap.addPolygon(new PolygonOptions().addAll(a).strokeColor(Color.BLUE).fillColor(Color.TRANSPARENT))); //draw polygon

//                            for (LatLng i : a)
//                            {
//                                System.out.print(i.toString() + " ");
//                            }
        }
    }

    public int isAverage(SensorData data, String value) {
        double v = Double.parseDouble(value);
        double average = getAverage(data);
        if ((average - v) > average * 0.001) {
            return Color.RED;
        } else if ((v - average) > average * 0.001) {
            return Color.GREEN;
        } else {
            return Color.GRAY;
        }
    }

    public double getAverage(SensorData data) {
        double average;
        SharedPreferences settings = getSharedPreferences(PREF_NAME, 0);
        String v = settings.getString(data.type.toString(), "0");
        average = Double.parseDouble(v);
        return average;
    }

    private void drawPolygon(LatLng point, Icon icon) {
        ArrayList<ArrayList<LatLng>> spirals = new ArrayList<ArrayList<LatLng>>();
        Icon Iboundry = icon;
        LatLng wpLoc = point;
        if (Boundry != null) {
            Boundry.remove();
        }
        for (Marker t : boundryList) {
            t.remove();
        }
        touchpointList.add(wpLoc);
        PolyArea area = new PolyArea();
        touchpointList = area.quickHull(touchpointList);

        if (touchpointList.contains(wpLoc)) {
            lastAdded.add(wpLoc);
        }

        if (lastAdded.size() != touchpointList.size()) {
            ArrayList<LatLng> tempLastAdded = new ArrayList<LatLng>(lastAdded);
            tempLastAdded.removeAll(touchpointList);
            //item that should be ommited
            lastAdded.remove(tempLastAdded.get(0));
        }

//        spirals = area.createSmallerPolygons(touchpointList);
        //spiralWaypoints = area.createSmallerPolygonsFlat(touchpointList);
        spiralWaypoints = area.computeSpiralsPolygonOffset(touchpointList);
        drawSmallerPolys(spiralWaypoints);

        for (LatLng i : touchpointList) {
            boundryList.add(mMapboxMap.addMarker(new MarkerOptions().position(i).icon(Iboundry))); //add all elements to boundry list
        }


        //System.out.println(lastAdded.size() + " " + touchpointList.size());
        //System.out.println(lastAdded);
        //System.out.println(touchpointList);
        PolygonOptions poly = new PolygonOptions().addAll(touchpointList).strokeColor(Color.BLUE).fillColor(Color.parseColor("navy")); //draw polygon
        //border gets aa'd better than the fill causing gaps between border and fill...
        poly.alpha((float) .6); //set interior opacity
        Boundry = mMapboxMap.addPolygon(poly);

    }
    public void saveMap()
    {
        if (mMapboxMap == null)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Map not saved (make sure youre connected to internet for downloading maps", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }

        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                networkConnection = hasActiveInternetConnection(context);
            }
        };
        thread.start();
        if (networkConnection == false) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Please Connect to the Internet first", Toast.LENGTH_LONG).show();
                }
            });
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mapInfo.setText("Map Information \n Nothing Pending");
                }
            });

            return;
        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Please leave app open and connected to the internet until the completion dialog shows", Toast.LENGTH_LONG).show();
            }
        });


        // Set up the OfflineManager
        OfflineManager offlineManager = OfflineManager.getInstance(this);
        offlineManager.setAccessToken(getString(R.string.mapbox_access_token));

        // Create a bounding box for the offline region
        LatLngBounds latLngBounds = mMapboxMap.getProjection().getVisibleRegion().latLngBounds;
        System.out.println("bounds: " + latLngBounds.toString());

//        LatLngBounds latLngBounds = new LatLngBounds.Builder()
//                .include(new LatLng(37.7897, -119.5073)) // Northeast
//                .include(new LatLng(37.6744, -119.6815)) // Southwest
//                .build();


        final String JSON_CHARSET = "UTF-8";
        final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";
        System.out.println("zoom min" + mMapboxMap.getMinZoom());
        System.out.println("zoom max" + mMapboxMap.getMaxZoom());
        System.out.println("zoom current " + mMapboxMap.getCameraPosition().zoom);
        // Define the offline region
        //change zoom to 15 if need be

        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                mv.getStyleUrl(),latLngBounds,19,21, this.getResources().getDisplayMetrics().density); //try 19

        // Set the metadata not sure but changing "area" to something longer might be causing crash
        byte[] metadata;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_FIELD_REGION_NAME, "Area");
            String json = jsonObject.toString();
            metadata = json.getBytes(JSON_CHARSET);
        } catch (Exception e) {
            Log.e(logTag, "Failed to encode metadata: " + e.getMessage());
            metadata = null;
        }

        //create region
        offlineManager.createOfflineRegion(definition, metadata, new OfflineManager.CreateOfflineRegionCallback() {
            @Override
            public void onCreate(OfflineRegion offlineRegion) {
                offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);

                offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                    @Override
                    public void onStatusChanged(OfflineRegionStatus status) {
                        double percentage = status.getRequiredResourceCount() >= 0 ?
                                (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) : 0.0;
                        percentage = Math.round(percentage);
                        if (status.isComplete()) {
                            mapInfo.setText("Map Information \n Map Downloaded");
                            System.out.println("download complete");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                    alertDialog.setTitle("Download Completed");
                                    alertDialog.show();
                                    alertDialog.setCancelable(true);
                                    alertDialog.setCanceledOnTouchOutside(true);
                                    alertDialog.setButton("Dismiss", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            alertDialog.dismiss();
                                        }
                                    });

                                }
                            });

                        } else if (status.isRequiredResourceCountPrecise()) {
                            mapInfo.setText("Map Information \n " + percentage + "% Downloaded");
                            //setPercentage((int) Math.round(percentage));
                        }
                    }

                    @Override
                    public void onError(OfflineRegionError error) {
                        // If an error occurs, print to logcat
                        Log.e(logTag, "onError reason: " + error.getReason());
                        Log.e(logTag, "onError message: " + error.getMessage());
                    }

                    @Override
                    public void mapboxTileCountLimitExceeded(long limit) {
                        // Notify if offline region exceeds maximum tile count
                        Log.e(logTag, "Mapbox tile count limit exceeded: " + limit);
                    }
                });
                //list offline regions

            }
            @Override
            public void onError(String error) {
                Log.e(logTag, "Error: " + error);
            }
        });
    }
    public void updateMarkers() {

        Runnable markerRun = new Runnable() {
            @Override
            public void run() {
                if (currentBoat != null && currentBoat.getLocation() != null && mMapboxMap != null) {
                    boat2.setPosition(currentBoat.getLocation());
                }

                //make deleting waypoints wierd, possibly due to thread synchronization when deleting mapbox objects
//                if (isFirstWaypointCompleted == false && isWaypointsRunning == false && mMapboxMap != null) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (boatToWP != null) {
//                                mMapboxMap.removePolyline(boatToWP); //remove old line
//                                System.out.println("line removed");
//                            }
//                            if (markerList.size() > 0 && currentBoat!= null && currentBoat.getLocation() != null) {
//                                boatToWP = mMapboxMap.addPolyline(new PolylineOptions().add(currentBoat.getLocation()).add(markerList.get(0).getPosition()).color(Color.GREEN).width(5));
//                                System.out.println("line added");
//                            }
//                        }
//                    });
//                }

//                }
//                else
//                {
//                    isFirstWaypointCompleted = true;
//                    mMapboxMap.removePolyline(boatToWP); //remove old line
//                }

            }
        };
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleAtFixedRate(markerRun, 0, 1000, TimeUnit.MILLISECONDS);

    }

    /*Not implemented on server side yet */
    /* Should be called from some start region button */
    /* Should be passed outer layer after quickhull is run */
    public void startArea(ArrayList<LatLng> perimeter)
    {
        UtmPose poseList[] = new UtmPose[perimeter.size()];
        for (LatLng position : perimeter)
        {
            convertLatLngUtm(position); //Convert all
        }
        currentBoat.returnServer().startWaypoints(poseList, "Spiral", new FunctionObserver<Void>() {
            @Override
            public void completed(Void aVoid) {

            }

            @Override
            public void failed(FunctionError functionError) {

            }
        });
    }

    //Change this to take a waypointlist in not sure global variable
    public void startWaypoints()
    {
        Thread thread = new Thread() {
            public void run() {
                //if (currentBoat.isConnected() == true) {
                if (currentBoat.getConnected() == true) {
                    checktest = true;
                    JSONObject JPose = new JSONObject();
                    if (waypointList.size() > 0) {

                        //Convert all UTM to latlong
                        UtmPose tempUtm = convertLatLngUtm(waypointList.get(waypointList.size() - 1));

                        waypointStatus = tempUtm.toString();

                        //Confused now, this does same thing as whats below uncomment if needed
                        //currentBoat.addWaypoint(tempUtm.pose, tempUtm.origin);
                        wpPose = new UtmPose[waypointList.size()];
                        synchronized (_waypointLock) {
                            //wpPose[0] = new UtmPose(tempUtm.pose, tempUtm.origin);
                            for (int i = 0; i < waypointList.size(); i++) {
                                wpPose[i] = convertLatLngUtm(waypointList.get(i));
                                allWaypointsSent.add(wpPose[i]);
                            }
                            tempPose = wpPose;
                        }

                        currentBoat.returnServer().setAutonomous(true, new FunctionObserver<Void>() {
                            @Override
                            public void completed(Void aVoid) {
                                Log.i(logTag, "Autonomy set to true");
                            }

                            @Override
                            public void failed(FunctionError functionError) {
                                Log.i(logTag, "Failed to set autonomy");
                            }
                        });
                        checkAndSleepForCmd();
                        currentBoat.returnServer().isAutonomous(new FunctionObserver<Boolean>() {
                            @Override
                            public void completed(Boolean aBoolean) {
                                isAutonomous = aBoolean;
                                Log.i(logTag, "isAutonomous: " + isAutonomous);
                            }

                            @Override
                            public void failed(FunctionError functionError) {

                            }
                        });
                        currentBoat.returnServer().startWaypoints(wpPose, "POINT_AND_SHOOT", new FunctionObserver<Void>() {
                            @Override
                            public void completed(Void aVoid) {

                                isWaypointsRunning = true;
                                System.out.println("startwaypoints - completed");
                            }

                            @Override
                            public void failed(FunctionError functionError) {
                                isCurrentWaypointDone = false;
                                System.out.println("startwaypoints - failed");
                                // = waypointStatus + "\n" + functionError.toString();
                                // System.out.println(waypointStatus);
                            }
                        });
                        currentBoat.returnServer().getWaypoints(new FunctionObserver<UtmPose[]>() {
                            @Override
                            public void completed(UtmPose[] wps) {
                                for (UtmPose i : wps) {
                                    System.out.println("wp");
                                    System.out.println(i.toString());
                                }
                            }

                            @Override
                            public void failed(FunctionError functionError) {

                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Please Select Waypoints", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    try {

                        mlogger.info(new JSONObject()
                                .put("Time", sdf.format(d))
                                .put("startWP", new JSONObject()
                                        .put("WP_num", wpPose.length)
                                        .put("AddWaypoint", Auto)));
                    } catch (JSONException e) {
                        Log.w(logTag, "Failed to log startwaypoint");
                    }
                }
            }
        };
        thread.start();
    }

    //this is used for checking if the boat reached the first waypoint for drawing a line
    public boolean reachedWaypoint(LatLng boatLocation,LatLng point)
    {
        //Marker i = markerList.get(0);
        if (isWaypointWithinDistance(boatLocation,point,GPSDIST))
        {
            return true;
        }
        return false;
    }
    public boolean isWaypointWithinDistance(LatLng a, LatLng b, double dist)
    {
//        double x = Math.pow((a.getLatitude() - b.getLatitude()),2);
//        double y = Math.pow((a.getLongitude() - b.getLongitude()),2);
//        double distanceBetweenPoints = Math.sqrt(x+y);
//        //if (distanceBetweenPoints < dist)//0.0000449)
        if (a.distanceTo(b) <= dist)
        {
            return true;
        }
        return false;
    }

    public void waypointsCompleted()
    {
        if(markerList.size() == 0)
        {
            return;
        }

        IconFactory mIconFactory = IconFactory.getInstance(context);
        //Drawable completedraw = ContextCompat.getDrawable(context, R.drawable.greenmarker);
        //Icon completed = mIconFactory.fromDrawable(completedraw);


        for (Marker i : markerList)
        {

            double x = Math.pow((i.getPosition().getLatitude() - currentBoat.getLocation().getLatitude()),2);
            double y = Math.pow((i.getPosition().getLongitude() - currentBoat.getLocation().getLongitude()),2);
            double dist = Math.sqrt(x+y);
            if (reachedWaypoint(i.getPosition(),currentBoat.getLocation()))
            {
                //if (i.getIcon().equals(completed))
                {
                    continue;
                }
                //i.setIcon(completed);
            }
        }

        Thread thread = new Thread()
        {
            public void run() {

                currentBoat.returnServer().getWaypoints(new FunctionObserver<UtmPose[]>() {
                    @Override
                    public void completed(UtmPose[] utmPoses) {
                        ArrayList<UtmPose> remainingOnboat = new ArrayList<UtmPose>();
                        for (UtmPose i : utmPoses)
                        {
                            remainingOnboat.add(i);
                            System.out.println("allwp added: " + i);
                            System.out.println("on boat: " + allWaypointsSent.get(0));
                        }
                        for (int i = 0; i < allWaypointsSent.size();i++)
                        {
                            if (remainingOnboat.contains(allWaypointsSent.get(i)) == false)
                            {
                                System.out.println("allwp" + allWaypointsSent.get(i));
                                IconFactory mIconFactory = IconFactory.getInstance(context);
//                                Drawable mboundry = ContextCompat.getDrawable(context, R.drawable.boundary);
//                                final Icon Iboundry = mIconFactory.fromDrawable(mboundry);
                                //Drawable completedraw = ContextCompat.getDrawable(context, R.drawable`.greenmarker);

                                //Icon completed = mIconFactory.fromDrawable(completedraw);

                                //markerList.get(i).setIcon(completed);
                                //markerList.get(i).setIcon(new Icon());
                            }
                        }
                    }

                    @Override
                    public void failed(FunctionError functionError) {

                    }
                });
            }
        };
        //thread.start();
    }
    public void onLoadWaypointLayout()
    {
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        waypointlayout = (LinearLayout) findViewById(R.id.relativeLayout_sensor);
        waypointregion = inflater.inflate(R.layout.waypoint_layout, waypointlayout);
        waypointButton = (ToggleButton) waypointregion.findViewById(R.id.waypointButton);
        deleteWaypoint = (Button) waypointregion.findViewById(R.id.waypointDeleteButton);
        pauseWP = (ToggleButton) waypointregion.findViewById(R.id.pause);
        startWaypoints = (Button) waypointregion.findViewById(R.id.waypointStartButton);
        speed = (Switch) waypointregion.findViewById(R.id.switch1);
        waypointInfo = (TextView) waypointregion.findViewById(R.id.waypoints_waypointstatus);
        speed.setTextOn("Slow");
        speed.setTextOff("Normal");
        Button dropWP = (Button) waypointregion.findViewById(R.id.waypointDropWaypointButton);

        dropWP.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (currentBoat == null)
//                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (currentBoat == null) {
                                Toast.makeText(getApplicationContext(), "No Boat Connected", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if (currentBoat.getLocation() == null)
                            {
                                Toast.makeText(getApplicationContext(), "Waiting on boat GPS", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if (mMapboxMap == null)
                            {
                                Toast.makeText(getApplicationContext(), "Map still loading", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    });
  //              }
                //if (currentBoat != null || currentBoat.getLocation() != null || mMapboxMap != null)
                {
                    waypointList.add(currentBoat.getLocation());
                    markerList.add(mMapboxMap.addMarker(new MarkerOptions().position(currentBoat.getLocation()).title(Integer.toString(WPnum))));
                    Waypath = mMapboxMap.addPolyline(new PolylineOptions().addAll(waypointList).color(Color.GREEN).width(5));
                }
            }
        });

        waypointButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread() {
                    public void run() {
                        if (waypointButton.isChecked()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Use long press to add waypoints", Toast.LENGTH_LONG).show();
                                }
                            });
                            startDraw = false;
                            if (waypointLayoutEnabled == false) {
                                drawPoly.setClickable(false);
                            }

                        } else {
                            if (waypointLayoutEnabled == false) {
                                drawPoly.setClickable(true);
                            }
                        }
                        if (!waypointlistener) {
                            currentBoat.returnServer().addWaypointListener(wl, new FunctionObserver<Void>() {
                                @Override
                                public void completed(Void aVoid) {
                                    waypointlistener = true;
                                }

                                @Override
                                public void failed(FunctionError functionError) {
                                    waypointlistener = false;
                                }
                            });
                        }


                        //System.out.println(currentBoat.returnServer().getGains(0);)


                    }
                };
                thread.start();
            }
        });

        pauseWP.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread() {
                    public void run() {

                                    /* If pauseWp.isChecked is false that means boat is currently paused
                         * if pauseWP.isChecked is true that means boat is running */
                        if (pauseWP.isChecked()) {
                            Auto = false;
                            isWaypointsRunning = false;
                        } else {
                            isWaypointsRunning = true;
                            Auto = true;
                        }
                        if (Auto) {
                            currentBoat.returnServer().setAutonomous(true, null);
                        } else {
                            currentBoat.returnServer().setAutonomous(false, null);
                        }
                    }
                };
                thread.start();
            }
        });
        deleteWaypoint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                isWaypointsRunning = false;
                pauseWP.setChecked(false);
                stopWaypoints = true;
                isCurrentWaypointDone = true;
                Thread thread = new Thread() {
                    public void run() {
                        currentBoat.returnServer().setAutonomous(false, null);
                        currentBoat.returnServer().isAutonomous(new FunctionObserver<Boolean>() {
                            @Override
                            public void completed(Boolean aBoolean) {
                                isAutonomous = aBoolean;
                                Log.i(logTag, "isAutonomous: " + isAutonomous);
                            }

                            @Override
                            public void failed(FunctionError functionError) {

                            }
                        });
                    }
                };
                thread.start();
                try
                {
                    //List<Annotation> anot = mMapboxMap.getAnnotations();
//                    if (anot.contains(markerList.get(0)))
//                    {
//                        System.out.println("in list");
//                    }
//                    System.out.println(anot);
//                    System.out.println("marker list size: " + markerList.size());

                    for (Marker m : markerList) {
                        mMapboxMap.removeMarker(m);
                    }
                    //delete markers not deleting has to do with mapbox

                    Waypath.remove();
                    waypointList.clear();
                    WPnum = 0;
                    N_waypoint = 0;

                    mlogger.info(new JSONObject()
                            .put("Time", sdf.format(d))
                            .put("Delete", new JSONObject()
                                    .put("WP_num", waypointList.size())));
                }
                catch (JSONException e) {
                }

            }
        });
        startWaypoints.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (!pauseWP.isChecked() && isWaypointsRunning)
                isFirstWaypointCompleted = false;
                if (pauseWP.isChecked()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Currently waypoints are paused, by pressing start waypoints the boat will restart the path. \n If you want to resume waypoints press cancel then toggle resume ")
                            .setCancelable(false)
                            .setPositiveButton("Restart Path", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    pauseWP.setChecked(false);
                                    startWaypoints();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    return;
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                if (pauseWP.isChecked() == false) {
                    startWaypoints();
                }

            }
        });

        speed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Thread thread = new Thread() {
                    public void run() {
                        if (currentBoat != null) {
                            if (speed.isChecked()) {
                                currentBoat.returnServer().setGains(0, low_tPID, null);
                                currentBoat.returnServer().setGains(5, low_rPID, null);
                            } else {
                                currentBoat.returnServer().setGains(0, tPID, null);
                                currentBoat.returnServer().setGains(5, rPID, null);
                            }
                            currentBoat.returnServer().getGains(0, new FunctionObserver<double[]>() {
                                @Override
                                public void completed(double[] doubles) {
                                    Log.i(logTag, "PID: " + doubles[0]);
                                }

                                @Override
                                public void failed(FunctionError functionError) {

                                }
                            });
                        }
                    }
                };
                thread.start();
            }
        });
    }
    public void onLoadRegionLayout() {
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        regionlayout = (LinearLayout) findViewById(R.id.relativeLayout_sensor);
        waypointregion = inflater.inflate(R.layout.region_layout, regionlayout);
        startRegion = (Button) regionlayout.findViewById(R.id.region_start);
        drawPoly = (ImageButton) regionlayout.findViewById(R.id.region_draw);
        removeMap = (Button) regionlayout.findViewById(R.id.regIon_remove);
        preimeter = (Button) regionlayout.findViewById(R.id.region_perimeter);
        clearRegion = (Button) regionlayout.findViewById(R.id.region_clear);
        drawPoly.setBackgroundResource(R.drawable.draw_icon);

        startRegion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread() {
                    public void run() {
                        //if (currentBoat.isConnected() == true) {
                        ArrayList<LatLng> flatlist = new ArrayList<LatLng>();
                        System.out.println("wpsize + " + spiralWaypoints.size());
                        for (ArrayList<LatLng> list : spiralWaypoints) {
                            for (LatLng wpoint : list) {
                                flatlist.add(wpoint);

                            }
                        }
                        if (currentBoat.getConnected() == true) {
                            checktest = true;
                            JSONObject JPose = new JSONObject();
                            if (flatlist.size() > 0) {

                                //Convert all UTM to latlong
                                UtmPose tempUtm = convertLatLngUtm(flatlist.get(flatlist.size() - 1));

                                waypointStatus = tempUtm.toString();

                                //Confused now, this does same thing as whats below uncomment if needed
                                //currentBoat.addWaypoint(tempUtm.pose, tempUtm.origin);
                                wpPose = new UtmPose[flatlist.size()];
                                synchronized (_waypointLock) {
                                    //wpPose[0] = new UtmPose(tempUtm.pose, tempUtm.origin);
                                    for (int i = 0; i < flatlist.size(); i++) {
                                        wpPose[i] = convertLatLngUtm(flatlist.get(i));
                                    }
                                    tempPose = wpPose;
                                }

                                currentBoat.returnServer().setAutonomous(true, new FunctionObserver<Void>() {
                                    @Override
                                    public void completed(Void aVoid) {
                                        Log.i(logTag, "Autonomy set to true");
                                    }

                                    @Override
                                    public void failed(FunctionError functionError) {
                                        Log.i(logTag, "Failed to set autonomy");
                                    }
                                });
                                checkAndSleepForCmd();
                                currentBoat.returnServer().isAutonomous(new FunctionObserver<Boolean>() {
                                    @Override
                                    public void completed(Boolean aBoolean) {
                                        isAutonomous = aBoolean;
                                        Log.i(logTag, "isAutonomous: " + isAutonomous);
                                    }

                                    @Override
                                    public void failed(FunctionError functionError) {

                                    }
                                });
                                currentBoat.returnServer().startWaypoints(wpPose, "POINT_AND_SHOOT", new FunctionObserver<Void>() {
                                    @Override
                                    public void completed(Void aVoid) {

                                        isWaypointsRunning = true;
                                        System.out.println("startwaypoints - completed");
                                    }

                                    @Override
                                    public void failed(FunctionError functionError) {
                                        isCurrentWaypointDone = false;
                                        System.out.println("startwaypoints - failed");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "Failed to start waypoints, you may be using a too large region", Toast.LENGTH_LONG).show();
                                            }
                                        });

                                        // = waypointStatus + "\n" + functionError.toString();
                                        // System.out.println(waypointStatus);
                                    }
                                });
                                currentBoat.returnServer().getWaypoints(new FunctionObserver<UtmPose[]>() {
                                    @Override
                                    public void completed(UtmPose[] wps) {
                                        for (UtmPose i : wps) {
                                            System.out.println("waypoints" + i.toString());
                                        }
                                    }

                                    @Override
                                    public void failed(FunctionError functionError) {

                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Please Select Waypoints", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            try {

                                mlogger.info(new JSONObject()
                                        .put("Time", sdf.format(d))
                                        .put("startWP", new JSONObject()
                                                .put("WP_num", wpPose.length)
                                                .put("AddWaypoint", Auto)));
                            } catch (JSONException e) {
                                Log.w(logTag, "Failed to log startwaypoint");
                            } catch (Exception e) {

                            }

                        }
                    }
                };
                thread.start();

            }
        });
        drawPoly.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startDraw == false) {
                    startDraw = true;
                    drawPoly.setBackgroundResource(R.drawable.draw_icon2);
                } else {
                    startDraw = false;
                    drawPoly.setBackgroundResource(R.drawable.draw_icon);
                }
            }
        });
        IconFactory mIconFactory = IconFactory.getInstance(this);
        Drawable mboundry = ContextCompat.getDrawable(this, R.drawable.boundary);
        final Icon Iboundry = mIconFactory.fromDrawable(mboundry);
        Drawable mboat = ContextCompat.getDrawable(this, R.drawable.pointarrow);
        Icon Iboat = mIconFactory.fromDrawable(mboat);

        removeMap.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    touchpointList.remove(touchpointList.indexOf(lastAdded.get(lastAdded.size() - 1)));
                    lastAdded.remove(lastAdded.size() - 1);

                    Boundry.remove();
                    for (Marker a : boundryList) {
                        a.remove(); //remove all markers in boundry list from map
                    }
                    boundryList.clear();
                    for (LatLng i : touchpointList) {
                        boundryList.add(mMapboxMap.addMarker(new MarkerOptions().position(i).icon(Iboundry))); //add all elements to boundry list
                    }

                    if (touchpointList.size() > 2) {
                        PolygonOptions poly = new PolygonOptions().addAll(touchpointList).strokeColor(Color.BLUE).fillColor(Color.parseColor("navy"));
                        poly.alpha((float) .6);
                        Boundry = mMapboxMap.addPolygon(poly);
                    }
                    PolyArea area = new PolyArea();


                    if (touchpointList.size() > 0) {
                        System.out.println("spiral called");
                        spiralWaypoints = area.createSmallerPolygonsFlat(touchpointList);
                        System.out.println("spiral " + spiralWaypoints.size());
                        drawSmallerPolys(spiralWaypoints);
                    }
                } catch (Exception e) {
                    //System.out.println(e.toString());
                }
            }
        });

        preimeter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latlongloc != null) {
                    LatLng point = new LatLng(latlongloc.latitudeValue(SI.RADIAN) * 180 / Math.PI, latlongloc.longitudeValue(SI.RADIAN) * 180 / Math.PI);
                    drawPolygon(point, Iboundry);
                }
            }
        });
    }
}


//
//class
