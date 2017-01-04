package hisaab.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.IOUtils;


public class ImageHelper {

	public static boolean writeToFile(InputStream uploadedInputStream,
			String basePath, String fileName, String directory) {
		boolean flag = false;
		
		try {
			
			
			check(basePath+directory);
			File newFile = new File(basePath+directory+"/"+fileName);
			System.out.println(newFile.getAbsolutePath());
			System.out.println(newFile.exists());
			System.out.println("-----"+newFile.createNewFile());
			OutputStream out = new FileOutputStream(newFile);
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(newFile);
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		
			flag = true;
		} catch (IOException e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	public static  String getRandomString() {
		String allowedChars = "abcdefghijklmnopqrstuvwxyz";
		int length = 6;
		if (allowedChars == null || allowedChars.trim().length() == 0
				|| length <= 0) {
			throw new IllegalArgumentException("Please provide valid input");
		}
		Random rand = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(allowedChars.charAt(rand.nextInt(allowedChars.length())));
		}
		return sb.toString();
	}

	
	public static String getNewImgName(String fileName){
		int i = fileName.lastIndexOf('.');
		String extension = fileName.substring(i);
		return getRandomString()+"-"+System.currentTimeMillis()+extension;
	}
	
	
	public static void main(String[] args) {
		for (int i = 0; i < 6; i++) {
			System.out.println(getRandomString());
		}

	}
	
	public static void check(String dir){
		
		File file = new File(dir);
		
		if(!file.exists()){
			file.mkdirs();
		}
		System.out.println("directory exist : "+file.exists());
		
	}

	
	public static byte[] getImage(String filePath){
		byte[] imageData = null;
		try {
			
			InputStream inputStream = new FileInputStream(filePath);
			
			imageData = IOUtils.toByteArray(inputStream);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return imageData;
	}
	
	
	
	
	public static boolean smallProfileThumbnail(BufferedImage src,String uploadedFileLocation, String keyName, String directory) throws IOException{
		boolean resFlag =  false;
		float ratio_h ;
		int height = 0 ;
		InputStream inputStream;
		BufferedImage thumbnail = null;
		boolean crop_flag = false;
		
		if(src == null){
				System.out.println("src is null...");
			}
			int width = 250;
			System.out.println("original scale of image : "+src.getWidth()+" X "+src.getHeight());
			
			if(src.getWidth()<width && src.getHeight()<(width-25)){
				width = src.getWidth();
				height = src.getHeight();
				System.out.println("width : "+width);
				System.out.println("height : "+height);
			}
			else if(src.getWidth()>src.getHeight()){
				System.out.println("in if...");
				ratio_h = (float)src.getWidth()/src.getHeight();
				height = (int) (width/ratio_h);
				System.out.println("ratio : "+ratio_h);
				System.out.println("height : "+height);	
			}
			else {
				System.out.println("In else");
				ratio_h = (float)src.getHeight()/src.getWidth();
			
				height = (int) (width*ratio_h);
				System.out.println("ratio : "+ratio_h);
				System.out.println("size : "+height+" X "+width);
			}
//			BufferedImage thumbnail = Scalr.resize(src, Scalr.Method.SPEED, Scalr.Mode.FIT_EXACT,
//					               width, height, Scalr.OP_BRIGHTER);
			
			
					
			
		 
				thumbnail = Thumbnails.of(src)
						.size(width, height)         //Scalr.resize(src,Scalr.Method.SPEED,width,height,Scalr.OP_ANTIALIAS);
						.asBufferedImage();
			
			
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(thumbnail, "jpg", os);
				inputStream = new ByteArrayInputStream(os.toByteArray());
				
				ByteArrayOutputStream osDespo = new ByteArrayOutputStream();
				ImageIO.write(thumbnail, "jpg", osDespo);
				
				InputStream contentDesposition = new ByteArrayInputStream(osDespo.toByteArray());
				
				 
				resFlag = 	writeToFile(inputStream, uploadedFileLocation , keyName, directory); 
				
			
			return resFlag;
	}

	
	
	/**
	 * this method is for generating
	 * thumbnails of large size with width = 700
	 * it calculate ratio of original image and 
	 * set a height with same ratio. 
	 * @throws IOException */
	public static void largeProfileThumbnail(BufferedImage src, String uploadedFileLocation, String keyName, String directory) throws IOException{
		float ratio_h ;
		int height = 0 ;
		InputStream inputStream;
		BufferedImage thumbnail = null;
//		try{
//			BufferedImage src = ImageIO.read(new URL("http://ngagimagebin.s3-website-us-west-2.amazonaws.com/"+keyName));
			if(src == null){
				System.out.println("src is null...");
			}
//			int size = src.
			int width = 700;
			System.out.println("original scale of image : "+src.getWidth()+" X "+src.getHeight());
			
			 if(src.getWidth()<700 && src.getHeight()<700){
				width = src.getWidth();
				height = src.getHeight();
				System.out.println("width : "+width);
				System.out.println("height : "+height);
			}
			 else if(src.getWidth()>src.getHeight()){
				System.out.println("in if...");
				ratio_h = (float)src.getWidth()/src.getHeight();
				height = (int) (width/ratio_h);
				System.out.println("ratio : "+ratio_h);
				System.out.println("height : "+height);	
			}
			else {
				System.out.println("In else");
				ratio_h = (float)src.getHeight()/src.getWidth();
				height = (int) (width*ratio_h);
				System.out.println("ratio : "+ratio_h);
				System.out.println("height : "+height);
				}
			
			 
				thumbnail = Thumbnails.of(src)
						.size(width, height)         //Scalr.resize(src,Scalr.Method.SPEED,width,height,Scalr.OP_ANTIALIAS);
						.asBufferedImage();
				String extension = keyName.substring(keyName.lastIndexOf(".")+1);
//			ByteArrayOutputStream os = new ByteArrayOutputStream();
//			ImageIO.write(thumbnail, "jpg", os);
//			inputStream = new ByteArrayInputStream(os.toByteArray());
//			keyName = "large_thumbs.db/"+keyName;
		
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(thumbnail, "jpg", os);
				inputStream = new ByteArrayInputStream(os.toByteArray());
				
				ByteArrayOutputStream osDespo = new ByteArrayOutputStream();
				ImageIO.write(thumbnail, "jpg", osDespo);
				
				InputStream contentDesposition = new ByteArrayInputStream(osDespo.toByteArray());
				
				 
//				ImageUtil.uploadFile(inputStream, keyName, contentDesposition, PROFILE_LARGE);
				
				writeToFile(inputStream, uploadedFileLocation , keyName, directory);
	}
	
	

	
	
	
	public static boolean UploadProfilePicProcessing(InputStream uploadStreamVar, String UploadProfilePicProcessing, String keyName){
		boolean result = false;
		
		try {
			
			
			BufferedImage image = ImageIO.read(uploadStreamVar);
			
			result = smallProfileThumbnail(image, UploadProfilePicProcessing, keyName, Constants.USER_IMAGES_SMALL);
			
			largeProfileThumbnail(image, UploadProfilePicProcessing, keyName, Constants.USER_IMAGES_LARGE);
			
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		
		
		return result;
	}
	

	
	
}
