package icu.Xell.Mainline.obfuscator;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.client.WebClient;

import java.util.HashMap;
import java.util.Map;

public final class XellRapApplication implements ApplicationConfiguration {
    static final String ENTRY_POINT_PATH = "/xell";

    @Override
    public void configure(Application application) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(WebClient.PAGE_TITLE, "Xell Obfuscator");
        application.addEntryPoint(ENTRY_POINT_PATH, XellRapEntryPoint.class, properties);
    }
}
