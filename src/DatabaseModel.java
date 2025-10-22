package src;

import java.util.HashMap;

abstract class DatabaseModel<Model extends DatabaseModel<Model>> {
    /* TODO: need to implement:
     * [x] id stuff
     * [x] CRUD methods (save, delete) that call DatabaseHelper
     * [x] helper() method to get DatabaseHelper instance
     * [ ] getColumns() to specify columns (excluding id)
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
                String[] cols = null; // TODO: get columns from field names or method
                helper = new DatabaseHelper<>((Class<Model>) this.getClass(), cols);
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
                String[] cols = null; // TODO: get columns from field names or method
                helper = new DatabaseHelper<>((Class<AnotherModel>) otherClass, cols);
                HELPERS.put(otherClass, helper);
                return helper;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create helper for " + otherClass.getSimpleName(), e);
            }
        }
    }
}