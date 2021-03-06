package me.amrl.pqrs;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button scanBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startScan();

        scanBtn = findViewById(R.id.button);
        scanBtn.setOnClickListener(this);
    }

    private void startScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan QR code");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result == null) {  // retry
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (result.getContents() == null) {
//            finish();  // exit on back while scanning
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        if (Patterns.WEB_URL.matcher(result.getContents()).matches()) {
            final String url = result.getContents();

            alert.setTitle("Web link found!");
            alert.setMessage(result.getContents());
            alert.setPositiveButton("Open", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            });
            alert.setNegativeButton("Rescan", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startScan();
                }
            });

        } else {
            alert.setTitle("Not a web link...");
            alert.setMessage(result.getContents());
            alert.setPositiveButton("Rescan", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startScan();
                }
            });
        }

        alert.create().show();
    }

    @Override
    public void onClick(View v) {
        startScan();
    }
}
