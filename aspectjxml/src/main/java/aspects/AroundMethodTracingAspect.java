package aspects;


import org.aspectj.lang.ProceedingJoinPoint;

import java.util.Arrays;

public class AroundMethodTracingAspect {

    public static Object run(ProceedingJoinPoint tjp) throws Throwable {

        System.out.println("AROUND-before " + tjp.getSignature().getName() + "(" + Arrays.toString(tjp.getArgs()) + ")");

        Object result;

        try {
            result = tjp.proceed();

        } catch (Throwable t) {
            System.out.println("AROUND-exception " + tjp.getSignature().getName() + " EX: " + t);
            throw t;
        }

        System.out.println("AROUND-return " + tjp.getSignature().getName() + " : " + result);

        return result;

    }

}
