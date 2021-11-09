/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 */
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class OSMTestSuiteRunner {
    public static void main(String[] args) {
        Result result= JUnitCore.runClasses(OSMClientTestSuite.class);
        for (Failure failure: result.getFailures()){
            System.out.println(failure.toString());
        }
        System.out.println(result.wasSuccessful());
    }
}
