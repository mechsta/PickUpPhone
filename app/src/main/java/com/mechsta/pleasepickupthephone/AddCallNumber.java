package com.mechsta.pleasepickupthephone;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddCallNumber extends Activity {
    private static final int REQUEST_PHONE_STATE = 0;

    private Context mContext;
    private ArrayAdapter<String> mAdapter;
    private ListView mListView;
    private EditText mEdit;
    private Button mAddBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONE_STATE);
        }
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if the notification policy access has been granted for the app.
        if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }
        mContext = this;
        setContentView(R.layout.activity_main);
        mListView = (ListView)findViewById(R.id.listview);
        mEdit = (EditText)findViewById(R.id.edit_number);
        mAddBtn = (Button)findViewById(R.id.add_btn);


        ArrayList<String> numbers = Utils.loadCallNumber(this);
        if (numbers.size() >= 10) {
            mAddBtn.setEnabled(false);
            mEdit.setEnabled(false);
        }
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, numbers);
        mListView.setAdapter(mAdapter);
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = mEdit.getText().toString();
                if (num.isEmpty()) return;
                mAdapter.add(num);
                mAdapter.notifyDataSetChanged();
                mEdit.setText("");
                int cnt = mAdapter.getCount();
                if (cnt >= 10) {
                    mAddBtn.setEnabled(false);
                    mEdit.setEnabled(false);
                }
                ArrayList<String> numbers = new ArrayList<>();
                for (int i = 0; i < cnt; i++) {
                    numbers.add(mAdapter.getItem(i));
                }
                Utils.saveCallNumber(mContext, numbers);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long arg3) {
                final String selNum = mAdapter.getItem(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.delete_item);
                builder.setMessage(selNum);
                builder.setCancelable(false);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick (DialogInterface dialog, int whichButton) {
                                mAdapter.remove(selNum);
                                mAdapter.notifyDataSetChanged();
                                ArrayList<String> numbers = new ArrayList<String>();
                                int cnt = mAdapter.getCount();
                                for (int i = 0; i < cnt; i++) {
                                    numbers.add(mAdapter.getItem(i));
                                }
                                Utils.saveCallNumber(mContext, numbers);
                                mAddBtn.setEnabled(true);
                                mEdit.setEnabled(true);
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

                return false;
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PHONE_STATE) {
            if (grantResults[0] != 0) {
                Toast.makeText(this, R.string.finish_app, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
