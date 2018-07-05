package com.hitachi.com.klgps;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class FuncDBAccess extends AsyncTask<String,Void,String>{

    private Context context;

    public FuncDBAccess(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {

            OkHttpClient okHttpClient = new OkHttpClient();
            Request.Builder builder = new Request.Builder();

            Request request = builder
                    .url(strings[0])
                    .build();


            Response response = okHttpClient.newCall(request).execute();
            Log.d("KLTag","response ==> " + response);
            return response.body().string();
        } catch (Exception e) {
            Log.d("KLTag","Exception ==> " + e.getMessage());
            e.printStackTrace();
            return null;
        }

    }

    public String SetJSONResult(String resultJSON)
    {
        Log.d("KLGPS","resultJSON == > " + resultJSON);
        resultJSON = resultJSON.replace("\\\"","\"");
        Log.d("KLGPS","resultJSON == > " + resultJSON);
        return resultJSON.substring(1,resultJSON.length()-1);

    }
}
