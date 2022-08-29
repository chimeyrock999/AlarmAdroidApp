package com.samsung.alarmteam6;

import static android.content.Context.ALARM_SERVICE;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.samsung.alarmteam6.adapters.AlarmAdapter;
import com.samsung.alarmteam6.database.AlarmOpenHelper;
import com.samsung.alarmteam6.database.WeekOpenHelper;
import com.samsung.alarmteam6.models.Alarm;
import com.samsung.alarmteam6.models.Week;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class AlarmFragment extends Fragment implements EventHandler {

    ListView alarmListView;
    ArrayList<Alarm> alarms = new ArrayList<Alarm>();
    AlarmAdapter mAdapter;
    public int mCreateCode = 0;
    public int mEditCode = 1;
    int id = 0;
    int posEdit = 0;
    FloatingActionButton btnAdd;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Calendar calendar;
    AlarmOpenHelper alarmOpenHelper;
    WeekOpenHelper weekOpenHelper;
    Context mContext;
    private static final String NOTIFICATION_ACTION = BuildConfig.APPLICATION_ID + ".NOTIFICATION_ACTION";
    private static final String CHANNEL_ID = "MY_CHANNEL_ID";
    private static final int NOTIFICATION_ID = 0;
    private NotificationManager mNotifyManager;


    public AlarmFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = container.getContext();
        return inflater.inflate(R.layout.fragment_alarm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        alarmOpenHelper = new AlarmOpenHelper(getActivity().getApplicationContext());
        weekOpenHelper = new WeekOpenHelper(getActivity().getApplicationContext());
        alarms = alarmOpenHelper.getAll();
        // Gán ID cuối để thêm vào database
        if (alarms.size() > 0)
            id = alarms.get(alarms.size() - 1).getId();
        btnAdd = getActivity().findViewById(R.id.alarm_add);
        alarmListView = getActivity().findViewById(R.id.alarm_list);
        mAdapter = new AlarmAdapter(mContext, alarms);
        alarmListView.setAdapter(mAdapter);
        Intent createIntent = new Intent(getActivity().getApplicationContext(), CreateAlarm.class);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(createIntent, mCreateCode);
            }
        });
    }

    @Override
    public void onClick(int pos) {
//        edit
        Intent editIntent = new Intent(getActivity().getApplicationContext(), EditAlarm.class);
        Alarm alarm = alarms.get(pos);
        posEdit = pos;
//        System.out.println(alarm.getHour() + ":" + alarm.getMinute());
        editIntent.putExtra("alarm", (Serializable) alarm);
        startActivityForResult(editIntent, mEditCode);

    }

    @Override
    public void onLongClick(int pos) {
//        remove
        alarmOpenHelper.deleteAlarm(alarms.get(pos).getId());
        weekOpenHelper.deleteWeek(alarms.get(pos).getId());
        alarms.remove(pos);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("RQC = " + requestCode + " RSC = " + resultCode + " RSOK = " + Activity.RESULT_OK);
        if (requestCode == mCreateCode) {
            if (resultCode == Activity.RESULT_OK) {
                int hour = data.getIntExtra("hour", 0);
                int minute = data.getIntExtra("minute", 0);
                boolean[] days = data.getBooleanArrayExtra("day");
                Alarm alarm = new Alarm(++id, hour, minute, 1);
                System.out.println("Id: " + id);
                alarmOpenHelper.addAlarm(alarm);
                alarms.add(alarm);
                for (int i = 0; i < days.length; i++) {
                    if (days[i]) {
//                        System.out.println(i + "True");
                        weekOpenHelper.addWeek(new Week(0, alarm.getId(), i, 1));
                    } else {
//                        System.out.println(i + "False");
                        weekOpenHelper.addWeek(new Week(0, alarm.getId(), i, 0));
                    }
                }

                System.out.println(hour + ":" + minute);

                ArrayList<Week> weeks = weekOpenHelper.getWeekbyAlarm(alarm.getId());
                for (Week week : weeks) {
                    System.out.println(week.getDay() + " " + week.getStatus());
                }

                mAdapter.notifyDataSetChanged();
                Intent intent = new Intent(getActivity().getApplicationContext(), AlarmReceiver.class);
                calendar = Calendar.getInstance();
                alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE); // Cho phép truy cập đến báo động của máy (báo thức)
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                mAdapter.setCalendar(calendar);
                Calendar now = Calendar.getInstance();
                now.set(Calendar.HOUR_OF_DAY, new Date().getHours());
                now.set(Calendar.MINUTE, new Date().getMinutes());
                System.out.println("CreateCode: " + mCreateCode);

                if (calendar.before(now)) {    //this condition is used for future reminder that means your reminder not fire for past time
                    calendar.add(Calendar.DATE, 7);
                }
                System.out.println(Calendar.HOUR_OF_DAY + ":" + Calendar.MINUTE);
                // Tồn tại trong suốt ứng dụng ngay cả khi thoát khỏi ứng dụng
                intent.putExtra("extra", "on");
                System.out.println("hello");
                final int idPen = (int) System.currentTimeMillis(); //Random request code for pendingintent
                pendingIntent = PendingIntent.getBroadcast(
                        getActivity().getApplicationContext(), idPen, intent, PendingIntent.FLAG_IMMUTABLE);
                mAdapter.setPendingIntent(pendingIntent);
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 7 * 24 * 60 * 60 * 1000, pendingIntent);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } else if (requestCode == mEditCode) {
            if (resultCode == Activity.RESULT_OK) {
                int idAlarm = data.getIntExtra("id", 0);
                int hour = data.getIntExtra("hour", 0);
                int minute = data.getIntExtra("minute", 0);
                int status = data.getIntExtra("status", 0);
                boolean[] days = data.getBooleanArrayExtra("day");
                Alarm alarm = new Alarm(idAlarm, hour, minute, status);
                System.out.println("Edit Id: " + idAlarm);
                alarmOpenHelper.updateAlarm(alarm);
                alarms.set(posEdit, alarm);

                weekOpenHelper.deleteWeek(idAlarm);
                for (int i = 0; i < days.length; i++) {
                    if (days[i]) {
//                        System.out.println(i + "True");
                        weekOpenHelper.addWeek(new Week(0, alarm.getId(), i, 1));
                    } else {
//                        System.out.println(i + "False");
                        weekOpenHelper.addWeek(new Week(0, alarm.getId(), i, 0));
                    }
                }

                ArrayList<Week> weeks = weekOpenHelper.getWeekbyAlarm(alarm.getId());
                for (Week week : weeks) {
                    System.out.println(week.getDay() + " " + week.getStatus());
                }

                System.out.println(hour + ":" + minute);
                mAdapter.notifyDataSetChanged();
                Intent intent = new Intent(getActivity().getApplicationContext(), AlarmReceiver.class);
                calendar = Calendar.getInstance();
                alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE); // Cho phép truy cập đến báo động của máy (báo thức)
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                mAdapter.setCalendar(calendar);
                Calendar now = Calendar.getInstance();
                now.set(Calendar.HOUR_OF_DAY, new Date().getHours());
                now.set(Calendar.MINUTE, new Date().getMinutes());

                if (calendar.before(now)) {
//                    Date date = new Date();
//                    Calendar c = Calendar.getInstance();
//                    c.setTime(date);
//                    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
//                    System.out.println(dayOfWeek);
                    //this condition is used for future reminder that means your reminder not fire for past time
                    calendar.add(Calendar.DATE, 7);
                }
                System.out.println(Calendar.HOUR_OF_DAY + Calendar.MINUTE);
                // Tồn tại trong suốt ứng dụng ngay cả khi thoát khỏi ứng dụng
                intent.putExtra("extra", "on");
                System.out.println("hello");
                final int idPen = (int) System.currentTimeMillis(); //Random request code for pendingintent
                pendingIntent = PendingIntent.getBroadcast(
                        getActivity().getApplicationContext(), idPen, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mAdapter.setPendingIntent(pendingIntent);
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 7 * 24 * 60 * 60 * 1000, pendingIntent);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }
}