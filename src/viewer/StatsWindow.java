package viewer;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class StatsWindow {
	
	private JFrame frame;
	private JPanel upperPanel;
	private JPanel lowerPanel;
	
	private JButton loadButton;
	private JButton lookupButton;
	
	public boolean openedLookup;
	
	private boolean openedFile;
	
	//public variables that other windows will need to access
	public HashMap<String, double[]> currentStats;
	public File currentFile;
	
	public JTextArea statsOutput;
	
	public StatsWindow() {
		frame = new JFrame("Smash Stats Viewer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 435);
		frame.setResizable(false);
		
		frame.setLocationRelativeTo(null);
		
		frame.addWindowListener(new StatsWindowListener());
		
		statsOutput = new JTextArea();
		statsOutput.setEditable(false);
		statsOutput.setFont(statsOutput.getFont().deriveFont(18f));
		
		//attempt to set look and feel, catching any errors
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			System.err.println(e);
		} catch (InstantiationException e) {
			System.err.println(e);
		} catch (IllegalAccessException e) {
			System.err.println(e);
		} catch (UnsupportedLookAndFeelException e) {
			System.err.println(e);
		}
		
		upperPanel = new JPanel();
		upperPanel.setLayout(new BorderLayout());
		upperPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
		upperPanel.add(statsOutput, BorderLayout.CENTER);
		
		//attempt to set up the load button correctly
		loadButton = new JButton("Load");
		try {
			Image loadImage = ImageIO.read(getClass().getResource("/img/Open.png"));
			loadButton.setIcon(new ImageIcon(loadImage));
		} catch (IOException e) {
			System.err.println(e);
		}
		
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(openedFile) {
					if(JOptionPane.showConfirmDialog(frame, "You already have a file "
							+ "loaded.\nLoad another one?", "Smash Stats Viewer",
							JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
						return;
					}
					else {
						saveFile();
						currentStats.clear();
					}
				}
				
				JFileChooser fileChooser = new JFileChooser(".");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Stats files (*.sel)", "sel");
				fileChooser.setFileFilter(filter);
				int willLoad = fileChooser.showOpenDialog(null);
				
				if (willLoad == JFileChooser.APPROVE_OPTION) {
					currentFile = fileChooser.getSelectedFile();
					loadFile();
				}
			}
		});
		
		lookupButton = new JButton("Look up stats");
		lookupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!openedFile) {
					JOptionPane.showMessageDialog(frame, "You must load a file first.",
							"Smash Stats Viewer", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				if(!openedLookup) {
					new LookupWindow(StatsWindow.this);
				}
			}
		});
		
		lowerPanel = new JPanel();
		lowerPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weightx = .2;
		lowerPanel.add(loadButton, gc);
		gc.gridx = 1;
		gc.weightx = .8;
		lowerPanel.add(lookupButton, gc);
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = .98;
		gc.fill = GridBagConstraints.BOTH;
		gc.anchor = GridBagConstraints.CENTER;
		frame.setLayout(new GridBagLayout());
		JScrollPane scrollPane = new JScrollPane(upperPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		frame.add(scrollPane, gc);
		gc.gridy = 1;
		gc.weighty = .02;
		frame.add(lowerPanel, gc);
		
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Icon.png")));
		
		DropTarget fileDropTarget = new DropTarget() {
			private static final long serialVersionUID = 1L;

			public synchronized void drop(DropTargetDropEvent evt) {
				try {
					if(openedFile) {
						if(JOptionPane.showConfirmDialog(frame, "You already have a file "
								+ "loaded.\nLoad another one?", "Smash Stats Viewer",
								JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
							evt.rejectDrop();
						}
						else {
							saveFile();
							currentStats.clear();
						}
					}
					
					if(evt.getTransferable().isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						evt.acceptDrop(DnDConstants.ACTION_COPY);
						@SuppressWarnings("unchecked")
						List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
						//only the first file will be loaded
						currentFile = droppedFiles.get(0);
						loadFile();
					}
					else {
						evt.rejectDrop();
					}
				} catch(Exception ex) {
					statsOutput.setText("Exception in drag/drop file system:\n" + ex);
				}
			}
		};
		
		statsOutput.setDropTarget(fileDropTarget);
		
		frame.setVisible(true);
	}
	
	
	
	/**
	 * This method is responsible for actually loading a file. The file should
	 * contain a <code>HashMap</code> object which can be loaded using an
	 * <code>ObjectInputStream</code>. If this is not the case, an exception is
	 * thrown.
	 * 
	 * If the file is successfully loaded, the tier list is scanned to see if
	 * there are any fighters present who are not currently in the stats system.
	 * If this is the case, they will be added to the system.
	 * 
	 * @throws IOException				Thrown if the file is not found, or if
	 * 									any other error happens while opening
	 * 									the file.
	 * @throws ClassNotFoundException	Thrown if the loaded file does not
	 * 									contain a <code>HashMap</code> object.
	 */
	@SuppressWarnings("unchecked")
	private void loadFile() {
		try {
			FileInputStream fis = new FileInputStream(currentFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			currentStats = (HashMap<String, double[]>) ois.readObject();
			ois.close();
			fis.close();
		} catch (FileNotFoundException e1) {
			System.err.println(e1);
		} catch (IOException e1) {
			System.err.println(e1);
		} catch (ClassNotFoundException e1) {
			System.err.println(e1);
		}
		
		statsOutput.setText("Loaded file " + currentFile.getName());
		frame.setTitle(currentFile.getName());
		openedFile = true;
	}
	
	private void saveFile() {
		try {
			FileOutputStream fos = new FileOutputStream(currentFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(currentStats);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e1) {
			System.err.println(e1);
		} catch (IOException e1) {
			System.err.println(e1);
		}
	}
	
	private class StatsWindowListener implements WindowListener {

		public void windowOpened(WindowEvent e) {

		}

		public void windowClosing(WindowEvent e) {
			
		}

		public void windowClosed(WindowEvent e) {
			if(openedFile) {
				saveFile();
			}
		}

		public void windowIconified(WindowEvent e) {

		}

		public void windowDeiconified(WindowEvent e) {

		}

		public void windowActivated(WindowEvent e) {

		}

		public void windowDeactivated(WindowEvent e) {

		}
	}
	
	public static String printDouble(double num) {
		if(num >= 0) {
			return new BigDecimal(String.valueOf(num)).setScale(2, RoundingMode.FLOOR).toString();
		}
		else if(num < 0) {
			return new BigDecimal(String.valueOf(num)).setScale(2, RoundingMode.CEILING).toString();
		}
		else {
			return "NaN";
		}
	}

}