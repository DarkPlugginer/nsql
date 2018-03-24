package me.dark;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private List<Class<?>> classes = new ArrayList<>();
    private HashMap<String, HashMap<String, Integer>> columns = new HashMap<>();
    private Connection con;

    public void init() throws SQLException {
        Statement statement = con.createStatement();
        StringBuilder stringBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS");

        for (Class<?> aClass : classes) {
            if (aClass.isAnnotationPresent(Entity.class)) {
                stringBuilder.append(" " + aClass.getSimpleName().toLowerCase() + "(");

                for (Field field : aClass.getFields()) {

                    if (field.isAnnotationPresent(Column.class)) {
                        Column c = field.getAnnotation(Column.class);

                        HashMap<String, Integer> map = new HashMap<>();
                        map.put(columnType(field.getType()), c.length());
                        columns.put(c.name(), map);
                    }

                    if (field.isAnnotationPresent(Id.class)) {
                        for (Map.Entry<String, HashMap<String, Integer>> entry : columns.entrySet()) {
                            for (Map.Entry<String, Integer> entry1 : entry.getValue().entrySet()) {
                                stringBuilder.append(entry.getKey() + " " + entry1.getKey() + "(" + entry1.getValue() + "), ");
                            }
                        }

                        stringBuilder.append(");");
                        if (field.isAnnotationPresent(GeneratedValue.class))
                            stringBuilder.append("PRIMARY KEY(" + field.getName().toLowerCase() + ")");

                        statement.executeUpdate(stringBuilder.toString());
                    }
                }
            }
        }
    }

    private String columnType(Class<?> clazz) {
        String string = "";
        if (clazz.getSimpleName().equalsIgnoreCase("integer")) {
            string = "int";
        } else if (clazz.getSimpleName().equalsIgnoreCase("string")) {
            string = "varchar";
        }
        return string;
    }
}
