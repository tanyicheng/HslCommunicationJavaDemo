package HslCommunication.Language;

/**
 * English Version Text
 */
public class English extends DefaultLanguage {

    /***********************************************************************************
     *
     *    Normal Info
     *
     ************************************************************************************/
    public String TimeDescriptionSecond        (){ return  " Second";}
    public String TimeDescriptionMinute        (){ return  " Minute";}
    public String TimeDescriptionHour          (){ return  " Hour";}
    public String TimeDescriptionDay           (){ return  " Day";}
    public String AuthorizationFailed          (){ return "System authorization failed, need to use activation code authorization, thank you for your support."; }
    public String ConnectedFailed              (){ return "Connected Failed: "; }
    public String ConnectedSuccess             (){ return "Connect Success!"; }
    public String UnknownError                 (){ return "Unknown Error"; }
    public String ErrorCode                    (){ return "Error Code: "; }
    public String TextDescription              (){ return "Description: "; }
    public String ExceptionMessage             (){ return "Exception Info: "; }
    public String ExceptionSource              (){ return "Exception Source："; }
    public String ExceptionType                (){ return "Exception Type："; }
    public String ExceptionStackTrace          (){ return "Exception Stack: "; }
    public String ExceptionTargetSite          (){ return "Exception Method: "; }
    public String ExceptionCustomer            (){ return "Error in user-defined method: "; }
    public String SuccessText                  (){ return "Success"; }
    public String TwoParametersLengthIsNotSame (){ return "Two Parameter Length is not same"; }
    public String NotSupportedDataType         (){ return "Unsupported DataType, input again"; }
    public String NotSupportedFunction         (){ return "The current feature logic does not support"; }
    public String DataLengthIsNotEnough        (){ return "Receive length is not enough，Should:{0},Actual:{1}"; }
    public String ReceiveDataTimeout           (){ return "Receive timeout: "; }
    public String ReceiveDataLengthTooShort    (){ return "Receive length is too short: "; }
    public String MessageTip                   (){ return "Message prompt:"; }
    public String Close                        (){ return "Close"; }
    public String Time                         (){ return "Time:"; }
    public String SoftWare                     (){ return "Software:"; }
    public String BugSubmit                    (){ return "Bug submit"; }
    public String MailServerCenter             (){ return "Mail Center System"; }
    public String MailSendTail                 (){ return "Mail Service system issued automatically, do not reply"; }
    public String IpAddressError               (){ return "IP address input exception, format is incorrect"; }
    public String Send                         (){ return "Send";}
    public String Receive                      (){ return "Receive";}

    /***********************************************************************************
     *
     *    System about
     *
     ************************************************************************************/

    public String SystemInstallOperater  (){ return "Install new software: ip address is"; }
    public String SystemUpdateOperater   (){ return "Update software: ip address is"; }


    /***********************************************************************************
     *
     *    Socket-related Information description
     *
     ************************************************************************************/

    public String SocketIOException                    (){ return "Socket transport error: "; }
    public String SocketSendException                  (){ return "Synchronous Data Send exception: "; }
    public String SocketHeadReceiveException           (){ return "Command header receive exception: "; }
    public String SocketContentReceiveException        (){ return "Content Data Receive exception: "; }
    public String SocketContentRemoteReceiveException  (){ return "Recipient content Data Receive exception: "; }
    public String SocketAcceptCallbackException        (){ return "Asynchronously accepts an incoming connection attempt: "; }
    public String SocketReAcceptCallbackException      (){ return "To re-accept incoming connection attempts asynchronously"; }
    public String SocketSendAsyncException             (){ return "Asynchronous Data send Error: "; }
    public String SocketEndSendException               (){ return "Asynchronous data end callback send Error"; }
    public String SocketReceiveException               (){ return "Asynchronous Data send Error: "; }
    public String SocketEndReceiveException            (){ return "Asynchronous data end receive instruction header error"; }
    public String SocketRemoteCloseException           (){ return "An existing connection was forcibly closed by the remote host"; }

    /***********************************************************************************
     *
     *    File related information
     *
     ************************************************************************************/

    public String FileDownloadSuccess      (){ return "File Download Successful"; }
    public String FileDownloadFailed       (){ return "File Download exception"; }
    public String FileUploadFailed         (){ return "File Upload exception"; }
    public String FileUploadSuccess        (){ return "File Upload Successful"; }
    public String FileDeleteFailed         (){ return "File Delete exception"; }
    public String FileDeleteSuccess        (){ return "File deletion succeeded"; }
    public String FileReceiveFailed        (){ return "Confirm File Receive exception"; }
    public String FileNotExist             (){ return "File does not exist"; }
    public String FileSaveFailed           (){ return "File Store failed"; }
    public String FileLoadFailed           (){ return "File load failed"; }
    public String FileSendClientFailed     (){ return "An exception occurred when the file was sent"; }
    public String FileWriteToNetFailed     (){ return "File Write Network exception"; }
    public String FileReadFromNetFailed    (){ return "Read file exceptions from the network"; }
    public String FilePathCreateFailed     (){ return "Folder path creation failed: "; }
    public String FileRemoteNotExist       (){ return "The other file does not exist, cannot receive!"; }

    /***********************************************************************************
     *
     *    Engine-related data for the server
     *
     ************************************************************************************/

    public String TokenCheckFailed             (){ return "Receive authentication token inconsistency"; }
    public String TokenCheckTimeout            (){ return "Receive authentication timeout: "; }
    public String CommandHeadCodeCheckFailed   (){ return "Command header check failed"; }
    public String CommandLengthCheckFailed     (){ return "Command length check failed"; }
    public String NetClientAliasFailed         (){ return "Client's alias receive failed: "; }
    public String NetClientAccountTimeout      (){ return  "Wait for account check timeout："; }
    public String NetEngineStart               (){ return "Start engine"; }
    public String NetEngineClose               (){ return "Shutting down the engine"; }
    public String NetClientOnline              (){ return "Online"; }
    public String NetClientOffline             (){ return "Offline"; }
    public String NetClientBreak               (){ return "Abnormal offline"; }
    public String NetClientFull                (){ return "The server hosts the upper limit and receives an exceeded request connection."; }
    public String NetClientLoginFailed         (){ return "Error in Client logon: "; }
    public String NetHeartCheckFailed          (){ return "Heartbeat Validation exception: "; }
    public String NetHeartCheckTimeout         (){ return "Heartbeat verification timeout, force offline: "; }
    public String DataSourceFormatError        (){ return "Data source format is incorrect"; }
    public String ServerFileCheckFailed        (){ return "Server confirmed file failed, please re-upload"; }
    public String ClientOnlineInfo             (){ return "Client [ {0} ] Online"; }
    public String ClientOfflineInfo            (){ return "Client [ {0} ] Offline"; }
    public String ClientDisableLogin           (){ return "Client [ {0} ] is not trusted, login forbidden"; }

    /***********************************************************************************
     *
     *    Client related
     *
     ************************************************************************************/

    public String ReConnectServerSuccess            (){ return "Re-connect server succeeded"; }
    public String ReConnectServerAfterTenSeconds    (){ return "Reconnect the server after 10 seconds"; }
    public String KeyIsNotAllowedNull               (){ return "The keyword is not allowed to be empty"; }
    public String KeyIsExistAlready                 (){ return "The current keyword already exists"; }
    public String KeyIsNotExist                     (){ return "The keyword for the current subscription does not exist"; }
    public String ConnectingServer                  (){ return "Connecting to Server..."; }
    public String ConnectFailedAndWait              (){ return "Connection disconnected, wait {0} seconds to reconnect"; }
    public String AttemptConnectServer              (){ return "Attempting to connect server {0} times"; }
    public String ConnectServerSuccess              (){ return "Connection Server succeeded"; }
    public String GetClientIpAddressFailed          (){ return "Client IP Address acquisition failed"; }
    public String ConnectionIsNotAvailable          (){ return "The current connection is not available"; }
    public String DeviceCurrentIsLoginRepeat        (){ return "ID of the current device duplicate login"; }
    public String DeviceCurrentIsLoginForbidden     (){ return "The ID of the current device prohibits login"; }
    public String PasswordCheckFailed               (){ return "Password validation failed"; }
    public String DataTransformError                (){ return "Data conversion failed, source data: "; }
    public String RemoteClosedConnection            (){ return "Remote shutdown of connection"; }

    /***********************************************************************************
     *
     *    Log related
     *
     ************************************************************************************/

    public String LogNetDebug    (){ return "Debug"; }
    public String LogNetInfo     (){ return "Info"; }
    public String LogNetWarn     (){ return "Warn"; }
    public String LogNetError    (){ return "Error"; }
    public String LogNetFatal    (){ return "Fatal"; }
    public String LogNetAbandon  (){ return "Abandon"; }
    public String LogNetAll      (){ return "All"; }


    /***********************************************************************************
     *
     *    Modbus related
     *
     ************************************************************************************/

    public String ModbusTcpFunctionCodeNotSupport         (){ return "Unsupported function code"; }
    public String ModbusTcpFunctionCodeOverBound          (){ return "Data read out of bounds"; }
    public String ModbusTcpFunctionCodeQuantityOver       (){ return "Read length exceeds maximum value"; }
    public String ModbusTcpFunctionCodeReadWriteException (){ return "Read and Write exceptions"; }
    public String ModbusTcpReadCoilException              (){ return "Read Coil anomalies"; }
    public String ModbusTcpWriteCoilException             (){ return "Write Coil exception"; }
    public String ModbusTcpReadRegisterException          (){ return "Read Register exception"; }
    public String ModbusTcpWriteRegisterException         (){ return "Write Register exception"; }
    public String ModbusAddressMustMoreThanOne            (){ return "The address value must be greater than 1 in the case where the start address is 1"; }
    public String ModbusAsciiFormatCheckFailed            (){ return "Modbus ASCII command check failed, not MODBUS-ASCII message"; }
    public String ModbusCRCCheckFailed                    (){ return "The CRC checksum check failed for Modbus"; }
    public String ModbusLRCCheckFailed                    (){ return "The LRC checksum check failed for Modbus"; }
    public String ModbusMatchFailed                       (){ return "Not the standard Modbus protocol"; }
    public String ModbusBitIndexOverstep                  (){ return "The index of the bit access is out of range, it should be between 0-15";}


    /***********************************************************************************
     *
     *    Melsec PLC related
     *
     ************************************************************************************/
    public String MelsecPleaseReferToManualDocument        (){ return "Please check Mitsubishi's communication manual for details of the alarm."; }
    public String MelsecReadBitInfo                        (){ return "The read bit variable array can only be used for bit soft elements, if you read the word soft component, call the Read method"; }
    public String MelsecCurrentTypeNotSupportedWordOperate (){ return "The current type does not support word read and write"; }
    public String MelsecCurrentTypeNotSupportedBitOperate  (){ return "The current type does not support bit read and write"; }
    public String MelsecFxReceiveZero                      (){ return "The received data length is 0"; }
    public String MelsecFxAckNagative                      (){ return "Invalid data from PLC feedback"; }
    public String MelsecFxAckWrong                         (){ return "PLC Feedback Signal Error: "; }
    public String MelsecFxCrcCheckFailed                   (){ return "PLC Feedback message and check failed!"; }


    public String MelsecError02      (){ return "The specified range of the \"read/write\" (in/out) device is incorrect.";}
    public String MelsecError51      (){ return "When using random access buffer memory for communication, the start address specified by the external device is set outside the range of 0-6143. Solution: Check and correct the specified start address.";}
    public String MelsecError52      (){ return "1. When using random access buffer memory for communication, the start address + data word count specified by the external device (depending on the setting when reading) is outside the range of 0-6143. \r\n2. Data of the specified word count (text) cannot be sent in one frame. (The data length value and the total text of the communication are not within the allowed range.)";}
    public String MelsecError54      (){ return "When \"ASCII Communication\" is selected in [Operation Settings]-[Communication Data Code] via GX Developer, ASCII codes from external devices that cannot be converted to binary codes are received.";}
    public String MelsecError55      (){ return "When [Operation Settings]-[Cannot Write in Run Time] cannot be set by GX Developer (No check mark), if the PLCCPU is in the running state, the external device requests to write data. ";}
    public String MelsecError56      (){ return "The device specified from the outside is incorrect.";}
    public String MelsecError58      (){ return "1. The command start address (start device number and start step number) specified by the external device can be set outside the specified range.\r\n2. The block number specified for the extended file register does not exist.\r\n3. File register (R) cannot be specified.\r\n4. Specify the word device for the bit device command.\r\n5. The start number of the bit device is specified by a certain value. This value is not a multiple of 16 in the word device command.";}
    public String MelsecError59      (){ return "The register of the extension file cannot be specified";}
    public String MelsecErrorC04D    (){ return "In the information received by the Ethernet module through automatic open UDP port communication or out-of-order fixed buffer communication, the data length specified in the application domain is incorrect.";}
    public String MelsecErrorC050    (){ return "When the operation setting of ASCII code communication is performed in the Ethernet module, ASCII code data that cannot be converted into binary code is received.";}
    public String MelsecErrorC051_54 (){ return "The number of read/write points is outside the allowable range.";}
    public String MelsecErrorC055    (){ return "The number of file data read/write points is outside the allowable range.";}
    public String MelsecErrorC056    (){ return "The read/write request exceeded the maximum address.";}
    public String MelsecErrorC057    (){ return "The length of the requested data does not match the data count of the character area (partial text).";}
    public String MelsecErrorC058    (){ return "After the ASCII binary conversion, the length of the requested data does not match the data count of the character area (partial text).";}
    public String MelsecErrorC059    (){ return "The designation of commands and subcommands is incorrect.";}
    public String MelsecErrorC05A_B  (){ return "The Ethernet module cannot read and write to the specified device.";}
    public String MelsecErrorC05C    (){ return "The requested content is incorrect. (Request to read/write to word device in bits.)";}
    public String MelsecErrorC05D    (){ return "Monitoring registration is not performed.";}
    public String MelsecErrorC05E    (){ return "The communication time between the Ethernet module and the PLC CPU exceeds the time of the CPU watchdog timer.";}
    public String MelsecErrorC05F    (){ return "The request cannot be executed on the target PLC.";}
    public String MelsecErrorC060    (){ return "The requested content is incorrect. (Incorrect data is specified for the bit device, etc.)";}
    public String MelsecErrorC061    (){ return "The length of the requested data does not match the number of data in the character area (partial text).";}
    public String MelsecErrorC062    (){ return "When the online correction is prohibited, the remote protocol I/O station (QnA compatible 3E frame or 4E frame) write operation is performed by the MC protocol.";}
    public String MelsecErrorC070    (){ return "Cannot specify the range of device memory for the target station";}
    public String MelsecErrorC072    (){ return "The requested content is incorrect. (Request to write to word device in bit units.) ";}
    public String MelsecErrorC074    (){ return "The target PLC does not execute the request. The network number and PC number need to be corrected.";}


    /***********************************************************************************
     *
     *    Siemens PLC related
     *
     ************************************************************************************/

    public String SiemensDBAddressNotAllowedLargerThan255  (){ return "DB block data cannot be greater than 255"; }
    public String SiemensReadLengthMustBeEvenNumber        (){ return "The length of the data read must be an even number"; }
    public String SiemensWriteError                        (){ return "Writes the data exception, the code name is: "; }
    public String SiemensReadLengthCannotLargerThan19      (){ return "The number of arrays read does not allow greater than 19"; }
    public String SiemensDataLengthCheckFailed             (){ return "Block length checksum failed, please check if Put/get is turned on and DB block optimization is turned off"; }
    public String SiemensFWError                           (){ return "An exception occurred, the specific information to find the Fetch/write protocol document"; }
    public String SiemensReadLengthOverPlcAssign           () { return  "读取的数据范围超出了PLC的设定";}

    /***********************************************************************************
     *
     *    Omron PLC related
     *
     ************************************************************************************/

    public String OmronAddressMustBeZeroToFifteen  (){ return "The bit address entered can only be between 0-15"; }
    public String OmronReceiveDataError            (){ return "Data Receive exception"; }
    public String OmronStatus0                     (){ return "Communication is normal."; }
    public String OmronStatus1                     (){ return "The message header is not fins"; }
    public String OmronStatus2                     (){ return "Data length too long"; }
    public String OmronStatus3                     (){ return "This command does not support"; }
    public String OmronStatus20                    (){ return "Exceeding connection limit"; }
    public String OmronStatus21                    (){ return "The specified node is already in the connection"; }
    public String OmronStatus22                    (){ return "Attempt to connect to a protected network node that is not yet configured in the PLC"; }
    public String OmronStatus23                    (){ return "The current client's network node exceeds the normal range"; }
    public String OmronStatus24                    (){ return "The current client's network node is already in use"; }
    public String OmronStatus25                    (){ return "All network nodes are already in use"; }



    /***********************************************************************************
     *
     *    AB PLC 相关
     *
     ************************************************************************************/

    public String AllenBradley04              (){ return "The IOI could not be deciphered. Either it was not formed correctly or the match tag does not exist."; }
    public String AllenBradley05              (){ return "The particular item referenced (usually instance) could not be found."; }
    public String AllenBradley06              (){ return "The amount of data requested would not fit into the response buffer. Partial data transfer has occurred."; }
    public String AllenBradley0A              (){ return "An error has occurred trying to process one of the attributes."; }
    public String AllenBradley13              (){ return "Not enough command data / parameters were supplied in the command to execute the service requested."; }
    public String AllenBradley1C              (){ return "An insufficient number of attributes were provided compared to the attribute count."; }
    public String AllenBradley1E              (){ return "A service request in this service went wrong."; }
    public String AllenBradley26              (){ return "The IOI word length did not match the amount of IOI which was processed."; }
    public String AllenBradleySessionStatus00 (){ return "success"; }
    public String AllenBradleySessionStatus01 (){ return "The sender issued an invalid or unsupported encapsulation command."; }
    public String AllenBradleySessionStatus02 (){ return "Insufficient memory resources in the receiver to handle the command. This is not an application error. Instead, it only results if the encapsulation layer cannot obtain memory resources that it need."; }
    public String AllenBradleySessionStatus03 (){ return "Poorly formed or incorrect data in the data portion of the encapsulation message."; }
    public String AllenBradleySessionStatus64 (){ return "An originator used an invalid session handle when sending an encapsulation message."; }
    public String AllenBradleySessionStatus65 (){ return "The target received a message of invalid length."; }
    public String AllenBradleySessionStatus69 (){ return "Unsupported encapsulation protocol revision."; }

    /***********************************************************************************
     *
     *    Panasonic PLC 相关
     *
     ************************************************************************************/

    public String PanasonicReceiveLengthMustLargerThan9 (){ return "The received data length must be greater than 9"; }
    public String PanasonicAddressParameterCannotBeNull (){ return "Address parameter is not allowed to be empty"; }
    public String PanasonicMewStatus20                  (){ return "Error unknown"; }
    public String PanasonicMewStatus21                  (){ return "Nack error, the remote unit could not be correctly identified, or a data error occurred."; }
    public String PanasonicMewStatus22                  (){ return "WACK Error: The receive buffer for the remote unit is full."; }
    public String PanasonicMewStatus23                  (){ return "Multiple port error: The remote unit number (01 to 16) is set to repeat with the local unit."; }
    public String PanasonicMewStatus24                  (){ return "Transport format error: An attempt was made to send data that does not conform to the transport format, or a frame data overflow or a data error occurred."; }
    public String PanasonicMewStatus25                  (){ return "Hardware error: Transport system hardware stopped operation."; }
    public String PanasonicMewStatus26                  (){ return "Unit Number error: The remote unit's numbering setting exceeds the range of 01 to 63."; }
    public String PanasonicMewStatus27                  (){ return "Error not supported: Receiver data frame overflow. An attempt was made to send data of different frame lengths between different modules."; }
    public String PanasonicMewStatus28                  (){ return "No answer error: The remote unit does not exist. (timeout)."; }
    public String PanasonicMewStatus29                  (){ return "Buffer Close error: An attempt was made to send or receive a buffer that is in a closed state."; }
    public String PanasonicMewStatus30                  (){ return "Timeout error: Persisted in transport forbidden State."; }
    public String PanasonicMewStatus40                  (){ return "BCC Error: A transmission error occurred in the instruction data."; }
    public String PanasonicMewStatus41                  (){ return "Malformed: The sent instruction information does not conform to the transmission format."; }
    public String PanasonicMewStatus42                  (){ return "Error not supported: An unsupported instruction was sent. An instruction was sent to a target station that was not supported."; }
    public String PanasonicMewStatus43                  (){ return "Processing Step Error: Additional instructions were sent when the transfer request information was suspended."; }
    public String PanasonicMewStatus50                  (){ return "Link Settings Error: A link number that does not actually exist is set."; }
    public String PanasonicMewStatus51                  (){ return "Simultaneous operation error: When issuing instructions to other units, the transmit buffer for the local unit is full."; }
    public String PanasonicMewStatus52                  (){ return "Transport suppression Error: Unable to transfer to other units."; }
    public String PanasonicMewStatus53                  (){ return "Busy error: Other instructions are being processed when the command is received."; }
    public String PanasonicMewStatus60                  (){ return "Parameter error: Contains code that cannot be used in the directive, or the code does not have a zone specified parameter (X, Y, D), and so on."; }
    public String PanasonicMewStatus61                  (){ return "Data error: Contact number, area number, Data code format (BCD,HEX, etc.) overflow, overflow, and area specified error."; }
    public String PanasonicMewStatus62                  (){ return "Register ERROR: Excessive logging of data in an unregistered state of operations (Monitoring records, tracking records, etc.). )。"; }
    public String PanasonicMewStatus63                  (){ return "PLC mode error: When an instruction is issued, the run mode is not able to process the instruction."; }
    public String PanasonicMewStatus65                  (){ return "Protection Error: Performs a write operation to the program area or system register in the storage protection state."; }
    public String PanasonicMewStatus66                  (){ return "Address Error: Address (program address, absolute address, etc.) Data encoding form (BCD, hex, etc.), overflow, underflow, or specified range error."; }
    public String PanasonicMewStatus67                  (){ return "Missing data error: The data to be read does not exist. (reads data that is not written to the comment register.)"; }


    /***********************************************************************************
     *
     *   Fatek PLC 永宏PLC相关
     *
     ************************************************************************************/
    public String FatekStatus02               (){ return  "Illegal value";}
    public String FatekStatus03               (){ return  "Write disabled";}
    public String FatekStatus04               (){ return  "Invalid command code";}
    public String FatekStatus05               (){ return  "Cannot be activated (down RUN command but Ladder Checksum does not match)";}
    public String FatekStatus06               (){ return  "Cannot be activated (down RUN command but PLC ID ≠ Ladder ID)";}
    public String FatekStatus07               (){ return  "Cannot be activated (down RUN command but program syntax error)";}
    public String FatekStatus09               (){ return  "Cannot be activated (down RUN command, but the ladder program command PLC cannot be executed)";}
    public String FatekStatus10               (){ return  "Illegal address";}



    /***********************************************************************************
     *
     *   Fuji PLC 富士PLC相关
     *
     ************************************************************************************/
    public String FujiSpbStatus01            (){ return  "Write to the ROM";}
    public String FujiSpbStatus02            (){ return  "Received undefined commands or commands that could not be processed";}
    public String FujiSpbStatus03            (){ return  "There is a contradiction in the data part (parameter exception)";}
    public String FujiSpbStatus04            (){ return  "Unable to process due to transfer interlocks from other programmers";}
    public String FujiSpbStatus05            (){ return  "The module number is incorrect";}
    public String FujiSpbStatus06            (){ return  "Search item not found";}
    public String FujiSpbStatus07            (){ return  "An address that exceeds the module range (when writing) is specified";}
    public String FujiSpbStatus09            (){ return  "Unable to execute due to faulty program (RUN)";}
    public String FujiSpbStatus0C            (){ return  "Inconsistent password";}


    /***********************************************************************************
     *
     *   MQTT相关
     *
     ************************************************************************************/
    public String MQTTDataTooLong            () { return  "The current data length exceeds the limit of the agreement";}
    public String MQTTStatus01               () { return  "unacceptable protocol version";}
    public String MQTTStatus02               () { return  "identifier rejected";}
    public String MQTTStatus03               () { return  "server unavailable";}
    public String MQTTStatus04               () { return  "bad user name or password";}
    public String MQTTStatus05               () { return  "not authorized";}

    /***********************************************************************************
     *
     *   SAM相关
     *
     ************************************************************************************/
    public String SAMReceiveLengthMustLargerThan8  () { return  "Received data length is less than 8, must be greater than 8";}
    public String SAMHeadCheckFailed               () { return  "Data frame header check failed for SAM。";}
    public String SAMLengthCheckFailed             () { return  "Data length header check failed for SAM。";}
    public String SAMSumCheckFailed                () { return  "SAM's data checksum check failed.";}
    public String SAMAddressStartWrong             () { return  "SAM string address identification error.";}
    public String SAMStatus90                      () { return  "Successful operation";}
    public String SAMStatus91                      () { return  "No content in the card";}
    public String SAMStatus9F                      () { return  "Find card success";}
    public String SAMStatus10                      () { return  "Received data checksum error";}
    public String SAMStatus11                      () { return  "Received data length error";}
    public String SAMStatus21                      () { return  "Receive data command error";}
    public String SAMStatus23                      () { return  "Unauthorized operation";}
    public String SAMStatus24                      () { return  "Unrecognized error";}
    public String SAMStatus31                      () { return  "Card authentication SAM failed";}
    public String SAMStatus32                      () { return  "SAM certificate / card failed";}
    public String SAMStatus33                      () { return  "Information validation error";}
    public String SAMStatus40                      () { return  "Unrecognized card type";}
    public String SAMStatus41                      () { return  "ID / card operation failed";}
    public String SAMStatus47                      () { return  "Random number failed";}
    public String SAMStatus60                      () { return  "SAM Self-test failed";}
    public String SAMStatus66                      () { return  "SAM unauthorized";}
    public String SAMStatus80                      () { return  "Failed to find card";}
    public String SAMStatus81                      () { return  "选取证/卡失败";}

}
