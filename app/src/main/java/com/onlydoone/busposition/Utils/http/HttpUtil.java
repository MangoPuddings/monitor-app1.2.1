package com.onlydoone.busposition.Utils.http;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpUtil {
    //使用Get方法，path存储一个网址，Map存储一个键值对
    public static void sendHttpRequestForGet(final String path,final Map<String,String> params , final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;

                try{
                    StringBuilder stringBuilder=new StringBuilder();
                    StringBuilder response=new StringBuilder();
                    stringBuilder.append(path).append("?");
                    if(params!=null&&params.size()!=0){
                        for(Map.Entry<String,String> entry:params.entrySet()){
                            //转换成UTF-8
                            stringBuilder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(),"utf-8"));
                            stringBuilder.append("&");
                        }
                        //删除最后一个字符&
                        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    }
                    //此时网址变长了，增加了Map中的内容
                    URL url=new URL(stringBuilder.toString());
                    //打印网址
                    //Logs.e("IOUtil",stringBuilder.toString());

                    connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(100000);
                    connection.setReadTimeout(100000);
                    connection.setRequestProperty("Content-Type", "application/json");
                    //200表示连接成功
                    if(connection.getResponseCode()==200){
                        InputStream in=connection.getInputStream();
                        BufferedReader reader=new BufferedReader(new InputStreamReader(in,"utf-8"));
                        String line;
                        while ((line=reader.readLine())!=null){
                            response.append(line);
                        }
                    }else{
                        System.out.println(connection.getResponseCode());
                        //Logs.e("IOUtil","fail");
                    }
                    if(listener!=null){
                        //Logs.e("IOUtil",response.toString());
                        //把response转换为String类型再作为参数传入，以便调用他的类访问
                        listener.onFinish(response.toString());
                    }
                }catch(Exception e){
                    if (listener!=null){
                        listener.onError(e);
                    }
                }finally {
                    if(connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public static void sendHttpRequestForPost(final String path,final Map<String,Object> paramsValue, final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                try{
                    StringBuilder response=new StringBuilder();
                    JSONObject jsonObject = new JSONObject();
                    for(Map.Entry<String,Object> entry:paramsValue.entrySet()){
                        jsonObject.put(entry.getKey(),entry.getValue());
                    }
                    URL url=new URL(path);
                    connection=(HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(100000);
                    connection.setReadTimeout(100000);
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    //千万要记着这个setRequestProperty
                    connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    //Logs.d("IOUtil", jsonObject.toString());
                    //将数据写给服务器
                    DataOutputStream out= new DataOutputStream(connection.getOutputStream());
                    out.writeBytes(URLEncoder.encode(jsonObject.toString(), "utf-8"));
                    //Logs.d("IOUtil",jsonObject.toString());
                    //成功
                    if(connection.getResponseCode()==200){
                        //Logs.d("IOUtil", "success");
                        InputStream in=connection.getInputStream();
                        BufferedReader reader=new BufferedReader(new InputStreamReader(in,"utf-8"));
                        String line;
                        while ((line=reader.readLine())!=null){
                            response.append(line);
                        }
                    }else{
                        //Logs.e("IOUtil",Integer.toString(connection.getResponseCode()));
                        //Logs.e("IOUtil","fail");
                    }
                    if(listener!=null){
                        //Logs.e("IOUtil","注册成功");
                        //Logs.e("IOUtil",response.toString());
                        listener.onFinish(response.toString());
                    }
                }catch(Exception e){
                    if (listener!=null){
                        //Logs.d("IOUtil",e.getMessage());
                        listener.onError(e);
                    }
                }finally {
                    if(connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}