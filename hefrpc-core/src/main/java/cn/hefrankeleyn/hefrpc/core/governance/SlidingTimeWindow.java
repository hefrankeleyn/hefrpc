package cn.hefrankeleyn.hefrpc.core.governance;

import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 滑动时间窗口
 * @Date 2024/4/3
 * @Author lifei
 */
public class SlidingTimeWindow {

    private static final Logger log = LoggerFactory.getLogger(SlidingTimeWindow.class);

    // 默认滑动时间窗口的大小（秒）
    private static final int DEFAULT_WINDOW_SIZE = 30;

    private int size;

    private long start_ts = -1L;
    private long current_ts = -1L;
    private int current_mark = -1;
    private int sum;
    private RingBuffer ringBuffer;

    public SlidingTimeWindow() {
        this(DEFAULT_WINDOW_SIZE);
    }

    public SlidingTimeWindow(int size) {
        this.size = size;
        this.ringBuffer = new RingBuffer(size);
    }

    public void record(long tms) {
        log.info("===> window before: " + this);
        // 毫秒转秒
        long ts = tms/1000;
        log.info("===> record(s): " + ts);
        if (start_ts==-1) {
            initRing(ts);
        } else if (ts == current_ts) {
            // 当前时间发生多次
            this.ringBuffer.inc(current_mark, 1);
        } else if (ts > current_ts && ts < current_ts + size) {
            int offset = (int)(ts - current_ts);
            // 时间窗口内，再次发生故障
            this.ringBuffer.reset(current_mark+1, offset);
            this.ringBuffer.inc(current_mark+offset, 1);
            this.current_ts = ts;
            this.current_mark = (current_mark + offset)%size;
        } else {
            // 超出了时间窗口
            this.ringBuffer.reset();
            initRing(ts);
        }
        this.sum = this.ringBuffer.sum();
        log.info("===> window after: " + this);
    }

    private void initRing(long ts) {
        this.start_ts = ts;
        this.current_ts = ts;
        this.current_mark = 0;
        this.ringBuffer.inc(current_mark, 1);
    }

    public int getSum() {
        return sum;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(SlidingTimeWindow.class)
                .add("size", size)
                .add("start_ts", start_ts)
                .add("current_ts", current_ts)
                .add("current_mark", current_mark)
                .toString();
    }
}
