package digital.exchange.rest.server.marketdata;

import io.quarkus.runtime.annotations.QuarkusMain;
import io.quarkus.runtime.Quarkus;

@QuarkusMain
public class Main {

    public static void main(final String[] args) throws InterruptedException {
        System.out.println("Starting");

//        System.out.println("Sleeping 30s to attach debugger");
//        Thread.sleep(30*1000);

        System.out.println("Running Quarkus...");
        Quarkus.run();
    }
}
