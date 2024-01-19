package rough;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

enum DAY {
  MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

@Slf4j
final class Dairy {

  private static final ThreadLocal<DAY> today = ThreadLocal.withInitial(() -> DAY.MONDAY);

  public static DAY currentDay() {
    return today.get();
  }

  public static void setToday(DAY day) {
    today.set(day);
  }

  public void executeTask(BiFunction<Double, Double, Double> task, Double num1, Double num2) {
    try {
      log.info("Today: {}", currentDay());
      val result = task.apply(num1, num2);
      log.info("Result: {} and then: {}", result, task.andThen(r -> r * 2));
      TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException iex) {
      Thread.currentThread().interrupt();
      throw new AssertionError(iex);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}

@Slf4j
final class CustomExecutor extends ThreadPoolExecutor {

  public CustomExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
      BlockingQueue<Runnable> workQueue) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
  }

  @Override
  protected void beforeExecute(Thread t, Runnable r) {
    if (t == null || r == null) {
      throw new NullPointerException();
    }
    log.info("Before execution: {}", Dairy.currentDay());
  }

  @Override
  protected void afterExecute(Runnable r, Throwable t) {
    if (r == null || t != null) {
      throw new RuntimeException();
    }
    log.info("After execution: {}", Dairy.currentDay());
  }
}

@Slf4j
final class DairyPool {

  private final int THREAD_COUNT = 2;
  private final Executor executor;
  private final Dairy dairy;

  public DairyPool() {
    this.executor = new CustomExecutor(
        THREAD_COUNT, THREAD_COUNT,
        5, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(10));
    this.dairy = new Dairy();
  }

  public void execute1() {
    executor.execute(() -> {
      Dairy.setToday(DAY.FRIDAY);
      dairy.executeTask(Double::sum, 5.0, 5.0);
    });
  }

  public void execute2() {
    executor.execute(() -> dairy.executeTask((t, u) -> t * u, 5.0, 5.0));
  }

  public void status() {
    if (executor instanceof CustomExecutor cex) {
      log.info("Total tasks: {}", cex.getTaskCount());
      log.info("Active threads: {}", cex.getActiveCount());
      log.info("Pool size: {}", cex.getPoolSize());
    }
  }

  public boolean isActive() {
    if (executor instanceof CustomExecutor cex) {
      return cex.getActiveCount() != 0;
    }
    return false;
  }

  public void shutdown() {
    if (executor instanceof CustomExecutor cex) {
      cex.shutdown();
    }
  }

}

@Slf4j
public class Rough {

  public static void main(String[] args) {
    DairyPool dairyPool = new DairyPool();
    dairyPool.execute1();
    dairyPool.execute2();
    dairyPool.execute2();
    log.info("-------------------------------------------");
    dairyPool.status();
    dairyPool.shutdown();
  }
}
