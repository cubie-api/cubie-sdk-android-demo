package com.cubie.openapi.demo;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.cubie.openapi.demo.model.Item;

public class GooglePlayActivity extends Activity {

  public static Intent createIntent(Activity activity, Item item) {
    final Intent intent = new Intent(activity, GooglePlayActivity.class);
    intent.putExtra("item", item);
    return intent;
  }

  private Item item;

  protected Intent createResultIntent() {
    final JSONObject purchaseData = new JSONObject();
    try {
      purchaseData.put("orderId", UUID.randomUUID().toString());
      purchaseData.put("packageName", getPackageName());
      purchaseData.put("productId", item.getId());
      purchaseData.put("purchaseTime", new Date().getTime());
      purchaseData.put("purchaseState", 0);
      purchaseData.put("developerPayload", "");
      purchaseData.put("purchaseToken", UUID.randomUUID().toString());
    } catch (final JSONException e) {
      throw new RuntimeException();
    }
    final Intent intent = new Intent();
    intent.putExtra("INAPP_PURCHASE_DATA", purchaseData.toString());
    return intent;
  }

  public Item extractItem(Intent intent) {
    return (Item) intent.getSerializableExtra("item");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_google_play);
    item = extractItem(getIntent());
    ((TextView) findViewById(R.id.item)).setText(item.getName() + " " + " $" + item.getPrice());
    findViewById(R.id.buy).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        setResult(RESULT_OK, createResultIntent());
        finish();
      }
    });
  }

}
