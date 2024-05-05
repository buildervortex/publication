package com.getposted.fileHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import com.getposted.enums.Stored;
import com.getposted.logger.Logging;
import com.getposted.system.Sysenv;

public class FileManager {

    private static Logger logger = Logging.getLogger(FileManager.class.getName());

    private static String fileSystemLocation = FileManager.getAbsPath(Sysenv.getEnv("FILESYSTEMLOCATION"));

    public static long storeFile(String storingFileName, InputStream inputStream, Stored type ) throws IOException{
        String pathToStore = fileSystemLocation+type;
        FileManager.createDirectory(pathToStore);

        String absoluteFilePath = pathToStore+storingFileName;
        if(FileManager.isExists(absoluteFilePath)) FileManager.deleteFileOrDirectory(absoluteFilePath);

        long storedSize = -1;

        try{
            storedSize = FileManager.writeToFile(inputStream, absoluteFilePath);
        }
        catch(IOException e){
            logger.severe(String.format("There is an IOException occoured in the com.getposted.fileHandler.FileManager class at storeFile method. The error message is %s. The file name is %s, the file type is %s",e.getMessage(),storingFileName,type));
            throw e;
        }
        return storedSize;
    }

    public static long retriveFile(String retrivingFileName, OutputStream outputStream, Stored type) throws IOException{
        long retrivedSize = 0;

        String absoluteFilePath = fileSystemLocation+type+retrivingFileName;
        if(! FileManager.isExists(absoluteFilePath)) return retrivedSize;

        try{
            FileManager.readFromFile(outputStream, absoluteFilePath);
        }
        catch(IOException e){
            logger.severe(String.format("There is an IOException occoured in the com.getposted.fileHandler.FileManager class at retriveFile method. The error message is %s. The file name is %s, the file type is %s",e.getMessage(),retrivingFileName,type));
            throw e;
        }

        return retrivedSize;
    }

    public static boolean isExists(String path){
        boolean exists = false;
        File file = new File(path);

        if(file.exists()) exists = true;

        return exists;
    }

    public static boolean isADirectory(String path){
        File file = new File(path);
        return file.isDirectory();
    }

    public static boolean createDirectory(String path){
        boolean isCreated = false;
        File file = new File(path);
        if(! isExists(path)){
            isCreated = file.mkdir();
        }
        return isCreated;
    }

    public static boolean deleteFileOrDirectory(String path){
        boolean isDeleted = false;
        File file = new File(path);

        if(isExists(path)){
            isDeleted = file.delete();
        }

        return isDeleted;
    }

    public static long writeToFile(InputStream inputStream, String fileName) throws IOException{
        long copiedBytes = -1;

        Path relativePath = Paths.get(fileName);
        Path normalizedPath = relativePath.normalize();
        Path resolvedPath = normalizedPath.toAbsolutePath();
        
        copiedBytes = Files.copy(inputStream, resolvedPath);
        return copiedBytes;
    }
    public static long readFromFile(OutputStream outputStream, String fileName) throws IOException{
        Path relativePath = Paths.get(fileName);
        Path normalizedPath = relativePath.normalize();
        Path resolvedPath = normalizedPath.toAbsolutePath();

        FileInputStream fileInputStream = new FileInputStream(resolvedPath.toFile());
        byte[] buffer = new byte[4096];
        int bytesRead;
        long readByteCount = 0;

        while((bytesRead = fileInputStream.read(buffer)) != -1){
            outputStream.write(buffer, 0, bytesRead);
            readByteCount += bytesRead;
        }

        fileInputStream.close();
        outputStream.flush();
        return readByteCount;
    }

    private static String getAbsPath(String path){
        String absPath = null;

        Path relativePath = Paths.get(path);
        Path normalizedPath = relativePath.normalize();
        Path resolvedPath = normalizedPath.toAbsolutePath();

        absPath = resolvedPath.toString();

        return absPath;
    }

    public static void reload(){
        FileManager.fileSystemLocation = FileManager.getAbsPath(Sysenv.getEnv("FILESYSTEMLOCATION"));
    }
}
