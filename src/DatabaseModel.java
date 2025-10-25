import java.util.HashMap;
import java.util.ArrayList;
import java.lang.reflect.Field;

public abstract class DatabaseModel<Model extends DatabaseModel<Model>> {
    private static final int UNSAVED_ID = 0;
    public int id = UNSAVED_ID;
    public static int nextId = 1;


    private static final HashMap<Class<?>, DatabaseHelper<?>> HELPERS = new HashMap<>();

    // Subclasses must have a public no-arg constructor.
    public DatabaseModel() {
    }

    @SuppressWarnings("unchecked")
    public void save() {
        DatabaseHelper<Model> h = helper();
        if (this.id == UNSAVED_ID) {
            this.id = nextId++;
            h.add((Model) this);
        } else {
            h.update((Model) this);
        }
    }

    @SuppressWarnings("unchecked")
    public void delete() {
        DatabaseHelper<Model> h = helper();
        if (this.id > 0) {
            h.delete((Model) this);
        }
    }

    @SuppressWarnings("unchecked")
    private DatabaseHelper<Model> helper() {
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

    @SuppressWarnings("unchecked")
    private static <AnotherModel extends DatabaseModel<AnotherModel>> DatabaseHelper<AnotherModel> helperFor(
            Class<AnotherModel> otherClass) {
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

    private static ArrayList<String> columnsFromClass(Class<?> cls) {
        ArrayList<String> columns = new ArrayList<>();
        for (Field field : cls.getFields()) {
            columns.add(field.getName());
        }
        return columns;
    }

    // Matching by params uses reflection by default.
    // Subclasses may override for custom logic.
    public boolean paramMatch(HashMap<String, String> params) {
        try {
            for (String key : params.keySet()) {
                Field field = this.getClass().getDeclaredField(key);
                field.setAccessible(true);
                Object value = field.get(this);

                if (value == null || !value.toString().equals(params.get(key))) {
                    return false;
                }
            }

            return true;
        } catch (NoSuchFieldException nsf) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static <T extends DatabaseModel<T>> ArrayList<T> all(Class<T> cls) {
        return helperFor(cls).all();
    }

    public static <T extends DatabaseModel<T>> T find(Class<T> cls, int id) {
        return helperFor(cls).find(id);
    }

    public static <T extends DatabaseModel<T>> ArrayList<T> where(Class<T> cls, HashMap<String, String> params) {
        return helperFor(cls).where(params);
    }

    public static <T extends DatabaseModel<T>> T first(Class<T> cls) {
        return helperFor(cls).first();
    }

    public static <T extends DatabaseModel<T>> int count(Class<T> cls) {
        return helperFor(cls).count();
    }

    public static <T extends DatabaseModel<T>> void clear(Class<T> cls) {
        helperFor(cls).clear();
    }
}