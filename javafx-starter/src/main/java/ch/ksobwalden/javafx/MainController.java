package ch.ksobwalden.javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.neo4j.driver.Query;

import java.util.List;
import java.util.Map;

import static org.neo4j.driver.Values.parameters;

public class MainController {
    private final ObservableList<String> pages = FXCollections.observableArrayList();

    @FXML
    TextField searchPageTitle;
    @FXML
    ListView<String> foundPageTitles;

    @FXML
    public void initialize() {
        foundPageTitles.setItems(pages);
    }

    @FXML
    protected void onKeyPressed() {
        Neo4jHelper neo4j = Neo4jHelper.getInstance();

        String searchTitle = searchPageTitle.getText();
        Query query = new Query("""
                MATCH (page:Page)
                WHERE page.urlTitle STARTS WITH $text
                RETURN page.title as title
                LIMIT 50
                """, parameters("text", searchTitle));

        List<Map<String, Object>> results = neo4j.queryForMultipleNodes(query);

        pages.clear();
        for (Map<String, Object> result : results) {
            pages.add((String) result.get("title"));
        }
    }


}
