import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.TextureLoader;
import javax.media.j3d.*;
//import javax.swing.*;
import java.awt.*;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;
import javax.media.j3d.Transform3D;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class PolarArm extends JFrame{

    PolarArm(){
         super("Polar Robot Arm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);


        GraphicsConfiguration config =
           SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas3D = new Canvas3D(config);
        canvas3D.setPreferredSize(new Dimension(960,720));

        add(canvas3D);
        pack();
        setVisible(true);

        BranchGroup scena = utworzScene();
	    scena.compile();
           

        SimpleUniverse simpleU = new SimpleUniverse(canvas3D);
        ViewingPlatform viewingPlatform = simpleU.getViewingPlatform();
        
        OrbitBehavior orbita = new OrbitBehavior(canvas3D,
						OrbitBehavior.REVERSE_ALL);
	BoundingSphere boundso = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
						   100.0);
	orbita.setSchedulingBounds(boundso);
	viewingPlatform.setViewPlatformBehavior(orbita);

        Transform3D przesuniecie_obserwatora = new Transform3D();
        Transform3D rot_obs = new Transform3D();
        rot_obs.rotY((float)(-Math.PI/7));
        przesuniecie_obserwatora.set(new Vector3f(-1.2f,1.5f,2.0f));
        przesuniecie_obserwatora.mul(rot_obs);
        rot_obs.rotX((float)(-Math.PI/6));
        przesuniecie_obserwatora.mul(rot_obs);

        simpleU.getViewingPlatform().getViewPlatformTransform().setTransform(przesuniecie_obserwatora);

        simpleU.addBranchGraph(scena);

    }

    BranchGroup utworzScene()
    {

        int i;

        
        BranchGroup wezel_scena = new BranchGroup();

        Appearance wyglad_ziemia = new Appearance();
        Appearance wyglad_mury   = new Appearance();
        Appearance wyglad_daszek = new Appearance();

        Texture tekstura_nieba = new TextureLoader("obrazki/clouds.gif", null, new Container()).getTexture();
        Appearance wyglad_niebo = new Appearance();
        wyglad_niebo.setTexture(tekstura_nieba);
        wyglad_niebo.setTextureAttributes(new TextureAttributes());
        Sphere niebo = new Sphere(5.0f,
        Primitive.GENERATE_NORMALS_INWARD + Primitive.GENERATE_TEXTURE_COORDS,
        wyglad_niebo);
        wezel_scena.addChild(niebo);
        //Material wmaterial_daszek = new Material(new Color3f(0.0f, 0.1f,0.0f), new Color3f(0.3f,0.0f,0.3f),
          //                                   new Color3f(0.6f, 0.1f, 0.1f), new Color3f(1.0f, 0.5f, 0.5f), 80.0f);
        //wyglad_daszek.setMaterial(wmaterial_daszek);

        TextureLoader loader = new TextureLoader("obrazki/beton.jpg",null);
        ImageComponent2D image = loader.getImage();

        Texture2D trawka = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
                                        image.getWidth(), image.getHeight());

        trawka.setImage(0, image);
        trawka.setBoundaryModeS(Texture.WRAP);
        trawka.setBoundaryModeT(Texture.WRAP);


        loader = new TextureLoader("obrazki/metal.jpg",this);
        image = loader.getImage();

        Texture2D murek = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
                                        image.getWidth(), image.getHeight());
        murek.setImage(0, image);
        murek.setBoundaryModeS(Texture.WRAP);
        murek.setBoundaryModeT(Texture.WRAP);

        

        BoundingSphere bounds = new BoundingSphere();
        AmbientLight lightA = new AmbientLight();
        lightA.setInfluencingBounds(bounds);
        wezel_scena.addChild(lightA);

        DirectionalLight lightD = new DirectionalLight();
        lightD.setInfluencingBounds(bounds);
        lightD.setDirection(new Vector3f(0.0f, 0.0f, -1.0f));
        lightD.setColor(new Color3f(1.0f, 1.0f, 1.0f));
        wezel_scena.addChild(lightD);


        wyglad_ziemia.setTexture(trawka);
        wyglad_mury.setTexture(murek);

        Point3f[]  coords = new Point3f[4];
        for(i = 0; i< 4; i++)
            coords[i] = new Point3f();

        Point2f[]  tex_coords = new Point2f[4];
        for(i = 0; i< 4; i++)
            tex_coords[i] = new Point2f();

        coords[0].y = 0.0f;
        coords[1].y = 0.0f;
        coords[2].y = 0.0f;
        coords[3].y = 0.0f;

        coords[0].x = 3.0f;
        coords[1].x = 3.0f;
        coords[2].x = -3.0f;
        coords[3].x = -3.0f;

        coords[0].z = 3.0f;
        coords[1].z = -3.0f;
        coords[2].z = -3.0f;
        coords[3].z = 3.0f;

        tex_coords[0].x = 0.0f;
        tex_coords[0].y = 0.0f;

        tex_coords[1].x = 10.0f;
        tex_coords[1].y = 0.0f;

        tex_coords[2].x = 0.0f;
        tex_coords[2].y = 10.0f;

        tex_coords[3].x = 10.0f;
        tex_coords[3].y = 0.0f;

        
        //ziemia

        QuadArray qa_ziemia = new QuadArray(4, GeometryArray.COORDINATES|
                GeometryArray.TEXTURE_COORDINATE_2);
        qa_ziemia.setCoordinates(0,coords);

        qa_ziemia.setTextureCoordinates(0, tex_coords);


        Shape3D ziemia = new Shape3D(qa_ziemia);
        ziemia.setAppearance(wyglad_ziemia);

        wezel_scena.addChild(ziemia);
        

        //podstawa
        float podstawa_szer = 0.3f;
        //float podstawa_dl = 0.4f;
        float podstawa_wys = 0.05f;
        
        Appearance wyglad_muru = new Appearance();
        Texture tekstura_muru = new TextureLoader("obrazki/murek.gif", this).getTexture();
        wyglad_muru.setTexture(tekstura_muru);
        Transform3D przesuniecie_muru = new Transform3D();
        przesuniecie_muru.setTranslation(new Vector3f(0.0f, podstawa_wys/2, 0.0f));
        TransformGroup mur_p = new TransformGroup(przesuniecie_muru);
        Box MurModel = new Box(podstawa_szer, podstawa_wys, podstawa_szer, Box.GENERATE_TEXTURE_COORDS, wyglad_muru);
        
 
 
        mur_p.addChild(MurModel);
        
        wezel_scena.addChild(mur_p);
        
        
        //wieza
        float wys_seg1=0.1f;

        
        TransformGroup wieza_p = new TransformGroup();
        Transform3D przesuniecie_wiezy = new Transform3D();
        przesuniecie_wiezy.set(new Vector3f(0.0f,podstawa_wys + wys_seg1/2,0.0f));
        wieza_p.setTransform(przesuniecie_wiezy);

        Cylinder walec = new Cylinder(podstawa_szer-0.05f,wys_seg1,Cylinder.GENERATE_NORMALS| Cylinder.GENERATE_TEXTURE_COORDS, wyglad_mury);
        wieza_p.addChild(walec);
        wezel_scena.addChild(wieza_p);
        
        float wys_seg2 = 0.6f;
        TransformGroup wieza_p2 = new TransformGroup();
        Transform3D przesuniecie_wiezy2 = new Transform3D();
        przesuniecie_wiezy2.set(new Vector3f(0.0f,podstawa_wys + wys_seg1 + wys_seg2/2,0.0f));
        wieza_p2.setTransform(przesuniecie_wiezy2);

        Cylinder walec2 = new Cylinder(podstawa_szer-0.15f,wys_seg2,Cylinder.GENERATE_NORMALS| Cylinder.GENERATE_TEXTURE_COORDS, wyglad_mury);
        wieza_p2.addChild(walec2);
        wezel_scena.addChild(wieza_p2);
        
        float gr_kola = 0.05f;
        float promien_kola = 0.15f;
        TransformGroup wieza_p3 = new TransformGroup();
        Transform3D przesuniecie_wiezy3 = new Transform3D();
        przesuniecie_wiezy3.set(new Vector3f(podstawa_szer/2-0.02f,podstawa_wys + wys_seg1 + wys_seg2+0.05f,0.0f));
        Transform3D  tmp_rot      = new Transform3D();
        tmp_rot.rotZ(Math.PI/2);
        przesuniecie_wiezy3.mul(tmp_rot);
        wieza_p3.setTransform(przesuniecie_wiezy3);

        Cylinder walec3 = new Cylinder(promien_kola,gr_kola,Cylinder.GENERATE_NORMALS| Cylinder.GENERATE_TEXTURE_COORDS, wyglad_mury);
        wieza_p3.addChild(walec3);
        wezel_scena.addChild(wieza_p3);
        TransformGroup wieza_p4 = new TransformGroup();
        Transform3D przesuniecie_wiezy4 = new Transform3D();
        przesuniecie_wiezy4.set(new Vector3f(-podstawa_szer/2+0.02f,podstawa_wys + wys_seg1 + wys_seg2+0.05f,0.0f));
        przesuniecie_wiezy4.mul(tmp_rot);
        wieza_p4.setTransform(przesuniecie_wiezy4);
        Cylinder walec4 = new Cylinder(promien_kola,gr_kola,Cylinder.GENERATE_NORMALS| Cylinder.GENERATE_TEXTURE_COORDS, wyglad_mury);
        wieza_p4.addChild(walec4);
        wezel_scena.addChild(wieza_p4);
        
        Box ramie1 = new Box(podstawa_szer-0.2f, podstawa_wys, podstawa_szer+0.1f, Box.GENERATE_TEXTURE_COORDS, wyglad_mury);
        Transform3D przesuniecie_ram = new Transform3D();
        przesuniecie_ram.setTranslation(new Vector3f(0.0f, podstawa_wys + wys_seg1 + wys_seg2 + promien_kola/2, 0.0f));
        TransformGroup ramie = new TransformGroup(przesuniecie_ram);
        
        //Appearance wyglad_ram = new Appearance();
        //Texture tekstura_muru = new TextureLoader("obrazki/murek.gif", this).getTexture();
        //wyglad_muru.setTexture(tekstura_muru);
 
 
        ramie.addChild(ramie1);
        
        wezel_scena.addChild(ramie);




        return wezel_scena;


    }

    public static void main(String args[]){
      new PolarArm();

   }

}
