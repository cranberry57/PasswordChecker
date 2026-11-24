import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BatchChecker {

    public static class Entry {
        public final int index;
        public final String password;
        public final PasswordChecker.Result result;
        public final String threadName;
        public final long millis;

        Entry(int index, String password, PasswordChecker.Result result, String threadName, long millis) {
            this.index = index;
            this.password = password;
            this.result = result;
            this.threadName = threadName;
            this.millis = millis;
        }
    }

    private static Entry checkOne(int index, String password, int simulatedWorkMs) {
        long start = System.nanoTime();
        PasswordChecker.Result result = PasswordChecker.checkPassword(password);
        if (simulatedWorkMs > 0) {
            try {
                Thread.sleep(simulatedWorkMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        long millis = (System.nanoTime() - start) / 1_000_000;
        return new Entry(index, password, result, Thread.currentThread().getName(), millis);
    }

    public static List<Entry> checkParallel(List<String> passwords, int threads, int simulatedWorkMs)
            throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            List<Callable<Entry>> tasks = new ArrayList<>();
            for (int i = 0; i < passwords.size(); i++) {
                final int index = i;
                final String pw = passwords.get(i);
                tasks.add(() -> checkOne(index, pw, simulatedWorkMs));
            }
            List<Future<Entry>> futures = pool.invokeAll(tasks);
            List<Entry> results = new ArrayList<>();
            for (Future<Entry> f : futures) {
                results.add(f.get());
            }
            return results;
        } finally {
            pool.shutdown();
        }
    }

    public static List<Entry> checkSequential(List<String> passwords, int simulatedWorkMs) {
        List<Entry> results = new ArrayList<>();
        for (int i = 0; i < passwords.size(); i++) {
            results.add(checkOne(i, passwords.get(i), simulatedWorkMs));
        }
        return results;
    }
}
