package com.example.changyeopzzzang;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    String ip = new String();
    private static final int REQUEST_ENABLE_BT = 10; //블루투스에 상태전달 위한 것
    BluetoothAdapter bluetoothAdapter;
    IntentFilter filter;
    private Set<BluetoothDevice> devices;
    private int pariedDeviceCount;
    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private BluetoothSocket bluetoothSocket = null; // 블루투스 소켓
    private OutputStream outputStream = null; // 블루투스에 데이터를 출력하기 위한 출력 스트림
    private InputStream inputStream = null; // 블루투스에 데이터를 입력하기 위한 입력 스트림

    private Thread workerThread = null; // 문자열 수신에 사용되는 쓰레드

    private byte[] readBuffer; // 수신 된 문자열을 저장하기 위한 버퍼

    private int readBufferPosition; // 버퍼 내 문자 저장 위치

    private String[] array=null;

    private static final int REQUEST_NEW_DEVICE = 1;
    GetBlutoothInformation GBI=new GetBlutoothInformation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ip = GetClientDeviceIp();
        GBI=new GetBlutoothInformation(ip);
        GBI.ReturnResult("abc"); // 결과 html

        //BluetoothCheck();

    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(TAG,deviceName);
            }
        }
    };

    /* 페어링 목록 조회 */
    public void Listinquiry(){
        // 이미 페어링 되어있는 블루투스 기기를 찾습니다.
        devices = bluetoothAdapter.getBondedDevices();
        // 페어링 된 디바이스의 크기를 저장
        pariedDeviceCount = devices.size();
        // 페어링 되어있는 장치가 없는 경우

        // 디바이스를 선택하기 위한 다이얼로그 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("페어링 된 디바이스 목록");
        // 페어링 된 각각의 디바이스의 이름과 주소를 저장
        List<String> list = new ArrayList<>();
        // 모든 디바이스의 이름을 리스트에 추가
        if(pariedDeviceCount != 0) {
            for (BluetoothDevice bluetoothDevice : devices)
                list.add(bluetoothDevice.getName());
            // List를 CharSequence 배열로 변경
            final CharSequence[] charSequences = list.toArray(new CharSequence[list.size()]);
            list.toArray(new CharSequence[list.size()]);
            // 해당 아이템을 눌렀을 때 호출 되는 이벤트 리스너
            builder.setItems(charSequences, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 해당 디바이스와 연결하는 함수 호출
                    connectDevice(charSequences[which].toString());
                }
            });
        }
        // 뒤로가기 버튼 누를 때 창이 안닫히도록 설정
        builder.setCancelable(false);
        builder.setNegativeButton("새 디바이스 연결", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // 블루투스 설정 화면 띄워주기
                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                // 종료 후 onActivityResult 호출, requestCode가 넘겨짐
                startActivityForResult(intent, REQUEST_NEW_DEVICE);
            }
        });

        builder.setPositiveButton("취소", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) { }
        });
        // 다이얼로그 생성
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /* connect 시작 */
    public void connectDevice(String deviceName) {
        // 페어링 된 디바이스들을 모두 탐색
        for(BluetoothDevice tempDevice : devices) {
            // 사용자가 선택한 이름과 같은 디바이스로 설정하고 반복문 종료
            if(deviceName.equals(tempDevice.getName())) {
                bluetoothDevice = tempDevice;
                break;
            }
        }
        // UUID 생성
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        // Rfcomm 채널을 통해 블루투스 디바이스와 통신하는 소켓 생성
        try {
            if(bluetoothSocket!=null)
                bluetoothSocket.close();
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            // 데이터 송,수신 스트림을 얻어옵니다.
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
            // 데이터 수신 함수 호출
            //데이터 송신 함수 호출
            sendData(ip);
            if(array!=null)
                array=null;
            receiveData();
        } catch (IOException e) {
            AlertDialog.Builder builders = new AlertDialog.Builder(this);
            builders.setTitle("블루투스 연결 실패").setMessage("기기와 연결할 수 없거나 해당 기기가 이미 연결되어 있습니다.");
            builders.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id) { }
            });
            AlertDialog alertDialog = builders.create();
            alertDialog.show();
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /* ip 전송 */
    void sendData(String text) {
        // 문자열에 개행문자("\n")를 추가해줍니다.
        text += "\n";
        try{
            outputStream.write(text.getBytes());
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /* array로 넘어온 4개의 값 넣어주기*/
    public void receiveData() throws InterruptedException {
        final Handler handler = new Handler();
        // 데이터를 수신하기 위한 버퍼를 생성
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        // 데이터를 수신하기 위한 쓰레드 생성
        workerThread = new Thread(new Runnable() {

            @Override

            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        // 데이터를 수신했는지 확인합니다.
                        int byteAvailable = inputStream.available();
                        // 데이터가 수신 된 경우
                        if(byteAvailable > 0) {
                            // 입력 스트림에서 바이트 단위로 읽어 옵니다.
                            byte[] bytes = new byte[byteAvailable];
                            inputStream.read(bytes);
                            // 입력 스트림 바이트를 한 바이트씩 읽어 옵니다.
                            for(int i = 0; i < byteAvailable; i++) {
                                byte tempByte = bytes[i];
                                // 개행문자를 기준으로 받음(한줄)
                                if(tempByte == '\n') {
                                    // readBuffer 배열을 encodedBytes로 복사
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    // 인코딩 된 바이트 배열을 문자열로 변환
                                    final String text = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            array = text.split("#");
                                            for(int i=0;i<array.length;i++) {
                                                Log.d(TAG,array[i]);
                                            }
                                        }
                                    });
                                } // 개행 문자가 아닐 경우
                                else
                                    readBuffer[readBufferPosition++] = tempByte;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    /* array에 뭐가 들어오면 쓰레드 중지 */
                    if(array!=null)
                        workerThread.interrupt();
                }
            }
        });
        workerThread.start();
    }

    /* 디바이스가 블루투스가 있는지에 대한 여부 */
    public void BluetoothCheck(){
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        /* 디바이스가 블루투스를 지원하지 않는 경우 */
        if (bluetoothAdapter == null) {
            Log.d(TAG, "이 기기는 블루투스를 지원하지 않는 기기입니다. 강제종료합니다");
            finish();
        }
        /* 블루투스가 켜져있지 않으면 블루투스 키는 엑티비티, 켜져있으면 지나감  */
        else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                Log.d(TAG, " 블루투스가 꺼져있어, 블루투스를 켜야함, 취소누르면 강제종료.");
            }
            else
                Listinquiry();
        }
    }

    /* 블루투스 키고 끄기 확인 취소 반응 여부 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) { // '사용'을 눌렀을 때
                Log.d(TAG, "사용 버튼 누름");
                Listinquiry();
            }
            else { // '취소'를 눌렀을 때 꺼버리기
                Log.d(TAG, "취소 버튼 누름, 강제종료 진행");
                finish();
            }
        }
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
                    //네트워크에는 항상 Localhost 즉, 루프백(LoopBack)주소가 있으며, 우리가 원하는 것이 아닙니다.
                    //IP는 IPv6와 IPv4가 있습니다.
                    //IPv6의 형태 : fe80::64b9::c8dd:7003
                    //IPv4의 형태 : 123.234.123.123
                    //어떻게 나오는지는 찍어보세요.
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        try {
            //unregisterReceiver(receiver);
            if(bluetoothSocket!=null)
                bluetoothSocket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }



}