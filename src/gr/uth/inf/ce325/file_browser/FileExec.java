package gr.uth.inf.ce325.file_browser;

import java.awt.*;
import java.io.*;

public class FileExec extends Thread {
  private File file;

  public FileExec(File file) {
    this.file = file;
  }

  public void run() {
    try {
      Desktop dt = Desktop.getDesktop();
      dt.open(file);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
