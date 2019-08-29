package gr.uth.inf.ce325.file_browser;

import java.io.File;

public class FileNode {
	private File file;
	private String fileName;
	
	public FileNode(File file) {
		this.file = file;
		this.fileName = file.getName();	
	}
	
	public File getFile() {
		return file;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String toString() {
		if(getFileName().length()== 0) {
			return "/";
		} else {
			return fileName;
		}
	}
}