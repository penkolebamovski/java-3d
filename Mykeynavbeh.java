package loaders;
import java.applet.Applet;
import java.awt.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.behaviors.keyboard.*;

import com.sun.j3d.loaders.Scene;
//import org.jdesktop.j3d.loaders.vrml97.VrmlLoader;
//import com.mnstarfire.loaders3d.Loader3DS;
import com.sun.j3d.loaders.objectfile.ObjectFile;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.util.*;

//@SuppressWarnings("serial")
public class Mykeynavbeh extends Applet implements KeyListener  {

 private SimpleUniverse universe = null;
 private Canvas3D canvas = null;
 private TransformGroup viewtrans = null;

 private TransformGroup tg = null;
 private Transform3D t3d = null;
 private Transform3D t3dstep = new Transform3D();
 private Matrix4d matrix = new Matrix4d();

 private MovingCube cube = null;

 public Mykeynavbeh() {
  setLayout(new BorderLayout());
  GraphicsConfiguration config = SimpleUniverse
    .getPreferredConfiguration();

  canvas = new Canvas3D(config);
  add("Center", canvas);
  universe = new SimpleUniverse(canvas);

  BranchGroup scene = createSceneGraph();
  universe.getViewingPlatform().setNominalViewingTransform();

  universe.getViewer().getView().setBackClipDistance(100.0);

  canvas.addKeyListener(this);

  universe.addBranchGraph(scene);
 }

 private BranchGroup createSceneGraph() {
  BranchGroup objRoot = new BranchGroup();

  BoundingSphere bounds = new BoundingSphere(new Point3d(), 10000.0);

  viewtrans = universe.getViewingPlatform().getViewPlatformTransform();

  KeyNavigatorBehavior keyNavBeh = new KeyNavigatorBehavior(viewtrans);
  keyNavBeh.setSchedulingBounds(bounds);
  PlatformGeometry platformGeom = new PlatformGeometry();
  platformGeom.addChild(keyNavBeh);
  universe.getViewingPlatform().setPlatformGeometry(platformGeom);

  objRoot.addChild(createCube());

  Background background = new Background();
  background.setColor(0.9f, 0.9f, 0.9f);
  background.setApplicationBounds(bounds);
  objRoot.addChild(background);

  return objRoot;
 }

 private BranchGroup createCube() {

  BranchGroup objRoot = new BranchGroup();
  tg = new TransformGroup();
  t3d = new Transform3D();

  //tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

  t3d.setTranslation(new Vector3d(0.0, -0.8, -10.0));
  t3d.setScale(0.0001);

  tg.setTransform(t3d);

  cube = new MovingCube("C://Users//user//workspace//3Dex//nb//cube.obj");
  tg.addChild(cube.tg);
  //cube.tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

  tg.addChild(cube);

  objRoot.addChild(tg);
  objRoot.addChild(createLight());

  objRoot.compile();

  return objRoot;

 }

 private Light createLight() {
  DirectionalLight light = new DirectionalLight(true, new Color3f(1.0f,
    1.0f, 1.0f), new Vector3f(-0.3f, 0.2f, -1.0f));

  light.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));

  return light;
 }

 public static void main(String[] args) {
  Mykeynavbeh applet = new Mykeynavbeh();
  Frame frame = new MainFrame(applet, 800, 600);
 }

 public void keyTyped(KeyEvent e) {
  char key = e.getKeyChar();

  if (key == 's') {

   t3dstep.rotY(Math.PI / 32);
   cube.tg.getTransform(cube.t3d);
   cube.t3d.get(matrix);
   cube.t3d.setTranslation(new Vector3d(0.0, 0.0, 0.0));
   cube.t3d.mul(t3dstep);
   cube.t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13,
     matrix.m23));
   cube.tg.setTransform(cube.t3d);

  }

  if (key == 'f') {

   t3dstep.rotY(-Math.PI / 32);
   cube.tg.getTransform(cube.t3d);
   cube.t3d.get(matrix);
   cube.t3d.setTranslation(new Vector3d(0.0, 0.0, 0.0));
   cube.t3d.mul(t3dstep);
   cube.t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13,
     matrix.m23));
   cube.tg.setTransform(cube.t3d);

  }

 }

 public void keyReleased(KeyEvent e) {
 }

 public void keyPressed(KeyEvent e) {
 }

 class MovingCube extends Behavior  {

  public TransformGroup tg = null;
  public Transform3D t3d = null;
  private Transform3D t3dstep = new Transform3D();
  private WakeupOnElapsedFrames wakeFrame = null;

  public MovingCube(String filename) {
   t3d = new Transform3D();

   tg = new TransformGroup(t3d);
   t3d.setScale(1.0);

   t3d.setTranslation(new Vector3d(0.0, 0.0, 0.0));

   //Loader3DS loader = new Loader3DS();
   ObjectFile loader = new ObjectFile();
   Scene s = null;

   try {
    s = loader.load(filename);
   } catch (Exception e) {
    System.err.println(e);
    System.exit(1);
   }

   tg.addChild(s.getSceneGroup());

   tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

   BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0,
     0.0), 1000.0);
   this.setSchedulingBounds(bounds);
  }

  public void initialize() {
   wakeFrame = new WakeupOnElapsedFrames(0);
   wakeupOn(wakeFrame);
  }

 // @SuppressWarnings("rawtypes")
public void processStimulus(Enumeration criteria) {
   t3dstep.set(new Vector3d(0.0, 0.0, 40.0f));
   tg.getTransform(t3d);
   t3d.mul(t3dstep);
   tg.setTransform(t3d);

   wakeupOn(wakeFrame);
  }
 }

}

