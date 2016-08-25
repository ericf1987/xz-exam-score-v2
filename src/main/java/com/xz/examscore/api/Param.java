package com.xz.examscore.api;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 接口调用参数类
 *
 * @author zhaorenwu
 */
@SuppressWarnings("UnusedDeclaration")
public class Param implements Serializable {

    private Map<String, String[]> parameters = new HashMap<>();     // 接口参数

    public Param() {
    }

    public Param setParameter(String key, String... value) {
        parameters.put(key, value);
        return this;
    }

    public Param setParameter(String key, Number... value) {
        String[] strValue = new String[value.length];

        for (int i = 0; i < value.length; i++) {
            Number number = value[i];
            strValue[i] = number == null ? null :
                    new BigDecimal(number.toString()).toPlainString();
        }

        parameters.put(key, strValue);
        return this;
    }

    public Param setParameter(String key, Boolean... value) {
        String[] strValue = new String[value.length];

        for (int i = 0; i < value.length; i++) {
            Boolean bool = value[i];
            strValue[i] = bool == null ? null : bool.toString();
        }

        parameters.put(key, strValue);
        return this;
    }

    public Param setParameter(String key, Date... value) {
        String[] strValue = new String[value.length];

        for (int i = 0; i < value.length; i++) {
            Date date = value[i];
            strValue[i] = date == null ? null : String.valueOf(date.getTime());
        }

        parameters.put(key, strValue);
        return this;
    }

    public boolean containsKey (String key) {
        return parameters.containsKey(key);
    }

    public Map<String, String[]> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this.parameters);
    }

    /////////////////////////////////////////

    /**
     * 获得参数值数组
     *
     * @param key 参数名
     *
     * @return 参数值。如果没有参数值则返回空数组
     */
    public String[] getStringValues(String key) {
        if (!parameters.containsKey(key)) {
            return new String[0];
        }

        String[] values = parameters.get(key);
        if (values.length == 1 && StringUtils.isEmpty(values[0])) {
            return new String[0];
        }
        return values;
    }

    public String getString(String key) {
        String[] values = getStringValues(key);
        return values.length == 0 ? null : values[0];
    }

    /**
     * 获得参数值数组
     *
     * @param key 参数名
     *
     * @return 参数值。如果没有参数值则返回空数组
     */
    public Double[] getDoubleValues(String key) {
        String[] values = getStringValues(key);

        return new ArrayConverter<Double>(Double.class) {

            @Override
            Double convert(String value) {
                return StringUtils.isEmpty(value) ? null : Double.valueOf(value);
            }
        }.convert(values);
    }

    public Double getDouble(String key) {
        Double[] values = getDoubleValues(key);
        return values.length == 0 ? null : values[0];
    }

    /**
     * 获得参数值数组
     *
     * @param key 参数名
     *
     * @return 参数值。如果没有参数值则返回空数组
     */
    public Integer[] getIntegerValues(String key) {
        String[] values = getStringValues(key);

        return new ArrayConverter<Integer>(Integer.class) {

            @Override
            Integer convert(String value) {
                return StringUtils.isEmpty(value)? null: Integer.valueOf(value);
            }
        }.convert(values);
    }

    public Integer getInteger(String key) {
        Integer[] values = getIntegerValues(key);
        return values.length == 0 ? null : values[0];
    }

    /**
     * 获得参数值数组
     *
     * @param key 参数名
     *
     * @return 参数值。如果没有参数值则返回空数组
     */
    public Long[] getLongValues(String key) {
        String[] values = getStringValues(key);

        return new ArrayConverter<Long>(Long.class) {

            @Override
            Long convert(String value) {
                return StringUtils.isEmpty(value) ? null : Long.valueOf(value);
            }
        }.convert(values);
    }

    public Long getLong(String key) {
        Long[] values = getLongValues(key);
        return values.length == 0 ? null : values[0];
    }

    public Boolean[] getBooleanValues(String key) {
        String[] values = getStringValues(key);

        return new ArrayConverter<Boolean>(Boolean.class) {

            @Override
            Boolean convert(String value) {
                return value != null && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes")) ?
                        Boolean.TRUE : Boolean.FALSE;
            }
        }.convert(values);
    }

    public Boolean getBoolean(String key) {
        Boolean[] values = getBooleanValues(key);
        return values.length == 0 ? null : values[0];
    }

    public Date[] getDateValues(String key) {
        String[] values = getStringValues(key);

        return new ArrayConverter<Date>(Date.class) {

            @Override
            Date convert(String value) {
                if (value == null) {
                    return null;
                }

                try {
                    if (value.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                        return new SimpleDateFormat("yyyy-MM-dd").parse(value);
                    } else if (value.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$")) {
                        return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(value);
                    } else if (value.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")) {
                        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value);
                    }
                } catch (ParseException e) {
                    throw new IllegalArgumentException("无法解析日期字符串'" + value + "'");
                }

                return new Date(Long.parseLong(value));
            }
        }.convert(values);
    }

    public Date getDate(String key) {
        Date[] values = getDateValues(key);
        return values.length == 0 ? null : values[0];
    }

    /////////////////////////////////////////

    /**
     * 转化数组对象的工具，不会返回 null
     *
     * @param <T>
     */
    @SuppressWarnings({"unchecked"})
    private abstract class ArrayConverter<T> {

        private Class<T> type;

        protected ArrayConverter(Class<T> type) {
            this.type = type;
        }

        T[] convert(String[] values) {

            T[] result;

            if (values != null) {
                result = (T[]) Array.newInstance(type, values.length);
                for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
                    String value = values[i];
                    try {
                        result[i] = value == null ? null : convert(value);
                    } catch (Exception e) {
                        throw new ApiException("提取 " + type + " 类型参数值失败", e);
                    }
                }
            } else {
                result = (T[]) Array.newInstance(type, 0);
            }

            return result;
        }

        abstract T convert(String value);
    }
}
