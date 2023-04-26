module ch.ksobwalden.starterjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.neo4j.driver;
    requires jdk.xml.dom;

    exports ch.ksobwalden.webview;
    opens ch.ksobwalden.webview to javafx.fxml;
}
