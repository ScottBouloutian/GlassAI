package com.bouloutian.connect_four;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

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

	private static final String SERVICE_HOST_NAME = "54.225.155.110";
	
	public static String upload(BufferedImage image) throws ClientProtocolException, IOException{
		// Before uploading the image, it makes sense to resize it to a manageable size.
		// Currently, the AI program uses the dimensions 622 x 457 for its images, so that is what we'll use here.
		final int RESIZED_WIDTH = 622;
		final int RESIZED_HEIGHT = 457;
		BufferedImage resizedImage = new BufferedImage(RESIZED_WIDTH, RESIZED_HEIGHT, image.getType());
		Graphics g = resizedImage.createGraphics();
		g.drawImage(image, 0, 0, RESIZED_WIDTH, RESIZED_HEIGHT, null);
		g.dispose();

		// Turn the resized image into an InputStream object
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(resizedImage, "jpeg", outputStream);
		InputStream newInputStream = new ByteArrayInputStream(outputStream.toByteArray());
		
		// Now that the image is resized, attempt to upload it to the AI service.
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpPost httppost = new HttpPost("http://" + SERVICE_HOST_NAME + "/opencv.php");
        MultipartEntity mpEntity = new MultipartEntity();
        ContentBody contentFile = new InputStreamBody(newInputStream,"GlassImage");
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
        	System.out.println(output);
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
