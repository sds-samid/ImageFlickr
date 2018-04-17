package com.quack_.searchflickr;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UpdateTask extends AsyncTask<String, List<NameValuePair>, JSONObject> {
    Context context;
    JSONParser jParser;
    String url;
    JSONObject jsonObject;
    List<NameValuePair> params;

    public UpdateTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... urls) {
        int i;
        for (i = 0; i < urls.length; i++) {
            return loadJSON(urls[i]); //Передаем url [] в метод loadJSON
        }
        return loadJSON(urls[i]);
    }

    public JSONObject loadJSON(String url) {
        jParser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //Формируем запрос с параметрами
        params.add(new BasicNameValuePair("host","https://api.flickr.com"));
        params.add(new BasicNameValuePair("api_key", context.getString(R.string.flickr_key)));
        params.add(new BasicNameValuePair("extras", "url_sq,url_t,url_s,url_q,url_m,url_n,url_z,url_c,url_l,url_o"));
        params.add(new BasicNameValuePair("format", "json")); //формат в формате json
        params.add(new BasicNameValuePair("per_page", "50"));
        params.add(new BasicNameValuePair("safe_search", "safe"));
        params.add(new BasicNameValuePair("content_type", "5"));
        params.add(new BasicNameValuePair("media", "photos"));


        if (!((MainActivity) context).getText_Search().equals("")) {
            params.add(new BasicNameValuePair("method", "flickr.photos.search"));
            params.add(new BasicNameValuePair("text", ((MainActivity) context).getText_Search()));
        }
        //Вызываем парсер json с GET запросом
        jsonObject = jParser.makeHttpRequest(url, "GET", params);
        return jsonObject;
    }

    @Override
    protected void onPostExecute(JSONObject jsonData) {
        List<String> list = new ArrayList<>();
        if (jsonData != null) {
            try {

                JSONObject sites = jsonData.getJSONObject("photos");
                JSONArray temp2 = sites.getJSONArray("photo");
                Log.e("Site", String.valueOf(sites));

                for (int i = 0; i < sites.length(); i ++) {
                    if (temp2.getJSONObject(i).getString("url_s") != null) {
                        list.add(temp2.getJSONObject(i).getString("url_s"));
                    }
                }


                ((MainActivity) context).setImage(list);
            } catch (JSONException e) {
                //При возникновении исключения создаем Toast с текстом
                Toast.makeText(((MainActivity) context).getApplicationContext(),context.getResources().getString(R.string.error_search),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
