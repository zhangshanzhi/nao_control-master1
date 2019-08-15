package com.example.nao_control;

import android.content.Context;
import android.content.Intent;
import java.util.Calendar;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Locale;

public class non_mood_part extends AppCompatActivity {

    private Context contex;
    private TextView txvResult;
    private TextView txvResult2;
    private TextView txvResult3;
    private String sever_ip = "";
    receive_socket receive = new receive_socket();
    public static final String EXTRA_MESSAGE = "com.example.Nao_control.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contex = getApplicationContext();
        setContentView(R.layout.activity_non_mood_part);
        txvResult = (TextView) findViewById(R.id.txvResult);
        txvResult2 = (TextView) findViewById(R.id.textView2);
        txvResult3 = (TextView) findViewById(R.id.textView3);
        receive.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //helwo
        ArrayList<String> result;

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txvResult3.setText(result.get(0));
                    user_Sorket socket = new user_Sorket();
                    socket.setMessage(result.get(0));
                    socket.setIp(this.sever_ip);
                    socket.execute();
                }
                break;
        }
    }

    public void Button_click(View v) {
        EditText editIp = (EditText) findViewById(R.id.editText);
        this.sever_ip = editIp.getText().toString();
        txvResult.setText(this.sever_ip);

        txvResult.setText(receive.get_json());

    }

    public void Button2_click(View v) {
        JSONObject jsonObject;
        Intent intent = null;
        String Thekey = "";
        String Thevalue = "";

        Intent intent_text = new Intent(this, text_present.class);
        Intent intent_speech = new Intent(this, testToSpeech.class);
        //Intent intent_voice = new Intent(this, text_to_voice.class);
        //Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();

        try {

            String json = receive.get_json();
            jsonObject = new JSONObject(json);
            txvResult.setText(json);

            if (jsonObject.names().get(0).toString().equals("url") && jsonObject.getString(jsonObject.names().get(0).toString()).substring(0, 4).equals("http")) {
                Toast.makeText(this, "do not suuport", Toast.LENGTH_SHORT).show();

                Thevalue = jsonObject.getString(jsonObject.names().get(0).toString());
                Uri uri = Uri.parse(Thevalue);

                startActivity(new Intent(Intent.ACTION_VIEW, uri));


            } if (jsonObject.names().get(2).toString().equals("speech") && !jsonObject.getString(jsonObject.names().get(2).toString()).equals("")) {
                Toast.makeText(this, jsonObject.getString(jsonObject.names().get(2).toString()), Toast.LENGTH_SHORT).show();

                Thevalue = jsonObject.getString(jsonObject.names().get(2).toString());
                intent_speech.putExtra(EXTRA_MESSAGE, Thevalue);
                startActivity(intent_speech);
            } if (jsonObject.names().get(1).toString().equals("text") && !jsonObject.getString(jsonObject.names().get(1).toString()).equals("")) {

                Thevalue ="Anna say: "+ jsonObject.getString(jsonObject.names().get(1).toString());
                intent_text.putExtra(EXTRA_MESSAGE, Thevalue);
                txvResult2.setText(Thevalue);
                //startActivity(intent_text);

            } if (jsonObject.names().get(4).toString().equals("set_calendar") && !jsonObject.getString(jsonObject.names().get(4).toString()).equals("")) {
                String event = jsonObject.getString(jsonObject.names().get(4).toString());


                String[] event_array = event.split("\\|");

                CalendarReminderUtils my_calendar = new CalendarReminderUtils();
                Calendar cal = Calendar.getInstance();

                //long t_start = cal.getTime().getTime(); // Long.parseLong("String")
                //long t_end = cal.getTime().getTime()+10*60; //Long.parseLong("String")
                txvResult3.setText(event_array[0]);
                long t_start =  Long.parseLong(event_array[0]);
                long t_end =  Long.parseLong(event_array[1]);

                String title = "Ana sleep"; //
                String description = "I want to sleep";
                my_calendar.addCalendarEvent(this, title, description, t_start, t_end, t_start+10*60, 1);

                //CalendarContentResolver my2_calender = CalendarContentResolver(contex);

                //saveCalender(v);
                //get_cal_event();

            }
            if (jsonObject.names().get(3).toString().equals("get_calendar") && !jsonObject.getString(jsonObject.names().get(3).toString()).equals("")) {

                Thevalue = jsonObject.getString(jsonObject.names().get(3).toString());

                calendar my2_cal = new calendar();
                long start_time=0;
                long end_time=0;

                JSONArray json_event = my2_cal.getcalendar(this,start_time,end_time);


                user_Sorket socket2 = new user_Sorket();
                socket2.setJsonArray(json_event);
                //socket.setIp(this.sever_ip);
                socket2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveCalender(View view) {
        Intent calendarIntent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, 8, 1, 7, 30);
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.getTimeInMillis());
        calendar.set(2019, 9, 1, 10, 30);
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendar.getTimeInMillis());
        calendarIntent.putExtra(CalendarContract.Events.TITLE, "上课");
        calendarIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "CCIS 1-140");
        calendarIntent.putExtra(CalendarContract.Events.DESCRIPTION, "Osmar's course, do not miss");
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, CalendarContract.EXTRA_EVENT_ALL_DAY);
        startActivity(calendarIntent);
    }

    public void search_cal_event(){
        Calendar c_start= Calendar.getInstance();
        c_start.set(2019,7,24,0,0); //Note that months start from 0 (January)
        Calendar c_end= Calendar.getInstance();
        c_end.set(2013,7,25,0,0); //Note that months start from 0 (January)
        String selection = "((dtstart >= "+c_start.getTimeInMillis()+") AND (dtend <= "+c_end.getTimeInMillis()+"))";
        txvResult.setText(selection);
    }
}

