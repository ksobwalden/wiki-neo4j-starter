package ch.ksobwalden.console;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Query;
import org.neo4j.driver.types.MapAccessor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Neo4jHelper {
    private static Neo4jHelper instance;
    public final Driver driver;

    private Neo4jHelper() {
        this.driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.none());
    }

    public static Neo4jHelper getInstance() {
        if (instance == null) {
            instance = new Neo4jHelper();
        }
        return instance;
    }

    public Optional<Map<String, Object>> queryForSingleNode(Query query) {
        try (var session = driver.session()) {
            return session.executeWrite(tx -> {
                var result = tx.run(query);
                if (result.hasNext()) {
                    return Optional.of(result.single().asMap());
                } else {
                    return Optional.empty();
                }
            });
        }
    }

    public List<Map<String, Object>> queryForMultipleNodes(Query query) {
        try (var session = driver.session()) {
            return session.executeWrite(tx -> {
                var result = tx.run(query);
                return result.stream().map(MapAccessor::asMap).collect(Collectors.toList());
            });
        }
    }
}
