import javax.media.j3d.*;
import java.util.Enumeration;


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