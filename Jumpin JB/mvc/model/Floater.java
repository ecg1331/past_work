package edu.uchicago.gerber._08final.mvc.model;

import java.awt.*;

public abstract class Floater extends Sprite {

    public Floater() {

        setTeam(Team.FLOATER);
        //default values, all of which can be overridden in the extending concrete classes
        setExpiry(250);
        setColor(Color.WHITE);
        setSpin(somePosNegValue(10));
        setDeltaX(-10);
        setDeltaY(0);
    }
}