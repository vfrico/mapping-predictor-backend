package org.dbpedia.mappingschecker.web;

import java.sql.Time;
import java.sql.Timestamp;

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
public class LockDAO {
    private int lockId;
    private UserDAO user;
    private boolean locked;
    private long dateStart;
    private long dateEnd;

    public int getLockId() {
        return lockId;
    }

    public LockDAO setLockId(int lockId) {
        this.lockId = lockId;
        return this;
    }

    public UserDAO getUser() {
        return user;
    }

    public LockDAO setUser(UserDAO user) {
        this.user = user;
        return this;
    }

    public boolean isLocked() {
        return locked;
    }

    public LockDAO setLocked(boolean locked) {
        this.locked = locked;
        return this;
    }

    public long getDateStart() {
        return dateStart;
    }

    public LockDAO setDateStart(long dateStart) {
        this.dateStart = dateStart;
        return this;
    }

    public long getDateEnd() {
        return dateEnd;
    }

    public LockDAO setDateEnd(long dateEnd) {
        this.dateEnd = dateEnd;
        return this;
    }
}
