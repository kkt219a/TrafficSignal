package com.example.changyeopzzzang;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class WebManager {

   private final String TAG="WebManager";
   private static WebManager instance = new WebManager();

   protected HttpResponse res;
   protected HttpPost post;
   protected HttpClient client;

   private boolean isLoginSuccess = false;
   public boolean isTimeOut = false;

   List<NameValuePair> postValue = new ArrayList<NameValuePair>(); // DSIS 종료시 파기 해야됨!

   /* 최초 객체 생성시 URL을 Login으로 Setting */
   private WebManager() {
      post = new HttpPost("http://113.198.237.193/"); // Test
      client = new DefaultHttpClient();
      client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
      client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
   }

   /* 클래스 내, WebManager 객체를 반환*/
   public static WebManager getInstance() { // instance return
      return instance;
   }

   public void ListInit(){postValue.clear();}

   /* 로그인이 돼 있으면 POST값 없이 결과 String으로 반환, 이외는 NULL*/
   public String getHtml(String _html) { // 주소를 넘겨주면 Execute 해서 그페이지의 소스 코드를
      // 로드함
      String resString = new String();
         post = new HttpPost(_html);

         try {
            post.setEntity(new UrlEncodedFormEntity(postValue,"UTF-8"));
            res = client.execute(post);
            resString = EntityUtils.toString(res.getEntity(), HTTP.UTF_8);
            Log.e(TAG,resString);

            return resString;
         } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }

      return null;
   }


   /* POST URL 값 설정*/
   public void setURL(String _url){post = new HttpPost(_url);}


   /*
    * Do not touch anything
    */
   // Https용 클라이언트
   private HttpClient getHttpsClient() {
      try {
         //openssl s_client -connect student.donga.ac.kr:443
         // SSL 인증서 갱신처리일자 : 2018.09.05 PM 9:45
         // 언제든지 SSL 갱신 준비해놓고 바로 갱신 가능하게 할 것
         // String + 연산 사용시 상당히 서비스 처리 시간 늘어남
         StringBuilder certificateStringBuilder = new StringBuilder();
         certificateStringBuilder.append("-----BEGIN CERTIFICATE-----\n")
                 .append("MIIG0DCCBbigAwIBAgIQOymh2mlpuAgqG+O+d55hgjANBgkqhkiG9w0BAQsFADCB\n")
                 .append("kDELMAkGA1UEBhMCR0IxGzAZBgNVBAgTEkdyZWF0ZXIgTWFuY2hlc3RlcjEQMA4G\n")
                 .append("A1UEBxMHU2FsZm9yZDEaMBgGA1UEChMRQ09NT0RPIENBIExpbWl0ZWQxNjA0BgNV\n")
                 .append("BAMTLUNPTU9ETyBSU0EgRG9tYWluIFZhbGlkYXRpb24gU2VjdXJlIFNlcnZlciBD\n")
                 .append("QTAeFw0xODA5MDMwMDAwMDBaFw0yMDA5MDIyMzU5NTlaMFoxITAfBgNVBAsTGERv\n")
                 .append("bWFpbiBDb250cm9sIFZhbGlkYXRlZDEdMBsGA1UECxMUUG9zaXRpdmVTU0wgV2ls\n")
                 .append("ZGNhcmQxFjAUBgNVBAMMDSouZG9uZ2EuYWMua3IwggEiMA0GCSqGSIb3DQEBAQUA\n")
                 .append("A4IBDwAwggEKAoIBAQCJURBK9M40RecJeBEGZOHbD3VI52mciL0YYCeg2B6Q3yRB\n")
                 .append("yp2+oyaMpepYmR6bCGgM+CsNRkS29+Qxi0jG2Z4fQARTuo4jYhlGQQ5E8cAqaE80\n")
                 .append("DRWH7cqwh2qiuJ4Fc6gqMeh1wb7uwLJ43nAwibRjsyNXpZkdcHLhLp2zZJCkf+J0\n")
                 .append("BLwwnmrfwRoZ3Vgq0faNDA4lMJ1qb/t4fsRfnhzRi+WA+wBQ4h0xucPbjFdgeJEB\n")
                 .append("HjVHO15U/ibFMWHgmhFzp1sAiJ8gawsPJZYxvZBoe5M9UildNAxfs8eCJoJG+8N5\n")
                 .append("q9CbVI8kd9o9niyl8m2V8SsdGt+x/hHy3GfnDv+FAgMBAAGjggNZMIIDVTAfBgNV\n")
                 .append("HSMEGDAWgBSQr2o6lFoL2JDqElZz30O0Oija5zAdBgNVHQ4EFgQUFCwE6t5PjUGz\n")
                 .append("eu6S4A26C+YTy0gwDgYDVR0PAQH/BAQDAgWgMAwGA1UdEwEB/wQCMAAwHQYDVR0l\n")
                 .append("BBYwFAYIKwYBBQUHAwEGCCsGAQUFBwMCME8GA1UdIARIMEYwOgYLKwYBBAGyMQEC\n")
                 .append("AgcwKzApBggrBgEFBQcCARYdaHR0cHM6Ly9zZWN1cmUuY29tb2RvLmNvbS9DUFMw\n")
                 .append("CAYGZ4EMAQIBMFQGA1UdHwRNMEswSaBHoEWGQ2h0dHA6Ly9jcmwuY29tb2RvY2Eu\n")
                 .append("Y29tL0NPTU9ET1JTQURvbWFpblZhbGlkYXRpb25TZWN1cmVTZXJ2ZXJDQS5jcmww\n")
                 .append("gYUGCCsGAQUFBwEBBHkwdzBPBggrBgEFBQcwAoZDaHR0cDovL2NydC5jb21vZG9j\n")
                 .append("YS5jb20vQ09NT0RPUlNBRG9tYWluVmFsaWRhdGlvblNlY3VyZVNlcnZlckNBLmNy\n")
                 .append("dDAkBggrBgEFBQcwAYYYaHR0cDovL29jc3AuY29tb2RvY2EuY29tMCUGA1UdEQQe\n")
                 .append("MByCDSouZG9uZ2EuYWMua3KCC2RvbmdhLmFjLmtyMIIBfgYKKwYBBAHWeQIEAgSC\n")
                 .append("AW4EggFqAWgAdQDuS723dc5guuFCaR+r4Z5mow9+X7By2IMAxHuJeqj9ywAAAWWc\n")
                 .append("2WyiAAAEAwBGMEQCIBpQwjx7SrVtKr03hDsy8jc7jiMfcvSXjLpvjfOlYYH9AiBT\n")
                 .append("dl8Ds12tdfxlaRRbw8PMzzKlVBo0a+iK7Pop1Kd2oAB2AF6nc/nfVsDntTZIfdBJ\n")
                 .append("4DJ6kZoMhKESEoQYdZaBcUVYAAABZZzZbOYAAAQDAEcwRQIgEztkrsgSFcbDWU7x\n")
                 .append("VyCiS16aPn9/gajZy0lV9rxsFQgCIQDiUNv4EONn6lGlrv09t3OpNGwvqsLL5a0N\n")
                 .append("UxA3dJaEXwB3AFWB1MIWkDYBSuoLm1c8U/DA5Dh4cCUIFy+jqh0HE9MMAAABZZzZ\n")
                 .append("bMEAAAQDAEgwRgIhAOAwWXV+TiZfsYoYUmtod5a31vW2DSlFeBIk+uWAmBryAiEA\n")
                 .append("hrT/WzKlI+KBPl7iZq4Rid+pKdbNPHS/jq1VQDjkAmkwDQYJKoZIhvcNAQELBQAD\n")
                 .append("ggEBAF4VfOVYr9C1xVRoY+rRczt7VJtYyx8CCsXNNdc7K1RV0UCcIA1KAmXHxK1m\n")
                 .append("tcPK3Hhw+uVyZ6RS8IjPLtIe8n6Qgpj4CYsBLWIsD2gJCFf3tFHMcXAnc167k8gP\n")
                 .append("XW6zSjdG0krYyPIg4bycWqOsTQWA3jJh3gjPpjXoGlO6TsS4wgN4GN7fm/0GcBhN\n")
                 .append("YOIgm7o31KfiDPm6Z+YHXOcWad5Oydc9UdSy88sYW5j7n9tYW4jQKK5HqfWZVXe2\n")
                 .append("HfQKsFb/3nFQMWWiAK4vhj7XmYaDcjPwtsRexwShr1kOHLFzJzuQPIUvYIsCPpz4\n")
                 .append("YtVvoVnLompHmYYd2SW+EKK7uB0=\n")
                 .append("-----END CERTIFICATE-----");

         String certificateString = certificateStringBuilder.toString();

         ByteArrayInputStream derInputStream = new ByteArrayInputStream(certificateString.getBytes());
         CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
         X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(derInputStream);
         String alias = cert.getSubjectX500Principal().getName();

         KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
         trustStore.load(null, null);
         trustStore.setCertificateEntry(alias, cert);
         //COLONY 수정 끝

         SSLSocketFactory sf = new SFSSLSocketFactory(trustStore);
         sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

         HttpParams params = new BasicHttpParams();
         HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
         HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
         SchemeRegistry registry = new SchemeRegistry();

         registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
         registry.register(new Scheme("https", sf, 443));

         ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

         return new DefaultHttpClient(ccm, params);
      } catch (Exception e) {
         return new DefaultHttpClient();
      }
   }

   // Https 용 소켓 클래스
   private class SFSSLSocketFactory extends SSLSocketFactory {
      SSLContext sslContext = SSLContext.getInstance("TLS");

      public SFSSLSocketFactory(KeyStore truststore)
              throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
         super(truststore);

         // X509TrustManager 관련 보안 이슈 발생으로 인한 제거
//         TrustManager[] tm = new TrustManager[]{ new X509TrustManager() {
//				@Override
//				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
//						throws java.security.cert.CertificateException {
//					// TODO Auto-generated method stub
//
//				}
//
//				@Override
//				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
//						throws java.security.cert.CertificateException {
//					// TODO Auto-generated method stub
//
//				}
//
//				@Override
//				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//					// TODO Auto-generated method stub
//					return null;
//				}
//			}};
//			sslContext.init(null, tm, new java.security.SecureRandom());

         // 변경 된 코드
         TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
         tmf.init(truststore);
         sslContext.init(null, tmf.getTrustManagers(), null);

      }

      @Override
      public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
              throws IOException, UnknownHostException {
         return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
      }

      @Override
      public Socket createSocket() throws IOException {
         return sslContext.getSocketFactory().createSocket();
      }

   }
}