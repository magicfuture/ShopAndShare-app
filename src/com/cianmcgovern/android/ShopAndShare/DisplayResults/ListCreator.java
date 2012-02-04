package com.cianmcgovern.android.ShopAndShare.DisplayResults;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import com.cianmcgovern.android.ShopAndShare.AddItem;
import com.cianmcgovern.android.ShopAndShare.EditItem;
import com.cianmcgovern.android.ShopAndShare.Item;
import com.cianmcgovern.android.ShopAndShare.R;
import com.cianmcgovern.android.ShopAndShare.Results;
import com.cianmcgovern.android.ShopAndShare.Share;
import com.cianmcgovern.android.ShopAndShare.ShopAndShare;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

/**
 * Activity to display the results from the native code execution
 * Displays two arrays with corresponding elements in two ListViews as specified in display_results.xml
 * 
 * @author Cian Mc Govern
 *
 */
public class ListCreator extends ListActivity{

    private Button mAddItem;
    private Button mShare;
    private Context mContext;

    public void onCreate(Bundle savedInstance){

        super.onCreate(savedInstance);
        mContext = this;
        
        setContentView(R.layout.display_results);
        View v = getLayoutInflater().inflate(R.layout.display_results_buttons_bottom, null);
        mAddItem = (Button)v.findViewById(R.id.addButton);
        mAddItem.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent in = new Intent(ShopAndShare.sContext,AddItem.class);
                startActivity(in);
                finish();
            }

        });
        mShare = (Button)v.findViewById(R.id.shareButton);
        mShare.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                // Only allow the user to continue if there is network access
                if(isNetworkAvailable()) {
                    Intent in = new Intent(ShopAndShare.sContext,Share.class);
                    startActivity(in);
                }
                else
                    new AlertDialog.Builder(mContext)
                    .setTitle("Network unavailable")
                    .setMessage("Network access must be available to use this feature")
                    .show();
            }

        });
        this.getListView().addFooterView(v);

        ArrayList<Item> products = new ArrayList<Item>();

        Iterator i = Results.getInstance().getProducts().entrySet().iterator();

        while(i.hasNext()){
            Map.Entry x = (Map.Entry)i.next();
            products.add((Item)x.getValue());
        }

        ItemAdapter adap = new ItemAdapter(this,R.layout.display_results_row,products);
        setListAdapter(adap);
    }

    /**
     * Returns a boolean on whether network access is available or not
     * 
     * Code obtained from: http://stackoverflow.com/questions/4238921/android-detect-whether-there-is-an-internet-connection-available
     * 
     * @return activeNetworkInfo
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        Item product = (Item) getListAdapter().getItem(position);
        Intent in = new Intent(ShopAndShare.sContext,EditItem.class);
        in.putExtra("Product", product.getProduct());
        startActivity(in);
        finish();
    }

    // Create options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.layout.results_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
        case R.id.exit:
            finish();
            return true;
        case R.id.save:
            try {
                Results.getInstance().saveCurrentResults();
                finish();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}