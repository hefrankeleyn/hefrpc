package cn.hefrankeleyn.hefrpc.core.registry;

import java.util.List;

/**
 * @Date 2024/3/21
 * @Author lifei
 */
public class Event {
    List<String> nodes;

    public Event(){}

    public Event(List<String> nodes) {
        this.nodes = nodes;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }


    @Override
    public String toString() {
        return "Event{" +
                "nodes=" + nodes +
                '}';
    }
}
