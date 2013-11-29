package com.hbfav.android.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import junit.framework.TestCase;

import org.joda.time.DateTime;

public class HBFavGsonTest extends TestCase {

    private class DummyEntry {
        @SerializedName("date_time")
        private DateTime dateTime;
        @TimelineExclude
        private String category;

        public DateTime getDateTime() {
            return dateTime;
        }

        public String getCategory() {
            return category;
        }
    }

    public void testDesrializeJodaTime() {
        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
                .create();
        String json = "{\"date_time\": \"2013-11-27T20:11:27+09:00\"}";
        DummyEntry dummyEntry = gson.fromJson(json, DummyEntry.class);
        assertEquals(dummyEntry.getDateTime().getMillis() / 1000l, 1385550687);
    }

    public void testTimelineExclude() {
        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
                .create();
        String json = "{\"date_time\":\"2013-11-27T20:11:27+09:00\", \"category\":\"super category\"}";
        DummyEntry dummyEntry = gson.fromJson(json, DummyEntry.class);
        assertEquals(dummyEntry.getDateTime().getMillis() / 1000l, 1385550687);
        assertEquals(dummyEntry.getCategory(), "super category");

        Gson exgson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
                .setExclusionStrategies(new TimelineExclusionStrategy())
                .create();
        dummyEntry = exgson.fromJson(json, DummyEntry.class);
        assertEquals(dummyEntry.getDateTime().getMillis() / 1000l, 1385550687);
        assertNull(dummyEntry.getCategory());
    }

}
