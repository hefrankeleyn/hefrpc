package cn.hefrankeleyn.hefrpc.core.registry.hef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Date 2024/5/26
 * @Author lifei
 */
public class HefHealthChecker {

    private static final Logger log = LoggerFactory.getLogger(HefHealthChecker.class);

    private ScheduledExecutorService consumerExecutor;
    private ScheduledExecutorService providerExecutor;

    public void start() {
        log.debug("====> [HefRegistry]: start health check");
        consumerExecutor = Executors.newSingleThreadScheduledExecutor();
        providerExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    public void stop() {
        log.debug("====> [HefRegistry]: stop health check");
        gracefulShutdown(consumerExecutor);
        gracefulShutdown(providerExecutor);
    }

    public void providerHealthCheck(Runnable command){
        providerExecutor.scheduleWithFixedDelay(()->{
           try{
               command.run();
           }catch (Exception e){
               log.error(e.getMessage(), e);
           }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public void consumerHealthCheck(Runnable command) {
        consumerExecutor.scheduleWithFixedDelay(()->{
            try {
                command.run();
            }catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 优雅的关闭
     * @param executor
     */
    private void gracefulShutdown(ScheduledExecutorService executor) {
        executor.shutdown();
        try {
            executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
            if (!executor.isTerminated()) {
                executor.shutdownNow();
            }
        }catch (Exception e) {
            // ignore
        }
    }

}
