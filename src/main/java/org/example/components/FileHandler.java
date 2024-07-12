package org.example.components;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class FileHandler {
    private String absolutePath;
    private String userExportFolderPath;
    private ArrayList<String> fileExtensionPaths = new ArrayList<>();

    /**
     * Constructor for FileHandler class.
     * @param absolutePath - The absolute path of the directory to be handled.
     */
    public FileHandler(String absolutePath) {
        if (!absolutePath.isEmpty()) {
            this.absolutePath = absolutePath;
        }
    }

    /**
     * Getter for absolutePath.
     * @return - The absolute path of the directory.
     */
    public String getAbsolutePath() {
        return this.absolutePath;
    }

    /**
     * Setter for absolutePath.
     * @param newUserFolderPath - The new absolute path of the directory.
     */
    public void setAbsolutePath(String newUserFolderPath) {
        this.absolutePath = newUserFolderPath;
    }

    /**
     * Sets the absolute path of the export directory.
     *
     * @param userExportFolderPath The new absolute path of the export directory.
     *                             This path will be used to create the folder structure for exporting files.
     *                             If the path does not exist, the export directory will default to the user-specified path.
     */
    public void setUserExportFolderPath(String userExportFolderPath) {
        this.userExportFolderPath = userExportFolderPath;
    }

    // Scan user path and return files/folders in scanned path
    /**
     * Scans the user-specified path and returns files/folders in the scanned path.
     * @return - An array of ScannedFile objects representing files and folders.
     */
    public ScannedFile[] getFilesAndFolders() {
        File folderToScan = new File(this.absolutePath);
        ArrayList<ScannedFile> filesAndFolders = new ArrayList<>();

        // determine file or folder
        for (File file : Objects.requireNonNull(folderToScan.listFiles())) {
            boolean isFile = file.isFile();
            if (isFile) {
                filesAndFolders.add(new ScannedFile(file.getName(), "file"));
            } else {
                filesAndFolders.add(new ScannedFile(file.getName(), "folder"));
            }
        }

        return filesAndFolders.toArray(new ScannedFile[0]);
    }

    /**
     * Returns all folders within the user-specified path.
     * @return - An array of ScannedFile object representing folder.
     */
    public ScannedFile[] getSubfolders() {
        ArrayList<ScannedFile> scannedFolders = new ArrayList<>();

        // HINT: Pass empty String array to scan every folder
        ArrayList<String> folders = getFoldersToScan(new String[] {});
        for (String folderPath : Objects.requireNonNull(folders)) {
            scannedFolders.add(new ScannedFile(folderPath, "folder"));
        }

        return scannedFolders.toArray(new ScannedFile[0]);
    }

    /**
     * Creates a folder structure within the user-specified export path, based on the provided file extensions.
     * @param fileExtensions - An array of file extensions to create folders for.
     */
    public void createFolderStructure(String[] fileExtensions) {
        // No folders to create
        if (fileExtensions.length == 0) {
            return;
        }

        // Use scan folder, if new path doesn't exist
        File file = new File(this.userExportFolderPath);
        if (!file.exists()) {
            this.userExportFolderPath = this.absolutePath;
        }

        try {
            // Create output folder
            String outputFolder = this.userExportFolderPath + File.separator + "Output";
            Path pathToNewDirectory = Paths.get(outputFolder);
            boolean newOutputDirectoryExists = pathToNewDirectory.toFile().exists();

            // Avoid FileAlreadyExistsException
            if (!newOutputDirectoryExists) {
                Files.createDirectory(pathToNewDirectory);
            }

            // Create folder per file extension
            for (String fileExtension : fileExtensions) {
                String extensionDirectory = outputFolder + File.separator + fileExtension + " files";
                Path pathToNewExtensionDirectory = Paths.get(extensionDirectory);
                boolean newExtensionDirectoryExists = pathToNewExtensionDirectory.toFile().exists();

                // Avoid FileAlreadyExistsException
                if (!newExtensionDirectoryExists) {
                    Files.createDirectory(pathToNewExtensionDirectory);
                }
            }
        } catch (IOException exception) {
            System.out.println("Error creating directory: " + exception);
            return;
        }

        moveFilesToFolderStructure(fileExtensions);
    }

    /**
     * Moves files from the user-specified path to the created folder structure based on their file extensions.
     * @param fileExtensions - An array of file extensions to move files for.
     */
    public void moveFilesToFolderStructure(String[] fileExtensions) {
        // Check if fileExtensions exists fileExtensionPaths, then add to output folder structure
        try {
            // Move files to created folder structure
            for (String fileExtensionPath : this.fileExtensionPaths) {
                File file = new File(fileExtensionPath);
                String extension = getFileExtension(file.getName());

                // Check if iterated getFileExtensions(fileExtensions) exists fileExtensionPaths
                if (Arrays.asList(fileExtensions).contains(extension)) {
                    String outputFolder = this.userExportFolderPath + File.separator + "Output";
                    String extensionDirectory = outputFolder + File.separator + extension + " files";

                    // Move
                    Path sourcePath = Paths.get(fileExtensionPath);
                    Path destinationPath = Paths.get(extensionDirectory + File.separator + file.getName());
                    Files.move(sourcePath, destinationPath);
                }
            }
        } catch (IOException exception) {
            System.out.println("Error moving files to folder structure: " + exception);
        }
    }

    /**
     * Scans the user-specified folders and files for unique file extensions.
     * @param chosenFolders - An array of folders to scan for file extensions.
     * @param chosenFiles - An array of files to scan for file extensions.
     * @return - An array of ScannedFile objects representing unique file extensions.
     */
    public ScannedFile[] getFileExtensions(String[] chosenFolders, String[] chosenFiles) {
        // No folders to scan
        if (chosenFolders.length == 0 && chosenFiles.length == 0) {
            return new ScannedFile[] {};
        }

        this.fileExtensionPaths = new ArrayList<>();
        ArrayList<String> uniqueFileExtensions = new ArrayList<>();
        ArrayList<ScannedFile> foundFileExtensions = new ArrayList<>();

        // Scan chosenFiles for unique file extensions
        for (String chosenFile : chosenFiles) {
            String chosenFilePath = this.absolutePath + File.separator + chosenFile;
            File file = new File(chosenFilePath);
            String extension = getFileExtension(chosenFile);
            String absoluteFilePath = file.getAbsolutePath();


            if (file.isFile() && !extension.isEmpty() && !uniqueFileExtensions.contains(extension)) {
                // Add unique file extension
                uniqueFileExtensions.add(extension);
                foundFileExtensions.add(new ScannedFile(extension, "extension"));
            }

            if (file.isFile() && !extension.isEmpty() && !this.fileExtensionPaths.contains(absoluteFilePath)) {
                // Add unique file extension path
                this.fileExtensionPaths.add(absoluteFilePath);
            }
        }

        ArrayList<String> folders = new ArrayList<>();
        if (chosenFolders.length > 0) {
            folders = getFoldersToScan(chosenFolders);
        }

        // Scan chosenFolders for unique file extensions
        while (!folders.isEmpty()) {
            String scanningFolder = folders.remove(0);
            File folder = new File(scanningFolder);
            if (folder.listFiles() == null) {
                continue; // Skip non-existent directories
            }

            // Scan folder
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                String extension = getFileExtension(file.getName());
                String absoluteFilePath = file.getAbsolutePath();

                if (file.isFile() && !extension.isEmpty() && !uniqueFileExtensions.contains(extension)) {
                    // Add unique file extension
                    uniqueFileExtensions.add(extension);
                    foundFileExtensions.add(new ScannedFile(extension, "extension"));
                }

                if (file.isFile() && !extension.isEmpty() && !this.fileExtensionPaths.contains(absoluteFilePath)) {
                    // Add unique file extension path
                    this.fileExtensionPaths.add(absoluteFilePath);
                }
            }
        }

        return foundFileExtensions.toArray(new ScannedFile[0]);
    }

    /* =======================
     Helpers below
     ======================= */
    /**
     * Retrieves a list of folders to scan for file extensions.
     * @param chosenFolders - An array of folders to scan.
     * @return - An array of folder paths to scan.
     */
    private ArrayList<String> getFoldersToScan(String[] chosenFolders) {
        // Add to global folders (for extension scanning)
        ArrayList<String> folders = new ArrayList<>(Arrays.asList(chosenFolders));

        // Store folders for processing
        ArrayList<String> foldersToScan = new ArrayList<>(folders);
        if (chosenFolders.length == 0) {
            // Used for subfolder scanning
            foldersToScan.add(this.absolutePath);
        } else {
            folders.replaceAll(s -> this.absolutePath + File.separator + s);
            foldersToScan.replaceAll(s -> this.absolutePath + File.separator + s);
        }

        while (!foldersToScan.isEmpty()) {
            String scanningFolder = foldersToScan.remove(0);
            // Scan folder
            File folder = new File(scanningFolder);
            if (folder.listFiles() == null) {
                continue; // Skip non-existent directories
            }

            // List files/folders in folder
            for (File item : Objects.requireNonNull(folder.listFiles())) {

                // Directory check
                boolean isDirectory = item.isDirectory();
                if (isDirectory) {
                    // Add to global folders (for extension scanning)
                    String itemPath = item.getAbsolutePath();
                    folders.add(itemPath);

                    // Add to additional folders to scan in while loop
                    foldersToScan.add(itemPath);
                }
            }
        }

        return new ArrayList<>(folders);
    }

    /**
     * Retrieves the file extension from a file name.
     * @param fileName - The name of the file.
     * @return - The file extension.
     */
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex);
        } else {
            return ""; // No extension found; return empty
        }
    }
}
