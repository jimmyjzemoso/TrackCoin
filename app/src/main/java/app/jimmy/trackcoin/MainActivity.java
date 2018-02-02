package app.jimmy.trackcoin;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    private ProgressBar progressBar;
    private RecyclerView mMainRecyclerView,mAllCoinRecyclerView;
    private RecyclerView.Adapter mMainAdapter,mAllCoinAdapter;
    private RecyclerView.LayoutManager mMainLayoutManager,mAllCoinLayoutManager;
    private ArrayList<CoinDataSet> mainCoinData = new ArrayList<>();
    private ArrayList<CoinDataSet> allCoinData = new ArrayList<>();
    private View addNewCoins;
    private GestureDetectorCompat mDetector;
    private int screenHeight;
    private TextView swipeView;

    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress);
        mMainRecyclerView = findViewById(R.id.recycler_view);
        mAllCoinRecyclerView = findViewById(R.id.all_coins);
        addNewCoins = findViewById(R.id.add_coin_parent);
        swipeView = findViewById(R.id.add_coin_scroll);

        swipeView.post(new Runnable() {
            @Override
            public void run() {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int viewHeight = swipeView.getHeight();
                screenHeight = displayMetrics.heightPixels - 2 * viewHeight;
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) addNewCoins.getLayoutParams();
                lp.setMargins(lp.leftMargin,screenHeight,lp.rightMargin,lp.bottomMargin);
                addNewCoins.setLayoutParams(lp);
            }
        });

        addNewCoins.setOnTouchListener(this);

        mDetector = new GestureDetectorCompat(this, new AddCoinGestureListener());

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mMainRecyclerView.setHasFixedSize(true);
        mAllCoinRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mMainLayoutManager = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        mMainRecyclerView.setLayoutManager(mMainLayoutManager);

        mAllCoinLayoutManager = new LinearLayoutManager(this);
        mAllCoinRecyclerView.setLayoutManager(mAllCoinLayoutManager);

        // specify an adapter (see also next example)
        mMainAdapter = new MainRcAdapter(mainCoinData,this);
        mAllCoinAdapter = new AllCoinAdapter(allCoinData, this);

        mMainRecyclerView.setAdapter(mMainAdapter);
        mAllCoinRecyclerView.setAdapter(mAllCoinAdapter);

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mMainRecyclerView);


        getResponse(getString(R.string.coinmarketcap_url));
        FloatingActionButton refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getResponse(getString(R.string.coinmarketcap_url));
            }
        });
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

    private void getResponse(String url){
        RequestQueue queue = Volley.newRequestQueue(this);

        progressBar.setVisibility(View.VISIBLE);
// Request a string response from the provided URL.
        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        progressBar.setVisibility(View.GONE);
                        Log.i(TAG,"Response: "+response);
                        for( int i = 0 ; i < response.length() ; i++ ){
                            JSONObject obj = response.optJSONObject(i);
                            allCoinData.add(new CoinDataSet(obj.optInt("Id"),obj.optString("name"),obj.optString("ImageUrl"),obj.optString("price_usd")));
                        }
                        mMainAdapter.notifyDataSetChanged();
                        mAllCoinAdapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                for(int i = 0; i<20;i++){
                    allCoinData.add(new CoinDataSet(i,"Dummy coin","","1000"));
                }
                Log.e(TAG,error.getMessage());
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()){
            case MotionEvent.ACTION_UP:{

//                if()
            }
        }
        return this.mDetector.onTouchEvent(motionEvent);
    }

    class AddCoinGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            Log.v(TAG,"Action Down");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) addNewCoins.getLayoutParams();
            int newPosY = Math.round(e2.getRawY() - e1.getY());

            if(marginLayoutParams.topMargin <= 0 && e2.getRawY() - e1.getRawY() < 0 ) {
                newPosY = 0;
            }

            marginLayoutParams.setMargins(marginLayoutParams.leftMargin,
                    newPosY,
                    marginLayoutParams.rightMargin,
                    marginLayoutParams.bottomMargin
            );
            Log.v(TAG,"On Scroll e1.y: "+e1.getRawY()+" e2.y: "+e2.getRawY()+" diffY :"+distanceY);
            addNewCoins.setLayoutParams(marginLayoutParams);

            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.v(TAG,"Fling Detected");
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) addNewCoins.getLayoutParams();

            if( (e2.getRawY() - e1.getRawY() < 0) ) {
                mlp.setMargins(mlp.leftMargin, 0, mlp.rightMargin, 100);
//                Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.swipe_up);
//                addNewCoins.startAnimation(hyperspaceJumpAnimation);
            }else{
                mlp.setMargins(mlp.leftMargin, 1575, mlp.rightMargin, 0);
            }
            return false;
        }
    }
}
