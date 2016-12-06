package com.ultramixer.macsandboxhelper.test;

import com.ultramixer.macsandboxhelper.MacSandBoxHelper;

import java.io.File;
import java.util.List;

/**
 * Created by TB on 06.12.16.
 */
public class Test
{
    public static void main(String[] args)
    {
        List<File> files = MacSandBoxHelper.NSOpenPanel_openPanel_runModal("Title", false, true, false, null);
        System.out.println("files = " + files);

        String bookmark = MacSandBoxHelper.NSURL_bookmarkDataWithOptions(files.get(0).getAbsolutePath());
        System.out.println("bookmark = " + bookmark);

        Object url = MacSandBoxHelper.NSURL_URLByResolvingBookmarkData_startAccessingSecurityScopedResource(bookmark);
        System.out.println("url = " + url);

        boolean isLockedFolder = MacSandBoxHelper.isLockedFolder(files.get(0));
        System.out.println("isLockedFolder = " + isLockedFolder);
    }
}
