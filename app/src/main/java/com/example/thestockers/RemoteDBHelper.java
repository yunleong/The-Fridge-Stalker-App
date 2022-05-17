package com.example.thestockers;

import android.os.Looper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.*;

public class RemoteDBHelper {

    static private final OkHttpClient client = new OkHttpClient();

    static void populateDB(HomeDatabaseHelper myDB){
        Request request = new Request.Builder()
                .url("https://ucx0ybjsh4.execute-api.us-west-1.amazonaws.com/queryall?table=inventory")
                .build();
        new Thread(() -> {
            try(Response response = client.newCall(request).execute()) {
//                System.out.println("Success Response: " + response.body().string());
                List<List<String>> decodedResult = decodeJSON(response);
//                System.out.println("Response in helper: " + decodedResult);
                long timerStart = System.currentTimeMillis();
                while(myDB.isLocked()) {
                    if(System.currentTimeMillis() - timerStart > 10000) {
                        Thread.currentThread().interrupt();
                    }
                }
                // Add the items to the local database
                myDB.lock();
                myDB.deleteAllRows();
                Looper.prepare();
                myDB.addItems(decodedResult);
                myDB.unlock();
            }
            catch(Exception e) {
                System.out.println("Error in execution: " + e.toString());
            }
        }).start();
    }

    // Updates the remote database
    static void insertDB(String id, String date, String name, String quantity, String uom) {
        String bodyData = id + "," + date + "," + name + "," + quantity + "," + uom;
        System.out.println("Body data: " + bodyData);
        RequestBody body = RequestBody.create(bodyData, MediaType.get("text/x-markdown"));
        Request request = new Request.Builder()
                .url("https://ucx0ybjsh4.execute-api.us-west-1.amazonaws.com/insert?table=inventory")
                .post(body)
                .build();
        new Thread(() -> {
            try(Response response = client.newCall(request).execute()) {
                System.out.println("Insert response: " + response.body().string());
//                System.out.println("Response in helper: " + queryTableQueue.peek());

            }
            catch(Exception e) {
                System.out.println("Error in execution: " + e.toString());
            }
        }).start();
    }

    static void deleteDB(String name) {
        RequestBody body = RequestBody.create(name, MediaType.get("text/x-markdown"));
        Request request = new Request.Builder()
                .url("https://ucx0ybjsh4.execute-api.us-west-1.amazonaws.com/delete?table=inventory&col=name")
                .delete(body)
                .build();
        new Thread(() -> {
            try(Response response = client.newCall(request).execute()) {
                System.out.println("Delete response: " + response.body().string());
//                System.out.println("Response in helper: " + queryTableQueue.peek());

            }
            catch(Exception e) {
                System.out.println("Error in execution: " + e.toString());
            }
        }).start();
    }

    // Parses JSON returns from the StockerAPI
    static private List<List<String>> decodeJSON(Response response) throws IOException {
        // Converting the response to a string and removing the outer brackets
        String fullStringResp = response.body().string();
        String stringResp = fullStringResp.substring(1, fullStringResp.length() - 1);
//        System.out.println("string response w/o []: " + stringResp);
        // Finding out how many values are in the response based on the number of square brackets
        int numVals = 0;
        char tmp;
        for(int i = 0; i < stringResp.length(); i++) {
            if(stringResp.charAt(i) == '[' || stringResp.charAt(i) == ']') { numVals++; }
        }
        numVals /= 2;
        if(numVals < 0) {
            System.out.println("Error: number of square brackets is incorrect");
            return null;
        }
        // Formatting string
        List<String> firstList = Arrays.asList(stringResp.split("]\\s*,\\s*"));
        List<List<String>> finalList = new ArrayList<List<String>>();
        for(int i = 0; i < firstList.size(); i++) {
            String tmpStr = firstList.get(i).replaceAll("\\[|\\]|\"", "");
            List<String> tmpList = Arrays.asList(tmpStr.split("\\s*,\\s*"));
            finalList.add(tmpList);
        }
        for(int i = 0; i < finalList.size(); i++) {
            System.out.println("Array " + i + ": " + finalList.get(i));
        }
        // Return
        return finalList;
    }

    // Inserts a list into the list table
    static void insertList(String id, String name) {
        String bodyData = id + "," + name;
        System.out.println("Body data: " + bodyData);
        RequestBody body = RequestBody.create(bodyData, MediaType.get("text/x-markdown"));
        Request request = new Request.Builder()
                .url("https://ucx0ybjsh4.execute-api.us-west-1.amazonaws.com/insert?table=lists")
                .post(body)
                .build();
        new Thread(() -> {
            try(Response response = client.newCall(request).execute()) {
                System.out.println("Insert response: " + response.body().string());
//                System.out.println("Response in helper: " + queryTableQueue.peek());

            }
            catch(Exception e) {
                System.out.println("Error in execution: " + e.toString());
            }
        }).start();
    }

    // Updates the inventory
    static void updateInv(String name, String quantity, String uom, String rowId) {
        String bodyData = name + "," + quantity + "," + uom + "," + rowId;
        System.out.println("Body data: " + bodyData);
        RequestBody body = RequestBody.create(bodyData, MediaType.get("text/x-markdown"));
        Request request = new Request.Builder()
                .url("https://ucx0ybjsh4.execute-api.us-west-1.amazonaws.com/update?table=inventory")
                .put(body)
                .build();
        new Thread(() -> {
            try(Response response = client.newCall(request).execute()) {
                System.out.println("Insert response: " + response.body().string());
//                System.out.println("Response in helper: " + queryTableQueue.peek());

            }
            catch(Exception e) {
                System.out.println("Error in execution: " + e.toString());
            }
        }).start();
    }
}
