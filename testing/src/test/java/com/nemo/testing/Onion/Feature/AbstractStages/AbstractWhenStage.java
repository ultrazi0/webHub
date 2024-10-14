package com.nemo.testing.Onion.Feature.AbstractStages;

/**
 * AbstractWhenStage serves as a base class for defining "When" stages in JGiven testing scenarios.
 * It extends the AbstractStage class, enabling shared functionalities and behaviors specific to "When" actions.
 *
 * @param <T> the type of the concrete stage that extends this abstract class
 */
public abstract class AbstractWhenStage<T extends AbstractWhenStage<T>> extends AbstractStage<T> {
}
