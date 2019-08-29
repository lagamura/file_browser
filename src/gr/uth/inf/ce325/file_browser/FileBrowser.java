package gr.uth.inf.ce325.file_browser;

import javax.swing.tree.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.swing.event.*;
import java.util.EventObject;
import javax.swing.tree.TreePath;
import java.awt.event.*;
import java.util.*;

public class FileBrowser extends JFrame {
	private static final int WIDTH = 1400;
	private static final int HEIGHT = 800;
	private JTree tree;
	private JPanel contentsPanel;
	private MyMouseListener mouseListener = new MyMouseListener();
	private FileLabel lastSingleClicked;
	private JPopupMenu popupMenu;
	private JPopupMenu popupMenuSelected;
	private DefaultTreeModel model;
	private AmenuItemListener menuItemListener = new AmenuItemListener();
	private boolean searchClicked;
	
	public FileBrowser() {
		super("CE325 File Browser");
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		createPopup();
		createMenu();
		createSearch();
		createRightPanel();
		createTree();
		createSplitPane();
	}

	public void search(String name, File file) {
		searchClicked = true;
		for(File tempFile : file.listFiles()) {
			if(!tempFile.isDirectory()) { 
				if(tempFile.getName().contains(name)) {
					createLabel(tempFile);
				}
			} else {
				if(tempFile.getName().contains(name)) {
					createLabel(tempFile);
				}
				search(name, tempFile);
			}
		}
	}

	public void createSearch() {
		JPanel pan = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton button = new JButton("Search");
		JTextField textField = new JTextField(20);
		ActionListener buttonListener = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(ae.getActionCommand().equals("Search")) {
					if(!textField.getText().equals("")) {
						contentsPanel.removeAll();
						contentsPanel.revalidate();
						contentsPanel.repaint();
						MyTreeNode tnode = (MyTreeNode)tree.getLastSelectedPathComponent();
						FileNode fnode = (FileNode)tnode.getUserObject();
						search(textField.getText(),fnode.getFile());
					}
				}
			}
		};
		button.addActionListener(buttonListener);
		textField.setFont(new Font("Serif", Font.BOLD, 14));
		textField.setPreferredSize(new Dimension(0,25));
		pan.add(button);
		pan.add(textField);
		this.add(pan,BorderLayout.NORTH);
	}
	
	public void createPopup() {
		popupMenu = new JPopupMenu();
		popupMenuSelected = new JPopupMenu();
		JMenuItem textItem = new JMenuItem("New Text Document");
		JMenuItem dirItem = new JMenuItem("New Directory");
		JMenuItem renameItem = new JMenuItem("Rename");
		JMenuItem delItem = new JMenuItem("Delete");
		textItem.addActionListener(menuItemListener);
		dirItem.addActionListener(menuItemListener);
		renameItem.addActionListener(menuItemListener);
		delItem.addActionListener(menuItemListener);
		popupMenu.add(textItem);
		popupMenu.add(dirItem);
		popupMenuSelected.add(renameItem);
		popupMenuSelected.add(delItem);
	}
	
	public void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu createMenu = new JMenu("Create");
		JMenuItem textItem = new JMenuItem("New Text Document");
		JMenuItem dirItem = new JMenuItem("New Directory");
		JMenuItem renameItem = new JMenuItem("Rename");
		JMenuItem delItem = new JMenuItem("Delete");
		textItem.addActionListener(menuItemListener);
		dirItem.addActionListener(menuItemListener);
		renameItem.addActionListener(menuItemListener);
		delItem.addActionListener(menuItemListener);
		fileMenu.setMnemonic(KeyEvent.VK_F);
		createMenu.setMnemonic(KeyEvent.VK_C);
		textItem.setMnemonic(KeyEvent.VK_T);
		dirItem.setMnemonic(KeyEvent.VK_I);
		renameItem.setMnemonic(KeyEvent.VK_R);
		delItem.setMnemonic(KeyEvent.VK_D);
		createMenu.add(textItem);
		createMenu.add(dirItem);
		fileMenu.add(createMenu);
		fileMenu.add(renameItem);
		fileMenu.add(delItem);
		menuBar.add(fileMenu);
		this.setJMenuBar(menuBar);
	}
	
	public void createRightPanel() {
		contentsPanel = new JPanel();
		contentsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		contentsPanel.setPreferredSize(new Dimension(1000,8000));
		contentsPanel.addMouseListener(mouseListener);
	}
	
	public void createTree() {
		MyTreeNode root = new MyTreeNode(new FileNode(new File("/")));
		tree = new JTree(root);
		tree.setExpandsSelectedPaths(true);
		root.expandNode(false,2);
		model = (DefaultTreeModel)tree.getModel();
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setOpenIcon(new ImageIcon("icons/folder_small.png"));
		renderer.setClosedIcon(renderer.getOpenIcon());
		renderer.setLeafIcon(renderer.getOpenIcon());
		renderer.setTextSelectionColor(Color.BLUE);
		setInitialPath(root);
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(renderer);
		
		TreeSelectionListener tListener = new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				MyTreeNode node = (MyTreeNode)tree.getLastSelectedPathComponent();
				if(node == null) {
					return;
				}
				node.expandNode(false,1);
				FileNode fnode = (FileNode)node.getUserObject();
				contentsPanel.removeAll();
				contentsPanel.revalidate();
				contentsPanel.repaint();
				showContents(fnode);
			}
		};
		tree.addTreeSelectionListener(tListener);
	}
	
	public void createSplitPane() {
		JScrollPane leftScroll = new JScrollPane(tree);
		leftScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		leftScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane rightScroll = new JScrollPane(contentsPanel);
		rightScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		rightScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,leftScroll,rightScroll);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(300);
		this.add(splitPane,BorderLayout.CENTER);
	}
	
	public void setInitialPath(MyTreeNode root) {
		String userDir = System.getProperty("user.home");
		userDir = userDir.replace("\\","/");
		userDir = userDir.substring(3);
		String[] dirNames = userDir.split("/");
		TreePath tPath = new TreePath(root);
		MyTreeNode tNode = root;
		for(String dirName : dirNames) {
			MyTreeNode child = matchChildNode(tNode, dirName);
			if(child == null) {
				return;
			}
			tPath = tPath.pathByAddingChild(child);
			tNode = child;
		}
		tree.setSelectionPath(tPath);
		tree.scrollPathToVisible(tPath);
		FileNode nodeUserHome = new FileNode(new File(createFilePath(tPath)));
		showContents(nodeUserHome);
	}
	
	public MyTreeNode matchChildNode(MyTreeNode node, String childName) {
		for(int i=0; i<node.getChildCount(); i++ ){ 
			MyTreeNode child = (MyTreeNode)node.getChildAt(i);
			FileNode fnode = (FileNode)child.getUserObject();
			String childNodeName = fnode.getFileName();
			if(childNodeName.equals(childName)) {
				return child;
			}
		}
		return null;
	}
	
	
	public void showContents(FileNode fnode) {
		File file = fnode.getFile();
		File[] files = file.listFiles();
		ArrayList<File> list = new ArrayList<>();
		if (files == null) {
			return;
		}
		for(File tempFile : files) {
			if(!tempFile.isHidden() && !(tempFile.getName().charAt(0) == '.')) {
				list.add(tempFile);
			}
		}
		Collections.sort(list, new Comparator<File>() {
			public int compare(File f1, File f2) {
				if(f1.isDirectory() && !f2.isDirectory()) {
					return -1;
				} else if(!f1.isDirectory() && f2.isDirectory()) {
					return 1;
				} else {
					return f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase());
				}
			}
		});
		for(File tempFile : list) {
			createLabel(tempFile);
		}
	}
	
	public void createLabel(File tempFile) {
		ImageIcon icon = createIcon(tempFile);
		FileLabel label = new FileLabel("<html>" + tempFile.getName() + "</html>",icon,JLabel.CENTER,tempFile);
		label.addMouseListener(mouseListener);
		label.setVerticalTextPosition(JLabel.BOTTOM);
		label.setHorizontalTextPosition(JLabel.CENTER);
		label.setPreferredSize(new Dimension(120,120));
		label.revalidate();
		contentsPanel.add(label);
	}
	
	public String createFilePath(TreePath treePath) {
		StringBuilder sb = new StringBuilder();
		Object[] nodes = treePath.getPath();
		for(int i=1;i<nodes.length;i++) {
			sb.append(File.separator);
			sb.append(nodes[i].toString());
		} 
		return sb.toString();
	}
	
	public ImageIcon createIcon(File tempFile) {
		if(tempFile.getName().endsWith(".txt")) {
			return new ImageIcon("icons/text.png");
		}
		if(tempFile.getName().endsWith(".pdf")) {
			return new ImageIcon("icons/pdf.png");
		}
		if(tempFile.getName().endsWith(".jpg") || tempFile.getName().endsWith(".bmp") || tempFile.getName().endsWith(".png")) {
			return new ImageIcon("icons/image.png");
		}
		if(tempFile.getName().endsWith(".mp3")) { 
			return new ImageIcon("icons/mp3.png");
		}
		if(tempFile.getName().endsWith(".html")) {
			return new ImageIcon("icons/html.png");
		}
		if(tempFile.getName().endsWith(".zip") || tempFile.getName().endsWith(".tgz") || tempFile.getName().endsWith(".tar") || tempFile.getName().endsWith(".rar")) {
			return new ImageIcon("icons/zip.png");
		}
		if(tempFile.getName().endsWith(".xml")) {
			return new ImageIcon("icons/xml.png");
		}
		if(tempFile.getName().endsWith(".xlsx")) {
			return new ImageIcon("icons/xlsx.png");
		}
		if(tempFile.getName().endsWith(".docx")) {
			return new ImageIcon("icons/word.png");
		}
		if(tempFile.isDirectory()) {
			return new ImageIcon("icons/folder.png");
		}
		return new ImageIcon("icons/unknown.png");
	}
	
	public void deleteFileOrDir(File file) {
		if(file.isDirectory()) {
			for(File temp : file.listFiles()) {
				deleteFileOrDir(temp);
			} 
		}
		file.delete();
	}
	
	private class MyMouseListener implements MouseListener {
		
		public void mouseClicked(MouseEvent me) {
			if(me.getSource() instanceof JLabel) {
				if(me.getButton() == MouseEvent.BUTTON1) {
					int clickCount = me.getClickCount();
					if(clickCount == 1) {
						oneClickEvent(me);
					} else if(clickCount ==2) {
						twoClickEvent(me);
					}
				}
			} else {
				if(lastSingleClicked != null) {
				lastSingleClicked.setBorder(BorderFactory.createEmptyBorder());
				}
			}
		}
		
		public void mousePressed(MouseEvent me) {
			showPopup(me);
		}
		
		public void mouseReleased(MouseEvent me) {
			showPopup(me);
		}
		
		public void mouseExited(MouseEvent me) {}
		
		public void mouseEntered(MouseEvent me) {}
		
		private void showPopup(MouseEvent me) {
			if(me.isPopupTrigger()) {
				if(me.getSource() instanceof JLabel) {
					popupMenuSelected.show(me.getComponent(),me.getX(),me.getY());
				} else {
					popupMenu.show(me.getComponent(),me.getX(),me.getY());
				}
			}
		}
	}
	
	public void oneClickEvent(MouseEvent me) {
		if(lastSingleClicked != null) {
			lastSingleClicked.setBorder(BorderFactory.createEmptyBorder());
		}
		FileLabel label = (FileLabel)me.getSource();
		label.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		lastSingleClicked = label;
	}
	
	public void twoClickEvent(MouseEvent me) {
		FileLabel label = (FileLabel)me.getSource();
		File file = label.getFile();
		if (file.isDirectory()) {
			if(searchClicked) {
				MyTreeNode activeNode = (MyTreeNode)tree.getLastSelectedPathComponent();
				String userDir = file.getAbsolutePath();
				userDir = userDir.replace("\\","/");
				userDir = userDir.substring(3);
				TreeNode[] nodes = activeNode.getPath();
				TreePath newPath = new TreePath(nodes);
				String[] dirNames = userDir.split("/");
				int i=0;
				for(String dirName : dirNames) {
					if(!dirName.equals(((FileNode)activeNode.getUserObject()).getFileName())) {
						i++;
					} else {
						break;
					}
				}
				for(int j=i; j<dirNames.length;j++) {
					for(int k=0;k<activeNode.getChildCount();k++) {
						if(((FileNode)((MyTreeNode)activeNode.getChildAt(k)).getUserObject()).getFileName().equals(dirNames[j])) {
							newPath = newPath.pathByAddingChild((MyTreeNode)activeNode.getChildAt(k));
							activeNode = (MyTreeNode)activeNode.getChildAt(k);
							k = activeNode.getChildCount();
						}
					}
				}
				tree.setSelectionPath(newPath);
				searchClicked = false;
			} else {
				MyTreeNode activeNode = (MyTreeNode)tree.getLastSelectedPathComponent();
				for(int i=0; i<activeNode.getChildCount(); i++) {
					if(((FileNode)((MyTreeNode)activeNode.getChildAt(i)).getUserObject()).getFileName().equals(file.getName())) {
						TreeNode[] nodes = ((MyTreeNode)(activeNode.getChildAt(i))).getPath();
						TreePath newPath = new TreePath(nodes);
						tree.setSelectionPath(newPath);
					}
				}
			}
		} else {
			FileExec fe = new FileExec(file);
			fe.start();
		}
	}
	
	private class AmenuItemListener implements ActionListener {
		
		public void actionPerformed(ActionEvent ae) {
			if(ae.getActionCommand().equals("New Text Document")) {
				String input = (String)JOptionPane.showInputDialog(null,"Enter the name of the new Text Document:","Create Text",JOptionPane.PLAIN_MESSAGE, null, null,"New Text.txt");
				if(input!= null) {
					MyTreeNode node = (MyTreeNode)tree.getLastSelectedPathComponent();
					TreeNode[] tNodes = node.getPath();
					TreePath tPath = new TreePath(tNodes);
					File file = new File(createFilePath(tPath) + "/" + input);
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
					createLabel(file);
					contentsPanel.revalidate();
					contentsPanel.repaint();
				}
			} 
			else if(ae.getActionCommand().equals("New Directory")) {
				String input = (String)JOptionPane.showInputDialog(null,"Enter new Directory Name:","Create Directory",JOptionPane.PLAIN_MESSAGE, null, null,"New Directory");
				if(input!= null) {
					MyTreeNode node = (MyTreeNode)tree.getLastSelectedPathComponent();
					TreeNode[] tNodes = node.getPath();
					TreePath tPath = new TreePath(tNodes);
					File file = new File(createFilePath(tPath) + "/" + input);
					file.mkdir();
					MyTreeNode temp = new MyTreeNode(new FileNode(file));
					node.add(temp);
					model.reload(node);
					createLabel(file);
					contentsPanel.revalidate();
					contentsPanel.repaint();
				}
			}
			else if(ae.getActionCommand().equals("Rename")) {
				String tempName = null;
				int tempIndex = 0;
				String input = (String)JOptionPane.showInputDialog(null,"Enter new Name:","Rename",JOptionPane.PLAIN_MESSAGE, null, null,lastSingleClicked.getFile().getName());
				if(input!= null) {
					MyTreeNode node = (MyTreeNode)tree.getLastSelectedPathComponent();
					MyTreeNode tempNode =null;
					TreeNode[] tNodes = node.getPath();
					TreePath tPath = new TreePath(tNodes);
					File oldFile = lastSingleClicked.getFile();
					File newFile = new File(createFilePath(tPath) + "/" + input);
					for(int i=0; i<node.getChildCount();i++) {
						if(((FileNode)((MyTreeNode)node.getChildAt(i)).getUserObject()).getFileName().equals(oldFile.getName())) {
							tempName = oldFile.getName();   // Keep details of the file before the rename to restore in case of error
							tempIndex = i;
							((FileNode)((MyTreeNode)node.getChildAt(i)).getUserObject()).setFileName(input);
							MyTreeNode tobeChanged = (MyTreeNode)node.getChildAt(i);
							tobeChanged.setUserObject(new FileNode(newFile));
							model.reload(node);
						}
					}
						
					if(oldFile.renameTo(newFile)) {
						lastSingleClicked.setText(input);
						lastSingleClicked.setFile(newFile);
						contentsPanel.revalidate();
						contentsPanel.repaint();
					} else {
						JOptionPane.showMessageDialog(null,"Rename unsuccessful.","Rename",JOptionPane.ERROR_MESSAGE);
						((FileNode)((MyTreeNode)node.getChildAt(tempIndex)).getUserObject()).setFileName(tempName);    // in case of error restore the old file details 
					}
				}
			}
			else if(ae.getActionCommand().equals("Delete")) {
				String fileOrDir = null;
				if(lastSingleClicked.getFile().isDirectory()) {
					fileOrDir = "directory";
				} else {
					fileOrDir = "file";
				}
				int response = JOptionPane.showConfirmDialog(null,"Are you sure you want to delete " + fileOrDir + " " + lastSingleClicked.getFile().getName() + " ?","Confirm Deletion",JOptionPane.YES_NO_OPTION);
				if(response == JOptionPane.OK_OPTION) {
					File file = lastSingleClicked.getFile();
					MyTreeNode node = (MyTreeNode)tree.getLastSelectedPathComponent();
					for(int i=0; i<node.getChildCount();i++) {
						if(((FileNode)((MyTreeNode)node.getChildAt(i)).getUserObject()).getFileName().equals(file.getName())) {
							MyTreeNode temp = (MyTreeNode)node.getChildAt(i);
							temp.removeAllChildren();
							model.removeNodeFromParent(temp);
						}
					}
					deleteFileOrDir(file);
					contentsPanel.remove(lastSingleClicked);
					contentsPanel.revalidate();
					contentsPanel.repaint();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		FileBrowser fb = new FileBrowser();
		fb.setVisible(true);
	}
}