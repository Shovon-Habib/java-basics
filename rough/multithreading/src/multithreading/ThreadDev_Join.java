package multithreading;

public class ThreadDev_Join {

  public static void main(String[] args) {
    Thread fibThread = new Thread(() -> {
      getTop20FibNumber();
    });
    fibThread.start();
    try {
      fibThread.join();
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
    System.out.println("Thread finThread: " + fibThread.getState());
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
