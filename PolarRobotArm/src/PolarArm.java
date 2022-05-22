import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import javax.media.j3d.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import javax.media.j3d.Transform3D;
import javax.vecmath.*;

// ActionListener - nasluchiwanie wcisnietych przyciskow
// KeyListener - nasluchiwanie wcisnietych klawiszy
public class Robot_3D extends JFrame implements ActionListener, KeyListener {

    BranchGroup glowna_scena = new BranchGroup();
    BoundingSphere bounds;
    SimpleUniverse simpleU;
    OrbitBehavior orbit; // musi być widoczny dla simpleU

    JButton reset_kamery = new JButton();
    JButton zacznij_nagrywanie = new JButton();
    JButton zakoncz_nagrywanie = new JButton();
    JButton odtworz_nagranie = new JButton();

    TransformGroup tg_podloga = new TransformGroup();
    TransformGroup tg_podstawka = new TransformGroup();
    TransformGroup tg_pierwszy_obraczacz = new TransformGroup();
    TransformGroup tg_pierwsze_ramie = new TransformGroup();
    TransformGroup tg_drugie_ramie = new TransformGroup();
    TransformGroup tg_pochylacz_chwytaka = new TransformGroup();
    TransformGroup tg_obraczacz_chwytaka = new TransformGroup();
    TransformGroup tg_chwytak = new TransformGroup();
    TransformGroup tg_kulka = new TransformGroup();

    Transform3D t3d_podloga = new Transform3D();
    Transform3D t3d_podstawka = new Transform3D();
    Transform3D t3d_pierwszy_obraczacz = new Transform3D();
    Transform3D t3d_pierwsze_ramie = new Transform3D();
    Transform3D t3d_drugie_ramie = new Transform3D();
    Transform3D t3d_pochylacz_chwytaka = new Transform3D();
    Transform3D t3d_obracacz_chwytaka = new Transform3D();
    Transform3D t3d_chwytak = new Transform3D();
    Transform3D t3d_kulka = new Transform3D();

    Transform3D t3d_podloga_nag = new Transform3D();
    Transform3D t3d_podstawka_nag = new Transform3D();
    Transform3D t3d_pierwszy_obraczacz_nag = new Transform3D();
    Transform3D t3d_pierwsze_ramie_nag = new Transform3D();
    Transform3D t3d_drugie_ramie_nag = new Transform3D();
    Transform3D t3d_pochylacz_chwytaka_nag = new Transform3D();
    Transform3D t3d_obracacz_chwytaka_nag = new Transform3D();
    Transform3D t3d_chwytak_nag = new Transform3D();
    Transform3D t3d_kulka_nag = new Transform3D();

    BranchGroup kulkaBranch = new BranchGroup();

    boolean nagrywanie;
    boolean odtwarzanie;
    Vector<KeyEvent> nagrane_przyciski = new Vector<KeyEvent>();

    boolean key_a;
    boolean key_d;
    boolean key_w;
    boolean key_s;
    boolean key_r;
    boolean key_f;
    boolean key_t;
    boolean key_g;
    boolean key_y;
    boolean key_h;
    boolean key_u;
    boolean key_j;

    CollisionDetectorGroup kolizja_kulki;
    CollisionDetectorGroup kolizja_chwytaka;
    CollisionDetectorGroup kolizja_podlogi;
    boolean podniesiona = false;
    boolean puszczona = false;
    boolean schwytany = false;

    private Timer grawitacjaTimer;

    float kat_pierwsze_ramie = 0.0f;
    float kat_drugie_ramie = 0.0f;
    float kat_pochylacz_chwytaka = 0.0f;
    float kat_obracacz_chwytaka = 0.0f;

    float kat_pierwsze_ramie_nag = 0.0f;
    float kat_drugie_ramie_nag = 0.0f;
    float kat_pochylacz_chwytaka_nag = 0.0f;
    float kat_obracacz_chwytaka_nag = 0.0f;

    /** Konstruktor klasy robota. Tworzone jest tutaj całe simpleUniverse, dodawane są zachowania kamery,
     * przyciski oraz rozmieszczenie elemetów w oknie JFrame **/
    Robot_3D() {
        super("Robot_3D Sebastian Krajna Kacper Bober");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setVisible(true);

        // utworzenie okna dla obiektów 3D
        Canvas3D comp = createCanvas3D(new Dimension(1000, 700));

        // umiejscowienie przyciskow, instrukcji oraz okna robota 3D
        add(BorderLayout.EAST, stworzPanelPrzyciskow());
        add(BorderLayout.WEST, dodanieInstrukcji());
        add(BorderLayout.CENTER, comp);

        // dodanie do okna nasluchiwanie klawiszy
        comp.addKeyListener(this);

        pack();

        // tworzenie sceny
        bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        BranchGroup scena = createSceneGraph(true);
        scena.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        // utworzenie czasu dla zasymulowania grawitacji dla opadajacej pilki
        grawitacjaTimer = new Timer();
        grawitacjaTimer.schedule(grawitacjaTimerTask, 0, 1);

        simpleU = new SimpleUniverse(comp);

        // tworzenie poruszania sceny
        createOrbitPlatform();
        simpleU.addBranchGraph(scena);
    }

    public BranchGroup createSceneGraph(boolean isInteractive) {

        glowna_scena.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        glowna_scena.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);

        stworzenieObiektow();
        dodanieSwiatla(glowna_scena);

        return glowna_scena;
    }

    /**Tworzone są tutaj wszystkie obiekty dodawane do sceny, podłączane są do nich kolizje oraz są one ustawiane
     * w odpowiednich pozycjach **/
    public void stworzenieObiektow() {

        Scene podloga_wczytanie = wczytajPlikRamienia("resources/podloga.obj");
        podloga_wczytanie.getSceneGroup().setUserData("podloga");

        tg_podloga.setTransform(t3d_podloga);
        tg_podloga.addChild(podloga_wczytanie.getSceneGroup());
        glowna_scena.addChild(tg_podloga);
        tg_podloga.setUserData("podloga");

        kolizja_podlogi = new CollisionDetectorGroup(tg_podloga,
                new BoundingBox(new Point3d(-10.0f, -0.01f, -10.23f), new Point3d(10.0f, 0f, 10.23f)));
        kolizja_podlogi.setSchedulingBounds(new BoundingSphere(new Point3d(), 20f));
        glowna_scena.addChild(kolizja_podlogi);

        /////////////////////////////////////////////////////////////////////

        Scene podstawka = wczytajPlikRamienia("resources/podstawka.obj");

        tg_podstawka.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg_podstawka.setTransform(t3d_podstawka);
        tg_podstawka.addChild(podstawka.getSceneGroup());
        glowna_scena.addChild(tg_podstawka);

        /////////////////////////////////////////////////////////////////////

        Scene pierwszy_obrot = wczytajPlikRamienia("resources/pierwszy_obraczacz.obj");

        tg_pierwszy_obraczacz.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg_pierwszy_obraczacz.addChild(pierwszy_obrot.getSceneGroup());

        Transform3D t3d_przesuniecie = new Transform3D();
        t3d_przesuniecie.set(new Vector3f(0, 0.32f, 0.0f)); // przesuwam obiekt z orgin na miejsce
        t3d_pierwszy_obraczacz.mul(t3d_przesuniecie);

        tg_pierwszy_obraczacz.setTransform(t3d_pierwszy_obraczacz);
        tg_podstawka.addChild(tg_pierwszy_obraczacz);

        /////////////////////////////////////////////////////////////////////

        Scene s_pierwsze_ramie = wczytajPlikRamienia("resources/pierwsze_ramiev2.obj");

        tg_pierwsze_ramie.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg_pierwsze_ramie.addChild(s_pierwsze_ramie.getSceneGroup());

        t3d_przesuniecie.set(new Vector3f(0, 0.24f, -0.13f)); // przesuwam obiekt z orgin na miejsce
        t3d_pierwsze_ramie.mul(t3d_przesuniecie);

        tg_pierwsze_ramie.setTransform(t3d_pierwsze_ramie);

        tg_pierwszy_obraczacz.addChild(tg_pierwsze_ramie);

        /////////////////////////////////////////////////////////////////////

        Scene s_drugie_ramie = wczytajPlikRamienia("resources/drugie_ramiev2.obj");

        tg_drugie_ramie.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg_drugie_ramie.addChild(s_drugie_ramie.getSceneGroup());

        t3d_przesuniecie.set(new Vector3f(0.04f, 0.75f, -0.02f)); // przesuwam obiekt z orgin na miejsce
        t3d_drugie_ramie.mul(t3d_przesuniecie);

        tg_drugie_ramie.setTransform(t3d_drugie_ramie);
        tg_pierwsze_ramie.addChild(tg_drugie_ramie);

        /////////////////////////////////////////////////////////////////////

        Scene s_pochylacz_chwytaka = wczytajPlikRamienia("resources/pochylacz_chwytakav2.obj");

        tg_pochylacz_chwytaka.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg_pochylacz_chwytaka.addChild(s_pochylacz_chwytaka.getSceneGroup());

        t3d_przesuniecie.set(new Vector3f(-0.05f, 0.00f, -0.75f)); // przesuwam obiekt z orgin na miejsce
        t3d_pochylacz_chwytaka.mul(t3d_przesuniecie);

        tg_pochylacz_chwytaka.setTransform(t3d_pochylacz_chwytaka);
        tg_drugie_ramie.addChild(tg_pochylacz_chwytaka);

        /////////////////////////////////////////////////////////////////////

        Scene s_obracacz_chwytaka = wczytajPlikRamienia("resources/obraczacz_chwytakav2.obj");

        tg_obraczacz_chwytaka.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg_obraczacz_chwytaka.addChild(s_obracacz_chwytaka.getSceneGroup());

        t3d_przesuniecie.set(new Vector3f(0.1f, -0.01f, -0.2f)); // przesuwam obiekt z orgin na miejsce
        t3d_obracacz_chwytaka.mul(t3d_przesuniecie);

        tg_obraczacz_chwytaka.setTransform(t3d_obracacz_chwytaka);
        tg_pochylacz_chwytaka.addChild(tg_obraczacz_chwytaka);

        /////////////////////////////////////////////////////////////////////

        Scene s_chwytak = wczytajPlikRamienia("resources/chwytakv2.obj");

        tg_chwytak.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg_chwytak.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tg_chwytak.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        tg_chwytak.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);


        tg_chwytak.addChild(s_chwytak.getSceneGroup());
        tg_chwytak.setUserData("chwytak");

        t3d_przesuniecie.set(new Vector3f(0.0f, 0.00f, -0.18f)); // przesuwam obiekt z orgin na miejsce
        t3d_chwytak.mul(t3d_przesuniecie);
        tg_chwytak.setTransform(t3d_przesuniecie);

        kolizja_chwytaka = new CollisionDetectorGroup(tg_chwytak,
                new BoundingSphere(new Point3d(0.0f, 0f, -0.23f), 0.04f)); // (0.09f, 1.3f, -1.28f)
        kolizja_chwytaka.setSchedulingBounds(new BoundingSphere(new Point3d(), 0.2f));
        glowna_scena.addChild(kolizja_chwytaka);

        tg_obraczacz_chwytaka.addChild(tg_chwytak);

        //////////////////////////////////////////////////////////////////////////

        Material kulkowy = new Material();
        kulkowy.setEmissiveColor(0.80f, 0.1f, 0.26f);
        kulkowy.setDiffuseColor(0.32f, 0.21f, 0.08f);
        kulkowy.setSpecularColor(0.45f, 0.32f, 0.21f);
        kulkowy.setShininess(38f);

        Appearance stylKulka = new Appearance();
        stylKulka.setMaterial(kulkowy);

        tg_kulka.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tg_kulka.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        t3d_przesuniecie.set(new Vector3f(0.0f, 0.35f, -1.78f)); // przesuwam obiekt z orgin na miejsce
        t3d_kulka.mul(t3d_przesuniecie);
        tg_kulka.setTransform(t3d_przesuniecie);
        Sphere kulka = new Sphere(0.2f, stylKulka);
        tg_kulka.addChild(kulka);
        tg_kulka.setUserData("kulka");

        kolizja_kulki = new CollisionDetectorGroup(tg_kulka,
                new BoundingSphere(new Point3d(0.0f, 0f, 0.0f), 0.2f)); // (0.09f, 1.3f, -1.28f)
        kolizja_kulki.setSchedulingBounds(new BoundingSphere(new Point3d(), 0.2f));
        tg_kulka.addChild(kolizja_kulki);

        kulkaBranch.setCapability(BranchGroup.ALLOW_DETACH);
        kulkaBranch.addChild(tg_kulka);
        glowna_scena.addChild(kulkaBranch);

    }

    public ViewingPlatform createOrbitPlatform() {

        ViewingPlatform viewingPlatform = simpleU.getViewingPlatform();
        viewingPlatform.setNominalViewingTransform();
        orbit = new OrbitBehavior(simpleU.getCanvas());
        orbit.setSchedulingBounds(bounds);
        orbit.setTranslateEnable(false);

        orbit.setRotFactors(0.5, 0.05);
        orbit.setReverseRotate(true);

        viewingPlatform.setViewPlatformBehavior(orbit);
        return viewingPlatform;
    }

    public static Canvas3D createCanvas3D(Dimension dim) {
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas3D = new Canvas3D(config);
        canvas3D.setPreferredSize(dim);
        return canvas3D;
    }

    public JPanel stworzPanelPrzyciskow() {
        JPanel panel_menu = new JPanel(new GridLayout(4, 1));

        reset_kamery.setText("Reset Kamery");
        reset_kamery.addActionListener(this);

        zacznij_nagrywanie.setText("Rozpocznij nagrywanie");
        zacznij_nagrywanie.addActionListener(this);

        zakoncz_nagrywanie.setText("Zakoncz nagrywanie");
        zakoncz_nagrywanie.addActionListener(this);

        odtworz_nagranie.setText("Odtworz nagranie");
        odtworz_nagranie.addActionListener(this);

        panel_menu.add(reset_kamery);
        panel_menu.add(zacznij_nagrywanie);
        panel_menu.add(zakoncz_nagrywanie);
        panel_menu.add(odtworz_nagranie);
        return panel_menu;
    }

    public static JPanel dodanieInstrukcji() {
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon("resources\\istrukcja_robota.jpg"));
        JPanel panel_instrukcji = new JPanel(new FlowLayout());
        panel_instrukcji.add(label);
        return panel_instrukcji;
    }


    /** Centrum wykrywania akcji, oraz nadawania animacji obiektom podczas odczytu odpowiednich przycisków z
     * klawiatury. Zaimplementowana została również możliwość nagrywania ruchu oraz jego odtwarzania **/
    public void actionPerformed(ActionEvent e) {

        //reset kamery realizowany przez przesunięcie obserwatora na wartość domyślną
        if (e.getSource() == reset_kamery) {
            Transform3D t = new Transform3D();
            Transform3D przesuniecie_obserwatora = new Transform3D();
            przesuniecie_obserwatora.set(new Vector3f(0.0f, 0.0f, 12.0f));
            przesuniecie_obserwatora.mul(t);

            simpleU.getViewingPlatform().getViewPlatformTransform().setTransform(przesuniecie_obserwatora);
        }

        // obsługa zdarzenia nagrywania. Jeżeli nagrywamy to usuwamy poprzednie nagranie oraz zapamiętujemy pozycje początkową
        if (e.getSource() == zacznij_nagrywanie) {

            nagrane_przyciski.clear();

            t3d_pierwszy_obraczacz_nag.set(t3d_pierwszy_obraczacz);
            t3d_pierwsze_ramie_nag.set(t3d_pierwsze_ramie);
            t3d_drugie_ramie_nag.set(t3d_drugie_ramie);
            t3d_pochylacz_chwytaka_nag.set(t3d_pochylacz_chwytaka);
            t3d_obracacz_chwytaka_nag.set(t3d_obracacz_chwytaka);
            t3d_chwytak_nag.set(t3d_chwytak);
            t3d_kulka_nag.set(t3d_kulka);


            kat_pierwsze_ramie_nag = kat_pierwsze_ramie;
            kat_drugie_ramie_nag = kat_drugie_ramie;
            kat_pochylacz_chwytaka_nag = kat_pochylacz_chwytaka;
            kat_obracacz_chwytaka_nag = kat_obracacz_chwytaka;

            nagrywanie = true;
        }

        if (e.getSource() == zakoncz_nagrywanie) {
            nagrywanie = false;
        }

        //gdy odtwarzamy nagranie to wracamy do zapamiętanej pozycji początkowej i realizujemy ruchy zapisane w wektorze
        //nagranej sekwencji przycisków klawiatury
        if (e.getSource() == odtworz_nagranie) {
            nagrywanie = false;
            odtwarzanie = true;

            t3d_pierwszy_obraczacz.set(t3d_pierwszy_obraczacz_nag);
            t3d_pierwsze_ramie.set(t3d_pierwsze_ramie_nag);
            t3d_drugie_ramie.set(t3d_drugie_ramie_nag);
            t3d_pochylacz_chwytaka.set(t3d_pochylacz_chwytaka_nag);
            t3d_obracacz_chwytaka.set(t3d_obracacz_chwytaka_nag);
            t3d_chwytak.set(t3d_chwytak_nag);
            t3d_kulka.set(t3d_kulka_nag);

            tg_pierwszy_obraczacz.setTransform(t3d_pierwszy_obraczacz_nag);
            tg_pierwsze_ramie.setTransform(t3d_pierwsze_ramie_nag);
            tg_drugie_ramie.setTransform(t3d_drugie_ramie_nag);
            tg_pochylacz_chwytaka.setTransform(t3d_pochylacz_chwytaka_nag);
            tg_obraczacz_chwytaka.setTransform(t3d_obracacz_chwytaka_nag);
            tg_chwytak.setTransform(t3d_chwytak_nag);
            tg_kulka.setTransform(t3d_kulka_nag);

            kat_pierwsze_ramie = kat_pierwsze_ramie_nag;
            kat_drugie_ramie = kat_drugie_ramie_nag;
            kat_pochylacz_chwytaka = kat_pochylacz_chwytaka_nag;
            kat_obracacz_chwytaka = kat_obracacz_chwytaka_nag;

            System.out.println(nagrane_przyciski.size());

            for (int i = 0; i < nagrane_przyciski.size(); i++) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

                keyPressed(nagrane_przyciski.elementAt(i));
                keyReleased(nagrane_przyciski.elementAt(i));
            }

            odtwarzanie = false;
        }
    }

    public void wykonajRuch() {

        // stworzenie przyciskow i nadanie im odpowiednich wartosci
        // aby moc dodac je do vectora i odtworzyc nagranie
        Button a = new Button("click");
        KeyEvent key_A = new KeyEvent(a, 1, 20, 1, KeyEvent.VK_A, 'A');
        KeyEvent key_D = new KeyEvent(a, 1, 20, 1, KeyEvent.VK_D, 'D');
        KeyEvent key_W = new KeyEvent(a, 1, 20, 1, KeyEvent.VK_W, 'W');
        KeyEvent key_S = new KeyEvent(a, 1, 20, 1, KeyEvent.VK_S, 'S');
        KeyEvent key_R = new KeyEvent(a, 1, 20, 1, KeyEvent.VK_R, 'R');
        KeyEvent key_F = new KeyEvent(a, 1, 20, 1, KeyEvent.VK_F, 'F');
        KeyEvent key_T = new KeyEvent(a, 1, 20, 1, KeyEvent.VK_T, 'T');
        KeyEvent key_G = new KeyEvent(a, 1, 20, 1, KeyEvent.VK_G, 'G');
        KeyEvent key_Y = new KeyEvent(a, 1, 20, 1, KeyEvent.VK_Y, 'Y');
        KeyEvent key_H = new KeyEvent(a, 1, 20, 1, KeyEvent.VK_H, 'H');
        KeyEvent key_U = new KeyEvent(a, 1, 20, 1, KeyEvent.VK_U, 'U');
        KeyEvent key_J = new KeyEvent(a, 1, 20, 1, KeyEvent.VK_J, 'J');
        KeyEvent key_I = new KeyEvent(a, 1, 20, 1, KeyEvent.VK_I, 'I');
        KeyEvent key_K = new KeyEvent(a, 1, 20, 1, KeyEvent.VK_K, 'K');

        Transform3D akcja = new Transform3D();

        //sekcja animowania ruchów pod wpływem naciśnięcia odpowiednich przycisków
        //sprawdzane są również warunki nagrywania oraz odtwarzania
        if (key_a == true) {
            akcja.rotY(Math.PI / 180);
            t3d_pierwszy_obraczacz.mul(akcja);
            tg_pierwszy_obraczacz.setTransform(t3d_pierwszy_obraczacz);
            if(podniesiona == false && 
                kolizja_chwytaka.czyKolizja() && ( kolizja_podlogi.czyKolizja() || kolizja_kulki.czyKolizja())){   
                akcja.rotY(-Math.PI / 180 * 2);
                t3d_pierwszy_obraczacz.mul(akcja);
                tg_pierwszy_obraczacz.setTransform(t3d_pierwszy_obraczacz);
            }
            else if (odtwarzanie == false)
                nagrane_przyciski.add(key_A);
        }
        if (key_d == true) {
            akcja.rotY(-Math.PI / 180);
            t3d_pierwszy_obraczacz.mul(akcja);
            tg_pierwszy_obraczacz.setTransform(t3d_pierwszy_obraczacz);
            if(podniesiona == false && kolizja_chwytaka.czyKolizja() && ( kolizja_podlogi.czyKolizja() || kolizja_kulki.czyKolizja())){   
                akcja.rotY(Math.PI / 180 * 2);
                t3d_pierwszy_obraczacz.mul(akcja);
                tg_pierwszy_obraczacz.setTransform(t3d_pierwszy_obraczacz);
            }
            else if (odtwarzanie == false)
                nagrane_przyciski.add(key_D);
        }

        //////////////////////////////////////////////////////////////////////////

        if (key_w == true && kat_pierwsze_ramie < Math.PI / 2) {
            kat_pierwsze_ramie += Math.PI / 180;
            akcja.rotX(Math.PI / 180);
            t3d_pierwsze_ramie.mul(akcja);
            tg_pierwsze_ramie.setTransform(t3d_pierwsze_ramie);
            if(podniesiona == false && kolizja_chwytaka.czyKolizja() && ( kolizja_podlogi.czyKolizja() || kolizja_kulki.czyKolizja())){
                kat_pierwsze_ramie -= Math.PI / 180;
                akcja.rotX(-Math.PI / 180);
                t3d_pierwsze_ramie.mul(akcja);
                tg_pierwsze_ramie.setTransform(t3d_pierwsze_ramie);
            } 
            else if (odtwarzanie == false)
                nagrane_przyciski.add(key_W);
        }
        if (key_s == true && -Math.PI / 2 < kat_pierwsze_ramie) {
            kat_pierwsze_ramie -= Math.PI / 180;
            akcja.rotX(-Math.PI / 180);
            t3d_pierwsze_ramie.mul(akcja);
            tg_pierwsze_ramie.setTransform(t3d_pierwsze_ramie);           
            if(podniesiona == false && kolizja_chwytaka.czyKolizja() && ( kolizja_podlogi.czyKolizja() || kolizja_kulki.czyKolizja())){
                kat_pierwsze_ramie += Math.PI / 180 * 2;
                akcja.rotX(Math.PI / 180 * 2);
                t3d_pierwsze_ramie.mul(akcja);
                tg_pierwsze_ramie.setTransform(t3d_pierwsze_ramie);
            }
            else if (odtwarzanie == false)
                nagrane_przyciski.add(key_S);
        }

        //////////////////////////////////////////////////////////////////////////

        if (key_r == true && kat_drugie_ramie < Math.PI * 2 / 4) {
            kat_drugie_ramie += Math.PI / 180;
            akcja.rotX(Math.PI / 180);
            t3d_drugie_ramie.mul(akcja);
            tg_drugie_ramie.setTransform(t3d_drugie_ramie);
            if(podniesiona == false && kolizja_chwytaka.czyKolizja() && ( kolizja_podlogi.czyKolizja() || kolizja_kulki.czyKolizja())){
                kat_drugie_ramie -= Math.PI / 180 * 2;
                akcja.rotX(-Math.PI / 180 * 2);
                t3d_drugie_ramie.mul(akcja);
                tg_drugie_ramie.setTransform(t3d_drugie_ramie);
            }
            else if (odtwarzanie == false)
                nagrane_przyciski.add(key_R);
        }
        if (key_f == true && -Math.PI * 2 / 4 < kat_drugie_ramie) {
            kat_drugie_ramie -= Math.PI / 180;
            akcja.rotX(-Math.PI / 180);
            t3d_drugie_ramie.mul(akcja);
            tg_drugie_ramie.setTransform(t3d_drugie_ramie);
            if(podniesiona == false && kolizja_chwytaka.czyKolizja() && ( kolizja_podlogi.czyKolizja() || kolizja_kulki.czyKolizja())){
                kat_drugie_ramie += Math.PI / 180 * 2;
                akcja.rotX(Math.PI / 180 * 2);
                t3d_drugie_ramie.mul(akcja);
                tg_drugie_ramie.setTransform(t3d_drugie_ramie);
            }
            else if (odtwarzanie == false)
                nagrane_przyciski.add(key_F);
        }

        //////////////////////////////////////////////////////////////////////////

        if (key_t == true && kat_pochylacz_chwytaka < Math.PI * 3 / 4) {
            kat_pochylacz_chwytaka += Math.PI / 180;
            akcja.rotX(Math.PI / 180);
            t3d_pochylacz_chwytaka.mul(akcja);
            tg_pochylacz_chwytaka.setTransform(t3d_pochylacz_chwytaka);
            if(podniesiona == false && kolizja_chwytaka.czyKolizja() && ( kolizja_podlogi.czyKolizja() || kolizja_kulki.czyKolizja())){
                kat_pochylacz_chwytaka -= Math.PI / 180 * 2;
                akcja.rotX(-Math.PI / 180 * 2);
                t3d_pochylacz_chwytaka.mul(akcja);
                tg_pochylacz_chwytaka.setTransform(t3d_pochylacz_chwytaka);
            }
            else if (odtwarzanie == false)
                nagrane_przyciski.add(key_T);
        }
        if (key_g == true && -Math.PI * 3 / 4 < kat_pochylacz_chwytaka) {
            kat_pochylacz_chwytaka -= Math.PI / 180;
            akcja.rotX(-Math.PI / 180);
            t3d_pochylacz_chwytaka.mul(akcja);
            tg_pochylacz_chwytaka.setTransform(t3d_pochylacz_chwytaka);
            if(podniesiona == false && kolizja_chwytaka.czyKolizja() && ( kolizja_podlogi.czyKolizja() || kolizja_kulki.czyKolizja())){
                kat_pochylacz_chwytaka += Math.PI / 180 * 2;
                akcja.rotX(Math.PI / 180 * 2);
                t3d_pochylacz_chwytaka.mul(akcja);
                tg_pochylacz_chwytaka.setTransform(t3d_pochylacz_chwytaka);
            }
            else if (odtwarzanie == false)
                nagrane_przyciski.add(key_G);
        }

        //////////////////////////////////////////////////////////////////////////

        if (key_y == true && kat_obracacz_chwytaka < Math.PI * 3 / 4) {
            kat_obracacz_chwytaka += Math.PI / 180;
            akcja.rotY(Math.PI / 180);
            t3d_obracacz_chwytaka.mul(akcja);
            tg_obraczacz_chwytaka.setTransform(t3d_obracacz_chwytaka);
            if(podniesiona == false && kolizja_chwytaka.czyKolizja() && ( kolizja_podlogi.czyKolizja() || kolizja_kulki.czyKolizja())){
                kat_obracacz_chwytaka -= Math.PI / 180 * 2;
                akcja.rotY(-Math.PI / 180 * 2);
                t3d_obracacz_chwytaka.mul(akcja);
                tg_obraczacz_chwytaka.setTransform(t3d_obracacz_chwytaka);
            }
            else if (odtwarzanie == false)
                nagrane_przyciski.add(key_Y);
        }
        if (key_h == true && -Math.PI * 3 / 4 < kat_obracacz_chwytaka) {
            kat_obracacz_chwytaka -= Math.PI / 180;
            akcja.rotY(-Math.PI / 180);
            t3d_obracacz_chwytaka.mul(akcja);
            tg_obraczacz_chwytaka.setTransform(t3d_obracacz_chwytaka);
            if(podniesiona == false && kolizja_chwytaka.czyKolizja() && ( kolizja_podlogi.czyKolizja() || kolizja_kulki.czyKolizja())){
                kat_obracacz_chwytaka += Math.PI / 180 * 2;
                akcja.rotY(Math.PI / 180 * 2);
                t3d_obracacz_chwytaka.mul(akcja);
                tg_obraczacz_chwytaka.setTransform(t3d_obracacz_chwytaka);
            }
            else if (odtwarzanie == false)
                nagrane_przyciski.add(key_H);
        }

        //////////////////////////////////////////////////////////////////////////

        if (key_u == true) {
            akcja.rotZ(Math.PI / 180);
            t3d_chwytak.mul(akcja);
            tg_chwytak.setTransform(t3d_chwytak);
            if (odtwarzanie == false)
                nagrane_przyciski.add(key_U);
        }
        if (key_j == true) {
            akcja.rotZ(-Math.PI / 180);
            t3d_chwytak.mul(akcja);
            tg_chwytak.setTransform(t3d_chwytak);
            if (odtwarzanie == false)
                nagrane_przyciski.add(key_J);
        }

        //sekcja odpowiedzialna za podnoszenie kulki gdy chwytak będzie dotykał kulki oraz naciśnięty przycisk podnoszenia

        if (podniesiona == true && schwytany == false) {
            schwytany = true;
            glowna_scena.removeChild(kulkaBranch);
            tg_chwytak.addChild(kulkaBranch);
            Transform3D t = new Transform3D();
            t.set(new Vector3f(0.0f, 0.0f, -0.31f)); // przesuwam obiekt z orgin na miejsce
            tg_kulka.setTransform(t);

            if (odtwarzanie == false)
                nagrane_przyciski.add(key_I);
        }

        if(puszczona == true  && schwytany == true){
            schwytany = false;
            Transform3D t = new Transform3D();
            Transform3D tt = new Transform3D();
            tg_chwytak.removeChild(kulkaBranch);
            t.mul(t3d_pierwszy_obraczacz);
            t.mul(t3d_pierwsze_ramie);
            t.mul(t3d_drugie_ramie);
            t.mul(t3d_pochylacz_chwytaka);
            t.mul(t3d_obracacz_chwytaka);
            t.mul(t3d_chwytak);
            tt.set(new Vector3f(0.0f, 0.0f, -0.31f));
            t.mul(tt);
            t3d_kulka.set(t);
            tg_kulka.setTransform(t);
            glowna_scena.addChild(kulkaBranch);
            if (odtwarzanie == false)
                nagrane_przyciski.add(key_K);
        }
    }

    /** wykrywania Eventu przyciśnięcia przycisku **/
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A: // pierwszy_obraczaczz
                key_a = true;
                break;
            case KeyEvent.VK_D:
                key_d = true;
                break;
            case KeyEvent.VK_W: // pierwsze_ramie
                key_w = true;
                break;
            case KeyEvent.VK_S:
                key_s = true;
                break;
            case KeyEvent.VK_R: // drugie_ramie
                key_r = true;
                break;
            case KeyEvent.VK_F:
                key_f = true;
                break;
            case KeyEvent.VK_T: // pochylacz_chwytaka
                key_t = true;
                break;
            case KeyEvent.VK_G:
                key_g = true;
                break;
            case KeyEvent.VK_Y: // obracacz_chwytaka
                key_y = true;
                break;
            case KeyEvent.VK_H:
                key_h = true;
                break;
            case KeyEvent.VK_U: // chwytak
                key_u = true;
                break;
            case KeyEvent.VK_J:
                key_j = true;
                break;
            case KeyEvent.VK_I:
                podniesiona = true;
                break;
            case KeyEvent.VK_K:
                puszczona = true;
                break;

        }
        wykonajRuch();
    }
    /** wykrywania Eventu puszczenia przycisku **/
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
            case KeyEvent.VK_R:
                key_r = false;
                break;
            case KeyEvent.VK_F:
                key_f = false;
                break;
            case KeyEvent.VK_T:
                key_t = false;
                break;
            case KeyEvent.VK_G:
                key_g = false;
                break;
            case KeyEvent.VK_Y:
                key_y = false;
                break;
            case KeyEvent.VK_H:
                key_h = false;
                break;
            case KeyEvent.VK_U:
                key_u = false;
                break;
            case KeyEvent.VK_J:
                key_j = false;
                break;
            case KeyEvent.VK_I:
                podniesiona = false;
                break;
            case KeyEvent.VK_K:
                puszczona = false;
                break;
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    /** wczytanie zewnętrznych plików 3d **/
    public Scene wczytajPlikRamienia(String filename) {
        ObjectFile loader = new ObjectFile();
        Scene s = null;
        File file = new java.io.File(filename);
        try {
            s = loader.load(file.toURI().toURL());
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
        return s;
    }

    public void dodanieSwiatla(BranchGroup glowna_scena) {
        Color3f ambientColor = new Color3f(0.0f, 0.0f, 0.0f);
        AmbientLight ambientLightNode = new AmbientLight(ambientColor);
        ambientLightNode.setInfluencingBounds(bounds);
        glowna_scena.addChild(ambientLightNode);

        Color3f light1Color = new Color3f(1.0f, 1.0f, 1.0f);
        Vector3f light1Direction = new Vector3f(1.0f, 1.0f, 1.0f);
        Color3f light2Color = new Color3f(1.0f, 1.0f, 1.0f);
        Vector3f light2Direction = new Vector3f(-1.0f, -1.0f, -1.0f);

        DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
        light1.setInfluencingBounds(bounds);
        glowna_scena.addChild(light1);

        DirectionalLight light2 = new DirectionalLight(light2Color, light2Direction);
        light2.setInfluencingBounds(bounds);
        glowna_scena.addChild(light2);
    }

    TimerTask grawitacjaTimerTask = new TimerTask() {
        public void run() {

            // spadanie pileczki po puszczeniu
            if(schwytany == false && !kolizja_podlogi.czyKolizja()){
                Transform3D t = new Transform3D();
                t.set(new Vector3f(0f, -0.001f, 0.0f));
                t3d_kulka.mul(t);
                tg_kulka.setTransform(t3d_kulka);
            }
        }
    };

    public static void main(String[] args) {
        new Robot_3D();
    }

}
