package learn.learnProxy.concrete;

import learn.learnProxy.Hello;

public class HelloTarget implements Hello {
    @Override
    public String sayHello(String name) {
        return "Hello "+name;
    }

    @Override
    public String sayHi(String name) {
        return "Hi "+name;
    }

    @Override
    public String sayThankou(String name) {
        return "Thankyou "+name;
    }
}
