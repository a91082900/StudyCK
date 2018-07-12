package tech.goda.studyck;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jerry on 2018/7/7.
 */

public class Network {
    public static final String LOGIN_URI = "http://study.ck.tp.edu.tw/login_chk.asp";
    public static final String LOGOUT_URI = "http://study.ck.tp.edu.tw/logout.asp";
    public static final String CHANGE_PWD_URI = "https://ldap.ck.tp.edu.tw/admin/chpass.php";

    public static String uploadFile(String sourceFileUri, InputStream in, String uploadFileName) {


        //String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        //CookieManager cookieManager = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        //File sourceFile = new File(sourceFileUri);
        String response = null;
        if(in == null){
            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Please Choose File First.", Toast.LENGTH_SHORT).show();
                }
            });*/
            return null;
        }
        else{
            try {

                // open a URL connection to the Servlet
                //FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(sourceFileUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("f_file", uploadFileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"f_hwintro\""+ lineEnd + lineEnd);
                dos.writeBytes("" + lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"f_file\";filename=\"");
                dos.write(uploadFileName.getBytes());
                dos.writeBytes("\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = in.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = in.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = in.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = in.read(buffer, 0, bufferSize);

                }

                // send multipart form data necessary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                int serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
                try{
                    BufferedReader br;
                    if (200 <= serverResponseCode && serverResponseCode <= 299) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "big5"));
                    } else {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "big5"));
                    }
                    StringBuilder sb = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }

                    response = sb.toString();
                    Log.e("Response", response);
                } catch(java.io.IOException e){
                    e.printStackTrace();
                }
                Log.e("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    /*runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.";

                            messageText.setText(msg);
                            //Toast.makeText(MainActivity.this, "File Upload Completed.",
                            //Toast.LENGTH_SHORT).show();
                        }
                    });*/
                }

                //close the streams //
                in.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException e) {

                e.printStackTrace();
                Log.e("Upload file to server", "error: " + e.getMessage(), e);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Upload", "Exception : "
                        + e.getMessage(), e);
            }
            return response;
        }
            /*if (!sourceFile.isFile()) {

                Log.e("uploadFile", "Source File not exist :"
                        +uploadFilePath);

                return 0;

            }

            else
            {
                try {

                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(uploadServerUri);

                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("f_file", fileName);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"f_hwintro\""+ lineEnd + lineEnd);
                    dos.writeBytes("" + lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"f_file\";filename=\""
                                    + fileName + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);



        // Responses from the server (code and message)
        serverResponseCode = conn.getResponseCode();
        String serverResponseMessage = conn.getResponseMessage();
        try{
            BufferedReader br = null;
            if (200 <= serverResponseCode && serverResponseCode <= 299) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            Log.e("Response", sb.toString());
        } catch(java.io.IOException e){
            e.printStackTrace();
        }
        Log.e("uploadFile", "HTTP Response is : "
                + serverResponseMessage + ": " + serverResponseCode);

        if(serverResponseCode == 200){

            runOnUiThread(new Runnable() {
                public void run() {

                    String msg = "File Upload Completed.";

                    messageText.setText(msg);
                    Toast.makeText(MainActivity.this, "File Upload Complete.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        //close the streams //
        fileInputStream.close();
        dos.flush();
        dos.close();

    } catch (MalformedURLException e) {

        e.printStackTrace();



        Log.e("Upload file to server", "error: " + e.getMessage(), e);
    } catch (Exception e) {

        e.printStackTrace();


        Log.e("Upload", "Exception : "
                + e.getMessage(), e);
    }
                return serverResponseCode;

            } // End else block
        */
    }



    public static String requestPost(String uri, Map<String, String> params){
        HttpURLConnection conn;
        DataOutputStream dos;
        String response = null;
        try {
            // open a URL connection to the Servlet
            URL url = new URL(uri);
            // Open a HTTP  connection to  the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            dos = new DataOutputStream(conn.getOutputStream());

            Set keySet = params.keySet();
            StringBuilder sb = new StringBuilder();
            for (Object objKey : keySet) {
                //有了鍵就可以通過map集合的get方法獲取其對應的値

                String key = objKey.toString();
                String value = params.get(key);

                sb.append(key).append("=").append(value).append("&");

                Log.e("Params", "key: " + key + ", value: " + value);
            }
            if(params.size() != 0){
                sb.deleteCharAt(sb.length()-1);
            }

            Log.e("StringBuilder", sb.toString());
            dos.writeBytes(sb.toString());
            

            // Responses from the server (code and message)
            int serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();
            try{
                BufferedReader br;
                if (200 <= serverResponseCode && serverResponseCode <= 299) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "big5"));
                } else {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
                }
                sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
                response = sb.toString();
                Log.e("Response", response);
            } catch(java.io.IOException e){
                e.printStackTrace();
            }
            Log.e("uploadFile", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);

            if(serverResponseCode == 200){

                /*runOnUiThread(new Runnable() {
                    public void run() {

                        String msg = "File Upload Completed.";

                        messageText.setText(msg);
                        //Toast.makeText(MainActivity.this, "File Upload Completed.",
                        //Toast.LENGTH_SHORT).show();
                    }
                });*/
            }

            dos.flush();
            dos.close();

        } catch (MalformedURLException e) {

            e.printStackTrace();
            Log.e("Upload file to server", "error: " + e.getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Upload", "Exception : "
                    + e.getMessage(), e);
        }

        return response;
    }

    public static String httpsRequestPost(String uri, Map<String, String> params){
        URLConnection conn;
        DataOutputStream dos;
        String response = null;
        try {
            // open a URL connection to the Servlet
            URL url = new URL(uri);
            // Open a HTTP  connection to  the URL
            conn = url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestProperty("Connection", "Keep-Alive");
            dos = new DataOutputStream(conn.getOutputStream());

            Set keySet = params.keySet();
            StringBuilder sb = new StringBuilder();
            for (Object objKey : keySet) {
                //有了鍵就可以通過map集合的get方法獲取其對應的値

                String key = objKey.toString();
                String value = params.get(key);

                sb.append(key).append("=").append(value).append("&");

                Log.e("Params", "key: " + key + ", value: " + value);
            }
            if(params.size() != 0){
                sb.deleteCharAt(sb.length()-1);
            }

            Log.e("StringBuilder", sb.toString());
            dos.writeBytes(sb.toString());
            

            // Responses from the server (code and message)

            try{
                BufferedReader br;
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
                response = sb.toString();
                Log.e("Response", response);
            } catch(java.io.IOException e){
                e.printStackTrace();
            }


            dos.flush();
            dos.close();

        } catch (MalformedURLException e) {

            e.printStackTrace();
            Log.e("Upload file to server", "error: " + e.getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Upload", "Exception : "
                    + e.getMessage(), e);
        }
        return response;
    }
    public static Drawable getDrawable(String imageUrl)
    {
        try
        {
            URL url = new URL(imageUrl);
            URLConnection connection = url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return Drawable.createFromStream(input, imageUrl);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

}
