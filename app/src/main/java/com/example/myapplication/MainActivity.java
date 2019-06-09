package com.example.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TEST_CITY = "Москва";
    private DBHelper dbHelper;
    private TextView cityName;
    private TextView temperature;
    private TextView humidity;
    private TextView dataSource;
    private String lastCity = TEST_CITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initGui();
        initDB();

        displayData(dbHelper.getCity(TEST_CITY));
    }

    private void initDB() {
        dbHelper = DBHelper.getDB(MainActivity.this);
    }

    private void displayData(CityWeather city) {
        cityName.setText(city.getCity());
        String temperatureS = Integer.toString(city.getTemperature());
        String temperatureSSign = city.getTemperature() > 0 ? "+ " + temperatureS : temperatureS;

        temperature.setText(String.format(getResources().getString(R.string.temperature), temperatureSSign));
        humidity.setText(String.format(getResources().getString(R.string.humidity), city.getHumidity()));
    }

    private void initGui() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cityName = (TextView) findViewById(R.id.city_name);
        temperature = (TextView) findViewById(R.id.temperature);
        humidity = (TextView) findViewById(R.id.humidity);
        dataSource = (TextView) findViewById(R.id.data_source);
        findViewById(R.id.btn_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CityWeather city = dbHelper.getCity(lastCity);
                if (city != null && !city.getCity().isEmpty()) {
                    city.setTemperature(getRandomValue(35));
                    city.setHumidity(getRandomValue(65));
                    dbHelper.editCity(city);
                    displayData(city);
                    dataSource.setText(getResources().getString(R.string.change_in_db));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchText = (SearchView) search.getActionView();
        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    CityWeather cityWeather;
                    if ((cityWeather = dbHelper.getCity(query)) != null) {
                        displayData(cityWeather);
                        dataSource.setText(getResources().getString(R.string.from_db));
                    } else {
                        dbHelper.addCity(query, getRandomValue(35), getRandomValue(65));
                        dataSource.setText(getResources().getString(R.string.to_db));
                    }

                    lastCity = query;
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int getRandomValue(int maxValue) {
        return new Random().nextInt(maxValue);
    }
}
