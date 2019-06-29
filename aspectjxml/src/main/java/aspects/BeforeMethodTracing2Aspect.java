package aspects;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public abstract class BeforeMethodTracing2Aspect {

    @Pointcut
    public abstract void tracingScope();


    @Before("tracingScope()")
    public void run(JoinPoint tjp) {
        System.out.println("BEFORE2 " + tjp.getSignature().getName());
    }

}
