package com.diogoperes.mobilecontrolstation;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;

import android.support.v7.app.ActionBarDrawerToggle;


import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.diogoperes.mobilecontrolstation.fragments.ControllerFragment;
import com.diogoperes.mobilecontrolstation.fragments.MissionsFragment;
import com.diogoperes.mobilecontrolstation.utils.CustomViewPager;
import com.diogoperes.mobilecontrolstation.utils.UVListAdapter;
import com.diogoperes.mobilecontrolstation.utils.slidingmenu.NavDrawerItem;
import com.diogoperes.mobilecontrolstation.utils.slidingmenu.NavDrawerListAdapter;

import org.mavlink.MAVLinkReader;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.messages.ardupilotmega.msg_ping;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity /*implements OnMapReadyCallback*/ {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;
    private CoordinatorLayout coordinatorLayout;


    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter_navDrawer;

    private ControllerFragment controllerFragment = new ControllerFragment();
    private MissionsFragment missionsFragment = new MissionsFragment();

    private boolean isActive = false;

    Button disconnectButton, armButton;
    ImageButton navDrawerToggleButton;

    private int sysID = 7, componentId = 1, sequence=1;

    private DatagramSocket serverSocket, clientSocket;
    private String mode = "";
    private int SERVERPORT_RECEIVE = 14550;
    private int SERVERPORT_SEND = -1;
    private String SERVERIP = "192.168.2.108";
    private InetAddress serverAddress;
    private byte[] receiveData = new byte[263];
    Thread threadConnection;
    private boolean connectedToDrone = false, droneIsArmed = false;
    private TextView modeIDTextView;

    private ArrayList<UV> uv_list = new ArrayList<UV>();

    Button btnClosePopup;

    private UV uv_selected;
    private ClientConnection clientConnection;
    private MessageSender messageSender;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        //Log.d("VIEWPAGER: ", "" + viewPager);
        viewPager.setPagingEnabled(false);
        setupViewPager(viewPager);
        //viewPager.requestFocus();




        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        clientConnection = new ClientConnection(this);
        clientConnection.startConnection();

        messageSender = new MessageSender(this);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        int SDK_INT = Build.VERSION.SDK_INT;
        if(SDK_INT > 8 ){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        //viewPager.requestFocus();

        /*modeIDTextView = (TextView) findViewById(R.id.modeIDTextView);

        disconnectButton = (Button) findViewById(R.id.disconnectButton);
        disconnectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                connectToDrone();
            }
        });

        //connectToDrone();

        armButton = (Button) findViewById(R.id.armButton);
        armButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                armDisarm();
            }
        });
*/
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/




        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //mDrawerLayout.clearFocus();


        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);



        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1), true, ""+ uv_list.size()));
        // Find People
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Photos
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Communities, Will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
        // Pages
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        // What's hot, We  will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));



        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list_uvs adapter
        adapter_navDrawer = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter_navDrawer);

        // enabling action bar app icon and behaving it as toggle button
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons

                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        /*if (savedInstanceState == null) {
            //on first time display view for first nav item
            //displayView(0);
        }*/





        navDrawerToggleButton = (ImageButton) findViewById(R.id.toggleDrawer);
        navDrawerToggleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //initiatePopupWindowUvList();
                if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);

                } else {
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }

            }
        });

        /*mDrawerLayout.clearFocus();
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        coordinatorLayout.requestFocus();*/
        mDrawerLayout.setVisibility(View.GONE); //to let focus pass
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {


            }
            @Override
            public void onDrawerOpened(View drawerView) {

            }
            @Override
            public void onDrawerClosed(View drawerView) {
                mDrawerLayout.setVisibility(View.GONE);
            }
            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }



    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    initiatePopupWindowUvList(view);
                    mDrawerLayout.closeDrawer(mDrawerList);
                    break;

            }
            //displayView(position);
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    public void connectToDrone(){
//
//        if(serverSocket==null){
//            //Creates the server adress
//            try {
//                serverAddress = InetAddress.getByName(SERVERIP);
//            } catch (UnknownHostException e) {
//                e.printStackTrace();
//            }
//            //Open the UDP Port
//            try {
//                serverSocket = new DatagramSocket(SERVERPORT_RECEIVE);
//
//            } catch (SocketException e) {
//                e.printStackTrace();
//            }
//
//
//        }
//
//        if(!connectedToDrone){
//            connectedToDrone=true;
//            threadConnection = new Thread() {
//                @Override
//                public void run() {
//                    try {
//                        //Creating a Packet to receive the data
//                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//                        ByteArrayInputStream bais;
//                        DataInputStream dis;
//                        while(connectedToDrone){
//                            //Socket receives byte Packets
//
//
//                            serverSocket.receive(receivePacket);
//
//
//                            receiveData = receivePacket.getData();
//
//                            SERVERPORT_SEND = receivePacket.getPort();
//
//
//                            bais = new ByteArrayInputStream(receiveData);
//                            dis = new DataInputStream(bais);
//
//
//                            MAVLinkReader MAVReader = new MAVLinkReader(dis);
//
//                            MAVLinkMessage msg = MAVReader.getNextMessage();
//
//                            ping();
//
//                            if(msg != null){
//                                System.err.println("SysID:" + msg.sysId + "		CompID:" + msg.componentId + "		Seq:" + msg.sequence + "	" +msg.toString());
//                                if(msg.toString().contains("MAVLINK_MSG_ID_HEARTBEAT")){
//                                    checkHeartbeat(msg);
//                                }
//                            }else{
//                                Toast.makeText(getApplicationContext(), "No Connection",
//                                        Toast.LENGTH_LONG).show();
//                                connectedToDrone=false;
//                                serverSocket.close();
//                                Intent myIntent = new Intent(MainActivity.this, MainActivityOld.class);
//                                MainActivity.this.startActivity(myIntent);
//                                finish();
//                            }
//                            dis.close();
//                        }
//                    } catch (SocketException e) {
//                        e.printStackTrace();
//                    } catch (UnknownHostException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//            threadConnection.start();
//            disconnectButton.setText("DISCONNECT");
//
//        }else{
//            connectedToDrone=false;
//            serverSocket.close();
//            Intent myIntent = new Intent(MainActivity.this, MainActivityOld.class);
//            MainActivity.this.startActivity(myIntent);
//            finish();
//            //disconnectButton.setText("CONNECT");
//            //connectedToDrone=false;
//        }
//
//
//
//    }
//
//    private void checkHeartbeat(MAVLinkMessage msg) {
//        final MAVLinkMessage m = msg;
//
//        runOnUiThread(new Runnable() {
//            //private MAVLinkMessage msg = m;
//
//            @Override
//            public void run() {
//                //CHECK ARM
//                String msgStr=m.toString();
//                String baseMode = msgStr.substring(msgStr.indexOf("base_mode=") + "base_mode=".length(), msgStr.indexOf("  system_status"));
//                if(Integer.parseInt(baseMode) == 81 && droneIsArmed ){
//                    armButton.setText("ARM");
//                    droneIsArmed=false;
//                }else if(Integer.parseInt(baseMode) == 209 && !droneIsArmed ){
//                    armButton.setText("DISARM");
//                    droneIsArmed=true;
//                }
//                //CHECK MODE
//                String customMode = msgStr.substring(msgStr.indexOf("custom_mode=") + "custom_mode=".length(), msgStr.indexOf("  type"));
//                if(Integer.parseInt(customMode) == 0 && !mode.equals("STABILIZED")){
//                    modeIDTextView.setText("STABILIZED");
//                }else if(Integer.parseInt(customMode) == 2 && !mode.equals("ALT_HOLT")){
//                    modeIDTextView.setText("ALT_HOLT");
//                }
//            }
//        });
//
//    }
//
//
//
//    public void ping(){
//        byte[] sendData = new byte[263];
//        try {
//            clientSocket = new DatagramSocket();
//
//            msg_ping msgPing  = new msg_ping (sysID, componentId);
//            msgPing.time_usec = System.nanoTime(); //Arms / Disarms a component
//            msgPing.target_system=1;
//            msgPing.target_component=0;
//            msgPing.seq = 0;
//            sendData = msgPing.encode();
//
//            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVERPORT_SEND );
//
//            clientSocket.send(sendPacket);
//            clientSocket.close();
//        } catch (SocketException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter_viewPager = new ViewPagerAdapter(getSupportFragmentManager());
        adapter_viewPager.addFragment(controllerFragment, "CONTROLLER");
        adapter_viewPager.addFragment(missionsFragment, "MISSIONS");

        viewPager.setAdapter(adapter_viewPager);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public UV getUvSelected() {
        return uv_selected;
    }

    public void setUvSelected(UV uv_selected) {
        this.uv_selected = uv_selected;
    }

    public ControllerFragment getControllerFragment() {
        return controllerFragment;
    }

    public MissionsFragment getMissionsFragment() {
        return missionsFragment;
    }

    public MessageSender getMessageSender() {
        return messageSender;
    }

    public ClientConnection getClientConnection() {
        return clientConnection;
    }



    public ArrayList<UV> getUv_list() {
        return uv_list;
    }

    public void addUV(UV uv){
        uv_list.add(uv);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                navDrawerItems.get(0).setCount("" + uv_list.size());
                adapter_navDrawer.notifyDataSetChanged();
                mDrawerList.setAdapter(adapter_navDrawer);
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        isActive = false;
    }

    public void onResume() {
        super.onResume();
        isActive = true;
    }

    public boolean isActive() {
        return isActive;
    }


    private PopupWindow pwindo;


    ListView list_uvs;
    String[] itemname ={
            "Safari",
            "Camera",
            "Global",
            "FireFox",
            "UC Browser",
            "Android Folder",
            "VLC Player",
            "Cold War"
    };

    Integer[] imgid={
            R.mipmap.ic_marker_quad,
            R.mipmap.ic_marker_quad,
            R.mipmap.ic_marker_quad,
            R.mipmap.ic_marker_quad,
            R.mipmap.ic_marker_quad,
            R.mipmap.ic_marker_quad,
            R.mipmap.ic_marker_quad,
            R.mipmap.ic_marker_quad,
    };

    private void initiatePopupWindowUvList(View anchorView) {
        View popupView = getLayoutInflater().inflate(R.layout.screen_popup, null); //get popup view

        Display display = getWindowManager().getDefaultDisplay(); //get size of device window
        Point size = new Point();
        display.getSize(size);

        // Example: If you have a TextView inside `popup_layout.xml`
        //TextView tv = (TextView) popupView.findViewById(R.id.txtView);
        //int location[] = new int[2];
        // Get the View's(the one that was clicked in the Fragment) location
        //anchorView.getLocationOnScreen(location);
        // Using location, the PopupWindow will be displayed right under anchorView
        /*popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY,
                location[0], location[1] + anchorView.getHeight());*/

//        pwindo = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, size.y-size.y/6); //create window with custom size
        pwindo = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); //create window with custom size
        pwindo.setFocusable(true);// If the PopupWindow should be focusable
        pwindo.setBackgroundDrawable(new ColorDrawable());// If you need the PopupWindow to dismiss when when touched outside
        pwindo.showAtLocation(anchorView, Gravity.CENTER,
                0, 0);



        final UVListAdapter adapter_uvList=new UVListAdapter(this, uv_list); //create list_uvs adapter
        list_uvs =(ListView)popupView.findViewById(R.id.list); //find list_uvs in xml
        list_uvs.setAdapter(adapter_uvList);   //set adapter
        list_uvs.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                uv_selected = (UV) adapter_uvList.getItem(position);
                pwindo.dismiss();
                //Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();

            }
        });
        //pwindo.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);

        btnClosePopup = (Button) popupView.findViewById(R.id.btn_close_popup);
        btnClosePopup.setOnClickListener(cancel_button_click_listener);

    }

    private View.OnClickListener cancel_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            pwindo.dismiss();

        }
    };



}
