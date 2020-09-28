package com.xuecheng.manage_media;

import com.sun.scenario.effect.Merge;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class TestFile {

    //测试文件分块
    @Test
    public void testChunk() throws IOException {

        //源文件目录
        File sourceFile = new File("D:/lucene.avi");
        //块文件目录
        String chunkFileFolder = "D:/test/";
        //定义分块的大小
        long chunkFileSize = 1 * 1024 * 1024;
        //定义分的块数
        long chunkFileNumber = (long) Math.ceil(sourceFile.length() * 1.0 / chunkFileSize);
        //创建读文件的对象
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");
        //缓冲区
        byte[] b = new byte[1024];
        for (int i = 0; i < chunkFileNumber; i++) {
            //块文件
            File chunkFile = new File(chunkFileFolder + i);
            //创建向块文件写入的对象
            RandomAccessFile raf_write = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len = raf_read.read(b)) != -1) {
                raf_write.write(b, 0, len);
                if (chunkFile.length() >= chunkFileSize) {
                    //如果块文件的大小超过一兆,开始写下一块
                    break;
                }
            }
            raf_write.close();
        }
        raf_read.close();
    }

    //测试文件合并  错
    public void testMerge() throws IOException {
        //1 块文件目录
        String chunkFileFolderPath = "D:\\test\\";
        //2 块文件目录对象
        File chunkFileFolder = new File(chunkFileFolderPath);
        //3 块文件列表
        File[] files = chunkFileFolder.listFiles();
        //  排序按文件名称(放进list集合中)
        List<File> filesList = Arrays.asList(files);
        Collections.sort(filesList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName()) > Integer.parseInt(o2.getName())) {
                    return 1;//升序
                }
                return -1;//降序
            }
        });
        //4 合并文件
        File mergeFile = new File("D:\\merge.avi");
        //5 创建新文件
        boolean newFile = mergeFile.createNewFile();
        //6 创建写的对象
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        byte[] bytes = new byte[1024];
        for (File chunkFile : filesList) {
            //创建一个读块的对象
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "r");
            int len = -1;
            while ((len = raf_read.read(bytes)) != -1) {
                raf_write.write(bytes, 0, len);
            }
            raf_read.close();
        }
        raf_write.close();
    }

    //测试文件合并方法
    @Test
    public void testMerge1() throws IOException {
        //块文件目录
        File chunkFolder = new File("D:/test/");
        //合并文件
        File mergeFile = new File("F:/lucene1.mp4");
        if (mergeFile.exists()) {
            mergeFile.delete();
        }
        //创建新的合并文件
        mergeFile.createNewFile();
        //用于写文件
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        //指针指向文件顶端
        raf_write.seek(0);
        //缓冲区
        byte[] b = new byte[1024];
        //分块列表
        File[] fileArray = chunkFolder.listFiles();
        // 转成集合，便于排序
        List<File> fileList = new ArrayList<File>(Arrays.asList(fileArray));
        // 从小到大排序
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName()) < Integer.parseInt(o2.getName())) {
                    return -1;
                }
                return 1;
            }
        });
        //合并文件
        for (File chunkFile : fileList) {
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len = raf_read.read(b)) != -1) {
                raf_write.write(b, 0, len);
            }
            raf_read.close();
        }
        raf_write.close();
    }
}
