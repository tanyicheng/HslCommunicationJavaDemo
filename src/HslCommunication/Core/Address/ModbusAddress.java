package HslCommunication.Core.Address;

/**
 * Modbus协议地址格式，可以携带站号，功能码，地址信息
 * Modbus protocol address format, can carry station number, function code, address information
 */
public class ModbusAddress extends DeviceAddressBase {

    /**
     * 实例化一个默认的对象
     * Instantiate a default object
     */
    public ModbusAddress() {
        Station = -1;
        Function = -1;
        setAddress(0);
    }

    /**
     * 实例化一个对象，使用指定的地址初始化
     * Instantiate an object, initialize with the specified address
     *
     * @param address 传入的地址信息，支持富地址，例如s=2;x=3;100
     */
    public ModbusAddress(String address) {
        Station = -1;
        Function = -1;
        setAddress(0);
        Parse(address);
    }

    /**
     * 实例化一个对象，使用指定的地址及功能码初始化
     * Instantiate an object and initialize it with the specified address and function code
     *
     * @param address  传入的地址信息，支持富地址，例如s=2;x=3;100
     * @param function 默认的功能码信息
     */
    public ModbusAddress(String address, byte function) {
        Station = -1;
        Function = function;
        setAddress(0);
        Parse(address);
    }

    /**
     * 实例化一个对象，使用指定的地址，站号，功能码来初始化
     * Instantiate an object, use the specified address, station number, function code to initialize
     *
     * @param address  传入的地址信息，支持富地址，例如s=2;x=3;100
     * @param station  站号信息
     * @param function 默认的功能码信息
     */
    public ModbusAddress(String address, byte station, byte function) {
        Station = -1;
        Function = function;
        Station = station;
        setAddress(0);
        Parse(address);
    }

    /**
     * 获取当前地址的站号信息
     * Get the station number information of the current address
     *
     * @return int
     */
    public int getStation() {
        return Station;
    }

    /**
     * 设置当前地址的站号信息
     * Set the station number information of the current address
     *
     * @param station 站号信息
     */
    public void setStation(int station) {
        Station = station;
    }

    private int Station = 0;

    /**
     * 获取当前地址携带的功能码
     * Get the function code carried by the current address
     *
     * @return int
     */
    public int getFunction() {
        return Function;
    }

    /**
     * 设置当前地址携带的功能码
     * Set the function code carried by the current address
     *
     * @param function 功能码
     */
    public void setFunction(int function) {
        Function = function;
    }

    private int Function = 0;

    @Override
    public void Parse(String address) {
        if (address.indexOf(';') < 0) {
            // 正常地址，功能码03
            setAddress(Integer.parseInt(address));
        } else {
            // 带功能码的地址
            String[] list = address.split(";");
            for (int i = 0; i < list.length; i++) {
                if (list[i].charAt(0) == 's' || list[i].charAt(0) == 'S') {
                    // 站号信息
                    this.Station = Integer.parseInt(list[i].substring(2));
                } else if (list[i].charAt(0) == 'x' || list[i].charAt(0) == 'X') {
                    this.Function = Integer.parseInt(list[i].substring(2));
                } else {
                    setAddress(Integer.parseInt(list[i]));
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (Station >= 0) sb.append("s=" + Station + ";");
        if (Function >= 1) sb.append("x=" + Function + ";");
        sb.append(String.valueOf(getAddress()));
        return sb.toString();
    }

    /**
     * 地址偏移指定的位置，返回一个新的地址对象
     * The address is offset by the specified position and a new address object is returned
     *
     * @param value 数据值信息
     * @return 新增后的地址信息
     */
    public ModbusAddress AddressAdd(int value) {
        ModbusAddress address = new ModbusAddress();
        address.setStation(this.getStation());
        address.setFunction(this.getFunction());
        address.setAddress(this.getAddress() + value);
        return address;
    }

    /**
     * 地址偏移1，返回一个新的地址对象
     * The address is offset by 1 and a new address object is returned
     *
     * @return 新增后的地址信息
     */
    public ModbusAddress AddressAdd() {
        return AddressAdd(1);
    }

}
