package digital.exchange.me;


import digital.exchange.me.infra.AppManager;

/**
 * Sample cluster application
 */
class ClusterApp{
    /**
     * The main method.
     * @param args command line args
     */
    public static void main(final String[] args) throws Exception {
        AppManager appManager = new AppManager();

        appManager.start();
    }
}
