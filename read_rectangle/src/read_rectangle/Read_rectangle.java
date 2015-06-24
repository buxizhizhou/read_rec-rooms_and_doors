/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 在CAD中在单独图层画矩形以切分房间，这里读取该图层中的矩形。
 * 加入了git版本控制
 */
package read_rectangle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    public static List<Line> alllns=new ArrayList();//所有门线的集合
    public static List<Guanxi> allgx=new ArrayList();//所有“点、矩形”对的集合
    
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
    
    public static Line readLine_dr(BufferedReader bfr,String dtuc) throws InstantiationException, IllegalAccessException, IOException{
           //这里假设坐标是按照xyz的顺序给出的，若要与顺序无关，可以把下面程序改成在while循环里一直读，然后循环里做判断。
           double x1=0,x2=0,y1=0,y2=0,z1=0,z2=0;
           String s1=null,s2=null;
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals("  8")==false);  //图层
           String tc=new String(s2);
           if(tc.equals(dtuc)==false) return null;//如果不是门图层的，则返回null
           //获取点坐标
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 10")==false);  //x坐标
           x1=Double.parseDouble(s2);
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 20")==false);
           y1=Double.valueOf(s2).doubleValue();
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 30")==false);
           z1=Double.valueOf(s2).doubleValue();
           
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 11")==false);  //x坐标
           x2=Double.valueOf(s2).doubleValue();   
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 21")==false);
           y2=Double.valueOf(s2).doubleValue();
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 31")==false);
           z2=Double.valueOf(s2).doubleValue();
           
           Line l1=new Line(x1,y1,x2,y2);
           
           return l1;
    }
    
    public static void readDoor(BufferedReader bfr, String dtuc) throws IOException, InstantiationException, IllegalAccessException
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
           if(s2.equals("LINE")){//线段
             Line ln=readLine_dr(bfr,dtuc);
             if(ln!=null){
               alllns.add(ln);
             }
          }//ifs2
         }//ifs1
      }//while  
    }
    
    public static void compute_pairs(boolean fg)
    {
 
      for(int i=0;i<alllns.size();++i)
      {//对每一条线
        Line ln=alllns.get(i);
        Point qd=ln.qd;
        Point zd=ln.zd;
        int qreck=find_rec(qd);
        int zreck=find_rec(zd);
        if(qreck<0 && zreck<0)
        {
          System.out.println("PLINE "+qd.x+","+qd.y+" "+zd.x+","+zd.y+" ");
          System.out.println("门连接的两个房间都是-2！！！");
        }
        int reck=qreck>=0?qreck:zreck;//reck标记的是qreck和zreck中不为-1的一个。
        Point jd=compute_jd(qd,zd,reck,qreck);
        Guanxi gx=new Guanxi(jd,qreck,zreck);
        allgx.add(gx);
        
        //if(qreck>=0) recflag[qreck]=1;
        //if(zreck>=0) recflag[zreck]=1;
      }
    }
    
    public static Point compute_jd(Point qd,Point zd,int reck,int qreck)
    {
      double px=0,py=0;
      Rect rec=allrec.get(reck);
      if(reck==qreck)//传进来的非-1房间是起点房间
      {
        if(zd.x>=rec.xz && zd.x<=rec.xy)//两个房间是上下关系
        {
          px=(qd.x+zd.x)/2;
          if(zd.y<=rec.yz) py=rec.yz;//终点矩形在起点矩形下方
          else py=rec.yy;//终点矩形在起点矩形上方
        }
        else//左右关系
        {
         py=(qd.y+zd.y)/2;
         if(zd.x<=rec.xz) px=rec.xz;//终点矩形在起点矩形的左边
         else px=rec.xy;//终点矩形在起点矩形右边
        }
      }
      else//传的是终点房间
      {
        if(qd.x>=rec.xz && qd.x<=rec.xy)//两个房间是上下关系
        {
          px=(qd.x+zd.x)/2;
          if(qd.y<=rec.yz) py=rec.yz;//终点矩形在起点矩形下方
          else py=rec.yy;//终点矩形在起点矩形上方
        }
        else//左右关系
        {
         py=(qd.y+zd.y)/2;
         if(qd.x<=rec.xz) px=rec.xz;//终点矩形在起点矩形的左边
         else px=rec.xy;//终点矩形在起点矩形右边
        }
      }
      
      return new Point(px,py);
    }
    
    public static int find_rec(Point pt)//由一个点，去找包含它的矩形，返回矩形在allrec中的下标;找不到就返回-2。（后面在保存到文件里时有个+1，-2即成为-1）
    {
      boolean flag=false;//是否找到矩形
      int k=-2;
      
      for(int i=0;i<allrec.size();++i)
      {
        Rect rec=allrec.get(i);
        if(pt.x>=rec.xz && pt.x<=rec.xy)
        {
          if(pt.y>=rec.yz && pt.y<=rec.yy) { flag=true; k=i;break;}//找到了
        }
      }
      
      if(flag==false)
      {
        System.out.println("门的点没找到矩形！！！");
        System.out.println(pt.x+","+pt.y);
      }
      return k;
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException {
        /*String fileName="E:\\cad-xx\\test-color-num.dxf";
        String tuc="rec";*/
        
        /*String fileName="E:\\cad-xx\\hospital-fengceng\\rec\\hospital_floor3-8b.dxf";
        String rectuc="rec";
        String doortuc="doorpoint";*/
        
        /*String fileName="E:\\cad-xx\\hospital-fengceng\\rec\\second-rec-dr.dxf";
        String rectuc="rec";
        String doortuc="doorpoint";*/
        
        String fileName="E:\\cad-xx\\hospital-fengceng\\rec\\ground-rec-dr.dxf";
        String rectuc="rec";
        String doortuc="doorpoint";
        
        File file=new File(fileName);
        FileReader fr=new FileReader(file);
        BufferedReader bfr= new BufferedReader(fr);
        readRectangle(bfr,rectuc);//读得所有的矩形到allrec
        bfr.close();
        fr.close();
        
        //为保持模块独立，重新读取该文件，而不是和上面矩形一起读
        File file2=new File(fileName);
        FileReader fr2=new FileReader(file2);
        BufferedReader bfr2= new BufferedReader(fr2);
        readDoor(bfr2,doortuc);//读得所有的门线到alllns
        bfr2.close();
        fr2.close();
        
        final double pc=10;
        Collections.sort(allrec, new Comparator<Rect>() {
            public int compare(Rect arg0, Rect arg1) {
                if(Math.abs(arg0.yy-arg1.yy)<pc){
                  if(arg0.yy<arg1.yy) return -1;
                  else return 1;
                }
                //否则，说明两者yy相同
                if(arg0.xy<arg1.xy) return -1;
                else return 1;
            }
          });
        
        boolean flag=false;//标记是否是第一层，第一层的话，如果门没有连接两个房间，则另一个连接的是标号为-1的室外
        //flag=true;
        compute_pairs(flag);
        
        save_to_file();
        
    }//main
    
    public static void save_to_file() throws IOException
    {
      /*File file=new File("hospital2-xx_rects.txt");   
      File file2=new File("hospital2-xx_relations.txt");*/  
      
      File file=new File("hospital1-xx_rects.txt");   
      File file2=new File("hospital1-xx_relations.txt"); 
        
      //保存矩形编号和坐标
      FileWriter fw=new FileWriter(file);
      BufferedWriter bfw=new BufferedWriter(fw);
      for(int i=0;i<allrec.size();++i)
      {
        Rect rec=allrec.get(i);
        bfw.write((i+1)+","+rec.xz+","+rec.yz+","+rec.xy+","+rec.yy+","+rec.type);
        bfw.newLine();
      }
      bfw.close();
      fw.close();
      
      //保存连接关系
      FileWriter fw2=new FileWriter(file2);
      BufferedWriter bfw2=new BufferedWriter(fw2);
      for(int i=0;i<allgx.size();++i)
      {
        Guanxi gx=allgx.get(i);
        Point jd=gx.pt;
        bfw2.write((i+1)+","+jd.x+","+jd.y+","+(gx.frec+1)+","+(gx.srec+1));
        bfw2.newLine();
      }
      bfw2.close();
      fw2.close();
    }
    
}
