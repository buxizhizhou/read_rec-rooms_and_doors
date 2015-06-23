/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package read_rectangle;

/**
 *
 * @author hello
 */
class Guanxi
{//连接关系
  Point pt;//门点
  int frec,srec;//连接的两个矩形
  
  Guanxi(Point p,int a,int b)
  {
    pt=new Point(p);
    frec=a;
    srec=b;
  }
}

class Point
{
  double x,y;
  
  Point(double a,double b)
  {
    x=a;
    y=b;
  }
  
  Point(Point p)
  {
    x=p.x;
    y=p.y;
  }
}

class Line
{
  Point qd,zd;
  
  Line(double x1,double y1,double x2,double y2)
  {//把x坐标小的作为起点
    if(x1<=x2)
    {
      qd=new Point(x1,y1);
      zd=new Point(x2,y2);
    }
    else
    {
      qd=new Point(x2,y2);
      zd=new Point(x1,y1);
    }
  }
}

class Rect
{
  double xz,yz,xy,yy;//左下角和右上角的x、y值
  int type;
  
  Rect(double x1,double y1,double x2,double y2,int t)
  {
    xz=x1;
    yz=y1;
    xy=x2;
    yy=y2;
    type=t;
  }
}

public class leixing {
    
}
