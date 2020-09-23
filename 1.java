import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Stack;
import java.util.HashSet;
import java.io.IOException;
import java.io.FileWriter;

public class FocussedWebCrawler {


	private static FileWriter UrlFileFaculty;

	private static FileWriter ParagraphFileFaculty;
	private static Stack<String> pendingURLs = new Stack<>();
	private static HashSet<String> visitedURLs = new HashSet<>();
	private static int depth=5;

	public static void getParaContent(String url) {

		try {
			Document file = Jsoup.connect(url).ignoreHttpErrors(true).ignoreContentType(true).get();

			file = Jsoup.connect(url).get();
			Elements paras = file.getElementsByTag("p");
			for (Element pElement : paras) {
				String content = pElement.text();
				if(content.length()>0) {
					try {
						String newElement = "<p>" + "," + content;
						ParagraphFileFaculty.write(newElement);
						String line="\n";
						ParagraphFileFaculty.write(line);

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

	public static void getUrls(String url,String newBase) {

		try {
			Document file = Jsoup.connect(url).ignoreHttpErrors(true).ignoreContentType(true).get();

			//Anchor tag elements
			Elements links = file.getElementsByTag("a");

			//Separating href from content and sending them in corresponding csv file
			for (Element link : links) {
				String linkHref = link.attr("href");
				String linkText = link.text();

				//checking for static and relative urls
				if(linkHref.contains("#") == false && linkHref.contains("http")== false && linkHref.contains("https")== false) {

					String baseUrl = newBase;
					String completeUrl = baseUrl + linkHref;
					//focussed crawler containing "Faculty" in url
					if(completeUrl.contains("faculty")==true || completeUrl.contains("Faculty")==true || completeUrl.contains("FACULTY")==true) {

						//fetch para content
						getParaContent(completeUrl);

						if(!visitedURLs.contains(completeUrl)) {

							pendingURLs.add(completeUrl);
							visitedURLs.add(completeUrl);

							try {
								//adding it to the file
								String newElement = completeUrl + "," + linkText;
								UrlFileFaculty.write(newElement);
								String line="\n";
								UrlFileFaculty.write(line);

							}
							catch (IOException e) {
								System.out.println("Error in writing in File");
								e.printStackTrace();
							}
						}
					}
				}
				//if it is a complete url
				else if (linkHref.contains("http://pec.ac.in")== true || linkHref.contains("https://pec.ac.in")== true) {

					if(linkHref.contains("faculty")==true || linkHref.contains("Faculty")==true || linkHref.contains("FACULTY")==true) {

						//fetch para content
						getParaContent(linkHref);

						if(!visitedURLs.contains(linkHref)) {

							pendingURLs.add(linkHref);
							visitedURLs.add(linkHref);
							try {
								//adding it to the file
								String newElement = linkHref + "," + linkText;
								UrlFileFaculty.write(newElement);
								String line="\n";
								UrlFileFaculty.write(line);
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

			UrlFileFaculty = new FileWriter("FacultyURLs.csv");
			String header1 = "Anchor Tags , Text";
			UrlFileFaculty.write(header1);
			String line="\n";
			UrlFileFaculty.write(line);
			System.out.println("URL File Created");

			//adding headers in file 2
			ParagraphFileFaculty = new FileWriter("FacultyParagraphs.csv");
			String header2 = "Link Text,URL\n";
			ParagraphFileFaculty.write(header2);
			ParagraphFileFaculty.write(line);
			System.out.println("Paragraph File Created");

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
			getUrls(newBase,newBase);
		}

		try {
			UrlFileFaculty.close();
			ParagraphFileFaculty.close();
			System.out.println("Files Closed Successfully");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
