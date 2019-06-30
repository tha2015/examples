package aspects;


import org.aspectj.lang.JoinPoint;

import java.util.Arrays;

public class BeforeMethodTracingAspect {

    public static void run(JoinPoint tjp) {

        System.out.println("BEFORE " + tjp.getSignature().getName() + "(" + Arrays.toString(tjp.getArgs()) + ")");
    }

}
