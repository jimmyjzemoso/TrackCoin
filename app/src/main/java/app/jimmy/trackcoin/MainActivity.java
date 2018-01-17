package app.jimmy.trackcoin;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private RecyclerView mMainRecyclerView,mAllCoinRecyclerView;
    private RecyclerView.Adapter mMainAdapter,mAllCoinAdapter;
    private RecyclerView.LayoutManager mMainLayoutManager,mAllCoinLayoutManager;
    private ArrayList<MainDataSet> mainDataSet = new ArrayList<>();

    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress);
        mMainRecyclerView = findViewById(R.id.recycler_view);
        mAllCoinRecyclerView = findViewById(R.id.all_coins);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mMainRecyclerView.setHasFixedSize(true);
        mAllCoinRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mMainLayoutManager = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        mMainRecyclerView.setLayoutManager(mMainLayoutManager);

        mAllCoinLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        mAllCoinRecyclerView.setLayoutManager(mAllCoinLayoutManager);

        // specify an adapter (see also next example)
        mMainAdapter = new MainRcAdapter(mainDataSet,this);
        mAllCoinAdapter = new AllCoinAdapter(mainDataSet, this);
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
                        // Display the first 500 characters of the response string.
//                        text.setText("Response is: "+ response);
                        progressBar.setVisibility(View.GONE);
                        for( int i = 0 ; i < response.length() ; i++ ){
                            JSONObject obj = response.optJSONObject(i);
                            mainDataSet.add(new MainDataSet(obj.optInt("Id"),obj.optString("name"),obj.optString("ImageUrl"),obj.optString("price_usd")));
                        }
                        mMainAdapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG,error.getMessage());
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
