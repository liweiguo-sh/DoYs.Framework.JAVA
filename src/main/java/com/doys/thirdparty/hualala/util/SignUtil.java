package com.doys.thirdparty.hualala.util;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.*;
public class SignUtil {
    public static final String SIGNATURE_SECRET_KEY = "appSecret";
    public static final String SIGNATURE_PREFIX = "key";
    public static final String SIGNATURE_SUFFIX = "secret";
    public static final String NOT_CONTAIN_SIGN = "signature";

    public SignUtil() {
    }

    public static Map<String, Object> getPropertyObject(Object field, Map<String, Object> treeMap) throws Exception {
        boolean isBaseTypeObjec = isBaseTypeObject(field);
        if (isBaseTypeObjec) {
            return treeMap;
        }
        else {
            for (Class sub = field.getClass(); sub != Object.class; sub = sub.getSuperclass()) {
                Field[] subFields = sub.getDeclaredFields();

                for (int i = 0; i < subFields.length; ++i) {
                    Field currtField = subFields[i];
                    Boolean sc = currtField.trySetAccessible();//  .isAccessible();
                    currtField.setAccessible(true);
                    if (currtField.getType().isAssignableFrom(List.class)) {
                        List<Object> listClass = (List) currtField.get(field);
                        if (listClass != null && listClass.size() > 0) {
                            treeMap = getPropertyObject(listClass.get(0), treeMap);
                        }
                    }
                    else {
                        boolean isBaseType = isBaseType(currtField);
                        if (!isBaseType && currtField.get(field) != null) {
                            treeMap = getPropertyObject(currtField.get(field), treeMap);
                        }
                        else {
                            String sFieldName = currtField.getName();
                            Object sFieldValue = getFieldValueByName(sFieldName, field);
                            if (sFieldValue != null) {
                                treeMap.put(sFieldName, sFieldValue);
                            }
                        }

                        currtField.setAccessible(sc);
                    }
                }
            }

            return treeMap;
        }
    }

    public static boolean isBaseType(Field field) {
        if (field.getType().isAssignableFrom(Integer.class)) {
            return true;
        }
        else if (field.getType().isAssignableFrom(Character.class)) {
            return true;
        }
        else if (field.getType().isAssignableFrom(Short.class)) {
            return true;
        }
        else if (field.getType().isAssignableFrom(Long.class)) {
            return true;
        }
        else if (field.getType().isAssignableFrom(Double.class)) {
            return true;
        }
        else if (field.getType().isAssignableFrom(Float.class)) {
            return true;
        }
        else if (field.getType().isAssignableFrom(Boolean.class)) {
            return true;
        }
        else if (field.getType().isAssignableFrom(Byte.class)) {
            return true;
        }
        else {
            return field.getType().isAssignableFrom(String.class);
        }
    }
    public static boolean isBaseTypeObject(Object object) {
        if (object instanceof Integer) {
            return true;
        }
        else if (object instanceof Character) {
            return true;
        }
        else if (object instanceof Short) {
            return true;
        }
        else if (object instanceof Long) {
            return true;
        }
        else if (object instanceof Double) {
            return true;
        }
        else if (object instanceof Float) {
            return true;
        }
        else if (object instanceof Byte) {
            return true;
        }
        else {
            return object instanceof String;
        }
    }

    private static Object getFieldValueByName(String fieldName, Object obj) {
        Object value = null;

        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = obj.getClass().getMethod(getter);
            value = method.invoke(obj);
        } catch (NoSuchMethodException var6) {
            return null;
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return value;
    }

    public static String getSha1(String encryptStr) throws Exception {
        try {
            if (encryptStr != null && !"".equals(encryptStr)) {
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                digest.update(encryptStr.getBytes());
                byte[] messageDigest = digest.digest();
                StringBuffer hexString = new StringBuffer();

                for (int i = 0; i < messageDigest.length; ++i) {
                    String shaHex = Integer.toHexString(messageDigest[i] & 255);
                    if (shaHex.length() < 2) {
                        hexString.append(0);
                    }

                    hexString.append(shaHex);
                }

                return hexString.toString().toUpperCase();
            }
            else {
                return null;
            }
        } catch (Exception var6) {
            var6.printStackTrace();
            throw new Exception(var6);
        }
    }

    public static SignModel getMapSign(Map<String, Object> paramMap, String devPwd) throws Exception {
        try {
            Map<String, Object> fieldMap = new TreeMap(new Comparator<String>() {
                public int compare(String o1, String o2) {
                    return o1.compareToIgnoreCase(o2);
                }
            });
            Set<String> keys = paramMap.keySet();
            if (keys.size() > 0) {
                Iterator var4 = keys.iterator();

                while (var4.hasNext()) {
                    String key = (String) var4.next();
                    if (!key.equalsIgnoreCase("signature")) {
                        boolean isBaseTypeObjec = isBaseTypeObject(paramMap.get(key));
                        if (isBaseTypeObjec) {
                            ((Map) fieldMap).put(key, paramMap.get(key));
                        }
                        else {
                            fieldMap = getPropertyObject(paramMap.get(key), (Map) fieldMap);
                        }
                    }
                }
            }

            ((Map) fieldMap).put("appSecret", devPwd);
            new StringBuilder();
            StringBuilder sb = new StringBuilder();
            sb.append("key");
            Iterator var11 = ((Map) fieldMap).keySet().iterator();

            String generatorSig;
            while (var11.hasNext()) {
                generatorSig = (String) var11.next();
                Object fieldValue = ((Map) fieldMap).get(generatorSig);
                sb.append(generatorSig).append(fieldValue);
            }

            sb.append("secret");
            String generatorStr = sb.toString();
            generatorSig = getSha1(generatorStr);
            SignModel signModel = new SignModel();
            signModel.setGeneratorSig(generatorSig);
            signModel.setGeneratorStr(generatorStr);
            return signModel;
        } catch (Exception var9) {
            throw new Exception(var9);
        }
    }
}