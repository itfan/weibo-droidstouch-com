/**
 *EmotionManager.java
 *2011-11-1 下午09:35:02
 *Touch Android
 *http://bbs.droidstouch.com
 */
package com.droidstouch.iweibo.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author <a href="http://bbs.droidstouch.com">Touch Android</a>
 *
 */
public class EmotionManager
{

	
	
	private String root="/sdcard/emotions/";
	
	private BlockingQueue<String> urlQueue =null;
	
	
	
	public EmotionManager( int emotionSize)
	{
		urlQueue = new ArrayBlockingQueue(emotionSize);
		createDir();
	}
	
	
	public void putURL(String url)
	{
		try
		{
			urlQueue.put(url);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public void createDir()
	{
		
		File file = new File(root);
		if(file.exists())
			file.mkdirs();
		
	}
	
	
	public void startThread()
	{
		
		new DownloadImageThread().start();
	}
	
	
	
	
	public void downEmotions(String url)
	{
		try
		{
			down(url);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	public void down(String url) throws Exception
	{
		
		URL u = new URL(url);
		
	 HttpURLConnection connection = 	(HttpURLConnection) u.openConnection();
	
	 String path = root + getEmotionName(url);
	 saveFile(path, connection.getInputStream());
		
	}
	
	
	
	public String getEmotionName(String url)
	{
		
		return url.substring(url.lastIndexOf(".") +1);
		
	}
	
	
	
	public void saveFile(String path,InputStream is)
	{
		
		
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		
		FileOutputStream fos = null;
		
		
		try
		{
			bis = new BufferedInputStream(is);
			fos = new FileOutputStream(new File(path));
			bos = new BufferedOutputStream(fos);
			
			byte[] buffer = new byte[1024];
			int length;
			
			while((length = bis.read(buffer)) != -1)
			{
				bos.write(buffer, 0, length);
			}
			
		} catch (Exception e)
		{
			
		}
		finally
		{
			
			try
			{
				if(null != bis)
				{
					bis.close();
				}
				
				if(fos != null)
					fos.close();
				if(null != bos)
					{
						bos.flush();
						bos.close();
					
					}
				
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		
	}
	

	
	private  class DownloadImageThread extends Thread
	{
		
		
		private boolean isRun=true;
		
		public void shutDown()
		{
			isRun =false;
		}
		
		
		public void run()
		{
			try
			{
				while(isRun)
				{
					String url= urlQueue.poll();
					
					if(null == url) break;
					
					downEmotions(url);
				}
			}
			catch (Exception e)
			{
				
			}
			finally
			{
				shutDown();
			}
			
		}
		
		
		
		
	}
	
	
	
	
	
	
}
