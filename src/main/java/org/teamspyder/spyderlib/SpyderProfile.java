package org.teamspyder.spyderlib;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class SpyderProfile {
    public static class Button {
        private final int id;
        private final Joystick controller;

        public Button(int id, Joystick controller) {
            this.id = id;
            this.controller = controller;
        }

        /**
         * Gets the ID of the axis
         * @return The ID of the axis
         */
        public int getID() {
            return id;
        }

        /**
         * Gets the Joystick object
         * @return The Joystick object
         */
        public Joystick getController() {
            return controller;
        }

        /**
         * Gets the JoystickButton object
         * @return The JoystickButton object
         */
        public JoystickButton getJoystickButton() {
            return new JoystickButton(controller, id);
        }

        /**
         * Gets button state (whether it's pressed or not)
         * @return Button state
         */
        public boolean getButtonState() {
            return controller.getRawButton(id);
        }

        /**
         * Gets whether the button has been pressed or not (NOTE: Not the same as getButtonState(), this will only return true once per press)
         * @return Whether the button has been pressed
         */
        public boolean getButtonPressed() {
            return controller.getRawButtonPressed(id);
        }
    }

    public static class Stick {
        private final int ID;
        private final Joystick controller;

        public Stick(int ID, Joystick controller) {
            this.ID = ID;
            this.controller = controller;
        }

        /**
         * Gets the ID of the axis
         * @return The ID of the axis
         */
        public int getID() {
            return ID;
        }

        /**
         * Gets the Joystick object
         * @return The Joystick object
         */
        public Joystick getController() {
            return controller;
        }

        /**
         * Gets the value of the stick
         * @return Stick value (-1 to 1)
         */
        public double getAxisState() {
            return controller.getRawAxis(ID);
        }
    }

    public static class Controller {
        public static final Joystick DRIVER = new Joystick(0);
        public static final Joystick OPERATOR = new Joystick(1);
        public static final Joystick TESTING = new Joystick(2);
    }

    protected static class ControllerAxis {
        public static final int LEFT_X = 0;
        public static final int LEFT_Y = 1;
        public static final int RIGHT_X = 2;
        public static final int RIGHT_Y = 3;
    }

    protected static class ControllerButton {
        public static final int X = 1;
        public static final int A = 2;
        public static final int B = 3;
        public static final int Y = 4;
        public static final int LEFT_BUMPER = 5;
        public static final int RIGHT_BUMPER = 6;
        public static final int LEFT_TRIGGER = 7;
        public static final int RIGHT_TRIGGER = 8;
        public static final int BACK = 9;
        public static final int START = 10;
        public static final int LEFT_STICK_BUTTON = 11;
        public static final int RIGHT_STICK_BUTTON = 12;
    }

    protected static class ControllerDPad {
        public static final int UNPRESSED = -1;
        public static final int UP = 0;
        public static final int UP_RIGHT = 45;
        public static final int RIGHT = 90;
        public static final int DOWN_RIGHT = 135;
        public static final int DOWN = 180;
        public static final int DOWN_LEFT = 225;
        public static final int LEFT = 270;
        public static final int UP_LEFT = 315;
    }

    /**
     * Rumble the specified controller
     * @param controller Controller to rumble
     * @param rumbleType Type of rumble to do (left or right)
     * @param value Rumble strength (0 to 1)
     */
    public void rumble(Joystick controller, GenericHID.RumbleType rumbleType, double value) {
        controller.setRumble(rumbleType, value);
    }
}