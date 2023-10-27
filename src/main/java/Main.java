import xyz.osamusasa.osmzip.ZipFile;
import xyz.osamusasa.osmzip.io.OsmZipIOException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String args[]) {

        final String PATH_TEXT_ZIP = "TEXT.zip";
        final String PATH_OUT_DIR = "out";


        // create temporary directory
        Path pathOutDir = Path.of("");
        try {
            pathOutDir = Files.createDirectories(
                    Path.of(
                            new File(Main.class.getResource("").getFile()).getAbsolutePath(),
                            PATH_OUT_DIR,
                            String.valueOf(System.currentTimeMillis()))
            );

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File zipFile = Path.of(Main.class.getResource(PATH_TEXT_ZIP).toURI()).toFile();

            ZipFile zip = ZipFile.read(zipFile);
            System.out.println(zip);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (OsmZipIOException e) {
            e.printStackTrace();
        }
    }
}
