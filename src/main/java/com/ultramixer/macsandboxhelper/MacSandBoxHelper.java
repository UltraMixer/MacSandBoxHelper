package com.ultramixer.macsandboxhelper;

import ca.weblite.objc.Client;
import ca.weblite.objc.Proxy;
import com.sun.jna.Pointer;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import static ca.weblite.objc.util.CocoaUtils.*;

/**
 * Created by TB on 06.12.16.
 */
public class MacSandBoxHelper
{
    private static Client objc;

    public static Client objc()
    {
        if (objc == null)
        {
            objc = new Client();
        }
        return objc;
    }

    public static Object NSData_initWithBase64Encoding(String text)
    {
        return objc().sendProxy("NSData", "data").send("initWithBase64Encoding:", text);
    }

    public static String NSURL_bookmarkDataWithOptions(String path)
    {
        return objc().sendProxy("NSURL", "fileURLWithPath:", path).sendProxy("bookmarkDataWithOptions:includingResourceValuesForKeys:relativeToURL:error:", 2048, null, null, null).sendString("base64Encoding");
    }

    public static Object NSURL_URLByResolvingBookmarkData_startAccessingSecurityScopedResource(String text)
    {

        return objc().sendProxy("NSURL", "URLByResolvingBookmarkData:options:relativeToURL:bookmarkDataIsStale:error:", NSData_initWithBase64Encoding(text), 1024, null, false, null).send("startAccessingSecurityScopedResource");
    }


    public static java.util.List<File> NSOpenPanel_openPanel_runModal(String title, boolean multipleMode, boolean canChooseDirectories, boolean canChooseFiles, String[] allowedFileTypes)
    {
        final java.util.List<File> result = new ArrayList<File>();

        final EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        final SecondaryLoop secondaryLoop = eventQueue.createSecondaryLoop();

        // WARNING: dispatch_sync seems to work on most Mac always causes a deadlock and freezes the application on others (in particular MBP with 2 graphics chips)
        dispatch_async(() ->
        {
            Pointer pool = createAutoreleasePool();
            Proxy peer = objc().sendProxy("NSOpenPanel", "openPanel");
            peer.send("retain");

            peer.send("setTitle:", title);
            peer.send("setAllowsMultipleSelection:", multipleMode ? 1 : 0);
            peer.send("setCanChooseDirectories:", canChooseDirectories ? 1 : 0);
            peer.send("setCanChooseFiles:", canChooseFiles ? 1 : 0);

            if (allowedFileTypes != null)
            {
                Proxy mutableArray = objc().sendProxy("NSMutableArray", "arrayWithCapacity:", allowedFileTypes.length);
                for (String type : allowedFileTypes)
                {
                    mutableArray.send("addObject:", type);
                }
                peer.send("setAllowedFileTypes:", mutableArray);
            }

            if (peer.sendInt("runModal") != 0)
            {
                Proxy nsArray = peer.getProxy("URLs");
                int size = nsArray.sendInt("count");
                for (int i = 0; i < size; i++)
                {
                    Proxy url = nsArray.sendProxy("objectAtIndex:", i);
                    String path = url.sendString("path");
                    result.add(new File(path));
                }
            }

            drainAutoreleasePool(pool);
            secondaryLoop.exit();
        });

        // Enter the loop to block the current event handler, but leave UI responsive
        if (!secondaryLoop.enter())
        {
            throw new IllegalStateException("SecondaryLoop");
        }

        return result;
    }


    public static boolean isLockedFolder(File folder)
    {
        // write permissions my not be available even after sandbox has granted permission (e.g. when accessing files of another user)
        return folder.isDirectory() && !folder.canRead();
    }

}
