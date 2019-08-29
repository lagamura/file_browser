package gr.uth.inf.ce325.file_browser;

import javax.swing.tree.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.swing.event.*;
import java.util.EventObject;
import javax.swing.tree.TreePath;
import java.awt.event.*;

public class FileLabel extends JLabel implements Comparable<FileLabel> {
	
	private File file;
	
	public FileLabel(String text, Icon icon, int horizontalAlignment, File file) {
		super(text,icon,horizontalAlignment);
		this.file = file;
	}
	
	public int compareTo(FileLabel label) {
		if(this.getFile().isDirectory() && !label.getFile().isDirectory()) {
			return -1;
		} else if(!this.getFile().isDirectory() && label.getFile().isDirectory()) {
			return 1;
		} else {
				return this.getFile().getName().compareTo(label.getFile().getName());
		}
		
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
}