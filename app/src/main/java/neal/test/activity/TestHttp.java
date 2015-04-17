package neal.test.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import neal.http.Http;
import neal.http.base.HttpError;
import neal.http.base.Response;
import neal.http.impl.request.FileRequest;
import neal.http.impl.request.GsonRequest;
import neal.http.impl.request.JsonObjectRequest;
import neal.http.impl.request.MultipartRequest;
import neal.http.impl.request.StringRequest;
import neal.http.ui.RecyclingNetImageView;
import neal.test.R;
import neal.test.model.PeopleList;
import neal.test.provider.ImageUrls;


/**
 * Created by Neal on 2014/9/28.
 * 优化缓存
 */
public class TestHttp extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView imageList=(ListView)findViewById(R.id.image_list);
        imageList.setAdapter(new ImageAdapter());
       /* Http.getRequestQueue(getApplicationContext()).add(new StringRequest("http://www.baidu.com",new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);

            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(HttpError error) {

            }
        }));
        Http.getRequestQueue(getApplicationContext()).add(new JsonObjectRequest("http://222.29.39.162/test.json",null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);

                try {
                    JSONArray array=response.getJSONArray("people");
                   for(int i=0;i<array.length();i++){
                        System.out.println(i+array.getJSONObject(i).toString()+"-"+array.getJSONObject(i).getString("email"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(HttpError error) {

            }
        }));

        Http.getRequestQueue(getApplicationContext()).add(new GsonRequest<PeopleList>(PeopleList.Input.url,PeopleList.Input.getPostParams(1), PeopleList.class,new Response.Listener<PeopleList>() {
            @Override
            public void onResponse(PeopleList response) {
                System.out.println(response.people.size());
                for(int i=0;i<response.people.size();i++) {
                    System.out.println(i+response.people.get(i).firstName+response.people.get(i).lastName+response.people.get(i).email);

                }
                //response.people.add(new PeopleList.listItem());

            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(HttpError error) {

            }
        }));

        HashMap<String,String> map1=new HashMap<String, String>();
        map1.put("id","1111");
        HashMap<String,File> map2=new HashMap<String, File>();
        File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"test.png");
        if(file==null){
            System.out.println("file null");
        }
        map2.put("file",file);
        Http.getRequestQueue(getApplicationContext()).add(new MultipartRequest("http://222.29.39.162/upload_file.php",map1,map2,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("file-success"+response);

            }
        }
        ,new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(HttpError error) {
                        System.out.println("file-error");

                    }
                }
        ));

        Http.getRequestQueue(getApplicationContext()).add(new FileRequest("http://222.29.39.162/123.MP3",new Response.Listener<File>() {
            @Override
            public void onResponse(File response) {
                System.out.println(response.getAbsoluteFile());

            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(HttpError error) {

            }
        }));*/

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class ImageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ImageUrls.imageSmallUrls.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(convertView==null){
                convertView= View.inflate(TestHttp.this, R.layout.image_list_item, null);
                viewHolder=new ViewHolder();
                viewHolder.imageListImage=(RecyclingNetImageView)convertView.findViewById(R.id.image_list_image);
                viewHolder.imageListText=(TextView)convertView.findViewById(R.id.image_list_text);
                convertView.setTag(viewHolder);
            }else{
                viewHolder=(ViewHolder)convertView.getTag();
            }
            viewHolder.imageListImage.setImageUrl(ImageUrls.imageSmallUrls[position],R.drawable.ic_launcher,R.drawable.ic_launcher);
            viewHolder.imageListText.setText("item"+position);
            return convertView;
        }

        private class ViewHolder{
            RecyclingNetImageView imageListImage;
            TextView imageListText;
        }
    }
}
