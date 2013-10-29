package com.hbfav.android.test.controllers;


import com.hbfav.android.controllers.BookmarksFetcher;

import junit.framework.TestCase;

public class BookmarksFetcherTest extends TestCase {
    public void testFetchBookmarks() throws Exception{
        Integer resCode = BookmarksFetcher.getInstance().fetchBookmarks("foobar");
        assertEquals(resCode.intValue(), 200);
    }
}
