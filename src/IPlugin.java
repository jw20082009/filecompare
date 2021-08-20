public interface IPlugin {

    String[] getPluginUsages();
    boolean work(String[] args,StringBuilder log);
}
