module ch.ksobwalden.starterjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.neo4j.driver;

    exports ch.ksobwalden.javafx;
    opens ch.ksobwalden.javafx to javafx.fxml;
}
