package mediacia.com.postdata;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText et1;
    EditText et2;
    EditText et3;
    EditText et4,et5;
    ImageView iv;
    Button post;
    TextView tvIsConnected;
    String title,author,description,tag,publish,image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvIsConnected = (TextView) findViewById(R.id.textView1);
        et1 = (EditText) findViewById(R.id.title);
        et2 = (EditText) findViewById(R.id.author);
        et3 = (EditText) findViewById(R.id.desc);
        et4 = (EditText) findViewById(R.id.tag);
        et5 = (EditText) findViewById(R.id.publish);
        iv = (ImageView) findViewById(R.id.image);
        post = (Button) findViewById(R.id.post);



        if(isConnected()){
            tvIsConnected.setBackgroundColor(0xFF00CC00);
            tvIsConnected.setText("You are conncted");
        }
        else{
            tvIsConnected.setText("You are NOT conncted");
        }

         title = et1.getText().toString();
         author = et2.getText().toString();
         description = et3.getText().toString();
        tag = et4.getText().toString();
        publish = et5.getText().toString();


        /*ArrayList<News> al = new ArrayList<>();
        News n = new News();
        n.setTitle(title);
        n.setAuthor(author);
        n.setDesc(description);

        al.add(n);*/




        post.setOnClickListener(this);

    }

    public static String POST(String url, News news) {
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("title", news.getTitle());
            jsonObject.accumulate("author", news.getAuthor());
            jsonObject.accumulate("description", news.getDesc());
            jsonObject.accumulate("tags",news.tags);
            jsonObject.accumulate("published",news.published);
            jsonObject.accumulate("image","https://d262ilb51hltx0.cloudfront.net/max/400/1*sxxTVuaXGa0AUdAyYhwwSw.jpeg");
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public void onClick(View v) {

        switch(v.getId()){
            case R.id.post:
                if(!validate())
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
                // call AsynTask to perform network operation on separate thread
                new HttpAsyncTask().execute("http://test.peppersquare.com/api/v1/article");
                break;
        }

    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            News news= new News();
            news.setTitle(title);
            news.setAuthor(author);
            news.setDesc(description);

            return POST(urls[0],news);
        }

        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }


    private boolean validate(){
        if(et1.getText().toString().trim().equals(""))
            return false;
        else if(et2.toString().trim().equals(""))
            return true;
        else if(et3.getText().toString().trim().equals(""))
            return false;
        else if (et4.toString().trim().equals(""))
            return false;
        else if (et5.toString().equals(""))
            return false;
        return true;
    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}


