package com.sparklyys.yweather;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sparklyys.yweather.fragment.CityFragment;
import com.sparklyys.yweather.fragment.WeatherFragment;
import com.sparklyys.yweather.model.Weather_model;
import com.sparklyys.yweather.utils.HttpDownloader;
import com.sparklyys.yweather.utils.NetUtil;
import com.sparklyys.yweather.utils.ParseNowWeatherUtil;


/**
 * Created by SparkklyYS on 2017/7/20.
 * Main Activity
 */
public class MainActivity extends AppCompatActivity {

    //实时天气情况信息
    private Weather_model nowWeather;

    //UI控件
    private Toolbar toolbar;              //工具栏
    private TextView title_temp;          //toolbar上显示的温度
    private TextView main_location;       //所在城市
    private TextView main_temp;           //所在城市的实时温度
    private TextView main_info;           //所在城市的实时天气描述
    private TextView city_tab;            //切换城市tab
    private TextView weather_tab;         //切换天气tab

    //Fragment Object
    private WeatherFragment weather_fg;       //weather_content fragment
    private CityFragment city_fg;             //city_content fragment
    private FragmentManager fManager = getSupportFragmentManager();


    //异步更新UI控件中的当前天气信息
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            try {
                String jsonString = (String) msg.obj;
                ParseNowWeatherUtil parseNowWeatherUtil = new ParseNowWeatherUtil();
                nowWeather = parseNowWeatherUtil.getInfomation(jsonString);

                Log.i("天气", nowWeather.getName());
                main_location.setText(nowWeather.getName());
                title_temp.setText(nowWeather.getTemperature() + "°");
                main_temp.setText(nowWeather.getTemperature() + "°");
                main_info.setText(nowWeather.getWeatherText());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //检查网络状态是否良好
        if (NetUtil.getNetWorkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("YWeather", "网络正常连接");
            Toast.makeText(MainActivity.this, "网络正常连接", Toast.LENGTH_LONG).show();
        } else {
            Log.d("YWeather", "网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_LONG).show();
        }

        bindViews();
        setEvent();
        weather_tab.performClick(); //初始停留在weather_tab下

        Log.i("标记: ", "实时显示天气");
        new Thread(new WeatherThread()).start();
    }


    //创建可供选择的菜单选项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * 绑定UI控件
     */
    public void bindViews() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        title_temp = (TextView) findViewById(R.id.title_temp);
        main_location = (TextView) findViewById(R.id.main_location);
        main_temp = (TextView) findViewById(R.id.main_tem);
        main_info = (TextView) findViewById(R.id.main_info);

        city_tab = (TextView) findViewById(R.id.tab_city);
        weather_tab = (TextView) findViewById(R.id.tab_weather);

        //设置自定义的toolbar为actionbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);        //隐藏label
    }

    /**
     * 实时查询天气的Thread
     */
    private class WeatherThread implements Runnable {


        /**
         * 初始化抓取北京的实时天气信息
         */
        @Override
        public void run() {
            String address = "https://api.seniverse.com/v3/weather/now.json?key=n4aglf4gdkteer35&location=beijing&language=zh-Hans&unit=c";
            HttpDownloader httpDownloader = new HttpDownloader();
            String jsonString = httpDownloader.download(address);

            //打印weather info
            Log.w("cn", jsonString);

            Message message = Message.obtain();
            message.obj = jsonString;
            mHandler.sendMessage(message);
        }
    }

    //重置tab选中状态
    private void ResetSelected() {
        city_tab.setSelected(false);
        weather_tab.setSelected(false);
    }

    //隐藏所有Fragment
    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if (weather_fg != null) fragmentTransaction.hide(weather_fg);
        if (city_fg != null) fragmentTransaction.hide(city_fg);
    }


    //为UI控件设置事件
    public void setEvent() {
        //切换事件
        SwitchListener switchListener = new SwitchListener();
        city_tab.setOnClickListener(switchListener);
        weather_tab.setOnClickListener(switchListener);


    }

    /**
     * city 和 weather 两个fragment之间的切换
     */
    private class SwitchListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            FragmentTransaction fTransaction = fManager.beginTransaction();
            hideAllFragment(fTransaction);
            switch (view.getId()) {
                case R.id.tab_weather:
                    ResetSelected();
                    weather_tab.setSelected(true);
                    if (weather_fg == null) {
                        weather_fg = new WeatherFragment();
                        fTransaction.add(R.id.main_layout, weather_fg);
                    } else {
                        fTransaction.show(weather_fg);
                    }
                    break;
                case R.id.tab_city:
                    ResetSelected();
                    city_tab.setSelected(true);
                    if (city_fg == null) {
                        city_fg = new CityFragment();
                        fTransaction.add(R.id.main_layout, city_fg);
                    } else {
                        fTransaction.show(city_fg);
                    }
                    break;
            }
            fTransaction.commit();
        }
    }

}


