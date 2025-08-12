package ludo.mentis.aciem.ecm.dev;

public interface DataLoaderCommand {

    int getOrder();
    String getName();
    boolean canItRun();
    int run();
}
