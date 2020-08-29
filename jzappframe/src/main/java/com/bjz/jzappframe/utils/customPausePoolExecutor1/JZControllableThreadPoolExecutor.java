package com.bjz.jzappframe.utils.customPausePoolExecutor1;

import com.bjz.jzappframe.utils.JZLog;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

// BEGIN android-note
// removed security manager docs
// END android-note

/**
 * An {@link ExecutorService} that executes each submitted task using
 * one of possibly several pooled threads, normally configured
 * using {@link Executors} factory methods.
 *
 * <p>Thread pools address two different problems: they usually
 * provide improved performance when executing large numbers of
 * asynchronous tasks, due to reduced per-task invocation overhead,
 * and they provide a means of bounding and managing the resources,
 * including threads, consumed when executing a collection of tasks.
 * Each {@code ThreadPoolExecutor} also maintains some basic
 * statistics, such as the number of completed tasks.
 * *执行每个提交的任务使用的{@link ExecutorService}
 *   *可能有几个合并的线程之一，通常配置
 *   *使用{@link Executors}工厂方法。
 *  *
 *   * <p>线程池解决两个不同的问题：他们通常
 *   *在执行大量数据时提供更好的性能
 *   *异步任务，由于每个任务的调用开销减少，
 *   *它们提供了限制和管理资源的手段，
 *   *包括执行任务集合时消耗的线程。
 *   *每个{@code ThreadPoolExecutor}也维护一些基本的
 *   *统计数据，如已完成的任务数量。
 * <p>To be useful across a wide range of contexts, this class
 * provides many adjustable parameters and extensibility
 * hooks. However, programmers are urged to use the more convenient
 * {@link Executors} factory methods {@link
 * Executors#newCachedThreadPool} (unbounded thread pool, with
 * automatic thread reclamation), {@link Executors#newFixedThreadPool}
 * (fixed size thread pool) and {@link
 * Executors#newSingleThreadExecutor} (single background thread), that
 * preconfigure settings for the most common usage
 * scenarios. Otherwise, use the following guide when manually
 * configuring and tuning this class:
 * 在广泛的上下文中有用，这个类
 * 提供许多可调参数和可扩展性
 * 钩子。 然而，程序员被敦促使用更方便
 * {@link Executors}工厂方法{@link
 *   *执行者＃newCachedThreadPool}（无限线程池，带
 *   *自动线程回收），{@link Executors＃newFixedThreadPool}
 *   *（固定大小的线程池）和{@link
 *   *执行者＃newSingleThreadExecutor}（单个后台线程），那个
 *   *预先配置最常用的设置
 *   *情景。 否则，手动使用以下指南
 *   *配置和调优此类：
 * <dl>
 *
 * <dt>Core and maximum pool sizes</dt>
 * 核心和最大池大小</ dt>
 * <dd style="font-family:'DejaVu Sans', Arial, Helvetica, sans-serif">
 * A {@code ThreadPoolExecutor} will automatically adjust the
 * pool size (see {@link #getPoolSize})
 * according to the bounds set by
 * corePoolSize (see {@link #getCorePoolSize}) and
 * maximumPoolSize (see {@link #getMaximumPoolSize}).
 * <p>
 * When a new task is submitted in method {@link #execute(Runnable)},
 * and fewer than corePoolSize threads are running, a new thread is
 * created to handle the request, even if other worker threads are
 * idle.  If there are more than corePoolSize but less than
 * maximumPoolSize threads running, a new thread will be created only
 * if the queue is full.  By setting corePoolSize and maximumPoolSize
 * the same, you create a fixed-size thread pool. By setting
 * maximumPoolSize to an essentially unbounded value such as {@code
 * Integer.MAX_VALUE}, you allow the pool to accommodate an arbitrary
 * number of concurrent tasks. Most typically, core and maximum pool
 * sizes are set only upon construction, but they may also be changed
 * dynamically using {@link #setCorePoolSize} and {@link
 * #setMaximumPoolSize}. </dd>
 * 一个{@code ThreadPoolExecutor}将自动调整
 *  *池大小（请参阅{@link #getPoolSize}）
 *  *根据设定的界限
 *  * corePoolSize（请参阅{@link #getCorePoolSize}）和
 *  * maximumPoolSize（请参阅{@link #getMaximumPoolSize}）。
 *  *
 *  *当方法{@link #execute（Runnable）}中提交新任务时，
 *  *和少于corePoolSize线程正在运行，一个新的线程是
 *  *创建处理请求，即使其他工作线程
 *  * 闲。如果有超过corePoolSize但小于
 *  * maximumPoolSize线程运行时，只会创建一个新的线程
 *  *如果队列已满通过设置corePoolSize和maximumPoolSize
 *  *同样，你创建一个固定大小的线程池。通过设置
 *  * maximumPoolSize为一个本质上无限的值，如{@code
 *  * Integer.MAX_VALUE}，您允许池适应任意
 *  *并发任务数。最典型的是核心和最大池
 *  *尺寸仅在施工时设定，但也可能更改
 *  *动态使用{@link #setCorePoolSize}和{@link
 *  * #setMaximumPoolSize}。 </ DD>
 * <dt>On-demand construction</dt>
 *
 * <dd style="font-family:'DejaVu Sans', Arial, Helvetica, sans-serif">
 * By default, even core threads are initially created and
 * started only when new tasks arrive, but this can be overridden
 * dynamically using method {@link #prestartCoreThread} or {@link
 * #prestartAllCoreThreads}.  You probably want to prestart threads if
 * you construct the pool with a non-empty queue. </dd>
 * 默认情况下，即使是最初创建核心线程也是
 *   *只有当新任务到达时才会启动，但这可以被覆盖
 *   *动态使用方法{@link #prestartCoreThread}或{@link
 *   * #prestartAllCoreThreads}。 你可能想要预先提供线程
 *   *使用非空队列构建池。
 * <dt>Creating new threads</dt>
 *
 * <dd style="font-family:'DejaVu Sans', Arial, Helvetica, sans-serif">
 * New threads are created using a {@link ThreadFactory}.  If not
 * otherwise specified, a {@link Executors#defaultThreadFactory} is
 * used, that creates threads to all be in the same {@link
 * ThreadGroup} and with the same {@code NORM_PRIORITY} priority and
 * non-daemon status. By supplying a different ThreadFactory, you can
 * alter the thread's name, thread group, priority, daemon status,
 * etc. If a {@code ThreadFactory} fails to create a thread when asked
 * by returning null from {@code newThread}, the executor will
 * continue, but might not be able to execute any tasks. Threads
 * should possess the "modifyThread" {@code RuntimePermission}. If
 * worker threads or other threads using the pool do not possess this
 * permission, service may be degraded: configuration changes may not
 * take effect in a timely manner, and a shutdown pool may remain in a
 * state in which termination is possible but not completed.</dd>
 * 使用{@link ThreadFactory}创建新线程。 如果不
 *   *否则指定，{@link Executors＃defaultThreadFactory}就是
 *   *使用，创建所有的线程都在同一个{@link
 *   * ThreadGroup}并且具有相同的{@code NORM_PRIORITY}优先级和
 *   *非守护进程状态。 通过提供不同的ThreadFactory，你可以
 *   *改变线程的名称，线程组，优先级，守护进程状态，
 *   *等等。如果{@code ThreadFactory}在询问时无法创建线程
 *   *通过从{@code newThread}返回null，执行者将会
 *   *继续，但可能无法执行任何任务。主题
 *   *应该拥有“modifyThread”{@code RuntimePermission}。 如果
 *   *工作线程或使用池的其他线程不具备此功能
 *   *权限，服务可能会降级：配置更改可能不会
 *   *及时生效，关闭池可能会保留在
 *   *可以终止但未完成的状态。
 * <dt>Keep-alive times</dt>
 *
 * <dd style="font-family:'DejaVu Sans', Arial, Helvetica, sans-serif">
 * If the pool currently has more than corePoolSize threads,
 * excess threads will be terminated if they have been idle for more
 * than the keepAliveTime (see {@link #getKeepAliveTime(TimeUnit)}).
 * This provides a means of reducing resource consumption when the
 * pool is not being actively used. If the pool becomes more active
 * later, new threads will be constructed. This parameter can also be
 * changed dynamically using method {@link #setKeepAliveTime(long,
 * TimeUnit)}.  Using a value of {@code Long.MAX_VALUE} {@link
 * TimeUnit#NANOSECONDS} effectively disables idle threads from ever
 * terminating prior to shut down. By default, the keep-alive policy
 * applies only when there are more than corePoolSize threads, but
 * method {@link #allowCoreThreadTimeOut(boolean)} can be used to
 * apply this time-out policy to core threads as well, so long as the
 * keepAliveTime value is non-zero. </dd>
 * 如果池当前有多个corePoolSize线程，
 *   *如果多余的线程已经闲置了，将会被终止
 *   *比keepAliveTime（见{@link #getKeepAliveTime（TimeUnit）}）。
 *   *这提供了一种减少资源消耗的方法
 *   *池没有被积极使用。 如果游泳池变得更加活跃
 *   *以后，将构建新的线程。 此参数也可以
 *   *使用方法{@link #setKeepAliveTime（long，
 *   * TimeUnit）}。 使用值{@code Long.MAX_VALUE} {@link
 *   * TimeUnit＃NANOSECONDS}有效地禁用空闲线程
 *   *关闭前终止。 默认情况下，保持活动策略
 *   *仅在存在多于corePoolSize线程的情况下才适用，但是
 *   *方法{@link #allowCoreThreadTimeOut（boolean）}可以用于
 *   *应用这个超时策略到核心线程，只要
 *   * keepAliveTime值不为零。
 * <dt>Queuing</dt>
 *
 * <dd style="font-family:'DejaVu Sans', Arial, Helvetica, sans-serif">
 * Any {@link BlockingQueue} may be used to transfer and hold
 * submitted tasks.  The use of this queue interacts with pool sizing:
 * 任何{@link BlockingQueue}都可以用于传输和保留
 *   *提交的任务。 这个队列的使用与池大小相互作用：
 * <ul>
 *
 * <li>If fewer than corePoolSize threads are running, the Executor
 * always prefers adding a new thread
 * rather than queuing.
 * 如果少于corePoolSize线程正在运行，则执行程序
 *   *总是喜欢添加一个新的线程
 *   *而不是排队。
 * <li>If corePoolSize or1 more threads are running, the Executor
 * always prefers queuing a request rather than adding a new
 * thread.
 * 如果corePoolSize或更多线程正在运行，则执行程序
 *   *总是喜欢排队请求，而不是添加新的
 *   *线程。
 * <li>If a request cannot be queued, a new thread is created unless
 * this would exceed maximumPoolSize, in which case, the task will be
 * rejected.
 * 如果请求不能排队，则创建一个新线程，除非
 *   *这将超过maximumPoolSize，在这种情况下，任务将是
 *   * 拒绝。
 * </ul>
 * <p>
 * There are three general strategies for queuing:排队有三种一般策略：
 * <ol>
 *
 * <li><em> Direct handoffs.</em> A good default choice for a work
 * queue is a {@link SynchronousQueue} that hands off tasks to threads
 * without otherwise holding them. Here, an attempt to queue a task
 * will fail if no threads are immediately available to run it, so a
 * new thread will be constructed. This policy avoids lockups when
 * handling sets of requests that might have internal dependencies.
 * Direct handoffs generally require unbounded maximumPoolSizes to
 * avoid rejection of new submitted tasks. This in turn admits the
 * possibility of unbounded thread growth when commands continue to
 * arrive on average faster than they can be processed.
 * 直接切换。</ em>一个很好的默认选择
 *   * queue是一个{@link SynchronousQueue}，将任务交给线程
 *   *没有其他方式持有他们。 这里，尝试排队一个任务
 *   *如果没有线程可以立即运行它，将会失败，所以a
 *   *将新建线程。 此策略避免锁定
 *   *处理可能具有内部依赖关系的请求集。
 *   *直接切换通常需要无限制的最大化流量
 *   *避免拒绝新提交的任务。 这反过来承认了
 *   *命令继续时无限制线程增长的可能性
 *   *平均速度比他们可以处理的要快。
 * <li><em> Unbounded queues.</em> Using an unbounded queue (for
 * example a {@link LinkedBlockingQueue} without a predefined
 * capacity) will cause new tasks to wait in the queue when all
 * corePoolSize threads are busy. Thus, no more than corePoolSize
 * threads will ever be created. (And the value of the maximumPoolSize
 * therefore doesn't have any effect.)  This may be appropriate when
 * each task is completely independent of others, so tasks cannot
 * affect each others execution; for example, in a web page server.
 * While this style of queuing can be useful in smoothing out
 * transient bursts of requests, it admits the possibility of
 * unbounded work queue growth when commands continue to arrive on
 * average faster than they can be processed.
 * 无限队列。</ em>使用无界队列（for
 *   *例如没有预定义的{@link LinkedBlockingQueue}
 *   *容量）将导致新任务在队列中等待全部
 *   * corePoolSize线程正忙 因此，不超过corePoolSize
 *   *将创建线程。 （和maximumPoolSize的值
 *   *因此没有任何效果。）这可能是适当的
 *   *每个任务完全独立于其他任务，所以任务不能
 *   *影响对方执行; 例如，在网页服务器中。
 *   虽然这种排队风格在平滑化方面是有用的
 *   *瞬间突发的请求，它承认的可能性
 *   *命令继续到达时无限制的工作队列增长
 *   *平均速度比他们可以处理的快。
 * <li><em>Bounded queues.</em> A bounded queue (for example, an
 * {@link ArrayBlockingQueue}) helps prevent resource exhaustion when
 * used with finite maximumPoolSizes, but can be more difficult to
 * tune and control.  Queue sizes and maximum pool sizes may be traded
 * off for each other: Using large queues and small pools minimizes
 * CPU usage, OS resources, and context-switching overhead, but can
 * lead to artificially low throughput.  If tasks frequently block (for
 * example if they are I/O bound), a system may be able to schedule
 * time for more threads than you otherwise allow. Use of small queues
 * generally requires larger pool sizes, which keeps CPUs busier but
 * may encounter unacceptable scheduling overhead, which also
 * decreases throughput.
 * 有界队列。</ em>有界队列（例如，
 *   * {@link ArrayBlockingQueue}）有助于防止资源耗尽
 *   *使用有限maxPoolSizes，但可能更难
 *   *调控。 可以交换队列大小和最大池大小
 *   *关闭彼此：使用大队列和小型池最小化
 *   * CPU使用率，操作系统资源和上下文切换开销，但可以
 *   *导致人为的低产量。 如果任务频繁阻止（for
 *   *示例，如果它们是I / O绑定），系统可能能够安排
 *   *更多线程的时间比您允许的更多。 使用小队列
 *   *通常需要较大的池大小，这样可以使CPU繁忙
 *   *可能会遇到不可接受的调度开销，这也是
 *   *降低吞吐量
 * </ol>
 *
 * </dd>
 *
 * <dt>Rejected tasks</dt>
 * 被拒绝的任务
 * <dd style="font-family:'DejaVu Sans', Arial, Helvetica, sans-serif">
 * New tasks submitted in method {@link #execute(Runnable)} will be
 * <em>rejected</em> when the Executor has been shut down, and also when
 * the Executor uses finite bounds for both maximum threads and work queue
 * capacity, and is saturated.  In either case, the {@code execute} method
 * invokes the {@link
 * JZRejectedExecutionHandler#rejectedExecution(Runnable, JZControllableThreadPoolExecutor)}
 * method of its {@link JZRejectedExecutionHandler}.  Four predefined handler
 * policies are provided:
 * 方法{@link #execute（Runnable）}中提交的新任务将会
 *   当执行者被关闭时，还有时候，* <em>拒绝</ em>
 *   *执行者对最大线程和工作队列使用有限边界
 *   *容量，饱和。 在任一情况下，{@code execute}方法
 *   *调用{@link
 *   * JZRejectedExecutionHandler＃rejectedExecution（Runnable，JZControllableThreadPoolExecutor）}
 *   *方法的{@link JZRejectedExecutionHandler}。 四个预定义的处理程序
 *   *提供政策：
 * <ol>
 *
 * <li>In the default {@link java.util.concurrent.ThreadPoolExecutor.AbortPolicy}, the
 * handler throws a runtime {@link RejectedExecutionException} upon
 * rejection.
 * 在默认的{@link java.util.concurrent.ThreadPoolExecutor.AbortPolicy}中，
 *   * handler抛出一个运行时{@link RejectedExecutionException}
 *   拒绝
 * <li>In {@link java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy}, the thread
 * that invokes {@code execute} itself runs the task. This provides a
 * simple feedback control mechanism that will slow down the rate that
 * new tasks are submitted.
 * 在{@link java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy}中，线程
 *   *调用{@code执行}本身运行任务。 这提供了一个
 *   *简单的反馈控制机制会降低速度
 *   *提交新任务。
 * <li>In {@link java.util.concurrent.ThreadPoolExecutor.DiscardPolicy}, a task that
 * cannot be executed is simply dropped.
 * 在{@link java.util.concurrent.ThreadPoolExecutor.DiscardPolicy}中，一个任务
 *   *不能被执行简单地删除。
 *  *
 * <li>In {@link java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy}, if the
 * executor is not shut down, the task at the head of the work queue
 * is dropped, and then execution is retried (which can fail again,
 * causing this to be repeated.)
 * 在{@link java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy}中，如果
 *   *执行者没有关闭，任务在工作队长的头上
 *   *被删除，然后重试执行（这可能会再次失败，
 *   *导致重复。）
 * </ol>
 * <p>
 * It is possible to define and use other kinds of {@link
 * JZRejectedExecutionHandler} classes. Doing so requires some care
 * especially when policies are designed to work only under particular
 * capacity or queuing policies. </dd>
 * 可以定义和使用其他种类的{@link
 *   * JZRejectedExecutionHandler}类。 这样做需要一些照顾
 *   *特别是当政策设计仅在特定情况下工作时
 *   *能力或排队政策。
 * <dt>Hook methods</dt>
 * 钩子方法
 * <dd style="font-family:'DejaVu Sans', Arial, Helvetica, sans-serif">
 * This class provides {@code protected} overridable
 * {@link #beforeExecute(Thread, Runnable)} and
 * {@link #afterExecute(Runnable, Throwable)} methods that are called
 * before and after execution of each task.  These can be used to
 * manipulate the execution environment; for example, reinitializing
 * ThreadLocals, gathering statistics, or adding log entries.
 * Additionally, method {@link #terminated} can be overridden to perform
 * any special processing that needs to be done once the Executor has
 * fully terminated.
 * 这个类提供{@code protected}可覆盖
 *   * {@link #beforeExecute（Thread，Runnable）}和
 *   * {@link #afterExecute（Runnable，Throwable）}调用的方法
 *   *执行每个任务之前和之后。 这些可以用来
 *   *操纵执行环境; 例如，重新初始化
 *   * ThreadLocals，收集统计信息或添加日志条目。
 *   *另外，方法{@link #terminated}可以被覆盖来执行
 *   *一旦执行人员有任何需要做的特殊处理
 *   *完全终止。
 * <p>If hook, callback, or BlockingQueue methods throw exceptions,
 * internal worker threads may in turn fail, abruptly terminate, and
 * possibly be replaced.</dd>
 * 如果钩子，回调或BlockingQueue方法抛出异常，
 *   内部工作线程可能会失败，突然终止，
 *   *可能被更换。
 * <dt>Queue maintenance</dt>
 * 队列维护
 * <dd style="font-family:'DejaVu Sans', Arial, Helvetica, sans-serif">
 * Method {@link #getQueue()} allows access to the work queue
 * for purposes of monitoring and debugging.  Use of this method for
 * any other purpose is strongly discouraged.  Two supplied methods,
 * {@link #remove(Runnable)} and {@link #purge} are available to
 * assist in storage reclamation when large numbers of queued tasks
 * become cancelled.</dd>
 * 方法{@link #getQueue（）}允许访问工作队列
 *   *用于监控和调试。 使用这种方法
 *   *强烈不鼓励任何其他目的。 两种提供的方法，
 *   * {@link #remove（Runnable）}和{@link #purge}可用(清除)
 *   *在大量排队任务时协助进行存储回收
 *   *被取消。
 * <dt>Finalization</dt>
 * 定稿
 * <dd style="font-family:'DejaVu Sans', Arial, Helvetica, sans-serif">
 * A pool that is no longer referenced in a program <em>AND</em>
 * has no remaining threads will be {@code shutdown} automatically. If
 * you would like to ensure that unreferenced pools are reclaimed even
 * if users forget to call {@link #shutdown}, then you must arrange
 * that unused threads eventually die, by setting appropriate
 * keep-alive times, using a lower bound of zero core threads and/or
 * setting {@link #allowCoreThreadTimeOut(boolean)}.  </dd>
 * 在程序<em> AND </ em>中不再引用的池
 *   *没有剩余的线程将自动{@code shutdown}。 如果
 *   *您希望确保未引用的池被回收
 *   *如果用户忘记致电{@link #shutdown}，那么您必须安排
 *   *未使用的线程最终会死亡，通过设置适当
 *   *保持活着的时间，使用零核心线程的下限和/或
 *   *设置{@link #allowCoreThreadTimeOut（boolean）}
 * </dl>
 *
 * <p><b>Extension example</b>. Most extensions of this class
 * override one or more of the protected hook methods. For example,
 * here is a subclass that adds a simple pause/resume feature:
 * 扩展示例</ b>。 这个类的大部分扩展
 *   *覆盖一个或多个受保护的钩子方法。 例如，
 *   *这是一个添加一个简单的暂停/恢复功能的子类：
 * <pre> {@code
 * class JZPausableThreadPoolExecutor extends ThreadPoolExecutor {
 *   private boolean isPaused;
 *   private ReentrantLock pauseLock = new ReentrantLock();
 *   private Condition unpaused = pauseLock.newCondition();
 *
 *   public JZPausableThreadPoolExecutor(...) { super(...); }
 *
 *   protected void beforeExecute(Thread t, Runnable r) {
 *     super.beforeExecute(t, r);
 *     pauseLock.lock();
 *     try {
 *       while (isPaused) unpaused.await();
 *     } catch (InterruptedException ie) {
 *       t.interrupt();
 *     } finally {
 *       pauseLock.unlock();
 *     }
 *   }
 *
 *   public void pause() {
 *     pauseLock.lock();
 *     try {
 *       isPaused = true;
 *     } finally {
 *       pauseLock.unlock();
 *     }
 *   }
 *
 *   public void resume() {
 *     pauseLock.lock();
 *     try {
 *       isPaused = false;
 *       unpaused.signalAll();
 *     } finally {
 *       pauseLock.unlock();
 *     }
 *   }
 * }}</pre>
 *
 * @author Doug Lea
 * @since 1.5
 */
public class JZControllableThreadPoolExecutor extends AbstractExecutorService {
    private final String TAG = "JZControllableThreadPoolExecutor";
    /**
     * The main pool control state, ctl, is an atomic integer packing
     * two conceptual fields
     * workerCount, indicating the effective number of threads
     * runState,    indicating whether running, shutting down etc
     * <p>
     * In order to pack them into one int, we limit workerCount to
     * (2^29)-1 (about 500 million) threads rather than (2^31)-1 (2
     * billion) otherwise rep resentable. If this is ever an issue in
     * the future, the variable can be changed to be an AtomicLong,
     * and the shift/mask constants below adjusted. But until the need
     * arises, this code is a bit faster and simpler using an int.
     * <p>
     * The workerCount is the number of workers that have been
     * permitted to start and not permitted to stop.  The value may be
     * transiently different from the actual number of live threads,
     * for example when a ThreadFactory fails to create a thread when
     * asked, and when exiting threads are still performing
     * bookkeeping before terminating. The user-visible pool size is
     * reported as the current size of the workers set.
     * <p>
     * The runState provides the main lifecycle control, taking on values:
     * <p>
     * RUNNING:  Accept new tasks and process queued tasks
     * SHUTDOWN: Don't accept new tasks, but process queued tasks
     * STOP:     Don't accept new tasks, don't process queued tasks,
     * and interrupt in-progress tasks
     * TIDYING:  All tasks have terminated, workerCount is zero,
     * the thread transitioning to state TIDYING
     * will run the terminated() hook method
     * TERMINATED: terminated() has completed
     * <p>
     * The numerical order among these values matters, to allow
     * ordered comparisons. The runState monotonically increases over
     * time, but need not hit each state. The transitions are:
     * <p>
     * RUNNING -> SHUTDOWN
     * On invocation of shutdown(), perhaps implicitly in finalize()
     * (RUNNING or SHUTDOWN) -> STOP
     * On invocation of shutdownNow()
     * SHUTDOWN -> TIDYING
     * When both queue and pool are empty
     * STOP -> TIDYING
     * When pool is empty
     * TIDYING -> TERMINATED
     * When the terminated() hook method has completed
     * <p>
     * Threads waiting in awaitTermination() will return when the
     * state reaches TERMINATED.
     * <p>
     * Detecting the transition from SHUTDOWN to TIDYING is less
     * straightforward than you'd like because the queue may become
     * empty after non-empty and vice versa during SHUTDOWN state, but
     * we can only terminate if, after seeing that it is empty, we see
     * that workerCount is 0 (which sometimes entails a recheck -- see
     * below).
     * 主池控制状态ctl是原子整数包装
     *      *两个概念领域
     *      * workerCount，表示有效的线程数
     *      * runState，指示是否运行，关闭等
     *      *
     *      *为了将它们打包成一个int，我们将workerCount限制为
     *      *（2 ^ 29）-1（约5亿）线程而不是（2 ^ 31）-1（2
     *      *亿）否则表示反感。如果这是一个问题
     *      *未来，变量可以改为原子龙，
     *      *和shift / mask常数下面调整。但直到需要
     *      *出现，这个代码使用int更快更简单。
     *      *
     *      workCount是已经有的工人数
     *      *允许启动，不允许停止。值可能是
     *      *与实际线程的实际数量暂时不同，
     *      *例如当ThreadFactory无法创建线程时
     *      *问，当退出线程仍然执行
     *      *终止前的簿记。用户可见的池大小是
     *      *报告为目前工作人数的大小。
     *      *
     *      * runState提供主要的生命周期控制，取值：
     *      *
     *      *运行：接受新任务并处理排队的任务
     *      * SHUTDOWN：不接受新任务，但处理排队的任务
     *      * STOP：不接受新任务，不处理排队的任务，
     *      *和中断进行中的任务
     *      * TIDYING：所有任务已终止，workerCount为零，
     *      *线程过渡到状态TIDYING
     *      *将运行terminate（）钩子方法
     *      * TERMINATED：terminated（）已完成
     *      *
     *      *这些值之间的数字顺序是重要的，以允许
     *      *有序比较。 runState单调增加
     *      *时间，但不需要打每个状态。过渡是：
     *      *
     *      *运行 - >关闭
     *      *关于shutdown（）的调用，可能隐含在finalize（）
     *      *（RUNNING或SHUTDOWN） - > STOP
     *      *调用shutdownNow（）
     *      * SHUTDOWN - > TIDYING
     *      *当队列和池都为空时
     *      *停止 - > TIDYING
     *      *当池为空时
     *      * TIDYING - >终止
     *      * terminate（）hook方法完成后
     *      *
     *      *等待等待的线程等待在Termination（）将返回
     *      国家到达TERMINATED。
     *      *
     *      *检测从SHUTDOWN到TIDYING的过渡是较少的
     *      *直接比你想要的，因为队列可能会成为
     *      *在空白之后为空，反之亦然，在SHUTDOWN状态下，但是
     *      *我们只能在看到它是空的之后终止
     *      * workerCount为0（有时需要重新检查 - 请参阅
     *      *以下）。
     */
    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY = (1 << COUNT_BITS) - 1;

    // runState is stored in the high-order bits// runState存储在高位位
    private static final int RUNNING = -1 << COUNT_BITS;
    private static final int SHUTDOWN = 0 << COUNT_BITS;
    private static final int STOP = 1 << COUNT_BITS;
    private static final int TIDYING = 2 << COUNT_BITS;
    private static final int TERMINATED = 3 << COUNT_BITS;

    // Packing and unpacking ctl//包装和拆包ctl
    private static int runStateOf(int c) {
        return c & ~CAPACITY;
    }

    private static int workerCountOf(int c) {
        return c & CAPACITY;
    }

    private static int ctlOf(int rs, int wc) {
        return rs | wc;
    }

    /*
     * Bit field accessors that don't require unpacking ctl.
     * These depend on the bit layout and on workerCount being never negative.
     * 不需要打包ctl的位字段访问器。
      *这些取决于位布局，而workerCount从不负面。
     */

    private static boolean runStateLessThan(int c, int s) {
        return c < s;
    }

    private static boolean runStateAtLeast(int c, int s) {
        return c >= s;
    }

    private static boolean isRunning(int c) {
        return c < SHUTDOWN;
    }

    /**
     * Attempts to CAS-increment the workerCount field of ctl.
     * 尝试CAS增加ctl的workerCount字段。
     */
    private boolean compareAndIncrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect + 1);
    }

    /**
     * Attempts to CAS-decrement the workerCount field of ctl.
     * 尝试CAS减去ctl的workerCount字段。
     */
    private boolean compareAndDecrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect - 1);
    }

    /**
     * Decrements the workerCount field of ctl. This is called only on
     * abrupt termination of a thread (see processWorkerExit). Other
     * decrements are performed within getTask.
     * 减去ctl的workerCount字段。 这仅仅是被称为
     *       *线程突然终止（请参阅processWorkerExit）。 其他
     *       *递减在getTask中执行。
     */
    private void decrementWorkerCount() {
        do {
        } while (!compareAndDecrementWorkerCount(ctl.get()));
    }

    /**
     * The queue used for holding tasks and handing off to worker
     * threads.  We do not require that workQueue.poll() returning
     * null necessarily means that workQueue.isEmpty(), so rely
     * solely on isEmpty to see if the queue is empty (which we must
     * do for example when deciding whether to transition from
     * SHUTDOWN to TIDYING).  This accommodates special-purpose
     * queues such as DelayQueues for which poll() is allowed to
     * return null even if it may later return non-null when delays
     * expire.
     * 用于保存任务并交给工作人员的队列
     *       *线程。 我们不要求工作Queue.poll（）返回
     *       * null必然意味着workQueue.isEmpty（），所以依赖
     *       *仅在isEmpty上查看队列是否为空（我们必须这样做）
     *       *例如在决定是否转换时
     *       *关闭TIDYING）。 这适应特殊目的
     *       *允许poll（）允许的队列，如DelayQueues
     *       *返回null，即使稍后可能在延迟时返回非空
     *       *过期
     */
    private final BlockingQueue<Runnable> workQueue;

    /**
     * Lock held on access to workers set and related bookkeeping.
     * While we could use a concurrent set of some sort, it turns out
     * to be generally preferable to use a lock. Among the reasons is
     * that this serializes interruptIdleWorkers, which avoids
     * unnecessary interrupt storms, especially during shutdown.
     * Otherwise exiting threads would concurrently interrupt those
     * that have not yet interrupted. It also simplifies some of the
     * associated statistics bookkeeping of largestPoolSize etc. We
     * also hold mainLock on shutdown and shutdownNow, for the sake of
     * ensuring workers set is stable while separately checking
     * permission to interrupt and actually interrupting.
     * 锁定进入工人集和相关簿记。
     *       *虽然我们可以使用一些并发的某种类型，但事实证明
     *       *一般最好使用锁。 原因之一是
     *       *这个序列化了interruptIdleWorkers，避免了
     *       *不必要的中断风暴，特别是在关机期间。
     *       *否则退出线程将同时中断这些
     *       *尚未中断。 它也简化了一些
     *       *相关的统计簿记簿最大的手机大小等
     *       *也在mainLock关机和shutdownNow，为了
     *       确保工作人员在分开检查时稳定
     *       *允许中断和实际中断。
     */
    private final ReentrantLock mainLock = new ReentrantLock();

    /**
     * Set containing all worker threads in pool. Accessed only when
     * holding mainLock.
     * 集合包含池中的所有工作线程。 只能在
     *       *持有mainLock。
     */
    private final HashSet<Worker> workers = new HashSet<>();

    /*
     * Wait condition to support awaitTermination.
     * 等待条件支持等待终止。
     */
    private final Condition termination = mainLock.newCondition();

    /**
     * Tracks largest attained pool size. Accessed only under
     * mainLock.
     * 达到最大游泳池大小。 只能在下面
     *       * mainLock。
     */
    private int largestPoolSize;

    /**
     * Counter for completed tasks. Updated only on termination of
     * worker threads. Accessed only under mainLock.
     * 计数器完成任务。 仅在终止时更新
     *       *工作线程。 只能在mainLock下使用。
     */
    private long completedTaskCount;

    /*
     * All user control parameters are declared as volatiles so that
     * ongoing actions are based on freshest values, but without need
     * for locking, since no internal invariants depend on them
     * changing synchronously with respect to other actions.
     * 所有用户控制参数都被声明为挥发物
      *持续的行动是基于最新鲜的价值观，但不需要
      *用于锁定，因为内部不变量取决于它们
      *与其他动作同步变化。
     */

    /**
     * Factory for new threads. All threads are created using this
     * factory (via method addWorker).  All callers must be prepared
     * for addWorker to fail, which may reflect a system or user's
     * policy limiting the number of threads.  Even though it is not
     * treated as an error, failure to create threads may result in
     * new tasks being rejected or existing ones remaining stuck in
     * the queue.
     * <p>
     * We go further and preserve pool invariants even in the face of
     * errors such as OutOfMemoryError, that might be thrown while
     * trying to create threads.  Such errors are rather common due to
     * the need to allocate a native stack in Thread.start, and users
     * will want to perform clean pool shutdown to clean up.  There
     * will likely be enough memory available for the cleanup code to
     * complete without encountering yet another OutOfMemoryError.
     * 工厂新线程。 所有线程都是使用这个创建的
     *       *工厂（通过方法addWorker）。 所有来电者都必须准备好
     *       * for addWorker失败，这可能反映了系统或用户的
     *       *策略限制线程数。 即使不是
     *       *被视为错误，无法创建线程可能会导致
     *       *新任务被拒绝或现有的任务仍然存在
     *       *队列。
     *      *
     *       *即使面对，我们进一步保存池不变量
     *       *错误，如OutOfMemoryError，可能会抛出
     *       尝试创建线程。 这样的错误是相当普遍的
     *       *需要在Thread.start和用户中分配本机堆栈
     *       *将要执行清洁池关闭来清理。 那里
     *       *可能有足够的内存可用于清理代码
     *       *完成，而不会遇到另外一个OutOfMemoryError。
     */
    private volatile ThreadFactory threadFactory;

    /**
     * Handler called when saturated or shutdown in execute.
     * 处理程序在执行饱和或关闭时调用。
     */
    private volatile JZRejectedExecutionHandler handler;

    /**
     * Timeout in nanoseconds for idle threads waiting for work.
     * Threads use this timeout when there are more than corePoolSize
     * present or if allowCoreThreadTimeOut. Otherwise they wait
     * forever for new work.
     * 超时等待工作的空闲线程的纳秒。
     *       *当有超过corePoolSize时，线程使用此超时
     *       * present或if allowCoreThreadTimeOut。 否则他们等待
     *       *永远为新的工作。
     */
    private volatile long keepAliveTime;

    /**
     * If false (default), core threads stay alive even when idle.
     * If true, core threads use keepAliveTime to time out waiting
     * for work.
     * 如果为false（默认），即使空闲时，内核线程仍然保持活动状态。
     *       *如果是真的，核心线程使用keepAliveTime超时等待
     *       *工作
     */
    private volatile boolean allowCoreThreadTimeOut;

    /**
     * Core pool size is the minimum number of workers to keep alive
     * (and not allow to time out etc) unless allowCoreThreadTimeOut
     * is set, in which case the minimum is zero.
     * 核心池大小是保持活力的最少工人人数
     *       *（并且不允许超时等），除非allowCoreThreadTimeOut
     *       *设置，在这种情况下，最小值为零。
     */
    private volatile int corePoolSize;

    /**
     * Maximum pool size. Note that the actual maximum is internally
     * bounded by CAPACITY.
     * 最大池大小。 请注意，实际最大值在内部
     *       *受CAPACITY限制。
     */
    private volatile int maximumPoolSize;

    /**
     * The default rejected execution handler.
     * 默认拒绝的执行处理程序。
     */
    private static final JZRejectedExecutionHandler defaultHandler =
            (JZRejectedExecutionHandler) new AbortPolicy();

    /**
     * Permission required for callers of shutdown and shutdownNow.
     * We additionally require (see checkShutdownAccess) that callers
     * have permission to actually interrupt threads in the worker set
     * (as governed by Thread.interrupt, which relies on
     * ThreadGroup.checkAccess, which in turn relies on
     * SecurityManager.checkAccess). Shutdowns are attempted only if
     * these checks pass.
     * <p>
     * All actual invocations of Thread.interrupt (see
     * interruptIdleWorkers and interruptWorkers) ignore
     * SecurityExceptions, meaning that the attempted interrupts
     * silently fail. In the case of shutdown, they should not fail
     * unless the SecurityManager has inconsistent policies, sometimes
     * allowing access to a thread and sometimes not. In such cases,
     * failure to actually interrupt threads may disable or delay full
     * termination. Other uses of interruptIdleWorkers are advisory,
     * and failure to actually interrupt will merely delay response to
     * configuration changes so is not handled exceptionally.
     * 呼叫者关机和关机所需的权限。
     *      *我们另外要求（见checkShutdownAccess）呼叫者
     *      *具有实际中断工作集中的线程的权限
     *      *（由Thread.interrupt所依赖的
     *      * ThreadGroup.checkAccess，依次依赖
     *      * SecurityManager.checkAccess）。只有在以下情况下才会尝试停机
     *      *这些支票通过。
     *      *
     *      * Thread.interrupt的所有实际调用（见
     *      * interruptIdleWorkers和interruptWorkers）忽略
     *      * SecurityExceptions，意味着尝试的中断
     *      *默默地失败。在关机的情况下，不应该失败
     *      *除非SecurityManager有不一致的策略
     *      *允许访问线程，有时不允许。在这种情况下，
     *      *无法实际中断线程可能会禁用或延迟完整
     *      *终止。 interruptIdleWorkers的其他用途是咨询，
     *      *和无法实际中断只会延迟响应
     *      *配置更改，因此没有特别处理。
     */
    private static final RuntimePermission shutdownPerm =
            new RuntimePermission("modifyThread");

    /**
     * Class Worker mainly maintains interrupt control state for
     * threads running tasks, along with other minor bookkeeping.
     * This class opportunistically extends AbstractQueuedSynchronizer
     * to simplify acquiring and releasing a lock surrounding each
     * task execution.  This protects against interrupts that are
     * intended to wake up a worker thread waiting for a task from
     * instead interrupting a task being run.  We implement a simple
     * non-reentrant mutual exclusion lock rather than use
     * ReentrantLock because we do not want worker tasks to be able to
     * reacquire the lock when they invoke pool control methods like
     * setCorePoolSize.  Additionally, to suppress interrupts until
     * the thread actually starts running tasks, we initialize lock
     * state to a negative value, and clear it upon start (in
     * runWorker).
     * 类工作者主要保持中断控制状态
     *       *线程运行任务，以及其他较小的簿记。
     *       *这个类机会地扩展了AbstractQueuedSynchronizer
     *       *简化获取和释放周围的锁
     *       *任务执行。 这样可以防止中断
     *       *旨在唤醒等待任务的工作线程
     *       *而是中断正在运行的任务。 我们实现一个简单的
     *       *不可重入互斥锁而不是使用
     *       * ReentrantLock因为我们不希望工作任务能够
     *       *当他们调用池控制方法时，重新获取锁
     *       * setCorePoolSize。 另外，为了抑制中断，直到
     *       *线程实际上开始运行任务，我们初始化锁
     *       *状态为负值，并在开始时清除（in
     *       * runWorker）。
     */
    // TODO:  原来为：  private final class Worker
    private final class Worker
            extends AbstractQueuedSynchronizer
            implements Runnable {
        /**
         * This class will never be serialized, but we provide a
         * serialVersionUID to suppress a javac warning.
         * 这个类永远不会被序列化，但是我们提供了一个
         *           * serialVersionUID来抑制javac警告。
         */
        private static final long serialVersionUID = 6138294804551838833L;

        /**
         * Thread this worker is running in.  Null if factory fails.线程这个工作正在运行。如果工厂出现故障，则为空。
         */
        final Thread thread;
        /**
         * Initial task to run.  Possibly null.
         *///运行的初始任务。 可能为空
        Runnable firstTask;
        /**
         * Per-thread task counter 每线程任务计数器
         */
        volatile long completedTasks;// TODO: 2017/5/13 完成的任务数

        /**
         * Creates with given first task and thread from ThreadFactory.
         *
         * @param firstTask the first task (null if none)
         *                  从ThreadFactory创建给定的第一个任务的线程。
         *                            * @param firstTask第一个任务（如果没有null）
         */
        Worker(Runnable firstTask) {// TODO: 2017/5/13 构造方法
            setState(-1); // inhibit interrupts until 禁止打断线程直到runWorker任务
            this.firstTask = firstTask;
            this.thread = getThreadFactory().newThread(this);
        }

        /**
         * Delegates main run loop to outer runWorker. 将主运行循环委托给外部的runWorker。
         */
        public void run() {
            runWorker(this);
        }

        // Lock methods
        //
        // The value 0 represents the unlocked state.
        // The value 1 represents the locked state.
        //锁定方法
        //值0表示解锁状态。
        //值为1表示锁定状态。

        protected boolean isHeldExclusively() {
            return getState() != 0;
        }

        protected boolean tryAcquire(int unused) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        protected boolean tryRelease(int unused) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        public void lock() {
            acquire(1);
        }

        public boolean tryLock() {
            return tryAcquire(1);
        }

        public void unlock() {
            release(1);
        }

        public boolean isLocked() {
            return isHeldExclusively();
        }

        void interruptIfStarted() {
            Thread t;
            if (getState() >= 0 && (t = thread) != null && !t.isInterrupted()) {
                try {
                    t.interrupt();
                } catch (SecurityException ignore) {
                }
            }
        }
    }

    /*
     * Methods for setting control state设置控制状态的方法
     */

    /**
     * Transitions runState to given target, or leaves it alone if
     * already at least the given target.
     * 将runState转换为给定目标，或者单独留下
     *       至少已经给定了目标。
     *
     * @param targetState the desired state, either SHUTDOWN or STOP
     *                    (but not TIDYING or TERMINATED -- use tryTerminate for that)
     *                    target将所需的状态设置为SHUTDOWN或STOP
     *                          *（但不是TIDYING或TERMINATED - 使用tryTerminate））
     */
    private void advanceRunState(int targetState) {
        // assert targetState == SHUTDOWN || targetState == STOP;
        for (; ; ) {
            int c = ctl.get();
            if (runStateAtLeast(c, targetState) ||
                    ctl.compareAndSet(c, ctlOf(targetState, workerCountOf(c))))
                break;
        }
    }

    /**
     * Transitions to TERMINATED state if either (SHUTDOWN and pool
     * and queue empty) or (STOP and pool empty).  If otherwise
     * eligible to terminate but workerCount is nonzero, interrupts an
     * idle worker to ensure that shutdown signals propagate. This
     * method must be called following any action that might make
     * termination possible -- reducing worker count or removing tasks
     * from the queue during shutdown. The method is non-private to
     * allow access from ScheduledThreadPoolExecutor.
     * 转换到TERMINATED状态，如果（SHUTDOWN和池
     *       *和队列为空）或（停止和池空）。 否则
     *       *有资格终止，但workerCount不为零，中断一个
     *       *空闲工作人员确保关闭信号传播。 这个
     *       必须按照可能产生的任何动作来调用*方法
     *       *终止可能 - 减少工人数或删除任务
     *       *在关机时从队列中。 该方法是非私有的
     *       *允许从ScheduledThreadPoolExecutor访问。
     */
    final void tryTerminate() {
        for (; ; ) {
            int c = ctl.get();
            if (isRunning(c) ||
                    runStateAtLeast(c, TIDYING) ||
                    (runStateOf(c) == SHUTDOWN && !workQueue.isEmpty()))
                return;
            if (workerCountOf(c) != 0) { // Eligible to terminate
                interruptIdleWorkers(ONLY_ONE);
                return;
            }

            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                if (ctl.compareAndSet(c, ctlOf(TIDYING, 0))) {
                    try {
                        terminated();
                    } finally {
                        ctl.set(ctlOf(TERMINATED, 0));
                        termination.signalAll();
                    }
                    return;
                }
            } finally {
                mainLock.unlock();
            }
            // else retry on failed CAS
        }
    }

    /*
     * Methods for controlling interrupts to worker threads.
     */

    /**
     * 控制工作线程中断的方法。
     * <p>
     *       *如果有安全管理员，确保来电者有
     *       *一般关闭线程的权限（请参阅shutdownPerm）。
     *       *如果通过，另外确保主叫方被允许
     *       *中断每个工作线程。 这可能不是真的，即使
     *       *首先检查通过，如果SecurityManager对待一些线程
     *       *特别
     * If there is a security manager, makes sure caller has
     * permission to shut down threads in general (see shutdownPerm).
     * If this passes, additionally makes sure the caller is allowed
     * to interrupt each worker thread. This might not be true even if
     * first check passed, if the SecurityManager treats some threads
     * specially.
     */
    private void checkShutdownAccess() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(shutdownPerm);
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                for (Worker w : workers)
                    security.checkAccess(w.thread);
            } finally {
                mainLock.unlock();
            }
        }
    }

    /**
     * Interrupts all threads, even if active. Ignores SecurityExceptions
     * (in which case some threads may remain uninterrupted).
     * 中断所有线程，即使活动。 忽略SecurityExceptions
     *       *（在这种情况下，一些线程可能保持不间断）。
     */
    private void interruptWorkers() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (Worker w : workers)
                w.interruptIfStarted();
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Interrupts threads that might be waiting for tasks (as
     * indicated by not being locked) so they can check for
     * termination or configuration changes. Ignores
     * SecurityExceptions (in which case some threads may remain
     * uninterrupted).
     *
     * @param onlyOne If true, interrupt at most one worker. This is
     *                called only from tryTerminate when termination is otherwise
     *                enabled but there are still other workers.  In this case, at
     *                most one waiting worker is interrupted to propagate shutdown
     *                signals in case all threads are currently waiting.
     *                Interrupting any arbitrary thread ensures that newly arriving
     *                workers since shutdown began will also eventually exit.
     *                To guarantee eventual termination, it suffices to always
     *                interrupt only one idle worker, but shutdown() interrupts all
     *                idle workers so that redundant workers exit promptly, not
     *                waiting for a straggler task to finish.
     *                中断可能正在等待任务的线程（as
     *                      *表示不被锁定），所以他们可以检查
     *                      *终止或配置更改。忽略
     *                      * SecurityExceptions（在这种情况下，一些线程可能会保留
     *                      *不间断）。
     *                     *
     *                      * @param onlyOne如果为true，则最多中断一名工作人员。 这是
     *                      *只有在终止时才从tryTerminate调用
     *                      *启用，但还有其他工作人员。 在这种情况下，
     *                      *大多数等待工作人员中断宣传关机
     *                      *信号，以防所有线程正在等待。
     *                      *中断任意任意线程确保新到达
     *                      *工人从关机开始也将最终退出。
     *                      *为保证最终的终止，始终是足够的
     *                      *仅中断一个空闲工作，但shutdown（）中断所有
     *                      *闲置的工作人员，使多余的工人迅速退出，而不是
     *                      *等待一个分散的任务完成。
     */
    private void interruptIdleWorkers(boolean onlyOne) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (Worker w : workers) {
                Thread t = w.thread;
                if (!t.isInterrupted() && w.tryLock()) {
                    try {
                        t.interrupt();
                    } catch (SecurityException ignore) {
                    } finally {
                        w.unlock();
                    }
                }
                if (onlyOne)
                    break;
            }
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Common form of interruptIdleWorkers, to avoid having to
     * remember what the boolean argument means.
     * 通常形式的interruptIdleWorkers，以避免必须
     *       *记住布尔参数的含义。
     */
    private void interruptIdleWorkers() {
        interruptIdleWorkers(false);
    }

    private static final boolean ONLY_ONE = true;

    /*
     * Misc utilities, most of which are also exported to
     * ScheduledThreadPoolExecutor
     * 其他公用事业，其中大部分也被导出
      * ScheduledThreadPoolExecutor
     */

    /**
     * Invokes the rejected execution handler for the given command.
     * Package-protected for use by ScheduledThreadPoolExecutor.
     * 调用给定命令的被拒绝的执行处理程序。
     *       *包保护供ScheduledThreadPoolExecutor使用。
     */
    final void reject(Runnable command) {
        handler.rejectedExecution(command, this);
    }

    /**
     * Performs any further cleanup following run state transition on
     * invocation of shutdown.  A no-op here, but used by
     * ScheduledThreadPoolExecutor to cancel delayed tasks.
     * 在运行状态转换之后执行任何进一步的清理
     *       *调用关机。 这里没有操作，但被
     *       * ScheduledThreadPoolExecutor取消延迟任务。
     */
    void onShutdown() {
    }

    /**
     * State check needed by ScheduledThreadPoolExecutor to
     * enable running tasks during shutdown.
     * ScheduledThreadPoolExecutor需要进行状态检查
     *       *在关机期间启用运行任务。
     *      *
     *       * @param shutdownOK如果应该返回true，如果SHUTDOWN
     *
     * @param shutdownOK true if should return true if SHUTDOWN
     */
    final boolean isRunningOrShutdown(boolean shutdownOK) {
        int rs = runStateOf(ctl.get());
        return rs == RUNNING || (rs == SHUTDOWN && shutdownOK);
    }

    /**
     * Drains the task queue into a new list, normally using
     * drainTo. But if the queue is a DelayQueue or any other kind of
     * queue for which poll or drainTo may fail to remove some
     * elements, it deletes them one by one.
     * 将任务队列排入新列表，通常使用
     *       * drainTo。 但是如果队列是DelayQueue或任何其他类型的
     *       *队列为哪个poll或drainTo可能无法删除一些
     *       *元素，它逐个删除它们。
     */
    private List<Runnable> drainQueue() {
        BlockingQueue<Runnable> q = workQueue;
        ArrayList<Runnable> taskList = new ArrayList<>();
        q.drainTo(taskList);
        if (!q.isEmpty()) {
            for (Runnable r : q.toArray(new Runnable[0])) {
                if (q.remove(r))
                    taskList.add(r);
            }
        }
        return taskList;
    }

    /*
     * Methods for creating, running and cleaning up after workers工作人员创建，运行和清理的方法
     */

    /**
     * Checks if a new worker can be added with respect to current
     * pool state and the given bound (either core or maximum). If so,
     * the worker count is adjusted accordingly, and, if possible, a
     * new worker is created and started, running firstTask as its
     * first task. This method returns false if the pool is stopped or
     * eligible to shut down. It also returns false if the thread
     * factory fails to create a thread when asked.  If the thread
     * creation fails, either due to the thread factory returning
     * null, or due to an exception (typically OutOfMemoryError in
     * Thread.start()), we roll back cleanly.
     *
     * @param firstTask the task the new thread should run first (or
     *                  null if none). Workers are created with an initial first task
     *                  (in method execute()) to bypass queuing when there are fewer
     *                  than corePoolSize threads (in which case we always start one),
     *                  or when the queue is full (in which case we must bypass queue).
     *                  Initially idle threads are usually created via
     *                  prestartCoreThread or to replace other dying workers.
     * @param core      if true use corePoolSize as bound, else
     *                  maximumPoolSize. (A boolean indicator is used here rather than a
     *                  value to ensure reads of fresh values after checking other pool
     *                  state).
     * @return true if successful
     * 检查是否可以添加新的工作人员
     *      *池状态和给定绑定（核心或最大值）。如果是这样，
     *      *相应调整工人人数，如有可能，
     *      * new worker被创建并启动，运行firstTask作为它
     *      *第一任务如果池停止或此方法返回false
     *      *有资格关闭。如果线程也返回false
     *      *询问时，工厂无法创建线程。如果线程
     *      *创建失败，由于线程工厂返回
     *      * null或由于异常（通常OutOfMemoryError in
     *      * Thread.start（）），我们回滚干净。
     *      *
     *      * @param firstTask新线程应该运行的任务（或
     *      *如果没有，则为null）。创建工作人员的初始第一任务
     *      *（在方法execute（））中绕过排队次数少的时候
     *      *比corePoolSize线程（在这种情况下我们总是启动一个），
     *      *或队列满时（在这种情况下我们必须绕过队列）。
     *      *最初空闲线程通常是通过创建的
     *      * prestartCoreThread或替换其他垂死的工作人员。
     *      *
     *      * @param core如果真的使用corePoolSize作为绑定，否则
     *      * maximumPoolSize。 （这里使用布尔指标，而不是a
     *      *值，以确保在检查其他池后读取新值
     *      *状态）。
     *      * @如果成功返回true
     */
    private boolean addWorker(Runnable firstTask, boolean core) {
        retry:
        for (; ; ) {
            int c = ctl.get();
            int rs = runStateOf(c);

            // Check if queue empty only if necessary.检查队列是否只在必要时为空。
            if (rs >= SHUTDOWN &&
                    !(rs == SHUTDOWN &&
                            firstTask == null &&
                            !workQueue.isEmpty()))
                return false;

            for (; ; ) {
                int wc = workerCountOf(c);
                if (wc >= CAPACITY ||
                        wc >= (core ? corePoolSize : maximumPoolSize))
                    return false;
                if (compareAndIncrementWorkerCount(c))
                    break retry;
                c = ctl.get();  // Re-read ctl
                if (runStateOf(c) != rs)
                    continue retry;
                // else CAS failed due to workerCount change; retry inner loop
                //其他CAS由于workerCount更改而失败; 重试内循环
            }
        }

        boolean workerStarted = false;
        boolean workerAdded = false;
        Worker w = null;
        try {
            w = new Worker(firstTask);

            final Thread t = w.thread;
            if (t != null) {
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    // Recheck while holding lock.
                    // Back out on ThreadFactory failure or if
                    // shut down before lock acquired.
                    //握住锁时重新检查。
//                     //退出ThreadFactory失败或if
//                     //在锁获取之前关闭。
                    int rs = runStateOf(ctl.get());

                    if (rs < SHUTDOWN ||
                            (rs == SHUTDOWN && firstTask == null)) {
                        if (t.isAlive()) // precheck that t is startable
                            throw new IllegalThreadStateException();
                        workers.add(w);
                        int s = workers.size();
                        if (s > largestPoolSize)
                            largestPoolSize = s;
                        workerAdded = true;
                    }
                } finally {
                    mainLock.unlock();
                }
                if (workerAdded) {
                    t.start();
                    mThreadList.add(w);
                    workerStarted = true;
                }
            }
        } finally {
            if (!workerStarted)
                addWorkerFailed(w);
        }
        return workerStarted;
    }

    // TODO: 2017/5/10 已运行的线程列表
    static List<Worker> mThreadList = new ArrayList<>();

    /**
     * Rolls back the worker thread creation.
     * - removes worker from workers, if present
     * - decrements worker count
     * - rechecks for termination, in case the existence of this
     * worker was holding up termination
     * 回滚工作线程创建。
     *       * - 如果存在，将工人从工人身上移走
     *       * - 减少工人数
     *       * - 重新检查终止，万一存在这种情况
     *       工作人员正在终止
     */
    private void addWorkerFailed(Worker w) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            if (w != null)
                workers.remove(w);
            decrementWorkerCount();
            tryTerminate();
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Performs cleanup and bookkeeping for a dying worker. Called
     * only from worker threads. Unless completedAbruptly is set,
     * assumes that workerCount has already been adjusted to account
     * for exit.  This method removes thread from worker set, and
     * possibly terminates the pool or replaces the worker if either
     * it exited due to user task exception or if fewer than
     * corePoolSize workers are running or queue is non-empty but
     * there are no workers.
     * 为垂死的工人执行清理和簿记。叫
     *       *只有从工作线程。 除非完成设定，
     *       *假设workerCount已经被调整到帐户
     *       *退出。 这个方法从工作集中删除线程
     *       *可能会终止池或替换工作者
     *       *由于用户任务异常而退出，或者少于
     *       * corePoolSize工作正在运行或队列不为空，但是
     *       *没有工人。
     *      *
     *       * @param w的工作人员
     *       * @param完成如果工作人员因用户异常而死亡
     *
     * @param w                 the worker
     * @param completedAbruptly if the worker died due to user exception
     */
    private void processWorkerExit(Worker w, boolean completedAbruptly) {
        if (completedAbruptly) // If abrupt, then workerCount wasn't adjusted
            decrementWorkerCount();

        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            completedTaskCount += w.completedTasks;
            workers.remove(w);
        } finally {
            mainLock.unlock();
        }

        tryTerminate();

        int c = ctl.get();
        if (runStateLessThan(c, STOP)) {
            if (!completedAbruptly) {
                int min = allowCoreThreadTimeOut ? 0 : corePoolSize;
                if (min == 0 && !workQueue.isEmpty())
                    min = 1;
                if (workerCountOf(c) >= min)
                    return; // replacement not needed
            }
            addWorker(null, false);
        }
    }

    /* 添加开始 */
    BlockingQueue<Runnable> pauseQueue = new ArrayBlockingQueue<>(1);//暂停时用来则塞线程的空任务队列
    boolean isPause = false;//暂停

    public void pause() {//暂停线程池，但是仍然接受任务
        isPause = true;
        System.out.println("暂停了" + isPause);
    }

    public void resume() {//恢复线程池，开始接着执行任务
        isPause = false;
        if (workQueue.isEmpty()) {
            return;
        }
        pauseQueue.offer(workQueue.poll());
    }
    /* 添加结束 */

    /**
     * Performs blocking or timed wait for a task, depending on
     * current configuration settings, or returns null if this worker
     * must exit because of any of:
     * 1. There are more than maximumPoolSize workers (due to
     * a call to setMaximumPoolSize).
     * 2. The pool is stopped.
     * 3. The pool is shutdown and the queue is empty.
     * 4. This worker timed out waiting for a task, and timed-out
     * workers are subject to termination (that is,
     * {@code allowCoreThreadTimeOut || workerCount > corePoolSize})
     * both before and after the timed wait, and if the queue is
     * non-empty, this worker is not the last thread in the pool.
     *
     * @return task, or null if the worker must exit, in which case
     * workerCount is decremented
     * 执行阻止或定时等待任务，具体取决于
     *       *当前配置设置，或返回null如果此工作
     *       *必须退出，因为以下任何一种：
     *       * 1.有超过maximumPoolSize的工作人员（由于
     *       *调用setMaximumPoolSize）。
     *       * 2.泳池停了。
     *       * 3.池被关闭，队列为空。
     *       * 4.这名工作人员超时等待任务，超时
     *       *工作人员将被终止（即，
     *       * {@code allowCoreThreadTimeOut || workerCount> corePoolSize}）
     *       *在定时等待之前和之后，如果队列是
     *       *非空，此工作人员不是池中的最后一个线程。
     *      *
     *       * @return任务，如果工作人员必须退出，则为null，在这种情况下
     *       * workerCount递减
     */
    private Runnable getTask() {
        boolean timedOut = false; // Did the last poll() time out?

        for (; ; ) {
            int c = ctl.get();
            int rs = runStateOf(c);

            // Check if queue empty only if necessary.
            if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
                decrementWorkerCount();
                return null;
            }

            int wc = workerCountOf(c);

            /* 添加开始 */
            if (isPause) {
                try {
                    return pauseQueue.take();
                } catch (InterruptedException e) {
                    JZLog.e(TAG, "getTask() to catch() InterruptedException: " + e.getMessage());
                }
            }
            /* 添加结束 */

            // Are workers subject to culling?
            boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;

            if ((wc > maximumPoolSize || (timed && timedOut))
                    && (wc > 1 || workQueue.isEmpty())) {
                if (compareAndDecrementWorkerCount(c))
                    return null;
                continue;
            }

            try {
                Runnable r = timed ?
                        workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                        workQueue.take();
                if (r != null)
                    return r;
                timedOut = true;
            } catch (InterruptedException retry) {
                timedOut = false;
            }
        }
    }

    /**
     * Main worker run loop.  Repeatedly gets tasks from queue and
     * executes them, while coping with a number of issues:
     * <p>
     * 1. We may start out with an initial task, in which case we
     * don't need to get the first one. Otherwise, as long as pool is
     * running, we get tasks from getTask. If it returns null then the
     * worker exits due to changed pool state or configuration
     * parameters.  Other exits result from exception throws in
     * external code, in which case completedAbruptly holds, which
     * usually leads processWorkerExit to replace this thread.
     * <p>
     * 2. Before running any task, the lock is acquired to prevent
     * other pool interrupts while the task is executing, and then we
     * ensure that unless pool is stopping, this thread does not have
     * its interrupt set.
     * <p>
     * 3. Each task run is preceded by a call to beforeExecute, which
     * might throw an exception, in which case we cause thread to die
     * (breaking loop with completedAbruptly true) without processing
     * the task.
     * <p>
     * 4. Assuming beforeExecute completes normally, we run the task,
     * gathering any of its thrown exceptions to send to afterExecute.
     * We separately handle RuntimeException, Error (both of which the
     * specs guarantee that we trap) and arbitrary Throwables.
     * Because we cannot rethrow Throwables within Runnable.run, we
     * wrap them within Errors on the way out (to the thread's
     * UncaughtExceptionHandler).  Any thrown exception also
     * conservatively causes thread to die.
     * <p>
     * 5. After task.run completes, we call afterExecute, which may
     * also throw an exception, which will also cause thread to
     * die. According to JLS Sec 14.20, this exception is the one that
     * will be in effect even if task.run throws.
     * <p>
     * The net effect of the exception mechanics is that afterExecute
     * and the thread's UncaughtExceptionHandler have as accurate
     * information as we can provide about any problems encountered by
     * user code.
     *
     * @param w the worker
     *          主要工人运行循环。重复从队列中获取任务
     *               *执行它们，同时应对一些问题：
     *               *
     *               * 1.我们可以从初始任务开始，在这种情况下我们
     *               *不需要得到第一个。否则，只要池是
     *               *运行，我们从getTask获取任务。如果它返回null，那么
     *               *由于更改池状态或配置，工作人员退出
     *               *参数。其他退出导致异常抛出
     *               *外部代码，在这种情况下完成了，其中
     *               *通常会导致processWorkerExit来替换此线程。
     *               *
     *               * 2.在运行任何任务之前，需要锁定以防止
     *               *其他池中断任务正在执行，然后我们
     *               *确保除非池停止，这个线程没有
     *               *其中断集。
     *               *
     *               * 3.每个任务运行之前是调用beforeExecute，其中
     *               *可能会抛出异常，在这种情况下，我们会导致线程死机
     *               *（打破循环，完成真实），无需处理
     *               * 任务。
     *               *
     *               * 4.假设beforeExecute正常完成，我们运行任务，
     *               *收集任何抛出的异常发送到afterExecute。
     *               *我们分别处理RuntimeException，Error（两者都是
     *               *规格保证我们陷阱）和任意Throwables。
     *               因为我们不能在Runnable.run内重复Throwables，所以我们
     *               *在出路的时候把它们包裹在错误中（对线程的）
     *               * UncaughtExceptionHandler）。任何抛出的异常也
     *               *保守地使线程死亡。
     *               *
     *               * 5. task.run完成后，我们调用afterExecute，这可能
     *               *也抛出异常，这也会导致线程
     *               * 死。根据JLS Sec 14.20，这个例外是
     *               *即使task.run抛出也会生效。
     *               *
     *               *异常机制的净效果是afterExecute
     *               *和线程的UncaughtExceptionHandler准确
     *               *信息，我们可以提供任何遇到的问题
     *               *用户代码。
     *               *
     *               * @param w的工作人员
     */
    final void runWorker(Worker w) {
        Thread wt = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        w.unlock(); // allow interrupts
        boolean completedAbruptly = true;
        try {
            while (task != null || (task = getTask()) != null) {
                w.lock();
                // If pool is stopping, ensure thread is interrupted;
                // if not, ensure thread is not interrupted.  This
                // requires a recheck in second case to deal with
                // shutdownNow race while clearing interrupt
                //如果池停止，请确保线程中断;
//                 //如果没有，确保线程不中断。 这个
//                 //需要在第二种情况下重新检查来处理
//                 // shutdownNow同时清除中断
                if ((runStateAtLeast(ctl.get(), STOP) ||
                        (Thread.interrupted() &&
                                runStateAtLeast(ctl.get(), STOP))) &&
                        !wt.isInterrupted())
                    wt.interrupt();
                try {
                    beforeExecute(wt, task);
                    Throwable thrown = null;
                    try {
                        task.run();
                    } catch (RuntimeException x) {
                        thrown = x;
                        throw x;
                    } catch (Error x) {
                        thrown = x;
                        throw x;
                    } catch (Throwable x) {
                        thrown = x;
                        throw new Error(x);
                    } finally {
                        afterExecute(task, thrown);
                    }
                } finally {
                    task = null;
                    w.completedTasks++;
                    w.unlock();
                }
            }
            completedAbruptly = false;
        } finally {
            processWorkerExit(w, completedAbruptly);
        }
    }

    // Public constructors and methods

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters and default thread factory and rejected execution handler.
     * It may be more convenient to use one of the {@link Executors} factory
     * methods instead of this general purpose constructor.
     *
     * @param corePoolSize    the number of threads to keep in the pool, even
     *                        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *                        pool
     * @param keepAliveTime   when the number of threads is greater than
     *                        the core, this is the maximum time that excess idle threads
     *                        will wait for new tasks before terminating.
     * @param unit            the time unit for the {@code keepAliveTime} argument
     * @param workQueue       the queue to use for holding tasks before they are
     *                        executed.  This queue will hold only the {@code Runnable}
     *                        tasks submitted by the {@code execute} method.
     * @throws IllegalArgumentException if one of the following holds:<br>
     *                                  {@code corePoolSize < 0}<br>
     *                                  {@code keepAliveTime < 0}<br>
     *                                  {@code maximumPoolSize <= 0}<br>
     *                                  {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException     if {@code workQueue} is null
     */
    public JZControllableThreadPoolExecutor(int corePoolSize,
                                            int maximumPoolSize,
                                            long keepAliveTime,
                                            TimeUnit unit,
                                            BlockingQueue<Runnable> workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                Executors.defaultThreadFactory(), defaultHandler);
    }

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters and default rejected execution handler.
     *
     * @param corePoolSize    the number of threads to keep in the pool, even
     *                        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *                        pool
     * @param keepAliveTime   when the number of threads is greater than
     *                        the core, this is the maximum time that excess idle threads
     *                        will wait for new tasks before terminating.
     * @param unit            the time unit for the {@code keepAliveTime} argument
     * @param workQueue       the queue to use for holding tasks before they are
     *                        executed.  This queue will hold only the {@code Runnable}
     *                        tasks submitted by the {@code execute} method.
     * @param threadFactory   the factory to use when the executor
     *                        creates a new thread
     * @throws IllegalArgumentException if one of the following holds:<br>
     *                                  {@code corePoolSize < 0}<br>
     *                                  {@code keepAliveTime < 0}<br>
     *                                  {@code maximumPoolSize <= 0}<br>
     *                                  {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException     if {@code workQueue}
     *                                  or {@code threadFactory} is null
     *                                  创建一个新的{@code ThreadPoolExecutor}与给定的初始
     *                                       *参数和默认拒绝执行处理程序。
     *                                       *
     *                                       * @param corePoolSize要保存在池中的线​​程数，甚至
     *                                       *如果空闲，除非{@code allowCoreThreadTimeOut}被设置
     *                                       * @param maximumPoolSize允许的最大线程数
     *                                       *游泳池
     *                                       * @param keepAliveTime当线程数大于
     *                                       *核心，这是多余的空闲线程的最大时间
     *                                       *等待新任务终止前。
     *                                       * @param单位{@code keepAliveTime}参数的时间单位
     *                                       * @param workQueue用于保存任务之前的队列
     *                                       *执行。这个队列只会保存{@code Runnable}
     *                                       *由{@code执行}方法提交的任务。
     *                                       * @param threadFactory工厂在执行时使用
     *                                       *创建一个新线程
     *                                       * @throws IllegalArgumentException如果有下列情况之一：<br>
     *                                       * {@code corePoolSize <0}
     *                                       * {@code keepAliveTime <0}
     *                                       * {@code maximumPoolSize <= 0}
     *                                       * {@code maximumPoolSize <corePoolSize}
     *                                       * @throws NullPointerException if {@code workQueue}
     *                                       *或{@code threadFactory}为空
     */
    public JZControllableThreadPoolExecutor(int corePoolSize,
                                            int maximumPoolSize,
                                            long keepAliveTime,
                                            TimeUnit unit,
                                            BlockingQueue<Runnable> workQueue,
                                            ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                threadFactory, defaultHandler);
    }

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters and default thread factory.
     *
     * @param corePoolSize    the number of threads to keep in the pool, even
     *                        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *                        pool
     * @param keepAliveTime   when the number of threads is greater than
     *                        the core, this is the maximum time that excess idle threads
     *                        will wait for new tasks before terminating.
     * @param unit            the time unit for the {@code keepAliveTime} argument
     * @param workQueue       the queue to use for holding tasks before they are
     *                        executed.  This queue will hold only the {@code Runnable}
     *                        tasks submitted by the {@code execute} method.
     * @param handler         the handler to use when execution is blocked
     *                        because the thread bounds and queue capacities are reached
     * @throws IllegalArgumentException if one of the following holds:<br>
     *                                  {@code corePoolSize < 0}<br>
     *                                  {@code keepAliveTime < 0}<br>
     *                                  {@code maximumPoolSize <= 0}<br>
     *                                  {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException     if {@code workQueue}
     *                                  or {@code handler} is null
     *                                  创建一个新的{@code ThreadPoolExecutor}与给定的初始
     *                                       *参数和默认线程工厂。
     *                                       *
     *                                       * @param corePoolSize要保存在池中的线​​程数，甚至
     *                                       *如果空闲，除非{@code allowCoreThreadTimeOut}被设置
     *                                       * @param maximumPoolSize允许的最大线程数
     *                                       *游泳池
     *                                       * @param keepAliveTime当线程数大于
     *                                       *核心，这是多余的空闲线程的最大时间
     *                                       *等待新任务终止前。
     *                                       * @param单位{@code keepAliveTime}参数的时间单位
     *                                       * @param workQueue用于保存任务之前的队列
     *                                       *执行。这个队列只会保存{@code Runnable}
     *                                       *由{@code执行}方法提交的任务。
     *                                       * @param处理程序在执行被阻止时使用的处理程序
     *                                       *因为达到线程限制和队列容量
     *                                       * @throws IllegalArgumentException如果有下列情况之一：<br>
     *                                       * {@code corePoolSize <0}
     *                                       * {@code keepAliveTime <0}
     *                                       * {@code maximumPoolSize <= 0}
     *                                       * {@code maximumPoolSize <corePoolSize}
     *                                       * @throws NullPointerException if {@code workQueue}
     *                                       *或{@code handler}为空
     */
    public JZControllableThreadPoolExecutor(int corePoolSize,
                                            int maximumPoolSize,
                                            long keepAliveTime,
                                            TimeUnit unit,
                                            BlockingQueue<Runnable> workQueue,
                                            JZRejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                Executors.defaultThreadFactory(), handler);
    }

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters.
     *
     * @param corePoolSize    the number of threads to keep in the pool, even
     *                        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *                        pool
     * @param keepAliveTime   when the number of threads is greater than
     *                        the core, this is the maximum time that excess idle threads
     *                        will wait for new tasks before terminating.
     * @param unit            the time unit for the {@code keepAliveTime} argument
     * @param workQueue       the queue to use for holding tasks before they are
     *                        executed.  This queue will hold only the {@code Runnable}
     *                        tasks submitted by the {@code execute} method.
     * @param threadFactory   the factory to use when the executor
     *                        creates a new thread
     * @param handler         the handler to use when execution is blocked
     *                        because the thread bounds and queue capacities are reached
     * @throws IllegalArgumentException if one of the following holds:<br>
     *                                  {@code corePoolSize < 0}<br>
     *                                  {@code keepAliveTime < 0}<br>
     *                                  {@code maximumPoolSize <= 0}<br>
     *                                  {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException     if {@code workQueue}
     *                                  or {@code threadFactory} or {@code handler} is null
     *                                  创建一个新的{@code ThreadPoolExecutor}与给定的初始
     *                                       *参数。
     *                                       *
     *                                       * @param corePoolSize要保存在池中的线​​程数，甚至
     *                                       *如果空闲，除非{@code allowCoreThreadTimeOut}被设置
     *                                       * @param maximumPoolSize允许的最大线程数
     *                                       *游泳池
     *                                       * @param keepAliveTime当线程数大于
     *                                       *核心，这是多余的空闲线程的最大时间
     *                                       *等待新任务终止前。
     *                                       * @param单位{@code keepAliveTime}参数的时间单位
     *                                       * @param workQueue用于保存任务之前的队列
     *                                       *执行。这个队列只会保存{@code Runnable}
     *                                       *由{@code执行}方法提交的任务。
     *                                       * @param threadFactory工厂在执行时使用
     *                                       *创建一个新线程
     *                                       * @param处理程序在执行被阻止时使用的处理程序
     *                                       *因为达到线程限制和队列容量
     *                                       * @throws IllegalArgumentException如果有下列情况之一：<br>
     *                                       * {@code corePoolSize <0}
     *                                       * {@code keepAliveTime <0}
     *                                       * {@code maximumPoolSize <= 0}
     *                                       * {@code maximumPoolSize <corePoolSize}
     *                                       * @throws NullPointerException if {@code workQueue}
     *                                       *或{@code threadFactory}或{@code handler}为空
     */
    public JZControllableThreadPoolExecutor(int corePoolSize,
                                            int maximumPoolSize,
                                            long keepAliveTime,
                                            TimeUnit unit,
                                            BlockingQueue<Runnable> workQueue,
                                            ThreadFactory threadFactory,
                                            JZRejectedExecutionHandler handler) {
        if (corePoolSize < 0 ||
                maximumPoolSize <= 0 ||
                maximumPoolSize < corePoolSize ||
                keepAliveTime < 0)
            throw new IllegalArgumentException();
        if (workQueue == null || threadFactory == null || handler == null)
            throw new NullPointerException();
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.handler = handler;
    }

    /**
     * Executes the given task sometime in the future.  The task
     * may execute in a new thread or in an existing pooled thread.
     * <p>
     * If the task cannot be submitted for execution, either because this
     * executor has been shutdown or because its capacity has been reached,
     * the task is handled by the current {@code JZRejectedExecutionHandler}.
     *
     * @param command the task to execute
     * @throws RejectedExecutionException at discretion of
     *                                    {@code JZRejectedExecutionHandler}, if the task
     *                                    cannot be accepted for execution
     *                                    在将来某个时候执行给定的任务。 任务
     *                                          *可以在新线程或现有的合并线程中执行。
     *                                         *
     *                                          *如果任务无法提交执行，无论是因为这一点
     *                                          *执行人已经关闭或由于其能力已经达到，
     *                                          *任务由当前的{@code JZRejectedExecutionHandler}处理。
     *                                         *
     *                                          * @param命令要执行的任务
     *                                          * @throws RejectedExecutionException由...决定
     *                                          * {@code JZRejectedExecutionHandler}，如果任务
     *                                          *不能接受执行
     * @throws NullPointerException       if {@code command} is null
     */
    public void execute(Runnable command) {
        if (command == null)
            throw new NullPointerException();
        /* 添加开始 */
        if (isPause) {
            if (!workQueue.offer(command)) reject(command);
            return;
        }
        /* 添加结束 */
        /*
         * Proceed in 3 steps:
         *
         * 1. If fewer than corePoolSize threads are running, try to
         * start a new thread with the given command as its first
         * task.  The call to addWorker atomically checks runState and
         * workerCount, and so prevents false alarms that would add
         * threads when it shouldn't, by returning false.
         *
         * 2. If a task can be successfully queued, then we still need
         * to double-check whether we should have added a thread
         * (because existing ones died since last checking) or that
         * the pool shut down since entry into this method. So we
         * recheck state and if necessary roll back the enqueuing if
         * stopped, or start a new thread if there are none.
         *
         * 3. If we cannot queue task, then we try to add a new
         * thread.  If it fails, we know we are shut down or saturated
         * and so reject the task.
         *
         * 继续进行3个步骤：
         *
          * 1.如果少于corePoolSize线程正在运行，请尝试
          *以指定的命令开头一个新的线程
          *任务。 调用addWorker原子地检查runState和
          * workerCount，因此可以防止添加的虚假警报
          *线程当它不应该，返回false。
         *
          * 2.如果任务可以成功排队，那么我们仍然需要
          *仔细检查是否应该添加一个线程
          *（因为现在的人自上次检查以来死亡）或那个
          *进入该方法后，池关闭。 所以我们
          *重新检查状态，如有必要，请回滚排队
          *停止，或启动一个新的线程，如果没有。
         *
          * 3.如果我们无法排队任务，那么我们尝试添加一个新的
          *线程。 如果失败，我们知道我们关闭或饱和
          *所以拒绝任务。
         */
        int c = ctl.get();
        if (workerCountOf(c) < corePoolSize) {
            if (addWorker(command, true))
                return;
            c = ctl.get();
        }
        if (isRunning(c) && workQueue.offer(command)) {
            int recheck = ctl.get();
            if (!isRunning(recheck) && remove(command))
                reject(command);
            else if (workerCountOf(recheck) == 0)
                addWorker(null, false);
        } else if (!addWorker(command, false))
            reject(command);
    }

    /**
     * Initiates an orderly shutdown in which previously submitted
     * tasks are executed, but no new tasks will be accepted.
     * Invocation has no additional effect if already shut down.
     *
     * <p>This method does not wait for previously submitted tasks to
     * complete execution.  Use {@link #awaitTermination awaitTermination}
     * to do that.
     * 启动先前提交的有序关闭
     *       *任务执行，但不会接受任何新任务。
     *       *如果已经关闭，调用不会产生额外的影响。
     *      *
     *       * <p>此方法不等待以前提交的任务
     *       *完成执行。 使用{@link #awaitTermination awaitTermination}
     *       * 要做到这一点。
     */
    // android-note: Removed @throws SecurityException
    public void shutdown() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            checkShutdownAccess();
            advanceRunState(SHUTDOWN);
            interruptIdleWorkers();
            onShutdown(); // hook for ScheduledThreadPoolExecutor
        } finally {
            mainLock.unlock();
        }
        tryTerminate();
    }

    /**
     * Attempts to stop all actively executing tasks, halts the
     * processing of waiting tasks, and returns a list of the tasks
     * that were awaiting execution. These tasks are drained (removed)
     * from the task queue upon return from this method.
     *
     * <p>This method does not wait for actively executing tasks to
     * terminate.  Use {@link #awaitTermination awaitTermination} to
     * do that.
     *
     * <p>There are no guarantees beyond best-effort attempts to stop
     * processing actively executing tasks.  This implementation
     * interrupts tasks via {@link Thread#interrupt}; any task that
     * fails to respond to interrupts may never terminate.
     * 尝试停止所有正在执行的任务，停止
     *       *处理等待任务，并返回任务列表
     *       *正在等待执行。 这些任务已被排除（已删除）
     *       *从这个方法返回任务队列。
     *      *
     *       * <p>此方法不等待主动执行任务
     *       *终止。 使用{@link #awaitTermination awaitTermination}到
     *       * 去做。
     *      *
     *       * <p>除了努力尝试停止之外，没有任何保证
     *       *处理积极执行的任务。 这个实现
     *       *通过{@link Thread＃interrupt}中断任务; 任何任务
     *       *无法响应中断可能永远不会终止。
     */
    // android-note: Removed @throws SecurityException
    public List<Runnable> shutdownNow() {
        List<Runnable> tasks;
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            checkShutdownAccess();
            advanceRunState(STOP);
            interruptWorkers();
            tasks = drainQueue();
        } finally {
            mainLock.unlock();
        }
        tryTerminate();
        return tasks;
    }

    public boolean isShutdown() {
        return !isRunning(ctl.get());
    }

    /**
     * Returns true if this executor is in the process of terminating
     * after {@link #shutdown} or {@link #shutdownNow} but has not
     * completely terminated.  This method may be useful for
     * debugging. A return of {@code true} reported a sufficient
     * period after shutdown may indicate that submitted tasks have
     * ignored or suppressed interruption, causing this executor not
     * to properly terminate.
     *
     * @return {@code true} if terminating but not yet terminated
     * 如果执行器正在终止，则返回true
     *       *在{@link #shutdown}或{@link #shutdownNow}之后，但没有
     *       *完全终止 此方法可能有用
     *       *调试 {@code true}的回报报告足够
     *       *关机后的时间可能表示提交的任务有
     *       *忽略或抑制中断，导致这个执行者没有
     *       *正确终止。
     *      *
     *       * @return {@code true}如果终止但尚未终止
     */
    public boolean isTerminating() {
        int c = ctl.get();
        return !isRunning(c) && runStateLessThan(c, TERMINATED);
    }

    public boolean isTerminated() {
        return runStateAtLeast(ctl.get(), TERMINATED);
    }

    public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            while (!runStateAtLeast(ctl.get(), TERMINATED)) {
                if (nanos <= 0L)
                    return false;
                nanos = termination.awaitNanos(nanos);
            }
            return true;
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Invokes {@code shutdown} when this executor is no longer
     * referenced and it has no threads.
     * 当这个执行者不再需要时，调用{@code shutdown}
     *       *引用，它没有线程。
     */
    protected void finalize() {
        shutdown();
    }

    /**
     * Sets the thread factory used to create new threads.
     * 设置用于创建新线程的线程工厂。
     *      *
     *       * @param threadFactory新线程工厂
     *       * @throws NullPointerException如果threadFactory为空
     *       * @see #getThreadFactory
     *
     * @param threadFactory the new thread factory
     * @throws NullPointerException if threadFactory is null
     * @see #getThreadFactory
     */
    public void setThreadFactory(ThreadFactory threadFactory) {
        if (threadFactory == null)
            throw new NullPointerException();
        this.threadFactory = threadFactory;
    }

    /**
     * Returns the thread factory used to create new threads.
     *
     * @return the current thread factory
     * @see #setThreadFactory(ThreadFactory)
     * 返回用于创建新线程的线程工厂。
     *      *
     *       *返回当前线程工厂
     *       * @see #setThreadFactory（ThreadFactory）
     */
    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    /**
     * Sets a new handler for unexecutable tasks.
     *
     * @param handler the new handler
     * @throws NullPointerException if handler is null
     * @see #getRejectedExecutionHandler
     * 为不可执行的任务设置一个新的处理程序。
     *      *
     *       * @param处理程序新的处理程序
     *       * @throws NullPointerException如果handler为null
     *       * @see #getRejectedExecutionHandler
     */
    public void setRejectedExecutionHandler(JZRejectedExecutionHandler handler) {
        if (handler == null)
            throw new NullPointerException();
        this.handler = handler;
    }

    /**
     * Returns the current handler for unexecutable tasks.
     *
     * @return the current handler
     * 返回不可执行任务的当前处理程序。
     *      *
     *       * @返回当前处理程序
     * @see #setRejectedExecutionHandler(JZRejectedExecutionHandler)
     */
    public JZRejectedExecutionHandler getRejectedExecutionHandler() {
        return handler;
    }

    /**
     * Sets the core number of threads.  This overrides any value set
     * in the constructor.  If the new value is smaller than the
     * current value, excess existing threads will be terminated when
     * they next become idle.  If larger, new threads will, if needed,
     * be started to execute any queued tasks.
     *
     * @param corePoolSize the new core size
     * @throws IllegalArgumentException if {@code corePoolSize < 0}
     * @see #getCorePoolSize
     * 设置核心线程数。 这将覆盖任何值集
     *       *在构造函数中。 如果新值小于
     *       *当前值，多余的线程将被终止
     *       他们接下来变得闲置 如果更大的话，如果需要，
     *       *开始执行任何排队的任务。
     *      *
     *       * @param corePoolSize新的核心大小
     *       * @throws IllegalArgumentException if {@code corePoolSize <0}
     *       * @see #getCorePoolSize
     */
    // Android-changed: Reverted code that threw an IAE when
    // {@code corePoolSize} is greater than the {@linkplain #getMaximumPoolSize()
    // maximum pool size}. This is due to defective code in a commonly used third
    // party library that does something like :
    //Android更改：转换代码时抛出IAE的时间
////   // {@code corePoolSize}大于{@linkplain #getMaximumPoolSize（）
// //最大池大小}。 这是由于常用的第三个缺陷代码
// //派对库可以做到：
    //
    // exec.setCorePoolSize(N);
    // exec.setMaxPoolSize(N);
    public void setCorePoolSize(int corePoolSize) {
        if (corePoolSize < 0)
            throw new IllegalArgumentException();
        int delta = corePoolSize - this.corePoolSize;
        this.corePoolSize = corePoolSize;
        if (workerCountOf(ctl.get()) > corePoolSize)
            interruptIdleWorkers();
        else if (delta > 0) {
            // We don't really know how many new threads are "needed".
            // As a heuristic, prestart enough new workers (up to new
            // core size) to handle the current number of tasks in
            // queue, but stop if queue becomes empty while doing so.
            // 我们不知道有多少新线程是“需要的”。
            //作为一种启发式，预备好足够的新员工（高达新的
            //核心大小）来处理当前的任务数量
            //队列，但如果队列变空则停止。
            int k = Math.min(delta, workQueue.size());
            while (k-- > 0 && addWorker(null, true)) {
                if (workQueue.isEmpty())
                    break;
            }
        }
    }

    /**
     * Returns the core number of threads.
     *
     * @return the core number of threads
     * @see #setCorePoolSize
     * 返回核心线程数。
     *      *
     *       *返回核心线程数
     *       * @see #setCorePoolSize
     */
    public int getCorePoolSize() {
        return corePoolSize;
    }

    /**
     * Starts a core thread, causing it to idly wait for work. This
     * overrides the default policy of starting core threads only when
     * new tasks are executed. This method will return {@code false}
     * if all core threads have already been started.
     *
     * @return {@code true} if a thread was started
     * 启动核心线程，使其无法等待工作。 这个
     *       *仅覆盖启动核心线程的默认策略
     *       *执行新任务。 此方法将返回{@code false}
     *       *如果所有核心线程已经启动。
     *      *
     *       * @return {@code true}如果线程已启动
     */
    public boolean prestartCoreThread() {
        return workerCountOf(ctl.get()) < corePoolSize &&
                addWorker(null, true);
    }

    /**
     * Same as prestartCoreThread except arranges that at least one
     * thread is started even if corePoolSize is 0.与prestartCoreThread相同，除了安排至少一个
     *       *即使corePoolSize为0也会启动线程。
     */
    void ensurePrestart() {
        int wc = workerCountOf(ctl.get());
        if (wc < corePoolSize)
            addWorker(null, true);
        else if (wc == 0)
            addWorker(null, false);
    }

    /**
     * Starts all core threads, causing them to idly wait for work. This
     * overrides the default policy of starting core threads only when
     * new tasks are executed.
     *
     * @return the number of threads started
     * 启动所有核心线程，导致他们等待工作。 这个
     *       *仅覆盖启动核心线程的默认策略
     *       *执行新任务。
     *      *
     *       * @返回开始的线程数
     */
    public int prestartAllCoreThreads() {
        int n = 0;
        while (addWorker(null, true))
            ++n;
        return n;
    }

    /**
     * Returns true if this pool allows core threads to time out and
     * terminate if no tasks arrive within the keepAlive time, being
     * replaced if needed when new tasks arrive. When true, the same
     * keep-alive policy applying to non-core threads applies also to
     * core threads. When false (the default), core threads are never
     * terminated due to lack of incoming tasks.
     *
     * @return {@code true} if core threads are allowed to time out,
     * else {@code false}
     * 如果此池允许核心线程超时，则返回true
     *       *如果没有任务在keepAlive时间内到达，则终止
     *       *当新任务到达时需要更换。 当真的一样
     *       *适用于非核心线程的保持活动策略也适用于
     *       *核心线程。 当假（默认），核心线程永远不会
     *       *由于缺乏传入任务而终止。
     *      *
     *       * @return {@code true}如果核心线程被允许超时，
     *       * else {@code false}
     * @since 1.6
     */
    public boolean allowsCoreThreadTimeOut() {
        return allowCoreThreadTimeOut;
    }

    /**
     * Sets the policy governing whether core threads may time out and
     * terminate if no tasks arrive within the keep-alive time, being
     * replaced if needed when new tasks arrive. When false, core
     * threads are never terminated due to lack of incoming
     * tasks. When true, the same keep-alive policy applying to
     * non-core threads applies also to core threads. To avoid
     * continual thread replacement, the keep-alive time must be
     * greater than zero when setting {@code true}. This method
     * should in general be called before the pool is actively used.
     *
     * @param value {@code true} if should time out, else {@code false}
     * @throws IllegalArgumentException if value is {@code true}
     *                                  and the current keep-alive time is not greater than zero
     *                                  设定管理核心线程是否可能超时的策略
     *                                        *如果没有任务在活着的时间内到达，则终止
     *                                        *当新任务到达时需要更换。 当假，核心
     *                                        *线程由于缺少进入而永远不会终止
     *                                        * 任务。 如果真的，同样的保留政策适用于
     *                                        *非核心线程也适用于核心线程。 避免
     *                                        *连续线程更换，保持活动时间必须
     *                                        *设置{@code true}时大于零。 这个方法
     *                                        一般应该在池被积极使用之前调用。
     *                                       *
     *                                        * @param value {@code true}如果超时，否则{@code false}
     *                                        * @throws IllegalArgumentException if value is {@code true}
     *                                        *并且当前保持活动时间不大于零
     * @since 1.6
     */
    public void allowCoreThreadTimeOut(boolean value) {
        if (value && keepAliveTime <= 0)
            throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
        if (value != allowCoreThreadTimeOut) {
            allowCoreThreadTimeOut = value;
            if (value)
                interruptIdleWorkers();
        }
    }

    /**
     * Sets the maximum allowed number of threads. This overrides any
     * value set in the constructor. If the new value is smaller than
     * the current value, excess existing threads will be
     * terminated when they next become idle.
     *
     * @param maximumPoolSize the new maximum
     * @throws IllegalArgumentException if the new maximum is
     *                                  less than or equal to zero, or
     *                                  less than the {@linkplain #getCorePoolSize core pool size}
     * @see #getMaximumPoolSize
     * 设置允许的最大线程数。 这覆盖任何
     *       *在构造函数中设置值。 如果新值小于
     *       *当前值，多余的线程将会
     *       *当他们下一个空闲时终止。
     *      *
     *       * @param maximumPoolSize为最大值
     *       * @throws如果新的最大值为IllegalArgumentException
     *       *小于或等于零，或
     *       *小于 #getCorePoolSize核心池大小}
     *       * @see #getMaximumPoolSize
     */
    public void setMaximumPoolSize(int maximumPoolSize) {
        if (maximumPoolSize <= 0 || maximumPoolSize < corePoolSize)
            throw new IllegalArgumentException();
        this.maximumPoolSize = maximumPoolSize;
        if (workerCountOf(ctl.get()) > maximumPoolSize)
            interruptIdleWorkers();
    }

    /**
     * Returns the maximum allowed number of threads.
     *
     * @return the maximum allowed number of threads
     * @see #setMaximumPoolSize
     * 返回允许的最大线程数。
     *      *
     *       * @返回允许的最大线程数
     *       * @see #setMaximumPoolSize
     */
    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    /**
     * Sets the thread keep-alive time, which is the amount of time
     * that threads may remain idle before being terminated.
     * Threads that wait this amount of time without processing a
     * task will be terminated if there are more than the core
     * number of threads currently in the pool, or if this pool
     * {@linkplain #allowsCoreThreadTimeOut() allows core thread timeout}.
     * This overrides any value set in the constructor.
     *
     * @param time the time to wait.  A time value of zero will cause
     *             excess threads to terminate immediately after executing tasks.
     * @param unit the time unit of the {@code time} argument
     * @throws IllegalArgumentException if {@code time} less than zero or
     *                                  if {@code time} is zero and {@code allowsCoreThreadTimeOut}
     * @see #getKeepAliveTime(TimeUnit)
     * 设置线程保持活动时间，这是时间量
     *       *线程可能在终止之前保持空闲状态。
     *       *等待这段时间的线程，而不处理
     *       *如果超过核心，任务将被终止
     *       *当前在池中的线程数，或者是这个池
     *       * {@linkplain #allowsCoreThreadTimeOut（）允许核心线程超时}。
     *       *这覆盖了构造函数中设置的任何值。
     *      *
     *       * @param等待时间。 时间值为零将导致
     *       *多余的线程在执行任务后立即终止。
     *       * @param单位{@code time}参数的时间单位
     *       * @throws IllegalArgumentException if {@code time}小于零或
     *       *如果{@code time}为零，{@code allowCoreThreadTimeOut}
     *       * @see #getKeepAliveTime（TimeUnit）
     */
    public void setKeepAliveTime(long time, TimeUnit unit) {
        if (time < 0)
            throw new IllegalArgumentException();
        if (time == 0 && allowsCoreThreadTimeOut())
            throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
        long keepAliveTime = unit.toNanos(time);
        long delta = keepAliveTime - this.keepAliveTime;
        this.keepAliveTime = keepAliveTime;
        if (delta < 0)
            interruptIdleWorkers();
    }

    /**
     * Returns the thread keep-alive time, which is the amount of time
     * that threads may remain idle before being terminated.
     * Threads that wait this amount of time without processing a
     * task will be terminated if there are more than the core
     * number of threads currently in the pool, or if this pool
     * {@linkplain #allowsCoreThreadTimeOut() allows core thread timeout}.
     *
     * @param unit the desired time unit of the result
     * @return the time limit
     * @see #setKeepAliveTime(long, TimeUnit)
     * 返回线程保持活动时间，这是时间量
     *       *线程可能在终止之前保持空闲状态。
     *       *等待这段时间的线程，而不处理
     *       *如果超过核心，任务将被终止
     *       *当前在池中的线程数，或者是这个池
     *       * {@linkplain #allowsCoreThreadTimeOut（）允许核心线程超时}。
     *      *
     *       * @param单位结果的所需时间单位
     *       *返回时间限制
     *       * @see #setKeepAliveTime（long，TimeUnit）
     */
    public long getKeepAliveTime(TimeUnit unit) {
        return unit.convert(keepAliveTime, TimeUnit.NANOSECONDS);
    }

    /* User-level queue utilities用户级别队列实用程序 */

    /**
     * Returns the task queue used by this executor. Access to the
     * task queue is intended primarily for debugging and monitoring.
     * This queue may be in active use.  Retrieving the task queue
     * does not prevent queued tasks from executing.
     *
     * @return the task queue
     * 返回此执行程序使用的任务队列。 访问
     *       *任务队列主要用于调试和监控。
     *       *此队列可能正在使用。 检索任务队列
     *       *不会阻止排队的任务执行。
     *      *
     *       *返回任务队列
     */
    public BlockingQueue<Runnable> getQueue() {
        return workQueue;
    }

    /**
     * Removes this task from the executor's internal queue if it is
     * present, thus causing it not to be run if it has not already
     * started.
     *
     * <p>This method may be useful as one part of a cancellation
     * scheme.  It may fail to remove tasks that have been converted
     * into other forms before being placed on the internal queue.
     * For example, a task entered using {@code submit} might be
     * converted into a form that maintains {@code Future} status.
     * However, in such cases, method {@link #purge} may be used to
     * remove those Futures that have been cancelled.
     *
     * @param task the task to remove
     * @return {@code true} if the task was removed
     * 从执行程序的内部队列中删除此任务
     *       *存在，从而导致它不能运行，如果还没有
     *       *开始了。
     *      *
     *       * <p>此方法可能作为取消的一部分有用
     *       *方案。 它可能无法删除已转换的任务
     *       *放入内部队列之前的其他形式。
     *       *例如，使用{@code submit}输入的任务可能是
     *       *转换为维护{@code Future}状态的表单。
     *       *但是，在这种情况下，可以使用方法{@link #purge}
     *       *删除那些已被取消的期货。
     *      *
     *       * @param任务要删除的任务
     *       * @return {@code true}如果任务被删除
     */
    public boolean remove(Runnable task) {
        boolean removed = workQueue.remove(task);
        tryTerminate(); // In case SHUTDOWN and now empty
        return removed;
    }

    /**
     * Tries to remove from the work queue all {@link Future}
     * tasks that have been cancelled. This method can be useful as a
     * storage reclamation operation, that has no other impact on
     * functionality. Cancelled tasks are never executed, but may
     * accumulate in work queues until worker threads can actively
     * remove them. Invoking this method instead tries to remove them now.
     * However, this method may fail to remove tasks in
     * the presence of interference by other threads.
     * 尝试从工作队列中删除所有{@link Future}
     *       *已被取消的任务。 此方法可用作a
     *       *存储回收操作，没有其他影响
     *       *功能。 取消的任务从不执行，但可能
     *       *积累在工作队列中，直到工作线程可以主动
     *       *删除它们。 现在调用此方法会尝试删除它。
     *       *但是，此方法可能无法删除任务
     *       *其他线程存在干扰。
     */
    public void purge() {
        final BlockingQueue<Runnable> q = workQueue;
        try {
            Iterator<Runnable> it = q.iterator();
            while (it.hasNext()) {
                Runnable r = it.next();
                if (r instanceof Future<?> && ((Future<?>) r).isCancelled())
                    it.remove();
            }
        } catch (ConcurrentModificationException fallThrough) {
            // Take slow path if we encounter interference during traversal.
            // Make copy for traversal and call remove for cancelled entries.
            // The slow path is more likely to be O(N*N).
            //如果在遍历过程中遇到干扰，请采取缓慢的路径。
            //为复制进行遍历，并为取消的条目调用remove。
            //慢路径更有可能是O（N * N）。
            for (Object r : q.toArray())
                if (r instanceof Future<?> && ((Future<?>) r).isCancelled())
                    q.remove(r);
        }

        tryTerminate(); // In case SHUTDOWN and now empty
    }

    /* Statistics 统计*/

    /**
     * Returns the current number of threads in the pool.
     *
     * @return the number of threads
     * 返回池中当前的线程数。
     *      *
     *       * @返回线程数
     */
    public int getPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            // Remove rare and surprising possibility of
            // isTerminated() && getPoolSize() > 0
            return runStateAtLeast(ctl.get(), TIDYING) ? 0
                    : workers.size();
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Returns the approximate number of threads that are actively
     * executing tasks.
     *
     * @return the number of threads
     * 返回大量主动线程数
     *       *执行任务
     *      *
     *       * @返回线程数
     */
    public int getActiveCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            int n = 0;
            for (Worker w : workers)
                if (w.isLocked())
                    ++n;
            return n;
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Returns the largest number of threads that have ever
     * simultaneously been in the pool.
     *
     * @return the number of threads
     * 返回有史以来最多的线程数
     *       *同时进入游泳池。
     *      *
     *       * @返回线程数
     */
    public int getLargestPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            return largestPoolSize;
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Returns the approximate total number of tasks that have ever been
     * scheduled for execution. Because the states of tasks and
     * threads may change dynamically during computation, the returned
     * value is only an approximation.
     *
     * @return the number of tasks
     * 返回过去任务的大致总数
     *       *计划执行。 因为任务状态
     *       *线程可能会在计算过程中动态更改，返回
     *       *值只是近似值。
     *      *
     *       *返回任务数
     */
    public long getTaskCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = completedTaskCount;
            for (Worker w : workers) {
                n += w.completedTasks;
                if (w.isLocked())
                    ++n;
            }
            return n + workQueue.size();
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Returns the approximate total number of tasks that have
     * completed execution. Because the states of tasks and threads
     * may change dynamically during computation, the returned value
     * is only an approximation, but one that does not ever decrease
     * across successive calls.
     *
     * @return the number of tasks
     * 返回大概总共拥有的任务数
     *       *完成执行。 因为任务和线程的状态
     *       *可能会在计算过程中动态更改返回的值
     *       *只是一个近似值，但不能减少
     *       *连续通话。
     *      *
     *       *返回任务数
     */
    public long getCompletedTaskCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = completedTaskCount;
            for (Worker w : workers)
                n += w.completedTasks;
            return n;
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Returns a string identifying this pool, as well as its state,
     * including indications of run state and estimated worker and
     * task counts.
     *
     * @return a string identifying this pool, as well as its state
     * 返回一个标识此池的字符串，以及它的状态，
     *       *包括运行状态和估计工人的迹象
     *       *任务计数。
     *      *
     *       * @返回一个标识这个池的字符串，以及它的状态
     */
    public String toString() {
        long ncompleted;
        int nworkers, nactive;
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            ncompleted = completedTaskCount;
            nactive = 0;
            nworkers = workers.size();
            for (Worker w : workers) {
                ncompleted += w.completedTasks;
                if (w.isLocked())
                    ++nactive;
            }
        } finally {
            mainLock.unlock();
        }
        int c = ctl.get();
        String runState =
                runStateLessThan(c, SHUTDOWN) ? "Running" :
                        runStateAtLeast(c, TERMINATED) ? "Terminated" :
                                "Shutting down";
        return super.toString() +
                "[" + runState +
                ", pool size = " + nworkers +
                ", active threads = " + nactive +
                ", queued tasks = " + workQueue.size() +
                ", completed tasks = " + ncompleted +
                "]";
    }

    /* Extension hooks 延长钩*/

    /**
     * Method invoked prior to executing the given Runnable in the
     * given thread.  This method is invoked by thread {@code t} that
     * will execute task {@code r}, and may be used to re-initialize
     * ThreadLocals, or to perform logging.
     *
     * <p>This implementation does nothing, but may be customized in
     * subclasses. Note: To properly nest multiple overridings, subclasses
     * should generally invoke {@code super.beforeExecute} at the end of
     * this method.
     *
     * @param t the thread that will run task {@code r}
     * @param r the task that will be executed
     *          在执行给定的Runnable之前调用的方法
     *                *给线程。 线程{@code t}调用此方法
     *                *将执行任务{@code r}，并可用于重新初始化
     *                * ThreadLocals，或执行日志记录。
     *               *
     *                * <p>此实现不执行任何操作，但可能会自定义
     *                *子类。 注意：正确嵌套多个覆盖，子类
     *                *一般应在{@code super.beforeExecute}之前调用
     *                *这种方法。
     *               *
     *                * @param t将运行任务的线程{@code r}
     *                * @param r将执行的任务
     */
    protected void beforeExecute(Thread t, Runnable r) {
    }

    /**
     * Method invoked upon completion of execution of the given Runnable.
     * This method is invoked by the thread that executed the task. If
     * non-null, the Throwable is the uncaught {@code RuntimeException}
     * or {@code Error} that caused execution to terminate abruptly.
     *
     * <p>This implementation does nothing, but may be customized in
     * subclasses. Note: To properly nest multiple overridings, subclasses
     * should generally invoke {@code super.afterExecute} at the
     * beginning of this method.
     *
     * <p><b>Note:</b> When actions are enclosed in tasks (such as
     * {@link FutureTask}) either explicitly or via methods such as
     * {@code submit}, these task objects catch and maintain
     * computational exceptions, and so they do not cause abrupt
     * termination, and the internal exceptions are <em>not</em>
     * passed to this method. If you would like to trap both kinds of
     * failures in this method, you can further probe for such cases,
     * as in this sample subclass that prints either the direct cause
     * or the underlying exception if a task has been aborted:
     * 完成指定Runnable的执行后调用方法。
     *      *该方法由执行任务的线程调用。如果
     *      *非空，Throwable是未知的{@code RuntimeException}
     *      *或{@code错误}导致执行突然终止。
     *      *
     *      * <p>此实现不执行任何操作，但可能会自定义
     *      *子类。注意：正确嵌套多个覆盖，子类
     *      *通常应该调用{@code super.afterExecute}
     *      *开始这个方法。
     *      *
     *      * <p> <b>注意：</ b>当操作包含在任务中（如
     *      * {@link FutureTask}）显式地或通过诸如此类的方法
     *      * {@code submit}，这些任务对象捕获和维护
     *      *计算异常，所以不会引起突发
     *      *终止，内部异常是<em>不</ em>
     *      *传递给这个方法。如果你想陷阱两种
     *      *这种方法的失败，可以进一步探索这种情况，
     *      *在这个示例子类中打印直接原因
     *      *或者一个任务已中止的基础异常：
     *
     * <pre> {@code
     * class ExtendedExecutor extends ThreadPoolExecutor {
     *   // ...
     *   protected void afterExecute(Runnable r, Throwable t) {
     *     super.afterExecute(r, t);
     *     if (t == null
     *         && r instanceof Future<?>
     *         && ((Future<?>)r).isDone()) {
     *       try {
     *         Object result = ((Future<?>) r).get();
     *       } catch (CancellationException ce) {
     *         t = ce;
     *       } catch (ExecutionException ee) {
     *         t = ee.getCause();
     *       } catch (InterruptedException ie) {
     *         // ignore/reset
     *         Thread.currentThread().interrupt();
     *       }
     *     }
     *     if (t != null)
     *       System.out.println(t);
     *   }
     * }}</pre>
     *
     * @param r the runnable that has completed
     * @param t the exception that caused termination, or null if
     *          execution completed normally
     */
    protected void afterExecute(Runnable r, Throwable t) {
    }

    /**
     * Method invoked when the Executor has terminated.  Default
     * implementation does nothing. Note: To properly nest multiple
     * overridings, subclasses should generally invoke
     * {@code super.terminated} within this method.
     * 执行程序已终止时调用方法。 默认
     *       *执行什么也没有。 注意：正确嵌套多个
     *       *覆盖，子类通常应该调用
     *       * {@code super.terminated}这个方法。
     */
    protected void terminated() {
    }

    /* Predefined RejectedExecutionHandlers 预定义拒绝的执行手段*/

    /**
     * A handler for rejected tasks that runs the rejected task
     * directly in the calling thread of the {@code execute} method,
     * unless the executor has been shut down, in which case the task
     * is discarded.
     * 运行被拒绝任务的被拒绝任务的处理程序
     *       *直接在{@code execute}方法的调用线程中，
     *       *除非执行人已经关闭，在这种情况下，任务
     *       *被丢弃。
     */
    public static class CallerRunsPolicy implements JZRejectedExecutionHandler {
        /**
         * Creates a {@code CallerRunsPolicy}.
         */
        public CallerRunsPolicy() {
        }

        /**
         * Executes task r in the caller's thread, unless the executor
         * has been shut down, in which case the task is discarded.
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         *          在调用者的线程中执行任务r，除非执行程序
         *                    *已被关闭，在这种情况下，任务被丢弃。
         *                   *
         *                    * @param r请求执行的可运行任务
         *                    * @param e执行者尝试执行此任务
         */
        public void rejectedExecution(Runnable r, JZControllableThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                r.run();
            }
        }
    }

    /**
     * A handler for rejected tasks that throws a
     * {@code RejectedExecutionException}.
     * 被拒绝的任务抛出一个处理程序
     *       * {@code RejectedExecutionException}。
     */
    public static class AbortPolicy implements JZRejectedExecutionHandler {
        /**
         * Creates an {@code AbortPolicy}.
         */
        public AbortPolicy() {
        }

        /**
         * Always throws RejectedExecutionException.
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         * @throws RejectedExecutionException always
         *                                    总是抛出RejectedExecutionException。
         *                                             *
         *                                              * @param r请求执行的可运行任务
         *                                              * @param e执行者尝试执行此任务
         *                                              * @throws总是拒绝执行ExecutionException
         */
        public void rejectedExecution(Runnable r, JZControllableThreadPoolExecutor e) {
            throw new RejectedExecutionException("Task " + r.toString() +
                    " rejected from " +
                    e.toString());
        }
    }

    /**
     * A handler for rejected tasks that silently discards the
     * rejected task.
     * 被拒绝的任务的处理程序静默地丢弃
     *       *拒绝任务
     */
    public static class DiscardPolicy implements JZRejectedExecutionHandler {
        /**
         * Creates a {@code DiscardPolicy}.
         */
        public DiscardPolicy() {
        }

        /**
         * Does nothing, which has the effect of discarding task r.
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         *          什么也没有，具有丢弃任务r的效果。
         *                   *
         *                    * @param r请求执行的可运行任务
         *                    * @param e执行者尝试执行此任务
         */
        public void rejectedExecution(Runnable r, JZControllableThreadPoolExecutor e) {
        }
    }

    /**
     * A handler for rejected tasks that discards the oldest unhandled
     * request and then retries {@code execute}, unless the executor
     * is shut down, in which case the task is discarded.
     * 被拒绝的任务的处理程序，丢弃最旧的未处理的任务
     *       *请求，然后重试{@code execute}，除非执行者
     *       *被关闭，在这种情况下，任务被丢弃。
     */
    public static class DiscardOldestPolicy implements JZRejectedExecutionHandler {
        /**
         * Creates a {@code DiscardOldestPolicy} for the given executor.
         */
        public DiscardOldestPolicy() {
        }

        /**
         * Obtains and ignores the next task that the executor
         * would otherwise execute, if one is immediately available,
         * and then retries execution of task r, unless the executor
         * is shut down, in which case task r is instead discarded.
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         *          获取和忽略执行者的下一个任务
         *                    *否则将执行，如果立即可用，
         *                    *然后重试执行任务r，除非执行者
         *                    *被关闭，在这种情况下，任务r被丢弃。
         *                   *
         *                    * @param r请求执行的可运行任务
         *                    * @param e执行者尝试执行此任务
         */
        public void rejectedExecution(Runnable r, JZControllableThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                e.getQueue().poll();
                e.execute(r);
            }
        }
    }
}
