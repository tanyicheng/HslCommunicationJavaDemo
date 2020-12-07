package HslCommunication.Core.Address;

/**
 * 设备地址数据的信息，通常包含起始地址，数据类型，长度
 * Device address data information, usually including the starting address, data type, length
 */
public class DeviceAddressDataBase {

    /**
     * 获取数字的起始地址，也就是偏移地址
     * The starting address of the number, which is the offset address
     * @return 值
     */
    public int getAddressStart() {
        return AddressStart;
    }

    /**
     * 设置数字的起始地址，也就是偏移地址
     * he starting address of the number, which is the offset address
     * @param addressStart 值
     */
    public void setAddressStart(int addressStart) {
        AddressStart = addressStart;
    }

    private int AddressStart = 0;

    /**
     * 获取取的数据长度，单位是字节还是字取决于设备方
     * The length of the data read, the unit is byte or word depends on the device side
     * @return 长度值
     */
    public int getLength() {
        return Length;
    }

    /**
     * 设置读取的数据长度，单位是字节还是字取决于设备方
     * The length of the data read, the unit is byte or word depends on the device side
     * @param length 长度值
     */
    public void setLength(int length) {
        Length = length;
    }

    private int Length = 0;

    /**
     * 从指定的地址信息解析成真正的设备地址信息
     * Parsing from the specified address information into real device address information
     * @param address 地址信息
     * @param length 数据长度
     */
    public void Parse(String address, int length){
        AddressStart = Integer.parseInt(address);
        Length = length;
    }

    @Override
    public String toString() {
        return String.valueOf(AddressStart);
    }


}
