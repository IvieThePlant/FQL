package src;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.lang.reflect.Field;

public abstract class DatabaseModel<Model extends DatabaseModel<Model>> {
    // id == 0 means "not yet persisted". Persisted records will get id > 0.
    public int id = 0;

    // static map of helpers per model class
    private static final HashMap<Class<?>, DatabaseHelper<?>> HELPERS = new HashMap<>();

    // Subclasses must have a public no-arg constructor.
    public DatabaseModel() {}

    // save(): add or update db based on id
    @SuppressWarnings("unchecked")
    public void save() {
        DatabaseHelper<Model> h = helper();
        if (this.id <= 0) {
            h.add((Model) this);
        } else {
            h.update((Model) this);
        }
    }
    // delete(): delete from db based on id
    @SuppressWarnings("unchecked")
    public void delete() {
        DatabaseHelper<Model> h = helper();
        if (this.id > 0) {
            h.delete((Model) this);
        }
    }

    // get or create helper instance for this model class
    @SuppressWarnings("unchecked")
    private DatabaseHelper<Model> helper() {
        // get cached helper from map, otherwise create new one
        DatabaseHelper<Model> helper = (DatabaseHelper<Model>) HELPERS.get(this.getClass());

        if (helper != null) {
            return helper;
        } else {
            try {
                helper = new DatabaseHelper<>((Class<Model>) this.getClass(), columnsFromClass(this.getClass()));
                HELPERS.put(this.getClass(), helper);
                return helper;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create helper for " + this.getClass().getSimpleName(), e);
            }
        }
    }

    // get or create helper instance for another model class
    @SuppressWarnings("unchecked")
    private static <AnotherModel extends DatabaseModel<AnotherModel>> DatabaseHelper<AnotherModel> helperFor(Class<AnotherModel> otherClass) {
        // get cached helper from map, otherwise create new one
        DatabaseHelper<AnotherModel> helper = (DatabaseHelper<AnotherModel>) HELPERS.get(otherClass);

        if (helper != null) {
            return helper;
        } else {
            try {
                helper = new DatabaseHelper<>((Class<AnotherModel>) otherClass, columnsFromClass(otherClass));
                HELPERS.put(otherClass, helper);
                return helper;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create helper for " + otherClass.getSimpleName(), e);
            }
        }
    }

    private static String[] columnsFromClass(Class<?> cls) {
        ArrayList<String> columns = new ArrayList<>();
        for (Field field : cls.getFields()) {
            if (!field.getName().equals("id")) {
                columns.add(field.getName());
            }
        }
        return columns.toArray(new String[0]);
    }

    // Matching by params uses reflection by default. Subclasses may override for custom logic.
    public boolean paramMatch(HashMap<String, String> params) {
        try {
            // for each param,
            for (String key : params.keySet()) {
                // get field by that name (and fail if not found)
                Field field = this.getClass().getDeclaredField(key);
                field.setAccessible(true);
                Object value = field.get(this);

                // if the value is empty or not equal, return false
                if (value == null || !value.toString().equals(params.get(key))) {
                    return false;
                }
            }
            
            // all matched, return true
            return true;

        } catch (NoSuchFieldException nsf) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Static methods for db actions
    public static <T extends DatabaseModel<T>> ArrayList<T> all(Class<T> cls)                              { return helperFor(cls).all();         }
    public static <T extends DatabaseModel<T>> T            find(Class<T> cls, int id)                     { return helperFor(cls).find(id);      }
    public static <T extends DatabaseModel<T>> ArrayList<T> where(Class<T> cls, Map<String,String> params) { return helperFor(cls).where(params); }
    public static <T extends DatabaseModel<T>> T            first(Class<T> cls)                            { return helperFor(cls).first();       }
    public static <T extends DatabaseModel<T>> int          count(Class<T> cls)                            { return helperFor(cls).count();       }
    public static <T extends DatabaseModel<T>> void         clear(Class<T> cls)                            {        helperFor(cls).clear();       }
}