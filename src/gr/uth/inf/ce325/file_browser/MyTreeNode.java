package gr.uth.inf.ce325.file_browser;

import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class MyTreeNode extends DefaultMutableTreeNode implements Comparable<MyTreeNode> {

  private boolean expanded;

  public MyTreeNode() {
    super();
  }

  public MyTreeNode(Object userObject) {
    super(userObject);
  }

  public int compareTo(MyTreeNode node) {
    FileNode fNode = (FileNode) node.getUserObject();
    FileNode myfNode = (FileNode) this.getUserObject();
    return myfNode.getFileName().compareTo(fNode.getFileName());
  }

  public void expandNode(boolean showHidden, int depth) {
    if (expanded == false) {
      expanded = true;
      FileNode node = (FileNode) this.getUserObject();
      File dir = node.getFile();
      File[] files = dir.listFiles();
      if (files == null) {
        return;
      }
      Set<MyTreeNode> set = new TreeSet<>();
      for (File temp : files) {
        if (!temp.isDirectory() || (showHidden == false && temp.isHidden())) {
          continue;
        } else {
          MyTreeNode newNode = new MyTreeNode(new FileNode(temp));
          set.add(newNode);
        }
      }
      Iterator<MyTreeNode> it = set.iterator();
      while (it.hasNext()) {
        MyTreeNode node2 = it.next();
        this.add(node2);
        if (depth > 0) {
          node2.expandNode(showHidden, depth - 1);
        }
      }
    }
  }
}
