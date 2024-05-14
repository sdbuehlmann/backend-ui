package ch.donkeycode.examples.persons.model;

public interface Buildable<TObject> {
    Builder<TObject> toBuilder();

    interface Builder<TObject> {
        TObject build();
    }
}
