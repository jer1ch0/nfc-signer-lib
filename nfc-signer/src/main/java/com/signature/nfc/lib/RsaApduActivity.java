package com.signature.nfc.lib;


import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Base64;
import android.util.Log;

import com.signature.nfc.nfc_sign.R;

import java.io.IOException;

public class RsaApduActivity extends AppCompatActivity {

    private static final String TAG = "RsaApduActivity";
    private NfcAdapter adapter;
    private String message = "";
    private String password= "";
    private String keyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_sign);
        adapter = NfcAdapter.getDefaultAdapter(this);
        Bundle b = getIntent().getExtras();
        message = b.getString("message");
        password = b.getString("password");
        keyId = b.getString("key");
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.enableForegroundDispatch(this,
                PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0),
                new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)},
                new String[][]{new String[]{IsoDep.class.getName()}});
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.adapter != null) {
            this.adapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        Intent returnIntent = getIntent();
        try {
            byte[] result = SenderRsaApdu.SendAPDU(intent, message, password, keyId);
            String sign = new String(Base64.encode(result, 0));
            returnIntent.putExtra("result", sign);
            setResult(RESULT_OK, returnIntent);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            returnIntent.putExtra("error", e.getMessage());
            setResult(RESULT_CANCELED, returnIntent);
        }
        finish();
    }


}
