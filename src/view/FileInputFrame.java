package view;

import static view.Constants.FRAME_HEIGHT;
import static view.Constants.FRAME_WIDTH;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * FileInputFrame class is responsible for taking user input and creating
 * simulation accordingly. This is the first frame that the user sees when the
 * program is run. It has 2 buttons Evolve and Simulate.
 * 
 */
@SuppressWarnings("serial")
public class FileInputFrame extends JFrame {

	/** ComboBox for file names. */
	@SuppressWarnings("rawtypes")
	private JComboBox fileNamesComboBox = new JComboBox();
	
	/** CheckBox for displaying labels. */
	private JCheckBox displayLabelCheckBox = new JCheckBox("Display Labels");
	
	/** Button to simulate a network. */
	private JButton simulateButton = new JButton("SIMULATE");
	
	/** Button to evolve a network. */
	private JButton evolveButton = new JButton("EVOLVE");
	
	/** Panel for holding all the components. */
	private JPanel fileInputPanel = new JPanel();

	/**
	 * Constructor: Gets called whenever an object of this class is initialized.
	 * Also sets the frame properties.
	 */
	public FileInputFrame() {
		initUI();
		this.setTitle("Load Input File");
		this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		this.add(fileInputPanel);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
	}

	/**
	 * Method to initialize the frame contents and components like the combo
	 * boxes, buttons etc.
	 */
	private void initUI() {
		fileInputPanel.setLayout(new GridBagLayout());

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(10, 5, 10, 5);
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		gridBagConstraints.gridwidth = 2;

		JLabel title = new JLabel("Welcome to ANNie");
		title.setFont(new Font("Serif", Font.BOLD, 36));

		fileInputPanel.add(title, gridBagConstraints);

		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.insets = new Insets(10, 5, 10, 5);

		fileInputPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		createFileDropdownArea(gridBagConstraints);
		createLabelSelectArea(gridBagConstraints);
		createButtons(gridBagConstraints);
	}

	/**
	 * Method to create the file list drop down. User selects the required file
	 * to simulate a network.
	 * 
	 * @param gridBagConstraints
	 *            gridbag parameters to specify GridBag layout for the
	 *            components.
	 */
	@SuppressWarnings("unchecked")
	private void createFileDropdownArea(GridBagConstraints gridBagConstraints) {
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		fileInputPanel.add(new JLabel("Choose network file to simulate :"),
				gridBagConstraints);

		String[] fileNames = getFileNames();

		for (String file : fileNames)
			fileNamesComboBox.addItem(file);

		if (fileNames.length > 0)
			fileNamesComboBox.setSelectedIndex(0);

		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		fileInputPanel.add(fileNamesComboBox, gridBagConstraints);
	}

	/**
	 * Method to create the display label check box. If checked the labels for
	 * various synapses and neurons are displayed.
	 * 
	 * @param gridBagConstraints
	 *            gridbag parameters to specify GridBag layout for the
	 *            components.
	 */
	private void createLabelSelectArea(GridBagConstraints gridBagConstraints) {
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		displayLabelCheckBox.setSelected(false);
		fileInputPanel.add(displayLabelCheckBox, gridBagConstraints);

	}

	/**
	 * Method to create the Evolve and Simulate buttons in the frame.
	 * 
	 * @param gridBagConstraints
	 *            gridbag parameters to specify GridBag layout for the
	 *            components.
	 */
	private void createButtons(GridBagConstraints gridBagConstraints) {
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		fileInputPanel.add(evolveButton, gridBagConstraints);

		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		fileInputPanel.add(simulateButton, gridBagConstraints);
	}

	/**
	 * Method to get all the file names from data folder and return the list to
	 * populate the drop down.
	 * 
	 * @return returns the list of file names.
	 */
	private String[] getFileNames() {
		File directory = new File(this.getClass().getClassLoader()
				.getResource("data").getFile());
		List<String> fileNames = new ArrayList<String>();

		for (File file : directory.listFiles())
			fileNames.add(file.getName());

		return fileNames.toArray(new String[fileNames.size()]);
	}

	/**
	 * 
	 * @return returns true if display label checkbox is selected, false
	 *         otherwise
	 */
	public boolean isLabelSelected() {
		return displayLabelCheckBox.isSelected();
	}

	/**
	 * 
	 * @return returns the selected file name from the drop down.
	 */
	public String getFileSelected() {
		return "data/" + fileNamesComboBox.getSelectedItem().toString();
	}

	/**
	 * Method to add simulate button action listener to frame.
	 * 
	 * @param simulateActionListener
	 *            object of the button listener
	 */
	public void addSimulateActionListener(ActionListener simulateActionListener) {
		simulateButton.addActionListener(simulateActionListener);
	}

	/**
	 * Method to add evolve button action listener to frame.
	 * 
	 * @param evolveActionListener
	 *            object of the button listener
	 */
	public void addEvolveActionListener(ActionListener evolveActionListener) {
		evolveButton.addActionListener(evolveActionListener);
	}

	// Open a popup that contains the error message passed
	public void displayErrorMessage(String errorMessage) {
		JOptionPane.showMessageDialog(this, errorMessage);
	}
}
