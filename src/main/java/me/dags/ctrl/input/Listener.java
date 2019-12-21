package me.dags.ctrl.input;

import java.util.function.Consumer;

public interface Listener {

    void handle(State state);

    default Listener requireCtrl() {
        Listener current = this;
        return state -> {
            if (Input.isCtrlDown()) {
                current.handle(state);
            }
        };
    }

    default Listener requireShift() {
        Listener current = this;
        return state -> {
            if (Input.isShiftDown()) {
                current.handle(state);
            }
        };
    }

    static Listener held(Runnable runnable) {
        return state -> {
            if (state == State.HELD) {
                runnable.run();
            }
        };
    }

    static Listener pressed(Runnable runnable) {
        return state -> {
            if (state == State.PRESSED) {
                runnable.run();
            }
        };
    }

    static Listener scrolled(Consumer<State> consumer) {
        return state -> {
            if (state == State.SCROLL_DOWN || state == State.SCROLL_UP) {
                consumer.accept(state);
            }
        };
    }
}
