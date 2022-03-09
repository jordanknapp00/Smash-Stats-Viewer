package viewer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LookupWindow {
	
	private JFrame frame;
	private JPanel topPanel;
	private JPanel midPanel;
	
	private JTextField toLookUp;
	private JButton lookupButton;
	
	private JComboBox<String> sortOptions;
	private JButton sortButton;
	
	private int selectedOption;
	
	private StatsWindow parent;
	
	public LookupWindow(StatsWindow parent) {
		this.parent = parent;
		
		frame = new JFrame("Smash Stats Viewer");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(325, 175);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		
		topPanel = new JPanel();
		topPanel.setBorder(BorderFactory.createTitledBorder("Lookup"));
		toLookUp = new JTextField(20);
		toLookUp.addActionListener(new LookupButtonActionListener());
		lookupButton = new JButton("Look up");
		lookupButton.addActionListener(new LookupButtonActionListener());
		topPanel.add(toLookUp);
		topPanel.add(lookupButton);
		
		midPanel = new JPanel();
		midPanel.setBorder(BorderFactory.createTitledBorder("Sort by"));
		String[] options = {"Fighters' overall win rate", "Players' overall win rate", 
				"P1 win rate", "P2 win rate", "P3 win rate", "P4 win rate", 
				"P5 win rate", "P6 win rate", "P7 win rate", "P8 win rate", 
				"Total battles"};
		sortOptions = new JComboBox<String>(options);
		sortOptions.setPreferredSize(new Dimension(200, 19));
		sortOptions.addActionListener(new SortOptionsActionListener());
		sortButton = new JButton("Sort");
		sortButton.addActionListener(new SortButtonActionListener());
		midPanel.add(sortOptions);
		midPanel.add(sortButton);
		
		frame.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = .33;
		gc.anchor = GridBagConstraints.CENTER;
		gc.fill = GridBagConstraints.BOTH;
		frame.add(topPanel, gc);
		gc.gridy = 1;
		frame.add(midPanel, gc);
		
		selectedOption = 0;
		
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Icon.png")));
		
		frame.addWindowListener(new LookupWindowListener());
		
		frame.setVisible(true);
	}
	
	private class SortButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String toOutput = "";
			ComparableArray[] data = new ComparableArray[parent.currentStats.size()];
			switch(selectedOption) {
				case 0:
					toOutput += "Sorted by fighters' overall win rate:\n";
					data = getTotalStats(3);
					break;
				case 1:
					toOutput += "Players' overall win rate:\n";
					data = getPlayerWinrateStats();
					for(int at = 0; at < 8; at++) {
						toOutput += data[at] + "\n";
					}
					
					parent.statsOutput.setText(toOutput);
					return;
				case 2:
					toOutput += "Sorted by Player 1's win rate:\n";
					data = getPlayerStats(0);
					break;
				case 3:
					toOutput += "Sorted by Player 2's win rate:\n";
					data = getPlayerStats(1);
					break;
				case 4:
					toOutput += "Sorted by Player 3's win rate:\n";
					data = getPlayerStats(2);
					break;
				case 5:
					toOutput += "Sorted by Player 4's win rate:\n";
					data = getPlayerStats(3);
					break;
				case 6:
					toOutput += "Sorted by Player 5's win rate:\n";
					data = getPlayerStats(4);
					break;
				case 7:
					toOutput += "Sorted by Player 6's win rate:\n";
					data = getPlayerStats(5);
					break;
				case 8:
					toOutput += "Sorted by Player 7's win rate:\n";
					data = getPlayerStats(6);
					break;
				case 9:
					toOutput += "Sorted by Player 8's win rate:\n";
					data = getPlayerStats(7);
					break;
				case 10:
					toOutput += "Sorted by total battles:\n";
					data = getTotalStats(2);
					Arrays.sort(data);
					data = reverse(data);
					int at = 1;
					for(ComparableArray arrAt: data) {
						toOutput += at + ". " + arrAt.getName() + " - " + arrAt.getBattles() + " battles\n";
						at++;
					}
					
					parent.statsOutput.setText(toOutput);
					return;
				default:
					toOutput += "Error, unrecognized sort ID.\n";
					return;
			}
			
			Arrays.sort(data);
			data = reverse(data);
			int at = 1;
			for(ComparableArray arrAt: data) {
				toOutput += at + ". " + arrAt + "\n";
				at++;
			}
			
			parent.statsOutput.setText(toOutput);
		}
		
		private ComparableArray[] getPlayerWinrateStats() {
			ComparableArray[] data = new ComparableArray[8];
			for(int indexAt = 0; indexAt < 8; indexAt++) {
				double totalWins = 0;
				double totalBattles = 0;
				for(String fighter: parent.currentStats.keySet()) {
					double[] fighterStats = parent.currentStats.get(fighter);
					totalWins += fighterStats[2 * indexAt];
					totalBattles += fighterStats[2 * indexAt + 1];
				}
				data[indexAt] = new ComparableArray("P" + (indexAt + 1) + " W%", totalWins, totalBattles, 3);
			}
			
			return data;
		}
		
		private ComparableArray[] getTotalStats(int column) {
			ComparableArray[] data = new ComparableArray[parent.currentStats.size()];
			int indexAt = 0;
			for(String fighter: parent.currentStats.keySet()) {
				double totalWins = 0;
				double totalBattles = 0;
				double[] fighterStats = parent.currentStats.get(fighter);
				for(int at = 0; at < 8; at++) {
					totalWins += fighterStats[2 * at];
					totalBattles += fighterStats[2 * at + 1];
				}
				data[indexAt] = new ComparableArray(fighter, totalWins, totalBattles, column);
				indexAt++;
			}
			
			return data;
		}
		
		private ComparableArray[] getPlayerStats(int player) {
			ComparableArray[] data = new ComparableArray[parent.currentStats.size()];
			int at = 0;
			for(String fighter: parent.currentStats.keySet()) {
				double[] fighterStats = parent.currentStats.get(fighter);
				data[at] = new ComparableArray(fighter, fighterStats[2 * player], fighterStats[2 * player + 1], 3);
				at++;
			}
			
			return data;
		}
		
		private ComparableArray[] reverse(ComparableArray[] start) {
			ComparableArray[] retArr = new ComparableArray[start.length];
			int j = start.length;
			for(int at = 0; at < start.length; at++) {
				retArr[j - 1] = start[at];
				j--;
			}
			
			return retArr;
		}
	}
	
	private class SortOptionsActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			selectedOption = sortOptions.getSelectedIndex();
		}		
	}
	
	private class LookupButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String toOutput = "";
			String lookup = toLookUp.getText();
			if(parent.currentStats.containsKey(lookup)) {
				toOutput += "Stats for " + lookup + ":\n";
				double[] fighterStats = parent.currentStats.get(lookup);
				double totalWins = 0;
				double totalFights = 0;
				for(int at = 0; at < 8; at++) {
					totalWins += fighterStats[2 * at];
					totalFights += fighterStats[2 * at + 1];
					toOutput += "Player " + (at + 1) + ": " + StatsWindow.printDouble((fighterStats[2 * at] / fighterStats[2 * at + 1]) * 100) + "% (" + (int)fighterStats[2 * at] + "/" + (int)fighterStats[2 * at + 1] + ")\n";
				}
				toOutput += "Overall: " + StatsWindow.printDouble((totalWins / totalFights) * 100) + "% (" + (int)totalWins + "/" + (int)totalFights + ")\n";
			}
			else if(lookup != null) {
				toOutput += "Fighter " + lookup + " not found!\n";
			}
			
			parent.statsOutput.setText(toOutput);
		}
	}
	
	private class LookupWindowListener implements WindowListener {

		public void windowOpened(WindowEvent e) {
			parent.openedLookup = true;
		}

		public void windowClosing(WindowEvent e) {

		}

		public void windowClosed(WindowEvent e) {
			parent.openedLookup = false;
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

}