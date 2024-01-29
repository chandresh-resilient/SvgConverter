package svg;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class SvgToJpegConverter {

    public static void main(String[] args) {
        // Replace 'your_root_directory' with the actual path of your root directory
        String rootDirectory = "D:\\Mendix\\Accewview Sp11 dev\\excels\\images";

        // Create a folder named 'jpeg' in the root directory to store the JPEG images
        String jpegFolder = rootDirectory + File.separator + "jpeg";
        File jpegFolderFile = new File(jpegFolder);
        jpegFolderFile.mkdirs();

        // Convert all SVG images to JPEG in the root directory and its subdirectories
        convertAllSvgToJpeg(rootDirectory, jpegFolder);
    }

    private static void convertAllSvgToJpeg(String directoryPath, String jpegFolder) {
        File directory = new File(directoryPath);

        // Get all files and subdirectories in the current directory
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".svg")) {
                    // Generate corresponding JPEG filename
                    String jpegFileName = file.getName().replace(".svg", ".jpeg");
                    String jpegPath = jpegFolder + File.separator + jpegFileName;

                    // Convert SVG to JPEG
                    convertSvgToJpeg(file, jpegPath);
                    System.out.println("Converted: " + file.getName() + " -> " + jpegFileName);
                } else if (file.isDirectory()) {
                    // Recursively process subdirectories
                    convertAllSvgToJpeg(file.getAbsolutePath(), jpegFolder);
                }
            }
        }
    }

    private static void convertSvgToJpeg(File svgFile, String jpegPath) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(svgFile);

            // Get all image elements in the SVG
            NodeList imageNodes = doc.getElementsByTagName("image");

            for (int i = 0; i < imageNodes.getLength(); i++) {
                Node imageNode = imageNodes.item(i);
                Node hrefAttr = imageNode.getAttributes().getNamedItem("xlink:href");

                if (hrefAttr != null) {
                    String hrefValue = hrefAttr.getNodeValue();

                    if (hrefValue.startsWith("data:image/png;base64,")) {
                        // Convert PNG-encoded data to JPEG
                        convertEncodedDataToJpeg(hrefValue, jpegPath);
                        System.out.println("Converted PNG-encoded data from: " + svgFile.getName() + " -> " + jpegPath);
                        return;  // Stop processing further if a conversion is done
                    } else if (hrefValue.startsWith("data:image/jpeg;base64,")) {
                        // Convert JPEG-encoded data to JPEG
                        convertEncodedDataToJpeg(hrefValue, jpegPath);
                        System.out.println("Converted JPEG-encoded data from: " + svgFile.getName() + " -> " + jpegPath);
                        return;  // Stop processing further if a conversion is done
                    }
                }
            }

            // If no encoded data found, perform the standard SVG to JPEG conversion
            try (FileInputStream inputStream = new FileInputStream(svgFile);
                 FileOutputStream outputStream = new FileOutputStream(jpegPath)) {

                // Create a JPEG transcoder with quality setting
                JPEGTranscoder transcoder = new JPEGTranscoder();
                transcoder.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, 0.85f); // Adjust quality as needed

                // Set the input and output for the transcoder
                TranscoderInput input = new TranscoderInput(inputStream);
                TranscoderOutput output = new TranscoderOutput(outputStream);

                // Perform the conversion
                try {
                    transcoder.transcode(input, output);
                } catch (TranscoderException e) {
                    System.out.println("Error while converting SVG to JPEG for " + svgFile.getName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            } catch (IOException e) {
                System.out.println("IO error while converting SVG to JPEG for " + svgFile.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        } catch (ParserConfigurationException | IOException e) {
            System.out.println("Error while processing SVG file " + svgFile.getName() + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unexpected error while processing SVG file " + svgFile.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void convertEncodedDataToJpeg(String encodedData, String jpegPath) {
        try {
            String base64Data = encodedData.split(",")[1];
            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);

            try (FileOutputStream outputStream = new FileOutputStream(jpegPath)) {
                outputStream.write(decodedBytes);
            }
        } catch (IOException e) {
            System.out.println("IO error while converting encoded data to JPEG: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
