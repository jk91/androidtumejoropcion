package com.jk91.android.tumejoropcion.entity;

import java.util.List;

/**
 * Created by jk91 on 14-11-24.
 */
public class Friend {

    private String id;
    private String displayName;
    private List<Activity> activityFied;

    public List<Activity> getActivityFied() {
        return activityFied;
    }

    public void setActivityFied(List<Activity> activityFied) {
        this.activityFied = activityFied;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
