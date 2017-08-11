package learn.learnProxy.testPak;

import learn.learnProxy.Hello;
import learn.learnProxy.HelloUppercase;
import learn.learnProxy.UppercaseHandler;
import learn.learnProxy.concrete.HelloTarget;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LearnTest {
    @Test
    public void simpleProxy() {
        Hello hello = new HelloTarget();
        assertThat(hello.sayHello("toby"), is("Hello toby"));
        assertThat(hello.sayHi("toby"), is("Hi toby"));
        assertThat(hello.sayThankou("toby"), is("Thankyou toby"));
    }

    @Test
    public void proxiedHello() {
        Hello proxied = new HelloUppercase(new HelloTarget());
        assertThat(proxied.sayHello("toby"), is("HELLO TOBY"));
        assertThat(proxied.sayHi("toby"), is("HI TOBY"));
        assertThat(proxied.sayThankou("toby"), is("THANKYOU TOBY"));
    }

    @Test
    public void proxiedHelloWithDynamicProxy() {
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{Hello.class},
                new UppercaseHandler(new HelloTarget())
        );
        assertThat(proxiedHello.sayHello("toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankou("toby"), is("THANKYOU TOBY"));

    }

    static class UppercaseAdvice implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {
            String ret = (String) methodInvocation.proceed();
            return ret.toUpperCase();
        }
    }

    @Test
    public void pointcutAdvisor() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*"); // sayH로 시작하는 메서드면 프록시가 걸린다.

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxiedHello = (Hello) pfBean.getObject();

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankou("Toby"), is("Thankyou Toby"));

    }


}

