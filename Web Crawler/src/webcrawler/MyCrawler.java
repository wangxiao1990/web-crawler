package webcrawler;

import java.util.Set;

public class MyCrawler {
	private static final int MAX_PAGES_TO_SEARCH = 10;

	public void crawling(String domainName) {
		String seed = "http://" + domainName;
		LinkQueue.addUnvisitedUrl(seed);
		while ((!LinkQueue.unvisitedUrlEmpty() || !LinkQueue.priorUrlEmpty())
				&& LinkQueue.getVisitedUrlNum() <= MAX_PAGES_TO_SEARCH) {
			String currentUrl;
			if (!LinkQueue.priorUrlEmpty()) {
				currentUrl = (String) LinkQueue.priorUrlDeQueue();
			} else {
				currentUrl = (String) LinkQueue.unvisitedUrlDeQueue();
			}
			if (currentUrl == null)
				continue;
			LinkQueue.addVisitedUrl(currentUrl);
			HtmlParser parser = new HtmlParser();
			System.out.println("**Searching** " + currentUrl);
			parser.crawl(currentUrl, domainName);
		}

		Set<String> resultSet = HtmlParser.getResults();
		System.out.println("Found these email addresses:");
		for (String result : resultSet) {
			System.out.println(result);
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyCrawler crawler = new MyCrawler();
		crawler.crawling(args[0]);
	}

}
