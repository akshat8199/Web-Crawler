import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Stack;
import java.util.HashSet;

public class WebCrawlerPDF {

	//creating URLFile that will contains anchor tags and content
	private static FileWriter URLFilePDF;
	//stack for dfs crawler
	private static Stack<String> pendingURLs = new Stack<>();
	//hashset to keep a check so that same url is not visited again
	private static HashSet<String> visitedURLs = new HashSet<>();
	//depth of the crawler set to 50
	private static int depth=50;

	public static void getURLs(String url,String newBase) {
		try {

			Document file = Jsoup.connect(url).ignoreHttpErrors(true).ignoreContentType(true).get();

			//Anchor tag elements
			Elements anchorTagLinks = file.getElementsByTag("a");

			//Separating href from content and sending them in csv file
			for (Element link : anchorTagLinks) {

				String hrefLink = link.attr("href");
				String textLink = link.text();

				//checking for static and relative urls
				if (hrefLink.contains("#") == false && hrefLink.contains("http") == false && hrefLink.contains("https") == false) {

					String baseURL = newBase;
					String completeURL = baseURL + hrefLink;

					if(completeURL.contains(".pdf")==true || completeURL.contains(".PDF")==true ) {

						if(!visitedURLs.contains(completeURL)) {

							pendingURLs.add(completeURL);
							visitedURLs.add(completeURL);

							try {
								//adding it to the file
								String newElement = completeURL + "," + textLink;
								URLFilePDF.write(newElement);
								String line="\n";
								URLFilePDF.write(line);
							}
							catch (IOException e) {
								System.out.println("Error in writing in File");
								e.printStackTrace();
							}
						}
					}
				}

				else if (hrefLink.contains("http://pec.ac.in")== true || hrefLink.contains("https://pec.ac.in")== true) {

					if(hrefLink.contains(".PDF")==true || hrefLink.contains(".pdf")==true) {

						if(!visitedURLs.contains(hrefLink)) {

							pendingURLs.add(hrefLink);
							visitedURLs.add(hrefLink);
							try {
								String newElement = hrefLink + "," + textLink;
								URLFilePDF.write(newElement);
								String line="\n";
								URLFilePDF.write(line);
							}
							catch (IOException e) {
								System.out.println("Error in writing in File");
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		try {
			//adding headers in file 1
			URLFilePDF = new FileWriter("URLsPDF.csv");
			String header1 = "Anchor Tags , Text";
			URLFilePDF.write(header1);
			String line="\n";
			URLFilePDF.write(line);
			System.out.println("URL File Created");
		}
		catch (IOException e) {
			System.out.println("Error in Creating File");
			e.printStackTrace();
		}

		pendingURLs.add("https://pec.ac.in/");
		visitedURLs.add("https://pec.ac.in/");
		while (!pendingURLs.isEmpty() && depth>0) {
			depth--;
			String newBase=pendingURLs.pop();
			getURLs(newBase,newBase);
		}

		try {
			URLFilePDF.close();
			System.out.println("File Closed Successfully");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
