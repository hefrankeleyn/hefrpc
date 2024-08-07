package cn.hefrankeleyn.hefrpc.core.governance;

import java.util.Arrays;

/**
 * 创建一个环
 * @Date 2024/4/3
 * @Author lifei
 */
public class RingBuffer {

    // 环的大小
    private int size;
    // 用数组表示环
    private int[] ring;
    public RingBuffer(int size) {
        this.ring = new int[size];
        this.size = size;
    }

    public int sum() {
        return Arrays.stream(ring).sum();
    }

    public void reset() {
        Arrays.fill(ring, 0);
    }

    public void reset(int index, int step) {
        int fromIndex  = index%size;
        int toIndex  = (index+step)%size;
        if (fromIndex<toIndex) {
            Arrays.fill(ring, fromIndex, toIndex, 0);
        } else if (fromIndex > toIndex){
            Arrays.fill(ring, fromIndex, size, 0);
            Arrays.fill(ring, 0, toIndex, 0);
        }
    }

    public void inc(int index, int val) {
        ring[index%size] += val;
    }
}
