package com.nemo.testing.Onion.Feature.AbstractStages;

import com.nemo.testing.Onion.Model.AbstractPage;

public abstract class AbstractWhenStage<T extends AbstractWhenStage<T, P>, P extends AbstractPage> extends AbstractStage<T, P> {
}
