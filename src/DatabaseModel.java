package src;

import java.util.HashMap;
import java.util.ArrayList;
import java.lang.reflect.Field;

abstract class DatabaseModel<Model extends DatabaseModel<Model>> {
    /* TODO: need to implement:
     * [x] id stuff
     * [x] CRUD methods (save, delete) that call DatabaseHelper
     * [x] helper() method to get DatabaseHelper instance
     * [x] columnsFromClass() to specify columns (excluding id)
     * [ ] static methods for all, first, where that call DatabaseHelper
     * [ ] toString() for display
     */

    // id == 0 means "not yet persisted". Persisted records will get id > 0.
    public int id = 0;

    // static map of helpers per model class
    private static final HashMap<Class<?>, DatabaseHelper<?>> HELPERS = new HashMap<>();

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
}