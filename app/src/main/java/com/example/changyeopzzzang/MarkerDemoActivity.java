package com.example.changyeopzzzang;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MarkerDemoActivity extends FragmentActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener,MapView.CurrentLocationEventListener {

    /** 위 경도 설정 */
    //double[] latitude = {35.209992,};
    //double[] longitude = {129.080091,};
    //String[] id = {}; // 아이디 설정
    //위에꺼 차래대로 반복문으로 돌리자 맵 포인트를 배열로 선언하고.
    int i=0;
    private static location lo = new location();
    private static int locationcnt = lo.address.length;
    private static final MapPoint[] CUSTOM_MARKER_POINT = new MapPoint[locationcnt];
    /** 위 경도 설정 끝 */

    private MapView mMapView;
    private MapPOIItem mCustomMarker;
    GetBlutoothInformation GBI=new GetBlutoothInformation();
    String ip = new String();
    Button CurrentButton;
    double curlon=0,curlat=0;
    boolean curmode=false;


    // CalloutBalloonAdapter 인터페이스 구현, 클릭 전 이미지 설정하는 부분
    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            ((ImageView) mCalloutBalloon.findViewById(R.id.badge)).setImageResource(R.drawable.ic_launcher);
            ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText("신호등");
            ((TextView) mCalloutBalloon.findViewById(R.id.desc)).setText(" ");
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for(int k=0;k<locationcnt;k++){
            CUSTOM_MARKER_POINT[k]=MapPoint.mapPointWithGeoCoord(lo.latitute[k],lo.longitute[k]);
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        setContentView(R.layout.demo_nested_mapview);
        mMapView = (MapView) findViewById(R.id.map_view);
        CurrentButton = (Button)findViewById(R.id.currentbutton);
        mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mMapView.setMapViewEventListener(this);
        mMapView.setPOIItemEventListener(this);
        mMapView.setCurrentLocationEventListener(this);

        ip = GetClientDeviceIp();
        GBI=new GetBlutoothInformation(ip);

        // 구현한 CalloutBalloonAdapter 등록
        mMapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());
        for(i=0;i<locationcnt;i++){
            createCustomMarker(mMapView);
        }
        //createCustomMarker(mMapView);
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
//        CurrentButton.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(!curmode) {
//                    mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
//                    curmode=true;
//                }
//                else{
//                    mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
//                    curmode=false;
//                }
//            }
//        });
    }

    /*맵뷰 초기화?*/
    @Override
    public void onMapViewInitialized(MapView mapView) {
    }

    private void createCustomMarker(MapView mapView) {
        mCustomMarker = new MapPOIItem();
        String name = lo.name[i]; // 이부분 매개변수 스트링 하나 더 받아서 이름 설정해주자
        mCustomMarker.setItemName(name); // 이름
        mCustomMarker.setTag(i); // 식별자 번호(ID) , 나중에 매개변수 추가해서 i로
        mCustomMarker.setMapPoint(CUSTOM_MARKER_POINT[i]); // 좌표

        mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 아이콘 타입
        mCustomMarker.setCustomImageResourceId(R.drawable.custom_marker_red); //이미지 설정
        mCustomMarker.setCustomImageAutoscale(false); //??
        mCustomMarker.setCustomImageAnchor(0.5f, 1.0f); //??

        mapView.addPOIItem(mCustomMarker); // 맵뷰에 추가
    	mapView.selectPOIItem(mCustomMarker, true);
    	mapView.setMapCenterPoint(CUSTOM_MARKER_POINT[i], false); // 지도의 중심점인가를 적음
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    /**  ClickListener */
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        Dialogs customDialog = new Dialogs(MarkerDemoActivity.this);
        String value = new String(),dist=new String();
        Log.d("태그 확인",String.valueOf(mapPOIItem.getTag()));
        value=GBI.ReturnResult(lo.address[mapPOIItem.getTag()]);
        dist= customDialog.distances(curlat,curlon,lo.latitute[mapPOIItem.getTag()],lo.longitute[mapPOIItem.getTag()]);
        Log.i("현재위치",dist);
        customDialog.TimeSet(value);
        while(!customDialog.complete);
        customDialog.callFunction(value,dist);
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    /* 사용자 기기 IP주소 받아오기 */
    protected String GetClientDeviceIp(){
        try {
            //Device에 있는 모든 네트워크에 대해 뺑뺑이
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                //네트워크 중에서 IP가 할당된 넘들에 대해서 뺑뺑이를 한 번 더 돔
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if(inetAddress.isLoopbackAddress())
                        Log.d("IPAddress", intf.getDisplayName() + "(loopback) | " + inetAddress.getHostAddress());
                    else
                        Log.d("IPAddress", intf.getDisplayName() + " | " + inetAddress.getHostAddress());
                    //루프백이 아니고, IPv4가 맞다면 리턴~~~
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
                        return inetAddress.getHostAddress().toString();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* 현재위치 위경도 저장 */
    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        curlon=mapPointGeo.longitude;
        curlat=mapPointGeo.latitude;
        Log.i("현재위치", String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

}
