package com.cyberneid.eidonsdk.example;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cyberneid.eidon.sdk.Constants;
import com.cyberneid.eidon.sdk.EIDONSDK;
import com.cyberneid.eidon.sdk.mrzscan.MRZInfo;
import com.cyberneid.eidon.sdk.nfc.data.Citizen;
import com.cyberneid.eidon.sdk.utils.SignatureInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    public static final String LOGTAG = "MainActivity";

    private static final int PERMISSIONS_REQUEST_CAMERA = 1;

    private static final int REQUEST_CAPTURE_MRZ = 10;
    private static final int REQUEST_CAPTURE_MRZ_FULL = 20;
    private static final int REQUEST_AUTHENTICATION = 30;
    private static final int REQUEST_GENERATE_IDENTITY = 40;
    private static final int REQUEST_SIGN_PDF = 50;
    TextView textView;
    ImageView imageView;

    Citizen citizen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestPermission();
    }

    public void onCaptureMRZ(View v)
    {
        EIDONSDK.captureMRZ(this, REQUEST_CAPTURE_MRZ );
    }

    public void onCaptureMRZWithNFC(View v)
    {
        EIDONSDK.captureMRZByNFC(this, REQUEST_CAPTURE_MRZ_FULL );
    }

    public void onAuthenticateCitizen(View v)
    {
        EIDONSDK.authenticateCitizen(this, REQUEST_AUTHENTICATION );
    }

    public void onGenerateIdentity(View v)
    {
        EIDONSDK.generateIdentity(this, REQUEST_GENERATE_IDENTITY);
    }

    public void onSignPDF(View v)
    {
        if(citizen == null)
        {
            Toast.makeText(this, "No citizen identity generated. Please generate a new digital identity", Toast.LENGTH_LONG).show();
            return;
        }

        if(citizen.getCertFile() == null)
        {
            Toast.makeText(this, "No citizen identity generated. Please generate a new digital identity", Toast.LENGTH_LONG).show();
            return;
        }

        // prepare signature info data for signtaure
        SignatureInfo signatureInfo = new SignatureInfo();

        // signature rect
        signatureInfo.x = 200;
        signatureInfo.y = 5;
        signatureInfo.width = 200;
        signatureInfo.width = 80;

        // signature certificate
        signatureInfo.certPath = citizen.getCertFile();
        signatureInfo.pkcs12Path = citizen.getP12File();

        // page where the signature must be shown
        signatureInfo.signaturePage = 1;

        // name of the signer
        signatureInfo.signerName = "Jhon Doe";

        // get pdf to sign
        String filePath = "file:///android_asset/agreement.pdf";

        EIDONSDK.signPDF(this, signatureInfo, filePath, REQUEST_SIGN_PDF);
    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_CAMERA);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show();
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(LOGTAG, "onActivityResult " + requestCode);

        if(resultCode == RESULT_CANCELED)
        {
            Log.d(LOGTAG, "RESULT_CANCELED ");
        }
        else if(resultCode == Constants.RESULT_ERROR)
        {
            Log.d(LOGTAG, "RESULT_ERROR ");
        }
        else
        {
            switch (requestCode) {
                case REQUEST_CAPTURE_MRZ:
                    MRZInfo mrz = data.getParcelableExtra(Constants.EXTRA_MRZ);
                    Log.d(LOGTAG, "mrz " + mrz.toString());

                    textView.setText(mrz.toString());

                    String imagePath = data.getStringExtra(Constants.EXTRA_IMAGE_ID_PATH);
                    Log.d(LOGTAG, "imagePath " + imagePath);

                    try
                    {
                        InputStream ins = new FileInputStream(imagePath);
                        Bitmap bitmap = BitmapFactory.decodeStream(ins);
                        ins.close();

                        imageView.setImageBitmap(bitmap);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    break;

                case REQUEST_CAPTURE_MRZ_FULL:
                    mrz = data.getParcelableExtra(Constants.EXTRA_MRZ);
                    Log.d(LOGTAG, "mrz " + mrz.toString());

                    citizen = data.getParcelableExtra(Constants.EXTRA_CITIZEN);

                    Log.d(LOGTAG, "citizen" + citizen.toString());
                    textView.setText(citizen.toString());

                    imagePath = data.getStringExtra(Constants.EXTRA_IMAGE_ID_PATH);
                    Log.d(LOGTAG, "imagePath " + imagePath);

                    try
                    {
                        InputStream ins = new FileInputStream(imagePath);
                        Bitmap bitmap = BitmapFactory.decodeStream(ins);
                        ins.close();

                        imageView.setImageBitmap(bitmap);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    break;

                case REQUEST_AUTHENTICATION:
                    mrz = data.getParcelableExtra(Constants.EXTRA_MRZ);
                    Log.d(LOGTAG, "mrz " + mrz);

                    citizen = data.getParcelableExtra(Constants.EXTRA_CITIZEN);

                    String photoPath = citizen.getPhoto();
                    String livePhotoPath = citizen.getLiveScanPhoto();


                    Log.d(LOGTAG, "citizen" + citizen.toString());
                    textView.setText(citizen.toString());

                    imagePath = data.getStringExtra(Constants.EXTRA_IMAGE_ID_PATH);
                    Log.d(LOGTAG, "imagePath " + imagePath);

                    try
                    {
                        InputStream ins = new FileInputStream(imagePath);
                        Bitmap bitmap = BitmapFactory.decodeStream(ins);
                        ins.close();

                        imageView.setImageBitmap(bitmap);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    break;

                case REQUEST_GENERATE_IDENTITY:
                    mrz = data.getParcelableExtra(Constants.EXTRA_MRZ);
                    Log.d(LOGTAG, "mrz " + mrz);

                    citizen = data.getParcelableExtra(Constants.EXTRA_CITIZEN);

                    Log.d(LOGTAG, "p12File " + citizen.getP12File());
                    Log.d(LOGTAG, "citizen " + citizen.getCertFile());

                    Log.d(LOGTAG, "citizen" + citizen.toString());
                    textView.setText(citizen.toString());

                    imagePath = data.getStringExtra(Constants.EXTRA_IMAGE_ID_PATH);
                    Log.d(LOGTAG, "imagePath " + imagePath);

                    try
                    {
                        InputStream ins = new FileInputStream(imagePath);
                        Bitmap bitmap = BitmapFactory.decodeStream(ins);
                        ins.close();

                        imageView.setImageBitmap(bitmap);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    break;

                case REQUEST_SIGN_PDF:
                    String signedPdfPath = data.getStringExtra(Constants.EXTRA_SIGNED_PDF_PATH);
                    Log.d(LOGTAG, "signedPdfPath " + signedPdfPath);
                    textView.setText(signedPdfPath);
                    break;
            }
        }
    }
}
