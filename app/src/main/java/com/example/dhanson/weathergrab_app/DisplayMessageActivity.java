package com.example.dhanson.weathergrab_app;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Getting message from 'intent'

        Intent intent = getIntent();
        String message = intent.getStringExtra(MyActivity.EXTRA_MESSAGE);
        String current_weather = "";
        String address = "http://api.openweathermap.org/data/2.5/weather?q="+message+"&mode=xml";

        TextView textView = new TextView(this);
        textView.setId(R.id.result_content_text);
        textView.setTextSize(40);
        setContentView(textView);
        new getWeatherData().execute(address);
    }


    private class getWeatherData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String weather = "";
            String error = "Please try again.";
            int counter = 0;
            try {
                //Get address and connect to server
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                //Begin streaming the data
                InputStream in = conn.getInputStream();
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(in);//(address);

                //Access data
                Node rootNode = doc.getFirstChild();
                NodeList templates = doc.getElementsByTagName("temperature");

                //Revise later on: Iterate through all temperature tags and grab the value attribute
                for (int i = 0; i < templates.getLength(); i++) {
                    Node currentNode = templates.item(i);
                    Element e = (Element)currentNode;
                    weather = e.getAttribute("value");
                    counter++;
                }

                //Convert to Fahrenheit
                int convertedWeather = WeatherConvert(weather);

                //Return new converted value
                return "" + convertedWeather + "F";

            } catch (IOException e) {
                e.printStackTrace();
                return error;
            } catch (SAXException e) {
                e.printStackTrace();
                return error;
            } catch (javax.xml.parsers.ParserConfigurationException e) {
                e.printStackTrace();
                return error;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            ((TextView)findViewById(R.id.result_content_text)).setText(result);
        }
    }

    private int WeatherConvert (String theWeather) {
        //Takes in the weather as a string and converts it to Fahrenheit

        double newWeather = Double.parseDouble(theWeather);
        int fTemp = (int) Math.round(((newWeather - 273.15) * 1.80) + 32);
        return(fTemp);
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
