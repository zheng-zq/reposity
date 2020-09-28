package com.xuecheng.manage_media_process;

import com.xuecheng.framework.utils.Mp4VideoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-07-12 9:11
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestProcessBuilder {

    //使用processBuilder来调用第三方应用程序
    @Test
    public void test1() throws IOException {
        //1 创建进程构建对象
        ProcessBuilder processBuilder = new ProcessBuilder();
        //2 设置启动的目标程序的命令
//        processBuilder.command("ping", "127.0.0.1");
        processBuilder.command("ipconfig");
        //3 将标准输入流和错误流合并
        processBuilder.redirectErrorStream(true);
        //4 启动一个进程
        Process process = processBuilder.start();
        //5 通过进程启动得到输出结果(字节流信息)
        InputStream inputStream = process.getInputStream();
        //6 将字节流转成字符流
        InputStreamReader reader = new InputStreamReader(inputStream, "gbk");
        //7 创建字符流读取对象
        char[] chars = new char[1024];
        //8 读流输出
        int len = -1;
        while ((len = reader.read(chars)) != -1) {
            String s = new String(chars, 0, len);
            System.out.println(s);
        }
        inputStream.close();
        reader.close();
    }


    //创建进程构建器来调用第三方程序
    @Test
    public void testProcessBuilder() throws IOException {

        //1 创建进程
        ProcessBuilder processBuilder = new ProcessBuilder();
        //2 设置执行的第三方程序(命令)  //processBuilder.command("ping","127.0.0.1");
        processBuilder.command("ipconfig");//processBuilder.command("java","-jar","f:/xc-service-manage-course.jar");
        //3 获得输出信息
        processBuilder.redirectErrorStream(true);
        //4 启动进程  相当于在cmd窗口输入一个start命令
        Process process = processBuilder.start();
        //5 读取输入流(默认读取的是字节流)
        InputStream inputStream = process.getInputStream();
        //6 将字节流转成字符流
        InputStreamReader reader = new InputStreamReader(inputStream, "gbk");
        //字符缓冲区
        char[] chars = new char[1024];
        int len = -1;//当读到末尾就是-1
        while ((len = reader.read(chars)) != -1) {
            String string = new String(chars, 0, len);
            System.out.println(string);
        }
        inputStream.close();
        reader.close();
    }

    //测试使用封装视频转格式工具类将avi转成mp4
    @Test
    public void testProcessMp4() {
        //1 ffmpeg.exe地址
        String ffmpeg_path = "D:\\abc\\aaa\\ffmpeg-1802\\bin\\ffmpeg.exe";
        //2 video_path视频地址
        String video_path = "D:\\lucene.avi";
        //3 mp4_name mp4文件名称
        String mp4_name = "1.mp4";
        //4 mp4folder_path mp4文件目录路径
        String mp4folder_path = "E:/";
        //1+2+3+4大结局
        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path, video_path, mp4_name, mp4folder_path);
        //开始编码,如果成功返回success，否则返回输出的日志
        String result = mp4VideoUtil.generateMp4();
        System.out.println(result);
    }

    //测试使用工具类将avi转成mp4
    @Test
    public void testFFmpeg() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        //定义命令内容
        List<String> command = new ArrayList<>();
        command.add("D:\\abc\\aaa\\ffmpeg-1802\\bin\\ffmpeg.exe");
        command.add("-i");
        command.add("D:\\lucene.avi");
        command.add("-c:v");
        command.add("libx264");
        command.add("-y");//覆盖输出文件
        command.add("-s");
        command.add("1280x720");
        command.add("-pix_fmt");
        command.add("yuv420p");
        command.add("-b:a");
        command.add("63k");
        command.add("-b:v");
        command.add("753k");
        command.add("-r");
        command.add("18");
        command.add("E:\\lucene.mp4");//将标准输入流和错误输入流合并，通过标准输入流读取信息
        processBuilder.redirectErrorStream(true);
        try {
            //启动进程
            Process start = processBuilder.start();
            //获取输入流
            InputStream inputStream = start.getInputStream();
            //转成字符输入流
            InputStreamReader reader = new InputStreamReader(inputStream, "gbk");
            int len = -1;
            char[] c = new char[1024];
            StringBuffer outputString = new StringBuffer();
            //读取进程输入流中的内容
            while ((len = reader.read(c)) != -1) {
                String s = new String(c, 0, len);
                outputString.append(s);
                System.out.print(s);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}