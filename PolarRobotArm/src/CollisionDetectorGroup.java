import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.TextureLoader;
import javax.media.j3d.*;
import java.awt.*;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;
import javax.media.j3d.Transform3D;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;


public class PolarArm extends JFrame implements ActionListener, KeyListener {

    //publiczne zmienne
    BranchGroup wezel_scena = new BranchGroup();
    TransformGroup obrot_animacja = new TransformGroup();
    BoundingSphere boundso;
    SimpleUniverse simpleU;
    OrbitBehavior orbita;

    JButton reset_kamery = new JButton();
    JButton nagrywaj = new JButton();
    JButton muzykaOnOff = new JButton();
    JButton odtworz_nagranie = new JButton();
    JButton ustawKoordynaty = new JButton();
    JButton reset_ustawienia = new JButton();

    JTextArea textObrot1 = new JTextArea();
    JTextArea textObrot2 = new JTextArea();
    JTextArea textWysuniecie = new JTextArea();
    
    Transform3D przesuniecie_seg2 = new Transform3D();
    Transform3D przesuniecie_seg = new Transform3D();
    Transform3D przesuniecie_seg3 = new Transform3D();
    Transform3D przesuniecie_seg4 = new Transform3D();
    Transform3D przesuniecie_ram = new Transform3D();
    Transform3D przesuniecie_chwytaka = new Transform3D();
    Transform3D kulka_trans3D = new Transform3D();
    Transform3D przesuniecie_seg5 = new Transform3D();
    Transform3D przesuniecie_seg6 = new Transform3D();
    Transform3D przesuniecie_ramie2 = new Transform3D();
    
    TransformGroup segment = new TransformGroup();
    TransformGroup segment2 = new TransformGroup();
    TransformGroup segment3 = new TransformGroup();
    TransformGroup segment4 = new TransformGroup();
    TransformGroup ramie_p1 = new TransformGroup();
    TransformGroup chwytakTr = new TransformGroup();
    TransformGroup kulka_p = new TransformGroup();
    TransformGroup segment5 = new TransformGroup();
    TransformGroup segment6 = new TransformGroup();
    TransformGroup ramie_p2 = new TransformGroup();
    
    Transform3D nag_przesuniecie_seg = new Transform3D();  
    Transform3D nag_przesuniecie_seg2 = new Transform3D();
    Transform3D nag_przesuniecie_seg3 = new Transform3D();
    Transform3D nag_przesuniecie_seg4 = new Transform3D();
    Transform3D nag_przesuniecie_seg5 = new Transform3D();
    Transform3D nag_przesuniecie_seg6 = new Transform3D();
    Transform3D nag_przesuniecie_ram = new Transform3D();
    Transform3D nag_przesuniecie_ramie2 = new Transform3D();
    Transform3D nag_przesuniecie_chwytaka = new Transform3D();
    Transform3D nag_kulka_trans3D = new Transform3D();

    BranchGroup kulkaBranch = new BranchGroup();
    
    detektorKolizji kolizja_kulki;
    detektorKolizji kolizja_chwytaka;
    detektorKolizji kolizja_podlogi;
    
    KeyEvent klawisz_Q = new KeyEvent(new Button(), 1, 20, 1, KeyEvent.VK_Q, 'Q');
    KeyEvent klawisz_E = new KeyEvent(new Button(), 1, 20, 1, KeyEvent.VK_E, 'E');
    KeyEvent klawisz_A = new KeyEvent(new Button(), 1, 20, 1, KeyEvent.VK_A, 'A');
    KeyEvent klawisz_D = new KeyEvent(new Button(), 1, 20, 1, KeyEvent.VK_D, 'D');
    KeyEvent klawisz_W = new KeyEvent(new Button(), 1, 20, 1, KeyEvent.VK_W, 'W');
    KeyEvent klawisz_S = new KeyEvent(new Button(), 1, 20, 1, KeyEvent.VK_S, 'S');
    KeyEvent klawisz_I = new KeyEvent(new Button(), 1, 20, 1, KeyEvent.VK_I, 'I');
    KeyEvent klawisz_K = new KeyEvent(new Button(), 1, 20, 1, KeyEvent.VK_K, 'K');
    
    boolean nagrywanie;
    boolean odtwarzanie;
    Vector<KeyEvent> nagrane_przyciski = new Vector<KeyEvent>();
    
    boolean key_a;
    boolean key_d;
    boolean key_w;
    boolean key_s;
    boolean key_q;
    boolean key_e;
    boolean key_i;
    boolean key_k;
    
    float wysuniecie=0.0f;
    float kat_wychylenia=0.0f;
    float kat_obrotu=0.0f;
    
    float nag_wysuniecie=0.0f;
    float nag_kat_wychylenia=0.0f;
    float nag_kat_obrotu=0.0f;
    
    
    boolean gra_dzwiek = false;
    boolean gra_muzyka = false;
    boolean chwycona = false;
    boolean nag_chwycona = false;
    
    char ostatni_klawisz = '0';
    
    javax.sound.sampled.Clip clip;
    javax.sound.sampled.Clip dziw;

    PolarArm(){
         super("Polar Robot Arm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GraphicsConfiguration config =
           SimpleUniverse.getPreferredConfiguration();
        
        
        Canvas3D canvas3D = new Canvas3D(config);
        canvas3D.setPreferredSize(new Dimension(1280,720));
        canvas3D.addKeyListener(this);
        add(canvas3D);
        pack();
        add(BorderLayout.EAST, stworzPanelPrzyciskow());
        add(BorderLayout.NORTH, dodanieInstrukcji());
        add(BorderLayout.CENTER, canvas3D);
        setVisible(true);
        
        BranchGroup scena = new BranchGroup();
        scena = utworzScene();
        scena.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	scena.compile();
        
        simpleU = new SimpleUniverse(canvas3D);
        ViewingPlatform viewingPlatform = simpleU.getViewingPlatform();
        
        
        orbita = new OrbitBehavior(canvas3D, OrbitBehavior.REVERSE_ALL);
	boundso = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
						   100.0);
	orbita.setSchedulingBounds(boundso);
	viewingPlatform.setViewPlatformBehavior(orbita);

        Transform3D przesuniecie_obserwatora = new Transform3D();
        Transform3D rot_obs = new Transform3D();
        rot_obs.rotY((float)(-Math.PI/7));
        przesuniecie_obserwatora.set(new Vector3f(-1.2f,2.0f,2.0f));
        przesuniecie_obserwatora.mul(rot_obs);
        rot_obs.rotX((float)(-Math.PI/6));
        przesuniecie_obserwatora.mul(rot_obs);

        simpleU.getViewingPlatform().getViewPlatformTransform().setTransform(przesuniecie_obserwatora);
        simpleU.addBranchGraph(scena);

    }
    public static JPanel dodanieInstrukcji() {
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon("obrazki\\instrukcja_robota.jpg"));
        JPanel panel_instrukcji = new JPanel(new FlowLayout());
        panel_instrukcji.add(label);
        return panel_instrukcji;
    }
    
    public JPanel stworzPanelPrzyciskow() {
        JPanel panel_menu = new JPanel(new GridLayout(12, 1, 10, 10));
        
        reset_ustawienia.setText("Reset ustawienia kulki");
        reset_ustawienia.addActionListener(this);
        reset_kamery.setText("Reset kamery");
        reset_kamery.addActionListener(this);

        nagrywaj.setText("Rozpocznij nagrywanie");
        nagrywaj.addActionListener(this);

        muzykaOnOff.setText("Włącz / wyłącz muzykę");
        muzykaOnOff.addActionListener(this);
        

        odtworz_nagranie.setText("Odtworz nagranie");
        odtworz_nagranie.addActionListener(this);
        odtworz_nagranie.setEnabled(false); //przycisk odtwarzania nieaktywny dopóki nie powstanie nagranie
        
        textObrot1 = new JTextArea(1,5);
        textObrot1.setFont(new Font("Tahoma",Font.BOLD,40));
        textObrot1.setText("0.00");
        textObrot2 = new JTextArea(1,5);
        textObrot2.setFont(new Font("Tahoma",Font.BOLD,40));
        textObrot2.setText("0.00");
        textWysuniecie = new JTextArea(1,5);
        textWysuniecie.setFont(new Font("Tahoma",Font.BOLD,40));
        textWysuniecie.setText("0.00");
        ustawKoordynaty.setText("Ustaw Koordynaty");
        ustawKoordynaty.addActionListener(this);
        
        panel_menu.add(muzykaOnOff);
        panel_menu.add(reset_ustawienia);
        panel_menu.add(reset_kamery);
        panel_menu.add(nagrywaj);
        panel_menu.add(odtworz_nagranie);
        panel_menu.add(new JLabel("Kąt robota (-360°,360°):"));
        panel_menu.add(textObrot1);
        panel_menu.add(new JLabel("Kąt ramienia [-45°,45°]:"));
        panel_menu.add(textObrot2);
        panel_menu.add(new JLabel("Wysuniecie ramienia [%]:"));
        panel_menu.add(textWysuniecie);
        panel_menu.add(ustawKoordynaty);
        return panel_menu;
    }
    BranchGroup utworzScene()
    {

        wezel_scena.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        wezel_scena.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        
   
        
        BoundingSphere bounds = new BoundingSphere(new Point3d(0f,0f,0f),5f);
        
        Appearance wyglad_ziemia = new Appearance();
        

        TextureLoader loader = new TextureLoader("obrazki/beton.jpg",null);
        ImageComponent2D image = loader.getImage();

        Texture2D podloga = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
                                        image.getWidth(), image.getHeight());

        podloga.setImage(0, image);
        podloga.setBoundaryModeS(Texture.WRAP);
        podloga.setBoundaryModeT(Texture.WRAP);

        loader = new TextureLoader("obrazki/black.jpg",this);
        image = loader.getImage();

        // tlo
        float odl_tla = 15f;
        float wys_tla = 10f;
        Appearance wyglad_tla = new Appearance();
        Texture tekstura_tla = new TextureLoader("obrazki/tlo.png", this).getTexture();
        wyglad_tla.setTexture(tekstura_tla);
        Appearance wyglad_tla_alt = new Appearance();
        Texture tekstura_tla_alt = new TextureLoader("obrazki/hala.png", this).getTexture();
        wyglad_tla_alt.setTexture(tekstura_tla_alt);
        Transform3D przesuniecie_tla1 = new Transform3D();
        przesuniecie_tla1.setTranslation(new Vector3f(odl_tla, 0.1f, 0.0f));
        TransformGroup tlo_p1 = new TransformGroup(przesuniecie_tla1);
        Box TloModel1 = new Box(0.05f, wys_tla, odl_tla, Box.GENERATE_TEXTURE_COORDS, wyglad_tla);
        Transform3D przesuniecie_tla2 = new Transform3D();
        przesuniecie_tla2.setTranslation(new Vector3f(-odl_tla, 0.1f, 0.0f));
        TransformGroup tlo_p2 = new TransformGroup(przesuniecie_tla2);
        Box TloModel2 = new Box(0.05f, wys_tla, odl_tla, Box.GENERATE_TEXTURE_COORDS, wyglad_tla);
        Transform3D przesuniecie_muru3 = new Transform3D();
        przesuniecie_muru3.setTranslation(new Vector3f(0.0f, 0.1f, odl_tla));
        TransformGroup tlo_p3 = new TransformGroup(przesuniecie_muru3);
        Box TloModel3 = new Box(odl_tla, wys_tla, 0.05f, Box.GENERATE_TEXTURE_COORDS, wyglad_tla);
        Transform3D przesuniecie_muru4 = new Transform3D();
        przesuniecie_muru4.setTranslation(new Vector3f(0.0f, 0.1f, -odl_tla));
        TransformGroup tlo_p4 = new TransformGroup(przesuniecie_muru4);
        Box TloModel4 = new Box(odl_tla, wys_tla, 0.05f, Box.GENERATE_TEXTURE_COORDS, wyglad_tla_alt);
        Appearance wyglad_sufitu = new Appearance();
        Texture tekstura_sufitu = new TextureLoader("obrazki/sufit.jpg", this).getTexture();
        wyglad_sufitu.setTexture(tekstura_sufitu);
        Transform3D przesuniecie_sufitu = new Transform3D();
        przesuniecie_sufitu.setTranslation(new Vector3f(0.0f, wys_tla, 0.0f));
        TransformGroup sufit_p = new TransformGroup(przesuniecie_sufitu);
        Box sufit = new Box(odl_tla, 5f, odl_tla, Box.GENERATE_TEXTURE_COORDS, wyglad_sufitu);

        tlo_p1.addChild(TloModel1);
        tlo_p2.addChild(TloModel2);
        tlo_p3.addChild(TloModel3);
        tlo_p4.addChild(TloModel4);
        sufit_p.addChild(sufit);
        wezel_scena.addChild(tlo_p1);
        wezel_scena.addChild(tlo_p2);
        wezel_scena.addChild(tlo_p3);
        wezel_scena.addChild(tlo_p4);
        wezel_scena.addChild(sufit_p);

        AmbientLight lightA = new AmbientLight();
        lightA.setInfluencingBounds(bounds);
        wezel_scena.addChild(lightA);

        // dwa światła punktowe nad robotem
        PointLight swiatlo_pkt = new PointLight(new Color3f(1.0f, 1.0f, 1.0f), new Point3f(0f,2.0f,2.0f), new Point3f(0.1f,0.1f,0.1f));
        PointLight swiatlo_pkt2 = new PointLight(new Color3f(1.0f, 1.0f, 1.0f), new Point3f(0f,2.0f,-2.0f), new Point3f(0.1f,0.1f,0.1f));
        swiatlo_pkt.setInfluencingBounds(bounds);
        swiatlo_pkt2.setInfluencingBounds(bounds);
        wezel_scena.addChild(swiatlo_pkt);
        wezel_scena.addChild(swiatlo_pkt2);
        wyglad_ziemia.setTexture(podloga);
        
        //podłoże
        float ziemia_wys = 0.02f;
        float ziemia_szer = 3.0f;
        Box ziemia = new Box(ziemia_szer, ziemia_wys, ziemia_szer, Box.GENERATE_TEXTURE_COORDS, wyglad_ziemia);
        Transform3D przesuniecie_ziemi = new Transform3D();
        przesuniecie_ziemi.setTranslation(new Vector3f(0.0f, -ziemia_wys, 0.0f));
        TransformGroup ziemia_trans = new TransformGroup(przesuniecie_ziemi);
        ziemia_trans.addChild(ziemia);
        wezel_scena.addChild(ziemia_trans);
        kolizja_podlogi = new detektorKolizji(ziemia_trans,
        new BoundingBox(new Point3d(-ziemia_szer, -0.05f, -ziemia_szer), new Point3d(ziemia_szer, -0.01f, ziemia_szer)));
        kolizja_podlogi.setSchedulingBounds(new BoundingSphere(new Point3d(), 10f));
        wezel_scena.addChild(kolizja_podlogi);
        ziemia_trans.setUserData("ziemia");
        //podstawa
        float podstawa_szer = 0.3f;
        float podstawa_wys = 0.05f;
        
        //murowana podstawa
        Appearance wyglad_muru = new Appearance();
        Texture tekstura_muru = new TextureLoader("obrazki/murek.gif", this).getTexture();
        wyglad_muru.setTexture(tekstura_muru);
        Transform3D przesuniecie_muru = new Transform3D();
        przesuniecie_muru.setTranslation(new Vector3f(0.0f, podstawa_wys/2, 0.0f));
        TransformGroup mur_p = new TransformGroup(przesuniecie_muru);
        Box MurModel = new Box(podstawa_szer, podstawa_wys, podstawa_szer, Box.GENERATE_TEXTURE_COORDS, wyglad_muru);
        
        mur_p.addChild(MurModel);
        
        wezel_scena.addChild(mur_p);
        
        
        //podstawa robota
        Appearance wyglad_robota   = new Appearance();
        Texture2D glowna_tekstura = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
                                        image.getWidth(), image.getHeight());
        glowna_tekstura.setImage(0, image);
        glowna_tekstura.setBoundaryModeS(Texture.WRAP);
        glowna_tekstura.setBoundaryModeT(Texture.WRAP);
        wyglad_robota.setTexture(glowna_tekstura);
        
        float wys_seg1=0.1f;
        segment.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        segment.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        segment2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        segment2.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        segment2.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        segment2.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        segment3.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        segment4.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        segment5.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        segment5.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        ramie_p1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        ramie_p2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        przesuniecie_seg.set(new Vector3f(0.0f,podstawa_wys + wys_seg1/2,0.0f));
        segment.setTransform(przesuniecie_seg);

        Cylinder walec = new Cylinder(podstawa_szer-0.05f,wys_seg1,Cylinder.GENERATE_NORMALS| Cylinder.GENERATE_TEXTURE_COORDS, wyglad_robota);
        segment.addChild(walec);
        wezel_scena.addChild(segment);
        
        //ten główny słup
        float wys_seg2 = 0.6f;
        
        przesuniecie_seg2.set(new Vector3f(0.0f,podstawa_wys + wys_seg1 + wys_seg2/2,0.0f));
        segment2.setTransform(przesuniecie_seg2);

        Cylinder walec2 = new Cylinder(podstawa_szer-0.2f,wys_seg2,Cylinder.GENERATE_NORMALS| Cylinder.GENERATE_TEXTURE_COORDS, wyglad_robota);
        segment2.addChild(walec2);
        wezel_scena.addChild(segment2);
        
        //te koła z boku
        
        float gr_kola = 0.05f;
        float promien_kola = 0.15f;
        
        przesuniecie_seg3.set(new Vector3f(podstawa_szer/2-0.02f,wys_seg2/2+promien_kola/2,0.0f));
        Transform3D  tmp_rot      = new Transform3D();
        tmp_rot.rotZ(Math.PI/2);
        przesuniecie_seg3.mul(tmp_rot);
        segment3.setTransform(przesuniecie_seg3);

        Cylinder walec3 = new Cylinder(promien_kola,gr_kola,Cylinder.GENERATE_NORMALS| Cylinder.GENERATE_TEXTURE_COORDS, wyglad_robota);
        segment3.addChild(walec3);
        segment2.addChild(segment3);
        
        przesuniecie_seg4.set(new Vector3f(-podstawa_szer/2+0.02f,wys_seg2/2+promien_kola/2,0.0f));
        przesuniecie_seg4.mul(tmp_rot);
        segment4.setTransform(przesuniecie_seg4);
        Cylinder walec4 = new Cylinder(promien_kola,gr_kola,Cylinder.GENERATE_NORMALS| Cylinder.GENERATE_TEXTURE_COORDS, wyglad_robota);
        segment4.addChild(walec4);
        segment2.addChild(segment4);
        
        
        //główne ramię
        Box ramie1 = new Box(podstawa_szer-0.2f, podstawa_wys, podstawa_szer+0.1f, Box.GENERATE_TEXTURE_COORDS, wyglad_robota);
        przesuniecie_ram.set(new Vector3f(0.0f, wys_seg2/2+promien_kola/2, 0.0f));
        ramie_p1.setTransform(przesuniecie_ram);
        ramie_p1.addChild(ramie1);
        segment2.addChild(ramie_p1);
        
        Appearance wyglad_alum = new Appearance();
        Texture tekstura_alum = new TextureLoader("obrazki/alum.jpg", this).getTexture();
        wyglad_alum.setTexture(tekstura_alum);
        
        segment6.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        segment6.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        przesuniecie_seg6.set(new Vector3f(0.0f,-0.01f,-0.4f));
        przesuniecie_seg6.mul(tmp_rot);
        segment6.setTransform(przesuniecie_seg6);
        Cylinder walec6 = new Cylinder(0.08f,podstawa_szer-0.09f,Cylinder.GENERATE_NORMALS| Cylinder.GENERATE_TEXTURE_COORDS, wyglad_alum);
        segment6.addChild(walec6);
        ramie_p1.addChild(segment6);
       
        //aluminiowy walec, wokół którego wykonywany jest obrót głównego ramienia w górę i w dół
        przesuniecie_seg5.set(new Vector3f(0.0f,0.0f,0.0f));
        przesuniecie_seg5.mul(tmp_rot);
        segment5.setTransform(przesuniecie_seg5);
        Cylinder walec5 = new Cylinder(0.1f,podstawa_szer+0.02f,Cylinder.GENERATE_NORMALS| Cylinder.GENERATE_TEXTURE_COORDS, wyglad_alum);
        segment5.addChild(walec5);
        ramie_p1.addChild(segment5);
        
        //wysuwane ramię aluminiowe
        przesuniecie_ramie2.set(new Vector3f(0.0f,0.0f,0.2f));
        Transform3D  tmp_rot2 = new Transform3D();
        tmp_rot2.rotX(Math.PI/2);
        przesuniecie_ramie2.mul(tmp_rot2);
        ramie_p2.setTransform(przesuniecie_ramie2);
        Cylinder ramie2 = new Cylinder(0.03f,podstawa_szer+0.7f,Cylinder.GENERATE_NORMALS| Cylinder.GENERATE_TEXTURE_COORDS, wyglad_alum);
        ramie_p2.addChild(ramie2);
        ramie_p1.addChild(ramie_p2);
        
        //chwytak
        Appearance  wyglad_chwytaka = new Appearance();
        Material chwytak_mat = new Material(new Color3f(0.0f, 0.0f,0.1f), new Color3f(0.0f,0.1f,0.1f),
                                                new Color3f(0.3f, 0.3f, 0.3f), new Color3f(1.0f, 1.0f, 1.0f),100.0f);
        wyglad_chwytaka.setMaterial(chwytak_mat);
        Cylinder chwytak = new Cylinder(0.05f, 0.05f,wyglad_chwytaka);
        chwytakTr.addChild(chwytak);
        przesuniecie_chwytaka.set(new Vector3f(0.0f,(podstawa_szer+0.7f)/2,0.0f));
        chwytakTr.setTransform(przesuniecie_chwytaka);
        ramie_p2.addChild(chwytakTr);
        
        chwytakTr.setUserData("Chwytak");
        chwytakTr.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        chwytakTr.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        chwytakTr.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        chwytakTr.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        
        kolizja_chwytaka = new detektorKolizji(chwytakTr,
                new BoundingSphere(new Point3d(0.0f, 0.0f, 0.1f), 0.03f)); 
        kolizja_chwytaka.setSchedulingBounds(new BoundingSphere(new Point3d(), 0.2f));
        chwytakTr.addChild(kolizja_chwytaka);
	    
	/// kulka 
     	Material material_kulki = new Material(new Color3f(0.5f, 0.3f,0.8f), new Color3f(0.1f,0.1f,0.1f),
                                                new Color3f(0.8f, 0.3f, 0.5f), new Color3f(0.1f, 0.1f, 0.1f), 20.0f);

        Appearance wyglad_kulki = new Appearance();
        wyglad_kulki.setMaterial(material_kulki);

        Transform3D przesuniecie_kulki = new Transform3D();
        kulka_p.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        kulka_p.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        przesuniecie_kulki.set(new Vector3f(0.0f, 0.11f, 1.0f)); 
        kulka_trans3D.mul(przesuniecie_kulki);
        kulka_p.setTransform(przesuniecie_kulki);
        Sphere kulka = new Sphere(0.1f, wyglad_kulki);
        kulka_p.addChild(kulka);
        kulka_p.setUserData("kulka");

        kulkaBranch.setCapability(BranchGroup.ALLOW_DETACH);
        kulkaBranch.addChild(kulka_p);
        wezel_scena.addChild(kulkaBranch);
        
        kolizja_kulki = new detektorKolizji(kulka_p,
                new BoundingSphere(new Point3d(0.0f, 0f, 0.0f), 0.1f)); 
        kolizja_kulki.setSchedulingBounds(new BoundingSphere(new Point3d(), 0.1f));
        kulka_p.addChild(kolizja_kulki);
        
        return wezel_scena;

    }

    public static void main(String args[]){
        
      new PolarArm();

   }
    //detektor kolizji przyjmujący obiekt i granicę kolizji
    public class detektorKolizji extends Behavior {

        private boolean wKolizji = false;
        private Group group;

        private WakeupOnCollisionEntry wEnter;
        private WakeupOnCollisionExit wExit;

        public detektorKolizji(Group grupa, Bounds bounds) {
            group = grupa;
            grupa.setCollisionBounds(bounds);
            wKolizji = false;
    }
    public void initialize() {
        wEnter = new WakeupOnCollisionEntry(group);
        wExit = new WakeupOnCollisionExit(group);
        wakeupOn(wEnter);
    }
    public void processStimulus(Enumeration criteria) {
        wKolizji = !wKolizji;
        if (wKolizji) {
            System.out.println("Kolizja       : " + group.getUserData());
            wakeupOn(wExit);
        }
        else {
            System.out.println("Nie ma kolizji: "  + group.getUserData());
            wakeupOn(wEnter);
        }
    }

    public boolean czyKolizja(){
        if(wKolizji) return true;
        else return false;
    }

}
    //uniwersalna funkcja zamrożenia ruchu na 20 ms, przydatna przy odtwarzania ruchu nagrywaniem, bądź ustawianiem koordynatów
    public void czekaj(){
        try {
            Thread.sleep(20);
      } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
      }
    }
    //muzyka w tle
    public void muzyka(){
        try {
        AudioInputStream muzyka = AudioSystem.getAudioInputStream(new File("muzyka\\dzwiek6.mid").getAbsoluteFile());
        dziw = AudioSystem.getClip();
        dziw.open(muzyka);
        dziw.start();
        } catch(Exception ex) {
            System.out.println("Nie można odtworzyć dźwięku");
            ex.printStackTrace();
        }
    }
    // dźwięk ruchu robota
    public void dzwiek(){
        try {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("muzyka\\dzwiek2.wav").getAbsoluteFile());
        clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.start();
        } catch(Exception ex) {
            System.out.println("Nie można odtworzyć dźwięku");
            ex.printStackTrace();
        }
    }
    // kolizja ujednolicona do naszego konkretnego przypadku; sprawdzamy czy kolizja powinna mieć wpływ na ruch
    public boolean sprawdzanieKolizji(){
        if(((!chwycona && (kolizja_chwytaka.czyKolizja() && kolizja_kulki.czyKolizja() || kolizja_chwytaka.czyKolizja() && kolizja_podlogi.czyKolizja())) || chwycona && kolizja_podlogi.czyKolizja()) && kat_wychylenia >= 0f){
            return true;
        }
        else return false;
    }
    
    public void wykonajRuch(){
        
        Transform3D akcja = new Transform3D();
        
        if (key_a) {
            akcja.rotY(Math.PI / 100); //krok obrotu to 1/100 PI
            kat_obrotu+=Math.PI / 100;
            kat_obrotu%=2*Math.PI;
            przesuniecie_seg2.mul(akcja);
            segment2.setTransform(przesuniecie_seg2);
            if(sprawdzanieKolizji() && ostatni_klawisz == 'a'){
               akcja.rotY(-Math.PI / 100);
               kat_obrotu-=Math.PI / 100;
               kat_obrotu%=2*Math.PI;
               przesuniecie_seg2.mul(akcja);
               segment2.setTransform(przesuniecie_seg2); 
            }
            ostatni_klawisz = 'a'; //zapisujemy ostatni klawisz, aby wiedzieć jakiego ruchu dokonano przed kolizją, aby blokować tylko ten ruch
            
        }
        if (key_w  && kat_wychylenia < Math.PI/4) { //ograniczamy wychylenie w górę i w dół do 45 stopni
            
            akcja.rotX(Math.PI / 100);
            kat_wychylenia+= Math.PI / 100;
            przesuniecie_ram.mul(akcja);
            ramie_p1.setTransform(przesuniecie_ram);
            if(sprawdzanieKolizji() && ostatni_klawisz == 'w'){
                akcja.rotX(-Math.PI / 100);
                kat_wychylenia-= Math.PI / 100;
                przesuniecie_ram.mul(akcja);
                ramie_p1.setTransform(przesuniecie_ram);
            }
            ostatni_klawisz = 'w';
        }
        if (key_s  && kat_wychylenia > -Math.PI/4) {
            
            akcja.rotX(-Math.PI / 100);
            kat_wychylenia-= Math.PI / 100;
            przesuniecie_ram.mul(akcja);
            ramie_p1.setTransform(przesuniecie_ram);
            if(sprawdzanieKolizji() && ostatni_klawisz == 's'){
                akcja.rotX(Math.PI / 100);
                kat_wychylenia+= Math.PI / 100;
                przesuniecie_ram.mul(akcja);
                ramie_p1.setTransform(przesuniecie_ram);
            }
            ostatni_klawisz = 's';
        }
        if (key_d) {
            
            akcja.rotY(-Math.PI / 100);
            kat_obrotu-=Math.PI / 100;
            kat_obrotu%=2*Math.PI;
            przesuniecie_seg2.mul(akcja);
            segment2.setTransform(przesuniecie_seg2);
            if(sprawdzanieKolizji() && ostatni_klawisz == 'd'){
                akcja.rotY(Math.PI / 100);
                kat_obrotu+=Math.PI / 100;
                kat_obrotu%=2*Math.PI;
                przesuniecie_seg2.mul(akcja);
                segment2.setTransform(przesuniecie_seg2);
            }
            ostatni_klawisz = 'd';
            
        }
        if (key_q && wysuniecie<0.59f) { //ograniczamy wysuniecie ramienia mniej więcej do jego długości
            
            akcja.set(new Vector3f(0.0f,0.01f,0.0f));
            wysuniecie+=0.01f;
            przesuniecie_ramie2.mul(akcja);
            ramie_p2.setTransform(przesuniecie_ramie2);
            if(sprawdzanieKolizji() && ostatni_klawisz == 'q'){
                akcja.set(new Vector3f(0.0f,-0.01f,0.0f));
                wysuniecie-=0.01f;
                przesuniecie_ramie2.mul(akcja);
                ramie_p2.setTransform(przesuniecie_ramie2);
            }
            ostatni_klawisz = 'q';
        }
        if (key_e && wysuniecie > 0.01f) {
           akcja.set(new Vector3f(0.0f,-0.01f,0.0f));
           wysuniecie-=0.01f;
           przesuniecie_ramie2.mul(akcja);
           ramie_p2.setTransform(przesuniecie_ramie2);
            
        }
        if(key_i && kolizja_chwytaka.czyKolizja() && kolizja_kulki.czyKolizja() && !chwycona){
           
            wezel_scena.removeChild(kulkaBranch); //zaczepiamy branch kulki do chwytaka
            chwytakTr.addChild(kulkaBranch);
            akcja.set(new Vector3f(0.0f, 0.11f, 0.0f)); 
            kulka_p.setTransform(akcja);
            chwycona = true;
            
        }
        if(key_k && chwycona){
            
            Vector3f position = new Vector3f();
            kulka_p.getLocalToVworld(kulka_trans3D); 
            kulka_trans3D.get(position);
            float wysokosc = position.getY();
            chwytakTr.removeChild(kulkaBranch);
            wezel_scena.addChild(kulkaBranch);
            akcja.set(position);
            kulka_p.setTransform(akcja);
            akcja.rotX(-kat_wychylenia);
            kulka_trans3D.mul(akcja);
            akcja.set(new Vector3f(0.0f, 0.11f, 0.0f));
            kulka_trans3D.mul(akcja);
            kulka_p.setTransform(kulka_trans3D);
            chwycona = false;
            while(wysokosc>0.14){ //animacja spadania - kulka spada dopóki nie osiągnie wysokości gruntu
                akcja.set(new Vector3f(0.0f, 0.00f, 0.04f));
                kulka_trans3D.mul(akcja);
                kulka_p.setTransform(kulka_trans3D);
                wysokosc-=0.04;
                czekaj();
            }
        }
        if((key_a || key_s || key_d || key_w || key_q ||key_e) && !gra_dzwiek){ //aktywujemy dźwięk ruchu robota gdy rusza się
            dzwiek();
            gra_dzwiek = true;
        }
        //koordynaty robota są zapisywane do okien widocznych w interfejsie
        textObrot1.setText(String.format("%.2f", kat_obrotu/Math.PI*180));
        textObrot2.setText(String.format("%.2f", kat_wychylenia/Math.PI*180));
        textWysuniecie.setText(String.format("%.2f", wysuniecie/0.6 * 100));
        
        
    }

    //sterowanie działaniem przycisków w interfejsie
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == reset_ustawienia) { //reset ustawienia kulki, przydatny gdy np. upuścimy kulkę przy maksymalnym wysunięciu i najbardziej poziomym kącie wychylenia
            if(chwycona){ //jeśli kulka była chwycona w momencie kliknięcia, odczepiamy ją najpierw od ramienia
                chwytakTr.removeChild(kulkaBranch);
                wezel_scena.addChild(kulkaBranch);
                chwycona = false;
            }
            kulka_trans3D.set(new Vector3f(0.0f, 0.11f, 1.0f));
            kulka_p.setTransform(kulka_trans3D);
        }
        if (e.getSource() == reset_kamery) { //pierwotne ustawienia obserwatora
            Transform3D przesuniecie_obserwatora = new Transform3D();
            Transform3D rot_obs = new Transform3D();
            rot_obs.rotY((float)(-Math.PI/7));
            przesuniecie_obserwatora.set(new Vector3f(-1.2f,2.0f,2.0f));
            przesuniecie_obserwatora.mul(rot_obs);
            rot_obs.rotX((float)(-Math.PI/6));
            przesuniecie_obserwatora.mul(rot_obs);

            simpleU.getViewingPlatform().getViewPlatformTransform().setTransform(przesuniecie_obserwatora);
        }
        if (e.getSource() == ustawKoordynaty) { //ustawiamy koordynaty zdefiniowane w interfejsie
            float celWysuniecia = Float.parseFloat(textWysuniecie.getText()) % 101;
            float celObrotu1 = Float.parseFloat(textObrot1.getText()) % 360;
            float celObrotu2 = Float.parseFloat(textObrot2.getText()) % 46;
            
            while(kat_obrotu/Math.PI*180<celObrotu1-1){
                keyPressed(klawisz_A);
                czekaj();
            }
            keyReleased(klawisz_A);
            while(kat_obrotu/Math.PI*180>celObrotu1+1){
                keyPressed(klawisz_D);
                czekaj();
                if(sprawdzanieKolizji()) break;
            }
            keyReleased(klawisz_D);
            while(kat_wychylenia/Math.PI*180<celObrotu2-1){
                keyPressed(klawisz_W);
                czekaj();
                if(sprawdzanieKolizji()) break;
            }
            keyReleased(klawisz_W);
            while(kat_wychylenia/Math.PI*180>celObrotu2+1){
                keyPressed(klawisz_S);
                czekaj();
                if(sprawdzanieKolizji()) break;
            }
            keyReleased(klawisz_S);
            while(wysuniecie/0.6*100<celWysuniecia-1){
                keyPressed(klawisz_Q);
                czekaj();
                if(sprawdzanieKolizji()) break;
            }
            keyReleased(klawisz_Q);
            while(wysuniecie/0.6*100>celWysuniecia+1.67){ //zamieniamy na wartości procentowe
                keyPressed(klawisz_E);
                czekaj();
                if(sprawdzanieKolizji()) break;
            }
            keyReleased(klawisz_E);
        }
        

        //Obsługa uczenia ruchu robota; usuwamy poprzednio nagrane przyciski i zapamiętujemy pozycję
        if (e.getSource() == nagrywaj) {
            if(!nagrywanie){
                nagrywaj.setBackground(new Color(160, 0, 0)); //przycisk nagrywania podświetla się na czerwono podczas nagrywania
                nagrywaj.setForeground(Color.white);
                nagrywaj.setText("Zakończ Nagrywanie"); //napis na przycisku i jego zastosowanie zależy czy jesteśmy w czasie nagrywania czy nie
                odtworz_nagranie.setEnabled(false); //odtwarzanie niedostępne podczas nagrywania
                reset_ustawienia.setEnabled(false); //reset pozycji kulki niedostępny podczas nagrywania
                nagrane_przyciski.clear(); //usuwamy poprzednio nagrane przyciski
            
                nag_przesuniecie_seg.set(przesuniecie_seg);
                nag_przesuniecie_seg2.set(przesuniecie_seg2);
                nag_przesuniecie_seg3.set(przesuniecie_seg3);
                nag_przesuniecie_seg4.set(przesuniecie_seg4);
                nag_przesuniecie_seg5.set(przesuniecie_seg5);
                nag_przesuniecie_seg6.set(przesuniecie_seg6);
                nag_przesuniecie_ram.set(przesuniecie_ram);
                nag_przesuniecie_ramie2.set(przesuniecie_ramie2);
                nag_przesuniecie_chwytaka.set(przesuniecie_chwytaka);
                nag_kulka_trans3D.set(kulka_trans3D);
                nag_chwycona = chwycona;

                nag_wysuniecie = wysuniecie;
                nag_kat_wychylenia = kat_wychylenia;
                nag_kat_obrotu = kat_obrotu;
                nagrywanie = true;
            }
            else if(nagrywanie){
                nagrywaj.setBackground(null); //domyślny kolor przycisku
                nagrywaj.setForeground(null);
                nagrywaj.setText("Rozpocznij Nagrywanie");
                nagrywanie = false;
                odtworz_nagranie.setEnabled(true);
                reset_ustawienia.setEnabled(true);
            }
        }

        if (e.getSource() == muzykaOnOff) { //włączamy lub wyłączamy muzykę
            if(!gra_muzyka){
                muzyka();
                gra_muzyka = true;
            }
            else{
                dziw.stop();
                gra_muzyka = false;
            }
        }

        //Odtwarzanie nagrania - wracamy do zapamiętanej pozycji początkowej i wykonujemy ruchy zapisane w wektorze. Ponieważ gdy przytrzymujemy przycisk, zapisywany jest on wielokrotnie, nie potrzebujemy mierzyć ruchów, ani czasu
        //nagranej sekwencji przycisków klawiatury
        if (e.getSource() == odtworz_nagranie) {
            nagrywanie = false;
            odtwarzanie = true;
            reset_ustawienia.setEnabled(false); //gdy odtwarzamy nagranie reset ustawienia również nie jest możliwy, ponieważ zafałszowałoby to nagranie

            przesuniecie_seg.set(nag_przesuniecie_seg);
            przesuniecie_seg2.set(nag_przesuniecie_seg2);
            przesuniecie_seg3.set(nag_przesuniecie_seg3);
            przesuniecie_seg4.set(nag_przesuniecie_seg4);
            przesuniecie_seg5.set(nag_przesuniecie_seg5);
            przesuniecie_seg6.set(nag_przesuniecie_seg6);
            przesuniecie_ram.set(nag_przesuniecie_ram);
            przesuniecie_ramie2.set(nag_przesuniecie_ramie2);
            przesuniecie_chwytaka.set(nag_przesuniecie_chwytaka);
            kulka_trans3D.set(nag_kulka_trans3D);
        
            segment.setTransform(nag_przesuniecie_seg);
            segment2.setTransform(nag_przesuniecie_seg2);
            segment3.setTransform(nag_przesuniecie_seg3);
            segment4.setTransform(nag_przesuniecie_seg4);
            segment5.setTransform(nag_przesuniecie_seg5);
            segment6.setTransform(nag_przesuniecie_seg6);
            ramie_p1.setTransform(nag_przesuniecie_ram);
            ramie_p2.setTransform(nag_przesuniecie_ramie2);
            chwytakTr.setTransform(nag_przesuniecie_chwytaka);
            
            wysuniecie = nag_wysuniecie;
            kat_wychylenia = nag_kat_wychylenia;
            kat_obrotu = nag_kat_obrotu;
            if(nag_chwycona && !chwycona){ //gdy przy rozpoczęciu nagrania była chwycona, a na końcu puszczona, przy odtwarzaniu musimy ją ponownie zaczepić 
                wezel_scena.removeChild(kulkaBranch);
                chwytakTr.addChild(kulkaBranch);
                Transform3D akcja = new Transform3D();
                akcja.set(new Vector3f(0.0f, 0.11f, 0.0f));
                kulka_trans3D.set(nag_kulka_trans3D);
                kulka_trans3D.mul(akcja);
                kulka_p.setTransform(akcja);
                chwycona = true;
            }
            else if(!nag_chwycona && chwycona){ //gdy chwycono ją w czasie nagrywania i pozostała chwycona do końca, trzeba ją odczepić
                chwytakTr.removeChild(kulkaBranch);
                wezel_scena.addChild(kulkaBranch);
                kulka_p.setTransform(nag_kulka_trans3D);
                chwycona = false;
            }
            else if(nag_chwycona && chwycona){ //jeśli na początku i na końcu była chwycona, to nie musimy nic robić, kulka nie zmieniła pozycji względem razmienia
                
            }
            else kulka_p.setTransform(nag_kulka_trans3D); //gdy kulka zaczęła i skończyła nagranie puszczona, jedynie przywracamy jej pozycję
            
            for (int i = 0; i < nagrane_przyciski.size(); i++) {
                
                keyPressed(nagrane_przyciski.elementAt(i));
                czekaj();
                if(i == nagrane_przyciski.size()-1 || nagrane_przyciski.elementAt(i+1) != nagrane_przyciski.elementAt(i)){
                keyReleased(nagrane_przyciski.elementAt(i));
                } //taka konstrukcja służy działaniu dźwięku, klawisz uznajemy za puszczony, gdy zmienił się na inny, lub skończył się ruch
            }
            odtwarzanie = false;
            reset_ustawienia.setEnabled(true); //przywracamy działanie przycisku resetowania kulki
        }
    }

    public void keyTyped(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void keyPressed(KeyEvent e) {
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:                
                key_a = true;
                if(nagrywanie) nagrane_przyciski.add(klawisz_A); //jeśli nagrywamy, dodajemy przycisk do wektora
                break;
            case KeyEvent.VK_D:
                key_d = true;
                if(nagrywanie) nagrane_przyciski.add(klawisz_D);
                break;
            case KeyEvent.VK_W: 
                if(nagrywanie) nagrane_przyciski.add(klawisz_W);
                key_w = true;
                break;
            case KeyEvent.VK_S:
                if(nagrywanie) nagrane_przyciski.add(klawisz_S);
                key_s = true;
                break;
            case KeyEvent.VK_Q:
                if(nagrywanie) nagrane_przyciski.add(klawisz_Q);
                key_q = true;
                break;
            case KeyEvent.VK_E:
                if(nagrywanie) nagrane_przyciski.add(klawisz_E);
                key_e = true;
                break;
            case KeyEvent.VK_I:
                if(nagrywanie) nagrane_przyciski.add(klawisz_I);
                key_i = true;
                break;
            case KeyEvent.VK_K:
                if(nagrywanie) nagrane_przyciski.add(klawisz_K);
                key_k = true;
                break;
        }
        
        wykonajRuch();
    }

    public void keyReleased(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                key_a = false;
                break;
            case KeyEvent.VK_D:
                key_d = false;
                break;
            case KeyEvent.VK_W:
                key_w = false;
                break;
            case KeyEvent.VK_S:
                key_s = false;
                break;
            case KeyEvent.VK_Q:
                key_q = false;
                break;
            case KeyEvent.VK_E:
                key_e = false;
                break;
            case KeyEvent.VK_I:
                key_i = false;
                break;
            case KeyEvent.VK_K:
                key_k = false;
                break;
        }
        gra_dzwiek = false;  //gdy puścimy przycisk, dźwięk robota ustaje
        clip.stop();
    }

}
