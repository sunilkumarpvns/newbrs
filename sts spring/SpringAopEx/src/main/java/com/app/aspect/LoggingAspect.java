package com.app.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {
@Pointcut("execution(* com.app.service.*.*())")
public void p1()
{
	
}
@Before("p1()")
public void showLogA()
{
	System.out.println("i am from befor advice");
}
}
