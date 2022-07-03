import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JList;

public class HomeScreen {

	private JFrame frame;
	private JTextField textFieldSearch;
	private Map<String, Slang> baseSlangs = new HashMap<String, Slang>();
	private static String FILE_PATH = "src/slang.txt";
	DefaultListModel<String> listMode = new DefaultListModel<String>();

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
		baseSlangs = readData(FILE_PATH);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 666, 525);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblWord = new JLabel("New label");
		lblWord.setBounds(34, 32, 367, 16);
		frame.getContentPane().add(lblWord);
		
		textFieldSearch = new JTextField();
		textFieldSearch.setBounds(32, 98, 249, 26);
		frame.getContentPane().add(textFieldSearch);
		textFieldSearch.setColumns(10);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setBounds(284, 98, 117, 29);
		frame.getContentPane().add(btnSearch);
		
		JList listHistories = new JList();
		listHistories.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		listHistories.setBounds(34, 60, 609, 26);
		frame.getContentPane().add(listHistories);
		
		JButton btnAdd = new JButton("New Word");
		btnAdd.setBounds(526, 27, 117, 29);
		frame.getContentPane().add(btnAdd);
		
		JButton btnReset = new JButton("Reset");
		btnReset.setBounds(413, 27, 117, 29);
		frame.getContentPane().add(btnReset);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.setEnabled(false);
		btnDelete.setBounds(575, 99, 68, 26);
		frame.getContentPane().add(btnDelete);
		
		JButton btnEdit = new JButton("Edit");
		btnEdit.setEnabled(false);
		btnEdit.setBounds(509, 98, 68, 29);
		frame.getContentPane().add(btnEdit);
		
		JList<String> listResult = new JList<String>(listMode);
		listResult.setVisibleRowCount(30);
		JScrollPane scrollPane = new JScrollPane(listResult);
		scrollPane.setBounds(34, 136, 609, 349);
		frame.getContentPane().add(scrollPane);
		
		setupViews(listResult, lblWord);
	}
	
	private void setupViews(JList<String> list, JLabel lblWord) {
		Object[] keys = baseSlangs.keySet().toArray(); 
		for (int i = 0; i < keys.length; i++) {
			Slang slang = baseSlangs.get(keys[i]);
			listMode.add(i, slang.toString());
		}
		
		int randomIndex = new Random().nextInt(keys.length);
		Slang randomSlang = baseSlangs.get(keys[randomIndex]);
		lblWord.setText(randomSlang.getSlag() + " means " + randomSlang.getMeaning() + ".");
	}
	
	private Map<String, Slang> readData(String filePath) {
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
}
