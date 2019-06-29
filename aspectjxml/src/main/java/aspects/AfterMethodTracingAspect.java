package aspects;


import org.aspectj.lang.JoinPoint;

public class AfterMethodTracingAspect {

    public static void run(JoinPoint tjp) {
        System.out.println("AFTER " + tjp.getSignature().getName());
    }

}
