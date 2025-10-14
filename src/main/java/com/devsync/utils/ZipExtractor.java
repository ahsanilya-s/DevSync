package com.devsync.utils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtractor {

    public static void extractZip(InputStream zipInputStream, String destDir) throws IOException {
        File destDirectory = new File(destDir);
        if (!destDirectory.exists()) {
            destDirectory.mkdirs();
        }

        try (ZipInputStream zipIn = new ZipInputStream(zipInputStream)) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                File destFile = new File(destDirectory, entry.getName());
                
                // Security check: prevent path traversal
                if (!destFile.getCanonicalPath().startsWith(destDirectory.getCanonicalPath())) {
                    throw new IOException("Entry is outside target directory: " + entry.getName());
                }
                
                if (!entry.isDirectory()) {
                    extractFile(zipIn, destFile.getAbsolutePath());
                } else {
                    destFile.mkdirs();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = zipIn.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
        }
    }
}
