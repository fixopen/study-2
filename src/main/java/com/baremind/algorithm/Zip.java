package com.baremind.algorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by fixopen on 3/6/15.
 */
public class Zip {
    public void uncompress(File zipFile, File folder) {
        try {
            FileInputStream fileInputStream = new FileInputStream(zipFile);
            ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
            ZipEntry zipEntry = null;
            byte[] buffer = new byte[256];
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                File fileItem = new File(folder.getAbsolutePath() + "/" + zipEntry.getName());
                File fileItemFolder = new File(fileItem.getParentFile().getPath());
                if (zipEntry.isDirectory()) {
                    if (!fileItem.exists()) {
                        boolean success = fileItem.mkdirs();
                        if (success) {
                            //do nothing
                        } else {
                            //do nothing
                        }
                    }
                    zipInputStream.closeEntry();
                } else {
                    if (!fileItemFolder.exists()) {
                        fileItemFolder.mkdirs();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(fileItem);
                    int readLength = 0;
                    while ((readLength = zipInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, readLength);
                    }
                    zipInputStream.closeEntry();
                    fileOutputStream.close();
                }
            }
            zipInputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            System.err.println("Extract error:" + e.getMessage());
        }
    }

    public void compress(File folder, File zipFile) {
        File[] files = zipFile.listFiles();
        String[] paths = new String[zipFile.list().length];
        if (files != null) {
            int i = 0;
            for (File file : files) {
                paths[i++] = file.getAbsolutePath();
            }
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(folder.getAbsolutePath());
            ZipOutputStream zipOutputStream = null;
            try {
                zipOutputStream = new ZipOutputStream(out);
                for (String path : paths) {
                    if (path.equals("")) {
                        continue;
                    }
                    File file = new File(path);
                    if (file.exists()) {
                        if (file.isDirectory()) {
                            zipDirectory(zipOutputStream, file.getPath(), file.getName() + File.separator);
                        } else {
                            zipFile(zipOutputStream, file.getPath(), "");
                        }
                    }
                }
                zipOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (zipOutputStream != null) {
                    try {
                        zipOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void zipDirectory(ZipOutputStream zos, String dirName, String basePath) throws Exception {
        File dir = new File(dirName);
        if (dir.exists()) {
            File files[] = dir.listFiles();
            if (files != null) {
                if (files.length > 0) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            zipDirectory(zos, file.getPath(), basePath
                                + file.getName().substring(file.getName().lastIndexOf(File.separator) + 1)
                                + File.separator);
                        } else {
                            zipFile(zos, file.getPath(), basePath);
                        }
                    }
                } else {
                    ZipEntry ze = new ZipEntry(basePath);
                    zos.putNextEntry(ze);
                }
            }
        }
    }

    private void zipFile(ZipOutputStream zos, String filename, String basePath) throws Exception {
        File file = new File(filename);
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(filename);
            ZipEntry ze = new ZipEntry(basePath + file.getName());
            zos.putNextEntry(ze);
            byte[] buffer = new byte[8192];
            int count = 0;
            while ((count = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, count);
            }
            fis.close();
        }
    }

//    static {
//        System.loadLibrary("crypto");
//    }

    private native int Compress(String folderName, String zipFileName);

    private native int Uncompress(String zipFileName, String folderName);
}
