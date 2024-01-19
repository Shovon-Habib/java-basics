package threadlocal;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

enum DAY {
  MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
}

@Slf4j
final class Dairy {

  private static final ThreadLocal<DAY> today = ThreadLocal.withInitial(() -> DAY.MONDAY);

  public static DAY currentDay() {
    return today.get();
  }

  public static void setDay(DAY day) {
    today.set(day);
  }

  public void threadTask(BiFunction<Double, Double, Double> task, Double num1, Double num2) {
    try {
      log.info("Current Day: {}", currentDay());
      val result = task.apply(num1, num2);
      log.info("Result of task {}: {}", task, result);
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
final class CustomThreadPoolExecutor extends ThreadPoolExecutor {

  public CustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
      TimeUnit unit, BlockingQueue<Runnable> workQueue) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
  }

  @Override
  protected void beforeExecute(Thread t, Runnable r) {
    if (r == null || t == null) {
      throw new NullPointerException();
    }
    log.info("Before execution- current day {}", Dairy.currentDay());
  }

  @Override
  protected void afterExecute(Runnable r, Throwable t) {
    if (t != null || r == null) {
      throw new RuntimeException(t);
    }
    log.info("After execution- current day {}", Dairy.currentDay());
    Dairy.setDay(DAY.MONDAY);
  }

  @Override
  protected void terminated() {
    log.info("Thread:{}  state:{}", Thread.currentThread().getName(),
        Thread.currentThread().getState());
  }
}

@Slf4j
final class DairyPool {

  private final int threadCount = 2;
  private final Executor executor;
  private final Dairy dairy;

  public DairyPool() {
    executor = new CustomThreadPoolExecutor(threadCount, threadCount, 5, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(1));
    dairy = new Dairy();
  }

  public void doSomething1() {
    executor.execute(() -> {
      dairy.threadTask((Double::sum), 5.0, 5.0);
    });
  }

  public void doSomething2() {
    executor.execute(() -> {
      Dairy.setDay(DAY.FRIDAY);
      dairy.threadTask(((t, u) -> t * u), 5.0, 5.0);
    });
  }

  public void stats() {
    if (executor instanceof CustomThreadPoolExecutor cexe) {
      log.info("Active threads: {}", cexe.getActiveCount());
      log.info("Pool size: {}", cexe.getPoolSize());
    }
  }

  public void shutDown() {
    if (executor instanceof CustomThreadPoolExecutor cexe) {
      cexe.shutdown();
      log.info("After shutdown");
      stats();
    }
  }
}

public class ThreadPoolExecutor_ThreadLocal {

  public static void main(String[] args) {
    val dairyPool = new DairyPool();
    dairyPool.doSomething1();
    dairyPool.doSomething2();
    dairyPool.doSomething2();

    try {
      TimeUnit.SECONDS.sleep(10);
      dairyPool.stats();
      dairyPool.shutDown();
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }
}
