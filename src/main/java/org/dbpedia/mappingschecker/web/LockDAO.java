package org.dbpedia.mappingschecker.web;

import java.sql.Time;
import java.sql.Timestamp;

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
