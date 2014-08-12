package com.cubie.openapi.demo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cubie.openapi.sdk.Cubie;
import com.cubie.openapi.sdk.CubieService;
import com.cubie.openapi.sdk.CubieServiceCallback;
import com.cubie.openapi.sdk.activity.CubieBaseActivity;
import com.cubie.openapi.sdk.exception.CubieException;
import com.cubie.openapi.sdk.service.CubieFriend;
import com.cubie.openapi.sdk.service.CubieFriendList;
import com.squareup.picasso.Picasso;

public class SelectFriendActivity extends CubieBaseActivity {
  private static class FriendListAdapter extends BaseAdapter {
    private static class ViewHolder {
      ImageView iconView;
      TextView nameView;
    }

    List<CubieFriend> friends = new ArrayList<CubieFriend>();

    @Override
    public int getCount() {
      return friends.size();
    }

    @Override
    public CubieFriend getItem(int position) {
      return friends.get(position);
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
        convertView = inflater.inflate(R.layout.item_friend, parent, false);
        viewHolder = new ViewHolder();
        viewHolder.iconView = (ImageView) convertView.findViewById(R.id.iconView);
        viewHolder.nameView = (TextView) convertView.findViewById(R.id.nameView);
        convertView.setTag(viewHolder);
      } else {
        viewHolder = (ViewHolder) convertView.getTag();
      }

      final CubieFriend friend = getItem(position);
      viewHolder.nameView.setText(friend.getNickname());
      Picasso.with(parent.getContext()).load(friend.getIconUrl()).into(viewHolder.iconView);
      return convertView;
    }

    public void reload(List<CubieFriend> friends) {
      this.friends = friends;
      notifyDataSetChanged();
    }

  }

  public static class Result {
    public static Result createFromIntent(Intent intent) {
      return new Result(intent.getStringExtra("friendUid"), intent.getStringExtra("friendName"));
    }

    public static Result fromCubieFriend(CubieFriend friend) {
      return new Result(friend.getUid(), friend.getNickname());
    }

    private final String friendUid;

    private final String friendName;

    public Result(String friendUid, String friendName) {
      this.friendUid = friendUid;
      this.friendName = friendName;
    }

    public String getFriendName() {
      return friendName;
    }

    public String getFriendUid() {
      return friendUid;
    }

    public Intent toIntent() {
      final Intent intent = new Intent();
      intent.putExtra("friendUid", friendUid);
      intent.putExtra("friendName", friendName);
      return intent;
    }

    @Override
    public String toString() {
      return "Result [friendUid=" + friendUid + ", friendName=" + friendName + "]";
    }
  }

  public static Intent createIntent(Activity activity) {
    final Intent intent = new Intent(activity, SelectFriendActivity.class);
    return intent;
  }

  private static final String TAG = SelectFriendActivity.class.getSimpleName();

  private FriendListAdapter friendListAdapter;
  private ListView friendListView;
  private View moreView;

  private CubieFriendList cubieFriendList;

  CubieServiceCallback<CubieFriendList> callback = new CubieServiceCallback<CubieFriendList>() {
    @Override
    public void done(CubieFriendList updatedFriendList, CubieException e) {
      if (e != null) {
        finishWithError(e.getMessage());
      } else if (updatedFriendList != null) {
        if (updatedFriendList.getAllFriends().isEmpty()) {
          finishWithError("no friends");
          return;
        }
        cubieFriendList = updatedFriendList;
        friendListAdapter.reload(updatedFriendList.getAllFriends());
        if (!updatedFriendList.hasMore()) {
          friendListView.removeFooterView(moreView);
        }
      }
    }
  };

  private void finishWithError(String errorMessage) {
    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
  }

  private void loadFriends() {
    Cubie.getService().requestFriends(cubieFriendList,
        CubieService.DEFAULT_FRIEND_LIST_PAGE_SIZE,
        callback);
  }

  @Override
  protected void onBaseCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_select_friend);

    friendListView = (ListView) findViewById(R.id.listView);
    friendListAdapter = new FriendListAdapter();

    moreView = LayoutInflater.from(this).inflate(R.layout.view_more, friendListView, false);
    moreView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        loadFriends();
      }
    });
    friendListView.addFooterView(moreView, null, false);
    friendListView.setAdapter(friendListAdapter);
    friendListView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final CubieFriend cubieFriend = friendListAdapter.getItem(position);
        setResult(RESULT_OK, Result.fromCubieFriend(cubieFriend).toIntent());
        finish();
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
    loadFriends();
  }
}
