package me.dags.ctrl.input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.LinkedList;
import java.util.List;

public abstract class Input {

    private final int id;
    private final List<Listener> listeners = new LinkedList<>();

    private boolean active;

    public Input(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void update() {
        boolean wasActive = active;
        active = isActive();
        event(getState(active, wasActive));
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    private void event(State state) {
        for (Listener listener : listeners) {
            listener.handle(state);
        }
    }

    protected abstract boolean isActive();

    protected State getState(boolean active, boolean wasActive) {
        if (wasActive) {
            if (active) {
                return State.HELD;
            } else {
                return State.RELEASED;
            }
        } else {
            if (active) {
                return State.PRESSED;
            } else {
                return State.NONE;
            }
        }
    }

    private static Input listen(Input input, Listener listener) {
        input.addListener(listener);
        return input;
    }

    public static Input key(int id, Listener listener) {
        return listen(new Key(id), listener);
    }

    public static Input button(int id, Listener listener) {
        return listen(new MouseButton(id), listener);
    }

    public static Input scroll(Listener listener) {
        return listen(new MouseScroll(), listener);
    }

    public static boolean isCtrlDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
    }

    public static boolean isShiftDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
    }

    private static class Key extends Input {

        private Key(int id) {
            super(id);
        }

        @Override
        protected boolean isActive() {
            return Keyboard.isKeyDown(getId());
        }
    }

    private static class MouseButton extends Input {

        private MouseButton(int id) {
            super(id);
        }

        @Override
        protected boolean isActive() {
            return Mouse.isButtonDown(getId());
        }
    }

    private static class MouseScroll extends Input {

        private long timestamp = 0L;
        private int scroll = 0;

        private MouseScroll() {
            super(-1);
        }

        @Override
        protected boolean isActive() {
            long now = System.currentTimeMillis();
            int state = Integer.compare(Mouse.getDWheel(), 0);

            if (state != 0) {
                if (state != scroll || (now - timestamp) > 1000L) {
                    timestamp = now;
                    scroll = state;
                    return true;
                }
            }

            return false;
        }

        @Override
        protected State getState(boolean active, boolean wasActive) {
            if (active && !wasActive) {
                if (scroll < 0) {
                    return State.SCROLL_DOWN;
                }
                if (scroll > 0) {
                    return State.SCROLL_UP;
                }
            }
            return State.NONE;
        }
    }
}
