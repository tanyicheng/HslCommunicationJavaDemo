package HslCommunication.BasicFramework;

import HslCommunication.Core.Thread.SimpleHybirdLock;

/**
 * 一个简单的不持久化的序号自增类，采用线程安全实现，并允许指定最大数字，到达后清空从指定数开始
 * A simple non-persistent serial number auto-increment class, which is implemented with thread safety,
 * and allows the maximum number to be specified, which will contain the maximum number, and will be cleared from the specified number upon arrival.
 */
public class SoftIncrementCount {

    /**
     * 实例化一个自增信息的对象，包括最大值
     * Instantiate an object with incremental information, including the maximum value and initial value, IncreaseTick
     *
     * @param max   数据的最大值，必须指定
     */
    public SoftIncrementCount(long max) {
        this.max = max;
        current = start;
        hybirdLock = new SimpleHybirdLock();
    }
    /**
     * 实例化一个自增信息的对象，包括最大值
     * Instantiate an object with incremental information, including the maximum value and initial value, IncreaseTick
     *
     * @param max   数据的最大值，必须指定
     * @param start 数据的起始值，默认为0
     */
    public SoftIncrementCount(long max, long start)  {
        this.start = start;
        this.max = max;
        current = start;
        hybirdLock = new SimpleHybirdLock();
    }
    /**
     * 实例化一个自增信息的对象，包括最大值
     * Instantiate an object with incremental information, including the maximum value and initial value, IncreaseTick
     *
     * @param max   数据的最大值，必须指定
     * @param start 数据的起始值，默认为0
     * @param tick 每次的增量数据
     */
    public SoftIncrementCount(long max, long start, int tick) {
        this.start = start;
        this.max = max;
        this.IncreaseTick = tick;
        current = start;
        hybirdLock = new SimpleHybirdLock();
    }


    private long start = 0;
    private long current = 0;
    private long max = Long.MAX_VALUE;
    private SimpleHybirdLock hybirdLock;
    private int IncreaseTick = 1;


    /**
     * 获取增加的单元，如果为0，就是不增加。如果小于0，那就是减少，会变成负数的可能。
     * Get the increased unit, if it is 0, it will not increase. If it is less than 0, it is reduced, and it may become negative.
     *
     * @return int的数据
     */
    public int getIncreaseTick() {
        return IncreaseTick;
    }

    /**
     * 设置增加的单元，如果设置为0，就是不增加。如果为小于0，那就是减少，会变成负数的可能。
     * Increased units, if set to 0, do not increase. If it is less than 0, it is a decrease and it may become a negative number.
     *
     * @param increaseTick 数据值信息
     */
    public void setIncreaseTick(int increaseTick) {
        IncreaseTick = increaseTick;
    }

    /**
     * 获取当前的计数器的最大的设置值。
     * Get the maximum setting value of the current counter.
     *
     * @return 最大值
     */
    public long getMaxValue() {
        return this.max;
    }

    /**
     * 获取自增信息，获得数据之后，下一次获取将会自增，如果自增后大于最大值，则会重置为最小值，如果小于最小值，则会重置为最大值。
     * Get the auto-increment information. After getting the data, the next acquisition will auto-increase.
     * If the auto-increment is greater than the maximum value, it will reset to the minimum value.
     * If the auto-increment is smaller than the minimum value, it will reset to the maximum value.
     * @return 值
     */
    public long GetCurrentValue() {
        long value = 0;
        hybirdLock.Enter();

        value = current;
        current += IncreaseTick;
        if (current > max) {
            current = start;
        } else if (current < start) {
            current = max;
        }

        hybirdLock.Leave();
        return value;
    }

    /**
     * 重置当前序号的最大值，最大值应该大于初始值，如果当前值大于最大值，则当前值被重置为最大值
     * Reset the maximum value of the current serial number. The maximum value should be greater than the initial value.
     * If the current value is greater than the maximum value, the current value is reset to the maximum value.
     * @param max 最大值
     */
    public void ResetMaxValue( long max ) {
        hybirdLock.Enter();

        if (max > start) {
            if (max < current)
                current = start;
            this.max = max;
        }

        hybirdLock.Leave();
    }

    /**
     * 重置当前序号的初始值，需要小于最大值，如果当前值小于初始值，则当前值被重置为初始值。
     * To reset the initial value of the current serial number, it must be less than the maximum value.
     * If the current value is less than the initial value, the current value is reset to the initial value.
     * @param start 初始值
     */
    public void ResetStartValue( long start ) {
        hybirdLock.Enter();

        if (start < this.max) {
            if (current < start)
                current = start;
            this.start = start;
        }

        hybirdLock.Leave();
    }

    /**
     * 将当前的值重置为初始值。
     * Reset the current value to the initial value.
     */
    public void ResetCurrentValue( ) {
        hybirdLock.Enter();

        this.current = this.start;

        hybirdLock.Leave();
    }

    /**
     * 将当前的值重置为指定值，该值不能大于max，如果大于max值，就会自动设置为max
     * Reset the current value to the specified value. The value cannot be greater than max. If it is greater than max, it will be automatically set to max.
     * @param value 指定的数据值
     */
    public void ResetCurrentValue( long value )
    {
        hybirdLock.Enter( );

        if (value > max)
        {
            this.current = max;
        }
        else if (value < start)
        {
            this.current = start;
        }
        else
        {
            this.current = value;
        }

        hybirdLock.Leave( );
    }

    @Override
    public String toString() {
        return "SoftIncrementCount["+ current + ']';
    }
}
