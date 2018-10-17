package max.dirscan.scan;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class AlphabeticalOrderValidator {


    public static Boolean validate(String file) {
        Path path = Paths.get(file);
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String left = br.readLine();
            String right;
            while ((right = br.readLine()) != null ) {
                if (isLess(left, right)) {
                    left = right;
                } else {
                    System.out.println(" LEFT == "+left);
                    System.out.println(" RIGHT == "+right);
                    throw new SortingException("The file " + path.toFile().getAbsolutePath() + " isn't sorted!");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("The file " + path.toFile().getAbsolutePath() + " is sorted!");
        return Boolean.TRUE;
    }

    private static boolean isLess(String left, String right) {
        int less = left.toLowerCase().compareTo(right.toLowerCase());
        if(less >= 0) {
            return false;
        } else {
            return true;
        }
    }

}
