package com.myblog.exception;

/**
 *      自定义异常
 *
 *
 *        ReceiveTimeoutTransportException: [][47.100.201.61:9300][cluster:monitor/nodes/liveness] request_id [23] t
 *  *  NoNodeAvailableException: None of the configured nodes are available:
 *  *
 *  *  java.io.IOException: Operation timed out
 *      * 	at sun.nio.ch.FileDispatcherImpl.read0(Native Method) ~[na:1.8.0_202]
 *      * 	at sun.nio.ch.SocketDispatcher.read(SocketDispatcher.java:39) ~[na:1.8.0_202]
 *      * 	at sun.nio.ch.IOUtil.readIntoNativeBuffer(IOUtil.java:223) ~[na:1.8.0_202]
 *      * 	at sun.nio.ch.IOUtil.read(IOUtil.java:192) ~[na:1.8.0_202]
 *      * 	at sun.nio.ch.SocketChannelImpl.read(SocketChannelImpl.java:380) ~[na:1.8.0_202]
 *  *
 *
 *
 */

public class MyCustomizeException  extends  RuntimeException {

    private Integer code;
    private String message;



    public MyCustomizeException(BaseErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public MyCustomizeException(String message) {
        this.message = message;
    }



    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
