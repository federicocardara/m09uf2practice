package cat.uvic.teknos.m09.uf2;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Program {
    private static boolean follow = true;
    private static Scanner in = new Scanner(System.in);
    private static HashParameters hashParameters;

    private static Properties properties;


    public static void main(String[] args) throws NoSuchAlgorithmException, URISyntaxException, IOException, InterruptedException {
        properties = new Properties();
        properties.load(Program.class.getResourceAsStream("/hash.properties"));

        hashParameters = new HashParameters(properties.getProperty("algorithm"), properties.getProperty("salt"));

        var t1 = new Thread(Program::updateHash,"t1 comprovant");
        t1.start();

        while (follow) {
            System.out.println("Type the path of the file you want to hash");
            System.out.println("Digest: " + getDigest(in.nextLine(), hashParameters));
            System.out.println("Salt: " + hashParameters.getSalt());
            askToFollow();
        }

        System.out.println("Bye!");

    }
    public static void updateHash() {
        while (follow){
            try {
                properties.load(Program.class.getResourceAsStream("/hash.properties"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                Thread.sleep(1000*60);
                if (!properties.getProperty("salt").equals(hashParameters.getSalt())||!properties.getProperty("algorithm").equals(hashParameters.getAlgorithm())) {
                    hashParameters.setSalt(properties.getProperty("salt"));
                    hashParameters.setAlgorithm(properties.getProperty("algorithm"));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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