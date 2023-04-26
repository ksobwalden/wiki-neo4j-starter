package ch.ksobwalden;

import org.neo4j.driver.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import static org.neo4j.driver.Values.parameters;


public class Main {

    public static void main(String... args) {
        System.out.print("""
                Enter:
                 1 for finding pages with a given prefix
                 2 for finding all pages linked to from a given page
                Option:\s""");
        int answer = new Scanner(System.in).nextInt();
        switch (answer) {
            case 1 -> findPagesWithPrefix();
            case 2 -> findLinkedPages();
            default -> System.out.println("Please enter a valid option");
        }
    }

    private static void findPagesWithPrefix() {
        Scanner scanner = new Scanner(System.in);
        Neo4jHelper neo4j = Neo4jHelper.getInstance();

        System.out.print("Enter a prefix to search for: ");
        String titlePrefix = scanner.next();

        Query query = new Query("""
                MATCH (page:Page)
                WHERE page.urlTitle STARTS WITH $titlePrefix
                RETURN page.title as title
                LIMIT 10
                """,
                parameters("titlePrefix", titlePrefix)
        );

        List<Map<String, Object>> results = neo4j.queryForMultipleNodes(query);

        System.out.println("We found the following pages:");
        for (Map<String, Object> result : results) {
            System.out.println(result.get("title"));
        }
    }

    private static void findLinkedPages() {
        Scanner scanner = new Scanner(System.in);
        Neo4jHelper neo4j = Neo4jHelper.getInstance();

        System.out.print("Input the title of the Wikipedia page as found in the url you wish to search for links: ");
        String urlTitle = scanner.next();


        Query findPageQuery = new Query("""
                MATCH (page:Page {urlTitle: $urlTitle})
                RETURN page
                """,
                parameters("urlTitle", urlTitle)
        );
        Optional<Map<String, Object>> page = neo4j.queryForSingleNode(findPageQuery);
        if (page.isEmpty()) {
            System.out.println("Could not find page with urlTitle: " + urlTitle);
            return;
        }

        Query findLinkedPagesQuery = new Query("""
                MATCH (page:Page {urlTitle: $urlTitle})-[:LINKS_TO]->(toPage:Page)
                RETURN toPage.title as title
                """,
                parameters("urlTitle", urlTitle)
        );

        List<Map<String, Object>> results = neo4j.queryForMultipleNodes(findLinkedPagesQuery);

        System.out.println("We found the following pages:");
        for (Map<String, Object> result : results) {
            System.out.println(result.get("title"));
        }
    }
}
