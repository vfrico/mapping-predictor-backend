package es.upm.oeg.tools.mappings.beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ApiError {
    String msg;
    int code;

    public ApiError(String msg, int code) {

        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public ApiError setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public int getCode() {
        return code;
    }

    public ApiError setCode(int code) {
        this.code = code;
        return this;
    }

    public String toString() {
        return "Err="+msg+"; code="+code;
    }
}
