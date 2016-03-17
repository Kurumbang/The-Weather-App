package com.example.bishal.myapplication;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private TextView cityName, temperature;
    private TextView description, sunrise, sunset, humidity, pressure, max_temp, min_temp;

    private String receivedCityName = "";

    private String URL = "http://api.openweathermap.org/data/2.5/weather?q=Hamburg&appid=44d0fc3e51b3da7b0e60c99de8bdfc88";


    public void setURL(String receivedCityName){
        URL = "http://api.openweathermap.org/data/2.5/weather?q=" + receivedCityName +"&appid=44d0fc3e51b3da7b0e60c99de8bdfc88";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Data from the web: ", response.toString());
                try {
                    String city = response.getString("name");
                    cityName.setText(city);

                    JSONArray jsonArrayWeather = response.getJSONArray("weather");
                    JSONObject jsonObjectDescription = jsonArrayWeather.getJSONObject(0);
                    String weatherDescription = jsonObjectDescription.getString("description");
                    description.setText(weatherDescription);


                    JSONObject jsonObjectMain = response.getJSONObject("main");
                    int temp = convertToCelsius(jsonObjectMain.getDouble("temp"));
                    double pressureData = jsonObjectMain.getDouble("pressure");
                    int humidityData = jsonObjectMain.getInt("humidity");
                    int max_Temp_Data = convertToCelsius(jsonObjectMain.getDouble("temp_max"));
                    int min_Temp_Data = convertToCelsius(jsonObjectMain.getDouble("temp_min"));
                    //float pressureData = jsonObjectMain.getLong("pressure");

                    temperature.setText(temp + "°");
                    pressure.setText(pressureData/100 + " mb");
                    humidity.setText(humidityData + " %");
                    max_temp.setText(max_Temp_Data + " °");
                    min_temp.setText(min_Temp_Data + " °");

                    JSONObject jsonObjectSys = response.getJSONObject("sys");
                    // int sunriseInfo = jsonObjectSys.getInt("sunrise");
                    //int sunsetInfo = jsonObjectSys.getInt("sunset");

                    DateFormat df = DateFormat.getTimeInstance();
                    String sunriseTime = df.format(new Date(jsonObjectSys.getInt("sunrise")));
                    String sunsetTime = df.format(new Date(jsonObjectSys.getInt("sunset")));
                    sunrise.setText(sunriseTime);
                    sunset.setText(sunsetTime);




                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Main activity: ", error.getMessage());
            }
        });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);



        //search_box = (EditText) findViewById(R.id.search_box);
        //search_button = (Button) findViewById(R.id.search_Button);

        /*search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receivedCityName = search_box.getText().toString();
                if (receivedCityName == null) {
                    //URL = "http://api.openweathermap.org/data/2.5/weather?q=Hamburg&appid=44db6a862fba0b067b1930da0d769e98";
                    Toast.makeText(getApplicationContext(), "Please Enter a city", Toast.LENGTH_LONG).show();
                } else {
                    setURL(receivedCityName);
                    search_box.setText("");
                    Log.v("String URL : " , URL);
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Data from the web: ", response.toString());

                        try {
                            String city = response.getString("name");
                            JSONObject jsonObjectMain = response.getJSONObject("main");
                            String temp = convertToCelsius(jsonObjectMain.getString("temp"));
                            temperature.setText(temp + "°");
                            cityName.setText(city);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Main activity: ", error.getMessage());
                    }
                });
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
            }
        });*/

        //RequestQueue queue = Volley.newRequestQueue(this);



    }
    public void init(){
        cityName = (TextView) findViewById(R.id.city_name);
        temperature = (TextView) findViewById(R.id.temperature);
        sunrise = (TextView) findViewById(R.id.sunrise);
        sunset = (TextView) findViewById(R.id.sunset);
        humidity = (TextView) findViewById(R.id.humidity);
        pressure = (TextView) findViewById(R.id.pressure);
        max_temp = (TextView) findViewById(R.id.max_temp);
        min_temp = (TextView) findViewById(R.id.min_temp);
        description = (TextView) findViewById(R.id.description);

    }
    public int convertToCelsius(double temperature){
        double temp = temperature;
        temp -= 273.15;
        int result = (int) Math.round(temp);
        return result;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (null != searchView) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                return true;
            }

            public boolean onQueryTextSubmit(String query) {

                //Here u can get the value "query" which is entered in the search box.
                if(query!=null){
                    Toast.makeText(getApplicationContext(),query,Toast.LENGTH_LONG).show();
                    receivedCityName = query;
                    if (receivedCityName == null) {
                        //URL = "http://api.openweathermap.org/data/2.5/weather?q=Hamburg&appid=44db6a862fba0b067b1930da0d769e98";
                        Toast.makeText(getApplicationContext(), "Please Enter a city", Toast.LENGTH_LONG).show();
                    } else {
                        setURL(receivedCityName);
                        //search_box.setText("");
                       // Log.v("String URL : " , URL);
                    }

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.v("Data from the web: ", response.toString());
                            try {
                                String city = response.getString("name");
                                cityName.setText(city);

                                JSONArray jsonArrayWeather = response.getJSONArray("weather");
                                JSONObject jsonObjectDescription = jsonArrayWeather.getJSONObject(0);
                                String weatherDescription = jsonObjectDescription.getString("description");
                                description.setText(weatherDescription);


                                JSONObject jsonObjectMain = response.getJSONObject("main");
                                int temp = convertToCelsius(jsonObjectMain.getDouble("temp"));
                                double pressureData = jsonObjectMain.getDouble("pressure");
                                int humidityData = jsonObjectMain.getInt("humidity");
                                int max_Temp_Data = convertToCelsius(jsonObjectMain.getDouble("temp_max"));
                                int min_Temp_Data = convertToCelsius(jsonObjectMain.getDouble("temp_min"));
                                //float pressureData = jsonObjectMain.getLong("pressure");

                                temperature.setText(temp + "°");
                                pressure.setText(pressureData/100 + " mb");
                                humidity.setText(humidityData + " %");
                                max_temp.setText(max_Temp_Data + " °");
                                min_temp.setText(min_Temp_Data + " °");

                                JSONObject jsonObjectSys = response.getJSONObject("sys");
                               // int sunriseInfo = jsonObjectSys.getInt("sunrise");
                                //int sunsetInfo = jsonObjectSys.getInt("sunset");

                                DateFormat df = DateFormat.getTimeInstance();
                                String sunriseTime = df.format(new Date(jsonObjectSys.getInt("sunrise")));
                                String sunsetTime = df.format(new Date(jsonObjectSys.getInt("sunset")));
                                sunrise.setText(sunriseTime);
                                sunset.setText(sunsetTime);




                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d("Main activity: ", error.getMessage());
                        }
                    });
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
                    return true;
                }else
                    return false;


            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
