import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.logging.Handler;

import javax.swing.JFrame;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

public class HomeScreen {

	private JFrame frame;
	private JTextField textFieldSearch;
	private Map<String, Slang> baseSlangs = new HashMap<String, Slang>();
	
	private static String SLANGS_FILE_PATH = "src/slang.txt";
	private static String HISTORIES_FILE_PATH = "src/histories.txt";
	
	private DefaultListModel<String> listMode = new DefaultListModel<String>();
	private DefaultListModel<String> historiesMode = new DefaultListModel<String>();
	private JButton btnReset = new JButton("Reset");
	private JLabel lblHistories = new JLabel("");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HomeScreen window = new HomeScreen();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public HomeScreen() {
		setupData();
		initialize();
	}
	
	private void setupData() {
		baseSlangs = readSlangsData(SLANGS_FILE_PATH);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 666, 580);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblWord = new JLabel("New label");
		lblWord.setBounds(34, 32, 367, 16);
		frame.getContentPane().add(lblWord);
		
		textFieldSearch = new JTextField();
		textFieldSearch.setBounds(34, 131, 244, 36);
		frame.getContentPane().add(textFieldSearch);
		textFieldSearch.setColumns(10);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setBounds(278, 136, 117, 29);
		btnSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				startSearch();
			}
			
		});
		frame.getContentPane().add(btnSearch);
		
		lblHistories.setText(readHistoriesData());
		JScrollPane historiesScrollPane = new JScrollPane();
		historiesScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		historiesScrollPane.setBounds(34, 60, 609, 59);
		historiesScrollPane.setViewportView(lblHistories);
		frame.getContentPane().add(historiesScrollPane);
		
		historiesScrollPane.setViewportView(lblHistories);
		
		JButton btnAdd = new JButton("New Word");
		btnAdd.setBounds(526, 27, 117, 29);
		btnAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Hashtable<String, String> result = showCreateDialog(frame);
				String slag = result.get("slag");
				String mean = result.get("mean");
				if (!slag.isEmpty() && !mean.isEmpty()) {
					if (baseSlangs.containsKey(slag)) {
						showDialog(frame, "Warning", "This word is existed!");
					} else {
						Slang s = new Slang();
						s.setMeaning(mean);
						s.setSlag(slag);
						listMode.addElement(s.toString());
						
						writeData(SLANGS_FILE_PATH, slag + "`" + mean, true);
						showDialog(frame, "Message", "This word is saved!");
					}
				}
			}
			
		});
		frame.getContentPane().add(btnAdd);
		
		btnReset.setBounds(413, 27, 117, 29);
		btnReset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				resetResultList();
			}
			
		});
		frame.getContentPane().add(btnReset);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.setEnabled(false);
		btnDelete.setBounds(575, 137, 68, 26);
		frame.getContentPane().add(btnDelete);
		
		JButton btnEdit = new JButton("Edit");
		btnEdit.setEnabled(false);
		btnEdit.setBounds(508, 136, 68, 29);
		frame.getContentPane().add(btnEdit);
		
		JList<String> listResult = new JList<String>(listMode);
		listResult.setVisibleRowCount(30);
		listResult.addListSelectionListener((ListSelectionListener) new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					String selected = listResult.getSelectedValue();
	                if (selected != null) {
	                	btnEdit.setEnabled(true);
	                	btnDelete.setEnabled(true);
	                }
	            }
			}
			
		});
		JScrollPane scrollPane = new JScrollPane(listResult);
		scrollPane.setBounds(34, 188, 609, 349);
		frame.getContentPane().add(scrollPane);
		
		btnEdit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String selected = listResult.getSelectedValue().toString();
				String[] data = selected.split(": ");
            
            	if (data.length > 1) {
            		String oldSlag = data[0];
            		String oldMean = data[1];
            		Hashtable<String, String> result = showEditDialog(frame, oldSlag, oldMean);
            		String slag = result.get("slag");
     				String mean = result.get("mean");
     				boolean isUpdatableSlag = !baseSlangs.containsKey(slag) && !slag.contentEquals(oldSlag);
     				boolean isChanged = (!oldMean.contentEquals(mean) && oldSlag.contentEquals(slag)) || (!oldMean.contentEquals(mean) && isUpdatableSlag) || (oldMean.contentEquals(mean) && isUpdatableSlag);
     				
            		if (isChanged) {
            			int updateIndex = listMode.indexOf(selected);
            			listMode.set(updateIndex, slag + ": " + mean);
            			updateInFile(SLANGS_FILE_PATH, oldSlag + "`" + oldMean, slag + "`" + mean);
            			System.out.println("updated valude: " + selected + ", newer: " + slag + ": " + mean);
            		}
            	}
			}
			
		});
		
		setupViews(lblWord);
	}
	
	private void updateInFile(String filePath, String older, String newer) {
		System.out.println("updateInFile - filePath=" + filePath + ", older=" + older + ", newer=" + newer);
		try {
			Path path = Paths.get(filePath);
			Charset charset = StandardCharsets.UTF_8;

			String content = new String(Files.readAllBytes(path), charset);
			content = content.replace(older, newer);
			Files.write(path, content.getBytes(charset));
		  } catch (IOException e) {
		     //Simple exception handling, replace with what's necessary for your use case!
		     throw new RuntimeException("Generating file failed", e);
		  }
	}
	
	private Hashtable<String, String> showEditDialog(JFrame frame, String slagText, String meaningText) {
	    Hashtable<String, String> updateInformation = new Hashtable<String, String>();

	    JPanel panel = new JPanel(new BorderLayout(5, 5));

	    JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
	    label.add(new JLabel("Slag", SwingConstants.RIGHT));
	    label.add(new JLabel("Meaning", SwingConstants.RIGHT));
	    panel.add(label, BorderLayout.WEST);

	    JPanel controls = new JPanel(new GridLayout(0, 1, 10, 2));
	    JTextField slag = new JTextField();
	    slag.setText(slagText);
	    controls.add(slag);
	    JTextField mean = new JTextField();
	    mean.setText(meaningText);
	    controls.add(mean);
	    panel.add(controls, BorderLayout.CENTER);

	    JOptionPane.showMessageDialog(frame, panel, "Modifying Slangword", JOptionPane.PLAIN_MESSAGE);

	    updateInformation.put("slag", slag.getText());
	    updateInformation.put("mean", mean.getText());
	    return updateInformation;
	}
	
	private void startSearch() {
		if (!textFieldSearch.getText().isEmpty()) {
			String keyword = textFieldSearch.getText();
			Object[] keys = { keyword };
			setupResultList(keys, true);
			historiesMode.addElement(keyword);
			if (lblHistories.getText().isEmpty()) {
				lblHistories.setText(keyword);
			} else {
				lblHistories.setText(lblHistories.getText() + ", " + keyword);
			}
			writeData(HISTORIES_FILE_PATH, lblHistories.getText(), false);
		}
	}
	
	private void searchForMeaning(String keyword) {
		btnReset.setEnabled(false);
		listMode.removeAllElements();
		int index = 0;
		Object[] data = baseSlangs.values().toArray();
		for (int i = 0; i < data.length; i++) {
			Slang s = (Slang) data[i];
			if (s.getMeaning().contains(keyword)) {
				listMode.add(index++, data[i].toString());
			}
		}
	}
	
	private void resetResultList() {
		Object[] keys = baseSlangs.keySet().toArray(); 
		setupResultList(keys, false);
	}
	
	private void setupViews(JLabel lblWord) {
		Object[] keys = baseSlangs.keySet().toArray(); 
		setupResultList(keys, false);
		
		int randomIndex = new Random().nextInt(keys.length);
		Slang randomSlang = baseSlangs.get(keys[randomIndex]);
		lblWord.setText(randomSlang.getSlag() + " means " + randomSlang.getMeaning() + ".");
	}
	
	private void setupResultList(Object[] keys, boolean isFromSearch) {
		btnReset.setEnabled(false);
		listMode.removeAllElements();
		
		for (int i = 0; i < keys.length; i++) {
			Slang slang = baseSlangs.get(keys[i]);
			if (slang != null) {
				listMode.add(i, slang.toString());
			} else if (isFromSearch) {
				searchForMeaning(keys[i].toString());
			}
		}
		btnReset.setEnabled(true);
		/**
		 * Thread resetThread = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("resetThread slang size=" + baseSlangs.size() + ", keys.length=" + keys.length);
				
			} 
			
		});
		resetThread.start();
		 * */
	}
	
	private Map<String, Slang> readSlangsData(String filePath) {
		Map<String, Slang> slangs = new HashMap<String, Slang>();
		try {
			File f = new File(filePath);
			if(f.exists()) { 
				InputStream bin = new FileInputStream(filePath);
				BufferedReader reader = new BufferedReader(new InputStreamReader(bin, "utf8"));
				int lineIndex = 0;
				while (reader.ready()) {
					String line = reader.readLine();
					if (lineIndex <= 0) {
						lineIndex++;
					} else {
						String[] meaning = line.split("`");
						if(meaning.length > 1) {
							Slang slang = new Slang();
							slang.setSlag(meaning[0]);
							slang.setMeaning(meaning[1]);
							slangs.put(meaning[0], slang);
							
						}
					}
				}
				
				reader.close();
				bin.close();
			}
			
		} catch(Exception e ) {
			e.printStackTrace();
		}
		return slangs;
	}
	
	private Hashtable<String, String> showCreateDialog(JFrame frame) {
	    Hashtable<String, String> newSlang = new Hashtable<String, String>();

	    JPanel panel = new JPanel(new BorderLayout(5, 5));

	    JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
	    label.add(new JLabel("Slag", SwingConstants.RIGHT));
	    label.add(new JLabel("Meaning", SwingConstants.RIGHT));
	    panel.add(label, BorderLayout.WEST);

	    JPanel controls = new JPanel(new GridLayout(0, 1, 10, 2));
	    JTextField slag = new JTextField();
	    controls.add(slag);
	    JTextField mean = new JTextField();
	    controls.add(mean);
	    panel.add(controls, BorderLayout.CENTER);

	    JOptionPane.showMessageDialog(frame, panel, "New Slangword", JOptionPane.PLAIN_MESSAGE);

	    newSlang.put("slag", slag.getText());
	    newSlang.put("mean", mean.getText());
	    return newSlang;
	}
	
	private void showDialog(JFrame frame, String title, String content) {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
	    JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
	    label.add(new JLabel(content, SwingConstants.RIGHT));
	    panel.add(label, BorderLayout.WEST);
		JOptionPane.showMessageDialog(frame, panel, title, JOptionPane.PLAIN_MESSAGE);
	}
	
	private String readHistoriesData() {
		String result = "";
		try {
			File f = new File(HISTORIES_FILE_PATH);
			if(f.exists()) { 
				InputStream bin = new FileInputStream(HISTORIES_FILE_PATH);
				BufferedReader reader = new BufferedReader(new InputStreamReader(bin, "utf8"));
				int lineIndex = 0;
				while (reader.ready()) {
					String line = reader.readLine();
					if (!line.isEmpty()) result = line;
				}
				
				reader.close();
				bin.close();
			}
			
		} catch(Exception e ) {
			e.printStackTrace();
		}
		return result;
	}
	
	private void writeData(String filePath, String line, boolean isAppend) {
		try {
			//DataOutputStream ft = new DataOutputStream(new FileOutputStream(FILE_PATH));
			FileWriter fw = new FileWriter(filePath, isAppend);
			
			fw.write(line);
			fw.write("\n");
			
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
