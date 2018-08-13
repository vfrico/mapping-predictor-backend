package es.upm.oeg.tools.mappings.beans;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Copyright 2018 Víctor Fernández <vfrico@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Víctor Fernández <vfrico@gmail.com>
 * @since 1.0.0
 */
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
