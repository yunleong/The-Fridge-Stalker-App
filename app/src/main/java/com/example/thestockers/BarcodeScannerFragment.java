package com.example.thestockers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.*;
import org.apache.http.impl.*;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;


import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BarcodeScannerFragment extends Fragment implements View.OnClickListener{

    Button scanBtn;
    public final String TAG = "In Barcode Scanner: ";
    String scanResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_barcode_scanner, container, false);

        scanBtn = view.findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
//                Toast.makeText(getContext(), "in Onclick", Toast.LENGTH_LONG).show();
            }
        });


//        // Create options for the barcode scanner with upc code formats to scan
//        BarcodeScannerOptions barOptions =
//                new BarcodeScannerOptions.Builder()
//                        .setBarcodeFormats(
//                                Barcode.FORMAT_UPC_A,
//                                Barcode.FORMAT_UPC_E)
//                        .build();
//
//        // Create bitmap object from image
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inSampleSize = 3;
//        Bitmap bitmapImage = BitmapFactory.decodeFile("/Users/umarbjr/" +
//                        "AndroidStudioProjects/The-Stockers/app/src/main/res/raw/test_barcode.png",
//                bmOptions);
//
//
//        // Changing the image into a usable object for mlkit
//        InputImage testBarcode = InputImage.fromBitmap(bitmapImage, 0);
//
//        // Creating the barcode scanner
//        BarcodeScanner scanner = BarcodeScanning.getClient(barOptions);
//        // Process the barcode
//        Task<List<Barcode>> result = scanner.process(testBarcode)
//                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
//                    @Override
//                    public void onSuccess(List<Barcode> barcodes) {
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                    }
//                });
////        result[1]
////        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        scanCode();
    }

    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
//        Toast.makeText(getContext(), "in end of scan", Toast.LENGTH_LONG).show();

    }

    //Establish https connection to API server
    public static String getRequest() {
        StringBuffer stringBuffer = new StringBuffer("");
        BufferedReader bufferedReader = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet();

            URI uri = new URI("https://api.iamdata.co/v1/products");
            httpGet.setURI(uri);
            httpGet.addHeader(BasicScheme.authenticate(
                    new UsernamePasswordCredentials("user", "password"),
                    HTTP.UTF_8, false));

            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            bufferedReader = new BufferedReader(new InputStreamReader(
                    inputStream));

            String readLine = bufferedReader.readLine();
            while (readLine != null) {
                stringBuffer.append(readLine);
                stringBuffer.append("\n");
                readLine = bufferedReader.readLine();
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // TODO: handle exception
                }
            }
        }
        return stringBuffer.toString();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Toast.makeText(getContext(), "in Onclick", Toast.LENGTH_LONG).show();

        if (result != null) {
            if (result.getContents() != null) {
                // 617375091986
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(result.getContents());
                Log.wtf(TAG, "onActivityResult: " + result);
                Log.wtf(TAG, "onActivityResult: " + result.getContents());
                builder.setTitle("Scanning Result");
                builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scanCode();
                    }
                }).setNegativeButton("finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(getContext(), "in finish", Toast.LENGTH_LONG).show();
                        return;
                        // finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
            else {
                Toast.makeText(getContext(), "No Results", Toast.LENGTH_LONG).show();
            }
        }
        else {
//            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}