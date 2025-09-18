package Components;

import Core.Entity;
import Editor.JCImGui;
import imgui.ImGui;
import imgui.type.ImInt;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component {

    private static int ID_COUNTER = 0;
    private int uid = -1;
    public transient Entity entity = null;

    public Component() {}

    public void start() {}

    public static void init(int maxID) {
        ID_COUNTER = maxID;
    }

    public void update(float dt) {}

    public void editorUpdate(float dt) {}

    public void imgui() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                if(Modifier.isTransient(field.getModifiers()))
                    continue;
                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if(isPrivate)
                    field.setAccessible(true);
                Class<?> type = field.getType();
                Object value = field.get(this);
                String name = field.getName();
                if(type == int.class) {
                    int val = (int)value;
                    field.set(this, JCImGui.drawIntControl(name, val));
                } else if(type == float.class) {
                    float val = (float)value;
                    field.set(this, JCImGui.drawFloatControl(name, val));
                } else if(type == boolean.class) {
                    boolean val = (boolean) value;
                    if(ImGui.checkbox(name + ": ", val))
                        field.set(this, !val);
                } else if(type == Vector2f.class) {
                    Vector2f val = (Vector2f) value;
                    JCImGui.drawVec2Control(name, val);
                }else if(type == Vector3f.class) {
                    Vector3f val = (Vector3f) value;
                    JCImGui.drawVec3Control(name, val);
                } else if(type == Vector4f.class) {
                    Vector4f val = (Vector4f) value;
                    JCImGui.drawColorControl4(name, val);
                } else if (type.isEnum()) {
                    @SuppressWarnings("unchecked")
                    Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) type;
                    String[] enumValues = getEnumValues(enumType);
                    String enumTypeName = ((Enum<?>) value).name();
                    ImInt index = new ImInt(indexOf(enumTypeName, enumValues));
                    if (ImGui.combo(field.getName(), index, enumValues, enumValues.length)) {
                        field.set(this, enumType.getEnumConstants()[index.get()]);
                    }
                }
                if(isPrivate)
                    field.setAccessible(false);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void destroy() {}

    public void generateID() {
        if(uid == -1)
            uid = ID_COUNTER++;
    }

    public int getID() {
        return uid;
    }

    private String[] getEnumValues(Class<? extends Enum<?>> enumType) {
        Enum<?>[] constants = enumType.getEnumConstants();
        String[] enumValues = new String[constants.length];
        for (int i = 0; i < constants.length; i++)
            enumValues[i] = constants[i].name();
        return enumValues;
    }

    private int indexOf(String string, String[] array) {
        for(int i = 0; i < array.length; i++) {
            if(string.equals(array[i]))
                return i;
        }
        return -1;
    }
}
