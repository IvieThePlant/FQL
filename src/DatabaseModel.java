package src;

abstract class DatabaseModel<Model extends DatabaseModel<Model>> {
    /* TODO: need to implement:
     * [x] id stuff
     * [ ] CRUD methods (save, delete) that call DatabaseHelper
     * [ ] static methods for all, first, where that call DatabaseHelper
     * [ ] getColumns() to specify columns (excluding id)
     * [ ] toString() for display
     */

    // id == 0 means "not yet persisted". Persisted records will get id > 0.
    public int id = 0;
}