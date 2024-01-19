package threadlocal;

import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class ThreadLocalDev1 {

  public static void main(String[] args) {
    SharedObject sharedObject = new SharedObject();

    val thread1 = new Thread(() -> {
      SharedObject.setRandomNumber(sharedObject);
    });
    val thread2 = new Thread(() -> {
      SharedObject.setRandomNumber(sharedObject);
    });
    thread1.start();
    thread2.start();

    try {
      thread1.join();
      thread2.join();
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new AssertionError(ex);
    }

    log.info("Thread: " + Thread.currentThread().getName());
    log.info("Value: " + sharedObject.getNumber());
  }
}

@Slf4j
class SharedObject {

  //  private ThreadLocal<Integer> number = new ThreadLocal<>();
  private ThreadLocal<Integer> number = ThreadLocal.withInitial(() -> 0);

  public Integer getNumber() {
    return number.get();
  }

  private void setNumber(Integer number) {
    this.number.set(number);
  }

  public static void setRandomNumber(final SharedObject sharedObject) {
    sharedObject.setNumber((int) (Math.random() * 100));
    try {
      TimeUnit.SECONDS.sleep(1);
      log.info("Thread: {}, Value: {}", Thread.currentThread().getName(), sharedObject.getNumber());
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new AssertionError(ex);
    } finally {
      sharedObject.number.remove();
      log.info("After removing. Thread: {}, Value: {}", Thread.currentThread().getName(),
          sharedObject.getNumber());
    }
  }
}
