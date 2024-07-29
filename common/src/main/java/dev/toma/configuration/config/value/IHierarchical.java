package dev.toma.configuration.config.value;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public interface IHierarchical {

    <T> Optional<IConfigValue<T>> getChild(Iterator<String> pathIterator, Class<T> targetType);

    <T> Optional<T> getChildValue(Iterator<String> iterator, Class<T> targetType);

    IConfigValue<?> getChildById(String childId);

    Collection<String> getChildrenKeys();
}
