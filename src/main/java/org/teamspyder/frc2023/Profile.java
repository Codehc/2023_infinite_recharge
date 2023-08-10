package org.teamspyder.frc2023;

import java.util.ResourceBundle.Control;

import org.teamspyder.spyderlib.SpyderProfile;

public class Profile extends SpyderProfile {
    private static Profile instance;

    public static Profile getProfile() {
        if (instance == null) {
            instance = new Profile();
        }
        return instance;
    }

    public final Stick TRANSLATE_X;
    public final Stick TRANSLATE_Y;
    public final Stick ROTATE;
    public final Stick LEFT_Y;
    public final Stick RIGHT_Y;
    public final Button SLOW_MODE;
    public final Button FORCE_RESET_ODO;
    public final Button ONE_BUTTON;
    public final Button TWO_BUTTON;
    public final Button THREE_BUTTON;
    public final Button FOUR_BUTTON;


    private Profile() {
        TRANSLATE_Y = new Stick(ControllerAxis.LEFT_X, Controller.DRIVER);
        TRANSLATE_X = new Stick(ControllerAxis.RIGHT_Y, Controller.OPERATOR);

        LEFT_Y = new Stick(ControllerAxis.LEFT_Y, Controller.DRIVER);
        RIGHT_Y = new Stick(ControllerAxis.RIGHT_X, Controller.DRIVER);

        ONE_BUTTON = new Button(ControllerButton.X, Controller.DRIVER);
        TWO_BUTTON = new Button(ControllerButton.A, Controller.DRIVER);
        THREE_BUTTON = new Button(ControllerButton.B, Controller.DRIVER);
        FOUR_BUTTON = new Button(ControllerButton.Y, Controller.DRIVER);

        ROTATE = new Stick(ControllerAxis.RIGHT_X, Controller.DRIVER);
        SLOW_MODE = new Button(ControllerButton.LEFT_TRIGGER, Controller.DRIVER);

        FORCE_RESET_ODO = new Button(ControllerButton.LEFT_TRIGGER, Controller.OPERATOR);
    }
}