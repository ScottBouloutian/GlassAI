package com.bouloutian.connect_four;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

public class AmazonInterface {

	public static String upload(InputStream inputStream) throws ClientProtocolException, IOException{
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpPost httppost = new HttpPost("http://ec2-54-82-63-132.compute-1.amazonaws.com/opencv.php");
        MultipartEntity mpEntity = new MultipartEntity();
        ContentBody contentFile = new InputStreamBody(inputStream,"GlassImage");
        mpEntity.addPart("userfile", contentFile);
        httppost.setEntity(mpEntity);
        System.out.println("executing request " + httppost.getRequestLine());
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity resEntity = response.getEntity(); 
 
        if(!(response.getStatusLine().toString()).equals("HTTP/1.1 200 OK")){
            // Successfully Uploaded
        }
        else{
            // Did not upload. Add your logic here. Maybe you want to retry.
        }
        System.out.println(response.getStatusLine());
        if (resEntity != null) {
        	String output=EntityUtils.toString(resEntity);
            String[] temp = output.split("<BEST_MOVE>");
			if (temp.length > 1) {
				int bestMove = Integer.parseInt(String.valueOf(temp[1]
						.charAt(0)));
				return "Best move is column " + bestMove + ".";
			}
        }
        if (resEntity != null) {
            resEntity.consumeContent();
        }
        httpclient.getConnectionManager().shutdown();
        return "";
    }
	
}
