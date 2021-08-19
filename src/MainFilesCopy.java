import utils.FileUtils;
import utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对比两个路径下的所有文件
 * 分析出是否存在同名文件，如果存在一方有，但是另一方不存在的文件，则会同步该文件
 * 同时会列出存在相同的文件
 */
public class MainFilesCopy {
    private final String TAG = "MainFilesCompare";
    private final String[] argsUsage = {
            "       String: 本地项目文件夹\n\r",
            "       String: 远程项目文件夹\n\r",
            "[0]    int: 拷贝方式，0（default）：只拷贝远程路径的到本地，1：既拷贝本地到远程也拷贝远程到本地\n\r"
    };

    public static void main(String[] args) {
        MainFilesCopy filesCopy = new MainFilesCopy();
        if (filesCopy.checkArgs(args)) {
            StringBuilder copyLog = new StringBuilder();
            filesCopy.copyFile(args[0], args[1], args.length >= 3 ? Integer.parseInt(args[2]) : 0,copyLog);
            filesCopy.print(copyLog.toString());
        }
    }

    private boolean checkArgs(String[] args) {
        if (argsUsage.length <= 0) {
            return true;
        }
        if (args == null || args.length < (argsUsage.length - getDefaultArgs())) {
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

    private int getDefaultArgs() {
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

    public boolean copyFile(String localDir, String remoteDir, int mode, StringBuilder copyLog) {
        boolean result = false;
        switch (mode) {
            case 1:
                result = copyFile(localDir, remoteDir,copyLog);
                result = result && copyFile(remoteDir, localDir,copyLog);
                break;
            case 0:
            default:
                result = copyFile(localDir, remoteDir,copyLog);
                break;
        }
        return result;
    }

    public boolean copyFile(String localPath, String remotePath,StringBuilder copyLog) {
        if (StringUtils.isEmpty(localPath)) {
            copyLog.append("copyFile localPath Empty");
            return false;
        }
        if (StringUtils.isEmpty(remotePath)) {
            copyLog.append("copyFile srcDir Empty");
            return false;
        }

        File local = new File(localPath);
        File remote = new File(remotePath);

        if (!remote.exists()) {
            copyLog.append("copyFile remote not exists:" + remotePath+"\n\r");
            return false;
        }

        if (isIgnoreFile(remote.getName())){
            return false;
        }

        if (remote.isDirectory()) {
            if (!local.exists()) {
                local.mkdir();
                copyLog.append("mkdir:"+ remotePath+"\n\r");
            }
            String[] remoteFiles = remote.list();
            for (String remoteName : remoteFiles) {
                copyFile(localPath + File.separator + remoteName, remotePath +File.separator+remoteName,copyLog);
            }
        } else {
            if (!local.exists()) {
                try {
                    FileUtils.copyFile(remote, local);
                    copyLog.append("copy:"+ remotePath+"\n\r");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 需要忽略拷贝的文件在此添加case
     * @param fileName
     * @return
     */
    private boolean isIgnoreFile(String fileName){
        if(!fileName.isEmpty() && fileName.startsWith(".")){
            return true;
        }
        return false;
    }

    private void print(String str) {
        System.out.println(str);
    }
}
