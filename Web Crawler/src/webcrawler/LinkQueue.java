package webcrawler;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Queue;

public class LinkQueue {
	private static Set<String> visitedUrl = new HashSet<String>();
	private static Queue<String> unvisitedUrl = new PriorityQueue<String>();
	private static Queue<String> priorUrl = new PriorityQueue<String>();

	public static void addVisitedUrl(String url) {
		visitedUrl.add(url);
	}

	public static Object unvisitedUrlDeQueue() {
		return unvisitedUrl.poll();
	}

	public static Object priorUrlDeQueue() {
		return priorUrl.poll();
	}

	public static void addUnvisitedUrl(String url) {
		if (url != null && !url.trim().equals("") && !visitedUrl.contains(url)
				&& !unvisitedUrl.contains(url) && !priorUrl.contains(url)) {
			unvisitedUrl.add(url);
		}
	}

	public static void addPriorUrl(String url) {
		if (url != null && !url.trim().equals("") && !visitedUrl.contains(url)
				&& !unvisitedUrl.contains(url) && !priorUrl.contains(url)) {
			priorUrl.add(url);
		}
	}

	public static int getVisitedUrlNum() {
		return visitedUrl.size();
	}

	public static boolean unvisitedUrlEmpty() {
		return unvisitedUrl.isEmpty();
	}

	public static boolean priorUrlEmpty() {
		return priorUrl.isEmpty();
	}
}
