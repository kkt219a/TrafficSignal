package com.example.changyeopzzzang;

import android.os.AsyncTask;

public class GetBlutoothInformation {
    boolean Complete=false;
    WebManager web;
    String ip=new String();
    String WebResult;
    String address=new String();

    GetBlutoothInformation() {web=WebManager.getInstance();}
    GetBlutoothInformation(String _ip) {
        web=WebManager.getInstance();
        ip=_ip;
        WebResult=new String();
    }

    String ReturnResult(String addressed){
        this.address=addressed;
        new hello().execute(null,null,null);
        while(!Complete);
        return WebResult;
    }

    public class hello extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            WebResult=web.getHtml("http://113.198.237.193:80/id_text.php?id="+address+"&ip="+ip);
            Complete=true;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
