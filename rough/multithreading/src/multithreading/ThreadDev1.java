package multithreading;

import java.util.concurrent.TimeUnit;

public class ThreadDev1 {

  private static boolean doneWorking = false;

  public static void main(String[] args) {
    Thread demoThread = new Thread(() -> {
//      getTop20FibNumber();
      throwRuntimeException();
      doneWorking = true;
    });

    try {
      demoThread.start();
      TimeUnit.MILLISECONDS.sleep(500);
      System.out.println();
      if (doneWorking) {
        System.out.println("Thread demoThread has finished the work!");
      } else {
        System.out.println("Thread demoThread didn't finish the work!");
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static void getTop20FibNumber() {
    for (int v = 1; v <= 20; v++) {
      System.out.printf(fib(v) + ", ");
    }
  }

  private static int fib(int n) {
    if (n == 1 || n == 2) {
      return 1;
    }
    return fib(n - 1) + fib(n - 2);
  }

  private static void throwRuntimeException(){
    throw new RuntimeException("Will not be caught by main thread.");
  }
}
