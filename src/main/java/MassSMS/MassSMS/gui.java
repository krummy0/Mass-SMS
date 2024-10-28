package MassSMS.MassSMS;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class gui {
    private JFrame frame;

    private List<Map<String, String>> leadsForSending;
    private List<Map<String, String>> stmpSettings;
    private List<String> apiKeys;
    private Map<String, String> hotWords;
    private List<String> personalizationKeywords;
    
    //For Sending and Testing
    private WordSmith wordSmith;
    private STMPRotator stmpRotator;
    private SMS sms;
    

    public gui() {
        apiKeys = new ArrayList<>();
        hotWords = new HashMap<>();
        personalizationKeywords = new ArrayList<>();
        stmpSettings = new ArrayList<>();
        leadsForSending = new ArrayList<>();

        setupFrame();
        setupMainButtons();
        frame.setVisible(true);
    }

    private void setupFrame() {
        frame = new JFrame("MassSMS");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());     
        frame.setResizable(false);
    }
    
    private void onSendButtonClicked(String from, String msg, boolean usaCanada) {
    	if (configureVars(usaCanada)) {
    		List<List<String>> sendList = sms.getAdresses();
    		for (int i = 0; i < sendList.size(); i++) {
    			//each number
    			for (String to : sendList.get(i)) {
    				String finalMsg = msg;
    				if (personalizationKeywords.isEmpty())
    					finalMsg = wordSmith.substitute(msg);
    				else
    					finalMsg = wordSmith.substitute(msg, sms.getPersonal(i));
    				stmpRotator.send(from, to, "", finalMsg);
    			}
    		}
    	}
    }
    
    private void onTestStmpClicked(String from, String msg) {
        JFrame frame = new JFrame("Alert");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(frame);
    	
    	if (configureVars(false)) {
	       JCheckBox checkBox = new JCheckBox("I agree");

	        // Create a panel to hold the checkbox
	        JPanel panel = new JPanel();
	        panel.setLayout(new BorderLayout());
	        panel.add(new JLabel("Phone Number:"), BorderLayout.NORTH);
	        panel.add(checkBox, BorderLayout.SOUTH);

	        // Show the input dialog
	        String input = JOptionPane.showInputDialog(panel, "Phone Number:", "Test SMTP", JOptionPane.PLAIN_MESSAGE);

	        // Check if the user provided input
	        if (input != null) {
	        	JOptionPane.showMessageDialog(frame, "Each server will text you its host. Sending now", "Alert", JOptionPane.INFORMATION_MESSAGE);
	        	SMS smsVar = new SMS(input, apiKeys.get(0), checkBox.isSelected());
	        	for (String to : smsVar.getAdresses().get(0))
	        		stmpRotator.test("Mass SMS", to);
	        	JOptionPane.showMessageDialog(frame, "Sent. Give texts a few minutes to be received.", "Alert", JOptionPane.INFORMATION_MESSAGE);
	        }
	        else {
	        	JOptionPane.showMessageDialog(frame, "Enter a phone number. Operation Canceled", "Error", JOptionPane.INFORMATION_MESSAGE);
	        }
    	}
    }

    private void onTestMsgClicked(String from, String msg) {
    	if (configureVars(false)) {
    		String test;
    		if (personalizationKeywords.size() == 0)
    			test = wordSmith.substitute(msg); //for msg
    		else
    			test = wordSmith.substitute(msg, leadsForSending.get(0)); //for msg
    		JOptionPane.showMessageDialog(frame, test, "Example Message", JOptionPane.INFORMATION_MESSAGE);
    	}
    }
    
    private boolean configureVars(boolean usaCanada) {
    	if (apiKeys.isEmpty() || stmpSettings.isEmpty() || leadsForSending.isEmpty())
    		return false;
    	
    	//setup stmp rotator
    	stmpRotator = new STMPRotator(stmpSettings);
    	//setup sms
    	sms = new SMS(leadsForSending, apiKeys, usaCanada);
    	//setup wordsmith
        wordSmith = new WordSmith(hotWords);
    	return true;
    }
    
    private void showHowToUseDialog() {
        String message = "How to Use the Program:\n\n" +
                         "1. Add STMP Servers (Test that they are inboxing before adding)\n" +
                         "2. Add API Keys to data247.com. It is used to find carrier\n" +
                         "3. Optionally add Personalization words before loading leads. These only apply if your leads contain information other than just numbers\n" +
                         "4. Make sure numbers are formated with international codes and are only didgits" +
                         "5. Add any hot words. These will be connected to a file of words and one will be randomly selected for each message\n" +
                         "6. When making your message include personal and hot words with {key}. Do not duplicate keys\n" +
                         "7. It is recomended for best delivery to make sure the sms gateways in your country are correct for major carriers";
        
        JOptionPane.showMessageDialog(frame, message, "How to Use", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setupMainButtons() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Vertical layout
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        // Title
        JLabel titleLabel = new JLabel("MassSMS", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(titleLabel, BorderLayout.NORTH);
        
        // Panel for "From" field
        JPanel fromPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcFrom = new GridBagConstraints();
        gbcFrom.gridx = 0;
        gbcFrom.gridy = 0;
        gbcFrom.anchor = GridBagConstraints.CENTER; // Center label
        JLabel fromLabel = new JLabel("From:");
        fromPanel.add(fromLabel, gbcFrom);

        gbcFrom.gridy = 1; // Move to next row for the text field
        JTextField fromField = new JTextField(20);
        fromPanel.add(fromField, gbcFrom);
        buttonPanel.add(fromPanel);

        // Panel for "Message" area
        JPanel messagePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcMessage = new GridBagConstraints();
        gbcMessage.gridx = 0;
        gbcMessage.gridy = 0;
        gbcMessage.anchor = GridBagConstraints.CENTER; // Center label
        JLabel messageLabel = new JLabel("Message:");
        messagePanel.add(messageLabel, gbcMessage);

        gbcMessage.gridy = 1; // Move to next row for the text area
        JTextArea messageArea = new JTextArea(5, 20);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messagePanel.add(messageScrollPane, gbcMessage);
        buttonPanel.add(messagePanel);

        // Centered checkbox for USA/Canada leads
        JPanel checkboxPanel = new JPanel();
        JCheckBox usaCanadaCheckBox = new JCheckBox("USA/Canada Leads Only");
        checkboxPanel.add(usaCanadaCheckBox);
        checkboxPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center checkbox
        buttonPanel.add(checkboxPanel);

        // Button panel
        JPanel buttonGroup = new JPanel(new GridBagLayout()); // Use GridBagLayout for button alignment
        GridBagConstraints gbcButtons = new GridBagConstraints();
        gbcButtons.gridx = 0;
        gbcButtons.gridy = 0;
        gbcButtons.anchor = GridBagConstraints.CENTER; // Center buttons
        gbcButtons.insets = new Insets(5, 5, 5, 5); // Add spacing between buttons

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> 
            onSendButtonClicked(fromField.getText(), messageArea.getText(), usaCanadaCheckBox.isSelected()));
        buttonGroup.add(sendButton, gbcButtons);

        gbcButtons.gridx = 1; // Move to next column
        JButton testsButton = new JButton("Tests");
        testsButton.addActionListener(e -> 
            showTestsScreen(fromField.getText(), messageArea.getText()));
        buttonGroup.add(testsButton, gbcButtons);

        gbcButtons.gridx = 2; // Move to next column
        JButton settingsButton = new JButton("Settings");
        settingsButton.addActionListener(e -> showSettingsScreen());
        buttonGroup.add(settingsButton, gbcButtons);

        gbcButtons.gridx = 3; // Move to next column
        JButton howToUseButton = new JButton("How to Use");
        howToUseButton.addActionListener(e -> showHowToUseDialog());
        buttonGroup.add(howToUseButton, gbcButtons);

        buttonPanel.add(buttonGroup);
        frame.add(buttonPanel, BorderLayout.CENTER);
    }
    
    private void showTestsScreen(String from, String msg) {
        frame.getContentPane().removeAll();
        frame.repaint();

        JLabel settingsLabel = new JLabel("Tests", JLabel.CENTER);
        settingsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(settingsLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton testMsgButton = new JButton("Test Message");
        testMsgButton.addActionListener(e -> onTestMsgClicked(from, msg));
        JButton testStmpButton = new JButton("Test STMP Servers");
        testStmpButton.addActionListener(e -> onTestStmpClicked(from, msg));
        
        buttonPanel.add(testMsgButton);
        buttonPanel.add(testStmpButton);

        frame.add(buttonPanel, BorderLayout.CENTER);

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            setupMainButtons(); // Re-setup the main buttons to return to the main screen
            frame.revalidate();
            frame.repaint();
        });

        frame.add(backButton, BorderLayout.SOUTH); // Add Back button at the bottom
        frame.revalidate();
        frame.repaint();
    }

    private void showSettingsScreen() {
        frame.getContentPane().removeAll();
        frame.repaint();

        JLabel settingsLabel = new JLabel("Settings", JLabel.CENTER);
        settingsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(settingsLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("API Keys", createApiKeysPanel());
        tabbedPane.addTab("Leads", createLeadsPanel());
        tabbedPane.addTab("SMTP Servers", createStmpPanel());
        tabbedPane.addTab("Hot Words", createHotWordsPanel());

        frame.add(tabbedPane, BorderLayout.CENTER);

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            setupMainButtons(); // Re-setup the main buttons to return to the main screen
            frame.revalidate();
            frame.repaint();
        });

        frame.add(backButton, BorderLayout.SOUTH); // Add Back button at the bottom
        frame.revalidate();
        frame.repaint();
    }
    
    private JPanel createHeader(String title, int count) {
        return createHeader(new String[] {title}, new int[] {count});
    }
    
    private JPanel createHeader(String[] title, int[] count) {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Left-aligned layout
        headerPanel.setBackground(Color.LIGHT_GRAY); // Set background color to light gray
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add some padding

        // Combined title and count label with bold font
        for (int i = 0; i < title.length && i < count.length; i++) {
	        JLabel headerLabel = new JLabel(title[i] + ": " + count[i]);
	        headerLabel.setFont(new Font("Arial", Font.BOLD, 12)); // Bold font
	        headerLabel.setForeground(Color.BLACK); // Set text color to black
	
	        headerPanel.add(headerLabel); // Add the header label to the panel
        }

        return headerPanel;
    }
    
    private void updateHeader(Container container, String title, int count) {
        updateHeader(container, new String[] {title}, new int[] {count});
    }
    private void updateHeader(Container container, String[] title, int[] count) {
        JPanel header = createHeader(title, count);
        container.add(header, BorderLayout.NORTH, 0);
        container.revalidate();
        container.repaint();
    }

    private JPanel createApiKeysPanel() {
        JPanel apiKeysPanel = new JPanel(new BorderLayout());
        DefaultListModel<String> apiKeysListModel = new DefaultListModel<>();
        JList<String> apiKeysList = new JList<>(apiKeysListModel);
        //load
        for (String key : apiKeys) {
        	apiKeysListModel.addElement(key);
        }
        apiKeysList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        apiKeysPanel.add(createHeader("API Keys",apiKeysListModel.getSize()), BorderLayout.NORTH);
        apiKeysPanel.add(new JScrollPane(apiKeysList), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton loadButton = createLoadApiButton(apiKeysPanel, apiKeysListModel);
        JButton addKeyButton = createAddApiButton(apiKeysPanel, apiKeysListModel);
        JButton removeKeyButton = createRemoveApiButton(apiKeysPanel, apiKeysListModel, apiKeysList);

        buttonPanel.add(loadButton);
        buttonPanel.add(addKeyButton);
        buttonPanel.add(removeKeyButton);
        apiKeysPanel.add(buttonPanel, BorderLayout.SOUTH);
        return apiKeysPanel;
    }

    private JPanel createLeadsPanel() {
        JPanel leadsPanel = new JPanel(new BorderLayout());
        DefaultListModel<String> leadsListModel = new DefaultListModel<>();
        JList<String> leadsList = new JList<>(leadsListModel);
        //load
        for (Map<String, String> map : leadsForSending) {
        	leadsListModel.addElement(map.get("number"));
        }
        leadsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leadsPanel.add(createHeader(new String[] {"Leads", "Personal Keywords"},
        		new int[] {leadsListModel.getSize(), personalizationKeywords.size()}),
        		BorderLayout.NORTH);
        leadsPanel.add(new JScrollPane(leadsList), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton loadLeadsButton = createLoadLeadsButton(leadsPanel, leadsListModel);
        JButton clearLeadsButton = new JButton("Clear");
        clearLeadsButton.addActionListener(e -> {
        	leadsListModel.clear();
        	updateHeader(leadsPanel, new String[] {"Leads", "Personal Keywords"},
            		new int[] {leadsListModel.getSize(), personalizationKeywords.size()});
        	});
        JButton personalizationButton = new JButton("Personalization");
        personalizationButton.addActionListener(e -> {
        	showPersonalizationScreen();
        	updateHeader(leadsPanel, new String[] {"Leads", "Personal Keywords"},
            		new int[] {leadsListModel.getSize(), personalizationKeywords.size()});
        });

        buttonPanel.add(loadLeadsButton);
        buttonPanel.add(clearLeadsButton);
        buttonPanel.add(personalizationButton);
        leadsPanel.add(buttonPanel, BorderLayout.SOUTH);
        return leadsPanel;
    }

    private JPanel createStmpPanel() {
        JPanel stmpPanel = new JPanel(new BorderLayout());
        DefaultListModel<String> stmpListModel = new DefaultListModel<>();
        JList<String> stmpList = new JList<>(stmpListModel);
        //load
        for (Map<String, String> map : stmpSettings) {
        	stmpListModel.addElement(map.get("host"));
        }
        stmpList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stmpPanel.add(createHeader("STMPs", stmpListModel.getSize()), BorderLayout.NORTH);
        stmpPanel.add(new JScrollPane(stmpList), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton loadStmpButton = createLoadStmpButton(stmpPanel, stmpListModel);
        JButton clearStmpButton = new JButton("Clear");
        clearStmpButton.addActionListener(e -> {
        	stmpListModel.clear();
        	stmpSettings.clear();
        	updateHeader(stmpPanel, "STMPs", stmpListModel.getSize());
        });

        buttonPanel.add(loadStmpButton);
        buttonPanel.add(clearStmpButton);
        stmpPanel.add(buttonPanel, BorderLayout.SOUTH);
        return stmpPanel;
    }

    private JPanel createHotWordsPanel() {
        JPanel hotWordsPanel = new JPanel(new BorderLayout());
        DefaultListModel<String> hotWordsListModel = new DefaultListModel<>();
        JList<String> hotWordsList = new JList<>(hotWordsListModel);
        //load
        for (String word : hotWords.keySet()) {
        	hotWordsListModel.addElement(word);
        }
        hotWordsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hotWordsPanel.add(createHeader("Hot Words", hotWordsListModel.getSize()), BorderLayout.NORTH);
        hotWordsPanel.add(new JScrollPane(hotWordsList), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addHotWordButton = new JButton("Add Hot Word");
        addHotWordButton.addActionListener(e -> {
            String hotWord = JOptionPane.showInputDialog(frame, "Enter Hot Word:");
            if (hotWord != null && !hotWord.trim().isEmpty()) {
                // Open file chooser to select a file
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(frame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    hotWords.put(hotWord, selectedFile.getAbsolutePath());
                    hotWordsListModel.addElement(hotWord);
                }
            }
            updateHeader(hotWordsPanel, "Hot Words", hotWordsListModel.getSize());
        });

        JButton removeHotWordButton = new JButton("Remove Hot Word");
        removeHotWordButton.addActionListener(e -> {
            int selectedIndex = hotWordsList.getSelectedIndex();
            if (selectedIndex != -1) {
                String hotWord = hotWordsListModel.getElementAt(selectedIndex);
                hotWords.remove(hotWord);
                hotWordsListModel.remove(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(frame, "No hot word selected to remove.", "Error", JOptionPane.WARNING_MESSAGE);
            }
            updateHeader(hotWordsPanel, "Hot Words", hotWordsListModel.getSize());
        });

        buttonPanel.add(addHotWordButton);
        buttonPanel.add(removeHotWordButton);
        hotWordsPanel.add(buttonPanel, BorderLayout.SOUTH);
        return hotWordsPanel;
    }

    private void showPersonalizationScreen() {
    	JDialog personalizationFrame = new JDialog(frame, "Personalization Keywords", true);
        personalizationFrame.setSize(400, 300);
        personalizationFrame.setLayout(new BorderLayout());
        personalizationFrame.add(createHeader("Personal Words", personalizationKeywords.size()), BorderLayout.NORTH);

        DefaultListModel<String> personalizationListModel = new DefaultListModel<>();
        JList<String> personalizationList = new JList<>(personalizationListModel);
        //load from list
        for (String word : personalizationKeywords) {
            personalizationListModel.addElement(word);
        }
        personalizationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        personalizationFrame.add(new JScrollPane(personalizationList), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addKeywordButton = new JButton("Add Keyword");
        addKeywordButton.addActionListener(e -> {
            String keyword = JOptionPane.showInputDialog(personalizationFrame, "Enter Keyword:");
            if (keyword != null && !keyword.trim().isEmpty()) {
                personalizationListModel.addElement(keyword);
                personalizationKeywords.add(keyword);
            }
            updateHeader(personalizationFrame, "Personal Words", personalizationKeywords.size());
        });

        JButton removeKeywordButton = new JButton("Remove Keyword");
        removeKeywordButton.addActionListener(e -> {
            int selectedIndex = personalizationList.getSelectedIndex();
            if (selectedIndex != -1) {
                String removedKeyword = personalizationListModel.getElementAt(selectedIndex);
                personalizationKeywords.remove(removedKeyword);
                personalizationListModel.remove(selectedIndex);
                updateHeader(personalizationFrame, "Personal Words", personalizationKeywords.size());
            } else {
                JOptionPane.showMessageDialog(personalizationFrame, "No keyword selected to remove.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton clearKeywordsButton = new JButton("Clear Keywords");
        clearKeywordsButton.addActionListener(e -> {
            personalizationListModel.clear();
            personalizationKeywords.clear();
            updateHeader(personalizationFrame, "Personal Words", personalizationKeywords.size());
        });

        buttonPanel.add(addKeywordButton);
        buttonPanel.add(removeKeywordButton);
        buttonPanel.add(clearKeywordsButton);
        personalizationFrame.add(buttonPanel, BorderLayout.SOUTH);

        personalizationFrame.setVisible(true);
        personalizationFrame.setLocationRelativeTo(frame);
    }

    // Button creation methods
    private JButton createLoadApiButton(JPanel panel, DefaultListModel<String> listModel) {
        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                FileDataReader reader = new FileDataReader(new String[]{"key"}, selectedFile.getAbsolutePath(), null);
                List<String> loadedKeys = reader.get("key");
                listModel.clear();
                listModel.addAll(loadedKeys);
                apiKeys.addAll(loadedKeys);
            }
            updateHeader(panel, "API Keys", apiKeys.size());
        });
        return loadButton;
    }

    private JButton createLoadLeadsButton(JPanel leadsPanel, DefaultListModel<String> leadsListModel) {
        JButton loadLeadsButton = new JButton("Load");
        loadLeadsButton.addActionListener(e -> {
        	String msg = "Make sure leads start with the number in internation format (no +, -, (), or spaces)\n" +
   				 		 "following the number should come each personalized key word in the order they are listed in the settings\n" +
   				 		 "delineator may be left blank or any character may be entered if only using numbers\n" +
   				 		 "Enter the log delineator:";
		   	String delineator = JOptionPane.showInputDialog(frame, msg);
		   	if (delineator != null && delineator.length() != 1) {
		   		String errMsg = "The delineator is the character that sperates each section of the log.\n" +
						    	"Please enter exactly one character or leave it blank.";
		   		JOptionPane.showMessageDialog(frame, errMsg, "Invalid Input", JOptionPane.ERROR_MESSAGE);
		   		return;
		   	}
        	
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                List<String> leadKeys = new ArrayList<>();
                leadKeys.add("number");
                leadKeys.addAll(personalizationKeywords);
                FileDataReader reader = new FileDataReader(leadKeys.toArray(new String[0]), selectedFile.getAbsolutePath(), delineator);
                List<Map<String, String>> fetchedLeads = reader.get();
                leadsForSending.addAll(fetchedLeads);
                leadsListModel.clear();
                for (Map<String, String> map : leadsForSending) {
                    leadsListModel.addElement((String)map.get("number"));
                }
            }
            
        	updateHeader(leadsPanel, new String[] {"Leads", "Personal Keywords"},
            		new int[] {leadsListModel.getSize(), personalizationKeywords.size()});
        	
        });
        return loadLeadsButton;
    }

    private JButton createLoadStmpButton(JPanel panel, DefaultListModel<String> stmpListModel) {
        JButton loadStmpButton = new JButton("Load");
        loadStmpButton.addActionListener(e -> {
        	String msg = "Make sure logs are in the order of host, port, username, passwords, encryption\n" +
        				 "Encryption must be SSL or TSL or if not given connection will be unencrypted\n" +
        				 "Enter the log delineator:";
        	String delineator = JOptionPane.showInputDialog(frame, msg);
        	if (delineator == null || delineator.length() != 1) {
        		String errMsg = "The delineator is the character that sperates each section of the log.\n" +
    				    		"Please enter exactly one character.";
        		JOptionPane.showMessageDialog(frame, errMsg, "Invalid Input", JOptionPane.ERROR_MESSAGE);
        		return;
        	}
        	
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                FileDataReader reader = new FileDataReader(new String[]{"host", "port", "username", "password"}, selectedFile.getAbsolutePath(), "encryption", delineator);
                stmpSettings.addAll(reader.get());
                stmpListModel.clear();
                for (Map<String, String> map : stmpSettings) {
                    stmpListModel.addElement((String)map.get("host"));
                }
            }
            updateHeader(panel, "STMPs", stmpSettings.size());
        });
        return loadStmpButton;
    }

    private JButton createAddApiButton(JPanel panel, DefaultListModel<String> listModel) {
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            String newKey = JOptionPane.showInputDialog(frame, "Enter new API Key:");
            if (newKey != null && !newKey.trim().isEmpty()) {
                listModel.addElement(newKey);
                apiKeys.add(newKey);
            }
            updateHeader(panel, "API Keys", apiKeys.size());
        });
        return addButton;
    }

    private JButton createRemoveApiButton(JPanel panel, DefaultListModel<String> listModel, JList<String> list) {
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> {
            int selectedIndex = list.getSelectedIndex();
            if (selectedIndex != -1) {
                String removedKey = listModel.getElementAt(selectedIndex);
                apiKeys.remove(removedKey);
                listModel.remove(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(frame, "No key selected to remove.", "Error", JOptionPane.WARNING_MESSAGE);
            }
            updateHeader(panel, "API Keys", apiKeys.size());
        });
        return removeButton;
    }
}
