package com.vblitzmaker.appremover;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AppAdapter.OnItemClickListener {

    private static final String TAG = "AppRemover";
    private static final int  DELETE_REQUEST_CODE = 1;
    private ArrayList<AppItem> dbAppList, installedAppList,commonList;
    private RecyclerView recyclerView;
    private Button btnScan;
    private TextView tvEmptyList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbAppList = new ArrayList<>();
        commonList = new ArrayList<>();
        installedAppList=new ArrayList<>();
        recyclerView = (RecyclerView)findViewById(R.id.rvApps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tvEmptyList = (TextView)findViewById(R.id.tvEmptyList);
        tvEmptyList.setVisibility(View.INVISIBLE);
        btnScan = (Button)findViewById(R.id.btnScan);
        btnScan.setEnabled(false);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compareItems();
            }
        });
        getInstalledPackages();
        registerDatabase();
    }

    /**
     * Function to compare the database list and installed packages
     */
    private void compareItems() {
        commonList.clear();
        for (AppItem installedItem:
             installedAppList) {
            for (AppItem harmful: dbAppList
                 ) {
                if(installedItem.p_name.equals(harmful.p_name)) {
                    commonList.add(installedItem);
                }
            }
        }
        //Create the adapter
        AppAdapter adapter = new AppAdapter(this,commonList);
        adapter.setOnClickListener(this);
        recyclerView.setAdapter(adapter);

        if(commonList.size()==0) {
            tvEmptyList.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Function to populate the list of installed applications
     */
    private void getInstalledPackages() {
        installedAppList.clear();
        List<PackageInfo> packageList = getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packageList.size();i++) {
            PackageInfo info=packageList.get(i);
            if((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
            {
                installedAppList.add(new AppItem(info.applicationInfo.loadLabel(getPackageManager()).toString(),
                        info.packageName,info.applicationInfo.loadIcon(getPackageManager())));
            }
        }
    }

    /**
     * Function to register the connection to database
     */
    private void registerDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                dbAppList.clear();
                Log.d(TAG, "onDataChange: count: "+ dataSnapshot.getChildrenCount());
                HashMap<String,String> tmpMap=null;
                for (DataSnapshot itemSnapShot:dataSnapshot.getChildren()
                     ) {
                    Log.d(TAG, "onDataChange item: "+itemSnapShot.getKey());
                    tmpMap = (HashMap<String,String>)itemSnapShot.getValue();
                    dbAppList.add(new AppItem(tmpMap.get("a_name"),tmpMap.get("p_name")));
                }
                btnScan.setEnabled(true);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
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

    @Override
    public void onItemClick(View view, int position) {
        //Create the delete intent an start activity for result
        Intent deleteIntent = new Intent(Intent.ACTION_DELETE);
        deleteIntent.setData(Uri.parse("package:"+commonList.get(position).p_name));
        deleteIntent.putExtra(Intent.EXTRA_RETURN_RESULT,true);
        startActivityForResult(deleteIntent,DELETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==DELETE_REQUEST_CODE)
        {
            if(resultCode==RESULT_OK) {
                Log.d(TAG, "onActivityResult: Uninstall Success");
                getInstalledPackages();
                compareItems();
                Toast.makeText(this, "Uninstall Success", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
