import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 定义了入参检测与用法打印
 */
public class Main {

    public static void main(String[] args) {
        Main entrance = new Main();
        IPlugin plugin = new FilesCopy();
        if (entrance.checkArgs(args, plugin.getPluginUsages())) {
            StringBuilder log = new StringBuilder();
            plugin.work(args, log);
            entrance.print(log.toString());
        }
    }

    private boolean checkArgs(String[] args, String[] argsUsage) {
        if (argsUsage.length <= 0) {
            return true;
        }
        if (args == null || args.length < (argsUsage.length - getDefaultArgs(argsUsage))) {
            StringBuilder inputArgs = new StringBuilder("inputArgs:");
            StringBuilder sbUsage = new StringBuilder("Usage:\n\r");
            for (int i = 0; i < argsUsage.length; i++) {
                if (i < args.length) {
                    inputArgs.append(args + " ");
                }
                sbUsage.append(argsUsage[i]);
            }
            System.out.println(inputArgs.append("\n\r").toString() + sbUsage.toString());
            return false;
        }
        return true;
    }

    private int getDefaultArgs(String[] argsUsage) {
        if (argsUsage.length <= 0) {
            return 0;
        }
        int defaultArgsNum = 0;
        Pattern pattern = Pattern.compile("\\[.*?\\]");
        for (String desc : argsUsage) {
            Matcher matcher = pattern.matcher(desc);
            if (matcher.find()) {
                defaultArgsNum++;
            }
        }
        return defaultArgsNum;
    }

    public static void print(String str) {
        System.out.println(str);
    }
}
