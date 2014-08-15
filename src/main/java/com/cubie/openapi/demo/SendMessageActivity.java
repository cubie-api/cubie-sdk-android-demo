package com.cubie.openapi.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cubie.openapi.sdk.Cubie;
import com.cubie.openapi.sdk.CubieServiceCallback;
import com.cubie.openapi.sdk.SessionHelper;
import com.cubie.openapi.sdk.activity.CubieBaseActivity;
import com.cubie.openapi.sdk.exception.CubieException;
import com.cubie.openapi.sdk.service.CubieMessage;
import com.cubie.openapi.sdk.service.CubieMessageActionParams;
import com.cubie.openapi.sdk.service.CubieMessageBuilder;
import com.cubie.openapi.sdk.service.CubieSendAck;
import com.cubie.openapi.sdk.service.CubieUser;
import com.squareup.picasso.Picasso;

public class SendMessageActivity extends CubieBaseActivity {

  enum RequestCode {
    SELECT_FRIENDS, //
    ;
  }

  private final static String TAG = SendMessageActivity.class.getSimpleName();
  private CheckBox textCheckBox;
  private CheckBox imageUrlCheckBox;
  private CheckBox linkTextCheckBox;
  private CheckBox buttonTextCheckBox;
  private EditText notificationEditText;
  private EditText textEditText;
  private EditText imageUrlEditText;
  private EditText linkTextEditText;
  private EditText linkExecuteParamsEditText;
  private EditText linkMarketParamsEditText;
  private EditText buttonTextEditText;
  private EditText buttonExecuteParamsEditText;
  private EditText buttonMarketParamsEditText;

  private CubieMessage buildMessage() {
    final CubieMessageBuilder builder = new CubieMessageBuilder();

    builder.setNotification(notificationEditText.getText().toString());

    if (textCheckBox.isChecked()) {
      builder.setText(textEditText.getText().toString());
    }

    if (imageUrlCheckBox.isChecked()) {
      builder.setImage(imageUrlEditText.getText().toString());
    }

    if (linkTextCheckBox.isChecked()) {
      builder.setAppLink(linkTextEditText.getText().toString(),
          new CubieMessageActionParams().withExecuteParam(linkExecuteParamsEditText.getText()
              .toString()).withMarketParam(linkMarketParamsEditText.getText().toString()));
    }

    if (buttonTextCheckBox.isChecked()) {
      builder.setAppButton(buttonTextEditText.getText().toString(),
          new CubieMessageActionParams().withExecuteParam(buttonExecuteParamsEditText.getText()
              .toString()).withMarketParam(buttonMarketParamsEditText.getText().toString()));
    }
    return builder.build();
  };

  private void consumeExecuteParams() {
    final String click = Cubie.resolveExecuteParams(getIntent(), "click");
    if (click == null || click.length() == 0) {
      return;
    }
    Toast.makeText(getActivity(), "you clicked a " + click, Toast.LENGTH_SHORT).show();
  }

  private Activity getActivity() {
    return this;
  }

  private void goToMainActivity() {
    startActivity(new Intent(this, DemoMainActivity.class));
    finish();
  }

  private void loadProfile() {
    final TextView nameView = (TextView) findViewById(R.id.name);
    final ImageView iconView = (ImageView) findViewById(R.id.icon);

    Cubie.getService().requestMe(new CubieServiceCallback<CubieUser>() {

      @Override
      public void done(CubieUser user, CubieException e) {
        if (e != null) {
          Log.e(TAG, "CubieService.requestMe.onFailure", e);
        } else {
          Log.d(TAG, "CubieService.requestMe.onSuccess:" + user);
          nameView.setText(user.getNickname());
          Picasso.with(getActivity()).load(user.getIconUrl()).into(iconView);
        }
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == RequestCode.SELECT_FRIENDS.ordinal() && resultCode == RESULT_OK) {
      final SelectFriendActivity.Result result = SelectFriendActivity.Result.createFromIntent(data);

      Cubie.getService().sendMessage(result.getFriendUid(),
          buildMessage(),
          new CubieServiceCallback<CubieSendAck>() {
            @Override
            public void done(CubieSendAck sendAck, CubieException e) {
              if (e != null) {
                Toast.makeText(getActivity(),
                    "fail to send message:" + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
              } else {
                Toast.makeText(getActivity(),
                    "message sent to " + result.getFriendName() + " successfully",
                    Toast.LENGTH_SHORT).show();
              }
            }
          });
    }

  }

  @Override
  protected void onBaseCreate(final Bundle savedInstanceState) {
    setContentView(R.layout.activity_send_message);

    textCheckBox = (CheckBox) findViewById(R.id.text_checkbox);
    imageUrlCheckBox = (CheckBox) findViewById(R.id.image_url_checkbox);
    linkTextCheckBox = (CheckBox) findViewById(R.id.link_text_checkbox);
    buttonTextCheckBox = (CheckBox) findViewById(R.id.button_text_checkbox);

    notificationEditText = (EditText) findViewById(R.id.notification_edittext);
    textEditText = (EditText) findViewById(R.id.text_edittext);
    imageUrlEditText = (EditText) findViewById(R.id.image_url_edittext);
    linkTextEditText = (EditText) findViewById(R.id.link_text_edittext);
    linkExecuteParamsEditText = (EditText) findViewById(R.id.link_execute_params_edittext);
    linkMarketParamsEditText = (EditText) findViewById(R.id.link_market_params_edittext);
    buttonTextEditText = (EditText) findViewById(R.id.button_text_edittext);
    buttonExecuteParamsEditText = (EditText) findViewById(R.id.button_execute_params_edittext);
    buttonMarketParamsEditText = (EditText) findViewById(R.id.button_market_params_edittext);

    findViewById(R.id.friend_button).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(final View v) {
        final CubieMessage cubieMessage = buildMessage();

        if (cubieMessage.isEmpty()) {
          Toast.makeText(getActivity(), "cannot send an empty message", Toast.LENGTH_LONG).show();
        } else {
          startActivityForResult(SelectFriendActivity.createIntent(getActivity()),
              RequestCode.SELECT_FRIENDS.ordinal());
        }
      }
    });

    consumeExecuteParams();
  }

  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    final MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.activity_send_message, menu);
    return true;
  }

  @Override
  protected void onNewIntent(final Intent intent) {
    setIntent(intent);
    consumeExecuteParams();
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    switch (item.getItemId()) {
      case R.id.make_access_token_need_to_refresh:
        SessionHelper.getSession().testMakeAccessTokenNeedToRefresh();
        return true;
      case R.id.item_show_access_token:
        final String accessTokenString = "access token:" + SessionHelper.getSession()
            .getAccessToken()
            + "\n\nexpire:"
            + SessionHelper.getSession().getExpireTime()
            + " ("
            + DateUtils.getRelativeTimeSpanString(SessionHelper.getSession()
                .getExpireTime()
                .getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)
            + ")";
        new AlertDialog.Builder(this).setMessage(accessTokenString).show();
        System.out.println(accessTokenString);
        return true;
      case R.id.item_invalidate_access_token:
        SessionHelper.getSession().testInvalidAccessToken();
        Toast.makeText(getActivity(), "try to pause then resume", Toast.LENGTH_SHORT).show();
        return true;
      case R.id.item_shop:
        startActivity(ShopActivity.createIntent(getActivity()));
        return true;
      case R.id.item_logout:
        disconnect();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onSessionClose() {
    Log.d(TAG, "onSessionClose");
    goToMainActivity();
  }

  @Override
  public void onSessionOpen() {
    Log.d(TAG, "onSessionOpen");
    loadProfile();
  }
}
