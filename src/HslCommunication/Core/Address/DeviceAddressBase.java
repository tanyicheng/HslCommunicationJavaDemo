package HslCommunication.Core.Address;

/**
 * 所有设备通信类的地址基础类
 * Address basic class of all device communication classes
 */
public class DeviceAddressBase {


    /**
     * 获取地址信息
     * Get the starting address
     * @return 地址信息
     */
    public int getAddress() {
        return Address;
    }

    /**
     * 设置地址信息
     * Set the starting address
     * @param address 地址信息
     */
    public void setAddress(int address) {
        Address = address;
    }

    /**
     * 获取或设置起始地址
     * Get or set the starting address
     */
    private int Address = 0;


    /**
     * 解析字符串的地址
     * Parse the address of the string
     * @param address 地址信息
     */
    public void Parse(String address) {
        Address = Integer.parseInt(address);
    }


    /**
     * 返回表示当前对象的字符串
     * @return 字符串
     */
    @Override
    public String toString() {
        return String.valueOf(Address);
    }
}
