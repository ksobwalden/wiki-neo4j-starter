package ch.ksobwalden.webview;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import org.neo4j.driver.Query;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.neo4j.driver.Values.parameters;

public class MainController {

    @FXML
    TextField searchPageTitle;
    @FXML
    WebView webView;

    @FXML
    void initialize() {
        loadSelectedPage();
        searchPageTitle.textProperty().bind(webView.getEngine().locationProperty());
    }

    @FXML
    void clickOnRandomLink() {
        Neo4jHelper neo4j = Neo4jHelper.getInstance();

        String urlTitle = getLastPathSegment(webView.getEngine().getLocation());

        System.out.println("Searching links for urlTitle: " + urlTitle);

        Query findLinkedPagesQuery = new Query("""
                MATCH (page:Page {urlTitle: $urlTitle})-[:LINKS_TO]->(toPage:Page)
                RETURN toPage.urlTitle as urlTitle
                """,
                parameters("urlTitle", urlTitle)
        );

        List<Map<String, Object>> results = neo4j.queryForMultipleNodes(findLinkedPagesQuery);
        Map<String, Object> randomLink = results.get(new Random().nextInt(results.size()));
        String link = (String) randomLink.get("urlTitle");

        System.out.println("Clicking on link: " + link);

        webView
                .getEngine()
                .executeScript(
                        "document.querySelector('#bodyContent a[href^=\"/wiki/%s\"]').click();".formatted(link)
                );

    }

    @FXML
    void loadSelectedPage() {
        String searchTitle = searchPageTitle.getText();
        webView.getEngine().load(searchTitle);
    }

    private String getLastPathSegment(String path) {
        String[] segments = path.split("/");
        return segments[segments.length - 1];
    }
}
