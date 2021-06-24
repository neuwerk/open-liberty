package common.utils.topology;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public enum WASVersion {
    /*
     * enum stuff
     */
    // WAS 6.0.2
    WAS_6_0_2("WAS_6_0_2"),
    WAS_6_0_2_CLIENT("WAS_6_0_2_CLIENT"),

    // WAS 6.1
    WAS_6_1("WAS_6_1"), 
    WAS_6_1_CLIENT("WAS_6_1_CLIENT"),

    // Web Service Feature Pack
    WEB_SERVICE_FEATURE_PACK("WEB_SERVICE_FEATURE_PACK"), 
    WEB_SERVICE_FEATURE_PACK_CLIENT("WEB_SERVICE_FEATURE_PACK_CLIENT"),

    // Pyxis (7.0)
    PYXIS("PYXIS"), 
    PYXIS_CLIENT("PYXIS_CLIENT"),

    // Unknown (when version can not be found)
    UNKNOWN_VERSION("UNKNOWN_VERSION");

    private final String wasVersion;
    private WASVersion(String wasVersion) {
        this.wasVersion = wasVersion;
    }
    public String getWASVersion() {
        return this.wasVersion;
    }

    /*
     * methods - private
     */

    private static final String PRODUCT_VERSION_BASE = "com.ibm.websphere.baseProductVersion";
    private static final String PRODUCT_VERSION_WSFP = "com.ibm.websphere.WebServicesFeaturePackProductVersion";
    private static final String PRODUCT_VERSION_PREPYXIS_CLIENT = "CLIENT";
    private static final String PRODUCT_VERSION_CLIENT = "APPCLIENT";
    private static final String PRODUCT_VERSION_WSFP_CLIENT = "WEBSERVICESCLIENT";

    private static final Map<String, List<String>> getVersionWAS602() {
        Map<String, List<String>> version = new LinkedHashMap<String, List<String>>();
        List<String> baseVersions = new ArrayList<String>();
        baseVersions.add("6.0.2.*");
        version.put(PRODUCT_VERSION_BASE, baseVersions);
        return version;
    }

    private static final Map<String, List<String>> getVersionWAS602Client() {
        Map<String, List<String>> version = new LinkedHashMap<String, List<String>>();
        List<String> clientVersions = new ArrayList<String>();
        clientVersions.add("6.0.2.*");
        version.put(PRODUCT_VERSION_PREPYXIS_CLIENT, clientVersions);
        return version;
    }

    private static final Map<String, List<String>> getVersionWSFP() {
        Map<String, List<String>> version = new LinkedHashMap<String, List<String>>();
        List<String> wsfpVersions = new ArrayList<String>();
        wsfpVersions.add("1.0.0.0");
        wsfpVersions.add("6.1.0.*");
        version.put(PRODUCT_VERSION_WSFP, wsfpVersions);
        return version;
    }

    private static final Map<String, List<String>> getVersionWSFPClient() {
        Map<String, List<String>> version = new LinkedHashMap<String, List<String>>();
        List<String> wsfpVersions = new ArrayList<String>();
        wsfpVersions.add("1.0.0.0");
        wsfpVersions.add("6.1.0.*");
        version.put(PRODUCT_VERSION_WSFP_CLIENT, wsfpVersions);
        return version;
    }

    private static final Map<String, List<String>> getVersionWAS61() {
        Map<String, List<String>> version = new LinkedHashMap<String, List<String>>();
        List<String> baseVersions = new ArrayList<String>();
        baseVersions.add("6.1.0.*");
        version.put(PRODUCT_VERSION_BASE, baseVersions);
        return version;
    }

    private static final Map<String, List<String>> getVersionWAS61Client() {
        Map<String, List<String>> version = new LinkedHashMap<String, List<String>>();
        List<String> clientVersions = new ArrayList<String>();
        clientVersions.add("6.1.0.*");
        version.put(PRODUCT_VERSION_PREPYXIS_CLIENT, clientVersions);
        return version;
    }

    private static final Map<String, List<String>> getVersionPyxis() {
        Map<String, List<String>> version = new LinkedHashMap<String, List<String>>();
        List<String> baseVersions = new ArrayList<String>();
        baseVersions.add("7.0.0.*");
        version.put(PRODUCT_VERSION_BASE, baseVersions);
        return version;
    }

    private static final Map<String, List<String>> getVersionPyxisClient() {
        Map<String, List<String>> version = new LinkedHashMap<String, List<String>>();
        List<String> clientVersions = new ArrayList<String>();
        clientVersions.add("7.0.0.*");
        version.put(PRODUCT_VERSION_CLIENT, clientVersions);
        return version;
    }

    /*
     * All version maps
     */
    private static final Map<Map, WASVersion> getVersionMaps() {
        Map<Map, WASVersion> allVersionMaps = new LinkedHashMap<Map, WASVersion>();
        /*
         * This map has an *order* because in this case, we care which we check first. The feature packs, for instance,
         * are an add-on to the major releases, and thus, share common version numbers. We must check for them first to
         * assure we look for the unique versions before we look for a common version.
         */
        allVersionMaps.put(getVersionWSFP(), WEB_SERVICE_FEATURE_PACK);
        allVersionMaps.put(getVersionWSFPClient(), WEB_SERVICE_FEATURE_PACK_CLIENT);
        allVersionMaps.put(getVersionWAS61(), WAS_6_1);
        allVersionMaps.put(getVersionWAS61Client(), WAS_6_1_CLIENT);
        allVersionMaps.put(getVersionPyxis(), PYXIS);
        allVersionMaps.put(getVersionPyxisClient(), PYXIS_CLIENT);
        allVersionMaps.put(getVersionWAS602(), WAS_6_0_2);
        allVersionMaps.put(getVersionWAS602Client(), WAS_6_0_2_CLIENT);
        return allVersionMaps;
    }

    /**
     * Checks to see if the two version match. This will check if the <code>matchingVersion</code> contains the
     * version number that exists in the <code>wasVersion</code>, assuming they both have the product version name
     * (left hand side).
     * 
     * @param wasVersion
     *            The actual WAS version map
     * @param matchingVersion
     *            A predefined version Map with multiple values for the... well, value.
     * @return true iff the wasVersion is contained within the matchingVersion
     */
    private static boolean matchVersion(Map<String, String> wasVersion, Map<String, List<String>> matchingVersion) {
        boolean majorMatch = true;
        for (String productVersion : matchingVersion.keySet()) {
            if (wasVersion.containsKey(productVersion)) {
                List<String> versionList = matchingVersion.get(productVersion);
                String versionNumber = wasVersion.get(productVersion);
                boolean minorMatch = false;
                for (String matchingVN : versionList) {
                    // support wildcards
                    if (matchingVN.endsWith(".*")) {
                        if (versionNumber.startsWith(matchingVN.substring(0, matchingVN.length() - 2))) {
                            minorMatch = true;
                            break;
                        }
                    } else if (versionNumber.equals(matchingVN)) {
                        minorMatch = true;
                        break;
                    }
                }
                if (!minorMatch) {
                    majorMatch = false;
                    // quit looking
                    break;
                }
            } else {
                majorMatch = false;
                // quit looking
                break;
            }
        }

        return majorMatch;
    }

    /*
     * methods - public
     */

    /**
     * Returns a WASVersion that corresponds to the <code>wasVersion</code> String. This is done through a direct
     * String compare, so the input should have been a WASVersion at some point, just converted to a String.
     * 
     * @param wasVersion
     *            The WASVersion in the form of a String
     * @return The WASVersion enum
     */
    public static WASVersion getWASVersion(String wasVersion) {
        WASVersion wasV = null;
        try {
            wasV = WASVersion.valueOf(wasVersion);
        } catch (IllegalArgumentException iae) {
        }

        return wasV;
    }

    /**
     * Returns a WASVersion for the <code>wasVersionMap</code> passed in. This is done through a series of checks
     * against previously established version Maps. If the <code>wasVersionMap</code> matches an established version,
     * the corresponding WASVersion is returned. If no versions match, UNKNOWN_VERSION is returned.
     * 
     * @param wasVersionMap
     *            The key/values for this WAS version
     * @return A WASVersion that represents the version Map passed in
     */
    public static WASVersion getWASVersion(Map<String, String> wasVersionMap) {
        WASVersion wasVersion = null;
        for (Map<String, List<String>> productVersionMap : getVersionMaps().keySet()) {
            if (matchVersion(wasVersionMap, productVersionMap)) {
                wasVersion = getVersionMaps().get(productVersionMap);
                break;
            }
        }
        if (wasVersion == null) {
            wasVersion = UNKNOWN_VERSION;
        }
        return wasVersion;
    }
}
