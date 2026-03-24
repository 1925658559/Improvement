import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JDK 动态代理示例
 * 
 * 动态代理可以在运行时动态创建代理对象，
 * 而不需要预先编写代理类的代码
 * 
 * 核心原理：
 * 1. 代理对象实现了被代理接口
 * 2. 方法调用会被转发到 InvocationHandler
 * 3. InvocationHandler 负责调用实际对象的方法
 */
public class JdkProxyDemo {

    /**
     * 定义业务接口（被代理的接口）
     * JDK 动态代理只能代理接口，不能代理类
     */
    interface UserService {
        void createUser(String name);
        void deleteUser(String name);
    }

    /**
     * 真实业务实现类（被代理的目标对象）
     * 实际执行业务逻辑的地方
     */
    static class UserServiceImpl implements UserService {
        @Override
        public void createUser(String name) {
            System.out.println("创建用户: " + name);
        }

        @Override
        public void deleteUser(String name) {
            System.out.println("删除用户: " + name);
        }
    }

    /**
     * 方法调用处理器（InvocationHandler）
     * 
     * 这是动态代理的核心：
     * - 代理对象的所有方法调用都会转发到这里
     * - 可以在调用前后添加额外的逻辑（增强）
     * - 最终调用真实对象的方法
     */
    static class LogInvocationHandler implements InvocationHandler {
        
        // 保存真实对象（被代理的对象）
        private final Object target;

        /**
         * 构造方法，传入真实对象
         * @param target 被代理的真实对象
         */
        public LogInvocationHandler(Object target) {
            this.target = target;
        }

        /**
         * 代理对象调用任何方法时都会触发此方法
         * 
         * @param proxy  代理对象本身（很少使用）
         * @param method 被调用的方法（反射获取）
         * @param args   方法参数
         * @return 方法执行的返回值
         * @throws Throwable 可以抛出任何异常
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 1. 调用前增强：打印日志
            System.out.println("调用方法: " + method.getName());
            
            // 2. 调用真实对象的方法
            // method.invoke() 会执行 target 对象上的该方法
            Object result = method.invoke(target, args);
            
            // 3. 调用后增强：打印返回结果
            System.out.println("方法调用返回结果: " + result);
            System.out.println("方法 " + method.getName() + " 调用结束");
            
            // 4. 返回方法执行结果
            return result;
        }
    }

    public static void main(String[] args) {
        // 步骤1：创建真实对象（被代理的对象）
        UserService real = new UserServiceImpl();

        // 步骤2：创建动态代理对象
        // Proxy.newProxyInstance() 参数说明：
        // - 第1个参数：类加载器（用于加载生成的代理类）
        //             使用真实对象的类加载器
        // - 第2个参数：代理类要实现的接口数组
        //             代理对象会实现这些接口
        // - 第3个参数：方法调用处理器
        //             所有方法调用都会转发到这里
        UserService proxy = (UserService) Proxy.newProxyInstance(
                real.getClass().getClassLoader(),           // 类加载器
                real.getClass().getInterfaces(),           // 要实现的接口
                new LogInvocationHandler(real)             // 方法调用处理器
        );

        // 步骤3：调用代理对象的方法
        // 实际上会触发 LogInvocationHandler 的 invoke 方法
        proxy.createUser("徐迎庆");
        proxy.deleteUser("测试用户");
        
        // 执行结果：
        // 调用方法: createUser
        // 创建用户: 徐迎庆
        // 方法调用返回结果: null
        // 方法 createUser 调用结束
        // 调用方法: deleteUser
        // 删除用户: 测试用户
        // 方法调用返回结果: null
        // 方法 deleteUser 调用结束
    }
}
