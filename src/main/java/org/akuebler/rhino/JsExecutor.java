package org.akuebler.rhino;

import org.akuebler.rhino.sql.PostgresConnectionFactory;
import org.akuebler.rhino.sql.RhinoSql;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileReader;

public class JsExecutor {

    public static void main(String[] args) throws Exception {
        ScriptEngine engine = setUpEngine();
        injectSqlSupport(engine);
        if (args.length > 0)
            engine.eval(new FileReader(args[0]));
    }

    private static void injectSqlSupport(ScriptEngine engine) {
        RhinoSql sql = new RhinoSql(PostgresConnectionFactory.createConnection());
        engine.put("sql", sql);
    }

    private static ScriptEngine setUpEngine() {
        ScriptEngineManager manager = new ScriptEngineManager();
        return manager.getEngineByName("JavaScript");
    }

}
