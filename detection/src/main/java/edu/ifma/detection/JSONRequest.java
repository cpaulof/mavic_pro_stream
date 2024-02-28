package edu.ifma.detection;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public class JSONRequest {
    URL url;
    String host;
    HttpURLConnection connection;

    public int status = 0;

    public JSONRequest(String host)  {
        this.host = host;
    }

    public void sendRequest(byte[] data, int width, int height){
        System.setProperty("http.keepAlive", "false");
        try{
            url = new URL (this.host);
            status = 0;
            connection =  (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonBody = createJsonBody(data, width, height);
            status = 1;

            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            connection.disconnect();
        }catch(IOException exception) {
            status = 888;
        }
        status = 2;
    }

    private String createJsonBody(byte[] data, int width, int height){
        String hexData = toHexString(data);
        String body = "{\"width\":"+width + ",\"height\":"+height + ",\"data\":\""+hexData+"\"}";
        return body;
    }

    private String toHexString(byte[] data){
        String result = "";
        for(byte b: data){
            String r = Integer.toHexString(b);
            r = r.length()==1?"0"+r:r;
            result+=r;
        }
        return result;
    }
}
