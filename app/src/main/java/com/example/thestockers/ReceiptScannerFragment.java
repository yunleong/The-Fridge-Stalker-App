package com.example.thestockers;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static java.util.Objects.isNull;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReceiptScannerFragment extends Fragment {
    private ImageView capturedImage;
    private Button captureBtn, addAllBtn;
    public TextView resultTV, priceTV;
    private Bitmap imageBitmap;
    SwitchCompat expenseSwitch;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    int CAMERA_PERMISSION_CODE = 200;
    int FILE_WRITE_PERMISSION = 300;
    String scanResult;
    List<String> itemName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_receipt_scanner, container, false);
        capturedImage = view.findViewById(R.id.receipt_logo);
        resultTV = view.findViewById(R.id.scan_resultTV);
        priceTV = view.findViewById(R.id.priceTV);
        expenseSwitch = view.findViewById(R.id.expense_switch);

        expenseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    priceTV.setVisibility(View.VISIBLE);
                }else{
                    priceTV.setVisibility(View.GONE);
                }
            }
        });

        captureBtn = view.findViewById(R.id.capture_button);
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    captureImage();
                } else {
                    requestPermission();
                }
            }
        });
        addAllBtn = view.findViewById(R.id.add_all_button);
        addAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeDatabaseHelper db = new HomeDatabaseHelper(getActivity());
                if(itemName.size() > 0) {
                    for (int n = 0; n < itemName.size(); ++n) {
                        // Add all scanned items to database
                        db.addItem(itemName.get(n), 1, "count");
                        //getActivity().onBackPressed();
                    }
                }else{
                    Toast.makeText(getActivity(), "No item(s) scanned.", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getActivity(), "Added successfully.", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    // returns true when camera permission is granted.
    private boolean checkPermissions() {
        int cameraPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{CAMERA}, CAMERA_PERMISSION_CODE);
        ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE}, FILE_WRITE_PERMISSION);
    }


    public void captureImage() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mStartForResult.launch(takePicture);
    }

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == REQUEST_IMAGE_CAPTURE || result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        Bundle extras = intent.getExtras();
                        imageBitmap = (Bitmap) extras.get("data");
                        capturedImage.setImageBitmap(imageBitmap);
                        capturedImage.setRotation(90);
                        try {
                            ScanReceipt();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    private File BitmapToJPEG(Bitmap bitmap) throws IOException {
        //create a file to write bitmap data
        File receiptFile = new File(getActivity().getCacheDir(), "receipt_image.jpeg");
        receiptFile.createNewFile();

        //Convert bitmap to byte array
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] bitmapData = bytes.toByteArray();

        //write bytes into file
        try {
            FileOutputStream fos = new FileOutputStream(receiptFile);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return receiptFile;
    }

    //Establish https connection to API server
    public void JavaReceiptOcr(File imageFile) throws Exception {
        final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
        //System.out.println("=== Java Receipt OCR ===");
        try{
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("client_id", "TEST")       // Use 'TEST' for testing purpose
                    .addFormDataPart("recognizer", "US")       // can be 'US', 'CA', 'JP', 'SG' or 'auto'
                    .addFormDataPart("ref_no", "ocr_java_123'") // optional caller provided ref code
                    .addFormDataPart("file", imageFile.getName(),
                        RequestBody.create(imageFile, MEDIA_TYPE_JPEG)
                    ).build();

        Request request = new Request.Builder()
                .url("https://ocr.asprise.com/api/v1/receipt")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if(!response.isSuccessful()) throw new IOException("Unexpected code "+ response);
            scanResult = response.body().string();
            //System.out.println("Scanned Result >> "+ scanResult);

        } catch (IOException e) {
            Toast.makeText(getActivity(), "Failed to scan." + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void ScanReceipt() throws Exception {
        new fileFromBitmap().execute();
    }

    // TODO: Fix exception handling without app crash!
    public void ExtractResult(String jsonString) throws JSONException {
        itemName = new ArrayList<String>();
        final JSONObject obj = new JSONObject(jsonString);
        if (!isNull(obj)) {
            final JSONArray receipt = obj.getJSONArray("receipts");
            try {
                final int receipt_len = receipt.length();
                for (int i = 0; i < receipt_len; ++i) {
                    JSONObject item = receipt.getJSONObject(i);
                    JSONArray itemList = item.getJSONArray("items");
                    final int item_len = itemList.length();
                    for (int j = 0; j < item_len; ++j) {
                        final JSONObject itemData = itemList.getJSONObject(j);
                        itemName.add(itemData.getString("description"));
                    }
                }
            } catch (NullPointerException e) {
                Toast.makeText(getActivity(), "Cannot detect receipt items, try again!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Scan error, try again!", Toast.LENGTH_SHORT).show();
        }
    }

    //This class handles API calls as background task
    class fileFromBitmap extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                //Convert bitmap to File
                File file = BitmapToJPEG(imageBitmap);
                //Send file to API
                JavaReceiptOcr(file);
            }catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                // Extract product name from json string
                ExtractResult(scanResult);
            } catch (JSONException e) {
                Toast.makeText(getActivity(), "Scan error, try again!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            // Build string to display result
            String resultText = " ";
            for (int n = 0; n < itemName.size(); ++n){
                resultText = resultText.concat(itemName.get(n) + " - 1\n");
            }
            resultTV.setText(resultText);
            //resultTV.setText("Milk - 1\nCheddar Cheese - 1\n Smoked Bacon - 1\n Frozen Pizza - 1\nPepsi - 1\nChicken Thighs - 1\nBrocolli - 1\nBell Peppers - 1");
            //priceTV.setText("$25.98");
            if(resultText.trim().length() > 0){
                addAllBtn.setEnabled(true);
            }
        }
    }
}
