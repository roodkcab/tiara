package me.shuoshi.tiara;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class News extends Activity {

	private ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
        listView=(ListView) findViewById(R.id.list);		
		new LoadNewsTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_news, menu);
		return true;
	}
	
	public class JsonAdapter extends SimpleAdapter {
		
	    public News context;
	    public ArrayList<HashMap<String,String>> list;
	    public String[] fieldNames;
	    public int[] fieldTargetIds;

	    public JsonAdapter(News c, 
	            ArrayList<HashMap<String, String>> newses,
	            int textViewResourceId,
	            String[] fieldNames,
	            int[] fieldTargetIds) {
	        super(c, newses, textViewResourceId, fieldNames, fieldTargetIds);
	        this.context = c;
	        this.list = newses;
	        this.fieldNames = fieldNames;
	        this.fieldTargetIds = fieldTargetIds;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {

	        View row = convertView;
	        if (row == null) {
	            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            row = vi.inflate(R.layout.listitem, null);
	        }
	        super.getView(position, convertView, parent);

	        for (int i=0; i<fieldNames.length; i++) {
	            TextView tv = (TextView) row.findViewById(fieldTargetIds[i]);
	            tv.setText(list.get(position).get(fieldNames[i]));              
	        }
	        return row;
	    }
	}

	private class LoadNewsTask extends AsyncTask<String, Void, ArrayList<HashMap<String, String>> > {

        protected ArrayList<HashMap<String, String>> doInBackground(String... urls) {
			ArrayList<HashMap<String, String>> newses = new ArrayList<HashMap<String, String>>();
        	try {
				DefaultHttpClient httpClient = new DefaultHttpClient();   
		    	//创建HttpGet实例
		    	HttpGet request = new HttpGet("http://1.cstartup.sinaapp.com/");
				// 连接服务器
				HttpResponse response = httpClient.execute(request);
				// 读取所有头数据
				Header[] header = response.getAllHeaders();
				HashMap<String, String> hm = new HashMap<String, String>();
				for (int i = 0; i < header.length; i++) {
					hm.put(header[i].getName(), header[i].getValue());
				}
				// 取得数据记录
				HttpEntity entity = response.getEntity();
				// 取得数据记录内容
				InputStream is = entity.getContent();
				// 显示数据记录内容
				BufferedReader in = new BufferedReader(new InputStreamReader(is));
				String str = "";// in.readLine();
				StringBuffer s = new StringBuffer("");
				while ((str = in.readLine()) != null) {
					s.append(str);
				}
				// 释放连接
				httpClient.getConnectionManager().shutdown();
				try {
					JSONArray newsesJson = new JSONArray(s.toString());
					for (int i=0; i<newsesJson.length(); i++){
						JSONObject jsonAttributes = newsesJson.getJSONObject(i);
						HashMap<String, String> map = new HashMap<String, String>();
				        map.put("com", jsonAttributes.getString("com"));
				        map.put("title", jsonAttributes.getString("title"));
				        map.put("user", jsonAttributes.getString("user"));
				        map.put("score", jsonAttributes.getString("score"));
				        map.put("comments", jsonAttributes.getString("comments"));
				        map.put("time", jsonAttributes.getString("time"));
						newses.add(map);
					}
				} catch (JSONException e) {
					System.out.println(e.getMessage());
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				Toast.makeText(getBaseContext(), "ClientProtocolException", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
    	    return newses;		    	
        }

		protected void onPostExecute(ArrayList<HashMap<String, String>> newses) {
			ListAdapter adapter = new JsonAdapter(News.this, newses, R.layout.listitem,
		              new String[] {"com", "title", "user", "score", "comments", "time"}, new int[] {R.id.com, R.id.title, R.id.user, R.id.score, R.id.comments, R.id.time});
			listView.setAdapter(adapter);
			((BaseAdapter) adapter).notifyDataSetChanged();
		}
	}
}
