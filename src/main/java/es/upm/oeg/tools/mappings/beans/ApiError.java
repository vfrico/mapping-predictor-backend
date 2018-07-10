package es.upm.oeg.tools.mappings.beans;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ApiError {
    String msg;
    int code;
    Throwable error;

    public ApiError(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    public ApiError(String msg, int code, Throwable error) {
        this(msg,code);
        this.error = error;
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

    public Throwable getError() {
        return error;
    }

    public ApiError setError(Throwable error) {
        this.error = error;
        return this;
    }

    public String toString() {
        if (error != null) {
            return "Err=" + msg + "; code=" + code + ". \n" + error.getMessage();
        } else {
            return "Err=" + msg + "; code=" + code;
        }
    }

    public Response.ResponseBuilder toResponse() {
        return Response.status(code).entity(this);
    }
}
