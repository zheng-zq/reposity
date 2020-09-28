package com.xuecheng.test.fastdfs;

public class Test {
    public static void main(String[] args) {
        // int [] arr=new int[4]{3,4,5,6};
        // float f=5+5.5;
        // String s= "join"+ "was"+ "here";
        int i = 10,j = 25,x = 30;
        switch (j - i){
            case 15:x++;
            case 16:x+=2;
            case 17:x+=3;
            default:--x;
        }
        System.out.println(x);
    }
}
