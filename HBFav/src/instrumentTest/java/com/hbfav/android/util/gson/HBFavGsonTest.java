package com.hbfav.android.util.gson;

import android.test.AndroidTestCase;

import com.google.gson.Gson;
import com.hbfav.android.model.Entry;

import junit.framework.TestCase;

public class HBFavGsonTest extends AndroidTestCase {
    private Gson mGson;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mGson = Entry.gson();
    }

    public void testDeseializeISOTime() {
        String json = "{\"datetime\": \"2013-11-27T20:11:27+09:00\"}";
        Entry entry = mGson.fromJson(json, Entry.class);
        assertEquals(entry.getDateTime().getTime() / 1000l, 1385550687);
    }

    public void testDeserializeCategories() {
        String json = "{\"datetime\":\"2013-11-27T20:11:27+09:00\", \"categories\":[\"super category\"]}";
        Entry entry = mGson.fromJson(json, Entry.class);
        assertEquals(entry.getDateTime().getTime() / 1000l, 1385550687);
        assertEquals(entry.getCategory(), "super category");
    }

    public void testDeserializeNullCategories() {
        String json = "{\"datetime\":\"2013-11-27T20:11:27+09:00\", \"categories\":[null]}";
        Entry entry = mGson.fromJson(json, Entry.class);
        assertEquals(entry.getDateTime().getTime() / 1000l, 1385550687);
        assertEquals(entry.getCategory(), null);
    }

}
