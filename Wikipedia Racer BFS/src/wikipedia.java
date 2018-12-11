import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.*;
import java.time.*;

public class wikipedia {
	public static ArrayList<Node> queue = new ArrayList<Node>();
	public static ArrayList<String> visited = new ArrayList<String>();
	public static String begin;
	public static String end;
	public static boolean found = false;
	public static String endPath = "";
	public static String fileStoragePath = "wiki-storage";
	
	public static void main (String args[]) throws IOException{
		
		//Creates new Path for storage
		File f = new File(fileStoragePath);
		Path p = Paths.get(f.getAbsolutePath());
		if (!Files.exists(p)) {
            		Files.createDirectories(p);
		}
		
		//Getting user input
		Scanner reader = new Scanner (System.in);
		System.out.println("Enter begin:");
		begin = reader.nextLine();
		System.out.println("Enter end:");
		end = reader.nextLine();
		System.out.println("Starting at " + begin);
		System.out.println("Aiming for " + end);
		
		//Starts timer
		long startTime = System.currentTimeMillis();
		
		//Begin BFS
		queue.add(new Node(begin, ""));
		while (found == false && queue.isEmpty()==false) {
			
			if (visited.contains(queue.get(0).pageName)) {
				queue.remove(0);
			}
			else {
				Node n = queue.get(0);
				System.out.println("Checking page : "+n.path+"->"+n.pageName);
				
				visited.add(n.pageName);
				
				try{
					getPageLinks(n);
				}catch(Exception e) {
					//System.out.println("NOT PAGE");
				}
				
				queue.remove(0);
			}
	
		} // End BFS
		
		long endTime = System.currentTimeMillis();
		
		if (!found) {
			System.out.println("Path not possible");
			return;
		}
		
		System.out.println();
		System.out.println("Found " + end);
		System.out.println(endPath);
		System.out.println("Found in " + ((endTime-startTime)/(float)1000) + " seconds");
		
		
	}
	
	public static String getPage(String pageTitle) throws IOException {
		//I was having trouble accessing the Internet the usual way I do it, due to
		//how the wikipedia database is structured.
		//For troubleshooting, this method was definitely inspired by:
		// https://stackoverflow.com/questions/33020645/get-brief-content-from-wikipedia-by-a-java-program
		
		
		URL url = new URL("https://en.wikipedia.org/w/index.php?action=raw&title=" + pageTitle.replace(" ", "_").replace("[[", "").replace("]]", ""));
		String text = "";
		
		try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()))) {
		    String line = null;
		    while (null != (line = br.readLine())) {
		        line = line.trim();
		            text += line;
		    }
		}
		return text;
	}
	
	public static void getPageLinks(Node node) throws IOException{
		File f = new File(fileStoragePath+"/"+node.pageName + ".txt");
		
		boolean goOffline = f.exists();
		if (goOffline) { // Checking links from offline system
			Scanner in = new Scanner(f);
			ArrayList<String> links = new ArrayList<String>();
			while (in.hasNextLine()) {
				String link = (in.nextLine());
				links.add(link);
				if (!visited.contains(link)) {
					queue.add(new Node(link, node.path+"->"+node.pageName));
				}
				if (link.toLowerCase().trim().equals(end.toLowerCase().trim())) {
					found = true;
					endPath = node.path+"->"+node.pageName+"->"+link;
				}
			}
			if (links.isEmpty()) {
				goOffline = false;
			}
			in.close();
		}
		if (!goOffline) { // Getting links from online and save to offline
			PrintWriter out = new PrintWriter(f);
			String page = getPage(node.pageName);
			ArrayList<String> links = new ArrayList<String>();
			while (page.contains("[[")) { // Parsing page for links in [[]]
				page = page.substring(page.indexOf("[["));
				int endex = page.indexOf("]]");
				
				String link = page.substring(0, endex+2);
				if (link.contains(":")) {
					page=page.substring(endex+2);
				}
				else {
					if (link.contains("|")) {
						link = link.substring(0, link.indexOf("|"))+"]]";
					}
					link = link.substring(2, link.length()-2);
					out.println(link);
					links.add(link);
					if (!visited.contains(link)) {
						queue.add(new Node(link, node.path+"->"+node.pageName));
					}
					if (link.toLowerCase().trim().equals(end.toLowerCase().trim())) {
						found = true;
						endPath = node.path+"->"+node.pageName+"->"+link;
					}
					page = page.substring(endex+2);
				}
				
			}
			out.close();
		}
	}
}
