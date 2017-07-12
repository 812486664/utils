package cn.shining.art.sys.tools;

import com.alibaba.druid.util.StringUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;


public class BeanUtils {

    public static void copySrcToDest(Object src, Object dest, Set<String> keys) {
        Field[] srcFields = src.getClass().getDeclaredFields();
        Field[] destFields = dest.getClass().getDeclaredFields();

        if (null != srcFields && null != destFields && keys.size() > 0) {

            for (Field sf : srcFields) {
                String srcName = sf.getName();
                if (keys.contains(srcName)) {
                    for (Field df : destFields) {
                        String destName = df.getName();
                        if (srcName.equals(destName)) {
                            try {
                                PropertyDescriptor pd = new PropertyDescriptor(srcName, src.getClass());
                                Method getMethod = pd.getReadMethod();// 获得get方法
                                Object oldv = getMethod.invoke(src);// 执行get方法返回一个Object
                                if (null != oldv && !StringUtils.isEmpty(oldv.toString()) && !"null".equals(oldv.toString())) {
                                    PropertyDescriptor pd2 = new PropertyDescriptor(destName, dest.getClass());
                                    Method writeMethod = pd2.getWriteMethod();// 获得set方法
                                    writeMethod.invoke(dest, oldv);// 执行set方法，将ohh的值放入
                                }
                            } catch (IntrospectionException e1) {
                                e1.printStackTrace();
                            } catch (IllegalAccessException e1) {
                                e1.printStackTrace();
                            } catch (InvocationTargetException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    public static void copyDiffSrcToDest(Object src, Object dest, Set<String> keys) {
        Field[] srcFields = src.getClass().getDeclaredFields();
        Field[] destFields = dest.getClass().getDeclaredFields();

        if (null != srcFields && null != destFields && keys.size() > 0) {

            for (Field sf : srcFields) {
                String srcName = sf.getName();
                if (!keys.contains(srcName)) {
                    for (Field df : destFields) {
                        String destName = df.getName();
                        if (srcName.equals(destName)) {
                            try {
                                PropertyDescriptor pd = new PropertyDescriptor(srcName, src.getClass());
                                Method getMethod = pd.getReadMethod();// 获得get方法
                                Object oldv = getMethod.invoke(src);// 执行get方法返回一个Object
                                if (null != oldv && !StringUtils.isEmpty(oldv.toString()) && !"null".equals(oldv.toString())) {
                                    PropertyDescriptor pd2 = new PropertyDescriptor(destName, dest.getClass());
                                    Method writeMethod = pd2.getWriteMethod();// 获得set方法
                                    writeMethod.invoke(dest, oldv);// 执行set方法，将ohh的值放入
                                }
                            } catch (IntrospectionException e1) {
                                e1.printStackTrace();
                            } catch (IllegalAccessException e1) {
                                e1.printStackTrace();
                            } catch (InvocationTargetException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}
