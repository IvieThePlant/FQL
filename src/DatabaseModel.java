package src;

abstract class DatabaseModel<Model extends DatabaseModel<Model>> {
    /* TODO: need to implement:
     * [x] id stuff
     * [x] CRUD methods (save, delete) that call DatabaseHelper
     * [ ] helper() method to get DatabaseHelper instance
     * [ ] static methods for all, first, where that call DatabaseHelper
     * [ ] getColumns() to specify columns (excluding id)
     * [ ] toString() for display
     */

    // id == 0 means "not yet persisted". Persisted records will get id > 0.
    public int id = 0;

    // save(): add or update db based on id
    public void save() {
        DatabaseHelper<Model> h = helper();
        if (this.id <= 0) {
            h.add((Model) this);
        } else {
            h.update((Model) this);
        }
    }
    // delete(): delete from db based on id
    public void delete() {
        DatabaseHelper<Model> h = helper();
        if (this.id > 0) {
            h.delete((Model) this);
        }
    }

    private DatabaseHelper<Model> helper() {
        // TODO
    }
}