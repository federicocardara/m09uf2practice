package cat.uvic.teknos.m09.uf2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Program implements Runnable {
    private static boolean follow = true;
    private static Scanner in = new Scanner(System.in);
    private static HashParameters hashParameters;
    public static Lock lock = new ReentrantLock();
    public static Condition condition = lock.newCondition();
    private static Properties properties;
    private static boolean finish = false;

    public static void main(String[] args) throws NoSuchAlgorithmException, URISyntaxException, IOException, InterruptedException {
        try {
            Thread thread = new Thread(Program::updateProperties);
            Thread threadGetHash = new Thread(Program::threadCalculateHash);
            properties = new Properties();
            properties.load(Program.class.getResourceAsStream("/hash.properties"));

            hashParameters = new HashParameters(properties.getProperty("algorithm"), properties.getProperty("salt"));

            threadGetHash.start();
            thread.start();

            threadGetHash.join();
            thread.join();
            System.out.println("Bye!");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void threadCalculateHash() {
        try {
            while (follow) {
                try{

                System.out.println("Type the path of the file you want to hash");
                String path = in.nextLine();
                lock.lock();
                System.out.println("Digest: " + getDigest(path, hashParameters));
                System.out.println("Salt: " + hashParameters.getSalt());
                lock.unlock();
                }catch(AccessDeniedException e){
                    System.out.println("File not Found");
                }
                askToFollow();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateProperties() {
        while (follow) {
            try {
                lock.lock();
                properties.load(Program.class.getResourceAsStream("/hash.properties"));
                hashParameters = new HashParameters(properties.getProperty("algorithm"), properties.getProperty("salt"));
                lock.unlock();

                Thread.sleep(20 * 1000);
            } catch (Exception e) {
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

            digestBase64 = base64Encoder.encodeToString(digest);
        }

        return digestBase64;
    }

    @Override
    public void run() {

    }
}