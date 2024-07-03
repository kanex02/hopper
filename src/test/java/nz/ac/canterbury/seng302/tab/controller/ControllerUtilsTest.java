package nz.ac.canterbury.seng302.tab.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ControllerUtilsTest {

    @Test
    void referrerUrlReturnsRedirectOfPath() {
        String url = "https://www.museswipr.com/path/to/resource";

        String redirect = ControllerUtils.referrerPath(url);

        Assertions.assertEquals("redirect:/path/to/resource", redirect);
    }

    @Test
    void nullReferrerUrlReturnsHome() {
        String redirect = ControllerUtils.referrerPath(null);

        Assertions.assertEquals(ControllerUtils.HOME_REDIRECT, redirect);
    }

    @Test
    void invalidReferrerUrlReturnsHome() {
        String redirect = ControllerUtils.referrerPath("notaurl @!#@  dsqdE!!!");

        Assertions.assertEquals(ControllerUtils.HOME_REDIRECT, redirect);
    }

    @Test
    void referrerUrl_NotLocal_StripsFirstComponent() {
        String url = "https://www.museswipr.com/path/to/resource";

        String redirect = ControllerUtils.referrerPath(url, false);

        Assertions.assertEquals("redirect:/to/resource", redirect);
    }

}
