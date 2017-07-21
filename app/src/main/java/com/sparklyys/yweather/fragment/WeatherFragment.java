package com.sparklyys.yweather.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sparklyys.yweather.R;
import com.sparklyys.yweather.model.Weather_model;
import com.sparklyys.yweather.utils.HttpDownloader;
import com.sparklyys.yweather.utils.ParseWeatherUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SparklyYS on 2017/7/20.
 * 显示近三天的天气情况，以及根据天气作出相应的建议等等
 */

public class WeatherFragment extends Fragment {
    private List<Weather_model> WeatherList = new ArrayList<>();

    //天气信息控件
    //今天
    private TextView nowDateTime;
    private TextView nowWeatherInfo;
    private TextView nowTempRange;

    //明天
    private TextView twoDateTime;
    private TextView twoWeatherInfo;
    private TextView twoTempRange;

    //后天
    private TextView threeDateTime;
    private TextView threeWeatherInfo;
    private TextView threeTempRange;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            try {
                String jsonString = (String) msg.obj;
                ParseWeatherUtil parseWeatherUtil = new ParseWeatherUtil();
                clearWeatherList();
                WeatherList = parseWeatherUtil.getInfomation(jsonString);
                //设置数据
                setDatas();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(new WeatherInfoThread()).start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_content, container, false);
        bindViews(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new Thread(new WeatherInfoThread()).start();
    }

    /**
     * 绑定UI控件
     *
     * @param view
     */
    private void bindViews(View view) {
        nowDateTime = view.findViewById(R.id.weather_now).findViewById(R.id.datetime);
        nowWeatherInfo = view.findViewById(R.id.weather_now).findViewById(R.id.weather_info);
        nowTempRange = view.findViewById(R.id.weather_now).findViewById(R.id.range_temp);

        twoDateTime = view.findViewById(R.id.weather_second).findViewById(R.id.datetime);
        twoWeatherInfo = view.findViewById(R.id.weather_second).findViewById(R.id.weather_info);
        twoTempRange = view.findViewById(R.id.weather_second).findViewById(R.id.range_temp);

        threeDateTime = view.findViewById(R.id.weather_third).findViewById(R.id.datetime);
        threeWeatherInfo = view.findViewById(R.id.weather_third).findViewById(R.id.weather_info);
        threeTempRange = view.findViewById(R.id.weather_third).findViewById(R.id.range_temp);
    }

    //清空天气列表中的数据
    private void clearWeatherList() {
        WeatherList.clear();
    }


    /**
     * 更新weather list线程
     */
    private class WeatherInfoThread implements Runnable {
        @Override
        public void run() {
            String address = "https://api.seniverse.com/v3/weather/daily.json?key=n4aglf4gdkteer35&location=beijing&language=zh-Hans&unit=c&start=0&days=3";
            HttpDownloader httpDownloader = new HttpDownloader();
            String jsonString = httpDownloader.download(address);

            //打印 weather Info
            Log.i("cn", jsonString);

            Message message = Message.obtain();
            message.obj = jsonString;
            mHandler.sendMessage(message);
        }
    }

    /**
     * UI控件设置数据
     */
    private void setDatas() {
        Weather_model newWeather;

        newWeather = WeatherList.get(0);
        nowDateTime.setText("今天");
        nowWeatherInfo.setText(newWeather.getText_day());
        nowTempRange.setText(newWeather.getLow_temp() + "~" + newWeather.getHigh_temp() + "°");

        newWeather = WeatherList.get(1);
        twoDateTime.setText("明天");
        twoWeatherInfo.setText(newWeather.getText_day());
        twoTempRange.setText(newWeather.getLow_temp() + "~" + newWeather.getHigh_temp() + "°");

        newWeather = WeatherList.get(2);
        threeDateTime.setText("后天");
        threeWeatherInfo.setText(newWeather.getText_day());
        threeTempRange.setText(newWeather.getLow_temp() + "~" + newWeather.getHigh_temp() + "°");
    }


}
