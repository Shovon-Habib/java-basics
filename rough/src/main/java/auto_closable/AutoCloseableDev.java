package auto_closable;

// https://www.geeksforgeeks.org/closeable-interface-in-java/


/*
*
* https://itecnote.com/tecnote/java-what-does-idempotent-method-mean-and-what-are-the-side-effects-in-case-of-calling-close-method-of-java-lang-autocloseable/
* */
class Demo implements AutoCloseable {
  private int value = 0;

//  https://stackoverflow.com/questions/32425976/how-is-in-java-the-idempotence-of-the-close-method-of-the-closeable-interface
  // AutoClosable.close is not idempotent. Multiple call may have side effect
  // Closable.close is idempotent. Can be called number of times.

//  JVM will call close() automatically and close the resource
  @Override
  public void close() throws Exception {
    // Close the resource as appropriate
    System.out.println("Demo class's close method executed");
  }

  public void manipulateResource() {
    System.out.println("Previous value " + value);
    this.value = 100;
    System.out.println("New value: " + value);
  }
}

public class AutoCloseableDev {

  public static void main(String[] args) {
    try (var demo = new Demo()) {
      demo.manipulateResource();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
