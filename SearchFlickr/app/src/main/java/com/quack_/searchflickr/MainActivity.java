package com.quack_.searchflickr;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btn_search;
    EditText editText_Search;
    public String text_search = "";
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Инициализация переменных
        btn_search = (Button) findViewById(R.id.btnSearch);
        editText_Search = (EditText) findViewById(R.id.editText_Search);
        gridView = (GridView) findViewById(R.id.grid);
        //Метод нажатия кнопки поиска
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage(getString(R.string.search_text));
                progressDialog.show();
                setText_search(editText_Search.getText().toString());
                //если есть подключение к интернету
                if (isOnline()) {
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    //Передаем запрос
                                    new UpdateTask(MainActivity.this).execute("https://api.flickr.com/services/rest/");
                                    progressDialog.dismiss();
                                }
                            }, 3000);
                } else {
                    //иначе вызываем Toast
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Загрузка изображения по url
    public void setImage (List <String> src) {
        List<String> list = src;

        //Cоздание адаптера
        ListAdapter adapter = new ListAdapter(this,list);

        //устанавливаем адаптер в GridView
        gridView.setAdapter(adapter);
    }

    public String getText_Search () {
        return this.text_search;
    }

    public void setText_search (String text_search) {
        this.text_search = text_search;
    }
    //Класс загрузки изображения по url
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    //проверка на доступ к интернету
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    //Адаптер для GridView
    public class ListAdapter extends ArrayAdapter<String> {
        private final Context context;
        private List<String> list;

        public ListAdapter(Context context, List<String> list) {
            super(context, R.layout.item_grid_image, list);
            this.context = context;
            this.list = list;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            // Создаем inflater
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // связываем переменную с layout файлом
            View rowView = inflater.inflate(R.layout.item_grid_image, parent, false);

            ImageView item_content = (ImageView) rowView.findViewById(R.id.image);
            new DownloadImageTask(item_content).execute(list.get(position));
            return rowView;
        }
    }
}
