package com.example.davydouski.weatherbelarus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*класс используется для связывания погодных условий с элементами списка ListView,
переопределения метода getView из ArrayAdapter для создания пользовательского макета
для каждого элемента ListView */
public class WeatherArrayAdapter extends ArrayAdapter<Weather> {
    //вложенный класс для повторного использования представлений
    private static class ViewHolder {//списка при прокрутке и удержания объекта View
        ImageView conditionImageView;
        TextView dayTextView;
        TextView lowTextView;
        TextView hiTextView;
        TextView humidityTextView;
    }
    //кэширование изображений для уже загруженных объектов Bitmap
    private Map<String, Bitmap> bitMaps = new HashMap<>();
    //конструктор для унаследованных членов суперкласса
    public WeatherArrayAdapter(Context context, List<Weather> forecast) {
        super(context, -1, forecast);//аргумент -1 означает, что в приложении используется
        // пользовательский макет, чтобы элемент списка не ограничивался одним компанентом TextView
    }

    @NonNull//метод вызывается для получения объекта View, используемого для отображения данных ListView
    @Override//Переопределение этого метода позволяет связать данные с нестандартным элементом ListView
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Weather day = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.conditionImageView =
                    (ImageView) convertView.findViewById(R.id.conditionImageView);
            viewHolder.dayTextView =
                    (TextView) convertView.findViewById(R.id.dayTextView);
            viewHolder.lowTextView = (TextView) convertView.findViewById(R.id.lowTextView);
            viewHolder.hiTextView = (TextView) convertView.findViewById(R.id.hiTextView);
            viewHolder.humidityTextView = (TextView) convertView.findViewById(R.id.humidityTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //если значок погодных условий уже загружен, использовать его, в противном случае загрузить в отдельном потоке
        if (bitMaps.containsKey(day.iconURL)) {
            viewHolder.conditionImageView.setImageBitmap(bitMaps.get(day.iconURL));
        } else {
            new LoadImageTask(viewHolder.conditionImageView).execute(day.iconURL);
        }

        //получить данные из объекта Weather и заполнить представление ListItem
        Context context = getContext();
        viewHolder.dayTextView.setText(context.getString(R.string.day_description, day.dayOfWeek, day.description));
        viewHolder.lowTextView.setText(context.getString(R.string.low_temp, day.minTemp));
        viewHolder.hiTextView.setText(context.getString(R.string.high_temp, day.maxTemp));
        viewHolder.humidityTextView.setText(context.getString(R.string.humidity, day.humidity));

        return convertView;
    }
    /*класс наследуется от AsyncTask и определяет как должна происходить загрузка значка погодных условий в отдельном потоке,
    а затем возвращает изображение потоку GUI для отображения в компаненте ImageView элемента ListView*/
    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override//загрузить изображение; params[0] содержит URL-адрес изображения
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(strings[0]);//создаём URL для изображения
                //открываем объект HttpURLConnection, получаем inputStream и загружаем изображение
                connection = (HttpURLConnection) url.openConnection();
                try (InputStream inputStream = connection.getInputStream()) {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    bitMaps.put(strings[0], bitmap);//кэширование
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();//закрываем HttpURLConnection
            }
            return bitmap;
        }

        //связать значок погодных условий с элементами списка
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
