package svg;
import java.io.File;

public class FolderCleanup {

    public static void main(String[] args) {
        // Replace 'your_root_directory' with the actual path of your root directory
        String rootDirectory = "D:\\Mendix\\AccuView-Sprint11_Dev\\excel\\svgbhas";

        // Specify the folder to clean up (in this case, "jpeg" folder)
        String folderToClean = "png";

        // Start cleanup from the root directory
        cleanupFolders(rootDirectory, folderToClean);
    }

    private static void cleanupFolders(String directoryPath, String folderToClean) {
        File directory = new File(directoryPath);

        // Get all files and subdirectories in the current directory
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && file.getName().equals(folderToClean)) {
                    // Delete the folder and its contents
                    deleteFolder(file);
                    System.out.println("Deleted: " + file.getAbsolutePath());
                } else if (file.isDirectory()) {
                    // Recursively process subdirectories
                    cleanupFolders(file.getAbsolutePath(), folderToClean);
                }
            }
        }
    }

    private static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursively delete subdirectories
                    deleteFolder(file);
                } else {
                    // Delete the file
                    file.delete();
                }
            }
        }

        // Delete the empty folder
        folder.delete();
    }
}
