package cat.uvic.teknos.m09.uf2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Properties;
import java.util.Scanner;

public class Program {
    private static boolean follow = true;
    private static Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws NoSuchAlgorithmException, URISyntaxException, IOException {
        var properties = new Properties();
        properties.load(Program.class.getResourceAsStream("/hash.properties"));
        var hashParameters = new HashParameters(properties.getProperty("algorithm"), properties.getProperty("salt"));

        Thread th = new Thread(Program::th);

        while (follow) {
            System.out.println("Type the path of the file you want to hash");
            System.out.println("Digest: " + getDigest(in.nextLine(), hashParameters));
            System.out.println("Salt: " + hashParameters.getSalt());

            askToFollow();
        }
        System.out.println("Bye!");
    }

    private static void th(){
        Properties prop = new Properties();
        try{
            while (true){
                Thread.sleep(60000);

                prop.load(new FileInputStream("src/main/resources/hash.properties"));
            }
        }
        catch (InterruptedException e){
            try {
                throw new InterruptedException();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void askToFollow() {
        System.out.println("Type 'e' to exit or just enter to follow");
        var exit = in.nextLine();

        if (exit.trim().toLowerCase().equals("e")) {
            follow = false;
        }
    }

    private static String getDigest(String path, HashParameters parameters) throws NoSuchAlgorithmException, URISyntaxException, IOException {
        String digestBase64 = null;
        var pathObj = Paths.get(path);
        if (Files.exists(pathObj)) {
            var data = Files.readAllBytes(pathObj);

            var messageDigest = MessageDigest.getInstance(parameters.getAlgorithm());
            messageDigest.update(parameters.getSaltBytes());

            var digest = messageDigest.digest(data);

            var base64Encoder = Base64.getEncoder();

            digestBase64 =  base64Encoder.encodeToString(digest);
        }

        return digestBase64;
    }


}