import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;


public class RSS_Reader {


	public static void console() {
		deleteTempFiles();
		System.out.println("Enter URL to get RSS from:");
		Scanner in = new Scanner(System.in); 
        String s = in.next(); 
        URL url = makeURL(s);
		getXMLfiles(getRSSlinks(url));
        in.close();
	}
	
	private static URL makeURL(String in) {
		URL url = null;
		try {
			url = new URL(in);
		} catch (MalformedURLException e) {
			//e.printStackTrace();
			if (e.getMessage().contains("no protocol")) {
	        	in = "https://" + in;
	        	return makeURL(in);
	        }
		}
		return url;
		
	}
	
	private static void streamToFile(String name, InputStream inStream) {
		try {
			OutputStream outStream = new FileOutputStream("temp/" + name);
				
			byte[] buffer = new byte[8 * 1024];
			int bytesRead;
			while ((bytesRead = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
				}
				inStream.close();
				outStream.close();
				
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	private static void deleteTempFiles() {
		File dir = new File("temp/");
		
		if(dir.isDirectory() == false) {
			System.out.println("Not a directory. Do nothing");
			return;
		}
		File[] listFiles = dir.listFiles();
		for(File file : listFiles){
			System.out.println("Deleting "+file.getName());
			file.delete();
		}
	}
	
	public static InputStream httpConnection(URL url) {
		try {
			if (url.getProtocol().equals("https")) {
				HttpsURLConnection huc = (HttpsURLConnection) url.openConnection();
				huc.setRequestMethod("GET");
	
				if (huc.getResponseCode() != HttpsURLConnection.HTTP_OK) {
					//System.out.println(huc.getResponseMessage());
				} else {
					//System.out.println(huc.getResponseMessage()); // OK
					return huc.getInputStream();
				}
			} else if (url.getProtocol().equals("http")) {
				HttpURLConnection huc = (HttpURLConnection) url.openConnection();
				huc.setRequestMethod("GET");
	
				if (huc.getResponseCode() != HttpURLConnection.HTTP_OK) {
					//System.out.println(huc.getResponseMessage());
				} else {
					//System.out.println(huc.getResponseMessage()); // OK
					return huc.getInputStream();
				}
			}
				
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	private static Vector<URL> getRSSlinks(URL url) {
		Vector<URL> result = new Vector<URL>();
		
		InputStream is = httpConnection(url);
		if (is == null) {
			return result;
		}
		Scanner in = new Scanner(is);
		Pattern p = Pattern.compile("href=\"(.*?\\.rss)");
				
		while (in.hasNextLine()) {
			String s = in.nextLine();
			Matcher m = p.matcher(s);
			while(m.find()) {
				for (int i = 1; i <= m.groupCount(); i++) {
					result.add(makeURL(m.group(i)));
				}
			}
			System.out.println(s);
		} 
    	in.close();
    	try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return result;
	}
	
	public static void getXMLfiles(Vector<URL> rssLinks) {
		System.out.println("Get XML files");
		int i = 1;
		for (URL url : rssLinks) {
			streamToFile("test" + i + ".xml", httpConnection(url));
			i++;
		}
	}
	
	public static void main(String[] args) {
		console();
		//deleteTempFiles();
		//URL url = makeURL("www.spiegel.de");
		//getXMLfiles(getRSSlinks(url));

	}

}
