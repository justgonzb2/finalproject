package org.nanohttpd;

/**
 *
 * @author justin
 */
public class status {

    // true is on, false is off
    private boolean on = true;

    public static status buildState(boolean value) {
        status state = new status();
        state.setOn(value);
        return state;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

}
