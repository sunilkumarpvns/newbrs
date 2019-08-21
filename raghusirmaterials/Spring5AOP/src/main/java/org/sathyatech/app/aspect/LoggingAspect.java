package org.sathyatech.app.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
  //order based on unicode : http://unicode.org/charts/PDF/U0000.pdf
	
	
	@Pointcut("execution(* org.sathyatech.app.*.*.*(..))")
	//@Pointcut("within(org.sathyatech.app.component.*)")
	//@Pointcut("args(..)")
	//@Pointcut("within(org.sathyatech.app.component.*) && args(..)")
	//@Pointcut("within(org.sathyatech.app.component.*) || args(int)")
	//@Pointcut("this(org.sathyatech.app.component.ProductService)")
	public void pointcut1() {}
	
	@Before("pointcut1()")
	public void beforeLog_(JoinPoint jp) {
		System.out.println("I'm from before1 Loggging::"+jp.getSignature());
	}
	
	@Before("pointcut1()")
	public void beforeLog1(JoinPoint jp) {
		System.out.println("I'm from before2 Loggging::"+jp.getSignature());
	}
	
	@After("pointcut1()")
	public void afterLog(JoinPoint jp) {
		System.out.println("I'm from after Loggging::"+jp.getArgs());
	}
	
	@Around("pointcut1()")
	public void aroundLog(ProceedingJoinPoint jp) {
		System.out.println("I'm from 1st Part of Loggging");
		try {
			jp.proceed();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println("I'm from 2nd Part of Loggging");
		
	}
	
	@AfterReturning(pointcut="pointcut1()",returning="ob")
	public void afterReturnLog(Object ob) {
		System.out.println("I'm from after Success::"+ob);
	}
	
	@AfterThrowing(pointcut="pointcut1()",throwing="th")
	public void afterThrowingLog(Throwable th) {
		System.out.println("I'm from after Exception::"+th);
	}
	
}
