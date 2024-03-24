package cn.hefrankeleyn.hefrpc.core.registry;

import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;

import java.util.List;

/**
 * @Date 2024/3/21
 * @Author lifei
 */
public class Event {
    List<InstanceMeta> data;

    public Event(){}

    public Event(List<InstanceMeta> data) {
        this.data = data;
    }

    public List<InstanceMeta> getData() {
        return data;
    }

    public void setData(List<InstanceMeta> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Event{" +
                "data=" + data +
                '}';
    }
}
