package com.doys.framework.upgrade.db.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassReflect {
    public static List<Field> getClassAllFields(Object entity) throws NoSuchFieldException {
        boolean blExist = false;
        Field fieldEntity, fieldSuper;

        Class entityClass = entity.getClass();
        Field[] arrFieldEntity = entityClass.getDeclaredFields();
        Field[] arrFieldSuper = entityClass.getFields();
        List<Field> lstFieldSuper = new ArrayList<>();
        List<Field> lstField = new ArrayList<>();
        // --------------------------------------------------------
        try {
            // -- 保留在父类中有定义，子类中没有定义的字段 --
            for (int i = 0; i < arrFieldSuper.length; i++) {
                fieldSuper = arrFieldSuper[i];

                blExist = false;
                for (int j = 0; j < arrFieldEntity.length; j++) {
                    fieldEntity = arrFieldEntity[j];
                    if (fieldSuper.getName().equalsIgnoreCase(fieldEntity.getName())) {
                        blExist = true;
                        break;
                    }
                }
                if (!blExist) {
                    lstFieldSuper.add(fieldSuper);
                }
            }

            // -- 父类中需要置前的字段 --
            for (int i = 0; i < lstFieldSuper.size(); i++) {
                fieldSuper = lstFieldSuper.get(i);
                if (fieldSuper.getName().equalsIgnoreCase("id")) {
                    lstField.add(fieldSuper);
                }
            }
            // -- 子类全部字段 --
            lstField.addAll(Arrays.asList(arrFieldEntity));
            // -- 父类中需后置前的字段 --
            for (int i = 0; i < lstFieldSuper.size(); i++) {
                fieldSuper = lstFieldSuper.get(i);
                if (!fieldSuper.getName().equalsIgnoreCase("id")) {
                    lstField.add(fieldSuper);
                }
            }

            return lstField;
        } catch (Exception e) {
            throw e;
        }
    }
}
