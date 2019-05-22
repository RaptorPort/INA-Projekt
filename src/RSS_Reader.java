import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class RSS_Reader {


	public void console() {
		System.out.println("Enter URL to get RSS from:");
		Scanner in = new Scanner(System.in); 
        String url = in.next(); 
        if (!url.startsWith("http//:")) {
        	url = "https://" + url;
        }
        in.close();
        getRSSlinks(url);
	}
	
	private static URL makeURL(String in) {
		URL url = null;
		try {
			url = new URL(in);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			if (!in.startsWith("http//:")) {
	        	in = "https://" + in;
	        }
		}
		return url;
		
	}
	
	private static void streamToFile(String name, InputStream inStream) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\nicom\\git\\repository\\INA-Projekt\\temp\\" + name));
			Scanner in = new Scanner(inStream);
	   
	    	while (in.hasNextLine()) {
				writer.write(in.nextLine());
				writer.newLine();
			} 
	    	in.close();
	    	writer.close();
	    }
	    catch (IOException e) {
				e.printStackTrace();
			}    
	}
	
	public static InputStream httpConnection(URL url) {
		try {
			if (url.getProtocol().toString() == "https") {
				HttpsURLConnection huc = (HttpsURLConnection) url.openConnection();
				huc.setRequestMethod("GET");
	
				if (huc.getResponseCode() != HttpsURLConnection.HTTP_OK) {
					//System.out.println(huc.getResponseMessage());
				} else {
					//System.out.println(huc.getResponseMessage()); // OK
					return huc.getInputStream();
				}
			} else if (url.getProtocol().toString() == "http") {
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
	
	
	
	private static Vector<URL> getRSSlinks(String myurl) {
		URL url = makeURL(myurl);
		Vector<URL> result = new Vector();
		
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
				for (int i = 1; i <= m.groupCount(); i++)
					try {
						System.out.println(i);
						//System.out.println(m.group(i));
						result.add(new URL(m.group(i)));
					} catch (MalformedURLException e) {
						//skip
					}
			}
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
		getXMLfiles(getRSSlinks("https://www.spiegel.de"));

	}

}
