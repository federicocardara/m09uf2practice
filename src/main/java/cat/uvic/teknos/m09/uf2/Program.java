package cat.uvic.teknos.m09.uf2;

import cat.uvic.teknos.m09.uf2.exceptions.M09Uf2PracticeException;

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
    private static HashParameters hashParameters;

    public static void main(String[] args){


        try {
            var checker=new Thread(Program::checkIfPropertiesChanged);
            checker.start();
            Thread.sleep(2000);
            while (follow) {
                System.out.println("Type the path of the file you want to hash");
                System.out.println("Digest: " + getDigest(in.nextLine(), hashParameters));
                System.out.println("Salt: " + hashParameters.getSalt());

                askToFollow();
            }
            System.out.println("Bye!");
        } catch (InterruptedException e) {
            throw new M09Uf2PracticeException("A problem occurred while calling static method 'sleep' of 'Thread class'",e);
        }

    }

    private static void checkIfPropertiesChanged() {
        try {
            Properties properties = new Properties();

            while (follow){
                synchronized (Program.class) {
                    properties.load(Program.class.getResourceAsStream("/hash.properties"));
                    hashParameters = new HashParameters(properties.getProperty("algorithm"), properties.getProperty("salt"));
                }
                Thread.sleep(1000 * 60);

            }
        } catch (InterruptedException e) {
            throw new M09Uf2PracticeException("A problem occurred while calling static method 'sleep' of 'Thread class'",e);
        } catch (IOException e) {
            throw new M09Uf2PracticeException("A problem occurred while loading the properties from the 'hash.properties' file",e);
        }

    }

    private static void askToFollow() {
        System.out.println("Type 'e' to exit or just enter to follow");
        var exit = in.nextLine();

        if (exit.trim().toLowerCase().equals("e")) {
            follow = false;
        }
    }

    private static String getDigest(String path, HashParameters parameters) {
        try{
            String digestBase64 = null;
            synchronized (Program.class) {
                var pathObj = Paths.get(path);
                if (Files.exists(pathObj)) {
                    var data = Files.readAllBytes(pathObj);

                    var messageDigest = MessageDigest.getInstance(parameters.getAlgorithm());
                    messageDigest.update(parameters.getSaltBytes());

                    var digest = messageDigest.digest(data);

                    var base64Encoder = Base64.getEncoder();

                    digestBase64 = base64Encoder.encodeToString(digest);


                    return digestBase64;
                }
                return digestBase64;
            }
        } catch (NoSuchAlgorithmException e) {
            throw new M09Uf2PracticeException("A problem occurred while trying to use the algorithm of the 'hash.properties', check if the name is written correctly.",e);
        } catch (IOException e) {
            throw new RuntimeException("A problem occurred while reading the given file",e);
        }
    }

}