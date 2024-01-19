package multithreading;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

class HeartBeat extends Thread {

  private volatile boolean beating = true;
  private String[] dots = {
      ".",
      "..",
      "...",
      "...."};

  @Override
  public void run() {
    while (beating) {
      for (String dot : dots) {
        System.out.println(dot);
        if (!beating) {
          break;
        }
        try {
          TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
          ex.printStackTrace();
        }
      }
    }
  }

  public void shutdown() {
    this.beating = false;
  }
}

class FileDownloader extends Thread {

  private String url;
  private String fileName;

  public FileDownloader(String url, String fileName) {
    this.url = url;
    this.fileName = fileName;
  }

  @Override
  public void run() {
    try {
      System.out.println("Started to download: " + fileName);
      URL resourceUrl = new URL(url);
      URLConnection connection = resourceUrl.openConnection();
      try (InputStream inputStream = connection.getInputStream()) {
        File fileToSave = new File(fileName);
        Files.copy(inputStream, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException ex) {
      System.out.println("Failed to download file: " + fileName);
    }
  }
}

public class ThreadDev_Join1 {

  public static void main(String[] args) {

    HeartBeat heartBeat = new HeartBeat();
    FileDownloader downloader1 = new FileDownloader(
        "https://images.pexels.com/photos/335257/pexels-photo-335257.jpeg", "img1");
    FileDownloader downloader2 = new FileDownloader(
        "https://images.pexels.com/photos/341523/pexels-photo-341523.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
        "img2");

    downloader1.start();
    downloader2.start();
    heartBeat.start();

    try {
      downloader1.join();
      downloader2.join();
      heartBeat.shutdown();
      heartBeat.join();
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
    System.out.println("\nDownload complete.");
  }
}
