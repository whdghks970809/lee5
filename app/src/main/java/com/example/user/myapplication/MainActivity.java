package com.example.user.myapplication;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    Intent intent;
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et = (EditText)findViewById(R.id.editText);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        intent = new Intent(this, getClass()).addFlags
                (intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity
                (this,0,intent,0);

    }

    boolean write(NdefMessage message, Tag tagFromintent){
        try{
            Ndef ndef = Ndef.get(tagFromintent);
            if(ndef != null){
                ndef.connect();
                ndef.writeNdefMessage(message);
                ndef.close();
                Toast.makeText(this,"기록됨.",Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        }catch (Exception e){
            Toast.makeText(this,"기록실패.",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String str = et.getText().toString();
        NdefMessage message = getNdefMessage(str);
        write(message,tagFromIntent);

    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    NdefMessage getNdefMessage(String text){
        byte[] textBytes = text.getBytes();
        NdefRecord textRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA,
                "text/plain".getBytes(),
                new byte[]{},
                textBytes
        );
        NdefMessage message = new NdefMessage(textRecord);
        return message;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(nfcAdapter != null){
            nfcAdapter.enableForegroundDispatch(this,pendingIntent,null,null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(nfcAdapter != null){
            nfcAdapter.disableForegroundDispatch(this);
        }
    }
}
