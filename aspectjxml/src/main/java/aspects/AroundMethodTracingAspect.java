package aspects;


import org.aspectj.lang.ProceedingJoinPoint;

public class AroundMethodTracingAspect {

    public static Object run(ProceedingJoinPoint tjp) throws Throwable {

        System.out.println("AROUND-before " + tjp.getSignature().getName());

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
