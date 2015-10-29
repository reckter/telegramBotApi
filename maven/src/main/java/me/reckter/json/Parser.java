package me.reckter.json;

import me.reckter.telegram.model.Error;
import org.json.JSONArray;
import org.json.JSONObject;
import me.reckter.telegram.Telegram;
import me.reckter.telegram.model.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author hannes
 */
public class Parser {


    private Map<String, Class<?>> parsebles = new HashMap<>();

    private Telegram telegram;

    public Parser(Telegram telegram) {
        this.telegram = telegram;
        staticAddParsables();
    }

    public void staticAddParsables() {

        addParsable(Message.class);
        addParsable(User.class);
        addParsable(Response.class);
        addParsable(GroupChat.class);
        addParsable(Update.class);
        addParsable(Contact.class);
        addParsable(Error.class);
    }

    public void addParsable(Class<?> clazz) {
        addParsable(clazz.getSimpleName(), clazz);
    }

    public void addParsable(String name, Class<?> clazz) {
        parsebles.put(name.toLowerCase(), clazz);
    }

    public Object parse(String json) {
        return parse(new JSONObject(json));
    }

    public boolean canBeParsed(JSONObject json) {
        return parsebles.values().stream().anyMatch(a -> isParsable(json, a));
    }

    public Object parse(JSONObject json) {
        try {
            return apply(json, parsebles.values().stream().filter(a -> isParsable(json, a)).findAny().get());
        } catch (NoSuchElementException e) {
            throw new RuntimeException("couldn't find a suitable class for " + json, e);
        }
    }

    private Field getDeclaredFieldInherited(Class clazz, String name) {
        Class original = clazz;
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
            }
            clazz = clazz.getSuperclass();
        }
        throw new RuntimeException("could not find Field " + name + " in class " + original + " inheritance tree");
    }

    private Field[] getDeclaredFieldsInherited(Class clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        Field[] ret = new Field[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            ret[i] = fields.get(i);
        }
        return ret;
    }

    private Map<String, Field> getJsonFields(Class<?> clazz, boolean withOptionals) {
        Map<String, Field> ret = new HashMap<>();
        Field[] fields = getDeclaredFieldsInherited(clazz);
        for (Field field : fields) {
            String name = field.getName();
            if (field.isAnnotationPresent(JsonIgnore.class) || (!withOptionals && field.isAnnotationPresent(JsonOptional.class))) {
                continue;
            }
            if (field.isAnnotationPresent(JsonName.class)) {
                name = field.getDeclaredAnnotation(JsonName.class).value();
            }
            ret.put(name, field);
        }
        return ret;
    }

    private boolean isParsable(JSONObject json, Class<?> clazz) {

        Map<String, Field> jsonNames = getJsonFields(clazz, true);
        return getJsonFields(clazz, false).keySet().stream().allMatch(a -> json.keySet().contains(a))
                && json.keySet().stream().allMatch(a -> jsonNames.keySet().contains(a));
    }

    private void injectTelegramObject(Object object) {
        injectObject(object, telegram, "telegram");
    }

    private void injectObject(Object object, Object objectToInject, String name) {

        Class clazz = object.getClass();

        boolean foundField = false;

        while (clazz != null) {

            try {
                Field telegramField = getDeclaredFieldInherited(clazz, name);
                telegramField.setAccessible(true);
                telegramField.set(object, objectToInject);
                return;
            } catch (IllegalAccessException ignored) {
            }
            clazz = clazz.getSuperclass();
        }

        //noinspection UnnecessaryLocalVariable
        RuntimeException up = new RuntimeException("could not inject object " + objectToInject + " in " + name + ":" + object);
        throw up; //hrhr
    }

    private <T> T apply(JSONObject json, Class<T> clazz) {
        T object = null;
        try {
            Constructor<T> constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            object = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("could not initilaize class " + clazz.getName() + " while parsing from json", e);
        }

        injectTelegramObject(object);


        Map<String, Field> jsonNames = getJsonFields(clazz, true);

        for (String name : json.keySet()) {
            try {
                Field field = jsonNames.get(name);
                field.setAccessible(true);
                Class<?> type = field.getType();
                String tmp = type.getTypeName();
                String getFunctionName = "get" + tmp.substring(0, 1).toUpperCase() + tmp.substring(1);


                //lets look if we have a get method for the type (primtives)
                boolean foundGetMethod = false;
                try {
                    Method getFunction = JSONObject.class.getDeclaredMethod(getFunctionName, String.class);

                    foundGetMethod = true;
                    field.set(object, getFunction.invoke(json, name));

                } catch (NoSuchMethodException ignored) {
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                //If we did not find a get method
                if (!foundGetMethod) {

                    Object tmpObj = json.get(name);
                    if ((tmpObj instanceof JSONObject && canBeParsed(json.getJSONObject(name)) || tmpObj instanceof JSONArray)) {

                        if (field.getType().isArray()) {
                            List<Object> array = new ArrayList<>();

                            if (json.optJSONArray(name) == null) {
                                array.add(parse(json.getJSONObject(name)));
                            } else {
                                // we have an array, so we fetch a JSONarray and parse it manualy
                                JSONArray jsonArray = json.getJSONArray(name);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    array.add(parse(jsonArray.getJSONObject(i)));
                                }
                            }

                            field.set(object, array.toArray());
                        } else {
                            field.set(object, parse(json.getJSONObject(name)));
                        }
                    } else {
                        field.set(object, json.get(name));
                    }
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return object;
    }
}
