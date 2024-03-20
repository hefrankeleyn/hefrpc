package cn.hefrankeleyn.hefrpc.core.api;

import java.util.List;

/**
 * @Date 2024/3/20
 * @Author lifei
 */
public interface Router<T> {

    List<T> route(List<T> urls);

    Router DEFAULT = (urls)->urls;
}
