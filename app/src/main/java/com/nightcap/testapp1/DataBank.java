package com.nightcap.testapp1;
import io.realm.RealmObject;

/**
 * Created by willi_000 on 13/11/2016.
 */

public class DataBank extends RealmObject{
    private String event_name;
    private Long time_ms;

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public Long getTime_ms() {
        return time_ms;
    }

    public void setTime_ms(Long time_ms) {
        this.time_ms = time_ms;
    }
}
