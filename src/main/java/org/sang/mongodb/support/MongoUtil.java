package org.sang.mongodb.support;

import org.bson.types.ObjectId;

import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Author: zhupeng@digiwin.com
 * @Datetime: 2021/11/5 15:03
 * @Description: Mongo工具类
 * @Version: 0.0.0.1
 */
public class MongoUtil {

    /**
     * 线程安全的下一个随机数,每次生成自增+1
     */
    // 随机
    private static AtomicInteger nextInc = new AtomicInteger((new Random()).nextInt());


    /**
     * 机器信息
     */
    private static final int machine;

    /**
     * 初始化机器信息 = 机器码 + 进程码
     */
    static {
        try {
            // 机器码
            int machinePiece;
            try {
                StringBuilder netSb = new StringBuilder();
                // 返回机器所有的网络接口
                Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
                // 遍历网络接口
                while (e.hasMoreElements()) {
                    NetworkInterface ni = e.nextElement();
                    // 网络接口信息
                    netSb.append(ni.toString());
                }
                // 保留后两位
                machinePiece = netSb.toString().hashCode() << 16;
            } catch (Throwable e) {
                // 出问题随机生成,保留后两位
                machinePiece = (new Random().nextInt()) << 16;
            }
            // 进程码
            // 因为静态变量类加载可能相同,所以要获取进程ID + 加载对象的ID值
            final int processPiece;
            // 进程ID初始化
            int processId = new Random().nextInt();
            try {
                // 获取进程ID
                processId = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().hashCode();
            } catch (Throwable t) {
            }

            ClassLoader loader = MongoUtil.class.getClassLoader();

            // 返回对象哈希码,无论是否重写hashCode方法
            int loaderId = loader != null ? System.identityHashCode(loader) : 0;

            // 进程ID + 对象加载ID
            StringBuilder processSb = new StringBuilder();
            processSb.append(Integer.toHexString(processId));
            processSb.append(Integer.toHexString(loaderId));
            // 保留前2位
            processPiece = processSb.toString().hashCode() & 0xFFFF;

            // 生成机器信息 = 取机器码的后2位和进程码的前2位
            machine = machinePiece | processPiece;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取一个objectId
     * 创建MongoDB ID生成策略实现
     * ObjectId由以下几部分组成：
     * 1. Time 时间戳。
     * 2. Machine 所在主机的唯一标识符，一般是机器主机名的散列值。
     * 3. PID 进程ID。确保同一机器中不冲突
     * 4. INC 自增计数器。确保同一秒内产生objectId的唯一性。
     */
    public static String objectId() {
        byte b[] = new byte[12];
        ByteBuffer bb = ByteBuffer.wrap(b);
        //4位
        bb.putInt((int) (System.currentTimeMillis() / 1000));
        //4位
        bb.putInt(machine);
        //4位
        bb.putInt(nextInc.getAndIncrement());
        StringBuilder buf = new StringBuilder(24);
        // 原来objectId格式化
        for (byte t : bb.array()) {
            // 小于两位左端补0
            int i = t & 0xff;
            if (i < 16) {
                buf.append("0").append(Integer.toHexString(i));
            } else {
                buf.append(Integer.toHexString(i));
            }

        }
        return buf.toString();
    }

    public static ObjectId get() {
        return ObjectId.get();
    }

    /**
     * 获取一个objectId用下划线分割
     */
    public static String objectIdUnderline() {

        byte b[] = new byte[12];
        ByteBuffer bb = ByteBuffer.wrap(b);
        //4位
        bb.putInt((int) (System.currentTimeMillis() / 1000));
        //4位
        bb.putInt(machine);
        //4位
        bb.putInt(nextInc.getAndIncrement());
        StringBuilder buf = new StringBuilder(24);
        // 原来objectId格式化太慢
        byte[] array = bb.array();
        for (int i = 0; i < array.length; i++) {
            if (i % 4 == 0 && i != 0) {
                buf.append("-");
            }
            int t = array[i] & 0xff;
            if (t < 16) {
                buf.append("0").append(Integer.toHexString(t));
            } else {
                buf.append(Integer.toHexString(t));
            }

        }
        return buf.toString();
    }

    public static void main(String[] args) {
        System.out.println(MongoUtil.objectId());
        System.out.println(MongoUtil.objectIdUnderline());
    }

    public static ObjectId objectId(String objectId) {
        return new ObjectId(objectId);
    }

    public static String objectId(ObjectId objectId) {
        return objectId.toHexString();
    }

    public static List<String> toStrings(List<ObjectId> objectIds) {
        return objectIds.stream().map(objectId -> objectId.toString()).collect(Collectors.toList());
    }

    public static List<ObjectId> toObjectIds(List<String> objectIds) {
        return objectIds.stream().map(s -> new ObjectId(s)).collect(Collectors.toList());
    }

}

