package leanderk.izou.wifipresence;

import intellimate.izou.contentgenerator.ContentGenerator;
import intellimate.izou.events.Event;
import intellimate.izou.resource.Resource;
import intellimate.izou.system.Context;
import intellimate.izou.system.IdentificationManager;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author LeanderK
 * @version 1.0
 */
public class WifiPresenceContentGenerator extends ContentGenerator {
    public static final String ID = WifiPresenceContentGenerator.class.getCanonicalName();
    private static final String RESOURCE_ID_GENERAL = "izou.presence.general";
    private static final String RESOURCE_ID_PRESENCE = "izou.presence";

    private WifiScanner wifiScanner;

    public WifiPresenceContentGenerator(Context context, WifiScanner wifiScanner) {
        super(ID, context);
        this.wifiScanner = wifiScanner;
    }

    /**
     * this method is called to register what resources the object provides.
     * just pass a List of Resources without Data in it.
     *
     * @return a List containing the resources the object provides
     */
    @Override
    public List<Resource> announceResources() {
        Resource<Boolean> generalResource = IdentificationManager.getInstance().getIdentification(this)
                .map(id -> new Resource<Boolean>(RESOURCE_ID_GENERAL, id))
                .orElse(new Resource<Boolean>(RESOURCE_ID_GENERAL));
        Resource<Boolean> presenceResource = IdentificationManager.getInstance().getIdentification(this)
                .map(id -> new Resource<Boolean>(RESOURCE_ID_PRESENCE, id))
                .orElse(new Resource<Boolean>(RESOURCE_ID_PRESENCE));
        return Arrays.asList(generalResource, presenceResource);
    }

    /**
     * this method is called to register for what Events it wants to provide Resources.
     *
     * @return a List containing ID's for the Events
     */
    @Override
    public List<String> announceEvents() {
        return Arrays.asList(Event.RESPONSE);
    }

    /**
     * this method is called when an object wants to get a Resource.
     * it has as an argument resource instances without data, which just need to get populated.
     *
     * @param resources a list of resources without data
     * @param event     if an event caused the action, it gets passed. It can also be null.
     * @return a list of resources with data
     */
    @Override
    public List<Resource> provideResource(List<Resource> resources, Optional<Event> event) {
        return resources.stream()
                .map(resource -> new Resource<Boolean>(resource.getResourceID(),
                        IdentificationManager.getInstance().getIdentification(this)
                                .orElseThrow(() -> new RuntimeException("Unable to create Event, missing ID"))))
                .map(resource -> resource.setResource(wifiScanner.anyPresent()))
                .collect(Collectors.toList());
    }
}
