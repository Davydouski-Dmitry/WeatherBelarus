package com.example.davydouski.weatherbelarus;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/* основной класс определяет логику взаимодействия с вэб-сервисом прогноза погоды,
логику обработки ответа в формате JSON, полученного от вэб-сервиса */
public class MainActivity extends AppCompatActivity {
    //Список объектов Weather,где каждый объект представляет один день в прогнозе погоды
    private List<Weather> weatherList = new ArrayList<>();
    //ArrayAdapter связывает объекты Weather с элементами ListView
    private WeatherArrayAdapter weatherArrayAdapter;
    //ссылка на компанент ListView для вывода информации
    private ListView weatherListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ArrayAdapter для связывания weatherList с weatherListView для заполнения элементов ListView
        weatherListView = findViewById(R.id.weatherListView);
        weatherArrayAdapter = new WeatherArrayAdapter(this, weatherList);
        weatherListView.setAdapter(weatherArrayAdapter);

        EditText locationEditText = findViewById(R.id.locationEditText);
        locationEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_ENTER ){
                    EditText locationEditText = findViewById(R.id.locationEditText);
                    loadWeather(locationEditText);
                    return true;
                }
                return false;
            }
        });

        //Сгенерированный код скрывает клавиатуру и выдаёт запрос к веб-сервису
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText locationEditText = findViewById(R.id.locationEditText);
                loadWeather(locationEditText);
            }
        });
    }

    private void loadWeather(EditText locationEditText) {
        URL url = createURL(locationEditText.getText().toString());// создать URL веб-сервисы

        if (url != null) {
            dismissKeyboard(locationEditText);//Скрываем клавиатуру
            //Создаём объект для получения прогноза погоды в отдельном потоке
            GetWeatherTask getLocalWeatherTask = new GetWeatherTask();
            //передаём URL запрос к веб-сервису
            getLocalWeatherTask.execute(url);
        } else {//если в процессе создания URL произошла ошибка,то создаём объект
            // Snackbar с сообщение о недействительном URL-адресе
            Snackbar.make(findViewById(R.id.coordinatorLayout),
                    R.string.invalid_url, Snackbar.LENGTH_LONG).show();
        }
    }
    /* метод скрывает экранную клавиатуру, когда пользователь касается кнопки FloatingActionButton
    для передачи заданного города приложению*/
    private void dismissKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if(imm != null && imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    //метод формирует строковое представление URL запросак веб-сервису
    private URL createURL(String city) {
        String apiKey = getString(R.string.api_key);
        String baseURL = getString(R.string.web_service_url);

        try {
            //Создание URL для заданного города и температурной шкалы (Цельсия)
            String urlString = baseURL + URLEncoder.encode(city, "UTF-8")
                    + ",by"
                    + "&units=metric&lang=by&cnt=16&APPID=" + apiKey;
            return new URL(urlString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;//Некорректный URL
    }

     /*класс выполняет запрос к веб-сервису и обрабатывает ответ в отдельном потоке,
 после чего передаёт информацию прогноза в виде JSONObject потоку GUI для отображения*/
    private class GetWeatherTask extends AsyncTask<URL, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(URL... urls) {
            HttpURLConnection connection = null;
            try {
                //создаём объект для запроса к REST-совместимому веб-сервису
                connection = (HttpURLConnection) urls[0].openConnection();
                int response = connection.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    } catch (IOException e) {
                        Snackbar.make(findViewById(R.id.coordinatorLayout),
                                R.string.read_error, Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    return new JSONObject(builder.toString());
                } else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout),
                            R.string.connect_error, Snackbar.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Snackbar.make(findViewById(R.id.coordinatorLayout),
                        R.string.connect_error, Snackbar.LENGTH_LONG).show();
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override//Обработка ответа JSON и обновление ListView
        protected void onPostExecute(JSONObject weather) {
            convertJSONtoArrayList(weather);
            weatherArrayAdapter.notifyDataSetChanged();
            weatherListView.smoothScrollToPosition(0);
        }

         //метод создаёт объекты Weather на базе JSONObject с прогнозом
        private void convertJSONtoArrayList(JSONObject forecast) {
            weatherList.clear();//стираем старые погодные данные

            if(forecast== null)
                return;

            try {
                //получение свойств "list" JSONArray
                JSONArray list = forecast.getJSONArray("list");
                //Преобразовываем каждый элемент списка в объект Weather
                for (int i = 0; i < list.length(); i++) {
                    JSONObject day = list.getJSONObject(i);
                    JSONObject temperatures = day.getJSONObject("main");
                    JSONObject weather = day.getJSONArray("weather").getJSONObject(0);
                    //добавляем новый объект Weather в weatherList
                    weatherList.add(
                            new Weather(
                                    day.getLong("dt"),
                                    temperatures.getDouble("temp_min"),
                                    temperatures.getDouble("temp_max"),
                                    temperatures.getDouble("humidity"),
                                    weather.getString("description"),
                                    weather.getString("icon")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
