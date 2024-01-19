package problems;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
class Philosopher implements Runnable {

  private final Object leftFork;
  private final Object rightFork;
  private final ThreadLocal<Boolean> done = ThreadLocal.withInitial(() -> false);

  public Philosopher(Object leftFork, Object rightFork) {
    this.leftFork = leftFork;
    this.rightFork = rightFork;
  }

  @Override
  public void run() {
    try {
      while (Boolean.FALSE.equals(done.get())) {
        perform("Thinking...");
        synchronized (leftFork) {
          perform("Picked up left fork.");
          synchronized (rightFork) {
            perform("Picked up right fork.");
            perform("Eating...");
            perform("Put down right fork.");
          }
          perform("Put down left fork.");
          this.done.set(true);
        }
      }
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new AssertionError(ex);
    } finally {
      this.done.remove();
    }
  }

  private void perform(String action) throws InterruptedException {
    log.info("{}: {} -> {}", Thread.currentThread().getName(), LocalDateTime.now(), action);
    TimeUnit.MILLISECONDS.sleep(5);
  }
}

@Slf4j
public class DiningPhilosophers {

  public static void main(String[] args) {

    val philosophers = new Philosopher[5];
    val forks = Stream.generate(Object::new).limit(5).toArray();

    for (int i = 0; i < philosophers.length; i++) {
      val leftFork = forks[i];
      val rightFork = forks[(i + 1) % forks.length];

      if (i == philosophers.length - 1) {
        philosophers[i] = new Philosopher(leftFork, rightFork);
      } else {
        philosophers[i] = new Philosopher(rightFork, leftFork);
      }

      val thread = new Thread(philosophers[i], "Philosopher " + (i + 1));
      thread.start();
    }
  }
}
