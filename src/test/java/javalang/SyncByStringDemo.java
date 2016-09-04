package javalang;

import com.xz.ajiaedu.common.concurrent.LockFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * (description)
 * created at 16/09/02
 *
 * @author yiding_he
 */
public class SyncByStringDemo {

    private static long start = System.currentTimeMillis();

    public static void main(String[] args) {

        // 基于 key 的并发控制：
        // 例如多个线程同时进行查询，如果有两个线程查询条件一样，那么需要对它们进行同步，
        // 也就是只允许其中一个线程查询，另一个线程直接使用缓存的查询结果。
        // 首先要从查询条件提取出缓存 key，相同的查询条件得到相同的 key，
        // 然后对该 key 进行同步，这时需要先获得对应的锁。
        final Object lock = LockFactory.getLock("__key__");

        // 然后就可以对其进行同步了：
        synchronized (lock) {
            // ....
        }

        //////////////////////////////////////////////////////////////

        // 下面是一个例子：
        String[] keys = {"1", "1", "2", "2", "3", "3"};
        Map<String, Object> cache = new ConcurrentHashMap<>();

        // 六个线程同时查询，其中 1、2 和 3 是同时并发查询的，而两个 1（或 2、3）查询是同步先后执行的，后者直接使用前者的查询结果。
        for (final String key : keys) {
            new Thread(() -> {

                Object value = null;
                if (cache.containsKey(key)) {
                    value = cache.get(key);

                } else {
                    Object _lock = LockFactory.getLock(key);

                    synchronized (_lock) {
                        System.out.println(prefix() + "lock " + key);

                        if (cache.containsKey(key)) {
                            value = cache.get(key);

                        } else {
                            sleep(3000);
                            value = "v" + key;
                            cache.put(key, value);
                        }

                        System.out.println(prefix() + "release " + key);
                    }
                }

                System.out.println(prefix() + "value is " + value);
            }).start();
        }
    }

    private static String prefix() {
        return String.format("%4d", (System.currentTimeMillis() - start)) + " |T" +
                Thread.currentThread().getId() + ": ";
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
