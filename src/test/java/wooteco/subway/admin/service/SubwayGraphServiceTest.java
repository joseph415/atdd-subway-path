package wooteco.subway.admin.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.Test;

import wooteco.subway.admin.domain.CustomEdge;

class SubwayGraphServiceTest {
    @Test
    public void getDijkstraShortestPath() {
        WeightedMultigraph<String, CustomEdge> graph = new WeightedMultigraph<>(CustomEdge.class);

        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 2);
        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);

        DijkstraShortestPath<String, CustomEdge> dijkstraShortestPath
            = new DijkstraShortestPath<>(graph);
        List<String> shortestPath
            = dijkstraShortestPath.getPath("v3", "v1").getVertexList();

        assertThat(shortestPath.size()).isEqualTo(3);
    }
}