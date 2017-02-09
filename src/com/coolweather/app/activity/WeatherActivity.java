package com.coolweather.app.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class WeatherActivity extends Activity {
	private TextView cityNameText;
	private TextView publishText;
	private LinearLayout weatherInfoLayout;
	private TextView currentDateText;
	private TextView weatherDespText;
	private TextView temp1Text;
	private TextView temp2Text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		cityNameText=(TextView)findViewById(R.id.city_name);
		publishText=(TextView)findViewById(R.id.publish_text);
		weatherInfoLayout=(LinearLayout)findViewById(R.id.weather_info_layout);
		currentDateText=(TextView)findViewById(R.id.current_date);
		weatherDespText=(TextView)findViewById(R.id.weather_desp);
		temp1Text=(TextView)findViewById(R.id.temp1);
		temp2Text=(TextView)findViewById(R.id.temp2);
		String countryCode=getIntent().getStringExtra("country_code");
		if(!TextUtils.isEmpty(countryCode)){
			publishText.setText("同步中……");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countryCode);
		}else{
			showWeather();
		}
	}
	
	private void queryWeatherCode(String countryCode){
		String address="http://www.weather.com.cn/data/list3/city"+countryCode+".xml";
		queryFromServer(address,"countryCode");
	}
	
	private void queryWeatherInfo(String weatherCode){
		String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		queryFromServer(address,"weatherCode");
	}
	

	private void queryFromServer(final String address,final String type){
		HttpUtil.sendHttpRequest(address,new HttpCallbackListener(){
			@Override
			public void onFinish(final String response) {
				if("countryCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						String[] array=response.split("\\|");
						if(array!=null&&array.length==2){
						String weatherCode=array[1];
						queryWeatherInfo(weatherCode);
					}
				}
				}else if("weatherCode".equals(type)){
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable(){
						@Override
						public void run(){
							showWeather();
						}
					});
				}	
			}
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable(){
					@Override
					public void run() {
						publishText.setText("同步失败");		
					}		
				});	
			}	
		});
	}
	
	private void showWeather(){
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(pref.getString("city_name", ""));
		publishText.setText("今天"+pref.getString("publish_time","")+"发布");
		currentDateText.setText(pref.getString("current_date",""));
		weatherDespText.setText(pref.getString("weather_desp",""));
		temp1Text.setText(pref.getString("temp1",""));
		temp2Text.setText(pref.getString("temp2",""));
		cityNameText.setVisibility(View.VISIBLE);
		weatherInfoLayout.setVisibility(View.VISIBLE);	
	}
}
