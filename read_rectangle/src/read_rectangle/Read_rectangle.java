/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 在CAD中在单独图层画矩形以切分房间，这里读取该图层中的矩形。
 */
package read_rectangle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hello
 */
public class Read_rectangle {
    
    public static double tminx=1000000000000000000.0;
    public static double tminy=1000000000000000000.0;
    public static double tmaxx=-100000000000000000.0;
    public static double tmaxy=-100000000000000000.0;
    
    public static List<Rect> allrec=new ArrayList();//所有的矩形集合
    
    public static Rect readLWpolyline_rec(BufferedReader bfr,String tuc) throws IOException, InstantiationException, IllegalAccessException{
             
             double x=0,y=0;
             String s1=null,s2=null;
             //通过while-if判断那样的方式读取不太可行，因为每个图层的dxf表示没有结束标志。
             
             while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals("  8")==false);  //图层
             String tc=new String(s2);
             if(tc.equals(tuc)==false) return null;//图层不合要求，返回null
             
             while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 62")==false && s1.equals(" 90")==false);  //颜色或顶点数
             boolean cflag=false;//是否有颜色标志
             int color=0;//颜色
             int num=0;//顶点数
             if(s1.equals(" 62")==true)//有颜色标志
             {
               cflag=true;
               color=Integer.parseInt(s2.trim());
               while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 90")==false);//顶点数
               num=Integer.parseInt(s2.trim());
             }
             else
             {
               num=Integer.parseInt(s2.trim());
             }
             
             while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 70")==false);  //闭合性
             int cls=Integer.parseInt(s2.trim());   //吐槽：尼玛坑爹啊，自己通过画图对比才知道这个标志多段线是否闭合啊（帮助文档里竟然写关闭，不是闭合。。。）
             if(cls!=1)
             {
               System.out.println("没有闭合？？！！");
               return null;
             }
             
             double minx=tminx;
             double maxx=tmaxx;
             double miny=tminy;
             double maxy=tmaxy;/*当前图元的四个极值*/
             
             for(int i=0;i<num;++i){
               while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 10")==false);  //坐标
               x=Double.parseDouble(s2);
               while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 20")==false);  //坐标
               y=Double.parseDouble(s2);
               minx=minx<x?minx:x;
               maxx=maxx>x?maxx:x;
               miny=miny<y?miny:y;
               maxy=maxy>y?maxy:y;
             }
             
             int tp=0;//类型
             if(cflag==true)
             {
               if(color==5) tp=1;
               else if(color==3) tp=2;
               else if(color==6) tp=3;
             }
             /*根据test-color-num.dxf可知，对于62字段，洋红是6，蓝色是5，绿色是3，黄色是rec图层本来的颜色，所以没有标记62字段。
              * rec图层，默认黄色表示房间0，蓝色5表示走廊1，绿色3表示楼梯2，洋红6表示电梯3.*/
             
             Rect rct=new Rect(minx,miny,maxx,maxy,tp);
             return rct;
    }
    
    public static void readRectangle(BufferedReader bfr, String tuc) throws IOException, InstantiationException, IllegalAccessException
    {
      String s1=null,s2=null;
      int flag=0;//指示所读是否为ENTITIES段，1为是，0为否。
      while((s1 = bfr.readLine())!=null && (s2 = bfr.readLine())!=null){ //&& s2.equals("ENDSEC")==false){
         if(s1.equals("  0") && s2.equals("SECTION")){
            s1=bfr.readLine();
            s2=bfr.readLine();
            if(s1.equals("  2") && s2.equals("ENTITIES")) { flag=1; System.out.println("begin in Entities");continue; }   //开始读ENTITIES段
         }
         if(flag==0) continue;    //所读内容不是ENTITIES段
         if(flag==1 && s1.equals("  0") && s2.equals("ENDSEC")) { flag=0; System.out.println("end of Entities"); break; }  //读完ENTITIES段
         if(s1.equals("  0")){//不判断s1直接判断s2是不对的。
           if(s2.equals("LWPOLYLINE")){//多段线
             Rect rec=readLWpolyline_rec(bfr,tuc);
             if(rec!=null){
               allrec.add(rec);
             }
          }//ifs2
         }//ifs1
      }//while  
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException {
        /*String fileName="E:\\cad-xx\\test-color-num.dxf";
        String tuc="rec";*/
        
        String fileName="E:\\cad-xx\\hospital-fengceng\\rec\\hospital_floor3-8b.dxf";
        String tuc="rec";
        
        File file=new File(fileName);
        FileReader fr=new FileReader(file);
        BufferedReader bfr= new BufferedReader(fr);
        readRectangle(bfr,tuc);
    }
    
}
