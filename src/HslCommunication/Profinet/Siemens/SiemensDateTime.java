package HslCommunication.Profinet.Siemens;

import HslCommunication.BasicFramework.SoftBasic;

import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;

public class SiemensDateTime {

    /**
     * Parses a {@link Date} value from bytes.
     * @param bytes Input bytes read from PLC.
     * @return A {@link Date} object representing the value read from PLC.
     */
    public static Date FromByteArray( byte[] bytes ) throws Exception {
        return FromByteArrayImpl(bytes);
    }

    /**
     * Parses an array of {@link Date}  values from bytes.
     * @param bytes Input bytes read from PLC.
     * @return An array of {@link Date} objects representing the values read from PLC.
     */
    public static Date[] ToArray( byte[] bytes ) throws Exception {
        int cnt = bytes.length / 8;
        Date[] result = new Date[bytes.length / 8];

        for (int i = 0; i < cnt; i++)
            result[i] = FromByteArrayImpl(SoftBasic.BytesArraySelectMiddle(bytes, i * 8, 8));
        return result;
    }

    private static int DecodeBcd( byte input ) {
        return 10 * ((int)input >> 4) + ((int)input & 0b00001111);
    }

    private static int ByteToYear( byte bcdYear ) throws Exception {
        int input = DecodeBcd( bcdYear );
        if (input < 90) return input + 2000;
        if (input < 100) return input + 1900;

        throw new Exception( "Value '" + input + "' is higher than the maximum '99' of S7 date and time representation." );
    }
    private static int AssertRangeInclusive( int input, byte min, byte max, String field ) throws Exception {
        if (input < min)
            throw new Exception( "Value '" + input + "' is lower than the minimum '" + min + "' allowed for " + field + "." );
        if (input > max)
            throw new Exception( "Value '" + input + "' is higher than the maximum '" + max + "' allowed for " + field + "." );
        return input;
    }

    private static Date FromByteArrayImpl( byte[] bytes ) throws Exception {
        if (bytes.length != 8)
            throw new Exception( "Parsing a DateTime requires exactly 8 bytes of input data, input data is " + bytes.length + " bytes long." );

        int year = ByteToYear( bytes[0] );
        int month = AssertRangeInclusive( DecodeBcd( bytes[1] ), (byte) 1, (byte)12, "month" );
        int day = AssertRangeInclusive( DecodeBcd( bytes[2] ), (byte)1, (byte)31, "day of month" );
        int hour = AssertRangeInclusive( DecodeBcd( bytes[3] ), (byte)0, (byte)23, "hour" );
        int minute = AssertRangeInclusive( DecodeBcd( bytes[4] ), (byte)0, (byte)59, "minute" );
        int second = AssertRangeInclusive( DecodeBcd( bytes[5] ), (byte)0, (byte)59, "second" );
        int hsec = AssertRangeInclusive( DecodeBcd( bytes[6] ), (byte)0, (byte)99, "first two millisecond digits" );
        int msec = AssertRangeInclusive( bytes[7] >> 4, (byte)0, (byte)9, "third millisecond digit" );
        int dayOfWeek = AssertRangeInclusive( bytes[7] & 0b00001111, (byte)1, (byte)7, "day of week" );

        Calendar c = Calendar.getInstance();
        c.set(year,month-1, day, hour, minute, second);
        return c.getTime();
    }

    private static byte EncodeBcd( int value ) {
        return (byte) ((value / 10 << 4) | value % 10);
    }

    private static byte MapYear( int year ) {
        return (byte) (year < 2000 ? year - 1900 : year - 2000);
    }

    /**
     * Converts a {@link Date} value to a byte array.
     * @param dateTime The DateTime value to convert.
     * @return A byte array containing the S7 date time representation of {@link Date}.
     */
    public static byte[] ToByteArray( Date dateTime ){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        return new byte[]
        {
            EncodeBcd(MapYear(calendar.get(Calendar.YEAR))),
            EncodeBcd(calendar.get(Calendar.MONTH) + 1),
            EncodeBcd(calendar.get(Calendar.DAY_OF_MONTH)),
            EncodeBcd(calendar.get(Calendar.HOUR_OF_DAY)),
            EncodeBcd(calendar.get(Calendar.MINUTE)),
            EncodeBcd(calendar.get(Calendar.SECOND)),
            EncodeBcd(calendar.get(Calendar.MILLISECOND) / 10),
            (byte) (calendar.get(Calendar.MILLISECOND) % 10 << 4 | calendar.get(Calendar.DAY_OF_WEEK))
        };
    }

    /**
     * Converts an array of {@link Date} values to a byte array.
     * @param dateTimes The DateTime values to convert.
     * @return A byte array containing the S7 date time representations of {@link Date}.
     * @throws Exception time not between
     */
    public static byte[] ToByteArray( Date[] dateTimes ) throws Exception {
        byte[] bytes = new byte[dateTimes.length * 8];
        for (int i = 0; i < dateTimes.length; i++) {
            byte[] byt = ToByteArray(dateTimes[i]);
            System.arraycopy(byt, 0, bytes, i * 8, 8);
        }
        return bytes;
    }
}
