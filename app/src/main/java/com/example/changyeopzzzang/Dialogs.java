package com.example.changyeopzzzang;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class Dialogs extends Activity {
    private Context context;
    int time;
    int index = 1;
    String[] value = new String[7];
    String TrafficLightColor;
    boolean complete = false;
    CountDownTimer countDownTimer;
    TextView tv,tv2;
    private LocationManager lm;
    private LocationListener ll;
    double mySpeed, maxSpeed;
    boolean possible=false;
    int distant;
    public Dialogs(Context context) {
        this.context = context;
    }

    public Dialogs() {
    }

    public void TimeSet(String val) {
        value = val.split("#");
        if (value[1].equals("0")) {
            TrafficLightColor = "지금은 빨간불입니다.";
            time = Integer.parseInt(value[2]);
            index = 0;
        } else {
            TrafficLightColor = "지금은 파란불입니다.";
            time = Integer.parseInt(value[4]);
            index = 1;
        }
        complete = true;
    }

    // 호출할 다이얼로그 함수를 정의한다. //final TextView main_label
    @SuppressLint({"ResourceAsColor", "MissingPermission"})
    public void callFunction(String val, String dist) {
        final Dialog bDialog = new Dialog(context);
        bDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bDialog.setContentView(R.layout.dialog);
        bDialog.setCanceledOnTouchOutside(false);
        final TextView countTxt = (TextView) bDialog.findViewById(R.id.counttext);
        final TextView titleTxt = (TextView) bDialog.findViewById(R.id.title);
        final Button okButton = (Button) bDialog.findViewById(R.id.okButton);
        final TextView distt = (TextView) bDialog.findViewById(R.id.distt);
        final TextView myspeed = (TextView) bDialog.findViewById(R.id.speed);
        final TextView myspeed2 = (TextView) bDialog.findViewById(R.id.speed2);
        final TextView conff = (TextView) bDialog.findViewById(R.id.conf);
        tv = myspeed;
        tv2=myspeed2;
        tv.setText("현재속도: 0 km/h");
        conff.setText("평균 값\n걷는속도: 1-2km/h\n  속보: 3-4km/h\n  달리기: 5-6km/h");
        maxSpeed = mySpeed = 0;
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        ll = new Dialogs.SpeedoActionListener();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
        distant = Integer.parseInt(dist);
        distt.setText("약 "+dist+"m 남았습니다.");
        titleTxt.setText(TrafficLightColor);
        if(TrafficLightColor.equals("지금은 파란불입니다."))
            titleTxt.setBackgroundColor(Color.parseColor("#01DF3A"));
        else
            titleTxt.setBackgroundColor(Color.parseColor("#FE2E2E"));

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                // 커스텀 다이얼로그를 종료한다.
                bDialog.dismiss();
            }
        });

        if(!Dialogs.this.isFinishing()) {
            bDialog.show();
            timeStart(countTxt,titleTxt,time);
            //countDownTimer.start(); // 다이얼로그를 띄우면서 카운트 다운을 시작한다.
        }

    }

    public void reset(final TextView countTxt, final TextView titleTxt, int t){
        countDownTimer.cancel();
        timeStart(countTxt, titleTxt, time);
    }

    public void timeStart(final TextView countTxt, final TextView titleTxt, int t){
        countDownTimer = new CountDownTimer((t*1000),1000) {  //6초간 1초간격으로 카운트다운을 진행한다.

            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                countTxt.setText(String.valueOf((millisUntilFinished/1000)/60)+"분 "+String.valueOf((millisUntilFinished/1000)%60)+"초 남았습니다.");
                //countTxt.setText(String.valueOf(millisUntilFinished /1000));  //TextView 값을 변경한다.
            }

            @SuppressLint({"ResourceAsColor", "SetTextI18n"})
            @Override
            public void onFinish() {
                possible=true;
                if(index==0) {
                    time = Integer.parseInt(value[5]);
                    index=1;
                    titleTxt.setText("지금은 파란불입니다.");
                    titleTxt.setBackgroundColor(Color.parseColor("#01DF3A"));
                    if(((double)distant/(double)time)*3.6<6){
                        tv2.setText(String.valueOf("추천속도: "+ String.valueOf(((double)distant/(double)time)*3.6)+"km/h로 다음 신호에 건너실 수 있습니다."));
                    }
                    else{
                        String minute1 = String.valueOf((int) (((double) distant / 0.833333) / 60));
                        String second1 = String.valueOf((int) (((double) distant / 0.833333) % 60)); //걷
                        String minute2 = String.valueOf((int) (((double) distant / 1.38889) / 60)); //뛰
                        String second2 = String.valueOf((int) (((double) distant / 1.38889) % 60));
                        tv2.setText("뛴다면: "+minute2+"분 "+second2+"초\n"+"걷는다면: "+minute1+"분"+second1+"초\n"+"뒤에 목적지에 도착합니다.");
                    }
                }
                else {
                    time = Integer.parseInt(value[3]);
                    index=0;
                    titleTxt.setBackgroundColor(R.color.colorAccent);
                    titleTxt.setBackgroundColor(Color.parseColor("#FE2E2E"));
                    titleTxt.setText("지금은 빨간불입니다.");
                    if(((double)distant/(double)time)*3.6<6){
                        tv2.setText(String.valueOf("추천속도: "+ String.valueOf(((double)distant/(double)time)*3.6)+"km/h로 다음 신호에 건너실 수 있습니다."));
                    }
                    else{
                        String minute1 = String.valueOf((int) (((double) distant / 0.833333) / 60));
                        String second1 = String.valueOf((int) (((double) distant / 0.833333) % 60)); //걷
                        String minute2 = String.valueOf((int) (((double) distant / 1.38889) / 60)); //뛰
                        String second2 = String.valueOf((int) (((double) distant / 1.38889) % 60));
                        tv2.setText("뛴다면: "+minute2+"분 "+second2+"초\n"+"걷는다면: "+minute1+"분"+second1+"초\n"+"뒤에 목적지에 도착합니다.");
                    }
                }
                reset(countTxt,titleTxt,time);
            }
        };
        countDownTimer.start();
    }
    public static String distances(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1609.344;

        return String.valueOf((int)Math.round(dist));
    }
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public class SpeedoActionListener implements LocationListener {
        TextView Speed;
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                mySpeed = location.getSpeed();
                Log.i("현재위치","현재 값"+ String.valueOf(Math.round(mySpeed*1000)/1000.0)+"km/h");
                tv.setText("현재 속도: " + String.valueOf(Math.round(mySpeed*100)/100.0) + " km/h");
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
    }
}
