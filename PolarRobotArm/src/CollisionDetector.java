
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.TransformGroup;

import javax.media.j3d.*;
import java.util.Enumeration;


/** Klasa wykrywania kolizji pozwala ona na wykrywanie kolizji w obiekcie klasy TransformGroup oraz odczytanie
 * obecnego stany tej kolizji **/
public class CollisionDetector extends Behavior {


    private boolean inCollision = false;
    private Group group;

    private WakeupOnCollisionEntry wEnter;
    private WakeupOnCollisionExit wExit;


    public CollisionDetector(Group group_given, Bounds bounds) {
        group = group_given;
        group_given.setCollisionBounds(bounds);
        inCollision = false;
    }

    public void initialize() {
        wEnter = new WakeupOnCollisionEntry(group);
        wExit = new WakeupOnCollisionExit(group);
        wakeupOn(wEnter);
    }

    public void processStimulus(Enumeration criteria) {
        inCollision = !inCollision;

        if (inCollision) {
            System.out.println("Kolizja       : " + group.getUserData());
            wakeupOn(wExit);
        }
        else {
            System.out.println("Nie ma kolizji: "  + group.getUserData());
            wakeupOn(wEnter);
        }
    }

    public boolean czyKolizja(){
        if(inCollision) return true;
        else return false;
    }



}
