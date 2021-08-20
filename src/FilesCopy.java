import utils.FileUtils;
import utils.MD5Utils;
import utils.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 对比两个路径下的所有文件
 * 分析出是否存在同名文件，如果存在则会对比两文件的hash，不一致时会打印出来
 * 如果存在一方有，但是另一方不存在的文件，则会同步该文件;
 * 同时会列出存在相同的文件
 */
public class FilesCopy implements IPlugin{

    @Override
    public String[] getPluginUsages() {
        return new String[]{
                "       String: 本地项目文件夹\n\r",
                "       String: 远程项目文件夹\n\r",
                "[0]    int: 拷贝方式，0（default）：只拷贝远程路径的到本地，1：既拷贝本地到远程也拷贝远程到本地\n\r"
        };
    }

    public boolean work(String[] args,StringBuilder log){
        return copyFile(args[0], args[1], args.length >= 3 ? Integer.parseInt(args[2]) : 0,log);
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
        if (TextUtils.isEmpty(localPath)) {
            copyLog.append("copyFile localPath Empty");
            return false;
        }
        if (TextUtils.isEmpty(remotePath)) {
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
            } else {
                try {
                    String hashLocal = MD5Utils.md5HashCode32(remote);
                    String hashRemote = MD5Utils.md5HashCode32(local);
                    if(!TextUtils.equals(hashLocal,hashRemote)){
                        copyLog.append("hashDiff:"+remotePath+"\n\r");
                    }
                } catch (FileNotFoundException e) {
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
        if(TextUtils.isEmpty(fileName) || fileName.startsWith(".")){
            return true;
        }
        return false;
    }
}
