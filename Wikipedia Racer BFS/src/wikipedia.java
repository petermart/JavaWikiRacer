import java.util.*;
import java.io.*;
import java.net.*;
import java.time.*;
public class wikipedia {
	public static ArrayList<Node> queue = new ArrayList<Node>();
	public static ArrayList<String> visited = new ArrayList<String>();
	public static String begin;
	public static String end;
	public static boolean found = false;
	public static String endPath = "";
	
	public static void main (String args[]) throws IOException{

		Scanner reader = new Scanner (System.in);
		System.out.println("Enter begin:");
		begin = reader.nextLine();
		System.out.println("Enter end:");
		end = reader.nextLine();
		
		System.out.println("Starting at " + begin);
		System.out.println("Aiming for " + end);
		
		
//		getPageLinks(begin);
		long startTime = System.currentTimeMillis();
		queue.add(new Node(begin, ""));
		
		//queue object should not be string, and should contain a previous pointer,
		//so that it can find the way it points back to it.
		
		
		while (found == false) {
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
	
		}
		long endTime = System.currentTimeMillis();
		System.out.println();
		System.out.println("Found " + end);
		System.out.println(endPath);
		System.out.println("Found in " + ((endTime-startTime)/(float)1000) + " seconds");
		
		//Print out path of queue
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
		String page = getPage(node.pageName);
		ArrayList<String> links = new ArrayList<String>();
		while (page.contains("[[")) {
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
				//System.out.println(link);
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
		
		
	}
}
