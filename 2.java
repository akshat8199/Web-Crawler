import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Stack;
import java.util.HashSet;
import java.io.IOException;
import java.io.FileWriter;

public class WebCrawler {

	private static FileWriter UrlFile;
	private static FileWriter ParaFile;
	private static Stack<String> pendingURLs = new Stack<>();
	private static HashSet<String> visitedURLs = new HashSet<>();
	private static int depth=5;

	public static void getUrls(String url,String newBase) {

		try {
			Document file = Jsoup.connect(url).ignoreHttpErrors(true).ignoreContentType(true).get();
			Elements links = file.getElementsByTag("a");
			for (Element link : links) {
				String linkHref = link.attr("href");
				String linkText = link.text();
				if(linkHref.contains("#") == false && linkHref.contains("http")== false && linkHref.contains("https")== false) {

					String baseUrl = newBase;
					String completeUrl = baseUrl + linkHref;

					if(!visitedURLs.contains(completeUrl)) {

						pendingURLs.add(completeUrl);
						visitedURLs.add(completeUrl);
						try {
							//adding it to the file
							String newElement = completeUrl + "," + linkText;
							UrlFile.write(newElement);
							String line="\n";
							UrlFile.write(line);

						}
						catch (IOException e) {
							System.out.println("Error in writing in File");
							e.printStackTrace();
						}
					}
				}
				//if it is a complete url
				else if (linkHref.contains("http://pec.ac.in")== true || linkHref.contains("https://pec.ac.in")== true) {

					if(!visitedURLs.contains(linkHref)) {

						pendingURLs.add(linkHref);
						visitedURLs.add(linkHref);

						try {
							//adding it to the file
							String newElement = linkHref + "," + linkText;
							UrlFile.write(newElement);
							String line="\n";
							UrlFile.write(line);
						}
						catch (IOException e) {
							System.out.println("Error in writing in File");
							e.printStackTrace();
						}
					}
				}
			}

			file = Jsoup.connect(url).ignoreHttpErrors(true).ignoreContentType(true).get();
			Elements paras = file.getElementsByTag("p");
			//Separating content from p tag and sending them in corresponding csv file

			for (Element paraElement : paras) {

				String content = paraElement.text();
				if(content.length()>0) {
					try {
						//adding it to the file
						String newElement = "<p>" + "," + content;
						ParaFile.write(newElement);
						String line="\n";
						ParaFile.write(line);
					}
					catch (IOException e) {
						System.out.println("Error in writing in File");
						e.printStackTrace();
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
			UrlFile = new FileWriter("URLs.csv");
			String header1 = "Anchor Tags , Text";
			UrlFile.write(header1);
			String line="\n";
			UrlFile.write(line);
			System.out.println("URL File Created");

			//adding headers in file 2
			ParaFile = new FileWriter("Paragraphs.csv");
			String header2 = "Link Text,URL\n";
			ParaFile.write(header2);
			ParaFile.write(line);
			System.out.println("Paragraph File Created");

		}
		catch (IOException e) {
			System.out.println("Error in Creating File");
			e.printStackTrace();
		}

		//starting url
		pendingURLs.add("http://pec.ac.in");
		visitedURLs.add("http://pec.ac.in");

		//till stack is not empty and we have not reached maximum limit of our dfs crawler
		while (!pendingURLs.isEmpty() && depth>0) {
			depth--;
			String newBase=pendingURLs.pop();
			//first argument is the page which will be crawled and it will only be the base url for all the relative urls present on that page
			getUrls(newBase,newBase);
		}

		try {
			UrlFile.close();
			ParaFile.close();
			System.out.println("Files Closed Successfully");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
