package digital.exchange.ingress.rest.server;

import io.quarkus.runtime.annotations.QuarkusMain;
import io.quarkus.runtime.Quarkus;
import digital.exchange.ingress.rest.server.aeron.infra.AeronAgentsRunner;

@QuarkusMain
public class Main {

    public static void main(final String[] args) throws InterruptedException {
        System.out.println("Starting");

//        System.out.println("Sleeping 30s to attach debugger");
//        Thread.sleep(30*1000);

        AeronAgentsRunner aeronAgentsRunner = new AeronAgentsRunner();
        aeronAgentsRunner.start();

        System.out.println("Running Quarkus...");
        Quarkus.run();
    }
}
