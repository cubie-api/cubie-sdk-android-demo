package com.cubie.openapi.demo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.cubie.openapi.sdk.activity.CubieBaseActivity;

public class DemoMainActivity extends CubieBaseActivity {

  private static final String TAG = DemoMainActivity.class.getSimpleName();

  private void goToSendMessageActivity() {
    startActivity(new Intent(this, SendMessageActivity.class));
    finish();
  }

  @Override
  protected void onBaseCreate(final Bundle savedInstanceState) {
    setContentView(R.layout.activity_demo_main);
    findViewById(R.id.loginButton).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(final View v) {
        connect();
      }
    });
  }

  @Override
  public void onSessionClose() {
    Log.d(TAG, "onSessionClose");
  }

  @Override
  public void onSessionOpen() {
    Log.d(TAG, "onSessionOpen");
    goToSendMessageActivity();
  }
}
