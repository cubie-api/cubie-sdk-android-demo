package com.cubie.openapi.demo;

import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cubie.openapi.demo.model.Item;
import com.cubie.openapi.demo.model.Shop;
import com.cubie.openapi.sdk.Cubie;
import com.cubie.openapi.sdk.CubieServiceCallback;
import com.cubie.openapi.sdk.activity.CubieBaseActivity;
import com.cubie.openapi.sdk.exception.CubieException;
import com.cubie.openapi.sdk.service.CubieTransactionRequest;

public class ShopActivity extends CubieBaseActivity {

  private static class ItemListAdapter extends BaseAdapter {

    private static class ViewHolder {
      TextView nameView;
    }

    @Override
    public int getCount() {
      return Shop.listItems().size();
    }

    @Override
    public Item getItem(int position) {
      return Shop.getItemAt(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder viewHolder;
      if (convertView == null) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        convertView = inflater.inflate(R.layout.item_item, parent, false);
        viewHolder = new ViewHolder();
        viewHolder.nameView = (TextView) convertView.findViewById(R.id.nameView);
        convertView.setTag(viewHolder);
      } else {
        viewHolder = (ViewHolder) convertView.getTag();
      }

      final Item friend = getItem(position);
      viewHolder.nameView.setText(friend.getName());
      return convertView;
    }
  }

  enum RequestCode {
    BUY,
  }

  public static Intent createIntent(Activity activity) {
    final Intent intent = new Intent(activity, ShopActivity.class);
    return intent;
  }

  private static final String TAG = ShopActivity.class.getSimpleName();

  private void createTransaction(String sku, String orderId, long purchaseTime) {
    final Item item = Shop.getItem(sku);
    final CubieTransactionRequest request = new CubieTransactionRequest(orderId,
        sku,
        item.getCurrency(),
        item.getPrice().toString(),
        new Date(purchaseTime),
        null);
    Log.d(TAG, "createTransaction: " + request);

    Cubie.getService().createTransaction(request, new CubieServiceCallback<Void>() {

      @Override
      public void done(Void object, CubieException e) {
        if (e != null) {
          Log.e(TAG, "createTransaction onFailure", e);
        } else {

          Log.d(TAG, "createTransaction onSuccess");
          Toast.makeText(ShopActivity.this,
              "Bought a " + item.getName().toLowerCase(Locale.US),
              Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == RequestCode.BUY.ordinal()) {
      if (resultCode == RESULT_OK && data != null) {
        final String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
        try {
          final JSONObject jo = new JSONObject(purchaseData);
          final String sku = jo.getString("productId");
          final String orderId = jo.getString("orderId");
          final long purchaseTime = jo.getLong("purchaseTime");
          createTransaction(sku, orderId, purchaseTime);
        } catch (final JSONException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  protected void onBaseCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_shop);

    final ListView itemList = (ListView) findViewById(R.id.listView);
    final ItemListAdapter itemListAdapter = new ItemListAdapter();
    itemList.setAdapter(itemListAdapter);
    itemList.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Item item = itemListAdapter.getItem(position);
        startActivityForResult(GooglePlayActivity.createIntent(ShopActivity.this, item),
            RequestCode.BUY.ordinal());
      }
    });
  }

  @Override
  public void onSessionClose() {
    Log.d(TAG, "onSessionClose");
    finish();
  }

  @Override
  public void onSessionOpen() {
    Log.d(TAG, "onSessionOpen");
  }
}
