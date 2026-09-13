package org.portmaster.heroesofloot2;

import com.badlogic.gdx.Input;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/** Keep cursor polling and game-requested cursor warps in the same space as input events. */
public final class CoordinateInput {
    private CoordinateInput() {}
    public static Input wrap(final Input physical, final DisplayLayout layout) {
        return (Input)Proxy.newProxyInstance(CoordinateInput.class.getClassLoader(),
            new Class<?>[]{Input.class}, (self, method, args) -> {
                String name = method.getName();
                try {
                    if (name.equals("getX")) return layout.inputX((Integer)method.invoke(physical,args));
                    if (name.equals("getY")) return layout.inputY((Integer)method.invoke(physical,args));
                    if (name.equals("getDeltaX")) return (int)Math.round((Integer)method.invoke(physical,args) * (double)layout.gameWidth / layout.width);
                    if (name.equals("getDeltaY")) return (int)Math.round((Integer)method.invoke(physical,args) * (double)layout.gameHeight / layout.height);
                    if (name.equals("setCursorPosition"))
                        args = new Object[]{layout.cursorX((Integer)args[0]),layout.cursorY((Integer)args[1])};
                    return method.invoke(physical,args);
                } catch (InvocationTargetException e) { throw e.getCause(); }
            });
    }
}
