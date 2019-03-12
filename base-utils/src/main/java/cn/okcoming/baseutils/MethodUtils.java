package cn.okcoming.baseutils;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;

/**
 * 一些可以共用的方法集合
 *
 * @author bluces
 */
public class MethodUtils {

    /**
     * 将分为单位的转换为元 （除100）
     *
     * @param amount
     * @return
     * @throws Exception
     */
    public static String changeF2Y(Long amount){
        if(amount == null){
            return null;
        }
        return BigDecimal.valueOf(amount).divide(new BigDecimal(100)).toString();
    }

    /**
     * 将元为单位的转换为分 （乘100）
     *
     * @param amount
     * @return
     */
    public static Long changeY2F(BigDecimal amount){
        if(amount == null){
            return null;
        }
        return amount.multiply(new BigDecimal(100)).longValue();
    }

    /**
     * 将元为单位的转换为分 替换小数点，支持以逗号区分的金额
     * @param amount
     * @return
     */
    public static Long changeY2F(String amount){
        if(amount == null){
            return null;
        }
        String currency =  amount.replaceAll("\\$|\\￥|\\,", "");  //处理包含, ￥ 或者$的金额
        int index = currency.indexOf(".");
        int length = currency.length();
        Long amLong = 0l;
        if(index == -1){
            amLong = Long.valueOf(currency+"00");
        }else if(length - index >= 3){
            amLong = Long.valueOf((currency.substring(0, index+3)).replace(".", ""));
        }else if(length - index == 2){
            amLong = Long.valueOf((currency.substring(0, index+2)).replace(".", "")+0);
        }else{
            amLong = Long.valueOf((currency.substring(0, index+1)).replace(".", "")+"00");
        }
        return amLong;
    }

    /**自定义线程池中线程名称 方便在日志追踪问题*/
    public static Runnable proxyRunnable(final Thread parentThread, Runnable action){
        return () -> {
            String threadName = Thread.currentThread().getName();
            try{
                Thread.currentThread().setName(threadName +"-"+parentThread.getName());
                action.run();
            }finally {
                Thread.currentThread().setName(threadName);
            }
        };
    }

    /**自定义线程池中线程名称 方便在日志追踪问题*/
    public static <T,R> R proxyFunction(Thread parentThread, T name , Function<T,R> fun) {
        String poolThreadName = Thread.currentThread().getName();
        if (!Objects.equals(parentThread.getName(), poolThreadName)) {//forkjoin的第一个任务线程就是主线程
            Thread.currentThread().setName(poolThreadName + "-" + parentThread.getName());
        }
        R result = fun.apply(name);
        Thread.currentThread().setName(poolThreadName);
        return result;
    }

    /**
     * 根据传入的类型获取spring管理的对应bean
     * @param clazz 类型
     * @param request 请求对象
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz, HttpServletRequest request) {
        BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        return factory.getBean(clazz);
    }

    public static <T> T getBean(String name,Class<T> clazz,HttpServletRequest request) {
        BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        return factory.getBean(name,clazz);
    }
}
