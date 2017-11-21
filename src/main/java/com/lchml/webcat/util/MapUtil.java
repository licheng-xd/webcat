package com.lchml.webcat.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;

/**
 * Created by lc on 11/20/17.
 */
public class MapUtil {

    public static String getString(Map map, Object key) {
        if(map != null) {
            Object answer = map.get(key);
            if(answer != null) {
                return answer.toString();
            }
        }

        return null;
    }

    public static String getString( Map map, Object key, String defaultValue ) {
        String answer = getString(map, key);
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    public static Number getNumber(final Map map, final Object key) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof Number) {
                    return (Number) answer;
                } else if (answer instanceof String) {
                    try {
                        String text = (String) answer;
                        return NumberFormat.getInstance().parse(text);
                    } catch (ParseException e) {
                        // failure means null is returned
                    }
                }
            }
        }
        return null;
    }

    public static Integer getInteger(final Map map, final Object key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Integer) {
            return (Integer) answer;
        }
        return answer.intValue();
    }

    public static int getIntValue(final Map map, final Object key) {
        Integer integerObject = getInteger(map, key);
        if (integerObject == null) {
            return 0;
        }
        return integerObject;
    }

    public static int getIntValue(final Map map, final Object key, int defaultValue) {
        Integer integerObject = getInteger(map, key);
        if (integerObject == null) {
            return defaultValue;
        }
        return integerObject;
    }

    public static <T1, T2> Map<T1, T2> getMap(final Map map, final Object key) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null && answer instanceof Map) {
                return (Map<T1, T2>) answer;
            }
        }
        return null;
    }

    public static <T1, T2> Map<T1, T2> getMap(Map map, Object key, Map<T1, T2> defaultValue ) {
        Map<T1, T2> answer = getMap( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    public static Boolean getBoolean(final Map map, final Object key) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof Boolean) {
                    return (Boolean) answer;

                } else if (answer instanceof String) {
                    return new Boolean((String) answer);

                } else if (answer instanceof Number) {
                    Number n = (Number) answer;
                    return (n.intValue() != 0) ? Boolean.TRUE : Boolean.FALSE;
                }
            }
        }
        return null;
    }

    public static boolean getBooleanValue(final Map map, final Object key) {
        Boolean booleanObject = getBoolean(map, key);
        if (booleanObject == null) {
            return false;
        }
        return booleanObject;
    }

    public static Long getLong(final Map map, final Object key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Long) {
            return (Long) answer;
        }
        return answer.longValue();
    }

    public static Float getFloat(final Map map, final Object key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Float) {
            return (Float) answer;
        }
        return answer.floatValue();
    }

    public static Double getDouble(final Map map, final Object key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Double) {
            return (Double) answer;
        }
        return new Double(answer.doubleValue());
    }
}
