package com.example.nouno.locateme;

import com.example.nouno.locateme.Data.WebResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by nouno on 06/09/2017.
 */

public class QueryUtils {
    //public static String IP_ADDRESSE = "192.168.0.57";

    public static WebResponse makeHttpPostRequest (String urlString, Map<String,String> parameters)
    {   String response = null;
        WebResponse webResponse = new WebResponse(false,null);
        InputStream inputStream=null;
        HttpURLConnection urlConnection=null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            String postParameters = buildParametersString(parameters);
            urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(postParameters);
            out.close();
            urlConnection.connect();
            if (urlConnection.getResponseCode()==200)
            {
                inputStream = urlConnection.getInputStream();
                response=readFromStream(inputStream);
                webResponse.setResponseString(response);
            }
            else
            {
                webResponse.setError(true);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (inputStream!=null)
            {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {

                webResponse.setError(true);

            }
            if (urlConnection!=null)
            {
                urlConnection.disconnect();
            }
            return webResponse;
        }
    }
    public static String buildParametersString(Map<String,String> parameters)
    {
        Set<Map.Entry<String,String>> entrySet = parameters.entrySet();
        Iterator<Map.Entry<String,String>> iterator = entrySet.iterator();
        StringBuilder builder = new StringBuilder("");
        boolean isFirstParameter = true;
        while (iterator.hasNext())
        {
            Map.Entry<String,String> entry = iterator.next();
            if (!isFirstParameter)
            {
                builder.append("&");
            }
            builder.append(entry.getKey()+"="+entry.getValue());
            isFirstParameter=false;
        }
        return builder.toString();
    }

    public static WebResponse makeHttpGetRequest (String urlString, Map<String,String> parameters)
    {
        String response = null;
        WebResponse webResponse = new WebResponse(false,null);
        InputStream inputStream=null;
        HttpURLConnection urlConnection=null;
        try {
            String postParameters = buildParametersString(parameters);
            URL url = new URL(urlString+"?"+postParameters);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(postParameters);
            out.close();
            urlConnection.connect();
            if (urlConnection.getResponseCode()==200)
            {
                inputStream = urlConnection.getInputStream();
                response=readFromStream(inputStream);
                webResponse.setResponseString(response);
            }
            else
            {
                webResponse.setError(true);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (inputStream!=null)
            {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {

                webResponse.setError(true);

            }
            if (urlConnection!=null)
            {
                urlConnection.disconnect();
            }
            return webResponse;
        }
    }

    //méthode pour lire un inputstream (zellal)

    public static String readFromStream (InputStream inputStream)
    {
        StringBuilder builder = new StringBuilder("");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(inputStreamReader);
        try {
            String line =reader.readLine();
            while (line != null)
            {
                builder.append(line);
                line=reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            return builder.toString();
        }
    }
}
