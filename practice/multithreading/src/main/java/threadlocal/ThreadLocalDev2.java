package threadlocal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class ThreadLocalDev2 {

  public static void main(String[] args) {

    val userDataService = new UserDataService();
    val thread1 = new Thread(() -> {
      userDataService.setUser(1).printUser();
    });
    val thread2 = new Thread(() -> {
      userDataService.setUser(2).printUser();
    });
    thread1.start();
    thread2.start();

    try {
      thread1.join();
      thread2.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new AssertionError(e);
    }
//    log.info(Thread.currentThread().getName());
  }
}

@Slf4j
class UserDataService {

  private ThreadLocal<UserData> localUserData = new ThreadLocal<>();
  private final Map<Integer, UserData> USER_MAP = new ConcurrentHashMap<>() {
    {
      put(1, new UserData(1, "Name1"));
      put(2, new UserData(2, "Name2"));
    }
  };

  public UserDataService setUser(int id) {
    localUserData.set(USER_MAP.get(id));
    return this;
  }

  public void printUser() {
    log.info("Value {}", localUserData.get());
  }
}

record UserData(Integer id, String name) {

}