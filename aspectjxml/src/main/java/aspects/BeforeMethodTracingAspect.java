package aspects;


import org.aspectj.lang.JoinPoint;

public class BeforeMethodTracingAspect {

    public static void run(JoinPoint tjp) {
        System.out.println("BEFORE " + tjp.getSignature().getName());
    }

}
