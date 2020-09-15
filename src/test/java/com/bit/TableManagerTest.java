package com.bit;

import com.bit.api.table.DatabaseManager;
import com.bit.exception.SameNameDatabaseException;
import com.bit.model.Database;
import org.junit.Test;

/**
 * @author aerfafish
 * @date 2020/9/15 8:00 下午
 */
public class TableManagerTest {

    @Test
    public void createDatabase() throws SameNameDatabaseException {
        DatabaseManager.getInstance().createDatabase(new Database("database0", null));
    }

}
