# eidon sdk example

eidon SDK 1.2 allows embedding the functions supplied by eidon app in any third party app in a very easy way. Third party app can be:

1)	Native iOS or Android App

2)	Ibrid iOS or Android App

eidon app offers two scenarios to third party apps:

1)	The user installs the eidon app autonomously from the app store and the third party app uses the installed app by sending intents

2)	The third party app embeds eidon sdk inside its own native or ibrid app (this scenario cannot be applied for webapps).

eidon app allows also customizing the UI by changing colors, logo etc., in order to meet the third party app theme and colors

## Getting Started with android

The sdk is supplied as .aar file: 

lib_eidonsdk-1.1.3.aar

To add eidon SDK in you app follows this preliminary steps:

1)	Add the .aar to your project in Android Studio

2)	Initialized the SDK in your Application class:

  EIDONSDK.init(this, clientid, secret);

Ask us for your clientid and secret

## Example:

    package com.cyberneid.eidon.test;

    import android.app.Application;

    import com.cyberneid.eidon.sdk.EIDonSDK;

    public class MainApplication extends Application {

    public static final String clientid = "ee943f5efd3c6eaeb53180308d1c174e1d87e8a6502717a8ce388e9179b6930a";

    public static final String secret = "f2f15b22b980c1b3e2a6e0f492139cd410181bfb12e90797563e0768a491fb78";

    @Override
    public void onCreate() {
        super.onCreate();

        EIDONSDK.init(this, clientid, secret);
    }
   

## Supplied functions

eidon SDK supplies the following functions:

**captureMRZ**
This function captures the MRZ on an id card or a passport by using the camera and OCR and return the MRZ record

**captureMRZByNFC**
This function captures the MRZ by using the camera and OCR and then reads the data inside the chip by NFC.
The function reads the content of the files: DG1 (MRZ), DG2 (photo), DG11 (if not empty) and DG12 (if not empty)

**authenticateCitizen**
This function perform a full authentication of the citizen by performing the face recognition using the photo read from the chip.
This function does:
1)	Scans the MRZ by the camera
2)	Reads the data and the photo from the chip by using NFC
3)	Performs a face detection of the user using a special camera that performs face detection with liveness detection
4)	Performs a face comparison between the photo read from the chip and the live photo taken by the camera
5)	Returns the results of the comparison

**generateIdentity**
This function generate a new digital identity for the authenticated citizen by using the personal data read from the chip
This function does:
1)	Scans the MRZ by the camera
2)	Reads the data and the photo from the chip by using NFC
3)	Performs a face detection of the user in front of the camera
4)	Performs a face comparision between the photo read from the chip and the live photo taken by the camera
5)	If the face match, generates a new digital identity by calling the certification authority that will release the digital certificate

The digital identity is composed by a digital certificate (.cer file) and a PKCS#12 file (.p12) that is protected by the citizen’s PIN

**signPDF**
This function add a digital signature to a pdf by using one of the generated digital identity 
 
## How to use the SDK

The class EIDONSDK supplies the following functions:

    public static void captureMRZ(Activity activity, int requestCode);

    public static void captureMRZByNFC(Activity activity, int requestCode)

    public static void authenticateCitizen(Activity activity, int requestCode)

    public static void generateIdentity(Activity activity, int requestCode)

    public static void signPDF(Activity activity, SignatureInfo signatureInfo, String filePath, int requestCode)

Once called the function will return the results in the method:

  protected void onActivityResult(int requestCode, int resultCode, Intent data)

Examples

**Capturing the MRZ and citizen info by nfc**

    EIDONSDK.captureMRZByNFC(this, REQUEST_CAPTURE_MRZ_FULL )


    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
        super.onActivityResult(requestCode, resultCode, data);

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
           switch (requestCode) 
           {
               case REQUEST_CAPTURE_MRZ_FULL:
                   MRZInfo mrz = data.getParcelableExtra(Constants.EXTRA_MRZ);
                   Log.d(LOGTAG, "mrz " + mrz.toString());

                   Citizen citizen = data.getParcelableExtra(Constants.EXTRA_CITIZEN);

                   Log.d(LOGTAG, "citizen" + citizen.toString());
                   textView.setText(citizen.toString());
                   break;
          }
        }
    }

**Authenticate the citizen**

      EIDONSDK.authenticateCitizen(this, REQUEST_AUTHENTICATION );


      protected void onActivityResult(int requestCode, int resultCode, Intent data) 
      {
          super.onActivityResult(requestCode, resultCode, data);

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
             switch (requestCode) 
             {
                 case REQUEST_AUTHENTICATION:
                     mrz = data.getParcelableExtra(Constants.EXTRA_MRZ);
                     Log.d(LOGTAG, "mrz " + mrz);

                     citizen = data.getParcelableExtra(Constants.EXTRA_CITIZEN);

                     String photoPath = citizen.getPhoto();
                     String livePhotoPath = citizen.getLiveScanPhoto();

                     Log.d(LOGTAG, "citizen" + citizen.toString());
                     textView.setText(citizen.toString());
                     break;
            }
          }
      }

**Generate a new identity**

      EIDONSDK.generateIdentity(this, REQUEST_GENERATE_IDENTITY);

      protected void onActivityResult(int requestCode, int resultCode, Intent data) 
      {
          super.onActivityResult(requestCode, resultCode, data);

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
             switch (requestCode) 
             {
                 case REQUEST_GENERATE_IDENTITY:
                     MRZInfo mrz = data.getParcelableExtra(Constants.EXTRA_MRZ);
                     Log.d(LOGTAG, "mrz " + mrz);

                     Citizen citizen = data.getParcelableExtra(Constants.EXTRA_CITIZEN);

          // certificate and p12 file
                     Log.d(LOGTAG, "p12File " + citizen.getP12file());
                     Log.d(LOGTAG, "citizen " + citizen.getCertFile());

                     Log.d(LOGTAG, "citizen" + citizen.toString());
                     textView.setText(citizen.toString());
                     break;    
        }
      }

**Sign a PDF**

      // prepare signature info data for signtaure
      SignatureInfo signatureInfo = new SignatureInfo();

      // signature rect
      signatureInfo.x = 200;
      signatureInfo.y = 5;
      signatureInfo.width = 200;
      signatureInfo.width = 80;

      // signature certificate
      signatureInfo.certPath = citizen.getCertFile();
      signatureInfo.pkcs12Path = citizen.getP12file();

      // page where the signature must be shown
      signatureInfo.signaturePage = 1;

      // name of the signer
      signatureInfo.signerName = "Jhon Doe";

      // get pdf to sign
      String filePath = "file:///android_asset/agreement.pdf";

      // performs the signature
      EIDONSDK.signPDF(this, signatureInfo, filePath, REQUEST_SIGN_PDF);



      protected void onActivityResult(int requestCode, int resultCode, Intent data) 
      {
          super.onActivityResult(requestCode, resultCode, data);

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
             switch (requestCode) 
             {
                 case REQUEST_SIGN_PDF:
                     String signedPdfPath = data.getStringExtra(Constants.EXTRA_SIGNED_PDF_PATH);
                     Log.d(LOGTAG, "signedPdfPath " + signedPdfPath);
                     textView.setText(signedPdfPath);
                     break;    
      }
    }

**Styling the sdk**

The activities supplied by the SDK can be customized by customizing coloros and logo.

      <color name="colorPrimary">#7C6EB0</color>
      <color name="colorPrimaryDark">#572b81</color>
      <color name="colorAccent">#00B0DB</color>
      <color name="windowBackground">#572b81</color>
      <color name="textColorPrimary">#FFFFFF</color>
      <color name="backgroundColor">#572b81</color>
      <color name="buttonTextColor">#FFFFFF</color>

To customize the logo adds a “logo.png” file in the drawable folder
