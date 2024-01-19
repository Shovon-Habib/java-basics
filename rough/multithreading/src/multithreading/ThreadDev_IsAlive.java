package multithreading;

public class ThreadDev_IsAlive {

  public static void main(String[] args) {
    Thread demoThread = new Thread(() -> {
      getTop20FibNumber();
    });
    demoThread.start();
    // alternative to Thread.Sleep()
    // isAlive() will return true from `Runnable` to `Terminated` state
    while (demoThread.isAlive()) {
    }
    System.out.println("\n");
    System.out.println("Thread `demoThread` status: " + demoThread.getState());
    System.out.println("Thread `demoThread` has terminated!");
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
}

