package com.nemo.testing.Onion.Model.Home;

import com.nemo.testing.Onion.Model.AbstractPage;

import static com.codeborne.selenide.Selenide.open;

public class HomePage extends AbstractPage {
    public static final String uri = "";  // empty string, because baseUrl already contains the slash


    @Override
    public String uri() {
        return uri;
    }

    @Override
    public void openPage() {
        open(uri);
    }
}
