package com.example.changyeopzzzang;

public class location {
    double[] latitute = new double[6];
    double[] longitute = new double[6];
    String[] name = new String[6];
    String[] address = new String[6];

    location(){
        setlocation(35.163302,129.158399,"위치1","abc",0); //
        setlocation(35.1633901,129.158939,"위치2","def",1); //
        setlocation(35.164241,129.160117,"위치3","ghi",2); //
        setlocation(35.164241,129.160117,"위치4","jkl",3); //
        setlocation(35.164118,129.160269,"위치5","mno",4); //
        setlocation(35.209992, 129.080091,"동래중 앞","pqr",5); //동래중 앞

    }
    /*각 인덱스에 저장*/
    public void setlocation(double lat, double lon, String names, String address, int i){
        this.latitute[i]=lat;
        this.longitute[i]=lon;
        this.name[i]=names;
        this.address[i]=address;
    }
}
