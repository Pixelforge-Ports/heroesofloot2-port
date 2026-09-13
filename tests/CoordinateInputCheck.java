package org.portmaster.heroesofloot2;
import com.badlogic.gdx.Input;
import java.lang.reflect.Proxy;
public class CoordinateInputCheck {
 public static void main(String[] args) {
  for(int[] size:new int[][]{{640,480},{720,480},{720,720},{1024,768},{1280,720}}) {
   DisplayLayout l=new DisplayLayout();l.resize(size[0],size[1]);
   final int[] cursor={0,0};
   Input physical=(Input)Proxy.newProxyInstance(Input.class.getClassLoader(),new Class<?>[]{Input.class},(o,m,a)->{
    switch(m.getName()) {
     case "setCursorPosition": cursor[0]=(Integer)a[0];cursor[1]=(Integer)a[1];return null;
     case "getX": return cursor[0]; case "getY": return cursor[1];
     case "getDeltaX": case "getDeltaY": return 3;
     case "isKeyPressed": return true;
     default: throw new AssertionError(m.getName());
    }
   });
   Input input=CoordinateInput.wrap(physical,l);
   for(int part:new int[]{1,2,3}) {
    int x=l.gameWidth*part/4,y=l.gameHeight*part/4;
    input.setCursorPosition(x,y);
    if(cursor[0]!=l.cursorX(x)||cursor[1]!=l.cursorY(y)) throw new AssertionError("Wrong physical cursor warp");
    if(Math.abs(input.getX()-x)>2||Math.abs(input.getY()-y)>2) throw new AssertionError("Cursor drift");
    if(input.getX(0)!=input.getX()||input.getY(0)!=input.getY()) throw new AssertionError("Pointer overload");
   }
   if(!input.isKeyPressed(Input.Keys.X)||input.getDeltaX()<=0||input.getDeltaY()<=0) throw new AssertionError("Input delegation");
  }
  System.out.println("MOUSE_COORDINATES_OK: polling and warps at five resolutions");
 }
}
