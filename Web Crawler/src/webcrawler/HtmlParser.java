package webcrawler;

import java.util.HashSet;
import java.util.Set;
import java.io.IOException;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class HtmlParser {
	private Set<String> links = new HashSet<String>();
	private static Set<String> resultSet = new HashSet<String>();

	public void crawl(String url, String domainName) {
		try {
			// Disable logger for unnecessary warnings
			java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit")
					.setLevel(java.util.logging.Level.OFF);
			java.util.logging.Logger.getLogger("org.apache.http").setLevel(
					java.util.logging.Level.OFF);
			if (isInvalidUrl(url))
				return;
			// Here we use htmlunit to parse websites
			WebClient webClient = new WebClient(BrowserVersion.CHROME);
			webClient.getOptions().setUseInsecureSSL(true);
			// setJavaScriptEnabled(true) is important. We have to parse some
			// contents after scripts loaded
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient.getOptions().setTimeout(50000);
			webClient.getOptions().setDoNotTrackEnabled(false);
			HtmlPage page = webClient.getPage(url);
			// Wait for scripts loading
			Thread.sleep(3000);
			findEmailAddresses(page, domainName);
			findValidLinks(page, "a", domainName);
			findValidLinks(page, "div", domainName);
			enqueue();
			webClient.close();
		} catch (IOException e) {
			System.out.println("**Failure**  HTTP response fails");
		} catch (InterruptedException e) {
			System.out.println("**Failure**  HTTP response fails");
		} catch (ClassCastException e) {
			System.out.println("**Failure**  HTTP response fails");
		}
	}

	private void enqueue() {
		for (String link : this.links) {
			// The last '/' is useless
			if (link.endsWith("/")) {
				link = link.substring(0, link.length() - 1);
			}
			// Remove "www." for all urls
			if (link.contains("://www.")) {
				link = "http://"
						+ link.substring(link.indexOf("://www.") + 7,
								link.length());
			}
			// These key words may indicate the location of email address. So
			// put them into priorUrl Queue
			if (link.contains("contact") || link.contains("email")
					|| link.contains("about") || link.contains("company")
					|| link.contains("career")) {
				LinkQueue.addPriorUrl(link);
			} else {
				LinkQueue.addUnvisitedUrl(link);
			}
		}
	}

	private void findValidLinks(HtmlPage page, String tagName, String domainName) {
		DomNodeList<HtmlElement> links = page.getBody().getElementsByTagName(
				tagName);

		for (DomElement link : links) {
			String href = link.getAttribute("href");
			// For absolute paths
			if (href.contains(domainName) && !href.startsWith("//")) {
				this.links.add(href);
			}
			// For absolute paths with "//" ahead
			else if (href.contains(domainName) && href.startsWith("//")) {
				this.links.add("http:" + href);
			}
			// For relative paths
			else if (href.startsWith("/") && !href.startsWith("//")) {
				this.links.add("http://" + domainName + href);
			}
		}
	}

	private void findEmailAddresses(HtmlPage page, String domainName) {
		if (page == null) {
			return;
		}
		String bodyText = page.getBody().asText();
		// Split by [\t\n\x0B\f\r]
		String[] cookedText = bodyText.split("\\s");
		for (int i = 0; i < cookedText.length; i++) {
			if (cookedText[i].contains("@") && !cookedText[i].startsWith("@")) {
				resultSet.add(cookedText[i]);
			}
		}
	}

	private boolean isInvalidUrl(String url) {
		// Avoid any content-type except html
		return (url.endsWith(".pdf") || url.endsWith(".rar")
				|| url.endsWith(".mp3") || url.endsWith(".mp4"));
	}

	public static Set<String> getResults() {
		return resultSet;
	}
}
