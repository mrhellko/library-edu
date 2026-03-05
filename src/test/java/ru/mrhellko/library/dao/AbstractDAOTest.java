package ru.mrhellko.library.dao;

import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = {
        "/db/init.sql",
        "/db/dataset.sql"
})
public abstract class AbstractDAOTest {
}
