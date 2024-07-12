package org.example.components;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class Gui {
    private final FileHandler userChosenFolder = new FileHandler("");

    /**
     * This method creates the GUI for the application.
     */
    public Gui() {
        createGui();
    }

    /**
     * This method handles the click event for the "Scan for file extensions" button.
     *
     * @param leftDynamicCheckboxPanel The JPanel object containing the checkboxes for the files and folders.
     * @param rightDynamicCheckboxPanel The JPanel object containing the checkboxes for the found file extensions.
     */
    private void handleFindFileExtensionsClick(JPanel leftDynamicCheckboxPanel, JPanel rightDynamicCheckboxPanel) {
        // Selected check
        Component[] checkBoxesLeft = leftDynamicCheckboxPanel.getComponents();
        ArrayList<String> checkedFolders = new ArrayList<>();
        ArrayList<String> checkedFiles = new ArrayList<>();

        for (Component checkbox : checkBoxesLeft) {
            if (checkbox instanceof JCheckBox) {

                // File path for checks
                String checkBoxName = ((JCheckBox) checkbox).getText();
                String checkBoxPath = userChosenFolder.getAbsolutePath() + File.separator + checkBoxName;
                File checkboxFile = new File(checkBoxPath);
                if (((JCheckBox) checkbox).isSelected() && checkboxFile.isDirectory()) {
                    // Checked folder
                    checkedFolders.add(checkboxFile.getName());
                } else if (((JCheckBox) checkbox).isSelected() && checkboxFile.isFile()) {
                    // Checked file
                    checkedFiles.add(checkboxFile.getName());
                }
            }
        }

        String[] chosenFolders = checkedFolders.toArray(new String[0]);
        String[] chosenFiles = checkedFiles.toArray(new String[0]);

        if (chosenFolders.length == 0 && chosenFiles.length == 0) {
            rightDynamicCheckboxPanel.removeAll();
            refreshComponent(rightDynamicCheckboxPanel);
            return;
        }

        // Create checkboxes for found items in chosen path
        ScannedFile[] checkBoxes = userChosenFolder.getFileExtensions(chosenFolders, chosenFiles);
        generateCheckBoxes(rightDynamicCheckboxPanel, checkBoxes);
    }

    /**
     * This method handles the click event for the "Show Subfolders" button.
     *
     * @param leftDynamicFolderPanel The JPanel object to display the found folders.
     */
    private void handleShowSubfoldersClick(JPanel leftDynamicFolderPanel) {
        if (userChosenFolder.getAbsolutePath() == null) {
            return;
        }
        File file = new File(userChosenFolder.getAbsolutePath());

        // Clear existing labels
        leftDynamicFolderPanel.removeAll();

        if (!file.exists()) {
            JLabel label = createLabel("No folders to populate");
            leftDynamicFolderPanel.add(label);
            refreshComponent(leftDynamicFolderPanel);
            return;
        }

        // Load folders
        ScannedFile[] folders = userChosenFolder.getSubfolders();

        if (folders.length > 0) {
            for (ScannedFile folder : folders) {
                JLabel label = createLabel(folder.getName());
                leftDynamicFolderPanel.add(label);
            }
        } else {
            JLabel label = createLabel("No folders to populate");
            leftDynamicFolderPanel.add(label);
        }

        // Refresh the leftDynamicFolderPanel to update the UI
        refreshComponent(leftDynamicFolderPanel);
    }

    /**
     * Refreshes the display of a given component.
     *
     * @param component The component to refresh.
     */
    private void refreshComponent(JComponent component) {
        component.revalidate();
        component.repaint();
    }

    /**
     * Retrieves a font based on the given style.
     *
     * @param fontWeight The style of the font (e.g., "regular", "bold", "light").
     * @return - The font with the specified style.
     */
    private Font getFont(String fontWeight) {
        Font font = null;

        try {
            InputStream inputStream;
            switch (fontWeight) {
                case "bold" -> {
                    inputStream = getClass().getResourceAsStream("/Fonts/Roboto-Bold.ttf");
                    font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(inputStream)).deriveFont(Font.PLAIN, 16);
                }
                case "regular" -> {
                    inputStream = getClass().getResourceAsStream("/Fonts/Roboto-Regular.ttf");
                    font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(inputStream)).deriveFont(Font.PLAIN, 14);
                }
                case "light" -> {
                    inputStream = getClass().getResourceAsStream("/Fonts/Roboto-Light.ttf");
                    font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(inputStream)).deriveFont(Font.PLAIN, 12);
                }
            }
        } catch (Exception e) {
            System.out.println("Error with getFont: " + e);
        }

        return font;
    }

    /**
     * Retrieves a color based on the given name.
     *
     * @param color The name of the color (e.g., "white", "blue", "green").
     * @return - The color with the specified name.
     */
    private Color getColor(String color) {
        return switch (color) {
            case "white" -> new Color(255, 255, 255);
            case "blue" -> new Color(82, 173, 200);
            case "green" -> new Color(76, 175, 80);
            default -> {
                System.out.println("Could not find color. Returning default.");
                yield new Color(255);
            }
        };
    }

    /**
     * Creates a button with the given text, background color, and font.
     *
     * @param text The text to display on the button.
     * @return - The created button.
     */
    private JButton createButton(String text) {
        JButton button = new RoundedButton(text, getColor("green"));
        button.setBackground(getColor("green"));
        button.setForeground(getColor("white"));
        button.setFont(getFont("regular"));
        return button;
    }

    /**
     * Creates a label with the given text, foreground color, and font.
     *
     * @param text The text to display on the label.
     * @return - The created label.
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(getColor("white"));
        label.setFont(getFont("light"));
        return label;
    }

    /**
     * Creates a checkbox with the given text, background color, foreground color, and font.
     *
     * @param text The text to display on the checkbox.
     * @return - The created checkbox.
     */
    private JCheckBox createCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkBox.setBackground(getColor("blue"));
        checkBox.setForeground(getColor("white"));
        checkBox.setFont(getFont("light"));
        return checkBox;
    }

    /**
     * Creates a scroll pane with the given component and dimension.
     *
     * @param attachToComponent The component to attach to the scroll pane.
     * @param dimension The dimension of the scroll pane.
     * @return - The created scroll pane.
     */
    private JScrollPane createScrollPane(Component attachToComponent, Dimension dimension) {
        JScrollPane scrollPane = new JScrollPane(attachToComponent, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(dimension);
        scrollPane.getVerticalScrollBar().setUnitIncrement(32);
        scrollPane.getVerticalScrollBar().setBlockIncrement(100);
        return scrollPane;
    }

    /**
     * Sets a titled border with the given text, title color, and title font.
     *
     * @param text The text to display on the border.
     * @return - The created titled border.
     */
    private TitledBorder setTitledBorder(String text) {
        TitledBorder titledBorder = new TitledBorder(text);
        titledBorder.setTitleColor(getColor("white"));
        titledBorder.setTitleFont(getFont("bold"));
        return titledBorder;
    }

    /**
     * Sets a box layout with the given panel and axis.
     *
     * @param jPanel The panel to set the layout for.
     * @return - The created box layout.
     */
    private BoxLayout setBoxLayout(JPanel jPanel) {
        return new BoxLayout(jPanel, BoxLayout.Y_AXIS);
    }

    /**
     * Generates check boxes for the given array of scanned files and adds them to the given panel.
     *
     * @param panel The panel to add the checkboxes to.
     * @param checkBoxes The array of scanned files to generate check boxes for.
     */
    private void generateCheckBoxes(JPanel panel, ScannedFile[] checkBoxes) {
        // clear existing checkboxes
        panel.removeAll();

        // generate checkboxes
        for (ScannedFile file : checkBoxes) {
            JCheckBox checkbox = createCheckBox(file.getName());
            panel.add(checkbox);
        }

        // update display
        refreshComponent(panel);
    }

    /**
     * Creates the GUI with all its components and functionality.
     */
    private void createGui() {
        // Create the frame
        JFrame frame = new JFrame("File Sorter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Get screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = screenSize.height;

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        /* Theme styling */
        Dimension themeDimension = new Dimension(screenHeight / 2 - 25, 50 * 10);
        FlowLayout themeFlowLeft = new FlowLayout(FlowLayout.LEFT, 5, 5);
        // Prevent empty space below
        Dimension maxLabelDimension = new Dimension(Integer.MAX_VALUE, 30);
        Dimension maxButtonDimension = new Dimension(Integer.MAX_VALUE, 40);

        /* ===========================================
         * GUI variable declarations
         * ======================================== */
        // Left
        JPanel leftPanel = new JPanel();
        // Left - Section 1
        JPanel leftPanelSectionOne = new JPanel();
        JLabel directoryLabel = createLabel("A/really/long/path/to/the/correct/folder/goes/here");
        // Left - Section 2
        JPanel chooseFolderPanel = new JPanel(themeFlowLeft);
        JLabel chosenFolderLabel = createLabel("<html>None selected</html>");
        JButton chooseFolderButton = createButton("Choose Folder To Scan");
        JButton showFoundSubfoldersButton = createButton("Show Subfolders");
        // Left - Section 3
        JPanel chooseSaveFolderPanel = new JPanel(themeFlowLeft);
        JLabel chosenSaveFolderLabel = createLabel("FolderXYZ (default)");
        JButton chooseSaveFolderButton = createButton("Choose Save/Output Folder");
        // Left - Section 4
        JPanel leftDynamicCheckboxPanel = new JPanel();
        // Left - Section 5
        JPanel selectDeselectPanelLeft = new JPanel(themeFlowLeft);
        JButton selectAllButtonLeft = createButton("Select All");
        JButton deselectAllButtonLeft = createButton("Deselect All");
        // Left - Section 6
        JPanel leftDynamicFolderPanel = new JPanel();
        JLabel foundFoldersLabel = createLabel("Click on 'Show Subfolders' to begin scan");

        // Right
        JPanel rightPanel = new JPanel();
        // Right - Section 1
        JPanel rightPanelScan = new JPanel(themeFlowLeft);
        JButton findExtensionsButton = createButton("Scan for file extensions");
        // Right - Section 2
        JPanel rightDynamicCheckboxPanel = new JPanel();
        // Right - Section 3
        JPanel selectDeselectPanelRight = new JPanel(themeFlowLeft);
        JButton selectAllButtonRight = createButton("Select All");
        JButton deselectAllButtonRight = createButton("Deselect All");
        // Right - Section 4
        JPanel sortFilesPanel = new JPanel(themeFlowLeft);
        JButton sortFilesButton = createButton("Sort Files");


        /* ===========================================
         * Panel - left half
         * ======================================== */
        leftPanel.setLayout(setBoxLayout(leftPanel));
        leftPanel.setBackground(getColor("blue"));
        leftPanel.setBorder(setTitledBorder("Files and Folders"));

        /* ===========================================
        * Panel - left section 1 (directory)
        * ========================================= */
        leftPanelSectionOne.setLayout(themeFlowLeft);
        leftPanelSectionOne.setBackground(getColor("blue"));
        leftPanelSectionOne.setMaximumSize(maxLabelDimension);

        leftPanelSectionOne.add(directoryLabel);
        leftPanel.add(leftPanelSectionOne);

        /* ===========================================
         * Panel - left section 2 (choose scan folder)
         * ======================================== */
        chooseFolderPanel.setBackground(getColor("blue"));
        chooseFolderPanel.setMaximumSize(maxButtonDimension);
        chosenFolderLabel.setPreferredSize(new Dimension(150, 30));

        chooseFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a file chooser
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select a folder to sort");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return "Folders";
                    }
                });

                // Show the file chooser dialog
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFolder = fileChooser.getSelectedFile();
                    // Print the path of the selected folder
                    directoryLabel.setText(selectedFolder.getAbsolutePath());
                    chosenFolderLabel.setText("<html>" + selectedFolder.getName() + "</html>");
                    chosenSaveFolderLabel.setText("<html>" + selectedFolder.getName() + " (default) </html>");

                    // Update found subfolders
                    leftDynamicFolderPanel.removeAll();
                    JLabel label = createLabel("Click on 'Show Subfolders' to begin scan");
                    leftDynamicFolderPanel.add(label);
                    refreshComponent(leftDynamicFolderPanel);

                    // Create checkboxes for found items in chosen path
                    userChosenFolder.setAbsolutePath(selectedFolder.getAbsolutePath());
                    userChosenFolder.setUserExportFolderPath(selectedFolder.getAbsolutePath());
                    ScannedFile[] checkBoxes = userChosenFolder.getFilesAndFolders();
                    generateCheckBoxes(leftDynamicCheckboxPanel, checkBoxes);
                } else {
                    directoryLabel.setText("No folder selected");
                    chosenFolderLabel.setText("No folder selected");
                    chosenSaveFolderLabel.setText("No folder selected");
                }
            }
        });
        showFoundSubfoldersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleShowSubfoldersClick(leftDynamicFolderPanel);
            }
        });

        chooseFolderPanel.add(chooseFolderButton);
        chooseFolderPanel.add(chosenFolderLabel);
        chooseFolderPanel.add(showFoundSubfoldersButton);
        leftPanel.add(chooseFolderPanel);

        /* ===========================================
         * Panel - left section 3 (choose output folder)
         * ======================================== */
        chooseSaveFolderPanel.setBackground(getColor("blue"));
        chooseSaveFolderPanel.setMaximumSize(maxButtonDimension);

        chooseSaveFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a file chooser
                JFileChooser fileChooser = new JFileChooser(userChosenFolder.getAbsolutePath());
                fileChooser.setDialogTitle("Select a location to save/output sorted files");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return "Folders";
                    }
                });

                // Show the file chooser dialog
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFolder = fileChooser.getSelectedFile();
                    // Print the path of the selected folder
                    chosenSaveFolderLabel.setText("<html>" + selectedFolder.getName() + "</html>");
                    userChosenFolder.setUserExportFolderPath(selectedFolder.getAbsolutePath());
                } else {
                    chosenSaveFolderLabel.setText("No folder selected");
                }
            }
        });

        chooseSaveFolderPanel.add(chooseSaveFolderButton);
        chooseSaveFolderPanel.add(chosenSaveFolderLabel);
        leftPanel.add(chooseSaveFolderPanel);

        /* ===========================================
         * Panel - left section 4 (dynamic checkbox)
         * ======================================== */
        leftDynamicCheckboxPanel.setLayout(setBoxLayout(leftDynamicCheckboxPanel));
        leftDynamicCheckboxPanel.setBackground(getColor("blue"));

        leftPanel.add(leftDynamicCheckboxPanel);

        /* ===========================================
         * Panel - left section 5 (select checkboxes)
         * ======================================== */
        selectDeselectPanelLeft.setBackground(getColor("blue"));
        selectDeselectPanelLeft.setMaximumSize(maxButtonDimension);

        selectAllButtonLeft.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Component[] checkBoxes = leftDynamicCheckboxPanel.getComponents();
                for (Component checkbox : checkBoxes) {
                    if (checkbox instanceof JCheckBox) {
                        ((JCheckBox) checkbox).setSelected(true);
                    }
                }
            }
        });

        deselectAllButtonLeft.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Component[] checkBoxes = leftDynamicCheckboxPanel.getComponents();
                for (Component checkbox : checkBoxes) {
                    if (checkbox instanceof JCheckBox) {
                        ((JCheckBox) checkbox).setSelected(false);
                    }
                }
            }
        });

        selectDeselectPanelLeft.add(selectAllButtonLeft);
        selectDeselectPanelLeft.add(deselectAllButtonLeft);
        leftPanel.add(selectDeselectPanelLeft);

        /* ===========================================
         * Panel - left section 6 (found folders)
         * ======================================== */
        leftDynamicFolderPanel.setLayout(setBoxLayout(leftDynamicFolderPanel));
        leftDynamicFolderPanel.setBackground(getColor("blue"));
        leftDynamicFolderPanel.setBorder(setTitledBorder("Found Subfolders"));

        leftDynamicFolderPanel.add(foundFoldersLabel);

        JScrollPane scrollPane = createScrollPane(leftDynamicFolderPanel, new Dimension(200, 300));

        // Add JScrollPane to the frame
        leftPanel.add(scrollPane);

        /* ===========================================
         * Panel - right half
         * ======================================== */
        rightPanel.setLayout(setBoxLayout(rightPanel));
        rightPanel.setBackground(getColor("blue"));
        rightPanel.setForeground(getColor("white"));
        rightPanel.setBorder(setTitledBorder("Extensions found"));

        /* ===========================================
         * Panel - right section 1 (scan for file extensions)
         * ======================================== */
        rightPanelScan.setBackground(getColor("blue"));
        rightPanelScan.setMaximumSize(maxButtonDimension);

        findExtensionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = new File(userChosenFolder.getAbsolutePath());
                if (!file.exists()) {
                    return;
                }

                handleFindFileExtensionsClick(leftDynamicCheckboxPanel, rightDynamicCheckboxPanel);
            }
        });

        rightPanelScan.add(findExtensionsButton);
        rightPanel.add(rightPanelScan);

        /* ===========================================
         * Panel - right section 2 (dynamic checkbox)
         * ======================================== */
        rightDynamicCheckboxPanel.setLayout(setBoxLayout(rightDynamicCheckboxPanel));
        rightDynamicCheckboxPanel.setBackground(getColor("blue"));

        rightPanel.add(rightDynamicCheckboxPanel);

        /* ===========================================
         * Panel - right section 3 (select checkboxes)
         * ======================================== */
        selectDeselectPanelRight.setBackground(getColor("blue"));
        selectDeselectPanelRight.setMaximumSize(maxButtonDimension);

        selectAllButtonRight.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Component[] checkBoxes = rightDynamicCheckboxPanel.getComponents();
                for (Component checkbox : checkBoxes) {
                    if (checkbox instanceof JCheckBox) {
                        ((JCheckBox) checkbox).setSelected(true);
                    }
                }
            }
        });

        deselectAllButtonRight.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Component[] checkBoxes = rightDynamicCheckboxPanel.getComponents();
                for (Component checkbox : checkBoxes) {
                    if (checkbox instanceof JCheckBox) {
                        ((JCheckBox) checkbox).setSelected(false);
                    }
                }
            }
        });

        selectDeselectPanelRight.add(selectAllButtonRight);
        selectDeselectPanelRight.add(deselectAllButtonRight);
        rightPanel.add(selectDeselectPanelRight);

        /* ===========================================
         * Panel - right section 4 (sort files)
         * ======================================== */
        sortFilesPanel.setBackground(getColor("blue"));
        sortFilesPanel.setMaximumSize(maxButtonDimension);

        sortFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userChosenFolder.getAbsolutePath() == null) {
                    return;
                }

                File file = new File(userChosenFolder.getAbsolutePath());
                if (!file.exists()) {
                    return;
                }

                // Get checked folders
                Component[] checkBoxesRight = rightDynamicCheckboxPanel.getComponents();
                ArrayList<String> checkedFileExtensions = new ArrayList<>();

                for (Component checkbox : checkBoxesRight) {
                    if (checkbox instanceof JCheckBox) {

                        // Get checkbox file extension
                        String checkBoxFileExtension = ((JCheckBox) checkbox).getText();
                        if (((JCheckBox) checkbox).isSelected()) {
                            // Add if checked
                            checkedFileExtensions.add(checkBoxFileExtension);
                        }
                    }
                }

                String[] fileExtensions = checkedFileExtensions.toArray(new String[0]);
                userChosenFolder.createFolderStructure(fileExtensions);

                /* Get file extensions button click */
                handleFindFileExtensionsClick(leftDynamicCheckboxPanel, rightDynamicCheckboxPanel);
            }
        });

        sortFilesPanel.add(sortFilesButton);
        rightPanel.add(sortFilesPanel);

        // Add the panels to JScrollPanes
        JScrollPane leftScrollPane = createScrollPane(leftPanel, themeDimension);
        JScrollPane rightScrollPane = createScrollPane(rightPanel, themeDimension);

//        JScrollPane foundFoldersScrollPane = createScrollPane(leftDynamicFolderPanel);

        // Finalize frame
        mainPanel.add(leftScrollPane);
        mainPanel.add(rightScrollPane);
        frame.add(mainPanel);
        frame.setSize(screenHeight, screenHeight / 2);
        frame.setLocationRelativeTo(null);

        // Make the frame visible
        frame.setVisible(true);
    }
}
