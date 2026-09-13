package org.portmaster.heroesofloot2;
import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
public class GameplaySmoke extends Main {
 boolean resumeSeen; int frame,play,previous=-1; long started=System.nanoTime();
 public static void main(String[] args) throws Exception {new Lwjgl3Application(new GameplaySmoke(),configuration());}
 void key(int code,boolean down) {if(down)Gdx.input.getInputProcessor().keyDown(code);else Gdx.input.getInputProcessor().keyUp(code);}
 Object read(String name)throws Exception {java.lang.reflect.Field f=com.orangepixel.dungeon2.myCanvas.class.getDeclaredField(name);f.setAccessible(true);return f.get(java.lang.reflect.Modifier.isStatic(f.getModifiers())?null:this);}
 boolean existingSave() throws Exception {Object profile=read("activePlayer");return profile!=null && ((com.orangepixel.dungeon2.PlayerProfile)profile).hasSavedGamefile(PROFILEID,false);}
 void saveReadback() throws Exception {
    com.orangepixel.dungeon2.PlayerProfile profile=(com.orangepixel.dungeon2.PlayerProfile)read("activePlayer");
    com.orangepixel.dungeon2.World world=(com.orangepixel.dungeon2.World)read("myWorld");
    com.orangepixel.dungeon2.Player player=(com.orangepixel.dungeon2.Player)read("myPlayer");
    profile.saveGamefile(world,player,PROFILEID);
    int life=player.life;player.life=-99;
    profile.loadGamefile(world,player,PROFILEID);
    if(!profile.hasSavedGamefile(PROFILEID,false) || player.life!=life)throw new IllegalStateException("Save readback failed");
    System.out.println("SAVE_READBACK_OK");
 }
 @Override public void render() {
  frame++;
  if(frame==240 && Boolean.getBoolean("heroesofloot2.expectResume")){try{resumeSeen=existingSave();if(!resumeSeen)throw new IllegalStateException("Save missing on restart");System.out.println("RESUME_DATA_FOUND");}catch(Exception e){throw new IllegalStateException(e);}}
  if(frame>240 && GameState!=6){
   if(frame%90==0){key(Input.Keys.X,true);key(Input.Keys.ENTER,true);}
   if(frame%90==3){key(Input.Keys.X,false);key(Input.Keys.ENTER,false);}
  }
  if(GameState==6){
   play++;
   if(play%120==1){key(Input.Keys.RIGHT,true);key(Input.Keys.X,true);}
   if(play%120==30){key(Input.Keys.RIGHT,false);key(Input.Keys.X,false);}
  }
  super.render();
  java.nio.IntBuffer v=com.badlogic.gdx.utils.BufferUtils.newIntBuffer(4);Gdx.gl20.glGetIntegerv(com.badlogic.gdx.graphics.GL20.GL_VIEWPORT,v);
  if(v.get(0)!=layout.x || v.get(1)!=layout.y || v.get(2)!=layout.width || v.get(3)!=layout.height)throw new IllegalStateException("Viewport lost");
  if(physicalGraphics.getWidth()!=Integer.getInteger("heroesofloot2.width",640) || physicalGraphics.getHeight()!=Integer.getInteger("heroesofloot2.height",480))throw new IllegalStateException("Physical size changed");
  if(GameState!=previous){System.out.println("STATE "+frame+" "+GameState);previous=GameState;}
  if(frame==240 || frame%600==0)capture(System.getProperty("heroesofloot2.output")+"/frame"+frame+".png");
  if(play>=360 || frame>=5400){
   capture(System.getProperty("heroesofloot2.output")+"/final.png");
   if(play<360)throw new IllegalStateException("Gameplay not reached long enough: "+play);
   double seconds=(System.nanoTime()-started)/1e9;if(seconds<(frame-2)/60.0)throw new IllegalStateException("Frame cap exceeded");
   if(Boolean.getBoolean("heroesofloot2.testSave")){try{saveReadback();}catch(Exception e){throw new IllegalStateException(e);}}
   System.out.println("GAMEPLAY_OK frames="+frame+" play="+play+" seconds="+seconds);Gdx.app.exit();
  }
 }
}
