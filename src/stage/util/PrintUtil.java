package stage.util;

/**
 * @author ZQJ
 * @date 3/4/2020
 * TIME: 8:12 PM
 */
public class PrintUtil {
    /**
     * print out exception prompt and exit system
     *
     * @param e      exception
     * @param prompt prompt
     */
    public static void printExceptionAndExit(Exception e, String prompt) {
        System.err.println(prompt);
        System.err.println(e.toString());
        System.exit(0);
    }
}
