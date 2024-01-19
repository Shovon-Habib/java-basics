package clone_object_dev;

public class Main {

  public static void main(String[] args) throws Exception {
    var demo = new Demo();
    var demo1 = demo.clone();

    System.out.println(demo);
    System.out.println(demo1);
    System.out.println("================================");
    demo.value = "Outer Class value1";
    System.out.println(demo);
    System.out.println(demo1);

    System.out.println();

    var demo3 = new Demo();
    var demo4 = demo3;
    System.out.println(demo3);
    System.out.println(demo4);
    System.out.println("================================");
    demo3.value = "new value";
    System.out.println(demo3);
    System.out.println(demo4);
  }
}

class Demo implements Cloneable{
  public String value = "Outer Class value";
  InnerDemo innerDemo = new InnerDemo();

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  private static class InnerDemo {
    public String value = "Inner Class value";

    @Override
    public String toString() {
      return "InnerDemo{" +
             "value='" + value + '\'' +
             '}';
    }
  }

  @Override
  public String toString() {
    return "Demo{" +
           "value='" + value + '\'' +
           ", innerDemo=" + innerDemo +
           '}';
  }
}
